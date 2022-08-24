package com.youming.youche.record.provider.utils;

import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;

import java.util.List;

public class SysStaticDataRedisUtils {

    public static SysStaticData getSysStaticDataByCodeName(RedisUtil redisUtil, String codeType, String codeName) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeName().equals(codeName)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    public static SysStaticData getSysStaticDataByCodeValue(RedisUtil redisUtil, String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }
    public static String getSysStaticDataByCodeValue2String(RedisUtil redisUtil, String codeType, String codeValue) {
        SysStaticData sysStaticData = getSysStaticDataByCodeValue(redisUtil, codeType, codeValue);
        if (sysStaticData == null) {
            return "";
        }
        return sysStaticData.getCodeName();
    }

    public static String getSysStaticDataById2String(RedisUtil redisUtil, String codeType, Long id) {
        SysStaticData sysStaticData = getSysStaticDataById(redisUtil, codeType, id);
        if (sysStaticData == null) {
            return "";
        }
        return sysStaticData.getCodeName();
    }
    public static String getSysStaticDataById2String(RedisUtil redisUtil, String codeType, String id) {
        SysStaticData sysStaticData = getSysStaticDataById(redisUtil, codeType, id);
        if (sysStaticData == null) {
            return "";
        }
        return sysStaticData.getCodeName();
    }

    public static String getSysStaticDataById2String(RedisUtil redisUtil, String codeType, Integer id) {
        if (null == id) {
            return "";
        }
        SysStaticData sysStaticData = getSysStaticDataById(redisUtil, codeType, Long.valueOf(id));
        if (sysStaticData == null) {
            return "";
        }
        return sysStaticData.getCodeName();
    }

    public static SysStaticData getSysStaticDataById(RedisUtil redisUtil, String codeType, Long id) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getId().equals(id)) {
                    return sysStaticData;
                }
            }
        }
        return null;
    }

    public static SysStaticData getSysStaticDataById(RedisUtil redisUtil, String codeType, String id) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getId().toString().equals(id)) {
                    return sysStaticData;
                }
            }
        }
        return null;
    }

}
