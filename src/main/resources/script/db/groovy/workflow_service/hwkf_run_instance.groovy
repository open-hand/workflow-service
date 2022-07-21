package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_instance.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_run_instance") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_run_instance_s', startValue: "1")
        }
        createTable(tableName: "hwkf_run_instance", remarks: "流程实例表") {
            column(name: "INSTANCE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "DEPLOYMENT_ID", type: "bigint", remarks: "流程定义ID，hwkf_def_deployment主键") { constraints(nullable: "false") }
            column(name: "DESCRIPTION", type: "varchar(" + 240 * weight + ")", remarks: "流程描述")
            column(name: "BUSINESS_KEY", type: "varchar(" + 80 * weight + ")", remarks: "业务主键编码，例如关联单据编码")
            column(name: "STATUS", type: "varchar(" + 10 * weight + ")", remarks: "流程实例状态,运行/挂起/结束") { constraints(nullable: "false") }
            column(name: "START_DATE", type: "datetime", remarks: "开始时间")
            column(name: "END_DATE", type: "datetime", remarks: "结束时间")
            column(name: "STARTER", type: "varchar(" + 60 * weight + ")", remarks: "流程发起人")
            column(name: "PARENT_INSTANCE_ID", type: "bigint", remarks: "父流程实例ID")
            column(name: "PARENT_LEVEL_PATH", type: "varchar(" + 1000 * weight + ")", remarks: "父流程层级路径")
            column(name: "REMARK", type: "varchar(" + 240 * weight + ")", remarks: "备注")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "PARENT_INSTANCE_NODE_ID", type: "bigint", remarks: "父流程中-子流程节点nodeId")
            column(name: "URGED_FLAG", type: "tinyint", defaultValue: "0", remarks: "催办标志")
        }

        createIndex(tableName: "hwkf_run_instance", indexName: "hwkf_run_instance_n2") {
            column(name: "BUSINESS_KEY")
            column(name: "DEPLOYMENT_ID")
        }
        createIndex(tableName: "hwkf_run_instance", indexName: "hwkf_run_instance_N1") {
            column(name: "DEPLOYMENT_ID")
        }
        createIndex(tableName: "hwkf_run_instance", indexName: "hwkf_run_instance_N3") {
            column(name: "START_DATE")
        }

    }

    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-05-08-hwkf_run_instance") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hwkf_run_instance') {
            column(name: "APPROVE_RESULT", type: "varchar(" + 30 * weight + ")",  remarks: "审批结果")
        }
        addColumn(tableName: 'hwkf_run_instance') {
            column(name: "DIMENSION", type: "varchar(" + 30 * weight + ")",  remarks: "维度：EMPLOYEE(员工) USER(用户)")
        }
    }

    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-06-18-hwkf_run_instance") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hwkf_run_instance') {
            column(name: "ATTACHMENT_UUID", type: "varchar(" + 50 * weight + ")", remarks: "附件集UUID")
        }
    }

    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-09-02-hwkf_run_instance") {
        dropIndex(tableName: 'hwkf_run_instance', indexName: 'hwkf_run_instance_N1')
        createIndex(tableName: "hwkf_run_instance", indexName: "hwkf_run_instance_N1") {
            column(name: "TENANT_ID")
        }
        createIndex(tableName: "hwkf_run_instance", indexName: "hwkf_run_instance_n4") {
            column(name: "STATUS")
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2021-10-15-hwkf_run_instance-1") {
        createIndex(tableName: "hwkf_run_instance", indexName: "hwkf_run_instance_n5") {
            column(name: "TENANT_ID")
            column(name: "PARENT_INSTANCE_ID")
        }
    }
}
