package com.youming.youche.system.utils.excel;

import com.youming.youche.util.DateUtil;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.poi.hssf.usermodel.HSSFCellStyle;

@SuppressWarnings("all")
public class ExportExcel implements Serializable {

    public static String getFileName() {
        return com.youming.youche.system.utils.DateUtil.toString(new Date(), "yyyyMMdd-HHmmss");
    }



    @SuppressWarnings("deprecation")
    public static HSSFWorkbook getWorkbookXls(List<?> resultList, String[] showName, String[] resourceField, Class<?> resultObj,
                                              Map<String, Map<String, String>> formatMap) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth((short) 20);
        HSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        centerStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        centerStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        centerStyle.setBorderRight(BorderStyle.THIN);// 右边框
        Font font = workbook.createFont();
        font.setBold(true); //粗体
        centerStyle.setFont(font);

        HSSFCellStyle contentStyle = workbook.createCellStyle();// 设置为水平居中
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        contentStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        contentStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        contentStyle.setBorderRight(BorderStyle.THIN);// 右边框

        HSSFRow row;
        HSSFCell cell;
        createTitleXls(showName, sheet, centerStyle);
        // 下面是输出各行的数据 下面是输出各行的数据 下面是输出各行的
        for (int i = 0; i < resultList.size(); i++) {
            Object result = resultList.get(i);
            row = sheet.createRow(i + 1);
            // 创建第 i+1 行
            for (int j = 0; j < resourceField.length; j++) {
                cell = row.createCell(j);// 创建第 j 列
                Method method;
                method = resultObj.getMethod(resourceField[j]);
                // 这里用到了反射机制，通 这里用到了反射机制， 这里用到了反射机制 过方法名来取得对应方法返回的结果对象
                Object obj = method.invoke(result);
                if (obj != null) {
                    if (formatMap != null && formatMap.containsKey(resourceField)) {
                        cell.setCellValue(formatMap.get(resourceField).get(obj.toString()));
                        cell.setCellStyle(contentStyle);
                    } else {
                        String type = method.getGenericReturnType().toString();
                        if ("class java.util.Date".equals(type)) {
                            cell.setCellValue(com.youming.youche.system.utils.DateUtil.toString((Date) obj, com.youming.youche.system.utils.DateUtil.DEFAULT_DATETIME_FORMAT_SEC));
                            cell.setCellStyle(contentStyle);
                        } else if ("class java.time.LocalDateTime".equals(type)) {
                            cell.setCellValue(com.youming.youche.util.DateUtil.formatLocalDateTime((LocalDateTime) obj, DateUtil.DATETIME_FORMAT));
                            cell.setCellStyle(contentStyle);
                        } else {
                            cell.setCellValue(obj.toString());
                            cell.setCellStyle(contentStyle);
                        }
                    }
                } else {
                    cell.setCellStyle(contentStyle);
                }
            }
        }
        return workbook;
    }

    @SuppressWarnings("deprecation")
    public static XSSFWorkbook getWorkbookXlsx(List<?> resultList, String[] showName, String[] resourceField, Class<?> resultObj,
                                               Map<String, Map<String, String>> formatMap) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth((short) 20);//设置宽度
        XSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        centerStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        centerStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        centerStyle.setBorderRight(BorderStyle.THIN);// 右边框
        Font font = workbook.createFont();
        font.setBold(true); //粗体
        centerStyle.setFont(font);

        XSSFCellStyle contentStyle = workbook.createCellStyle();// 设置为水平居中
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        contentStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        contentStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        contentStyle.setBorderRight(BorderStyle.THIN);// 右边框

        XSSFRow row;
        XSSFCell cell;
        createTitleXlsx(showName, sheet, centerStyle);
        // 下面是输出各行的数据 下面是输出各行的数据 下面是输出各行的
        for (int i = 0; i < resultList.size(); i++) {
            Object result = resultList.get(i);
            row = sheet.createRow(i + 1);
            // 创建第 i+1 行
            for (int j = 0; j < resourceField.length; j++) {
                cell = row.createCell(j);// 创建第 j 列
                Method method;
                method = resultObj.getMethod(resourceField[j]);
                // 这里用到了反射机制，通 这里用到了反射机制， 这里用到了反射机制 过方法名来取得对应方法返回的结果对象
                Object obj = method.invoke(result);
                if (obj != null) {
                    if (formatMap != null && formatMap.containsKey(resourceField)) {
                        cell.setCellValue(formatMap.get(resourceField).get(obj.toString()));
                        cell.setCellStyle(contentStyle);
                    } else {
                        String type = method.getGenericReturnType().toString();
                        if ("class java.util.Date".equals(type)) {
                            cell.setCellValue(com.youming.youche.system.utils.DateUtil.toString((Date) obj, com.youming.youche.system.utils.DateUtil.DEFAULT_DATETIME_FORMAT_SEC));
                            cell.setCellStyle(contentStyle);
                        } else if ("class java.time.LocalDateTime".equals(type)) {
                            cell.setCellValue(com.youming.youche.util.DateUtil.formatLocalDateTime((LocalDateTime) obj, DateUtil.DATETIME_FORMAT));
                            cell.setCellStyle(contentStyle);
                        } else {
                            cell.setCellValue(obj.toString());
                            cell.setCellStyle(contentStyle);
                        }
                    }
                } else {
                    cell.setCellStyle(contentStyle);
                }
            }
        }
        return workbook;
    }

    @SuppressWarnings("deprecation")
    public static XSSFWorkbook getWorkbookXlsx(String[] showName,
                                               Map<String, Map<String, String>> formatMap) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth((short) 20);
        XSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        centerStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        centerStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        centerStyle.setBorderRight(BorderStyle.THIN);// 右边框
        Font font = workbook.createFont();
        font.setBold(true); //粗体
        centerStyle.setFont(font);

        XSSFCellStyle contentStyle = workbook.createCellStyle();// 设置为水平居中
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        contentStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        contentStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        contentStyle.setBorderRight(BorderStyle.THIN);// 右边框

        XSSFRow row;
        XSSFCell cell;
        createTitleXlsx(showName, sheet, centerStyle);
        return workbook;
    }

    @SuppressWarnings("deprecation")
    public static XSSFWorkbook getWorkbookXlsxContract(String[] showName,
                                                       Map<String, Map<String, String>> formatMap) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        CellStyle textStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        textStyle.setDataFormat(format.getFormat("@"));
        for (int i = 0; i < showName.length; i++) {
            sheet.setDefaultColumnStyle(0, textStyle);
        }
        sheet.setDefaultColumnWidth((short) 20);
        XSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        centerStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        centerStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        centerStyle.setBorderRight(BorderStyle.THIN);// 右边框
        Font font = workbook.createFont();
        font.setBold(true); //粗体
        centerStyle.setFont(font);

        XSSFCellStyle contentStyle = workbook.createCellStyle();// 设置为水平居中
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        contentStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        contentStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        contentStyle.setBorderRight(BorderStyle.THIN);// 右边框

        XSSFRow row;
        XSSFCell cell;
        createTitleXlsx(showName, sheet, centerStyle);
        return workbook;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheet    要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     * @return 设置好的sheet.
     */
    public static XSSFSheet setXSSFValidation(XSSFSheet sheet,
                                              String[] textlist, int firstRow, int endRow, int firstCol,
                                              int endCol) {
        // 加载下拉列表内容  
        DVConstraint constraint = DVConstraint
                .createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列  
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // 数据有效性对象  
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }

    private static void createTitleXls(String[] showName, HSSFSheet sheet, HSSFCellStyle cellStyle) {
        HSSFRow row = sheet.createRow(0); // 创建第 1 行，也就是输出表头 创建第
        HSSFCell cell;
        for (int i = 0; i < showName.length; i++) {
            cell = row.createCell(i);
            // 创建第 i 列 创建第
            cell.setCellValue(new HSSFRichTextString(showName[i]));
            cell.setCellStyle(cellStyle);
        }
    }

    private static void createTitleXlsx(String[] showName, XSSFSheet sheet, XSSFCellStyle cellStyle) {
        XSSFRow row = sheet.createRow(0); // 创建第 1 行，也就是输出表头 创建第
        XSSFCell cell;
        for (int i = 0; i < showName.length; i++) {
            cell = row.createCell(i);
            // 创建第 i 列 创建第
            cell.setCellValue(new XSSFRichTextString(showName[i]));
            cell.setCellStyle(cellStyle);
        }
    }

    private static void createTitle2(String[] showName, HSSFSheet sheet, HSSFCellStyle centerStyle, HSSFCellStyle style) {
        HSSFRow row = sheet.createRow(3); // 创建第 1 行，也就是输出表头 创建第
        HSSFCell cell;
        for (int i = 0; i < showName.length; i++) {
            cell = row.createCell(i);
            // 创建第 i 列 创建第
            cell.setCellValue(new HSSFRichTextString(showName[i]));
            cell.setCellStyle(centerStyle); // 样式，居中 
            cell.setCellStyle(style); //填充亮橘色
        }
    }

    /**
     * @param @param  resultList
     * @param @param  showName
     * @param @return 设定文件
     * @return HSSFWorkbook 返回类型
     * @throws
     * @Title: createWorkbook
     * @Description: 创建Workbook
     * @author: 李超峰
     * @date 2014-09-18 11:13:23 +0800
     */
    @SuppressWarnings("deprecation")
    public static HSSFWorkbook createWorkbook(List<List<Cell>> resultList, String[] showName) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        HSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
//        centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        createTitleXls(showName, sheet, centerStyle);
        HSSFRow row = null;
        HSSFCell cell = null;
        if (resultList.size() > 0) {
            int[][] arraSort = new int[resultList.get(0).size()][resultList.size()];
            for (int i = 0; i < resultList.size(); i++) {
                row = sheet.createRow(i + 1);
                //  sheet.setColumnWidth(i + 1, 15);
                List<Cell> cellList = resultList.get(i);
                for (int j = 0; j < cellList.size(); j++) {
                    cell = row.createCell(j);// 创建第 j 列
                    cell.setCellValue(cellList.get(j).getValue());
                    int b = cell.getStringCellValue().getBytes().length;
                    arraSort[j][i] = b;
                    if (cellList.get(j).getStyle() != null) {
                        cell.setCellStyle(cellList.get(j).getStyle());
                    }
                }
            }
            //列的最大列宽值（不包括标题）
            int widthInfo[] = TwoMaxInfo(arraSort);
            //与标题在比较列宽
            for (int i = 0; i < showName.length; i++) {
                //sheet.autoSizeColumn(i);
                //算出列（包括标题的最大列宽）
                int maxWidthInfo = showName[i].getBytes().length > widthInfo[i] ? showName[i].getBytes().length : widthInfo[i];
                sheet.setColumnWidth(i, maxWidthInfo > 255 ? 255 * 256 : maxWidthInfo * 256);
            }
        }
        return workbook;
    }

    public static int[] TwoMaxInfo(int[][] arraSort) {
        int[] arraySortInfo = null;
        arraySortInfo = new int[arraSort.length];
        int count = 0;
        for (int[] is : arraSort) {
            int[] arraInfo = is;
            Arrays.sort(arraInfo);
            arraySortInfo[count] = arraInfo[arraInfo.length - 1];
            count++;
        }
        return arraySortInfo;
    }

    /**
     * @param @param  resultList
     * @param @param  showName
     * @param @return 设定文件
     * @return HSSFWorkbook 返回类型
     * @throws
     * @Title: createWorkbookAll
     * @Description: 创建Workbook
     * @author: 张燕
     * @date 2015-06-23 11:13:23 +0800
     */
    @SuppressWarnings("deprecation")
    public static HSSFWorkbook createWorkbookAll(Map<String, List<List<Cell>>> vMap, String[] showName) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (Map.Entry<String, List<List<Cell>>> entry : vMap.entrySet()) {
            HSSFSheet sheet = workbook.createSheet(entry.getKey());
            sheet.setDefaultColumnWidth((short) 15);
            HSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
//            centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//            centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            createTitleXls(showName, sheet, centerStyle);
            HSSFRow row;
            HSSFCell cell;
            for (int i = 0; i < entry.getValue().size(); i++) {
                row = sheet.createRow(i + 1);
                List<Cell> cellList = entry.getValue().get(i);
                for (int j = 0; j < cellList.size(); j++) {
                    cell = row.createCell(j);// 创建第 j 列
                    cell.setCellValue(cellList.get(j).getValue());
                    if (cellList.get(j).getStyle() != null) {
                        cell.setCellStyle(cellList.get(j).getStyle());
                    }
                }
            }
            for (int i = 0; i < showName.length; i++) {
                sheet.autoSizeColumn(i);
            }
        }
        return workbook;
    }

    public static InputStream workbook2InputStreamXls(HSSFWorkbook workbook, String fileName) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        baos.flush();
        byte[] aa = baos.toByteArray();
        InputStream excelStream = new ByteArrayInputStream(aa, 0, aa.length);
        baos.close();
        return excelStream;
    }

    public static InputStream workbook2InputStreamXlsx(XSSFWorkbook workbook, String fileName) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        baos.flush();
        byte[] aa = baos.toByteArray();
        InputStream excelStream = new ByteArrayInputStream(aa, 0, aa.length);
        baos.close();
        return excelStream;
    }

    /**
     * @param @param  resultList 导出的数据集合
     * @param @param  showName 导出的字段名称
     * @param @param  headerName Excel表头参数
     * @param @param  resourceField 实例类对象get方法名，通过反射获取值
     * @param @param  resultObj 实例类
     * @param @param  formatMap
     * @param @return 返回workbook
     * @param @throws SecurityException
     * @param @throws NoSuchMethodException
     * @param @throws IllegalArgumentException
     * @param @throws IllegalAccessException
     * @param @throws InvocationTargetException 设定文件
     * @return HSSFWorkbook 返回类型
     * @throws
     * @Title: createWorkbookVariety
     * @Description: 导出Excel报表
     * @author: CYP
     * @date 2016年4月8日 上午9:35:38
     */

    public static HSSFWorkbook createWorkbookVariety(List<?> resultList, String[] showName, ArrayList<String> headerName, String[] resourceField,
                                                     Class<?> resultObj, Map<String, Map<String, String>> formatMap) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth((short) 15);
        HSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
//        centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        /**
         * 设置表头的样式
         */
        HSSFCellStyle titylStyle = workbook.createCellStyle();
        createTitleVariety(showName, headerName, sheet, titylStyle);
        HSSFRow row;
        HSSFCell cell;
        for (int i = 0; i < resultList.size(); i++) {
            Object result = resultList.get(i);
            if (headerName != null && headerName.size() > 0) {
                row = sheet.createRow(i + 1 + headerName.size());
            } else {
                row = sheet.createRow(i + 1);
            }
            // 创建第 i+1 行
            for (int j = 0; j <= resourceField.length; j++) {
                cell = row.createCell(j);// 创建第 j 列
                cell.setCellStyle(centerStyle);
                if (j == 0) {
                    // 为Excel表的第一列添加编号，表头为：序号；eg:1,2,3,4……
                    cell.setCellValue(i + 1);
                } else {
                    Method method;
                    method = resultObj.getMethod(resourceField[j - 1]);
                    // 这里用到了反射机制，通 这里用到了反射机制， 这里用到了反射机制 过方法名来取得对应方法返回的结果对象
                    Object obj = method.invoke(result);
                    if (obj != null) {
                        if (formatMap != null && formatMap.containsKey(resourceField)) {
                            cell.setCellValue(formatMap.get(resourceField).get(obj.toString()));
                        } else {
                            String type = method.getGenericReturnType().toString();
                            if ("class java.util.Date".equals(type)) {
                                cell.setCellValue(com.youming.youche.system.utils.DateUtil.toString((Date) obj, com.youming.youche.system.utils.DateUtil.DEFAULT_DATETIME_FORMAT_SEC));
                            } else {
                                cell.setCellValue(obj.toString());
                            }
                        }
                    }
                }
            }
        }
        return workbook;
    }

    /**
     * @param @param showName
     * @param @param headerName
     * @param @param sheet 设定文件
     * @return void 返回类型
     * @throws
     * @Title: createTitleVariety
     * @Description: 多行表头
     * @author: CYP
     * @date 2016年4月12日 上午9:32:25
     */
    private static void createTitleVariety(String[] showName, ArrayList<String> headerName, HSSFSheet sheet, HSSFCellStyle titylStyle) {
        HSSFRow row;
        HSSFCell cell;
//        titylStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        titylStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        titylStyle.setAlignment(HorizontalAlignment.CENTER);
        titylStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        if (headerName != null && headerName.size() > 0) {
            for (int i = 0; i < headerName.size(); i++) {
                row = sheet.createRow((short) i);
                if (i == 0) {
                    cell = row.createCell(i);
                    sheet.addMergedRegion(new CellRangeAddress(i, i, (short) 0, (short) showName.length));
                    cell.setCellStyle(titylStyle);
                    if (headerName.get(i) != null) {
                        cell.setCellValue(new HSSFRichTextString(headerName.get(i).toString()));
                    } else {
                        cell.setCellValue(new HSSFRichTextString(""));
                    }
                } else {
                    cell = row.createCell(i - 1);
                    sheet.addMergedRegion(new CellRangeAddress(i, i, (short) 0, (short) showName.length));
                    if (headerName.get(i) != null) {
                        cell.setCellValue(new HSSFRichTextString(headerName.get(i).toString()));
                    } else {
                        cell.setCellValue(new HSSFRichTextString(""));
                    }
                }
            }
        }
        // 设置Excel字段
        if (headerName != null && headerName.size() > 0) {
            row = sheet.createRow((short) headerName.size());
        } else {
            row = sheet.createRow(0);
        }
        for (int n = 0; n <= showName.length; n++) {
            if (n == 0) {
                cell = row.createCell(n);
                cell.setCellStyle(titylStyle);
                cell.setCellValue(new HSSFRichTextString("序号"));
            } else {
                cell = row.createCell(n);
                cell.setCellStyle(titylStyle);
                cell.setCellValue(new HSSFRichTextString(showName[n - 1]));
            }
        }
    }

    public static HSSFWorkbook createWorkbookVarietyParam(ArrayList<ArrayList<String>> resultList, String[] showName, ArrayList<String> headerName) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth((short) 15);
        HSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
//        centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        /**
         * 设置表头的样式
         */
        HSSFCellStyle titylStyle = workbook.createCellStyle();
        createTitleVariety(showName, headerName, sheet, titylStyle);
        HSSFRow row;
        HSSFCell cell;
        if (resultList != null && resultList.size() > 0) {
            for (int i = 0; i < resultList.size(); i++) {
                ArrayList<String> rowResultList = resultList.get(i);
                if (headerName != null && headerName.size() > 0) {
                    row = sheet.createRow((short) (i + 1 + headerName.size()));
                } else {
                    row = sheet.createRow((short) (i + 1));
                }
                if (rowResultList != null && rowResultList.size() > 0) {

                    for (int n = 0; n <= rowResultList.size(); n++) {
                        cell = row.createCell(n);// 创建第 j 列
                        cell.setCellStyle(centerStyle);
                        if (n == 0) {
                            // 为Excel表的第一列添加编号，表头为：序号；eg:1,2,3,4……
                            cell.setCellValue(i + 1);
                        } else if (rowResultList.get(n - 1) != null) {
                            cell.setCellValue(rowResultList.get(n - 1).toString());
                        } else {
                            cell.setCellValue("");
                        }
                    }

                }
            }
        }

        return workbook;
    }

    /**
     * @param @param  resultList
     * @param @param  headList
     * @param @param  sumList
     * @param @param  showName
     * @param @param  resourceField
     * @param @param  resultObj
     * @param @param  formatMap
     * @param @return
     * @param @throws SecurityException
     * @param @throws NoSuchMethodException
     * @param @throws IllegalArgumentException
     * @param @throws IllegalAccessException
     * @param @throws InvocationTargetException    设定文件
     * @return HSSFWorkbook    返回类型
     * @throws
     * @Title: getWorkbook2
     * @Description: TODO(导出车辆运行过程分析Excel)
     * @author: 陈梁
     * @date 2017年9月28日 上午10:45:05
     */
    @SuppressWarnings("deprecation")
    public static HSSFWorkbook getWorkbook2(List<?> resultList, List<?> headList, List<?> sumList, String[] showName, String[] resourceField, Class<?> resultObj,
                                            Map<String, Map<String, String>> formatMap) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth((short) 20);
        HSSFCellStyle centerStyle = workbook.createCellStyle();// 设置为水平居中
//        centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        centerStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        centerStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        centerStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        centerStyle.setBorderRight(BorderStyle.THIN);// 右边框
        HSSFDataFormat format = workbook.createDataFormat();
        //这样才能真正的控制单元格格式，@就是指文本型
        centerStyle.setDataFormat(format.getFormat("@"));

        HSSFCellStyle style = workbook.createCellStyle();
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN); // 下边框  
        style.setBorderLeft(BorderStyle.THIN);// 左边框  
        style.setBorderTop(BorderStyle.THIN);// 上边框  
        style.setBorderRight(BorderStyle.THIN);// 右边框
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填充单元格   
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());//填亮橘色

        HSSFCellStyle greenStyle = workbook.createCellStyle();
//        greenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        greenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        greenStyle.setAlignment(HorizontalAlignment.CENTER);
        greenStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        greenStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        greenStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        greenStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        greenStyle.setBorderRight(BorderStyle.THIN);// 右边框
        greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填充单元格   
        //greenStyle.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);//填亮绿色
        greenStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());//填深绿色
        Font greenfont = workbook.createFont();
        greenfont.setBold(true); //粗体
        greenStyle.setFont(greenfont);

        HSSFCellStyle overGreenStyle = workbook.createCellStyle();
//        overGreenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        overGreenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        overGreenStyle.setAlignment(HorizontalAlignment.CENTER);
        overGreenStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        overGreenStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        overGreenStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        overGreenStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        overGreenStyle.setBorderRight(BorderStyle.THIN);// 右边框
        overGreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填充单元格   
        overGreenStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());//填深绿色

        HSSFCellStyle fontStyle = workbook.createCellStyle();//字体样式
        fontStyle.setAlignment(HorizontalAlignment.CENTER);
        fontStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        fontStyle.setBorderBottom(BorderStyle.THIN); // 下边框  
        fontStyle.setBorderLeft(BorderStyle.THIN);// 左边框  
        fontStyle.setBorderTop(BorderStyle.THIN);// 上边框  
        fontStyle.setBorderRight(BorderStyle.THIN);// 右边框
        fontStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填充单元格   
        fontStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());//填深绿色
        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());    //白字
        fontStyle.setFont(font);

        HSSFRow row;
        HSSFCell cell;
        //createTitle2(showName, sheet, centerStyle, style);

        for (int j = 0; j < 3; j++) {
            row = sheet.createRow(j);
            for (int i = 0; i < showName.length; i++) {
                cell = row.createCell(i);
                if (j == 0) {
                    if (i == 0) {
                        cell.setCellValue(new HSSFRichTextString("查询时间"));
                        cell.setCellStyle(greenStyle);
                    } else if (i == 1) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(j, j, (short) i, (short) (i + 3));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString(headList.get(0).toString()));
                        cell.setCellStyle(fontStyle);
                    }
                } else if (j == 1) {
                    if (i == 0) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(j, (j + 1), (short) 0, (short) 0);
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("车辆信息"));
                        cell.setCellStyle(greenStyle);
                    } else if (i == 1) {
                        cell.setCellValue(new HSSFRichTextString("车牌号"));
                    } else if (i == 2) {
                        cell.setCellValue(new HSSFRichTextString("所属公司"));
                    } else if (i == 3) {
                        cell.setCellValue(new HSSFRichTextString("车辆类型"));
                    } else if (i == 4) {
                        cell.setCellValue(new HSSFRichTextString("入网时间"));
                    } else if (i == 5) {
                        cell.setCellValue(new HSSFRichTextString("车身颜色"));
                    } else if (i == 6) {
                        cell.setCellValue(new HSSFRichTextString("型号"));
                    } else if (i == 7) {
                        cell.setCellValue(new HSSFRichTextString("司机"));
                    } else if (i == 8) {
                        cell.setCellValue(new HSSFRichTextString("手机号"));
                    }
                    if (i > 0 && i < 9) {
                        cell.setCellStyle(overGreenStyle);
                    }
                } else if (j == 2) {
                    if (i == 1) {
                        cell.setCellValue(new HSSFRichTextString(headList.get(1).toString()));
                    } else if (i == 2) {
                        cell.setCellValue(new HSSFRichTextString(headList.get(2).toString()));
                    } else if (i == 3) {
                        cell.setCellValue(new HSSFRichTextString(headList.get(3).toString()));
                    } else if (i == 4) {
                        cell.setCellValue(new HSSFRichTextString(headList.get(4).toString()));
                    } else if (i == 5) {
                        cell.setCellValue(new HSSFRichTextString(headList.get(5).toString()));
                    } else if (i == 6) {
                        cell.setCellValue(new HSSFRichTextString(headList.get(6).toString()));
                    } else if (i == 7) {
                        cell.setCellValue(new HSSFRichTextString(headList.get(7).toString()));
                    } else if (i == 8) {
                        cell.setCellValue(new HSSFRichTextString(headList.get(8).toString()));
                    }
                    if (i > 0 && i < 9) {
                        cell.setCellStyle(fontStyle);
                    }
                }
            }
        }

        for (int k = 0; k < 3; k++) {
            row = sheet.createRow(k + 3);
            for (int i = 0; i < showName.length; i++) {
                cell = row.createCell(i);
                if (k == 0) {
                    if (i == 0) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i + 1));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("时间区间"));
                    } else if (i == 2) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i + 1));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("时长"));
                    } else if (i == 4) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("位置信息"));
                    } else if (i == 5) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("车辆状态"));
                    } else if (i == 6) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("行驶里程（km）"));
                    } else if (i == 7) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("超速（次数）"));
                    } else if (i == 8) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 3, (short) (i), (short) (i + 3));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("疲劳驾驶"));
                    } else if (i == 12) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("急加速（次数）"));
                    } else if (i == 13) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("急减速（次数）"));
                    } else if (i == 14) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("急转弯（次数）"));
                    } else if (i == 15) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("平均速度（km/h）"));
                    } else if (i == 16) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("最高速度（km/h）"));
                    }
                } else if (k == 1) {
                    if (i == 8) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("次数"));
                    } else if (i == 9) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 3, (short) (i), (short) (i + 1));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("时长"));
                    } else if (i == 11) {
                        //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("里程（km）"));
                    }
                } else if (k == 2) {
                    if (i == 0) {
                        cell.setCellValue(new HSSFRichTextString("开始时间"));
                    } else if (i == 1) {
                        cell.setCellValue(new HSSFRichTextString("结束时间"));
                    } else if (i == 2) {
                        cell.setCellValue(new HSSFRichTextString("小时"));
                    } else if (i == 3) {
                        cell.setCellValue(new HSSFRichTextString("分钟"));
                    } else if (i == 4) {
                        cell.setCellValue(new HSSFRichTextString("详细地址"));
                    } else if (i == 5) {
                        cell.setCellValue(new HSSFRichTextString("状态"));
                    } else if (i == 9) {
                        cell.setCellValue(new HSSFRichTextString("小时"));
                    } else if (i == 10) {
                        cell.setCellValue(new HSSFRichTextString("分钟"));
                    }
                }
                cell.setCellStyle(style); //填充亮橘色
            }
        }

        // 下面是输出各行的数据 下面是输出各行的数据 下面是输出各行的
        for (int i = 0; i < resultList.size(); i++) {
            Object result = resultList.get(i);
            row = sheet.createRow(i + 6);
            // 创建第 i+1 行
            for (int j = 0; j < resourceField.length; j++) {
                cell = row.createCell(j);// 创建第 j 列
                Method method;
                method = resultObj.getMethod(resourceField[j]);
                // 这里用到了反射机制，通 这里用到了反射机制， 这里用到了反射机制 过方法名来取得对应方法返回的结果对象
                Object obj = method.invoke(result);
                if (obj != null) {
                    if (formatMap != null && formatMap.containsKey(resourceField)) {
                        cell.setCellValue(formatMap.get(resourceField).get(obj.toString()));
                        cell.setCellStyle(centerStyle); // 样式，居中
                    } else {
                        String type = method.getGenericReturnType().toString();
                        if ("class java.util.Date".equals(type)) {
                            cell.setCellValue(com.youming.youche.system.utils.DateUtil.toString((Date) obj, com.youming.youche.system.utils.DateUtil.DEFAULT_DATETIME_FORMAT_SEC));
                        } else {
                            cell.setCellValue(obj.toString());
                        }
                        cell.setCellStyle(centerStyle); // 样式，居中 
                    }
                } else {
                    cell.setCellStyle(centerStyle); // 样式，居中
                }
            }
        }

        //参数1：起始行 参数2：终止行 参数3：起始列 参数4：终止列      
        //CellRangeAddress region1 = new CellRangeAddress(showName.length, showName.length, (short) 0, (short) 11);     
        row = sheet.createRow(resultList.size() + 6);
        for (int i = 0; i < showName.length; i++) {
            cell = row.createCell(i);
            if (i == 0) {
                //参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
                CellRangeAddress region1 = new CellRangeAddress(resultList.size() + 6, resultList.size() + 6, (short) (i), (short) (i + 1));
                sheet.addMergedRegion(region1);
                cell.setCellValue(new HSSFRichTextString("总计"));
                cell.setCellStyle(greenStyle);
            } else if (i == 2) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(0).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 3) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(1).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 4) {//折合时间（min）
                cell.setCellValue(new HSSFRichTextString("折合时间（min）"));
                cell.setCellStyle(greenStyle);
            } else if (i == 5) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(2).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 6) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(3).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 7) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(4).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 8) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(5).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 9) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(6).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 10) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(7).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 11) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(8).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 12) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(9).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 13) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(10).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 14) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(11).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 15) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(12).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 16) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(13).toString()));
                cell.setCellStyle(fontStyle);
            }
        }

        return workbook;
    }
}
