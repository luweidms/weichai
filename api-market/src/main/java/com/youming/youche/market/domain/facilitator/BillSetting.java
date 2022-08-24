package com.youming.youche.market.domain.facilitator;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车队的开票设置
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BillSetting extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 是否有开票能力 1、有开票能力，2、没有开票能力
     */
    private Integer billAbility;

    /**
     * 平台开票方式
     */
    private Long billMethod;

    /**
     * 费率设置
     */
    private Long rateId;

    /**
     * 创建日期
     */
    private LocalDateTime createDate;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 是否支持费非外调车开平台票据（0、不支持；1、支持）
     */
    private Integer notOtherCarGetPlatformBill;

    /**
     * 账户
     */
    private String lugeAcct;

    /**
     * 密码
     */
    private String lugePassword;

    /**
     * 支付密码
     */
    private String lugePayPassword;

    /**
     * 附加运费
     */
    private Double attachFree;

    /**
     * 费率名称
     */
    @TableField(exist = false)
    private String rateName;


}
