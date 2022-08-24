package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OilEntityVo implements Serializable {

    private String startTime;// 开始时间
    private String endTime; //结束时间
    private String companyName; //公司名称
    private Integer sourceProvinc; //始发省
    private Integer sourceRegion; //始发市
    private Integer sourceCounty; //始发县
    private Integer desProvince; //到达省
    private Integer desRegion; //到达市
    private Integer desCounty; //到达县
    private Integer verifySts; //油卡状态
    private String orderId; //订单号
    private String oilCarNum; //油卡卡号
    private String busiCode; //业务编码
    private String carNo; //车编号
    private String customName; //客户名称
    private String carDriverMan; //司机名称
    private String carDriverPhone; //司机手机号码
    private String sourceName; //线路名称
    private Integer vehicleClass; //车辆类型
    private String plateNumber; //车牌号
    private String serviceName; //服务商名称
    private LocalDateTime verificationDate; //核销时间
}
