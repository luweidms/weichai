package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderInfoVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 记录订单到达操作时间
     */
    private LocalDateTime arriveRecordDate;
    /**
     * 上一单的到达时间
     */
    private LocalDateTime beforeArriveTime;
    /**
     * 上一单订单格式为：车辆上一单,主驾驶上一单,副驾驶上一单,
     */
    private String beforeOrders;
    /**
     *渠道标识
     */
    private String channelType;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 客户单号
     */
    private Long customerId;
    /**
     * 到达县
     */
    private Integer desCounty;
    /**
     * 目的省份ID
     */
    private Integer desProvince;
    /**
     * 到达市
     */
    private Integer desRegion;
    /**
     * 来源订单编号
     */
    private Long fromOrderId;
    /**
     * 来源租户id
     */
    private Long fromTenantId;
    /**
     * 是否开票
     */
    private Integer isNeedBill;
    /**
     * 是否有经停城市
     */
    private Integer isTransit;
    /**
     * 待审核的异常的总数量
     */
    private Integer noAuditExcNum;
    /**
     * 收入异常待处理数
     */
    private Integer noAuditExcNumIn;
    /**
     * 操作人
     */
    private Long opId;
    /**
     *操作员
     */
    private String opName;
    /**
     * 操作员组织
     */
    private Long opOrgId;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 订单状态
     */
    private Integer orderState;
    /**
     * 订单类型
     */
    private Integer orderType;
    /**
     * 修改订单状态 0 修改 1 审核
     */
    private Integer orderUpdateState;
    /**
     * 项目部
     */
    private Integer orgId;
    /**
     * 拒单原因
     */
    private String refuseOrderReason;
    /**
     * 备注
     */
    private String remark;
    /**
     * 始发县
     */
    private Integer sourceCounty;
    /**
     * 标识数据来源，0、表示平台发展1、表示爬虫2、表示百度3、表示自动生成4、表示外购
     */
    private Integer sourceFlag;
    /**
     * 始发省
     */
    private Integer sourceProvince;
    /**
     * 起始点
     */
    private Integer sourceRegion;
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 车队名称
     */
    private String tenantName;
    /**
     * 接单订单
     */
    private Long toOrderId;
    /**
     * 接单车队ID
     */
    private Long toTenantId;
    /**
     * 转单租户的名称
     */
    private String toTenantName;
    /**
     * 修改数据的操作人id
     */
    private Long updateOpId;

    /**
     * 是否转发：0表示没有转发，1表示转发
     */
    @TableField(exist = false)
    private Integer isTansit;

    @TableField(exist=false)
    private Long workId;//工单ID
    /**
     * 归属部门
     */
    @TableField(exist = false)
    private String orgName;
    @TableField(exist = false)
    private String orgIdName;
    /**
     * 始发省名称
     */
    @TableField(exist=false)
    private String sourceProvinceName;
    /**
     * 始发市
     */
    @TableField(exist=false)
    private String sourceRegionName;
    /**
     * 始发县名称
     */
    @TableField(exist=false)
    private String sourceCountyName;
    /**
     * 到达省名称
     */
    @TableField(exist=false)
    private String desProvinceName;
    /**
     * 目的地
     */
    @TableField(exist=false)
    private String desRegionName;
    /**
     * 到达县名称
     */
    @TableField(exist=false)
    private String desCountyName;





}
