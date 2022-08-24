package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 闲置车辆表
 * </p>
 *
 * @author wuhao
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleIdle extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 纬度
     */
    private String lat;

    /**
     * 经度
     */
    private String lng;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车辆id
     */
    private Long vid;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 是否挂车 0否 1、是
     */
    private Integer flag;

    /**
     * 创建时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDateTime updateTime;




}
