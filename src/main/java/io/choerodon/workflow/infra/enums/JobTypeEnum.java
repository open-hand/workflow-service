package io.choerodon.workflow.infra.enums;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/3 16:57
 */
public enum JobTypeEnum {
    /**
     * cd 部署任务
     */
    CD_DEPLOY("cdDeploy"),

    /**
     * cd 审核任务
     */
    CD_AUDIT("cdAudit"),

    /**
     * cd 主机部署任务
     */
    CD_HOST("cdHost"),

    /**
     * cd 部署组部署
     */
    CD_DEPLOYMENT("cdDeployment"),

    /**
     * cd 外部卡点任务
     */
    CD_EXTERNAL_APPROVAL("cdExternalApproval"),

    /**
     * cd API测试任务
     */
    CD_API_TEST("cdApiTest"),
    /**
     * hzero部署
     */
    HZERO_DEPLOY("hzeroDeploy");


    private final String value;

    JobTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
