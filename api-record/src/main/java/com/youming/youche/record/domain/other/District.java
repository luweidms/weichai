package com.youming.youche.record.domain.other;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 县、区表
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class District extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 市ID
     */
    private Integer cityId;

    /**
     * 名称
     */
    private String name;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * dist类型
     */
    private String distType;

    /**
     * 租户ID
     */
    private Long tenantId;


}
