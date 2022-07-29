package script.db.groovy.workflow_service
databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_custom_node_att.groovy') {
    def weight_c = 1
    if(helper.isSqlServer()){
    weight_c = 2
    }
    if(helper.isOracle()){
    weight_c = 3
    }
    changeSet(author: "xiuhong.chen@hand-china.com", id: "hwkf_def_custom_node_att-2021-08-31-version-1"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_def_custom_node_att_s', startValue:"1")
        }
        createTable(tableName: "hwkf_def_custom_node_att", remarks: "自定义节点属性表") {
            column(name: "attr_id", type: "bigint",autoIncrement: true,    remarks: "")  {constraints(primaryKey: true)} 
            column(name: "custom_node_id", type: "bigint",  remarks: "自定义节点ID")  {constraints(nullable:"false")}  
            column(name: "order_no", type: "int",  remarks: "序号")   
            column(name: "attr_code", type: "varchar(" + 30* weight_c + ")",  remarks: "属性编码")  {constraints(nullable:"false")}  
            column(name: "attr_name", type: "varchar(" + 80* weight_c + ")",  remarks: "属性名称")  {constraints(nullable:"false")}  
            column(name: "attr_desc", type: "varchar(" + 240* weight_c + ")",  remarks: "属性描述")   
            column(name: "attr_type", type: "varchar(" + 30* weight_c + ")",  remarks: "属性类型：下拉框、文本框等")  {constraints(nullable:"false")}  
            column(name: "attr_value_source", type: "varchar(" + 30* weight_c + ")",  remarks: "属性值来源：下拉列表代码")   
            column(name: "default_value", type: "varchar(" + 30* weight_c + ")",  remarks: "默认值")   
            column(name: "multi_select_flag", type: "tinyint",  remarks: "支持多选")   
            column(name: "required_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否必输")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed :"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"attr_code,custom_node_id",tableName:"hwkf_def_custom_node_att",constraintName: "hwkf_def_custom_node_att_u1")
    }

    changeSet(author: "hwkf@hand-china.com", id: "2021-09-14-hwkf_def_custom_node_att") {
        addColumn(tableName: 'hwkf_def_custom_node_att') {
            column(name: "create_prompt", type: "varchar(" + 240* weight_c + ")", remarks: "创建提示")
        }
    }

    changeSet(author: "hwkf@hand-china.com", id: "2021-10-28-hwkf_def_custom_node_att") {
        addColumn(tableName: 'hwkf_def_custom_node_att') {
            column(name: "display_flag", type: "tinyint", defaultValue: "1", remarks: "变量是否显示")
        }
    }

    changeSet(author: "hwkf@hand-china.com", id: "2021-11-01-hwkf_def_custom_node_att") {
        addColumn(tableName: 'hwkf_def_custom_node_att') {
            column(name: "variable_id", type: "bigint", remarks: "变量ID")
        }
    }
}
