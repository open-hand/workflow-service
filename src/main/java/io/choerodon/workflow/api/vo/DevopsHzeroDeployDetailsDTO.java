package io.choerodon.workflow.api.vo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/7/28 1:37
 */
public class DevopsHzeroDeployDetailsDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ApiModelProperty("hzero部署记录id")
    @Encrypt
    private Long deployRecordId;

    @ApiModelProperty("环境id")
    @Encrypt
    private Long envId;

    @ApiModelProperty("市场服务id")
    @Encrypt
    private Long mktServiceId;

    @ApiModelProperty("部署对象id")
    @Encrypt
    private Long mktDeployObjectId;

    @ApiModelProperty("部署配置id")
    @Encrypt
    private Long valueId;

    /**
     * {@link io.choerodon.devops.infra.enums.HzeroDeployDetailsStatusEnum}
     */
    @ApiModelProperty("部署状态")
    private String status;

    @ApiModelProperty("实例code")
    private String instanceCode;

    @ApiModelProperty("部署顺序")
    private Long sequence;

    public DevopsHzeroDeployDetailsDTO() {
    }

    public DevopsHzeroDeployDetailsDTO(Long deployRecordId, Long envId, Long mktServiceId, Long mktDeployObjectId, Long valueId, String status, String instanceCode, Long sequence) {
        this.deployRecordId = deployRecordId;
        this.envId = envId;
        this.mktServiceId = mktServiceId;
        this.mktDeployObjectId = mktDeployObjectId;
        this.valueId = valueId;
        this.status = status;
        this.instanceCode = instanceCode;
        this.sequence = sequence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeployRecordId() {
        return deployRecordId;
    }

    public void setDeployRecordId(Long deployRecordId) {
        this.deployRecordId = deployRecordId;
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Long getMktServiceId() {
        return mktServiceId;
    }

    public void setMktServiceId(Long mktServiceId) {
        this.mktServiceId = mktServiceId;
    }

    public Long getMktDeployObjectId() {
        return mktDeployObjectId;
    }

    public void setMktDeployObjectId(Long mktDeployObjectId) {
        this.mktDeployObjectId = mktDeployObjectId;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstanceCode() {
        return instanceCode;
    }

    public void setInstanceCode(String instanceCode) {
        this.instanceCode = instanceCode;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }
}
