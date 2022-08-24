package com.youming.youche.order.vo;

import javax.xml.crypto.Data;
import java.io.Serializable;

@lombok.Data
public class OrderFeeVo implements Serializable {

    private static final long serialVersionUID = -4190865953767914000L;
    /**
     * 主驾补贴
     */
    private Long carDriverSubsidy;

    /**
     * 主驾补贴总费用
     */
    private Float carDriverSubsidyValue;

    /**
     * 总油耗
     */
    private Float oilTotal;

    /**
     *
     */
    private Long driverSwitchSubsidy;

    /**
     * 司机补贴天数
     */
    private Integer driverSubsidyDays;

    /**
     * 空载距离
     */
    private Long emptyDistance;

    /**
     * 副驾驶补贴
     */
    private Float copilotSubsidy;

    /**
     * 副驾驶补贴总费用
     */
    private Float copilotSubsidyValue;

    /**
     * 路桥费费用
     */
    private Float pontageFee;

    /**
     * 主驾补贴天数
     */
    private Data carDriverSubsidyDate;

    /**
     * 油单价
     */
    private Float oilPrice;

    /**
     * 总油费
     */
    private Float oilCostPrice;

    /**
     * 预估成本
     */
    private Float estFee;

}
