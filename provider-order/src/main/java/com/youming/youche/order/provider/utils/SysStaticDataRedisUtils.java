package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SysStaticDataRedisUtils {


    @Resource
    RedisUtil redisUtil;

    public SysStaticData getSysStaticDataByCodeValue(String codeType, String codeValue) {
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

    public String getSysStaticDataByCodeValue2String(String codeType, String codeValue) {
        SysStaticData sysStaticData = getSysStaticDataByCodeValue(codeType, codeValue);
        if (sysStaticData == null) {
            return "";
        }
        return sysStaticData.getCodeName();
    }

    public String getSysStaticDataById2String(String codeType, Long id) {
        SysStaticData sysStaticData = getSysStaticDataById(codeType, id);
        if (sysStaticData == null) {
            return "";
        }
        return sysStaticData.getCodeName();
    }

    public String getSysStaticDataById2String(String codeType, Integer id) {
        if (null == id) {
            return "";
        }
        SysStaticData sysStaticData = getSysStaticDataById(codeType, Long.valueOf(id));
        if (sysStaticData == null) {
            return "";
        }
        return sysStaticData.getCodeName();
    }

    public SysStaticData getSysStaticDataById(String codeType, Long id) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(id.toString())) {
                    return sysStaticData;
                }
            }
        }
        return null;
    }

}
