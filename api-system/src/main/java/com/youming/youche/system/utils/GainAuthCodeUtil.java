package com.youming.youche.system.utils;

import com.youming.youche.conts.EnumConsts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GainAuthCodeUtil implements Serializable {

    public static Map<String, Long> TEMPLATE_MAP;
    public static Map<String, String> VALIDKEY_MAP;

    static {
        VALIDKEY_MAP = new HashMap<String, String>();
        /**注册  验证码（在用）**/
        VALIDKEY_MAP.put("1", EnumConsts.RemoteCache.VALIDKEY_CODE);
        /**重置 登录密码 验证码 （在用）*/
        VALIDKEY_MAP.put("2", EnumConsts.RemoteCache.VALIDKEY_CODE_RESET);
        /**支付验证码  （在用）**/
        VALIDKEY_MAP.put("3", EnumConsts.RemoteCache.VALIDKEY_PAY);

        VALIDKEY_MAP.put("4", EnumConsts.RemoteCache.BUNDING_PHONENUMBER);//绑定手机号

        VALIDKEY_MAP.put("5", EnumConsts.RemoteCache.SHORTCUT_REGISTERE);//快捷注册
        VALIDKEY_MAP.put("6", EnumConsts.RemoteCache.FEIGET_LOGIN_PASSWORD);//忘记登录密码验证码
        VALIDKEY_MAP.put("7", EnumConsts.RemoteCache.UPDATE_LOGIN_PASSWORD);//修改登录密码验证码

        VALIDKEY_MAP.put("8", EnumConsts.RemoteCache.RESET_PAY_PASSWORD);//设置支付密码
        VALIDKEY_MAP.put("9", EnumConsts.RemoteCache.UPDATE_PAY_PASSWORD);//修改支付密码
        VALIDKEY_MAP.put("10", EnumConsts.RemoteCache.FORGET_PAY_PASSWORD);// 忘记支付密码
        /**修改登录手机号 （在用）**/
        VALIDKEY_MAP.put("11", EnumConsts.RemoteCache.UPDATE_PHONENUMBER);

        VALIDKEY_MAP.put("12", EnumConsts.RemoteCache.INVITE_REGISTERE);//邀请注册 1000000001
        VALIDKEY_MAP.put("13", EnumConsts.RemoteCache.DRAWING);//提款
        VALIDKEY_MAP.put("14", EnumConsts.RemoteCache.TRANSFER_ACCOUNTS);//转账
        VALIDKEY_MAP.put("15", EnumConsts.RemoteCache.ORDER_PAY);//订单支付

        VALIDKEY_MAP.put("16", EnumConsts.RemoteCache.SAFE_PAY);//保险支付

        /**手机登录验证码 （在用）**/
        VALIDKEY_MAP.put("17", EnumConsts.RemoteCache.VALIDKEY_CODE_LOGIN);

        TEMPLATE_MAP = new HashMap<String, Long>();
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.VALIDKEY_CODE, EnumConsts.SmsTemplate.REGIST_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.VALIDKEY_CODE_RESET, EnumConsts.SmsTemplate.RESET_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.VALIDKEY_PAY, EnumConsts.SmsTemplate.PAY_CODE);

        TEMPLATE_MAP.put(EnumConsts.RemoteCache.INVITE_REGISTERE, EnumConsts.SmsTemplate.REGIST_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.SHORTCUT_REGISTERE, EnumConsts.SmsTemplate.REGIST_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.FEIGET_LOGIN_PASSWORD, EnumConsts.SmsTemplate.RESET_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.UPDATE_LOGIN_PASSWORD, EnumConsts.SmsTemplate.MODIFY_PHONE);

        TEMPLATE_MAP.put(EnumConsts.RemoteCache.RESET_PAY_PASSWORD, EnumConsts.SmsTemplate.REGIST_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.UPDATE_PAY_PASSWORD, EnumConsts.SmsTemplate.MODIFY_PWD_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.FORGET_PAY_PASSWORD, EnumConsts.SmsTemplate.RESET_PWD_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.UPDATE_PHONENUMBER, EnumConsts.SmsTemplate.MODIFY_PHONE);

        TEMPLATE_MAP.put(EnumConsts.RemoteCache.BUNDING_PHONENUMBER, EnumConsts.SmsTemplate.MODIFY_PHONE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.DRAWING, EnumConsts.SmsTemplate.WITHDRAWALS_REGIST_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.TRANSFER_ACCOUNTS, EnumConsts.SmsTemplate.TRAMSFER_REGIST_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.ORDER_PAY, EnumConsts.SmsTemplate.ORDER_PAY);

        TEMPLATE_MAP.put(EnumConsts.RemoteCache.SAFE_PAY, EnumConsts.SmsTemplate.ZHI_FU_CODE);
        TEMPLATE_MAP.put(EnumConsts.RemoteCache.VALIDKEY_CODE_LOGIN, EnumConsts.SmsTemplate.LOGIN_CODE);

    }

    public static String getValidValue(String key) {
        String validKey = VALIDKEY_MAP.get(key);
        return validKey;

    }

    public static Long getTemplateValue(String key) {
        Long templateId = TEMPLATE_MAP.get(key);
        return templateId;

    }

}
