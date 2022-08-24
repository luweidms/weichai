package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName WithdrawCallBackDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/5/11 14:36
 */
@Data
public class WithdrawCallBackDto implements Serializable {

    /** 平台编号 */
    private String platformNo;
    /** 原付方子商户 */
    private String origPayMbrNo;
    /** 收方子商户编号 */
    private String origRecvMbrNo;
    /** 同请求接口中origReqNo流水 */
    private String origReqNo;
    /** 交易日期 */
    private String origTranDate;
    /** 原交易银行返回流水 */
    private String origRespNo;
    /** 交易完成时间 */
    private String origTranTime;
    /** 提现金额(单位为元) */
    private String origTranAmt;
    /** 提现状态 */
    private String origTranStatus;
    /** 原摘要 */
    private String origRemark;

}
