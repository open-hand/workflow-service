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
import org.hzero.workflow.def.domain.entity.DefType;
import org.hzero.workflow.def.domain.entity.DefWorkflow;
import org.hzero.workflow.def.domain.repository.DefTypeRepository;
import org.hzero.workflow.def.domain.repository.DefWorkflowRepository;
import org.hzero.workflow.engine.util.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
    private static final String ORG_TYPE_NAME = "需求审核";
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
        String typeCode = DEFAULT_TYPE_CODE + "_" + organizationInfoVO.getTenantNum();
        validInitDefType(tenantId, typeCode);
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

    private void validInitDefType(Long tenantId, String typeCode) {
        DefType validDefType = getDefTypeByCode(tenantId, typeCode);
        if (!ObjectUtils.isEmpty(validDefType)) {
            throw new CommonException(ERROR_WORK_FLOW_INIT_TYPE_EXIST);
        }
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
