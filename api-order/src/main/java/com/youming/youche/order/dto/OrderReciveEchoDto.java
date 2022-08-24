package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.OrderReceipt;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/5/16
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderReciveEchoDto implements Serializable {
    /**
     * 回单状态
     */
    private Integer reciveState;
    /**
     * 回单类型
     */
    private Integer reciveType;
    private String reciveTypeName;
    /**
     * 回单地址的省份名称
     */
    private String reciveProvinceName;
    /**
     * 回单地址的市名称
     */
    private String reciveCityName;
    /**
     * 回单地址
     */
    private String reciveAddr;
    private List<OrderReceipt> receipts;

}
