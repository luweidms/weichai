package com.youming.youche.system.api.mycenter;

import com.youming.youche.system.dto.mycenter.SysUserDto;
import com.youming.youche.system.vo.mycenter.UpdatePhoneVo;

/**
 * @InterfaceName IMyCenterService
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/18 17:05
 */
public interface IMyCenterService {

    /** 获取用户信息
     * */
    SysUserDto getSysUserById(Long userId,Long tenantId);

    /** 修改密码
     * */
    boolean updatePwd(Long userId,String newPwd);

    /** 重置密码
     * */
    boolean resetPwd(String phone,String newPwd);

    /** 发送验证码--修改手机号
     * */
    void sendVerifyCodeByUpdatePhone(Long userId,String phone, Boolean isNewPhone);

    /** 发送验证码--重置密码
     * */
    void sendVerifyCodeByResetPwd(String phone);

    /** 检查验证码--修改手机号
     * */
    boolean checkVerifyCodeByUpdatePhone(String phone, String verifyCode);

    /** 检查验证码--重置密码
     * */
    boolean checkVerifyCodeByResetPwd(String phone, String verifyCode);

    /** 修改手机号
     * */
    boolean updatePhone(UpdatePhoneVo updatePhoneVo);

}