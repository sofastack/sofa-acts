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

/**
 * <p>
 * A <code>Property</code> represents a single member variable of a class,
 * possibly including its accessor methods (getX, setX). The name stored in this
 * class is the actual name of the property as given for the class, not an
 * alias.
 * </p>
 * 
 * <p>
 * Objects of this class have a total ordering which defaults to ordering based
 * on the name of the property.
 * </p>
 */
public abstract class Property implements Comparable<Property> {

    private final String   name;
    private final Class<?> type;

    /**
     * Constructor.
     *
     * @param name the name
     * @param type the type
     */
    public Property(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Get actual type arguments class [ ].
     *
     * @return the class [ ]
     */
    abstract public Class<?>[] getActualTypeArguments();

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return getName() + " of " + getType();
    }

    /**
     * Compare to int.
     *
     * @param o the o
     * @return the int
     */
    public int compareTo(Property o) {
        return name.compareTo(o.name);
    }

    /**
     * Is writable boolean.
     *
     * @return the boolean
     */
    public boolean isWritable() {
        return true;
    }

    /**
     * Is readable boolean.
     *
     * @return the boolean
     */
    public boolean isReadable() {
        return true;
    }

    /**
     * Set.
     *
     * @param object the object
     * @param value the value
     * @throws Exception the exception
     */
    abstract public void set(Object object, Object value) throws Exception;

    /**
     * Get object.
     *
     * @param object the object
     * @return the object
     */
    abstract public Object get(Object object);

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode();
    }

    /**
     * Equals boolean.
     *
     * @param other the other
     * @return the boolean
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Property) {
            Property p = (Property) other;
            return name.equals(p.getName()) && type.equals(p.getType());
        }
        return false;
    }
}