package io.choerodon.workflow.app.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.choerodon.core.exception.CommonException;
import io.choerodon.workflow.api.controller.dto.DevopsPipelineDTO;
import io.choerodon.workflow.app.service.ProcessInstanceService;
import io.choerodon.workflow.domain.handler.DevopsPipelineBpmnHandler;
import io.choerodon.workflow.domain.repository.DevopsServiceRepository;
import io.choerodon.workflow.infra.util.ActivitiUserLoginUtil;
import io.choerodon.workflow.infra.util.DynamicWorkflowUtil;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.model.payloads.GetTasksPayload;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Sheep on 2019/4/2.
 */
@Service
public class ProcessInstanceServiceImpl implements ProcessInstanceService {

    @Autowired
    DevopsServiceRepository devopsServiceRepository;

    @Autowired
    ActivitiUserLoginUtil activitiUserLoginUtil;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    ProcessRuntime processRuntime;
    @Autowired
    TaskRuntime taskRuntime;
    private Logger logger = LoggerFactory.getLogger(ProcessInstanceServiceImpl.class);

    @Override
    public String beginDevopsPipeline(DevopsPipelineDTO devopsPipelineDTO) {

        Map<String, Object> params = new HashMap<>();
        BpmnModel model = DevopsPipelineBpmnHandler.initDevopsCDPipelineBpmn(devopsPipelineDTO, params);

        if (!DynamicWorkflowUtil.checkValidate(model)) {
            throw new CommonException("invlid workflow module");
        }
        String filePath = "bmpn/" + UUID.randomUUID().toString() + ".bpmn";
        Deployment deployment = repositoryService.createDeployment().addBpmnModel(filePath, model).name("test").deploy();
        org.activiti.engine.repository.ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        String name = "部署CD流程";
        logger.info(String.format("部署CD流程:%s  开始", devopsPipelineDTO.getPipelineRecordId()));
        ProcessInstance processInstance = processRuntime.start(ProcessPayloadBuilder
                .start()
                .withProcessDefinitionKey(processDefinition.getKey())
                .withName(name)
                .withVariables(params)
                .build());
        return processInstance.getId();
    }

    @Override
    public Boolean approveUserTask(String processInstanceId) {
        GetTasksPayload getTasksPayload = new GetTasksPayload();
        getTasksPayload.setProcessInstanceId(processInstanceId);
        Page<Task> tasks = taskRuntime.tasks(Pageable.of(0, 10), getTasksPayload);
        taskRuntime.complete(TaskPayloadBuilder.complete().withTaskId(tasks.getContent().get(0).getId()).build());
        return true;
    }


}
