package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AccAuthReqRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:30
 */
@Data
public class AccAuthReqRepDto implements Serializable {
    /** 请求流水 */
    private String reqNo;

    /** 银行处理流水，用作后续验证 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 鉴权过期时间，精确到天yyyyMMdd */
    private String expireTime;

    /** 打款交易日期 */
    private String tranDate;

    /** 绑定商户编号 */
    private String merchNo;

    /** 银行账号4位尾号 */
    private String accNo;
}
