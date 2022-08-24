package com.youming.youche.finance.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 对账单明细
 *
 * @author zengwen
 * @date 2022/4/14 12:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AccountStatementDetails extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 对账单编号
     */
    private Long accountStatementId;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车辆编码
     */
    private Long vehicleCode;

    /**
     * 司机Id
     */
    private Long carDriverId;

    /**
     * 司机姓名
     */
    private String carDriverName;

    /**
     * 司机手机
     */
    private String carDriverPhone;

    /**
     * 接收人Id
     */
    private Long answerId;

    /**
     * 接收人姓名
     */
    private String answerName;

    /**
     * 接收人手机
     */
    private String answerPhone;

    /**
     * 总费用
     */
    private Long totalFee;

    /**
     * 管理费
     */
    private Long manageFee;

    /**
     * 车辆租赁费用
     */
    private Long vehicleRentalFee;

    /**
     * 贷款利息
     */
    private Long loanInterestFee;

    /**
     * 加油费用
     */
    private Long oilFee;

    /**
     * 维修费用
     */
    private Long repairFee;

    /**
     * 车船税
     */
    private Long travelTaxFee;

    /**
     * 轮胎
     */
    private Long tireFee;

    /**
     * 挂车验审
     */
    private Long trailerInspectionFee;

    /**
     * 车辆验审
     */
    private Long vehicleInspectionFee;

    /**
     * 挂车货柜保险费
     */
    private Long trailerCabinetsInsuranceFee;

    /**
     * 违章罚款
     */
    private Long violationFee;

    /**
     * 挂车租赁费
     */
    private Long trailerRentalFee;

    /**
     * 交强险
     */
    private Long compulsoryInsuranceFee;

    /**
     * 商业险
     */
    private Long commercialInsuranceFee;

    /**
     * 挂车车船税
     */
    private Long trailerVehicleTaxFee;

    /**
     * 挂车轮胎磨损
     */
    private Long trailerTireWearFee;

    /**
     * 其它配件
     */
    private Long otherAccessoriesFee;

    /**
     * 行程补偿
     */
    private Long strokeCompensationFee;

    /**
     * 其他费用
     */
    private Long otherFee;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态 1有效 0无效
     */
    private Integer state;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 操作员ID
     */
    private Long opId;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 修改操作员ID
     */
    private Long updateOpId;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 车辆类型名称
     */
    private String vehicleClassName;

    /**
     * 路桥费
     */
    private Long etcFee;

    /**
     * 数据来源 1ETC消费记录 2自由设置
     */
    private Integer countType;


}
