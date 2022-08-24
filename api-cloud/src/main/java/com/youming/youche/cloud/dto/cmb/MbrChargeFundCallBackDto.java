package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MbrChargeFundCallBackDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:59
 */
@Data
public class MbrChargeFundCallBackDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 子商户编号 */
    private String mbrNo;

    /** 子商户名称 */
    private String mbrName;

    /** 入账金额 */
    private String chargeAmt;

    /** 入账时间 */
    private String chargeTime;

    /** 银行处理流水号 */
    private String bizSeq;

    /** 对手方银行账号 */
    private String oppAccNo;

    /** 对手方银行户名 */
    private String oppAccName;
}
