package io.choerodon.workflow.infra.repository.impl;

import org.springframework.stereotype.Repository;

import io.choerodon.workflow.domain.entity.ProjectWorkflowRel;
import io.choerodon.workflow.domain.repository.ProjectWorkflowRelRepository;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

@Repository
public class ProjectWorkflowRelRepositoryImpl extends BaseRepositoryImpl<ProjectWorkflowRel> implements ProjectWorkflowRelRepository {
}
