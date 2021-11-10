package io.choerodon.workflow.api.controller.v1;

import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.api.vo.DevopsPipelineVO;
import io.choerodon.workflow.api.vo.HzeroDeployPipelineVO;
import io.choerodon.workflow.app.service.PipelineService;
import io.choerodon.workflow.app.service.ProcessInstanceService;

/**
 * Created by Sheep on 2019/4/2.
 */

@Controller
@RequestMapping("/v1/projects/{project_id}/process_instances")
public class ProcessInstanceController {


    @Autowired
    ProcessInstanceService processInstanceService;
    @Autowired
    private PipelineService  pipelineService;

    /**
     * Devops部署pipeline
     * @param  projectId  项目id
     * @param  devopsPipelineVO  CD流水线信息
     * @return String
     */
    @Permission(permissionWithin = true)
    @ApiOperation(value = "Devops部署pipeline")
    @PostMapping
    public ResponseEntity<Void> create(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "应用信息", required = true)
            @RequestBody DevopsPipelineVO devopsPipelineVO) {
        pipelineService.beginDevopsPipelineSaga(devopsPipelineVO);
        return ResponseEntity.noContent().build();
    }


    /**
     * 审核DevopsCD任务
     *
     * @param  projectId  项目id
     * @param  businessKey  流程实例id
     * @return Boolean
     */
    @Permission(permissionWithin = true)
    @ApiOperation(value = "Devops部署pipeline")
    @PutMapping
    public ResponseEntity<Boolean> approveUserTask(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "实例Id", required = true)
            @RequestParam(value = "business_key") String businessKey) {
        return Optional.ofNullable(processInstanceService.approveUserTask(businessKey))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.task.approve"));
    }


    /**
     * 根据业务key删除实例
     *
     * @param  projectId  项目id
     * @param  businessKey  流程业务id
     * @return
     */
    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据业务key删除实例")
    @GetMapping
    public ResponseEntity<Void> stopInstance(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "实例Id", required = true)
            @RequestParam(value = "business_key") String businessKey) {
        processInstanceService.stopInstance(businessKey);
        return ResponseEntity.noContent().build();
    }


    /**
     * Devops部署pipeline
     * @param  projectId  项目id
     * @param  devopsPipelineVO  CD流水线信息
     * @return String
     */
    @Permission(permissionWithin = true)
    @ApiOperation(value = "Devops cicd流水线")
    @PostMapping("/cicd_pipeline")
    public ResponseEntity<Void> createCiCdPipeline(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "应用信息", required = true)
            @RequestBody DevopsPipelineVO devopsPipelineVO) {
        pipelineService.beginDevopsPipelineSagaCiCd(devopsPipelineVO);
        return ResponseEntity.noContent().build();
    }

    /**
     * hzero部署流水线
     * @param projectId
     * @param hzeroDeployPipelineVO
     * @return
     */
    @Permission(permissionWithin = true)
    @ApiOperation(value = "hzero部署流水线")
    @PostMapping("/hzero_pipeline")
    public ResponseEntity<Void> createHzeroPipeline(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(value = "project_id") Long projectId,
            @ApiParam(value = "应用信息", required = true)
            @RequestBody HzeroDeployPipelineVO hzeroDeployPipelineVO) {
        pipelineService.createHzeroPipeline(projectId, hzeroDeployPipelineVO);
        return ResponseEntity.noContent().build();
    }
}
