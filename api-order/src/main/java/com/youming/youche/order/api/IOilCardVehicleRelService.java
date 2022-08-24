package com.youming.youche.order.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.OilCardVehicleRel;

/**
 * <p>
 * 油卡-车辆关系表 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-07
 */
public interface IOilCardVehicleRelService extends IBaseService<OilCardVehicleRel> {


    /**
     * 删除油卡绑定的车辆
     * @param cardId
     */
     void deleteOilCardVehicleRelByCardId(Long cardId);


    /**
     * 保存油卡车辆关系
     * @param vehicleNumber
     * @param cardId
     * @param oilCardNum
     * @param tenantId
     */
    void saveOilCardVehicleRel(String vehicleNumber,Long cardId,String oilCardNum,Long tenantId);



}
