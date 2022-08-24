package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderFeeDto implements Serializable {

    private static final long serialVersionUID = -2724654059451027475L;
    private Long carDriverId;//主驾司机id
    private Long copilotUserId;//副驾司机id
    private String  plateNumber;//车牌号码
    private Long oilPrice;//油单价
    private String pontagePer;//过路费单价
    private Float distance;//距离
    private Long vehicleCode;//车辆编码
    private Long lineId;//线路id
    private Long emptyDistance;//空载的运输距离
    private Long loadFullOilCost;//载重油耗
    private Integer provinceId;//省份id
    private String arriveTime;//到达时限
    private String  nand;//纬度
    private String eand;//经度
    private Long loadEmptyOilCost;//空载油耗
    private Integer region;//城市
    private Long trailerId;//挂车记录id
    private String dependTime;//靠台时间
    private Integer isUpdate;//是否修改
}
