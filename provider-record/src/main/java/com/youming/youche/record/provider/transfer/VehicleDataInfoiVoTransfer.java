package com.youming.youche.record.provider.transfer;

import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.account.AccountBankRel;
import com.youming.youche.record.provider.utils.ReadisUtil;
import com.youming.youche.record.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.record.vo.VehicleDataInfoiVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.youming.youche.record.common.EnumConsts.SysStaticData.*;

@Component
@RequiredArgsConstructor
public class VehicleDataInfoiVoTransfer {

    private final ReadisUtil readisUtil;

    private final IAccountBankRelService accountBankRelService;

    private final RedisUtil redisUtil;


    public List<VehicleDataInfoiVo> toVehicleDataInfoiVo(List<VehicleDataInfoiVo> vehicleDataInfoiVos, Long tenantIdIn) {
        for (VehicleDataInfoiVo vehicleDataInfoiVo : vehicleDataInfoiVos) {
//            Integer vehicleClassRtn = DataFormat.getIntKey(rtnMap, "vehicleClass");

            Integer licenceTypeRtn = vehicleDataInfoiVo.getLicenceType();
            Long tenantId = vehicleDataInfoiVo.getTenantId();
            if (tenantId == null || tenantId < 0) {
                vehicleDataInfoiVo.setIsSelfVehicle(0);
                vehicleDataInfoiVo.setTenantName("平台合作车");

            } else if (tenantId == tenantIdIn) {
                vehicleDataInfoiVo.setIsSelfVehicle(1);
            } else {
                vehicleDataInfoiVo.setIsSelfVehicle(0);
            }
//            if (vehicleClassRtn > -1) {
//                readisUtil.getSysStaticData(VEHICLE_CLASS,vehicleClassRtn)
//                rtnMap.put("vehicleClassName", SysStaticDataUtil.getSysStaticDataCodeName("VEHICLE_CLASS", "" + vehicleClassRtn));
//            }
            if (licenceTypeRtn > -1) {
                String licenceType = readisUtil.getSysStaticData("LICENCE_TYPE", licenceTypeRtn.toString()).getCodeName();
                vehicleDataInfoiVo.setLicenceTypeName(licenceType);
            }

            Long vehicleStatusRtn = vehicleDataInfoiVo.getVehicleStatus();
            String vehicleLengthRtn = vehicleDataInfoiVo.getVehicleLength();
            if (vehicleStatusRtn != null && vehicleStatusRtn > 0) {
                vehicleDataInfoiVo.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", vehicleDataInfoiVo.getVehicleStatus()));
            }
            if (StringUtils.isNotBlank(vehicleLengthRtn) && !"-1".equals(vehicleLengthRtn)) {
                if (vehicleStatusRtn == 8) {
                    SysStaticData vehicleStatusSubtype = readisUtil.getSysStaticData("VEHICLE_STATUS_SUBTYPE", vehicleDataInfoiVo.getVehicleLength() + "");
                    if (vehicleStatusSubtype != null) {
                        vehicleDataInfoiVo.setVehicleLengthName(vehicleStatusSubtype.getCodeName());
                    }
                } else {
                    vehicleDataInfoiVo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", vehicleDataInfoiVo.getVehicleLength() != null && !vehicleDataInfoiVo.getVehicleLength().equals("") ? vehicleDataInfoiVo.getVehicleLength() : null));
                }
            }
            if (vehicleStatusRtn != null && vehicleStatusRtn > 0 && StringUtils.isNotBlank(vehicleLengthRtn) && !"-1".equals(vehicleLengthRtn)) {
                String vehicleStatusV = vehicleDataInfoiVo.getVehicleStatusName();
                String vehicleLengthV = vehicleDataInfoiVo.getVehicleLengthName();
                vehicleDataInfoiVo.setVehicleInfo(vehicleStatusV + "/" + vehicleLengthV);
            } else if (vehicleStatusRtn != null && vehicleStatusRtn > 0 && (StringUtils.isBlank(vehicleLengthRtn) || "-1".equals(vehicleLengthRtn))) {
                String vehicleStatusV = vehicleDataInfoiVo.getVehicleStatusName();
                vehicleDataInfoiVo.setVehicleInfo(vehicleStatusV);
            } else {
                String vehicleLengthV = vehicleDataInfoiVo.getVehicleLengthName();
                vehicleDataInfoiVo.setVehicleInfo(vehicleLengthV);
            }
            //翻译省市区
            Integer provinceId = vehicleDataInfoiVo.getProvinceId();
            Integer cityId = vehicleDataInfoiVo.getCityId();
            Integer countyId = vehicleDataInfoiVo.getCountyId();
            if (provinceId != null) {
                String provinceName = readisUtil.getSysStaticData(SYS_PROVINCE, provinceId.toString()).getCodeName();
                vehicleDataInfoiVo.setProvinceName(provinceName);
            }
            if (cityId != null) {
                String cityName = readisUtil.getSysStaticData(SYS_CITY, cityId.toString()).getCodeName();
                vehicleDataInfoiVo.setCityName(cityName);
            }
            if (countyId != null) {
                String countyName = readisUtil.getSysStaticData(SYS_DISTRICT, countyId.toString()).getCodeName();
                vehicleDataInfoiVo.setCountyName(countyName);
            }
            vehicleDataInfoiVo.setIsBindCard("未绑定");
            if (vehicleDataInfoiVo.getDriverUserId() != null) {
                if (accountBankRelService.isUserTypeBindCardAll(Long.valueOf(vehicleDataInfoiVo.getDriverUserId()), SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                    vehicleDataInfoiVo.setIsBindCard("已绑定");
                }
            }
            if (vehicleDataInfoiVo.getVehicleClass() != null) {
                String vehicleClassName = readisUtil.getSysStaticData("VEHICLE_CLASS", vehicleDataInfoiVo.getVehicleClass().toString()).getCodeName();
                vehicleDataInfoiVo.setVehicleClassName(vehicleClassName);
            }
        }

        return vehicleDataInfoiVos;
    }
}
