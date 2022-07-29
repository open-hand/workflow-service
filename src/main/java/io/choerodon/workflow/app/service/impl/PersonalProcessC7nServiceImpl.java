package io.choerodon.workflow.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.workflow.api.vo.RunTaskHistoryVO;
import io.choerodon.workflow.app.service.PersonalProcessC7nService;
import io.choerodon.workflow.domain.repository.PersonalTodoC7nRepository;
import io.choerodon.workflow.infra.feign.BaseFeignClient;

import org.hzero.core.base.BaseConstants;
import org.hzero.workflow.def.infra.feign.PlatformFeignClient;
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
    private BaseFeignClient baseFeignClient;

    @Autowired
    private PlatformFeignClient platformFeignClient;

    @Override
    public List<RunTaskHistoryVO> listApproveHistoryByInstanceId(Long tenantId, Long instanceId) {
        List<RunTaskHistoryVO> result = new ArrayList<>();
        listApproveHistoryWithUserDTO(tenantId, instanceId, result);
        return result;
    }

    private void listApproveHistoryWithUserDTO(Long tenantId, Long instanceId, List<RunTaskHistoryVO> runTaskHistoryVOList) {
        List<RunTaskHistory> runTaskHistories = personalTodoRepository.selectHistory(tenantId, instanceId, Collections.singletonList(instanceId));
        if (CollectionUtils.isEmpty(runTaskHistories)) {
            return;
        }
        Collections.reverse(runTaskHistories);
        List<String>  realNames = new ArrayList<>();
        runTaskHistories.forEach(history -> {
            String assignee = history.getAssignee();
            RunTaskHistoryVO runTaskHistoryVO = new RunTaskHistoryVO();
            runTaskHistoryVO.setRunTaskHistory(history);
            if (!Objects.isNull(assignee)) {
                String realName = assignee.substring(0,assignee.lastIndexOf("("));
                if (!ObjectUtils.isEmpty(realName)) {
                    realNames.add(realName);
                }
            }
            runTaskHistoryVOList.add(runTaskHistoryVO);
        });
        if (!CollectionUtils.isEmpty(realNames)) {
            fillUser(realNames, runTaskHistoryVOList);
        }
    }

    private void fillUser(List<String> realNames, List<RunTaskHistoryVO> runTaskHistoryVOList) {
        List<UserDTO> userDTOList = baseFeignClient.listUsersByRealNames(false,new HashSet<>(realNames)).getBody();
        Map<String, List<UserDTO>> userDTOMap= userDTOList.stream().collect(Collectors.groupingBy(UserDTO::getRealName));
        runTaskHistoryVOList.forEach(history -> {
            String assignee = history.getRunTaskHistory().getAssignee();
            if (!Objects.isNull(assignee)) {
                String loginName = assignee.substring(assignee.lastIndexOf("(") + 1, assignee.lastIndexOf(")"));
                String realName = assignee.substring(0, assignee.lastIndexOf("("));
                List<UserDTO> userDTOS = userDTOMap.get(realName);
                if(!CollectionUtils.isEmpty(userDTOS)){
                    for (UserDTO userDTO : userDTOS) {
                        if (Objects.equals(loginName, userDTO.getLoginName()) || Objects.equals(loginName, userDTO.getEmail())) {
                            history.setUserDTO(userDTO);
                        }
                    }
                }
            }
        });
    }

    @Override
    public Page<PersonalTodoViewDTO> pageByOptions(Long tenantId, PageRequest pageRequest, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
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
