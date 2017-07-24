## Dubbo 源码解析

### 一. 各个模块的功能

- dubbo-common：公共基础模块，包括工具类和通用模型
- dubbo-remoting：远程通讯模块，相当于 dubbo 的协议实现
- dubbo-rpc：远程调用模块，抽象各种协议以及动态代理，只包含一对一的调用，不关心集群的管理
- dubbo-cluster：集群模块，将多个服务提供方伪装成一个提供方，包括负载均衡、容错，以及路由等，集群的地址列表可以是静态配置的，也可以是由注册中心下发的
- dubbo-registry：注册中心模块，基于注册中心下发地址的集群方式，以及对各种注册中心的抽象
- dubbo-monitor：监控模块，统计服务调用次数、调用时间，调用链跟踪
- dubbo-config：配置模块，dubbo 暴露对外使用的 API，从而让开发者基于配置的方式使用 dubbo
- dubbo-container：容器模块，standalone 容器，以 main 函数的方式加载 spring 启动