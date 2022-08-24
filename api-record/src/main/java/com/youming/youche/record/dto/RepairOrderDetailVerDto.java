package com.youming.youche.record.dto;

import com.youming.youche.record.domain.oa.OaFiles;


import com.youming.youche.record.domain.service.ServiceRepairItemsVer;
import com.youming.youche.record.dto.service.ServiceRepairOrderDetailVerDto;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: luona
 * @date: 2022/5/23
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class RepairOrderDetailVerDto implements Serializable {
    private List<OaFiles> repairOrderPicMapList; // 图片信息
    private List<SysOperLog> sysOperLogList; // 日志
    private SysTenantDef tenant;// 车队信息
    private List<ServiceRepairItemsVer> serviceRepairItemsVerList; // 维修项目将
    // 车队id
    private Long tenantId;
    private Map<String, Object> serviceRepairOrderVer; // 维修保养信息
    private Map shopInfo;  // 服务商名称

}
