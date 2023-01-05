package io.choerodon.workflow.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * C7N二开工作流配置
 * @author gaokuo.dai@zknow.com 2023-01-05
 */
@ConfigurationProperties(prefix = WorkFlowServiceC7nApplicationConfigurationProperties.PREFIX)
public class WorkFlowServiceC7nApplicationConfigurationProperties {

    public static final String PREFIX = "hzero.workflow";

    /**
     * 启动时是否刷新缓存
     */
    private Boolean initCache = true;

    /**
     * @return 启动时是否刷新缓存
     */
    public Boolean getInitCache() {
        return initCache;
    }

    public WorkFlowServiceC7nApplicationConfigurationProperties setInitCache(Boolean initCache) {
        this.initCache = initCache;
        return this;
    }
}
