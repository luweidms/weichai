package com.youming.youche.record.domain.service;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 服务商维修项
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceRepairItems extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 创建时间
     */
  //  private String createTime;

    /**
     * 车队id
     */
    private Long tenantId;

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
