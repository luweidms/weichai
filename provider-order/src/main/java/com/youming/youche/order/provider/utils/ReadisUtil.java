package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public SysStaticData getSysStaticDatabyId(String codeType, String codeValue){
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getId().toString().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }
    public SysStaticData getSysStaticData(Long tenantId,String codeType,String codeValue){
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));

        if (list != null && list.size() > 0) {
            Stream<SysStaticData> stream = list.stream();
            List<SysStaticData> collect = stream.filter(p -> (p.getTenantId().equals(tenantId))).collect(Collectors.toList());
            for (SysStaticData sysStaticData : collect) {
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
    public String getCodeNameStr(List<SysStaticData> list,String codeValue){
        List<SysStaticData> collect = list.stream().filter(a -> a.getCodeValue().equals(codeValue)).collect(Collectors.toList());
        if(collect != null && collect.size() > 0){
           return   collect.get(0).getCodeName();
        }
//        if (list != null && list.size() > 0) {
//            for (SysStaticData sysStaticData : list) {
//                if (sysStaticData.getCodeValue().equals(codeValue)) {
//                    return sysStaticData.getCodeName();
//                }
//            }
//        }
        return  "";
    }
    public SysCfg getSysCfg(String cfgName, String cfgSystem){
    SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));

        if (null != sysCfg && (Integer.parseInt(cfgSystem) == -1 || Integer.parseInt(cfgSystem)==(sysCfg.getCfgSystem()))) {
            return sysCfg;
        }

        return new SysCfg();
    }
}
