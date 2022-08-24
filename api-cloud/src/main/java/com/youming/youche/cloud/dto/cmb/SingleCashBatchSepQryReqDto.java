package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SingleCashBatchSepQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:26
 */
@Data
public class SingleCashBatchSepQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 原分账请求流水 */
    private String origReqNo;
}
