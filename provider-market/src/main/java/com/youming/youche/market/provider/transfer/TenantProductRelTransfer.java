package com.youming.youche.market.provider.transfer;

import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantProductOut;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.vo.facilitator.TenantProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_DISTRICT;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_STATE_DESC;
import static com.youming.youche.record.common.EnumConsts.SysStaticData.SYS_CITY;
import static com.youming.youche.record.common.EnumConsts.SysStaticData.SYS_PROVINCE;
@Component
@RequiredArgsConstructor
public class TenantProductRelTransfer {

    private final ReadisUtil readisUtil;

    public TenantProductVo  ok(TenantProductVo tenantProductOut){
        if(tenantProductOut.getCityId() !=null){
            String cityName = readisUtil.getSysStaticData(SYS_CITY, tenantProductOut.getCityId().toString()).getCodeName();
            tenantProductOut.setCityName(cityName);
        }
        if(tenantProductOut.getCountyId() != null){
            String countyName = readisUtil.getSysStaticData(SYS_DISTRICT, tenantProductOut.getCountyId().toString()).getCodeName();
            tenantProductOut.setCountyName(countyName);
        }
        if(tenantProductOut.getIsBillAbility() != null ){
            if(tenantProductOut.getIsBillAbility() == 1){
                tenantProductOut.setIsBillAbilityName("支持");
            }else if(tenantProductOut.getIsBillAbility() == 2){
                tenantProductOut.setIsBillAbilityName("不支持");
            }
        }
        if(tenantProductOut.getLocationType() != null){
            String locationTypeName = readisUtil.getSysStaticData("LOCATION_TYPE", tenantProductOut.getLocationType().toString()).getCodeName();
            tenantProductOut.setLocationTypeName(locationTypeName);
        }
        if(tenantProductOut.getOilCardType() != null){
            String productCardTypeName = readisUtil.getSysStaticData("PRODUCT_CARD_TYPE", tenantProductOut.getOilCardType().toString()).getCodeName();
            tenantProductOut.setOilCardTypeName(productCardTypeName);
        }
        if(tenantProductOut.getProvinceId() != null){
            String provinceName = readisUtil.getSysStaticData(SYS_PROVINCE, tenantProductOut.getProvinceId().toString()).getCodeName();
            tenantProductOut.setProvinceName(provinceName);
        }
        if(tenantProductOut.getState() != null){
            String stateName = readisUtil.getSysStaticData(SYS_STATE_DESC, tenantProductOut.getState().toString()).getCodeName();
            tenantProductOut.setStateName(stateName);
        }
        return tenantProductOut;
    }

    public TenantProductOut  ok(TenantProductOut tenantProductOut){
        if(tenantProductOut.getCityId() !=null){
            String cityName = readisUtil.getSysStaticData(SYS_CITY, tenantProductOut.getCityId().toString()).getCodeName();
            tenantProductOut.setCityName(cityName);
        }
        if(tenantProductOut.getCountyId() != null){
            String countyName = readisUtil.getSysStaticData(SYS_DISTRICT, tenantProductOut.getCountyId().toString()).getCodeName();
            tenantProductOut.setCountyName(countyName);
        }
        if(tenantProductOut.getIsBillAbility() != null ){
            if(tenantProductOut.getIsBillAbility() == 1){
                tenantProductOut.setIsBillAbilityName("支持");
            }else if(tenantProductOut.getIsBillAbility() == 2){
                tenantProductOut.setIsBillAbilityName("不支持");
            }
        }
        if(tenantProductOut.getLocationType() != null){
            String locationTypeName = readisUtil.getSysStaticData("LOCATION_TYPE", tenantProductOut.getLocationType().toString()).getCodeName();
            tenantProductOut.setLocationTypeName(locationTypeName);
        }
        if(tenantProductOut.getOilCardType() != null){
            String productCardTypeName = readisUtil.getSysStaticData("PRODUCT_CARD_TYPE", tenantProductOut.getOilCardType().toString()).getCodeName();
            tenantProductOut.setOilCardTypeName(productCardTypeName);
        }
        if(tenantProductOut.getProvinceId() != null){
            String provinceName = readisUtil.getSysStaticData(SYS_PROVINCE, tenantProductOut.getProvinceId().toString()).getCodeName();
            tenantProductOut.setProvinceName(provinceName);
        }
        if(tenantProductOut.getState() != null){
            String stateName = readisUtil.getSysStaticData(SYS_STATE_DESC, tenantProductOut.getState().toString()).getCodeName();
            tenantProductOut.setStateName(stateName);
        }
        return tenantProductOut;
    }
}
