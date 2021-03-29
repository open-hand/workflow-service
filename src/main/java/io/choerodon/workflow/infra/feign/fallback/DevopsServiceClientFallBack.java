package io.choerodon.workflow.infra.feign.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.FeignException;
import io.choerodon.workflow.infra.feign.DevopsServiceClient;

/**
 * Created by Sheep on 2019/4/15.
 */
@Component
public class DevopsServiceClientFallBack implements DevopsServiceClient {
    @Override
    public ResponseEntity autoDeploy(Long stageRecordId, Long taskRecordId) {
        throw new FeignException("error.auto.deploy");
    }

    @Override
    public ResponseEntity setAutoDeployTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskRecordId, Boolean status) {
        throw new FeignException("error.set.auto.deploy.task.status");
    }

    @Override
    public ResponseEntity<String> getAutoDeployTaskStatus(Long stageRecordId, Long taskRecordId) {
        throw new FeignException("error.get.auto.deploy.task.status");
    }

    @Override
    public ResponseEntity cdHostDeploy(Long pipelineRecordId, Long stageRecordId, Long jobRecordId) {
        throw new FeignException("error.get.cd.host.deploy");
    }

    @Override
    public ResponseEntity<Boolean> envAutoDeploy(Long pipelineRecordId, Long stageRecordId, Long jobRecordId) {
        throw new FeignException("error.get.env.auto.deploy");
    }

    @Override
    public ResponseEntity setAppDeployStatus(Long pipelineRecordId, Long stageRecordId, Long jobRecordId, Boolean status) {
        throw new FeignException("error.update.deploy.job.status");
    }

    @Override
    public ResponseEntity<String> getJobStatus(Long pipelineRecordId, Long stageRecordId, Long jobRecordId) {
        throw new FeignException("error.get.deploy.job.status");
    }

    @Override
    public ResponseEntity<Void> executeApiTestTask(Long pipelineRecordId, Long stageRecordId, Long jobRecordId) {
        throw new FeignException("error.execute.api.test.task");
    }

    @Override
    public ResponseEntity<String> getDeployStatus(Long pipelineRecordId, String deployJobName) {
        throw new FeignException("error.get.deploy..status");
    }

    @Override
    public ResponseEntity<Void> executeExternalApprovalTask(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId) {
        throw new FeignException("error.get.deploy..status");
    }

//    @Override
//    public ResponseEntity<Void> setExternalApprovalTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskRecordId, boolean execReslut) {
//        throw new FeignException("error.get.deploy..status");
//    }
}
