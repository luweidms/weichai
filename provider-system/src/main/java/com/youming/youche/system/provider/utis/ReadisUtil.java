package com.youming.youche.system.provider.utis;

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

    public List<SysStaticData> getSysStaticDataList(String codeType){
        return  (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
    }

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
}
