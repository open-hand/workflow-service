package io.choerodon.workflow.infra.delegate;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.workflow.infra.constant.PipelineConstants;
import io.choerodon.workflow.infra.enums.JobStatusEnum;
import io.choerodon.workflow.infra.feginoperator.DevopsServiceRepository;

/**
 * @author scp
 * @date 2020/7/3
 * @description
 */
@Component
public class DevopsCdApiTestDelegate implements JavaDelegate {

    @Autowired
    DevopsServiceRepository devopsServiceRepository;
    @Autowired
    ProcessRuntime processRuntime;

    private Logger logger = LoggerFactory.getLogger(DevopsCdApiTestDelegate.class);


    @Override
    public void execute(DelegateExecution delegateExecution) {

        // 1.
        String[] ids = delegateExecution.getCurrentActivityId().split("\\.");
        Long pipelineRecordId = Long.parseLong(ids[1]);
        Long stageRecordId = Long.parseLong(ids[2]);
        Long taskRecordId = Long.parseLong(ids[3]);
        String deployJobName = ids[4];

        if (!PipelineConstants.NOT_WAIT_DEPLOY_JOB.equals(deployJobName)) {
            int[] count = {0};
            Boolean[] status = {false};
            Runnable runnable = () -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        logger.warn("error.thread.sleep");
                    }
                    count[0] = count[0] + 1;

                    String deployResult = devopsServiceRepository.getDeployStatus(pipelineRecordId, deployJobName);
                    logger.info(deployResult);
                    if (JobStatusEnum.SUCCESS.value().equals(deployResult)) {
                        logger.info("cd ServiceTask: {}, 关联部署任务部署成功，开始执行API测试任务", delegateExecution.getCurrentActivityId());
                        status[0] = true;
                        Thread.currentThread().interrupt();

                    }
                    if (count[0] == 20) {
                        logger.info("cd ServiceTask: {}, 关联部署任务部署超时，API测试任务执行失败", delegateExecution.getCurrentActivityId());
                        status[0] = false;
                        devopsServiceRepository.setAppDeployStatus(pipelineRecordId, stageRecordId, taskRecordId, false);
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
            if (!status[0]) {
                throw new CommonException("error.execute.service.task");
            }
        }

        logger.info(String.format("ServiceTask:%s 开始", delegateExecution.getCurrentActivityId()));

        // 2.
        devopsServiceRepository.executeApiTestTask(pipelineRecordId, stageRecordId, taskRecordId);

    }
}

