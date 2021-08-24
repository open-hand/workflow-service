package io.choerodon.workflow.app.service.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.workflow.api.vo.DevopsPipelineVO;
import io.choerodon.workflow.app.service.PipelineService;
import io.choerodon.workflow.app.service.ProcessInstanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                        .withRefId("1"));
    }

}
