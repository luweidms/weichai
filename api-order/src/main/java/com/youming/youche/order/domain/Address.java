package com.youming.youche.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* <p>
    * 地址库
    * </p>
* @author liangyan
* @since 2022-03-12
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class Address extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 名称
            */
        @TableField("ADDRESS_NAME")
    private String addressName;

            /**
            * 租户Id
            */
        @TableField("TENANT_ID")
    private Long tenantId;

            /**
            * 省id
            */
        @TableField("PROVINCE_ID")
    private Integer provinceId;

            /**
            * 省
            */
        @TableField("PROVINCE_NAME")
    private String provinceName;

            /**
            * 市id
            */
        @TableField("CITY_ID")
    private Integer cityId;

            /**
            * 市
            */
        @TableField("CITY_NAME")
    private String cityName;

            /**
            * 区id
            */
        @TableField("DISTRICT_ID")
    private Integer districtId;

            /**
            * 区
            */
        @TableField("DISTRICT_NAME")
    private String districtName;

            /**
            * 导航详细地址
            */
        @TableField("ADDRESS_DETAIL")
    private String addressDetail;

            /**
            * 展示地址
            */
        @TableField("ADDRESS_SHOW")
    private String addressShow;

            /**
            * 经度
            */
        @TableField("LNG")
    private String lng;

            /**
            * 纬度
            */
        @TableField("LAT")
    private String lat;
    /**
     * 客户编号
     */
    private Long customerId;


}
