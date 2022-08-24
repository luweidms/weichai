package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MerchRegQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:07
 */
@Data
public class MerchRegQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 证照类型 */
    private String certType;

    /** 证照编号 */
    private String certNo;
}
