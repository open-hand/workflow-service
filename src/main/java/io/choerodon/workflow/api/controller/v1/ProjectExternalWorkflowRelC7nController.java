package io.choerodon.workflow.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.api.vo.ProjectWorkflowRelVO;
import io.choerodon.workflow.app.service.ProjectWorkflowRelService;

import org.hzero.core.util.Results;

/**
 * @author huaxin.deng@hand-china.com 2021-03-18 15:15:56
 */
@RestController
@RequestMapping(value = "/choerodon/v1/projects/{project_id}/project_external_workflow_rel")
public class ProjectExternalWorkflowRelC7nController {

    @Autowired
    private ProjectWorkflowRelService projectWorkflowRelService;

    /**
     * 与io.choerodon.workflow.api.controller.v1.ProjectWorkflowRelC7nController#queryWorkflow(java.lang.Long)重复<br/>
     * 不知道哪个是多余的
     * @param projectId 项目ID
     * @return 查询结果
     */
    @Permission(permissionLogin = true)
    @ApiOperation("查询项目关联的流程")
    @GetMapping("/query")
    public ResponseEntity<ProjectWorkflowRelVO> queryWorkflow(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId) {
        return Results.success(projectWorkflowRelService.queryProjectWorkflow(projectId));
    }
}
