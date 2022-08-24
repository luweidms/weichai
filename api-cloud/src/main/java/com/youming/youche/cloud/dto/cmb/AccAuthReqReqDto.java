package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AccAuthReqReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:31
 */
@Data
public class AccAuthReqReqDto implements Serializable {
    /** 请求流水 */
    private String reqNo;

    /** 平台编号 */
    private String platformNo;

    /** 商户编号 */
    private String merchNo;

    /** 银行账号 */
    private String accNo;

    /** 银行户名 */
    private String accName;

    /** 开户行联行号 */
    private String bnkNo;

    /** 开户行名称 */
    private String bnkName;

    /** 开户行地址 */
    private String bnkAddress;
}
