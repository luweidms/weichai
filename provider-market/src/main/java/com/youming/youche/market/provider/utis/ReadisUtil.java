package com.youming.youche.market.provider.utis;

import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.util.JsonHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReadisUtil {
    private final RedisUtil redisUtil;

    /***
     * @Description: 获取静态数据
     * @Author: luwei
     * @Date: 2022/8/18 17:06
     * @Param codeType: 业务类型
      * @Param codeValue: 类型值
     * @return: com.youming.youche.commons.domain.SysStaticData
     * @Version: 1.0
     **/
    public SysStaticData getSysStaticData(String codeType, String codeValue){
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

    /***
     * @Description: 获取静态数据集
     * @Author: luwei
     * @Date: 2022/8/18 17:05
     * @Param codeType: 业务类型
     * @return: java.util.List<com.youming.youche.commons.domain.SysStaticData>
     * @Version: 1.0
     **/
    public List<SysStaticData> getSysStaticDataList(String codeType){
        return  (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
    }

    /***
     * @Description: 设置油站地理位置
     * @Author: luwei
     * @Date: 2022/8/18 17:04
     * @Param serviceProduct:
     * @return: void
     * @Version: 1.0
     **/
    public void redisProductAddress(ServiceProduct serviceProduct){
        Map<String, String> inMap = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(serviceProduct.getEand()) && StringUtils.isNotEmpty(serviceProduct.getNand())) {
            //经纬度不能为空
            inMap.put("oilId", serviceProduct.getId() + "");
            inMap.put("oilName", serviceProduct.getProductName());
            inMap.put("eand", serviceProduct.getEand());
            inMap.put("nand", serviceProduct.getNand());
            String address = getSysStaticData(EnumConsts.SysStaticDataAL.SYS_PROVINCE,serviceProduct.getProvinceId().toString()).toString();
            if (serviceProduct.getCityId() != null) {
                address += getSysStaticData(EnumConsts.SysStaticDataAL.SYS_CITY,serviceProduct.getCityId().toString()).getCodeName();
            }
            if (serviceProduct.getCountyId() != null) {
                address += getSysStaticData(EnumConsts.SysStaticDataAL.SYS_DISTRICT,serviceProduct.getCountyId().toString());
            }
            address += serviceProduct.getAddress();
            inMap.put("address", address);
            inMap.put("oilbillId", serviceProduct.getServiceCall());
            inMap.put("introduce", serviceProduct.getIntroduce());
            inMap.put("businessType", serviceProduct.getBusinessType() + "");
        }
        Map<String, String> outMap = new HashMap<String, String>();
        outMap.put(serviceProduct.getId() + "", JsonHelper.toJson(inMap));
        redisUtil.set(EnumConsts.RemoteCache.OIL_LOCATION_INFO,outMap);
    };

    public SysCfg getSysCfg(String cfgName, String cfgSystem){
        SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));

        if (null != sysCfg && (Integer.parseInt(cfgSystem) == -1 || Integer.parseInt(cfgSystem)==(sysCfg.getCfgSystem()))) {
            return sysCfg;
        }

        return new SysCfg();
    }
}
