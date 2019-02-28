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

import au.com.bytecode.opencsv.CSVWriter;
import com.alipay.test.acts.constant.ActsPathConstants;
import com.alipay.test.acts.db.enums.CSVColEnum;
import com.alipay.test.acts.exception.ActsException;
import com.alipay.test.acts.log.ActsLogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DB template generation tool
 *
 * @author xiaoleicxl
 * @version $Id: dbTableModelUtil.java, v 0.1 2015年10月22日 下午10:20:08 xiaoleicxl Exp $
 */
public class DbTableModelUtil {

    private static final Log LOG = LogFactory.getLog(DbTableModelUtil.class);

    /**
     * generate a single table template
     * @param conn
     * @param table
     * @param dbType "ORACLE"\"OB"\"MYSQL"
     */
    public static void genDBCSVFile(String csvModelRootPath, Connection conn, String table,
                                    String dataSql, String dbType, String encode) {

        if (StringUtils.equals(dbType, "OB") || StringUtils.equals(dbType, "MYSQL")) {

            List<Map<String, Object>> result = null;
            List<Map<String, Object>> dataResult = null;
            String sqlOBorMysql = "desc " + table;
            result = executeQuerySql(conn, sqlOBorMysql);
            if (StringUtils.isNotBlank(dataSql)) {
                dataResult = executeQuerySql(conn, dataSql);
            }
            if (result.isEmpty()) {
                throw new ActsException(table
                                        + "Failed to generate template! Please check the query sql");
            }

            //Get the table name without sub-library table
            String tableName = table;
            while (StringUtils.isNumeric(StringUtils.substring(tableName, tableName.length() - 1,
                tableName.length()))) {
                tableName = StringUtils.substringBeforeLast(tableName, "_");
            }

            File dbModel = FileUtil.getTestResourceFileByRootPath(csvModelRootPath + "/"
                                                                  + ActsPathConstants.DB_DATA_PATH);
            if (!dbModel.exists()) {
                dbModel.mkdir();
            }
            genMysqlDBCSVFile(csvModelRootPath + "/" + ActsPathConstants.DB_DATA_PATH + tableName
                              + ".csv", result, dataResult, encode);

        }

        if (StringUtils.equals(dbType, "ORACLE")) {

            List<Map<String, Object>> dataResult = null;
            Map<String, Map<String, String>> result = null;
            String sqlOracle = "select COLUMN_NAME,DATA_TYPE,DATA_LENGTH,NULLABLE　from user_tab_columns where table_name =UPPER('"
                               + table + "')";

            result = getOracleColumnInfo(conn, sqlOracle);
            if (StringUtils.isNotBlank(dataSql)) {
                dataResult = executeQuerySql(conn, dataSql);
            }

            if (result.isEmpty()) {
                sqlOracle = "select COLUMN_NAME,DATA_TYPE,DATA_LENGTH,NULLABLE　from all_tab_columns where table_name =UPPER('"
                            + table + "')";
                result = getOracleColumnInfo(conn, sqlOracle);
            }
            //Get the table name without sub-library table
            String tableName = table;
            while (StringUtils.isNumeric(StringUtils.substring(tableName, tableName.length() - 1,
                tableName.length()))) {
                tableName = StringUtils.substringBeforeLast(tableName, "_");
            }

            genOracleDBCSVFile(csvModelRootPath + "/" + ActsPathConstants.DB_DATA_PATH + tableName
                               + ".csv", result, dataResult, encode);

        }

    }

    public static void genMysqlDBCSVFile(String csvRootPath, List<Map<String, Object>> map,
                                         List<Map<String, Object>> dataMap, String encode) {
        if (StringUtils.isBlank(csvRootPath)) {
            throw new ActsException("The path is empty and the CSV file cannot be generated.");
        }
        File file = FileUtil.getTestResourceFileByRootPath(csvRootPath);

        //if file exists then return
        if (file.exists() || map.isEmpty()) {
            return;
        }
        List<String[]> outputValues = new ArrayList<String[]>();
        //Assemble the first line of the csv file : the header line
        List<String> header = new ArrayList<String>();
        header.add(CSVColEnum.COLUMN.getCode());
        header.add(CSVColEnum.COMMENT.getCode());
        header.add(CSVColEnum.TYPE.getCode());
        header.add(CSVColEnum.RULE.getCode());
        header.add(CSVColEnum.FLAG.getCode());

        if (dataMap != null && !dataMap.isEmpty()) {
            for (int i = 1; i < dataMap.size() + 1; i++) {
                header.add("value" + i);
            }
        } else {
            header.add("value");
        }
        outputValues.add(header.toArray(new String[header.size()]));

        for (Map<String, Object> childMap : map) {
            List<String> value = new ArrayList<String>();
            String columnName = null;
            if (!(childMap.get("field") == null)) {
                //ob
                columnName = childMap.get("field").toString();
            } else if (!(childMap.get("Field") == null)) {
                //mysql
                columnName = childMap.get("Field").toString();
            }
            String comment = "";
            String columnType = "";
            String columnRule = "";
            String flag;
            if (childMap.get("key") != null) {
                comment = childMap.get("comment") == null ? "" : childMap.get("comment").toString();
                columnType = childMap.get("type") == null ? "" : childMap.get("type").toString();

            } else if (childMap.get("Key") != null) {
                comment = childMap.get("Comment") == null ? "" : childMap.get("Comment").toString();
                columnType = childMap.get("Type") == null ? "" : childMap.get("Type").toString();

            }

            //default check
            flag = "Y";

            value.add(columnName);
            value.add(comment);
            value.add(columnType);
            value.add(columnRule);
            value.add(flag);
            if (dataMap != null) {
                for (Map<String, Object> cloumValue : dataMap) {
                    if (null != cloumValue.get(columnName)) {
                        value.add((String) cloumValue.get(columnName).toString());
                    } else {
                        value.add("");
                    }

                }
            } else {
                if (columnName.equals("currency")) {
                    value.add("156");
                } else {
                    value.add("");
                }
            }
            outputValues.add(value.toArray(new String[value.size()]));

        }
        writeToCsv(file, outputValues, encode);
    }

    public static void genOracleDBCSVFile(String csvRootPath, Map<String, Map<String, String>> map,
                                          List<Map<String, Object>> dataMap, String encode) {
        if (StringUtils.isBlank(csvRootPath)) {
            throw new ActsException("The path is empty and the CSV file cannot be generated.");
        }
        File file = FileUtil.getTestResourceFileByRootPath(csvRootPath);

        if (file.exists() || map.isEmpty()) {
            return;
        }
        List<String[]> outputValues = new ArrayList<String[]>();

        //Assemble the first line of the csv file : the header line
        List<String> header = new ArrayList<String>();
        header.add(CSVColEnum.COLUMN.getCode());
        header.add(CSVColEnum.COMMENT.getCode());
        header.add(CSVColEnum.TYPE.getCode());
        header.add(CSVColEnum.RULE.getCode());
        header.add(CSVColEnum.FLAG.getCode());

        if (dataMap != null && !dataMap.isEmpty()) {
            for (int i = 1; i < dataMap.size() + 1; i++) {
                header.add("value" + i);
            }
        } else {
            header.add("value");
        }
        outputValues.add(header.toArray(new String[header.size()]));

        for (String column : map.keySet()) {
            List<String> value = new ArrayList<String>();
            String columnName = column;
            String comment = "";
            String columnType = map.get(column).get("DATA_TYPE") + "("
                                + map.get(column).get("DATA_LENGTH") + ")";
            String columnRule = "";
            String flag = "Y";

            value.add(columnName);
            value.add(comment);
            value.add(columnType);
            value.add(columnRule);
            value.add(flag);
            if (dataMap != null) {
                for (Map<String, Object> cloumValue : dataMap) {
                    if (null != cloumValue.get(columnName)) {
                        value.add((String) cloumValue.get(columnName).toString());
                    } else {
                        value.add("");
                    }
                }
            } else {
                if (columnName.equals("currency")) {
                    value.add("156");
                } else {
                    value.add("");
                }
            }
            outputValues.add(value.toArray(new String[value.size()]));

        }
        writeToCsv(file, outputValues, encode);
    }

    public static void writeToCsv(File file, List<String[]> outputValues, String encode) {

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (Exception e) {
            ActsLogUtil.error(LOG, "failed to find file [" + file.getName() + "].", e);
        }
        //Write the generated content to a CSV file
        try {
            OutputStreamWriter osw = null;
            osw = new OutputStreamWriter(outputStream, Charset.forName(encode));
            CSVWriter csvWriter = new CSVWriter(osw);
            csvWriter.writeAll(outputValues);
            csvWriter.close();
            ActsLogUtil.warn(LOG, file.getName() + " generated successfully");
        } catch (Exception e) {
            ActsLogUtil.fail(LOG, "Failed to generate CSV file:" + file.getName(), e);
        }
    }

    public static List<Map<String, Object>> executeQuerySql(Connection conn, String sql) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> rowMap = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    rowMap.put(columnName, value);
                }
                result.add(rowMap);
            }
            return result;
        } catch (Exception e) {
            ActsLogUtil.error(LOG, "sql execute exception ，sql=" + sql, e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                ActsLogUtil.error(LOG, "close connection exception!!", e);
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    public static Map<String, Map<String, String>> getOracleColumnInfo(Connection con, String sql) {

        Statement stmt = null;
        ResultSet rs = null;
        Map<String, Map<String, String>> map = new LinkedHashMap<String, Map<String, String>>();
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Map<String, String> cloumMap = new LinkedHashMap<String, String>();
                String key = rs.getString("COLUMN_NAME");
                cloumMap.put("DATA_TYPE", rs.getString("DATA_TYPE"));
                cloumMap.put("DATA_LENGTH", rs.getString("DATA_LENGTH"));
                cloumMap.put("NULLABLE", rs.getString("NULLABLE"));
                map.put(key, cloumMap);
            }
        } catch (Exception e) {
            ActsLogUtil.error(LOG, "query exception!", e);
        } finally {
            try {
                if (stmt != null) {
                    if (rs != null) {
                        rs.close();
                    }
                    stmt.close();
                }
            } catch (Exception e) {
                ActsLogUtil.error(LOG, "close connection exception!", e);
            }
        }
        return map;

    }

    static void genDOCSVFile(String testResourcePath, ClassLoader clsLoader, String fullClassName,
                             String encode) {

        try {
            if (StringUtils.isBlank(testResourcePath)) {
                ActsLogUtil.warn(LOG, "The path is empty and the CSV file cannot be generated.");
                return;
            }

            File dbModel = FileUtil.getTestResourceFileByRootPath(testResourcePath + "/"
                                                                  + ActsPathConstants.DB_DATA_PATH);
            if (!dbModel.exists()) {
                dbModel.mkdir();
            }

            String tableName = StringUtils.substringAfterLast(fullClassName, ".");
            String csvModelRootPath = testResourcePath + "/" + ActsPathConstants.DB_DATA_PATH
                                      + tableName + ".csv";

            File file = FileUtil.getTestResourceFileByRootPath(csvModelRootPath);

            if (null == fullClassName) {
                ActsLogUtil.warn(LOG, "The DO class name is empty and cannot generate a CSV file.");
                return;
            }

            if (file.exists()) {
                ActsLogUtil.warn(LOG, "file [" + csvModelRootPath
                                      + "] already exist, skip directly");
                return;
            }

            Class<?> cls = clsLoader.loadClass(fullClassName);
            Field[] classFields = cls.getDeclaredFields();

            List<String[]> outputValues = new ArrayList<String[]>();
            //Assemble the first line of the csv file : the header line
            List<String> header = new ArrayList<String>();
            header.add(CSVColEnum.COLUMN.getCode());
            header.add(CSVColEnum.COMMENT.getCode());
            header.add(CSVColEnum.TYPE.getCode());
            header.add(CSVColEnum.RULE.getCode());
            header.add(CSVColEnum.FLAG.getCode());
            header.add("value");

            outputValues.add(header.toArray(new String[header.size()]));

            for (Field field : classFields) {
                List<String> value = new ArrayList<String>();

                String fieldName = field.getName();
                StringBuilder builder = new StringBuilder("");
                for (int i = 0; i < fieldName.length(); i++) {
                    char c = fieldName.charAt(i);
                    if (Character.isUpperCase(c)) {
                        builder.append("_" + StringUtils.lowerCase(String.valueOf(c)));

                    } else {
                        builder.append(String.valueOf(c));
                    }
                }
                //COLUMN
                value.add(builder.toString());
                //COMMENT
                value.add("");
                //TYPE
                value.add("");
                //RULE
                value.add("");
                //FLAG
                value.add("Y");
                //VALUE
                value.add("");

                outputValues.add(value.toArray(new String[value.size()]));

            }

            writeToCsv(file, outputValues, encode);

        } catch (ClassNotFoundException e) {
            ActsLogUtil.error(LOG, "genDOCSVFile error! ", e);
        }
    }
}
