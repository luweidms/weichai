package com.youming.youche.record.api.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.user.UserDataInfoVer;

import java.util.List;

/**
 * <p>
 * 用户资料信息 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IUserDataInfoVerService extends IService<UserDataInfoVer> {

    /**
     * 获取待审核用户信息
     *
     * @param userId 用户编号
     */
    UserDataInfoVer getUserDataInfoVerNoTenant(Long userId);

    /**
     * 保存用防护资料信息
     */
    Long saveById(UserDataInfoVer userDataInfoVer);

    /**
     * 查询用户资料
     *
     * @param userId 用户编号
     */
    UserDataInfoVer getUserDataInfoVer(Long userId);

    /**
     * 查询用户资料
     *
     * @param userId   user_data_info表主键id
     * @param verState 审核转台待审
     * @return
     */
    List<UserDataInfoVer> getUserDataInfoVerList(Long userId, Integer verState);

}
