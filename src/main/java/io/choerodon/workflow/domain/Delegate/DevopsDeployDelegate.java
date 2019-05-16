package io.choerodon.workflow.domain.Delegate;


import io.choerodon.workflow.domain.repository.DevopsServiceRepository;
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

    public static final String DEPLOY_STATUS = "deployStatus";
    @Autowired
    DevopsServiceRepository devopsServiceRepository;
    @Autowired
    ProcessRuntime processRuntime;

    private Logger logger = LoggerFactory.getLogger(DevopsDeployDelegate.class);

    private final String SUCCRESS = "success";
    private final String RUNNING = "running";
    private final String FAILED = "failed";

    @Override
    public void execute(DelegateExecution delegateExecution) {

//        if (delegateExecution.getVariable(DEPLOY_STATUS) == null) {
//
//            logger.info(String.format("ServiceTask:%s 开始", delegateExecution.getCurrentActivityId()));
//
//            String[] ids = delegateExecution.getCurrentActivityId().split("\\.");
//            Long pipelineId = Long.parseLong(ids[1]);
//            Long stageId = Long.parseLong(ids[2]);
//            Long taskId = Long.parseLong(ids[3]);
//            delegateExecution.getProcessInstanceId();
//
//        devopsServiceRepository.autoDeploy(stageId, taskId);
//            int[] count = {0};
//            boolean[] success = {false};
//            Runnable runnable = () -> {
//                while (!Thread.currentThread().isInterrupted()) {
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                    }
//                    count[0] = count[0] + 1;
//                    String deployResult = devopsServiceRepository.getAutoDeployTaskStatus(stageId, taskId);
//                    logger.info(deployResult);
//                    if (SUCCRESS.equals(deployResult)) {
//                        success[0] = true;
//                        Thread.currentThread().interrupt();
//                    }
//                    //自动部署失败或者执行20s以上没反应也重置为失败
//                    if (FAILED.equals(deployResult) || count[0] == 10) {
//                        devopsServiceRepository.setAutoDeployTaskStatus(pipelineId, stageId, taskId, false);
//                        Thread.currentThread().interrupt();
//                    }
//                }
//            };
//            Thread thread = new Thread(runnable);
//            thread.start();
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                logger.info(e.getMessage());
//            }
//            if (success[0]) {
//                devopsServiceRepository.setAutoDeployTaskStatus(pipelineId, stageId, taskId, true);
//                logger.info(String.format("ServiceTask:%s  结束", delegateExecution.getCurrentActivityId()));
//            } else {
//                delegateExecution.setVariable(DEPLOY_STATUS, "failed");
//                logger.info(String.format("ServiceTask:%s  失败", delegateExecution.getCurrentActivityId()));
//            }
//        }
    }
}
