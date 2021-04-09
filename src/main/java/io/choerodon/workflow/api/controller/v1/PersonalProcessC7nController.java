package io.choerodon.workflow.api.controller.v1;

import io.choerodon.workflow.api.vo.RunTaskHistoryVO;
import io.choerodon.workflow.app.service.PersonalProcessC7nService;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:20:24
 */
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/personal_process")
public class PersonalProcessC7nController {

    @Autowired
    private PersonalProcessC7nService personalProcessC7nService;

    @Permission(level = ResourceLevel.ORGANIZATION,permissionLogin = true)
    @ApiOperation("通过instanceId查询工作流审批历史记录")
    @ProcessLovValue(
            targetField = {"body.runTaskHistory",  "body.runTaskHistory.subProcessHistoryList", "body.runTaskHistory.subProcessHistoryList.subProcessHistoryList"}
    )
    @GetMapping({"/approve_history"})
    public ResponseEntity<List<RunTaskHistoryVO>> listApproveHistoryByInstanceId(@ApiParam(value = "组织id", required = true)
                                                                                 @PathVariable("organization_id") Long tenantId,
                                                                                 @ApiParam(value = "instance id", required = true)
                                                                                 @RequestParam @Encrypt Long instanceId) {
        return Optional.ofNullable(personalProcessC7nService.listApproveHistoryByInstanceId(tenantId, instanceId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.list.approveHistory"));
    }

}
