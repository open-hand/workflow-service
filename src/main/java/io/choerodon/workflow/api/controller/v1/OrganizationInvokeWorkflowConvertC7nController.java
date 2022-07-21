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
import io.choerodon.workflow.app.service.OrganizationWorkflowC7nService;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.workflow.engine.model.node.FlowApproverValue;
import org.hzero.workflow.engine.run.action.AppointNextNodeApproverActionHandler;
import org.hzero.workflow.engine.run.dto.ProcessJumpNodeDTO;
import org.hzero.workflow.engine.util.EngineConstants;
import org.hzero.workflow.monitor.api.dto.ProcessInstanceDTO;
import org.hzero.workflow.monitor.app.service.MonitorProcessService;
import org.hzero.workflow.personal.api.dto.DetailDTO;
import org.hzero.workflow.personal.app.service.PersonalActionService;
import org.hzero.workflow.personal.app.service.PersonalProcessService;
import org.hzero.workflow.personal.app.service.RunCommentTemplateService;
import org.hzero.workflow.personal.domain.entity.RunAttachment;
import org.hzero.workflow.personal.domain.entity.RunCommentTemplate;
import org.hzero.workflow.personal.domain.repository.RunCommentTemplateRepository;

/**
 * @author zhaotianxin 2021-03-16 10:01
 */
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/organization_invoke_workflow")
public class OrganizationInvokeWorkflowConvertC7nController extends BaseController {

    @Autowired
    private RunCommentTemplateRepository runCommentTemplateRepository;
    @Autowired
    private RunCommentTemplateService runCommentTemplateService;
    @Autowired
    private MonitorProcessService monitorProcessService;
    @Autowired
    private PersonalProcessService personalProcessService;
    @Autowired
    private PersonalActionService personalActionService;
    @Autowired
    private OrganizationWorkflowC7nService organizationWorkflowC7NService;
    @Autowired
    private AppointNextNodeApproverActionHandler appointNextNodeApproverActionHandler;

    @ApiOperation("自定义审批意见表列表")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/run_comment_templates")
    public ResponseEntity<List<RunCommentTemplate>> commentTemplateList(@PathVariable("organization_id") Long organizationId,
                                                                        @ApiParam(name = "审批意见",value = "commentContent")
                                                                        @RequestParam(required = false) String commentContent) {
        return Results.success(this.runCommentTemplateService.commentTemplateList(organizationId, commentContent));
    }

    @ApiOperation("保存自定义审批意见")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping("/run_comment_templates")
    public ResponseEntity<List<RunCommentTemplate>> saveCommentTemplate(@PathVariable("organization_id") Long organizationId,
                                                                        @RequestBody List<RunCommentTemplate> runCommentTemplates) {
        this.validList(runCommentTemplates);
        SecurityTokenHelper.validTokenIgnoreInsert(runCommentTemplates);
        List<RunCommentTemplate> result = this.runCommentTemplateService.saveCommentTemplate(organizationId, runCommentTemplates);
        return Results.success(result);
    }

    @ApiOperation("启用/禁用自定义审批意见")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PutMapping("/run_comment_templates/{commentTemplateId}")
    public ResponseEntity<RunCommentTemplate> enableCommentTemplate(@PathVariable("organization_id") Long organizationId,
                                                                    @RequestBody RunCommentTemplate runCommentTemplate,
                                                                    @ApiParam(name = "启用1/禁用0",required = true)
                                                                    @RequestParam Integer enabledFlag) {
        Assert.notNull(organizationId, BaseConstants.ErrorCode.NOT_NULL);
        validObject(runCommentTemplate);
        RunCommentTemplate result = this.runCommentTemplateService.enableCommentTemplate(organizationId, runCommentTemplate, enabledFlag);
        return Results.success(result);
    }

    @ApiOperation("删除自定义审批意见")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @DeleteMapping("/run_comment_templates")
    public ResponseEntity<Void> removeCommentTemplate(@PathVariable("organization_id") Long organizationId,
                                                      @RequestBody RunCommentTemplate runCommentTemplate) {
        Assert.notNull(organizationId, BaseConstants.ErrorCode.NOT_NULL);
        SecurityTokenHelper.validToken(runCommentTemplate);
        runCommentTemplateRepository.deleteByPrimaryKey(runCommentTemplate);
        return Results.success();
    }

    @ApiOperation("查询流程实例缩略图")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ProcessLovValue(targetField = {"body", "body.processInstanceNodeHistory.taskHistoryList"})
    @GetMapping({"/monitor_process/{instanceId}/diagram"})
    public ResponseEntity<ProcessInstanceDTO.ProcessInstanceDiagramDTO> getProcessInstanceDiagram(@PathVariable("organization_id") Long organizationId,
                                                                                                  @PathVariable("instanceId") @Encrypt Long instanceId) {
        ProcessInstanceDTO.ProcessInstanceDiagramDTO diagramDTO = monitorProcessService.getProcessInstanceDiagram(organizationId, instanceId, null);
        return Results.success(diagramDTO);
    }

    @ApiOperation("我的待办-审批通过")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping({"/personal_process/approve"})
    public ResponseEntity<Void> flowApprove(@PathVariable("organization_id") Long organizationId,
                                            @Encrypt @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                            @RequestBody(required = false) Map<String, Object> paramMap) {
        personalActionService.batchProcess(organizationId, taskIds, EngineConstants.ApproveAction.APPROVED,false,null, paramMap);
        return Results.success();
    }

    @ApiOperation("我的待办-审批拒绝")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping({"/personal_process/reject"})
    public ResponseEntity<Void> flowReject(@PathVariable("organization_id") Long organizationId,
                                           @Encrypt @RequestParam(value = "taskIds", required = false) List<Long> taskIds,
                                           @RequestParam(required = false) Integer reApproveFlag,
                                           @RequestBody(required = false) Map<String, Object> paramMap) {
        personalActionService.batchProcess(organizationId, taskIds, EngineConstants.ApproveAction.REJECTED, false, reApproveFlag, paramMap);
        return Results.success();
    }

    @ApiOperation("附件上传/删除")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping("/personal_process/attach_upload")
    public ResponseEntity<Void> attachmentUpload(@PathVariable("organization_id") Long organizationId,
                                                 @RequestBody RunAttachment runAttachment) {
        runAttachment.setTenantId(organizationId);
        validObject(runAttachment);
        personalProcessService.attachmentUpload(runAttachment);
        return Results.success();
    }

    @ApiOperation("预测下一审批人")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping({"/personal_process/{taskId}/forecastNextNode"})
    public ResponseEntity<Map<String, Object>> forecastNextNode(@PathVariable("organization_id") Long organizationId,
                                                                @PathVariable("taskId") @Encrypt Long taskId) {
        Assert.notNull(organizationId, BaseConstants.ErrorCode.NOT_NULL);
        return Results.success(appointNextNodeApproverActionHandler.forecastNextNode(taskId));
    }

    @ApiOperation("我的待办-查询可驳回列表")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping({"/personal_process/{taskId}/rebutNodeList"})
    public ResponseEntity<List<ProcessJumpNodeDTO>> getRebutNodes(@PathVariable("organization_id") Long organizationId,
                                                            @PathVariable("taskId") @Encrypt Long taskId) {
        return Results.success(personalActionService.getRebutNodes(organizationId, taskId));
    }
    @ApiOperation("我的待办-根据taskId处理审批动作")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping({"/personal_process/{taskId}/executeTaskById"})
    public ResponseEntity<Void> executeTaskById(@PathVariable("organization_id") Long organizationId,
                                                @PathVariable("taskId") @Encrypt Long taskId,
                                                @RequestParam String approveAction,
                                                @RequestBody Map<String, Object> paramMap) {
        personalActionService.executeTaskById(organizationId, taskId, approveAction, paramMap);
        return Results.success();
    }

    @ApiOperation("我的待办-流程手动抄送")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping({"/personal_process/carbon-copy"})
    public ResponseEntity<Void> flowCarbonCopy(@PathVariable("organization_id") Long organizationId,
                                               @Encrypt @RequestParam("taskId") Long taskId,
                                               @RequestBody List<FlowApproverValue> toPersonList) {
        personalActionService.carbonCopy(organizationId, taskId, toPersonList);
        return Results.success();
    }

    @ApiOperation("我的待办详情")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping({"/personal_process/task/{taskId}"})
    @ProcessLovValue(targetField = {"body.taskDetail", "body.historyList"})
    public ResponseEntity<DetailDTO.TaskDetailDTO> taskDetail(@PathVariable("organization_id") Long organizationId,
                                                              @PathVariable("taskId") @Encrypt Long taskId) {
        return Results.success(this.personalProcessService.taskDetail(organizationId, taskId, false));
    }

    @ApiOperation("我参与的流程详情")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping({"/personal_process/participated/{instanceId}"})
    @ProcessLovValue(targetField = {"body.instanceDetail", "body.historyList"})
    public ResponseEntity<DetailDTO.ParticipatedDetailDTO> participatedDetail(@PathVariable("organization_id") Long organizationId,
                                                                              @PathVariable("instanceId") @Encrypt Long instanceId) {
        return Results.success(this.personalProcessService.participatedDetail(organizationId, instanceId, false));
    }

    @ApiOperation("我发起的流程详情")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping({"/personal_process/submitted/{instanceId}"})
    @ProcessLovValue(targetField = {"body.submittedDetail", "body.historyList"})
    public ResponseEntity<DetailDTO.SubmittedDetailDTO> submittedDetail(@PathVariable("organization_id") Long organizationId,
                                                                        @PathVariable("instanceId") @Encrypt Long instanceId) {
        return Results.success(this.personalProcessService.submittedDetail(organizationId, instanceId));
    }

    @ApiOperation("抄送给我的流程详情")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping({"/personal_process/carbon_copied/{taskHistoryId}"})
    @ProcessLovValue(targetField = {"body.carbonCopyDetail", "body.historyList"})
    public ResponseEntity<DetailDTO.CarbonCopiedDetailDTO> carbonCopiedDetail(@PathVariable("organization_id") Long organizationId,
                                                                              @PathVariable("taskHistoryId") @Encrypt Long taskHistoryId,
                                                                              @RequestParam(required = false) Integer carbonCopyTodoFlag) {
        return Results.success(this.personalProcessService.carbonCopiedDetail(organizationId, taskHistoryId, carbonCopyTodoFlag, false));
    }

    @ApiOperation("我的抄送流程-评论")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping({"/personal_process/carbon_comment"})
    public ResponseEntity<Void> carbonCopyComment(@PathVariable("organization_id") Long organizationId,
                                                  @RequestParam("taskHistoryId") @Encrypt Long taskHistoryId,
                                                  @RequestParam("comment") String comment,
                                                  @RequestParam(name = "assignee",required = false) String assignee) {
        this.personalActionService.carbonCopyComment(organizationId, taskHistoryId, comment, assignee);
        return Results.success();
    }

    @ApiOperation("我发起的流程-催办")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping({"/personal_process/urge"})
    public ResponseEntity<Void> flowUrge(@PathVariable("organization_id") Long organizationId,
                                         @RequestParam("instanceIds") @Encrypt List<Long> instanceIds) {
        this.personalActionService.urge(organizationId, instanceIds);
        return Results.success();
    }

    @ApiOperation("组织层流程分类、定义初始化")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping({"/def_workflow/init"})
    public ResponseEntity<Void> initWorkflow(
            @PathVariable("organization_id") Long tenantId){
        organizationWorkflowC7NService.initDefWorkFlows(tenantId);
        return Results.success();
    }

    @ApiOperation("组织层流程分类、定义初始化")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/def_workflow/check_init"})
    public ResponseEntity<Boolean> checkInitWorkflow(
            @PathVariable("organization_id") Long tenantId){
        return Results.success(organizationWorkflowC7NService.checkInit(tenantId));
    }

    @ApiOperation("增量导入新定义的审批人规则流程变量等")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping({"/def_workflow/reimport"})
    public ResponseEntity<Boolean> reimportWorkflow(@PathVariable("organization_id") Long tenantId) {
        organizationWorkflowC7NService.reimportWorkflow(tenantId);
        return Results.success();
    }

}
