package com.youming.youche.system.provider.utis;

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

    /***
     * @Description: 查询城市静态数据
     * @Author: luwei
     * @Date: 2022/8/18 17:09
     * @Param redisUtil: bean
      * @Param codeType: 业务类型
      * @Param codeValue: 类型值
     * @return: com.youming.youche.components.citys.City 城市bean
     * @Version: 1.0
     **/
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

    /***
     * @Description: 查询城市静态数据集
     * @Author: luwei
     * @Date: 2022/8/18 17:10
     * @Param redisUtil: bean
      * @Param codeType:  业务类型
     * @return: java.util.List<com.youming.youche.components.citys.City> 城市bean
     * @Version: 1.0
     **/
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

    /***
     * @Description: 查询城市静态数据
     * @Author: luwei
     * @Date: 2022/8/18 17:10
     * @Param redisUtil: bean
     * @Param tenantId:  租户id
     * @Param codeType:  业务类型
     * @Param codeValue: 类型值
     * @return: com.youming.youche.commons.domain.SysStaticData 静态数据bean
     * @Version: 1.0
     **/
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

    /***
     * @Description: 获取静态数据集合
     * @Author: luwei
     * @Date: 2022/8/18 17:18
     * @Param redisUtil: bean
      * @Param codeType: 业务类型
      * @Param tenantId: 租户id
     * @return: java.util.List<com.youming.youche.commons.domain.SysStaticData> 静态bean
     * @Version: 1.0
     **/
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

    /***
     * @Description: 获取静态数据集合
     * @Author: luwei
     * @Date: 2022/8/18 17:18
     * @Param redisUtil: bean
      * @Param tenantId: 租户id
      * @Param codeType: 业务类型
     * @return: java.util.List<com.youming.youche.commons.domain.SysStaticData> 静态数据bean
     * @Version: 1.0
     **/
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

    /***
     * @Description: 获取静态数据名称
     * @Author: luwei
     * @Date: 2022/8/18 17:19
     * @Param redisUtil: bean
      * @Param tenantId: 租户id
      * @Param codeType: 业务类型
      * @Param codeValue: 类型值
     * @return: java.lang.String 静态数据名称
     * @Version: 1.0
     **/
    public static String getSysStaticDataCodeName(RedisUtil redisUtil, Long tenantId, String codeType, String codeValue) {
        String codeName = "";
        SysStaticData staticData = getSysStaticData(redisUtil, tenantId, codeType, codeValue);
        if (staticData != null) {
            codeName = staticData.getCodeName();
        }

        return codeName;
    }

    /***
     * @Description: 获取静态数据
     * @Author: luwei
     * @Date: 2022/8/18 17:20
     * @Param redisUtil: bean
      * @Param codeType: 业务类型
      * @Param codeValue: 类型值
     * @return: com.youming.youche.commons.domain.SysStaticData 静态bean
     * @Version: 1.0
     **/
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

}
