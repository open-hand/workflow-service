package io.choerodon.workflow.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.workflow.def.api.dto.DefWorkflowDTO;
import org.hzero.workflow.def.domain.entity.DefWorkflow;

/**
 * @author huaxin.deng@hand-china.com 2021-04-01 17:21:22
 */
public interface DefWorkflowC7nService {

    /**
     * 分页查询已发布的流程定义
     * @param tenantId
     * @param pageRequest
     * @param queryDTO
     * @return
     */
    Page<DefWorkflow> pageReleasedByOptions(Long tenantId, PageRequest pageRequest, DefWorkflowDTO.DefWorkflowQueryDTO queryDTO);
}
