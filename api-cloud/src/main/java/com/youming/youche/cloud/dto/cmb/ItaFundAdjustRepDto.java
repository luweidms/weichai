package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ItaFundAdjustRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:42
 */
@Data
public class ItaFundAdjustRepDto implements Serializable {
    /** 同请求接口中reqNo流水 */
    private String reqNo;

    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 调账金额 */
    private String tranAmt;

    /** 收方子商户号,同请求报文 */
    private String tgtMbrNo;

    /** 交易完成日期 */
    private String tranDate;

    /** 交易完成时间 */
    private String tranTime;
}
