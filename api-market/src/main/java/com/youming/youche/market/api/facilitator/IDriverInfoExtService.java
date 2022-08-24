package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.facilitator.DriverInfoExt;

/**
 * <p>
 * 司机信息扩展表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-01
 */
public interface IDriverInfoExtService extends IBaseService<DriverInfoExt> {


    /**
     * 修改认证状态、处理状态
     */
    void updateLuGeAuthState(Long userId, Boolean authState, String remark, Integer processState);

    /**
     * 通过userid查询
     * @param userId
     * @return
     */
    DriverInfoExt getDriverInfoExtByUserId(Long userId);


    /**
     * 根据userId创建一条扩展记录
     * @param userId
     * @return
     */
    DriverInfoExt createDriverInfoExt(Long userId);

}
