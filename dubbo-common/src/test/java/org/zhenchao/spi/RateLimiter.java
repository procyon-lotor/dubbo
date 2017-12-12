package org.zhenchao.spi;

import com.alibaba.dubbo.common.extension.SPI;

/**
 * @author zhenchao.wang 2016-09-30 09:40
 * @version 1.0.0
 */
@SPI
public interface RateLimiter {

    boolean reject();

}
