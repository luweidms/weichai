package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BnkAccListQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:37
 */
@Data
public class BnkAccListQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** merchNo */
    private String merchNo;
}
