package com.youming.youche.finance.business.controller;

import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IClaimExpenseCategoryService;
import com.youming.youche.finance.domain.ClaimExpenseCategory;
import com.youming.youche.finance.dto.DriverWxDto;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.api.ISysOrganizeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 车管类目表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@RestController
@RequestMapping("claim/expense/category")
public class ClaimExpenseCategoryController extends BaseController<ClaimExpenseCategory, IClaimExpenseCategoryService> {

	@DubboReference(version = "1.0.0")
	IClaimExpenseCategoryService claimExpenseCategoryService;

	@DubboReference(version = "1.0.0")
	ISysMenuBtnService sysMenuBtnService;
	@DubboReference(version = "1.0.0")
	ISysOrganizeService sysOrganizeService;
	@Override
	public IClaimExpenseCategoryService getService() {
		return claimExpenseCategoryService;
	}

	/**
	 * 查询报销类型
	 */
	@GetMapping({ "get" })
	public ResponseResult gets(
			@RequestParam(value = "cateLevel") Integer cateLevel,
			@RequestParam(value = "parentCateId", defaultValue = "-1") Long parentCateId) {
		List<ClaimExpenseCategory> domain = claimExpenseCategoryService.selectAllByCateLevelAndParentCateId(cateLevel,
				parentCateId);
		return ResponseResult.success(domain);
	}
	/**
	 * 获取车管报销科目
	 * 接口编码：21300
	 * cateLevel：1 一级类目  2 二级类目
	 * parentCateId：一级类目ID
	 * @param
	 * @return
	 * @throws Exception
	 */
	@GetMapping("queryCategoryByParameter")
	public ResponseResult queryCategoryByParameter(Long cateLevel,Long parentCateId){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		List<ClaimExpenseCategory> list =  claimExpenseCategoryService.queryCategoryByParameter(cateLevel,parentCateId);
		return ResponseResult.success(list);
	}

	/**
	 * 21202  押金明细
	 */
	@GetMapping("getAccountDetailsPledge")
	public ResponseResult getAccountDetailsPledge(Long userId,String orderId,String startTime,String endTime,
										String sourceRegion,String desRegion,String name,String carDriverPhone,String plateNumber,
										Integer PageSize,Integer PageNum){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		PageInfo<DriverWxDto> driverWxDto = claimExpenseCategoryService.getAccountDetailsPledge(userId,orderId,startTime,endTime,sourceRegion,desRegion,
				name,carDriverPhone,plateNumber,PageSize,PageNum,accessToken);
		return ResponseResult.success(driverWxDto);
	}
	/**
	 * 我的订单-报销-添加修改（21103）
	 * @param
	 * @return Map
	 * @throws Exception
	 */
	@PostMapping("saveOrUpdateExpenseDriver")
	public ResponseResult saveOrUpdateExpenseDriver(Long expenseId, String amountString, Long stairCategory,
													String appReason, String weightFee, Integer type, String orderId,
													String plateNumber, String carOwnerName, String carPhone, String imgIds, Integer isNeedBill){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		String s = claimExpenseCategoryService.saveOrUpdateExpenseDriver(expenseId,amountString,stairCategory,appReason,weightFee,type,orderId,
				plateNumber,carOwnerName,carPhone,imgIds,isNeedBill,accessToken);

		return ResponseResult.success(s);
	}
}
