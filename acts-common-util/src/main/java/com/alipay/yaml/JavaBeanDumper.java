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

import java.io.StringWriter;
import java.io.Writer;

import com.alipay.yaml.DumperOptions.FlowStyle;
import com.alipay.yaml.introspector.BeanAccess;
import com.alipay.yaml.nodes.Tag;
import com.alipay.yaml.representer.Representer;

/**
 * Convenience utility to serialize JavaBeans.
 * 
 * @deprecated use Yaml.dumpAs(data, Tag.MAP) instead
 */
public class JavaBeanDumper {
    private boolean          useGlobalTag;
    private FlowStyle        flowStyle;
    private DumperOptions    options;
    private Representer      representer;
    private final BeanAccess beanAccess;

    /**
     * Create Dumper for JavaBeans
     * 
     * @param useGlobalTag
     *            true to emit the global tag with the class name
     */
    public JavaBeanDumper(boolean useGlobalTag, BeanAccess beanAccess) {
        this.useGlobalTag = useGlobalTag;
        this.beanAccess = beanAccess;
        this.flowStyle = FlowStyle.BLOCK;
    }

    public JavaBeanDumper(boolean useGlobalTag) {
        this(useGlobalTag, BeanAccess.DEFAULT);
    }

    public JavaBeanDumper(BeanAccess beanAccess) {
        this(false, beanAccess);
    }

    /**
     * Create Dumper for JavaBeans. Use "tag:yaml.org,2002:map" as the root tag.
     */
    public JavaBeanDumper() {
        this(BeanAccess.DEFAULT);
    }

    public JavaBeanDumper(Representer representer, DumperOptions options) {
        if (representer == null) {
            throw new NullPointerException("Representer must be provided.");
        }
        if (options == null) {
            throw new NullPointerException("DumperOptions must be provided.");
        }
        this.options = options;
        this.representer = representer;
        this.beanAccess = null; // bean access in not used if representer
        // supplied
    }

    /**
     * Serialize JavaBean
     * 
     * @param data
     *            JavaBean instance to serialize
     * @param output
     *            destination
     */
    public void dump(Object data, Writer output) {
        DumperOptions doptions;
        if (this.options == null) {
            doptions = new DumperOptions();
            if (!useGlobalTag) {
                doptions.setExplicitRoot(Tag.MAP);
            }
            doptions.setDefaultFlowStyle(flowStyle);
        } else {
            doptions = this.options;
        }
        Representer repr;
        if (this.representer == null) {
            repr = new Representer();
            repr.getPropertyUtils().setBeanAccess(beanAccess);
        } else {
            repr = this.representer;
        }
        Yaml dumper = new Yaml(repr, doptions);
        dumper.dump(data, output);
    }

    /**
     * Serialize JavaBean
     * 
     * @param data
     *            JavaBean instance to serialize
     * @return serialized YAML document
     */
    public String dump(Object data) {
        StringWriter buffer = new StringWriter();
        dump(data, buffer);
        return buffer.toString();
    }

    public boolean isUseGlobalTag() {
        return useGlobalTag;
    }

    public void setUseGlobalTag(boolean useGlobalTag) {
        this.useGlobalTag = useGlobalTag;
    }

    public FlowStyle getFlowStyle() {
        return flowStyle;
    }

    public void setFlowStyle(FlowStyle flowStyle) {
        this.flowStyle = flowStyle;
    }
}
