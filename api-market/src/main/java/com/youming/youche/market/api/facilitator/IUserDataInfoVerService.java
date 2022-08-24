package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.facilitator.UserDataInfoVer;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
public interface IUserDataInfoVerService extends IBaseService<UserDataInfoVer> {

     /**
      * 获取待审用户信息
      *
      * @param userId 用户编号
      */
     UserDataInfoVer getUserDataInfoVerNoTenant(Long userId);

}
