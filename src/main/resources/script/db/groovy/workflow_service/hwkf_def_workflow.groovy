package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_workflow.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_workflow") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_workflow_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_workflow", remarks: "工作流流程定义表") {
            column(name: "FLOW_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程分类ID") { constraints(nullable: "false") }
            column(name: "FLOW_CODE", type: "varchar(" + 30 * weight + ")", remarks: "流程编码") { constraints(nullable: "false") }
            column(name: "FLOW_NAME", type: "varchar(" + 240 * weight + ")", remarks: "流程名称") { constraints(nullable: "false") }
            column(name: "VERSION", type: "int", defaultValue: "0", remarks: "流程版本，0表示未发布") { constraints(nullable: "false") }
            column(name: "DEPLOYMENT_ID", type: "bigint", remarks: "部署ID")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "FLOW_CODE,TENANT_ID", tableName: "hwkf_def_workflow", constraintName: "hwkf_def_workflow_u1")
    }

    changeSet(author: 'xiuhong.chen@hand-china.com', id: '2021-08-17-hwkf_def_workflow') {
        createIndex(tableName: "hwkf_def_workflow", indexName: "hwkf_def_workflow_n1") {
            column(name: "TYPE_ID")
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2021-11-03-hwkf_def_workflow') {
        addColumn(tableName: 'hwkf_def_workflow') {
            column(name: "MSG_CONFIG_JSON", type: "longtext", remarks: "消息通知配置")
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2022-02-28-hwkf_def_workflow') {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hwkf_def_workflow') {
            column(name: "FORM_CODE", type: "varchar(" + 240 * weight + ")", remarks: "表单编码")
        }
    }
}
