package org.zhenchao.spi;

/**
 * @author zhenchao.wang 2016-09-30 09:43
 * @version 1.0.0
 */
public class IpRateLimiter implements RateLimiter {

    @Override
    public boolean reject() {
        System.out.println("do ip visit rate upper limit check");
        return false;
    }
}
