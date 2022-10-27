package io.choerodon.workflow.app.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.workflow.api.vo.ProjectWorkflowRelVO;
import io.choerodon.workflow.app.service.ProjectWorkflowRelService;
import io.choerodon.workflow.domain.entity.ProjectWorkflowRel;
import io.choerodon.workflow.domain.repository.ProjectWorkflowRelRepository;

import org.hzero.core.base.BaseConstants;
import org.hzero.workflow.def.domain.entity.DefWorkflow;
import org.hzero.workflow.def.domain.repository.DefWorkflowRepository;

/**
 * @author zhaotianxin
 * @date 2021-03-08 19:16
 */
@Service
public class ProjectWorkflowRelServiceImpl implements ProjectWorkflowRelService {

    @Autowired
    private ProjectWorkflowRelRepository projectWorkflowRelRepository;

    @Autowired
    private DefWorkflowRepository defWorkflowRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectWorkflowRel createOrUpdateOrRemove(Long projectId, ProjectWorkflowRel projectWorkflowRel) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        Assert.notNull(projectWorkflowRel, BaseConstants.ErrorCode.NOT_NULL);
        Assert.notNull(projectWorkflowRel.getOrganizationId(), BaseConstants.ErrorCode.NOT_NULL);
        final String flowCode = projectWorkflowRel.getFlowCode();
        ProjectWorkflowRel projectWorkflowRelInDb = queryByProjectId(projectId);
        if (projectWorkflowRelInDb != null) {
            if(StringUtils.isNotBlank(flowCode)) {
                projectWorkflowRelInDb.setFlowCode(flowCode);
                this.projectWorkflowRelRepository.updateOptional(projectWorkflowRelInDb, ProjectWorkflowRel.FIELD_FLOW_CODE);
            } else {
                this.projectWorkflowRelRepository.deleteByPrimaryKey(projectWorkflowRelInDb);
            }
        } else {
            projectWorkflowRel.setProjectId(projectId);
            this.projectWorkflowRelRepository.insertSelective(projectWorkflowRel);
        }
        return projectWorkflowRel;
    }

    /**
     * 根据项目ID查询关联关系
     * @param projectId 项目ID
     * @return 查询结果
     */
    private ProjectWorkflowRel queryByProjectId(Long projectId) {
        ProjectWorkflowRel queryDTO = new ProjectWorkflowRel();
        queryDTO.setProjectId(projectId);
        return projectWorkflowRelRepository.selectOne(queryDTO);
    }

    @Override
    public ProjectWorkflowRelVO queryProjectWorkflow(Long projectId) {
        ProjectWorkflowRel projectWorkflowRel = queryByProjectId(projectId);
        if (projectWorkflowRel == null) {
            return null;
        }
        ProjectWorkflowRelVO projectWorkflowRelVO = new ProjectWorkflowRelVO();
        BeanUtils.copyProperties(projectWorkflowRel, projectWorkflowRelVO);
        // 查询流程的信息
        DefWorkflow workflow = new DefWorkflow();
        workflow.setFlowCode(projectWorkflowRel.getFlowCode());
        workflow.setTenantId(projectWorkflowRel.getOrganizationId());
        DefWorkflow defWorkflow = defWorkflowRepository.selectOne(workflow);
        if (defWorkflow == null) {
            return null;
        }
        projectWorkflowRelVO.setDefWorkflow(defWorkflow);
        return projectWorkflowRelVO;
    }
}
