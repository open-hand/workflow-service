package io.choerodon.workflow.app.service;

import io.choerodon.workflow.api.vo.RunTaskHistoryVO;

import java.util.List;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:21:45
 */
public interface PersonalProcessC7nService {

    List<RunTaskHistoryVO> listApproveHistoryByInstanceId(Long tenantId, Long instanceId);
}
