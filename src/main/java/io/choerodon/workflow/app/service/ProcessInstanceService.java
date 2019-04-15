package io.choerodon.workflow.app.service;

import io.choerodon.workflow.api.controller.dto.DevopsPipelineDTO;

/**
 * Created by Sheep on 2019/4/2.
 */
public interface ProcessInstanceService {


    /**
     * Devops部署pipeline
     * @param  devopsPipelineDTO  CD流水线信息
     * @return String
     */
    String beginDevopsPipeline(DevopsPipelineDTO devopsPipelineDTO);


    /**
     * 审核DevopsCD任务
     *
     * @param  processInstanceId  流程实例id
     * @return
     */
    Boolean approveUserTask(String processInstanceId);
}
