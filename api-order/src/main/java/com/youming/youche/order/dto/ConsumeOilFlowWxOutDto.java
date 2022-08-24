package com.youming.youche.order.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/4/24
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class ConsumeOilFlowWxOutDto implements Serializable {
    /**
     * 加油使用未到期金额(分)
     */
    private long marginBalance;
    private long expireBalance;
    /**
     * 平台服务费金额
     */
    private long platformServiceCharge;
    /**
     * 产品名称
     */
    private String productName;

    private Page<ConsumeOilFlowWxDto> page;

    private List<ConsumeOilFlowWxDto> consumeOilFlowWxDtos;




}
