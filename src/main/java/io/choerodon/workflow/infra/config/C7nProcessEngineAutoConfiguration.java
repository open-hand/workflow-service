package io.choerodon.workflow.infra.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import javax.sql.DataSource;

import org.activiti.api.runtime.shared.identity.UserGroupManager;
import org.activiti.engine.cfg.ProcessEngineConfigurator;
import org.activiti.engine.impl.persistence.StrongUuidGenerator;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.activiti.spring.boot.ActivitiProperties;
import org.activiti.spring.boot.ProcessDefinitionResourceFinder;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.activiti.spring.boot.process.validation.AsyncPropertyValidator;
import org.activiti.spring.bpmn.parser.CloudActivityBehaviorFactory;
import org.activiti.validation.ProcessValidatorImpl;
import org.activiti.validation.validator.ValidatorSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/11/26 12:48
 */
@Configuration
@AutoConfigureAfter(
        name = {"org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration", "org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration"}
)
@EnableConfigurationProperties({ActivitiProperties.class})
public class C7nProcessEngineAutoConfiguration extends AbstractProcessEngineAutoConfiguration {

    private final UserGroupManager userGroupManager;

    public C7nProcessEngineAutoConfiguration(UserGroupManager userGroupManager) {
        this.userGroupManager = userGroupManager;
    }

    private void configureProcessDefinitionResources(ProcessDefinitionResourceFinder processDefinitionResourceFinder, SpringProcessEngineConfiguration conf) throws IOException {
        List<Resource> procDefResources = processDefinitionResourceFinder.discoverProcessDefinitionResources();
        if (!procDefResources.isEmpty()) {
            conf.setDeploymentResources((Resource[])procDefResources.toArray(new Resource[0]));
        }

    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(DataSource dataSource, PlatformTransactionManager transactionManager, SpringAsyncExecutor springAsyncExecutor, ActivitiProperties activitiProperties, ProcessDefinitionResourceFinder processDefinitionResourceFinder, @Autowired(required = false) ProcessEngineConfigurationConfigurer processEngineConfigurationConfigurer, @Autowired(required = false) List<ProcessEngineConfigurator> processEngineConfigurators) throws IOException {
        SpringProcessEngineConfiguration conf = new SpringProcessEngineConfiguration();
        conf.setConfigurators(processEngineConfigurators);
        this.configureProcessDefinitionResources(processDefinitionResourceFinder, conf);
        conf.setDataSource(dataSource);
        conf.setTransactionManager(transactionManager);
        if (springAsyncExecutor != null) {
            conf.setAsyncExecutor(springAsyncExecutor);
        }
        // 设置缓存数为2， 避免OOM
        conf.setProcessDefinitionCacheLimit(2);

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
}
