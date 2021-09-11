package io.choerodon.workflow.app.eventhandler;

import com.google.gson.Gson;
import io.choerodon.asgard.saga.SagaDefinition;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.workflow.api.vo.DevopsPipelineVO;
import io.choerodon.workflow.api.vo.HzeroDeployPipelineVO;
import io.choerodon.workflow.app.service.ProcessInstanceService;
import io.choerodon.workflow.infra.constant.SagaConstants;
import io.choerodon.workflow.infra.constant.SagaTaskConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE_AND_ID,
            seq = 1)
    public String workflowCreatePipelineCiCd(String data) {
        DevopsPipelineVO devopsPipelineDTO = gson.fromJson(data, DevopsPipelineVO.class);
        // hzero注入的Executor需要设置这个，否则会报错
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        processInstanceService.beginDevopsPipelineCiCd(devopsPipelineDTO);
        return data;
    }

    /**
     * hzero部署
     */
    @SagaTask(code = SagaTaskConstants.HZERO_DEPLOY_PIPELINE,
            description = "cicd工作流启动cd",
            sagaCode = SagaConstants.HZERO_DEPLOY_PIPELINE,
            maxRetryCount = 0,
            concurrentLimitPolicy = SagaDefinition.ConcurrentLimitPolicy.TYPE_AND_ID,
            seq = 1)
    public String createHzeroDeployPipeline(String data) {
        HzeroDeployPipelineVO hzeroDeployPipelineVO = gson.fromJson(data, HzeroDeployPipelineVO.class);
        processInstanceService.createHzeroDeployPipeline(hzeroDeployPipelineVO);
        return data;
    }


}
