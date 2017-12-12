package org.zhenchao.spi;

/**
 * @author zhenchao.wang 2016-09-30 09:42
 * @version 1.0.0
 */
public class UserRateLimiter implements RateLimiter {

    @Override
    public boolean reject() {
        System.out.println("do user visit rate upper limit check");
        return false;
    }

}
