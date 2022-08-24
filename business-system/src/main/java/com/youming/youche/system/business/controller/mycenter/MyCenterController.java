package com.youming.youche.system.business.controller.mycenter;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.AesEncryptUtil;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.mycenter.IMyCenterService;
import com.youming.youche.system.dto.mycenter.SysUserDto;
import com.youming.youche.system.vo.mycenter.ResetPwdVo;
import com.youming.youche.system.vo.mycenter.UpdatePhoneVo;
import com.youming.youche.system.vo.mycenter.UpdatePwdVo;
import com.youming.youche.system.vo.mycenter.SysUserVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @ClassName MyCenterController
 * @Description 个人中心
 * @Author zag
 * @Date 2022/1/25 16:25
 */
@RestController
@RequestMapping("/mycenter/user")
public class MyCenterController {

    @DubboReference(version = "1.0.0")
    IMyCenterService myCenterService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @Resource
    protected HttpServletRequest request;

    @Resource
    LoginUtils loginUtils;

    @Resource
    public BCryptPasswordEncoder passwordEncoder;


    /**
     * 根据accessToken获取当前登录用户信息
     * @author zag
     * @date 2022/3/10 14:08
     * @param accessToken
     * @return com.youming.youche.commons.domain.LoginInfo
     */
    private LoginInfo getLoginUser(String accessToken) {
        return loginUtils.get(accessToken);
    }

    /**
     * @description 获取登录员工用户信息
     * @author zag
     * @date 2022/2/15 10:04
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("get")
    public ResponseResult get() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LoginInfo loginInfo = getLoginUser(accessToken);
        SysUserDto sysUserDto = myCenterService.getSysUserById(loginInfo.getUserInfoId(),loginInfo.getTenantId());
        SysUserVo sysUserVo = new SysUserVo();
        BeanUtils.copyProperties(sysUserDto,sysUserVo);
        return ResponseResult.success(sysUserVo);
    }

    /**
     * @description 修改用户密码
     * @author zag
     * @date 2022/2/15 10:04
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("updatePwd")
    public ResponseResult updatePwd(@Valid @RequestBody UpdatePwdVo updatePwdVo) {
        String oldPwd = AesEncryptUtil.desEncrypt(updatePwdVo.getOldPwd());
        String newPwd = AesEncryptUtil.desEncrypt(updatePwdVo.getNewPwd());
        String newPwd2 = AesEncryptUtil.desEncrypt(updatePwdVo.getNewPwd2());
        if (!newPwd.equals(newPwd2)) {
            throw new BusinessException("新密码与确认密码不一致");
        }
        SysUser sysUser = sysUserService.getByUserInfoId(updatePwdVo.getUserId());
        if (!passwordEncoder.matches(oldPwd, sysUser.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        String password = passwordEncoder.encode(newPwd);
        boolean result = myCenterService.updatePwd(updatePwdVo.getUserId(), password);
        if (result) {
            return ResponseResult.success("密码修改成功");
        }
        return ResponseResult.failure("密码修改失败");
    }

    /**
     * 设置新密码
     * @author zag
     * @date 2022/3/10 10:46
     * @param resetPwdVo
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("resetPwd")
    public ResponseResult resetPwd(@Valid @RequestBody ResetPwdVo resetPwdVo) {
        String newPwd = AesEncryptUtil.desEncrypt(resetPwdVo.getNewPwd());
        String newPwd2 = AesEncryptUtil.desEncrypt(resetPwdVo.getNewPwd2());
        if (!newPwd.equals(newPwd2)) {
            throw new BusinessException("新密码与确认密码不一致");
        }
        String password = passwordEncoder.encode(newPwd);
        boolean result = myCenterService.resetPwd(resetPwdVo.getPhone(), password);
        if (result) {
            return ResponseResult.success("设置新密码成功");
        }
        return ResponseResult.failure("设置新密码失败");
    }

    /**
     * @description 修改员工手机事情-发送手机验证码
     * @author zag
     * @date 2022/2/15 10:21
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("sendVerifyCodeByUpdatePhone")
    public ResponseResult sendVerifyCodeByUpdatePhone(@RequestParam(value = "phone") String phone,
                                         @RequestParam(value = "isNewPhone",defaultValue = "false") Boolean isNewPhone){
        if(StringUtils.isBlank(phone)){
            throw new BusinessException("手机号不能为空");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LoginInfo loginInfo = getLoginUser(accessToken);
        myCenterService.sendVerifyCodeByUpdatePhone(loginInfo.getUserInfoId(), phone,isNewPhone);
        return ResponseResult.success();
    }

    /**
     * 设置新密码-发送手机验证码
     * @author zag
     * @date 2022/3/10 14:32
     * @param phone
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("sendVerifyCodeByResetPwd/{phone}")
    public ResponseResult sendVerifyCodeByResetPwd(@PathVariable String phone){
        myCenterService.sendVerifyCodeByResetPwd(phone);
        return ResponseResult.success();
    }

    /**
     * 员工修改手机号-验证手机验证码
     * @author zag
     * @date 2022/3/10 14:33
     * @param phone
     * @param verifyCode
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("checkVerifyCodeByUpdatePhone")
    public ResponseResult checkVerifyCodeByUpdatePhone(@RequestParam(value = "phone") String phone,
                                                       @RequestParam(value = "verifyCode") String verifyCode) {
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (StringUtils.isBlank(verifyCode)) {
            throw new BusinessException("验证码不能为空");
        }
        Boolean result = myCenterService.checkVerifyCodeByUpdatePhone(phone, verifyCode);
        return result == true ? ResponseResult.success() : ResponseResult.failure("验证码错误");
    }

    /**
     * 设置新密码-验证手机验证码
     * @author zag
     * @date 2022/3/10 14:35
     * @param phone
     * @param verifyCode
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("checkVerifyCodeByResetPwd")
    public ResponseResult checkVerifyCodeByResetPwd(@RequestParam(value = "phone") String phone,
                                                    @RequestParam(value = "verifyCode") String verifyCode){

        if (StringUtils.isBlank(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (StringUtils.isBlank(verifyCode)) {
            throw new BusinessException("验证码不能为空");
        }
        Boolean result = myCenterService.checkVerifyCodeByResetPwd(phone, verifyCode);
        return result == true ? ResponseResult.success() : ResponseResult.failure("验证码错误");
    }


    /**
     * @description 员工修改手机号
     * @author zag
     * @date 2022/2/15 10:04
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("updatePhone")
    public ResponseResult updatePhone(@Valid @RequestBody UpdatePhoneVo updatePhoneVo){
        if(updatePhoneVo.getOldPhone().equals(updatePhoneVo.getNewPhone())){
            throw new BusinessException("原手机号与新手机号不能一致");
        }
        boolean result= myCenterService.updatePhone(updatePhoneVo);
        if(result){
            return ResponseResult.success("修改手机号成功");
        }
        return ResponseResult.failure("修改手机号失败");
    }

    /**
     * @description 用户登出
     * @author zag
     * @date 2022/2/15 10:05
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("logout")
    public ResponseResult logOut(){
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        redisUtil.del(accessToken);
        return ResponseResult.success();
    }


}
