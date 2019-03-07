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
package com.alipay.sofa.isle.sample;

import com.alipay.sofa.isle.sample.model.Request;
import com.alipay.sofa.isle.sample.model.Result;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author qingqin
 * @version $Id: SampleService.java, v 0.1 2019年03月07日 上午11:33 qingqin Exp $
 */
public interface SampleService {

    String message();

    String message(int i);


    Map<String, String> messages(List<String> list);

    Result getMessage(Request request);

    void postMessage(Date date);

    String deleteMessage(String str);
}