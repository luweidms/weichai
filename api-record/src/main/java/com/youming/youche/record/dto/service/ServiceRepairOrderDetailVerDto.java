package com.youming.youche.record.dto.service;

import com.youming.youche.record.domain.service.ServiceRepairItemsVer;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @description 车辆维保
 * @date 2022/1/21 18:39
 */
@Data
public class ServiceRepairOrderDetailVerDto implements Serializable {

    private static final long serialVersionUID = 1L;
    // 服务商维修
    private List<ServiceRepairItemsVer> serviceRepairItemsVerList;
    // 车队id
    private Long tenantId;
    // 维修保养订单信息
    private Map<String, Object> serviceRepairOrderVer;
    // 服务商名称
    private Map shopInfo;
}
