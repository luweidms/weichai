package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author: luona
 * @date: 2022/5/5
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class LineInfoDto implements Serializable {
    private String sourceName;//线路名称
    private Double margin;//线路毛利（收入统计-成本统计）
    private String grossMargin;//线路毛利率（线路毛利/收入统计）
    private Long weight;//货物重量
    private Long square;//货物体积
    private Double notarizeReceivable;//收入统计(订单收入+异常款+对账调整)
    private Double mileageNumber;//公里数
    private Double preOilVirtualFee;//预付虚拟油卡金额
    private Double preOilFee;//预付油卡金额
    private Double oilTotal;//油费
    private Double consumeOilFee;//上报费用油费
    private Double pontage;//路桥费
    private Double consumePontageFee;//上报费用路桥费
    private Double consumeFee;//其他费用
    private Double costStatistics;//成本统计（油费+路桥费+其他费用）
    private String createTime;//线路月份
    private Integer paymentWay;//1:智能模式 2:报账模式 3:承包模式
    private Double preEtcFee;//etc


    private Double driverOaLoanAmount;//司机借支
    private Double userOaLoanAmount;//员工借支
    private Double amount;//司机报销
    private Long basicSalaryFee;//司机工资






}
