package io.choerodon.workflow.infra.feign;

import io.choerodon.workflow.infra.feign.fallback.DevopsServiceClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Sheep on 2019/4/15.
 */

@FeignClient(value = "devops-service", fallback = DevopsServiceClientFallBack.class)

public interface DevopsServiceClient {

    @GetMapping(value = "/workflow/auto_deploy")
    ResponseEntity autoDeploy(
            @RequestParam(value="stage_record_id") Long stageRecordId,
            @RequestParam(value="task_id") Long taskId);

    @PutMapping(value = "/workflow/auto_deploy/status")
    ResponseEntity setAutoDeployTaskStatus(
            @RequestParam(value="pipeline_record_id") Long pipelineRecordId,
            @RequestParam(value="stage_record_id") Long stageRecordId,
            @RequestParam(value="task_id") Long taskId,
            @RequestParam(value="status") Boolean status);

    @GetMapping(value = "/workflow/auto_deploy/status")
    ResponseEntity<String> getAutoDeployTaskStatus(
            @RequestParam(value="stage_record_id") Long stageRecordId,
            @RequestParam(value="task_id") Long taskId);

}
