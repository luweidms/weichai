package com.youming.youche.market.domain.facilitator;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-25
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class LineOilDepotScheme extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 详细地址
            */
    private String address;

            /**
            * 沿途距离
            */
    private Long alongDistance;

            /**
            * 服务商ID
            */
    private Long businessUserId;

            /**
            * 服务商名称
            */
    private String businessUserName;

            /**
            * 距离靠台地
            */
    private Long dependDistance;

            /**
            * 油站纬度
            */
    private String eand;

            /**
            * 是否有开票能力
            */
    private Integer isBillAbility;

            /**
            * 线路ID
            */
    private Long lineId;

            /**
            * 油站经度
            */
    private String nand;

            /**
            * 油站主键ID
            */
    private Long oilId;

            /**
            * 油站名称
            */
    private String oilName;

            /**
            * 油费单价
            */
    private Long oilPrice;

            /**
            * 开票油价
            */
    private Long oilPriceBill;

            /**
            * 油站折扣率
            */
    private String oilRate;

            /**
            * 油站开票折扣率
            */
    private String oilRateInvoice;

            /**
            * 省份ID
            */
    private Integer provinceId;

            /**
            * 油站电话
            */
    private String serviceCall;

            /**
            * 租户ID
            */
    private Long tenantId;


}
