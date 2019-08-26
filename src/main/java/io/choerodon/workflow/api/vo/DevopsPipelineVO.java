package io.choerodon.workflow.api.vo;

import java.util.List;


/**
 * Created by Sheep on 2019/4/2.
 */
public class DevopsPipelineVO {

    private Long pipelineRecordId;
    private String businessKey;
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
}
