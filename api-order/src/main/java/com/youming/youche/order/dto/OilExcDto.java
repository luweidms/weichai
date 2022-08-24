package com.youming.youche.order.dto;

import com.youming.youche.order.domain.TurnCashLog;
import com.youming.youche.order.domain.TurnCashOrder;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OilExcDto implements Serializable {

    private String createTime;
    private String updateTime;
    private Integer userType;
    private Long userId;
    private Long tenantId;
    /**
     * 转现类型(1油转现，2etc转现)
     */
    private String turnType;
    private String turnOilType;
    private String turnEntityOilCard;
    /**
     * 转移折扣
     */
    private Long turnDiscountDouble;
    /**
     * 转现月份
     */
    private String turnMonth;
    /**
     * 已转金额
     */
    private Long turnBalance;
    private String vehicleAffiliation;
    private String oilAffiliation;
    private OrderOilSource sourcrList;
    private OrderLimit orderLimit;
    private TurnCashLog turnCashLog;
    private List<TurnCashOrder> turnCashOrder;
}
