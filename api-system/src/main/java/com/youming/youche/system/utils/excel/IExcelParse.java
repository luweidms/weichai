/*
 * IExcelParse.java
 *
 * 2016-1-6 下午4:45:53
 *
 * RecluseKapoor
 *
 * Copyright © 2016, RecluseKapoor. All rights reserved.
 *
 */
package com.youming.youche.system.utils.excel;

import java.io.InputStream;

/**
 * @Title: recluse-Excel文件解析接口
 * @Description:Excel文件解析接口，所有版本的Excel解析类都要实现该接口
 * @author: Chen
 * @CreateDate：2016-1-6 下午9:42:08
 * @version: 1.0
 * @lastModify:
 */
public interface IExcelParse {

    void loadExcel(InputStream file) throws Exception;

    String getSheetName(int sheetNo);

    int getSheetCount() throws Exception;

    int getRowCount(int sheetNo);

    int getRealRowCount(int sheetNo);

    String readExcelByRowAndCell(int sheetNo, int rowNo, int cellNo)
            throws Exception;

    String[] readExcelByRow(int sheetNo, int rowNo) throws Exception;

    String[] readExcelByCell(int sheetNo, int cellNo) throws Exception;

    void setCellValue(int sheetNo, int rowNo, int cellNo, String cellValue) throws Exception;

    void downloadFile(String toPath, String fileName);

    void close();

    void createFile(String toPath, String fileName);
}