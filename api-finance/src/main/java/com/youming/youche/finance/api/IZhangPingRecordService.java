package com.youming.youche.finance.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.ZhangPingRecord;

/**
* <p>
    *  服务类
    * </p>
* @author WuHao
* @since 2022-04-13
*/
    public interface IZhangPingRecordService extends IBaseService<ZhangPingRecord> {
    /**
     * 强制帐平
     */
    void saveForceZhangPingNew(Long userId, String zhangPingType, Integer userType, String accessToken);
}
