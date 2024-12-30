# Virtual Thread Demo

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-blue.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

这是一个演示Java 21虚拟线程（Virtual Thread）特性的Spring Boot项目。通过对比虚拟线程、平台线程和串行执行的性能差异，帮助开发者更好地理解和使用虚拟线程。

## 项目特性

- 基于Spring Boot 3.4.1和Java 21
- 实现了三种任务处理模式的对比：
  - 虚拟线程异步处理
  - 传统平台线程池处理
  - 串行同步处理
- 提供了性能对比API
- 包含完整的异步配置和异常处理
- 支持优雅关闭线程池

## 技术栈

- Java 21
- Spring Boot 3.4.1
- Spring Web
- Lombok
- Spring DevTools
- JUnit 5
- Gradle 8.5+
- SLF4J + Logback

## 快速开始

### 环境要求

- JDK 21+
- Gradle 8.5+

### 构建和运行

1. 克隆项目
```bash
git clone https://github.com/wldyj/virtual_thread.git
cd virtual_thread
```

2. 编译打包
```bash
./gradlew build
```

3. 运行应用
```bash
java -jar build/libs/virtual-thread-0.0.1-SNAPSHOT.jar
```

### API接口

项目提供了以下REST API接口：

1. 虚拟线程处理
```
GET /api/virtual-thread/executor
```

2. 平台线程处理
```
GET /api/virtual-thread/platform-thread
```

3. 串行处理
```
GET /api/virtual-thread/sequential
```

每个接口都会返回处理时间和结果数量的统计信息。

## 核心配置

项目的核心配置在`AsyncConfig`类中，主要包括：

- 虚拟线程池配置
- 异步任务异常处理
- 线程池监控
- 优雅关闭机制

## 贡献指南

欢迎提交Issue和Pull Request。在提交PR之前，请确保：

1. 代码符合Java代码规范
2. 添加了必要的测试用例
3. 更新了相关文档

## 许可证

本项目采用MIT许可证 - 查看[LICENSE](LICENSE)文件了解详情。

## 联系方式

如果您有任何问题或建议，欢迎通过以下方式联系：

- GitHub Issues: [https://github.com/wldyj/virtual_thread/issues](https://github.com/wldyj/virtual_thread/issues)
