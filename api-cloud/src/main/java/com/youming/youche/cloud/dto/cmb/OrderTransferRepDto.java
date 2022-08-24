package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderTransferRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:18
 */
@Data
public class OrderTransferRepDto implements Serializable {
    /** 同请求接口中reqNo流水 */
    private String reqNo;

    /** 银行业务处理流水 */
    private String respNo;

    /** 平台编号，同请求 */
    private String platformNo;

    /** 业务系统订单编号，同请求 */
    private String orderNo;

    /** 付方子商户编号，同请求 */
    private String mbrNo;

    /** 收方子商户编号，同请求 */
    private String recvMbrNo;

    /** 交易金额 */
    private String tranAmt;

    /** 交易日期 */
    private String tranDate;

    /** 交易完成时间 */
    private String tranTime;

    /** 摘要 */
    private String remark;
}
