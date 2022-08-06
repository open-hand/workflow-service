package io.choerodon.workflow.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @date 2021-04-12 16:18
 */
@VersionAudit
@ModifyAudit
@Table(name = "cwkf_backlog_instance_rel")
public class BacklogInstanceRel extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_INSTANCE_ID = "instanceId";
    public static final String FIELD_BACKLOG_ID = "backlogId";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";

    @Id
    @GeneratedValue
    @Encrypt
    private Long id;

    @Unique
    private Long instanceId;

    @Unique
    private Long backlogId;

    @Unique
    private Long organizationId;

    public BacklogInstanceRel() {
    }

    public BacklogInstanceRel(Long instanceId, Long organizationId, Long backlogId) {
        this.instanceId = instanceId;
        this.organizationId = organizationId;
        this.backlogId = backlogId;
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

    public Long getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(Long backlogId) {
        this.backlogId = backlogId;
    }
}
