package io.choerodon.workflow.infra.upgrade;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.choerodon.workflow.infra.util.ChangeLogHelper;

import org.hzero.core.redis.RedisHelper;
import org.hzero.workflow.def.app.service.DataFixService;
import org.hzero.workflow.engine.util.EngineConstants;

@Component
public class UpgradeDataFixCommandLineRunner implements CommandLineRunner {

    @Autowired
    private ChangeLogHelper changeLogHelper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private DataFixService dataFixService;

    private final Logger logger = LoggerFactory.getLogger(UpgradeDataFixCommandLineRunner.class);

    @Override
    public void run(String... args) throws Exception {
        try {
            this.fixHzeroWorkflow1_7();
        } catch (Exception ex) {
            this.logger.error(ex.getMessage(), ex);
        }
        try {
            this.fixHzeroWorkflow1_11();
        }  catch (Exception ex) {
            this.logger.error("failed to drop redis keys \"hwkf:def*\" and \"hwkf:approve-action*\", please retry or process them manually");
            this.logger.error("cause:");
            this.logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 班翎工作流1.6 - 1.7升级程序
     */
    private void fixHzeroWorkflow1_7() {
        final String changeSetId = "2022-08-07-cwkf-program-fix-update-hwkf-1.7-001";
        final String author = "gaokuo.dai@zknow.com";
        final String fileName = "script/db/z001_cwkf_data_fix.groovy";
        if(!this.changeLogHelper.checkShouldRun(changeSetId, author, fileName)) {
            return;
        }
        this.dataFixService.dimensionDataFix(EngineConstants.ApproveDimension.USER);
        this.changeLogHelper.writeChangeLog(
                changeSetId,
                author,
                fileName,
                "execute org.hzero.workflow.def.app.service.DataFixService.dimensionDataFix(\"USER\")"
        );
    }

    /**
     * 班翎工作流1.10 - 1.11升级程序
     */
    private void fixHzeroWorkflow1_11() {
        final String changeSetId = "2022-08-07-cwkf-program-fix-update-hwkf-1.11-001";
        final String author = "gaokuo.dai@zknow.com";
        final String fileName = "script/db/z001_cwkf_data_fix.groovy";
        if(!this.changeLogHelper.checkShouldRun(changeSetId, author, fileName)) {
            return;
        }

        // 注意, cluster模式下会报错, 如报错请手工处理
        this.redisHelper.deleteKeysWithPrefix("hwkf:def");
        this.redisHelper.deleteKeysWithPrefix("hwkf:approve-action");

        this.changeLogHelper.writeChangeLog(
                changeSetId,
                author,
                fileName,
                "drop redis keys \"hwkf:def*\" and \"hwkf:approve-action*\""
        );
    }

}
