package com.youming.youche.market.vo.facilitator;

import com.youming.youche.market.dto.facilitator.SysTenantDto;
import lombok.Data;

@Data
public class SysTenantOutVo extends SysTenantDto {
    /**
     * 小车队状态
     * @see
     */
    private Integer virtualState;
    private String plateName;//当前开票平台名称，页面报错需要

    private String preSaleServicePhone;

    private String afterSaleServicePhone;
}
