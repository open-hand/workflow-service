package io.choerodon.workflow.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.app.service.RunInstanceExtendService;
import io.choerodon.workflow.infra.dto.ProjectWorkflowRelDTO;
import io.choerodon.workflow.infra.dto.RunInstanceExtendDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaotianxin
 * @date 2021-04-09 17:11
 */
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/run_instance_extend")
public class RunInstanceExtendController {
    @Autowired
    private RunInstanceExtendService runInstanceExtendService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建实例的扩展信息")
    @PostMapping
    public ResponseEntity<RunInstanceExtendDTO> create(@ApiParam(value = "组织Id", required = true)
                                                       @PathVariable(name = "organization_id") Long organizationId,
                                                       @RequestParam Long instanceId) {
        return new ResponseEntity<>(runInstanceExtendService.create(organizationId, instanceId), HttpStatus.CREATED);
    }
}
