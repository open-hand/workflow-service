package io.choerodon.workflow.app.service;

import io.choerodon.workflow.api.vo.DevopsPipelineVO;

/**
 * Created by Sheep on 2019/5/16.
 */
public interface PipelineService {


    /**
     * Devops部署pipeline
     * @param  devopsPipelineVO  CD流水线信息
     * @return String
     */
    void beginDevopsPipelineSaga(DevopsPipelineVO devopsPipelineVO);
}
