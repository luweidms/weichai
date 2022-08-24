package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TotalOrderFundQryRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 13:43
 */
@Data
public class TotalOrderFundQryRepDto implements Serializable {
    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 子商户编号 */
    private String mbrNo;

    /** 子商户名称 */
    private String mbrName;

    /** 当前冻结金额 */
    private String frzBal;
}
