package io.choerodon.workflow.domain.repository;

import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;

import java.util.List;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/09 17:21
 */
public interface PersonalTodoC7nRepository {
    List<PersonalTodoViewDTO> selectPersonalTodo(Long tenantId, PersonalTodoQueryDTO queryDTO);

    List<PersonalTodoViewDTO> selectMobilePersonalTask(PersonalTodoQueryDTO doneParam);

    List<ParticipatedDTO> selectMineParticipated(Long tenantId, PersonalTodoQueryDTO queryDTO);

    List<SubmittedDTO> selectMineSubmitted(Long tenantId, PersonalTodoQueryDTO queryDTO);

    List<CarbonCopyDTO> selectMineCarbonCopied(Long tenantId, PersonalTodoQueryDTO queryDTO);

    List<BaseViewDTO> selectSubProcess(PersonalTodoQueryDTO queryDTO);
}
