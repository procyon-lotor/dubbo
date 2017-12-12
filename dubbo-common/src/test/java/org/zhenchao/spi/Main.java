package org.zhenchao.spi;

import com.alibaba.dubbo.common.extension.ExtensionLoader;

/**
 * @author zhenchao.wang 2016-09-30 09:47
 * @version 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        ExtensionLoader<RateLimiter> extensionLoader = ExtensionLoader.getExtensionLoader(RateLimiter.class);
        for (final String name : extensionLoader.getSupportedExtensions()) {
            RateLimiter limiter = extensionLoader.getExtension(name);
            limiter.reject();
        }
    }

}
