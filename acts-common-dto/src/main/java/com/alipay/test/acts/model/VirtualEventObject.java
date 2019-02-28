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
package com.alipay.test.acts.model;

/**
 * 
 * @author tantian.wc
 * @version $Id: VirtualEventObject.java, v 0.1 2015年10月13日 上午10:22:13 tantian.wc Exp $
 */
public class VirtualEventObject extends TestNode {

    public VirtualObject eventObject;

    /** eventCode */
    public String        eventCode;

    /** topicId  */
    public String        topicId;

    /** flag */
    public String        isExist = "Y";

    /**
     * Gets is exist.
     *
     * @return the is exist
     */
    public String getIsExist() {
        return isExist;
    }

    /**
     * Sets flag.
     *
     * @param isExist the is exist
     */
    public void setFlag(String isExist) {
        this.isExist = isExist;
    }

    /**
     * Gets event code.
     *
     * @return the event code
     */
    public String getEventCode() {
        return eventCode;
    }

    /**
     * Sets event code.
     *
     * @param eventCode the event code
     */
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    /**
     * Gets topic id.
     *
     * @return the topic id
     */
    public String getTopicId() {
        return topicId;
    }

    /**
     * Sets topic id.
     *
     * @param topicId the topic id
     */
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    /**
     * Gets event object.
     *
     * @return the event object
     */
    public VirtualObject getEventObject() {
        return eventObject;
    }

    /**
     * Sets event object.
     *
     * @param eventObject the event object
     */
    public void setEventObject(VirtualObject eventObject) {
        this.eventObject = eventObject;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualEventObject [eventObject=" + eventObject + ", eventCode=" + eventCode
               + ", topicId=" + topicId + ", isExist=" + isExist + "]";
    }

}
