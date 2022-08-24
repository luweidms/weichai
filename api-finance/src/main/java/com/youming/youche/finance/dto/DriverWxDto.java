package com.youming.youche.finance.dto;

import com.youming.youche.order.domain.OilCardManagement;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DriverWxDto implements Serializable {
    private String sourceRegion; //始发市
    private Long pledgeOilcardFee; //油卡抵押金额
    private Long orderId; //订单号
    private LocalDateTime dependTime; //靠台时间

    private String sourceRegionName; //始发市名称
    private String desRegion; //到达市
    private String desRegionName; //到达市名称
    private String carDriverPhone; //司机手机号
    private String carDriverName; //司机姓名
    private String plateNumber; //车牌号
    private Long carDriverId; //司机id
    private String name; //姓名
    private List<OilCardManagement> oilCardList; //油卡
}
