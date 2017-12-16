package org.zhenchao.spi;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;

import java.util.List;

/**
 * @author zhenchao.wang 2016-09-30 09:47
 * @version 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        ExtensionLoader<RateLimiter> extensionLoader = ExtensionLoader.getExtensionLoader(RateLimiter.class);
        List<RateLimiter> rateLimiters = extensionLoader.getActivateExtension(URL.valueOf("http://www.zhenchao.org"), "");
        for (final RateLimiter limiter : rateLimiters) {
            limiter.reject();
        }
        /*for (final String name : extensionLoader.getSupportedExtensions()) {
            RateLimiter limiter = extensionLoader.getExtension(name);
            limiter.reject();
        }*/
    }

}
