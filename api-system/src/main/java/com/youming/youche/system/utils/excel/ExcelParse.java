package com.youming.youche.system.utils.excel;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @Title:recluse-Excel文件解析工具类（兼容2003和2007版本Excel）
 * @Description: 该工具类用于解析Excel文件，同时兼容2003版和2007版Excel文件的解析，且随时可以进行新版本的扩展，
 * <p>
 * 若要支持新版本Excel格式的解析，只需要在excle包下新增一个实现了IExcelParse接口的实现类，
 * <p>
 * 在新增的实现类中实现新对版本Excel格式的解析的功能代码即可 ； 该扩展方法可以最大程度的实现解耦 。
 * <p>
 * @Company: 卡普工作室
 * @Website: http://www.cnblogs.com/reclusekapoor/
 * @author: RecluseKapoor
 * @CreateDate：2016-1-6 下午9:43:56
 * @version: 1.0
 * @lastModify:
 */
public class ExcelParse  {
    private IExcelParse excelParse = null;

    /**
     * 加载实例，根据不同版本的Excel文件，加载不同的具体实现实例
     *
     * @param path
     * @return
     */
    private boolean getInstance(String path) throws Exception {
        path = path.toLowerCase();
        if (path.endsWith(".xls")) {
            excelParse = new ExcelParse2003();
        } else if (path.endsWith(".xlsx")) {
            excelParse = new ExcelParse2007();
        } else {
            throw new Exception("对不起，目前系统不支持对该版本Excel文件的解析。");
        }
        return true;
    }

    /**
     * 获取excel工作区
     *
     * @param path
     * @throws Exception
     */
    public void loadExcel(String filePathAndName, InputStream file) throws Exception {
        getInstance(filePathAndName);
        excelParse.loadExcel(file);
    }

    /**
     * 获取sheet页名称
     *
     * @param sheetNo
     * @return
     */
    public String getSheetName(int sheetNo) {
        return excelParse.getSheetName(sheetNo);
    }

    /**
     * 获取sheet页数
     *
     * @return
     * @throws Exception
     */
    public int getSheetCount() throws Exception {
        return excelParse.getSheetCount();
    }

    /**
     * 获取sheetNo页行数
     *
     * @param sheetNo
     * @return
     * @throws Exception
     */
    public int getRowCount(int sheetNo) {
        return excelParse.getRowCount(sheetNo);
    }

    /**
     * 获取sheetNo页行数(含有操作或者内容的真实行数)
     *
     * @param sheetNo
     * @return
     * @throws Exception
     */
    public int getRealRowCount(int sheetNo) {
        return excelParse.getRealRowCount(sheetNo);
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
    public String readExcelByRowAndCell(int sheetNo, int rowNo, int cellNo)
            throws Exception {
        return excelParse.readExcelByRowAndCell(sheetNo, rowNo, cellNo);
    }

    /**
     * 读取指定SHEET页指定行的Excel内容
     *
     * @param sheetNo 指定SHEET页
     * @param lineNo  指定行
     * @return
     * @throws Exception
     */
    public String[] readExcelByRow(int sheetNo, int rowNo) throws Exception {
        return excelParse.readExcelByRow(sheetNo, rowNo);
    }

    /**
     * 读取指定SHEET页指定列中的数据
     *
     * @param sheetNo 指定SHEET页
     * @param cellNo  指定列号
     * @return
     * @throws Exception
     */
    public String[] readExcelByCell(int sheetNo, int cellNo) throws Exception {
        return excelParse.readExcelByCell(sheetNo, cellNo);
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
    public void setCellValue(int sheetNo, int rowNo, int cellNo, String cellValue) throws Exception {
        excelParse.setCellValue(sheetNo, rowNo, cellNo, cellValue);
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
        excelParse.downloadFile(toPath, fileName);
    }

    /**
     * 关闭excel工作区，释放资源
     */
    public void close() {
        excelParse.close();
    }

    /**
     * 创建新文件
     *
     * @throws Exception
     * @Title: createFile
     * @Description: 创建新文件
     * @param: @param toPath
     * @param: @param fileName 设定文件
     * @return: void 返回类型
     * @author: 陈梁
     * @date: 2019年8月9日 上午10:12:54
     * @throws:
     * @since: JDK 1.8
     */
    public void createFile(String toPath, String fileName) throws Exception {
        fileName = fileName.toLowerCase();
        if (fileName.endsWith(".xls")) {
            excelParse = new ExcelParse2003();
        } else if (fileName.endsWith(".xlsx")) {
            excelParse = new ExcelParse2007();
        } else {
            throw new Exception("对不起，目前系统不支持对该版本Excel文件的解析。");
        }
        excelParse.createFile(toPath, fileName);
    }

}
