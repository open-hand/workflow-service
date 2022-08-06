package io.choerodon.workflow.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.workflow.api.vo.RunTaskHistoryVO;
import io.choerodon.workflow.app.service.PersonalProcessC7nService;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.workflow.def.infra.annotation.ProcessUserDecrypt;
import org.hzero.workflow.def.infra.annotation.UserDecrypt;
import org.hzero.workflow.personal.api.dto.PersonalTodoDTO;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:20:24
 */
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/personal_process")
public class PersonalProcessC7nController {

    @Autowired
    private PersonalProcessC7nService personalProcessC7nService;

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation("通过instanceId查询工作流审批历史记录")
    @ProcessLovValue(
            targetField = {"body.runTaskHistory", "body.runTaskHistory.subProcessHistoryList", "body.runTaskHistory.subProcessHistoryList.subProcessHistoryList"}
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

    @ApiOperation("分页查询我的待办")
    @Permission(
            level = ResourceLevel.ORGANIZATION
    )
    @GetMapping({"/task/page"})
    @ProcessLovValue(
            targetField = {"body"}
    )
    @ProcessUserDecrypt
    public ResponseEntity<Page<PersonalTodoDTO.PersonalTodoViewDTO>> choerodonPageByOptions(@PathVariable("organization_id") Long tenantId,
                                                                                            PageRequest pageRequest,
                                                                                            @UserDecrypt(targetField = {"starter"})
                                                                                            PersonalTodoDTO.PersonalTodoQueryDTO queryDTO,
                                                                                            @RequestParam(required = false) List<Long> backlogIds) {
        return Results.success(this.personalProcessC7nService.selectPersonalTodo(tenantId, pageRequest, queryDTO, backlogIds));
    }

    /**
     * 晓燕说没有这个功能, 不知道是干啥的
     * @param tenantId tenantId
     * @param pageRequest pageRequest
     * @param queryDTO queryDTO
     * @param backlogIds backlogIds
     * @return result
     */
    @ApiOperation("分页查询我的已办")
    @Permission(
            level = ResourceLevel.ORGANIZATION
    )
    @GetMapping({"/task/done"})
    @ProcessLovValue(
            targetField = {"body"}
    )
    public ResponseEntity<Page<PersonalTodoDTO.PersonalTodoViewDTO>> choerodonPageDone(@PathVariable("organization_id") Long tenantId,
                                                                                       PageRequest pageRequest,
                                                                                       PersonalTodoDTO.PersonalTodoQueryDTO queryDTO,
                                                                                       @RequestParam(required = false) List<Long> backlogIds) {
        return Results.success(this.personalProcessC7nService.pageDone(tenantId, pageRequest, queryDTO, backlogIds));
    }

    @ApiOperation("分页查询我参与的流程")
    @Permission(
            level = ResourceLevel.ORGANIZATION
    )
    @GetMapping({"/participated"})
    @CustomPageRequest
    @ProcessLovValue(
            targetField = {"body", "body.subProcessChildren", "body.subProcessChildren.subProcessChildren"}
    )
    @ProcessUserDecrypt
    public ResponseEntity<Page<PersonalTodoDTO.ParticipatedDTO>> choerodonMineParticipated(@PathVariable("organization_id") Long tenantId,
                                                                                           @ApiIgnore PageRequest pageRequest,
                                                                                           @UserDecrypt(targetField = {"starter", "assignee"})
                                                                                           PersonalTodoDTO.PersonalTodoQueryDTO queryDTO,
                                                                                           @RequestParam(required = false) List<Long> backlogIds) {
        return Results.success(this.personalProcessC7nService.mineParticipated(tenantId, pageRequest, queryDTO, backlogIds));
    }

    @ApiOperation("分页查询我发起的流程")
    @Permission(
            level = ResourceLevel.ORGANIZATION
    )
    @GetMapping({"/submitted"})
    @CustomPageRequest
    @ProcessLovValue(
            targetField = {"body", "body.subProcessChildren", "body.subProcessChildren.subProcessChildren"}
    )
    @ProcessUserDecrypt
    public ResponseEntity<Page<PersonalTodoDTO.SubmittedDTO>> choerodonMineSubmitted(@PathVariable("organization_id") Long tenantId,
                                                                                     @ApiIgnore PageRequest pageRequest,
                                                                                     @UserDecrypt(targetField = {"assignee"})
                                                                                     PersonalTodoDTO.PersonalTodoQueryDTO queryDTO,
                                                                                     @RequestParam(required = false) List<Long> backlogIds) {
        return Results.success(this.personalProcessC7nService.mineSubmitted(tenantId, pageRequest, queryDTO, backlogIds));
    }

    @ApiOperation("分页查询我抄送的流程")
    @Permission(
            level = ResourceLevel.ORGANIZATION
    )
    @GetMapping({"/carbon-copied"})
    @CustomPageRequest
    @ProcessLovValue(
            targetField = {"body", "body.subProcessChildren", "body.subProcessChildren.subProcessChildren"}
    )
    @ProcessUserDecrypt
    public ResponseEntity<Page<PersonalTodoDTO.CarbonCopyDTO>> choerodonMineCarbonCopied(@PathVariable("organization_id") Long tenantId,
                                                                                         @ApiIgnore PageRequest pageRequest,
                                                                                         @UserDecrypt(targetField = {"starter", "assignee"})
                                                                                         PersonalTodoDTO.PersonalTodoQueryDTO queryDTO,
                                                                                         @RequestParam(required = false) List<Long> backlogIds) {
        return Results.success(this.personalProcessC7nService.mineCarbonCopied(tenantId, pageRequest, queryDTO, backlogIds));
    }

}
