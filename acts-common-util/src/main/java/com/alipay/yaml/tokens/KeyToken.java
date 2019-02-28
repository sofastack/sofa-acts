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

import com.alipay.yaml.error.Mark;

public final class KeyToken extends Token {

    /**
     * Constructor.
     *
     * @param startMark the start mark
     * @param endMark the end mark
     */
    public KeyToken(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    /**
     * Gets token id.
     *
     * @return the token id
     */
    @Override
    public Token.ID getTokenId() {
        return ID.Key;
    }
}
