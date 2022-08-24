package com.youming.youche.record.business.controller.vehicle;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.trailer.IVehicleDateCostRelService;
import com.youming.youche.record.domain.vehicle.VehicleDateCostRel;
import com.youming.youche.record.dto.trailer.TrailerGuaCarDto;
import com.youming.youche.record.dto.trailer.VehicleDateCostRelDto;
import com.youming.youche.record.dto.trailer.ZcVehicleTrailerDto;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 *
 * @description 整车 牵引车 资产详情
 * @date 2022/1/14 16:49
 */
@RestController
@RequestMapping(value = "vehicle/asset")
public class VehicleDateCostController extends BaseController<VehicleDateCostRel, IVehicleDateCostRelService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleDateCostRel.class);

    @Override
    public IVehicleDateCostRelService getService() {
        return null;
    }

    @DubboReference(version = "1.0.0")
    IVehicleDateCostRelService iVehicleDateCostRelService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    /**
     * 整车牵引车资产查询
     */
    @GetMapping("queryZcjz")
    public ResponseResult queryZcjz() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            List<VehicleDateCostRelDto> vehicleDateCostRelDtos = iVehicleDateCostRelService.queryZcjz(accessToken);
            return ResponseResult.success(vehicleDateCostRelDtos);
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 挂车车资产查询
     */
    @GetMapping("queryGua")
    public ResponseResult queryGua() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            List<TrailerGuaCarDto> guaCarDtoList = iVehicleDateCostRelService.queryGua(accessToken);

            return ResponseResult.success(guaCarDtoList);
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 资产详情
     */
    @GetMapping("assetDetails")
    public ResponseResult queryZcXq() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            List<ZcVehicleTrailerDto> zcVehicleTrailerDtos = iVehicleDateCostRelService.selectAssetDetails(accessToken);
            return ResponseResult.success(zcVehicleTrailerDtos);
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 资产详情导出
     */
    @GetMapping("export")
    public ResponseResult exportList() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("资产详情导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iVehicleDateCostRelService.queryZcXqList(accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败资产详情列表异常" + e);
            return ResponseResult.failure("导出失败资产详情异常");
        }
    }

}
