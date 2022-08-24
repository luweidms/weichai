package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/3/26 11:49
 */
@Data
public class AdvanceChargeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /************外调车入参***************/
    private long userId;//用户编号 如果是代收单时此userId是代收人用户id
    private String vehicleAffiliation;//资金渠道
    private long amountFee;//可用金额单位(分)
    private long virtualOilFee;//虚拟油卡金额单位(分)
    private long entityOilFee;//实体油卡金额单位(分)
    private long ETCFee;//ETCFee金额单位(分)
    private long orderId;//订单编号
    private Long tenantId;//订单开单租户id
    private long totalFee;//订单总金额
    private int isNeedBill;//是否开票
    private int payType;
    private String oilAffiliation;//资金渠道(油)
    private int oilUserType;//油使用方式
    //油费预存资金:0否，1是
    private Integer oilFeePrestore;
    //平安付款账户编号(平安虚拟子账号)
    private String pinganPayAcctId;
    //油费消费类型:1自有油站，2共享油站
    private Integer oilConsumer;
    //果是代收单时此collectionDriverUserId是司机id
    private long collectionDriverUserId;
    //油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
    private Integer oilAccountType;
    //油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
    private Integer oilBillType;

    /************自有车入参***************/
    private long masterUserId;//司机id(主驾驶)
    private long fictitiousOilFee;//虚拟油卡金额单位(分)
    private long bridgeFee;//路桥费 只记录流水不扣款
    private long masterSubsidy;//补贴 给司机增加金额（主驾驶）
    private long slaveSubsidy;//补贴 给司机增加金额（副驾驶）
    private long slaveUserId;//副驾驶司机id

}
