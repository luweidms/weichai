package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 客户线路经停点表
    * </p>
* @author wuhao
* @since 2022-03-30
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmCustomerLineSubway extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 线路ID
            */
    private Long lineId;

            /**
            * 客户编号
            */
    private Long customerId;

            /**
            * 目的省份ID
            */
    private Integer desProvince;

            /**
            * 目的市编码ID
            */
    private Integer desCity;

            /**
            * 目的县区ID
            */
    private Integer desCounty;

            /**
            * 目的地经度
            */
    private String desEnd;

            /**
            * 目的地纬度
            */
    private String desNand;

            /**
            * 目的地详细地址
            */
    private String desAddress;

            /**
            * 经停顺序
            */
    private Integer seq;

            /**
            * 文字描述目的地址，不填写默认使用百度定位地址
            */
    private String navigatDesLocation;

            /**
            * 到达时限（小时）
            */
    private Float arriveTime;

            /**
            * 公里数
            */
    private Integer mileageNumber;

    /**
     * 目的导航经度
     */
    private String desEand;


}
