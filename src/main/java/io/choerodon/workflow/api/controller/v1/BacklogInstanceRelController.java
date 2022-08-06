package io.choerodon.workflow.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.app.service.BacklogInstanceRelService;
import io.choerodon.workflow.domain.entity.BacklogInstanceRel;

import org.hzero.core.util.Results;

/**
 * @author zhaotianxin 2021-04-12 16:26
 */
@Controller
@RequestMapping("/choerodon/v1/organizations/{organization_id}/backlog_instance_rel")
public class BacklogInstanceRelController {
    @Autowired
    private BacklogInstanceRelService backlogInstanceRelService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建实例的扩展信息")
    @PostMapping
    public ResponseEntity<BacklogInstanceRel> create(@ApiParam(value = "组织Id", required = true)
                                                        @PathVariable(name = "organization_id") Long organizationId,
                                                     @RequestParam Long instanceId,
                                                     @RequestParam Long backlogId) {
        return Results.created(backlogInstanceRelService.create(organizationId, instanceId, backlogId));
    }
}
