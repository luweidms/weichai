package com.youming.youche.record.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.service.ServiceRepairOrder;
import com.youming.youche.record.domain.service.ServiceRepairOrderVer;
import com.youming.youche.record.dto.RepairOrderDetailVerDto;
import com.youming.youche.record.dto.RepairOrderDto;
import com.youming.youche.record.dto.service.GetRepairOrderPartsVerDto;

/**
 * <p>
 * 维修保养订单 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-04-08
 */
public interface IServiceRepairOrderVerService extends IBaseService<ServiceRepairOrderVer> {

    /**
     * 维修保养订单查询
     * @param flowId 主表主键、维修保养订单id
     * @param diffFlg
     * @return
     */
    ServiceRepairOrderVer getServiceRepairOrderVer(long flowId, Integer diffFlg);

    /**
     * 维修保养工单配件详情  82016
     */
    GetRepairOrderPartsVerDto getRepairOrderPartsVer(Long hisId, Long flowId, Long orderItemId);

    /**
     * 获取维修保养工单列表(82014)
     * @param orderStatus
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<ServiceRepairOrder> doQueryOrderListApp(Integer orderStatus,Integer pageNum,Integer pageSize,String accessToken);

    /**
     * 维修保养工单详情(82015)
     * @param flowId
     * @return
     */
    RepairOrderDetailVerDto getRepairOrderDetailVerApp(Long flowId);



    /**
     * 发起维修保养(82012)
     * @param repairOrderDto
     * @return
     */
    boolean doSaveOrUpdateServiceRepairOrder(RepairOrderDto repairOrderDto, String accessToken);
}
