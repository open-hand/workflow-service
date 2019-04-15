package io.choerodon.workflow.api.controller.v1;

import java.util.Optional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.api.controller.dto.DevopsPipelineDTO;
import io.choerodon.workflow.app.service.ProcessInstanceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Sheep on 2019/4/2.
 */


@Controller
@RequestMapping("/v1/projects/{project_id}/process_instances")
public class ProcessInstanceController {


    @Autowired
    ProcessInstanceService processInstanceService;

    /**
     * Devops部署pipeline
     * @param  projectId  项目id
     * @param  devopsPipelineDTO  CD流水线信息
     * @return String
     */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "Devops部署pipeline")
    @PostMapping
    public ResponseEntity<String> create(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "应用信息", required = true)
            @RequestBody DevopsPipelineDTO devopsPipelineDTO) {
        return Optional.ofNullable(processInstanceService.beginDevopsPipeline(devopsPipelineDTO))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.devops.pipeline.process.start"));
    }


    /**
     * 审核DevopsCD任务
     *
     * @param  projectId  项目id
     * @param  processInstanceId  流程实例id
     * @return Boolean
     */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "Devops部署pipeline")
    @PutMapping
    public ResponseEntity<Boolean> approveUserTask(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "应用信息", required = true)
            @RequestParam String processInstanceId) {
        return Optional.ofNullable(processInstanceService.approveUserTask(processInstanceId))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.task.approve"));
    }


}
