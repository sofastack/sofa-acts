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

import com.alipay.test.acts.cache.ActsCacheData;
import com.alipay.test.acts.object.comparer.UnitComparer;

/**
 *
 * @author baishuo.lp
 * @version $Id: CustomComparer.java,v 0.1 2010-8-12 上午11:26:37 baishuo.lp Exp $
 */
public class CustomComparer implements UnitComparer {

    @Override
    public boolean compare(Object expect, Object actual, String comparerFlagCode) {
        UnitComparer comparer = ActsCacheData.getCustomComparer(comparerFlagCode);
        return comparer.compare(expect, actual, comparerFlagCode);
    }
}
