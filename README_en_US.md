English | [简体中文](./README.md)

# Workflow Service   

`Workflow Service` is a Workflow Service based on HZERO-WORKFLOW, which supports dynamically and flexibly creating processes, starting processes, monitoring processes and management processes.

## Feature

- Pipeline management

  This function can dynamically create pipeline Modal.
  
- Pipeline Instance management

  This function can start and delete pipeline instances and audit human audit tasks.
  
- User Management

  This function is used to log Choerodon users into HZERO-WORKFLOW.

## 环境依赖
- JDK-8
- [Maven](http://www.maven-sf.com/)
- [MySQL](https://www.mysql.com)
- [HZERO-WORKFLOW](https://open-hand-china.com/)

## Installation and Getting Started

1. init database

    ```sql
        CREATE USER 'choerodon'@'%' IDENTIFIED BY "choerodon";
        CREATE DATABASE workflow_service DEFAULT CHARACTER SET utf8;
        GRANT ALL PRIVILEGES ON workflow_service.* TO choerodon@'%';
        FLUSH PRIVILEGES;
    ```
2. run command as follow or run `WorkFlowServiceApplication` in IntelliJ IDEA

    ```bash
    mvn clean spring-boot:run
    ```

## Service dependencies
- `eureka-server`: Register & configure center
- `oauth-server` authentication center
- `asgard-service`: Asgard Service
- `MySQL`: gitlab_service

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.
