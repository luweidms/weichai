package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderFundReserveReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:17
 */
@Data
public class OrderFundReserveReqDto implements Serializable {
    /** 请求方流水 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 业务系统订单编号 */
    private String orderNo;

    /** 子商户号，如果传入相同的订单编号，则子商户号必须也相同 */
    private String mbrNo;

    /** 冻结子商户名称 */
    private String mbrName;

    /** 冻结金额 */
    private String tranAmt;

    /** 摘要 */
    private String remark;
}
