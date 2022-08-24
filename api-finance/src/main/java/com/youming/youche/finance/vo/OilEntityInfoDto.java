package com.youming.youche.finance.vo;


import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Wuhao
 * @date 2022/4/14
 */
@Data
public class OilEntityInfoDto implements Serializable {

    private static final long serialVersionUID = -2091976793303817142L;

        private Long id; //主键
        private Long orderId; //订单号
        private Integer sourceRegion; //始发市
        private Integer desRegion; //到达市
        private String sourceRegionName; //始发市名称
        private String desRegionName; //到达市名称
        private Long carDriverId; //司机ID
        private String plateNumber; //车牌号
        private String vehicleLengh; //车辆类型
        private Integer vehicleStatus; //车辆状态
        private String vehicleLenghName; //车辆类型名称
        private String vehicleStatusName; //车辆状态名称
        private LocalDateTime dependTime; //靠台时间
        private Integer carStatus; //车辆状态
        private String carStatusName; //车辆状态名称
        private String oilCarNum; //油卡卡号
        private Integer oilcardType; //油卡类型
        private Integer verificationState; //核销状态
        private LocalDateTime verificationDate; //核销时间
        private String busiName; //业务编号名称
        private double showMoney; //显示金额
        private Long noVerificateEntityFee; //未核销金额
        private Long costEntityOil; //实体油
        private double noVerificateEntityFeeDouble; // 未核销油费
        private double costEntityOilDouble; //实体邮费
        private Integer hasIncrEntityFeeFlag; //是否为实体
        private Integer carUserType; //司机类型
        private Long tenantId; //租户ID
        private String serviceName; //服务商名称
        private double preOilScaleStandard; //预付油卡比例
        private double preOilVirtualScaleStandrd; //预付虚拟油卡比例
        private String verificationStateName; //核销状态名称
        private Long userId; //用户ID
        private Long vehicleCode; //车辆code
        private Integer vehicleClass; //车辆类型

        private LocalDateTime createTime; //创建时间
        private Long voucherAmount; //代金券金额
        private double voucherAmountDouble; //代金券金额
        private Integer rechargeState;//状态：1未充值/待发起、2待付款(平台服务商卡)、3已付款(平台服务商卡)、4服务商已充值(平台服务商卡)、5已充值
        private Integer lineState;//是否走平台线上充值：0否，1是(平台服务商卡)
        private Integer oilType;// 油类型
        private String carDriverMan;//司机姓名
        private String carDriverPhone;//司机手机号
        private String customName;//客户名称
        private String sourceName;//线路名称
        private String createDate; //创建时间
        private Long rechargeAmount; //充值金额
        private String amountToBeWrittenOff; //注销金额
        private String oilCardStatus; //油卡状态
        private String carOwnerName; //车辆拥有者名称
        private String startAndEndSource; //开始和结束线路
        private String arriveSource; //目的地
        private String carLengh; //车长
        private String carType; //车类型

        public Long getNoVerificateEntityFee() {
            if (noVerificateEntityFee == null) {
                setNoVerificateEntityFee(0l);
            }
            return noVerificateEntityFee;
        }

        public void setNoVerificateEntityFee(Long noVerificateEntityFee) {
            this.noVerificateEntityFee = noVerificateEntityFee;
        }

//        public double getNoVerificateEntityFeeDouble() {
//            return CommonUtil.getDoubleFormatLongMoney(getNoVerificateEntityFee(), 2);
//        }
        public Long getCostEntityOil() {
            if (costEntityOil == null) {
                setCostEntityOil(0l);
            }
            return costEntityOil;
        }

        public void setCostEntityOil(Long costEntityOil) {
            this.costEntityOil = costEntityOil;
        }

        public double getCostEntityOilDouble() {
            return CommonUtil.getDoubleFormatLongMoney(getCostEntityOil(), 2);
        }

        public void setCostEntityOilDouble(double costEntityOilDouble) {
            this.costEntityOilDouble = costEntityOilDouble;
        }

        public Long getVoucherAmount() {
            if (voucherAmount == null) {
                setVoucherAmount(0l);
            }
            return voucherAmount;
        }

        public void setVoucherAmount(Long voucherAmount) {
            this.voucherAmount = voucherAmount;
        }

        public double getVoucherAmountDouble() {
            return CommonUtil.getDoubleFormatLongMoney(getVoucherAmount(), 2);
        }

        public void setVoucherAmountDouble(double voucherAmountDouble) {
            this.voucherAmountDouble = voucherAmountDouble;
        }
}
