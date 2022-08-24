package com.youming.youche.market.api.facilitator;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.dto.facilitator.UserDataInfoInDto;

/**
 * <p>
 * 用户资料信息 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
public interface IUserDataInfoMarketService extends IBaseService<UserDataInfo> {


    /**
     * 用户登录信息获取
     */
    UserDataInfo getUserDataInfoByAccessToken(String accessToken);


    /**
     * 根据登陆人手机号查找用户信息（UserDataInfo）
     *
     * @param loginAcct
     * @return
     * @throws Exception
     */
    UserDataInfo getUserDataInfoByLoginAcct(String loginAcct);

    /**
     * 保存的到id
     *
     * @param userDataInfo
     * @return
     */
    Long saveUserDataInfo(UserDataInfo userDataInfo);

    /**
     * 服务商用修改用户信息
     *
     * @param in
     * @throws Exception
     */
    void modifyUserDataInfo(UserDataInfoInDto in, LoginInfo user);


    /**
     * 修改服务商用户走审核流程
     *
     * @param userDataInfoIn
     * @return
     * @throws Exception
     */
    String doUpdateServ(UserDataInfoInDto userDataInfoIn);

    /**
     * 审核服务商用户
     *
     * @param userId    用户编号
     * @param auditFlg  审核结果 true 通过  false 不通过
     * @param auditDesc 审核原因
     * @return
     * @throws Exception
     */
    String doAuditServ(Long userId, Boolean auditFlg, String auditDesc, LoginInfo baseUser);

}
