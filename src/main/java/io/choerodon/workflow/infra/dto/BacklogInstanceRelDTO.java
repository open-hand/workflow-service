package io.choerodon.workflow.infra.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author zhaotianxin
 * @date 2021-04-12 16:18
 */
@VersionAudit
@ModifyAudit
@Table(name = "cwkf_backlog_instance_rel")
public class BacklogInstanceRelDTO extends AuditDomain {
    @Id
    @GeneratedValue
    @Encrypt
    private Long id;

    private Long instanceId;

    private Long organizationId;

    public BacklogInstanceRelDTO() {
    }

    public BacklogInstanceRelDTO(Long instanceId, Long organizationId) {
        this.instanceId = instanceId;
        this.organizationId = organizationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
