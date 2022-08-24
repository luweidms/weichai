package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: luona
 * @date: 2022/5/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class EstimatedCostsDto implements Serializable {
    private List<Map> driverSubsidyDays;//补贴天数集合
    private Float driverSwitchSubsidy;//切换司机补贴
    private Float copilotSubsidy;//副驾补贴具体时间
    private String copilotSubsidyDate;//副驾补贴
    private Float emptyDistance;//空载的运输距离
    private Float pontageFee;//路桥费费用
    private Float oilTotal;// 总油耗
    private Float oilPrice;//油价(分/升)
}
