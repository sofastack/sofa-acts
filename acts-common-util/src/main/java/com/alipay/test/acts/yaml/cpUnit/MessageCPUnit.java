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
package com.alipay.test.acts.yaml.cpUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * CP unit of message
 * 
 * @author baishuo.lp
 * @version $Id: DataBaseCPUnit.java, v 0.1 2015年8月12日 上午11:23:52 baishuo.lp Exp $
 */
public class MessageCPUnit extends BaseCPUnit {

    private final List<ObjectCPUnit> attributeList = new ArrayList<ObjectCPUnit>();

    private final String             messageKey;

    private String                   eventTopic;

    private String                   eventCode;

    @SuppressWarnings("unchecked")
    public MessageCPUnit(String unitName, List<Object> rawData) {
        messageKey = "";
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MessageCPUnit [attributeList=" + attributeList + ", messageKey=" + messageKey
               + ", eventTopic=" + eventTopic + ", eventCode=" + eventCode + ", unitName="
               + unitName + ", unitType=" + unitType + "]";
    }

    /**
     * Getter method for property <tt>attributeMap</tt>.
     * 
     * @return property value of attributeMap
     */
    public List<ObjectCPUnit> getAttributeList() {
        return attributeList;
    }

    /**
     * Getter method for property <tt>eventTopic</tt>.
     * 
     * @return property value of eventTopic
     */
    public String getEventTopic() {
        return eventTopic;
    }

    /**
     * Setter method for property <tt>eventTopic</tt>.
     * 
     * @param eventTopic value to be assigned to property eventTopic
     */
    public void setEventTopic(String eventTopic) {
        this.eventTopic = eventTopic;
    }

    /**
     * Getter method for property <tt>eventCode</tt>.
     * 
     * @return property value of eventCode
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * Setter method for property <tt>eventCode</tt>.
     * 
     * @param eventCode value to be assigned to property eventCode
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * Getter method for property <tt>messageKey</tt>.
     * 
     * @return property value of messageKey
     */
    public String getMessageKey() {
        return messageKey;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object dump() {
        List objList = new ArrayList();
        for (ObjectCPUnit cpUnit : this.attributeList) {
            objList.add(cpUnit.dump());
        }
        return objList;
    }

}
