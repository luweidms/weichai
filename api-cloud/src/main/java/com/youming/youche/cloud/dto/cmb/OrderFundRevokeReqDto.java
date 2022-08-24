package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderFundRevokeReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:17
 */
@Data
public class OrderFundRevokeReqDto implements Serializable {
    /** 请求方流水 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 订单编号 */
    private String orderNo;

    /** 子商户号，需要与下单请求接口一致 */
    private String mbrNo;

    /** 冻结金额 */
    private String tranAmt;

    /** 摘要 */
    private String remark;
}
