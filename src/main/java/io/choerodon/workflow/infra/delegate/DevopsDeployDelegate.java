package io.choerodon.workflow.infra.delegate;

import io.choerodon.workflow.infra.feginoperator.DevopsServiceRepository;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Sheep on 2019/4/3.
 */

@Component
public class DevopsDeployDelegate implements JavaDelegate {

    public static final String DEPLOY_FAILED = "deployFailed";
    @Autowired
    DevopsServiceRepository devopsServiceRepository;
    @Autowired
    ProcessRuntime processRuntime;

    private Logger logger = LoggerFactory.getLogger(DevopsDeployDelegate.class);

    private final String SUCCRESS = "success";
    private final String FAILED = "failed";
    private final String PARALLEL = "parallel";

    @Override
    public void execute(DelegateExecution delegateExecution) {

        String[] ids = delegateExecution.getCurrentActivityId().split("\\.");
        Long pipelineId = Long.parseLong(ids[1]);
        Long stageId = Long.parseLong(ids[2]);
        Long taskRecordId = Long.parseLong(ids[3]);
        String parallel = ids[4];


        //并行任务中serviceTask即使有一个执行失败,其他的serviceTask也要继续执行
        Boolean isDeploy = false;
        if (delegateExecution.getVariable(DEPLOY_FAILED) == null) {
            isDeploy = true;
        } else {
            if (delegateExecution.getVariable(DEPLOY_FAILED).equals(pipelineId + ":" + stageId) && delegateExecution.getVariable(PARALLEL).equals("true")) {
                isDeploy = true;
            }
        }
        if (isDeploy) {

            logger.info(String.format("ServiceTask:%s 开始", delegateExecution.getCurrentActivityId()));


            devopsServiceRepository.autoDeploy(stageId, taskRecordId);

            int[] count = {0};
            boolean[] success = {false};
            Runnable runnable = () -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                    count[0] = count[0] + 1;
                    String deployResult = devopsServiceRepository.getAutoDeployTaskStatus(stageId, taskRecordId);
                    logger.info(deployResult);
                    if (SUCCRESS.equals(deployResult)) {
                        success[0] = true;
                        Thread.currentThread().interrupt();
                    }
                    //自动部署失败或者执行3min以上没反应也重置为失败
                    if (FAILED.equals(deployResult) || count[0] == 60) {
                        devopsServiceRepository.setAutoDeployTaskStatus(pipelineId, stageId, taskRecordId, false);
                        Thread.currentThread().interrupt();
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.info(e.getMessage());
            }
            if (success[0]) {
                devopsServiceRepository.setAutoDeployTaskStatus(pipelineId, stageId, taskRecordId, true);
                logger.info(String.format("ServiceTask:%s  结束", delegateExecution.getCurrentActivityId()));
            } else {
                delegateExecution.setVariable(DEPLOY_FAILED, pipelineId + ":" + stageId);
                delegateExecution.setVariable(PARALLEL, parallel);
                logger.info(String.format("ServiceTask:%s  失败", delegateExecution.getCurrentActivityId()));
            }
        }
    }
}
