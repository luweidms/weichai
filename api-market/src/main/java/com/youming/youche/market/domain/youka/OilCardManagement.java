package com.youming.youche.market.domain.youka;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 油卡管理表
 * </p>
 *
 * @author Terry
 * @since 2022-02-09
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
    /**
     * 油卡状态code_type='oilcard_status'
     */
    @TableField(exist = false)
    private String oilCardStatusName;
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;
    /**
     * 油卡类型
     */
    @TableField(exist = false)
    private String cardTypeName;
    /**
     * 卡类型(1中石油 2中石化)
     */
    @TableField(exist = false)
    private String oilCardTypeName;
    /**
     * 是否需要提醒
     */
    @TableField(exist = false)
    private Boolean isNeedWarn;

}
