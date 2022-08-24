package com.youming.youche.finance.dto.ac;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/29 16:59
 */
@Data
public class CmSalaryInfoExtDto implements Serializable {

    /**
     * 自定义列名称
     */
    private String fieldCode;

    /**
     * 自定义列数值
     */
    private String fieldValue;

    public CmSalaryInfoExtDto (String fieldCode, String fieldValue) {
        this.fieldCode = fieldCode;
        this.fieldValue = fieldValue;
    }
}
