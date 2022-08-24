package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.common.EncryPwd;
import com.youming.youche.system.api.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController<SysUser, ISysUserService> {

	@DubboReference(version = "1.0.0")
	ISysUserService sysUserService;

	@Override
	public ISysUserService getService() {
		return sysUserService;
	}

	/**
	 * 方法实现说明 获取当前登录者登录账户信息
	 * @author      terry
	 * @param
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 15:16
	 */
	@GetMapping({ "get" })
	public ResponseResult get() {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		return ResponseResult.success(sysUserService.get(accessToken));
	}

    /**
     * 接口名称：修改手机号校验登录密码
     * 接口编号：10028
     * <p>
     * 接口入参：
     * userId      用户编号
     * loginPwd	   旧登录密码  base 加密
     * <p>
     * 接口出参：
     * result 操作结果：Y-成功，N-失败
     */
    @PostMapping("valideLoginPwdForUpdatePhone")
    public ResponseResult valideLoginPwdForUpdatePhone(Long userId, String loginPwd) {
        if (StringUtils.isEmpty(loginPwd)) {
            throw new BusinessException("登录密码不能为空！");
        }

        //验证登路密码
        loginPwd = EncryPwd.pwdDecryption(loginPwd);
        if (StringUtils.isEmpty(loginPwd)) {
            throw new BusinessException("登录密码不能为空！");
        }

        if (userId <= 0) {
            throw new BusinessException("用户编号不能为空！");
        }
        sysUserService.valideLoginPwdForUpdatePhone(userId, loginPwd);
        return ResponseResult.success();
    }

	/**
	 * 接口编码:11007
	 * 接口入参：
	 * billId	         用户手机
	 * codeType	         验证码类型  1：注册验证码2、重置/忘记登录验证码3、支付密码 11、修改登录手机验证码17、登录验证码（其他待扩展）
	 * <p>
	 * 获取短信验证码接口
	 */
	@GetMapping("getMessageAuthCode")
	public ResponseResult getMessageAuthCode(String billId, Integer codeType) {
		sysUserService.getMessageAuthCode(billId, codeType);
		return ResponseResult.success();
	}

}
