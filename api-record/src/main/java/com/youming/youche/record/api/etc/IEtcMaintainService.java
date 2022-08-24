package com.youming.youche.record.api.etc;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.etc.EtcMaintain;

/**
 * @Date:2021/12/22
 */
public interface IEtcMaintainService extends IService<EtcMaintain> {
    /**
     * 车辆解绑ETC卡
     * @param tenantId
     * @param plateNumber
     */
    public void untieEtc(long tenantId,String plateNumber);
}
