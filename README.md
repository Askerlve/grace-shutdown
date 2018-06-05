# grace-shutdown-starter
```$xslt
springboot 2.0优雅关机starter，支持Tomcat和Undertow
```
## Tomcat使用方式

### properties配置

```xml

#server
server.port=9088
logging.config=classpath:log4j2.xml
#访问路径，配置后就和1.x版本路径一样
management.endpoints.web.base-path=/
# 暴露我们自定义的endpoints，也可以暴露单个或多个
management.endpoints.web.exposure.include=graceShutdown,health
# 关闭原生shutdown
management.endpoint.shutdown.enabled=false

endpoints.shutdown.grace.timeout=60
endpoints.shutdown.grace.wait=5

```

### 执行停机endpoint访问
```curl
curl -X POST "http://localhost:9088/graceShutdown"
```

### 健康监测

```curl
curl -X GET "http://localhost:9088/health"
```

## Undertow方式

### properties配置

```xml

#server
server.port=9088
logging.config=classpath:log4j2.xml
#访问路径，配置后就和1.x版本路径一样
management.endpoints.web.base-path=/
# 暴露我们自定义的endpoints，也可以暴露单个或多个
management.endpoints.web.exposure.include=graceShutdown,health
# 关闭原生shutdown
management.endpoint.shutdown.enabled=false

endpoints.shutdown.grace.timeout=60
endpoints.shutdown.grace.wait=5

#默认为tomcat
grace.shutdown.server.container=undertow

```

其他操作与tomcat一致,参考项目,springboot1.0优雅停机方式:https://github.com/corentin59/spring-boot-graceful-shutdown
