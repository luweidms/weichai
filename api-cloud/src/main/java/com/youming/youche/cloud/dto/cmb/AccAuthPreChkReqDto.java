package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AccAuthPreChkReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:20
 */
@Data
public class AccAuthPreChkReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 商户编号 */
    private String merchNo;

    /** 银行账号 */
    private String accNo;
}
