package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 维修保养记录表
    * </p>
* @author liangyan
* @since 2022-03-24
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class UserRepairMargin extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 用户id
            */
    private Long userId;

            /**
            * 手机号码
            */
    private String userBill;

            /**
            * 用户名
            */
    private String userName;

            /**
            * 费用类型 1：司机自付  2：公司自付
            */
    private Integer costType;

            /**
            * user_repair_info表主键id
            */
    private Long repairId;

            /**
            * 单号（订单、消费单等）
            */
    private String orderId;

            /**
            * 消费金额(分)
            */
    private Long amount;

            /**
            * 平台服务费金额(分)
            */
    private Long platformAmount;

            /**
            * 平台服务费比例(10.2% 则记成10.2)
            */
    private String platformRate;

            /**
            * 对方用户id
            */
    private Long otherUserId;

            /**
            * 对方手机号码
            */
    private String otherUserBill;

            /**
            * 对方用户名
            */
    private String otherName;

            /**
            * 资金渠道类型
            */
    private String vehicleAffiliation;

            /**
            * 资金来源租户id
            */
    private Long tenantId;

            /**
            * 产品id
            */
    private Long productId;

            /**
            * 0未到期，1已到期，2未到期转已到期失败
            */
    private Integer state;

            /**
            * 到期类型：0自动到期；1手动到期
            */
    private Integer expireType;

            /**
            * 服务商已到期金额
            */
    private Long expiredAmount;

            /**
            * 服务商未到期金额
            */
    private Long undueAmount;

            /**
            * 预支未到期手续费
            */
    private Long serviceCharge;

            /**
            * 帐期，用于未到期转可用
            */
    private LocalDateTime getDate;

    private String getResult;

            /**
            * 加油是否需要开票 0无需 1需要
            */
    private Integer isNeedBill;

            /**
            * 产品名称
            */
    private String productName;

            /**
            * 服务电话
            */
    private String serviceCall;

            /**
            * 地址
            */
    private String address;

            /**
            * 使用维修基金金额(分)
            */
    private Long repairBalance;

            /**
            * 使用现金金额(分)
            */
    private Long balance;

            /**
            * 使用未到期金额(分)
            */
    private Long marginBalance;

            /**
            * 预支手续费(分)
            */
    private Long advanceFee;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 修改操作员id
            */
    private Long updateOpId;

            /**
            * 备注
            */
    private String remark;

            /**
            * 资金(油)渠道类型
            */
    private String oilAffiliation;

    @TableField(exist=false)
    private Long subjectsId;

    /**
     * 司机姓名
     */
    @TableField(exist = false)
    private String carDriverMan;

    /**
     * 代收人
     */
    @TableField(exist = false)
    private String collectionUserName;

    /**
     * 车辆类型
     */
    @TableField(exist = false)
    private Integer vehicleClass;

}
