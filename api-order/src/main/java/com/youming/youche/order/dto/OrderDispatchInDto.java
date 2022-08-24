package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.vo.OrderOilCardInfoVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhouchao on 18/4/9.
 */
@Data
public class OrderDispatchInDto implements Serializable {

    /**
     * preTotalScaleStandard : 0.82
     * preOilFeeY : 0
     * preOilVirtualScaleShow : 40
     * carDriverMan : 张永
     * acctName : 翁锡逵
     * preCashScaleShow : 2
     * insuranceFee : 0
     * totalFeeY : 20
     * preEtcFeeY : 8
     * preEtcScaleShow : 40
     * vehicleStatusName : 厢车
     * plateNumber : 云C22453
     * preOilScaleShow : 0
     * tenantId : 104
     * finalScale : 0.18
     * remark : remark
     * acctNo : 6226090203831587
     * preCashScaleStandard : 0.02
     * tenantName : 志鸿
     * finalCashFeeY : 3.6
     * isNeedBill :
     * preOilVirtualScaleStandard : 0.4
     * preEtcScaleStandard : 0.4
     * appointWay : 3
     * paymentDays : 15
     * vehicleClassName : 临时外调车
     * receiveUser :
     * preOilVirtualFeeY : 8
     * finalScaleShow : 18
     * finalFeeY : 3.6
     * vehicleClass : 3
     * preCashFeeY : 0.4
     * preTotalScaleShow : 82
     * preTotalFeeY : 16.4
     * preOilScaleStandard : 0
     * vehicleLengthName : 17.5米
     * carDriverPhone : 18587005826
     * carDriverId : 10029591141235
     */

    private String preTotalScaleStandard;
    private String preOilFeeY;
    private String preOilVirtualScaleShow;
    private String carDriverMan;
    private String acctName;
    private String preCashScaleShow;
    private String insuranceFee;
    private String totalFeeY;
    private String preEtcFeeY;
    private String preEtcScaleShow;
    private String vehicleStatusName;
    private String plateNumber;
    private String preOilScaleShow;
    private String tenantId;
    private String finalScale;
    /**
     * 备注
     */
    private String remark;
    private String acctNo;
    private String preCashScaleStandard;
    private String tenantName;
    private String finalCashFeeY;
    private String isNeedBill;
    private String preOilVirtualScaleStandard;
    private String preEtcScaleStandard;
    private String appointWay;
    /**
     * 账期
     */
    private String paymentDays;
    private String vehicleClassName;
    private String receiveUser;
    private String preOilVirtualFeeY;
    private String finalScaleShow;
    private String finalFeeY;
    private String vehicleClass;
    private String preCashFeeY;
    private String preTotalScaleShow;
    private String preTotalFeeY;
    private String preOilScaleStandard;
    private String vehicleLengthName;
    private String carDriverPhone;
    private String carDriverId;
    private String vehicleCode;
    private String trailerId;
    private String trailerPlate;
    private String copilotMan;
    private String copilotUserId;
    private String copilotPhone;
    private String oilLitreTotal;
    private String oilLitreVirtual;

    private String oilDepotIdOne;
    private String oilDepotNameOne;
    private String dependDistanceOne;
    private String oilDepotPriceOne;
    private String oilDepotLitreOne;
    private String oilDepotFeeOne;
    private String oilDepotNandOne;
    private String oilDepotEandOne;
    private String oilDepotIdTwo;
    private String oilDepotNameTwo;
    private String dependDistanceTwo;
    private String oilDepotPriceTwo;
    private String oilDepotLitreTwo;
    private String oilDepotFeeTwo;
    private String oilDepotNandTwo;
    private String oilDepotEandTwo;
    private String oilDepotIdThree;
    private String oilDepotNameThree;
    private String dependDistanceThree;
    private String oilDepotPriceThree;
    private String oilDepotLitreThree;
    private String oilDepotFeeThree;
    private String oilDepotNandThree;
    private String oilDepotEandThree;
    private String isContract;
    private String isBackhaul;
    private String backhaulPriceY;
    private String backhaulLinkMan;
    private String backhaulLinkManBill;
    private String backhaulLinkManId;

    private String userSubsidy;
    private String copilotSubsidy;
    private String pontage;
    private String estFee;

    private String vehicleStatus;
    private String vehicleLength;
    private String guidePrice;

    private String orderInfoId;
    private String orderInfoExtId;
    private String orderFeeId;
    private String orderGoodsId;
    private String orderFeeExtId;
    private String toTenantName;

    private String emptyOilCostPerY;//空载油耗
    private String oilCostPerY;//载重油耗
    private String pontagePerY;//路桥费
    private String licenceType;//牌照类型
    private String oilLitreEntity;//实体油
    private String oilPrice;//省份油单价
    private String carDriverSubsidyDate;//主驾补贴
    private String copilotSubsidyDate;//副驾补贴

    private String oilSelfEntity;//自有车实体油费
    private String oilSelfVirtual;//自有车虚拟油费
    private String runWay;//空驶距离
    private String isUseCarOilCost;//是否用车辆油耗
    private String guidePriceY;//拦标价  共享订单时

    //转单信息
    private String fromTenantId;
    private String fromOrderId;
    private String toOrderId;
    private String orderState;
    private String sourceFlag;
    private String orgId;
    private String orderType;
    private String isTransit;
    private String localUser;
    private String localUserName;
    private String localPhone;
    /**
     * 回单期限（天）
     */
    private String reciveTime;
    /**
     * 开票期限（天）
     */
    private String invoiceTime;
    /**
     * 收款期限
     */
    private String collectionTime;
    private String reciveAddr;

    /**
     * 代收人
     */
    private String isAgent;
    /**
     * 代收服务商id
     */
    private String agentId;
    private String agentPhone;
    private String agentAccountNo;
    private String agentAccountName;

    private String oilIsNeedBill;
    private String oilUseType;
    private String paymentWay;

    private List<OrderOilCardInfoVo> oilCardStr;

    private String arrivePaymentFeeY;
    private String arrivePaymentFeeScaleShow;

    //账单接收人
    private String billReceiverMobile;
    private String billReceiverName;
    private String billReceiverUserId;

    private String oilAccountType;//油费来源账户
    private String oilBillType;//油费开票类型

    private String oilConsumer;//油消费对象

    private String orderSchedulerId;

    private String orderId;
    /**
     * 接单车队ID
     */
    private String toTenantId;


}
