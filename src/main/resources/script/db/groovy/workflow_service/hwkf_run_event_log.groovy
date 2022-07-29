package script.db.groovy.workflow_service
databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_event_log.groovy') {
    def weight_c = 1
    if(helper.isSqlServer()){
        weight_c = 2
    }
    if(helper.isOracle()){
        weight_c = 3
    }
    changeSet(author: "like.zhang@hand-china.com", id: "hwkf_run_event_log-2021-09-02"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_run_event_log_s', startValue:"1")
        }
        createTable(tableName: "hwkf_run_event_log", remarks: "工作流执行事件日志记录表") {
            column(name: "EVENT_LOG_ID", type: "bigint",autoIncrement: true,    remarks: "表主键")  {constraints(primaryKey: true)}
            column(name: "RUN_INSTANCE_ID", type: "bigint",  remarks: "流程实例ID")  {constraints(nullable:"false")}
            column(name: "RUN_NODE_ID", type: "bigint",  remarks: "运行节点ID")
            column(name: "RUN_TASK_ID", type: "bigint",  remarks: "运行任务ID")
            column(name: "EVENT_CODE", type: "varchar(" + 60* weight_c + ")",  remarks: "事件编码")  {constraints(nullable:"false")}
            column(name: "EVENT_TYPE", type: "varchar(" + 60* weight_c + ")",  remarks: "事件类型（HWKF.EVENT_TYPE事件/服务）")  {constraints(nullable:"false")}
            column(name: "SYNC_FLAG", type: "tinyint",   defaultValue:"0",   remarks: "是否同步执行")  {constraints(nullable:"false")}
            column(name: "EVENT_TRIGGER", type: "varchar(" + 60* weight_c + ")",  remarks: "事件触发类型（审批前/后，服务事件）")  {constraints(nullable:"false")}
            column(name: "EVENT_NAME", type: "varchar(" + 80* weight_c + ")",  remarks: "事件名称")
            column(name: "INTERFACE_CODE", type: "varchar(" + 60* weight_c + ")",  remarks: "接口定义编码")
            column(name: "INTERFACE_NAME", type: "varchar(" + 240* weight_c + ")",  remarks: "接口定义名称")
            column(name: "PERMISSION_CODE", type: "varchar(" + 128* weight_c + ")",  remarks: "接口权限编码")
            column(name: "METHOD", type: "varchar(" + 10* weight_c + ")",  remarks: "调用方式 GET/POST/PUT/DELETE")
            column(name: "INTERFACE_URL", type: "varchar(" + 1000* weight_c + ")",  remarks: "接口调用地址")
            column(name: "SERVICE_NAME", type: "varchar(" + 90* weight_c + ")",  remarks: "接口调用服务名")
            column(name: "EXECUTE_STATUS", type: "varchar(" + 10* weight_c + ")",  remarks: "接口执行结果（S/E)")  {constraints(nullable:"false")}
            column(name: "EXECUTE_MESSAGE", type: "longtext",  remarks: "接口执行结果信息")
            column(name: "DEPLOYMENT_ID", type: "bigint",  remarks: "流程部署ID")  {constraints(nullable:"false")}
            column(name: "TENANT_ID", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "EVENT_KEY", type: "varchar(" + 60* weight_c + ")",  remarks: "事件唯一键")  {constraints(nullable:"false")}
            column(name: "EVENT_SORT", type: "int",  remarks: "事件排序")
        }
        createIndex(tableName: "hwkf_run_event_log", indexName: "hwkf_run_event_log_n1") {
            column(name: "EVENT_KEY")
        }
        createIndex(tableName: "hwkf_run_event_log", indexName: "hwkf_run_event_log_n2") {
            column(name: "RUN_TASK_ID")
            column(name: "RUN_NODE_ID")
            column(name: "RUN_INSTANCE_ID")
        }
    }

    changeSet(author: "xiuhong.chen@hand-china.com", id: "2022-03-30-hwkf_run_event_log") {
        dropNotNullConstraint(tableName: 'hwkf_run_event_log', columnName: 'DEPLOYMENT_ID', columnDataType: 'bigint')
    }
}
