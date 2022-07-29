package script.db.groovy.workflow_service
databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_custom_node_att_tl.groovy') {
    def weight_c = 1
    if(helper.isSqlServer()){
    weight_c = 2
    }
    if(helper.isOracle()){
    weight_c = 3
    }
    changeSet(author: "xiuhong.chen@hand-china.com", id: "hwkf_def_custom_node_att_tl-2021-09-01-version-1"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_def_custom_node_att_tl_s', startValue:"1")
        }
        createTable(tableName: "hwkf_def_custom_node_att_tl", remarks: "工作流事件多语言") {
            column(name: "attr_id", type: "bigint",  remarks: "属性ID")  {constraints(nullable:"false")}  
            column(name: "attr_name", type: "varchar(" + 80* weight_c + ")",  remarks: "属性名称")  {constraints(nullable:"false")}  
            column(name: "attr_desc", type: "varchar(" + 240* weight_c + ")",  remarks: "属性描述")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30* weight_c + ")",  remarks: "语言")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"attr_id,lang",tableName:"hwkf_def_custom_node_att_tl",constraintName: "hwkf_def_custom_node_att_tl_u1")
    }

    changeSet(author: "hwkf@hand-china.com", id: "2021-09-14-hwkf_def_custom_node_att_tl") {
        addColumn(tableName: 'hwkf_def_custom_node_att_tl') {
            column(name: "create_prompt", type: "varchar(" + 240* weight_c + ")", remarks: "创建提示")
        }
    }
}
