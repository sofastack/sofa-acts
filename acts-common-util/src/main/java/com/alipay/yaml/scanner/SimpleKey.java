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
package com.alipay.yaml.scanner;

import com.alipay.yaml.error.Mark;

/**
 * Simple keys treatment.
 * <p>
 * Helper class for {@link ScannerImpl}.
 * </p>
 * 
 * @see ScannerImpl
 */
final class SimpleKey {
    private int     tokenNumber;
    private boolean required;
    private int     index;
    private int     line;
    private int     column;
    private Mark    mark;

    /**
     * Constructor.
     *
     * @param tokenNumber the token number
     * @param required the required
     * @param index the index
     * @param line the line
     * @param column the column
     * @param mark the mark
     */
    public SimpleKey(int tokenNumber, boolean required, int index, int line, int column, Mark mark) {
        this.tokenNumber = tokenNumber;
        this.required = required;
        this.index = index;
        this.line = line;
        this.column = column;
        this.mark = mark;
    }

    /**
     * Gets token number.
     *
     * @return the token number
     */
    public int getTokenNumber() {
        return this.tokenNumber;
    }

    /**
     * Gets column.
     *
     * @return the column
     */
    public int getColumn() {
        return this.column;
    }

    /**
     * Gets mark.
     *
     * @return the mark
     */
    public Mark getMark() {
        return mark;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets line.
     *
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * Is required boolean.
     *
     * @return the boolean
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return "SimpleKey - tokenNumber=" + tokenNumber + " required=" + required + " index="
               + index + " line=" + line + " column=" + column;
    }
}
