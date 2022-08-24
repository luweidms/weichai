package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class RepairInfoDto implements Serializable {
    /**
     * 租户
     */
    private Long tenantId;
    /**
     * 维修单号
     */
    private String repairCode;

    /**
     *维修时间
     */
    private String repairDate;
    /**
     * 租户姓名
     */
    private String tenantName;

    private List<Integer> states;

    /**
     * 站点编号
     */
    private Long productId;
}
