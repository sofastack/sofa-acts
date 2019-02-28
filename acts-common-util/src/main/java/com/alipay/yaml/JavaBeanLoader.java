/**
 * Copyright (c) 2008-2013, http://www.snakeyaml.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.yaml;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import com.alipay.yaml.constructor.Constructor;
import com.alipay.yaml.introspector.BeanAccess;
import com.alipay.yaml.reader.UnicodeReader;
import com.alipay.yaml.representer.Representer;
import com.alipay.yaml.resolver.Resolver;

/**
 * Convenience utility to parse JavaBeans. When the YAML document contains a
 * global tag with the class definition like '!!com.package.MyBean' it is
 * ignored in favour of the runtime class <code>T</code>.
 * 
 * @deprecated use Yaml.loadAs() methods instead
 * @see <a
 *      href="http://www.artima.com/weblogs/viewpost.jsp?thread=208860">Reflecting
 *      generics</a>
 */
public class JavaBeanLoader<T> {
    private Yaml loader;

    public JavaBeanLoader(TypeDescription typeDescription) {
        this(typeDescription, BeanAccess.DEFAULT);
    }

    public JavaBeanLoader(TypeDescription typeDescription, BeanAccess beanAccess) {
        this(new LoaderOptions(typeDescription), beanAccess);
    }

    public JavaBeanLoader(LoaderOptions options, BeanAccess beanAccess) {
        if (options == null) {
            throw new NullPointerException("LoaderOptions must be provided.");
        }
        if (options.getRootTypeDescription() == null) {
            throw new NullPointerException("TypeDescription must be provided.");
        }
        Constructor constructor = new Constructor(options.getRootTypeDescription());
        loader = new Yaml(constructor, options, new Representer(), new DumperOptions(),
            new Resolver());
        loader.setBeanAccess(beanAccess);
    }

    public <S extends T> JavaBeanLoader(Class<S> clazz, BeanAccess beanAccess) {
        this(new TypeDescription(clazz), beanAccess);
    }

    public <S extends T> JavaBeanLoader(Class<S> clazz) {
        this(clazz, BeanAccess.DEFAULT);
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * JavaBean.
     * 
     * @param yaml
     *            YAML document
     * @return parsed JavaBean
     */
    @SuppressWarnings("unchecked")
    public T load(String yaml) {
        return (T) loader.load(new StringReader(yaml));
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * JavaBean.
     * 
     * @param io
     *            data to load from (BOM is respected and removed)
     * @return parsed JavaBean
     */
    @SuppressWarnings("unchecked")
    public T load(InputStream io) {
        return (T) loader.load(new UnicodeReader(io));
    }

    /**
     * Parse the first YAML document in a stream and produce the corresponding
     * Java object.
     * 
     * @param io
     *            data to load from (BOM must not be present)
     * @return parsed JavaBean
     */
    @SuppressWarnings("unchecked")
    public T load(Reader io) {
        return (T) loader.load(io);
    }

}
