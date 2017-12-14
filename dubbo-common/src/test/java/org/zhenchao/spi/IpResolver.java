package org.zhenchao.spi;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;

/**
 * @author zhenchao.wang 2017-12-14 18:08
 * @version 1.0.0
 */
@SPI("local")
public interface IpResolver {

    @Adaptive("resolver")
    String resolve(URL url);

}
