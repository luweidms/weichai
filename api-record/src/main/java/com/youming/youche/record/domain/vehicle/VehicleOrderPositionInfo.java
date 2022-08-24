package com.youming.youche.record.domain.vehicle;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 车辆实时位置记录表
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleOrderPositionInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 车辆编码
     */
    private Long vehicleCode;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 最后运作订单ID
     */
    private Long lastOrderId;

    /**
     * 最后运作订单时间
     */
    private LocalDateTime lastOrderDate;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 省ID
     */
    private Long provinceId;

    /**
     * 市ID
     */
    private Long cityId;

    /**
     * 区ID
     */
    private Long countId;

    /**
     * 是否共享 0否 1是
     */
    private Integer shareFlg;


}
