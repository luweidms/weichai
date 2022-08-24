package com.youming.youche.record.domain.other;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 城市表
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class City extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 省ID
     */
    private Integer provId;

    /**
     * 名称
     */
    private String name;

    /**
     * 市Code
     */
    private String areaCode;

    /**
     * 市类型
     */
    private String cityType;


}
