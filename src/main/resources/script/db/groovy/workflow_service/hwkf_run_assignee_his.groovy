package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_assignee_his.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-08-30-hwkf_run_assignee_his") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_assignee_his_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_assignee_his", remarks: "处理人表") {
            column(name: "ASSIGNEE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "INSTANCE_ID", type: "bigint", remarks: "流程实例ID，hwkf_run_instance主键") { constraints(nullable: "false") }
            column(name: "NODE_ID", type: "bigint", remarks: "执行节点ID，hwkf_run_node主键")
            column(name: "TASK_ID", type: "bigint", remarks: "流程任务ID，hwkf_run_task主键")
            column(name: "TASK_HISTORY_ID", type: "bigint", remarks: "hwkf_run_task_history主键")
            column(name: "ASSIGNEE_TYPE", type: "varchar(" + 30 * weight + ")", remarks: "处理类型:  SERIAL_ASSIGNEE(串行审批人)、APPOINT_NEXT_APPROVER(指定下一审批人)") { constraints(nullable: "false") }
            column(name: "ASSIGNEE", type: "varchar(" + 60 * weight + ")", remarks: "处理人") { constraints(nullable: "false") }
            column(name: "DIMENSION", type: "varchar(" + 30 * weight + ")", remarks: "维度：EMPLOYEE(员工) USER(用户)") { constraints(nullable: "false") }
            column(name: "ORDER_NO", type: "bigint", remarks: "序号")
            column(name: "NEXT_NODE_CODE", type: "varchar(" + 80 * weight + ")", remarks: "下一节点编码")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATED_BY", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }
        createIndex(tableName: "hwkf_run_assignee_his", indexName: "hwkf_run_assignee_his_N1") {
            column(name: "INSTANCE_ID")
        }
        createIndex(tableName: "hwkf_run_assignee_his", indexName: "hwkf_run_assignee_his_N2") {
            column(name: "NODE_ID")
        }
        createIndex(tableName: "hwkf_run_assignee_his", indexName: "hwkf_run_assignee_his_N3") {
            column(name: "TASK_ID")
        }
    }
}
