package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderFundRevokeRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:17
 */
@Data
public class OrderFundRevokeRepDto implements Serializable {
    /** 同请求接口中reqNo流水 */
    private String reqNo;

    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 订单编号 */
    private String orderNo;

    /** 解冻金额 */
    private String tranAmt;

    /** 剩余冻结金额 */
    private String splsAmt;

    /** 交易日期 */
    private String tranDate;

    /** 交易完成时间 */
    private String tranTime;
}
