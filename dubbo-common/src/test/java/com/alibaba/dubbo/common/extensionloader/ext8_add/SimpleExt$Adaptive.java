package com.alibaba.dubbo.common.extensionloader.ext8_add;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.extensionloader.ext1.SimpleExt;

/**
 * @author zhenchao.wang 2017-12-12 13:42
 * @version 1.0.0
 */
public class SimpleExt$Adaptive implements SimpleExt {

    public java.lang.String echo(URL arg0, String arg1) {
        if (arg0 == null) throw new IllegalArgumentException("url == null");
        com.alibaba.dubbo.common.URL url = arg0;
        String extName = url.getParameter("simple.ext", "impl1");
        if (extName == null) {
            throw new IllegalStateException("Fail to get extension(com.alibaba.dubbo.common.extensionloader.ext1.SimpleExt) name from url(" + url.toString() + ") use keys([simple.ext])");
        }
        SimpleExt extension = ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension(extName);
        return extension.echo(arg0, arg1);
    }

    public java.lang.String yell(URL arg0, java.lang.String arg1) {
        if (arg0 == null) throw new IllegalArgumentException("url == null");
        com.alibaba.dubbo.common.URL url = arg0;
        String extName = url.getParameter("key1", url.getParameter("key2", "impl1"));
        if (extName == null) {
            throw new IllegalStateException("Fail to get extension(com.alibaba.dubbo.common.extensionloader.ext1.SimpleExt) name from url(" + url.toString() + ") use keys([key1, key2])");
        }
        SimpleExt extension = ExtensionLoader.getExtensionLoader(SimpleExt.class).getExtension(extName);
        return extension.yell(arg0, arg1);
    }

    public java.lang.String bang(com.alibaba.dubbo.common.URL arg0, int arg1) {
        throw new UnsupportedOperationException("method public abstract java.lang.String com.alibaba.dubbo.common.extensionloader.ext1.SimpleExt.bang(com.alibaba.dubbo.common.URL,int) of interface com.alibaba.dubbo.common.extensionloader.ext1.SimpleExt is not adaptive method!");
    }
}
