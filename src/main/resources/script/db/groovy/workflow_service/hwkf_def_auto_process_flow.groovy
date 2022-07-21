package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_auto_process_flow.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2021-09-07-hwkf_def_auto_process_flow") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_auto_process_flow_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_auto_process_flow", remarks: "工作流自动处理-流程表") {
            column(name: "AUTOMATIC_FLOW_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "AUTOMATIC_ID", type: "bigint", remarks: "工作流自动处理表主键") { constraints(nullable: "false") }
            column(name: "FLOW_CODE", type: "varchar(" + 255 * weight + ")", remarks: "HWKF_DEF_WORKFLOW表code") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hwkf_def_auto_process_flow", indexName: "hwkf_def_auto_process_flow_n1") {
            column(name: "FLOW_CODE")
            column(name: "TENANT_ID")
        }

        addUniqueConstraint(columnNames: "AUTOMATIC_ID,FLOW_CODE", tableName: "hwkf_def_auto_process_flow", constraintName: "hwkf_def_auto_process_flow_u1")
    }
}
