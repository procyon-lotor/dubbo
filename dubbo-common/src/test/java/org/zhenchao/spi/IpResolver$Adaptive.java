package org.zhenchao.spi;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;

/**
 * @author zhenchao.wang 2017-12-16 10:57
 * @version 1.0.0
 */
public class IpResolver$Adaptive implements IpResolver {

    public java.lang.String resolve(URL arg0) {
        if (arg0 == null) throw new IllegalArgumentException("url == null");
        URL url = arg0;
        String extName = url.getParameter("resolver", "local");
        if (extName == null) throw new IllegalStateException("Fail to get extension(org.zhenchao.spi.IpResolver) name from url(" + url.toString() + ") use keys([resolver])");
        IpResolver extension = ExtensionLoader.getExtensionLoader(IpResolver.class).getExtension(extName);
        return extension.resolve(arg0);
    }

}