package io.choerodon.workflow.infra.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.choerodon.workflow.infra.config.WorkFlowServiceC7nApplicationConfigurationProperties;

import org.hzero.core.redis.RedisHelper;

/**
 * 刷新缓存Job
 * @author gaokuo.dai@zknow.com 2023-01-05
 */
@Component
public class InitCacheJob implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(InitCacheJob.class);

    @Autowired
    private WorkFlowServiceC7nApplicationConfigurationProperties properties;
    @Lazy
    @Autowired
    private RedisHelper redisHelper;

    @Override
    @SuppressWarnings("deprecation")
    public void run(String... args) throws Exception {
        Boolean initCache = properties.getInitCache();
        if(!Boolean.TRUE.equals(initCache)) {
            logger.debug("skip init cache via configuration...");
            return;
        }
        this.redisHelper.deleteKeysWithPrefix("hwkf");
        logger.debug("init cache success");
    }

}
