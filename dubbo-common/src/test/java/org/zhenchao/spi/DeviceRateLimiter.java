package org.zhenchao.spi;

import com.alibaba.dubbo.common.extension.Activate;

/**
 * @author zhenchao.wang 2016-09-30 09:42
 * @version 1.0.0
 */
@Activate
public class DeviceRateLimiter implements RateLimiter {

    @Override
    public boolean reject() {
        System.out.println("do device visit rate upper limit check");
        return false;
    }

}
