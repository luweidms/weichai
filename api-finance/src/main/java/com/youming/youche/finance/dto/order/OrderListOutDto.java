package com.youming.youche.finance.dto.order;

import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.record.domain.user.UserSalaryInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/22 9:11
 */
@Data
public class OrderListOutDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 司机ID
     */
    private Long carDriverId;

    /**
     * 司机姓名
     */
    private String carDriverName;

    /**
     * 司机手机号码
     */
    private String carDriverPhone;

    /**
     * 副驾驶ID
     */
    private Long copilotUserId;

    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;

    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 订单状态
     */
    private Long orderState;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 公里数
     */
    private Long mieageNumber;

    /**
     * 公里数
     */
    private Double mieageNumberStr;

    /**
     * 付款方式 1:智能模式 2:报账模式 3:承包模式
     */
    private Integer paymentWay;

    /**
     *
     */
    private String orderLine;

    /**
     * 公里数
     */
    private String formerMileage;

    /**
     * 本躺工资
     */
    private Long tripSalaryFee;

    /**
     * 本躺工资
     */
    private Double tripSalaryFeeDouble;

    /**
     * 节油奖
     */
    private Long saveOilBonus;

    /**
     * 节油奖
     */
    private Double saveOilBonusDouble;

    /**
     * 订单状态
     */
    private String c;

    /**
     * [tenant_user_salary_rel]
     *
     * 薪资模式：1里程模式 2按趟模式
     *         1：普通 2：里程  3：按趟
     */
    private Integer salaryPattern;

    /**
     * 薪资模式名称
     */
    private String salaryPatternName;

    /**
     * 补贴天数  [order_fee_ext]
     */
    private Long subsidyDay;

    /**
     * 补贴金额总
     */
    private Long subsidyFeeSum;

    /**
     * 补贴金额总
     */
    private Double subsidyFeeSumDouble;

    /**
     * 补贴
     */
    private Long subsidyFee;

    /**
     * 补贴  copilotSalary(副驾驶补贴)+  salary(主驾驶补贴)  driverSwitchSubsidy(切换驾驶补贴) [order_fee_ext]
     */
    private Double subsidyFeeDouble;

    /**
     * 里程模式里程信息
     */
    private List<UserSalaryInfo> userSalaryInfoList;

    /**
     * 补贴状态
     */
    private Integer subsidyFeeSumState;

    private Integer lyingNumber = 1;

    private String subsidyFeeSumStateName;

    private LocalDateTime orderCompleteTime;

    /**
     * 已结算金额
     */
    private Long settleMoney;

    private Double settleMoneyDouble;

    public Long getSubsidyDay() {
        return this.subsidyDay != null ? this.subsidyDay : 0L;
    }

    public Long getSubsidyFee() {
        return subsidyFee;
    }

    public Double getSubsidyFeeDouble() {
        setSubsidyFeeDouble(CommonUtil.getDoubleFormatLongMoney(subsidyFee, 2));
        return subsidyFeeDouble;
    }

    public Double getSubsidyFeeSumDouble() {
        setSubsidyFeeSumDouble(CommonUtil.getDoubleFormatLongMoney(subsidyFeeSum, 2));
        return subsidyFeeSumDouble;
    }

    public Double getSettleMoneyDouble() {
        setSettleMoneyDouble(CommonUtil.getDoubleFormatLongMoney(settleMoney, 2));
        return settleMoneyDouble;
    }

    public String getSubsidyFeeSumStateName() {
        if (this.subsidyFeeSumState == null || this.subsidyFeeSumState == 0) {
            return "待发送";
        }
        if (this.subsidyFeeSumState == 2) {
            return "待确认";
        }
        if (this.subsidyFeeSumState == 3) {
            return "已确认";
        }
        if (this.subsidyFeeSumState == 4) {
            return "部分结算";
        }
        if (this.subsidyFeeSumState == 5) {
            return "已结算";
        }
        return "";
    }


}
