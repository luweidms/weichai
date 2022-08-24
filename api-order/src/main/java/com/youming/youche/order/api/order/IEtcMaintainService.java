package com.youming.youche.order.api.order;


import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.EtcMaintain;

import java.util.List;
import java.util.Map;

/**
* <p>
    *  服务类
    * </p>
* @author xxx
* @since 2022-03-28
*/
    public interface IEtcMaintainService extends IBaseService<EtcMaintain> {
    /**
     * 获取etc信息
     * @param bindVehicle
     * @param tenantId
     * @param accessToken
     * @return
     */
    EtcMaintain getETCardByNumber(String bindVehicle, Long tenantId,String accessToken);

    /**
     * 校验改车辆真真能绑定一个etc卡
     * @param bindVehicle
     * @param tenantId
     * @return
     */
    Map<String, Object> checkEtcBindVehicle(String bindVehicle, Long tenantId);

    /**
     * 根据车辆Id查询ETC信息
     * @param vehicleCode
     * @param state
     * @param tenantId
     * @return
     */
    List<EtcMaintain> queryEtcMaintainByVehicleCode(Long vehicleCode, Integer state,Long tenantId);

    /**
     * 检验改etc卡是否已经存在了
     * @param cardNo
     * @return
     */
    List<Map> checkEtcCode(String cardNo);
}
