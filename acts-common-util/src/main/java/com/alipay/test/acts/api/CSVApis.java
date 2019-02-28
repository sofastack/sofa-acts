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
package com.alipay.test.acts.api;

import com.alipay.test.acts.util.CSVApisUtil;

import java.util.Set;

/**
 * CSV operation
 * 
 * @author baishuo.lp
 * @version $Id: CSVApis.java, v 0.1 2015年9月6日 下午3:18:25 baishuo.lp Exp $
 */
public class CSVApis {

    /**
     * Generate csv files for the input parameters and return parameter of the specified method
     *
     * @param genRootPath
     * @param clsLoader
     * @param clsName
     * @param methodName
     * @param isResultOnly
     * @return
     * @throws ClassNotFoundException
     */
    public static Set<String> genCsvFromSpeciMethodByRootPath(String genRootPath,
                                                              ClassLoader clsLoader,
                                                              String clsName, String methodName,
                                                              boolean isResultOnly)
                                                                                   throws ClassNotFoundException {

        return CSVApisUtil.paraClassSpeciMethodToCscFile(clsName, clsLoader, genRootPath,
            methodName, isResultOnly);
    }

    /**
     * Generate csv files based on class
     *
     * @param clsName    -targeted class
     * @param genRootPath        -CSV file path
     * @throws ClassNotFoundException
     */
    public static Set<String> genCsvFromObjClassByRootPath(String genRootPath,
                                                           ClassLoader clsLoader, String clsName)
                                                                                                 throws ClassNotFoundException {

        return CSVApisUtil.paraClassToCscFile(clsName, clsLoader, genRootPath);
    }

}
