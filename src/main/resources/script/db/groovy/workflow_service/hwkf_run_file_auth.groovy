package script.db.groovy.workflow_service
databaseChangeLog(logicalFilePath: 'script/db/hwkf_run_file_auth.groovy') {
    def weight_c = 1
    if(helper.isSqlServer()){
    weight_c = 2
    }
    if(helper.isOracle()){
    weight_c = 3
    }
    changeSet(author: "hwkf@hand-china.com", id: "hwkf_run_file_auth-2021-12-15-version-1"){
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwkf_run_file_auth_s', startValue:"1")
        }
        createTable(tableName: "hwkf_run_file_auth", remarks: "附件权限表") {
            column(name: "AUTH_ID", type: "bigint",autoIncrement: true,    remarks: "")  {constraints(primaryKey: true)} 
            column(name: "ATTACHMENT_UUID", type: "varchar(" + 50* weight_c + ")",  remarks: "附件uuid")  {constraints(nullable:"false")}  
            column(name: "FILE_ID", type: "bigint",  remarks: "文件ID")  {constraints(nullable:"false")}  
            column(name: "AUTH_TYPE", type: "varchar(" + 30* weight_c + ")",  remarks: "权限类型，HWKF.FILE_AUTH_TYPE")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最近更新人")  {constraints(nullable:"false")}  
            if(helper.isMysql()){
               column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            }
            if(helper.isSqlServer()){
                column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            }
            if(helper.isOracle()){
                column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            }
            if(helper.isPostgresql()){
                column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间")  {constraints(nullable:"false")}  
            }
            if(helper.isMysql()){
               column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            }
            if(helper.isSqlServer()){
                column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            }
            if(helper.isOracle()){
                column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            }
            if(helper.isPostgresql()){
                column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最近更新时间")  {constraints(nullable:"false")}  
            }
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
        }
       createIndex(tableName: "hwkf_run_file_auth", indexName: "hwkf_run_file_auth_n1") {
           column(name: "ATTACHMENT_UUID")
       }
        addUniqueConstraint(columnNames:"FILE_ID",tableName:"hwkf_run_file_auth",constraintName: "hwkf_run_file_auth_u1")
    }
}
