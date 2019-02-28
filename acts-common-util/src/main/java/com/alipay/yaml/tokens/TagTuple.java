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
package com.alipay.yaml.tokens;

public final class TagTuple {
    private final String handle;
    private final String suffix;

    /**
     * Constructor.
     *
     * @param handle the handle
     * @param suffix the suffix
     */
    public TagTuple(String handle, String suffix) {
        if (suffix == null) {
            throw new NullPointerException("Suffix must be provided.");
        }
        this.handle = handle;
        this.suffix = suffix;
    }

    /**
     * Gets handle.
     *
     * @return the handle
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets suffix.
     *
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }
}
