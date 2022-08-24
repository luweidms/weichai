package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/5/14
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderListWxOutDto implements Serializable {
    private Long orderId; //  订单id
    private LocalDateTime dependTime;//靠台时间
    private Integer sourceRegion;//始发市
    private Integer desRegion;//到达市
    private Integer orderState;//订单状态
    private String orderStateName;//订单状态
    private String plateNumber;//车牌号
    private String carDriverMan;//司机姓名
    private String carDriverPhone;//司机手机
    private Integer vehicleClass;//车辆类型 例如 招商挂靠车
    private String vehicleClassName;//车辆类型 例如 招商挂靠车
    private String goodsName;//商品名
    private String customName;//客户名称
    private Float square;//体积
    private Float weight;//重量
    private String reciveName;//收货人
    private String recivePhone;//收货人手机
    private Long localUser;//跟单人id
    private String localPhone;//跟单人手机
    private String localUserName;//跟单人名称
    private Integer orderUpdateState;
    private Integer reciveType;//回单类型
    private String preAmountFlagName;//支付预付款状态
    private String sourceName;//线路名称
    private Long reciveCityId;//回单城市
    private Long reciveProvinceId;//回单省份
    private String inReciveAddr;//收入回单地址
    private Integer noAuditExcNum;//待审核异常数量
    private String contractId;//合同Id
    private Long arrivePaymentFee;//到付费用
    private Long guidePrice;//指导价
    private Long totalFee;//总运费
    private Long preOilFee;//预付实体油
    private Long preEtcFee;//Etc
    private Long preTotalFee;//预付总计金额
    private Long preOilVirtualFee;//预付虚拟油
    private Long estFee;//预估成本
    private Long pontage;//路桥费
    private Integer paymentWay;//自有车付款方式
    private String paymentWayName;//自有车付款方式
    private Integer preOilAuditSts;//预付油审核状态 0 为不需审核 1 需要审核
    private Integer preEtcAuditSts;//预付etc审核状态 0 为不需审核 1 需要审核
    private Integer preTotalAuditSts;//预付款审核状态 0 为不需审核 1 需要审核
    private Integer totalAuditSts;//中标价审核状态 0 为不需审核 1 需要审核
    private Long preEtcScaleStandard;//预付ETC比例标准
    private Long preTotalScaleStandard;//预付总计上限比例标准
    private Long preOilVirtualScaleStandard;//预付虚拟油卡比例标准
    private Long preOilScaleStandard;//预付油卡比例标准
    private Integer reciveState;//回单状态
    private Integer loadState;//合同状态
    private String loadStateName;//合同状态名称
    private String prePayEquivalenceCardNumber;//现付等值卡卡号
    private String afterPayEquivalenceCardNumber;//后付等值卡卡号


}
