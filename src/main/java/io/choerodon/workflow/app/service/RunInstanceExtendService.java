package io.choerodon.workflow.app.service;

import io.choerodon.workflow.infra.dto.RunInstanceExtendDTO;

/**
 * @author zhaotianxin
 * @date 2021-04-09 17:05
 */
public interface RunInstanceExtendService {

    RunInstanceExtendDTO create(Long organizationId, Long instanceId);
}
