package io.choerodon.workflow.app.eventhandler;

import com.google.gson.Gson;
import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.workflow.api.vo.DevopsPipelineVO;
import io.choerodon.workflow.app.service.ProcessInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Sheep on 2019/5/16.
 */

@Component
public class WorkFlowSagaHandler {

    private Gson gson = new Gson();


    @Autowired
    ProcessInstanceService processInstanceService;

    /**
     * devops创建环境
     */
    @SagaTask(code = "workflowCreateCD",
            description = "工作流启动cd",
            sagaCode = "workflow-create-pipeline",
            maxRetryCount = 0,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE_AND_ID,
            seq = 1)
    public String workflowCreatePipeline(String data) {
        DevopsPipelineVO devopsPipelineDTO = gson.fromJson(data, DevopsPipelineVO.class);
        processInstanceService.beginDevopsPipeline(devopsPipelineDTO);
        return data;
    }

    /**
     * devops创建环境
     */
    @SagaTask(code = "cicdWorkflowCreatePipeline",
            description = "cicd工作流启动cd",
            sagaCode = "cicd-workflow-pipeline",
            maxRetryCount = 0,
//            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE_AND_ID,
            seq = 1)
    public String workflowCreatePipelineCiCd(String data) {
        DevopsPipelineVO devopsPipelineDTO = gson.fromJson(data, DevopsPipelineVO.class);
        processInstanceService.beginDevopsPipelineCiCd(devopsPipelineDTO);
        return data;
    }



}
