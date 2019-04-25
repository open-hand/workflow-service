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
     * @param  businessKey  CD业务id
     * @return
     */
    Boolean approveUserTask(String businessKey);


    /**
     * 停止实例
     *
     * @param  businessKey  CD业务id
     * @return
     */
    void stopInstance(String businessKey);
}
