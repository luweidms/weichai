package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/13 13:24
 */
@Data
public class OperDataParam implements Serializable {

    /**
     * 字段名称
     */
    private String columnName;
    /**
     * 字段值
     */
    private String columnValue;
    /**
     * 操作符 如：+ - 等
     */
    private String operate;

    private String dataType;

    public OperDataParam(String columnName,String columnValue,String operate){
        this.columnName = columnName;
        this.columnValue = columnValue;
        this.operate = operate;
    }
}
