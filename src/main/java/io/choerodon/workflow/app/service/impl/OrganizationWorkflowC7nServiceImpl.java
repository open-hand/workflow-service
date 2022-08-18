package io.choerodon.workflow.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.workflow.app.service.OrganizationWorkflowC7nService;
import io.choerodon.workflow.infra.constant.HzeroWorkFlowConstants;
import io.choerodon.workflow.infra.feign.IamFeignClient;
import io.choerodon.workflow.infra.feign.vo.OrganizationInfoVO;
import io.choerodon.workflow.infra.util.PredefineWorkflowDataUtil;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.workflow.def.app.service.DefModelService;
import org.hzero.workflow.def.app.service.DefTypeImportExportService;
import org.hzero.workflow.def.app.service.DefTypeService;
import org.hzero.workflow.def.domain.entity.*;
import org.hzero.workflow.def.domain.repository.*;

/**
 * @author chihao.ran@hand-china.com
 * 2021/03/30 14:54
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrganizationWorkflowC7nServiceImpl implements OrganizationWorkflowC7nService {

//    private static final String FILE_PATH = "/templates/default_flow.json";
//    private static final String ORG_TYPE_NAME = "需求审核-预定义";
//    private static final String ERROR_WORK_FLOW_INIT_TYPE_EXIST = "error.work.flow.init.type.exist";
//    private static final String ERROR_WORK_FLOW_INIT_FILE_INVALID = "error.init_file_invalid";
//    private static final String ERROR_WORK_FLOW_DEFAULT_TYPE_NOT_EXIST = "error.work.flow.default.type.not.exist";
    private static final String ERROR_ORG_NOT_EXIST = "error.org.not.exist";

    @Autowired
    private DefWorkflowRepository defWorkflowRepository;
    @Autowired
    private DefTypeRepository defTypeRepository;
    @Autowired
    private DefTypeService defTypeService;
    @Autowired
    private DefModelService defModelService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IamFeignClient iamFeignClient;
    @Autowired
    private DefVariableRepository defVariableRepository;
    @Autowired
    private DefApproverRuleRepository defApproverRuleRepository;
    @Autowired
    private DefApproverRuleLineRepository defApproverRuleLineRepository;
    @Autowired
    private DefApproverReturnRepository defApproverReturnRepository;
    @Autowired
    private DefParameterValueRepository defParameterValueRepository;
    @Autowired
    private DefTypeImportExportService importExportService;

    @Override
    public void initDefWorkFlows(Long tenantId) {
//        DefType defaultDefType = getDefTypeByCode(BaseConstants.DEFAULT_TENANT_ID, HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
//        if (ObjectUtils.isEmpty(defaultDefType)) {
//            throw new CommonException(ERROR_WORK_FLOW_DEFAULT_TYPE_NOT_EXIST);
//        }
//        DefType newDefType = copyDefaultType(defaultDefType, tenantId);
//        List<DefWorkflow> defWorkFlows = getDefaultWorkFlowFromJson();
//        for (DefWorkflow defWorkflow : defWorkFlows) {
//            defWorkflow.setDiagramJson(importWorkFlowJsonConvert(defWorkflow.getDiagramJson()));
//            defWorkflow.setTenantId(tenantId);
//            defWorkflow.setTypeCode(newDefType.getTypeCode());
//            defWorkflow.setTypeId(newDefType.getTypeId());
//            defWorkflowRepository.insertSelective(defWorkflow);
//            defModelService.saveAndReturnConfig(tenantId, defWorkflow.getFlowId(), defWorkflow.getModelConfigVO(), defWorkflow.getDiagramJson());
//        }
        final MultipartFile initData = PredefineWorkflowDataUtil.generateMultipartFile();
        Assert.notNull(initData, "can not find predefine workflow data!");
        this.importExportService.importDefTypeInfo(tenantId, initData);
    }

    @Override
    public Boolean checkInit(Long tenantId) {
        OrganizationInfoVO organizationInfoVO = iamFeignClient.queryOrganizationInfo(tenantId).getBody();
        if (ObjectUtils.isEmpty(organizationInfoVO)) {
            throw new CommonException(ERROR_ORG_NOT_EXIST);
        }
        return validInitDefType(tenantId, HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
    }

    @Override
    public void reimportWorkflow(Long tenantId) {
        if (!checkInit(tenantId)) {
            throw new CommonException("error.workflow.not.init");
        }
        DefType defType = getDefTypeByCode(tenantId, HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
        if (ObjectUtils.isEmpty(defType)) {
            throw new CommonException("error.workflow.predefined.workflow");
        }
        Long typeId = defType.getTypeId();
        DefType defaultDefType = getDefTypeByCode(BaseConstants.DEFAULT_TENANT_ID, HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
        Long defaultDefTypeId = defaultDefType.getTypeId();
        //增量添加流程变量
        reimportDefVariable(tenantId, typeId, defaultDefTypeId);
        //增量添加审批人规则
        reimportDefApproverRule(tenantId, typeId, defaultDefTypeId);
    }

    /**
     * 重新同步审批人规则
     * @param tenantId 当前租户ID
     * @param typeId 当前流程分类ID
     * @param defaultDefTypeId 预定义流程分类ID
     */
    private void reimportDefApproverRule(Long tenantId, Long typeId, Long defaultDefTypeId) {
        // 查询当前租户已存在的审批人规则
        final List<DefApproverRule> currentDefApproverRules = this.defApproverRuleRepository.selectByCondition(Condition.builder(DefApproverRule.class)
                .where(Sqls.custom()
                        .andEqualTo(DefApproverRule.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(DefApproverRule.FIELD_TYPE_ID, typeId)
                ).build());
        final Map<String, DefApproverRule> currentDefApproverRuleCodes = currentDefApproverRules.stream().collect(Collectors.toMap(DefApproverRule::getRuleCode, Function.identity()));
        // 查询预定义的审批人规则
        final List<DefApproverRule> defaultDefApproverRules = this.defApproverRuleRepository.selectByCondition(Condition.builder(DefApproverRule.class)
                .where(Sqls.custom()
                        .andEqualTo(DefApproverRule.FIELD_TENANT_ID, BaseConstants.DEFAULT_TENANT_ID)
                        .andEqualTo(DefApproverRule.FIELD_TYPE_ID, defaultDefTypeId)
                ).build());

        for (DefApproverRule rule : defaultDefApproverRules) {
            final Long defaultRuleId = rule.getRuleId();
            Long ruleId;
            if (currentDefApproverRuleCodes.containsKey(rule.getRuleCode())) {
                ruleId = currentDefApproverRuleCodes.get(rule.getRuleCode()).getRuleId();
            } else {
                // 找到当前租户不存在但预定义里存在的审批人规则, 执行插入
                rule.setRuleId(null);
                rule.setTypeId(typeId);
                rule.setTenantId(tenantId);
                defApproverRuleRepository.insertSelective(rule);
                ruleId = rule.getRuleId();
            }
            // 处理审批人规则明细
            this.reimportDefApproverRuleLine(tenantId, ruleId, defaultRuleId);
        }
    }

    /**
     * 重新同步审批人规则明细
     * @param tenantId 当前租户ID
     * @param ruleId 当前审批人规则ID
     * @param defaultRuleId 预定义审批人规则ID
     */
    private void reimportDefApproverRuleLine(Long tenantId, Long ruleId, Long defaultRuleId) {
        // 查询当前租户已存在的审批人规则明细
        final List<DefApproverRuleLine> currentDefApproverRuleLines = this.defApproverRuleLineRepository.selectByCondition(Condition.builder(DefApproverRuleLine.class)
                .where(Sqls.custom()
                        .andEqualTo(DefApproverRuleLine.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(DefApproverRuleLine.FIELD_RULE_ID, ruleId)
                ).build());
        final Map<String, DefApproverRuleLine> currentDefApproverRuleLineCodes = currentDefApproverRuleLines.stream().collect(Collectors.toMap(DefApproverRuleLine::getLineCode, Function.identity()));
        // 查询预定义的审批人规则明细
        final List<DefApproverRuleLine> defaultDefApproverRuleLines = this.defApproverRuleLineRepository.selectByCondition(Condition.builder(DefApproverRuleLine.class)
                .where(Sqls.custom()
                        .andEqualTo(DefApproverRuleLine.FIELD_TENANT_ID, BaseConstants.DEFAULT_TENANT_ID)
                        .andEqualTo(DefApproverRuleLine.FIELD_RULE_ID, defaultRuleId)
                ).build());

        // 找到当前租户不存在但预定义里存在的审批人规则明细, 执行插入
        for (DefApproverRuleLine line : defaultDefApproverRuleLines) {
            final Long defaultRuleLineId = line.getRuleLineId();
            Long ruleLineId;
            if (currentDefApproverRuleLineCodes.containsKey(line.getLineCode())) {
                ruleLineId = currentDefApproverRuleLineCodes.get(line.getLineCode()).getRuleLineId();
            } else {
                line.setRuleLineId(null);
                line.setRuleId(ruleId);
                line.setTenantId(tenantId);
                this.defApproverRuleLineRepository.insertSelective(line);
                ruleLineId = line.getRuleLineId();
            }
            reimportDefApproverReturn(tenantId, ruleLineId, defaultRuleLineId);
            reimportDefParameterValue(tenantId, ruleLineId, defaultRuleLineId);
        }

    }

    /**
     * 重新同步审批人规则明细参数
     * @param tenantId 当前租户ID
     * @param ruleLineId 当前审批人规则明细ID
     * @param defaultRuleLineId 预定义审批人规则明细ID
     */
    private void reimportDefParameterValue(Long tenantId, Long ruleLineId, Long defaultRuleLineId) {
        DefParameterValue defParameterValue = new DefParameterValue();
        defParameterValue.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        defParameterValue.setSourceId(defaultRuleLineId);
        defParameterValue.setSourceType("DEFAULT");
        defParameterValue.setSourceTable("HWKF_DEF_APPROVER_RULE_LINE");
        for (DefParameterValue parameterValue : defParameterValueRepository.select(defParameterValue)) {
            DefParameterValue insertOne = new DefParameterValue();
            insertOne.setTenantId(tenantId);
            insertOne.setSourceId(ruleLineId);
            insertOne.setSourceType(parameterValue.getSourceType());
            insertOne.setSourceTable(parameterValue.getSourceTable());
            insertOne.setParameterCode(parameterValue.getParameterCode());
            if (defParameterValueRepository.selectOne(insertOne) != null) {
                continue;
            }
            BeanUtils.copyProperties(parameterValue, insertOne);
            insertOne.setParameterId(null);
            insertOne.setTenantId(tenantId);
            insertOne.setSourceId(ruleLineId);
            defParameterValueRepository.insert(insertOne);
        }
    }

    /**
     * 重新同步审批人规则明细返回值
     * @param tenantId 当前租户ID
     * @param ruleLineId 当前审批人规则明细ID
     * @param defaultRuleLineId 预定义审批人规则明细ID
     */
    private void reimportDefApproverReturn(Long tenantId, Long ruleLineId, Long defaultRuleLineId) {
        DefApproverReturn approverReturn = new DefApproverReturn();
        approverReturn.setRuleLineId(defaultRuleLineId);
        approverReturn.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        for (DefApproverReturn defApproverReturn : defApproverReturnRepository.select(approverReturn)) {
            DefApproverReturn insertOne = new DefApproverReturn();
            insertOne.setRuleLineId(ruleLineId);
            insertOne.setFieldCode(defApproverReturn.getFieldCode());
            insertOne.setTenantId(tenantId);
            if (defApproverReturnRepository.selectOne(insertOne) != null) {
                continue;
            }
            BeanUtils.copyProperties(defApproverReturn, insertOne);
            insertOne.setReturnId(null);
            insertOne.setRuleLineId(ruleLineId);
            insertOne.setTenantId(tenantId);
            defApproverReturnRepository.insert(insertOne);
        }
    }

    /**
     * 重新同步流程变量
     * @param tenantId 当前租户ID
     * @param typeId 当前流程分类ID
     * @param defaultDefTypeId 预定义流程分类ID
     */
    private void reimportDefVariable(Long tenantId, Long typeId, Long defaultDefTypeId) {
        // 查询当前租户已存在的流程变量定义
        final List<DefVariable> currentDefVariables = this.defVariableRepository.selectByCondition(Condition.builder(DefVariable.class)
                        .where(Sqls.custom()
                                .andEqualTo(DefVariable.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(DefVariable.FIELD_TYPE_ID, typeId)
                        ).build());
        final Set<String> currentDefVariableCodes = currentDefVariables.stream().map(DefVariable::getVariableCode).collect(Collectors.toSet());
        // 查询预定义的流程变量定义
        final List<DefVariable> defaultDefVariables = this.defVariableRepository.selectByCondition(Condition.builder(DefVariable.class)
                .where(Sqls.custom()
                        .andEqualTo(DefVariable.FIELD_TENANT_ID, BaseConstants.DEFAULT_TENANT_ID)
                        .andEqualTo(DefVariable.FIELD_TYPE_ID, defaultDefTypeId)
                ).build());

        // 找到当前租户不存在但预定义里存在的流程变量, 执行插入
        defaultDefVariables.stream()
                .filter(var -> !currentDefVariableCodes.contains(var.getVariableCode()))
                .peek(var -> {
                    var.setVariableId(null);
                    var.setTypeId(typeId);
                    var.setTenantId(tenantId);
                })
                .forEach(defVariableRepository::insertSelective);
    }

//    private List<DefWorkflow> getDefaultWorkFlowFromJson() {
//        List<DefWorkflow> defWorkFlows;
//        try {
//            InputStream inputStream = this.getClass().getResourceAsStream(FILE_PATH);
//            String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//            defWorkFlows = JsonUtils.toObject(this.objectMapper, json, new TypeReference<List<DefWorkflow>>() {
//            });
//        } catch (Exception e) {
//            throw new CommonException(ERROR_WORK_FLOW_INIT_FILE_INVALID);
//        }
//        return defWorkFlows;
//    }

//    private DefType copyDefaultType(DefType defType, Long tenantId) {
//        OrganizationInfoVO organizationInfoVO = iamFeignClient.queryOrganizationInfo(tenantId).getBody();
//        if (ObjectUtils.isEmpty(organizationInfoVO)) {
//            throw new CommonException(ERROR_ORG_NOT_EXIST);
//        }
//        String typeCode = HzeroWorkFlowConstants.DEFAULT_TYPE_CODE;
//        if (validInitDefType(tenantId, typeCode)) {
//            throw new CommonException(ERROR_WORK_FLOW_INIT_TYPE_EXIST);
//        }
//        DefTypeDTO.DefTypeCreateDTO defTypeCreate = new DefTypeDTO.DefTypeCreateDTO();
//        BeanUtils.copyProperties(defType, defTypeCreate);
//        defTypeCreate.setTypeCode(typeCode);
//        defTypeCreate.setTypeName(ORG_TYPE_NAME);
//        defTypeCreate.setCopyTypeId(defType.getTypeId());
//        defTypeCreate.setInitFlag(false);
//        defTypeCreate.set_status(AuditDomain.RecordStatus.create);
//
//        defTypeService.copyDefType(tenantId, defTypeCreate);
//        return getDefTypeByCode(tenantId, typeCode);
//    }

    @Override
    public boolean validInitDefType(Long tenantId, String typeCode) {
        DefType validDefType = getDefTypeByCode(tenantId, typeCode);
        return !ObjectUtils.isEmpty(validDefType);
    }


//    private String importWorkFlowJsonConvert(String diagramJson) {
//        ObjectNode root = (ObjectNode) JsonUtils.jsonToJsonNode(this.objectMapper, diagramJson);
//        ArrayNode nodes = (ArrayNode) root.get("nodes");
//
//        for (int i = 0; i < nodes.size(); ++i) {
//            JsonNode node = nodes.get(i);
//            String type = node.get("type").asText();
//            JsonNode chainJsonNodes;
//            if ("subProcessNode".equals(type)) {
//                ((ObjectNode) node).replace("version", null);
//                chainJsonNodes = node.get("subProcess");
//                ((ObjectNode) chainJsonNodes).replace("version", null);
//            } else if ("manualNode".equals(type)) {
//                ((ObjectNode) node).replace("version", null);
//                chainJsonNodes = node.get("approveChain");
//                if (chainJsonNodes != null && !chainJsonNodes.isNull()) {
//                    ((ObjectNode) chainJsonNodes).replace("version", null);
//                }
//            }
//        }
//
//        return root.toString();
//    }

    private DefType getDefTypeByCode(Long tenantId, String code) {
        DefType record = new DefType();
        record.setTypeCode(code);
        record.setTenantId(tenantId);
        return defTypeRepository.selectOne(record);
    }
}
