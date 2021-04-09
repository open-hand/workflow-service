package io.choerodon.workflow.app.service.impl;

import org.hzero.workflow.def.infra.feign.PlatformFeignClient;
import org.hzero.workflow.def.infra.feign.dto.UserDTO;
import org.hzero.workflow.engine.dao.dto.EmployeeUserDTO;
import org.hzero.workflow.engine.dao.entity.RunTaskHistory;
import org.hzero.workflow.personal.domain.repository.PersonalTodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.workflow.api.vo.RunTaskHistoryVO;
import io.choerodon.workflow.app.service.PersonalProcessC7nService;
import io.choerodon.workflow.infra.feign.BaseFeignClient;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:21:57
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PersonalProcessC7nServiceImpl implements PersonalProcessC7nService {

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
        List<RunTaskHistory> runTaskHistories = personalTodoRepository.selectHistory(tenantId, instanceId);
        if (CollectionUtils.isEmpty(runTaskHistories)) {
            return;
        }
        Collections.reverse(runTaskHistories);
        List<String> empNumList = new ArrayList<>();
        runTaskHistories.forEach(history -> {
            String assignee = history.getAssignee();
            RunTaskHistoryVO runTaskHistoryVO = new RunTaskHistoryVO();
            runTaskHistoryVO.setRunTaskHistory(history);
            if (!Objects.isNull(assignee)) {
                String empNum = assignee.substring(assignee.lastIndexOf("(") + 1, assignee.lastIndexOf(")"));
                empNumList.add(empNum);
            }
            runTaskHistoryVOList.add(runTaskHistoryVO);
        });
        if (CollectionUtils.isEmpty(empNumList)) {
            return;
        }
        List<EmployeeUserDTO> empList = CollectionUtils.isEmpty(empNumList) ? new ArrayList<>() : platformFeignClient.getEmpUsersByEmpNumList(tenantId, empNumList);
        List<Long> userIds = empList.stream().map(EmployeeUserDTO::getUserId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(userIds)) {
            fillUser(tenantId, userIds, runTaskHistoryVOList);
        }
    }

    private void fillUser(Long tenantId, List<Long> userIds, List<RunTaskHistoryVO> runTaskHistoryVOList) {
        List<UserDTO> userDTOList = baseFeignClient.listUsersByIds(userIds.toArray(new Long[0]), false);
        Map<Long, UserDTO> userMap = userDTOList.stream().collect(Collectors.toMap(UserDTO::getId, Function.identity()));
        userIds.forEach(id -> {
            EmployeeUserDTO employeeUserVO = platformFeignClient.getEmployeeByUserId(tenantId, null, id);
            runTaskHistoryVOList.forEach(history -> {
                String assignee = history.getRunTaskHistory().getAssignee();
                if (!Objects.isNull(assignee)) {
                    String empNum = assignee.substring(assignee.lastIndexOf("(") + 1, assignee.lastIndexOf(")"));
                    if (Objects.equals(employeeUserVO.getEmployeeNum(), empNum)) {
                        history.setUserDTO(userMap.get(employeeUserVO.getUserId()));
                    }
                }
            });
        });
    }
}
