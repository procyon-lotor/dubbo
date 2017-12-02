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

整体上按照分层结构进行分包，与分层的不同点在于：

- container 为服务容器，用于部署运行服务，没有在层中画出。
- protocol 层和 proxy 层都放在 rpc 模块中，这两层是 rpc 的核心，在不需要集群也就是只有一个提供者时，可以只使用这两层完成 rpc 调用。
- transport 层和 exchange 层都放在 remoting 模块中，为 rpc 调用的通讯基础。
- serialize 层放在 common 模块中，以便更大程度复用。

架构分层：

![image](http://dubbo.io/books/dubbo-dev-book/sources/images/dubbo-framework.jpg)

> - 图中左边淡蓝背景的为服务消费方使用的接口，右边淡绿色背景的为服务提供方使用的接口，位于中轴线上的为双方都用到的接口。
> - 图中从下至上分为十层，各层均为单向依赖，右边的黑色箭头代表层之间的依赖关系，每一层都可以剥离上层被复用，其中，Service 和 Config 层为 API，其它各层均为 SPI。
> - 图中绿色小块的为扩展接口，蓝色小块为实现类，图中只显示用于关联各层的实现类。
> - 图中蓝色虚线为初始化过程，即启动时组装链，红色实线为方法调用过程，即运行时调时链，紫色三角箭头为继承，可以把子类看作父类的同一个节点，线上的文字为调用的方法。

- __config 配置层__：对外配置接口，以 ServiceConfig, ReferenceConfig 为中心，可以直接初始化配置类，也可以通过 spring 解析配置生成配置类
- __proxy 服务代理层__：服务接口透明代理，生成服务的客户端 Stub 和服务器端 Skeleton, 以 ServiceProxy 为中心，扩展接口为 ProxyFactory
- __registry 注册中心层__：封装服务地址的注册与发现，以服务 URL 为中心，扩展接口为 RegistryFactory, Registry, RegistryService
- __cluster 路由层__：封装多个提供者的路由及负载均衡，并桥接注册中心，以 Invoker 为中心，扩展接口为 Cluster, Directory, Router, LoadBalance
- __monitor 监控层__：RPC 调用次数和调用时间监控，以 Statistics 为中心，扩展接口为 MonitorFactory, Monitor, MonitorService
- __protocol 远程调用层__：封将 RPC 调用，以 Invocation, Result 为中心，扩展接口为 Protocol, Invoker, Exporter
- __exchange 信息交换层__：封装请求响应模式，同步转异步，以 Request, Response 为中心，扩展接口为 Exchanger, ExchangeChannel, ExchangeClient, ExchangeServer
- __transport 网络传输层__：抽象 mina 和 netty 为统一接口，以 Message 为中心，扩展接口为 Channel, Transporter, Client, Server, Codec
- __serialize 数据序列化层__：可复用的一些工具，扩展接口为 Serialization, ObjectInput, ObjectOutput, ThreadPool