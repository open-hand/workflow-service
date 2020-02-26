FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.8.0
COPY app.jar /workflow-service.jar
RUN chown -R www-data:www-data  /workflow-service.jar
USER www-data
CMD java $JAVA_OPTS $SKYWALKING_OPTS  -jar /workflow-service.jar

