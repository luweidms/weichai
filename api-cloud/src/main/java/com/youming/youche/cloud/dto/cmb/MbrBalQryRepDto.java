package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MbrBalQryRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:56
 */
@Data
public class MbrBalQryRepDto implements Serializable {
    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 子商户编号 */
    private String mbrNo;

    /** 子商户名称 */
    private String mbrName;

    /** 可用余额 */
    private String avaBal;

    /** 已冻结资金总额，由多个冻结编号涉及的资金累加完成。 */
    private String frzBal;
}
