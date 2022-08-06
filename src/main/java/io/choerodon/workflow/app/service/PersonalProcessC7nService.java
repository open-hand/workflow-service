package io.choerodon.workflow.app.service;


import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.workflow.api.vo.RunTaskHistoryVO;

import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:21:45
 */
public interface PersonalProcessC7nService {

    List<RunTaskHistoryVO> listApproveHistoryByInstanceId(Long tenantId, Long instanceId);

    Page<PersonalTodoViewDTO> selectPersonalTodo(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    /**
     * 晓燕说没有这个功能, 不知道是干啥的
     * @param tenantId tenantId
     * @param pageRequest pageRequest
     * @param queryDTO queryDTO
     * @param backlogIds backlogIds
     * @return result
     */
    Page<PersonalTodoViewDTO> pageDone(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    Page<ParticipatedDTO> mineParticipated(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    Page<SubmittedDTO> mineSubmitted(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    Page<CarbonCopyDTO> mineCarbonCopied(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);
}
