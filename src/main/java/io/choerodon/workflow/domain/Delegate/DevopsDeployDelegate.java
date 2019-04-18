package io.choerodon.workflow.domain.Delegate;

import java.util.concurrent.CountDownLatch;

import io.choerodon.workflow.domain.repository.DevopsServiceRepository;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Sheep on 2019/4/3.
 */
@Component
public class DevopsDeployDelegate implements JavaDelegate {

    @Autowired
    DevopsServiceRepository devopsServiceRepository;
    private Logger logger = LoggerFactory.getLogger(DevopsDeployDelegate.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {

        logger.info(String.format("ServiceTask:%s 开始", delegateExecution.getCurrentActivityId()));

        String[] ids = delegateExecution.getCurrentActivityId().split("\\.");
        Long pipelineId = Long.parseLong(ids[1]);
        Long stageId = Long.parseLong(ids[2]);
        Long taskId = Long.parseLong(ids[3]);

        devopsServiceRepository.autoDeploy(stageId, taskId);

        boolean[] exit = {false};

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Runnable runnable = () -> {
            while (!exit[0]) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Boolean deployResult = devopsServiceRepository.getAutoDeployTaskStatus(stageId, taskId);
                if (deployResult) {
                    exit[0] = true;
                    countDownLatch.countDown();
                }else {
                    devopsServiceRepository.setAutoDeployTaskStatus(pipelineId, stageId, taskId,false);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        devopsServiceRepository.setAutoDeployTaskStatus(pipelineId, stageId, taskId,true);
        logger.info(String.format("ServiceTask:%s  结束", delegateExecution.getCurrentActivityId()));
    }
}
