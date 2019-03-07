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

import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.isle.sample.model.Request;
import com.alipay.sofa.isle.sample.model.Result;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author qingqin
 * @version $Id: SampleServiceImpl.java, v 0.1 2019年03月07日 上午11:36 qingqin Exp $
 */
public class SampleServiceImpl implements SampleService {

    @Override
    public String message() {
        return "Hello, Service slitecore";
    }

    public String message(int i) {
        if (i > 0) {
            return "Hello, Service SOFABoot";
        } else if (i == 0) {
            return "Hello, world";
        }
        return "Hello, Acts";
    }

    @Override
    public Map<String, String> messages(List<String> list) {
        Map<String, String> map = new HashMap<>();
        map.put("Hello", "acts");
        return map;
    }

    @Override
    public Result getMessage(Request request) {
        Result result = new Result();
        result.setMsg("fail");

        if ("1".equals(request.getId())) {
            result.setCode("01");
            result.setMsg("success");
        }

        return result;
    }

    @Override
    public void postMessage(Date date) {

    }

    @Override
    public String deleteMessage(String str) {
        if (StringUtil.contains(str, "123")) {
            return str + " deleted";
        } else {
            return "error args";
        }
    }

}