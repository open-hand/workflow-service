package io.choerodon.workflow.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;

import java.util.List;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/09 17:34
 */
public interface PersonalTodoC7nMapper {
    List<PersonalTodoViewDTO> selectPersonalTodo(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds") List<Long> backlogIds);
    List<PersonalTodoViewDTO> selectPersonalTask(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds")List<Long> backlogIds);
    List<ParticipatedDTO> selectMineParticipated(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds")List<Long> backlogIds);
    List<SubmittedDTO> selectMineSubmitted(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds")List<Long> backlogIds);
    List<CarbonCopyDTO> selectMineCarbonCopied(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds") List<Long> backlogIds);
    List<BaseViewDTO> selectSubProcess(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds") List<Long> backlogIds);
}
