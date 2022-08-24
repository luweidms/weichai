package com.youming.youche.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/4/19
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OilCardRechargeDto implements Serializable {

    private Long id; //订单号
    private Long batchId; //序列号
    private Long orderId; //订单号
    private Long carDriverId; //司机ID
    private Integer sourceRegion; //始发市
    private Integer desRegion; //到达市
    private String vehicleLengh; //车长
    private Integer vehicleStatus; //车辆类型
    private Integer carUserType; //司机类型
    private String plateNumber; //车牌号
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dependTime; //靠台时间
    private Integer rootOrgId; //组织ID
    private String vehicleAffiliation; //资金来源
    private String companyName; //服务商名称
    private String oilCardNum; //油卡卡号
    private Long assignTotal; //油卡金额
    private Long noVerificationAmount; //待核销金额
    private Integer state; //状态
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verificationDate; //核销时间
    private Long tenantId; //车队ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate; //发起时间
    private Integer isReport; //是否处理
    private String reportRemark; //处理描述
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportTime; //处理时间
    private Long vehicleCode; //车牌Code
    private String linkman; //司机姓名
    private String mobilePhone; //司机手机
    private String vehicleStatusName; //车辆状态名称
    private String stateName; //状态名称
    private double assignTotalDouble; //油卡金额
    private double noVerificationAmountDouble; //待核销金额

    public double getNoVerificationAmountDouble() {
        if (noVerificationAmount != null) {
            setNoVerificationAmountDouble(CommonUtil.getDoubleFormatLongMoney(noVerificationAmount, 2));
        }
        return noVerificationAmountDouble;
    }

    public double getAssignTotalDouble() {
        if (assignTotal == null) {
            return 0D;
        } else {
            return CommonUtil.getDoubleFormatLongMoney(getAssignTotal(), 2);
        }
    }

    public void setAssignTotalDouble(double assignTotalDouble) {
        this.assignTotalDouble = assignTotalDouble;
    }

    public String getStateName() {
        if (state != null) {
            if (state.equals(1)) {
                setStateName("未充值");
            } else {
                setStateName("已充值");
            }
        }
        return stateName;
    }

    public String getVehicleStatusName() {
        if (carUserType != null) {
            switch (carUserType) {
                case 1:
                    setVehicleStatusName("公司自有司机");
                    break;
                case 2 :
                    setVehicleStatusName("业务招商司机");
                    break;
                case 3:
                    setVehicleStatusName("临时外调司机");
                    break;
                case 4:
                    setVehicleStatusName("外来挂靠司机");
                    break;
                default:
                    setVehicleStatusName("临时外调司机");
            }
        }
        return vehicleStatusName;
    }
}
