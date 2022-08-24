package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderFundReserveRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:13
 */
@Data
public class OrderFundReserveRepDto implements Serializable {
    /** 同请求接口中reqNo流水 */
    private String reqNo;

    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 业务系统订单编号，同请求 */
    private String orderNo;

    /** 冻结编号，实际资金冻结位置 */
    private String frzNo;

    /** 冻结子商户，同请求 */
    private String mbrNo;

    /** 冻结金额，同请求 */
    private String tranAmt;

    /** 交易完成日期 */
    private String tranDate;

    /** 交易完成时间 */
    private String tranTime;
}
