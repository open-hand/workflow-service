package io.choerodon.workflow.api.vo;

import java.util.List;


/**
 * Created by Sheep on 2019/4/2.
 */
public class DevopsPipelineVO {

    private Long pipelineRecordId;
    private Long pipelineId;
    private String pipelineName;
    private String businessKey;
    private List<String> userNames;
    private Boolean multiAssign;
    private List<DevopsPipelineStageVO> stages;


    public Long getPipelineRecordId() {
        return pipelineRecordId;
    }

    public void setPipelineRecordId(Long pipelineRecordId) {
        this.pipelineRecordId = pipelineRecordId;
    }

    public List<DevopsPipelineStageVO> getStages() {
        return stages;
    }

    public void setStages(List<DevopsPipelineStageVO> stages) {
        this.stages = stages;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    public Boolean getMultiAssign() {
        return multiAssign;
    }

    public void setMultiAssign(Boolean multiAssign) {
        this.multiAssign = multiAssign;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }
}
