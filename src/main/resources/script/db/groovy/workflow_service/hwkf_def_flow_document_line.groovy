package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_flow_document_line.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-04-13-hwkf_def_flow_document_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_def_flow_document_line_s', startValue:"1")
        }
        createTable(tableName: "hwkf_def_flow_document_line", remarks: "业务单据映射行表") {
            column(name: "LINE_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "RELATE_ID", type: "bigint",  remarks: "关联ID，FLOW_DOCUMENT表主键")  {constraints(nullable:"false")}
            column(name: "FLOW_ID", type: "bigint",  remarks: "流程定义ID")  {constraints(nullable:"false")}
            column(name: "DESCRIPTION", type: "varchar(" + 255 * weight + ")",  remarks: "备注")   
            column(name: "CONDITION_JSON", type: "longtext",  remarks: "条件报文")  {constraints(nullable:"false")}  
            column(name: "ENABLED_FLAG", type: "tinyint",   defaultValue:"1",   remarks: "启用")  {constraints(nullable:"false")}
            column(name: "DEFAULT_FLAG", type: "tinyint",   defaultValue:"0",   remarks: "默认")  {constraints(nullable:"false")}
            column(name: "TENANT_ID", type: "bigint",  remarks: "")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "bigint",   defaultValue:"-1",   remarks: "")

        }

        addUniqueConstraint(columnNames:"RELATE_ID,FLOW_ID",tableName:"hwkf_def_flow_document_line",constraintName: "RELATE_ID")
    }
}
