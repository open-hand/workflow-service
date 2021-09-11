//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.hzero.autoconfigure;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import javax.annotation.Nonnull;
import org.hzero.workflow.config.CustomLocaleResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.LocaleResolver;

@ComponentScan({"org.hzero.workflow"})
@EnableChoerodonResourceServer
@EnableAspectJAutoProxy(
    proxyTargetClass = true,
    exposeProxy = true
)
@EnableFeignClients({"org.hzero"})
@EnableCaching
@EnableAsync
public class WorkFlowAutoConfiguration {
    @Value("${hzero.workflow.executor.core-pool-size}")
    private int corePoolSize;
    @Value("${hzero.workflow.executor.max-pool-size}")
    private int maxPoolSize;
    @Value("${hzero.workflow.executor.queue-capacity}")
    private int queueCapacity;
    @Value("${hzero.workflow.executor.keep-alive-seconds}")
    private int keepAliveSeconds;

    public WorkFlowAutoConfiguration() {
    }

    @Bean
    @LoadBalanced
    public RestTemplate initRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(this.corePoolSize);
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setQueueCapacity(this.queueCapacity);
        executor.setKeepAliveSeconds(this.keepAliveSeconds);
        executor.setThreadNamePrefix("hwkf-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setTaskDecorator(new WorkFlowAutoConfiguration.ContextCopyingDecorator());
        return executor;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new CustomLocaleResolver();
    }

    @Bean
    public ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource rbms = new ResourceBundleMessageSource();
        rbms.setDefaultEncoding("UTF-8");
        rbms.setBasenames(new String[]{"ValidationMessages"});
        return rbms;
    }

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(this.getMessageSource());
        return validator;
    }

    static class ContextCopyingDecorator implements TaskDecorator {
        ContextCopyingDecorator() {
        }

        @Nonnull
        public Runnable decorate(@Nonnull Runnable runnable) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes == null) {
                return runnable;
            } else {
                RequestAttributes context = RequestContextHolder.currentRequestAttributes();
                SecurityContext securityContext = SecurityContextHolder.getContext();
                return () -> {
                    try {
                        RequestContextHolder.setRequestAttributes(context);
                        SecurityContextHolder.setContext(securityContext);
                        runnable.run();
                    } finally {
                        SecurityContextHolder.clearContext();
                        RequestContextHolder.resetRequestAttributes();
                    }

                };
            }


        }
    }
}
