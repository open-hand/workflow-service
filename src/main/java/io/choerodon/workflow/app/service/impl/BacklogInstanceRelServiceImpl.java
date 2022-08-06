package io.choerodon.workflow.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.workflow.app.service.BacklogInstanceRelService;
import io.choerodon.workflow.domain.entity.BacklogInstanceRel;
import io.choerodon.workflow.domain.repository.BacklogInstanceRelRepository;

import org.hzero.mybatis.helper.UniqueHelper;

/**
 * @author zhaotianxin
 * @date 2021-04-12 16:22
 */
@Service
public class BacklogInstanceRelServiceImpl implements BacklogInstanceRelService {
    @Autowired
    private BacklogInstanceRelRepository backlogInstanceRelRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BacklogInstanceRel create(Long organizationId, Long instanceId, Long backlogId) {
        BacklogInstanceRel backlogInstanceRel = new BacklogInstanceRel(instanceId, organizationId, backlogId);
        if(UniqueHelper.valid(backlogInstanceRel)) {
            this.backlogInstanceRelRepository.insertSelective(backlogInstanceRel);
        }
        return backlogInstanceRel;
    }
}
