package com.youming.youche.record.api.trailer;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.record.domain.vehicle.VehicleDateCostRel;
import com.youming.youche.record.dto.trailer.TrailerGuaCarDto;
import com.youming.youche.record.dto.trailer.VehicleDateCostRelDto;
import com.youming.youche.record.dto.trailer.ZcVehicleTrailerDto;

import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @since 2022-01-24
 */
public interface IVehicleDateCostRelService extends IBaseService<VehicleDateCostRel> {

    /**
     * 整车牵引车资产查询
     */
    List<VehicleDateCostRelDto> queryZcjz(String accessToken) throws ParseException;

    /**
     * 挂车车资产查询
     */
    List<TrailerGuaCarDto> queryGua(String accessToken) throws ParseException;

    List<ZcVehicleTrailerDto> queryZcXq();

    /**
     * 资产详情导出
     */
    void queryZcXqList(String accessToken, ImportOrExportRecords record);

    /**
     * 资产详情
     */
    List<ZcVehicleTrailerDto> selectAssetDetails(String accessToken);
}
