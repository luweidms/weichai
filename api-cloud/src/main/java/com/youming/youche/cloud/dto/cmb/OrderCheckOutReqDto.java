package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderCheckOutReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:13
 */
@Data
public class OrderCheckOutReqDto implements Serializable {
    /** 请求方流水 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 业务系统订单编号，要求同下单一致 */
    private String orderNo;

    /** 子商户号，同下单一致 */
    private String mbrNo;

    /** 冻结子商户名称 */
    private String mbrName;

    /** 冻结执行金额 */
    private String tranAmt;

    /** 收方子商户编号 */
    private String recvMbrNo;

    /** 收方子商户名称 */
    private String recvMbrName;

    /** 支取方式:
     1:无校验
     2:短信验证码，需要附带短信包 */
    private String wdwTyp;

    /** 摘要 */
    private String remark;

    /** 验证信息 */
    private AuthInfoReqDto authInfo;
}
