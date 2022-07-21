package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_mail_template.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_mail_template") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_mail_template_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_mail_template", remarks: "工作流邮件审批模板") {
            column(name: "TEMPLATE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TEMPLATE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "邮件审批模板编码") { constraints(nullable: "false") }
            column(name: "TEMPLATE_NAME", type: "varchar(" + 240 * weight + ")", remarks: "邮件审批模板名称") { constraints(nullable: "false") }
            column(name: "TEMPLATE_CONTENT", type: "longtext", remarks: "邮件审批模板内容") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
            column(name: "TYPE_ID", type: "bigint", remarks: "流程分类ID") { constraints(nullable: "false") }
            column(name: "INTERFACE_ID", type: "bigint", remarks: "数据源接口定义ID")
            column(name: "TEMPLATE_REMARK", type: "varchar(" + 240 * weight + ")", remarks: "模板备注")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


    }

    changeSet(author: "weisen.yang@hand-china.com", id: "2021-06-24-hwkf_def_mail_template") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hwkf_def_mail_template') {
            column(name: "RECORD_SOURCE_TYPE", type: "varchar(" + 30 * weight + ")", defaultValue: "CUSTOMIZE", remarks: "记录来源：PREDEFINED(预定义)、CUSTOMIZE(自定义)")
        }
    }
    changeSet(author: "like.zhang@hand-china.com", id: "2021-08-10-hwkf_def_mail_template") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hwkf_def_mail_template') {
            column(name: "CONTENT_SOURCE", type: "varchar(" + 30 * weight + ")",  remarks: "邮件内容来源")
        }
        addColumn(tableName: 'hwkf_def_mail_template') {
            column(name: "CUSTOMIZE_STRUCT_TEMPLATE", type: "varchar(" + 60 * weight + ")",  remarks: "自定义邮件结构模板")
        }
        addColumn(tableName: 'hwkf_def_mail_template') {
            column(name: "REPORT_EXPORT_TYPE", type: "varchar(" + 30 * weight + ")",  remarks: "报表输出类型")
        }
        addColumn(tableName: 'hwkf_def_mail_template') {
            column(name: "REPORT_ID", type: "bigint",  remarks: "关联的报表ID")
        }
    }
}
