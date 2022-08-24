package com.youming.youche.record.api.vehicle;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.record.domain.vehicle.VehicleAccident;
import com.youming.youche.record.vo.VehicledentAccidentVo;

/**
 * @author hzx
 * @description 车辆事故
 * @date 2022/1/15 16:13
 */
public interface IVehicleAccidentService extends IBaseService<VehicleAccident> {

    /**
     * 新增事故记录
     */
    Integer insertAccidentRecord(VehicleAccident vehicleAccident, String accessToken);

    /**
     * 新增定损
     */
    Integer insertTheDamageRecord(VehicleAccident vehicleAccident, String accessToken);

    /**
     * 新增核赔
     */
    Integer insertNuclearDamageRecord(VehicleAccident vehicleAccident, String accessToken);

    /**
     * 事故详情
     */
    VehicleAccident selectAccidentById(Long id);

    /**
     * 修改事故记录
     */
    Integer updateAccidentRecord(VehicleAccident vehicleAccident, String accessToken);

    /**
     * 查看事故列表
     */
    Page<VehicleAccident> queryAllRecord(Page<VehicleAccident> objectPage,
                                         String vehicleCode, String accidentStatus,
                                         String reportDateStart, String reportDateEnd,
                                         String accidentDateStart, String accidentDateEnd,
                                         String createDateStart, String createDateEnd,
                                         String accessToken);

    /**
     * 车辆事故记录导出
     *
     * @param vehicleCode       车牌
     * @param accidentStatus    事故状态
     * @param reportDateStart   报案日期-生效日期
     * @param reportDateEnd     报案日期-失效日期
     * @param accidentDateStart 出险日期-生效日期
     * @param accidentDateEnd   出险日期-失效日期
     * @param createDateStart   创建日期-生效日期
     * @param createDateEnd     创建日期-失效日期
     */
    void queryAllRecordExport(String vehicleCode, String accidentStatus,
                              String reportDateStart, String reportDateEnd,
                              String accidentDateStart, String accidentDateEnd,
                              String createDateStart, String createDateEnd,
                              String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 实现功能: 分页查询事故明细表
     *
     * @param monthTime      事故月份
     * @param licenceType    牌照类型 1:整车，2：拖头
     * @param accidentStatus 处理状态 1、已登记、2、已维修、3、已核赔
     */
    Page<VehicledentAccidentVo> getVehicledentAccident(Page<VehicledentAccidentVo> page, String monthTime, Integer licenceType, Integer accidentStatus, String accessToken);

    /**
     * 事故明细导出
     *
     * @param monthTime      事故月份
     * @param licenceType    牌照类型 1:整车，2：拖头
     * @param accidentStatus 处理状态 1、已登记、2、已维修、3、已核赔
     */
    void getVehicledentAccidentExport(String monthTime, Integer licenceType, Integer accidentStatus, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 车辆报表获取车辆事故上个月理赔金额
     */
    Long getVehicleClaimAmountByMonth(Long vehicleCode, Long tenantId, String month);

}
