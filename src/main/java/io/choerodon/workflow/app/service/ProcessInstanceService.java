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
    void beginDevopsPipeline(DevopsPipelineDTO devopsPipelineDTO);


    /**
     * 审核DevopsCD任务
     *
     * @param  pipelineRecordId  CD pipelineId
     * @return
     */
    Boolean approveUserTask(Long pipelineRecordId);


    /**
     * 停止实例
     *
     * @param  pipelineRecordId  CD pipelineId
     * @return
     */
    void stopInstance(Long pipelineRecordId);
}
