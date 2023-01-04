简体中文 | [English](./README.en_US.md)

# Workflow Service   

`Workflow Service`是基于HZERO-WORKFLOW搭建的工作流服务，支持动态灵活地创建流程，启动流程、监控流程以及管理流程。

## 功能

- 流水线管理

  此功能用于动态创建流水线Modal。
  
- 流水线实例管理

  此功能用于启动、删除流水线实例，审核人工审核任务。
  
- 用户管理功能

  此功能用于将Choerodon用户登录到HZERO-WORKFLOW。

## 环境依赖
- JDK-8
- [Maven](http://www.maven-sf.com/)
- [MySQL](https://www.mysql.com)
- [HZERO-WORKFLOW](https://open.hand-china.com/)


## 安装与启动

1. 初始化数据库
    ```sql
       CREATE USER 'choerodon'@'%' IDENTIFIED BY "choerodon";
       CREATE DATABASE workflow_service DEFAULT CHARACTER SET utf8;
       GRANT ALL PRIVILEGES ON workflow_service.* TO choerodon@'%';
       FLUSH PRIVILEGES;
    ```

2. 执行下列命令或在 IntelliJ IDEA 中运行`WorkFlowServiceApplication`类

    ```bash
    mvn clean spring-boot:run
    ```

## 服务依赖
- `eureka-server`: 注册&配置中心
- `oauth-server` 认证中心
- `asgard-service`: Asgard 服务
- `MySQL`: workflow_service 数据库


## 问题报告

如果您发现任何缺陷或bug，请在  [问题报告](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md) 中提出

## 贡献

欢迎贡献代码！ [如何贡献](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md)

