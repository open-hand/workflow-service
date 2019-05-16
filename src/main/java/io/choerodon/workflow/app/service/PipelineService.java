package io.choerodon.workflow.app.service;

import io.choerodon.workflow.api.controller.dto.DevopsPipelineDTO;

/**
 * Created by Sheep on 2019/5/16.
 */
public interface PipelineService {


    /**
     * Devops部署pipeline
     * @param  devopsPipelineDTO  CD流水线信息
     * @return String
     */
    void beginDevopsPipelineSaga(DevopsPipelineDTO devopsPipelineDTO);
}
