package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;

import com.youming.youche.commons.domain.WechatUserRel;
import com.youming.youche.system.dto.WechatUserInfoDto;
import com.youming.youche.system.vo.WechatUserVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author chenzhe
 * @since 2022-04-13
 */
public interface IWechatUserRelService extends IBaseService<WechatUserRel> {

    /**
     * 方法实现说明 将用户信息与openid进行绑定，
     * @author      terry
     * @param wechatUserVo
     * @return      void
     * @exception
     * @date        2022/5/31 15:35
     */
    void create(WechatUserVo wechatUserVo);

    /**
     * 方法实现说明  根据微信openid删除微信绑定
     * @author      terry
     * @param openId
     * @return      boolean
     * @exception
     * @date        2022/5/1 11:28
     */
    boolean delByOpenId(String openId);

    /**
     * 微信用户关系表
     *
     * @param appCode 多个app区分标示：1服务商 2驻场
     * @param state   (1：有效，0：无效)
     * @return
     */
    List<WechatUserRel> getWechatUserRelByAppCodeAndState(Integer appCode, Integer state);

    /**
     * 方法实现说明  查询微信用户信息
     * @author      terry
     * @param platformType
    * @param accessToken
     * @return      com.youming.youche.system.dto.WechatUserInfoDto
     * @exception
     * @date        2022/5/1 11:28
     */
    WechatUserInfoDto queryInfo(Integer platformType, String accessToken);

    /**
     * 方法实现说明  微信用户解绑
     * @author      terry
     * @param accessToken
     * @return      java.lang.Boolean
     * @exception
     * @date        2022/5/1 11:27
     */
    Boolean unbind(String accessToken);

    /**
     * 方法实现说明  微信用户解绑
     * @author      terry
     * @param phone
     * @return      java.lang.Boolean
     * @exception
     * @date        2022/5/1 11:27
     */
    Boolean unbindPhone(String phone);
}
