package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SaveExportOrderDto implements Serializable {


    private static final long serialVersionUID = 7545316559475470988L;

    /**
     * 线路信息:线路编号
     */
    private String lineCodeRule;
    /**
     * 线路信息:线路名称
     */
    private String sourceName;

    /**
     * 线路信息:要求靠台时间（必填）
     */
    private String dependTime;

    /**
     * 线路信息:客户公里数（km）
     */
    private String cmMileageNumber;

    /**
     * 线路信息:供应商公里数
     */
    private String mileageNumber;

    /**
     * 货物信息:货物名称
     */
    private String goodsName;

    /**
     * 货物信息:货物类型
     */
    private String goodsType;

    /**
     * 货物信息:货物重量（吨）
     */
    private String weight;

    /**
     * 货物信息:货物体积（方）
     */
    private String square;

    /**
     * 收货信息:收货人
     */
    private String reciveName;

    /**
     * 收货信息:收货电话
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

    /**
     * 收入信息:预估收入
     */
    private String costPrice;

    /**
     *调度信息:车牌号
     */
    private String plateNumber;

    /**
     * 调度信息:车辆种类
     */
    private String carStatus;

    /**
     *调度信息:挂车车牌
     */
    private String trailerPlate;

    /**
     * 调度信息:主驾驶手机号
     */
    private String mainDriverPhone;

    /**
     * 调度信息:副驾驶手机号
     */
    private String copilotPhone;

    /**
     * 调度信息:成本模式（必选）
     */
    private String costModel;

    /**
     * 报账模式:空载油耗(升/百公里)
     */
    private String bzLoadEmptyOilCost;

    /**
     * 报账模式:载重油耗(升/百公里)
     */
    private String bzLoadFullOilCost;

    /**
     * 报账模式:虚拟油充值金额(元)
     */
    private String bzPreOilVirtualFee;

    /**
     *报账模式:分配油来源 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private String bzOilAccountType;

    /**
     * 承包模式:承包价(元)
     */
    private String cbGuideMerchant;

    /**
     *承包模式:预付现金(元)
     */
    private String cbPreCashFee;

    /**
     *承包模式:虚拟油卡(元)
     */
    private String cbOilVirtualScale;

    /**
     *承包模式:实体油卡（元）
     */
    private String cbOilcardMoneyEntity;

    /**
     * 承包模式:ETC(元)
     */
    private String cbEtcMonery;

    /**
     * 承包模式:到付款
     */
    private String cbIncomeArrivePaymentFee;

    /**
     * 承包模式:尾款（元）
     */
    private String cbBalancePaymentValue;

    /**
     * 承包模式:分配油来源 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private String cbOilAccountType;

    /**
     *智能模式:空载油耗（升/百公里）
     */
    private String znLoadEmptyOilCost;
    /**
     * 智能模式:载重油耗（升/百公里）
     */
    private String znLoadFullOilCost;
    /**
     * 智能模式；路桥费（元/公里）
     */
    private String znPontage;

    /**
     * 智能模式:空载距离（KM）
     */
    private String znEmptyDistance;
    /**
     * 智能模式:总耗油量（升）
     */
    private String znOilCost;
    /**
     * 智能模式：油站名称1
     */
    private String znOilStationName1;
    /**
     * 智能模式：油站地址1
     */
    private String znOilStationAddress1;
    /**
     * 智能模式：加油升数1（升）
     */
    private String znOilDepotLitre1;
    /**
     * 智能模式：油站名称2
     */
    private String znOilStationName2;

    /**
     * 智能模式：油站地址2
     */
    private String znOilStationAddress2;
    /**
     * 智能模式：加油升数2（升）
     */
    private String znOilDepotLitre2;
    /**
     * 智能模式：油站名称3
     */
    private String znOilStationName3;
    /**
     * 智能模式：油站地址3
     */
    private String znOilStationAddress3;
    /**
     * 智能模式：加油升数3（升）
     */
    private String znOilDepotLitre3;
    /**
     * 智能模式：分配油来源 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     *
     */
    private String znOilAccountType;
    /**
     * 智能模式：油卡升数（升）
     */
    private String znOilcardLiters;
    /**
     * 智能模式：油卡单价（元/升）
     */
    private String znOilPrice;

    /**
     * 临时外调车/业务招商车/外来挂靠车:中标价(元)
     */
    private String totalFee;
    /**
     * 临时外调车/业务招商车/外来挂靠车:预付现金(元)
     */
    private String preCashFee;
    /**
     * 临时外调车/业务招商车/外来挂靠车:虚拟油卡(元)
     */
    private String oilVirtualScale;
    /**
     * 临时外调车/业务招商车/外来挂靠车:实体油卡(元)
     */
    private String oilcardMoneyEntity;
    /**
     * 临时外调车/业务招商车/外来挂靠车:到付款(元)
     */
    private String incomeArrivePaymentFee;
    /**
     * 临时外调车/业务招商车/外来挂靠车:尾款(元)
     */
    private String balancePaymentValue;
    /**
     * 临时外调车/业务招商车/外来挂靠车:分配油来源
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private String  oilAccountType;
    /**
     * 临时外调车/业务招商车/外来挂靠车:代收人手机号
     */
    private String collectionUserPhone;
    /**
     * 临时外调车/业务招商车/外来挂靠车:代收人名称
     */
    private String collectionUserName;

    /**
     * 结算信息:结算方式，1账期，2月结
     */
    private String balanceType;
    /**
     * 结算信息:回单期限
     */
    private String reciveTime;
    /**
     * 结算信息:对账期限
     */
    private String reconciliationDay;
    /**
     * 结算信息:开票期限
     */
    private String invoiceTime;
    /**
     * 结算信息:付款期限
     */
    private String payPeriod;

    /**
     * 归属信息:跟单员手机号
     */
    private String stateNamePhone;
    /**
     * 归属信息:归属部门
     */
    private String orgName;


}
