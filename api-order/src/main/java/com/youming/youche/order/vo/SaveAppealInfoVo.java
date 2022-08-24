package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/17
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class SaveAppealInfoVo implements Serializable {
    /**
     * 备注
     */
    private String remark;
    private String imgIds;
    private String imgUrls;
    /**
     * 时效罚款id
     */
    private Long agingId;
    private Boolean isCustomerAging;

}
