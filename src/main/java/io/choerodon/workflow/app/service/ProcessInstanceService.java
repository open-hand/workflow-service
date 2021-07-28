package io.choerodon.workflow.app.service;

import io.choerodon.workflow.api.vo.DevopsPipelineVO;
import io.choerodon.workflow.api.vo.HzeroDeployPipelineVO;

/**
 * Created by Sheep on 2019/4/2.
 */
public interface ProcessInstanceService {

    /**
     * Devops部署pipeline执行方法
     * @param  devopsPipelineVO  CD流水线信息
     * @return String
     */
    void beginDevopsPipeline(DevopsPipelineVO devopsPipelineVO);

    void beginDevopsPipelineCiCd(DevopsPipelineVO devopsPipelineVO);


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

    void createHzeroDeployPipeline(HzeroDeployPipelineVO hzeroDeployPipelineVO);

}
