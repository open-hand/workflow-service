package io.choerodon.workflow.app.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.hzero.workflow.def.api.dto.DefTypeDTO;
import org.hzero.workflow.def.app.service.DefModelService;
import org.hzero.workflow.def.app.service.DefTypeService;
import org.hzero.workflow.def.domain.entity.*;
import org.hzero.workflow.def.domain.repository.*;
import org.hzero.workflow.engine.util.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.workflow.app.service.OrganizationWorkflowC7nService;
import io.choerodon.workflow.infra.feign.BaseFeignClient;
import io.choerodon.workflow.infra.feign.vo.OrganizationInfoVO;

/**
 * @author chihao.ran@hand-china.com
 * 2021/03/30 14:54
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrganizationWorkflowC7nServiceImpl implements OrganizationWorkflowC7nService {

    private static final String FILE_PATH = "/templates/default_flow.json";
    private static final String ORG_TYPE_NAME = "需求审核-预定义";
    private static final String DEFAULT_TYPE_CODE = "HWKF_BACKLOG_APPROVE";
    private static final String ERROR_WORK_FLOW_INIT_TYPE_EXIST = "error.work.flow.init.type.exist";
    private static final String ERROR_WORK_FLOW_INIT_FILE_INVALID = "error.init_file_invalid";
    private static final String ERROR_WORK_FLOW_DEFAULT_TYPE_NOT_EXIST = "error.work.flow.default.type.not.exist";
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
    private BaseFeignClient baseFeignClient;
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

    private static final List<String> ADD_DEF_VARIABLE =
            Arrays.asList("project_id");

    private static final Map<String, List<String>> ADD_RULE_MAP = new HashMap<>();

    static  {
        ADD_RULE_MAP.put("APPROVER_RULE_006_PRE", Arrays.asList("LINE_APPOINT_PROJECT_ID"));
    }

    @Override
    public void initDefWorkFlows(Long tenantId) {
        DefType defaultDefType = getDefTypeByCode(0L, DEFAULT_TYPE_CODE);
        if (ObjectUtils.isEmpty(defaultDefType)) {
            throw new CommonException(ERROR_WORK_FLOW_DEFAULT_TYPE_NOT_EXIST);
        }
        DefType newDefType = copyDefaultType(defaultDefType, tenantId);
        List<DefWorkflow> defWorkFlows = getDefaultWorkFlowFromJson();
        defWorkFlows.forEach(defWorkflow -> {
            defWorkflow.setDiagramJson(importWorkFlowJsonConvert(defWorkflow.getDiagramJson()));
            defWorkflow.setTenantId(tenantId);
            defWorkflow.setTypeCode(newDefType.getTypeCode());
            defWorkflow.setTypeId(newDefType.getTypeId());
            defWorkflowRepository.insertSelective(defWorkflow);
            defModelService.saveConfig(tenantId, defWorkflow.getFlowId(), defWorkflow.getModelConfigVO(), defWorkflow.getDiagramJson());
        });
    }

    @Override
    public Boolean checkInit(Long tenantId) {
        OrganizationInfoVO organizationInfoVO = baseFeignClient.queryOrganizationInfo(tenantId).getBody();
        if (ObjectUtils.isEmpty(organizationInfoVO)) {
            throw new CommonException(ERROR_ORG_NOT_EXIST);
        }
        String typeCode = DEFAULT_TYPE_CODE + "_ORG";
        return validInitDefType(tenantId, typeCode);
    }

    @Override
    public void reimportWorkflow(Long tenantId) {
        if (!checkInit(tenantId)) {
            throw new CommonException("error.workflow.not.init");
        }
        DefType defType = getDefTypeByCode(tenantId,DEFAULT_TYPE_CODE + "_ORG");
        if (ObjectUtils.isEmpty(defType)) {
            throw new CommonException("error.workflow.predefined.workflow");
        }
        Long typeId = defType.getTypeId();
        DefType defaultDefType = getDefTypeByCode(0L, DEFAULT_TYPE_CODE);
        Long defaultDefTypeId = defaultDefType.getTypeId();
        //增量添加流程变量
        addDefVariable(tenantId, typeId, defaultDefTypeId);
        //增量添加审批人规则
        addDefApproverRule(tenantId, typeId, defaultDefTypeId);
    }

    private void addDefApproverRule(Long tenantId, Long typeId, Long defaultDefTypeId) {
        ADD_RULE_MAP.forEach((code, lineCodes) -> {
            DefApproverRule rule = new DefApproverRule();
            rule.setTenantId(tenantId);
            rule.setTypeId(typeId);
            rule.setRuleCode(code);
            DefApproverRule existedRule = defApproverRuleRepository.selectOne(rule);
            if (!ObjectUtils.isEmpty(existedRule)) {
                return;
            }
            DefApproverRule defaultRule = new DefApproverRule();
            defaultRule.setTenantId(0L);
            defaultRule.setTypeId(defaultDefTypeId);
            defaultRule.setRuleCode(code);
            DefApproverRule defaultExistedRule = defApproverRuleRepository.selectOne(defaultRule);
            if (ObjectUtils.isEmpty(defaultExistedRule)) {
                throw new CommonException("error.workflow.defApproverRule." + code + ".not.existed");
            }
            DefApproverRule insertRule = new DefApproverRule();
            BeanUtils.copyProperties(defaultExistedRule, insertRule);
            insertRule.setRuleId(null);
            insertRule.setTypeId(typeId);
            insertRule.setTenantId(tenantId);
            if (defApproverRuleRepository.insert(insertRule) != 1) {
                throw new CommonException("error.insert.workflow.defApproverRule");
            }
            Long ruleId = insertRule.getRuleId();
            Long defaultRuleId = defaultExistedRule.getRuleId();
            lineCodes.forEach(lineCode -> {
                DefApproverRuleLine ruleLine = new DefApproverRuleLine();
                ruleLine.setTenantId(tenantId);
                ruleLine.setRuleId(ruleId);
                ruleLine.setLineCode(lineCode);
                DefApproverRuleLine existedRuleLine = defApproverRuleLineRepository.selectOne(ruleLine);
                if (!ObjectUtils.isEmpty(existedRuleLine)) {
                    return;
                }

                DefApproverRuleLine defaultRuleLine = new DefApproverRuleLine();
                defaultRuleLine.setTenantId(0L);
                defaultRuleLine.setRuleId(defaultRuleId);
                defaultRuleLine.setLineCode(lineCode);
                DefApproverRuleLine defaultExistedRuleLine = defApproverRuleLineRepository.selectOne(defaultRuleLine);
                if (ObjectUtils.isEmpty(defaultExistedRuleLine)) {
                    throw new CommonException("error.workflow.defApproverRuleLine." + lineCode + ".not.existed");
                }
                DefApproverRuleLine insertRuleLine = new DefApproverRuleLine();
                BeanUtils.copyProperties(defaultExistedRuleLine, insertRuleLine);
                insertRuleLine.setRuleLineId(null);
                insertRuleLine.setRuleId(ruleId);
                insertRuleLine.setTenantId(tenantId);
                insertRuleLine.setSourceCode("hwkf.ydy.queryUserByProjectRoles");
                if (defApproverRuleLineRepository.insert(insertRuleLine) != 1) {
                    throw new CommonException("error.insert.workflow.defApproverRuleLine");
                }
                Long ruleLineId = insertRuleLine.getRuleLineId();
                Long defaultRuleLineId = defaultExistedRuleLine.getRuleLineId();
                insertDefApproverReturn(tenantId, ruleLineId, defaultRuleLineId);
                insertDefParameterValue(tenantId, ruleLineId, defaultRuleLineId);
            });
        });

    }

    private void insertDefParameterValue(Long tenantId, Long ruleLineId, Long defaultRuleLineId) {
        DefParameterValue defParameterValue = new DefParameterValue();
        defParameterValue.setTenantId(0L);
        defParameterValue.setSourceId(defaultRuleLineId);
        defParameterValue.setSourceType("DEFAULT");
        defParameterValue.setSourceTable("HWKF_DEF_APPROVER_RULE_LINE");
        defParameterValueRepository.select(defParameterValue).forEach(x -> {
            DefParameterValue insertOne = new DefParameterValue();
            insertOne.setTenantId(tenantId);
            insertOne.setSourceId(ruleLineId);
            insertOne.setSourceType(x.getSourceType());
            insertOne.setSourceTable(x.getSourceTable());
            insertOne.setParameterCode(x.getParameterCode());
            if (defParameterValueRepository.selectOne(insertOne) != null) {
                return;
            }
            BeanUtils.copyProperties(x, insertOne);
            insertOne.setParameterId(null);
            insertOne.setTenantId(tenantId);
            insertOne.setSourceId(ruleLineId);
            if (defParameterValueRepository.insert(insertOne) != 1) {
                throw new CommonException("error.insert.workflow.defParameterValue");
            }
        });
    }

    private void insertDefApproverReturn(Long tenantId, Long ruleLineId, Long defaultRuleLineId) {
        DefApproverReturn approverReturn = new DefApproverReturn();
        approverReturn.setRuleLineId(defaultRuleLineId);
        approverReturn.setTenantId(0L);
        defApproverReturnRepository.select(approverReturn).forEach(x -> {
            DefApproverReturn insertOne = new DefApproverReturn();
            insertOne.setRuleLineId(ruleLineId);
            insertOne.setFieldCode(x.getFieldCode());
            insertOne.setTenantId(tenantId);
            if (defApproverReturnRepository.selectOne(insertOne) != null) {
                return;
            }
            BeanUtils.copyProperties(x, insertOne);
            insertOne.setReturnId(null);
            insertOne.setRuleLineId(ruleLineId);
            insertOne.setTenantId(tenantId);
            if (defApproverReturnRepository.insert(insertOne) != 1) {
                throw new CommonException("error.insert.workflow.defApproverReturn");
            }
        });
    }

    private void addDefVariable(Long tenantId, Long typeId, Long defaultDefTypeId) {
        ADD_DEF_VARIABLE.forEach(variable -> {
            DefVariable defVariable = new DefVariable();
            defVariable.setTenantId(tenantId);
            defVariable.setTypeId(typeId);
            defVariable.setVariableCode(variable);
            DefVariable existedOne = defVariableRepository.selectOne(defVariable);
            if (ObjectUtils.isEmpty(existedOne)) {
                DefVariable defaultDefVariable = new DefVariable();
                defaultDefVariable.setTenantId(0L);
                defaultDefVariable.setTypeId(defaultDefTypeId);
                defaultDefVariable.setVariableCode(variable);
                DefVariable defaultExistedOne = defVariableRepository.selectOne(defaultDefVariable);
                if (ObjectUtils.isEmpty(defaultExistedOne)) {
                    throw new CommonException("error.workflow.defVariable." + variable + ".not.existed");
                }
                DefVariable insertOne = new DefVariable();
                BeanUtils.copyProperties(defaultExistedOne, insertOne);
                insertOne.setVariableId(null);
                insertOne.setTypeId(typeId);
                insertOne.setTenantId(tenantId);
                if (defVariableRepository.insert(insertOne) != 1) {
                    throw new CommonException("error.insert.workflow.defVariable");
                }
            }
        });
    }

    private List<DefWorkflow> getDefaultWorkFlowFromJson() {
        List<DefWorkflow> defWorkFlows;
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(FILE_PATH);
            String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            defWorkFlows = JsonUtils.toObject(this.objectMapper, json, new TypeReference<List<DefWorkflow>>() {
            });
        } catch (Exception e) {
            throw new CommonException(ERROR_WORK_FLOW_INIT_FILE_INVALID);
        }
        return defWorkFlows;
    }

    private DefType copyDefaultType(DefType defType, Long tenantId) {
        OrganizationInfoVO organizationInfoVO = baseFeignClient.queryOrganizationInfo(tenantId).getBody();
        if (ObjectUtils.isEmpty(organizationInfoVO)) {
            throw new CommonException(ERROR_ORG_NOT_EXIST);
        }
        String typeCode = DEFAULT_TYPE_CODE + "_ORG";
        if (validInitDefType(tenantId, typeCode)) {
            throw new CommonException(ERROR_WORK_FLOW_INIT_TYPE_EXIST);
        }
        DefTypeDTO.DefTypeCreateDTO defTypeCreate = new DefTypeDTO.DefTypeCreateDTO();
        BeanUtils.copyProperties(defType, defTypeCreate);
        defTypeCreate.setTypeCode(typeCode);
        defTypeCreate.setTypeName(ORG_TYPE_NAME);
        defTypeCreate.setCopyTypeId(defType.getTypeId());
        defTypeCreate.setInitFlag(false);
        defTypeCreate.set_status(AuditDomain.RecordStatus.create);

        defTypeService.copyDefType(tenantId, defTypeCreate);
        return getDefTypeByCode(tenantId, typeCode);
    }

    private boolean validInitDefType(Long tenantId, String typeCode) {
        DefType validDefType = getDefTypeByCode(tenantId, typeCode);
        return !ObjectUtils.isEmpty(validDefType);
    }


    private String importWorkFlowJsonConvert(String diagramJson) {
        ObjectNode root = (ObjectNode) JsonUtils.jsonToJsonNode(this.objectMapper, diagramJson);
        ArrayNode nodes = (ArrayNode) root.get("nodes");

        for (int i = 0; i < nodes.size(); ++i) {
            JsonNode node = nodes.get(i);
            String type = node.get("type").asText();
            JsonNode chainJsonNodes;
            if ("subProcessNode".equals(type)) {
                ((ObjectNode) node).replace("version", null);
                chainJsonNodes = node.get("subProcess");
                ((ObjectNode) chainJsonNodes).replace("version", null);
            } else if ("manualNode".equals(type)) {
                ((ObjectNode) node).replace("version", null);
                chainJsonNodes = node.get("approveChain");
                if (chainJsonNodes != null && !chainJsonNodes.isNull()) {
                    ((ObjectNode) chainJsonNodes).replace("version", null);
                }
            }
        }

        return root.toString();
    }

    private DefType getDefTypeByCode(Long tenantId, String code) {
        DefType record = new DefType();
        record.setTypeCode(code);
        record.setTenantId(tenantId);
        return defTypeRepository.selectOne(record);
    }
}
