package io.choerodon.workflow.infra.mapper;

import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;

import java.util.List;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/09 17:34
 */
public interface PersonalTodoC7nMapper {
    List<PersonalTodoViewDTO> selectPersonalTodo(PersonalTodoQueryDTO queryDTO);
    List<PersonalTodoViewDTO> selectPersonalTask(PersonalTodoQueryDTO queryDTO);
    List<ParticipatedDTO> selectMineParticipated(PersonalTodoQueryDTO queryDTO);
    List<SubmittedDTO> selectMineSubmitted(PersonalTodoQueryDTO queryDTO);
    List<CarbonCopyDTO> selectMineCarbonCopied(PersonalTodoQueryDTO queryDTO);
    List<BaseViewDTO> selectSubProcess(PersonalTodoQueryDTO queryDTO);
}
