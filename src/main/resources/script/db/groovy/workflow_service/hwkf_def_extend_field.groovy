package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_extend_field.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-07-16-hwkf_def_extend_field") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_def_extend_field_s', startValue:"1")
        }
        createTable(tableName: "hwkf_def_extend_field", remarks: "扩展字段表") {
            column(name: "FIELD_ID", type: "bigint(20)", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "FLOW_ID", type: "bigint(20)",  remarks: "流程定义ID")  {constraints(nullable:"false")}  
            column(name: "FIELD_CODE", type: "varchar(" + 80 * weight + ")",  remarks: "字段编码")  {constraints(nullable:"false")}  
            column(name: "FIELD_NAME", type: "varchar(" + 80 * weight + ")",  remarks: "字段名称")  {constraints(nullable:"false")}
            column(name: "DISPLAY_FLAG", type: "tinyint",   defaultValue:"1",   remarks: "是否展示")  {constraints(nullable:"false")}
            column(name: "ORDER_NO", type: "tinyint",   defaultValue:"1",   remarks: "序号")  {constraints(nullable:"false")}
            column(name: "REMARK", type: "varchar(" + 240 * weight + ")",  remarks: "备注描述")
            column(name: "TENANT_ID", type: "bigint(20)",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint(20)",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint(20)",  remarks: "")   
            column(name: "LAST_UPDATED_BY", type: "bigint(20)",  remarks: "")   
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hwkf_def_extend_field", indexName: "hwkf_def_extend_field_N1") {
            column(name: "FLOW_ID")
        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2021-11-17-hwkf_def_extend_field") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropNotNullConstraint(tableName: 'hwkf_def_extend_field', columnName: 'FLOW_ID', columnDataType: 'bigint(20)')
        addColumn(tableName: 'hwkf_def_extend_field') {
            column(name: "FIELD_SOURCE",  type: "varchar(" + 30 * weight + ")",  remarks: "字段来源",defaultValue: "BUSINESS_FIELD")
        }
        addColumn(tableName: 'hwkf_def_extend_field') {
            column(name: "PAGE_TYPE",  type: "varchar(" + 30 * weight + ")",  remarks: "所属页面")
        }
        addColumn(tableName: 'hwkf_def_extend_field') {
            column(name: "FROZEN_FLAG",  type: "tinyint",  remarks: "是否冻结",defaultValue:"0")
        }
        addColumn(tableName: 'hwkf_def_extend_field') {
            column(name: "SELECT_FLAG",  type: "tinyint",  remarks: "是否查询可见",defaultValue:"0")
        }
        addColumn(tableName: 'hwkf_def_extend_field') {
            column(name: "DETAIL_FLAG",  type: "tinyint",  remarks: "是否详情可见",defaultValue:"0")
        }
        addColumn(tableName: 'hwkf_def_extend_field') {
            column(name: "JUMP_FLAG",  type: "tinyint",  remarks: "是否跳转",defaultValue:"0")
        }
        addColumn(tableName: 'hwkf_def_extend_field') {
            column(name: "ENABLED_FLAG",  type: "tinyint",  remarks: "启用",defaultValue:"1")
        }
        addColumn(tableName: 'hwkf_def_extend_field') {
            column(name: "RECORD_SOURCE_TYPE",  type: "varchar(" + 30 * weight + ")",  remarks: "记录来源：PREDEFINED(预定义)、CUSTOMIZE(自定义)",defaultValue: "CUSTOMIZE")
        }
        createIndex(tableName: "hwkf_def_extend_field", indexName: "hwkf_def_extend_field_N2") {
            column(name: "TENANT_ID")
        }
        addUniqueConstraint(columnNames: "FLOW_ID,FIELD_CODE,PAGE_TYPE,TENANT_ID", tableName: "hwkf_def_extend_field", constraintName: "hwkf_def_extend_field_U1")

    }

    changeSet(author: "hzero@hand-china.com", id: "2021-11-23-hwkf_def_extend_field") {
        modifyDataType(tableName: "hwkf_def_extend_field",  columnName: "ORDER_NO", newDataType:"int")
    }
}
