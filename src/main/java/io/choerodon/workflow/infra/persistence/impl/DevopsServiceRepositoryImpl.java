package io.choerodon.workflow.infra.persistence.impl;

import java.util.Optional;

import feign.FeignException;
import io.choerodon.core.exception.CommonException;
import io.choerodon.workflow.domain.repository.DevopsServiceRepository;
import io.choerodon.workflow.infra.feign.DevopsServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by Sheep on 2019/4/15.
 */

@Service
public class DevopsServiceRepositoryImpl implements DevopsServiceRepository {


    @Autowired
    DevopsServiceClient devopsServiceClient;

    @Override
    public void autoDeploy(Long stageRecordId, Long taskId, String processInstanceId) {

        try {
            devopsServiceClient.autoDeploy(stageRecordId, taskId, processInstanceId);
        } catch (FeignException e) {
            throw new CommonException(e);
        }

    }

    @Override
    public void setAutoDeployTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskId, Boolean status) {

        try {
            devopsServiceClient.setAutoDeployTaskStatus(pipelineRecordId, stageRecordId, taskId, status);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public String getAutoDeployTaskStatus(Long stageRecordId, Long taskId) {

        try {
            ResponseEntity<String> responseEntity = devopsServiceClient.getAutoDeployTaskStatus(stageRecordId, taskId);
            Optional<String> result = Optional.ofNullable(responseEntity.getBody());
            if (result.isPresent()) {
                return result.get();
            } else {
                throw new CommonException("error.get.auto.deploy.task.status");
            }
        } catch (FeignException e) {
            throw new CommonException(e);
        }
    }
}
