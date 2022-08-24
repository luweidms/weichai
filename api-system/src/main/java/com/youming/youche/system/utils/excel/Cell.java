package com.youming.youche.system.utils.excel;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;

import java.io.Serializable;

/**
 * @ClassName: Cell
 * @Description: 导出 cell设置
 * @author: 李超峰
 * @company: 上海航盛实业有限公司
 * @date 2014-09-18 13:43:50 +0800
 */
public class Cell implements Serializable {

    /**
     * @Fields value : cell数值
     */
    private String value;

    /**
     * @Fields style : cell style
     */
    private HSSFCellStyle style;

    public Cell(String value) {
        this.value = value;
    }

    public Cell(String value, HSSFCellStyle style) {
        this.value = value;
        this.style = style;
    }

    public String getValue() {
        return this.value;
    }

    public HSSFCellStyle getStyle() {
        return this.style;
    }
}
