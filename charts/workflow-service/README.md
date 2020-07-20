# Choerodon Workflow Service
`Workflow Service`是基于Activiti7搭建的工作流服务，支持动态灵活地创建流程，启动流程、监控流程以及管理流程。

## Add Helm chart repository

``` bash
helm repo add choerodon https://openchart.choerodon.com.cn/choerodon/c7n
helm repo update
```

## Installing the Chart

```bash
$ helm install c7n/workflow-service --name workflow-service
```

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`.

## Uninstalling the Chart

```bash
$ helm delete workflow-service
```

## Configuration

Parameter | Description	| Default
--- |  ---  |  ---
 replicaCount | ReplicaSet数量 | 1 
 image.repository| 镜像仓库地址 | registry.cn-hangzhou.aliyuncs.com/choerodon-c7ncd/workflow-service
image.pullPolicy|镜像拉取策略 | IfNotPresent
metrics.path|监控地址|/actuator/prometheus
metrics.group|监控组|spring-boot
log.parser|日志|spring-boot
deployment.managementPort|管理端口|8066
env.open.SPRING_DATASOURCE_URL|数据库链接地址|jdbc:mysql://localhost::3306/demo_service?useUnicode=true&characterEncoding=utf-8&useSSL=false
env.open.SPRING_DATASOURCE_USERNAME|数据库用户名|choerodon
env.open.SPRING_DATASOURCE_PASSWORD|数据库密码|123456
env.open.SPRING_CLOUD_CONFIG_ENABLED|启用配置中心|true
env.open.SPRING_CLOUD_CONFIG_URI|配置中心地址|http://config-server.framework:8010/
env.open.EUREKA_CLIENT_SERVICEURL_DEFAULTZONE|注册服务地址|http://register-server.io-choerodon:8000/eureka/
persistence.enabled|是否启用持久化存储|false
service.enabled|是否创建service|false
service.type|service类型|ClusterIP
service.port|service端口|8065
ingress.enabled|是够创建域名|false
resources.limits.memory|资源请求限制|2Gi
resources.requests.memory|资源请求需求|1.5Gi

### SkyWalking Configuration
Parameter | Description
--- |  ---
`javaagent` | SkyWalking 代理jar包(添加则开启 SkyWalking，删除则关闭)
`skywalking.agent.application_code` | SkyWalking 应用名称
`skywalking.agent.sample_n_per_3_secs` | SkyWalking 采样率配置
`skywalking.agent.namespace` | SkyWalking 跨进程链路中的header配置
`skywalking.agent.authentication` | SkyWalking 认证token配置
`skywalking.agent.span_limit_per_segment` | SkyWalking 每segment中的最大span数配置
`skywalking.agent.ignore_suffix` | SkyWalking 需要忽略的调用配置
`skywalking.agent.is_open_debugging_class` | SkyWalking 是否保存增强后的字节码文件
`skywalking.collector.backend_service` | SkyWalking OAP 服务地址和端口配置

```bash
$ helm install c7n/workflow-service \
    --set env.open.SKYWALKING_OPTS="-javaagent:/agent/skywalking-agent.jar -Dskywalking.agent.application_code=workflow-service  -Dskywalking.agent.sample_n_per_3_secs=-1 -Dskywalking.collector.backend_service=oap.skywalking:11800" \
    --name workflow-service
```

## 验证部署
```bash
curl -s $(kubectl get po -n c7n-system -l choerodon.io/release=workflow-service -o jsonpath="{.items[0].status.podIP}"):8066/actuator/health | jq -r .status
```
出现以下类似信息即为成功部署

```bash
UP
```
