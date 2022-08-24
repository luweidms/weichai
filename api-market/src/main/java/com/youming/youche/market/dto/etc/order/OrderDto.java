package com.youming.youche.market.dto.etc.order;

import lombok.Data;

/**
 * 订单ETC
 * 聂杰伟
 */
@Data
public class OrderDto {
    private Long orderId;  //订单号
    private String customName;//客户名称（简称）
    private String line_code_name;//线路名称o
    private String plateNumber;//车牌号
    private String carType;//车型
    private String carDependDate;//实际靠台时间
    private float etc_money; //社会车ETC金额
    private float etc_money_merchant;// 招商车ETC金额
    private String driver_name;//司机名称
    private String driver_Number;//司机手机号
    private Integer cost_model; //成本模式
}
