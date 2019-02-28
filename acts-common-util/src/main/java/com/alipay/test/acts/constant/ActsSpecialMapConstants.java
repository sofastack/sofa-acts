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
package com.alipay.test.acts.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * Some custom field constants in Acts，used for database data preparation and verification
 * 
 * @author baishuo.lp
 * @version $Id: ActsSpeicalMapConstants.java, v 0.1 2015年8月14日 下午2:35:17 baishuo.lp Exp $
 */
public class ActsSpecialMapConstants {

    //Only check does not exist when checking data, only when using check method
    public final static String      NOTEXIST           = "$NotExist";

    //Delete data during data preparation and only apply the insert method
    public final static String      ONLYDELETE         = "$OnlyDelete";

    //data source
    public final static String      DBCONFIGKEY        = "$DBConfigKey";

    //virtual key used on share table
    public final static String      SPLITKEY           = "$SplitKey";

    //virtual value used on share table
    public final static String      SPLITVALUE         = "$SplitValue";

    //Group data validation, internally used to sql sort, not for external use
    public final static String      ORDERBY            = "$OrderBy";

    //Special field collection, when adding a new field, you need to fill in the field into this collection
    public final static Set<String> specialConstantSet = new HashSet<String>();

    static {
        specialConstantSet.add(NOTEXIST);
        specialConstantSet.add(ONLYDELETE);
        specialConstantSet.add(DBCONFIGKEY);
        specialConstantSet.add(SPLITKEY);
        specialConstantSet.add(SPLITVALUE);
    }
}
