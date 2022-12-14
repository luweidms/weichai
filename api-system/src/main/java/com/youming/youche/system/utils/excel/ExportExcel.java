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
        HSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        centerStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderRight(BorderStyle.THIN);// ?????????
        Font font = workbook.createFont();
        font.setBold(true); //??????
        centerStyle.setFont(font);

        HSSFCellStyle contentStyle = workbook.createCellStyle();// ?????????????????????
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        contentStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        contentStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        contentStyle.setBorderRight(BorderStyle.THIN);// ?????????

        HSSFRow row;
        HSSFCell cell;
        createTitleXls(showName, sheet, centerStyle);
        // ?????????????????????????????? ?????????????????????????????? ????????????????????????
        for (int i = 0; i < resultList.size(); i++) {
            Object result = resultList.get(i);
            row = sheet.createRow(i + 1);
            // ????????? i+1 ???
            for (int j = 0; j < resourceField.length; j++) {
                cell = row.createCell(j);// ????????? j ???
                Method method;
                method = resultObj.getMethod(resourceField[j]);
                // ????????????????????????????????? ?????????????????????????????? ??????????????????????????? ??????????????????????????????????????????????????????
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
        sheet.setDefaultColumnWidth((short) 20);//????????????
        XSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        centerStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderRight(BorderStyle.THIN);// ?????????
        Font font = workbook.createFont();
        font.setBold(true); //??????
        centerStyle.setFont(font);

        XSSFCellStyle contentStyle = workbook.createCellStyle();// ?????????????????????
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        contentStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        contentStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        contentStyle.setBorderRight(BorderStyle.THIN);// ?????????

        XSSFRow row;
        XSSFCell cell;
        createTitleXlsx(showName, sheet, centerStyle);
        // ?????????????????????????????? ?????????????????????????????? ????????????????????????
        for (int i = 0; i < resultList.size(); i++) {
            Object result = resultList.get(i);
            row = sheet.createRow(i + 1);
            // ????????? i+1 ???
            for (int j = 0; j < resourceField.length; j++) {
                cell = row.createCell(j);// ????????? j ???
                Method method;
                method = resultObj.getMethod(resourceField[j]);
                // ????????????????????????????????? ?????????????????????????????? ??????????????????????????? ??????????????????????????????????????????????????????
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
        XSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        centerStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderRight(BorderStyle.THIN);// ?????????
        Font font = workbook.createFont();
        font.setBold(true); //??????
        centerStyle.setFont(font);

        XSSFCellStyle contentStyle = workbook.createCellStyle();// ?????????????????????
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        contentStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        contentStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        contentStyle.setBorderRight(BorderStyle.THIN);// ?????????

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
        XSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        centerStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        centerStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderRight(BorderStyle.THIN);// ?????????
        Font font = workbook.createFont();
        font.setBold(true); //??????
        centerStyle.setFont(font);

        XSSFCellStyle contentStyle = workbook.createCellStyle();// ?????????????????????
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        contentStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        contentStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        contentStyle.setBorderRight(BorderStyle.THIN);// ?????????

        XSSFRow row;
        XSSFCell cell;
        createTitleXlsx(showName, sheet, centerStyle);
        return workbook;
    }

    /**
     * ????????????????????????????????????????????????,???????????????.
     *
     * @param sheet    ????????????sheet.
     * @param textlist ????????????????????????
     * @param firstRow ?????????
     * @param endRow   ?????????
     * @param firstCol ?????????
     * @param endCol   ?????????
     * @return ????????????sheet.
     */
    public static XSSFSheet setXSSFValidation(XSSFSheet sheet,
                                              String[] textlist, int firstRow, int endRow, int firstCol,
                                              int endCol) {
        // ????????????????????????  
        DVConstraint constraint = DVConstraint
                .createExplicitListConstraint(textlist);
        // ????????????????????????????????????????????????,?????????????????????????????????????????????????????????????????????  
        CellRangeAddressList regions = new CellRangeAddressList(firstRow,
                endRow, firstCol, endCol);
        // ?????????????????????  
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }

    private static void createTitleXls(String[] showName, HSSFSheet sheet, HSSFCellStyle cellStyle) {
        HSSFRow row = sheet.createRow(0); // ????????? 1 ??????????????????????????? ?????????
        HSSFCell cell;
        for (int i = 0; i < showName.length; i++) {
            cell = row.createCell(i);
            // ????????? i ??? ?????????
            cell.setCellValue(new HSSFRichTextString(showName[i]));
            cell.setCellStyle(cellStyle);
        }
    }

    private static void createTitleXlsx(String[] showName, XSSFSheet sheet, XSSFCellStyle cellStyle) {
        XSSFRow row = sheet.createRow(0); // ????????? 1 ??????????????????????????? ?????????
        XSSFCell cell;
        for (int i = 0; i < showName.length; i++) {
            cell = row.createCell(i);
            // ????????? i ??? ?????????
            cell.setCellValue(new XSSFRichTextString(showName[i]));
            cell.setCellStyle(cellStyle);
        }
    }

    private static void createTitle2(String[] showName, HSSFSheet sheet, HSSFCellStyle centerStyle, HSSFCellStyle style) {
        HSSFRow row = sheet.createRow(3); // ????????? 1 ??????????????????????????? ?????????
        HSSFCell cell;
        for (int i = 0; i < showName.length; i++) {
            cell = row.createCell(i);
            // ????????? i ??? ?????????
            cell.setCellValue(new HSSFRichTextString(showName[i]));
            cell.setCellStyle(centerStyle); // ??????????????? 
            cell.setCellStyle(style); //???????????????
        }
    }

    /**
     * @param @param  resultList
     * @param @param  showName
     * @param @return ????????????
     * @return HSSFWorkbook ????????????
     * @throws
     * @Title: createWorkbook
     * @Description: ??????Workbook
     * @author: ?????????
     * @date 2014-09-18 11:13:23 +0800
     */
    @SuppressWarnings("deprecation")
    public static HSSFWorkbook createWorkbook(List<List<Cell>> resultList, String[] showName) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        HSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
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
                    cell = row.createCell(j);// ????????? j ???
                    cell.setCellValue(cellList.get(j).getValue());
                    int b = cell.getStringCellValue().getBytes().length;
                    arraSort[j][i] = b;
                    if (cellList.get(j).getStyle() != null) {
                        cell.setCellStyle(cellList.get(j).getStyle());
                    }
                }
            }
            //??????????????????????????????????????????
            int widthInfo[] = TwoMaxInfo(arraSort);
            //????????????????????????
            for (int i = 0; i < showName.length; i++) {
                //sheet.autoSizeColumn(i);
                //??????????????????????????????????????????
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
     * @param @return ????????????
     * @return HSSFWorkbook ????????????
     * @throws
     * @Title: createWorkbookAll
     * @Description: ??????Workbook
     * @author: ??????
     * @date 2015-06-23 11:13:23 +0800
     */
    @SuppressWarnings("deprecation")
    public static HSSFWorkbook createWorkbookAll(Map<String, List<List<Cell>>> vMap, String[] showName) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        for (Map.Entry<String, List<List<Cell>>> entry : vMap.entrySet()) {
            HSSFSheet sheet = workbook.createSheet(entry.getKey());
            sheet.setDefaultColumnWidth((short) 15);
            HSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
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
                    cell = row.createCell(j);// ????????? j ???
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
     * @param @param  resultList ?????????????????????
     * @param @param  showName ?????????????????????
     * @param @param  headerName Excel????????????
     * @param @param  resourceField ???????????????get?????????????????????????????????
     * @param @param  resultObj ?????????
     * @param @param  formatMap
     * @param @return ??????workbook
     * @param @throws SecurityException
     * @param @throws NoSuchMethodException
     * @param @throws IllegalArgumentException
     * @param @throws IllegalAccessException
     * @param @throws InvocationTargetException ????????????
     * @return HSSFWorkbook ????????????
     * @throws
     * @Title: createWorkbookVariety
     * @Description: ??????Excel??????
     * @author: CYP
     * @date 2016???4???8??? ??????9:35:38
     */

    public static HSSFWorkbook createWorkbookVariety(List<?> resultList, String[] showName, ArrayList<String> headerName, String[] resourceField,
                                                     Class<?> resultObj, Map<String, Map<String, String>> formatMap) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth((short) 15);
        HSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
//        centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        /**
         * ?????????????????????
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
            // ????????? i+1 ???
            for (int j = 0; j <= resourceField.length; j++) {
                cell = row.createCell(j);// ????????? j ???
                cell.setCellStyle(centerStyle);
                if (j == 0) {
                    // ???Excel???????????????????????????????????????????????????eg:1,2,3,4??????
                    cell.setCellValue(i + 1);
                } else {
                    Method method;
                    method = resultObj.getMethod(resourceField[j - 1]);
                    // ????????????????????????????????? ?????????????????????????????? ??????????????????????????? ??????????????????????????????????????????????????????
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
     * @param @param sheet ????????????
     * @return void ????????????
     * @throws
     * @Title: createTitleVariety
     * @Description: ????????????
     * @author: CYP
     * @date 2016???4???12??? ??????9:32:25
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
        // ??????Excel??????
        if (headerName != null && headerName.size() > 0) {
            row = sheet.createRow((short) headerName.size());
        } else {
            row = sheet.createRow(0);
        }
        for (int n = 0; n <= showName.length; n++) {
            if (n == 0) {
                cell = row.createCell(n);
                cell.setCellStyle(titylStyle);
                cell.setCellValue(new HSSFRichTextString("??????"));
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
        HSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
//        centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        /**
         * ?????????????????????
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
                        cell = row.createCell(n);// ????????? j ???
                        cell.setCellStyle(centerStyle);
                        if (n == 0) {
                            // ???Excel???????????????????????????????????????????????????eg:1,2,3,4??????
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
     * @param @throws InvocationTargetException    ????????????
     * @return HSSFWorkbook    ????????????
     * @throws
     * @Title: getWorkbook2
     * @Description: TODO(??????????????????????????????Excel)
     * @author: ??????
     * @date 2017???9???28??? ??????10:45:05
     */
    @SuppressWarnings("deprecation")
    public static HSSFWorkbook getWorkbook2(List<?> resultList, List<?> headList, List<?> sumList, String[] showName, String[] resourceField, Class<?> resultObj,
                                            Map<String, Map<String, String>> formatMap) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth((short) 20);
        HSSFCellStyle centerStyle = workbook.createCellStyle();// ?????????????????????
//        centerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        centerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        centerStyle.setAlignment(HorizontalAlignment.CENTER);
        centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        centerStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        centerStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        centerStyle.setBorderRight(BorderStyle.THIN);// ?????????
        HSSFDataFormat format = workbook.createDataFormat();
        //?????????????????????????????????????????????@??????????????????
        centerStyle.setDataFormat(format.getFormat("@"));

        HSSFCellStyle style = workbook.createCellStyle();
//        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN); // ?????????  
        style.setBorderLeft(BorderStyle.THIN);// ?????????  
        style.setBorderTop(BorderStyle.THIN);// ?????????  
        style.setBorderRight(BorderStyle.THIN);// ?????????
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//???????????????   
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());//????????????

        HSSFCellStyle greenStyle = workbook.createCellStyle();
//        greenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        greenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        greenStyle.setAlignment(HorizontalAlignment.CENTER);
        greenStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        greenStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        greenStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        greenStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        greenStyle.setBorderRight(BorderStyle.THIN);// ?????????
        greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//???????????????   
        //greenStyle.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);//????????????
        greenStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());//????????????
        Font greenfont = workbook.createFont();
        greenfont.setBold(true); //??????
        greenStyle.setFont(greenfont);

        HSSFCellStyle overGreenStyle = workbook.createCellStyle();
//        overGreenStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        overGreenStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        overGreenStyle.setAlignment(HorizontalAlignment.CENTER);
        overGreenStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        overGreenStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        overGreenStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        overGreenStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        overGreenStyle.setBorderRight(BorderStyle.THIN);// ?????????
        overGreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//???????????????   
        overGreenStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());//????????????

        HSSFCellStyle fontStyle = workbook.createCellStyle();//????????????
        fontStyle.setAlignment(HorizontalAlignment.CENTER);
        fontStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        fontStyle.setBorderBottom(BorderStyle.THIN); // ?????????  
        fontStyle.setBorderLeft(BorderStyle.THIN);// ?????????  
        fontStyle.setBorderTop(BorderStyle.THIN);// ?????????  
        fontStyle.setBorderRight(BorderStyle.THIN);// ?????????
        fontStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//???????????????   
        fontStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());//????????????
        Font font = workbook.createFont();
        font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());    //??????
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
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                        cell.setCellStyle(greenStyle);
                    } else if (i == 1) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(j, j, (short) i, (short) (i + 3));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString(headList.get(0).toString()));
                        cell.setCellStyle(fontStyle);
                    }
                } else if (j == 1) {
                    if (i == 0) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(j, (j + 1), (short) 0, (short) 0);
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                        cell.setCellStyle(greenStyle);
                    } else if (i == 1) {
                        cell.setCellValue(new HSSFRichTextString("?????????"));
                    } else if (i == 2) {
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 3) {
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 4) {
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 5) {
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 6) {
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 7) {
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 8) {
                        cell.setCellValue(new HSSFRichTextString("?????????"));
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
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i + 1));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 2) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i + 1));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 4) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 5) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 6) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("???????????????km???"));
                    } else if (i == 7) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("??????????????????"));
                    } else if (i == 8) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 3, (short) (i), (short) (i + 3));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 12) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("?????????????????????"));
                    } else if (i == 13) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("?????????????????????"));
                    } else if (i == 14) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("?????????????????????"));
                    } else if (i == 15) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("???????????????km/h???"));
                    } else if (i == 16) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 5, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("???????????????km/h???"));
                    }
                } else if (k == 1) {
                    if (i == 8) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 9) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 3, (short) (i), (short) (i + 1));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 11) {
                        //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                        CellRangeAddress region1 = new CellRangeAddress(k + 3, k + 4, (short) (i), (short) (i));
                        sheet.addMergedRegion(region1);
                        cell.setCellValue(new HSSFRichTextString("?????????km???"));
                    }
                } else if (k == 2) {
                    if (i == 0) {
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 1) {
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 2) {
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 3) {
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 4) {
                        cell.setCellValue(new HSSFRichTextString("????????????"));
                    } else if (i == 5) {
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 9) {
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    } else if (i == 10) {
                        cell.setCellValue(new HSSFRichTextString("??????"));
                    }
                }
                cell.setCellStyle(style); //???????????????
            }
        }

        // ?????????????????????????????? ?????????????????????????????? ????????????????????????
        for (int i = 0; i < resultList.size(); i++) {
            Object result = resultList.get(i);
            row = sheet.createRow(i + 6);
            // ????????? i+1 ???
            for (int j = 0; j < resourceField.length; j++) {
                cell = row.createCell(j);// ????????? j ???
                Method method;
                method = resultObj.getMethod(resourceField[j]);
                // ????????????????????????????????? ?????????????????????????????? ??????????????????????????? ??????????????????????????????????????????????????????
                Object obj = method.invoke(result);
                if (obj != null) {
                    if (formatMap != null && formatMap.containsKey(resourceField)) {
                        cell.setCellValue(formatMap.get(resourceField).get(obj.toString()));
                        cell.setCellStyle(centerStyle); // ???????????????
                    } else {
                        String type = method.getGenericReturnType().toString();
                        if ("class java.util.Date".equals(type)) {
                            cell.setCellValue(com.youming.youche.system.utils.DateUtil.toString((Date) obj, com.youming.youche.system.utils.DateUtil.DEFAULT_DATETIME_FORMAT_SEC));
                        } else {
                            cell.setCellValue(obj.toString());
                        }
                        cell.setCellStyle(centerStyle); // ??????????????? 
                    }
                } else {
                    cell.setCellStyle(centerStyle); // ???????????????
                }
            }
        }

        //??????1???????????? ??????2???????????? ??????3???????????? ??????4????????????      
        //CellRangeAddress region1 = new CellRangeAddress(showName.length, showName.length, (short) 0, (short) 11);     
        row = sheet.createRow(resultList.size() + 6);
        for (int i = 0; i < showName.length; i++) {
            cell = row.createCell(i);
            if (i == 0) {
                //??????1????????? ??????2??????????????? ??????3????????? ??????4???????????????
                CellRangeAddress region1 = new CellRangeAddress(resultList.size() + 6, resultList.size() + 6, (short) (i), (short) (i + 1));
                sheet.addMergedRegion(region1);
                cell.setCellValue(new HSSFRichTextString("??????"));
                cell.setCellStyle(greenStyle);
            } else if (i == 2) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(0).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 3) {
                cell.setCellValue(new HSSFRichTextString(sumList.get(1).toString()));
                cell.setCellStyle(fontStyle);
            } else if (i == 4) {//???????????????min???
                cell.setCellValue(new HSSFRichTextString("???????????????min???"));
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
