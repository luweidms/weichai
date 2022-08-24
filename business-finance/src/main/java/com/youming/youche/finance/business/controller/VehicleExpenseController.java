package com.youming.youche.finance.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IVehicleExpenseService;
import com.youming.youche.finance.domain.VehicleExpense;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.dto.VehicleListDto;
import com.youming.youche.finance.vo.GetVehicleExpenseVo;
import com.youming.youche.system.api.ISysExpenseService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 车辆费用表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-23
 */
@RestController
@RequestMapping("/vehicle/expense")
public class VehicleExpenseController extends BaseController<VehicleExpense, IVehicleExpenseService> {

	private  static  final Logger LOGGER = LoggerFactory.getLogger(VehicleExpenseController.class);
	@DubboReference(version = "1.0.0")
	IVehicleExpenseService vehicleExpenseService;
	@Override
	public IVehicleExpenseService getService() {
		return vehicleExpenseService;
	}

	@DubboReference(version = "1.0.0")
	ImportOrExportRecordsService importOrExportRecordsService;


	/**
	 * 车辆费用查询功能
	 * @param domain
	 * @return
	 */
	@GetMapping({ "getVehicleExpense" })
	public ResponseResult getVehicleExpense(GetVehicleExpenseDto domain) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		IPage<GetVehicleExpenseVo> vehicleExpense = vehicleExpenseService.getVehicleExpense(domain, accessToken);

		return ResponseResult.success(vehicleExpense);
	}

	/**
	 * 车辆费用导出功能
	 * @param domain
	 * @return
	 */
	@GetMapping({ "vehicleExpenseExport" })
	public ResponseResult vehicleExpenseExport(GetVehicleExpenseDto domain) {
		try {
			String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
			ImportOrExportRecords record = new ImportOrExportRecords();
			record.setName("车辆费用信息导出");
			record.setBussinessType(2);
			record.setState(1);
			record = importOrExportRecordsService.saveRecords(record, accessToken);
			vehicleExpenseService.vehicleExpenseOutList(domain,accessToken,record);
			return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
		} catch (Exception e) {
			LOGGER.error("车辆费用信息导出异常" + e);
			return ResponseResult.failure("车辆费用信息导出异常");
		}
	}




//	@PostMapping({ "creates" })
//	public ResponseResult create(@Valid @RequestBody CreateVehicleExpenseVo domain) {
//		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//		Long created = vehicleExpenseService.saveOrUpdate(domain, accessToken);
//		if (null == created) {
//			return ResponseResult.failure();
//		}
//		boolean b = resourceBusinessService.insertResource(domain.getResourceIds(), created,
//				BusinessConstant.Finance.COST_REPORT);
//		if (!b) {
//			vehicleExpenseService.remove(created);
//			return ResponseResult.failure();
//		}
//		return ResponseResult.success("创建成功");
//	}
//
//	@PutMapping({ "updates" })
//	public ResponseResult update(@Valid @RequestBody CreateVehicleExpenseVo domain) {
//		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//		Long updated = vehicleExpenseService.saveOrUpdate(domain, accessToken);
//		boolean b = resourceBusinessService.updateResource(domain.getResourceIds(), domain.getId(),
//				BusinessConstant.Finance.COST_REPORT);
//		return b ? ResponseResult.success("编辑成功")
//				: ResponseResult.failure(FinanceResponseCode.ASYN_UPDATE_FAIL.message());
//	}

	// @GetMapping({"get/{Id}"})
//	@Override
//	public ResponseResult get(@PathVariable Long Id) {
//		VehicleExpense vehicleExpense = this.getService().get(Id);
//		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//		List<String> urls = resourceBusinessService.selectUrlByBusinessIdAndBusinessCodeAndAccessToken(Id,
//				BusinessConstant.Finance.COST_REPORT, accessToken);
//		SysExpense sysExpense = sysExpenseService.get(vehicleExpense.getType());
//		VehicleExpenseDto vehicleExpenseDto = VehicleExpenseDto.of();
//		BeanUtil.copyProperties(vehicleExpense, vehicleExpenseDto);
//		vehicleExpenseDto.setUrls(urls);
//		vehicleExpenseDto.setTypeName(sysExpense.getName());
//		return ResponseResult.success(vehicleExpenseDto);
//	}

	/**
	 * 接口编码：21015
	 * app接口-选择车辆
	 * @param
	 * @return
	 * @throws Exception
	 *   2018-08-23  liujl  司机通过APP申请借支，若订单中有拖头，选择车辆时可以选择拖头车牌
	 */
	@PostMapping("getVehicleList")
	public ResponseResult getVehicleList(Long userId,Long orderId) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		VehicleListDto vehicleList = vehicleExpenseService.getVehicleList(userId,orderId,accessToken);
		return ResponseResult.success(vehicleList);
	}


}
