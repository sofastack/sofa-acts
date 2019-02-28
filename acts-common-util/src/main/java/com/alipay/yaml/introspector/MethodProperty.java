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
package com.alipay.yaml.introspector;

import java.beans.PropertyDescriptor;

/**
 * <p>
 * A <code>MethodProperty</code> is a <code>Property</code> which is accessed
 * through accessor methods (setX, getX). It is possible to have a
 * <code>MethodProperty</code> which has only setter, only getter, or both. It
 * is not possible to have a <code>MethodProperty</code> which has neither
 * setter nor getter.
 * </p>
 */
public class MethodProperty extends GenericProperty {

    private final PropertyDescriptor property;
    private final boolean            readable;
    private final boolean            writable;

    public MethodProperty(PropertyDescriptor property) {
        super(property.getName(), property.getPropertyType(),
            property.getReadMethod() == null ? null : property.getReadMethod()
                .getGenericReturnType());
        this.property = property;
        this.readable = property.getReadMethod() != null;
        this.writable = property.getWriteMethod() != null;
    }

    @Override
    public void set(Object object, Object value) throws Exception {
        property.getWriteMethod().invoke(object, value);
    }

    @Override
    public Object get(Object object) {
        try {
            property.getReadMethod().setAccessible(true);// issue 50
            return property.getReadMethod().invoke(object);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public boolean isReadable() {
        return readable;
    }
}