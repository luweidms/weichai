package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/19
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderListAppInVo implements Serializable {
    private Boolean isHis;
    private Long orderId;
    private Integer desRegion;
    private String orderStates;
    private String reciveState;
    private Integer sourceRegion;
    private String tenantName;
    private Long carDriverId;
    private Integer orderSelectType;
    private String customNumber;//客户单号或回单号
    private Integer vehicleClass;//车辆类型 例如 招商挂靠车
    private Integer odsiState;
    private Integer[] vehicleClassOther;
}
