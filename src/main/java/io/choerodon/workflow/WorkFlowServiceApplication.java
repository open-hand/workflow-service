package io.choerodon.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

import org.hzero.autoconfigure.EnableHzeroWorkflow;

@EnableFeignClients("io.choerodon")
@EnableEurekaClient
@SpringBootApplication
@EnableChoerodonResourceServer
@EnableAsync
@EnableCaching
@EnableHzeroWorkflow
@ComponentScan(value = {"org.hzero.workflow", "io.choerodon.workflow"})
public class WorkFlowServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowServiceApplication.class);

    public static void main(String[] args){
        try{
            SpringApplication.run(WorkFlowServiceApplication.class, args);
        } catch (Throwable thr) {
            LOGGER.error(thr.getMessage(), thr);
        }
    }

}

