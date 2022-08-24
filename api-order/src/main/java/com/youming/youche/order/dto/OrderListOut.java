package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class OrderListOut implements Serializable {
    private static final long serialVersionUID = -991847116084122351L;
    private Long orderId; //  订单id
    private String goodsName;//品名
    private String createTime;
    private String orderName;
    private Integer sourceRegion;//始发市
    private String sourceRegionName;//始发市
    private Integer desRegion;//到达市
    private String desRegionName;//到达市
    private Integer orderUpdateState;//修改订单状态
    private Long fromOrderId;//来源订单
    private Long toOrderId;//接单订单
    private String customName;//客户名称
    private String plateNumber;//车牌号
    private String carDriverPhone;//司机手机
    private Long carDriverId;//司机ID
    private String carDriverMan;//司机姓名
    private Long copilotUserId;//副驾ID
    private Long vehicleCode;//车辆ID
    private String carLengh;//车长
    private String carLenghName;//车长
    private Integer carStatus;//车辆种类  例如:箱车
    private String carStatusName;//车辆种类  例如:箱车
    private Integer carUserType;//用户类型
    private String carUserTypeName;//用户类型
    private Integer vehicleClass;//车辆类型 例如 招商挂靠车
    private String vehicleClassName;//车辆类型 例如 招商挂靠车
    private LocalDateTime dependTime;//靠台时间
    private Integer orderState;//订单状态
    private String orderStateName;//订单状态
    private Integer reciveType;//回单类型
    private String reciveTypeName;//回单类型
    private Integer reciveState;//回单状态
    private String reciveStateName;//回单状态
    private String receiptsId;//签收回单ID（多个已英文逗号分开）
    private String receiptsUrl;//签收回单URL（多个已英文逗号分开
    private String contractId;//合同图片ID
    private String contractUrl;//合同图片地址
    private Integer exceptionDealNum;//异常待处理数量
    private Long guidePrice;//指导价
    private Long costPrice;//预估收入
    private Long totalFee;//中标价
    private Long estFee;//预估成本
    private Integer orderType;//订单类型
    private String orderTypeName;//订单类型
    private Float square;//体积
    private Float weight;//重量
    private Long preAmountFlag;//支付预付款状态
    private Integer isTransit;//是否外发
    private Long preTotalFee;//预付总计金额
    private Long preCashFee;//预付现金金额
    private Long preOilVirtualFee;//预付虚拟油卡金额
    private Long preOilFee;//预付油卡金额
    private Long preEtcFee;//预付ETC金额
    private Long finalFee;//尾款金额
    private Integer loadState;//合同状态
    private String loadStateName;//合同状态
    private String toTenantName;//承运人
    private Integer appointWay;//指派方式
    private Boolean isNeedAudit;//是否需要修改订单审核
    private Boolean isJurisdiction;//是否有审核权限
    private Long toTenantId;//接单租户
    private Integer auditSource;//审核来源
    private String auditSourceName;//审核来源
    private String refuseOrderReason;//拒单原因
    private Float arriveTime;//到达时限;
    private Integer paymentWay;//自由车付款方式
    private String paymentWayName;
    private Long copilotSalary;//副驾驶补贴
    private Long salary;//主驾驶补贴
    private Long driverSwitchSubsidy;//切换司机补贴
    private Long subsidyFeeSum;//补贴总金额
    private String receiptsNumber;//回单编号
    private Integer exceptionCount;//异常总数量
    private Integer agingCount;//时效数量
    private Integer agingAduitCount;//时效待审核数量
    private Integer isCollection;//是否代收 0 不代收 1 代收
    private String costReportStateName;//费用上报状态
    private String orderLine;//订单总线路
    private Boolean isTransitLine;//是否有经停城市
    private Boolean isPayDriverSubsidy;//是否需要支付补贴
    private Long sourceId;//线路ID
    private Integer trackType;//轨迹变化
    private String trackMsg;//轨迹变化提示
    private String sourceName;//线路名称
    private Boolean isAddProblem;//是否能添加异常
    private String customNumber;//客户单号

    private Integer arrivePaymentState;//到付状态 0 未支付 1 已支付
    private Long arrivePaymentFee;//到付费用

    private Boolean isPreAuditJurisdiction;
    private String auditPreUserName;
    private Boolean isPreFinallyNode;

    private Boolean isArriveAuditJurisdiction;
    private String auditArriveUserName;
    private Boolean isArriveFinallyNode;

    private Boolean isFinalAuditJurisdiction;
    private String auditFinalUserName;
    private Boolean isFinalFinallyNode;
    private Integer licenceType;//车辆类型(整车/拖头)

    private Integer goodsType;//货品类型
    private String goodsTypeName;

    private Boolean isCalibration;

    private String collectionUserName;//代收人名

    private String lugeSignAgreeUrl;//路歌协议地址
    private Integer signAgreeState;//签订协议状态

    private Integer isNeedBill;//票据信息
    private Integer oilAccountType;//油费来源账户
    private Integer oilBillType;//油费开票类型
    private Boolean isNeedUploadAgreement;//是否需要上传协议

    //校验靠台时间
    private Date verifyDependDate;
    //校验离台时间
    private Date verifyStartDate;
    //校验到达时间
    private Date verifyArriverDate;
    //校验离场时间
    private Date verifyDepartureDate;

    //运输线路
    private String transportationRoute;
    //异常创建时间
    private Date exceptionCreationTime;
    //异常类型
    private String exceptionType;
    //等级金额
    private Long registeredAmount;
    //处理金额
    private Long processingAmount;
    //异常描述
    private String exceptionDescription;
    //异常来源
    private String abnormalSource;
    //内部审核
    private Long internalAudit;
    //处理时间
    private Date processingTime;

    private Long tenantId;
    //上报状态
    private Integer reportState;
    /**
     * 路桥费
     */
    private Long pontage;

    public String getAuditSourceName() {
        if(this.auditSource == null){
            this.auditSourceName = "";
        }else if(this.auditSource == 1){
            this.auditSourceName = "修改订单";
        }else if(this.auditSource == 2){
            this.auditSourceName = "价格异常";
        }
        return auditSourceName;
    }

    public void setAuditSourceName(String auditSourceName) {
        this.auditSourceName = auditSourceName;
    }

    /**
     * 司机 消息  1已读 2未读
     */
    private Integer message;

}
