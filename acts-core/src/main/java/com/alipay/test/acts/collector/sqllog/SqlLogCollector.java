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
package com.alipay.test.acts.collector.sqllog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections.MapUtils;
import org.springframework.util.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alipay.test.acts.model.VirtualTable;

/**
 * SQL log collector
 * 
 * @author chao.gao
 * @author hongling.xiang
 * @version $Id: SqlLogCollector.java, v 0.1 2015-10-26 9:41:48 a.m
 *          hongling.xiang Exp $
 */
public class SqlLogCollector {

    /** log */
    private static final Log     log               = LogFactory.getLog(SqlLogCollector.class);

    /** insert sql regex */
    private static final Pattern insertSqlParttner = Pattern
                                                       .compile("insert([\\s\\S]*) into([\\s\\S]*) values([\\s\\S]*)");

    /** update sql regex */
    private static final Pattern updateSqlParttner = Pattern
                                                       .compile("update([\\s\\S]*) set([\\s\\S]*) where([\\s\\S]*) ");

    public static final String   START_FLAG        = "Start acts_caseId=";
    public static final String   FINISH_FLAG       = "Finish acts_caseId=";

    /**
     * collect sql logs in specified logfile
     * 
     * <p>
     * reading logfile(avoid any complex computation)
     * </p>
     * 
     * @param sqlLogfileName
     * @return
     * @throws IOException
     */
    public static Map<String, List<List<String>>> collectConcernedSqlLog(String sqlLogfileName,
                                                                         String caseId) {

        Map<String, List<List<String>>> caseSqlLogLines = new HashMap<String, List<List<String>>>();

        BufferedReader logReader = null;
        try {
            logReader = new BufferedReader(new FileReader(sqlLogfileName));

            doCollectSqlLog(sqlLogfileName, logReader, caseSqlLogLines, caseId);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            try {
                if (null != logReader) {
                    logReader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return caseSqlLogLines;
    }

    /**
     * collect SQL log
     * 
     * @param sqlLogfileName
     * @param logReader
     * @param caseSqlLogLines restore<caseId|component,log content>
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void doCollectSqlLog(String sqlLogfileName, BufferedReader logReader,
                                        Map<String, List<List<String>>> caseSqlLogLines,
                                        String orgCaseId) throws IOException {

        String curLine = StringUtils.EMPTY;
        String caseId = getLastCaseId(orgCaseId);
        List<List<String>> singleCaseSqlLogs = new ArrayList<List<String>>();
        boolean isValiable = false;

        List<List<String>> caseSqlLogs = new ArrayList<List<String>>();
        caseSqlLogLines.put(orgCaseId, caseSqlLogs);

        while ((curLine = logReader.readLine()) != null) {
            //end
            if (StringUtils.contains(curLine, caseId) && StringUtils.contains(curLine, FINISH_FLAG)) {
                singleCaseSqlLogs.clear();
                isValiable = false;
                continue;
            }
            //start
            else if (StringUtils.contains(curLine, caseId)
                     && StringUtils.contains(curLine, START_FLAG)) {
                isValiable = true;
            } else {
                if (!isValiable) {
                    continue;
                }
                paseExtSql(curLine, logReader, singleCaseSqlLogs);

                if (!CollectionUtils.isEmpty(singleCaseSqlLogs)) {
                    caseSqlLogLines.get(orgCaseId).addAll(singleCaseSqlLogs);
                }
                singleCaseSqlLogs.clear();
            }
        }
    }

    /**
     * 
     * @param orgCaseId
     */
    public static String getFirstCaseId(String orgCaseId) {

        String firstCaseId = StringUtils.EMPTY;
        if (StringUtils.contains(orgCaseId, "|")) {
            String[] strs = orgCaseId.split("\\|");
            firstCaseId = strs[0];
        } else {
            firstCaseId = orgCaseId;
        }

        return firstCaseId;
    }

    /**
     * last Id of this form, example： Measured method Id||The first component Id||The first nested component Id
     * @param orgCaseId
     */
    public static String getLastCaseId(String orgCaseId) {

        String lastCaseId = StringUtils.EMPTY;
        if (StringUtils.contains(orgCaseId, "|")) {
            String[] strs = orgCaseId.split("\\|");
            lastCaseId = strs[strs.length - 1];
        } else {
            lastCaseId = orgCaseId;
        }

        return lastCaseId;
    }

    /**
     * parse sql
     * @param curLine
     * @param logReader
     * @param singleCaseSqlLogs
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void paseExtSql(String curLine, BufferedReader logReader,
                                  List<List<String>> singleCaseSqlLogs)
                                                                       throws FileNotFoundException,
                                                                       IOException {

        if (StringUtils.contains(curLine, "Executing Statement:")) {
            List<String> singleSqlExecLog = new ArrayList<String>();
            // exec sql line
            singleSqlExecLog.add(curLine);
            // sql param line
            String paramFirstLine = logReader.readLine();

            String currentLine = logReader.readLine();
            while (currentLine != null) {
                if (!StringUtils.contains(currentLine, "Types:")) {
                    paramFirstLine += ";";
                    paramFirstLine += currentLine;
                    currentLine = logReader.readLine();
                } else {
                    break;
                }
            }
            singleSqlExecLog.add(paramFirstLine);

            singleSqlExecLog.add(currentLine);

            singleSqlExecLog = parseToLow(singleSqlExecLog);

            if (isConcernedSql(singleSqlExecLog)) {
                singleCaseSqlLogs.add(singleSqlExecLog);
            }
        }
    }

    /**
     * Convert uppercase to lowercase for subsequent matches
     * @param singleSqlExecLog
     * @return
     */
    public static List<String> parseToLow(List<String> singleSqlExecLog) {
        String execSqlLogLine = singleSqlExecLog.get(0);

        if (execSqlLogLine.contains("INSERT ")) {
            execSqlLogLine = execSqlLogLine.replace("INSERT ", "insert ");
            execSqlLogLine = execSqlLogLine.replace(" INTO", " into");
            execSqlLogLine = execSqlLogLine.replace("VALUES ", "values ");
        }

        if (execSqlLogLine.contains("UPDATE ")) {
            execSqlLogLine = execSqlLogLine.replace("UPDATE ", "update ");
            execSqlLogLine = execSqlLogLine.replace("SET ", "set ");
            execSqlLogLine = execSqlLogLine.replace("WHERE ", "where ");
        }

        singleSqlExecLog.set(0, execSqlLogLine);

        return singleSqlExecLog;
    }

    /**
     * resolve sql log and build VirtualTable object
     * 
     * @param curCaseSqlLogLines
     * @return
     */
    public static List<VirtualTable> parseSqlLog(List<List<String>> curCaseSqlLogLines) {

        // thread safe List
        final List<VirtualTable> virtualTableSet = Collections
            .synchronizedList(new ArrayList<VirtualTable>());

        // resolve all tables associated with a single case
        parseSingleCaseSqlLog(curCaseSqlLogLines, virtualTableSet);

        // remove duplicated tables
        exculeRepeatTableData(virtualTableSet);

        return virtualTableSet;
    }

    /**
     * remove duplicated table data
     * 
     * @param virtualTableSet
     */
    private static void exculeRepeatTableData(List<VirtualTable> virtualTableSet) {

        if (CollectionUtils.isEmpty(virtualTableSet)) {
            return;
        }

        Iterator<VirtualTable> tableIters = virtualTableSet.iterator();

        List<VirtualTable> originalTableSet = new ArrayList<VirtualTable>();
        originalTableSet.addAll(virtualTableSet);

        int index = 0;
        while (tableIters.hasNext()) {
            VirtualTable virtualTable = tableIters.next();
            index++;
            for (int i = index; i < originalTableSet.size(); i++) {
                if (StringUtils.equalsIgnoreCase(virtualTable.getTableName(),
                    originalTableSet.get(i).getTableName())) {
                    List<Map<String, Object>> target = virtualTable.getTableData();
                    List<Map<String, Object>> other = originalTableSet.get(i).getTableData();

                    doExclueRepeatTableData(target, other);

                    if (CollectionUtils.isEmpty(target)) {
                        tableIters.remove();
                    }
                    break;
                }
            }

        }

    }

    /**
     * Delete duplicate data in addition to this object
     * @param target
     * @param other
     * @return
     */
    @SuppressWarnings("unchecked")
    private static void doExclueRepeatTableData(List<Map<String, Object>> target,
                                                List<Map<String, Object>> other) {

        Iterator<Map<String, Object>> targetIter = target.iterator();
        while (targetIter.hasNext()) {

            Map<String, Object> orderMapRow = MapUtils.orderedMap(targetIter.next());
            for (Map<String, Object> map : other) {
                Map<String, Object> orderTempRow = MapUtils.orderedMap(map);
                if (StringUtils.equalsIgnoreCase(orderMapRow.toString(), orderTempRow.toString())) {
                    targetIter.remove();
                    break;
                }
            }

        }

    }

    /**
     * Asynchronous threads resolve sql logs of single case.
     * 
     * @param curCaseSqlLogLines
     * @param virtualTableSet
     */
    private static void parseSingleCaseSqlLog(List<List<String>> curCaseSqlLogLines,
                                              final List<VirtualTable> virtualTableSet) {

        for (final List<String> singleSqlExecLog : curCaseSqlLogLines) {
            try {
                preprocessSqlLog(singleSqlExecLog);

                VirtualTable virtualTable = SqlLogParseFactory.genVirtualTable(
                    getSqlType(singleSqlExecLog.get(0)), singleSqlExecLog);
                if (null != virtualTable) {
                    virtualTableSet.add(virtualTable);
                }

            } catch (Throwable t) {
                log.warn("Collecting case result-unknown exception while parsing SQL ,SQL="
                         + singleSqlExecLog.get(0), t);
            }
        }

    }

    /**
     * Preprocess each line of sql logs, and remove unrelated strings
     * 
     * @param singleSqlExecLog
     */
    public static void preprocessSqlLog(List<String> singleSqlExecLog) {

        String execSqlLogLine = singleSqlExecLog.get(0).trim();
        String sqlParamLogLine = singleSqlExecLog.get(1).trim();
        String sqlParamTypeLine = singleSqlExecLog.get(2).trim();

        execSqlLogLine = execSqlLogLine
            .substring(execSqlLogLine.indexOf("Executing Statement:") + 20);

        sqlParamLogLine = sqlParamLogLine.substring(sqlParamLogLine.indexOf("Parameters:") + 11)
            .trim();
        sqlParamLogLine = sqlParamLogLine.substring(1, sqlParamLogLine.length() - 1);

        sqlParamTypeLine = sqlParamTypeLine.substring(
            StringUtils.lastIndexOf(sqlParamTypeLine, "Types:") + 6).trim();
        sqlParamTypeLine = sqlParamTypeLine.substring(1, sqlParamTypeLine.length() - 1);

        singleSqlExecLog.set(0, execSqlLogLine);
        singleSqlExecLog.set(1, sqlParamLogLine);
        singleSqlExecLog.set(2, sqlParamTypeLine);
    }

    /**
     * get sql type
     * 
     * @param sql
     * @return
     */
    private static String getSqlType(String sql) {

        if (insertSqlParttner.matcher(sql).find()) {
            return SqlTypeEnum.INSERT_SQL.getCode();
        }

        if (updateSqlParttner.matcher(sql).find()) {
            return SqlTypeEnum.UPDATE_SQL.getCode();
        }

        throw new Error("unknown sql type：" + sql);
    }

    /**
     * Focus only insert, update sql
     * 
     * @param singleSqlExecLog
     * @return
     */
    private static boolean isConcernedSql(List<String> singleSqlExecLog) {

        String execSqlLogLine = singleSqlExecLog.get(0);
        String sqlParamLogLine = singleSqlExecLog.get(1);
        String sqlParamTypeLine = singleSqlExecLog.get(2);

        if (StringUtils.isBlank(execSqlLogLine) || StringUtils.isBlank(sqlParamLogLine)
            || StringUtils.isBlank(sqlParamTypeLine)) {
            return false;
        }

        if (!StringUtils.contains(sqlParamLogLine, "Parameters:")) {
            return false;
        }

        if (!StringUtils.contains(sqlParamTypeLine, "Types:")) {
            return false;
        }

        if (insertSqlParttner.matcher(execSqlLogLine.trim()).find()
            || updateSqlParttner.matcher(execSqlLogLine.trim()).find()) {
            return true;
        }

        return false;
    }
}
