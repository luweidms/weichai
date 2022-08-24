package com.youming.youche.record.domain.other;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 省份表
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Province extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 名称
     */
    private String name;

    /**
     * 排序Id
     */
    private Integer sortId;

    /**
     * 省编码
     */
    private String provType;

    /**
     * 租户Id
     */
    private Long tenantId;


}
