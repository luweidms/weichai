package com.youming.youche.record.domain.service;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 维修保养订单
 *
 * @date 2022/1/8 11:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceRepairOrder extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */

    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 工单号(内)
     */
    private String orderCode;

    /**
     * 业务单号(外)
     */
    private String orderSn;

    /**
     * 手机号码
     */
    private String contractMobile;

    /**
     * 司机姓名
     */
    private String contractName;

    /**
     * 车牌号
     */
    private String carNo;

    /**
     * 进厂公里数
     */
    private String carMileage;

    /**
     * 计划开始时间
     */
    private String scrStartTime;

    /**
     * 计划结束时间
     */
    private String scrEndTime;

    /**
     * 进厂时间
     */
    private String inFactoryTime;

    /**
     * 维保结束时间
     */
    private String outFactoryTime;

    /**
     * 维保开始时间
     */
    private String repairStartTime;

    /**
     * 支付时间
     */
    private String repairEndTime;

    /**
     * 工单类型，’GHCWX’-维修，’GHCBY’-保养
     */
    private String workType;

    /**
     * 工单类型
     */
    @TableField(exist = false)
    private String workTypeName;

    /**
     * 维修站点编码
     */
    private Long shopId;

    /**
     * 维修站点名称
     */
    private String shopName;

    /**
     * 消费总金额(分)
     */
    private Long totalAmount;

    /**
     * 工单状态 YJD已接单 DSH待审核 DSG待施工 SGZ施工中 SGWC施工完成 YFQ已废弃 YWC已完成 YQX已取消
     */
    private String workStatus;

    /**
     * 费用类型 1：司机自付 2：公司自付
     */
    private Integer costType;

    /**
     * 站点id
     */
    private Long productId;

    /**
     * 订单状态 1申请待审核 2申请不通过 3待接单 4开单待确认 5开单待审核 6开单不通过 7维保中 8完成待确认 9完成待审核 10完成不通过 11待支付 12已完成
     */
    private Integer orderStatus;

    /**
     * 订单类型
     */
    @TableField(exist = false)
    private String orderStatusName;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 修改时间
     */
    private String updateDate;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 用户名称
     */
    @TableField(exist = false)
    private String opName;

    /**
     * 用户手机号
     */
    @TableField(exist = false)
    private String opPhone;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 工单结束时间
     */
    private String orderEndTime;

    /**
     * 推送状态
     */
    private Integer pushState;

    /**
     * 发起保养计划时的保养项目
     */
    private String items;

    /**
     * 推送失败次数
     */
    private Integer pushFailCount;

    /**
     * 最后一次推送失败时间
     */
    private String pushFailTime;

    /**
     * 最后一次推送失败原因
     */
    private String pushFailRemark;

    /**
     * 车辆品牌
     */
    private String brandModel;

    /**
     * 车辆型号
     */
    @TableField(exist = false)
    private String vehicleModel;

    /**
     * 上单里程数
     */
    private String lastOrderMileage;

    /**
     * 品牌单价
     */
    private Double brandPrice;

    /**
     * 核算金额
     */
    private Double checkAmount;

    /**
     * 项目数量
     */
    @TableField(exist = false)
    private String itemCount;

    /**
     * 服务商名称
     */
    @TableField(exist = false)
    private String serviceName;

    /**
     * 车队名称
     */
    @TableField(exist = false)
    private String tenantName;

    /**
     * 支付类型
     */
    @TableField(exist = false)
    private String isAutomatic;
}
