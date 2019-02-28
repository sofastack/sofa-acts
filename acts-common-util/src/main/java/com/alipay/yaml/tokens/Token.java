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
import com.alipay.yaml.error.YAMLException;

public abstract class Token {
    public enum ID {
        Alias, Anchor, BlockEnd, BlockEntry, BlockMappingStart, BlockSequenceStart, Directive, DocumentEnd, DocumentStart, FlowEntry, FlowMappingEnd, FlowMappingStart, FlowSequenceEnd, FlowSequenceStart, Key, Scalar, StreamEnd, StreamStart, Tag, Value
    }

    private final Mark startMark;
    private final Mark endMark;

    public Token(Mark startMark, Mark endMark) {
        if (startMark == null || endMark == null) {
            throw new YAMLException("Token requires marks.");
        }
        this.startMark = startMark;
        this.endMark = endMark;
    }

    public String toString() {
        return "<" + this.getClass().getName() + "(" + getArguments() + ")>";
    }

    public Mark getStartMark() {
        return startMark;
    }

    public Mark getEndMark() {
        return endMark;
    }

    /**
     * @see "__repr__ for Token in PyYAML"
     */
    protected String getArguments() {
        return "";
    }

    /**
     * For error reporting.
     * 
     * @see "class variable 'id' in PyYAML"
     */
    public abstract Token.ID getTokenId();

    /*
     * for tests only
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            return toString().equals(obj.toString());
        } else {
            return false;
        }
    }

    /*
     * for tests only
     */
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
