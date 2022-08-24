package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BindAccInfoRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:32
 */
@Data
public class BindAccInfoRepDto implements Serializable {
    /** 银行账号4位尾号 */
    private String accNo;

    /** 银行户名 */
    private String accName;

    /** 账户类型，
     C：对公
     P：个人 */
    private String accType;

    /** 是否为招行账户
     Y：是
     N：否 */
    private String isCmbAcc;

    /** 开户行联行号 */
    private String bnkNo;

    /** 开户行名称 */
    private String bnkName;

    /** 开户行地址 */
    private String bnkAddress;

    /** 银行预留手机号，绑定个人账户时必填 */
    private String accMobile;

    /** 绑定子商户编号，充值识别使用 */
    private String bindMbrNo;

    /** 原绑定流水，可用于超时判断 */
    private String origReqNo;
}
