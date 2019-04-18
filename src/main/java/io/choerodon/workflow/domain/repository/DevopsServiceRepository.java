package io.choerodon.workflow.domain.repository;


/**
 * Created by Sheep on 2019/4/15.
 */
public interface DevopsServiceRepository {


    void autoDeploy(Long stageRecordId, Long taskId);

    void setAutoDeployTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskId, Boolean status);

    Boolean getAutoDeployTaskStatus(Long stageRecordId, Long taskId);
}
