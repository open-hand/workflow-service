package io.choerodon.workflow.api.vo;

import org.hzero.workflow.def.domain.entity.DefWorkflow;

/**
 * @author zhaotianxin
 * @date 2021-03-08 19:14
 */
public class ProjectWorkflowRelVO {
    private Long id;

    private String flowCode;

    private Long projectId;

    private Long organizationId;

    private DefWorkflow defWorkflow;

    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public DefWorkflow getDefWorkflow() {
        return defWorkflow;
    }

    public void setDefWorkflow(DefWorkflow defWorkflow) {
        this.defWorkflow = defWorkflow;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}
