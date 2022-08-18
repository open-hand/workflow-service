package io.choerodon.workflow.infra.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.choerodon.workflow.app.service.OrganizationWorkflowC7nService;
import io.choerodon.workflow.infra.constant.HzeroWorkFlowConstants;

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

    @Override
    public void run(String... args) throws Exception {
        try{
            final boolean initialized = this.workflowC7nService.validInitDefType(BaseConstants.DEFAULT_TENANT_ID, HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
            if(initialized) {
                logger.debug("predefine workflow type {} has been imported", HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
                return;
            }
            logger.info("predefine workflow type {} has not been imported, start import...", HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
            this.workflowC7nService.initDefWorkFlows(BaseConstants.DEFAULT_TENANT_ID);
            logger.info("predefine workflow type {} import success", HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
        }catch (Exception ex) {
            // log and ignore
            logger.error("init predefine workflow type {} failed, skip init, details↓", HzeroWorkFlowConstants.DEFAULT_TYPE_CODE);
            logger.error(ex.getMessage(), ex);
        }
    }

}
