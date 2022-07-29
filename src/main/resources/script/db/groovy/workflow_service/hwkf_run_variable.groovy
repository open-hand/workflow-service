package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_variable.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_variable") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_variable_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_variable", remarks: "流程变量表") {
            column(name: "VARIABLE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "INSTANCE_ID", type: "bigint", remarks: "流程实例ID，hwkf_run_instance主键") { constraints(nullable: "false") }
            column(name: "NODE_ID", type: "bigint", remarks: "执行节点ID，hwkf_run_node主键")
            column(name: "TASK_ID", type: "bigint", remarks: "流程任务ID，hwkf_run_task主键")
            column(name: "TYPE", type: "varchar(" + 10 * weight + ")", remarks: "变量类型") { constraints(nullable: "false") }
            column(name: "VARIABLE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "变量编码") { constraints(nullable: "false") }
            column(name: "VALUE", type: "varchar(" + 240 * weight + ")", remarks: "变量值")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hwkf_run_variable", indexName: "hwkf_run_variable_N3") {
            column(name: "TASK_ID")
        }
        createIndex(tableName: "hwkf_run_variable", indexName: "hwkf_run_variable_N2") {
            column(name: "NODE_ID")
        }
        createIndex(tableName: "hwkf_run_variable", indexName: "hwkf_run_variable_N1") {
            column(name: "INSTANCE_ID")
        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2022-02-18-hwkf_run_variable") {
        createIndex(tableName: "hwkf_run_variable", indexName: "hwkf_run_variable_N4") {
            column(name: "VARIABLE_CODE")
        }
    }
}
