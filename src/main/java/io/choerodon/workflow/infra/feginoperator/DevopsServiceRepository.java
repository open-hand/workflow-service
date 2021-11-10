package io.choerodon.workflow.infra.feginoperator;


/**
 * Created by Sheep on 2019/4/15.
 */
public interface DevopsServiceRepository {


    void autoDeploy(Long stageRecordId, Long taskRecordId);

    void setAutoDeployTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskRecordId, Boolean status);

    String getAutoDeployTaskStatus(Long stageRecordId, Long taskRecordId);

    void cdHostDeploy(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId);

    void envAutoDeploy(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId);

    void setAppDeployStatus(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId, Boolean status);

    String getJobStatus(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId);

    void executeApiTestTask(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId);

    String getDeployStatus(Long cdPipelineRecordId, String deployJobName);

    void executeExternalApproval(Long cdPipelineRecordId, Long cdStageRecordId, Long cdJobRecordId);

    void hzeroDeploy(Long detailsId);

//    void setExternalApprovalTaskStatus(Long pipelineRecordId, Long stageRecordId, Long taskRecordId, boolean execReslut);
}
