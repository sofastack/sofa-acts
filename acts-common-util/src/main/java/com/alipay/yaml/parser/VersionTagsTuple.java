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
package com.alipay.yaml.parser;

import java.util.Map;

import com.alipay.yaml.DumperOptions.Version;

/**
 * Store the internal state for directives
 */
class VersionTagsTuple {
    private Version             version;
    private Map<String, String> tags;

    /**
     * Constructor.
     *
     * @param version the version
     * @param tags the tags
     */
    public VersionTagsTuple(Version version, Map<String, String> tags) {
        this.version = version;
        this.tags = tags;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Gets tags.
     *
     * @return the tags
     */
    public Map<String, String> getTags() {
        return tags;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return String.format("VersionTagsTuple<%s, %s>", version, tags);
    }
}
