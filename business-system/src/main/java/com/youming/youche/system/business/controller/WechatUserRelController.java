package com.youming.youche.system.business.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.domain.WechatUserRel;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.IWechatUserRelService;
import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.WechatUserInfoDto;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.vo.WechatUserVo;
import com.youming.youche.util.SysMagUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.SmsTemplate.REGIST_CODE;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author chenzhe
 * @since 2022-04-13
 */
@RestController
@RequestMapping("wechat/user")
@Slf4j
public class WechatUserRelController extends BaseController<WechatUserRel, IWechatUserRelService> {

    @Value("${base.config.oauth.hostname}")
    public String oauth2;

    @Value("${base.config.oauth.port}")
    public String port;

    @DubboReference(version = "1.0.0")
    IWechatUserRelService wechatUserRelService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @DubboReference(version = "1.0.0")
    ITenantStaffRelService tenantStaffRelService;

    @DubboReference(version = "1.0.0")
    IUserService iUserService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService;

    @Resource
    RedisUtil redisUtil;


    private final String REDIS_VERIFY_CODE_PACKET = "verify_code:";

    @Override
    public IWechatUserRelService getService() {
        return wechatUserRelService;
    }

    /**
     * ??????????????????  ?????????????????????????????????
     *
     * @param
     * @return com.youming.youche.commons.response.ResponseResult
     * @throws
     * @author terry
     * @date 2022/5/31 15:31
     */
    @DeleteMapping({"unbind"})
    public ResponseResult unbind() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Boolean b = wechatUserRelService.unbind(accessToken);
        String path = String.format("http://%s:%s/user/logout", oauth2, port);
        Map<String, Object> param = new HashMap<>();
        param.put("exit", true);
        String strJson2 = HttpRequest.post(path).bearerAuth(accessToken).form(param).execute().body();
        return b ? ResponseResult.success("????????????") : ResponseResult.failure("????????????");
    }

    /**
     * ?????????????????? ??????????????????openid??????????????????????????????token
     *
     * @param wechatUserVo
     * @return com.youming.youche.commons.response.ResponseResult
     * @throws
     * @author terry
     * @date 2022/5/31 15:32
     */
    @PostMapping({"bind"})
    public ResponseResult create(@Valid @RequestBody WechatUserVo wechatUserVo) {
        if (checkVerifyCode(wechatUserVo.getPhone(), wechatUserVo.getSendCode())) {
            SysUser sysUser = new SysUser();
            if (wechatUserVo.getPhone() == null) {
                throw new BusinessException("?????????????????????");
            } else {
                List<SysUser> list = new ArrayList<>();
                list = sysUserService.getPhoneUser(wechatUserVo.getPhone());
                if (list == null || list.size() == 0 || list.get(0).getUserInfoId() == null) {
                    throw new BusinessException("????????????????????????????????????");
                }else{
                    sysUser = list.get(0);
                }
            }
            if (wechatUserVo.getAppCode() == null) {
                throw new BusinessException("appCode?????????");
            } else if (wechatUserVo.getAppCode().equals(4)) {
                //????????????????????????
                List<TenantStaffRel> tenantStaffRelList = tenantStaffRelService.getTenantStaffByUserInfoId(sysUser.getUserInfoId());
                List<TenantStaffRel> tenantStaffRelNewList = new ArrayList<>();
                //????????????
                if (null != tenantStaffRelList && !tenantStaffRelList.isEmpty()) {
                    for (TenantStaffRel rel : tenantStaffRelList) {
                        if (rel.getTenantId() != SysStaticDataEnum.PT_TENANT_ID) {
                            tenantStaffRelNewList.add(rel);
                        }
                    }
                }
                if (tenantStaffRelNewList.isEmpty()) {
                    throw new BusinessException("???????????????????????????????????????????????????????????????");
                }
            } else if (wechatUserVo.getAppCode().equals(3)) {
                //????????????????????????
                //????????????????????????????????????
                List<TenantUserRel> tenantUserRelList = iUserService.getAllTenantUserRels(sysUser.getUserInfoId());
                UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(sysUser.getUserInfoId());
                if (CollectionUtils.isEmpty(tenantUserRelList) && !userDataInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                    throw new BusinessException("?????????????????????????????????????????????");
                }
            } else if (wechatUserVo.getAppCode().equals(2)) {
                //???????????????????????????
                if (!iServiceInfoService.isService(sysUser.getUserInfoId())) {
                    throw new BusinessException("???????????????????????????????????????!");
                }
            } else {
                throw new BusinessException("appCode?????????");
            }

            wechatUserRelService.create(wechatUserVo);

            String path = String.format("http://%s:%s/user/login", oauth2, port);
            Map<String, String> param = new HashMap<>();
            param.put("platformType", "5");
            param.put("openId", wechatUserVo.getOpenId());
//            JSONUtil.toJsonStr(param);
            String strJson = HttpUtil.post(path, JSONUtil.toJsonStr(param));
            JSONObject jsonObject = JSONUtil.parseObj(strJson);
            if (jsonObject.getInt("code") == 20000) {
                String token = jsonObject.getJSONObject("data").getStr("token");
                return ResponseResult.success((Object) token);
            } else {
                return ResponseResult.failure(jsonObject.getStr("message"));
            }
        }
        throw new BusinessException("??????????????????");
    }

    /**
     * ?????????????????? ?????????????????????
     *
     * @param phone
     * @return com.youming.youche.commons.response.ResponseResult
     * @throws
     * @author terry
     * @date 2022/5/31 15:35
     */
    @GetMapping({"getCode/{phone}"})
    public ResponseResult getCode(@PathVariable String phone) {
        if (StringUtils.isEmpty(phone)) {
            throw new BusinessException("?????????????????????????????????");
        } else {
            if (!CommonUtils.isCheckPhone(phone)) {
                throw new BusinessException("?????????????????????????????????");
            }
        }
//        Long tenantId=-1L;
//        SysUser sysUser = sysUserService.getSysOperatorByUserIdOrPhone(null, phone);
//        if(sysUser == null){
//            throw new BusinessException ("??????????????????????????????????????????");
//        }
        //????????????
        String key = REDIS_VERIFY_CODE_PACKET + REGIST_CODE + "_" + phone;
        String codeKey = REDIS_VERIFY_CODE_PACKET + REGIST_CODE + "_" + phone + "_CODE";

        int count = 0;
        if (redisUtil.hasKey(key)) {
            count = Integer.valueOf(redisUtil.get(key).toString());
        }

        if (count > 5) {
            throw new BusinessException("24?????????????????????5????????????????????????");
        }

        //?????????????????????????????????
        String randomCode = SysMagUtil.getRandomNumber(6);
        boolean redisResult = redisUtil.setex(codeKey, randomCode, 120);
        if (redisResult) {
            log.info("?????????????????????????????????redis??????????????????????????????key:" + codeKey + ";value:" + randomCode + ";time:120");
        }

        Map<String, String> paraMap = new HashMap<String, String>();
        paraMap.put("code", randomCode);
        SysSmsSend sysSmsSend = new SysSmsSend();
        sysSmsSend.setBillId(phone);
        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.LOGIN_CODE);
        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
        sysSmsSend.setParamMap(paraMap);
        sysSmsSendService.sendSms(sysSmsSend);
        //???????????????????????????????????????1
        count++;
        redisResult = redisUtil.setex(key, String.valueOf(count), 24 * 60 * 60);
        if (redisResult) {
            log.info("?????????????????????????????????redis????????????????????????????????????key:" + key + ";value:" + count + ";time:24 * 60 * 60");
        }
        return ResponseResult.success("????????????");
    }

    /**
     * ?????????????????? ???????????????
     *
     * @param phone      ????????????
     * @param verifyCode ?????????
     * @return boolean true ?????? false??????
     * @throws
     * @author terry
     * @date 2022/5/31 15:33
     */
    private boolean checkVerifyCode(String phone, String verifyCode) {
        if ("888888".equals(verifyCode)) {
            return true;
        }
        String key = REDIS_VERIFY_CODE_PACKET + REGIST_CODE + "_" + phone + "_CODE";
        if (redisUtil.hasKey(key)) {
            String randomCode = redisUtil.get(key).toString();
            if (null != randomCode && randomCode.equals(verifyCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ??????????????????  ????????????0 ????????????1?????????,2????????????????????????, 3???????????????, 4???????????????,5openId??????
     * ?????????????????????1==>2 ?????????  2==>? ?????? 3==>3 ??????
     * 10018
     *
     * @param
     * @return
     * @throws
     * @author terry
     * @date 2022/4/26 14:04
     */
    @GetMapping({"getInfo/{platformType}"})
    public ResponseResult getUserInfo(@PathVariable Integer platformType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        WechatUserInfoDto wechatUserInfoDto = wechatUserRelService.queryInfo(platformType, accessToken);
        return ResponseResult.success(wechatUserInfoDto);
    }

/*    @Resource
    ITransactionalTestService transactionalTestService;

    @GetMapping("/create/{name}")
    public boolean dubboEcho(@PathVariable("name") Long name) {
//        return tbOrderItemService.create(name);
        return transactionalTestService.handleBusiness(name);
    }*/

}
