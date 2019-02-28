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

import com.alipay.yaml.representer.Representer;

/**
 * @deprecated Dumper's functionality was moved to Yaml
 */
public final class Dumper {
    protected final Representer   representer;
    protected final DumperOptions options;

    public Dumper(Representer representer, DumperOptions options) {
        this.representer = representer;
        this.options = options;
    }

    public Dumper(DumperOptions options) {
        this(new Representer(), options);
    }

    public Dumper(Representer representer) {
        this(representer, new DumperOptions());
    }

    public Dumper() {
        this(new Representer(), new DumperOptions());
    }
}
