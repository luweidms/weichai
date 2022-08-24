package com.youming.youche.finance.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.finance.api.IVehicleExpenseService;
import com.youming.youche.finance.commons.FinanceResponseCode;
import com.youming.youche.finance.constant.FinanceBusinessConstant;
import com.youming.youche.finance.domain.VehicleExpense;
import com.youming.youche.finance.dto.AccountQueryDetailDto;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.dto.VehicleListByDriverDto;
import com.youming.youche.finance.dto.VehicleListDto;
import com.youming.youche.finance.dto.order.OrderMainReportDto;
import com.youming.youche.finance.provider.mapper.VehicleExpenseDetailedMapper;
import com.youming.youche.finance.provider.mapper.VehicleExpenseMapper;
import com.youming.youche.finance.vo.CreateVehicleExpenseVo;
import com.youming.youche.finance.vo.GetVehicleExpenseVo;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.youming.youche.record.common.SysStaticDataEnum.AUTH_STATE.AUTH_STATE2;
import static com.youming.youche.record.common.SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1;

/**
 * <p>
 * 车辆费用表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-23
 */
@DubboService(version = "1.0.0")
public class VehicleExpenseServiceImpl extends BaseServiceImpl<VehicleExpenseMapper, VehicleExpense>
		implements IVehicleExpenseService {

	@Resource
	RedisUtil redisUtil;
	@Resource
	private LoginUtils loginUtils;
	@Resource
	VehicleExpenseMapper vehicleExpenseMapper;
	@Resource
	VehicleExpenseDetailedMapper vehicleExpenseDetailedMapper;
	@DubboReference(version = "1.0.0")
	ImportOrExportRecordsService importOrExportRecordsService;
	@DubboReference(version = "1.0.0")
	IAuditOutService auditOutService;
	@DubboReference(version = "1.0.0")
	IOrderSchedulerService orderSchedulerService;
	@DubboReference(version = "1.0.0")
	IOrderSchedulerHService orderSchedulerHService;
	@Override
	public Long saveOrUpdate(CreateVehicleExpenseVo vehicleExpenseVo, String accessToken) {
		LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
		VehicleExpense vehicleExpense = new VehicleExpense();
		BeanUtil.copyProperties(vehicleExpenseVo, vehicleExpense);
		LocalDateTime now = LocalDateTime.now();
		vehicleExpense.setOpId(loginInfo.getId());
//		vehicleExpense.setState(FinanceBusinessConstant.EXPENSE_TYPE.WAIT);
		vehicleExpense.setTenantId(loginInfo.getTenantId());
//		vehicleExpense.setApplyTime(now);
		boolean b = saveOrUpdate(vehicleExpense);
		if (!b) {
			throw new BusinessException(FinanceResponseCode.DATA_UPDATE_FAIL.message());
		}
		return vehicleExpense.getId();
	}

	@Override
	public IPage<GetVehicleExpenseVo> getVehicleExpense(GetVehicleExpenseDto dto, String accessToken) {
		Page<GetVehicleExpenseVo> page =new Page<>(dto.getPageNum(),dto.getPageSize());
		LoginInfo loginInfo = loginUtils.get(accessToken);
		dto.setTenantId(loginInfo.getTenantId());
		String beginApplyTime = dto.getBeginApplyTime();
		String endApplyTime = dto.getEndApplyTime();
		if(beginApplyTime != null && StringUtils.isNotEmpty(beginApplyTime)){
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String applyTimeStr = beginApplyTime+" 00:00:00";
			LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
			dto.setBeginApplyTime1(beginApplyTime1);
		}
		if(endApplyTime != null && StringUtils.isNotEmpty(endApplyTime)){
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String endApplyTimeStr = endApplyTime+" 23:59:59";
			LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
			dto.setEndApplyTime1(endApplyTime1);
		}
		List<Long> lids = new ArrayList<>();
		if(dto.getWaitDeal()){
			//TODO 审核流程
			lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());
			String auditCodeTube = AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE;// 车管报销
			// 查询数据id不为空的情况 获取待审核的id
			Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
			if(!lids.isEmpty()) {
				Map<Long, Boolean> hasPermission = auditOutService.isHasPermission(accessToken, auditCodeTube, lids);
				hasPermissionMap.putAll(hasPermission);
			}
			if(lids != null && lids.size() > 0){
				List<GetVehicleExpenseVo> vehicleExpenseVoByPage = vehicleExpenseMapper.getVehicleExpenseVoByPage(dto,lids);
				for (GetVehicleExpenseVo getVehicleExpenseVo: vehicleExpenseVoByPage) {
					GetVehicleExpenseDto getVehicleExpenseDto = new GetVehicleExpenseDto();
					getVehicleExpenseDto.setApplyNo(getVehicleExpenseVo.getApplyNo());
					getVehicleExpenseDto.setTenantId(loginInfo.getTenantId());
					boolean isTrue=false;
					if(hasPermissionMap.get(getVehicleExpenseVo.getVehicleExpenseId())!=null&&hasPermissionMap.get(getVehicleExpenseVo.getVehicleExpenseId())){
						isTrue=true;
					}
					getVehicleExpenseVo.setIsHasPermission(isTrue);
					Long sumApplyAmount = vehicleExpenseDetailedMapper.sumApplyAmount(getVehicleExpenseDto);
					getVehicleExpenseVo.setApplyAmount(sumApplyAmount);
				}
				if(dto.getBeginApplyAmount() == null){
					dto.setBeginApplyAmount(0L);
				}
				List<GetVehicleExpenseVo> collect = null;
				if(dto.getEndApplyAmount() == null){
					collect = vehicleExpenseVoByPage.stream().filter(o -> o.getApplyAmount() >= dto.getBeginApplyAmount()).collect(Collectors.toList());
				}else {
					collect = vehicleExpenseVoByPage.stream().filter(o -> o.getApplyAmount() >= dto.getBeginApplyAmount() && o.getApplyAmount() <= dto.getEndApplyAmount()).collect(Collectors.toList());

				}
				List<GetVehicleExpenseVo> getVehicleExpenseVos =null;
				if(collect.size() < (dto.getPageNum() - 1) * (dto.getPageSize()) + dto.getPageSize()){
					getVehicleExpenseVos = collect.subList((dto.getPageNum() - 1) * (dto.getPageSize()), collect.size());
				}else {
					getVehicleExpenseVos = collect.subList((dto.getPageNum() - 1) * (dto.getPageSize()), (dto.getPageNum() - 1) * (dto.getPageSize()) + dto.getPageSize());
				}
				IPage<GetVehicleExpenseVo> pageList = new Page<>();
				pageList.setRecords(getVehicleExpenseVos);
				pageList.setTotal(collect.size());
				pageList.setCurrent(dto.getPageNum());
				pageList.setSize(dto.getPageSize());
				pageList.setPages(collect.size() % dto.getPageSize() == 0 ? collect.size() / dto.getPageSize() : (collect.size() / dto.getPageSize() + 1));
//				PageInfo<GetVehicleExpenseVo> customerInfoPageInfo = new PageInfo<>(collect);
				return pageList;
			}
			return new Page<GetVehicleExpenseVo>();
		}else {

			//TODO 审核流程
			lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());
			String auditCodeTube = AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE;// 车管报销
			// 查询数据id不为空的情况 获取待审核的id
			Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
			if(!lids.isEmpty()) {
				Map<Long, Boolean> hasPermission = auditOutService.isHasPermission(accessToken, auditCodeTube, lids);
				hasPermissionMap.putAll(hasPermission);
			}

			List<GetVehicleExpenseVo> vehicleExpenseVoByPage = vehicleExpenseMapper.getVehicleExpenseVoByPage(dto,new ArrayList<>());
			for (GetVehicleExpenseVo getVehicleExpenseVo: vehicleExpenseVoByPage) {
				GetVehicleExpenseDto getVehicleExpenseDto = new GetVehicleExpenseDto();
				getVehicleExpenseDto.setApplyNo(getVehicleExpenseVo.getApplyNo());
				getVehicleExpenseDto.setTenantId(loginInfo.getTenantId());
				boolean isTrue=false;
				if(hasPermissionMap.get(getVehicleExpenseVo.getVehicleExpenseId())!=null&&hasPermissionMap.get(getVehicleExpenseVo.getVehicleExpenseId())){
					isTrue=true;
				}
				getVehicleExpenseVo.setIsHasPermission(isTrue);
				Long sumApplyAmount = vehicleExpenseDetailedMapper.sumApplyAmount(getVehicleExpenseDto);
				getVehicleExpenseVo.setApplyAmount(sumApplyAmount);
			}

			if(dto.getBeginApplyAmount() == null){
				dto.setBeginApplyAmount(0L);
			}
			List<GetVehicleExpenseVo> collect = null;
			if(dto.getEndApplyAmount() == null){
				collect = vehicleExpenseVoByPage.stream().filter(o -> o.getApplyAmount() >= dto.getBeginApplyAmount()).collect(Collectors.toList());
			}else {
				collect = vehicleExpenseVoByPage.stream().filter(o -> o.getApplyAmount() >= dto.getBeginApplyAmount() && o.getApplyAmount() <= dto.getEndApplyAmount()).collect(Collectors.toList());
			}
			List<GetVehicleExpenseVo> getVehicleExpenseVos =null;
			if(collect.size() < (dto.getPageNum() - 1) * (dto.getPageSize()) + dto.getPageSize()){
				getVehicleExpenseVos = collect.subList((dto.getPageNum() - 1) * (dto.getPageSize()), collect.size());
			}else {
				getVehicleExpenseVos = collect.subList((dto.getPageNum() - 1) * (dto.getPageSize()), (dto.getPageNum() - 1) * (dto.getPageSize()) + dto.getPageSize());
			}
			IPage<GetVehicleExpenseVo> pageList = new Page<>();
			pageList.setRecords(getVehicleExpenseVos);
			pageList.setTotal(collect.size());
			pageList.setCurrent(dto.getPageNum());
			pageList.setSize(dto.getPageSize());
			pageList.setPages(collect.size() % dto.getPageSize() == 0 ? collect.size() / dto.getPageSize() : (collect.size() / dto.getPageSize() + 1));
//			PageInfo<GetVehicleExpenseVo> customerInfoPageInfo = new PageInfo<>(collect);
			return pageList;
		}


	}

	@Override
	public void vehicleExpenseOutList(GetVehicleExpenseDto dto, String accessToken, ImportOrExportRecords record) {
		LoginInfo loginInfo = loginUtils.get(accessToken);
		dto.setTenantId(loginInfo.getTenantId());
		dto.setPageSize(99999);
		//获得导出数据
		IPage<GetVehicleExpenseVo> vehicleExpense = getVehicleExpense(dto, accessToken);
		List<GetVehicleExpenseVo> records = vehicleExpense.getRecords();
		try {
			String[] showName = null;
			String[] resourceFild = null;
			//TODO 获取数据为空的处理
			if (records != null && records.size() > 0) {
				for (GetVehicleExpenseVo getVehicleExpenseVo: records) {
					Long applyAmount1 = getVehicleExpenseVo.getApplyAmount();
					String applyAmountStr = "";
					if(applyAmount1 == null){
						applyAmountStr = "0";
					}else {
						double applyAmount=applyAmount1.doubleValue()/100;
						applyAmountStr = String.valueOf(applyAmount);
					}
					getVehicleExpenseVo.setApplyAmountStr(applyAmountStr);
				}

			}
			showName = new String[]{"申请单号", "状态",  "申请人", "申请时间", "车牌号", "申请金额", "费用归属" };
			resourceFild = new String[]{"getApplyNo", "getStateStr", "getApplyName", "getApplyTime", "getPlateNumber", "getApplyAmountStr", "getExpenseDepartment"};
			XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, GetVehicleExpenseVo.class,
					null);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			workbook.write(os);
			byte[] b = os.toByteArray();
			InputStream inputStream = new ByteArrayInputStream(b);
			//上传文件
			FastDFSHelper client = FastDFSHelper.getInstance();
			String path = client.upload(inputStream, "车辆费用信息导出.xlsx", inputStream.available());
			os.close();
			inputStream.close();
			record.setMediaUrl(path);
			record.setState(2);
			importOrExportRecordsService.saveOrUpdate(record);
		} catch (Exception e) {
			record.setState(3);
			importOrExportRecordsService.saveOrUpdate(record);
			e.printStackTrace();
		}
	}

	@Override
	public List<WorkbenchDto> getTableVehicleCostCount() {
		return baseMapper.getTableVehicleCostCount();
	}

	@Override
	public List<WorkbenchDto> getTableVehicleCostMeCount() {
		return baseMapper.getTableVehicleCostMeCount();
	}

	@Override
	public VehicleListDto getVehicleList(Long userId, Long orderId, String accessToken) {
		VehicleListDto vehicleListDto = new VehicleListDto();
		Long tenantId = 0L;
		String trailerPlate = "";
		String plateNumberDef = "";
		OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
		if(orderScheduler == null){
			OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
			tenantId = orderSchedulerH.getTenantId();
			trailerPlate = orderSchedulerH.getTrailerPlate();
			plateNumberDef = orderSchedulerH.getPlateNumber();
		}else{
			tenantId = orderScheduler.getTenantId();
			trailerPlate = orderScheduler.getTrailerPlate();
			plateNumberDef = orderScheduler.getPlateNumber();
		}
		List<VehicleListByDriverDto> list = this.getVehicleListByDriverUserId(userId);
		List canSelPlateNumbers = new ArrayList();
		if(list!=null && list.size()>0){
			for(VehicleListByDriverDto map:list){
				if(org.apache.commons.lang3.StringUtils.equals(map.getTenantId().toString(),tenantId+"")){
					canSelPlateNumbers.add(map.getPlateNumber());
				}
			}
		}
		if(orderScheduler!=null){
			if(org.apache.commons.lang3.StringUtils.isNotBlank(trailerPlate)){
				canSelPlateNumbers.add(trailerPlate);
			}
			if(!canSelPlateNumbers.contains(plateNumberDef)){
				canSelPlateNumbers.add(plateNumberDef);
			}
		}
		vehicleListDto.setPlateNumberDef(plateNumberDef);
		vehicleListDto.setCanSelPlateNumbers(canSelPlateNumbers);
		return vehicleListDto;
	}

	@Override
	public Integer getStatisticsCarCost(String accessToken) {

		LoginInfo loginInfo = loginUtils.get(accessToken);

		GetVehicleExpenseDto dto = new GetVehicleExpenseDto();
		dto.setTenantId(loginInfo.getTenantId());

		List<Long> lids = new ArrayList<>();
		lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());

		String auditCodeTube = AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE;// 车管报销

		// 查询数据id不为空的情况 获取待审核的id
		Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
		if (!lids.isEmpty()) {
			Map<Long, Boolean> hasPermission = auditOutService.isHasPermission(accessToken, auditCodeTube, lids);
			hasPermissionMap.putAll(hasPermission);
		}

		if (lids != null && lids.size() > 0) {
			List<GetVehicleExpenseVo> vehicleExpenseVoByPage = vehicleExpenseMapper.getVehicleExpenseVoByPage(dto, lids);

//			lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE, loginInfo.getUserInfoId(), loginInfo.getTenantId());
//			baseMapper.getCarCostReportAuditCount(loginInfo.getTenantId(), lids);

			return vehicleExpenseVoByPage.size();
		}
		return 0;
	}

	private List<VehicleListByDriverDto> getVehicleListByDriverUserId(Long driverUserId) {
		List<VehicleListByDriverDto> vehicleListByDriverDto = vehicleExpenseMapper.selectOr(driverUserId);

		List<VehicleListByDriverDto> resList = new ArrayList<>();
		if (null != vehicleListByDriverDto && !vehicleListByDriverDto.isEmpty()) {
			for (int i = 0; i < vehicleListByDriverDto.size(); i++) {
				VehicleListByDriverDto outMap = vehicleListByDriverDto.get(i);
				Long state = outMap.getAuthState();
				if (state == AUTH_STATE2 || state == -1) {
					resList.add(outMap);
				}
			}
		}
		return vehicleListByDriverDto;
	}

}
