package io.choerodon.workflow.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.api.vo.ProjectWorkflowRelVO;
import io.choerodon.workflow.app.service.ProjectWorkflowRelService;
import io.choerodon.workflow.domain.entity.ProjectWorkflowRel;

import org.hzero.core.util.Results;

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
    public ResponseEntity<ProjectWorkflowRel> createOrUpdate(@ApiParam(value = "项目id", required = true)
                                                                @PathVariable(name = "project_id") Long projectId,
                                                             @RequestBody ProjectWorkflowRel projectWorkflowRel) {
        return Results.created(projectWorkflowRelService.createOrUpdate(projectId, projectWorkflowRel));
    }

    /**
     * 与io.choerodon.workflow.api.controller.v1.ProjectExternalWorkflowRelC7nController#queryWorkflow(java.lang.Long)重复<br/>
     * 不知道哪个是多余的
     * @param projectId 项目ID
     * @return 查询结果
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询项目关联的流程")
    @GetMapping("/query")
    public ResponseEntity<ProjectWorkflowRelVO> queryWorkflow(@ApiParam(value = "项目id", required = true)
                                                              @PathVariable(name = "project_id") Long projectId) {
        return Results.success(projectWorkflowRelService.queryProjectWorkflow(projectId));
    }
}
