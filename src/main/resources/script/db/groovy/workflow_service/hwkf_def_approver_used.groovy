package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_approver_used.groovy') {
    changeSet(author: "xiuhong.chen@hand-china.com", id: "2021-07-14-hwkf_def_approver_used") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_def_approver_used_s', startValue:"1")
        }
        createTable(tableName: "hwkf_def_approver_used", remarks: "") {
            column(name: "USED_ID", type: "bigint(20)", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "RULE_LINE_ID", type: "bigint(20)",  remarks: "规则行ID")  {constraints(nullable:"false")}  
            column(name: "USED_TYPE", type: "varchar(" + 30 * weight + ")",  remarks: "应用类型")  {constraints(nullable:"false")}  
            column(name: "FLOW_ID", type: "bigint(20)",  remarks: "流程定义ID")   
            column(name: "NODE_CODE", type: "varchar(" + 30 * weight + ")",  remarks: "节点编码")   
            column(name: "NODE_NAME", type: "varchar(" + 30 * weight + ")",  remarks: "节点名称")   
            column(name: "REMARK", type: "varchar(" + 30 * weight + ")",  remarks: "备注")   
            column(name: "CHAIN_LINE_ID", type: "bigint(20)",  remarks: "审批链行ID")   
            column(name: "VERSION", type: "int", remarks: "版本号")
            column(name: "TENANT_ID", type: "bigint(20)",  remarks: "")  {constraints(nullable:"false")}
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint(20)",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "CREATED_BY", type: "bigint(20)",  remarks: "")   
            column(name: "LAST_UPDATED_BY", type: "bigint(20)",  remarks: "")   
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "LAST_UPDATE_LOGIN", type: "int(11)",  remarks: "")   

        }

    }
}
