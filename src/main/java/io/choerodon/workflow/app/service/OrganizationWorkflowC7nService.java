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
     * 已初始化的工作流，重新导入新的审批人规则和流程变量
     *
     * @param tenantId 租户ID
     */
    void reimportWorkflow(Long tenantId);

    /**
     * 检查租户下是否存在某个流程分类
     * @param tenantId 租户ID
     * @param typeCode 流程分类Code
     * @return 是否存在
     */
    boolean validInitDefType(Long tenantId, String typeCode);
}
