package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AccAuthReqQryRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:21
 */
@Data
public class AccAuthReqQryRepDto implements Serializable {
    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 同请求流水 */
    private String origReqNo;

    /** 申请鉴权时的银行处理流水，用作后续验证 */
    private String origRespNo;

    /** 鉴权交易日期 */
    private String tranDate;

    /** 鉴权过期时间，精确到天，yyyyMMdd */
    private String expireTime;

    /** 银行账号4位尾号 */
    private String accNo;
}
