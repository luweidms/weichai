package com.youming.youche.order.dto.order;

import com.youming.youche.order.domain.OrderRetrographyCostInfo;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: luona
 * @date: 2022/5/13
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderDetailsAppDto implements Serializable {
    private static final long serialVersionUID = 9211651022796314586L;
    private Long estFee;//预估成本
    private Long orderId;
    private String goodsName;//品名
    private Integer sourceRegion;//始发市
    private Integer desRegion;//到达市
    private Long distance;//距离 单位/米
    private String tenantName;//车队名称
    private Date dependDate;//靠台时间
    private LocalDateTime dependTime;//靠台时间
    private Integer orderState;//订单状态
    private String source;//起始地
    private String des;//目的地
    private Date carArriveTime;//到达时间
    private LocalDateTime carArriveDate;//到达时间
    private Float square;//体积 单位/方
    private Float weight;//重量 单位/吨
    private String vehicleLengh;//需求车长
    private Integer vehicleStatus;//需求车类型
    private String localPhone;//跟单人手机
    private String localUserName;//跟单人名称
    private String plateNumber;//车牌号
    private String carDriverPhone;//司机手机
    private Long carDriverId;//司机ID
    private String carDriverMan;//司机姓名
    private String copilotMan;//副驾驶
    private String copilotPhone;//副驾手机
    private Long copilotUserId;//副驾名称
    private String carLengh;//车辆车长
    private Integer carStatus;//车型
    private Integer vehicleClass;//车辆类型
    private Integer paymentWay;//自有车付款方式
    private String paymentWayName;
    private Integer preAmountFlag;//支付预付款状态
    private Integer finalAmountFlag;//支付尾款状态
    private Float capacityOil;//空载油耗
    private Float runOil;//载重油耗
    private Integer reciveType;//回单类型
    private Integer reciveState;//回单状态
    private String receiptsId;//回单图片
    private String receiptsUrl;//回单图片
    private String trailerPlate;//挂车车牌

    private Long totalFee;//总运费
    private Long preOilVirtualFee;//预付虚拟油
    private Long preOilFee;//预付实体油
    private Long preCashFee;//预付现金
    private Long preEtcFee;//Etc
    private Long preTotalFee;//预付总计金额
    private Long finalFee;//尾款金额
    private Long insuranceFee;//运费险
    private Long salary;//主驾补贴
    private Long copilotSalary;//副驾补贴


    private Integer reportNum;//报备数量
    private Long exceptionFee;//异常款
    private Long agingFineFee;//时效罚款
    private Long loanSumFee;//借支总额
    private Long expenseSumFee;//报销总额

    private String reciveTypeName;//回单类型
    private String carStatusName;//车辆种类
    private String reciveStateName;//回单状态
    private String vehicleStatusName;//车类型名称
    private String desRegionName;//到达市
    private String sourceRegionName;//始发市
    private Integer isHis;//是否历史单
    private Integer isEvaluate;//是否评价
    private Integer agingStarLevel;//时效星级
    private Integer serviceStarLevel;//服务星级
    private Integer mannerStarLevel;//态度星级

    private String eand;//起点纬度
    private String eandDes;//目的地纬度
    private String nand;//起始经度
    private String nandDes;//目的经度

    private String preAmountFlagName;//支付预付款状态
    private String finalAmountFlagName;//支付尾款状态

    private Long preOilFeeSum;//预付油总金额
    private Long subsidyFeeSum;//补贴总金额

    private String contractId;//合同Id
    private String contractUrl;//合同地址


    private Integer loadState;//合同状态
    private String loadStateName;//合同状态名称

    private String absoluteReceiptsUrl;//回单图片 绝对路径
    private String absoluteCcontractUrl;//合同地址 绝对路径

    private List<OrderOilDepotScheme> depotSchemes;//油站分配方案

    private String vehicleClassName;

    private String customName;//客户名称
    private String customPhone;//客户手机

    private String inReciveAddr;//收入回单地址
    private Integer inReciveTime;//收入回单期限
    private Integer inInvoiceTime;//收入开票期限
    private Integer inCollectionTime;//收入收款期限


    private Integer costReciveTime;//成本回单期限
    private Integer costInvoiceTime;//成本开票期限
    private Integer costCollectionTime;//成本开票期限
    private String costReciveAddr;//收入回单地址

    private Integer isNeedBill;//是否开票
    private String isNeedBillName;//票据类型


    private String orderStateName;//订单状态

    private Long pontage;//路桥费

    private String oilCarNum;//油卡号

    private String receiptsNumber;

    private Long guidePrice;//指导价

    private Long costPrice;//预估收入
    private Integer isUrgent;//是否紧急加班车

    private Long reciveProvinceId;//回单省份
    private Long reciveCityId;//回单城市
    /**
     * 回单地址的省份名称
     */
    private String reciveProvinceName;
    /**
     * 回单地址的市名称
     */
    private String reciveCityName;

    private String dispatcherName;//调度员名称
    private Long dispatcherId;
    private String dispatcherBill;

    private Long orgId;
    private String orgName;
    /**
     * 车队id
     */
    private Long tenantId;
    private Integer isTempTenant;

    private Integer incomeIsNeedBill;//开单方是否开票
    private String incomeIsNeedBillName;//开单方票据类型

    private Long fromOrderId;//来源订单
    private Long runWay;//空驶距离

    private Integer orderCostReportDay;//费用上报失效时间/天
    private Integer orderCostReportState;

    private OrderPaymentDaysInfo costPaymentDaysInfo;
    private OrderPaymentDaysInfo incomePaymentDaysInfo;

    private Integer isReportFee;
    private Long totalReportFee;//上报总费用
    private Date endDate;


    private Long fixedCost;//固定成本
    private Long fee;//费用
    private Long operCost;//直接成本
    private Long changeCost;//变动成本
    private Long totalCost;//总成本 = 固定成本 + 费用 +  直接成本 +变动成本

    private OrderRetrographyCostInfo orderRetrographyCostInfo;//订单反写成本
    private List<OrderTransitLineInfo> transitLineInfos;//经停点
    private List<Map> driverSubsidyDays;

    private Float arriveTime;//到达时限
    private String orderLine;//订单总线路
    private Boolean isTransitLine;//是否有经停城市

    private Long onDutyDriverId;
    private String onDutyDriverName;
    private String onDutyDriverPhone;

    private Long driverSwitchSubsidy;

    private String reciveName;//收货人
    private String recivePhone;
    private List<String> receiptUrlList;//回单图片集合

    private Integer arrivePaymentState;
    private Long arrivePaymentFee;
    private String arrivePaymentStateName;

    private String sourceName;

    private Boolean isPreAuditJurisdiction;
    private String auditPreUserName;
    private Boolean isPreFinallyNode;

    private Boolean isArriveAuditJurisdiction;
    private String auditArriveUserName;
    private Boolean isArriveFinallyNode;

    private String customNumber;//客户单号

    private String remark;//备注
    private Integer goodsType;
    private String goodsTypeName;
    /**
     * 接单车队ID
     */
    private Long toTenantId;
    private Integer isCollection;

    private String lugeSignAgreeUrl;//路歌协议地址
    private boolean isNeedUploadAgreement;//是否需要签署协议
    private Integer signAgreeState;//签订协议状态
    private String signAgreeStateName;

    private Integer balanceType;
    private String balanceTypeName;

    private String companyName;//公司全称

    public void setIsNeedUploadAgreement(boolean isNeedUploadAgreement) {
        this.isNeedUploadAgreement = isNeedUploadAgreement;
    }
}
