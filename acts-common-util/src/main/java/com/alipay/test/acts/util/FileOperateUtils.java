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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author tinghe
 * @version $Id: FileOperateUtils.java,v 0.1 2010-6-29 11:41:15 tinghe Exp $
 */
public class FileOperateUtils {

    /** Log */
    protected static final Log log = LogFactory.getLog(FileOperateUtils.class);

    /**
     *
     * @param findDir
     * @param fileNameRegex
     * @param fileList
     */
    public static void findFileRecursive(final String findDir, final String fileNameRegex,
                                         List<File> fileList) {
        if (fileList == null) {
            return;
        }
        File file = new File(findDir);
        if (file.isFile()) {
            if (null == fileNameRegex || file.getName().matches(fileNameRegex)) {
                fileList.add(file);
            }
        } else if (file.isDirectory()) {
            File[] dirFiles = file.listFiles();
            for (File dirFile : dirFiles) {
                findFileRecursive(dirFile.getAbsolutePath(), fileNameRegex, fileList);
            }
        }
    }

    /**
     *
     * @param findDir
     * @param fileNameRegex
     * @param fileList
     */
    public static void findFolderRecursive(final String findDir, final String fileNameRegex,
                                           List<File> fileList) {
        if (fileList == null) {
            return;
        }
        File file = new File(findDir);
        if (file.isDirectory()) {
            if (null == fileNameRegex || file.getName().matches(fileNameRegex)) {
                fileList.add(file);
            } else {
                File[] dirFiles = file.listFiles();
                for (File dirFile : dirFiles) {
                    findFolderRecursive(dirFile.getAbsolutePath(), fileNameRegex, fileList);
                }
            }
        } else {
            return;
        }
    }

    /**
     *
     * @param fromFile
     * @return
     * @author mokong
     */
    public static boolean backupFile(File fromFile) {

        if (!fromFile.exists()) {
            return false;
        }

        String bakFileName = fromFile.getName() + ".bak";

        return renameFile(fromFile, bakFileName);

    }

    /**
     *
     * @param fromFile
     * @return
     * @author mokong
     */
    public static boolean backupFileToDel(File fromFile) {

        if (!fromFile.exists()) {
            return false;
        }

        return backupFile(fromFile) && fromFile.delete();

    }

    /**
     *
     * @param fromFile
     * @param newName
     * @return
     * @author mokong
     */
    public static boolean renameFile(File fromFile, String newName) {

        String orgiFilePath = fromFile.getParent();
        File newFile = new File(orgiFilePath + File.separator + newName);

        if (newFile.exists() && newFile.delete()) {
            log.error(newFile.getAbsolutePath() + " delete sucessfully");
        }

        if (fromFile.renameTo(newFile)) {
            log.error(fromFile.getName() + " rename to " + newFile.getName() + "sucessfully");
            return true;
        } else {
            log.error(fromFile.getName() + " rename to" + newFile.getName() + "failed");
            return false;
        }

    }

    /**
     *
     * @param fromFile
     * @param toFile
     * @return
     * @throws IOException 
     *  @author mokong
     */
    public static boolean copyFile(File fromFile, File toFile) throws IOException {

        if (toFile.exists() && toFile.delete()) {
            log.error(toFile.getAbsolutePath() + " delete sucessfully");
        }

        if (fromFile.exists()) {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fromFile));
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(toFile));

            //write
            int c;
            while ((c = bin.read()) != -1) {
                bout.write(c);

            }
            bin.close();
            bout.close();
            return true;
        } else {
            log.error(fromFile.getAbsolutePath() + " occured wrong");
            return false;
        }

    }
}
