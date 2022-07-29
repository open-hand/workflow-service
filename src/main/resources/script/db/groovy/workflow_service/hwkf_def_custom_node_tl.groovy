package script.db.groovy.workflow_service
databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_custom_node_tl.groovy') {
    def weight_c = 1
    if(helper.isSqlServer()){
    weight_c = 2
    }
    if(helper.isOracle()){
    weight_c = 3
    }
    changeSet(author: "xiuhong.chen@hand-china.com", id: "hwkf_def_custom_node_tl-2021-09-01-version-1"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_def_custom_node_tl_s', startValue:"1")
        }
        createTable(tableName: "hwkf_def_custom_node_tl", remarks: "工作流事件多语言") {
            column(name: "custom_node_id", type: "bigint",  remarks: "自定义节点ID")  {constraints(nullable:"false")}  
            column(name: "custom_node_name", type: "varchar(" + 80* weight_c + ")",  remarks: "自定义节点名称")  {constraints(nullable:"false")}  
            column(name: "\"desc\"", type: "varchar(" + 240* weight_c + ")",  remarks: "自定义节点描述")
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30* weight_c + ")",  remarks: "语言")  {constraints(nullable:"false")}  
        }
        addUniqueConstraint(columnNames:"custom_node_id,lang",tableName:"hwkf_def_custom_node_tl",constraintName: "hwkf_def_custom_node_tl_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2021-10-15-hwkf_def_custom_node_tl") {
        renameColumn(tableName: "hwkf_def_custom_node_tl", oldColumnName: "\"desc\"",
                newColumnName: "custom_node_desc", columnDataType: "varchar(" + 240 * weight_c + ")", remarks: "自定义节点描述")
    }
}
