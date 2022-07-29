package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_comment_template.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_comment_template") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_comment_template_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_comment_template", remarks: "自定义审批意见表") {
            column(name: "COMMENT_TEMPLATE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "EMPLOYEE_NUM", type: "varchar(" + 30 * weight + ")", remarks: "员工编码") { constraints(nullable: "false") }
            column(name: "COMMENT_CONTENT", type: "varchar(" + 240 * weight + ")", remarks: "审批意见内容") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", type: "tinyint", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


    }

    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-05-08-hwkf_run_comment_template") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hwkf_run_comment_template') {
            column(name: "USER_ID", type: "bigint",  remarks: "用户ID")
        }
        dropNotNullConstraint(tableName: "hwkf_run_comment_template", columnName: "EMPLOYEE_NUM", columnDataType: "varchar(" + 30 * weight + ")")
    }

    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-06-18-hwkf_run_comment_template") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hwkf_run_comment_template",  columnName: "COMMENT_CONTENT", newDataType:"varchar(" + 1000 * weight + ")")
    }
}
