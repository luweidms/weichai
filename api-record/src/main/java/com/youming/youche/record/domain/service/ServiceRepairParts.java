package com.youming.youche.record.domain.service;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 维修零配件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServiceRepairParts extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配件id
     */
    private Long partsId;

    /**
     * 项目id
     */
    private Long orderItemId;

    /**
     * 维修保养订单id
     */
    private Long repairOrderId;

    /**
     * 配件名称
     */
    private String partsName;

    /**
     * 配件数量
     */
    private Double partsNumber;

    /**
     * 配件单价
     */
    private Long partsPrice;

    /**
     * 配件总价
     */
    private Long totalPartsPrice;

    /**
     * 车队id
     */
    private Long tenantId;

}
