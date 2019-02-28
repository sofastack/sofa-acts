/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.test.acts.object.comparer.impl;

import org.apache.commons.lang.StringUtils;
import com.alipay.test.acts.log.ActsLogUtil;
import com.alipay.test.acts.object.comparer.UnitComparer;
import com.alipay.test.acts.util.FileUtil;

public class FileComparer implements UnitComparer {

    @Override
    public boolean compare(Object expect, Object actual, String comparerFlagCode) {
        if (actual == null && StringUtils.isBlank(String.valueOf(expect))) {
            return true;
        }
        String expectStr = FileUtil.readFile(String.valueOf(expect));
        if (!StringUtils.equals(expectStr, String.valueOf(actual))) {
            ActsLogUtil.addProcessLog("Check failure!");
            ActsLogUtil.addProcessLog("expect:" + expectStr);
            ActsLogUtil.addProcessLog("actual:" + actual);
            return false;
        }
        return true;
    }

}
