package io.choerodon.workflow.infra.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 15:00:45
 */
@Configuration
@EnableFeignClients("io.choerodon")
public class EnableFeignClientsConfiguration {
}
