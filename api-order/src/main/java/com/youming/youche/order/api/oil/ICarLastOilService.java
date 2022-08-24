package com.youming.youche.order.api.oil;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.oil.CarLastOil;

/**
 * <p>
 * 车辆最后油卡号表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-03-24
 */
public interface ICarLastOilService extends IBaseService<CarLastOil> {

    /**
     * 根据车牌号查询最后一次油卡
     */
    CarLastOil getCarLastOilByPlateNumber(String plateNumber, Long tenantId);

}
