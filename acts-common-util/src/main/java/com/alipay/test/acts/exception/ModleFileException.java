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
package com.alipay.test.acts.exception;

/**
 * modle csv file
 * 
 * @author mokong
 * @version $Id: ModleFileException.java, v 0.1 2015年11月13日 下午11:18:44 mokong Exp $
 */
public class ModleFileException extends ActsException {

    /**  */
    private static final long serialVersionUID = -8485466662105298618L;

    /**
     * @param modleFilePath
     */
    public ModleFileException(String modleFilePath) {

        super(modleFilePath + " is error!");

    }

    public ModleFileException(String modleFilePath, Exception e) {

        super(modleFilePath + " can't be loaded.", e);

    }

}
