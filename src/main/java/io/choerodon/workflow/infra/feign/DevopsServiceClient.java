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
            @RequestParam Long stageRecordId,
            @RequestParam Long taskId);

    @PutMapping(value = "/workflow/auto_deploy/status")
    ResponseEntity setAutoDeployTaskStatus(
            @RequestParam Long pipelineRecordId,
            @RequestParam Long stageRecordId,
            @RequestParam Long taskId);

    @GetMapping(value = "/workflow/auto_deploy/status")
    ResponseEntity<Boolean> getAutoDeployTaskStatus(
            @RequestParam Long stageRecordId,
            @RequestParam Long taskId);

}
