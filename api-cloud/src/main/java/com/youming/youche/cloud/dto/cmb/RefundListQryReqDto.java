package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RefundListQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:21
 */
@Data
public class RefundListQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 退票日期 */
    private String clearDate;
}
