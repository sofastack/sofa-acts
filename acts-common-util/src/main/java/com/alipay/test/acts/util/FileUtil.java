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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.alipay.test.acts.context.ActsSuiteContextHolder;
import com.alipay.test.acts.log.ActsLogUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author baishuo.lp
 * @version $Id: FileUtil.java, v 0.1 2015年2月8日 上午1:51:39 baishuo.lp Exp $
 */
public class FileUtil {
    private static final Log    LOG          = LogFactory.getLog(FileUtil.class);

    private static final String DEFAULT_PATH = "/src/test/resources/";

    private static String       DIR_PATH     = System.getProperty("user.dir");

    /**
     * Based on Test Bundle, Get relative path file from src/test/resources
     *
     * @param fileRelativePath
     * @return
     */
    public static File getTestResourceFile(String fileRelativePath) {
        String fileFullPath = DIR_PATH + DEFAULT_PATH + fileRelativePath;
        File file = new File(fileFullPath);
        return file;
    }

    /**
     * Based on Test Bundle, Get relative path file from absolute paths
     *
     * @param fileRootPath
     * @return
     */
    public static File getTestResourceFileByRootPath(String fileRootPath) {
        if (StringUtils.isBlank(fileRootPath)) {
            return null;
        }
        File file = new File(fileRootPath);
        return file;
    }

    /**
     * Read files from relative paths
     *
     * @param relatePath
     * @return
     */
    public static String readFile(String relatePath) {
        try {
            InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(relatePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            LOG.error("Failed to read data from " + relatePath, e);
            return null;
        }
    }

    /**
     * Based on the original path, modify the csv jump-path to the absolute path under the test bundle.
     * Give priority to the csv path in the driver folder, followed by the relative path under the test bundle
     *
     * @param targetFilePath
     * @param originFilePath
     * @return
     */
    public static String getRelativePath(String targetFilePath, String originFilePath) {
        if (StringUtils.isBlank(originFilePath)) {
            originFilePath = "";
        } else {
            originFilePath = originFilePath.contains(File.separator) ? originFilePath.substring(0,
                originFilePath.lastIndexOf(File.separator) + 1) : "";
        }
        String finalFilePath = originFilePath + targetFilePath;
        File file = new File(finalFilePath);
        if (file.exists()) {
            //relative path under the test bundle
            return finalFilePath;
        } else {
            if (ActsSuiteContextHolder.exists()) {
                finalFilePath = ActsSuiteContextHolder.get().getCsvFolderPath() + targetFilePath;
                file = getTestResourceFile(finalFilePath);
                if (file.exists()) {
                    return finalFilePath;
                }
            }
            file = getTestResourceFile(targetFilePath);
            if (file.exists()) {

                return targetFilePath;
            } else {
                ActsLogUtil.fail(LOG, "file not fund，targetFilePath = " + targetFilePath
                                      + ", originFolderPath = " + originFilePath);
            }
        }
        return null;
    }

    /**
     * 打印字符串到新文件
     * mode=-1: skip
     * mode=0: append
     * mode=1: new
     *
     * @param file
     * @param fileContent
     * @return
     */
    public static boolean writeFile(File file, String fileContent, int mode) {
        try {
            if (file.exists()) {
                switch (mode) {
                    case -1:
                        ActsLogUtil.debug(LOG, "File already exists, skip");
                        return true;
                    case 0:
                        ActsLogUtil.debug(LOG,
                            "The file already exists, add a separator to continue writing");
                        fileContent = "\n\n========================================================================\n\n"
                                      + fileContent;
                        break;
                    case 1:
                        ActsLogUtil.debug(LOG, "File already exists, delete and rewrite");
                        file.delete();
                        file.createNewFile();
                        break;
                    default:
                        return false;
                }
            } else {
                file.createNewFile();
            }
            Writer writer = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
            writer.write(fileContent);
            writer.close();
            ActsLogUtil.debug(LOG, "written successfully");
            return true;
        } catch (Exception e) {
            ActsLogUtil.fail(LOG, "written failed", e);
            return false;
        }
    }

    /**
     * read yaml file
     *
     * @param yamlFile
     * @return
     */
    public static LinkedHashMap<?, ?> readYaml(File yamlFile) {
        InputStream is = null;
        try {
            is = new FileInputStream(yamlFile);
            InputStreamReader reader = new InputStreamReader(is);
            Iterator<Object> iterator = new Yaml().loadAll(reader).iterator();
            LinkedHashMap<?, ?> rawData = (LinkedHashMap<?, ?>) iterator.next();
            return rawData;
        } catch (FileNotFoundException e) {
            LogApis.warn("Can't find file" + yamlFile.getAbsolutePath());
            return null;
        } catch (Exception e) {
            LogApis.warn("Wrong file format" + yamlFile.getAbsolutePath());
            return null;
        }
    }

    /**
     * Setter method for property <tt>dIR_PATH</tt>.
     *
     * @param dIR_PATH value to be assigned to property dIR_PATH
     */
    public static void setDIR_PATH(String dIR_PATH) {
        DIR_PATH = dIR_PATH;
    }

    public static File findDbModelPath(File file) {

        String fileName = "dbModel";
        return findModelPath(file, fileName);
    }

    public static File findObjModelPath(File file) {

        String fileName = "objModel";
        return findModelPath(file, fileName);
    }

    /**
     *
     * @param file
     * @param fileName
     * @return
     */
    private static File findModelPath(File file, String fileName) {
        File result = null;
        List<File> dalConfigFileList = new ArrayList<File>();
        FileOperateUtils.findFolderRecursive(file.getAbsolutePath(), fileName, dalConfigFileList);

        for (File f : dalConfigFileList) {

            if (f.getAbsolutePath().contains("target")) {
                continue;
            } else {
                result = f;
                break;
            }
        }
        return result;
    }
}
