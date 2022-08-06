package io.choerodon.workflow.domain.repository;

import java.util.List;

import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/09 17:21
 */
public interface PersonalTodoC7nRepository {
    List<PersonalTodoViewDTO> selectPersonalTodo(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    /**
     * 晓燕说没有这个功能, 不知道是干啥的
     * @param doneParam queryDTO
     * @param backlogIds backlogIds
     * @return result
     */
    List<PersonalTodoViewDTO> selectMobilePersonalTask(PersonalTodoQueryDTO doneParam, List<Long> backlogIds);

    List<ParticipatedDTO> selectMineParticipated(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    List<SubmittedDTO> selectMineSubmitted(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    List<CarbonCopyDTO> selectMineCarbonCopied(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);

    List<BaseViewDTO> selectSubProcess(PersonalTodoQueryDTO queryDTO, List<Long> backlogIds);
}
