package com.youming.youche.record.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.record.common.EnumConsts;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

public class SysCfgUtil {
    public SysCfgUtil() {
    }

//    public static boolean getCfgBooleanVal(String cfgName) {
//        return getCfgBooleanVal(cfgName, -1);
//    }

//    public static boolean getCfgBooleanVal(String cfgName, int system) {
//        SysCfg cfg = null;
//        if (system > -1) {
//            cfg = getSysCfg(cfgName, system);
//        } else {
//            cfg = getSysCfg(cfgName);
//        }
//
//        return cfg == null || system != -1 && system != cfg.getCfgSystem() ? false : (Boolean)getCfgVal(cfgName, system, Boolean.class);
//    }

//    public static Object getCfgVal(String cfgName, int system, Class type) {
//        SysCfg cfg = getSysCfg(cfgName);
//        if (cfg != null && (system == -1 || system == cfg.getCfgSystem())) {
//            if (type.equals(Integer.class)) {
//                return Integer.valueOf(cfg.getCfgValue());
//            } else if (type.equals(Double.class)) {
//                return Double.parseDouble(cfg.getCfgValue());
//            } else if (type.equals(Float.class)) {
//                return Float.parseFloat(cfg.getCfgValue());
//            } else {
//                return !type.equals(Boolean.class) ? String.valueOf(cfg.getCfgValue()) : "1".equals(cfg.getCfgValue()) || "true".equals(cfg.getCfgValue().toLowerCase());
//            }
//        } else {
//            return null;
//        }
//    }

//    public static Object getCfgVal(long tenantId, String cfgName, int system, Class type) {
//        SysCfg cfg = getSysCfg(tenantId, cfgName);
//        if (cfg == null) {
//            cfg = getSysCfg(cfgName);
//        }
//
//        if (cfg != null && (system == -1 || system == cfg.getCfgSystem())) {
//            if (type.equals(Integer.class)) {
//                return Integer.valueOf(cfg.getCfgValue());
//            } else if (type.equals(Double.class)) {
//                return Double.parseDouble(cfg.getCfgValue());
//            } else if (type.equals(Float.class)) {
//                return Float.parseFloat(cfg.getCfgValue());
//            } else {
//                return !type.equals(Boolean.class) ? String.valueOf(cfg.getCfgValue()) : "1".equals(cfg.getCfgValue()) || "true".equals(cfg.getCfgValue().toLowerCase());
//            }
//        } else {
//            return null;
//        }
//    }

//    public static SysCfg getSysCfg(String cfgName, int system) {
//        SysCfg sysCfg = getSysCfg(cfgName);
//        if (sysCfg.getCfgSystem() == null) {
//            throw new BusinessException("数据库系统参数异常！");
//        } else if (sysCfg.getCfgSystem() == system) {
//            return sysCfg;
//        } else {
//            throw new BusinessException("系统参数类型与数据库不一致！");
//        }
//    }

    public static SysCfg getSysCfg(long tenantId, String cfgName, int system, RedisUtil redisUtil) {
        SysCfg sysCfg = getSysCfg(tenantId, cfgName,redisUtil);
        if (sysCfg.getCfgSystem() == null) {
            throw new BusinessException("数据库系统参数异常！");
        } else if (sysCfg.getCfgSystem() == system) {
            return sysCfg;
        } else {
            throw new BusinessException("系统参数类型与数据库不一致！");
        }
    }

    public static SysCfg getSysCfg(String cfgName,String accessToken, LoginUtils loginUtils,RedisUtil redisUtil) {
        long tenantId = -1L;
        SysCfg sysCfg = null;
        LoginInfo user = loginUtils.get(accessToken);
        if (user != null && user.getTenantId() != null) {
            tenantId = user.getTenantId();
        }

        if (tenantId > 0L) {
            sysCfg = getSysCfg(tenantId, cfgName,redisUtil);
        }

        if (sysCfg == null) {
            sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
            if (sysCfg == null) {
                throw new BusinessException("没有找到对应的系统参数！");
            }
        }

        return sysCfg;
    }

    public static SysCfg getSysCfg(long tenantId, String cfgName,RedisUtil redisUtil) {
        return (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
    }

    public static boolean getCfgBooleanVal(String cfgName, String accessToken, LoginUtils loginUtils, RedisUtil redisUtil) {
        return getCfgBooleanVal(cfgName, -1, accessToken, loginUtils, redisUtil);
    }

    public static boolean getCfgBooleanVal(String cfgName, int system, String accessToken, LoginUtils loginUtils, RedisUtil redisUtil) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        SysCfg cfg = null;
        if (system > -1) {
            cfg = getSysCfg(loginInfo.getTenantId(), cfgName, system, redisUtil);
        } else {
            cfg = getSysCfg(cfgName, accessToken, loginUtils, redisUtil);
        }

        return cfg == null || system != -1 && system != cfg.getCfgSystem() ? false : (Boolean) getCfgVal(cfgName, system, Boolean.class, accessToken, loginUtils, redisUtil);
    }

    public static Object getCfgVal(String cfgName, int system, Class type, String accessToken, LoginUtils loginUtils, RedisUtil redisUtil) {
        SysCfg cfg = getSysCfg(cfgName, accessToken, loginUtils, redisUtil);
        if (cfg != null && (system == -1 || system == cfg.getCfgSystem())) {
            if (type.equals(Integer.class)) {
                return Integer.valueOf(cfg.getCfgValue());
            } else if (type.equals(Double.class)) {
                return Double.parseDouble(cfg.getCfgValue());
            } else if (type.equals(Float.class)) {
                return Float.parseFloat(cfg.getCfgValue());
            } else {
                return !type.equals(Boolean.class) ? String.valueOf(cfg.getCfgValue()) : "1".equals(cfg.getCfgValue()) || "true".equals(cfg.getCfgValue().toLowerCase());
            }
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        int i = 5;
        Integer b = 5;
        System.out.println(String.valueOf(i).equals(String.valueOf(b)));
    }
}
