package script.db.groovy.workflow_service

/**
 * 班翎工作流升级sql
 * @author gaokuo.dai@zknow.com 2022-08-07
 **/
databaseChangeLog(logicalFilePath: 'script/db/z002_pi21_remove_activiti.groovy') {

    changeSet(id: '2022-12-30-cz002_pi21_remove_activiti', author: 'gaokuo.dai@zknow.com') {
        dropTable(tableName:"act_evt_log")
        dropTable(tableName:"act_ge_bytearray")
        dropTable(tableName:"act_ge_property")
        dropTable(tableName:"act_hi_actinst")
        dropTable(tableName:"act_hi_attachment")
        dropTable(tableName:"act_hi_comment")
        dropTable(tableName:"act_hi_detail")
        dropTable(tableName:"act_hi_identitylink")
        dropTable(tableName:"act_hi_procinst")
        dropTable(tableName:"act_hi_taskinst")
        dropTable(tableName:"act_hi_varinst")
        dropTable(tableName:"act_procdef_info")
        dropTable(tableName:"act_re_deployment")
        dropTable(tableName:"act_re_model")
        dropTable(tableName:"act_re_procdef")
        dropTable(tableName:"act_ru_deadletter_job")
        dropTable(tableName:"act_ru_event_subscr")
        dropTable(tableName:"act_ru_execution")
        dropTable(tableName:"act_ru_identitylink")
        dropTable(tableName:"act_ru_integration")
        dropTable(tableName:"act_ru_job")
        dropTable(tableName:"act_ru_suspended_job")
        dropTable(tableName:"act_ru_task")
        dropTable(tableName:"act_ru_timer_job")
        dropTable(tableName:"act_ru_variable")
    }
}
