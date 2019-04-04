package io.choerodon.workflow.app.service;

import io.choerodon.workflow.api.controller.dto.DevopsPipelineDTO;

/**
 * Created by Sheep on 2019/4/2.
 */
public interface ProcessInstanceService {

    String beginDevopsPipeline(DevopsPipelineDTO devopsPipelineDTO);

    void approveUserTask(String processInstanceId);
}
