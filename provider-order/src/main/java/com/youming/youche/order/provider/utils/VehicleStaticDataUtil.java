package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.SysStaticData;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
public class VehicleStaticDataUtil {
    @Resource
    private ReadisUtil readisUtil;

    public  String getVehicleLengthName(String vehicleStatus, String vehicleLength) {
        String name = "";

        SysStaticData staticData = readisUtil.getSysStaticData("VEHICLE_STATUS", vehicleStatus);
        List<SysStaticData> lengthList = readisUtil.getSysStaticDataList("VEHICLE_LENGTH");
        if (null != staticData && CollectionUtils.isNotEmpty(lengthList)) {
            for (SysStaticData lengthData : lengthList) {
                if (Objects.equals(staticData.getCodeId(),lengthData.getCodeId()) && lengthData.getCodeValue().equals(vehicleLength)) {
                    name = lengthData.getCodeName();
                    break;
                }
            }

        }
        return name;
    }


    public  String getVehicleLengthNameById(String vehicleStatus, String vehicleLength) {
        String name = "";

        SysStaticData staticData = readisUtil.getSysStaticDatabyId("VEHICLE_STATUS", vehicleStatus);
        List<SysStaticData> lengthList = readisUtil.getSysStaticDataList("VEHICLE_LENGTH");
        if (null != staticData && CollectionUtils.isNotEmpty(lengthList)) {
            for (SysStaticData lengthData : lengthList) {
                if (Objects.equals(staticData.getCodeId(),lengthData.getCodeId()) && lengthData.getId().toString().equals(vehicleLength)) {
                    name = lengthData.getCodeName();
                    break;
                }
            }

        }
        return name;
    }
}
