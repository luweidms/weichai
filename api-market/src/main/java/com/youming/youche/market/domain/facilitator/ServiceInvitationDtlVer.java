package com.youming.youche.market.domain.facilitator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 服务商申请合作明细版本
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@Data
@Accessors(chain = true)
public class ServiceInvitationDtlVer implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * 历史主键
     */
    private Long hisId;

    private Long id;
    /**
     * 邀请主键ID
     */
    private Long inviteId;

    /**
     * 站点ID
     */
    private Long productId;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 创建时间
     */
    private String createTime;


    /**
     * 修改操作人
     */
    private Long updateOpId;

    /**
     * 是否可用
     */
    private Integer isUse;

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
     * 固定价结算(开票)
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
