package script.db.groovy.workflow_service

/**
 * 班翎工作流升级sql
 * @author gaokuo.dai@zknow.com 2022-08-07
 **/
databaseChangeLog(logicalFilePath: 'script/db/z002_pi21_remove_activiti.groovy') {

    changeSet(id: '2022-12-30-cz002_pi21_remove_activiti', author: 'gaokuo.dai@zknow.com') {
        // 解锁外键约束
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "SET FOREIGN_KEY_CHECKS=0"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_evt_log"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ge_bytearray"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ge_property"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_hi_actinst"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_hi_attachment"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_hi_comment"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_hi_detail"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_hi_identitylink"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_hi_procinst"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_hi_taskinst"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_hi_varinst"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_procdef_info"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_re_deployment"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_re_model"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_re_procdef"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_deadletter_job"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_event_subscr"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_execution"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_identitylink"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_integration"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_job"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_suspended_job"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_task"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_timer_job"
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "drop table if exists act_ru_variable"
        }
        // 恢复外键约束
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "SET FOREIGN_KEY_CHECKS=1"
        }
    }
}
