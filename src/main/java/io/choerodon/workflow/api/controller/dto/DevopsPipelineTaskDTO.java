package io.choerodon.workflow.api.controller.dto;

import java.util.List;


/**
 * Created by Sheep on 2019/4/2.
 */
public class DevopsPipelineTaskDTO {

    private Long taskrecordId;
    private String taskName;
    private List<String> usernames;
    private String taskType;
    private boolean multiAssign;
    private boolean sign;


    public Long getTaskrecordId() {
        return taskrecordId;
    }

    public void setTaskrecordId(Long taskrecordId) {
        this.taskrecordId = taskrecordId;
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

    public boolean isMultiAssign() {
        return multiAssign;
    }

    public void setMultiAssign(boolean multiAssign) {
        this.multiAssign = multiAssign;
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }
}
