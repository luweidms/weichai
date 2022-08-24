package com.youming.youche.record.provider.service.impl.vehicle;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.record.api.vehicle.IVehicleObjectLineVerService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.vehicle.VehicleObjectLineVer;
import com.youming.youche.record.dto.VehicleObjectLineDto;
import com.youming.youche.record.provider.mapper.vehicle.VehicleObjectLineVerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车辆心愿线路版本 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class VehicleObjectLineVerServiceImpl extends ServiceImpl<VehicleObjectLineVerMapper, VehicleObjectLineVer> implements IVehicleObjectLineVerService {

    @Resource
    RedisUtil redisUtil;

    @Override
    public List<VehicleObjectLineDto> getVehicleObjectLineVer(long vehicleCode, Long hisId, Integer verState, Integer... flg) {
        LambdaQueryWrapper<VehicleObjectLineVer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VehicleObjectLineVer::getVehicleCode, vehicleCode);
        queryWrapper.eq(VehicleObjectLineVer::getVerState, verState);

        queryWrapper.orderByAsc(VehicleObjectLineVer::getId);
        List<VehicleObjectLineVer> list = this.list(queryWrapper);

        List<VehicleObjectLineDto> listMap = new ArrayList<VehicleObjectLineDto>();

        for (VehicleObjectLineVer vehicleObjectLineVer : list) {
            VehicleObjectLineDto result = new VehicleObjectLineDto();
            BeanUtil.copyProperties(vehicleObjectLineVer, result);

            if (result.getSourceProvince() != null && result.getSourceProvince() > 0) {
                result.setSourceProvinceName(getSysStaticData(EnumConsts.SysStaticDataAL.SYS_PROVINCE, result.getSourceProvince().toString()).getCodeName());
            }
            if (result.getSourceRegion() != null && result.getSourceRegion() > 0) {
                result.setSourceRegionName(getSysStaticData(EnumConsts.SysStaticDataAL.SYS_CITY, result.getSourceRegion().toString()).getCodeName());
            }
            if (result.getSourceCounty() != null && result.getSourceCounty() > 0) {
                result.setSourceCountyName(getSysStaticData(EnumConsts.SysStaticDataAL.SYS_DISTRICT, result.getSourceCounty().toString()).getCodeName());
            }
            if (result.getDesProvince() != null && result.getDesProvince() > 0) {
                result.setDesProvinceName(getSysStaticData(EnumConsts.SysStaticDataAL.SYS_PROVINCE, result.getDesProvince().toString()).getCodeName());
            }
            if (result.getDesRegion() != null && result.getDesRegion() > 0) {
                result.setDesRegionName(getSysStaticData(EnumConsts.SysStaticDataAL.SYS_CITY, result.getDesRegion().toString()).getCodeName());
            }
            if (result.getDesCounty() != null && result.getDesCounty() > 0) {
                result.setDesCountyName(getSysStaticData(EnumConsts.SysStaticDataAL.SYS_DISTRICT, result.getDesCounty().toString()).getCodeName());
            }
            if ((null == flg || flg.length == 0) && result.getCarriagePrice() != null && result.getCarriagePrice() != -1) {
                result.setCarriagePrice(divide(result.getCarriagePrice()));
            }

            listMap.add(result);
        }

        return listMap;
    }

    public static Long divide(long value) {
        if (-1 == value) {
            return 0L;
        }
        BigDecimal bd = new BigDecimal(100);
        return new BigDecimal(value).divide(bd).longValue();
    }

    private SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> staticDataList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        long codeId = Long.valueOf(codeValue);
        for (SysStaticData sysStaticData : staticDataList) {
            if (sysStaticData.getCodeId() == codeId) {
                return sysStaticData;
            }
        }
        return null;
    }

}
