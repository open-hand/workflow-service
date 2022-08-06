package io.choerodon.workflow.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.app.service.DefWorkflowC7nService;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Results;
import org.hzero.workflow.def.api.dto.DefWorkflowDTO;
import org.hzero.workflow.def.domain.entity.DefWorkflow;

/**
 * @author huaxin.deng@hand-china.com 2021-04-01 17:07:50
 */
@Controller
@RequestMapping("/choerodon/v1/projects/{project_id}/def_workflows")
public class DefWorkflowC7nController {

    @Autowired
    private DefWorkflowC7nService defWorkflowC7nService;

    @ApiOperation("项目层-分页查询已发布的流程定义列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/page/released"})
    public ResponseEntity<Page<DefWorkflow>> pageReleasedByCondition(@PathVariable("project_id") Long projectId,
                                                                     @RequestParam("organizationId") Long tenantId,
                                                                     PageRequest pageRequest,
                                                                     DefWorkflowDTO.DefWorkflowQueryDTO queryDTO) {
        queryDTO.setSiteFlag(BaseConstants.Flag.NO);
        return Results.success(defWorkflowC7nService.pageReleasedByOptions(tenantId, pageRequest, queryDTO));
    }
}
