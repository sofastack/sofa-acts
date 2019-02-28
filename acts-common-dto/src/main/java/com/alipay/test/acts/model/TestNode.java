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
 * The minimum part of test such as VirtualObject VirtualTable...
 * @author tantian.wc
 * @version $Id: TestUnitPart.java, v 0.1 2015-10-26 7:27:06 p.m tantian.wc Exp $
 */
public class TestNode {

    /** for test framework to decide if this part should be counted */
    String nodeGroup;

    /** description  */
    String nodeDesc;

    /**
     * Gets node group.
     *
     * @return the node group
     */
    public String getNodeGroup() {
        return nodeGroup;
    }

    /**
     * Sets node group.
     *
     * @param nodeGroup the node group
     */
    public void setNodeGroup(String nodeGroup) {
        this.nodeGroup = nodeGroup;
    }

    /**
     * Gets node desc.
     *
     * @return the node desc
     */
    public String getNodeDesc() {
        return nodeDesc;
    }

    /**
     * Sets node desc.
     *
     * @param nodeDesc the node desc
     */
    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

}
