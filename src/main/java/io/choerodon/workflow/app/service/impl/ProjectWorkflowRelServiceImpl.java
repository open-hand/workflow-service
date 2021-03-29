package io.choerodon.workflow.app.service.impl;

import io.choerodon.workflow.api.vo.ProjectWorkflowRelVO;
import io.choerodon.workflow.app.service.ProjectWorkflowRelService;
import io.choerodon.workflow.infra.dto.ProjectWorkflowRelDTO;
import io.choerodon.workflow.infra.mapper.ProjectWorkflowRelMapper;
import io.choerodon.core.exception.CommonException;
import org.hzero.workflow.def.domain.entity.DefWorkflow;
import org.hzero.workflow.def.infra.mapper.DefWorkflowMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @author zhaotianxin
 * @date 2021-03-08 19:16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectWorkflowRelServiceImpl implements ProjectWorkflowRelService {

    @Autowired
    private ProjectWorkflowRelMapper projectWorkflowRelMapper;

    @Autowired
    private DefWorkflowMapper defWorkflowMapper;

    @Override
    public ProjectWorkflowRelDTO createOrUpdate(Long projectId, ProjectWorkflowRelDTO projectWorkflowRelDTO) {
        ProjectWorkflowRelDTO workflowRelDTO = queryByProjectId(projectId);
        if (!ObjectUtils.isEmpty(workflowRelDTO)) {
            projectWorkflowRelMapper.deleteByPrimaryKey(workflowRelDTO.getId());
        }
        if (!ObjectUtils.isEmpty(projectWorkflowRelDTO.getFlowCode())) {
            projectWorkflowRelDTO.setProjectId(projectId);
            projectWorkflowRelDTO.setOrganizationId(projectWorkflowRelDTO.getOrganizationId());
            baseCreate(projectWorkflowRelDTO);
        }
        return projectWorkflowRelDTO;
    }

    private void baseCreate(ProjectWorkflowRelDTO projectWorkflowRelDTO) {
        if (projectWorkflowRelMapper.insertSelective(projectWorkflowRelDTO) != 1) {
            throw new CommonException("error.insert.project.workflow.rel");
        }
    }

    private ProjectWorkflowRelDTO queryByProjectId(Long projectId) {
        ProjectWorkflowRelDTO queryDTO = new ProjectWorkflowRelDTO();
        queryDTO.setProjectId(projectId);
        return projectWorkflowRelMapper.selectOne(queryDTO);
    }

    @Override
    public ProjectWorkflowRelVO queryProjectWorkflow(Long projectId) {
        ProjectWorkflowRelDTO projectWorkflowRelDTO = queryByProjectId(projectId);
        if (ObjectUtils.isEmpty(projectWorkflowRelDTO)) {
            return null;
        }
        ProjectWorkflowRelVO projectWorkflowRelVO = new ProjectWorkflowRelVO();
        BeanUtils.copyProperties(projectWorkflowRelDTO, projectWorkflowRelVO);
        // 查询流程的信息
        DefWorkflow workflow = new DefWorkflow();
        workflow.setFlowCode(projectWorkflowRelDTO.getFlowCode());
        workflow.setTenantId(projectWorkflowRelDTO.getOrganizationId());
        DefWorkflow defWorkflow = defWorkflowMapper.selectOne(workflow);
        if (ObjectUtils.isEmpty(defWorkflow)) {
            return null;
        }
        projectWorkflowRelVO.setDefWorkflow(defWorkflow);
        return projectWorkflowRelVO;
    }
}
