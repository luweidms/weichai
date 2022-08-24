package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.citys.City;
import com.youming.youche.conts.EnumConsts;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

@Component
public class SysCfgRedisUtils {


    @Resource
    RedisUtil redisUtil;

    @Resource
    private LoginUtils loginUtils;

    public SysCfg getSysCfgByCfgNameAndCfgSystem(String cfgName, Integer cfgSystem) {
        SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));

        if (null != sysCfg && (cfgSystem == -1 || cfgSystem.equals(sysCfg.getCfgSystem()))) {
            return sysCfg;
        }

        return new SysCfg();
    }


    public City getCityDataList(String codeType, String codeValue) {
        if (codeType != null && !codeType.equals("") && !codeType.equals("null") && codeValue != null && !codeValue.equals("") && !codeValue.equals("null")) {
//            List<City> cityDataList = getCityDataList(codeType);
            List<City> cityDataList = (List<City>) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(codeType));
            if (cityDataList != null && cityDataList.size() > 0) {
                Iterator var3 = cityDataList.iterator();
                while (var3.hasNext()) {
                    City sysData = (City) var3.next();
                    if (sysData.getId() == Long.parseLong(codeValue)) {
                        return sysData;
                    }
                }
            }
        }
        return new City();
    }

    public Object getCfgVal(long tenantId, String cfgName, int system, Class type,String token) {
        SysCfg cfg = getSysCfg(tenantId, cfgName);
        if (cfg == null) {
            cfg = getSysCfg(cfgName,token);
        }
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

    public SysCfg getSysCfg(long tenantId, String cfgName) {
        SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
        if (sysCfg != null && sysCfg.getTenantId() != null && sysCfg.getTenantId().longValue() == tenantId) {
            return sysCfg;
        } else {
            return new SysCfg();
        }
    }

    public SysCfg getSysCfg(String cfgName, String token) {
        long tenantId = -1L;
        SysCfg sysCfg = null;
        LoginInfo user = loginUtils.get(token);
        if (user != null && user.getTenantId() != null) {
            tenantId = user.getTenantId();
        }
        if (tenantId > 0L) {
            sysCfg = getSysCfg(tenantId, cfgName);
        }

        if (sysCfg == null) {
            sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
            if (sysCfg == null) {
                throw new BusinessException("没有找到对应的系统参数！");
            }
        }
        return sysCfg;
    }

    public SysCfg getSysCfg(String cfgName, Long tenantId) {
        SysCfg sysCfg = null;
        if (tenantId != null && tenantId > 0) {

        } else {
            tenantId = -1L;
        }
        if (tenantId > 0L) {
            sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(tenantId + "_" + cfgName));
        }

        if (sysCfg == null) {
            sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
            if (sysCfg == null) {
                throw new BusinessException("没有找到对应的系统参数！");
            }
        }
        return sysCfg;
    }
}
