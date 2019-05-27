package io.choerodon.workflow.domain.repository;


/**
 * Created by Sheep on 2019/4/15.
 */
public interface DevopsServiceRepository {


    void autoDeploy(Long stageRecordId, Long taskRecordId);

    void setAutoDeployTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskRecordId, Boolean status);

    String getAutoDeployTaskStatus(Long stageRecordId, Long taskRecordId);
}
