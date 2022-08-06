package io.choerodon.workflow.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/09 17:34
 */
public interface PersonalTodoC7nMapper {
    List<PersonalTodoViewDTO> selectPersonalTodo(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds") List<Long> backlogIds);

    /**
     * 晓燕说没有这个功能, 不知道是干啥的
     * @param queryDTO queryDTO
     * @param backlogIds backlogIds
     * @return result
     */
    List<PersonalTodoViewDTO> selectMobilePersonalTask(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds")List<Long> backlogIds);
    List<ParticipatedDTO> selectMineParticipated(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds")List<Long> backlogIds);
    List<SubmittedDTO> selectMineSubmitted(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds")List<Long> backlogIds);
    List<CarbonCopyDTO> selectMineCarbonCopied(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds") List<Long> backlogIds);
    List<BaseViewDTO> selectSubProcess(@Param("queryDTO") PersonalTodoQueryDTO queryDTO, @Param("backlogIds") List<Long> backlogIds);
}
