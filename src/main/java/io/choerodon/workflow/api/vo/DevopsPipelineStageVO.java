package io.choerodon.workflow.api.vo;

import java.util.List;

/**
 * Created by Sheep on 2019/4/2.
 */
public class DevopsPipelineStageVO {


    private String stageName;
    private String stageTaskName;
    private Long stageRecordId;
    private List<DevopsPipelineTaskVO> tasks;
    private Boolean parallel;
    private List<String> usernames;
    private String nextStageTriggerType;
    private boolean multiAssign;


    public Long getStageRecordId() {
        return stageRecordId;
    }

    public void setStageRecordId(Long stageRecordId) {
        this.stageRecordId = stageRecordId;
    }

    public List<DevopsPipelineTaskVO> getTasks() {
        return tasks;
    }

    public void setTasks(List<DevopsPipelineTaskVO> tasks) {
        this.tasks = tasks;
    }

    public String getNextStageTriggerType() {
        return nextStageTriggerType;
    }

    public void setNextStageTriggerType(String nextStageTriggerType) {
        this.nextStageTriggerType = nextStageTriggerType;
    }

    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }


    public boolean isMultiAssign() {
        return multiAssign;
    }

    public void setMultiAssign(boolean multiAssign) {
        this.multiAssign = multiAssign;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStageTaskName() {
        return stageTaskName;
    }

    public void setStageTaskName(String stageTaskName) {
        this.stageTaskName = stageTaskName;
    }

}
