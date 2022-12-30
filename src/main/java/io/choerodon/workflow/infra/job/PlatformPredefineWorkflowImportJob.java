package io.choerodon.workflow.infra.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.choerodon.workflow.app.service.OrganizationWorkflowC7nService;
import io.choerodon.workflow.infra.constant.HzeroWorkFlowConstants;
import io.choerodon.workflow.infra.util.ChangeLogHelper;

import org.hzero.core.base.BaseConstants;

/**
 * 平台预制工作流导入Job
 * @author gaokuo.dai@zknow.com 2022-08-18
 */
@Component
public class PlatformPredefineWorkflowImportJob implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(PlatformPredefineWorkflowImportJob.class);

    @Autowired
    private OrganizationWorkflowC7nService workflowC7nService;
    @Autowired
    private ChangeLogHelper changeLogHelper;

    @Override
    public void run(String... args) throws Exception {
        try{
            // 定义change log
            final String changeSetId = "2022-12-30-cwkf-update-predefine-workflow";
            final String author = "gaokuo.dai@zknow.com";
            final String fileName = "script/db/2022-12-30-cwkf-update-predefine-workflow";
            // 检查是否需要初始化
            if(!this.changeLogHelper.checkShouldRun(changeSetId, author, fileName)) {
                logger.debug("predefine workflow type {} has been imported", HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
                return;
            }
            // 执行初始化
            logger.info("predefine workflow type {} has not been imported, start import...", HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
            this.workflowC7nService.initDefWorkFlows(BaseConstants.DEFAULT_TENANT_ID);
            logger.info("predefine workflow type {} import success", HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
            // 写 change log
            this.changeLogHelper.writeChangeLog(
                    changeSetId,
                    author,
                    fileName,
                    "execute org.hzero.workflow.def.app.service.DataFixService.dimensionDataFix(\"USER\")"
            );
        }catch (Exception ex) {
            // log and ignore
            logger.error("init predefine workflow type {} failed, skip init, details↓", HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
            logger.error(ex.getMessage(), ex);
        }
    }

}
