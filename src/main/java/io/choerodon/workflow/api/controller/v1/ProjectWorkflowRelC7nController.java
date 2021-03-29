package io.choerodon.workflow.api.controller.v1;

import io.choerodon.workflow.api.vo.ProjectWorkflowRelVO;
import io.choerodon.workflow.app.service.ProjectWorkflowRelService;
import io.choerodon.workflow.infra.dto.ProjectWorkflowRelDTO;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaotianxin
 * @date 2021-03-08 19:41
 */
@RestController
@RequestMapping(value = "/choerodon/v1/projects/{project_id}/project_workflow_rel")
public class ProjectWorkflowRelC7nController {
    @Autowired
    private ProjectWorkflowRelService projectWorkflowRelService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("建立项目和工作流的关系")
    @PostMapping
    public ResponseEntity<ProjectWorkflowRelDTO> createOrUpdate(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                                @RequestBody ProjectWorkflowRelDTO projectWorkflowRelDTO) {
        return new ResponseEntity<>(projectWorkflowRelService.createOrUpdate(projectId, projectWorkflowRelDTO), HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询项目关联的流程")
    @GetMapping("/query")
    public ResponseEntity<ProjectWorkflowRelVO> queryWorkflow(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId) {
        return new ResponseEntity<>(projectWorkflowRelService.queryProjectWorkflow(projectId), HttpStatus.OK);
    }
}
