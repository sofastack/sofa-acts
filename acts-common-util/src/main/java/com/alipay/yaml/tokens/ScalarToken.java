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

public final class ScalarToken extends Token {
    private final String  value;
    private final boolean plain;
    private final char    style;

    /**
     * Constructor.
     *
     * @param value the value
     * @param startMark the start mark
     * @param endMark the end mark
     * @param plain the plain
     */
    public ScalarToken(String value, Mark startMark, Mark endMark, boolean plain) {
        this(value, plain, startMark, endMark, (char) 0);
    }

    /**
     * Constructor.
     *
     * @param value the value
     * @param plain the plain
     * @param startMark the start mark
     * @param endMark the end mark
     * @param style the style
     */
    public ScalarToken(String value, boolean plain, Mark startMark, Mark endMark, char style) {
        super(startMark, endMark);
        this.value = value;
        this.plain = plain;
        this.style = style;
    }

    /**
     * Gets plain.
     *
     * @return the plain
     */
    public boolean getPlain() {
        return this.plain;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Gets style.
     *
     * @return the style
     */
    public char getStyle() {
        return this.style;
    }

    @Override
    protected String getArguments() {
        return "value=" + value + ", plain=" + plain + ", style=" + style;
    }

    /**
     * Gets token id.
     *
     * @return the token id
     */
    @Override
    public Token.ID getTokenId() {
        return ID.Scalar;
    }
}
