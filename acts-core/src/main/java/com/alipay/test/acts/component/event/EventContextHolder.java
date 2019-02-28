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
package com.alipay.test.acts.component.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event content holder
 * @author jie.peng
 * @version $Id: EventContextHolder.java, v 0.1 2015-6-29 下午06:00:25 jie.peng Exp $
 */
public class EventContextHolder {

    /**
     * Business event message
     */
    private static Map<String, List<Object>> bizEvent = new HashMap<String, List<Object>>();

    /**
     * Gets the event content held in the thread variable and clears the value in the thread variable.
     * A thread is used only once to avoid data chaos when a thread is caused to run by multiple test cases.
     *
     * @return
     */
    public static Map<String, List<Object>> getBizEvent() {
        return bizEvent;
    }

    public static void setEvent(String eventCode, String topicId, Object payLoad) {
        if (bizEvent == null) {
            bizEvent = new HashMap<String, List<Object>>();
        }
        String key = eventCode + "|" + topicId;
        if (bizEvent.containsKey(key)) {
            bizEvent.get(key).add(payLoad);
        } else {
            List<Object> payLoads = new ArrayList<Object>();
            payLoads.add(payLoad);
            bizEvent.put(key, payLoads);
        }
    }

    /**
     * Clean up thread variable
     */
    public static void clear() {
        bizEvent = new HashMap<String, List<Object>>();
    }
}
