package script.db.groovy.workflow_service

/**
 * 班翎工作流升级sql
 * @author gaokuo.dai@zknow.com 2022-08-07
 **/
databaseChangeLog(logicalFilePath: 'script/db/z001_cwkf_data_fix.groovy') {

    // ↓↓↓↓ hwkf 1.7 - 1.8 ↓↓↓↓
    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-001', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 流程分类操作记录修复
            "update hwkf_def_history set TABLE_NAME = 'HWKF_DEF_EVENT' where TABLE_NAME = 'HWKF_DEF_FORM' and JSON_DETAIL like '%eventName%'"
        }
    }
    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-002', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 迁移节点多语言到归档表
            "insert into hwkf_run_task_his_tl_arch(TASK_HISTORY_ID, NODE_NAME, LANG, TENANT_ID)\n" +
                    "            select TASK_HISTORY_ID, NODE_NAME, LANG, TENANT_ID from hwkf_run_task_history_tl where task_history_id in\n" +
                    "                    (SELECT task_history_id from hwkf_run_task_hist_arch)"
        }
    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-003', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 删除已经归档的多语言
            "delete from hwkf_run_task_history_tl where task_history_id in\n" +
                    "            (SELECT task_history_id from hwkf_run_task_hist_arch)"
        }
    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-004', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 迁移审批意见到归档表
            "insert into hwkf_run_comment_arch(COMMENT_ID,TASK_ID,INSTANCE_ID,NODE_ID,COMMENT_CONTENT,REMARK,TENANT_ID,OBJECT_VERSION_NUMBER,\n" +
                    "CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,LAST_UPDATE_DATE,TASK_HISTORY_ID)\n" +
                    "select COMMENT_ID,TASK_ID,INSTANCE_ID,NODE_ID,COMMENT_CONTENT,REMARK,TENANT_ID,OBJECT_VERSION_NUMBER,\n" +
                    "CREATION_DATE,CREATED_BY,LAST_UPDATED_BY,LAST_UPDATE_DATE,TASK_HISTORY_ID from hwkf_run_comment where INSTANCE_ID in \n" +
                    "(SELECT INSTANCE_ID from hwkf_run_instance_arch)"
        }
    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-005', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 删除已经归档的审批意见
            "delete from hwkf_run_comment where INSTANCE_ID in \n" +
                    "(SELECT INSTANCE_ID from hwkf_run_instance_arch)"
        }
    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-006', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 迁移已经结束的流程run_assignee到历史表
            "insert into hwkf_run_assignee_his(ASSIGNEE_ID, INSTANCE_ID, NODE_ID, TASK_ID, TASK_HISTORY_ID, ASSIGNEE_TYPE,\n" +
                    "ASSIGNEE, DIMENSION, ORDER_NO, NEXT_NODE_CODE, TENANT_ID, OBJECT_VERSION_NUMBER, \n" +
                    "CREATION_DATE, CREATED_BY, LAST_UPDATED_BY, LAST_UPDATE_DATE) \n" +
                    "select ra.ASSIGNEE_ID, ra.INSTANCE_ID, ra.NODE_ID, ra.TASK_ID, ra.TASK_HISTORY_ID,ra.ASSIGNEE_TYPE,\n" +
                    "ra.ASSIGNEE, ra.DIMENSION, ra.ORDER_NO, ra.NEXT_NODE_CODE, ra.TENANT_ID, ra.OBJECT_VERSION_NUMBER, \n" +
                    "ra.CREATION_DATE, ra.CREATED_BY, ra.LAST_UPDATED_BY, ra.LAST_UPDATE_DATE from hwkf_run_assignee ra \n" +
                    "left join hwkf_run_instance ri on ra.INSTANCE_ID = ri.INSTANCE_ID \n" +
                    "where ri.STATUS in ('END','WITHDRAW','INTERRUPT')"
        }

    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-007', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 删除已经结束的run_assignee
            "delete from hwkf_run_assignee_his where INSTANCE_ID in \n" +
                    "(select INSTANCE_ID from hwkf_run_instance ri \n" +
                    "where ri.STATUS in ('END','WITHDRAW','INTERRUPT'))"
        }
    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-008', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 处理规则
            "UPDATE hwkf_def_automatic_process set RULE_TYPE = 'SINGLE' WHERE RULE_TYPE IS NULL"
        }
    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.8-009', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 审批表单，去掉默认表单功能
            "UPDATE hwkf_def_form SET DEFAULT_FLAG = 0 WHERE DEFAULT_FLAG = 1"
        }
    }

    // ↓↓↓↓ hwkf 1.8 - 1.9 ↓↓↓↓
    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.9-001', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // HWKF_DEF_EXTEND_FIELD
            "UPDATE HWKF_DEF_EXTEND_FIELD SET FIELD_SOURCE = 'BUSINESS_FIELD',PAGE_TYPE = 'TODO',RECORD_SOURCE_TYPE = 'CUSTOMIZE' WHERE FLOW_ID IS NOT NULL AND PAGE_TYPE IS NULL"
        }
    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.9-002', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 流程变量定义
            "update hwkf_def_variable set RANGE_LOV_SOURCE = 'SYSTEM' WHERE RANGE_TYPE in ('LOV','LIST')"
        }
    }

    changeSet(id: '2022-08-07-cwkf-data-fix-update-hwkf-1.9-003', author: 'gaokuo.dai@zknow.com') {
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            // 审批表单，去掉默认表单功能
            "UPDATE hwkf_def_form SET DEFAULT_FLAG = 0 WHERE DEFAULT_FLAG = 1"
        }
    }
}
