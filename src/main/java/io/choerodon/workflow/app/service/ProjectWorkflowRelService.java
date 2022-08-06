package io.choerodon.workflow.app.service;

import io.choerodon.workflow.api.vo.ProjectWorkflowRelVO;
import io.choerodon.workflow.domain.entity.ProjectWorkflowRel;

/**
 * @author zhaotianxin
 * @date 2021-03-08 19:01
 */
public interface ProjectWorkflowRelService {

    /**
     * 创建或更新项目关联的需求审批工作流
     * @param projectId 项目ID
     * @param projectWorkflowRel 关系实体
     * @return 处理后的结果
     */
    ProjectWorkflowRel createOrUpdate(Long projectId, ProjectWorkflowRel projectWorkflowRel);

    /**
     * 根据项目ID查询项目和需求审批工作流的关系对象
     * @param projectId 项目ID
     * @return 项目和需求审批工作流的关系对象
     */
    ProjectWorkflowRelVO queryProjectWorkflow(Long projectId);
}
