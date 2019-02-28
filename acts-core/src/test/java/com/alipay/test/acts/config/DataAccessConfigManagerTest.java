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
package com.alipay.test.acts.config;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author qingqin
 * @version $Id: DataAccessConfigManagerTest.java, v 0.1 2019年01月12日 下午9:38 qingqin Exp $
 */
public class DataAccessConfigManagerTest {

    /***
     * Test method for {@link DataAccessConfigManager#findTableName(String)}
     */
    @Test
    public void testFindTableName() {
        DataAccessConfigManager.dataSourceMap.clear();
        DataAccessConfigManager.extDataSourceMap.clear();

        DataAccessConfigManager dataAccessConfigManager = new DataAccessConfigManager();

        DataAccessConfig dataAccessConfig = new DataAccessConfig("test",
            new String[] { "aa", "bb" }, new String[] { "cc", "dd" });

        DataAccessConfigManager.configMap.put("xxxxxx", dataAccessConfig);
        String tablename = dataAccessConfigManager.findTableName("xxxxxx");

        Assert.assertEquals(tablename, "test");

        DataAccessConfigManager.configMap.clear();
    }

    /***
     * Test method for {@link DataAccessConfigManager#findDataSourceName(String)}
     */
    @Test
    public void testFindDataSourceName() {
        DataAccessConfigManager.dataSourceMap.clear();
        DataAccessConfigManager.extDataSourceMap.clear();

        DataAccessConfigManager dataAccessConfigManager = new DataAccessConfigManager();

        DataAccessConfig dataAccessConfig = new DataAccessConfig("test",
            new String[] { "aa", "bb" }, new String[] { "cc", "dd" });
        DataAccessConfigManager.configMap.put("uuuuu", dataAccessConfig);

        List<String> list = Arrays.asList("test", "example", "qingqin", "abc*", "*dfg", "*yyy*");
        DataAccessConfigManager.dataSourceMap.put("ds_example", list);

        List<String> listExt = Arrays.asList("test_ext", "example_ext", "cdd_ext", "qwe*", "*rty",
            "*xxx*");
        DataAccessConfigManager.extDataSourceMap.put("ds_example_ext", listExt);

        String dsName = dataAccessConfigManager.findDataSourceName("test");
        Assert.assertEquals(dsName, "ds_example");

        dsName = dataAccessConfigManager.findDataSourceName("example_ext");
        Assert.assertEquals(dsName, "ds_example_ext");

        //*
        dsName = dataAccessConfigManager.findDataSourceName("abcd");
        Assert.assertEquals(dsName, "ds_example");

        dsName = dataAccessConfigManager.findDataSourceName("cdfg");
        Assert.assertEquals(dsName, "ds_example");

        dsName = dataAccessConfigManager.findDataSourceName("yyyyyyy");
        Assert.assertEquals(dsName, "ds_example");

        dsName = dataAccessConfigManager.findDataSourceName("qwer");
        Assert.assertEquals(dsName, "ds_example_ext");

        dsName = dataAccessConfigManager.findDataSourceName("erty");
        Assert.assertEquals(dsName, "ds_example_ext");

        dsName = dataAccessConfigManager.findDataSourceName("xxxxxxxx");
        Assert.assertEquals(dsName, "ds_example_ext");

        //regular
        dsName = dataAccessConfigManager.findDataSourceName("qing.*");
        Assert.assertEquals(dsName, "ds_example");

        dsName = dataAccessConfigManager.findDataSourceName("^c.*");
        Assert.assertEquals(dsName, "ds_example_ext");

        //null

        dsName = dataAccessConfigManager.findDataSourceName("ooooooo");
        Assert.assertEquals(dsName, "ds_example");

        DataAccessConfigManager.dataSourceMap.clear();
        DataAccessConfigManager.extDataSourceMap.clear();
    }

    /***
     * Test method for {@link DataAccessConfigManager#findDataAccessConfig(String)} (String)}
     */
    @Test
    public void testFindDataAccessConfig() {
        DataAccessConfigManager.dataSourceMap.clear();
        DataAccessConfigManager.extDataSourceMap.clear();

        DataAccessConfigManager dataAccessConfigManager = new DataAccessConfigManager();

        DataAccessConfig dataAccessConfig = new DataAccessConfig("test",
            new String[] { "aa", "bb" }, new String[] { "cc", "dd" });
        DataAccessConfigManager.configMap.put("xxxxxx", dataAccessConfig);

        DataAccessConfig config = dataAccessConfigManager.findDataAccessConfig("xxxxxx");

        Assert.assertEquals(config.toString(), dataAccessConfig.toString());

        DataAccessConfigManager.configMap.clear();
    }

    /***
     * Test method for {@link DataAccessConfigManager#getDAO(String)}
     */
    @Test
    public void testGetDAO() {
        DataAccessConfigManager dataAccessConfigManager = new DataAccessConfigManager();

        Object object = dataAccessConfigManager.getDAO("xxxxxx");

        Assert.assertEquals(object, null);
    }

    /***
     * Test method for {@link DataAccessConfigManager#updateExtDataSourceMap(String, String)}
     */
    @Test
    public void testUpdateExtDataSourceMap() {
        DataAccessConfigManager.extDataSourceMap.clear();

        DataAccessConfigManager dataAccessConfigManager = new DataAccessConfigManager();

        dataAccessConfigManager.updateExtDataSourceMap("123", "abc");

        Assert.assertEquals(DataAccessConfigManager.extDataSourceMap.size(), 1);
        Assert.assertEquals(DataAccessConfigManager.extDataSourceMap.get("123").size(), 1);

        Assert.assertEquals(DataAccessConfigManager.extDataSourceMap.get("123").get(0), "abc");

        DataAccessConfigManager.extDataSourceMap.clear();
    }

    /***
     * Test method for {@link DataAccessConfigManager#clearExtDataSourceMap()} (String, String)}
     */
    @Test
    public void testClearExtDataSourceMap() {
        DataAccessConfigManager.extDataSourceMap.clear();

        DataAccessConfigManager dataAccessConfigManager = new DataAccessConfigManager();

        dataAccessConfigManager.updateExtDataSourceMap("123", "abc");

        DataAccessConfigManager.clearExtDataSourceMap();

        Assert.assertEquals(DataAccessConfigManager.extDataSourceMap.size(), 0);

        DataAccessConfigManager.extDataSourceMap.clear();
    }

}