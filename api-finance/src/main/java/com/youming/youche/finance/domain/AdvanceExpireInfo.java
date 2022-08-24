package com.youming.youche.finance.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 手动到期沉淀表
    * </p>
* @author luona
* @since 2022-04-13
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class AdvanceExpireInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 业务流水号（尾款-订单id）
            */
    private Long fromFlowId;

            /**
            * 收款人
            */
    private String userName;

            /**
            * 手机号码
            */
    private String billId;

            /**
            * 主驾司机
            */
    private String driverName;

            /**
            * 到期款类型 1尾款 2油 3维修款
            */
    private String signType;

            /**
            * 到期款 单位分
            */
    private Long marginBalance;

            /**
            * 计划到期时间
            */
    private LocalDateTime planExpireDate;

            /**
            * 到期状态 0未到期 1已到期
            */
    private Integer state;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 操作员
            */
    private String opName;

            /**
            * 渠道类型
            */
    private String channelType;

            /**
            * 修改操作员id
            */
    private Long updateOpId;

            /**
            * 租户
            */
    private Long tenantId;



}
