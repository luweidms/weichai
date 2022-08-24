package com.youming.youche.order.dto;


import lombok.Data;

import java.io.Serializable;


@Data
public class OrderInfoExportDto implements Serializable {
    /**
     * 线路信息:线路编号
     */
    private String lineCodeRule;

    /**
     * 线路名称
     */
    private String sourceName;

    /**
     * 靠台时间
     */
    private String carDependDate;

    /**
     * 线路信息:客户公里数（km）
     */
    private String cmMileageNumber;


    /**
     * 线路信息:供应商公里数
     */
    private String mileageNumber;

    /**
     * 货物名称
     */
    private String goodsName;


    /**
     * 货物信息:货物类型
     */
    private String goodsType;


    /**
     * 货物重量KG
     */
    private String weight;


    /**
     * 货物体积
     */
    private String square;


    /**
     * 收货人
     */
    private String reciveName;


    /**
     * 收货电话
     */
    private String recivePhone;

    /**
     * 收入信息:是否加班车(必选)
     */
    private String isUrgent;


    /**
     * 收入信息:收入单价
     */
    private String priceUnit;


    /**
     * 收入信息:计算单位
     */
    private String unitName;



    private String costPrice;//预估收入

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 车辆种类
     */
    private String carStatus;


    /**
     * 挂车车牌
     */
    private String trailerPlate;

    /**
     * 调度信息:主驾驶手机号
     */
    private String mainDriverPhone;


    /**
     * 副驾驶手机
     */
    private String copilotPhone;

    /**
     * 成本模式
     */
    private String paymentWay;

    /**
     * 票据要求
     */
    private String isNeedBill;

    /**
     * 空载油耗
     */
    private String runOil;

    /**
     * 载重油耗
     */
    private String capacityOil;
    /**
     * 虚拟金额
     */
    private String virtualAmount;


    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     * 分配油来源
     */
    private String oilAccountType;

    /**
     * 承包价格
     */
    private String cashFee;


    /**
     *承包模式:预付现金(元)
     */
    private String cbPreCashFee;



    /**
     * 虚拟油卡金额
     */
    private String preOilVirtualFee;

    /**
     * 实体油卡金额
     */
    private String entityOilFee;

    /**
     * ETC元
     */
    private String etcFee;


    /**
     * 到付款
     */
    private String arriveFee;

    /**
     * 尾款
     */
    private String balance;

    /**
     * 分配油来源
     */
    private String distributionOilSource;
    /**
     * 空载油耗
     */
    private String loadEmptyOilCost;

    /**
     * 载重油耗
     */
    private String intelligentCapacityOil;
    /**
     * 桥路费
     */
    private String bridgeToll;

    /**
     * 空载距离
     */
    private String emptyDistance;

    /**
     * 总耗油量
     */
    private String oilLitreTotal;

    /**
     * 智能模式：油站名称1
     */
    private String znOilStationName1;
    /**
     * 智能模式：油站地址1
     */
    private String znOilStationAddress1;
    /**
     * 加油升数1
     */
    private String oilRise1;
    /**
     * 智能模式：油站名称2
     */
    private String znOilStationName2;
    /**
     * 智能模式：油站地址2
     */
    private String znOilStationAddress2;
    /**
     * 加油升数2
     */
    private String oilRise2;
    /**
     * 智能模式：油站名称3
     */
    private String znOilStationName3;
    /**
     * 智能模式：油站地址3
     */
    private String znOilStationAddress3;
    /**
     * 加油升数3
     */
    private String oilRise3;
    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     * 分配油来源
     */
    private String intelligentOilAccountType;
    /**
     * 智能模式：油卡升数（升）
     */
    private String znOilcardLiters;

    /**
     * 智能模式：油卡单价（元/升）
     */
    private String znOilPrice;

    /**
     * 中标价
     */
    private String totalFee;

    /**
     * 预付现金金额
     */
    private String preCashFee;

    /**
     * 虚拟油卡金额
     */
    private String preOilVirtualFee2;

    /**
     *智能模式:实体油卡（元）
     */
    private String cbOilcardMoneyEntity;

    private String intelligentArriveFee;//到付款

    /**
     * 尾款金额
     */
    private String finalFee;

    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     * 分配油来源
     */
    private String intelligentoilAccountType1;
    /**
     * 代收人手机
     */
    private String collectionUserPhone;

    /**
     * 代收人名称
     */
    private String collectionUserName;

    /**
     * '结算方式,
     */
    private String balanceType;

    /**
     * 回单期限日
     */
    private String reciveDay;



    /**
     * 对账期限
     */
    private String reconciliationTime;


    /**
     * 开票期限
     */
    private String billPeriod;


    /**
     * 付款期限
     */
    private String payPeriod;


    /**
     * 归属信息:跟单员手机号
     */
    private String stateNamePhone;




    /**
     * 归属部门
     */
    private String attachedOrgName;

    /**
     * 失败原因
     */
    private String failure;
}
