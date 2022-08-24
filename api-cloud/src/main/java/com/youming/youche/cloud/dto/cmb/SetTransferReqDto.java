package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SetTransferReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:23
 */
@Data
public class SetTransferReqDto implements Serializable {
    /** 请求方流水 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 订单编号  */
    private String orderNo;

    /** 子商户号 */
    private String mbrNo;

    /** 子商户名称 */
    private String mbrName;

    /** 交易金额 */
    private String tranAmt;

    /** 中间子商户号 */
    private String midMbrNo;

    /** 中间子商户名称 */
    private String midMbrName;

    /** 收方子商户编号 */
    private String recvMbrNo;

    /** 收方子商户名称 */
    private String recvMbrName;

    /** 支取方式:
     1:无校验
     2:短信验证码，需要附带短信包 */
    private String wdwTyp;

    /** 交易补充信息 */
    private String remark;
}
