package script.db.groovy.workflow_service

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_type.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_type") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_type_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_type", remarks: "工作流分类表") {
            column(name: "TYPE_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "TYPE_CODE", type: "varchar(" + 30 * weight + ")", remarks: "工作流类型编码") { constraints(nullable: "false") }
            column(name: "TYPE_NAME", type: "varchar(" + 80 * weight + ")", remarks: "工作流类型名称") { constraints(nullable: "false") }
            column(name: "TYPE_DESC", type: "varchar(" + 255 * weight + ")", remarks: "工作流类型描述")
            column(name: "PARENT_TYPE_ID", type: "bigint", remarks: "父分类ID")
            column(name: "LEAF_FLAG", type: "tinyint", defaultValue: "0", remarks: "是否叶子节点  1/是  0/否") { constraints(nullable: "false") }
            column(name: "LEVEL_PATH", type: "varchar(" + 240 * weight + ")", remarks: "层级路径") { constraints(nullable: "false") }
            column(name: "ENABLED_FLAG", type: "tinyint", defaultValue: "1", remarks: "是否启用  1/启用 0/失效") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hwkf_def_type", indexName: "hwkf_def_type_n1") {
            column(name: "LEVEL_PATH")
        }

        addUniqueConstraint(columnNames: "TYPE_CODE,TENANT_ID", tableName: "hwkf_def_type", constraintName: "hwkf_def_type_u1")
    }

    changeSet(author: "hwkf@hand-china.com", id: "2021-10-18-hwkf_def_type") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hwkf_def_type') {
            column(name: "BUSINESS_OBJECT_ID", type: "bigint",  remarks: "业务对象ID")
        }
        addColumn(tableName: 'hwkf_def_type') {
            column(name: "BUSINESS_OBJECT_CODE", type: "varchar(" + 30 * weight + ")",  remarks: "业务对象编码")
        }
    }

    changeSet(author: "hwkf@hand-china.com", id: "2022-01-06-hwkf_def_type") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hwkf_def_type') {
            column(name: "SOURCE_MODE", type: "varchar(" + 30 * weight + ")",  remarks: "来源方式：新建、复制、分配、导入")
        }
        addColumn(tableName: 'hwkf_def_type') {
            column(name: "SOURCE_TYPE_ID", type: "bigint",  remarks: "来源流程分类ID")
        }
    }
}
