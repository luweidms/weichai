package com.youming.youche.finance.business.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IClaimExpenseInfoService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.constant.ClaimExpenseConsts;
import com.youming.youche.finance.domain.ClaimExpenseInfo;
import com.youming.youche.finance.dto.ClaimExpenseCategoryDto;
import com.youming.youche.finance.dto.ClaimExpenseInfoDto;
import com.youming.youche.finance.dto.QueryClaimExpenseInfoDto;
import com.youming.youche.finance.dto.UserDataInfoDto;
import com.youming.youche.finance.vo.ClaimExpenseInfoVo;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysMenuBtn;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 车管报销表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@RestController
@RequestMapping("claim/expense/info")
public class ClaimExpenseInfoController extends BaseController<ClaimExpenseInfo, IClaimExpenseInfoService> {

	private static final Logger LOGGER = LoggerFactory.getLogger(OaLoanController.class);

	@DubboReference(version = "1.0.0")
	IClaimExpenseInfoService claimExpenseInfoService;

	@DubboReference(version = "1.0.0")
	IUserDataInfoService userDataInfoService;

	@DubboReference(version = "1.0.0")
	ISysMenuBtnService sysMenuBtnService;

	@DubboReference(version = "1.0.0")
	ISysOrganizeService sysOrganizeService;

	@DubboReference(version = "1.0.0")
	IOrderInfoHService iOrderInfoHService;

	@DubboReference(version = "1.0.0")
	IOrderInfoService orderInfoService;

	@DubboReference(version = "1.0.0")
	ImportOrExportRecordsService importOrExportRecordsService;
	@Override
	public IClaimExpenseInfoService getService() {
		return claimExpenseInfoService;
	}


	/**
	 * 管理费用列表（车管 和司机）
	 * @param expenseType
	 * @param specialExpenseNum
	 * @param stairCategory
	 * @param secondLevelCategory
	 * @param expenseSts
	 * @param userName
	 * @param userPhone
	 * @param plateNumber
	 * @param startTime
	 * @param endTime
	 * @param flowId
	 * @param orderId
	 * @param oneOrgId
	 * @param twoOrgId
	 * @param threeOrgId
	 * @param stairCategoryS 小程序
	 * @param secondLevelCategoryS 小程序
	 * expenseStsS 小程序
	 * @param waitDeal
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/doQuery")
	public  ResponseResult doQuery(@RequestParam(value = "expenseType", required = false) Integer expenseType,
								   @RequestParam(value = "specialExpenseNum", required = false) String specialExpenseNum,
								   @RequestParam(value = "stairCategory", required = false) String stairCategory,
								   @RequestParam(value = "secondLevelCategory", required = false) String secondLevelCategory,
								   @RequestParam(value = "expenseSts", required = false) Integer expenseSts,
								   @RequestParam(value = "userName", required = false) String userName,
								   @RequestParam(value = "userPhone", required = false) String userPhone,
								   @RequestParam(value = "plateNumber", required = false) String plateNumber,
								   @RequestParam(value = "startTime", required = false) String startTime,
								   @RequestParam(value = "endTime", required = false) String endTime,
								   @RequestParam(value = "flowId", required = false) String flowId,
								   @RequestParam(value = "orderId", required = false) String orderId,
								   @RequestParam(value = "oneOrgId", required = false) String oneOrgId,
								   @RequestParam(value = "twoOrgId", required = false) String twoOrgId,
								   @RequestParam(value = "threeOrgId", required = false) String threeOrgId,
								   @RequestParam(value = "stairCategoryS", required = false) String stairCategoryS,
								   @RequestParam(value = "secondLevelCategoryS", required = false) String secondLevelCategoryS,
								   @RequestParam(value = "waitDeal",required = true, defaultValue = "false") Boolean waitDeal,
								   @RequestParam(value = "expenseStsS" ,required = false) String expenseStsS,
								   @RequestParam(value = "userId", required = false) String userId,
								   @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
								   @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);
		List<Long> dataPermissionIds = Lists.newArrayList();
		for (SysMenuBtn sysMenuBtn : sysMenuBtns) {
			dataPermissionIds.add(sysMenuBtn.getId());
		}

		List<String> stairCategoryList = new ArrayList<>();
		List<String> secondLevelCategoryList = new ArrayList<>();
		List<String> expenseStsList = new ArrayList<>();
		if (stairCategoryS == null || StringUtils.isBlank(stairCategoryS)) {
			stairCategoryList = null;
		} else {
			stairCategoryList = Arrays.asList(stairCategoryS.split(","));
		}
		if (secondLevelCategoryS == null || StringUtils.isBlank(secondLevelCategoryS)) {
			secondLevelCategoryList = null;
		} else {
			secondLevelCategoryList = Arrays.asList(secondLevelCategoryS.split(","));
		}
		if (expenseStsS == null || StringUtils.isBlank(expenseStsS)) {
			expenseStsList = null;
		} else {
			expenseStsList = Arrays.asList(expenseStsS.split(","));
		}
		IPage<ClaimExpenseCategoryDto> claimExpenseCategoryDtoIPage = claimExpenseInfoService.doQuery(expenseType,
				specialExpenseNum, stairCategory, secondLevelCategory, expenseSts,
				userName, userPhone, plateNumber, startTime, endTime, flowId, orderId, oneOrgId, twoOrgId, threeOrgId,
				stairCategoryList, secondLevelCategoryList, waitDeal, dataPermissionIds, pageNum, pageSize,accessToken, expenseStsList,userId);

		return ResponseResult.success(claimExpenseCategoryDtoIPage);
	}


	/**
	 * 查询报销详情
	 * @param expenseId
	 * @param busiCode 业务编码
	 * @return
	 */
	@GetMapping("/queryClaimExpenseById")
	public ResponseResult 	queryClaimExpenseById(@RequestParam(value = "expenseId") Long expenseId,
												   @RequestParam(value = "busiCode") String busiCode){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		ClaimExpenseInfo dto = claimExpenseInfoService.queryClaimExpenseById(expenseId, accessToken, busiCode);
		return ResponseResult.success(dto);
	}

	/**
	 * 新增费用
	 */
	@PostMapping({ "creates" })
//	@SysOperatorSaveLog(code = SysOperLogConst.BusiCode.TubeExpense, type = SysOperLogConst.OperType.Add,
//			comment = "新增报销申请")
	public ResponseResult create(@Valid @RequestBody ClaimExpenseInfoVo infoIn) {
		Long amount = Long.valueOf(infoIn.getAmount());
		Long insuranceMoney = Long.valueOf(infoIn.getInsuranceMoneyString());
		UserDataInfo userDataInfo = createOrUpdateVaile(infoIn, amount, insuranceMoney);
		UserDataInfoDto userDataInfoDto = UserDataInfoDto.of();
		BeanUtil.copyProperties(userDataInfo, userDataInfoDto);
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		// 如果有选择订单，则借支的归属部门为订单的归属部门；如果借支没有选择订单，则借支的归属部门为操作员的归属部门。
		Long created;
		if (StringUtils.isNotBlank(infoIn.getOrderId())) {
			// TODO: 2022/1/27 查询订单id，将订单的部门id和车队id 赋值给infoIn
			/*
			 * long orderId = Long.parseLong(infoIn.getOrderId()); IOrderInfoSV
			 * orderInfoSV = (IOrderInfoSV) SysContexts.getBean("orderInfoSV");
			 *  long tenantId = -1;
			 *   long orgId = -1;
			 *  OrderInfo orderInfo =orderInfoSV.getOrder(orderId);
			 * if (orderInfo == null) {
			 * OrderInfoH orderInfoH = orderInfoSV.getOrderH(orderId);
			 * if (orderInfoH == null) {
			 * throw new BusinessException("找不到订单信息"); } tenantId =
			 * orderInfoH.getTenantId(); orgId = orderInfoH.getOrgId(); }else{ tenantId =
			 * orderInfo.getTenantId(); orgId = orderInfo.getOrgId(); }
			 * expenseInfo.setTenantId(tenantId); expenseInfo.setOrgId(orgId);
			 */
			Long tenantId = -1L;
			Long orgId = -1L;
			OrderInfo orderInfo = orderInfoService.getOrder(Long.valueOf(infoIn.getOrderId()));
			  if (orderInfo == null) {
			  OrderInfoH orderInfoH = iOrderInfoHService.getOrderH(Long.valueOf(infoIn.getOrderId()));
			  if (orderInfoH == null) {
			  throw new BusinessException("找不到订单信息");
			  }
			  tenantId = orderInfoH.getTenantId();
			  orgId = orderInfoH.getOrgId().longValue();
			  }else{
			  	tenantId = orderInfo.getTenantId();
			  	orgId = orderInfo.getOrgId().longValue();
			  }
			infoIn.setTenantId(tenantId);
			infoIn.setOrgId(orgId);
			created = claimExpenseInfoService.create(infoIn, userDataInfoDto, amount, insuranceMoney, accessToken,
					true);
		}
		else {
			created = claimExpenseInfoService.create(infoIn, userDataInfoDto, amount, insuranceMoney, accessToken,
					false);
		}
		return created != -1 ? ResponseResult.success("管理费用新增成功") : ResponseResult.failure("管理费用新增失败");
	}

	/**
	 * 修改费用
	 */
	@PostMapping({ "updates" })
	public ResponseResult update(@Valid @RequestBody ClaimExpenseInfoVo infoIn) {
		Long amount = Long.valueOf(infoIn.getAmount());
		Long insuranceMoney = Long.valueOf(infoIn.getInsuranceMoneyString());
		UserDataInfo userDataInfo = createOrUpdateVaile(infoIn, amount, insuranceMoney);
		if (infoIn.getState() == ClaimExpenseConsts.EXPENSE_STS.UNDER_AUDIT) {
			throw new BusinessException("报销审核中,不予修改");
		}
		if (infoIn.getState() == ClaimExpenseConsts.EXPENSE_STS.AUDIT_PASS) {
			throw new BusinessException("该报销已完成,不予修改");
		}
		if (infoIn.getState() == ClaimExpenseConsts.EXPENSE_STS.CANCEL) {
			throw new BusinessException("该报销信息已撤销,不予修改");
		}

		UserDataInfoDto userDataInfoDto = UserDataInfoDto.of();
		BeanUtil.copyProperties(userDataInfo, userDataInfoDto);

		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

		// 如果有选择订单，则借支的归属部门为订单的归属部门；如果借支没有选择订单，则借支的归属部门为操作员的归属部门。
		Long updated;
		if (StringUtils.isNotBlank(infoIn.getOrderId())) {
			updated = claimExpenseInfoService.update(infoIn, userDataInfoDto, amount, insuranceMoney, accessToken,
					true);
		}
		else {
			updated = claimExpenseInfoService.update(infoIn, userDataInfoDto, amount, insuranceMoney, accessToken,
					false);
		}
		return updated != -1 ? ResponseResult.success("管理费用修改成功") : ResponseResult.failure("管理费用修改失败");
	}


	private UserDataInfo createOrUpdateVaile(@RequestBody @Valid ClaimExpenseInfoVo infoIn, Long amount,
			Long insuranceMoney) {
		if (infoIn.getStairCategory() <= 0) {
			throw new BusinessException("亲，一级类目不能为空！");
		}
		if (infoIn.getSecondLevelCategory() <= 0) {
			throw new BusinessException("亲，二级类目不能为空！");
		}
		if (StringUtils.isEmpty(infoIn.getAmount())) {
			throw new BusinessException("亲，申请金额不能为空！");
		}
		if (StringUtils.isEmpty(infoIn.getPlateNumber())) {
			throw new BusinessException("亲，请输入车牌号！");
		}
		if (infoIn.getStairCategory() == 4 && infoIn.getSecondLevelCategory() == 14 && infoIn.getWeightFee() <= 0) {// 运营费
																													// 过磅费
			throw new BusinessException("亲，请输入过磅重量！");
		}
		if (infoIn.getStairCategory() == 3 && StringUtils.isBlank(infoIn.getInsuranceEffectiveDate())) {// 保险
			throw new BusinessException("亲，请输入保险生效时间！");
		}
		if (StringUtils.isEmpty(infoIn.getAppReason())) {
			throw new BusinessException("亲，报销原因不能为空！");
		}
		if (infoIn.getStairCategory() == 4 && infoIn.getSecondLevelCategory() == 22) {// 运营费
																						// 出险预报
			if (StringUtils.isEmpty(infoIn.getAccidentDate())) {
				throw new BusinessException("亲，事故时间不能为空！");
			}
			if (StringUtils.isEmpty(infoIn.getInsuranceDate())) {
				throw new BusinessException("亲，出险时间不能为空！");
			}
			if (infoIn.getAccidentType() <= 0) {
				throw new BusinessException("亲，事故类型不能为空！");
			}
			if (infoIn.getAccidentReason() <= 0) {
				throw new BusinessException("亲，事故原因不能为空！");
			}
			if (infoIn.getDutyDivide() <= 0) {
				throw new BusinessException("亲，责任划分不能为空！");
			}
			if (infoIn.getAccidentDivide() <= 0) {
				throw new BusinessException("亲，事故司机不能为空！");
			}
			if (StringUtils.isEmpty(infoIn.getInsuranceFirm())) {
				throw new BusinessException("亲，保险公司不能为空！");
			}
			if (StringUtils.isEmpty(infoIn.getReportNumber())) {
				throw new BusinessException("亲，报案号不能为空！");
			}
		}

		if (infoIn.getIsNeedBill() < 0) {
			throw new BusinessException("亲，请选择是否需要票据！");
		}

		if (CommonUtil.isNumber(infoIn.getAmount())) {
			Double convertNum = Double.parseDouble(infoIn.getAmount()) * 100;
			amount = convertNum.longValue();
		}
		else {
			throw new BusinessException("亲，请填写正确的申请金额！");
		}
		if (amount <= 0) {
			throw new BusinessException("亲，申请金额必须大于0！");
		}
		if (!StringUtils.isEmpty(infoIn.getInsuranceMoneyString())) {
			if (CommonUtil.isNumber(infoIn.getInsuranceMoneyString())) {
				Double convertNum = Double.parseDouble(infoIn.getInsuranceMoneyString()) * 100;
				insuranceMoney = convertNum.longValue();
			}
			else {
				throw new BusinessException("亲，请填写正确的理赔金额！");
			}
		}

		if (infoIn.getAccUserId() <= 0) {
			throw new BusinessException("找不到收款用户信息");
		}
		UserDataInfo userDataInfo = userDataInfoService.getById(infoIn.getAccUserId());
		if (userDataInfo == null) {
			throw new BusinessException("找不到收款用户信息");
		}
		return userDataInfo;
	}


	/**
	 * 取消报销申请
	 * expenseId 主键
	 * @return
	 */
	@PostMapping("/cancelClaimExpense" )
	public ResponseResult cancelClaimExpense(@Valid @RequestBody ClaimExpenseInfoVo infoIn){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		claimExpenseInfoService.cancelClaimExpense(infoIn.getExpenseId(),accessToken);
		return  ResponseResult.success("取消成功");
	}
	/**
	 * 取消报销申请
	 * expenseId 主键
	 * @return
	 */
	@PostMapping("/cancelClaimExpenses" )
	public ResponseResult cancelClaimExpenses(Long expenseId){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		String s = claimExpenseInfoService.cancelClaimExpenses(expenseId,accessToken);
		return  ResponseResult.success(s);
	}

	/**
	 * 审核报销信息
	 * @param infoIn
	 * @return
	 */
	@PostMapping("/examineClaimExpense")
	public ResponseResult examineClaimExpense(@Valid @RequestBody ClaimExpenseInfoVo infoIn){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		 claimExpenseInfoService.examineClaimExpense(infoIn, accessToken);
		return ResponseResult.success();
	}

	// 费用管理 报销导出
	@GetMapping("/excelReport")
	public  ResponseResult 	excelReport	(@RequestParam(value = "expenseType", required = false) Integer expenseType,
											  @RequestParam(value = "specialExpenseNum", required = false) String specialExpenseNum,
											  @RequestParam(value = "stairCategory", required = false) String stairCategory,
											  @RequestParam(value = "secondLevelCategory", required = false) String secondLevelCategory,
											  @RequestParam(value = "expenseSts", required = false) Integer expenseSts,
											  @RequestParam(value = "userName", required = false) String userName,
											  @RequestParam(value = "userPhone", required = false) String userPhone,
											  @RequestParam(value = "plateNumber", required = false) String plateNumber,
											  @RequestParam(value = "startTime", required = false) String startTime,
											  @RequestParam(value = "endTime", required = false) String endTime,
											  @RequestParam(value = "flowId", required = false) String flowId,
											  @RequestParam(value = "orderId", required = false) String orderId,
											  @RequestParam(value = "oneOrgId", required = false) String oneOrgId,
											  @RequestParam(value = "twoOrgId", required = false) String twoOrgId,
											  @RequestParam(value = "threeOrgId", required = false) String threeOrgId,
											  @RequestParam(value = "stairCategoryS", required = false) String stairCategoryS,
											  @RequestParam(value = "secondLevelCategoryS", required = false) String secondLevelCategoryS,
											  @RequestParam(value = "waitDeal",required = true, defaultValue = "false") Boolean waitDeal,
											  @RequestParam(value = "expenseStsS" ,required = false) String expenseStsS,
											  @RequestParam(value = "userId", required = false) String userId,
											  @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
											  @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		List<SysMenuBtn> sysMenuBtns = sysMenuBtnService.selectAllByUserIdAndData(accessToken);
		List<Long> dataPermissionIds = Lists.newArrayList();
		for (SysMenuBtn sysMenuBtn : sysMenuBtns) {
			dataPermissionIds.add(sysMenuBtn.getId());
		}
		List<String> stairCategoryList = new ArrayList<>();
		List<String> secondLevelCategoryList = new ArrayList<>();
		List<String> expenseStsList = new ArrayList<>();
		if (stairCategoryS == null || StringUtils.isBlank(stairCategoryS)) {
			stairCategoryList = null;
		} else {
			stairCategoryList = Arrays.asList(stairCategoryS.split(","));
		}
		if (secondLevelCategoryS == null || StringUtils.isBlank(secondLevelCategoryS)) {
			secondLevelCategoryList = null;
		} else {
			secondLevelCategoryList = Arrays.asList(secondLevelCategoryS.split(","));
		}
		if (expenseStsS == null || StringUtils.isBlank(expenseStsS)) {
			expenseStsList = null;
		} else {
			expenseStsList = Arrays.asList(expenseStsS.split(","));
		}
		try {
			ImportOrExportRecords record = new ImportOrExportRecords();
			record.setName("费用管理列表导出");
			record.setBussinessType(2);
			record.setState(1);
			record = importOrExportRecordsService.saveRecords(record, accessToken);
			claimExpenseInfoService.excelExport(expenseType,
					specialExpenseNum, stairCategory, secondLevelCategory, expenseSts,
					userName, userPhone, plateNumber, startTime, endTime, flowId, orderId, oneOrgId, twoOrgId, threeOrgId,
					stairCategoryList, secondLevelCategoryList, waitDeal, dataPermissionIds, pageNum, pageSize,accessToken, expenseStsList,userId,record);
			return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
		}catch (Exception e){
			LOGGER.error("导出费用管理列表异常" + e);
			return ResponseResult.failure("导出费用管理列表异常");
		}
	}

	/**
	 * 报销审核-报销列表查询
	 * 接口编码：21110
	 */
	@GetMapping("queryClaimExpense")
	public ResponseResult queryClaimExpense(ClaimExpenseInfoVo vo,
											@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
											@RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

		Page<ClaimExpenseInfoDto> claimExpenseInfoDtoPage = claimExpenseInfoService.doQueryWx(vo, accessToken, pageNum, pageSize);
		return ResponseResult.success(claimExpenseInfoDtoPage);
	}

	/**
	 * 报销审核-报销详情
	 * 接口编码：21111
	 */
	@GetMapping("getClaimExpenseInfoById")
	public ResponseResult getClaimExpenseInfoById(Long expenseId) {

		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

		return ResponseResult.success(
				claimExpenseInfoService.getClaimExpenseInfoById(expenseId, accessToken)
		);
	}

	/**
	 * 报销审核-司机报销审核
	 * 接口编码：21112
	 *
	 * @param expenseId
	 * @param desc
	 * @param chooseResult 1 审核通过，2 审核不通过
	 */
	@PostMapping("driverExpenseAudit")
	public ResponseResult driverExpenseAudit(Long expenseId, String desc, Integer chooseResult) {
		if (chooseResult == 2 && org.apache.commons.lang3.StringUtils.isBlank(desc)) {
			throw new BusinessException("审核不通过时需要填写原因");
		}

		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		claimExpenseInfoService.driverExpenseAudit(expenseId, desc, chooseResult, accessToken);

		return ResponseResult.success();
	}

	/**
	 * 报销审核-车管报销审核
	 * 接口编码：21113
	 *
	 * @param expenseId
	 * @param desc
	 * @param chooseResult
	 */
	@PostMapping("tubeExpenseAudit")
	public ResponseResult tubeExpenseAudit(Long expenseId, String desc, Integer chooseResult) {
		if (chooseResult == 2 && org.apache.commons.lang3.StringUtils.isBlank(desc)) {
			throw new BusinessException("审核不通过时需要填写原因");
		}

		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		claimExpenseInfoService.tubeExpenseAudit(expenseId, desc, chooseResult, accessToken);

		return ResponseResult.success();
	}

	/**
	 * 我的订单-报销-报销列表(21100)
	 * @return
	 */
	@GetMapping("queryClaimExpenseInfoByOrderId")
	public ResponseResult queryClaimExpenseInfoByOrderId(Long orderId){
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		List<QueryClaimExpenseInfoDto> queryClaimExpenseInfoDtos = claimExpenseInfoService.queryClaimExpenseInfoByOrderId(orderId,accessToken);
		return ResponseResult.success(queryClaimExpenseInfoDtos);
	}
	/**
	 * 报销审核-报销详情
	 * 接口编码：21101
	 */
	@PostMapping("getClaimExpenseInfoByIds")
	public ResponseResult getClaimExpenseInfoByIds(Long expenseId) {
		String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
		QueryClaimExpenseInfoDto queryClaimExpenseInfoDto = claimExpenseInfoService.getClaimExpenseInfoByIds(expenseId, accessToken);
		return ResponseResult.success(queryClaimExpenseInfoDto);
	}


}
