package io.choerodon.workflow.domain.repository;

import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;

import java.util.List;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/09 17:21
 */
public interface PersonalTodoC7nRepository {
    List<PersonalTodoViewDTO> selectPersonalTodo(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    List<PersonalTodoViewDTO> selectMobilePersonalTask(PersonalTodoQueryDTO doneParam, List<Long> backlogIds);

    List<ParticipatedDTO> selectMineParticipated(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    List<SubmittedDTO> selectMineSubmitted(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    List<CarbonCopyDTO> selectMineCarbonCopied(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    List<BaseViewDTO> selectSubProcess(PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);
}
