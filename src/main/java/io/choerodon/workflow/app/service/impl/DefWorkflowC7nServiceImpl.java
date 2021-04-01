package io.choerodon.workflow.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.workflow.app.service.DefWorkflowC7nService;
import org.hzero.workflow.def.api.dto.DefWorkflowDTO;
import org.hzero.workflow.def.app.service.DefWorkflowService;
import org.hzero.workflow.def.domain.entity.DefWorkflow;
import org.hzero.workflow.def.infra.common.utils.PageConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author huaxin.deng@hand-china.com 2021-04-01 17:27:22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DefWorkflowC7nServiceImpl implements DefWorkflowC7nService {

    @Autowired
    private DefWorkflowService defWorkflowService;

    @Override
    public Page<DefWorkflow> pageReleasedByOptions(Long tenantId, PageRequest pageRequest, DefWorkflowDTO.DefWorkflowQueryDTO queryDTO) {
        PageRequest allPage = new PageRequest(0, 0);
        Page<DefWorkflow> page = defWorkflowService.pageByOptions(tenantId, allPage, queryDTO);
        List<DefWorkflow> defWorkflowList = page.getContent();
        List<DefWorkflow> releasedDefWorkFlowList = defWorkflowList.stream()
                .filter(defWorkflow -> (!Objects.isNull(defWorkflow.getVersion()) && defWorkflow.getVersion() > 0))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(releasedDefWorkFlowList)) {
            return new Page<>();
        }
        return PageConvertUtils.convert(pageRequest.getPage(), pageRequest.getSize(), releasedDefWorkFlowList);
    }
}
