package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车辆信息变更通知
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleUpateNotify extends BaseDomain {

    private static final long serialVersionUID = 1L;


   // @TableId(value = "notify_id", type = IdType.AUTO)
    private Long notifyId;

    /**
     * 接收通知的租户ID
     */
    private Long tenantId;

    /**
     * 车辆主键
     */
    private Long vehicleCode;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 原自有车队
     */
    private Long sourceTenantId;

    /**
     * 原车队名称
     */
    private String sourceTenantName;

    /**
     * 是否已读
     */
    private Boolean alreadyRead;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;


}
