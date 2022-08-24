package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MbrBaseInfoQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:58
 */
@Data
public class MbrBaseInfoQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 子商户编号 */
    private String mbrNo;
}
