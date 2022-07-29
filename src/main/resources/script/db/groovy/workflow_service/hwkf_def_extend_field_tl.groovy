package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_extend_field_tl.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-07-19-hwkf_def_extend_field_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_extend_field_tl_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_extend_field_tl", remarks: "扩展字段表多语言") {
            column(name: "FIELD_ID", type: "bigint", remarks: "字段ID") { constraints(nullable: "false") }
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "FIELD_NAME", type: "varchar(" + 80 * weight + ")", remarks: "字段名称") { constraints(nullable: "false") }
            column(name: "REMARK", type: "varchar(" + 240 * weight + ")", remarks: "描述")
            column(name: "TENANT_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "FIELD_ID,LANG", tableName: "hwkf_def_extend_field_tl", constraintName: "hwkf_def_extend_field_tl_u1")
    }
}
