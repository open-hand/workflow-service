package io.choerodon.workflow;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.hzero.autoconfigure.EnableHzeroWorkflow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients("io.choerodon")
@EnableEurekaClient
@SpringBootApplication
@EnableChoerodonResourceServer
@EnableAsync
@EnableCaching
@EnableHzeroWorkflow
@ComponentScan(value = {"org.hzero.workflow", "io.choerodon.workflow"})
public class WorkFlowServiceApplication {
    
    public static void main(String[] args){
        SpringApplication.run(WorkFlowServiceApplication.class, args);
    }
    
}

