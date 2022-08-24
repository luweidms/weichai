package com.youming.youche.record.domain.vehicle;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *  车辆心愿线路关系表
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleLineRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


//    /**
//     * 关系ID
//     */
//  //  @TableId(value = "rel_id", type = IdType.AUTO)
//    private Long relId;

    /**
     * 车辆ID
     */
    private Long vehicleCode;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 线路ID
     */
    private Long lineId;

    /**
     * 线路类型
     */
    private Integer state;

    /**
     * 线路编号
     */
    private String lineCodeRule;

    /**
     * 操作人员
     */
    private Long opId;

    /**
     * 操作时间
     */
    private LocalDateTime opDate;


}
