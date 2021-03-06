package script.db.groovy.workflow_service

/**
 *
 * @author zhaotianxin* @date 2021-03-08 10:50
 **/
 databaseChangeLog(logicalFilePath: 'cwkf_project_workflow_rel.groovy') {
     changeSet(id: '2021-03-08-cwkf-project-workflow-rel', author: 'ztxemail@163.com') {
         createTable(tableName: "cwkf_project_workflow_rel", remarks: '项目和工作流的关系') {
             column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '主键Id') {
                 constraints(primaryKey: true)
             }
             column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目Id') {
                 constraints(nullable: false)
             }
             column(name: 'flow_code', type: 'VARCHAR(255)', remarks: '流程编码') {
                 constraints(nullable: false)
             }

             column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织Id') {
                 constraints(nullable: false)
             }

             column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
             column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
             column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
             column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
             column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
         }
     }

     changeSet(id: '2021-04-13-cwkf-project-workflow-rel-add-index', author: 'ztxemail@163.com'){
         createIndex(indexName: "idx_organization_id", tableName: "cwkf_project_workflow_rel") {
             column(name: "organization_id")
         }
         createIndex(indexName: "idx_project_id", tableName: "cwkf_project_workflow_rel") {
             column(name: "project_id")
         }
     }
 }
