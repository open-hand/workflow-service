package io.choerodon.workflow.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.workflow.def.infra.common.utils.UserUtils;
import org.hzero.workflow.engine.dao.entity.RunNode;
import org.hzero.workflow.engine.dao.entity.RunTask;
import org.hzero.workflow.engine.dao.mapper.RunNodeMapper;
import org.hzero.workflow.engine.dao.mapper.RunTaskMapper;
import org.hzero.workflow.engine.model.FlowAssignee;
import org.hzero.workflow.engine.model.node.ApproveAction;
import org.hzero.workflow.engine.run.IModelService;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;
import org.hzero.workflow.personal.domain.repository.PersonalTodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import io.choerodon.workflow.domain.repository.PersonalTodoC7nRepository;
import io.choerodon.workflow.infra.mapper.PersonalTodoC7nMapper;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/09 17:23
 */
@Component
public class PersonalTodoC7nRepositoryImpl implements PersonalTodoC7nRepository {

    @Autowired
    private PersonalTodoRepository personalTodoRepository;
    @Resource
    private PersonalTodoC7nMapper personalTodoC7nMapper;
    @Resource
    private RunTaskMapper runTaskMapper;
    @Resource
    private RunNodeMapper runNodeMapper;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private IModelService modelService;

    @Override
    public List<PersonalTodoViewDTO> selectPersonalTodo(Long tenantId, PersonalTodoQueryDTO queryDTO) {
        queryDTO.setTenantId(tenantId);
        String self = this.userUtils.selfConfirm(queryDTO.getSelf());
        queryDTO.setSelf(self);
        List<PersonalTodoViewDTO> result = this.personalTodoC7nMapper.selectPersonalTodo(queryDTO);
        this.processExtraFields(result, tenantId, self, queryDTO.getSeparator());
        return result;
    }

    @Override
    public List<PersonalTodoViewDTO> selectMobilePersonalTask(PersonalTodoQueryDTO queryDTO) {
        List<PersonalTodoViewDTO> result = this.personalTodoC7nMapper.selectPersonalTask(queryDTO);
        this.processExtraFields(result, queryDTO.getTenantId(), queryDTO.getSelf(), queryDTO.getSeparator());
        return result;
    }

    @Override
    public List<ParticipatedDTO> selectMineParticipated(Long tenantId, PersonalTodoQueryDTO queryDTO) {
        queryDTO.setTenantId(tenantId);
        String self = this.userUtils.selfConfirm(queryDTO.getSelf());
        queryDTO.setSelf(self);
        List<ParticipatedDTO> result = this.personalTodoC7nMapper.selectMineParticipated(queryDTO);
        this.processExtraFields(result, tenantId, self, queryDTO.getSeparator());
        return result;
    }

    @Override
    public List<SubmittedDTO> selectMineSubmitted(Long tenantId, PersonalTodoQueryDTO queryDTO) {
        queryDTO.setTenantId(tenantId);
        String self = this.userUtils.selfConfirm(queryDTO.getSelf());
        queryDTO.setSelf(self);
        List<SubmittedDTO> result = this.personalTodoC7nMapper.selectMineSubmitted(queryDTO);
        this.processExtraFields(result, tenantId, self, queryDTO.getSeparator());

        for (SubmittedDTO submittedDTO : result) {
            submittedDTO.processUrgeEnableFlag(submittedDTO.getInstanceStatus());
            Integer withdrawConfig = (Integer) this.modelService.getConfig(submittedDTO.getDeploymentId(), "backFlag");
            if (Objects.nonNull(submittedDTO.getParentInstanceId())) {
                submittedDTO.setWithdrawEnableFlag(BaseConstants.Flag.NO);
            } else {
                submittedDTO.processWithdrawEnableFlag(withdrawConfig, submittedDTO.getInstanceStatus());
            }
        }

        return result;
    }

    @Override
    public List<CarbonCopyDTO> selectMineCarbonCopied(Long tenantId, PersonalTodoQueryDTO queryDTO) {
        queryDTO.setTenantId(tenantId);
        String self = this.userUtils.selfConfirm(queryDTO.getSelf());
        queryDTO.setSelf(self);
        List<CarbonCopyDTO> result = this.personalTodoC7nMapper.selectMineCarbonCopied(queryDTO);
        this.processExtraFields(result, tenantId, self, queryDTO.getSeparator());
        return result;
    }

    @Override
    public List<BaseViewDTO> selectSubProcess(PersonalTodoQueryDTO queryDTO) {
        String self = this.userUtils.selfConfirm(queryDTO.getSelf());
        queryDTO.setSelf(self);
        List<BaseViewDTO> baseViewDTOS = this.personalTodoC7nMapper.selectSubProcess(queryDTO);
        this.processExtraFields(baseViewDTOS, queryDTO.getTenantId(), queryDTO.getSelf(), queryDTO.getSeparator());
        return baseViewDTOS;
    }

    private <T extends BaseViewDTO> void processExtraFields(List<T> dtoList, Long tenantId, String self, String separator) {
        if (!CollectionUtils.isEmpty(dtoList)) {
            separator = StringUtils.isEmpty(separator) ? "," : separator;
            Set<Long> instanceIdSet = dtoList.stream().map(BaseViewDTO::getInstanceId).collect(Collectors.toSet());
            List<RunTask> runTasks = Optional.ofNullable(this.runTaskMapper.selectByInstanceIds(tenantId, instanceIdSet)).orElse(new ArrayList<>());
            List<RunNode> runNodes = Optional.ofNullable(this.runNodeMapper.selectByInstanceIds(tenantId, instanceIdSet)).orElse(new ArrayList<>());
            List<String> assignees = runTasks.stream().map(RunTask::getAssignee).collect(Collectors.toList());
            List<String> starterEmpIdList = dtoList.stream().map(BaseViewDTO::getStarter).collect(Collectors.toList());
            Map<String, FlowAssignee> assigneeMap = personalTodoRepository.getFlowAssigneeMap(tenantId, assignees);
            Map<String, FlowAssignee> starterMap = personalTodoRepository.getFlowAssigneeMap(tenantId, starterEmpIdList);
            List<String> carbonCopyEmpIdList = dtoList.stream().filter((d) -> {
                return d instanceof CarbonCopyDTO;
            }).map((d) -> {
                return ((CarbonCopyDTO) d).getCarbonCopyFrom();
            }).collect(Collectors.toList());
            Map<String, FlowAssignee> carbonCopyMap = personalTodoRepository.getFlowAssigneeMap(tenantId, carbonCopyEmpIdList);
            Map<Long, List<RunTask>> runTaskGroupMap = runTasks.stream().collect(Collectors.groupingBy(RunTask::getInstanceId));
            Map<Long, List<RunNode>> runNodeGroupMap = runNodes.stream().filter((dtox) -> {
                return StringUtils.isEmpty(dtox.getNodeType()) || StringUtils.equalsAny(dtox.getNodeType(), new CharSequence[]{"manualNode", "approveChainLineNode", "subProcessNode"});
            }).collect(Collectors.groupingBy(RunNode::getInstanceId));

            for (T t : dtoList) {
                this.nodeNameAndApproverProcess(separator, assigneeMap, runTaskGroupMap, runNodeGroupMap, t);
                FlowAssignee starterAssignee = starterMap.get(t.getStarter());
                if (Objects.nonNull(starterAssignee)) {
                    t.setStarterNumber(starterAssignee.getKey());
                    t.setStarter(String.format("%s(%s)", starterAssignee.getName(), starterAssignee.getCode()));
                }

                if (t instanceof CarbonCopyDTO) {
                    ((CarbonCopyDTO) t).setReadFlag((Optional.ofNullable(t.getReadPerson()).orElse("")).contains(self) ? BaseConstants.Flag.YES : BaseConstants.Flag.NO);
                    String carbonCopyFromNum = ((CarbonCopyDTO) t).getCarbonCopyFrom();
                    FlowAssignee carbonCopyFrom = Optional.ofNullable(carbonCopyMap.get(carbonCopyFromNum)).orElse(new FlowAssignee());
                    ((CarbonCopyDTO) t).setCarbonCopyFrom(String.format("%s(%s)", carbonCopyFrom.getName(), carbonCopyFrom.getCode()));
                }

                if (t instanceof PersonalTodoViewDTO) {
                    List<ApproveAction> actionList = this.modelService.getActionList(t.getTaskCode(), t.getDeploymentId(), t.getTaskType());
                    ((PersonalTodoViewDTO) t).setActionList(actionList);
                    t.setSelfEmpNum(self);
                }
            }

        }
    }

    private void nodeNameAndApproverProcess(String separator, Map<String, FlowAssignee> assigneeMap, Map<Long, List<RunTask>> runTaskGroupMap, Map<Long, List<RunNode>> runNodeGroupMap, BaseViewDTO dto) {
        List<RunTask> dtoRunTasks = Optional.ofNullable(runTaskGroupMap.get(dto.getInstanceId())).orElse(new ArrayList<>());
        List<RunNode> dtoRunNodes = Optional.ofNullable(runNodeGroupMap.get(dto.getInstanceId())).orElse(new ArrayList<>());
        Map<Long, List<RunTask>> taskGroupByNodeId = dtoRunTasks.stream().collect(Collectors.groupingBy(RunTask::getNodeId));
        List<String> nodeNames = taskGroupByNodeId.values().stream().map((tasks) -> {
            return (tasks.get(0)).getTaskName();
        }).collect(Collectors.toList());
        List<String> noApproverNodeNames = dtoRunNodes.stream().filter((r) -> {
            return StringUtils.isNotEmpty(r.getNodeName()) && !nodeNames.contains(r.getNodeName());
        }).map(RunNode::getNodeName).collect(Collectors.toList());
        nodeNames.addAll(noApproverNodeNames);
        dto.setNodeName(String.join(separator, nodeNames));
        Set<String> currentAssignees = dtoRunTasks.stream().map(RunTask::getAssignee).collect(Collectors.toSet());
        dto.setAssignee(currentAssignees.stream().map((ca) -> {
            return assigneeMap.containsKey(ca) ? String.format("%s(%s)", (assigneeMap.get(ca)).getName(), (assigneeMap.get(ca)).getCode()) : ca;
        }).collect(Collectors.joining(separator)));
        List<String> currentNodeAssigneeList = new ArrayList<>();

        for (List<RunTask> taskList : taskGroupByNodeId.values()) {
            String nodeName = (taskList.get(0)).getTaskName();
            String approver = taskList.stream().map((ca) -> {
                String caAssignee = ca.getAssignee();
                return assigneeMap.containsKey(caAssignee) ? String.format("%s(%s)", (assigneeMap.get(caAssignee)).getName(), (assigneeMap.get(caAssignee)).getCode()) : caAssignee;
            }).collect(Collectors.joining(separator));
            currentNodeAssigneeList.add(String.format("%s【%s】", nodeName, approver));
        }

        currentNodeAssigneeList.addAll(noApproverNodeNames);
        dto.setCurrentNodeAssignee(String.join(separator, currentNodeAssigneeList));
    }
}
