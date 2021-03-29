package io.choerodon.workflow.api.vo;

/**
 * @author zhaotianxin
 * @date 2021-03-08 20:39
 */
public class BacklogStartInstanceVO {
    private Long backlogId;

    private String projectCode;

    private Long organizationId;

    public Long getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(Long backlogId) {
        this.backlogId = backlogId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }
}
