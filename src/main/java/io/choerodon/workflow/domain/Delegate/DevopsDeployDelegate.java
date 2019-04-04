package io.choerodon.workflow.domain.Delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Created by Sheep on 2019/4/3.
 */
@Component
public class DevopsDeployDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        System.out.println(delegateExecution.getId() + ":完成!");
    }
}
