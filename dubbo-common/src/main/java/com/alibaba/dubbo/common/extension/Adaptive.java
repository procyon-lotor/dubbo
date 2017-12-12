/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.dubbo.common.extension;

import com.alibaba.dubbo.common.URL;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在 {@link ExtensionLoader} 生成 Extension 的 Adaptive Instance 时，为 {@link ExtensionLoader} 提供信息。
 *
 * @author ding.lid
 * @export
 * @see ExtensionLoader
 * @see URL
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Adaptive {

    /**
     * 从{@link URL} 的 Key 对应的 Value 作为要 Adapt 成的 Extension 名称。
     *
     * 如果 {@link URL} 这些 Key 都没有 Value，使用缺省的扩展（在接口的 {@link SPI} 中设定的值）。
     *
     * 比如 String[] {"key1", "key2"} 表示：
     * 1. 先在 URL 上找 key1 的 Value 作为要 Adapt 成的 Extension 名称；
     * 2. key1 没有 Value，则使用 key2 的 Value 作为要 Adapt 成的 Extension 名称。
     * 3. key2 没有 Value，使用缺省的扩展。
     * 4. 如果没有设定缺省扩展，则方法调用会抛出{@link IllegalStateException}。
     *
     * 如果不设置则缺省使用 Extension 接口类名的点分隔小写字串
     * 即对于 Extension 接口 {@code com.alibaba.dubbo.xxx.YyyInvokerWrapper} 的缺省值为 String[] {"yyy.invoker.wrapper"}
     *
     * @see SPI#value()
     */
    String[] value() default {};

}