package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_task_his_tl_arch.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-08-30-hwkf_run_task_his_tl_arch") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_task_his_tl_arch_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_task_his_tl_arch", remarks: "") {
            column(name: "TASK_HISTORY_ID", type: "bigint", remarks: "") { constraints(nullable: "false") }
            column(name: "NODE_NAME", type: "varchar(" + 100 * weight + ")", remarks: "节点名称")
            column(name: "LANG", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "TASK_HISTORY_ID,LANG", tableName: "hwkf_run_task_his_tl_arch", constraintName: "hwkf_run_task_his_tl_arch_ul")
    }

    changeSet(author: "xiuhong.chen@hand-china.com", id: "2022-06-09-hwkf_run_task_his_tl_arch") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hwkf_run_task_his_tl_arch') {
            column(name: "REMARK", type: "varchar(" + 240 * weight + ")", remarks: "备注")
        }
    }
}
