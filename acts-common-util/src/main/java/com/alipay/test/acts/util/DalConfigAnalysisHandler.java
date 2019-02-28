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
package com.alipay.test.acts.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *  Analytic processor of dal-config.xml
 * @author xiaoleicxl
 * @version $Id: DalConfigAnalysisHandler.java, v 0.1 2016年1月4日 上午10:39:21 xiaoleicxl Exp $
 */
class DalConfigAnalysisHandler extends DefaultHandler {

    private List<String> findIncludeList = new ArrayList<String>();

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
                                                                                               throws SAXException {

        if (StringUtils.equals(qName, "include")) {

            findIncludeList.add(attributes.getValue(0));
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

    }

    /**
     * Get table name set according to parsed result
     * 
     * @return
     */
    public HashSet<String> getFullTables() {
        HashSet<String> tableSet = new HashSet<String>();
        for (String s : findIncludeList) {
            String tableTemp = StringUtils.substringAfterLast(s, "tables/");
            String table = StringUtils.substringBefore(tableTemp, ".");
            tableSet.add(table);
        }

        return tableSet;
    }
}