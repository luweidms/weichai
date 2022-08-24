package com.youming.youche.system.utils.excel;

import com.youming.youche.system.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExcelMessage implements Serializable {

    /**
     * 表头
     */
    private String[] headParam;

    /**
     * 必填项
     */
    private String[] mustParam;

    /**
     * 需要输入参数的所有列,Key:表示列数,Value:表示列的名称
     */
    private Map<Integer, String> sub;

    private HSSFSheet sheet;

    private Class<?> object;

    private String[] methodName;

    public ExcelMessage() {
    }

    public ExcelMessage(String[] headParam, String[] mustParam) {
        this.headParam = headParam;
        this.mustParam = mustParam;
        this.sub = joinParam(headParam, mustParam);
    }

    /**
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param headParam
     * @param mustParam
     * @param input
     * @param n         第几个工作簿
     * @throws Exception
     */
    public ExcelMessage(String[] headParam, String[] mustParam, String[] methodName, FileInputStream input, int n, Class<?> object) throws Exception {
        this.headParam = headParam;
        this.mustParam = mustParam;
        this.methodName = methodName;
        this.object = object;
        this.sub = joinParam(headParam, mustParam);
        this.sheet = readSheet(input, n);
        input.close();
        checkHead();
        if (this.flag)
            this.resultList = readExcel(this.sheet);
    }

    /**
     * 逐行解析工作表中的内容
     *
     * @param sheet
     * @return 解析工作表后生成的二维数组
     * @throws Exception
     */
    public List<Object> readExcel(HSSFSheet sheet) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        // 获得该工作表的行数
        int rows = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rows; i++) {
            if (i != 0) {
                // 解析该行中的每个单元格
                HSSFRow row = sheet.getRow(i);
                if (row != null) {
                    // 将每行解析出的一维数组加入到list中
                    list.add(readCells(row));
                }
            }
        }
        return list;
    }

    /**
     * 解析工作表中的一行数据
     *
     * @param row
     * @param excelMessage
     * @return 本行解析后的一维数组
     * @throws IllegalAccessException
     * @throws Exception
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    private Object readCells(HSSFRow row) throws Exception {
        Object obj = object.newInstance();
        // 获取所有必填项的所在列的索引
        Set<Integer> keySet = this.sub.keySet();

        int sum = this.headParam.length;
        for (int i = 0; i < sum; i++) {
            HSSFCell cell = row.getCell(i);
            String value = null;
            if (cell != null) {
                // 根据单元格内容的类型分别处理
                switch (cell.getCellType()) {
                    case HSSFCell.CELL_TYPE_FORMULA:
                        value = cell.getCellFormula();
                        break;
                    case HSSFCell.CELL_TYPE_NUMERIC: {

                        // 若该单元格内容为数值类型，则判断是否为日期类型
                        if (cell.getCellStyle().getDataFormatString().equals("yyyy\"年\"m\"月\";@") || HSSFDateUtil.isCellDateFormatted(cell)) {
                            try {
                                // 若转换发生异常，则记录下单元格的位置，并跳过该行的读取
                                //value = String.valueOf(DateUtil.toString(cell.getDateCellValue(), "yyyy-MM-dd"));
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                value = format.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                            } catch (Exception e) {
                                errMessage.append(row.getRowNum() + "行" + (i + 1) + "列" + "日期格式错误;");
                                return null;
                            }

                        } else {
                            try {
                                // 将数字类型转换为字符串类型，若为浮点类型数据，则去除掉小数点后的内容
                                DecimalFormat df = new DecimalFormat("#");
                                value = df.format(cell.getNumericCellValue());
                            } catch (Exception e) {
                                // 若转换发生异常，则记录下单元格的位置，并跳过该行的读取
                                errMessage.append(row.getRowNum() + "行" + (i + 1) + "列" + "数字格式错误;");
                                return null;
                            }
                        }
                        break;
                    }
                    case HSSFCell.CELL_TYPE_STRING:
                        value = cell.getStringCellValue();
                        break;
                    case HSSFCell.CELL_TYPE_BLANK:
                        value = "";
                        break;
                    default: {
                        // 若单元格内容的格式与常规类型不符，则记录下单元格位置，并跳过该行的读取，继续解析下一行
                        errMessage.append(row.getRowNum() + "行" + (i + 1) + "列" + "格式与常规类型不符;");
                        return null;
                    }
                }
            } else {
                // 如果当前单元格是空,需要确认是否为必填项的单元格
                if (keySet.contains(i)) {
                    this.flag = false;
                    errMessage.append((row.getRowNum() + 1) + "行," + (i + 1) + "列,必填项有空值;");
                }
            }
            Field field = object.getDeclaredField(methodName[i]);
            field.setAccessible(true);
            if (value != null) {
                if (field.getType() == Double.class) {
                    if (StringUtils.isNotBlank(value)) field.set(obj, Double.parseDouble(value));
                } else if (field.getType() == Integer.class) {
                    if (StringUtils.isNotBlank(value)) field.set(obj, Integer.parseInt(value));
                } else if (field.getType() == Date.class) {
                    DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                    if (StringUtils.isNotBlank(value)) field.set(obj, dateFormat1.parse(value));
                } else {
                    field.set(obj, value);
                }
            }
        }

        return obj;
    }

    /**
     * @param @param  fileInputStream
     * @param @param  n 第几张工作表
     * @param @return 设定文件
     * @return HSSFSheet 返回类型
     * @throws
     * @Title: readSheet
     * @Description: 获取工作表
     * @author: CYP
     * @date 2017年3月13日 下午1:38:50
     */
    public HSSFSheet readSheet(FileInputStream fileInputStream, int n) {
        HSSFSheet sheet = null;
        // 声明并初始化一个工作薄对象
        HSSFWorkbook wb = null;
        try {
            // 生成Excel工作薄对象
            wb = new HSSFWorkbook(fileInputStream);
            // 获得工作薄中的第一个工作表
            sheet = wb.getSheetAt(n);
            // 解析Excel文件结束后，关闭输入流
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sheet;
    }

    /**
     * @param @param  param
     * @param @param  mustParam
     * @param @return 设定文件
     * @return ExcelMessage 返回类型
     * @throws
     * @Title: joinParam
     * @Description:获取所有必填项的列的角标
     * @author: CYP
     * @date 2017年3月13日 下午6:11:49
     */
    private Map<Integer, String> joinParam(String[] param, String[] mustParam) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        for (int i = 0; i < mustParam.length; i++) {
            for (int j = 0; j < param.length; j++) {
                if (param[j].equals(mustParam[i]))
                    map.put(j, mustParam[i]);
            }
        }
        return map;
    }

    /**
     * @param @param  sheet
     * @param @param  param
     * @param @param  excelMessage
     * @param @return 设定文件
     * @return ExcelMessage 返回类型
     * @throws
     * @Title: checkHead
     * @Description: 检验模板表头是否和上传的一致
     * @author: CYP
     * @date 2017年3月14日 上午9:44:09
     */
    public void checkHead() {
        // 获取第一行表头数据
        HSSFRow row = this.sheet.getRow(0);
        String message = "";
        for (int i = 0; i < this.headParam.length; i++) {
            HSSFCell cell = row.getCell(i);
            if (cell != null) {
                if (!cell.getStringCellValue().matches("^.*" + this.headParam[i] + ".*$")) {
                    this.flag = false;
                    message = message + "<" + this.headParam[i] + ">";
                    //errMessage.append("<第一行" + i + 1 + "列,模板表头:" + this.headParam[i] + "不正确>");
                }
            } else {
                this.flag = false;
                message = message + "<" + this.headParam[i] + ">";
                //errMessage.append("<第一行" + i + 1 + "列,模板表头:" + this.headParam[i] + "不正确>");
            }
        }
        if (StringUtils.isNotBlank(message)) {
            errMessage.append("第一行,模板表头:" + message + "格式不正确");
        }
    }

    /**
     * flag:标记
     */
    private Boolean flag = true;
    /**
     * errMessage:错误信息
     */
    public StringBuffer errMessage = new StringBuffer();

    /**
     * 如果flag为True,所有验证成功,可以插入数据 如果flag为false,所有验证失败,返回页面提示错误信息:errMessage
     */
    private List<Object> resultList;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public List<Object> getResultList() {
        return resultList;
    }

    public void setResultList(List<Object> resultList) {
        this.resultList = resultList;
    }

    public String[] getHeadParam() {
        return headParam;
    }

    public void setHeadParam(String[] headParam) {
        this.headParam = headParam;
    }

    public String[] getMustParam() {
        return mustParam;
    }

    public void setMustParam(String[] mustParam) {
        this.mustParam = mustParam;
    }

    public Map<Integer, String> getSub() {
        return sub;
    }

    public void setSub(Map<Integer, String> sub) {
        this.sub = sub;
    }

    public List<Object> objectClass(Class<?> object, String[] methodName, ArrayList<ArrayList<String>> params) {
        List<Object> lists = new ArrayList<Object>();
        try {
            for (List<String> param : params) {
                Object obj = object.newInstance();
                for (int i = 0; i < param.size(); i++) {
                    Field field = object.getDeclaredField(methodName[i]);
                    field.setAccessible(true);
                    if (param.get(i) != null) {
                        if (field.getType() == Double.class) {
                            field.set(obj, Double.parseDouble(param.get(i)));
                        } else if (field.getType() == Integer.class) {
                            field.set(obj, Integer.parseInt(param.get(i)));
                        } else if (field.getType() == Date.class) {
                            field.set(obj, DateUtil.getTime(param.get(i), DateUtil.DEFAULT_DATETIME_FORMAT));
                        } else {
                            field.set(obj, param.get(i));
                        }
                    }
                }
                lists.add(obj);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return lists;
    }

}
