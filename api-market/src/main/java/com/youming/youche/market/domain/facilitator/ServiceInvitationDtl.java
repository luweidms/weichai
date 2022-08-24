package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 服务商申请合作明细表
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceInvitationDtl extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 邀请主键ID
     */
    private Long inviteId;

    /**
     * 站点ID
     */
    private Long productId;

    /**
     * 浮动价结算
     */
    private String floatBalance;

    /**
     * 固定价结算
     */
    private Long fixedBalance;

    /**
     * 手续费
     */
    private String serviceCharge;

    /**
     * 浮动价结算(开票)
     */
    private String floatBalanceBill;

    /**
     * `固定价结算`(开票)
     */
    private Long fixedBalanceBill;

    /**
     * 手续费(开票)
     */
    private String serviceChargeBill;

    /**
     * 车队认证状态
     */
    private Integer tenantAuthState;

    /**
     * 现场价 1是 0 不是
     */
    private Integer localeBalanceState;


}
