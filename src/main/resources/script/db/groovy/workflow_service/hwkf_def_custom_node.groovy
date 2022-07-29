package script.db.groovy.workflow_service
databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_custom_node.groovy') {
    def weight_c = 1
    if(helper.isSqlServer()){
    weight_c = 2
    }
    if(helper.isOracle()){
    weight_c = 3
    }
    changeSet(author: "xiuhong.chen@hand-china.com", id: "hwkf_def_custom_node-2021-09-07-version-2"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_def_custom_node_s', startValue:"1")
        }
        createTable(tableName: "hwkf_def_custom_node", remarks: "自定义节点表") {
            column(name: "custom_node_id", type: "bigint",autoIncrement: true, startWith: "1", incrementBy:"1",   remarks: "")  {constraints(primaryKey: true)}
            column(name: "type_id", type: "bigint", remarks: "流程分类ID") { constraints(nullable: "false") }
            column(name: "custom_node_code", type: "varchar(" + 30* weight_c + ")",  remarks: "自定义节点编码")  {constraints(nullable:"false")}  
            column(name: "custom_node_name", type: "varchar(" + 80* weight_c + ")",  remarks: "自定义节点名称")  {constraints(nullable:"false")}  
            column(name: "custom_node_type", type: "varchar(" + 30* weight_c + ")",  remarks: "节点类型")  {constraints(nullable:"false")}  
            column(name: "custom_node_desc", type: "varchar(" + 240* weight_c + ")",  remarks: "描述")   
            column(name: "executor_interface_id", type: "bigint",  remarks: "处理器-接口定义ID")  {constraints(nullable:"false")}
            column(name: "validator_interface_id", type: "bigint",  remarks: "校验器-接口定义ID")
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "")
            column(name: "tenant_id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"type_id,custom_node_code",tableName:"hwkf_def_custom_node",constraintName: "hwkf_def_custom_node_u1")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-10-15-hwkf_def_custom_node') {
        addColumn(tableName: 'hwkf_def_custom_node') {
            column(name: "record_source_type", type: "varchar(" + 30* weight_c + ")", remarks: "记录来源:PREDEFINED(预定义),CUSTOMIZE(自定义)", defaultValue:"CUSTOMIZE") {constraints(nullable:"false")}
        }
    }
}
