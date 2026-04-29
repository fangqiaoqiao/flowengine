# FlowEngine 转发平台

基于 Spring Boot 的可配置化流程转发引擎。每个业务组件（Component）包含多个顺序执行的环节（Node），支持动态配置每个环节的输入/输出参数映射，利用 Redis 存储运行时上下文，并提供可视化管理页面。

## 核心特性

- **组件化设计**：每个业务场景对应一个组件（如 `SQ007070`），组件内可按顺序配置多个环节（如 `SQ01` → `SQ21` → ...）
- **上下文存储**：使用 Redis 存储每个调用链的完整上下文（所有环节的入参/出参），后续环节可以根据“环节ID.字段名”获取前置环节的输出值
- **最新 keyId 机制**：每次调用场景入口都会生成新的唯一 keyId（30分钟过期），后续环节必须携带该 keyId 且只能使用最新生成的 keyId，防止旧链继续执行
- **灵活的参数映射**：支持从请求体、上下文（SpEL 表达式）、常量等多种来源绑定入参；出参支持 JSONPath/SpEL 表达式提取处理器结果
- **可扩展的处理器**：内置 HTTP 转发、Groovy 脚本、空处理器，支持通过接口扩展新类型
- **管理后台**：网页端可视化管理组件、环节、出入参配置，实时生效

## 技术栈

- **后端**：Spring Boot 2.7.18, Spring Data JPA, Oracle, Redis (Lettuce)
- **前端**：原生 HTML + JavaScript (无框架依赖，简单易改)
- **脚本引擎**：Groovy（支持动态脚本环节）
- **表达式解析**：Spring Expression Language (SpEL)

## 快速开始

### 环境要求

- JDK 11+
- Oracle 11g 或更高版本
- Redis 5.0+

### 数据库初始化

执行 `src/main/resources/db/schema.sql` 创建以下表：

- `component`：组件定义
- `node`：环节定义（包含顺序、处理器类型、处理器配置）
- `node_input_param`：环节输入参数映射规则
- `node_output_param`：环节输出参数映射规则

可选执行 `data.sql` 插入示例数据。

### 配置修改

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//localhost:1521/XEPDB1
    username: flowengine
    password: your_password
    driver-class-name: oracle.jdbc.OracleDriver
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8