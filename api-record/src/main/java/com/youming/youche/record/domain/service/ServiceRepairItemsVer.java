package com.youming.youche.record.domain.service;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 服务商维修想版本
 * @author
 */
@Data
@Accessors(chain = true)
public class ServiceRepairItemsVer implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @TableId(
            value = "hisId",
            type = IdType.AUTO
    )
    private Long hisId;

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @TableField(
            value = "create_time",
            fill = FieldFill.INSERT
    )
    private LocalDateTime createTime;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @TableField(
            value = "update_time",
            fill = FieldFill.INSERT_UPDATE
    )
    private LocalDateTime updateTime;

    /**
     * 主表id
     */
    private Long id;

    /**
     * 项目id
     */
    private Long itemId;

    /**
     * 维修保养订单id
     */
    private Long repairOrderId;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 工时
     */
    private Double itemManHour;

    /**
     * 工时单价
     */
    private Long itemPrice;

    /**
     * 工时总价
     */
    private Long totalItemPrice;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 维修保养主历史表id
     */
    private Long repairHisId;

    /**
     * 配件总价
     */
    @TableField(exist = false)
    private Long itemPartsPirce;

    /**
     * 配件数量
     */
    @TableField(exist = false)
    private String itemPartsCount;

}
