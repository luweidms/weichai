package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AccAuthReqQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:23
 */
@Data
public class AccAuthReqQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 请求流水 */
    private String origReqNo;
}
