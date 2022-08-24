/*
 * ExcelParse2003.java
 *
 * 2016-1-6 下午4:45:53
 *
 * RecluseKapoor
 *
 * Copyright © 2016, RecluseKapoor. All rights reserved.
 *
 */
package com.youming.youche.system.utils.excel;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * @Title: recluse--2003版Excel文件解析工具
 * @Description: 解析2003版Excel文件具体实现类
 * @Company: 卡普工作室
 * @Website: http://www.cnblogs.com/reclusekapoor/
 * @author: RecluseKapoor
 * @CreateDate：2016-1-6 下午9:59:51
 * @version: 1.0
 * @lastModify:
 */
public class ExcelParse2003 implements IExcelParse, Serializable {
    // Excel工作区
    private HSSFWorkbook wb = null;

    private InputStream is = null;

    private POIFSFileSystem fs = null;

    /**
     * 加载excel文件，获取excel工作区
     *
     * @param filePathAndName
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void loadExcel(InputStream filePathAndName) throws FileNotFoundException,
            IOException {
//        FileInputStream fis = null;
//        POIFSFileSystem fs = null;
        try {
            is = filePathAndName;
            fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new FileNotFoundException("加载Excel文件失败：" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("加载Excel文件失败：" + e.getMessage());
        }
    }

    /**
     * 获取sheet页名称
     *
     * @param sheetNo
     * @return
     */
    public String getSheetName(int sheetNo) {
        return wb.getSheetName(sheetNo - 1);
    }

    /**
     * 获取sheet页数
     *
     * @return int
     */
    public int getSheetCount() throws Exception {
        int sheetCount = wb.getNumberOfSheets();
        if (sheetCount == 0) {
            throw new Exception("Excel中没有SHEET页");
        }
        return sheetCount;
    }

    /**
     * 获取sheetNo页行数
     *
     * @param sheetNo
     * @return
     */
    public int getRowCount(int sheetNo) {
        int rowCount = 0;
        HSSFSheet sheet = wb.getSheetAt(sheetNo - 1);
        rowCount = sheet.getLastRowNum();
        return rowCount;
    }

    /**
     * 获取sheetNo页行数(含有操作或者内容的真实行数)
     *
     * @param sheetNo
     * @return
     */
    @SuppressWarnings("deprecation")
    public int getRealRowCount(int sheetNo) {
        int rowCount = 0;
        int rowNum = 0;
        HSSFSheet sheet = wb.getSheetAt(sheetNo - 1);
        rowCount = sheet.getLastRowNum();
        if (rowCount == 0) {
            return rowCount;
        }
        HSSFRow row = null;
        HSSFCell cell = null;
        rowNum = rowCount;
        for (int i = 0; i < rowCount; i++) {
            row = sheet.getRow(rowNum);
            rowNum--;
            if (row == null) {
                continue;
            }
            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();
            for (int j = firstCellNum; j < lastCellNum; j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                    continue;
                } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                    String value = cell.getStringCellValue();
                    if (value == null || value.equals("")) {
                        continue;
                    } else {
                        value = value.trim();
                        if (value.isEmpty() || value.equals("")
                                || value.length() == 0) {
                            continue;
                        }
                    }
                }
                rowCount = rowNum + 2;
                return rowCount;
            }
        }
        rowCount = rowNum;
        return rowCount;
    }

    /**
     * 读取第sheetNo个sheet页中第rowNo行第cellNo列的数据
     *
     * @param sheetNo sheet页编号
     * @param rowNo   行号
     * @param cellNo  列号
     * @return 返回相应的excel单元格内容
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public String readExcelByRowAndCell(int sheetNo, int rowNo, int cellNo)
            throws Exception {
        String rowCellData = "";
        sheetNo = sheetNo - 1;
        HSSFSheet sheet = wb.getSheetAt(sheetNo);
        String sheetName = wb.getSheetName(sheetNo);
        try {
            HSSFRow row = sheet.getRow(rowNo - 1);
            if (row == null) {
                return "";
            }
            HSSFCell cell = row.getCell((cellNo - 1));
            if (cell == null) {
                return "";
            }
            int cellType = cell.getCellType();
            if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {// 数值(包括excel中数值、货币、日期、时间、会计专用等单元格格式)
                //判断数值是否为日期或时间；但是该判断方法存在漏洞，只能识别一种日期格式。
                if (HSSFDateUtil.isCellDateFormatted(cell)) {//日期、时间
                    double d = cell.getNumericCellValue();
                    Date date = HSSFDateUtil.getJavaDate(d);
                    Timestamp timestamp = new Timestamp(date.getTime());
                    String temp = timestamp.toString();
                    if (temp.endsWith("00:00:00.0")) {
                        rowCellData = temp.substring(0,
                                temp.lastIndexOf("00:00:00.0"));
                    } else if (temp.endsWith(".0")) {
                        rowCellData = temp.substring(0, temp.lastIndexOf(".0"));
                    } else {
                        rowCellData = timestamp.toString();
                    }
                } else {//数值、货币、会计专用、百分比、分数、科学记数 单元格式
                    rowCellData = new DecimalFormat("0.########").format(cell
                            .getNumericCellValue());
                }
            } else if (cellType == HSSFCell.CELL_TYPE_STRING) {// 字符串
                rowCellData = cell.getStringCellValue();
            } else if (cellType == HSSFCell.CELL_TYPE_FORMULA) {// 公式
                double d = cell.getNumericCellValue();
                rowCellData = String.valueOf(d);
            } else if (cellType == HSSFCell.CELL_TYPE_BLANK) {// 空值
                rowCellData = "";
            } else if (cellType == HSSFCell.CELL_TYPE_BOOLEAN) {// boolean值
                rowCellData = "";
            } else if (cellType == HSSFCell.CELL_TYPE_ERROR) {// 异常
                rowCellData = "";
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(sheetName + "sheet页中" + "第" + rowNo + "行" + "第"
                    + cellNo + "列" + "数据不符合要求,请检查sheet页");
        }
        return rowCellData;
    }

    /**
     * 读取第sheetNo个sheet页中第rowNo行的数据
     *
     * @param sheetNo 指定sheetNo页
     * @param rowNo   指定rowNo行
     * @return 返回第rowNo行的数据
     * @throws Exception
     */
    public String[] readExcelByRow(int sheetNo, int rowNo) throws Exception {
        String[] rowData = null;
        HSSFSheet sheet = wb.getSheetAt(sheetNo - 1);
        HSSFRow row = sheet.getRow(rowNo - 1);
        int cellCount = row.getLastCellNum();
        rowData = new String[cellCount];
        for (int k = 1; k <= cellCount; k++) {
            rowData[k - 1] = readExcelByRowAndCell(sheetNo, rowNo, k);
        }
        return rowData;
    }

    /**
     * 读取第sheetNo个sheet页中第cellNo列的数据
     *
     * @param sheetNo 指定sheetNo页
     * @param cellNo  指定cellNo列号
     * @return 返回第cellNo列的数据
     * @throws Exception
     */
    public String[] readExcelByCell(int sheetNo, int cellNo) throws Exception {
        String[] cellData = null;
        HSSFSheet sheet = wb.getSheetAt(sheetNo - 1);
        int rowCount = sheet.getLastRowNum();
        cellData = new String[rowCount + 1];
        for (int i = 0; i <= rowCount; i++) {
            cellData[i] = readExcelByRowAndCell(sheetNo - 1, i, cellNo - 1);
        }
        return cellData;
    }

    /**
     * @param @param  sheetNo
     * @param @param  rowNo
     * @param @param  cellNo
     * @param @param  cellValue
     * @param @throws Exception    设定文件
     * @return void    返回类型
     * @throws
     * @Title: setCellValue
     * @Description: TODO(设置单元格内容)
     * @author: 陈梁
     * @date 2017年12月26日 下午8:13:58
     */
    public void setCellValue(int sheetNo, int rowNo, int cellNo, String cellValue) {
        HSSFSheet sheet = wb.getSheetAt(sheetNo - 1);
        HSSFCellStyle centerStyle = wb.createCellStyle();// 设置为水平居中
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFRow row = sheet.getRow(rowNo - 1);
        if (row == null) {
            row = sheet.createRow(rowNo - 1);
        }
        HSSFCell cell = row.getCell((cellNo - 1));
        if (cell == null) {
            cell = row.createCell(cellNo - 1);
        }
        cell.setCellValue(cellValue);
        cell.setCellStyle(centerStyle);
    }

    /**
     * @param @param toPath
     * @param @param fileName    设定文件
     * @return void    返回类型
     * @throws
     * @Title: downloadFile
     * @Description: TODO(导出本Excel到本地)
     * @author: 陈梁
     * @date 2017年12月26日 下午8:38:25
     */
    public void downloadFile(String toPath, String fileName) {

        File toFile = new File(toPath, fileName);
        OutputStream os;
        try {
            os = new FileOutputStream(toFile);
            wb.write(os);
            // 关闭输出流
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 复制单元格
     *
     * @param srcCell
     * @param distCell
     * @param copyValueFlag true则连同cell的内容一起复制
     */
    @SuppressWarnings("deprecation")
    public static void copyCell(HSSFWorkbook wb, HSSFCell srcCell, HSSFCell distCell,
                                boolean copyValueFlag) {
        HSSFCellStyle newstyle = wb.createCellStyle();
        copyCellStyle(srcCell.getCellStyle(), newstyle);
        //distCell.setEncoding(srcCell.getEncoding);  
        //样式  
        distCell.setCellStyle(newstyle);
        //评论  
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }
        // 不同数据类型处理  
        int srcCellType = srcCell.getCellType();
        distCell.setCellType(srcCellType);
        if (copyValueFlag) {
            if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {
                if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
                    distCell.setCellValue(srcCell.getDateCellValue());
                } else {
                    distCell.setCellValue(srcCell.getNumericCellValue());
                }
            } else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {
                distCell.setCellValue(srcCell.getRichStringCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {
                // nothing21  
            } else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {
                distCell.setCellValue(srcCell.getBooleanCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {
                distCell.setCellErrorValue(srcCell.getErrorCellValue());
            } else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {
                distCell.setCellFormula(srcCell.getCellFormula());
            } else { // nothing29  
            }
        }
    }

    /**
     * 复制一个单元格样式到目的单元格样式
     *
     * @param fromStyle
     * @param toStyle
     */
    @SuppressWarnings("deprecation")
    public static void copyCellStyle(HSSFCellStyle fromStyle,
                                     HSSFCellStyle toStyle) {
        toStyle.setAlignment(fromStyle.getAlignment());
        //边框和边框颜色  
        toStyle.setBorderBottom(fromStyle.getBorderBottom());
        toStyle.setBorderLeft(fromStyle.getBorderLeft());
        toStyle.setBorderRight(fromStyle.getBorderRight());
        toStyle.setBorderTop(fromStyle.getBorderTop());
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

        //背景和前景  
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

        toStyle.setDataFormat(fromStyle.getDataFormat());
        toStyle.setFillPattern(fromStyle.getFillPattern());
//      toStyle.setFont(fromStyle.getFont(null));  
        toStyle.setHidden(fromStyle.getHidden());
        toStyle.setIndention(fromStyle.getIndention());//首行缩进  
        toStyle.setLocked(fromStyle.getLocked());
        toStyle.setRotation(fromStyle.getRotation());//旋转  
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
        toStyle.setWrapText(fromStyle.getWrapText());

    }

    /**
     * 关闭excel工作区，释放资源
     *
     * @throws Exception
     */
    public void close() {
        if (wb != null) {
            try {
                if (fs != null) {
                    fs.close();
                }
                wb.close();
                wb = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建新文件
     *
     * @Title: createFile
     * @Description: 创建新文件
     * @param: @param toPath
     * @param: @param fileName 设定文件
     * @return: void 返回类型
     * @author: 陈梁
     * @date: 2019年8月9日 上午10:29:04
     * @throws:
     * @since: JDK 1.8
     */
    public void createFile(String toPath, String fileName) {
        try {
            File toFile = new File(toPath + fileName);
            OutputStream os;
            // Create Blank workbook
            HSSFWorkbook workbook = new HSSFWorkbook();
            workbook.createSheet();
            os = new FileOutputStream(toFile);
            workbook.write(os);
            os.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}