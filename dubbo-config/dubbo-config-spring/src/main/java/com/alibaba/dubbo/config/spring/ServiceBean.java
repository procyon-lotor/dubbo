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

package com.alibaba.dubbo.config.spring;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ServiceFactoryBean
 *
 * @author william.liangf
 * @export
 */
public class ServiceBean<T> extends ServiceConfig<T>
        implements InitializingBean, DisposableBean, ApplicationContextAware, ApplicationListener, BeanNameAware {

    private static final long serialVersionUID = 213195494150089726L;

    private static transient ApplicationContext SPRING_CONTEXT;

    private transient ApplicationContext applicationContext;

    private transient String beanName;

    private transient boolean supportedApplicationListener;

    public ServiceBean() {
        super();
    }

    public ServiceBean(Service service) {
        super(service);
    }

    public static ApplicationContext getSpringContext() {
        return SPRING_CONTEXT;
    }

    // org.springframework.context.support.ClassPathXmlApplicationContext
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        // 记录 applicationContext 对象到 set 集合中
        SpringExtensionFactory.addApplicationContext(applicationContext);
        if (applicationContext != null) {
            SPRING_CONTEXT = applicationContext;
            try {
                // 兼容 Spring2.0.1，获取并激活 addApplicationListener 方法
                Method method = applicationContext.getClass().getMethod("addApplicationListener", ApplicationListener.class);
                method.invoke(applicationContext, this);
                supportedApplicationListener = true;
            } catch (Throwable t) {
                if (applicationContext instanceof AbstractApplicationContext) {
                    try {
                        // 兼容Spring2.0.1
                        Method method = AbstractApplicationContext.class.getDeclaredMethod("addListener", ApplicationListener.class);
                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(applicationContext, this);
                        supportedApplicationListener = true;
                    } catch (Throwable t2) {
                    }
                }
            }
        }
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    /**
     * 实现了 org.springframework.context.ApplicationListener.onApplicationEvent 方法，订阅事件
     * 先完成 setApplicationContext 和 afterPropertiesSet 的执行，最后再来执行该方法
     *
     * @param event
     */
    public void onApplicationEvent(ApplicationEvent event) {
        if (ContextRefreshedEvent.class.getName().equals(event.getClass().getName())) {
            if (this.isDelay() && !this.isExported() && !this.isUnexported()) {
                if (logger.isInfoEnabled()) {
                    logger.info("The service ready on spring started. service: " + getInterface());
                }
                this.export();
            }
        }
    }

    /**
     * 是否延迟暴露
     *
     * @return
     */
    private boolean isDelay() {
        Integer delay = this.getDelay();
        ProviderConfig provider = this.getProvider();
        if (delay == null && provider != null) {
            delay = provider.getDelay();
        }
        // delay = -1, 延迟到Spring初始化完成后再暴露服务：(基于Spring的ContextRefreshedEvent事件触发暴露)
        return supportedApplicationListener && (delay == null || delay == -1);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public void afterPropertiesSet() throws Exception {

        // provider 未初始化
        if (this.getProvider() == null) {

            /*
             * 获取所有的 ProviderConfig 类型（及其子类型）
             * <dubbo:provider id="com.alibaba.dubbo.config.ProviderConfig" />
             */
            Map<String, ProviderConfig> providerConfigMap = applicationContext == null ?
                    null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ProviderConfig.class, false, false);

            if (providerConfigMap != null && providerConfigMap.size() > 0) {

                // 获取所有的 ProtocolConfig 类型（及其子类型）
                Map<String, ProtocolConfig> protocolConfigMap = applicationContext == null ?
                        null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ProtocolConfig.class, false, false);

                if ((protocolConfigMap == null || protocolConfigMap.size() == 0)
                        && providerConfigMap.size() > 1) { // 兼容旧版本
                    // 没有 protocol 配置，但是存在 provider 配置
                    List<ProviderConfig> providerConfigs = new ArrayList<ProviderConfig>();
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() != null && config.isDefault()) { // 如果是默认值
                            providerConfigs.add(config);
                        }
                    }
                    if (providerConfigs.size() > 0) {
                        this.setProviders(providerConfigs);
                    }
                } else {
                    ProviderConfig providerConfig = null;
                    for (ProviderConfig config : providerConfigMap.values()) {
                        if (config.isDefault() == null || config.isDefault()) {  // provider 是默认配置
                            if (providerConfig != null) {
                                // 保证只有一份默认值配置
                                throw new IllegalStateException("Duplicate provider configs: " + providerConfig + " and " + config);
                            }
                            providerConfig = config;
                        }
                    }
                    if (providerConfig != null) {
                        this.setProvider(providerConfig);
                    }
                }
            }
        }

        // application 未初始化，且 provider 或 provider 下的 application 未初始化
        if (this.getApplication() == null
                && (this.getProvider() == null || this.getProvider().getApplication() == null)) {

            /*
             * 获取所有的 ApplicationConfig 类型（及其子类型）
             * <dubbo:application name="dubbo-demo" id="dubbo-demo" />"
             */
            Map<String, ApplicationConfig> applicationConfigMap = applicationContext == null ?
                    null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ApplicationConfig.class, false, false);

            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (applicationConfig != null) {
                            // 保证只有一份默认配置
                            throw new IllegalStateException("Duplicate application configs: " + applicationConfig + " and " + config);
                        }
                        applicationConfig = config;
                    }
                }
                if (applicationConfig != null) {
                    this.setApplication(applicationConfig);
                }
            }
        }

        // module
        if (this.getModule() == null
                && (this.getProvider() == null || this.getProvider().getModule() == null)) {

            Map<String, ModuleConfig> moduleConfigMap = applicationContext == null ?
                    null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ModuleConfig.class, false, false);

            if (moduleConfigMap != null && moduleConfigMap.size() > 0) {
                ModuleConfig moduleConfig = null;
                for (ModuleConfig config : moduleConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (moduleConfig != null) {
                            throw new IllegalStateException("Duplicate module configs: " + moduleConfig + " and " + config);
                        }
                        moduleConfig = config;
                    }
                }
                if (moduleConfig != null) {
                    this.setModule(moduleConfig);
                }
            }
        }

        // registry
        if ((this.getRegistries() == null || this.getRegistries().size() == 0)
                && (this.getProvider() == null || this.getProvider().getRegistries() == null || this.getProvider().getRegistries().size() == 0)
                && (this.getApplication() == null || this.getApplication().getRegistries() == null || this.getApplication().getRegistries().size() == 0)) {

            Map<String, RegistryConfig> registryConfigMap = applicationContext == null ?
                    null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, RegistryConfig.class, false, false);

            if (registryConfigMap != null && registryConfigMap.size() > 0) {
                List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
                for (RegistryConfig config : registryConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        registryConfigs.add(config);
                    }
                }
                if (registryConfigs.size() > 0) {
                    super.setRegistries(registryConfigs);
                }
            }
        }

        // monitor
        if (this.getMonitor() == null
                && (this.getProvider() == null || this.getProvider().getMonitor() == null)
                && (this.getApplication() == null || this.getApplication().getMonitor() == null)) {

            Map<String, MonitorConfig> monitorConfigMap = applicationContext == null ?
                    null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MonitorConfig.class, false, false);

            if (monitorConfigMap != null && monitorConfigMap.size() > 0) {
                MonitorConfig monitorConfig = null;
                for (MonitorConfig config : monitorConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        if (monitorConfig != null) {
                            throw new IllegalStateException("Duplicate monitor configs: " + monitorConfig + " and " + config);
                        }
                        monitorConfig = config;
                    }
                }
                if (monitorConfig != null) {
                    this.setMonitor(monitorConfig);
                }
            }
        }

        // protocol
        if ((this.getProtocols() == null || this.getProtocols().size() == 0)
                && (this.getProvider() == null || this.getProvider().getProtocols() == null || this.getProvider().getProtocols().size() == 0)) {

            Map<String, ProtocolConfig> protocolConfigMap = applicationContext == null ?
                    null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ProtocolConfig.class, false, false);

            if (protocolConfigMap != null && protocolConfigMap.size() > 0) {
                List<ProtocolConfig> protocolConfigs = new ArrayList<ProtocolConfig>();
                for (ProtocolConfig config : protocolConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault()) {
                        protocolConfigs.add(config);
                    }
                }
                if (protocolConfigs.size() > 0) {
                    super.setProtocols(protocolConfigs);
                }
            }
        }

        // beanName = org.zhenchao.rpc.dubbo.api.CalculateService
        if (this.getPath() == null || this.getPath().length() == 0) {
            if (beanName != null && beanName.length() > 0
                    && this.getInterface() != null && this.getInterface().length() > 0
                    && beanName.startsWith(this.getInterface())) {
                this.setPath(beanName);
            }
        }

        // delay == null
        if (!this.isDelay()) {
            this.export();
        }
    }

    public void destroy() throws Exception {
        unexport();
    }

}