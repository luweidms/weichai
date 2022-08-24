package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TranListQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 13:46
 */
@Data
public class TranListQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 付方（冻结）子商户号 */
    private String payMbrNo;

    /** 收方子商户号 */
    private String recvMbrNo;

    /** 业务类型：
     OD：下单
     OR：下单撤销
     OC：结单
     BP：可用余额支付
     WD：提现
     AC：调账
     ST：套账 */
    private String tranType;

    /** 查询开始日期 */
    private String startDate;

    /** 查询结束日期 */
    private String endDate;

    /** 页码 */
    private String pageNum;

    /** 每页条数 */
    private String pageSize;
}
