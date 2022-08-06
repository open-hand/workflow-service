package io.choerodon.workflow.app.service;

import io.choerodon.workflow.domain.entity.BacklogInstanceRel;

/**
 * @author zhaotianxin
 * @date 2021-04-12 16:20
 */
public interface BacklogInstanceRelService {
   BacklogInstanceRel create(Long organizationId, Long instanceId, Long backlogId);
}
