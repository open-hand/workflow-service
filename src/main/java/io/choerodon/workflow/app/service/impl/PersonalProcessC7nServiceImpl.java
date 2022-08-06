package io.choerodon.workflow.app.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.workflow.api.vo.RunTaskHistoryVO;
import io.choerodon.workflow.app.service.PersonalProcessC7nService;
import io.choerodon.workflow.domain.repository.PersonalTodoC7nRepository;
import io.choerodon.workflow.infra.feign.IamFeignClient;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;
import org.hzero.workflow.def.infra.feign.dto.UserDTO;
import org.hzero.workflow.engine.dao.entity.RunTaskHistory;
import org.hzero.workflow.engine.exception.EmployeeNotFoundException;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;
import org.hzero.workflow.personal.domain.repository.PersonalTodoRepository;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:21:57
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PersonalProcessC7nServiceImpl implements PersonalProcessC7nService {

    @Autowired
    private PersonalTodoC7nRepository personalTodoC7nRepository;

    @Autowired
    private PersonalTodoRepository personalTodoRepository;

    @Autowired
    private IamFeignClient iamFeignClient;

    @Override
    public List<RunTaskHistoryVO> listApproveHistoryByInstanceId(Long tenantId, Long instanceId) {
        return listApproveHistoryWithUserDTO(tenantId, instanceId);
    }

    private List<RunTaskHistoryVO> listApproveHistoryWithUserDTO(Long tenantId, Long instanceId) {
        List<RunTaskHistory> runTaskHistories = personalTodoRepository.selectHistory(tenantId, instanceId, Collections.singletonList(instanceId));
        if (CollectionUtils.isEmpty(runTaskHistories)){
            return Collections.emptyList();
        }
        Collections.reverse(runTaskHistories);
        List<RunTaskHistoryVO> runTaskHistoryVOList = runTaskHistories.stream()
                .map(history -> new RunTaskHistoryVO().setRunTaskHistory(history))
                .collect(Collectors.toList());
        fillRunTaskHistoryUserInfo(runTaskHistoryVOList);
        return runTaskHistoryVOList;
    }

    private void fillRunTaskHistoryUserInfo(List<RunTaskHistoryVO> runTaskHistoryVOList) {
        if(CollectionUtils.isEmpty(runTaskHistoryVOList)) {
            return;
        }
        final Long[] assigneeUserIds = runTaskHistoryVOList.stream()
                .map(RunTaskHistoryVO::getRunTaskHistory)
                .map(RunTaskHistory::getTaskAssignee)
                .filter(StringUtils::isNotBlank)
                .map(Long::parseLong)
                .collect(Collectors.toSet())
                .toArray(new Long[0]);
        List<UserDTO> userDTOList = ResponseUtils.getResponse(iamFeignClient.listUsersByIds(assigneeUserIds, false), new TypeReference<List<UserDTO>>() {});
        if(CollectionUtils.isEmpty(userDTOList)) {
            return;
        }
        Map<String, UserDTO> loginNameToUserMap = userDTOList.stream().collect(Collectors.toMap(UserDTO::getLoginName, Function.identity()));
        for (RunTaskHistoryVO history : runTaskHistoryVOList) {
            String loginName = history.getRunTaskHistory().getCode();
            if (StringUtils.isNotBlank(loginName)) {
                history.setUserDTO(loginNameToUserMap.get(loginName));
            }
        }
    }

    @Override
    public Page<PersonalTodoViewDTO> selectPersonalTodo(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        try {
            return PageHelper.doPageAndSort(
                    pageRequest,
                    () -> this.personalTodoC7nRepository.selectPersonalTodo(tenantId, queryDTO, backlogIds));
        } catch (EmployeeNotFoundException e) {
            return new Page<>();
        }
    }

    @Override
    public Page<PersonalTodoViewDTO> pageDone(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        queryDTO.setDoneFlag(BaseConstants.Flag.YES);
        return PageHelper.doPageAndSort(pageRequest, () -> this.personalTodoC7nRepository.selectMobilePersonalTask(queryDTO, backlogIds));
    }

    @Override
    public Page<ParticipatedDTO> mineParticipated(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        queryDTO.setTenantId(tenantId);

        try {
            Page<ParticipatedDTO> participatedPage = PageHelper.doPageAndSort(
                    pageRequest,
                    () -> this.personalTodoC7nRepository.selectMineParticipated(tenantId, queryDTO, backlogIds));
            this.selectSubProcess(tenantId, participatedPage.getContent(), queryDTO, backlogIds);
            return participatedPage;
        } catch (EmployeeNotFoundException e) {
            return new Page<>();
        }
    }

    @Override
    public Page<SubmittedDTO> mineSubmitted(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        queryDTO.setTenantId(tenantId);

        try {
            Page<SubmittedDTO> submittedPage = PageHelper.doPageAndSort(
                    pageRequest,
                    () -> this.personalTodoC7nRepository.selectMineSubmitted(tenantId, queryDTO, backlogIds));
            this.selectSubProcess(tenantId, submittedPage.getContent(), queryDTO, backlogIds);
            return submittedPage;
        } catch (EmployeeNotFoundException e) {
            return new Page<>();
        }
    }

    @Override
    public Page<CarbonCopyDTO> mineCarbonCopied(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        queryDTO.setTenantId(tenantId);

        try {
            Page<CarbonCopyDTO> carbonCopyPage = PageHelper.doPageAndSort(
                    pageRequest,
                    () -> this.personalTodoC7nRepository.selectMineCarbonCopied(tenantId, queryDTO, backlogIds));
            this.selectSubProcess(tenantId, carbonCopyPage.getContent(), queryDTO, backlogIds);
            return carbonCopyPage;
        } catch (EmployeeNotFoundException e) {
            return new Page<>();
        }
    }

    private <T extends BaseViewDTO> void selectSubProcess(Long tenantId, List<T> viewDTOS, PersonalTodoQueryDTO query,List<Long> backlogIds) {
        PersonalTodoQueryDTO queryDTO = new PersonalTodoQueryDTO();
        queryDTO.setTenantId(tenantId).setSelf(query.getSelf()).setSeparator(query.getSeparator());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(viewDTOS)) {
            List<Long> parentInstanceIds = viewDTOS.stream().map(BaseViewDTO::getInstanceId).collect(Collectors.toList());
            queryDTO.setParentInstanceIds(parentInstanceIds);
            List<BaseViewDTO> subProcessInstance = this.personalTodoC7nRepository.selectSubProcess(queryDTO, backlogIds);
            Map<Long, List<BaseViewDTO>> subProcessInstanceMap = subProcessInstance.stream().collect(Collectors.groupingBy(BaseViewDTO::getParentInstanceId));
            viewDTOS.forEach((view) -> view.setSubProcessChildren(subProcessInstanceMap.get(view.getInstanceId())));
            this.selectSubProcess(tenantId, subProcessInstance, query, backlogIds);
        }
    }
}
