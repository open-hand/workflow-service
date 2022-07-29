package io.choerodon.workflow.api.controller.v1;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.workflow.engine.model.node.FlowApproverValue;
import org.hzero.workflow.engine.run.action.AppointNextNodeApproverActionHandler;
import org.hzero.workflow.engine.run.dto.ProcessJumpNodeDTO;
import org.hzero.workflow.engine.util.EngineConstants;
import org.hzero.workflow.personal.app.service.PersonalActionService;
import org.hzero.workflow.personal.app.service.PersonalProcessService;
import org.hzero.workflow.personal.app.service.RunCommentTemplateService;
import org.hzero.workflow.personal.domain.entity.RunAttachment;
import org.hzero.workflow.personal.domain.entity.RunCommentTemplate;
import org.hzero.workflow.personal.domain.repository.RunCommentTemplateRepository;

/**
 * @author zhaotianxin 2021-03-10 16:21
 */
@RestController
@RequestMapping(value = "/choerodon/v1/projects/{project_id}/project_invoke_workflow")
public class ProjectInvokeWorkflowC7nController extends BaseController {
    @Autowired
    private PersonalActionService personalActionService;
    @Autowired
    private AppointNextNodeApproverActionHandler appointNextNodeApproverActionHandler;
    @Autowired
    private RunCommentTemplateService runCommentTemplateService;
    @Autowired
    private PersonalProcessService personalProcessService;
    @Autowired
    private RunCommentTemplateRepository runCommentTemplateRepository;

    @ApiOperation("项目层-我的待办-审批通过")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/personal-process/approve")
    public ResponseEntity<Void> flowApprove(@PathVariable("project_id") Long projectId,
                                            @RequestParam("organizationId") Long tenantId,
                                            @Encrypt @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                            @RequestBody(required = false) Map<String, Object> paramMap) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        personalActionService.batchProcess(tenantId, taskIds, EngineConstants.ApproveAction.APPROVED,false,null, paramMap);
        return Results.success();
    }

    @ApiOperation("项目层-我的待办-审批拒绝")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/personal-process/reject")
    public ResponseEntity<Void> flowReject(@PathVariable("project_id") Long projectId,
                                           @RequestParam("organizationId") Long tenantId,
                                           @Encrypt @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                           @RequestParam(required = false) Integer reApproveFlag,
                                           @RequestBody(required = false) Map<String, Object> paramMap) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        personalActionService.batchProcess(tenantId, taskIds, EngineConstants.ApproveAction.REJECTED, false, reApproveFlag, paramMap);
        return Results.success();
    }

    @ApiOperation("项目层-我的待办-根据taskId处理审批动作")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping({"/personal-process/{taskId}/executeTaskById"})
    public ResponseEntity<Void> executeTaskById(@PathVariable("project_id") Long projectId,
                                                @RequestParam("organizationId") Long tenantId,
                                                @PathVariable("taskId") @Encrypt Long taskId,
                                                @RequestParam String approveAction,
                                                @RequestBody Map<String, Object> paramMap) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        personalActionService.executeTaskById(tenantId, taskId, approveAction, paramMap);
        return Results.success();
    }

    @ApiOperation("项目层-我的待办-流程手动抄送")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping({"/personal-process/carbon-copy"})
    public ResponseEntity<Void> flowCarbonCopy(@PathVariable("project_id") Long projectId,
                                               @RequestParam("organizationId") Long tenantId,
                                               @Encrypt @RequestParam("taskId") Long taskId,
                                               @RequestBody List<FlowApproverValue> toPersonList) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        personalActionService.carbonCopy(tenantId, taskId, toPersonList);
        return Results.success();
    }

    @ApiOperation("项目层-预测下一审批人")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/personal-process/{taskId}/forecastNextNode"})
    public ResponseEntity<Map<String, Object>> forecastNextNode(@PathVariable("project_id") Long projectId,
                                                                @RequestParam("organizationId") Long tenantId,
                                                                @PathVariable("taskId") Long taskId) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        Assert.notNull(tenantId, BaseConstants.ErrorCode.NOT_NULL);
        return Results.success(appointNextNodeApproverActionHandler.forecastNextNode(taskId));
    }

    @ApiOperation("项目层-我的待办-查询可驳回列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/personal-process/{taskId}/rebutNodeList"})
    public ResponseEntity<List<ProcessJumpNodeDTO>> getRebutNodes(@PathVariable("project_id") Long projectId,
                                                                  @RequestParam("organizationId") Long tenantId,
                                                                  @PathVariable("taskId") Long taskId) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        return Results.success(personalActionService.getRebutNodes(tenantId, taskId));
    }

    @ApiOperation("项目层-自定义审批意见表列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/run-comment-templates")
    public ResponseEntity<List<RunCommentTemplate>> commentTemplateList(@PathVariable("project_id") Long projectId,
                                                                        @RequestParam("organizationId") Long tenantId,
                                                                        @ApiParam(name = "审批意见",value = "commentContent") @RequestParam(required = false) String commentContent) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        return Results.success(this.runCommentTemplateService.commentTemplateList(tenantId, commentContent));
    }

    @ApiOperation("附件上传/删除")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping({"/personal-process/attach-upload"})
    public ResponseEntity<Void> attachmentUpload(@PathVariable("project_id") Long projectId,
                                                 @RequestParam("organizationId") Long tenantId,
                                                 @RequestBody RunAttachment runAttachment) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        runAttachment.setTenantId(tenantId);
        validObject(runAttachment);
        personalProcessService.attachmentUpload(runAttachment);
        return Results.success();
    }

    @ApiOperation("项目层-保存自定义审批意见")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/run_comment_templates")
    public ResponseEntity<List<RunCommentTemplate>> saveCommentTemplate(@PathVariable("project_id") Long projectId,
                                                                        @RequestParam("organizationId") Long tenantId,
                                                                        @RequestBody List<RunCommentTemplate> runCommentTemplates) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        validList(runCommentTemplates);
        SecurityTokenHelper.validTokenIgnoreInsert(runCommentTemplates);
        List<RunCommentTemplate> result = this.runCommentTemplateService.saveCommentTemplate(tenantId, runCommentTemplates);
        return Results.success(result);
    }

    @ApiOperation("项目层-删除自定义审批意见")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/run_comment_templates")
    public ResponseEntity<Void> removeCommentTemplate(@PathVariable("project_id") Long projectId,
                                                      @RequestBody RunCommentTemplate runCommentTemplate) {
        Assert.notNull(projectId, BaseConstants.ErrorCode.NOT_NULL);
        SecurityTokenHelper.validToken(runCommentTemplate);
        runCommentTemplateRepository.deleteByPrimaryKey(runCommentTemplate);
        return Results.success();
    }
}
