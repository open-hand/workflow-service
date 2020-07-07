package io.choerodon.workflow.infra.feginoperator.impl;

import java.util.Optional;

import feign.FeignException;

import io.choerodon.core.exception.CommonException;
import io.choerodon.workflow.infra.feginoperator.DevopsServiceRepository;
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
    public void autoDeploy(Long stageRecordId, Long taskRecordId) {

        try {
            devopsServiceClient.autoDeploy(stageRecordId, taskRecordId);
        } catch (FeignException e) {
            throw new CommonException(e);
        }

    }

    @Override
    public void setAutoDeployTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskRecordId, Boolean status) {

        try {
            devopsServiceClient.setAutoDeployTaskStatus(pipelineRecordId, stageRecordId, taskRecordId, status);
        } catch (FeignException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public String getAutoDeployTaskStatus(Long stageRecordId, Long taskRecordId) {

        try {
            ResponseEntity<String> responseEntity = devopsServiceClient.getAutoDeployTaskStatus(stageRecordId, taskRecordId);
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

    @Override
    public Boolean cdHostDeploy(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId) {
        try {
            ResponseEntity<Boolean> responseEntity = devopsServiceClient.cdHostDeploy(cdPipelineRecordId, cdStageRecordId, cdJobRecordId);
            Optional<Boolean> result = Optional.ofNullable(responseEntity.getBody());
            if (result.isPresent()) {
                return result.get();
            } else {
                throw new CommonException("error.deploy.cd.host");
            }
        } catch (FeignException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public Boolean envAutoDeploy(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId) {
        try {
            ResponseEntity<Boolean> responseEntity = devopsServiceClient.envAutoDeploy(cdPipelineRecordId, cdStageRecordId, cdJobRecordId);
            Optional<Boolean> result = Optional.ofNullable(responseEntity.getBody());
            if (result.isPresent()) {
                return result.get();
            } else {
                throw new CommonException("error.deploy.env.auto");
            }
        } catch (FeignException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public void setAppDeployStatus(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId, Boolean status) {
        try {
            ResponseEntity responseEntity = devopsServiceClient.setAppDeployStatus(cdPipelineRecordId, cdStageRecordId, cdJobRecordId, status);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new CommonException("error.update.deploy.job.status");
            }
        } catch (FeignException e) {
            throw new CommonException(e);
        }
    }

    @Override
    public String getJobStatus(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId) {
        try {
            ResponseEntity<String> responseEntity = devopsServiceClient.getJobStatus(cdPipelineRecordId, cdStageRecordId, cdJobRecordId);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new CommonException("error.get.deploy.job.status");
            }
            return responseEntity.getBody();
        } catch (FeignException e) {
            throw new CommonException(e);
        }
    }
}
