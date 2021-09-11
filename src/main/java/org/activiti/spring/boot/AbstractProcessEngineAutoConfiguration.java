//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.activiti.spring.boot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.integration.IntegrationContextManager;
import org.activiti.engine.integration.IntegrationContextService;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringCallerRunsRejectedJobsHandler;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.SpringRejectedJobsHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

public abstract class AbstractProcessEngineAutoConfiguration extends AbstractProcessEngineConfiguration {
    public AbstractProcessEngineAutoConfiguration() {
    }

    @Bean
    public SpringAsyncExecutor springAsyncExecutor(TaskExecutor activityExecutor) {
        return new SpringAsyncExecutor(activityExecutor, this.springRejectedJobsHandler());
    }

    @Bean
    public SpringRejectedJobsHandler springRejectedJobsHandler() {
        return new SpringCallerRunsRejectedJobsHandler();
    }

    protected Set<Class<?>> getCustomMybatisMapperClasses(List<String> customMyBatisMappers) {
        Set<Class<?>> mybatisMappers = new HashSet();
        Iterator var3 = customMyBatisMappers.iterator();

        while(var3.hasNext()) {
            String customMybatisMapperClassName = (String)var3.next();

            try {
                Class customMybatisClass = Class.forName(customMybatisMapperClassName);
                mybatisMappers.add(customMybatisClass);
            } catch (ClassNotFoundException var6) {
                throw new IllegalArgumentException("Class " + customMybatisMapperClassName + " has not been found.", var6);
            }
        }

        return mybatisMappers;
    }

    @Bean
    public ProcessEngineFactoryBean processEngine(SpringProcessEngineConfiguration configuration) {
        return super.springProcessEngineBean(configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    public RuntimeService runtimeServiceBean(ProcessEngine processEngine) {
        return super.runtimeServiceBean(processEngine);
    }

    @Bean
    @ConditionalOnMissingBean
    public RepositoryService repositoryServiceBean(ProcessEngine processEngine) {
        return super.repositoryServiceBean(processEngine);
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskService taskServiceBean(ProcessEngine processEngine) {
        return super.taskServiceBean(processEngine);
    }

    @Bean
    @ConditionalOnMissingBean
    public HistoryService historyServiceBean(ProcessEngine processEngine) {
        return super.historyServiceBean(processEngine);
    }

    @Bean
    @ConditionalOnMissingBean
    public ManagementService managementServiceBeanBean(ProcessEngine processEngine) {
        return super.managementServiceBeanBean(processEngine);
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    public IntegrationContextManager integrationContextManagerBean(ProcessEngine processEngine) {
        return super.integrationContextManagerBean(processEngine);
    }

    @Bean
    @ConditionalOnMissingBean
    public IntegrationContextService integrationContextServiceBean(ProcessEngine processEngine) {
        return super.integrationContextServiceBean(processEngine);
    }
}
