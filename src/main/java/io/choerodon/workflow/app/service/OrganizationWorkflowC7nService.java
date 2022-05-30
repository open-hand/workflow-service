package io.choerodon.workflow.app.service;

/**
 * @author chihao.ran@hand-china.com
 * 2021/03/30 14:54
 */
public interface OrganizationWorkflowC7nService {

    /**
     * 初始化组织层流程图
     * @param tenantId 组织id
     */
    void initDefWorkFlows(Long tenantId);

    /**
     * 检查是否初始化组织层流程定义
     * @param tenantId 租户id
     * @return 是否初始化组织层流程定义
     */
    Boolean checkInit(Long tenantId);

    /**
     * 兼容已初始化的工作流，重新导入新的审批人规则流程变量等
     *
     * @param tenantId
     */
    void reimportWorkflow(Long tenantId);
}
