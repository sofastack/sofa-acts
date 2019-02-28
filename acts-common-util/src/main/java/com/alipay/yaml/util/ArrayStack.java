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
package com.alipay.yaml.util;

import java.util.ArrayList;

public class ArrayStack<T> {
    private ArrayList<T> stack;

    /**
     * Constructor.
     *
     * @param initSize the init size
     */
    public ArrayStack(int initSize) {
        stack = new ArrayList<T>(initSize);
    }

    /**
     * Push.
     *
     * @param obj the obj
     */
    public void push(T obj) {
        stack.add(obj);
    }

    /**
     * Pop t.
     *
     * @return the t
     */
    public T pop() {
        return stack.remove(stack.size() - 1);
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Clear.
     */
    public void clear() {
        stack.clear();
    }
}
