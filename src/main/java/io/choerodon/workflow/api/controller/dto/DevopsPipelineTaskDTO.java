package io.choerodon.workflow.api.controller.dto;

import java.util.List;


/**
 * Created by Sheep on 2019/4/2.
 */
public class DevopsPipelineTaskDTO {

    private Long taskId;
    private String taskName;
    private List<String> usernames;
    private String taskType;
    private boolean isMultiAssign;
    private boolean isSign;


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Boolean getMultiAssign() {
        return isMultiAssign;
    }

    public void setMultiAssign(Boolean isMultiAssign) {
        this.isMultiAssign = isMultiAssign;
    }

    public Boolean getSign() {
        return isSign;
    }

    public void setSign(Boolean isSign) {
        this.isSign = isSign;
    }
}
