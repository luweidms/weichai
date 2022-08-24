package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BnkAccBindReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:34
 */
@Data
public class BnkAccBindReqDto implements Serializable {
    /** 平台编号 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 商户编号 */
    private String merchNo;

    /** 子商户编号，如果指定，则将该账户绑定为子商户默认充值账户 */
    private String mbrNo;

    /** 鉴权申请银行返回流水，同2.2中返回的respNo
     如果填写，则会校验鉴权流水，否则不校验，是否强制校验依赖银行配置 */
    private String origAuthRespNo;

    /** 鉴权打款金额，origAuthRespNo有值时必填 */
    private String authAmt;

    /** 持有人证件类型，绑定个人账户时必填
     P01：个体身份证 */
    private String certType;

    /** 持有人个人证件编号，绑定个人账户时必填 */
    private String certNo;

    /** 银行账号 */
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
}
