package io.choerodon.workflow.infra.config;

import org.activiti.api.process.runtime.events.ProcessCompletedEvent;
import org.activiti.api.process.runtime.events.listener.ProcessRuntimeEventListener;
import org.activiti.api.task.runtime.events.TaskAssignedEvent;
import org.activiti.api.task.runtime.events.TaskCompletedEvent;
import org.activiti.api.task.runtime.events.listener.TaskRuntimeEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Created by Sheep on 2019/4/2.
 */
@Configuration
public class ActivitiInitBeanConfig {

    @Value("${choerodon.executor.core-pool-size}")
    private int corePoolSize;
    @Value("${choerodon.executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${choerodon.executor.queue-capacity}")
    private int queueCapacity;
    @Value("${choerodon.executor.keep-alive-seconds}")
    private int keepAliveSeconds;

    private Logger logger = LoggerFactory.getLogger(ActivitiInitBeanConfig.class);

    @Bean
    public UserDetailsService myUserDetailsService() {
        return new InMemoryUserDetailsManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public ProcessRuntimeEventListener<ProcessCompletedEvent> processCompletedListener() {
        return processCompleted -> logger.info(processCompleted.getEntity().getName() +
                ":流程完成");
    }

    @Bean
    public TaskRuntimeEventListener<TaskAssignedEvent> taskAssignedListener() {
        return taskAssigned -> logger.info(taskAssigned.getEntity().getName() + ":分配给:" + taskAssigned.getEntity().getAssignee());
    }

    @Bean
    public TaskRuntimeEventListener<TaskCompletedEvent> taskCompletedListener() {
        return taskCompleted ->
                logger.info(taskCompleted.getEntity().getAssignee() + " 审批:" + taskCompleted.getEntity().getName() + ":完成");
    }

    @Bean(name = "activityExecutor")
    public TaskExecutor activityExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.corePoolSize);
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setQueueCapacity(this.queueCapacity);
        executor.setKeepAliveSeconds(this.keepAliveSeconds);
        executor.setThreadNamePrefix("c7n-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
