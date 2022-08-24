package com.youming.youche.record.api.service;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.service.ServiceRepairItems;

import java.util.List;

/**
 * <p>
 * 服务商维修项 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-04-08
 */
public interface IServiceRepairItemsService extends IBaseService<ServiceRepairItems> {

    /**
     * 获取服务商维修项
     *
     * @param flowIdList 维修保养订单id
     */
    List<ServiceRepairItems> getRepairOrderItems(List<Long> flowIdList);

}
