package com.youming.youche.finance.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.DateUtil;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IClaimExpenseCategoryService;
import com.youming.youche.finance.api.IClaimExpenseInfoService;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.constant.ClaimExpenseConsts;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.constant.OaLoanData;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.ClaimExpenseCategory;
import com.youming.youche.finance.domain.ClaimExpenseInfo;
import com.youming.youche.finance.domain.OaFiles;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.dto.ClaimExpenseCategoryDto;
import com.youming.youche.finance.dto.ClaimExpenseInfoByIdDto;
import com.youming.youche.finance.dto.ClaimExpenseInfoDto;
import com.youming.youche.finance.dto.ClaimExpenseInfoInDto;
import com.youming.youche.finance.dto.QueryClaimExpenseInfoDto;
import com.youming.youche.finance.dto.UserDataInfoDto;
import com.youming.youche.finance.provider.mapper.ClaimExpenseInfoMapper;
import com.youming.youche.finance.provider.mapper.OaFilesMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.ClaimExpenseInfoVo;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.SysTenantDefDto;
import com.youming.youche.order.dto.order.PayoutIntfDto;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.common.SysOperLogConst;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@DubboService(version = "1.0.0")
public class ClaimExpenseInfoServiceImpl extends BaseServiceImpl<ClaimExpenseInfoMapper, ClaimExpenseInfo>
		implements IClaimExpenseInfoService {

	@Resource
	RedisUtil redisUtil;

	@Resource
	private LoginUtils loginUtils;
	@Resource
	IClaimExpenseCategoryService claimExpenseCategoryService;

	@Resource
	OaFilesMapper oaFilesMapper;

	@DubboReference(version = "1.0.0")
	IAuditOutService auditOutService;

	@Resource
	ClaimExpenseInfoMapper claimExpenseInfoMapper;

	@Resource
	IClaimExpenseInfoService iClaimExpenseInfoService;

	@DubboReference(version = "1.0.0")
	ISysOperLogService sysOperLogService; // ??????

	@DubboReference(version = "1.0.0")
	ISysSmsSendService sysSmsSendService;

	@DubboReference(version = "1.0.0")
	IAuditService iAuditService;

	@DubboReference(version = "1.0.0")
	ImportOrExportRecordsService importOrExportRecordsService;

	@DubboReference(version = "1.0.0")
	ISysOrganizeService sysOrganizeService;

	@Resource
	IOaLoanThreeService oaLoanService;

	@DubboReference(version = "1.0.0")
	IUserDataInfoService iUserDataInfoService;

	@DubboReference(version = "1.0.0")
	IBusiSubjectsRelService iBusiSubjectsRelService;

	@DubboReference(version = "1.0.0")
	IAccountDetailsService iAccountDetailsService;

	@DubboReference(version = "1.0.0")
	IAccountBankRelService iAccountBankRelService;

	@DubboReference(version = "1.0.0")
	ISysTenantDefService sysTenantDefService;

	@DubboReference(version = "1.0.0")
	IPayFeeLimitService payFeeLimitService;

	@DubboReference(version = "1.0.0")
	IBaseBusiToOrderService iBaseBusiToOrderService;

	@DubboReference(version = "1.0.0")
	IOrderAccountService iOrderAccountService;

	@DubboReference(version = "1.0.0")
	ISysRoleService iSysRoleService;

	@DubboReference(version = "1.0.0")
	IAuditSettingService iAuditSettingService;
	@DubboReference(version = "1.0.0")
	IOrderInfoService orderInfoService;
	@DubboReference(version = "1.0.0")
	IOrderInfoHService orderInfoHService;
	@Resource
	IClaimExpenseInfoService claimExpenseInfoService;
	@DubboReference(version = "1.0.0")
	ITenantVehicleRelService iTenantVehicleRelService;
	@DubboReference(version = "1.0.0")
	IPayoutIntfService iPayoutIntfService;
	@DubboReference(version = "1.0.0")
	IOrderOilSourceService iOrderOilSourceService;
	@Override
	public long create(ClaimExpenseInfoVo infoIn, UserDataInfoDto userDataInfoDto, Long amount, Long insuranceMoney,
			String accessToken, boolean b) {

		ClaimExpenseInfo expenseInfo = new ClaimExpenseInfo();
		BeanUtil.copyProperties(infoIn, expenseInfo);
		if (StringUtils.isNotBlank(infoIn.getOrderId())) {
			expenseInfo.setOrderId(Long.parseLong(infoIn.getOrderId()));
		}
		LocalDateTime now = LocalDateTime.now();

		long incr = redisUtil.incr(EnumConsts.RemoteCache.EXPENSE_NUMBER);
		// ??????????????????
		expenseInfo.setSpecialExpenseNum(CommonUtil.createExpenseNumber(incr));
		// ?????????
		expenseInfo.setState(ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT);
		expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT);

		ClaimExpenseCategory rootGategory = claimExpenseCategoryService.selectOneByCateValue(infoIn.getStairCategory());

		if (rootGategory != null) {
			expenseInfo.setStairCategoryName(rootGategory.getCateName());
		}
		ClaimExpenseCategory gategory = claimExpenseCategoryService
				.selectOneByCateValue(infoIn.getSecondLevelCategory());
		if (gategory != null) {
			expenseInfo.setSecondLevelCategoryName(gategory.getCateName());
		}
		expenseInfo.setAppDate(now);// ????????????
		LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
		expenseInfo.setUserInfoId(loginInfo.getUserInfoId());
		expenseInfo.setUserInfoName(loginInfo.getName());
		expenseInfo.setUserInfoPhone(loginInfo.getTelPhone());
		expenseInfo.setAmount(amount);
		expenseInfo.setBorrowPhone(userDataInfoDto.getMobilePhone());
		expenseInfo.setBorrowName(userDataInfoDto.getLinkman());
		expenseInfo.setBorrowUserId(userDataInfoDto.getId());
		expenseInfo.setPlateNumber(infoIn.getPlateNumber());
		expenseInfo.setVehicleCode(infoIn.getVehicleCode());//??????id
		expenseInfo.setCarOwnerName(infoIn.getCarOwnerName()); //????????????
		expenseInfo.setCarPhone(infoIn.getCarPhone()); // ???????????????
		expenseInfo.setExpenseType(1); // ????????????
		// expenseInfo.setId(null);
		if (infoIn.getStairCategory() == 4 && infoIn.getSecondLevelCategory() == 22) {
			formateAccidentInfo(expenseInfo, infoIn, insuranceMoney);
		}
		if (infoIn.getStairCategory() == 3) {
			expenseInfo.setInsuranceEffectiveDate(
					DateUtil.formatStringToLocalDate(infoIn.getInsuranceEffectiveDate(), DateUtil.DATETIME_FORMAT));
		}
		// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		// b true ??????????????? false ?????????
		if (b) {
			expenseInfo.setOrgId(infoIn.getOrgId());
			expenseInfo.setTenantId(infoIn.getTenantId());
		}
		else {
//			expenseInfo.setOrgId(loginInfo.getOrgIds().get(0));
			expenseInfo.setOrgId(infoIn.getOrgId());
			expenseInfo.setTenantId(loginInfo.getTenantId());
		}

		com.youming.youche.commons.constant.SysOperLogConst.BusiCode  busi_code = com.youming.youche.commons.constant.
				SysOperLogConst.BusiCode.TubeExpense;//????????????
		expenseInfo.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
		if(expenseInfo.getExpenseType()==1){
			busi_code=com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeExpense;//????????????
		}else if(expenseInfo.getExpenseType()==2){
			busi_code=com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense;//????????????
		}
		String tubeExpense = AuditConsts.AUDIT_CODE.TubeExpense;
		if(expenseInfo.getExpenseType()==1){
			tubeExpense= AuditConsts.AUDIT_CODE.TubeExpense;//????????????
		}else if(expenseInfo.getExpenseType()==2){
			tubeExpense=AuditConsts.AUDIT_CODE.DriverExpense;//????????????
		}
		// ?????????????????????????????????
		// TODO: 2022/1/28 ?????????????????????????????????
		// expenseInfo.setReceUserType(getCurUserTpye(expenseInfo.getBorrowUserId(),expenseInfo.getCollectAcctId(),
		// expenseInfo.getAccNo(),expenseInfo.getTenantId()));
		expenseInfo.setReceUserType(1);
		// session.saveOrUpdate(expenseInfo);
		expenseInfo.setExpenseId(0L);
		boolean save = save(expenseInfo);
		// ????????????
		oaLoanService.saveFileForLoanExpense(expenseInfo.getId(),loginInfo.getTenantId(),expenseInfo.getUserInfoId(),
				infoIn.getStrfileId(),OaLoanData.RELTYPE4);
		 // ????????????
		sysOperLogService.saveSysOperLog(loginInfo,busi_code,expenseInfo.getId(),
				com.youming.youche.commons.constant.SysOperLogConst.OperType.Add,"??????????????????");
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params.put("expenseId", expenseInfo.getId());

		boolean bool = iAuditService.startProcess(tubeExpense,
				expenseInfo.getId(), busi_code, params, accessToken, loginInfo.getTenantId());
		if (!bool) {
			throw new BusinessException("???????????????????????????");
		}
		return save ? expenseInfo.getId() : -1;
	}

	@Override
	public Long update(ClaimExpenseInfoVo infoIn, UserDataInfoDto userDataInfoDto, Long amount, Long insuranceMoney,
			String accessToken, boolean b) {
		LambdaQueryWrapper<ClaimExpenseInfo> wrapper = Wrappers.lambdaQuery();
		if (infoIn.getExpenseId() == null && infoIn.getExpenseId() <= 0) {
			throw new BusinessException("????????????");
		}
		ClaimExpenseInfo expenseInfo = get(infoIn.getExpenseId());

		ClaimExpenseCategory rootGategory = claimExpenseCategoryService.selectOneByCateValue(infoIn.getStairCategory());
		if (rootGategory != null) {
			expenseInfo.setStairCategoryName(rootGategory.getCateName());
		}
		ClaimExpenseCategory gategory = claimExpenseCategoryService
				.selectOneByCateValue(infoIn.getSecondLevelCategory());
		if (gategory != null) {
			expenseInfo.setSecondLevelCategoryName(gategory.getCateName());
		}
		expenseInfo.setAccName(infoIn.getAccName()==null?"":infoIn.getAccName());
		expenseInfo.setAccNo(infoIn.getAccNo()==null?"":infoIn.getAccNo());
		expenseInfo.setBankType(infoIn.getBankType());
		expenseInfo.setCollectAcctId(infoIn.getCollectAcctId());
		expenseInfo.setIsNeedBill(infoIn.getIsNeedBill());
		expenseInfo.setBankName(infoIn.getBankName()==null?"":infoIn.getBankName());
		expenseInfo.setBankBranch(infoIn.getBankBranch());

		// TODO 2022-6-14 ???????????? ????????????????????? ????????? ??????????????????
		expenseInfo.setBorrowUserId(userDataInfoDto.getId());
		expenseInfo.setBorrowName(userDataInfoDto.getLinkman());
		expenseInfo.setBorrowPhone(userDataInfoDto.getMobilePhone());

		expenseInfo.setCarOwnerName(infoIn.getCarOwnerName()); //????????????
		expenseInfo.setCarPhone(infoIn.getCarPhone()); // ???????????????
		expenseInfo.setExpenseType(1); // ????????????
		if (expenseInfo.getState() == ClaimExpenseConsts.EXPENSE_STS.AUDIT_NOT_PASS) {// ??????
																						// ?????????????????????
			expenseInfo.setState(ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT);// ?????????
			expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT);
		}
		expenseInfo.setCarOwnerName(infoIn.getCarOwnerName());
		expenseInfo.setCarPhone(infoIn.getCarPhone());
		expenseInfo.setReportNumber(infoIn.getReportNumber());
		expenseInfo.setWeightFee(infoIn.getWeightFee());
		expenseInfo.setPlateNumber(infoIn.getPlateNumber());
		expenseInfo.setVehicleCode(infoIn.getVehicleCode());//??????id
		if (StringUtils.isNotBlank(infoIn.getOrderId())) {
			expenseInfo.setOrderId(Long.parseLong(infoIn.getOrderId()));
		}

		LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
		 // TODO 2022-6-9 ????????????  ???????????? ?????????????????????????????????
//		expenseInfo.setAppDate(LocalDateTime.now());
//		expenseInfo.setUserInfoId(loginInfo.getUserInfoId());
//		expenseInfo.setUserInfoName(loginInfo.getName());
//		expenseInfo.setUserInfoPhone(loginInfo.getTelPhone());

		expenseInfo.setStairCategory(infoIn.getStairCategory());
		expenseInfo.setSecondLevelCategory(infoIn.getSecondLevelCategory());
		expenseInfo.setAmount(amount);
		expenseInfo.setAppReason(infoIn.getAppReason());
		expenseInfo.setAppReason(infoIn.getAppReason());
		expenseInfo.setExpenseType(1);
		if (infoIn.getStairCategory() == 4 && infoIn.getSecondLevelCategory() == 22) {
			formateAccidentInfo(expenseInfo, infoIn, insuranceMoney);
		}
		if (infoIn.getStairCategory() == 3) {
			expenseInfo.setInsuranceEffectiveDate(
					DateUtil.formatStringToLocalDate(infoIn.getInsuranceEffectiveDate(), DateUtil.DATETIME_FORMAT));
		}
		// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		// b true ??????????????? false ?????????
		if (b) {
			expenseInfo.setOrgId(infoIn.getOrgId());
			expenseInfo.setTenantId(infoIn.getTenantId());
		}
		else {
//			expenseInfo.setOrgId(loginInfo.getOrgIds().get(0));
			expenseInfo.setOrgId(infoIn.getOrgId());
			expenseInfo.setTenantId(loginInfo.getTenantId());
		}
		expenseInfo.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
		com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.
				SysOperLogConst.BusiCode.TubeExpense;//????????????
		expenseInfo.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
		if(expenseInfo.getExpenseType()==1){
			busi_code= com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeExpense;//????????????
		}else if(expenseInfo.getExpenseType()==2){
			busi_code=com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense;//????????????
		}
		String tubeExpense = AuditConsts.AUDIT_CODE.TubeExpense;
		if(expenseInfo.getExpenseType()==1){
			tubeExpense= AuditConsts.AUDIT_CODE.TubeExpense;//????????????
		}else if(expenseInfo.getExpenseType()==2){
			tubeExpense=AuditConsts.AUDIT_CODE.DriverExpense;//????????????
		}
		// ?????????????????????????????????
		// TODO: 2022/1/28 ?????????????????????????????????
		expenseInfo.setReceUserType(1);
		// expenseInfo.setReceUserType(getCurUserTpye(expenseInfo.getBorrowUserId(),expenseInfo.getCollectAcctId(),
		// expenseInfo.getAccNo(),expenseInfo.getTenantId()));
		boolean b1 = saveOrUpdate(expenseInfo);
		// ????????????
		oaLoanService.saveFileForLoanExpense(expenseInfo.getId(),loginInfo.getTenantId(),expenseInfo.getUserInfoId(),
				infoIn.getStrfileId(),OaLoanData.RELTYPE4);
		// ????????????
		sysOperLogService.saveSysOperLog(loginInfo,busi_code,expenseInfo.getId(),
				com.youming.youche.commons.constant.SysOperLogConst.OperType.Update,"??????????????????");
		 //  TODO ??????????????????
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params.put("expenseId", expenseInfo.getId());
		boolean bool = iAuditService.startProcess(
				tubeExpense,
				expenseInfo.getId(), busi_code,
				params, accessToken, loginInfo.getTenantId());
		if (!bool) {
			throw new BusinessException("???????????????????????????");
		}
		return b1 ? expenseInfo.getId() : -1;
	}

	//??????????????????
	@Override
	public List<OaFiles> queryOaFilesByRelType4(Long id, Long tenantId) {
		QueryWrapper<OaFiles> wrapper = new QueryWrapper<>();
		wrapper.eq("REL_ID",id)
				.eq("REL_TYPE", OaLoanData.RELTYPE4)
				.eq("TENANT_ID",tenantId);
		List<OaFiles> oaFiles = oaFilesMapper.selectList(wrapper);
		return oaFiles;
	}

	/**
	 * ??????????????????
	 * @param expenseInfo
	 * @param infoIn
	 * @param insuranceMoney
	 */
	public void formateAccidentInfo(ClaimExpenseInfo expenseInfo, ClaimExpenseInfoVo infoIn, long insuranceMoney) {
		expenseInfo
				.setAccidentDate(DateUtil.formatStringToLocalDate(infoIn.getAccidentDate(), DateUtil.DATETIME_FORMAT));
		expenseInfo.setInsuranceDate(
				DateUtil.formatStringToLocalDate(infoIn.getInsuranceDate(), DateUtil.DATETIME_FORMAT));
		expenseInfo.setAccidentType(infoIn.getAccidentType());
		expenseInfo.setAccidentReason(infoIn.getAccidentReason());
		expenseInfo.setDutyDivide(infoIn.getDutyDivide());
		expenseInfo.setAccidentDivide(infoIn.getAccidentDivide());
		expenseInfo.setInsuranceFirm(infoIn.getInsuranceFirm());
		expenseInfo.setInsuranceMoney(insuranceMoney);
		expenseInfo.setAccidentExplain(infoIn.getAccidentExplain());
	}


	@Override
	public IPage<ClaimExpenseCategoryDto> doQuery(Integer expenseType, String specialExpenseNum, String stairCategory,
												  String secondLevelCategory, Integer expenseSts, String userName,
												  String userPhone, String plateNumber, String startTime, String endTime,
												  String flowId, String orderId, String oneOrgId, String twoOrgId,
												  String threeOrgId, List<String> stairCategoryList,
												  List<String> secondLevelCategoryList, Boolean waitDeal,
												  List<Long> dataPermissionIds,
												  Integer pageNum, Integer pageSize, String accessToken, List<String> expenseStsList,String userId) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		List<Long> orgIds = loginInfo.getOrgIds();
		List<Long> subOrgList = sysOrganizeService.getSubOrgList(loginInfo.getTenantId(),orgIds);
		List<Long> lids = new ArrayList<>();
		// ????????????  ??????????????? ????????????????????? ???????????? 21000100
		if (waitDeal){
			List<Long> busiIdByUserId = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.TubeExpense, loginInfo.getUserInfoId(), loginInfo.getTenantId());
			List<Long> busiIdByUserId1 = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.DriverExpense, loginInfo.getUserInfoId(), loginInfo.getTenantId());
			for (Long l :busiIdByUserId) {
				lids.add(l);
			}
			for (Long l :busiIdByUserId1) {
				lids.add(l);
			}
		}
		String userIds = null;
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(userId)){
			userIds = userId;
		}
		LocalDateTime startTime1 = null;
		LocalDateTime endTime1 = null;
		if(startTime != null && org.apache.commons.lang3.StringUtils.isNotEmpty(startTime)){
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String applyTimeStr = startTime+" 00:00:00";
			startTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
		}
		if(endTime != null && org.apache.commons.lang.StringUtils.isNotEmpty(endTime)){
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String endApplyTimeStr = endTime+" 23:59:59";
			endTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
		}
		Boolean aBoolean = iSysRoleService.hasAllData(loginInfo);
		Page<ClaimExpenseCategoryDto> page = new Page<>(pageNum, pageSize);
		Integer type = null;
//		if ((org.apache.commons.lang3.StringUtils.isNotEmpty(specialExpenseNum))||stairCategory.equals("6")){
//			type = 2;
//			stairCategory = null;
//		}
		// ??????
		if (StringUtils.isBlank(stairCategory)){
			stairCategory = null;
		}else  if (!StringUtils.isBlank(stairCategory)){
				if (stairCategory.equals("6")){
					type = 2;
					stairCategory = null;
				}
		}
		IPage<ClaimExpenseCategoryDto> claimExpenseCategoryDtoIPage = baseMapper.doQuery(expenseType,specialExpenseNum,
				stairCategory,secondLevelCategory,expenseSts,userName,userIds,userPhone,plateNumber,startTime1,endTime1,flowId,orderId,oneOrgId,twoOrgId,
				threeOrgId,stairCategoryList,secondLevelCategoryList,waitDeal,dataPermissionIds,subOrgList,lids,loginInfo.getTenantId(),
				null,aBoolean,type,page );
		// ?????????????????????
		List<ClaimExpenseCategoryDto> listOut = new ArrayList<>();
		List<ClaimExpenseCategoryDto> records = claimExpenseCategoryDtoIPage.getRecords();
		 List<Long> busiIds = new ArrayList<>();

		for (ClaimExpenseCategoryDto dto :records) {
			busiIds.add(dto.getId());
		}
		String auditCodeTube = AuditConsts.AUDIT_CODE.TubeExpense;// ????????????
		String auditCodeDriver = AuditConsts.AUDIT_CODE.DriverExpense; // ????????????
		// ????????????id?????????????????? ??????????????????id
		Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
		if(!busiIds.isEmpty()) {
			Map<Long, Boolean> hasPermission = auditOutService.isHasPermission(accessToken, auditCodeTube, busiIds);
			Map<Long, Boolean> hasPermission1 = auditOutService.isHasPermission(accessToken, auditCodeDriver, busiIds);
			hasPermissionMap.putAll(hasPermission);
			hasPermissionMap.putAll(hasPermission1);
		}
		// ?????????????????????
		for (ClaimExpenseCategoryDto expenseInfo : records) {
			boolean isTrue=false;
			if(hasPermissionMap.get(expenseInfo.getId())!=null&&hasPermissionMap.get(expenseInfo.getId())){
				isTrue=true;
			}
			//????????????????????????
			expenseInfo.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), expenseInfo.getOrgId()));
			expenseInfo.setIsHasPermission(isTrue);
			 //?????? ???????????????name
			expenseInfo.setStsString(expenseInfo.getSts()==null? "" : getSysStaticData(
					"EXPENSE_STS",String.valueOf(expenseInfo.getSts())).getCodeName());

			expenseInfo.setStateString(expenseInfo.getState()==null? "" : getSysStaticData(
					"EXPENSE_STS",String.valueOf(expenseInfo.getState())).getCodeName());
			// ????????????
			expenseInfo.setAccidentTypeName(expenseInfo.getAccidentType()==null? "": getSysStaticData(
					"LOAN_ACCIDENT_TYPE",String.valueOf(expenseInfo.getAccidentType())
			).getCodeName());
			 // ????????????
			expenseInfo.setAccidentReasonName(expenseInfo.getAccidentReason()==null? "": getSysStaticData(
					"LOAN_ACCIDENT_REASON",String.valueOf(expenseInfo.getAccidentDivide())
			).getCodeName());
			// ????????????
			expenseInfo.setDutyDivideName(expenseInfo.getDutyDivide()==null? "": getSysStaticData(
					"LOAN_DUTY_DIVIDE",String.valueOf(expenseInfo.getDutyDivide())
			).getCodeName());
			// ????????????
			expenseInfo.setAccidentDivideName(expenseInfo.getAccidentDivide()==null? "": getSysStaticData(
					"LOAN_ACCIDENT_DRIVER",String.valueOf(expenseInfo.getAccidentDivide())
			).getCodeName());
			expenseInfo.setExpenseTypeName(expenseInfo.getExpenseType()!=null&&expenseInfo.getExpenseType()==1?"????????????":"????????????");
			// ?????? ????????????
			if (expenseInfo.getVehicleCode()!=null){
				TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(expenseInfo.getVehicleCode());
				if (tenantVehicleRel!=null && tenantVehicleRel.getVehicleClass()!=null){
					expenseInfo.setVehicleClass(tenantVehicleRel.getVehicleClass());
				}
			}

		}
		return claimExpenseCategoryDtoIPage;
	}

	/**
	 * ??????????????????
	 * @param expenseId
	 * @param busiCode ????????????
	 * @return
	 */
	@Override
	public ClaimExpenseInfo queryClaimExpenseById(Long expenseId, String accessToken, String... busiCode) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		List<Long> fileId = new ArrayList<Long>();//??????id
		List<String> fileUrl = new ArrayList<String>();//??????????????????
		List<String> fileName = new ArrayList<String>();
//		if (org.apache.commons.lang3.StringUtils.isEmpty(expenseId.toString())) {
//			throw new BusinessException("????????????????????????");
//		}
		ClaimExpenseInfo expenseInfo = getClaimExpenseInfo(expenseId,busiCode);
		if (expenseInfo == null) {
			throw new BusinessException("?????????????????????");
		}
//		expenseId = expenseInfo.getExpenseId();
		expenseId = expenseInfo.getId();
//		ClaimExpenseInfoOut expenseInfoOut = new ClaimExpenseInfoOut();
//		ClaimExpenseInfoOut expenseInfoOut  = new ClaimExpenseInfoOut();
//		try {
//			BeanUtils.copyProperties(expenseInfo,expenseInfoOut);
//		}catch (Exception e){
//			throw new BusinessException("???????????????");
//		}

//		List<OaFiles> oaFiles = queryOaFilesByRelType4(expenseId,expenseInfo.getTenantId());
		List<OaFiles> oaFiles = iClaimExpenseInfoService.queryOaFilesByRelType4(expenseId, expenseInfo.getTenantId());
		if (oaFiles != null) {
			for (OaFiles of : oaFiles) {
				if (of.getRelType().equals(OaLoanData.RELTYPE4)) {
					fileId.add(of.getFileId());
					fileUrl.add(of.getFileUrl());
					fileName.add(of.getFileName());
				}
			}
		}
		expenseInfo.setFileUrl(fileUrl);
		expenseInfo.setFileName(fileName);
		expenseInfo.setFileId(fileId);
		//?????? ???????????????name
		expenseInfo.setStsString(expenseInfo.getState()==null? "" : getSysStaticData(
				"EXPENSE_STS",String.valueOf(expenseInfo.getState())).getCodeName());
		// ????????????
		expenseInfo.setAccidentTypeName(expenseInfo.getAccidentType()==null? "": getSysStaticData(
				"LOAN_ACCIDENT_TYPE",String.valueOf(expenseInfo.getAccidentType())
		).getCodeName());
		// ????????????
		expenseInfo.setAccidentReasonName(expenseInfo.getAccidentReason()==null? "": getSysStaticData(
				"LOAN_ACCIDENT_REASON",String.valueOf(expenseInfo.getAccidentDivide())
		).getCodeName());
		// ????????????
		expenseInfo.setDutyDivideName(expenseInfo.getDutyDivide()==null? "": getSysStaticData(
				"LOAN_DUTY_DIVIDE",String.valueOf(expenseInfo.getDutyDivide())
		).getCodeName());
		// ????????????
		expenseInfo.setAccidentDivideName(expenseInfo.getAccidentDivide()==null? "": getSysStaticData(
				"LOAN_ACCIDENT_DRIVER",String.valueOf(expenseInfo.getAccidentDivide())
		).getCodeName());
		// ????????????
		expenseInfo.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), expenseInfo.getOrgId()));

		expenseInfo.setExpenseTypeName(expenseInfo.getExpenseType()!=null&&expenseInfo.getExpenseType()==1?"????????????":"????????????");
//		expenseId = expenseInfoOut.getExpenseId();
		expenseId = expenseInfo.getId();
		int code = SysOperLogConst.BusiCode.DriverExpense.getCode();
		String auditCode = AuditConsts.AUDIT_CODE.DriverExpense;
		if (expenseInfo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {//???????????? : ??????
			code = SysOperLogConst.BusiCode.TubeExpense.getCode();
			auditCode = AuditConsts.AUDIT_CODE.TubeExpense;
		}
		// TDOO ?????????????????????
//		List<SysOperLog> sysOperLogs = tf.querySysOperLogAll(code, expenseId, false,ceo.getTenantId(),auditCode,expenseId);


		return expenseInfo;
	}

	/**
	 *
	 * @param expenseId ??????
	 * @param busiCode ????????????
	 * @return
	 */
	@Override
	public ClaimExpenseInfo getClaimExpenseInfo(Long expenseId, String... busiCode) {
//		Session session = SysContexts.getEntityManager();
		ClaimExpenseInfo claimExpenseInfo = null;
//		Criteria ca = session.createCriteria(ClaimExpenseInfo.class);
		QueryWrapper<ClaimExpenseInfo> wrapper = new QueryWrapper<>();
		if(expenseId != null && expenseId>0){
//			ca.add(Restrictions.eq("id", expenseId));
			wrapper.eq("id",expenseId);
		}else if(busiCode.length>0){
//			ca.add(Restrictions.eq("specialExpenseNum", busiCode[0]));
			wrapper.eq("special_expense_num",busiCode[0]);
		}else{
			return null;
		}
//		List<ClaimExpenseInfo> lists = ca.list();
		List<ClaimExpenseInfo> claimExpenseInfos = claimExpenseInfoMapper.selectList(wrapper);
		if(claimExpenseInfos.size() > 0){
			claimExpenseInfo = (ClaimExpenseInfo) claimExpenseInfos.get(0);
		}
		return claimExpenseInfo;
	}

	/**
	 * ??????????????????
	 * @param expenseId
	 */
	@Override
	public void cancelClaimExpense(Long expenseId,String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
//		ClaimExpenseInfo expenseInfo = claimExpenseInfoSV.getClaimExpenseInfo(expenseId);
		ClaimExpenseInfo expenseInfo = this.getClaimExpenseInfo(expenseId);
		if (expenseId <= 0) {
			throw new BusinessException("??????ID??????");
		}
		if (expenseInfo == null) {
			throw new BusinessException("?????????????????????");
		}
		if (expenseInfo.getState() == ClaimExpenseConsts.EXPENSE_STS.UNDER_AUDIT) {
			throw new BusinessException("???????????????,????????????");
		}
		if (expenseInfo.getState() == ClaimExpenseConsts.EXPENSE_STS.AUDIT_PASS) {
			throw new BusinessException("??????????????????,????????????");
		}
		if (expenseInfo.getState() == ClaimExpenseConsts.EXPENSE_STS.CANCEL) {
			throw new BusinessException("????????????????????????,??????????????????");
		}
		expenseInfo.setState(ClaimExpenseConsts.EXPENSE_STS.CANCEL);
		expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.CANCEL);
//		claimExpenseInfoSV.saveOrUpdate(expenseInfo);
		this.saveOrUpdate(expenseInfo);
		//??????????????????
//		ISysOperLogSV logSV = (ISysOperLogSV) SysContexts.getBean("sysOperLogSV");

		String auditCode = AuditConsts.AUDIT_CODE.DriverExpense;
//		SysOperLogConst.BusiCode busi_code = 	com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense;
		com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense;//????????????
		if (expenseInfo.getExpenseType() == 1) {//???????????? : ??????
			busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeExpense;//????????????
			auditCode = AuditConsts.AUDIT_CODE.TubeExpense;
		}
//		logSV.saveSysOperLog(busi_code, expenseInfo.getExpenseId(), SysOperLogConst.OperType.Update, "??????????????????");
		sysOperLogService.saveSysOperLog(loginInfo,busi_code,expenseInfo.getId(),com.youming.youche.commons.constant.SysOperLogConst.OperType.Update,
				"??????????????????",loginInfo.getTenantId());
		//??????????????????
		try{
//			IAuditOutTF auditOutTF = (IAuditOutTF) SysContexts.getBean("auditOutTF");
//			auditOutTF.cancelProcess(auditCode, expenseInfo.getExpenseId(),expenseInfo.getTenantId());
			auditOutService.cancelProcess(auditCode,expenseInfo.getId(),expenseInfo.getTenantId());
		}catch (Exception e){
			e.printStackTrace();
		}
//		return "Y";
	}



	/**
	 * ??????????????????
	 * ?????????????????????????????????????????????????????????
	 */
	@Transactional
	@Override
	public void examineClaimExpense(ClaimExpenseInfoVo infoIn, String accessToken) {
		ClaimExpenseInfo expenseInfo = getClaimExpenseInfo(infoIn.getExpenseId());
		if (expenseInfo!=null && expenseInfo.getSts()!=null){
			if (expenseInfo.getSts() == ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT) {
				expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.UNDER_AUDIT);
				this.saveOrUpdate(expenseInfo);
			}
		}
	}


	// ??????????????????
	@Async
	@Override
	public void excelExport(Integer expenseType, String specialExpenseNum, String stairCategory,
							String secondLevelCategory, Integer expenseSts, String userName, String userPhone,
							String plateNumber, String startTime, String endTime, String flowId, String orderId,
							String oneOrgId, String twoOrgId, String threeOrgId, List<String> stairCategoryList,
							List<String> secondLevelCategoryList, Boolean waitDeal, List<Long> dataPermissionIds,
							Integer pageNum, Integer pageSize, String accessToken,
							List<String> expenseStsList, String userId, ImportOrExportRecords record ) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		// ????????????????????????
		IPage<ClaimExpenseCategoryDto> claimExpenseCategoryDtoIPage = this.doQuery(expenseType, specialExpenseNum,
				stairCategory, secondLevelCategory, expenseSts,
				userName, userPhone, plateNumber, startTime, endTime,
				flowId, orderId, oneOrgId, twoOrgId, threeOrgId, stairCategoryList,
				secondLevelCategoryList, waitDeal, dataPermissionIds,
				pageNum, 9999, accessToken, expenseStsList, userId);
		List<ClaimExpenseCategoryDto> records1 = claimExpenseCategoryDtoIPage.getRecords();
		try {
			String[] showName = null;
			String[] resourceFild = null;
			String fileName = null;
			for (ClaimExpenseCategoryDto dto:records1) {
				dto.setAppDates(com.youming.youche.util.DateUtil.asDate(dto.getAppDate()));
				dto.setStsString(dto.getSts()==null? "" : getSysStaticData(
						"EXPENSE_STS",String.valueOf(dto.getSts())).getCodeName());
				dto.setStateString(dto.getState()==null? "" : getSysStaticData(
						"EXPENSE_STS",String.valueOf(dto.getState())).getCodeName());
				if (dto.getAmount()!=null){
					dto.setAmountDouble(dto.getAmount()/100.00);
				}
				//????????????????????????
				dto.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), dto.getOrgId()));
			}
			showName = new String[]{
					"????????????", "??????", "??????",
					"????????????", "?????????",
					"??????????????????", "????????????",
				    "?????????",
					"???????????????","??????"
//					"????????????"
			};
			resourceFild = new String[]{
					"getSpecialExpenseNum", "getStairCategoryName", "getAmountDouble",
					"getOrgName", "getUserInfoName",
					"getUserInfoPhone", "getAppDates",
					"getPlateNumber",
					"getFlowId","getStsString"
//					"flowId"
			};
			XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records1, showName, resourceFild, ClaimExpenseCategoryDto.class,
					null);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			workbook.write(os);
			byte[] b = os.toByteArray();
			InputStream inputStream = new ByteArrayInputStream(b);
			//????????????
			FastDFSHelper client = FastDFSHelper.getInstance();
			String path = client.upload(inputStream, "??????????????????.xlsx", inputStream.available());
			os.close();
			inputStream.close();
			record.setMediaUrl(path);
			record.setState(2);
			importOrExportRecordsService.saveOrUpdate(record);
		}catch ( Exception e){
			record.setState(3);
			importOrExportRecordsService.saveOrUpdate(record);
			e.printStackTrace();
		}
	}

	@Override
	public List<WorkbenchDto> getTableManageCostCount() {
		return baseMapper.getTableManageCostCount();
	}

	@Override
	public List<WorkbenchDto> getTableManageCostMeCount() {
		return baseMapper.getTableManageCostMeCount();
	}

	@Override
	public void sucess(Long busiId, String desc, Map paramsMap,String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		Long expenseId = DataFormat.getLongKey(paramsMap, "expenseId");
		ClaimExpenseInfo expenseInfo =this.getClaimExpenseInfo(expenseId);
		expenseInfo.setVerifyDate(DateUtil.dateTransition(new Date()));
		Long payUserId = expenseInfo.getBorrowUserId();
		UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(payUserId);
		if (userDataInfo == null) {
			throw new BusinessException("????????????????????????");
		}
//		if(StringUtils.equals(sysTenantDef.getAdminUser()+"",expenseInfo.getBorrowUserId()+"")){
//			throw new BusinessException("?????????????????????????????????????????????????????????");
//		}
		if (expenseInfo.getExpenseType() == 2) {//???????????????????????????
			//???????????????
			payExpenseInfoOut(expenseInfo, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,accessToken);
		}else {
			//????????????????????????????????????????????????
			payVehicleExpenseInfoOut(expenseInfo,OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,accessToken);
		}
		// ?????????????????? ?????????????????????
		com.youming.youche.order.domain.order.PayoutIntf payoutIntfId = iPayoutIntfService.getPayoutIntfId(expenseInfo.getSpecialExpenseNum());
		expenseInfo.setFlowId(payoutIntfId.getId());// ??????id
		expenseInfo.setState(ClaimExpenseConsts.EXPENSE_STS.AUDIT_PASS);//????????????
		expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.AUDIT_PASS);
		expenseInfo.setVerifyUserInfoId(loginInfo.getId()); //  ???????????????????????????id
		expenseInfo.setVerifyUserName(loginInfo.getName()); //  ?????????????????????????????????
		this.saveOrUpdate(expenseInfo);
		com.youming.youche.commons.constant.SysOperLogConst.BusiCode  busi_code = com.youming.youche.commons.constant.
				SysOperLogConst.BusiCode.TubeExpense;//????????????
		sysOperLogService.saveSysOperLog(loginInfo,busi_code,expenseInfo.getId(),
				com.youming.youche.commons.constant.SysOperLogConst.OperType.Audit,"????????????????????????");
	}

	@Override
	public void fail(Long busiId, String desc, Map paramsMap,String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		Long expenseId = DataFormat.getLongKey(paramsMap, "expenseId");
		ClaimExpenseInfo expenseInfo = this.getClaimExpenseInfo(expenseId);
		expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.AUDIT_NOT_PASS);//?????????????????????????????????
		expenseInfo.setState(ClaimExpenseConsts.EXPENSE_STS.AUDIT_NOT_PASS);//?????????????????????????????????
		this.saveOrUpdate(expenseInfo);
//		com.youming.youche.commons.constant.SysOperLogConst.BusiCode  busi_code = com.youming.youche.commons.constant.
//				SysOperLogConst.BusiCode.TubeExpense;//????????????
//		sysOperLogService.saveSysOperLog(loginInfo,busi_code,expenseInfo.getId(),
//				com.youming.youche.commons.constant.SysOperLogConst.OperType.Audit,"???????????????????????????");
	}

	@Override
	public Integer queryClaimInfoCount(String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		Long tenantId = loginInfo.getTenantId();
		Long userId = loginInfo.getId();

		List<Long> lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.TubeExpense, userId, tenantId);
		List<Long> lids_driver = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.DriverExpense, userId, tenantId);

		if (lids != null && lids.size() > 0) {
			lids.addAll(lids_driver);
		} else {
			lids = lids_driver;
		}

//		if (lids != null && lids.size() > 0) {
		//?????????????????????????????????(????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????)
		LambdaQueryWrapper<ClaimExpenseInfo> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ClaimExpenseInfo::getTenantId, tenantId);
		queryWrapper.in(ClaimExpenseInfo::getSts, 1, 2);
		queryWrapper.in(lids != null && lids.size() > 0, ClaimExpenseInfo::getExpenseId, lids);

//			boolean hasAllDataPermission = iSysRoleService.hasAllData(loginInfo);
//			queryWrapper.in(!hasAllDataPermission, ClaimExpenseInfo::getOrgId, sysOrganizeService.getSubOrgList(tenantId, loginInfo.getId()));

		int count = this.count(queryWrapper);
		return count;
//		} else {
//			return 0;
//		}
	}

	@Override
	public Page<ClaimExpenseInfoDto> doQueryWx(ClaimExpenseInfoVo vo, String accessToken, Integer pageNum, Integer pageSize) {
		LoginInfo loginInfo = loginUtils.get(accessToken);

		LambdaQueryWrapper<ClaimExpenseInfo> queryWrapper = new LambdaQueryWrapper<>();

		if (vo.getStairCategory() != null && vo.getStairCategory() > 0) {
			queryWrapper.eq(ClaimExpenseInfo::getStairCategory, vo.getStairCategory());
		}
		if (vo.getSecondLevelCategory() != null && vo.getSecondLevelCategory() > 0) {
			queryWrapper.eq(ClaimExpenseInfo::getSecondLevelCategory, vo.getSecondLevelCategory());
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getUserName())) {
			queryWrapper.like(ClaimExpenseInfo::getUserInfoName, "%" + vo.getUserName() + "%");
		}
		if (org.apache.commons.lang3.StringUtils.isNotBlank(vo.getUserId())) {
			queryWrapper.eq(ClaimExpenseInfo::getUserInfoId, vo.getUserId());
		} else {
			queryWrapper.eq(ClaimExpenseInfo::getTenantId, loginInfo.getTenantId());
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getUserPhone())) {
			queryWrapper.like(ClaimExpenseInfo::getUserInfoPhone, "%" + vo.getUserPhone() + "%");
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getPlateNumber())) {
			queryWrapper.like(ClaimExpenseInfo::getPlateNumber, "%" + vo.getPlateNumber() + "%");
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getSpecialExpenseNum())) {
			queryWrapper.like(ClaimExpenseInfo::getSpecialExpenseNum, "%" + vo.getSpecialExpenseNum() + "%");
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getStartTime())) {
			queryWrapper.ge(ClaimExpenseInfo::getAppDate, vo.getStartTime() + " 00:00:00");
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getEndTime())) {
			queryWrapper.le(ClaimExpenseInfo::getAppDate, vo.getStartTime() + " 23:59:59");
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getOrderId())) {
			queryWrapper.eq(ClaimExpenseInfo::getOrderId, vo.getOrderId());
		}
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(vo.getFlowId())) {
			queryWrapper.eq(ClaimExpenseInfo::getFlowId, vo.getFlowId());
		}
		if (vo.getExpenseType() != null) {
			if (vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.DRIVER || vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {
				queryWrapper.eq(ClaimExpenseInfo::getExpenseType, vo.getExpenseType());
			}
			if (vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.DRIVER && org.apache.commons.lang3.StringUtils.isNotBlank(vo.getStairCategoryList())) {
				queryWrapper.eq(ClaimExpenseInfo::getStairCategory, vo.getStairCategoryList());
			}
			if (vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE && org.apache.commons.lang3.StringUtils.isNotBlank(vo.getStairCategoryList())) {
				queryWrapper.eq(ClaimExpenseInfo::getStairCategory, vo.getStairCategoryList());
				if (org.apache.commons.lang3.StringUtils.isNotBlank(vo.getSecondLevelCategoryList())) {
					queryWrapper.eq(ClaimExpenseInfo::getSecondLevelCategory, vo.getSecondLevelCategoryList());
				}
			}
		}
		if (org.apache.commons.lang3.StringUtils.isNotBlank(vo.getExpenseStsList())) {
			queryWrapper.eq(ClaimExpenseInfo::getSts, vo.getExpenseStsList());
		}
		if (vo.getExpenseSts() > 0) {
			queryWrapper.eq(ClaimExpenseInfo::getSts, vo.getExpenseSts());
		}
		//?????????????????????????????????(????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????)
		boolean hasAllDataPermission = iSysRoleService.hasAllData(loginInfo);
		if (!hasAllDataPermission && vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {
			List<Long> orgList = sysOrganizeService.getSubOrgList(accessToken);
			queryWrapper.in(ClaimExpenseInfo::getOrgId, orgList);
		}
		if (vo.getWaitDeal() != null && !vo.getWaitDeal()) {// ????????????
			List<Long> lids = null;
			if (vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {// ????????????
				lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.TubeExpense, loginInfo.getUserInfoId(), loginInfo.getTenantId());
			} else if (vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.DRIVER) {// ????????????
				lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.DriverExpense, loginInfo.getUserInfoId(), loginInfo.getTenantId());
			}
			if (lids != null && lids.size() > 0) {
				queryWrapper.in(ClaimExpenseInfo::getId, lids);
				queryWrapper.in(ClaimExpenseInfo::getSts, 1, 2);
			} /*else {
				queryWrapper.eq(ClaimExpenseInfo::getExpense_id, null);
			}*/
		}

		queryWrapper.orderByDesc(ClaimExpenseInfo::getAppDate);

		Page<ClaimExpenseInfo> page = this.page(new Page<ClaimExpenseInfo>(pageNum, pageSize), queryWrapper);
		List<ClaimExpenseInfo> lists = page.getRecords();

		List<ClaimExpenseInfoDto> listOut = new ArrayList<ClaimExpenseInfoDto>();

		List<Long> busiIds = new ArrayList<>();
		Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
		for (ClaimExpenseInfo expenseInfo : lists) {
			busiIds.add(expenseInfo.getId());
		}

		String auditCode = "";
		if (vo.getExpenseType() != null) {
			if (vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {// ????????????
				auditCode = AuditConsts.AUDIT_CODE.TubeExpense;
			} else if (vo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.DRIVER) {// ????????????
				auditCode = AuditConsts.AUDIT_CODE.DriverExpense;
			}
		}

		if (!busiIds.isEmpty()) {
			hasPermissionMap = auditOutService.isHasPermission(accessToken, auditCode, busiIds);
		}

		for (ClaimExpenseInfo expenseInfo : lists) {
			ClaimExpenseInfoDto expenseInfoOut = new ClaimExpenseInfoDto();
			BeanUtil.copyProperties(expenseInfo, expenseInfoOut);
			boolean isTrue = false;
			if (hasPermissionMap.get(expenseInfo.getId()) != null && hasPermissionMap.get(expenseInfo.getId())) {
				isTrue = true;
			}
			expenseInfoOut.setHasPermission(isTrue);

			//?????? ???????????????name
			expenseInfoOut.setStsString(expenseInfo.getState() == null ? "" : getSysStaticData(
					"EXPENSE_STS", String.valueOf(expenseInfo.getState())).getCodeName());
			// ????????????
			expenseInfoOut.setAccidentTypeName(expenseInfo.getAccidentType() == null ? "" : getSysStaticData(
					"LOAN_ACCIDENT_TYPE", String.valueOf(expenseInfo.getAccidentType())
			).getCodeName());
			// ????????????
			expenseInfoOut.setAccidentReasonName(expenseInfo.getAccidentReason() == null ? "" : getSysStaticData(
					"LOAN_ACCIDENT_REASON", String.valueOf(expenseInfo.getAccidentDivide())
			).getCodeName());
			// ????????????
			expenseInfoOut.setDutyDivideName(expenseInfo.getDutyDivide() == null ? "" : getSysStaticData(
					"LOAN_DUTY_DIVIDE", String.valueOf(expenseInfo.getDutyDivide())
			).getCodeName());
			// ????????????
			expenseInfoOut.setAccidentDivideName(expenseInfo.getAccidentDivide() == null ? "" : getSysStaticData(
					"LOAN_ACCIDENT_DRIVER", String.valueOf(expenseInfo.getAccidentDivide())
			).getCodeName());

			listOut.add(expenseInfoOut);
		}

		Page<ClaimExpenseInfoDto> claimExpenseInfoDtoPage = new Page<>();
		claimExpenseInfoDtoPage.setRecords(listOut);
		claimExpenseInfoDtoPage.setSize(page.getSize());
		claimExpenseInfoDtoPage.setPages(page.getPages());
		claimExpenseInfoDtoPage.setCurrent(page.getCurrent());
		claimExpenseInfoDtoPage.setTotal(page.getTotal());

		return claimExpenseInfoDtoPage;
	}

	@Override
	public ClaimExpenseInfoByIdDto getClaimExpenseInfoById(Long expenseId, String accessToken) {
		ClaimExpenseInfo ceo = this.queryClaimExpenseById(expenseId, accessToken);

		int code = SysOperLogConst.BusiCode.DriverExpense.getCode();
		String auditCode = com.youming.youche.record.common.AuditConsts.AUDIT_CODE.DriverExpense;
		if (ceo.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {//???????????? : ??????
			code = SysOperLogConst.BusiCode.TubeExpense.getCode();
			auditCode = com.youming.youche.record.common.AuditConsts.AUDIT_CODE.TubeExpense;
		}
		List<SysOperLog> sysOperLogs = sysOperLogService.querySysOperLogAll(code, expenseId, false, ceo.getTenantId(), auditCode, expenseId);

		ClaimExpenseInfoByIdDto dto = new ClaimExpenseInfoByIdDto();
		dto.setClaimExpenseInfo(ceo);
		dto.setSysOperLogs(sysOperLogs);
		return dto;
	}

	@Override
	public void driverExpenseAudit(Long expenseId, String desc, Integer chooseResult, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);

		ClaimExpenseInfo expenseInfo = this.getClaimExpenseInfo(expenseId);
		if (expenseInfo != null) {
			if (ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT == expenseInfo.getSts()) {
				expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.UNDER_AUDIT);
				this.saveOrUpdate(expenseInfo);
			}
		}
		expenseInfo.setVerifyUserInfoId(loginInfo.getId());
		expenseInfo.setVerifyUserName(loginInfo.getName());
		this.saveOrUpdate(expenseInfo);
		AuditCallbackDto auditCallbackDto = iAuditSettingService.sure(AuditConsts.AUDIT_CODE.DriverExpense, expenseId, desc, chooseResult, accessToken);
		//  ??????????????????
		if (null != auditCallbackDto && !auditCallbackDto.getIsNext()  && auditCallbackDto.getIsAudit() && chooseResult == AuditConsts.RESULT.SUCCESS){
			sucess(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),auditCallbackDto.getParamsMap(),accessToken);
			//  ??????????????????
		}else if (AuditConsts.RESULT.FAIL == chooseResult && null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()){
			fail(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),auditCallbackDto.getParamsMap(),accessToken);
		}
	}

	@Override
	public void tubeExpenseAudit(Long expenseId, String desc, Integer chooseResult, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		ClaimExpenseInfo expenseInfo = getClaimExpenseInfo(expenseId);
		if (null != expenseInfo.getSts() && ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT == expenseInfo.getSts()) {
			expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.UNDER_AUDIT);
			this.saveOrUpdate(expenseInfo);
		}

		expenseInfo.setVerifyUserInfoId(loginInfo.getId());
		expenseInfo.setVerifyUserName(loginInfo.getName());
		this.saveOrUpdate(expenseInfo);
		AuditCallbackDto auditCallbackDto = iAuditSettingService.sure(AuditConsts.AUDIT_CODE.TubeExpense, expenseId, desc, chooseResult, accessToken);
		//  ??????????????????
		if (null != auditCallbackDto && !auditCallbackDto.getIsNext()  && auditCallbackDto.getIsAudit() && chooseResult == AuditConsts.RESULT.SUCCESS){
			sucess(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),auditCallbackDto.getParamsMap(),accessToken);
			//  ??????????????????
		}else if (AuditConsts.RESULT.FAIL == chooseResult && null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()){
			fail(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),auditCallbackDto.getParamsMap(),accessToken);
		}

	}

	public SysStaticData getSysStaticData(String codeType, String codeValue) {
		List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
		if (list != null && list.size() > 0) {
			for (SysStaticData sysStaticData : list) {
				if (sysStaticData.getCodeValue().equals(codeValue)) {
					return sysStaticData;
				}
			}
		}
		return new SysStaticData();
	}


	public void payExpenseInfoOut(ClaimExpenseInfo expenseInfo, String vehicleAffiliation,String accessToken) {
			LoginInfo loginInfo = loginUtils.get(accessToken);
			Long tenantId = loginInfo.getTenantId();
			SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
			if(expenseInfo.getUserInfoId()<=0){
				throw new BusinessException("??????????????????ID!");
			}
			// TODO  ????????????
	//		boolean isLock = SysContexts.getLock(this.getClass().getName() + "payExpenseInfoOut" + expenseInfo.getOrderId() , 3, 5);
	//		if(!isLock){
	//			throw new BusinessException("????????????????????????????????????!");
	//		}

			// ??????userid??????????????????
			UserDataInfo sysOperator = iUserDataInfoService.getUserDataInfo(expenseInfo.getUserInfoId());
			if (sysOperator == null) {
				throw new BusinessException("????????????????????????!");
			}
			Long soNbr = CommonUtil.createSoNbr();

			// ????????????ID???????????????????????????????????????(??????)
			OrderAccount driverAccount = iOrderAccountService.queryOrderAccount(expenseInfo.getUserInfoId(), vehicleAffiliation, 0L,
				tenantId, vehicleAffiliation, expenseInfo.getReceUserType());
			List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
				// ????????????(????????????)
			BusiSubjectsRel amountFeeSubjectsRel1 = new BusiSubjectsRel();
			amountFeeSubjectsRel1.setSubjectsId(EnumConsts.SubjectIds.EXPENSE_RECEIVABLE);
			amountFeeSubjectsRel1.setAmountFee(expenseInfo.getAmount());
			busiList.add(amountFeeSubjectsRel1);
			// ??????????????????
			List<BusiSubjectsRel> busiSubjectsRelList =iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.DRIVER_EXPENSE_ABLE, busiList);
			// ????????????????????????????????????????????????
			iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,EnumConsts.PayInter.DRIVER_EXPENSE_ABLE,
					sysTenantDef.getAdminUser(), sysTenantDef.getShortName(),
					driverAccount,busiSubjectsRelList, soNbr, expenseInfo.getOrderId(),
					sysOperator.getLoginName(), null, tenantId, null, "",null, vehicleAffiliation,loginInfo);
			// ????????????ID???????????????????????????????????????(??????)
			OrderAccount tenantAccount = iOrderAccountService.queryOrderAccount(sysTenantDef.getAdminUser(), vehicleAffiliation, 0L,
					tenantId, vehicleAffiliation, expenseInfo.getPayUserType());
			List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
			// ????????????(????????????)
			BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
			amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.EXPENSE_HANDLE);
			amountFeeSubjectsRel.setAmountFee(expenseInfo.getAmount());
			tenantBusiList.add(amountFeeSubjectsRel);
			// ??????????????????
			List<BusiSubjectsRel> tenantSubjectsRels =iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.DRIVER_EXPENSE_ABLE, tenantBusiList);
			// ????????????????????????????????????????????????
			iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,EnumConsts.PayInter.DRIVER_EXPENSE_ABLE,
					sysOperator.getId(), sysOperator.getLinkman(), tenantAccount,tenantSubjectsRels,
					soNbr, expenseInfo.getOrderId(), sysOperator.getLoginName(), null, tenantId,
					null, "",null, vehicleAffiliation,loginInfo);
			//????????????
			sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense,
					expenseInfo.getId(),com.youming.youche.commons.constant.SysOperLogConst.OperType.Audit,
					"????????????????????????????????????:"+expenseInfo.getAmount()/100.00);
			//????????????????????????????????????
			AccountBankRel defalutAccountBankRel = iAccountBankRelService.getDefaultAccountBankRel(expenseInfo.getUserInfoId(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,
					expenseInfo.getReceUserType());
			// ??????????????????
			PayoutIntf payoutIntf = new PayoutIntf();
			payoutIntf.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15????????????(??????)
			payoutIntf.setObjId(Long.parseLong(sysOperator.getMobilePhone()));

			if(defalutAccountBankRel != null){
				payoutIntf.setBankCode(getSysStaticData("BANK_TYPE",defalutAccountBankRel.getBankId()+"").getCodeName());
				payoutIntf.setProvince(defalutAccountBankRel.getProvinceName());
				payoutIntf.setCity(defalutAccountBankRel.getCityName());
				payoutIntf.setAccNo(defalutAccountBankRel.getPinganCollectAcctId());
				payoutIntf.setAccName(defalutAccountBankRel.getAcctName());
			}
			payoutIntf.setVehicleAffiliation(vehicleAffiliation);
			payoutIntf.setTxnAmt(expenseInfo.getAmount());
			payoutIntf.setUserId(expenseInfo.getUserInfoId());
			//????????????????????????
			payoutIntf.setUserType(expenseInfo.getReceUserType());
			payoutIntf.setPayUserType(expenseInfo.getPayUserType());
			//????????????????????????
			payoutIntf.setCreateDate(new Date());
			payoutIntf.setOrderId(expenseInfo.getOrderId());
			//????????????????????????
			if(expenseInfo.getReceUserType() == SysStaticDataEnum.USER_TYPE.ADMIN_USER){
				payoutIntf.setTenantId(sysTenantDefService.getSysTenantDefByAdminUserId(expenseInfo.getUserInfoId()).getId());
			}else{
				payoutIntf.setTenantId(-1L);
			}
			payoutIntf.setPayTenantId(tenantId);
			payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);//??????-??????
			payoutIntf.setRemark("????????????");
			payoutIntf.setSourceRemark(SysStaticDataEnum.SOURCE_REMARK.SOURCE_REMARK_5);
			payoutIntf.setPayObjId(sysTenantDef.getAdminUser());
			payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);//????????????
			payoutIntf.setBusiId(EnumConsts.PayInter.DRIVER_EXPENSE_ABLE);
			payoutIntf.setSubjectsId(EnumConsts.SubjectIds.EXPENSE_RECEIVABLE);
			payoutIntf.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
			payoutIntf.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);//??????????????????
			payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
			if(expenseInfo.getIsNeedBill() == 0){
				payoutIntf.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL4);
				payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//????????????????????????
			}else if(expenseInfo.getIsNeedBill() == 1){
				payoutIntf.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL5);
				payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//????????????????????????
			}
			//??????????????????
			Boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(tenantId);
			/**
			 * ???????????? >> 2018-07-04 liujl
			 * ???????????????????????????????????????
			 * ???1??????????????????
			 *       A?????????????????????????????????????????????????????????
			 *       B???????????????????????????????????????????????????????????????
			 *         ?????????????????????????????????????????????????????????????????????????????????????????????payout_intf??????????????????????????????????????????????????????????????????,???????????????????????????payout_intf??????
			 *         ?????????????????????????????????????????????????????????payout_intf????????????????????????????????????????????????????????????????????????
			 * ???2????????????????????????
			 *       ????????????????????????????????????????????????payout_intf????????????????????????????????????????????????????????????????????????
			 */
			if (isAutoTransfer) {
				payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);//??????????????????
			} else {
				payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//????????????
			}
			payoutIntf.setBusiCode(expenseInfo.getSpecialExpenseNum());
			payoutIntf.setPlateNumber(expenseInfo.getPlateNumber());
			payoutIntf.setBankRemark("?????????:"+sysTenantDef.getName());
//			payOutIntfTF.doSavePayOutIntfForOA(payoutIntf);
			PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
			BeanUtil.copyProperties(payoutIntf,payoutIntfDto);
			iBaseBusiToOrderService.doSavePayOutIntfForOA(payoutIntfDto,accessToken);
			com.youming.youche.order.domain.order.PayoutIntf payoutIntfId = iPayoutIntfService.getPayoutIntfId(expenseInfo.getSpecialExpenseNum());
			expenseInfo.setFlowId(payoutIntfId.getId());
			//?????????????????????????????????????????????
			ParametersNewDto dto = iOrderOilSourceService.setParametersNew(sysOperator.getId(), sysOperator.getMobilePhone(),
					EnumConsts.PayInter.DRIVER_EXPENSE_ABLE, expenseInfo.getOrderId(), expenseInfo.getAmount(), vehicleAffiliation, "");
			dto.setExpenseId(expenseInfo.getId());
			dto.setFlowId(payoutIntfId.getId());
			SysTenantDefDto sysTenantDefDto = new SysTenantDefDto();
			BeanUtil.copyProperties(sysTenantDef,sysTenantDefDto);
			dto.setSysTenantDef(sysTenantDefDto);
			dto.setBatchId(soNbr.toString());
			busiSubjectsRelList.addAll(tenantSubjectsRels);
		iOrderOilSourceService.busiToOrderNew(dto,busiSubjectsRelList,loginInfo);
	}

	/**
	 * ????????????????????????????????????????????????
	 */
	public void payVehicleExpenseInfoOut(ClaimExpenseInfo expenseInfo, String vehicleAffiliation,String accessToken){
		LoginInfo loginInfo = loginUtils.get(accessToken);
		Long tenantId = loginInfo.getTenantId();
		SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
		if(expenseInfo.getBorrowUserId()<=0){
			throw new BusinessException("??????????????????ID!");
		}

		//??????userid??????????????????
		UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(expenseInfo.getBorrowUserId());
		if (userDataInfo == null) {
			throw new BusinessException("????????????????????????!");
		}

		Long soNbr = CommonUtil.createSoNbr();
		// ????????????ID???????????????????????????????????????(??????)
		OrderAccount driverAccount =iOrderAccountService.queryOrderAccount(expenseInfo.getBorrowUserId(),
				vehicleAffiliation,0L,
				tenantId,vehicleAffiliation,expenseInfo.getReceUserType());
		List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
		// ????????????(????????????)
		BusiSubjectsRel amountFeeSubjectsRel1 = new BusiSubjectsRel();
		amountFeeSubjectsRel1.setSubjectsId(EnumConsts.SubjectIds.EXPENSE_TUBE_RECEIVABLE);
		amountFeeSubjectsRel1.setAmountFee(expenseInfo.getAmount());
		busiList.add(amountFeeSubjectsRel1);
		// ??????????????????
		List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.TUBE_EXPENSE_ABLE, busiList);
		// ????????????????????????????????????????????????
		iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,
				EnumConsts.PayInter.TUBE_EXPENSE_ABLE, sysTenantDef.getAdminUser(),
				sysTenantDef.getShortName(), driverAccount,busiSubjectsRelList,soNbr,
				-1L, userDataInfo.getLoginName(), null, tenantId, null,
				"",null, vehicleAffiliation,loginInfo);
		// ????????????ID???????????????????????????????????????(??????)
		OrderAccount tenantAccount = iOrderAccountService.queryOrderAccount(sysTenantDef.getAdminUser(),
				vehicleAffiliation,0L, tenantId,vehicleAffiliation,expenseInfo.getPayUserType());
		List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
		// ????????????(????????????)
		BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
		amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.EXPENSE_TUBE_HANDLE);
		amountFeeSubjectsRel.setAmountFee(expenseInfo.getAmount());
		tenantBusiList.add(amountFeeSubjectsRel);
		// ??????????????????
		List<BusiSubjectsRel> tenantSubjectsRels =iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.TUBE_EXPENSE_ABLE, tenantBusiList);
		// ????????????????????????????????????????????????
		iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE,
				EnumConsts.PayInter.TUBE_EXPENSE_ABLE, userDataInfo.getId(), userDataInfo.getLinkman(),
				tenantAccount,tenantSubjectsRels, soNbr, -1L, userDataInfo.getLoginName(),
				null, tenantId, null, "",null, vehicleAffiliation,loginInfo);
		//????????????
		sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense,expenseInfo.getId(),
				com.youming.youche.commons.constant.SysOperLogConst.OperType.Audit, "???????????????????????????????????????"+expenseInfo.getAmount()/100.00);

		// ??????????????????
		PayoutIntf payoutIntf = new PayoutIntf();
		payoutIntf.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15????????????(??????)
		payoutIntf.setObjId(Long.parseLong(userDataInfo.getMobilePhone()));
		payoutIntf.setPinganCollectAcctId(expenseInfo.getCollectAcctId());
		payoutIntf.setAccNo(expenseInfo.getCollectAcctId());
		payoutIntf.setBankType(expenseInfo.getBankType());
		List<AccountBankRel> accountBankRels =iAccountBankRelService.queryAccountBankRel(expenseInfo.getBorrowUserId(), expenseInfo.getReceUserType(), null);
		if(accountBankRels!=null && accountBankRels.size()>0){
			for(AccountBankRel ac : accountBankRels){
				if(org.apache.commons.lang3.StringUtils.equals(expenseInfo.getCollectAcctId(),ac.getPinganCollectAcctId())){
					payoutIntf.setBankCode(getSysStaticData("BANK_TYPE",ac.getBankId()+"").getCodeName());
					payoutIntf.setProvince(ac.getProvinceName());
					payoutIntf.setCity(ac.getCityName());
					payoutIntf.setAccName(ac.getAcctName());
				}
			}
		}

		payoutIntf.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
		payoutIntf.setTxnAmt(expenseInfo.getAmount());
		payoutIntf.setUserId(expenseInfo.getBorrowUserId());
		//????????????????????????
		payoutIntf.setUserType(expenseInfo.getReceUserType());
		payoutIntf.setPayUserType(expenseInfo.getPayUserType());
		//????????????????????????
		payoutIntf.setCreateDate(new Date());
		payoutIntf.setOrderId(expenseInfo.getOrderId());
		//????????????????????????
		SysTenantDef sysTenantDef_rece = sysTenantDefService.getSysTenantDefByAdminUserId(expenseInfo.getBorrowUserId());
		if(expenseInfo.getReceUserType() == SysStaticDataEnum.USER_TYPE.ADMIN_USER && sysTenantDef_rece!=null){
			payoutIntf.setTenantId(sysTenantDef_rece.getId());
		}else{
			payoutIntf.setTenantId(-1L);
		}
		payoutIntf.setPayTenantId(tenantId);
		payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);//??????-??????
		payoutIntf.setRemark("????????????");
		payoutIntf.setSourceRemark(SysStaticDataEnum.SOURCE_REMARK.SOURCE_REMARK_2);
		payoutIntf.setPayObjId(sysTenantDef.getAdminUser());
		payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);//????????????
		payoutIntf.setBusiId(EnumConsts.PayInter.TUBE_EXPENSE_ABLE);
		payoutIntf.setSubjectsId(EnumConsts.SubjectIds.EXPENSE_TUBE_RECEIVABLE);
		if(expenseInfo.getReceUserType()==SysStaticDataEnum.USER_TYPE.SERVICE_USER){
			payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.SERVICE);
		}else if(expenseInfo.getReceUserType()==SysStaticDataEnum.USER_TYPE.CUSTOMER_USER){
			payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.STAFF);
		}else{
			payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.USER);
		}

		payoutIntf.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);//??????????????????

		if(expenseInfo.getIsNeedBill() == 0){
			payoutIntf.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL4);
			payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//????????????????????????
		}else if(expenseInfo.getIsNeedBill() == 1){
			payoutIntf.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL5);
			payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//????????????????????????
		}
		//??????????????????
		Boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(tenantId);
		/**
		 * ???????????? >> 2018-07-04 liujl
		 * ???????????????????????????????????????
		 * ???1??????????????????
		 *       A?????????????????????????????????????????????????????????
		 *       B???????????????????????????????????????????????????????????????
		 *         ?????????????????????????????????????????????????????????????????????????????????????????????payout_intf??????????????????????????????????????????????????????????????????,???????????????????????????payout_intf??????
		 *         ?????????????????????????????????????????????????????????payout_intf????????????????????????????????????????????????????????????????????????
		 * ???2????????????????????????
		 *       ????????????????????????????????????????????????payout_intf????????????????????????????????????????????????????????????????????????
		 */
		if (isAutoTransfer) {
			payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);//??????????????????
		} else {
			payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//????????????
		}
		payoutIntf.setBusiCode(expenseInfo.getSpecialExpenseNum());
		payoutIntf.setPlateNumber(expenseInfo.getPlateNumber());
		payoutIntf.setBankRemark("?????????:"+sysTenantDef.getName());
		PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
		BeanUtil.copyProperties(payoutIntf,payoutIntfDto);
		iBaseBusiToOrderService.doSavePayOutIntfForOA(payoutIntfDto,accessToken);
		// ?????????????????? ?????????????????????
		com.youming.youche.order.domain.order.PayoutIntf payoutIntfId = iPayoutIntfService.getPayoutIntfId(expenseInfo.getSpecialExpenseNum());
		expenseInfo.setFlowId(payoutIntfId.getId());
		//?????????????????????????????????????????????
		ParametersNewDto dto = iOrderOilSourceService.setParametersNew(userDataInfo.getId(), userDataInfo.getMobilePhone(),
				EnumConsts.PayInter.TUBE_EXPENSE_ABLE, -1L, expenseInfo.getAmount(), OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, "");
		dto.setExpenseId(expenseInfo.getId());
		dto.setFlowId(payoutIntfId.getId());
		SysTenantDefDto sysTenantDefDto = new SysTenantDefDto();
		BeanUtil.copyProperties(sysTenantDef,sysTenantDefDto);
		dto.setSysTenantDef(sysTenantDefDto);
		dto.setBatchId(soNbr.toString());
		dto.setExpenseType(ClaimExpenseConsts.EXPENSE_TYPE.VEHICLE);
		busiSubjectsRelList.addAll(tenantSubjectsRels);
		iOrderOilSourceService.busiToOrderNew(dto,busiSubjectsRelList,loginInfo);
	}

	@Override
	public List<QueryClaimExpenseInfoDto> queryClaimExpenseInfoByOrderId(Long orderId, String accessToken) {
		LoginInfo user = loginUtils.get(accessToken);
		if(orderId<=0){
			throw new BusinessException("???????????????id");
		}
		QueryWrapper<ClaimExpenseInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("order_id",orderId).eq("user_info_id",user.getUserInfoId()).eq("expense_type",OaLoanConsts.EXPENSE_TYPE.DRIVER)
				.orderByDesc("app_date");
		List<ClaimExpenseInfo> list = this.list(queryWrapper);
		List<QueryClaimExpenseInfoDto> listOut = new ArrayList<>();
		for (ClaimExpenseInfo expenseInfo : list) {
			QueryClaimExpenseInfoDto queryClaimExpenseInfoDto = new QueryClaimExpenseInfoDto();
			queryClaimExpenseInfoDto.setAmount(expenseInfo.getAmount());
			queryClaimExpenseInfoDto.setAppReason(expenseInfo.getAppReason());
			queryClaimExpenseInfoDto.setExpenseId(expenseInfo.getId());
			List<String> fileId = new ArrayList<String>();//??????id
			List<String> fileUrl = new ArrayList<String>();//??????????????????
			List<OaFiles> oaFiles = iClaimExpenseInfoService.queryOaFilesByRelType4(expenseInfo.getId(),expenseInfo.getTenantId());
			if (oaFiles != null) {
				for (OaFiles of : oaFiles) {
					if (of.getRelType().equals(OaLoanData.RELTYPE4)) {
						fileId.add(String.valueOf(of.getFileId()));
						fileUrl.add(of.getFileUrl());
					}
				}
			}
			queryClaimExpenseInfoDto.setImgIds(fileId);
			queryClaimExpenseInfoDto.setImgUrls(fileUrl);
			queryClaimExpenseInfoDto.setOrderId(expenseInfo.getOrderId());
			queryClaimExpenseInfoDto.setPlateNumber(expenseInfo.getPlateNumber());
			queryClaimExpenseInfoDto.setSpecialExpenseNum(expenseInfo.getSpecialExpenseNum());
			queryClaimExpenseInfoDto.setStairCategoryId(expenseInfo.getStairCategory());
			queryClaimExpenseInfoDto.setStairCategoryName(expenseInfo.getStairCategoryName());
			queryClaimExpenseInfoDto.setIsNeedBill(expenseInfo.getIsNeedBill());
			queryClaimExpenseInfoDto.setIsNeedBillName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil,user.getTenantId(),"IS_NEED_BILL_OA", expenseInfo.getIsNeedBill() + ""));
			queryClaimExpenseInfoDto.setSts(expenseInfo.getSts());
			queryClaimExpenseInfoDto.setUserId(expenseInfo.getUserInfoId());
			queryClaimExpenseInfoDto.setUserName(expenseInfo.getUserInfoName());
			queryClaimExpenseInfoDto.setAppDate(expenseInfo.getAppDate());
			queryClaimExpenseInfoDto.setStsName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil,user.getTenantId(),"EXPENSE_STS", expenseInfo.getSts()+""));
			queryClaimExpenseInfoDto.setVerifyNotes(expenseInfo.getVerifyNotes());
			Double weightFee=new Double(0);
			if(expenseInfo.getWeightFee()!=null){
				weightFee = expenseInfo.getWeightFee()/1000.0;
			}
			queryClaimExpenseInfoDto.setWeightFee(weightFee);
			listOut.add(queryClaimExpenseInfoDto);
		}
		return listOut;
	}

	@Override
	public QueryClaimExpenseInfoDto getClaimExpenseInfoByIds(Long expenseId, String accessToken) {
		LoginInfo user = loginUtils.get(accessToken);
		ClaimExpenseInfo expenseInfo = this.getById(expenseId);
		if (expenseInfo == null) {
			throw new BusinessException("?????????????????????");
		}
		QueryClaimExpenseInfoDto queryClaimExpenseInfoDto = new QueryClaimExpenseInfoDto();
		queryClaimExpenseInfoDto.setAmount(expenseInfo.getAmount());
		queryClaimExpenseInfoDto.setAppReason(expenseInfo.getAppReason());
		queryClaimExpenseInfoDto.setExpenseId(expenseInfo.getExpenseId());
		queryClaimExpenseInfoDto.setOrderId(expenseInfo.getOrderId());
		queryClaimExpenseInfoDto.setPlateNumber(expenseInfo.getPlateNumber());
		queryClaimExpenseInfoDto.setSpecialExpenseNum(expenseInfo.getSpecialExpenseNum());
		queryClaimExpenseInfoDto.setStairCategoryId(expenseInfo.getStairCategory());
		queryClaimExpenseInfoDto.setStairCategoryName(expenseInfo.getStairCategoryName());
		queryClaimExpenseInfoDto.setIsNeedBill(expenseInfo.getIsNeedBill());
		queryClaimExpenseInfoDto.setIsNeedBillName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil,user.getTenantId(),"IS_NEED_BILL_OA", expenseInfo.getIsNeedBill() + ""));
		queryClaimExpenseInfoDto.setSts(expenseInfo.getSts());
		queryClaimExpenseInfoDto.setUserId(expenseInfo.getUserInfoId());
		queryClaimExpenseInfoDto.setUserName(expenseInfo.getUserInfoName());
		queryClaimExpenseInfoDto.setAppDate(expenseInfo.getAppDate());
		queryClaimExpenseInfoDto.setStsName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil,user.getTenantId(),"EXPENSE_STS", expenseInfo.getSts()+""));
		queryClaimExpenseInfoDto.setVerifyNotes(expenseInfo.getVerifyNotes());
		queryClaimExpenseInfoDto.setExpenseId(expenseId);
		double weightFee=0;
		if(expenseInfo.getWeightFee()!=null){
			weightFee = expenseInfo.getWeightFee()/1000.0;
		}

		queryClaimExpenseInfoDto.setWeightFee(weightFee);
		List<String> fileId = new ArrayList<String>();//??????id
		List<String> fileUrl = new ArrayList<String>();//??????????????????
		List<OaFiles> oaFiles = iClaimExpenseInfoService.queryOaFilesByRelType4(expenseId,expenseInfo.getTenantId());
		if (oaFiles != null) {
			for (OaFiles of : oaFiles) {
				if (of.getRelType().equals(OaLoanData.RELTYPE4)) {
					fileId.add(String.valueOf(of.getFileId()));
					fileUrl.add(of.getFileUrl());
				}
			}
		}

		List<SysOperLog> sysOperLogs = sysOperLogService.querySysOperLogAll(SysOperLogConst.BusiCode.DriverExpense.getCode(),
				expenseId, false,expenseInfo.getTenantId(),AuditConsts.AUDIT_CODE.DriverExpense,expenseId);
		queryClaimExpenseInfoDto.setImgIds(fileId);
		queryClaimExpenseInfoDto.setImgUrls(fileUrl);
		queryClaimExpenseInfoDto.setSysOperLogs(sysOperLogs);
		return queryClaimExpenseInfoDto;
	}

	@Override
	public String saveOrUpdateExpenseDriver(ClaimExpenseInfoInDto infoIn,String accessToken) {
		if (infoIn.getStairCategory() <= 0) {
			throw new BusinessException("???????????????????????????");
		}
		if (org.apache.commons.lang3.StringUtils.isEmpty(infoIn.getAmountString())) {
			throw new BusinessException("?????????????????????????????????");
		}
		if (infoIn.getStairCategory() == SysStaticDataEnum.DRIVER_EXPENSE_TYPE.EXPENSE_CATEGORY2) {//?????????????????????????????????
			if (infoIn.getWeightFee() == null || infoIn.getWeightFee() <= 0) {
				throw new BusinessException("??????????????????????????????");
			}
		}
		if (org.apache.commons.lang3.StringUtils.isEmpty(infoIn.getPlateNumber())) {
			throw new BusinessException("???????????????????????????");
		}
		if (org.apache.commons.lang3.StringUtils.isEmpty(infoIn.getAppReason())) {
			throw new BusinessException("?????????????????????????????????");
		}
		if (infoIn.getAccUserId() <= 0) {
			throw new BusinessException("???????????????????????????");
		}
		return this.saveOrUpdateExpenseDrivers(infoIn,accessToken);
	}

	private String saveOrUpdateExpenseDrivers(ClaimExpenseInfoInDto infoIn,String accessToken) {
		LoginInfo user = loginUtils.get(accessToken);
		Long amount = 0l;
		Long weightFee = 0l;
		if (CommonUtil.isNumber(infoIn.getAmountString())) {
			Double convertNum = Double.parseDouble(infoIn.getAmountString());
			amount = convertNum.longValue();
		} else {
			throw new BusinessException("???????????????????????????????????????");
		}
		if(amount<=0){
			throw new BusinessException("??????????????????????????????0???");
		}
		if (infoIn.getIsNeedBill() < 0) {
			throw new BusinessException("????????????????????????????????????");
		}
		if (CommonUtil.isNumber(infoIn.getWeightFee()+"")) {
			weightFee = infoIn.getWeightFee();
		} else {
			throw new BusinessException("???????????????????????????????????????");
		}
		UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(infoIn.getAccUserId());
		if (userDataInfo == null) {
			throw new BusinessException("???????????????????????????");
		}


		//????????????????????????????????????
		AccountBankRel defalutAccountBankRel =iAccountBankRelService.getDefaultAccountBankRel(infoIn.getAccUserId(),EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,SysStaticDataEnum.USER_TYPE.DRIVER_USER);

		Date appDate = new Date();
		String falg = "??????";
		ClaimExpenseInfo expenseInfo = new ClaimExpenseInfo();
		com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense;//????????????
		com.youming.youche.commons.constant.SysOperLogConst.OperType operType = com.youming.youche.commons.constant.SysOperLogConst.OperType.Add;
		Long incr = redisUtil.incr(EnumConsts.RemoteCache.EXPENSE_NUMBER);
		if (infoIn.getType() == 1) {//??????
			BeanUtil.copyProperties( infoIn,expenseInfo);
			expenseInfo.setUserInfoPhone(infoIn.getCarPhone());
			expenseInfo.setSpecialExpenseNum(CommonUtil.createExpenseNumber(incr));//??????????????????
			expenseInfo.setState(ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT);//?????????
			expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT);
			if (infoIn.getStairCategory() == SysStaticDataEnum.DRIVER_EXPENSE_TYPE.EXPENSE_CATEGORY2) {
				expenseInfo.setWeightFee(weightFee);
			}
			expenseInfo.setStairCategoryName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil,user.getTenantId(),"EXPENSE_CATEGORY", String.valueOf(infoIn.getStairCategory())));
			expenseInfo.setAppDate(getDateToLocalDateTime(appDate));//????????????
			expenseInfo.setUserInfoId(userDataInfo.getId());
			expenseInfo.setUserInfoName(userDataInfo.getLinkman());
			expenseInfo.setAmount(amount);
			expenseInfo.setBankType(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0);
			expenseInfo.setBorrowPhone(userDataInfo.getMobilePhone());
			expenseInfo.setBorrowName(userDataInfo.getLinkman());
			expenseInfo.setBorrowUserId(userDataInfo.getId());
			expenseInfo.setCarOwnerName(userDataInfo.getLinkman());
			expenseInfo.setCarPhone(userDataInfo.getMobilePhone());
			expenseInfo.setExpenseType(2);//????????????
		} else if (infoIn.getType() == 2) {//??????
			expenseInfo = getClaimExpenseInfo(infoIn.getExpenseId());
			if (expenseInfo.getSts() == ClaimExpenseConsts.EXPENSE_STS.UNDER_AUDIT) {
				throw new BusinessException("???????????????,????????????");
			}
			if (expenseInfo.getSts() == ClaimExpenseConsts.EXPENSE_STS.AUDIT_PASS) {
				throw new BusinessException("??????????????????,????????????");
			}
			if (expenseInfo.getSts() == ClaimExpenseConsts.EXPENSE_STS.CANCEL) {
				throw new BusinessException("????????????????????????,????????????");
			}
			if (expenseInfo.getSts() == ClaimExpenseConsts.EXPENSE_STS.AUDIT_NOT_PASS) {//?????? ?????????????????????
				expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT);//?????????
			}
			if (infoIn.getStairCategory() == SysStaticDataEnum.DRIVER_EXPENSE_TYPE.EXPENSE_CATEGORY2) {
				expenseInfo.setWeightFee(weightFee);
			}
			expenseInfo.setUserInfoPhone(infoIn.getCarPhone());
			expenseInfo.setPlateNumber(infoIn.getPlateNumber());
			expenseInfo.setOrderId(Long.parseLong(infoIn.getOrderId()));
			expenseInfo.setStairCategory(infoIn.getStairCategory());
			expenseInfo.setStairCategoryName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil,user.getTenantId(),"EXPENSE_CATEGORY", String.valueOf(infoIn.getStairCategory())));
			expenseInfo.setAmount(amount);
			expenseInfo.setAppReason(infoIn.getAppReason());
			expenseInfo.setIsNeedBill(infoIn.getIsNeedBill());
			//??????????????????
			operType = com.youming.youche.commons.constant.SysOperLogConst.OperType.Update;
			falg = "??????";
		}
		if(defalutAccountBankRel != null){
			expenseInfo.setAccName(defalutAccountBankRel.getAcctName());
			expenseInfo.setAccNo(defalutAccountBankRel.getAcctNo());
			expenseInfo.setBankName(defalutAccountBankRel.getBankName());
			expenseInfo.setBankBranch(defalutAccountBankRel.getBranchName());
			expenseInfo.setCollectAcctId(defalutAccountBankRel.getPinganCollectAcctId());
		}
		//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		if(org.apache.commons.lang3.StringUtils.isNotBlank(infoIn.getOrderId())){
			long orderId = Long.parseLong(infoIn.getOrderId());
			long tenantId = -1;
			long orgId = -1;
			OrderInfo orderInfo = orderInfoService.getOrder(orderId);
			if (orderInfo == null) {
				OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
				if (orderInfoH == null) {
					throw new BusinessException("?????????????????????");
				}
				tenantId = orderInfoH.getTenantId();
				orgId = orderInfoH.getOrgId();
			}else{
				tenantId = orderInfo.getTenantId();
				orgId = orderInfo.getOrgId();
			}
			expenseInfo.setTenantId(tenantId);
			expenseInfo.setOrgId(orgId);
		}else{
			expenseInfo.setOrgId(user.getId());
			expenseInfo.setTenantId(user.getTenantId());
		}
		expenseInfo.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
		expenseInfo.setReceUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
		this.saveOrUpdate(expenseInfo);

		oaLoanService.saveFileForLoanExpense(expenseInfo.getId(),expenseInfo.getTenantId(),expenseInfo.getUserInfoId(),
				infoIn.getImgIds(),OaLoanData.RELTYPE4);
		//??????????????????
		sysOperLogService.saveSysOperLog(user,busi_code,expenseInfo.getExpenseId(),operType,falg+"????????????",user.getTenantId());
		//??????????????????
		Map<String, Object> params = new ConcurrentHashMap<String, Object>();
		params.put("expenseId", expenseInfo.getId());
		Boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.DriverExpense, expenseInfo.getId(),
				com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense, params,accessToken,user.getTenantId());
		if (!bool) {
			throw new BusinessException("???????????????????????????");
		}
		return "Y";
	}
	private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
		Date date;
		ZoneId zoneId = ZoneId.systemDefault();
		ZonedDateTime zdt = localDateTime.atZone(zoneId);
		date = Date.from(zdt.toInstant());
		return date;
	}

	private LocalDateTime getDateToLocalDateTime(Date date) {
		Instant instant = date.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
		return localDateTime;
	}

	@Override
	public String cancelClaimExpenses(Long expenseId, String accessToken) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		ClaimExpenseInfo expenseInfo = claimExpenseInfoService.getClaimExpenseInfo(expenseId);

		if (expenseId <= 0) {
			throw new BusinessException("??????ID??????");
		}
		if (expenseInfo == null) {
			throw new BusinessException("?????????????????????");
		}
		if (expenseInfo.getSts() == ClaimExpenseConsts.EXPENSE_STS.UNDER_AUDIT) {
			throw new BusinessException("???????????????,????????????");
		}
		if (expenseInfo.getSts() == ClaimExpenseConsts.EXPENSE_STS.AUDIT_PASS) {
			throw new BusinessException("??????????????????,????????????");
		}
		if (expenseInfo.getSts() == ClaimExpenseConsts.EXPENSE_STS.CANCEL) {
			throw new BusinessException("????????????????????????,??????????????????");
		}
		expenseInfo.setSts(ClaimExpenseConsts.EXPENSE_STS.CANCEL);
		this.saveOrUpdate(expenseInfo);
		//??????????????????
		String auditCode = AuditConsts.AUDIT_CODE.DriverExpense;
		com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverExpense;//????????????
		if (expenseInfo.getExpenseType() == 1) {//???????????? : ??????
			busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeExpense;//????????????
			auditCode = AuditConsts.AUDIT_CODE.TubeExpense;
		}
		sysOperLogService.saveSysOperLog(loginInfo,busi_code, expenseInfo.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update,
				"??????????????????",loginInfo.getTenantId());
		//??????????????????
		try{
			auditOutService.cancelProcess(auditCode, expenseInfo.getId(),expenseInfo.getTenantId());
		}catch (Exception e){
			e.printStackTrace();
		}
		return "Y";
	}

	@Override
	public void notice(String accessToken,Integer nodeIndex, Long busiId, Integer targetObjectType, String targetObjId) throws Exception {
		if(targetObjectType == 2){
			Arrays.asList(targetObjId.split(",")).forEach(userId->{
				try {
					UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(Long.parseLong(userId));
					ClaimExpenseInfo out = this.queryClaimExpenseById(busiId,accessToken);
					Map paraMap = new HashMap();
					paraMap.put("auditMan",userDataInfo.getLinkman());
					paraMap.put("applyMan",out.getUserInfoName());
					paraMap.put("amount", CommonUtil.getDoubleFormatLongMoney(out.getAmount(), 2));
					paraMap.put("busiId",out.getSpecialExpenseNum());
					paraMap.put("busiName","??????");
					String url = "../ac/expense/driverClaimExpense.html";
					if(out.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE){
						url = "../ac/expense/vehicleClaimExpense.html";
					}
					//Web??????
					sysSmsSendService.sendSms(userDataInfo.getMobilePhone(), EnumConsts.SmsTemplate.AUDIT_TIP, SysStaticDataEnum.WEB_SMS_TYPE.OPEN_TAB ,8,url, paraMap,accessToken);
					//?????????????????????
					List<SysStaticData> staticData = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("WX_AUDIT_TEMP"));
					if(staticData!=null && staticData.size()>0){

						Map map = new HashMap();
						map.put("wxTemplateId",staticData.get(0).getCodeValue());
						if(out.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE){
							map.put("page","packages/special-expense/list/specialExpense?expenseType=1&waitDeal=true");
						}else{
							map.put("page","packages/special-expense/list/specialExpense?expenseType=2&waitDeal=true");
						}
						ItemObj itemObj = new ItemObj();
						itemObj.setValue("????????????");
						map.put("first",itemObj);

						ItemObj itemObj1 = new ItemObj();
						itemObj1.setValue(out.getUserInfoName());
						map.put("keyword1",itemObj1);

						ItemObj itemObj2 = new ItemObj();
						itemObj2.setValue("??????");
						map.put("keyword2",itemObj2);

						ItemObj itemObj3 = new ItemObj();
						itemObj3.setValue(CommonUtil.getDoubleFormatLongMoney(out.getAmount(), 2)+"");
						map.put("keyword3",itemObj3);

						ItemObj itemObj4 = new ItemObj();
						itemObj4.setValue(out.getSpecialExpenseNum());
						map.put("keyword4",itemObj4);

						ItemObj itemObj5 = new ItemObj();
						itemObj5.setValue("????????????");
						map.put("keyword5",itemObj5);

						ItemObj itemObjR = new ItemObj();
						itemObjR.setValue("????????????");
						map.put("remark",itemObjR);
						sysSmsSendService.sendWxSms(userDataInfo.getMobilePhone(), EnumConsts.SmsTemplate.AUDIT_TIP, map,accessToken);
					}
				} catch (Exception e) {
					log.error("??????????????????????????????????????????"+e.getMessage());
				}
			});
		}
	}

	class ItemObj{
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}
}
