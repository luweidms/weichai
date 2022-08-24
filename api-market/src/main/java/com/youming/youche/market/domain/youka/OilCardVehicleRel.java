package com.youming.youche.market.domain.youka;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 油卡-车辆关系表
 * </p>
 *
 * @author Terry
 * @since 2022-02-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OilCardVehicleRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 油卡id
     */
    private Long id;

    /**
     * 油卡号
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
