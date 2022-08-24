package com.youming.youche.system.domain.tenant;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 平台服务费到期记录表
 * </p>
 *
 * @author Terry
 * @since 2022-01-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceChargeReminder extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 待缴车队长Id
     */
    private Long userId;

    /**
     * 服务费金额
     */
    private Long amount;

    /**
     * 到期时间
     */
    private LocalDateTime expireDate;

    /**
     * 到期缴费状态 0失效 1未交费 2缴费(打款中)3支付成功
     */
    private Integer state;

    /**
     * 操作员
     */
    private Long opId;

    /**
     * 操作员名称
     */
    private String opName;

    /**
     * 渠道
     */
    private String channelType;

    /**
     * 修改操作员ID
     */
    private Long updateOpId;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 业务编号
     */
    private String reminderId;


}
