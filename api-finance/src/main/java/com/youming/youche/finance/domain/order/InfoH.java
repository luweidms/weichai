package com.youming.youche.finance.domain.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 新的订单表
    * </p>
* @author liangyan
* @since 2022-03-15
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    @TableName("order_info_h")
    public class InfoH extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 订单编号
            */
    private Long orderId;

            /**
            * 订单状态0:待确认、1：正在交易、2：已确认、3：在运输、4完成
            */
    private Integer orderState;

            /**
            * 标识数据来源，0、表示平台发展1、表示爬虫2、表示百度3、表示自动生成4、表示外购
            */
    private Integer sourceFlag;

            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 来源租户id
            */
    private Long fromTenantId;

            /**
            * 二级组织
            */
    private Integer orgId;

            /**
            * 渠道类型
            */
    private String channelType;

            /**
            * 上一单订单格式为：车辆上一单,主驾驶上一单,副驾驶上一单,
            */
    private String beforeOrders;

            /**
            * 上一单的到达时间
            */
    private LocalDateTime beforeArriveTime;

            /**
            * 订单类型
            */
    private Integer orderType;

            /**
            * 是否需要开票 0无需 1需要
            */
    private Integer isNeedBill;

            /**
            * 修改订单状态 0 修改 1 审核
            */
    private Integer orderUpdateState;

            /**
            * 操作员ID
            */
    private Long opId;

            /**
            * 客户单号
            */
    private String customerId;

            /**
            * 始发市
            */
    private Integer sourceRegion;

            /**
            * 到达市
            */
    private Integer desRegion;

            /**
            * 始发省
            */
    private Integer sourceProvine;

            /**
            * 到达省
            */
    private Integer desProvine;

            /**
            * 始发县
            */
    private Integer sourceCounty;

            /**
            * 到达县
            */
    private Integer desCounty;

            /**
            * 修改数据的操作人id
            */
    private Long updateOpId;

            /**
            * 备注
            */
    private String remark;

            /**
            * 转出的租户id
            */
    private Long toTenantId;

            /**
            *  来源订单编号
            */
    private Long fromOrderId;

            /**
            * 转出订单编号
            */
    private Long toOrderId;

            /**
            * 是否转发：0表示不转发，1表示转发
            */
    private Integer isTransit;

            /**
            * 操作人名称
            */
    private String opName;

            /**
            * 待审核的异常的总数量
            */
    private Integer noAuditExcNum;

            /**
            * 结束时间
            */
    private LocalDateTime endDate;

            /**
            * 转单租户的名称
            */
    private String toTenantName;

            /**
            * 租户名称
            */
    private String tenantName;

            /**
            * 拒单原因
            */
    private String refuseOrderReason;

            /**
            * 收入异常待处理数
            */
    private Integer noAuditExcNumIn;

            /**
            * 操作员组织
            */
    private Long opOrgId;

            /**
            * 记录订单到达操作时间
            */
    private LocalDateTime arriveRecordDate;

    private LocalDateTime createDate;

    private Integer desProvince;

    private Integer sourceProvince;

    private LocalDateTime updateDate;


}
