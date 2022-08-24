package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ItaTranQryReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:47
 */
@Data
public class ItaTranQryReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 被查询的目标请求流水 */
    private String origReqNo;

    /** 业务类型：
     OD：下单
     OR：下单撤销
     OC：结单
     BP：可用余额支付
     WD：提现
     AC：调账
     ST: 套账 */
    private String origReqType;

    /** 返回码
     OrigReqType为WD时必填；
     示例值：
     10000
     10001 */
    private String returnCode;
}
