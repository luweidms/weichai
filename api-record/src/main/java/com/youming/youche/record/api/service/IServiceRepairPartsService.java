package com.youming.youche.record.api.service;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.service.ServiceRepairParts;

import java.util.List;

/**
 * <p>
 * 维修零配件 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-04-08
 */
public interface IServiceRepairPartsService extends IBaseService<ServiceRepairParts> {

    /**
     * 查询维修零配件
     *
     * @param flowIdList 维修保养订单id
     */
    List<ServiceRepairParts> getRepairOrderParts(List<Long> flowIdList);

}
