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
}
