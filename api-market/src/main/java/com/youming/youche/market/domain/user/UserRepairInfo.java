package com.youming.youche.market.domain.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.market.commons.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 维修记录
 *
 * @author hzx
 * @date 2022/3/12 11:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserRepairInfo extends BaseDomain {

    /**
     * 单号
     */
    private String repairCode;

    /**
     * 服务商ID
     */
    private Long serviceUserId;

    /**
     * 商品名
     */
    private String serviceName;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 服务商名
     */
    private String productName;

    /**
     * 省份ID
     */
    private Integer provinceId;

    /**
     * 市编码ID
     */
    private Integer cityId;

    /**
     * 县区ID
     */
    private Integer countyId;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 地址纬度
     */
    private String nand;

    /**
     * 地址经度
     */
    private String eand;

    /**
     * 维修时间
     */
    private LocalDateTime repairDate;

    /**
     * 交付时间
     */
    private LocalDateTime deliveryDate;

    /**
     * 消费人ID
     */
    private Long userId;

    /**
     * 消费人号码
     */
    private String userBill;

    /**
     * 消费人账户ID
     */
    private Long accId;

    /**
     * 消费人名
     */
    private String userName;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 车辆类别
     */
    private Integer vehicleClass;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 金额
     */
    private Long totalFee;
    @TableField(exist = false)
    private Double totalFeeDouble;

    /**
     * 结算类型 1月结 2账期
     */
    private Integer acctType;

    /**
     * 收入帐期（单位天）
     */
    private Integer accountPeriod;

    /**
     * 到期时间，用于服务商未到期转可用
     */
    private LocalDateTime getDate;

    /**
     * 油老板帐期处理结果
     */
    private String getResult;

    /**
     * 状态：未到期，已到期
     */
    private Integer getType;

    /**
     * 支付维修单 未到期金额
     */
    private Long marginAmount;

    /**
     * 支付维修单 可用金额
     */
    private Long balanceAmount;

    /**
     * 维修单状态
     */
    private Integer repairState;

    /**
     * 质量星级
     */
    private Integer qualityStar;

    /**
     * 价格星级
     */
    private Integer priceStar;

    /**
     * 服务星级
     */
    private Integer serviceStar;

    /**
     * 司机描述
     */
    private String driverDes;

    /**
     * 车管中心描述
     */
    private String carCenterDes;

    /**
     * 财务部描述
     */
    private String financeDes;

    /**
     * 支付方式：1为公司付 2为自付-账户 3为自付微信 4为自付现金
     */
    private Integer payWay;

    /**
     * 资金渠道
     */
    private String vehicleAffilation;

    /**
     * 商品开票折扣率
     */
    private String oilRateInvoice;

    /**
     * 自付-现金 是否确认收款
     */
    private Integer isConfirmCash;

    /**
     * 司机是否确认  1为确认 0为驳回
     */
    private Integer isSure;

    /**
     * 用户归属的顶级组织
     */
    private Long rootOrgId;

    /**
     * 用户归属的二级组织
     */
    private Long orgId;

    /**
     * 车管中心是否操作,1为已操作（同意或者驳回）0为未操作
     */
    private Integer isCarcentsSure;

    /**
     * 财务部是否操作,1为已操作（同意或者驳回）0为未操作
     */
    private Integer isFinanceSure;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 审核人
     */
    private String auditMan;

    /**
     * 审核人id
     */
    private Long auditManId;

    /**
     * 平台服务费（共享站点的时候才有）
     */
    private String serviceCharge;

    /**
     * App维修状态
     */
    private Integer appRepairState;

    /**
     * 是否开票（1：开，2：不开）
     */
    private Integer isBill;

    /**
     * 是否选择维修基金1：是，2：否
     */
    private Integer isFund;

    @TableField(exist = false)
    private String info; // 返回结果

    private Long state; //状态

    @TableField(exist = false)
    private String vehicleLengthName; // 车长

    @TableField(exist = false)
    private String vehicleStatusName; // 车结构

    public Double getTotalFeeDouble() {
        return CommonUtil.getDoubleFormatLongMoney(getTotalFee(), 2);
    }

    /**
     * 状态
     */
    @TableField(exist = false)
    private String repairStateStr;

    /**
     * 司机确认
     */
    @TableField(exist = false)
    private String isSureName;

    public String getIsSureName() {
        if (isSure != null && isSure > 0) {
            if (isSure == 1) {
                return "确认";
            } else {
                return "驳回";
            }
        }
        return isSureName;
    }

}
