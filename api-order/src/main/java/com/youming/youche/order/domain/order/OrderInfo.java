package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.order.annotation.SysStaticDataInfoDict;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static com.youming.youche.conts.EnumConsts.SysStaticData.ORDER_STATE;
import static com.youming.youche.conts.EnumConsts.SysStaticData.ORDER_TYPE;
import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_CITY;
import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_DISTRICT;
import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_PROVINCE;

/**
 * 应收订单
 *
 * @author hzx
 * @date 2022/2/8 9:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderInfo extends BaseDomain {

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 订单状态0:待确认、1：正在交易、2：已确认、3：在运输、4完成
     */
    @SysStaticDataInfoDict(dictDataSource = ORDER_STATE)
    private Integer orderState;

    /**
     * 标识数据来源，0、表示平台发展,1、表示爬虫,2、表示百度,3、表示自动生成,4、表示外购,7微信,9、新录入页面开单
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

    @TableField(exist = false)
    private String orgName;

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
    @SysStaticDataInfoDict(dictDataSource =ORDER_TYPE )
    private Integer orderType;

    /**
     * 是否需要开票 0无需 1需要
     */
    @SysStaticDataInfoDict(dictDataSource = "IS_NEED_BILL")
    private Integer isNeedBill;

    /**
     * 创建时间
     */
//    private LocalDateTime createDate;

    /**
     * 修改时间
     */
//    private LocalDateTime updateDate;

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
    @SysStaticDataInfoDict(dictDataSource = SYS_CITY)
    private Integer sourceRegion;

    /**
     * 到达市
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_CITY)
    private Integer desRegion;

    /**
     * 始发省
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_PROVINCE)
    private Integer sourceProvince;

    /**
     * 到达省
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_PROVINCE)
    private Integer desProvince;

    /**
     * 始发县
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_DISTRICT)
    private Integer sourceCounty;

    /**
     * 到达县
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_DISTRICT)
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
     * 来源订单编号
     */
    private Long fromOrderId;

    /**
     * 转出订单编号
     */
    private Long toOrderId;

//    /**
//     * 是否转发：0表示没有转发，1表示转发
//     */
//    private Integer isTansit;

    /**
     * 操作人名称
     */
    private String opName;

    /**
     * 待审核的异常的总数量
     */
    private Integer noAuditExcNum;

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


    private Integer isTransit;

    @TableField(exist=false)
    private Long workId;//工单ID

    @TableField(exist=false)
    private String sourceProvinceName;

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

    @TableField(exist=false)
    private String desRegionName;
    /**
     * 到达县名称
     */
    @TableField(exist=false)
    private String desCountyName;
    @TableField(exist = false)
    private String orgIdName;

}
