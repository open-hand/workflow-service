package io.choerodon.workflow.infra.delegate;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.workflow.infra.feginoperator.DevopsServiceRepository;

/**
 * @author scp
 * @date 2020/7/3
 * @description
 */
@Component
public class DevopsCdDeployDelegate implements JavaDelegate {

    @Autowired
    DevopsServiceRepository devopsServiceRepository;
    @Autowired
    ProcessRuntime processRuntime;

    private Logger logger = LoggerFactory.getLogger(DevopsDeployDelegate.class);

    private final String SUCCRESS = "success";
    private final String FAILED = "failed";

    @Override
    public void execute(DelegateExecution delegateExecution) {

        // 1.
        String[] ids = delegateExecution.getCurrentActivityId().split("\\.");
        Long pipelineRecordId = Long.parseLong(ids[1]);
        Long stageRecordId = Long.parseLong(ids[2]);
        Long taskRecordId = Long.parseLong(ids[3]);
        logger.info(String.format("ServiceTask:%s 开始", delegateExecution.getCurrentActivityId()));


        // 2.
        // todo 调用部署任务方法
        devopsServiceRepository.autoDeploy(stageRecordId, taskRecordId);

        int[] count = {0};

        Runnable runnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {

                }
                count[0] = count[0] + 1;
                // todo 获取状态
                String deployResult = devopsServiceRepository.getAutoDeployTaskStatus(stageRecordId, taskRecordId);
                logger.info(deployResult);
                if (SUCCRESS.equals(deployResult)) {
                    logger.info(String.format("ServiceTask:%s  结束,任务执行成功！", delegateExecution.getCurrentActivityId()));
                    Thread.currentThread().interrupt();
                }
                //自动部署失败或者执行3min以上没反应也重置为失败
                if (FAILED.equals(deployResult) || count[0] == 60) {
                    // todo 强制失败
                    devopsServiceRepository.setAutoDeployTaskStatus(pipelineRecordId, stageRecordId, taskRecordId, false);
                    logger.info(String.format("ServiceTask:%s  结束,任务执行失败！", delegateExecution.getCurrentActivityId()));
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

    }
}

