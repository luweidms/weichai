package com.youming.youche.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**

 * 源代码统计量

 *

 * @createTime: 2012-8-1 下午01:25:56

 * @author: hubo

 * @version: 0.1

 * @lastVersion: 0.1

 * @updateTime:

 * @updateAuthor: hubo

 * @changesSum:

 *

 */

public class test {

    private static final String FILE_TYPE = "js";

    private long rows = 0;

    private StringBuffer sbBuffer = new StringBuffer();

    /**

     * 根据文件计算代码行数

     * @param file

     * @return

     * @throws IOException

     * @auther hubo

     * @return long

     * 2012-8-1 下午01:43:38

     */

    public long staticRowsByFile(File file) throws IOException {

        if (file.isDirectory()) { // 非文件

            throw new IOException("is not file:" + file);

        }
//        else if (!file.getName().endsWith("." + FILE_TYPE)) { // 非java文件
//
//            return 0;
//
//        }

        long rows = 0;

        FileInputStream fis = new FileInputStream(file);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String str = null;

        while ((str = br.readLine()) != null) {

            str = str.trim();

            if (str.length() > 1 && !str.startsWith("/") && !str.startsWith("*")) {

                rows ++;

            }

        }

        return rows;

    }

    /**

     * 根据目录计算目录以内的代码行数

     * @param dirFile

     * @return

     * @throws IOException

     * @auther hubo

     * @return long

     * 2012-8-1 下午01:43:52

     */

    public long staticRowsByDirectory(File dirFile) throws IOException {

        if (!dirFile.isDirectory()) {

            throw new IOException("is not Directory:" + dirFile);

        }

        File[] files = dirFile.listFiles();

        for (File childFile : files) {

            if (childFile.isDirectory()) {

                staticRowsByDirectory(childFile);

            } else {

                rows += staticRowsByFile(childFile);

            }

        }

        return rows;

    }

    /**

     * 获取文件代码

     * @param file

     * @return

     * @throws IOException

     * @auther hubo

     * @return long

     * 2012-8-1 下午01:43:38

     */

    public String getSrcByFile(File file) throws IOException {

        if (file.isDirectory()) { // 非文件

            throw new IOException("is not file:" + file);

        } else if (!file.getName().endsWith("." + FILE_TYPE)) { // 非java文件

            return "";

        }

        StringBuffer sbBuffer = new StringBuffer();

        FileInputStream fis = new FileInputStream(file);

        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String str = null;

        while ((str = br.readLine()) != null) {

            if (str.trim().length() > 0) {

                sbBuffer.append(str);

                sbBuffer.append("\r\n");

            }

        }

        return sbBuffer.toString();

    }

    /**

     * 获取目录以内所有的源代码

     * @param dirFile

     * @return

     * @throws IOException

     * @auther hubo

     * @return long

     * 2012-8-1 下午01:43:52

     */

    public String getSrcByDirectory(File dirFile) throws IOException {

        if (!dirFile.isDirectory()) {

            throw new IOException("is not Directory:" + dirFile);

        }

        File[] files = dirFile.listFiles();

        for (File childFile : files) {

            if (childFile.isDirectory()) {

                getSrcByDirectory(childFile);

            } else {

                sbBuffer.append(getSrcByFile(childFile));

            }

        }

        return sbBuffer.toString();

    }

    public static void main(String[] args) throws IOException {

// ass——14014 common——6896 protocol——3531short-master——1163

// server——7666+178+7703=15547 web——6583

        File file = new File("/Users/luwei/youche/weixin/motorcade_applet");

        test jsu = new test();

        System.out.println(jsu.staticRowsByDirectory(file));

//        OutputStream os = new FileOutputStream(new File("d:/crm.java"));
//
//        os.write(jsu.getSrcByDirectory(file).getBytes());

    }

}