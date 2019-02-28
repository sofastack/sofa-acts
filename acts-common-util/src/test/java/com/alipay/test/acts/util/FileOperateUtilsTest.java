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

import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qingqin
 * @version $Id: FileOperateUtilsTest.java, v 0.1 2019年01月15日 下午2:30 qingqin Exp $
 */
public class FileOperateUtilsTest {

    @Test
    public void testFindFileRecursive() {
        List<File> fileList = new ArrayList<File>(6);
        FileOperateUtils.findFileRecursive(getFileFromURL("/PrepareData").getAbsolutePath(),
            ".*csv", fileList);

        Assert.assertTrue(fileList.size() > 0);
    }

    public static File getFileFromURL(String path) {
        File folder = null;
        try {
            folder = new ClassPathResource(path).getFile();
        } catch (IOException e) {
            Assert.fail("error! ", e);
        }
        return folder;
    }

    @Test
    public void testFindFolderRecursive() {
        List<File> fileList = new ArrayList<File>(6);
        FileOperateUtils.findFolderRecursive(getFileFromURL("/").getAbsolutePath(), "PrepareData",
            fileList);

        Assert.assertEquals(fileList.size(), 1);
    }

    @Test
    public void testBackupFile() {
        FileOperateUtils.backupFile(new File("notExists.csv"));

        FileOperateUtils.backupFile(getFileFromURL("PrepareData/PrepareData.csv"));

        FileOperateUtils.renameFile(getFileFromURL("PrepareData/PrepareData.csv.bak"),
            "PrepareData.csv");
    }

    @Test
    public void testCopyFile() throws Exception {
        FileOperateUtils.copyFile(getFileFromURL("PrepareData/PrepareData.csv"),
            getFileFromURL("PrepareData/tmp.csv"));
    }
}