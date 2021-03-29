package io.choerodon.workflow.api.controller.v1;

import io.choerodon.workflow.api.vo.ProjectWorkflowRelVO;
import io.choerodon.workflow.app.service.ProjectWorkflowRelService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huaxin.deng@hand-china.com 2021-03-18 15:15:56
 */
@RestController
@RequestMapping(value = "/choerodon/v1/projects/{project_id}/project_external_workflow_rel")
public class ProjectExternalWorkflowRelC7nController {

    @Autowired
    private ProjectWorkflowRelService projectWorkflowRelService;

    @Permission(permissionLogin = true)
    @ApiOperation("查询项目关联的流程")
    @GetMapping("/query")
    public ResponseEntity<ProjectWorkflowRelVO> queryWorkflow(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId) {
        return new ResponseEntity<>(projectWorkflowRelService.queryProjectWorkflow(projectId), HttpStatus.OK);
    }
}
