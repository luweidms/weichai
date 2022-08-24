package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TotalOrderFundQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 13:43
 */
@Data
public class TotalOrderFundQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** orderNo类型,
     1:按平台
     2:按银行 */
    private String orderNoType;

    /** 冻结编号，如果orderNoType为1，传业务平台orderNo；如果orderNoType为2，传银行返回的frzN */
    private String orderNo;

    /** 子商户号 */
    private String mbrNo;
}
