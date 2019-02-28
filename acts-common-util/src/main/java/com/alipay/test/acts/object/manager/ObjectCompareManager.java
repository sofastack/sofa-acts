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
package com.alipay.test.acts.object.manager;

import com.alipay.test.acts.object.comparer.UnitComparer;
import com.alipay.test.acts.object.comparer.impl.AvailableComparer;
import com.alipay.test.acts.object.comparer.impl.ConditionComparer;
import com.alipay.test.acts.object.comparer.impl.CustomComparer;
import com.alipay.test.acts.object.comparer.impl.DateComparer;
import com.alipay.test.acts.object.comparer.impl.FileComparer;
import com.alipay.test.acts.object.comparer.impl.MapComparer;
import com.alipay.test.acts.object.comparer.impl.NoCheckComparer;
import com.alipay.test.acts.object.comparer.impl.RegexComparer;
import com.alipay.test.acts.object.comparer.impl.SimpleComparer;
import com.alipay.test.acts.object.enums.UnitFlagEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author baishuo.lp
 * @version $Id: UnitComparerManager.java, v 0.1 2015年8月19日 上午10:57:04 baishuo.lp Exp $
 */
public class ObjectCompareManager {

    private static Map<UnitFlagEnum, UnitComparer> comparerManager = new HashMap<UnitFlagEnum, UnitComparer>();

    static {
        comparerManager.put(UnitFlagEnum.N, new NoCheckComparer());
        comparerManager.put(UnitFlagEnum.Y, new SimpleComparer());
        comparerManager.put(UnitFlagEnum.D, new DateComparer());
        comparerManager.put(UnitFlagEnum.R, new RegexComparer());
        comparerManager.put(UnitFlagEnum.C, new ConditionComparer());
        comparerManager.put(UnitFlagEnum.A, new AvailableComparer());
        comparerManager.put(UnitFlagEnum.M, new MapComparer());
        comparerManager.put(UnitFlagEnum.F, new FileComparer());
        comparerManager.put(UnitFlagEnum.CUSTOM, new CustomComparer());
    }

    public static Map<UnitFlagEnum, UnitComparer> getComparerManager() {
        return comparerManager;
    }

}
