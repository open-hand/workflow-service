package io.choerodon.workflow.app.service.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.workflow.api.vo.DevopsPipelineVO;
import io.choerodon.workflow.api.vo.HzeroDeployPipelineVO;
import io.choerodon.workflow.app.service.PipelineService;
import io.choerodon.workflow.app.service.ProcessInstanceService;
import io.choerodon.workflow.infra.constant.SagaConstants;

/**
 * Created by Sheep on 2019/5/16.
 */

@Service
public class PipelineServiceImpl implements PipelineService {

    private Gson gson = new Gson();

    @Autowired
    TransactionalProducer producer;

    @Autowired
    ProcessInstanceService processInstanceService;

    @Override
    @Saga(code = "workflow-create-pipeline",
            description = "启动cd pipeline", inputSchema = "{}")
    public void beginDevopsPipelineSaga(DevopsPipelineVO devopsPipelineVO) {
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.SITE)
                        .withRefType("workflow")
                        .withSagaCode("workflow-create-pipeline"),
                builder -> builder
                        .withPayloadAndSerialize(devopsPipelineVO)
                        .withRefId("1"));
    }


    @Override
    @Saga(code = "cicd-workflow-pipeline",
            description = "创建cicd流水线创建流程实例", inputSchema = "{}")
    public void beginDevopsPipelineSagaCiCd(DevopsPipelineVO devopsPipelineVO) {
        String refId = devopsPipelineVO.getPipelineId() == null ? "1" : devopsPipelineVO.getPipelineId().toString();
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.SITE)
                        .withRefType("workflow")
                        .withSagaCode("cicd-workflow-pipeline"),
                builder -> builder
                        .withPayloadAndSerialize(devopsPipelineVO)
                        .withRefId(refId));
    }

    @Override
    @Saga(code = SagaConstants.HZERO_DEPLOY_PIPELINE,
            description = "创建hzero部署流水线", inputSchema = "{}")
    public void createHzeroPipeline(Long projectId, HzeroDeployPipelineVO hzeroDeployPipelineVO) {
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withRefType("workflow")
                        .withSagaCode(SagaConstants.HZERO_DEPLOY_PIPELINE),
                builder -> builder
                        .withPayloadAndSerialize(hzeroDeployPipelineVO)
                        .withRefId(String.valueOf(projectId)));
    }

}
