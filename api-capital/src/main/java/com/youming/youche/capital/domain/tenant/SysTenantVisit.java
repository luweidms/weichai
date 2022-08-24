package com.youming.youche.capital.domain.tenant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 访客档案
 * </p>
 *
 * @author Terry
 * @since 2022-01-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysTenantVisit extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 租户CODE
     */
    private String tenantCode;

    /**
     * 租户名称(车队全称)
     */
    private String companyName;

    /**
     * 车队简称
     */
    private String shortName;

    /**
     * 省id
     */
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市id
     */
    private Integer cityId;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 县/区id
     */
    private Integer districtId;

    /**
     * 县/区名称
     */
    private String districtName;

    /**
     * 常用办公地址
     */
    private String address;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 售前跟踪ID
     */
    private Long preSaleServiceId;

    /**
     * 售前跟踪员工姓名
     */
    private String preSaleServiceName;

    /**
     * 入场日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime entranceDate;

    /**
     * 售前责任组
     */
    private Long preSaleOrgId;

    /**
     * 交付周期（天）
     */
    private Integer deliveryCycle;

    /**
     * 签约状态（0、未签约；1、已签约）
     */
    private Integer signState;

    /**
     * 归属部门
     */
    private Long orgId;


}
