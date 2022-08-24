package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MbrInfoChgReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:59
 */
@Data
public class MbrInfoChgReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 子商户编号 */
    private String mbrNo;

    /** 子商户名称 */
    private String mbrName;

    /** 变更后业务系统编号 */
    private String chgFromMbrNo;
}
