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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.alibaba.fastjson.JSONObject;
import com.alipay.test.acts.utils.PropertiesUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.annotation.IVTableGroupCmdMethod;
import com.alipay.test.acts.driver.ActsConfiguration;
import com.alipay.test.acts.config.DataAccessConfig;
import com.alipay.test.acts.config.DataAccessConfigManager;
import com.alipay.test.acts.constant.ActsConstants;
import com.alipay.test.acts.exception.ActsTestException;
import com.alipay.test.acts.model.VirtualTable;
import com.alipay.test.acts.runtime.ActsRuntimeContextThreadHold;
import com.alipay.test.acts.runtime.ComponentsActsRuntimeContextThreadHold;
import com.alipay.test.acts.utils.DetailCollectUtils;
import com.alipay.test.acts.util.VelocityUtil;
import com.alipay.test.acts.utils.config.ConfigrationFactory;

/***
 *
 * @author tantian.wc
 * @modified qingqin.cdd
 */
public class DBDatasProcessor {

    protected static final Log                log                           = LogFactory
                                                                                .getLog(DBDatasProcessor.class);

    protected Map<String, JdbcTemplate>       jdbcTemplateMap;

    protected DataAccessConfigManager         dataAccessConfigManager;

    /**before table do something*/
    private final List<IVTableGroupCmdMethod> beforeVTableExecuteMethodList = new LinkedList<IVTableGroupCmdMethod>();

    /**after table do something*/
    private final List<IVTableGroupCmdMethod> afterVTableExecuteMethodList  = new LinkedList<IVTableGroupCmdMethod>();

    /**special groupid*/
    private final List<String>                specialGroupIds               = new ArrayList<String>();

    private ApplicationContext                applicationContext;

    /**
     * Constructor.
     */
    public DBDatasProcessor() {
    }

    /**
     * Constructor.
     *
     * @param applicationContext the application context
     */
    public DBDatasProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Init data source.
     */
    public void initDataSource() {
        dataAccessConfigManager = (DataAccessConfigManager) applicationContext
            .getBean("dataAccessConfigManager");

        String bundleName = ConfigrationFactory.getConfigration()
            .getPropertyValue("datasource_bundle_name").replace(";", "");

        ConfigrationFactory.loadFromConfig("config/dbConf/"
                                           + ConfigrationFactory.getConfigration()
                                               .getPropertyValue("dbmode") + "db.conf");
        Map<String, String> configs = ConfigrationFactory.getConfigration().getConfig();
        if (configs == null) {
            Assert
                .assertTrue(
                    false,
                    "datasource config not exist, add config in [acts-config.properties] ,modify datasource config starting with [ds_]");

        }
        for (String key : configs.keySet()) {
            if (key.startsWith("ds_")) {
                String dsName = key.replace("ds_", "");
                List<String> tables = new ArrayList<String>();
                for (String tableName : configs.get(key).split(",")) {
                    tables.add(tableName);
                }

                DataSource ds = null;
                //Attempt to read db.conf to obtain configs
                if (configs.containsKey(dsName + "_url")) {
                    String db_url = configs.get(dsName + "_url");
                    String db_username = configs.get(dsName + "_username");
                    String db_password = configs.get(dsName + "_password");
                    BasicDataSource datasource = new BasicDataSource();
                    String type = getDBType(db_url);

                    try {
                        if (type.equalsIgnoreCase("oracle")) {
                            Class.forName("oracle.jdbc.OracleDriver");
                            datasource.setDriverClassName("oracle.jdbc.OracleDriver");
                        } else {
                            Class.forName("com.mysql.jdbc.Driver");
                            datasource.setDriverClassName("com.mysql.jdbc.Driver");
                        }
                        datasource.setUrl(db_url);
                        datasource.setUsername(db_username);
                        datasource.setPassword(db_password);
                        ds = datasource;
                    } catch (Exception e) {
                        throw new ActsTestException("Failed obtaining datasource:" + dsName, e);
                    }

                } else {

                    ds = ((DataSource) applicationContext.getBean(dsName));
                    if (ds == null) {
                        //default on-the-fly-bundle
                        ds = ((DataSource) applicationContext.getBean(dsName));
                    }
                }
                if (ds == null) {
                    Assert.assertTrue(false, "Failed obtaining datasource,bean:[" + dsName
                                             + "] specified bundleName: [" + bundleName + "]");
                }

                JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
                if (jdbcTemplateMap == null) {
                    jdbcTemplateMap = new HashMap<String, JdbcTemplate>();
                }
                jdbcTemplateMap.put(dsName, jdbcTemplate);
                DataAccessConfigManager.dataSourceMap.put(dsName, tables);
            }

        }

    }

    /**User-defined table-datasource Map
     * extMapInfo:Map<tableName,dataSourceName>
     * Only extra zdal datasource suppported for the moment.
     * direct connection config not supported.
     *  */
    public void updateDataSource(Map<String, String> extMapInfo) {
        if (extMapInfo == null) {
            return;
        }
        DataAccessConfigManager.clearExtDataSourceMap();
        for (Map.Entry<String, String> entry : extMapInfo.entrySet()) {
            String dataSourceName = "datasource_bean_name_" + entry.getValue();
            JdbcTemplate jdbcTemplate = ActsDBUtils.getJdbcTemplate(entry.getKey(),
                entry.getValue());
            jdbcTemplateMap.put(dataSourceName, jdbcTemplate);
            DataAccessConfigManager.updateExtDataSourceMap(dataSourceName, entry.getKey());
        }
    }

    /**
     * Import dep db datas boolean.
     *
     * @param depTables the dep tables
     * @param groupIds the group ids
     * @return the boolean
     */
    @SuppressWarnings("rawtypes")
    public boolean importDepDBDatas(List<VirtualTable> depTables, String... groupIds) {
        if (depTables == null || depTables.isEmpty()) {
            return true;
        }
        //Dynamic assembly of SQL based on DO class attribute prefix, then execute
        for (int i = 0; i < depTables.size(); i++) {

            VirtualTable table = depTables.get(i);
            if (table == null) {
                continue;
            }
            if ((ArrayUtils.isEmpty(groupIds) && StringUtils.isEmpty(table.getNodeGroup()))
                || ArrayUtils.contains(groupIds, table.getNodeGroup())) {
                List<String> fields = filterByColumns(table.getTableName(), table.getTableData()
                    .get(0));
                String sql = genInsertSql(table, table.getTableName(), fields, null);
                DetailCollectUtils.appendDetail(sql);

                Execute(beforeVTableExecuteMethodList, table.getTableName(), table.getNodeGroup());
                //Assemble parameters and execute SQL
                doImport(table, sql, fields);
                Execute(afterVTableExecuteMethodList, table.getTableName(), table.getNodeGroup());
            }
        }
        return true;
    }

    protected List<String> filterByColumns(String tableName, Map<String, Object> row) {

        List<String> allFields = new ArrayList<String>();
        for (String key : row.keySet()) {
            allFields.add(key);
        }
        return allFields;
    }

    /**
     * Import dep db datas with dao boolean.
     *
     * @param depTables the dep tables
     * @return the boolean
     */
    public boolean importDepDBDatasWithDao(List<VirtualTable> depTables) {

        return true;
    }

    /**
     * Delete db datas with dao.
     *
     * @param depTables the dep tables
     */
    @SuppressWarnings("rawtypes")
    public void deleteDBDatasWithDao(List<VirtualTable> depTables) {

    }

    protected String genInsertSql(VirtualTable table, String tableName, List<String> fields,
                                  Map<String, Object> row) {
        StringBuffer fieldBuffer = new StringBuffer();
        StringBuffer fieldPlaceholderBuffer = new StringBuffer();

        for (String field : fields) {
            String currentFlag = table.getFlags().get(field);
            if (StringUtils.isBlank(currentFlag)) {
                currentFlag = table.getFlags().get(field.toLowerCase());
                if (StringUtils.isBlank(currentFlag)) {
                    currentFlag = table.getFlags().get(field.toUpperCase());
                }
            }

            if (currentFlag != null) {

                if (currentFlag.equalsIgnoreCase("F")) {
                    if (row == null || row.get(field) == null) {
                        return null;
                    }
                    fieldBuffer.append(field).append(",");
                    fieldPlaceholderBuffer.append(row.get(field)).append(",");
                } else if (!currentFlag.equalsIgnoreCase("n")) {
                    fieldBuffer.append(field).append(",");
                    fieldPlaceholderBuffer.append("?").append(",");
                }

            } else {
                fieldBuffer.append(field).append(",");
                fieldPlaceholderBuffer.append("?").append(",");
            }

        }

        String fieldPart = "insert into " + tableName + " ("
                           + fieldBuffer.substring(0, fieldBuffer.length() - 1) + ") ";

        String placeholderPart = " ("
                                 + fieldPlaceholderBuffer.substring(0,
                                     fieldPlaceholderBuffer.length() - 1) + ") ";
        return fieldPart + "values" + placeholderPart;
    }

    /**
     * Execute SQL to import data
     *
     * @param table
     * @param fields
     * @param sql
     * @param fields
     */
    protected void doImport(VirtualTable table, String sql, List<String> fields) {
        List<Map<String, Object>> rows = table.getTableData();
        if (rows == null || rows.isEmpty()) {
            return;
        }

        for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
            String currentSql = sql;
            Map<String, Object> rowData = rows.get(rowNum);
            List<Object> args = new ArrayList<Object>();
            if (currentSql == null) {
                //SQL is empty, which means that each row needs to be stitched once
                currentSql = genInsertSql(table, table.getTableName(), fields, rows.get(rowNum));
                if (currentSql == null) {
                    throw new ActsTestException("tagged with F，but no [function name] in [value]");
                }
            }
            for (String field : fields) {
                if (table.getFlags() != null && table.getFlags().get(field) != null) {
                    if (!table.getFlags().get(field).equalsIgnoreCase("n")
                        && !table.getFlags().get(field).equalsIgnoreCase("F")
                        && !table.getFlags().get(field).equalsIgnoreCase("L")) {
                        //F is sys function, already put in sql when splicing, L need Separate processing
                        args.add(rowData.get(field));
                    } else if (table.getFlags().get(field).equalsIgnoreCase("L")) {
                        String fieldValue = PropertiesUtil.convert2String(
                            transStringToMap((String) rowData.get(field)), false);
                        args.add(fieldValue);
                    }
                } else {
                    args.add(rowData.get(field));
                }

            }
            //execute sql
            getJdbcTemplate(table.getTableName()).update(currentSql, args.toArray());
            String message = "";
            for (Object arg : args) {
                message += arg + ",";
            }
            DetailCollectUtils.appendAndLog("Executing sql:" + currentSql + ",parameters:"
                                            + message, log);
        }
    }

    /**
     * Clean db datas.
     *
     * @param depTables the dep tables
     * @param groupIds the group ids
     */
    public void cleanDBDatas(List<VirtualTable> depTables, String... groupIds) {

        if (depTables == null || depTables.isEmpty()) {
            return;
        }
        for (int i = 0; i < depTables.size(); i++) {

            //Dynamically assemble SQL and execute according to DO class attribute prefix
            VirtualTable table = depTables.get(i);
            if (table == null) {
                continue;
            }
            if ((ArrayUtils.isEmpty(groupIds) && StringUtils.isEmpty(table.getNodeGroup()))
                || ArrayUtils.contains(groupIds, table.getNodeGroup())) {
                List<String> fields = filterByColumns(table.getTableName(), table.getTableData()
                    .get(0));

                String sql = genDeleteSql(table, fields);
                Execute(beforeVTableExecuteMethodList, table.getTableName(), table.getNodeGroup());
                //Assembly parameter and execution sql
                doDelete(table, sql, fields);
                Execute(afterVTableExecuteMethodList, table.getTableName(), table.getNodeGroup());
            }
        }

    }

    /**
     * Query batch results
     *
     * @param tableName
     * @param sql
     * @return
     */
    public List<Map<String, Object>> queryForList(String tableName, String sql) {
        return getJdbcTemplate(tableName).queryForList(sql);
    }

    /**
     * Generate SQL template
     *
     * @param table
     * @param fields
     * @return
     */
    protected String genDeleteSql(VirtualTable table, List<String> fields) {

        String tableName = table.getTableName();

        String prefixPart = "delete from " + tableName + " where ";

        String wherePart = "";

        String[] selectKeys = getSelectKeys(table, fields);
        for (String key : selectKeys) {
            wherePart = wherePart + " and " + key + " = ?";
        }

        return prefixPart + StringUtils.substringAfter(wherePart, "and ");
    }

    protected void doDelete(VirtualTable table, String sql, List<String> fields) {
        List<Map<String, Object>> rows = table.getTableData();
        String[] keys = getSelectKeys(table, fields);
        Map<String, Object> paramBuffer = ActsRuntimeContextThreadHold.getContext().paramMap;

        for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
            Map<String, Object> rowData = rows.get(rowNum);
            Object[] args = new Object[keys.length];
            int i = 0;
            for (; i < args.length; ++i) {
                if (String.valueOf(rowData.get(keys[i])).startsWith("=")) {
                    throw new ActsTestException("tableName " + table.getTableName()
                                                + " condition: " + keys[i]
                                                + "cannot be assignment statement&");
                }
                if (String.valueOf(rowData.get(keys[i])).startsWith("$")) {
                    String key = String.valueOf(rowData.get(keys[i])).replace("$", "");
                    if (paramBuffer.containsKey(key)) {
                        args[i] = paramBuffer.get(key);
                    } else {
                        break;
                    }
                } else {
                    args[i] = rowData.get(keys[i]);
                }
            }
            if (i < args.length) {
                //If the parameter is insufficient, it will not be deleted, execute the next statement
                continue;
            }

            getJdbcTemplate(table.getTableName()).update(sql, args);
            String message = "";
            for (Object arg : args) {
                message += arg + ",";
            }
            DetailCollectUtils.appendAndLog("Executing sql:" + sql + ",parameters:" + message, log);
            log.info("Executing sql:" + sql + ",parameters:" + message);
        }
    }

    /**
     * Compare 2 db datas.
     *
     * @param virtualTables the virtual tables
     * @param groupIds the group ids
     */
    public void compare2DBDatas(List<VirtualTable> virtualTables, String... groupIds) {

        boolean isAllTableSame = true;
        StringBuilder allErr = new StringBuilder();
        for (int i = 0; i < virtualTables.size(); i++) {

            VirtualTable table = virtualTables.get(i);
            if ((ArrayUtils.isEmpty(groupIds) && StringUtils.isEmpty(table.getNodeGroup()))
                || ArrayUtils.contains(groupIds, table.getNodeGroup())) {
                List<String> fields = filterByColumns(table.getTableName(), table.getTableData()
                    .get(0));

                String sql = genSelectSql(table, fields);
                DetailCollectUtils.appendDetail(sql);

                Execute(beforeVTableExecuteMethodList, table.getTableName(), table.getNodeGroup());

                boolean isTableSame = doSelectAndCompare(table, fields, sql, allErr);

                Execute(afterVTableExecuteMethodList, table.getTableName(), table.getNodeGroup());

                if (!isTableSame) {
                    isAllTableSame = false;
                }
            }
        }

        if (!isAllTableSame) {
            throw new ActsTestException("Failed checking DB!\n" + allErr);
        }

    }

    protected String genSelectSql(VirtualTable table, List<String> fields) {

        String tableName = table.getTableName();

        //Column to be queried
        StringBuffer queryCoulumsBuffer = new StringBuffer();

        for (String field : fields) {
            queryCoulumsBuffer.append(field + " as " + field).append(",");
        }

        String queryCoulums = queryCoulumsBuffer.substring(0, queryCoulumsBuffer.length() - 1);

        //Where condition ,Only the default query conditions are supported first
        StringBuffer wherePartBuffer = new StringBuffer();
        String[] selectKeys = getSelectKeys(table, fields);

        for (String selectKey : selectKeys) {
            wherePartBuffer.append("(" + selectKey + " = ? " + " ) and ");
        }

        String wherePart = wherePartBuffer.substring(0, wherePartBuffer.length() - 4);

        return "select " + queryCoulums + " from " + tableName + " where " + wherePart;
    }

    protected boolean doSelectAndCompare(VirtualTable table, List<String> fields, String sql,
                                         StringBuilder allErr) {
        List<Map<String, Object>> rows = table.getTableData();
        String[] selectKeys = getSelectKeys(table, fields);

        boolean isSame = true;
        StringBuilder err = new StringBuilder();
        Map<String, Object> paramBuffer = ComponentsActsRuntimeContextThreadHold.getContext().paramMap;

        boolean assertEmpty = willAssertEmpty(table, fields);

        for (int rowNum = 0; rowNum < rows.size(); rowNum++) {
            Map<String, Object> rowData = rows.get(rowNum);
            if (rowData == null) {
                continue;
            }

            //The actual value of the placeholder in the select statement
            Object[] valsOfArgs = getValsForSelectKeys(selectKeys, rowData, table.getTableName());

            //Multiple rows saved after executing sql
            List<Map<String, Object>> dbRowsResult = null;
            try {
                dbRowsResult = getJdbcTemplate(table.getTableName()).queryForList(sql, valsOfArgs);
                String message = "";
                for (Object arg : valsOfArgs) {
                    message += arg + ",";
                }
                log.info("Executing sql:" + sql + ",parameters:" + message);
                if (assertEmpty && dbRowsResult.size() > 0) {
                    isSame = false;
                    err.append("Failed checking db, tableName" + table.getTableName()
                               + "has unexpected data " + dbRowsResult + "\n");
                } else if (!assertEmpty && dbRowsResult.size() <= 0) {
                    isSame = false;
                    err.append("Failed checking DB, tableName: " + table.getTableName()
                               + " does not contains data: " + rowData + "\n");
                }

            } catch (Exception e) {
                isSame = false;
                throw new ActsTestException("sql="
                                            + sql
                                            + ",args="
                                            + ToStringBuilder.reflectionToString(valsOfArgs,
                                                ToStringStyle.SHORT_PREFIX_STYLE), e);
            }
            for (Map<String, Object> dbOneRowResult : dbRowsResult) {
                //All results meet expectations
                Iterator<String> iterator = rowData.keySet().iterator();
                while (iterator.hasNext()) {
                    String fieldName = iterator.next();
                    Object expectedFieldValue = rowData.get(fieldName);
                    //Add variables to the context parameter list
                    if (String.valueOf(expectedFieldValue).startsWith("=")) {
                        paramBuffer.put(String.valueOf(expectedFieldValue).replace("=", ""),
                            dbOneRowResult.get(fieldName));
                        continue;
                    }

                    // Uniformly use Velocity as variable substitution
                    String expectedFieldValueStr = String.valueOf(expectedFieldValue);
                    if (expectedFieldValueStr.indexOf("$") != -1) {
                        String parsedValue = VelocityUtil.evaluateString(paramBuffer,
                            expectedFieldValueStr);
                        if (parsedValue.indexOf("$") != -1) { // There are also variables that cannot be parsed
                            throw new ActsTestException(
                                "param " + fieldName + " value not found, variable " + fieldName
                                        + " has no assigned value before querying table: "
                                        + table.getTableName());
                        }
                        expectedFieldValue = parsedValue;
                    }

                    //obtain current flag, compatible with case
                    String currentFlag = table.getFlagByFieldNameIgnoreCase(fieldName);

                    if (currentFlag != null) {
                        if (currentFlag.equalsIgnoreCase("N")) {
                            continue;
                        } else if (currentFlag.startsWith("D")) {
                            //date
                            String tmp = currentFlag.replace("D", "");
                            long timeFlow = Long.valueOf(StringUtils.isEmpty(tmp) ? "0" : tmp);
                            Date real = null;
                            Date expect = null;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                if (dbOneRowResult.get(fieldName) instanceof Date) {
                                    real = (Date) dbOneRowResult.get(fieldName);
                                } else {

                                    real = sdf.parse((String) dbOneRowResult.get(fieldName));

                                }
                                if (expectedFieldValue instanceof Date) {
                                    expect = (Date) expectedFieldValue;
                                } else if (((String) expectedFieldValue).equalsIgnoreCase("now()")) {
                                    expect = new Date();
                                } else if (((String) expectedFieldValue).equalsIgnoreCase("today")) {
                                    expect = new Date();
                                } else {
                                    expect = sdf.parse((String) expectedFieldValue);
                                }
                            } catch (ParseException e) {
                                throw new ActsTestException(table.getTableName() + " key:  "
                                                            + fieldName
                                                            + " is not Date or valid date format"
                                                            + err);
                            }
                            Long realTime = real.getTime();
                            Long expectTime = expect.getTime();
                            //different from the given value
                            if (Math.abs((realTime - expectTime) / 1000) > timeFlow) {
                                isSame = false;
                                err.append("Failed checking db param, tableName: "
                                           + table.getTableName() + " key:  " + fieldName
                                           + " ,value is " + real + " expect vaule is " + expect
                                           + "time shift: " + real.compareTo(expect) + " is over "
                                           + timeFlow + "\n");

                            }
                            continue;
                        } else if (currentFlag.startsWith("R")) {
                            Pattern pattern = Pattern.compile(expectedFieldValue.toString());
                            Matcher matcher = pattern.matcher(dbOneRowResult.get(fieldName)
                                .toString());
                            boolean matchRes = matcher.matches();
                            if (!matchRes) {
                                isSame = false;
                                err.append("The comparison of the db fields is failed, table:"
                                           + table.getTableName() + " key:" + fieldName
                                           + " ,value is " + dbOneRowResult.get(fieldName)
                                           + " expect vaule is " + expectedFieldValue + "\n");
                            }
                            continue;
                        } else if (currentFlag.equalsIgnoreCase("J")) {
                            //Comparison of JSON strings
                            String valueExp = String.valueOf(expectedFieldValue);
                            String valueDb = String.valueOf(dbOneRowResult.get(fieldName));
                            if (!JSONObject.parseObject(valueExp).equals(
                                JSONObject.parseObject(valueDb))) {
                                isSame = false;
                                err.append("The comparison of the db fields (JSON) is failed, table:"
                                           + table.getTableName()
                                           + " key:"
                                           + fieldName
                                           + " ,value is "
                                           + dbOneRowResult.get(fieldName)
                                           + " expect vaule is " + expectedFieldValue + "\n");
                            }
                            continue;
                        } else if (currentFlag.equalsIgnoreCase("L")) {
                            //Problems with large field wrapping
                            String valueExp = PropertiesUtil.convert2String(
                                transStringToMap(String.valueOf(expectedFieldValue)), false);
                            String valueDb = String.valueOf(dbOneRowResult.get(fieldName));
                            if (!StringUtils.equals(valueExp, valueDb)) {
                                isSame = false;
                                err.append("The comparison of the db fields is failed, table:"
                                           + table.getTableName() + " key:" + fieldName
                                           + " ,value is " + dbOneRowResult.get(fieldName)
                                           + " expect vaule is " + expectedFieldValue + "\n");
                            }
                            continue;
                        } else if (currentFlag.startsWith("P")) {
                            currentFlag = StringUtils.trim(currentFlag);

                            // Custom separator
                            String customSeparator = ";";
                            if (currentFlag.length() > 1) {
                                customSeparator = currentFlag.substring(1);
                            }

                            //According to the extension field to match the fields in the db
                            Map<String, String> valueExpMap = convertToMap(
                                String.valueOf(expectedFieldValue), customSeparator);

                            String valueDbStr = String.valueOf(dbOneRowResult.get(fieldName));
                            Map<String, String> valueDbMap = PropertiesUtil.toMap(PropertiesUtil
                                .restoreFromString(valueDbStr));
                            for (Entry<String, String> entry : valueExpMap.entrySet()) {
                                String extKey = entry.getKey();
                                String valueExp = entry.getValue();
                                String valueDb = valueDbMap.get(extKey);
                                if (!StringUtils.equals(valueExp, valueDb)) {
                                    //Is unicode？
                                    if (valueExp != null && valueExp.startsWith("\\u")) {
                                        String decodeValueExp = decodeUnicodeStr(valueExp);
                                        if (StringUtils.equals(decodeValueExp, valueDb)) {
                                            continue;
                                        }
                                    }
                                    isSame = false;
                                    err.append("The comparison of the db fields is failed, table:"
                                               + table.getTableName() + " key:" + fieldName + "."
                                               + extKey + " ,value is " + valueDb
                                               + " expect vaule is " + valueExp + "\n");
                                }
                            }

                            continue;
                        }
                    }

                    Object valueFromDB = dbOneRowResult.get(fieldName);
                    if (expectedFieldValue instanceof Date) {
                        Date real = (Date) dbOneRowResult.get(fieldName);
                        Date expect = (Date) expectedFieldValue;

                        if (real.compareTo(expect) != 0) {
                            isSame = false;
                            err.append("The comparison of the db fields is failed, table:"
                                       + table.getTableName() + " key:" + fieldName + " ,value is "
                                       + real + " expect vaule is " + expect + "\n");
                            continue;
                        }
                    } else {
                        if (String.valueOf(expectedFieldValue).equalsIgnoreCase("now()")) {
                            expectedFieldValue = new Date();
                        }

                        //Do not distinguish between "" and null of string fields in the database
                        if (!isNullBlankDiff()
                            && (expectedFieldValue == null || StringUtils.equals(
                                String.valueOf(expectedFieldValue), ""))) {

                            if (valueFromDB != null) {

                                if (!StringUtils.equals(String.valueOf(valueFromDB), "null")
                                    && !StringUtils.equals(String.valueOf(valueFromDB), "")) {
                                    isSame = false;
                                    err.append("The comparison of the db fields is failed, table:"
                                               + table.getTableName() + " key:" + fieldName
                                               + " ,value is " + valueFromDB + " expect vaule is "
                                               + expectedFieldValue + "\n");

                                }

                            }

                            continue;

                        }
                        if (!String.valueOf(expectedFieldValue).equals(String.valueOf(valueFromDB))) {
                            isSame = false;
                            err.append("The comparison of the db fields is failed, table:"
                                       + table.getTableName() + " key:" + fieldName + " ,value is "
                                       + dbOneRowResult.get(fieldName) + " expect vaule is "
                                       + expectedFieldValue + "\n");
                            continue;
                        }

                    }
                    log.info("The comparison of the db fields is successful, table:"
                             + table.getTableName() + " ,key:" + fieldName + " ,value is "
                             + dbOneRowResult.get(fieldName) + " expect vaule is "
                             + expectedFieldValue);

                }
            }
        }

        if (!isSame) {
            //throw new ActsTestException("Failed checking DB" + err);
            allErr.append(err);
            log.error("Failed checking DB:\n" + err);
        }

        return isSame;

    }

    /**
     * Convert Unicode encoding to string
     */
    private String decodeUnicodeStr(String unicodeStr) {
        StringBuilder string = new StringBuilder();
        String[] hex = unicodeStr.split("\\\\u");
        for (int t = 1; t < hex.length; t++) {
            int data = Integer.parseInt(hex[t], 16);
            string.append((char) data);
        }
        return string.toString();
    }

    /**
     * Whether the database row is not expected to be generated
     * @param table
     * @param fields
     * @return true: not expected to be generated；false: expected to be generated
     */
    private boolean willAssertEmpty(VirtualTable table, List<String> fields) {
        if (table.getFlags() != null) {
            for (String field : fields) {
                //compatible with case of flag
                String currentFlag = table.getFlagByFieldNameIgnoreCase(field);

                if (currentFlag != null) {
                    if (currentFlag.equalsIgnoreCase("cn")) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    /**
     * Find the true value of the select field
     * @param selectKeys Table fields that need to be selected
     * @param rowData One row of data in a table
     * @param tableName table name
     * @return selectKeys The real value of the field
     */
    private Object[] getValsForSelectKeys(String[] selectKeys, Map<String, Object> rowData,
                                          String tableName) {
        Object[] vals = new Object[selectKeys.length];
        Map<String, Object> paramBuffer = ComponentsActsRuntimeContextThreadHold.getContext().paramMap;
        for (int i = 0; i < selectKeys.length; i++) {
            if (String.valueOf(rowData.get(selectKeys[i])).startsWith("=")) {
                throw new ActsTestException("tableName: " + tableName + " condition: "
                                            + selectKeys[i] + "cannot be assignment statement&");
            }
            if (String.valueOf(rowData.get(selectKeys[i])).startsWith("$")) {
                String key = String.valueOf(rowData.get(selectKeys[i])).replace("$", "");
                if (paramBuffer.containsKey(key)) {
                    vals[i] = paramBuffer.get(key);
                } else {
                    throw new ActsTestException("param" + selectKeys[i]
                                                + "value not found, variable $" + key
                                                + " must be assigned before querying table: "
                                                + tableName);
                }
            } else {
                vals[i] = rowData.get(selectKeys[i]);
            }
        }

        return vals;
    }

    /**
     * Get query conditions
     *
     * @param table
     * @param fields
     * @return
     */
    public String[] getSelectKeys(VirtualTable table, List<String> fields) {

        List<String> tmpSelectKeys = new ArrayList<String>();
        if (table.getFlags() != null) {
            for (String field : fields) {
                //obtain flag of current field，if get，compatible with case of Flag，Value
                String currentFlag = table.getFlagByFieldNameIgnoreCase(field);
                if (currentFlag != null) {
                    if (currentFlag.equalsIgnoreCase("c")) {
                        tmpSelectKeys.add(field);
                    }
                    if (currentFlag.equalsIgnoreCase("cn")) {
                        tmpSelectKeys.add(field);
                    }
                }
            }

        }
        if (dataAccessConfigManager != null) {
            DataAccessConfig dataAccessConfig = dataAccessConfigManager.findDataAccessConfig(table
                .getDataObjClazz());

            if (dataAccessConfig != null) {
                for (String configKey : dataAccessConfig.getSelectKeys()) {
                    if (!tmpSelectKeys.contains(configKey)) {
                        tmpSelectKeys.add(configKey);
                    }
                }
            }
        }
        return tmpSelectKeys.toArray(new String[0]);
    }

    /**
     * get DB type
     *
     * @param url
     * @return
     */
    public String getDBType(String url) {

        if (StringUtils.isBlank(url)) {
            return "mysql";
        }
        if (url.indexOf("mysql") != -1) {
            return "mysql";
        } else if (url.indexOf("oracle") != -1) {
            return "oracle";
        } else {
            return "mysql";
        }

    }

    /**
     * Get data source template
     *
     * @param tableName
     * @return
     */
    public JdbcTemplate getJdbcTemplate(String tableName) {
        String dsName = DataAccessConfigManager.findDataSourceName(tableName);
        if (StringUtils.isBlank(dsName)) {
            log.error("Table not Found. tableName="
                      + tableName
                      + "add datasource config in [acts-config.properties], modify variable [ds_datasource]");
        }
        return jdbcTemplateMap.get(dsName);
    }

    /**
     * get datasource
     *
     * @param dsName
     * @param bundleName
     * @return
     */
    public DataSource getDataSource(String dsName, String bundleName) {
        DataSource ds = ((DataSource) applicationContext.getBean(dsName));
        if (ds == null) {
            //default on-the-fly-bundle
            ds = ((DataSource) applicationContext.getBean(dsName));
        }

        return ds;
    }

    /**
     * String to map for large
     *
     * @param mapString
     * @return
     */
    public static Map<String, String> transStringToMap(String mapString) {
        Map<String, String> map = new HashMap<String, String>();
        java.util.StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(mapString, ";"); entrys.hasMoreTokens(); map
            .put(items.nextToken(), items.hasMoreTokens() ? ((String) (items.nextToken())) : null)) {
            items = new StringTokenizer(entrys.nextToken(), "=");
        }
        return map;
    }

    /**
     * Convert large fields in a data table to a map structure
     * @param expectedDbStr The expected value filled in the data sheet
     * @param separator Custom separator, default English semicolon
     * @return
     */
    private Map<String, String> convertToMap(String expectedDbStr, String separator) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isNotBlank(expectedDbStr)) {
            if (separator == null) {
                separator = ";";
            }

            String[] pairs = StringUtils.split(expectedDbStr, separator);
            if (pairs != null && pairs.length > 0) {
                for (String pair : pairs) {
                    String[] keyAndValue = pair.split("=");
                    if (keyAndValue != null && keyAndValue.length == 2) {
                        map.put(keyAndValue[0], keyAndValue[1]);
                    }
                }
            }
        }

        return map;
    }

    private boolean isNullBlankDiff() {
        String isdiff = ActsConfiguration.getInstance().getActsConfigMap()
            .get(ActsConstants.IS_DB_NULL_BLANK_DIFF);

        if (StringUtils.isNotBlank(isdiff)) {
            return Boolean.valueOf(isdiff).booleanValue();
        } else {
            return false;
        }
    }

    /**
     * Executing annotations
     * 
     * @param methods
     * @param tableName
     * @param groupId
     */
    private void Execute(List<IVTableGroupCmdMethod> methods, String tableName, String groupId) {

        for (IVTableGroupCmdMethod m : methods) {
            m.invoke(tableName, groupId);
        }
    }

    /**
     * Gets jdbc template map.
     *
     * @return the jdbc template map
     */
    public Map<String, JdbcTemplate> getJdbcTemplateMap() {
        return jdbcTemplateMap;
    }

    /**
     * Sets jdbc template map.
     *
     * @param jdbcTemplateMap the jdbc template map
     */
    public void setJdbcTemplateMap(Map<String, JdbcTemplate> jdbcTemplateMap) {
        this.jdbcTemplateMap = jdbcTemplateMap;
    }

    /**
     * Gets data access config manager.
     *
     * @return the data access config manager
     */
    public DataAccessConfigManager getDataAccessConfigManager() {
        return dataAccessConfigManager;
    }

    /**
     * Sets data access config manager.
     *
     * @param dataAccessConfigManager the data access config manager
     */
    public void setDataAccessConfigManager(DataAccessConfigManager dataAccessConfigManager) {
        this.dataAccessConfigManager = dataAccessConfigManager;
    }

    /**
     * Getter method for property <tt>beforeVTableExecuteMethodList</tt>.
     * 
     * @return property value of beforeVTableExecuteMethodList
     */
    public List<IVTableGroupCmdMethod> getBeforeVTableExecuteMethodList() {
        return beforeVTableExecuteMethodList;
    }

    /**
     * Getter method for property <tt>afterVTableExecuteMethodList</tt>.
     * 
     * @return property value of afterVTableExecuteMethodList
     */
    public List<IVTableGroupCmdMethod> getAfterVTableExecuteMethodList() {
        return afterVTableExecuteMethodList;
    }

    /**
     * Getter method for property <tt>specialGroupIds</tt>.
     * 
     * @return property value of specialGroupIds
     */
    public List<String> getSpecialGroupIds() {
        return specialGroupIds;
    }

    /**
     * Add special group ids.
     *
     * @param groupIds the group ids
     */
    public void addSpecialGroupIds(String[] groupIds) {
        this.specialGroupIds.addAll(Arrays.asList(groupIds));
    }

}
