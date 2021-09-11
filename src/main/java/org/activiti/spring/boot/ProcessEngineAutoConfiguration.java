//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.activiti.spring.boot;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import org.activiti.api.process.model.events.ProcessDeployedEvent;
import org.activiti.api.process.runtime.events.listener.ProcessRuntimeEventListener;
import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.cfg.ProcessEngineConfigurator;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.runtime.api.model.impl.APIProcessDefinitionConverter;
import org.activiti.spring.ProcessDeployedEventProducer;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.process.validation.AsyncPropertyValidator;
import org.activiti.spring.bpmn.parser.CloudActivityBehaviorFactory;
import org.activiti.validation.ProcessValidatorImpl;
import org.activiti.validation.validator.ValidatorSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AutoConfigureAfter(
    name = {"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration", "org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration"}
)
@EnableConfigurationProperties({ActivitiProperties.class})
public class ProcessEngineAutoConfiguration extends AbstractProcessEngineAutoConfiguration {

    @Value("${choerodon.executor.core-pool-size}")
    private int corePoolSize;
    @Value("${choerodon.executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${choerodon.executor.queue-capacity}")
    private int queueCapacity;
    @Value("${choerodon.executor.keep-alive-seconds}")
    private int keepAliveSeconds;

    private final UserGroupManager userGroupManager;

    public ProcessEngineAutoConfiguration(UserGroupManager userGroupManager) {
        this.userGroupManager = userGroupManager;
    }

    @Bean
    public SpringAsyncExecutor springAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.corePoolSize);
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setQueueCapacity(this.queueCapacity);
        executor.setKeepAliveSeconds(this.keepAliveSeconds);
        executor.setThreadNamePrefix("c7n-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return new SpringAsyncExecutor(executor, this.springRejectedJobsHandler());
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager transactionManager, SpringAsyncExecutor springAsyncExecutor, ActivitiProperties activitiProperties, ProcessDefinitionResourceFinder processDefinitionResourceFinder, @Autowired(required = false) ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer, @Autowired(required = false) List<ProcessEngineConfigurator> processEngineConfigurators) throws IOException {
        SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration();
        conf.setConfigurators(processEngineConfigurators);
        this.configureProcessDefinitionResources(processDefinitionResourceFinder, conf);
        conf.setDataSource(dataSource);
        conf.setTransactionManager(transactionManager);
        if (springAsyncExecutor != null) {
            conf.setAsyncExecutor(springAsyncExecutor);
        }

        conf.setDeploymentName(activitiProperties.getDeploymentName());
        conf.setDatabaseSchema(activitiProperties.getDatabaseSchema());
        conf.setDatabaseSchemaUpdate(activitiProperties.getDatabaseSchemaUpdate());
        conf.setDbHistoryUsed(activitiProperties.isDbHistoryUsed());
        conf.setAsyncExecutorActivate(activitiProperties.isAsyncExecutorActivate());
        if (!activitiProperties.isAsyncExecutorActivate()) {
            ValidatorSet springBootStarterValidatorSet = new ValidatorSet("activiti-spring-boot-starter");
            springBootStarterValidatorSet.addValidator(new AsyncPropertyValidator());
            if (conf.getProcessValidator() == null) {
                ProcessValidatorImpl processValidator = new ProcessValidatorImpl();
                processValidator.addValidatorSet(springBootStarterValidatorSet);
                conf.setProcessValidator(processValidator);
            } else {
                conf.getProcessValidator().getValidatorSets().add(springBootStarterValidatorSet);
            }
        }

        conf.setMailServerHost(activitiProperties.getMailServerHost());
        conf.setMailServerPort(activitiProperties.getMailServerPort());
        conf.setMailServerUsername(activitiProperties.getMailServerUserName());
        conf.setMailServerPassword(activitiProperties.getMailServerPassword());
        conf.setMailServerDefaultFrom(activitiProperties.getMailServerDefaultFrom());
        conf.setMailServerUseSSL(activitiProperties.isMailServerUseSsl());
        conf.setMailServerUseTLS(activitiProperties.isMailServerUseTls());
        if (this.userGroupManager != null) {
            conf.setUserGroupManager(this.userGroupManager);
        }

        conf.setHistoryLevel(activitiProperties.getHistoryLevel());
        conf.setCopyVariablesToLocalForTasks(activitiProperties.isCopyVariablesToLocalForTasks());
        conf.setSerializePOJOsInVariablesToJson(activitiProperties.isSerializePOJOsInVariablesToJson());
        conf.setJavaClassFieldForJackson(activitiProperties.getJavaClassFieldForJackson());
        if (activitiProperties.getCustomMybatisMappers() != null) {
            conf.setCustomMybatisMappers(this.getCustomMybatisMapperClasses(activitiProperties.getCustomMybatisMappers()));
        }

        if (activitiProperties.getCustomMybatisXMLMappers() != null) {
            conf.setCustomMybatisXMLMappers(new HashSet(activitiProperties.getCustomMybatisXMLMappers()));
        }

        if (activitiProperties.getCustomMybatisXMLMappers() != null) {
            conf.setCustomMybatisXMLMappers(new HashSet(activitiProperties.getCustomMybatisXMLMappers()));
        }

        if (activitiProperties.isUseStrongUuids()) {
            conf.setIdGenerator(new StrongUuidGenerator());
        }

        if (activitiProperties.getDeploymentMode() != null) {
            conf.setDeploymentMode(activitiProperties.getDeploymentMode());
        }

        conf.setActivityBehaviorFactory(new CloudActivityBehaviorFactory());
        if (processEngineConfigurationConfigurer != null) {
            processEngineConfigurationConfigurer.configure(conf);
        }

        return conf;
    }

    private void configureProcessDefinitionResources(ProcessDefinitionResourceFinder processDefinitionResourceFinder, SpringProcessEngineConfiguration conf) throws IOException {
        List<Resource> procDefResources = processDefinitionResourceFinder.discoverProcessDefinitionResources();
        if (!procDefResources.isEmpty()) {
            conf.setDeploymentResources((Resource[])procDefResources.toArray(new Resource[0]));
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessDefinitionResourceFinder processDefinitionResourceFinder(ActivitiProperties activitiProperties, ResourcePatternResolver resourcePatternResolver) {
        return new ProcessDefinitionResourceFinder(activitiProperties, resourcePatternResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessDeployedEventProducer processDeployedEventProducer(RepositoryService repositoryService, APIProcessDefinitionConverter converter, @Autowired(required = false) List<ProcessRuntimeEventListener<ProcessDeployedEvent>> listeners, ApplicationEventPublisher eventPublisher) {
        return new ProcessDeployedEventProducer(repositoryService, converter, (List)Optional.ofNullable(listeners).orElse(Collections.emptyList()), eventPublisher);
    }
}
