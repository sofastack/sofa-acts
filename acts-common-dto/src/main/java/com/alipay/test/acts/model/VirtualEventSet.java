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

import java.util.ArrayList;
import java.util.List;

/**
 * Message check list
 * @author tantian.wc
 * @version $Id: VirtualEventSet.java, v 0.1 2015年10月13日 上午10:24:15 tantian.wc Exp $
 */
public class VirtualEventSet extends TestUnit {
    /** A series of message objects that need to be checked */
    private List<VirtualEventObject> virtualEventObjects = new ArrayList<VirtualEventObject>();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static VirtualEventSet getInstance() {
        return new VirtualEventSet();
    }

    /**
     * Add a message check object
     * @param virtualEventObject
     * @return
     */
    public VirtualEventSet addEventObject(VirtualEventObject virtualEventObject) {
        if (virtualEventObjects == null) {
            virtualEventObjects = new ArrayList<VirtualEventObject>();
        }
        virtualEventObjects.add(virtualEventObject);
        return this;
    }

    /**
     * Add a message check object
     * @param eventObject
     * @param eventCode
     * @param topicId
     * @return
     */
    public VirtualEventSet addEventObject(Object eventObject, String eventCode, String topicId) {
        if (virtualEventObjects == null) {
            virtualEventObjects = new ArrayList<VirtualEventObject>();
        }
        VirtualEventObject virtualEventObject = new VirtualEventObject();
        virtualEventObject.setEventCode(eventCode);
        virtualEventObject.setTopicId(topicId);
        virtualEventObject.setEventObject(new VirtualObject(eventObject));
        virtualEventObjects.add(virtualEventObject);
        return this;
    }

    /**
     * Gets virtual event objects.
     *
     * @return the virtual event objects
     */
    public List<VirtualEventObject> getVirtualEventObjects() {
        return virtualEventObjects;
    }

    /**
     * Sets virtual event objects.
     *
     * @param virtualEventObjects the virtual event objects
     */
    public void setVirtualEventObjects(List<VirtualEventObject> virtualEventObjects) {
        this.virtualEventObjects = virtualEventObjects;
    }

    /** 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualEventSet [virtualEventObjects=" + virtualEventObjects + "]";
    }

}
