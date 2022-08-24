package com.youming.youche.market.domain.facilitator;


import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 全国油价表
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OilPriceProvince extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 省份id
     */
    @TableField("PROVINCE_ID")
    private Integer provinceId;

    /**
     * 油价
     */
    @TableField("OIL_PRICE")
    @ExcelProperty(index= 1)
    private Long oilPrice;

    /**
     * 省份名
     */
    @TableField("PROVINCE_NAME")
    @ExcelProperty(index = 0)
    private String provinceName;

    /**
     * 租户id
     */
    @TableField("TENANT_ID")
    private Long tenantId;


}
