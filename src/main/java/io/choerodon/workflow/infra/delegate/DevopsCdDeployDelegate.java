package io.choerodon.workflow.infra.delegate;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.workflow.infra.enums.JobStatusEnum;
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


    @Override
    public void execute(DelegateExecution delegateExecution) {

        // 1.
        String[] ids = delegateExecution.getCurrentActivityId().split("\\.");
        Long pipelineRecordId = Long.parseLong(ids[1]);
        Long stageRecordId = Long.parseLong(ids[2]);
        Long taskRecordId = Long.parseLong(ids[3]);
        logger.info(String.format("ServiceTask:%s 开始", delegateExecution.getCurrentActivityId()));


        // 2.
        devopsServiceRepository.envAutoDeploy(pipelineRecordId, stageRecordId, taskRecordId);

        // 3.
        //自动部署失败或者执行3min以上没反应也重置为失败
        int[] count = {0};
        Boolean[] status = {false};
        Runnable runnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    logger.warn("error.thread.sleep");
                }
                count[0] = count[0] + 1;

                String deployResult = devopsServiceRepository.getJobStatus(pipelineRecordId, stageRecordId, taskRecordId);
                logger.info(deployResult);
                if (JobStatusEnum.SUCCESS.value().equals(deployResult) || JobStatusEnum.SKIPPED.value().equals(deployResult)) {
                    status[0] = true;
                    Thread.currentThread().interrupt();
                }

                if (JobStatusEnum.FAILED.value().equals(deployResult) || count[0] == 60) {
                    status[0] = false;

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

        // 4.
        devopsServiceRepository.setAppDeployStatus(pipelineRecordId, stageRecordId, taskRecordId, status[0]);
        logger.info(String.format("cd ServiceTask:%s  结束,任务执行状态为%s", delegateExecution.getCurrentActivityId(), status[0]));
    }
}

