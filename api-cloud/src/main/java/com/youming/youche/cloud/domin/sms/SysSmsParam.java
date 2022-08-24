package com.youming.youche.cloud.domin.sms;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2022-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysSmsParam extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 参数code
     */
    private String paramCode;
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数值表达式
     */
    private String paramValueExpr;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 模板Id
     */
    private Long templateId;
    /**
     * 车队Id
     */
    private Long tenantId;


}
