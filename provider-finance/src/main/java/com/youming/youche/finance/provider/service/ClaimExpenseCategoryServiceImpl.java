package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.finance.api.IClaimExpenseCategoryService;
import com.youming.youche.finance.api.IClaimExpenseInfoService;
import com.youming.youche.finance.domain.ClaimExpenseCategory;
import com.youming.youche.finance.dto.ClaimExpenseInfoInDto;
import com.youming.youche.finance.dto.DriverWxDto;
import com.youming.youche.finance.provider.mapper.ClaimExpenseCategoryMapper;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 车管类目表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@DubboService(version = "1.0.0")
public class ClaimExpenseCategoryServiceImpl extends BaseServiceImpl<ClaimExpenseCategoryMapper, ClaimExpenseCategory>
		implements IClaimExpenseCategoryService {

	@Resource
	private LoginUtils loginUtils;

	@DubboReference(version = "1.0.0")
	ISysTenantDefService sysTenantDefService;
	@DubboReference(version = "1.0.0")
	IOilCardManagementService oilCardManagementService;
	@DubboReference(version = "1.0.0")
	IOrderInfoService orderInfoService;
	@DubboReference(version = "1.0.0")
	IOrderInfoHService orderInfoHService;
	@Resource
	IClaimExpenseInfoService iClaimExpenseInfoService;


	@Override
	public List<ClaimExpenseCategory> selectAllByCateLevelAndParentCateId(Integer cateLevel, Long parentCateId) {
		LambdaQueryWrapper<ClaimExpenseCategory> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ClaimExpenseCategory::getCateLevel, cateLevel);
		if (parentCateId != -1) {
			wrapper.eq(ClaimExpenseCategory::getParentCateId, parentCateId);
		}
		return baseMapper.selectList(wrapper);
	}

	@Override
	public ClaimExpenseCategory selectOneByCateValue(Long stairCategory) {
		LambdaQueryWrapper<ClaimExpenseCategory> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(ClaimExpenseCategory::getCateValue, stairCategory);
		return baseMapper.selectOne(wrapper);
	}

	@Override
	public List<ClaimExpenseCategory> queryCategoryByParameter(Long cateLevel, Long parentCateId) {
		List<ClaimExpenseCategory> list = null;
		QueryWrapper<ClaimExpenseCategory> queryWrapper = new QueryWrapper<>();
		if (cateLevel != null && cateLevel > 0) {
			queryWrapper.eq("cate_level",cateLevel);
		}
		if (parentCateId != null && parentCateId > 0) {
			queryWrapper.eq("parent_cate_id", parentCateId);
		}
		list = this.list(queryWrapper);
		return list;
	}

	@Override
	public PageInfo<DriverWxDto> getAccountDetailsPledge(Long userId, String orderId, String startTime, String endTime, String sourceRegion, String desRegion, String name, String carDriverPhone, String plateNumber, Integer pageSize, Integer pageNum, String accessToken) {
		// 通过租户id，找到租户用户id
		LoginInfo user = loginUtils.get(accessToken);
		Long tenantUserId = null;
		SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(user.getTenantId());
		if(sysTenantDef != null){
			tenantUserId = sysTenantDef.getAdminUser();
		}else {
			tenantUserId = user.getId();
		}
		PageHelper.startPage(pageNum, pageSize);
		List<DriverWxDto> driverWxDtoPage = baseMapper.selectOr(userId,  orderId,  startTime,  endTime,  sourceRegion,  desRegion,  name,  carDriverPhone,  plateNumber,user.getTenantId(),tenantUserId);
		PageInfo<DriverWxDto> customerInfoPageInfo = new PageInfo<>(driverWxDtoPage);
		if(driverWxDtoPage!=null&& driverWxDtoPage.size()>0){
			for(DriverWxDto driverWxDto:driverWxDtoPage){
				String curOrderId = driverWxDto.getOrderId()+"";
				List<OilCardManagement> oilCardList = oilCardManagementService.getOilCardByOrderId(Long.parseLong(curOrderId),user.getTenantId());
				driverWxDto.setOilCardList(oilCardList);
			}
		}
		return customerInfoPageInfo;

	}

	@Override
	public String saveOrUpdateExpenseDriver(Long expenseId, String amountString, Long stairCategory, String appReason, String weightFee, Integer type, String orderId, String plateNumber, String carOwnerName, String carPhone, String imgIds, Integer isNeedBill,String accessToken) {
		LoginInfo user = loginUtils.get(accessToken);

		Long tenantId = -1L;

		if(org.apache.commons.lang3.StringUtils.isBlank(orderId)){
			throw new BusinessException("入参错误，订单号为空！");
		}

		OrderInfo orderInfo = orderInfoService.getOrder(Long.parseLong(orderId));
		if(orderInfo!=null){
			tenantId = orderInfo.getTenantId();
		}else {
			OrderInfoH orderInfoH = orderInfoHService.getOrderH(Long.parseLong(orderId));
			if(orderInfoH!=null){
				tenantId = orderInfoH.getTenantId();
			}
		}

		ClaimExpenseInfoInDto infoIn = new ClaimExpenseInfoInDto();
		infoIn.setIsNeedBill(isNeedBill);
		infoIn.setExpenseId(expenseId);
		infoIn.setAmountString(amountString);
		infoIn.setStairCategory(stairCategory);
		infoIn.setAppReason(appReason);
		infoIn.setPlateNumber(plateNumber);
		infoIn.setOrderId(orderId);
		infoIn.setCarPhone(carPhone);
		infoIn.setCarOwnerName(carOwnerName);
		infoIn.setAccUserId(user.getUserInfoId());
		Double weightFeeDouble = StringUtils.isBlank(weightFee)?0L:(Double.parseDouble(weightFee)*1000);
		infoIn.setWeightFee(weightFeeDouble.longValue());
		infoIn.setType(type);
		infoIn.setTenantId(tenantId);
		infoIn.setImgIds(imgIds);
		String info = iClaimExpenseInfoService.saveOrUpdateExpenseDriver(infoIn,accessToken);
		return  info;
	}
}
