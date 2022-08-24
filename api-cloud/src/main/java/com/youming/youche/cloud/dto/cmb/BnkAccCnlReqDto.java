package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BnkAccCnlReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:35
 */
@Data
public class BnkAccCnlReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 商户编号 */
    private String merchNo;

    /** 银行账号 */
    private String accNo;
}
