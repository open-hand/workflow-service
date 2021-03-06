package io.choerodon.workflow.app.service;

import io.choerodon.workflow.infra.dto.BacklogInstanceRelDTO;

/**
 * @author zhaotianxin
 * @date 2021-04-12 16:20
 */
public interface BacklogInstanceRelService {
   BacklogInstanceRelDTO create(Long organizationId, Long instanceId, Long backlogId);
}
