package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AccAuthPreChkRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:19
 */
@Data
public class AccAuthPreChkRepDto implements Serializable {
    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 商户编号 */
    private String merchNo;

    /** 银行账号 */
    private String accNo;
}
