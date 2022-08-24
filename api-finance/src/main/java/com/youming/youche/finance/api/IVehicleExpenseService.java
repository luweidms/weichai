package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.VehicleExpense;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.dto.VehicleListDto;
import com.youming.youche.finance.vo.CreateVehicleExpenseVo;
import com.youming.youche.finance.vo.GetVehicleExpenseVo;

import java.util.List;

/**
 * <p>
 * 车辆费用表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-23
 */
public interface IVehicleExpenseService extends IBaseService<VehicleExpense> {

	/**
	 * 方法实现说明 保存车辆费用信息
	 *
	 * 车辆费用未通过审核，修改后重新申请
	 * @author terry
	 * @param vehicleExpenseVo * @param accessToken
	 * @return java.lang.Long
	 * @exception
	 * @date 2022/1/23 22:56
	 */
	Long saveOrUpdate(CreateVehicleExpenseVo vehicleExpenseVo, String accessToken);

	/**
	 * 车辆费用查询功能
	 */
	IPage<GetVehicleExpenseVo> getVehicleExpense(GetVehicleExpenseDto dto, String accessToken);

	/**
	 *条件导出车辆费用信息
	 *@author
	 */
	void vehicleExpenseOutList(GetVehicleExpenseDto dto, String accessToken, ImportOrExportRecords record);

	/**
	 * 营运工作台  车辆费用 待我审
	 */
	List<WorkbenchDto> getTableVehicleCostCount();

	/**
	 * 营运工作台  车辆费用 我的
	 */
	List<WorkbenchDto> getTableVehicleCostMeCount();
	/**
	 * 接口编码：21015
	 * app接口-选择车辆
	 * @param
	 * @return
	 * @throws Exception
	 *   2018-08-23  liujl  司机通过APP申请借支，若订单中有拖头，选择车辆时可以选择拖头车牌
	 */
    VehicleListDto getVehicleList(Long userId, Long orderId, String accessToken);

	/**
	 * 车队小程序 首页数字统计 - 车辆费用审核数量
	 */
	Integer getStatisticsCarCost(String accessToken);

}
