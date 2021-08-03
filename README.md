# [CoSky](https://github.com/Ahoo-Wang/CoSky) High-performance, low-cost microservice governance platform (Service Discovery and Configuration Service)

> [中文文档](https://github.com/Ahoo-Wang/CoSky/blob/main/README.zh-CN.md)

*[CoSky](https://github.com/Ahoo-Wang/CoSky)* is a lightweight, low-cost service registration, service discovery, and configuration service SDK. By using Redis in the existing infrastructure (I believe you have already deployed Redis), it doesn’t need to bring extra to the operation and maintenance deployment. Cost and burden. With the high performance of Redis, *CoSky* provides ultra-high TPS&QPS (100,000+/s [JMH Benchmark](#jmh-benchmark)). *CoSky* combines the process cache strategy + *Redis PubSub* to achieve real-time process cache refresh, with unparalleled QPS performance (70,000,000+/s [JMH Benchmark](#jmh-benchmark)) and real-time consistency
between process cache and Redis.

### Service Discovery

![CoSky-Discovery](./docs/CoSky-Discovery.png)

### Configuration

![CoSky-Configuration](./docs/CoSky-Configuration.png)

### CoSky-Mirror (Real-time synchronization of service instance change status)

> CoSky-Mirror is like a mirror placed between Nacos and CoSky to build a unified service discovery platform.

![CoSky-Mirror](./docs/CoSky-Mirror.png)

![CoSky-Mirror-Unified](./docs/CoSky-Mirror-Unified.png)

## Examples

[Service Consumer --RPC--> Service Provider Examples](https://github.com/Ahoo-Wang/CoSky/tree/main/examples)

## Installation

### Gradle

> Kotlin DSL

``` kotlin
    val coskyVersion = "1.2.10";
    implementation("me.ahoo.cosky:spring-cloud-starter-cosky-config:${coskyVersion}")
    implementation("me.ahoo.cosky:spring-cloud-starter-cosky-discovery:${coskyVersion}")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer:3.0.3")
```

### Maven

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <artifactId>demo</artifactId>
    <properties>
        <cosky.version>1.2.10</cosky.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>me.ahoo.cosky</groupId>
            <artifactId>spring-cloud-starter-cosky-config</artifactId>
            <version>${cosky.version}</version>
        </dependency>
        <dependency>
            <groupId>me.ahoo.cosky</groupId>
            <artifactId>spring-cloud-starter-cosky-discovery</artifactId>
            <version>${cosky.version}</version>
        </dependency>
        <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-loadbalancer</artifactId>
          <version>3.0.3</version>
        </dependency>
    </dependencies>

</project>
```

### bootstrap.yaml (Spring-Cloud-Config)

```yaml
spring:
  application:
    name: ${service.name:cosky-rest-api}
  cloud:
    cosky:
      namespace: ${cosky.namespace:cosky-{system}}
      config:
        config-id: ${spring.application.name}.yaml
      redis:
        mode: ${cosky.redis.mode:standalone}
        url: ${cosky.redis.uri:redis://localhost:6379}
logging:
  file:
    name: logs/${spring.application.name}.log
```

## REST-API Server (``Optional``)

### Installation REST-API Server

#### Option 1：Download the executable file

> Download [cosky-rest-api-server](https://github.com/Ahoo-Wang/cosky/releases/download/1.2.10/cosky-rest-api-1.2.10.tar)

> tar *cosky-rest-api-1.2.10.tar*

```shell
cd cosky-rest-api-1.2.10
# Working directory: cosky-rest-api-1.2.10
bin/cosky-rest-api --server.port=8080 --cosky.redis.uri=redis://localhost:6379
```

#### Option 2：Run On Docker

```shell
docker pull ahoowang/cosky-rest-api:1.2.10
docker run --name cosky-rest-api -d -p 8080:8080 --link redis -e COSKY_REDIS_URI=redis://redis:6379  ahoowang/cosky-rest-api:1.2.10
```

#### Option 3：Run On Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cosky-rest-api
  labels:
    app: cosky-rest-api
spec:
  replicas: 1
  selector:
    matchLabels:
      app: cosky-rest-api
  template:
    metadata:
      labels:
        app: cosky-rest-api
    spec:
      containers:
        - env:
            - name: COSKY_REDIS_MODE
              value: standalone
            - name: COSKY_REDIS_URI
              value: redis://redis-uri:6379
          image: ahoowang/cosky-rest-api:1.2.10
          name: cosky-rest-api
          ports:
            - containerPort: 8080
              protocol: TCP
          resources:
            limits:
              cpu: "1"
              memory: 1280Mi
            requests:
              cpu: 250m
              memory: 1024Mi
          volumeMounts:
            - mountPath: /etc/localtime
              name: volume-localtime
      volumes:
        - hostPath:
            path: /etc/localtime
            type: ""
          name: volume-localtime

---
apiVersion: v1
kind: Service
metadata:
  name: cosky-rest-api
  labels:
    app: cosky-rest-api
spec:
  selector:
    app: cosky-rest-api
  ports:
    - name: rest
      port: 80
      protocol: TCP
      targetPort: 8080
```

### Dashboard

> [http://localhost:8080/dashboard](http://localhost:8080/dashboard)

![dashboard-dashboard](./docs/dashboard-dashboard.png)

### Service dependent topology

![dashboard-topology](./docs/dashboard-topology.png)

### Role-based access control(RBAC)

- cosky: Reserved username, super user, with the highest authority. When the application is launched for the first time, the super user (cosky) password will be initialized and printed on the console. Don't worry if you forget your password, you can configure `enforce-init-super-user: true`, *CoSky* will help you reinitialize the password and print it on the console.

```log
---------------- ****** CoSky -  init super user:[cosky] password:[6TrmOux4Oj] ****** ----------------
```

- admin: Reserved roles, super administrator roles, have all permissions, a user can be bound to multiple roles, and a role can be bound to multiple resource operation permissions.
- Permission control granularity is namespace, read and write operations

#### Role Permissions

![dashboard-role](./docs/dashboard-role.png)

##### Add Role

![dashboard-role-add](./docs/dashboard-role-add.png)

#### User Management

![dashboard-user](./docs/dashboard-user.png)

##### Add User

![dashboard-user-add](./docs/dashboard-user-add.png)

#### Audit Log

![dashboard-audit-log](./docs/dashboard-audit-log.png)

#### Namespace

![dashboard-namespace](./docs/dashboard-namespace.png)

#### Config

![dashboard-config](./docs/dashboard-config.png)

##### Edit configuration

![dashboard-config-edit](./docs/dashboard-config-edit.png)

##### Rollback configuration

![dashboard-config-rollback](./docs/dashboard-config-rollback.png)

##### Import configuration from Nacos

![dashboard-config-import](./docs/dashboard-config-import.gif)

#### Service

![dashboard-service](./docs/dashboard-service.png)

##### Edit Service Instance

![dashboard-service-edit](./docs/dashboard-service-edit.png)

### REST-API

> http://localhost:8080/swagger-ui/index.html#/

#### Namespace

![rest-api-namespace](./docs/rest-api-namespace.png)

- /v1/namespaces
    - GET
- /v1/namespaces/{namespace}
    - PUT
    - GET
- /v1/namespaces/current
    - GET
- /v1/namespaces/current/{namespace}
    - PUT

#### Config

![rest-api-config](./docs/rest-api-config.png)

- /v1/namespaces/{namespace}/configs
    - GET
- /v1/namespaces/{namespace}/configs/{configId}
    - GET
    - PUT
    - DELETE
- /v1/namespaces/{namespace}/configs/{configId}/versions
    - GET
- /v1/namespaces/{namespace}/configs/{configId}/versions/{version}
    - GET
- /v1/namespaces/{namespace}/configs/{configId}/to/{targetVersion}
    - PUT

#### Service

![rest-api-service](./docs/rest-api-service.png)

- /v1/namespaces/{namespace}/services/
    - GET
- /v1/namespaces/{namespace}/services/{serviceId}/instances
    - GET
    - PUT
- /v1/namespaces/{namespace}/services/{serviceId}/instances/{instanceId}
    - DELETE
- /v1/namespaces/{namespace}/services/{serviceId}/instances/{instanceId}/metadata
    - PUT
- /v1/namespaces/{namespace}/services/{serviceId}/lb
    - GET

## JMH-Benchmark

- The development notebook : MacBook Pro (M1)
- All benchmark tests are carried out on the development notebook.
- Deploying Redis on the development notebook.

### ConfigService

``` shell
gradle cosky-config:jmh
# or
java -jar cosky-config/build/libs/cosky-config-1.2.10-jmh.jar -bm thrpt -t 25 -wi 1 -rf json -f 1
```

```
Benchmark                                          Mode  Cnt          Score   Error  Units
ConsistencyRedisConfigServiceBenchmark.getConfig  thrpt       256733987.827          ops/s
RedisConfigServiceBenchmark.getConfig             thrpt          241787.679          ops/s
RedisConfigServiceBenchmark.setConfig             thrpt          140461.112          ops/s
```

### ServiceDiscovery

``` shell
gradle cosky-discovery:jmh
# or
java -jar cosky-discovery/build/libs/cosky-discovery-1.2.10-jmh.jar -bm thrpt -t 25 -wi 1 -rf json -f 1
```

```
Benchmark                                                Mode  Cnt          Score   Error  Units
ConsistencyRedisServiceDiscoveryBenchmark.getInstances  thrpt        76621729.048          ops/s
ConsistencyRedisServiceDiscoveryBenchmark.getServices   thrpt       455760632.346          ops/s
RedisServiceDiscoveryBenchmark.getInstances             thrpt          226909.985          ops/s
RedisServiceDiscoveryBenchmark.getServices              thrpt          304979.150          ops/s
RedisServiceRegistryBenchmark.deregister                thrpt          255305.648          ops/s
RedisServiceRegistryBenchmark.register                  thrpt          110664.160          ops/s
RedisServiceRegistryBenchmark.renew                     thrpt          210960.325          ops/s
```
