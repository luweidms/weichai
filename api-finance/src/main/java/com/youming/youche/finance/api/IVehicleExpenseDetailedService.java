package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.VehicleExpenseDetailed;
import com.youming.youche.finance.domain.vehicle.VehicleMiscellaneouFeeDto;
import com.youming.youche.finance.dto.CancelVehicleExpenseDto;
import com.youming.youche.finance.dto.CreateVehicleExpenseDto;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.vo.GetVehicleExpenseVo;
import com.youming.youche.finance.vo.VehicleExpenseVo;

import java.util.List;

/**
* <p>
    * 车辆费用明细表 服务类
    * </p>
* @author liangyan
* @since 2022-04-19
*/
public interface IVehicleExpenseDetailedService extends IBaseService<VehicleExpenseDetailed> {

    /**
     * 方法实现说明 保存车辆费用信息
     *
     * @author liangyan
     * @param domain * @param accessToken
     * @return java.lang.Long
     * @exception
     * @date 2022/4/19 22:56
     */
    String doSaveOrUpdateNew(CreateVehicleExpenseDto domain, String accessToken);

    /**
     * 车辆费用申请详情功能
     */
    VehicleExpenseVo getVehicleExpense(String applyNo, String accessToken);

    /**
     * 车辆费用明细列表功能
     * @param dto
     * @param accessToken
     * @return
     */
    IPage<GetVehicleExpenseVo> getVehicleExpenseDetailedList(GetVehicleExpenseDto dto, String accessToken);

    /**
     * 车辆费用明细列表功能
     * @param dto
     * @param accessToken
     * @return
     */
    Long getSumApplyAmount(GetVehicleExpenseDto dto, String accessToken);

    /**
     *条件导出车辆费用明细
     *@author
     */
    void vehicleExpenseDetailedList(GetVehicleExpenseDto dto, String accessToken, ImportOrExportRecords record);

    /**
     * 方法实现说明 修改车辆费用信息
     *
     * @author liangyan
     * @param domain * @param accessToken
     * @return java.lang.Long
     * @exception
     * @date 2022/4/19 22:56
     */
    void updateVehicleExpenseDetailed(CreateVehicleExpenseDto domain, String accessToken);


    /**
     * 流程结束，车辆费用审核通过的回调方法
     * @param vehicleExpenseId    车辆费用单号
     * @param auditContent      结果的描述
     * @return
     */
    void doPublicVehicleExpense(Long vehicleExpenseId, int authState,String auditContent,String accessToken);

    /**
     * 取消车辆费用审核
     */
    String cancelVehicleExpense (CancelVehicleExpenseDto cancelVehicleExpenseDto , String accessToken);

    /**
     * 车辆多级审核中，修改为审核中
     * @param vehicleExpenseId    车辆费用单号
     * @return
     */
    void doPublicVehicleExpenseState(Long vehicleExpenseId);

    /**
     * 车帘报表获取车联费用数据
     */
    List<VehicleMiscellaneouFeeDto> getVehicleFeeByMonth(String plateNumber, Long tenantId, String month);

}
