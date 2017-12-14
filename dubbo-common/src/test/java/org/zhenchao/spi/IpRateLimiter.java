package org.zhenchao.spi;

import com.alibaba.dubbo.common.URL;

/**
 * @author zhenchao.wang 2016-09-30 09:43
 * @version 1.0.0
 */
public class IpRateLimiter implements RateLimiter {

    private IpResolver ipResolver;

    @Override
    public boolean reject() {
        System.out.println("do ip visit rate upper limit check");
        ipResolver.resolve(URL.valueOf("dubbo://127.0.0.1:2880/IpResolver/resolve?resolver=local"));
        return false;
    }

    public IpRateLimiter setIpResolver(IpResolver ipResolver) {
        this.ipResolver = ipResolver;
        return this;
    }
}
