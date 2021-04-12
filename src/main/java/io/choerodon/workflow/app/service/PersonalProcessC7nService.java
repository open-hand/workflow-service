package io.choerodon.workflow.app.service;

import org.hzero.workflow.personal.api.dto.PersonalTodoDTO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.workflow.api.vo.RunTaskHistoryVO;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.CarbonCopyDTO;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.ParticipatedDTO;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.PersonalTodoQueryDTO;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.PersonalTodoViewDTO;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.SubmittedDTO;

import java.util.List;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:21:45
 */
public interface PersonalProcessC7nService {

    List<RunTaskHistoryVO> listApproveHistoryByInstanceId(Long tenantId, Long instanceId);

    Page<PersonalTodoViewDTO> pageByOptions(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    Page<PersonalTodoViewDTO> pageDone(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    Page<ParticipatedDTO> mineParticipated(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    Page<SubmittedDTO> mineSubmitted(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    Page<CarbonCopyDTO> mineCarbonCopied(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);
}
