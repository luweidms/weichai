package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data

/**
 * 聂杰伟
 * 订单-回单
 */
public class OrderReceiptDto implements Serializable {
    String failResult;//审核失败原因
    Integer successNum; //成功条数
    Integer  failNum; // 失败条数
}
