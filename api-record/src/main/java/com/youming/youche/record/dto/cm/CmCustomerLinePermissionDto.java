package com.youming.youche.record.dto.cm;

import lombok.Data;

import java.util.List;

/**
 * 线路权限
 */
@Data
public class CmCustomerLinePermissionDto {


    /**
     * 所有权限
     */
    private Integer hasAllData;
    /**
     * 成本权限
     */
    private Integer hasCostPermission;
    /**
     * 收入权限
     */
    private Integer hasIncomePermission;
    /**
     * 线路详情
     */
    private List<CmCustomerLineDto> cmCustomerLineDtos;
}
