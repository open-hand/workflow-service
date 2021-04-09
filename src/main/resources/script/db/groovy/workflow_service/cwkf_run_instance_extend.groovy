package script.db.groovy.workflow_service

/**
 *
 * @author zhaotianxin
 * @date 2021-04-09 16:45
 **/
databaseChangeLog(logicalFilePath: 'cwkf_run_instance_extends.groovy') {
    changeSet(id: '2021-04-09-cwkf-run-instance-extend', author: 'ztxemail@163.com') {
        createTable(tableName: "cwkf_run_instance_extend", remarks: '实例扩展表') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键Id') {
                constraints(primaryKey: true)
            }
            column(name: 'instance_id', type: 'BIGINT UNSIGNED', remarks: '流程实例Id') {
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
}