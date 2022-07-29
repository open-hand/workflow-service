package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approver_rule_tl.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_approver_rule_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_approver_rule_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_approver_rule_tl", remarks: "工作流审批人规则多语言表") {
            column(name: "RULE_ID", type: "bigint", remarks: "规则ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 20 * weight + ")", remarks: "多语言") { constraints(nullable: "false") }
            column(name: "RULE_NAME", type: "varchar(" + 80 * weight + ")", remarks: "审批人规则名称") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "")
        }


        addUniqueConstraint(columnNames: "RULE_ID,LANG", tableName: "hwkf_def_approver_rule_tl", constraintName: "hwkf_def_approver_rule_tl_u1")
    }
}
