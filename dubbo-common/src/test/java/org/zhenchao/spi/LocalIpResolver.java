package org.zhenchao.spi;

import com.alibaba.dubbo.common.URL;

/**
 * @author zhenchao.wang 2017-12-14 18:09
 * @version 1.0.0
 */
public class LocalIpResolver implements IpResolver {

    @Override
    public String resolve(URL url) {
        System.out.println("Resolve ip by local resolver.");
        return "";
    }

}
