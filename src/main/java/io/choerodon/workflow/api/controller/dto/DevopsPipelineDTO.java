package io.choerodon.workflow.api.controller.dto;

import java.util.List;


/**
 * Created by Sheep on 2019/4/2.
 */
public class DevopsPipelineDTO {

    private Long bussinessId;
    private List<DevopsPipelineStageDTO> stages;


    public Long getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(Long bussinessId) {
        this.bussinessId = bussinessId;
    }

    public List<DevopsPipelineStageDTO> getStages() {
        return stages;
    }

    public void setStages(List<DevopsPipelineStageDTO> stages) {
        this.stages = stages;
    }
}
