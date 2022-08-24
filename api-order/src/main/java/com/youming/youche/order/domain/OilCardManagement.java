package com.youming.youche.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 油卡管理表
    * </p>
* @author liangyan
* @since 2022-03-07
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilCardManagement extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 实体油卡卡号
            */
    private String oilCarNum;

            /**
            * 油卡状态code_type='oilcard_status'
            */
    private Integer oilCardStatus;

            /**
            * 服务商用户id
            */
    private Long userId;

            /**
            * 服务商联系人
            */
    private String linkman;

            /**
            * 创建时间
            */
    private LocalDateTime createDate;

            /**
            * 租户
            */
    private Long tenantId;

            /**
            * 当前抵押订单号
            */
    private Long pledgeOrderId;

            /**
            * 当前抵押金额
            */
    private Long pledgeFee;

            /**
            * 油卡类型
            */
    private Integer cardType;

            /**
            * 理论余额
            */
    private Long cardBalance;

            /**
            * 卡类型(1中石油 2中石化)
            */
    private Integer oilCardType;
    @TableField(exist=false)
    private Boolean isNeedWarn;//是否需要提醒

    @TableField(exist=false)
    private String cardTypeName;

}
