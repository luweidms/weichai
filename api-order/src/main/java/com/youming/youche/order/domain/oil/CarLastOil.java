package com.youming.youche.order.domain.oil;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车辆最后油卡号表
 * </p>
 *
 * @author hzx
 * @since 2022-03-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CarLastOil extends BaseDomain {

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 实体油卡号
     */
    private String oilCarNum;

    /**
     * 租户id
     */
    private Long tenantId;


}
