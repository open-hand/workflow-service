package io.choerodon.workflow.infra.feign.fallback;

import io.choerodon.workflow.infra.feign.DevopsServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Sheep on 2019/4/15.
 */
public class DevopsServiceClientFallBack implements DevopsServiceClient {
    @Override
    public ResponseEntity autoDeploy(Long stageRecordId, Long taskId) {
        return new ResponseEntity("error.auto.deploy", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity setAutoDeployTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskId, Boolean status) {
        return new ResponseEntity("error.set.auto.deploy.task.status", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> getAutoDeployTaskStatus(Long stageRecordId, Long taskId) {
        return new ResponseEntity("error.get.auto.deploy.task.status", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
