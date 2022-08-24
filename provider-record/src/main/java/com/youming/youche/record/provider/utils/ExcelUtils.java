package com.youming.youche.record.provider.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.util.ExcelFilesVaildate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Excel工具类
 */
@Slf4j
public class ExcelUtils {

    /**
     * 导出Excel(07版.xlsx)到指定路径下
     *
     * @param path      路径
     * @param excelName Excel名称
     * @param sheetName sheet页名称
     * @param clazz     Excel要转换的类型
     * @param data      要导出的数据
     */
    public static void export2File(String path, String excelName, String sheetName, Class clazz, List data) {
        String fileName = path.concat(excelName).concat(ExcelTypeEnum.XLSX.getValue());
        EasyExcel.write(fileName, clazz).sheet(sheetName).doWrite(data);
    }

    /**
     * 导出Excel(07版.xlsx)到web
     *
     * @param response  响应
     * @param excelName Excel名称
     * @param sheetName sheet页名称
     * @param clazz     Excel要转换的类型
     * @param data      要导出的数据
     * @throws Exception
     */
    public static void export2Web(HttpServletResponse response, String excelName, String sheetName, Class clazz, List data) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        excelName = URLEncoder.encode(excelName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + excelName + ExcelTypeEnum.XLSX.getValue());
        EasyExcel.write(response.getOutputStream(), clazz).sheet(sheetName).doWrite(data);
    }

    /**
     * 导出Excel(07版.xlsx)到文件服务器
     *
     * @param excelName Excel名称
     * @param sheetName sheet页名称
     * @param clazz     Excel要转换的类型
     * @param data      要导出的数据
     * @throws Exception
     */
    public static ImportOrExportRecords export2Fdfs(ImportOrExportRecords record, String excelName, String sheetName, Class clazz, List data) {
        try {
            // 这里URLEncoder.encode可以防止中文乱码
            excelName = URLEncoder.encode(excelName, "UTF-8");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            EasyExcel.write(os, clazz).sheet(sheetName).doWrite(data);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, excelName, inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
        } catch (Exception e) {
            record.setState(3);
            e.printStackTrace();
        }
        return record;
    }


    /**
     * 将指定位置指定名称的Excel导出到web
     *
     * @param response  响应
     * @param path      文件路径
     * @param excelName 文件名称
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String export2Web4File(HttpServletResponse response, String path, String excelName) throws UnsupportedEncodingException {
        File file = new File(path.concat(excelName).concat(ExcelTypeEnum.XLSX.getValue()));
        if (!file.exists()) {
            return "文件不存在！";
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
        excelName = URLEncoder.encode(excelName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + excelName + ExcelTypeEnum.XLSX.getValue());

        try (
                FileInputStream in = new FileInputStream(file);
                ServletOutputStream out = response.getOutputStream();
        ) {
            IOUtils.copy(in, out);
            return "导出成功！";
        } catch (Exception e) {
            log.error("导出文件异常：", e);
        }

        return "导出失败！";
    }

    public static List<List<String>> getExcelContent(InputStream inputStream, int beginRow, ExcelFilesVaildate[] validates) throws Exception {
        DataFormatter dataFormatter = new DataFormatter();
        List<List<String>> fileContent = new ArrayList();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        if (rows >= 1000 + beginRow) {
            throw new BusinessException("excel文件的行数超过系统允许导入最大行数：" + 1000);
        } else {
            if (rows >= beginRow) {
                List rowList = null;
                Row row = null;

                for (int i = beginRow - 1; i < rows; ++i) {
                    row = sheet.getRow(i);
                    rowList = new ArrayList();
                    fileContent.add(rowList);
                    if (row != null) {
                        int cells = row.getLastCellNum();
                        if (cells > 200) {
                            throw new BusinessException("文件列数超过200列，请检查文件！");
                        }

                        for (int j = 0; j < cells; ++j) {
                            Cell cell = row.getCell(j);
                            String cellValue = "";
                            if (cell != null) {
                                //  log.debug("Reading Excel File row:" + i + ", col:" + j + " cellType:" + cell.getCellType());
                                switch (cell.getCellType()) {
                                    case 0:
                                        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                                            cellValue = com.youming.youche.util.DateUtil.formatDateByFormat(cell.getDateCellValue(), com.youming.youche.util.DateUtil.DATETIME_FORMAT);
                                        } else {
                                            cellValue = dataFormatter.formatCellValue(cell);
                                        }
                                        break;
                                    case 1:
                                        cellValue = cell.getStringCellValue();
                                        break;
                                    case 2:
                                        cellValue = String.valueOf(cell.getNumericCellValue());
                                        break;
                                    case 3:
                                        cellValue = "";
                                        break;
                                    case 4:
                                        cellValue = String.valueOf(cell.getBooleanCellValue());
                                        break;
                                    case 5:
                                        cellValue = String.valueOf(cell.getErrorCellValue());
                                }
                            }

                            if (validates != null && validates.length > j) {
                                if (cellValue == null) {
                                    throw new BusinessException("第" + (i + beginRow - 1) + "行,第" + (j + 1) + "列数据校验出错：" + validates[j].getErrorMsg());
                                }

                                Pattern p = Pattern.compile(validates[j].getPattern());
                                Matcher m = p.matcher(cellValue);
                                if (!m.matches()) {
                                    throw new BusinessException("第" + (i + beginRow - 1) + "行,第" + (j + 1) + "列数据校验出错：" + validates[j].getErrorMsg());
                                }
                            }

                            rowList.add(cellValue);
                        }
                    }
                }
            }

            return fileContent;
        }
    }

    public static String getExcelValue(List<String> list, int index) {
        if (list == null || list.isEmpty() || list.size() < index + 1) {
            return null;
        }
        return StringUtils.trim(list.get(index));
    }

    public static boolean modifyValue(List<String> list, int index, Map param, String key) {
        boolean isModify = false;
        String value = getExcelValue(list, index);
        if (StringUtils.isNotBlank(value)) {
            isModify = true;
            param.put(key, value);
        }
        return isModify;
    }

    public static String checkDateTypeValue(List<String> list, int index, Map param, String key, String keyName, boolean isModify) {
        isModify = modifyValue(list, index, param, key) || isModify;
        String timeValueInput = getExcelValue(list, index);
        if (StringUtils.isNotBlank(timeValueInput) && !isDate(timeValueInput)) {
            return keyName + "格式不正确，";
        } else {
            return "";
        }
    }

    public static String checkDateTypeValueAdd(List<String> list, int index, Map param, String key, String keyName) {
        String value = getExcelValue(list, index);
        //param.put(key, value);
        if (StringUtils.isNotBlank(value) && !isDate(value)) {
            return keyName + "格式不正确，";
        } else {
            param.put(key, com.youming.youche.util.DateUtil.parseDate(value, com.youming.youche.util.DateUtil.DATE_FORMAT));
            return "";
        }
    }


    private static boolean isDate(String dateStr) {
        try {
            if (dateStr != null && !dateStr.equals("")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                simpleDateFormat.parse(dateStr);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
