package io.choerodon.workflow.infra.feign;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.util.Results;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.infra.feign.fallback.DevopsServiceClientFallBack;

/**
 * Created by Sheep on 2019/4/15.
 */

@FeignClient(value = "devops-service", fallback = DevopsServiceClientFallBack.class)

public interface DevopsServiceClient {

    @GetMapping(value = "/workflow/auto_deploy")
    ResponseEntity autoDeploy(
            @RequestParam(value = "stage_record_id") Long stageRecordId,
            @RequestParam(value = "task_record_id") Long taskRecordId);

    @PutMapping(value = "/workflow/auto_deploy/status")
    ResponseEntity setAutoDeployTaskStatus(
            @RequestParam(value = "pipeline_record_id") Long pipelineRecordId,
            @RequestParam(value = "stage_record_id") Long stageRecordId,
            @RequestParam(value = "task_record_id") Long taskRecordId,
            @RequestParam(value = "status") Boolean status);

    @GetMapping(value = "/workflow/auto_deploy/status")
    ResponseEntity<String> getAutoDeployTaskStatus(
            @RequestParam(value = "stage_record_id") Long stageRecordId,
            @RequestParam(value = "task_record_id") Long taskRecordId);

    @PostMapping(value = "/v1/cd_pipeline/cd_host_deploy")
    ResponseEntity cdHostDeploy(@RequestParam(value = "pipeline_record_id") Long pipelineRecordId,
                                @RequestParam(value = "stage_record_id") Long stageRecordId,
                                @RequestParam(value = "job_record_id") Long jobRecordId);

    @PostMapping(value = "/v1/cd_pipeline/env_auto_deploy")
    ResponseEntity envAutoDeploy(@RequestParam(value = "pipeline_record_id") Long pipelineRecordId,
                                 @RequestParam(value = "stage_record_id") Long stageRecordId,
                                 @RequestParam(value = "job_record_id") Long jobRecordId);

    @PutMapping("/v1/cd_pipeline/auto_deploy/status")
    ResponseEntity setAppDeployStatus(
            @RequestParam(value = "pipeline_record_id") Long pipelineRecordId,
            @RequestParam(value = "stage_record_id") Long stageRecordId,
            @RequestParam(value = "job_record_id") Long jobRecordId,
            @RequestParam(value = "status") Boolean status);

    @GetMapping("/v1/cd_pipeline/job/status")
    ResponseEntity<String> getJobStatus(
            @RequestParam(value = "pipeline_record_id") Long pipelineRecordId,
            @RequestParam(value = "stage_record_id") Long stageRecordId,
            @RequestParam(value = "job_record_id") Long jobRecordId);


    @PostMapping(value = "/v1/cd_pipeline/execute_api_test_task")
    ResponseEntity<Void> executeApiTestTask(
            @RequestParam(value = "pipeline_record_id") Long pipelineRecordId,
            @RequestParam(value = "stage_record_id") Long stageRecordId,
            @RequestParam(value = "job_record_id") Long jobRecordId);

    @GetMapping(value = "/v1/cd_pipeline/deploy_status")
    ResponseEntity<String> getDeployStatus(
            @RequestParam(value = "pipeline_record_id") Long pipelineRecordId,
            @RequestParam(value = "deploy_job_name") String deployJobName);


    @PostMapping("/v1/cd_pipeline/execute_external_approval_task")
    ResponseEntity<Void> executeExternalApprovalTask(@RequestParam(value = "pipeline_record_id") Long cdPipelineRecordId,
                                                        @RequestParam(value = "stage_record_id") Long cdStageRecordId,
                                                        @RequestParam(value = "job_record_id") Long cdJobRecordId);

    @ApiOperation(value = "hzero部署接口")
    @PostMapping("/v1/cd_pipeline/hzero_deploy")
    ResponseEntity<String> hzeroDeploy( @RequestParam(value = "details_record_id") Long detailsRecordId);
}
