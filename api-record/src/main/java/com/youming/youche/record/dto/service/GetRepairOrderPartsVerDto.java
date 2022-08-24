package com.youming.youche.record.dto.service;

import com.youming.youche.record.domain.service.ServiceRepairPartsVer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/5/23 10:47
 */
@Data
public class GetRepairOrderPartsVerDto implements Serializable {

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 工时总价
     */
    private Long totalItemPrice;

    /**
     * 工时
     */
    private Double itemManHour;

    /**
     * 修保养订单信息
     */
    private List<ServiceRepairPartsVer> serviceRepairPartsVerList;

    /**
     * 配件总价
     */
    private Long totalPartsPrice;

    /**
     * 总价 = 工时总价 + 配件总价
     */
    private Long totalPrice;
}
