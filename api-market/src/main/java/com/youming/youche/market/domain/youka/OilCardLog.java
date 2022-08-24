package com.youming.youche.market.domain.youka;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author XXX
 * @since 2022-03-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OilCardLog extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 车辆司机名称
     */
    private String carDriverMan;
    /**
     * 司机类型
     */
    private Integer carUserType;
    /**
     * 油卡id
     */
    private Long cardId;
    /**
     * 创建时间
     */
    private LocalDateTime logDate;
    /**
     * 描述
     */
    private String logDesc;
    /**
     * 使用状态
     */
    private Integer logSts;
    /**
     * 使用类型
     */
    private Integer logType;
    /**
     * 油卡金额
     */
    private Long oilFee;
    /**
     * 油卡费用
     */
    private Long oilFeeMerchant;
    /**
     * 订单ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long orderId;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 组织
     */
    private Long rootOrgId;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 用户编号
     */
    private Long userId;


}
