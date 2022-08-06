package io.choerodon.workflow.infra.repository.impl;

import org.springframework.stereotype.Repository;

import io.choerodon.workflow.domain.entity.BacklogInstanceRel;
import io.choerodon.workflow.domain.repository.BacklogInstanceRelRepository;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

@Repository
public class BacklogInstanceRelRepositoryImpl extends BaseRepositoryImpl<BacklogInstanceRel> implements BacklogInstanceRelRepository {
}
