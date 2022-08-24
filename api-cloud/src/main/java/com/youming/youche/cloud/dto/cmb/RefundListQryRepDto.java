package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName RefundListQryRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:21
 */
@Data
public class RefundListQryRepDto implements Serializable {
    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 退票日期 */
    private String clearDate;

    /** 退票明细列表 */
    private List<RefundInfoRepDto> refundList;
}
