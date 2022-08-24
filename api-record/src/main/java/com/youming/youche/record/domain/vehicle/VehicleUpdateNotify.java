package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 车辆信息变更通知
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@Table(name = "vehicle_upate_notify")
@Entity
@Accessors(chain = true)
public class VehicleUpdateNotify implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id // 主键id
    @Column()
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    @TableId(value = "NOTIFY_ID", type = IdType.AUTO)
    private Long notifyId;

    /**
     * 接收通知的租户ID
     */
    @TableField("TENANT_ID")
    @Column(name = "TENANT_ID")
    private Long tenantId;

    /**
     * 车辆主键
     */
    @TableField("VEHICLE_CODE")
    @Column(name = "VEHICLE_CODE")
    private Long vehicleCode;

    /**
     * 车牌号码
     */
    @TableField("PLATE_NUMBER")
    @Column(name = "PLATE_NUMBER")
    private String plateNumber;

    /**
     * 车辆类型
     */
    @TableField("VEHICLE_CLASS")
    @Column(name = "VEHICLE_CLASS")
    private Integer vehicleClass;

    /**
     * 原自有车队
     */
    @TableField("SOURCE_TENANT_ID")
    @Column(name = "SOURCE_TENANT_ID")
    private Long sourceTenantId;

    /**
     * 原车队名称
     */
    @TableField("SOURCE_TENANT_NAME")
    @Column(name = "SOURCE_TENANT_NAME")
    private String sourceTenantName;

    /**
     * 是否已读
     */
    @TableField("already_read")
    @Column(name = "already_read")
    private Boolean alreadyRead;

    /**
     * 创建时间
     */
    @TableField("CREATE_DATE")
    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

}
