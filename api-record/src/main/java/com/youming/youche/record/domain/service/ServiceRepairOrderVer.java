package com.youming.youche.record.domain.service;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 维修保养订单
 */
@Data
@Accessors(chain = true)
public class ServiceRepairOrderVer extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


    private LocalDateTime updateTime;

    /**
     * 主表主键
     */
    @TableField("FLOW_ID")
    @Column(name = "FLOW_ID")
    private Long flowId;

    /**
     * 用户ID
     */
    @TableField("USER_ID")
    @Column(name = "USER_ID")
    private Long userId;

    /**
     * 工单号
     */
    @TableField("ORDER_CODE")
    @Column(name = "ORDER_CODE")
    private String orderCode;

    /**
     * 业务单号
     */
    @TableField("ORDER_SN")
    @Column(name = "ORDER_SN")
    private String orderSn;

    /**
     * 手机号码
     */
    @TableField("CONTRACT_MOBILE")
    @Column(name = "CONTRACT_MOBILE")
    private String contractMobile;

    /**
     * 司机姓名
     */
    @TableField("CONTRACT_NAME")
    @Column(name = "CONTRACT_NAME")
    private String contractName;

    /**
     * 车牌号
     */
    @TableField("CAR_NO")
    @Column(name = "CAR_NO")
    private String carNo;

    /**
     * 进厂公里数
     */
    @TableField("CAR_MILEAGE")
    @Column(name = "CAR_MILEAGE")
    private String carMileage;

    /**
     * 计划开始时间
     */
    @TableField("SCR_START_TIME")
    @Column(name = "SCR_START_TIME")
    private String scrStartTime;

    /**
     * 计划结束时间
     */
    @TableField("SCR_END_TIME")
    @Column(name = "SCR_END_TIME")
    private String scrEndTime;

    /**
     * 进厂时间
     */
    @TableField("IN_FACTORY_TIME")
    @Column(name = "IN_FACTORY_TIME")
    private String inFactoryTime;

    /**
     * 出厂时间
     */
    @TableField("OUT_FACTORY_TIME")
    @Column(name = "OUT_FACTORY_TIME")
    private String outFactoryTime;

    /**
     * 维保开始时间
     */
    @TableField("REPAIR_START_TIME")
    @Column(name = "REPAIR_START_TIME")
    private String repairStartTime;

    /**
     * 维保结束时间
     */
    @TableField("REPAIR_END_TIME")
    @Column(name = "REPAIR_END_TIME")
    private String repairEndTime;

    /**
     * 工单类型，’GHCWX’-维修，’GHCBY’-保养
     */
    @TableField("WORK_TYPE")
    @Column(name = "WORK_TYPE")
    private String workType;

    /**
     * 维修站点编码
     */
    @TableField("SHOP_ID")
    @Column(name = "SHOP_ID")
    private Long shopId;

    /**
     * 维修站点名称
     */
    @TableField("SHOP_NAME")
    @Column(name = "SHOP_NAME")
    private String shopName;

    /**
     * 消费总金额(分)
     */
    @TableField("TOTAL_AMOUNT")
    @Column(name = "TOTAL_AMOUNT")
    private Long totalAmount;

    /**
     * 工单状态 YJD已接单 DSH待审核 DSG待施工 SGZ施工中 SGWC施工完成 YFQ已废弃 YWC已完成 YQX已取消
     */
    @TableField("WORK_STATUS")
    @Column(name = "WORK_STATUS")
    private String workStatus;

    /**
     * 费用类型 1：司机自付 2：公司自付
     */
    @TableField("COST_TYPE")
    @Column(name = "COST_TYPE")
    private Integer costType;

    /**
     * 站点id
     */
    @TableField("PRODUCT_ID")
    @Column(name = "PRODUCT_ID")
    private Long productId;

    /**
     * 订单状态 1申请待审核 2申请不通过 3待接单 4开单待确认 5开单待审核 6开单不通过 7维保中 8完成待确认 9完成待审核 10完成不通过 11待支付 12已完成
     */
    @TableField("ORDER_STATUS")
    @Column(name = "ORDER_STATUS")
    private Integer orderStatus;

    /**
     * 创建时间
     */
    @TableField("CREATE_DATE")
    @Column(name = "CREATE_DATE")
    private String createDate;

    /**
     * 修改时间
     */
    @TableField("UPDATE_DATE")
    @Column(name = "UPDATE_DATE")
    private String updateDate;

    /**
     * 操作员id
     */
    @TableField("OP_ID")
    @Column(name = "OP_ID")
    private Long opId;

    /**
     * 修改操作员id
     */
    @TableField("UPDATE_OP_ID")
    @Column(name = "UPDATE_OP_ID")
    private Long updateOpId;

    /**
     * 备注
     */
    @TableField("REMARK")
    @Column(name = "REMARK")
    private String remark;

    /**
     * 审核备注
     */
    @TableField("AUDIT_REMARK")
    @Column(name = "AUDIT_REMARK")
    private String auditRemark;

    /**
     * 车队id
     */
    @TableField("TENANT_ID")
    @Column(name = "TENANT_ID")
    private Long tenantId;

    /**
     * 工单结束时间
     */
    @TableField("ORDER_END_TIME")
    @Column(name = "ORDER_END_TIME")
    private String orderEndTime;

    /**
     * 发起保养计划时的保养项目
     */
    @TableField("ITEMS")
    @Column(name = "ITEMS")
    private String items;

    /**
     * 差异标志
     */
    @TableField("DIFF_FLG")
    @Column(name = "DIFF_FLG")
    private Integer diffFlg;

}
