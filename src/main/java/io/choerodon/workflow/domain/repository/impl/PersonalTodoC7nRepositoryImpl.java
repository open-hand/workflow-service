package io.choerodon.workflow.domain.repository.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.workflow.domain.repository.PersonalTodoC7nRepository;
import io.choerodon.workflow.infra.mapper.PersonalTodoC7nMapper;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.workflow.def.domain.entity.DefExtendField;
import org.hzero.workflow.def.domain.repository.DefExtendFieldRepository;
import org.hzero.workflow.def.infra.constant.WorkflowConstants;
import org.hzero.workflow.engine.dao.entity.RunNode;
import org.hzero.workflow.engine.dao.entity.RunTask;
import org.hzero.workflow.engine.dao.entity.RunVariable;
import org.hzero.workflow.engine.dao.entity.RunVariableHistory;
import org.hzero.workflow.engine.dao.mapper.RunNodeMapper;
import org.hzero.workflow.engine.dao.mapper.RunTaskMapper;
import org.hzero.workflow.engine.dao.service.RunVariableHistoryService;
import org.hzero.workflow.engine.dao.service.RunVariableService;
import org.hzero.workflow.engine.model.FlowAssignee;
import org.hzero.workflow.engine.model.node.ApproveAction;
import org.hzero.workflow.engine.run.IModelService;
import org.hzero.workflow.engine.util.EngineConstants;
import org.hzero.workflow.helper.FlowAssigneeHelper;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO.*;
import org.hzero.workflow.personal.domain.repository.PersonalTodoRepository;

/**
 * @author chihao.ran@hand-china.com
 * 2021/04/09 17:23
 */
@Component
public class PersonalTodoC7nRepositoryImpl implements PersonalTodoC7nRepository {

    @Autowired
    private DefExtendFieldRepository defExtendFieldRepository;
    @Autowired
    private PersonalTodoRepository personalTodoRepository;
    @Resource
    private FlowAssigneeHelper flowAssigneeHelper;
    @Resource
    private PersonalTodoC7nMapper personalTodoC7nMapper;
    @Resource
    private RunTaskMapper runTaskMapper;
    @Resource
    private RunNodeMapper runNodeMapper;
    @Autowired
    private IModelService modelService;
    @Autowired
    private RunVariableService runVariableService;
    @Autowired
    private RunVariableHistoryService runVariableHistoryService;

    @Override
    public List<PersonalTodoViewDTO> selectPersonalTodo(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        queryDTO.setPageType(WorkflowConstants.PageType.TODO);
        queryDTO.setTodoCardFlag(Optional.ofNullable(queryDTO.getTodoCardFlag()).orElse(0));
        queryDTO.setTenantId(tenantId);
        List<PersonalTodoDTO.PersonalTodoViewDTO> result = personalTodoC7nMapper.selectPersonalTodo(queryDTO, backlogIds);
        processExtraFields(result, tenantId, queryDTO.getSelfUserId(), queryDTO.getSelfEmployeeNum(), queryDTO.getSeparator());
        processBusinessExtendField(tenantId,result,queryDTO);
        return result;
    }

    @Override
    public List<PersonalTodoViewDTO> selectMobilePersonalTask(PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        List<PersonalTodoDTO.PersonalTodoViewDTO> result = personalTodoC7nMapper.selectPersonalTask(queryDTO, backlogIds);
        processExtraFields(result, queryDTO.getTenantId(), queryDTO.getSelfUserId(), queryDTO.getSelfEmployeeNum(), queryDTO.getSeparator());
        return result;
    }

    @Override
    public List<ParticipatedDTO> selectMineParticipated(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        queryDTO.setPageType(WorkflowConstants.PageType.PARTICIPATED);
        queryDTO.setTenantId(tenantId);
        List<PersonalTodoDTO.ParticipatedDTO> result = personalTodoC7nMapper.selectMineParticipated(queryDTO, backlogIds);
        processExtraFields(result, tenantId, queryDTO.getSelfUserId(), queryDTO.getSelfEmployeeNum(), queryDTO.getSeparator());
        processBusinessExtendField(tenantId,result,queryDTO);
        return result;
    }

    @Override
    public List<SubmittedDTO> selectMineSubmitted(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        queryDTO.setPageType(WorkflowConstants.PageType.SUBMITTED);
        queryDTO.setTenantId(tenantId);
        List<PersonalTodoDTO.SubmittedDTO> result = personalTodoC7nMapper.selectMineSubmitted(queryDTO, backlogIds);
        processExtraFields(result, tenantId, queryDTO.getSelfUserId(), queryDTO.getSelfEmployeeNum(), queryDTO.getSeparator());

        for (PersonalTodoDTO.SubmittedDTO submittedDTO : result) {
            submittedDTO.processUrgeEnableFlag(submittedDTO.getInstanceStatus());
            Integer withdrawConfig = (Integer) modelService.getConfig(tenantId,submittedDTO.getFlowCode(),submittedDTO.getDeploymentId(), EngineConstants.ConfigCode.WITHDRAW_CONFIG);
            if (Objects.nonNull(submittedDTO.getParentInstanceId())) {
                // 子流程不允许撤回
                submittedDTO.setWithdrawEnableFlag(BaseConstants.Flag.NO);
            } else {
                submittedDTO.processWithdrawEnableFlag(withdrawConfig, submittedDTO.getInstanceStatus());
            }
        }
        processBusinessExtendField(tenantId,result,queryDTO);
        return result;
    }

    @Override
    public List<CarbonCopyDTO> selectMineCarbonCopied(Long tenantId, PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        queryDTO.setPageType(queryDTO.getCarbonCopyTodoFlag()!=null && queryDTO.getCarbonCopyTodoFlag()==1?WorkflowConstants.PageType.RECEIVE_CARBON_COPY:WorkflowConstants.PageType.CARBON_COPIED);
        queryDTO.setTenantId(tenantId);
        List<PersonalTodoDTO.CarbonCopyDTO> result = personalTodoC7nMapper.selectMineCarbonCopied(queryDTO, backlogIds);
        processExtraFields(result, tenantId, queryDTO.getSelfUserId(), queryDTO.getSelfEmployeeNum(), queryDTO.getSeparator());
        processBusinessExtendField(tenantId,result,queryDTO);
        return result;
    }

    @Override
    public List<BaseViewDTO> selectSubProcess(PersonalTodoQueryDTO queryDTO, List<Long> backlogIds) {
        this.personalTodoRepository.setSelfInfo(queryDTO);
        List<PersonalTodoDTO.BaseViewDTO> baseViewDTOS = personalTodoC7nMapper.selectSubProcess(queryDTO, backlogIds);
        processExtraFields(baseViewDTOS, queryDTO.getTenantId(), queryDTO.getSelfUserId(), queryDTO.getSelfEmployeeNum(), queryDTO.getSeparator());
        return baseViewDTOS;
    }

    private <T extends PersonalTodoDTO.BaseViewDTO> void processExtraFields(List<T> dtoList, Long tenantId, String selfUserId, String selfEmployeeNum, String separator) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }

        separator = StringUtils.isEmpty(separator) ? BaseConstants.Symbol.COMMA : separator;

        Set<Long> instanceIdSet = dtoList.stream().map(PersonalTodoDTO.BaseViewDTO::getInstanceId).collect(Collectors.toSet());
        List<RunTask> runTasks = Optional.ofNullable(runTaskMapper.selectByInstanceIds(tenantId, instanceIdSet)).orElse(new ArrayList<>());
        // 非人工节点的当前节点信息
        List<RunNode> runNodes = Optional.ofNullable(runNodeMapper.selectByInstanceIds(tenantId, instanceIdSet)).orElse(new ArrayList<>());

        Set<String> employeeNums = runTasks.stream().filter(item -> EngineConstants.ApproveDimension.EMPLOYEE.equals(item.getDimension()))
                .map(RunTask::getAssignee).collect(Collectors.toSet());
        Set<String> userIds = runTasks.stream().filter(item -> EngineConstants.ApproveDimension.USER.equals(item.getDimension()))
                .map(RunTask::getAssignee).collect(Collectors.toSet());

        employeeNums.addAll(dtoList.stream().filter(item -> EngineConstants.ApproveDimension.EMPLOYEE.equals(item.getInstanceDimension()))
                .map(PersonalTodoDTO.BaseViewDTO::getStarter).collect(Collectors.toList()));
        userIds.addAll(dtoList.stream().filter(item -> EngineConstants.ApproveDimension.USER.equals(item.getInstanceDimension()))
                .map(PersonalTodoDTO.BaseViewDTO::getStarter).collect(Collectors.toList()));

        // 抄送
        List<String> carbonCopyUserIdList = dtoList.stream().filter(d -> d instanceof PersonalTodoDTO.CarbonCopyDTO)
                .map(d -> ((PersonalTodoDTO.CarbonCopyDTO) d).getCarbonCopyFrom()).collect(Collectors.toList());
        userIds.addAll(carbonCopyUserIdList);

        Map<String, FlowAssignee> assigneeMap = flowAssigneeHelper.getFlowAssigneeMap(tenantId, employeeNums, userIds);

        Map<Long, List<RunTask>> runTaskGroupMap = runTasks.stream().collect(Collectors.groupingBy(RunTask::getInstanceId));
        Map<Long, List<RunNode>> runNodeGroupMap = runNodes.stream()
                .filter(dto -> StringUtils.isEmpty(dto.getNodeType())
                        || StringUtils.equalsAny(dto.getNodeType(), EngineConstants.NodeType.MANUAL_NODE, EngineConstants.NodeType.APPROVE_CHAIN_LINE_NODE, EngineConstants.NodeType.SUB_PROCESS_NODE))
                .collect(Collectors.groupingBy(RunNode::getInstanceId));
        for (PersonalTodoDTO.BaseViewDTO dto : dtoList) {
            nodeNameAndApproverProcess(separator, assigneeMap, runTaskGroupMap, runNodeGroupMap, dto);
            // 流程申请人
            String key = dto.getInstanceDimension() + BaseConstants.Symbol.MIDDLE_LINE + dto.getStarter();
            FlowAssignee starterAssignee = assigneeMap.get(key);
            if (Objects.nonNull(starterAssignee)) {
                dto.setStarterNumber(starterAssignee.getKey());
                dto.setStarter(starterAssignee.getName());
                dto.setCode(starterAssignee.getCode());
                dto.setEmail(starterAssignee.getEmail());
                if (Objects.equals(starterAssignee.getDimension(),EngineConstants.ApproveDimension.EMPLOYEE)){
                    dto.setPhone(starterAssignee.getEmployee().getMobile());
                }
                if (Objects.equals(starterAssignee.getDimension(),EngineConstants.ApproveDimension.USER)){
                    dto.setPhone(starterAssignee.getUser().getPhone());
                }
            }

            // 抄送
            if (dto instanceof PersonalTodoDTO.CarbonCopyDTO) {
                // 抄送来源
                String carbonCopyFromNum = ((PersonalTodoDTO.CarbonCopyDTO) dto).getCarbonCopyFrom();
                String carbonCopyKey = EngineConstants.ApproveDimension.USER + BaseConstants.Symbol.MIDDLE_LINE + carbonCopyFromNum;
                FlowAssignee carbonCopyFrom = Optional.ofNullable(assigneeMap.get(carbonCopyKey)).orElse(new FlowAssignee());
                ((PersonalTodoDTO.CarbonCopyDTO) dto).setCarbonCopyFrom(String.format("%s(%s)", carbonCopyFrom.getName(), carbonCopyFrom.getCode()));
            }

            if (dto instanceof PersonalTodoDTO.PersonalTodoViewDTO) {
                // 审批动作列表
                List<ApproveAction> actionList = modelService.getActionList(dto.getTaskCode(), dto.getDeploymentId(), dto.getTaskType(),dto.getFlowCode());
                ((PersonalTodoDTO.PersonalTodoViewDTO) dto).setActionList(actionList);
                dto.setSelfEmpNum(selfEmployeeNum);
                dto.setSelfUserId(selfUserId);
            }
        }
    }

    private void nodeNameAndApproverProcess(String separator, Map<String, FlowAssignee> assigneeMap, Map<Long, List<RunTask>> runTaskGroupMap, Map<Long, List<RunNode>> runNodeGroupMap, BaseViewDTO dto) {
        List<RunTask> dtoRunTasks = Optional.ofNullable(runTaskGroupMap.get(dto.getInstanceId())).orElse(new ArrayList<>());
        List<RunNode> dtoRunNodes = Optional.ofNullable(runNodeGroupMap.get(dto.getInstanceId())).orElse(new ArrayList<>());
        Map<Long, List<RunTask>> taskGroupByNodeId = dtoRunTasks.stream().collect(Collectors.groupingBy(RunTask::getNodeId));
        List<String> nodeNames = taskGroupByNodeId.values().stream().map((tasks) -> (tasks.get(0)).getTaskName()).collect(Collectors.toList());
        List<String> noApproverNodeNames = dtoRunNodes.stream().filter((r) -> StringUtils.isNotEmpty(r.getNodeName()) && !nodeNames.contains(r.getNodeName())).map(RunNode::getNodeName).collect(Collectors.toList());
        nodeNames.addAll(noApproverNodeNames);
        dto.setNodeName(String.join(separator, nodeNames));
        Set<String> currentAssignees = dtoRunTasks.stream().map(RunTask::getAssignee).collect(Collectors.toSet());
        dto.setAssignee(currentAssignees.stream().map((ca) -> assigneeMap.containsKey(ca) ? String.format("%s(%s)", (assigneeMap.get(ca)).getName(), (assigneeMap.get(ca)).getCode()) : ca).collect(Collectors.joining(separator)));
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

    /***
     * 处理业务扩展字段
     */
    private <T extends PersonalTodoDTO.BaseViewDTO> void processBusinessExtendField(Long tenantId,List<T> result,PersonalTodoDTO.PersonalTodoQueryDTO queryDTO){
        if(CollectionUtils.isEmpty(result)){
            return;
        }
        //查询字段列表
        List<DefExtendField> globalFieldList = defExtendFieldRepository.listTableFieldByPageType(tenantId,queryDTO.getPageType());
        List<DefExtendField> globalBusinessFieldList = CollectionUtils.isEmpty(globalFieldList)?new ArrayList<>():globalFieldList.stream().filter(dto->WorkflowConstants.FieldSource.BUSINESS_FIELD.equals(dto.getFieldSource())).collect(Collectors.toList());

        Set<Long> instanceIdSet = result.stream().map(PersonalTodoDTO.BaseViewDTO::getInstanceId).collect(Collectors.toSet());
        List<DefExtendField> extendFieldList = defExtendFieldRepository.selectByInstanceIdList(instanceIdSet);
        if(CollectionUtils.isEmpty(globalBusinessFieldList) && CollectionUtils.isEmpty(extendFieldList)){
            for(PersonalTodoDTO.BaseViewDTO baseViewDTO : result){
                baseViewDTO.setDefExtendFieldList(globalFieldList);
            }
            return;
        }

        //查询流程变量
        List<RunVariable> runVariableList = runVariableService.selectByCondition(Condition.builder(RunVariable.class).where(Sqls.custom()
                .andIn(RunVariable.FIELD_INSTANCE_ID,instanceIdSet)
                .andEqualTo(RunVariable.FIELD_TENANT_ID,tenantId)
                .andIsNull(RunVariable.FIELD_NODE_ID)).build());
        List<RunVariableHistory> runVariableHistoryList = runVariableHistoryService.selectByCondition(Condition.builder(RunVariableHistory.class).where(Sqls.custom()
                .andIn(RunVariableHistory.FIELD_INSTANCE_ID,instanceIdSet)
                .andEqualTo(RunVariableHistory.FIELD_TENANT_ID,tenantId)
                .andIsNull(RunVariableHistory.FIELD_NODE_ID)).build());
        if(CollectionUtils.isEmpty(runVariableList) && CollectionUtils.isEmpty(runVariableHistoryList)){
            return;
        }
        List<RunVariable> allVariableList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(runVariableList)){
            allVariableList.addAll(runVariableList);
        }
        if(!CollectionUtils.isEmpty(runVariableHistoryList)){
            runVariableHistoryList.forEach(dto->{
                RunVariable ru = new RunVariable();
                BeanUtils.copyProperties(dto,ru);
                allVariableList.add(ru);
            });
        }

        //设置业务字段
        Map<Long,List<DefExtendField>> extendFieldMap = extendFieldList.stream().collect(Collectors.groupingBy(DefExtendField::getInstanceId));
        Map<Long,List<RunVariable>> runVariableMap = allVariableList.stream().collect(Collectors.groupingBy(RunVariable::getInstanceId));
        for(PersonalTodoDTO.BaseViewDTO baseViewDTO : result){
            Long instanceId = baseViewDTO.getInstanceId();
            Map<String,String> variableCodeValueMap = runVariableMap.get(instanceId).stream().collect(Collectors.toMap(RunVariable::getVariableCode,RunVariable::getValue));

            //设置业务字段值map
            Map<String,Object> bsFieldMap = new HashMap<>(16);
            globalBusinessFieldList.forEach(dto-> bsFieldMap.put(dto.getFieldCode(),variableCodeValueMap.get(dto.getFieldCode())));
            if(extendFieldMap != null && CollectionUtils.isNotEmpty(extendFieldMap.get(instanceId))){
                extendFieldMap.get(instanceId).forEach(dto-> bsFieldMap.put(dto.getFieldCode(),variableCodeValueMap.get(dto.getFieldCode())));
            }
            baseViewDTO.setBusinessFieldMap(bsFieldMap);

            //卡片模式特殊处理，设置全局字段+流程定义层字段到businessFieldList中
            List<DefExtendField> allFieldList = new ArrayList<>(globalFieldList);
            if(extendFieldMap != null && CollectionUtils.isNotEmpty(extendFieldMap.get(instanceId))){
                allFieldList.addAll(extendFieldMap.get(instanceId));
                allFieldList = allFieldList.stream().sorted(Comparator.comparing(DefExtendField::getFrozenFlag).reversed().thenComparing(DefExtendField::getOrderNo)).collect(Collectors.toList());
            }
            baseViewDTO.setDefExtendFieldList(allFieldList);
        }
    }
}
