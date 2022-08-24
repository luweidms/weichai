package com.youming.youche.record.api.dirverInfo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.dirverInfo.DriverInfoExt;

/**
 * <p>
 * 司机信息扩展表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IDriverInfoExtService extends IService<DriverInfoExt> {

    /**
     * 修改为未认证、未处理
     *
     * @param userId       用户编号
     * @param authState    是否路哥认证（0、否；1、是）
     * @param remark       描述/备注
     * @param processState 路哥处理状态(0、未处理；1、已处理)
     */
    void updateLuGeAuthState(Long userId, Boolean authState, String remark, Integer processState);

    /**
     * 查询司机扩展信息
     *
     * @param userId 用户编号
     * @return
     */
    DriverInfoExt getDriverInfoExtByUserId(Long userId);

    /**
     * 修改为未认证、未处理
     *
     * @param userId 用户编号
     */
    void resetLuGeAuthState(Long userId);

}
