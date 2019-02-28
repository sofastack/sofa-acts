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
package com.alipay.test.acts.component.db;

import org.apache.commons.lang.StringUtils;
import com.alipay.test.acts.model.VirtualTable;
import com.alipay.test.acts.utils.config.ConfigrationFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author tianzhu.wtzh
 * @version $Id: ActsDBUtils.java, v 0.1 2015年11月4日 下午10:31:58 tianzhu.wtzh Exp $
 */
public class ActsDBUtils {

    private static Map<String, ApplicationContext> contextHashMap = new HashMap<String, ApplicationContext>(
                                                                      1);

    public static void initContextMap(ApplicationContext applicationContext) {
        contextHashMap.put("ApplicationContext", applicationContext);
    }

    /**
     * execute update, also supports specified data sources
     * 
     * @param sql
     * @param tableName
     * @return
     */
    public static int getUpdateResultMap(String sql, String tableName, String dbConfigKey) {

        JdbcTemplate jdbcTemplate = getJdbcTemplate(tableName, dbConfigKey);
        int result = jdbcTemplate.update(sql);
        return result;

    }

    /**
     * execute update,but not supports specified data sources
     * 
     * @param sql
     * @param tableName
     * @return
     */
    public static int getUpdateResultMap(String sql, String tableName) {
        DBDatasProcessor processor = new DBDatasProcessor(contextHashMap.get("ApplicationContext"));
        processor.initDataSource();

        return processor.getJdbcTemplate(tableName).update(sql);
    }

    /**
     * execute query，also supports specified data sources
     * 
     * @param sql
     * @param tableName
     * @return
     */
    public static List<Map<String, Object>> getQueryResultMap(String sql, String tableName,
                                                              String dbConfigKey) {

        JdbcTemplate jdbcTemplate = getJdbcTemplate(tableName, dbConfigKey);
        List<Map<String, Object>> map = jdbcTemplate.queryForList(sql);

        return map;
    }

    /**
     * execute query，but not support specified data sources
     * 
     * @param sql
     * @param tableName
     * @return
     */
    public static List<Map<String, Object>> getQueryResultMap(String sql, String tableName) {

        DBDatasProcessor processor = new DBDatasProcessor(contextHashMap.get("ApplicationContext"));
        processor.initDataSource();

        return processor.getJdbcTemplate(tableName).queryForList(sql);
    }

    /**
     * obtain jdbc connects
     * 
     * @param tableName
     * @param dbConfigKey
     * @return
     */
    public static JdbcTemplate getJdbcTemplate(String tableName, String dbConfigKey) {
        String bundleNameAndBeanName = ConfigrationFactory.getConfigration().getPropertyValue(
            "datasource_bean_name_" + dbConfigKey);

        if (StringUtils.isBlank(bundleNameAndBeanName)) {
            throw new RuntimeException();
        }

        String[] args = bundleNameAndBeanName.split(";");
        String bundleName = args[0];
        String dataSourceBeanName = args[1];
        DBDatasProcessor processor = new DBDatasProcessor(contextHashMap.get("ApplicationContext"));
        DataSource ds = null;

        ds = processor.getDataSource(dataSourceBeanName, bundleName);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        return jdbcTemplate;

    }

    /**
     * clean DB data by model
     * 
     * @param tableName
     * @param groupIds
     */
    public static void cleanDBData(VirtualTable tableName, String... groupIds) {
        List<VirtualTable> depTables = new ArrayList<VirtualTable>();
        depTables.add(tableName);
        DBDatasProcessor processor = new DBDatasProcessor(contextHashMap.get("ApplicationContext"));
        processor.initDataSource();
        processor.cleanDBDatas(depTables, groupIds);
    }

    /**
     * prepare db data by model
     * 
     * @param tableName
     * @param groupIds
     */
    public static void preDBData(VirtualTable tableName, String... groupIds) {
        List<VirtualTable> depTables = new ArrayList<VirtualTable>();
        depTables.add(tableName);
        DBDatasProcessor processor = new DBDatasProcessor(contextHashMap.get("ApplicationContext"));
        processor.initDataSource();
        processor.importDepDBDatas(depTables, groupIds);
    }

    /**
     * compare Db Data by model
     * 
     * @param tableName
     * @param groupIds
     */
    public static void compareDbData(VirtualTable tableName, String... groupIds) {
        List<VirtualTable> depTables = new ArrayList<VirtualTable>();
        depTables.add(tableName);
        DBDatasProcessor processor = new DBDatasProcessor(contextHashMap.get("ApplicationContext"));
        processor.initDataSource();
        processor.compare2DBDatas(depTables, groupIds);
    }

}
