package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/4/30
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class VehicleOrderVo implements Serializable {
    /**
     * 订单号
     */
    private String orderIds;

    /**
     *车牌号
     */
    private String plateNumber;

    /**
     * 靠台开始时间
     */
    private String beginDependTime;

    /**
     * 靠台结束时间
     */
    private String endDependTime;

    /**
     * 录入开始时间
     */
    private String beginOrderTime;

    /**
     * 录入结束时间
     */
    private String endOrderTime;

    /**
     *司机姓名
     */
    private String carUserName;

    /**
     * 客户名称
     */
    private String customName;

    /**
     * 客户单号
     */
    private String customNumber;

    /**
     * 归属部门Id
     */
    private String orgId;

    /**
     * 跟单人员
     */
    private String localUserName;

    /**
     * 录入人
     */
    private String opName;

    /**
     * 副驾姓名
     */
    private String copilotMan;

    /**
     * 线路关键字
     */
    private String lineKey;

    /**
     * 挂车车牌
     */
    private String trailerPlate;

    /**
     * 订单状态
     */
    private String orderState;
}
