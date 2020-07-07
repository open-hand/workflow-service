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
public class DevopsCdHostDelegate implements JavaDelegate {

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
        Boolean status = devopsServiceRepository.cdHostDeploy(pipelineRecordId, stageRecordId, taskRecordId);

        // 3.
        devopsServiceRepository.setAppDeployStatus(pipelineRecordId, stageRecordId, taskRecordId, status);
    }
}

