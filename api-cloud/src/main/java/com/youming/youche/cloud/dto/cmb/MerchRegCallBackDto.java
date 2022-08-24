package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MerchRegCallBackDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:00
 */
@Data
public class MerchRegCallBackDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 证照类型 */
    private String certType;

    /** 同请求报文 */
    private String certNo;

    /** 注册状态
     S：成功
     F：失败 */
    private String status;

    /** status为F时，返回失败原因 */
    private String result;

    /** 商户编号，审批通过后分配 */
    private String merchNo;

    /** 商户名称，同请求中的certName */
    private String merchName;

    /** openMode=’F’时必填，子商户编号，审批后生成。 */
    private String mbrNo;

    /** openMode=’F’时必填,同原始请求报文 */
    private String fromMbrNo;

    /** openMode=’F’时必填,同原始请求报文 */
    private String fromMbrName;

    /** 商户进件时对应的请求流水 */
    private String origReqNo;
}
