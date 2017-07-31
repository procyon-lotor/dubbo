/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.dubbo.config.spring.schema;

import com.alibaba.dubbo.common.Version;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * DubboNamespaceHandler
 *
 * <dubbo:service/> 服务配置，用于暴露一个服务，定义服务的元信息，一个服务可以用多个协议暴露，一个服务也可以注册到多个注册中心。
 * <dubbo:reference/> 引用配置，用于创建一个远程服务代理，一个引用可以指向多个注册中心。
 * <dubbo:protocol/> 协议配置，用于配置提供服务的协议信息，协议由提供方指定，消费方被动接受。
 * <dubbo:application/> 应用配置，用于配置当前应用信息，不管该应用是提供者还是消费者。
 * <dubbo:module/> 模块配置，用于配置当前模块信息，可选。
 * <dubbo:registry/> 注册中心配置，用于配置连接注册中心相关信息。
 * <dubbo:monitor/> 监控中心配置，用于配置连接监控中心相关信息，可选。
 * <dubbo:provider/> 提供方的缺省值，当ProtocolConfig和ServiceConfig某属性没有配置时，采用此缺省值，可选。
 * <dubbo:consumer/> 消费方缺省配置，当ReferenceConfig某属性没有配置时，采用此缺省值，可选。
 * <dubbo:method/> 方法配置，用于ServiceConfig和ReferenceConfig指定方法级的配置信息。
 * <dubbo:argument/> 用于指定方法参数配置。
 *
 * @author william.liangf
 * @export
 */
public class DubboNamespaceHandler extends NamespaceHandlerSupport {

    static {
        // 保证容器中只有一份 DubboNamespaceHandler 定义
        Version.checkDuplicate(DubboNamespaceHandler.class);
    }

    // 注册对应的标签解析器
    public void init() {
        // <dubbo:application/> 配置应用信息
        this.registerBeanDefinitionParser("application", new DubboBeanDefinitionParser(ApplicationConfig.class, true));

        // <dubbo:module/> 模块信息配置
        this.registerBeanDefinitionParser("module", new DubboBeanDefinitionParser(ModuleConfig.class, true));

        // <dubbo:registry/> 注册中心配置
        // 如果有多个不同的注册中心，可以声明多个<dubbo:registry>标签，并在<dubbo:service>或<dubbo:reference>的registry属性指定使用的注册中心
        this.registerBeanDefinitionParser("registry", new DubboBeanDefinitionParser(RegistryConfig.class, true));

        // <dubbo:monitor/> 监控中心配置
        this.registerBeanDefinitionParser("monitor", new DubboBeanDefinitionParser(MonitorConfig.class, true));

        // <dubbo:provider/> 服务提供者默认值配置
        // 用于为 <dubbo:service/> 和 <dubbo:protocol/> 提供配置默认值
        this.registerBeanDefinitionParser("provider", new DubboBeanDefinitionParser(ProviderConfig.class, true));

        // <dubbo:consumer/> 服务消费者默认值配置
        // 用于为 <dubbo:reference/> 标签提供默认值
        this.registerBeanDefinitionParser("consumer", new DubboBeanDefinitionParser(ConsumerConfig.class, true));

        // <dubbo:protocol/> 服务提供者协议配置
        // 如果需要支持多协议，可以声明多个<dubbo:protocol>标签，并在<dubbo:service>中通过protocol属性指定使用的协议
        this.registerBeanDefinitionParser("protocol", new DubboBeanDefinitionParser(ProtocolConfig.class, true));

        // <dubbo:service/> 暴露的服务接口, 定义服务的元信息
        this.registerBeanDefinitionParser("service", new DubboBeanDefinitionParser(ServiceBean.class, true));

        // <dubbo:reference/> 服务消费者引用服务配置
        // 用于创建一个远程服务代理，一个引用可以指向多个注册中心
        this.registerBeanDefinitionParser("reference", new DubboBeanDefinitionParser(ReferenceBean.class, false));


        this.registerBeanDefinitionParser("annotation", new DubboBeanDefinitionParser(AnnotationBean.class, true));
    }

}