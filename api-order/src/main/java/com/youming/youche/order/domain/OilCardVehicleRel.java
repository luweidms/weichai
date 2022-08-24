package com.youming.youche.order.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 油卡-车辆关系表
    * </p>
* @author liangyan
* @since 2022-03-07
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilCardVehicleRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 油卡id
            */
    private Long cardId;

            /**
            * 油卡号
            */
    private String oilCardNum;

            /**
            * 车牌号
            */
    private String vehicleNumber;

            /**
            * 租户id
            */
    private Long tenantId;


}
