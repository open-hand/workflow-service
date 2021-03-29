package io.choerodon.workflow.app.service;

import io.choerodon.workflow.api.vo.ProjectWorkflowRelVO;
import io.choerodon.workflow.infra.dto.ProjectWorkflowRelDTO;

/**
 * @author zhaotianxin
 * @date 2021-03-08 19:01
 */
public interface ProjectWorkflowRelService {

    ProjectWorkflowRelDTO createOrUpdate(Long projectId, ProjectWorkflowRelDTO projectWorkflowRelDTO);

    ProjectWorkflowRelVO queryProjectWorkflow(Long projectId);
}
