package com.youming.youche.finance.api;

import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.ClaimExpenseCategory;
import com.youming.youche.finance.dto.DriverWxDto;

import java.util.List;

/**
 * <p>
 * 车管类目表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
public interface IClaimExpenseCategoryService extends IBaseService<ClaimExpenseCategory> {

	/**
	 * 查询报销类型
	 */
	List<ClaimExpenseCategory> selectAllByCateLevelAndParentCateId(Integer cateLevel, Long parentCateId);

	ClaimExpenseCategory selectOneByCateValue(Long stairCategory);

	/**
	 * 获取车管报销科目
	 * 接口编码：21300
	 * cateLevel：1 一级类目  2 二级类目
	 * parentCateId：一级类目ID
	 * @param
	 * @return
	 * @throws Exception
	 */
    List<ClaimExpenseCategory> queryCategoryByParameter(Long cateLevel, Long parentCateId);

	/**
	 * 查询押金明细
	 */
	PageInfo<DriverWxDto> getAccountDetailsPledge(Long userId, String orderId, String startTime, String endTime, String sourceRegion, String desRegion,
												  String name, String carDriverPhone, String plateNumber, Integer pageSize, Integer pageNum, String accessToken);
	/**
	 * 我的订单-报销-添加修改（21103）
	 * @param
	 * @return Map
	 * @throws Exception
	 */
	String saveOrUpdateExpenseDriver(Long expenseId, String amountString, Long stairCategory,
								   String appReason, String weightFee, Integer type, String orderId, String plateNumber,
								   String carOwnerName, String carPhone, String imgIds, Integer isNeedBill,String accessToken);

}
