package com.youming.youche.order.dto.order;

import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderOilDepotSchemeVer;
import com.youming.youche.order.domain.order.OrderReceipt;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author zengwen
 * @date 2022/5/13 13:47
 */
@Data
public class OrderListWxDto implements Serializable {

    private Long orderId;// 订单号
    private LocalDateTime dependTime; //靠台时间
    private Integer sourceRegion;//始发市
    private Integer desRegion;//到达市
    private Integer orderState;//订单状态
    private String plateNumber;//车牌号
    private String carDriverMan;//司机姓名
    private String carDriverPhone;//司机电话
    private Integer vehicleClass;//车辆类型 例如 招商挂靠车
    private String customName;//客户名称
    private String companyName;//客户全称

    private String orderStateName;//订单状态
    private String desRegionName;//到达市
    private String sourceRegionName;//始发市

    /**
     * 估算价格状态0:等待2:失败1:成功
     */
    private Integer preAmountFlag;

    private String goodsName;//品名
    private Float square;//体积
    private Float weight;//重量
    private String reciveName;//收货人
    private String recivePhone;//收货人手机
    private Long localUser;//跟单人
    private String localPhone;//跟单人手机
    private String localUserName;//跟单人名称

    /**
     * 修改订单状态 0 修改 1 审核
     */
    private Integer orderUpdateState;

    /**
     *
     */
    private Integer auditType;

    /**
     *
     */
    private String auditTypeName;

    /**
     * 回单地址
     */
    private String reciveAddr;

    /**
     * 回单地址的市id
     */
    private Long reciveCityId;

    /**
     * 回单地址的省份id
     */
    private Long reciveProvinceId;

    /**
     * 回单地址的市名称
     */
    private String reciveCityIdName;

    /**
     * 回单地址的省份名称
     */
    private String reciveProvinceIdName;


    private Integer noAuditExcNum;//待审核异常数量

    private String receiptsNumber;//回单编号

    /**
     * 图片id
     */
    private String contractId;

    /**
     * 合同图片url
     */
    private String contractUrl;

    /**
     *
     */
    private String receiptsId;

    /**
     *
     */
    private String receiptsUrl;

    private String absoluteReceiptsUrl;//回单图片 绝对路径
    private String absoluteCcontractUrl;//合同地址 绝对路径
    private List<OrderReceipt> absoluteReceiptsUrls;//回单地址集合

    /**
     * 指导价格
     */
    private Long guidePrice;

    /**
     * 总费用
     */
    private Long totalFee;

    /**
     * 预付总计金额
     */
    private Long preTotalFee;

    /**
     * 预付油卡金额
     */
    private Long preOilFee;

    /**
     * 预付油卡金额
     */
    private Long preEtcFee;

    /**
     * 预付虚拟油卡金额
     */
    private Long preOilVirtualFee;

    /**
     *
     */
    private Long oilFeeSum;

    /**
     * 预估成本
     */
    private Long  estFee;

    /**
     * 路桥费
     */
    private Long pontage;

    /**
     *
     */
    private Integer isContract;

    /**
     * 付款方式 1:包干模式 2:实报实销模式 3:承包模式
     */
    private Integer paymentWay;

    /**
     * 付款方式名称
     */
    private String paymentWayName;

    /**
     * 预付油审核状态 0 为不需审核 1 需要审核
     */
    private Integer preOilAuditSts;

    /**
     * 预付ETC审核状态 0 为不需审核 1 需要审核
     */
    private Integer preEtcAuditSts;

    /**
     * 预付款审核状态 0 为不需审核 1 需要审核
     */
    private Integer preTotalAuditSts;

    /**
     * 中标价审核状态 0 为不需审核 1 需要审核
     */
    private Integer totalAuditSts;

    /**
     * 预付总计上限比例标准
     */
    private Long preTotalScaleStandard;

    /**
     * 预付虚拟油卡比例标准
     */
    private Long preOilVirtualScaleStandard;

    /**
     * 预付油卡比例标准
     */
    private Long preOilScaleStandard;

    /**
     * 预付ETC比例标准
     */
    private Long preEtcScaleStandard;

    /**
     *
     */
    private Long preTotalFeeStandard;

    /**
     *
     */
    private Long preOilFeeStandard;

    /**
     *
     */
    private Long preEtcFeeStandard;

    private Integer reciveState;//回单状态
    private String reciveStateName;//回单状态
    private Integer loadState;//合同状态
    private String loadStateName;//合同状态

    /**
     * 现付等值卡卡号
     */
    private String prePayEquivalenceCardNumber;

    /**
     * 后付等值卡卡号
     */
    private String afterPayEquivalenceCardNumber;

    private Integer reciveType;//回单类型

    /**
     * 回单类型名称
     */
    private String reciveTypeName;

    private List<Map<String, Object>> compareOut;//修改订单差异字段
    private List<OrderOilDepotScheme> schemes;//原始订单油站分配
    private List<OrderOilDepotSchemeVer> schemeVers;//修改之后油站分配

    private String orderLine;//订单总线路
    private Boolean isTransitLine;//是否有经停城市

    /**
     *
     */
    private Integer arrivePaymentState;

    /**
     * 到付费用
     */
    private Long arrivePaymentFee;

    /**
     * 到付状态名称
     */
    private String arrivePaymentStateName;

    /**
     * 线路名称
     */
    private String sourceName;

    /**
     *
     */
    private Boolean isFinalAuditJurisdiction;

    /**
     *
     */
    private String auditFinalUserName;

    /**
     *
     */
    private Boolean isFinalFinallyNode;

    public Long getOilFeeSum() {
        setOilFeeSum((getPreOilFee() != null ? getPreOilFee() : 0) +  (getPreOilVirtualFee() != null ? getPreOilVirtualFee() : 0 ));
        return oilFeeSum;
    }

    public Long getPreTotalFeeStandard() {
        setPreTotalFeeStandard((getTotalFee() != null ? getTotalFee() : 0 ) *  (getPreTotalScaleStandard() != null ? getPreTotalScaleStandard() : 0));
        return preTotalFeeStandard;
    }

    public Long getPreOilFeeStandard() {
        setPreOilFeeStandard((getTotalFee() != null ? getTotalFee() : 0 )  *  ((getPreOilScaleStandard() != null ? getPreOilScaleStandard() : 0) + (getPreOilVirtualScaleStandard() != null ? getPreOilVirtualScaleStandard() : 0 )) );
        return preOilFeeStandard;
    }

    public Long getPreEtcFeeStandard() {
        setPreEtcFeeStandard((getTotalFee() != null ? getTotalFee() : 0 )  * (getPreEtcScaleStandard() != null ? getPreEtcScaleStandard() : 0));
        return preEtcFeeStandard;
    }
}
