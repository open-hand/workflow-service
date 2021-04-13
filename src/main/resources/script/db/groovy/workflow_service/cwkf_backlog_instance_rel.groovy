package script.db.groovy.workflow_service

/**
 *
 * @author zhaotianxin* @date 2021-04-12 16:15
 **/
databaseChangeLog(logicalFilePath: 'cwkf_backlog_instance_rel.groovy') {
    changeSet(id: '2021-04-12-cwkf-backlog-instance-rel', author: 'ztxemail@163.com') {
        createTable(tableName: "cwkf_backlog_instance_rel", remarks: '需求和实例的关系表') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键Id') {
                constraints(primaryKey: true)
            }
            column(name: 'backlog_id', type: 'BIGINT UNSIGNED', remarks: '需求Id') {
                constraints(nullable: false)
            }
            column(name: 'instance_id', type: 'BIGINT UNSIGNED', remarks: '实例Id') {
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织Id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2021-04-13-cwkf-backlog-instance-rel-add-index', author: 'ztxemail@163.com'){
        createIndex(indexName: "idx_organization_id", tableName: "cwkf_backlog_instance_rel") {
            column(name: "organization_id")
        }
        createIndex(indexName: "idx_backlog_id", tableName: "cwkf_backlog_instance_rel") {
            column(name: "backlog_id")
        }
        createIndex(indexName: "idx_instance_id", tableName: "cwkf_backlog_instance_rel") {
            column(name: "instance_id")
        }
    }
}
