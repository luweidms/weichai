package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SingleCashBatchSepRepDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:27
 */
@Data
public class SingleCashBatchSepRepDto implements Serializable {
    /** 同请求接口中reqNo流水 */
    private String reqNo;

    /** 银行处理流水 */
    private String respNo;

    /** 平台编号 */
    private String platformNo;

    /** 整体处理状态：
     SP：可继续分账PF：分账失败
     SO: 分账完成
     SD: 分账中 */
    private String totalStatus;

    /** 付方交易后余额，-1表示无值或未同步到 */
    private String payMbrBal;

    /** 分账明细列表 */
    private List<SepInfoRepDto> sepList;
}
