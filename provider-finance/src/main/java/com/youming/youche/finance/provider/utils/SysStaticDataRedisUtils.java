package com.youming.youche.finance.provider.utils;

import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.citys.City;
import com.youming.youche.conts.SysStaticDataEnum;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author zengwen
 * @date 2022/4/12 13:49
 */
public class SysStaticDataRedisUtils {

    public static City getCityDataList(RedisUtil redisUtil, String codeType, String codeValue) {
        if (codeType != null && !codeType.equals("") && !codeType.equals("null") && codeValue != null && !codeValue.equals("") && !codeValue.equals("null")) {
            List<City> cityDataList = getCityDataList(redisUtil, codeType);
            if (cityDataList != null && cityDataList.size() > 0) {
                Iterator var3 = cityDataList.iterator();

                while(var3.hasNext()) {
                    City sysData = (City)var3.next();
                    if (sysData.getId() == Long.parseLong(codeValue)) {
                        return sysData;
                    }
                }
            }
        }

        return new City();
    }

    public static List<City> getCityDataList(RedisUtil redisUtil, String codeType) {
        if (StringUtils.isNotEmpty(codeType)) {
            List<SysStaticData> sysStaticDataList = (List) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
            List<City> cityDataList = new ArrayList<>();
            for (SysStaticData sysStaticData : sysStaticDataList) {
                City city = new City();
                city.setId(Long.valueOf(sysStaticData.getCodeValue()));
                city.setName(sysStaticData.getCodeName());
                cityDataList.add(city);
            }
            return cityDataList;
        } else {
            return new ArrayList();
        }
    }

    public static SysStaticData getSysStaticData(RedisUtil redisUtil, Long tenantId, String codeType, String codeValue) {
        List<SysStaticData> staticDataList = getSysStaticDataList(redisUtil, codeType, tenantId);
        if (staticDataList != null && staticDataList.size() > 0) {
            Iterator var3 = staticDataList.iterator();

            while(var3.hasNext()) {
                SysStaticData sysData = (SysStaticData)var3.next();
                if (sysData.getCodeValue().equals(codeValue)) {
                    return sysData;
                }
            }
        }

        return new SysStaticData();
    }

    public static SysStaticData getSysStaticDataId(RedisUtil redisUtil, Long tenantId, String codeType, Long id) {
        List<SysStaticData> staticDataList = getSysStaticDataList(redisUtil, codeType, tenantId);
        if (staticDataList != null && staticDataList.size() > 0) {
            Iterator var3 = staticDataList.iterator();

            while(var3.hasNext()) {
                SysStaticData sysData = (SysStaticData)var3.next();
                if (sysData.getId().equals(id)) {
                    return sysData;
                }
            }
        }

        return new SysStaticData();
    }

    public static List<SysStaticData> getSysStaticDataList(RedisUtil redisUtil, String codeType, Long tenantId) {
        if (StringUtils.isNotEmpty(codeType)) {
            if (Objects.isNull(tenantId)) {
                tenantId = -1L;
            }

            return getSysStaticDataList(redisUtil, tenantId, codeType);
        } else {
            return null;
        }
    }

    public static List<SysStaticData> getSysStaticDataList(RedisUtil redisUtil, long tenantId, String codeType) {
        List<SysStaticData> staticDataList = null;
        if (StringUtils.isNotEmpty(codeType)) {
            staticDataList = (List) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(tenantId + "_" + codeType));
        }

        if (staticDataList == null || staticDataList.isEmpty()) {
            staticDataList = (List) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        }

        return staticDataList;
    }

    public static String getSysStaticDataCodeName(RedisUtil redisUtil, Long tenantId, String codeType, String codeValue) {
        String codeName = "";
        SysStaticData staticData = getSysStaticData(redisUtil, tenantId, codeType, codeValue);
        if (staticData != null) {
            codeName = staticData.getCodeName();
        }

        return codeName;
    }

}
