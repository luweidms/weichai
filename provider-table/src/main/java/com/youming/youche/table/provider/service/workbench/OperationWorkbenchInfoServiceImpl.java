package com.youming.youche.table.provider.service.workbench;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.capital.api.IOilAccountService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.api.IClaimExpenseInfoService;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.api.IVehicleExpenseService;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.api.order.IOrderInfoThreeService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.record.api.cm.ICmCustomerInfoService;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.api.service.IServiceRepairOrderService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.trailer.ITrailerManagementService;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.api.vehicle.IVehicleAnnualReviewService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.table.api.statistic.IStatementCustomerDayService;
import com.youming.youche.table.api.statistic.IStatementDepartmentDayService;
import com.youming.youche.table.api.statistic.IStatementDepartmentMonthService;
import com.youming.youche.table.api.vehicleReport.IVehicleReportService;
import com.youming.youche.table.api.workbench.IOperationWorkbenchInfoService;
import com.youming.youche.table.domain.workbench.BossWorkbenchCustomerInfo;
import com.youming.youche.table.domain.workbench.BossWorkbenchDayInfo;
import com.youming.youche.table.domain.workbench.BossWorkbenchInfo;
import com.youming.youche.table.domain.workbench.BossWorkbenchMonthInfo;
import com.youming.youche.table.domain.workbench.FinancialWorkbenchInfo;
import com.youming.youche.table.domain.workbench.OperationWorkbenchInfo;
import com.youming.youche.table.domain.workbench.WechatOperationWorkbenchInfo;
import com.youming.youche.table.dto.workbench.BossWorkbenchInfoDto;
import com.youming.youche.table.dto.workbench.FinancialWorkbenchInfoDto;
import com.youming.youche.table.dto.workbench.WechatOperationWorkbenchInfoDto;
import com.youming.youche.table.provider.mapper.workbench.BossWorkbenchCustomerInfoMapper;
import com.youming.youche.table.provider.mapper.workbench.BossWorkbenchDayInfoMapper;
import com.youming.youche.table.provider.mapper.workbench.BossWorkbenchInfoMapper;
import com.youming.youche.table.provider.mapper.workbench.BossWorkbenchMonthInfoMapper;
import com.youming.youche.table.provider.mapper.workbench.FinancialWorkbenchInfoMapper;
import com.youming.youche.table.provider.mapper.workbench.OperationWorkbenchInfoMapper;
import com.youming.youche.table.provider.mapper.workbench.WechatOperationWorkbenchInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zengwen
 * @since 2022-04-30
 */
@Slf4j
@DubboService(version = "1.0.0")
public class OperationWorkbenchInfoServiceImpl extends BaseServiceImpl<OperationWorkbenchInfoMapper, OperationWorkbenchInfo> implements IOperationWorkbenchInfoService {

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IVehicleAnnualReviewService iVehicleAnnualReviewService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    ITrailerManagementService iTrailerManagementService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IUserService iUserService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    ITenantVehicleRelService iTenantVehicleRelService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IServiceRepairOrderService iServiceRepairOrderService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IVehicleExpenseService iVehicleExpenseService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IClaimExpenseInfoService iClaimExpenseInfoService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IOaLoanThreeService iOaLoanThreeService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IOrderInfoService iOrderInfoService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IOrderAgingInfoService iOrderAgingInfoService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    ICmCustomerInfoService iCmCustomerInfoService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    ICmCustomerLineService iCmCustomerLineService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IServiceInfoService iServiceInfoService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IServiceProductService iServiceProductService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    ISysUserService iSysUserService;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IBankAccountService iBankAccountService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IOilAccountService iOilAccountService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IPayoutIntfThreeService iPayoutIntfThreeService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    IOrderInfoThreeService iOrderInfoThreeService;

    @DubboReference(version = "1.0.0", timeout = 10000000)
    ISysTenantDefService sysTenantDefService;

    @Resource
    FinancialWorkbenchInfoMapper financialWorkbenchInfoMapper;

    @Resource
    IStatementDepartmentDayService statementDepartmentDayService;

    @Resource
    IStatementDepartmentMonthService statementDepartmentMonthService;

    @Resource
    BossWorkbenchInfoMapper bossWorkbenchInfoMapper;

    @Resource
    BossWorkbenchDayInfoMapper bossWorkbenchDayInfoMapper;

    @Resource
    BossWorkbenchMonthInfoMapper bossWorkbenchMonthInfoMapper;

    @Resource
    BossWorkbenchCustomerInfoMapper bossWorkbenchCustomerInfoMapper;

    @Resource
    IStatementCustomerDayService iStatementCustomerDayService;

    @Resource
    WechatOperationWorkbenchInfoMapper wechatOperationWorkbenchInfoMapper;

    @Resource
    IVehicleReportService vehicleReportService;

    @Override
    public void initOperationWorkbenchInfoData() {
        Long startTime = System.currentTimeMillis();
        log.info("initOperationWorkbenchInfoData query start :" + startTime);

        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime maxLocalDateTime = baseMapper.maxLocalDateTime();
        maxLocalDateTime = maxLocalDateTime == null ? localDateTime : maxLocalDateTime;

        // 车辆年审预警数量
        List<WorkbenchDto> vehicleAnnualReviewDtoList = iVehicleAnnualReviewService.getTableVehicleCount();

        // 挂车年审预警数量
        List<WorkbenchDto> trailerAnnualReviewDtoList = iTrailerManagementService.getTableTrailerCount();

        // 驾驶证预警数量
        List<WorkbenchDto> driverLicenseReivewDtoList = iUserService.getTableUserDriverCount();

        // 从业资格预警数量
        List<WorkbenchDto> occupationRequireReivewDtoList = iUserService.getTableUserQcCertiCount();

        // 交强险预警数量
        List<WorkbenchDto> compulsoryInsuranceCountReviewDtoList = iTenantVehicleRelService.getTableCompulsoryInsuranceCount();

        // 商业险预警数量
        List<WorkbenchDto> commercialInsuranceReviewDtoList = iTenantVehicleRelService.getTableCommercialInsuranceCount();

        // 导入失败次数
        List<WorkbenchDto> importFailReviewDtoList = importOrExportRecordsService.getTableImportFailCount();

        // 自有车数量
        List<WorkbenchDto> ownerCarReviewDtoList = iTenantVehicleRelService.getTableOwnerCarCount();

        // 外调车数量
        List<WorkbenchDto> temporaryCarReviewDtoList = iTenantVehicleRelService.getTableTemporaryCarCount();

        // 招商车数量
        List<WorkbenchDto> attractCarReviewDtoList = iTenantVehicleRelService.getTableAttractCarCount();

        // 挂车数量
        List<WorkbenchDto> trailerCarReviewDtoList = iTrailerManagementService.getTableTrailerCarCount();

        // 维保费用 待我审
        List<WorkbenchDto> maintenanceReviewDtoList = iServiceRepairOrderService.getTableMaintenanceCount();

        // 维保费用 我的
        List<WorkbenchDto> maintenanceMeReviewDtoList = iServiceRepairOrderService.getTableMaintenanceMeCount();

        // 车辆费用  待我审
        List<WorkbenchDto> vehicleCostReviewDtoList = iVehicleExpenseService.getTableVehicleCostCount();

        // 车辆费用  我的
        List<WorkbenchDto> vehicleCostMeReviewDtoList = iVehicleExpenseService.getTableVehicleCostMeCount();

        // 管理费用  待我审
        List<WorkbenchDto> manageCostReviewDtoList = iClaimExpenseInfoService.getTableManageCostCount();

        // 管理费用 我的
        List<WorkbenchDto> manageCostMeReviewDtoList = iClaimExpenseInfoService.getTableManageCostMeCount();

        // 费用借支  待我审
        List<WorkbenchDto> costBorrowReviewDtoList = iOaLoanThreeService.getTableCostBorrowCount();

        // 费用借支  我的
        List<WorkbenchDto> costBorrowMeReviewDtoList = iOaLoanThreeService.getTableCostBorrowMeCount();

        // 改单审核
        List<WorkbenchDto> modifyExamineReviewDtoList = iOrderInfoService.getTableModifyExamineCount();

        // 时效审核
        List<WorkbenchDto> invalidExamineReviewDtoList = iOrderAgingInfoService.getTableInvalidExamineCount();

        // 异常审核
        List<WorkbenchDto> exceptionExamineReviewDtoList = iOrderInfoService.getTableExceptionExamineCount();

        // 回单审核
        List<WorkbenchDto> receiptExamineReviewDtoList = iOrderInfoService.getTableReceiptExamineCount();

        // 今日开单数
        List<WorkbenchDto> billTodayReviewDtoList = iOrderInfoService.getTableBillTodayCount();

        // 待预付数量
        List<WorkbenchDto> prepaidOrderReviewDtoList = iOrderInfoService.getTablePrepaidOrderCount();

        // 在途数量
        List<WorkbenchDto> intransitOrderReviewDtoList = iOrderInfoService.getTableIntransitOrderCount();

        // 回单审核数量
        List<WorkbenchDto> retrialOrderReviewDtoList = iOrderInfoService.getTableRetrialOrderCount();

        // 客户档案数
        List<WorkbenchDto> customerReviewDtoList = iCmCustomerInfoService.getTableCustomerCount();

        // 线路档案数
        List<WorkbenchDto> lineReviewDtoList = iCmCustomerLineService.getTableLineCount();

        // 司机档案数
        List<WorkbenchDto> driverReviewDtoList = iUserService.getTableDriverCount();

        // 服务商数量
        List<WorkbenchDto> managerReviewDtoList = iServiceInfoService.getTableManagerCount();

        // 服务商站点数量
        List<WorkbenchDto> managerSiteReviewDtoList = iServiceProductService.getTableManagerSiteCount();

        List<SysUser> sysUserAll = iSysUserService.getSysUserAll();
        for (SysUser sysUser : sysUserAll) {
            Long tenantId = -1L;
            Long userInfoId = sysUser.getUserInfoId();

            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userInfoId);
            if(sysTenantDef != null && sysTenantDef.getId() != null) {
                tenantId = sysTenantDef.getId();
            }else{
                tenantId =  sysUser.getTenantId();
            }
            OperationWorkbenchInfo operationWorkbenchInfo = new OperationWorkbenchInfo();
            operationWorkbenchInfo.setVehicleAnnualReviewAmount(getListToValueByTId(vehicleAnnualReviewDtoList, tenantId));
            operationWorkbenchInfo.setTrailerAnnualReviewAmount(getListToValueByTId(trailerAnnualReviewDtoList, tenantId));
            operationWorkbenchInfo.setDriverLicenseReviewAmount(getListToValueByTId(driverLicenseReivewDtoList, tenantId));
            operationWorkbenchInfo.setOccupationRequireReviewAmount(getListToValueByTId(occupationRequireReivewDtoList, tenantId));
            operationWorkbenchInfo.setCompulsoryInsuranceReviewAmount(getListToValueByTId(compulsoryInsuranceCountReviewDtoList, tenantId));
            operationWorkbenchInfo.setCommercialInsuranceReviewAmount(getListToValueByTId(commercialInsuranceReviewDtoList, tenantId));
            operationWorkbenchInfo.setImportFailReviewAmount(getListToValueByTId(importFailReviewDtoList, tenantId));
            operationWorkbenchInfo.setOwnCarSumReviewAmount(getListToValueByTId(ownerCarReviewDtoList, tenantId));
            operationWorkbenchInfo.setTemporaryCarSumReviewAmount(getListToValueByTId(temporaryCarReviewDtoList, tenantId));
            operationWorkbenchInfo.setAttractCarSumReviewAmount(getListToValueByTId(attractCarReviewDtoList, tenantId));
            operationWorkbenchInfo.setTrailerCarSumReviewAmount(getListToValueByTId(trailerCarReviewDtoList, tenantId));
            operationWorkbenchInfo.setCarSumReviewAmount(operationWorkbenchInfo.getOwnCarSumReviewAmount() + operationWorkbenchInfo.getTemporaryCarSumReviewAmount()
                    + operationWorkbenchInfo.getAttractCarSumReviewAmount() + operationWorkbenchInfo.getTrailerCarSumReviewAmount());
            operationWorkbenchInfo.setMaintenanceReviewAmount(getListToValueByTId(maintenanceReviewDtoList, tenantId));
            operationWorkbenchInfo.setMaintenanceMeReviewAmount(getListToValueByUId(maintenanceMeReviewDtoList, userInfoId));
            operationWorkbenchInfo.setVehicleCostReviewAmount(getListToValueByTUId(vehicleCostReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setVehicleCostMeReviewAmount(getListToValueByTUId(vehicleCostMeReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setManageCostReviewAmount(getListToValueByTUId(manageCostReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setManageCostMeReviewAmount(getListToValueByTUId(manageCostMeReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setCostBorrowReviewAmount(getListToValueByTUId(costBorrowReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setCostBorrowMeReviewAmount(getListToValueByTUId(costBorrowMeReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setModifyExamineReviewAmount(getListToValueByTUId(modifyExamineReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setInvalidExamineReviewAmount(getListToValueByTUId(invalidExamineReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setExceptionExamineReviewAmount(getListToValueByTUId(exceptionExamineReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setReceiptExamineReviewAmount(getListToValueByTUId(receiptExamineReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setBillTodayReviewAmount(getListToValueByTId(billTodayReviewDtoList, tenantId));
            operationWorkbenchInfo.setPrepaidOrderReviewAmount(getListToValueByTId(prepaidOrderReviewDtoList, tenantId));
            operationWorkbenchInfo.setIntransitOrderReviewAmount(getListToValueByTId(intransitOrderReviewDtoList, tenantId));
            operationWorkbenchInfo.setRetrialOrderReviewAmount(getListToValueByTId(retrialOrderReviewDtoList, tenantId));
            operationWorkbenchInfo.setCustomerReviewAmount(getListToValueByTId(customerReviewDtoList, tenantId));
            operationWorkbenchInfo.setLineReviewAmount(getListToValueByTId(lineReviewDtoList, tenantId));
            operationWorkbenchInfo.setDriverReviewAmount(getListToValueByTId(driverReviewDtoList, tenantId));
            operationWorkbenchInfo.setCarReviewAmount(operationWorkbenchInfo.getCarSumReviewAmount());
            operationWorkbenchInfo.setManagerReviewAmount(getListToValueByTUId(managerReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setManagerSiteReviewAmount(getListToValueByTUId(managerSiteReviewDtoList, tenantId, userInfoId));
            operationWorkbenchInfo.setTenantId(tenantId);
            operationWorkbenchInfo.setUserInfoId(userInfoId);
            operationWorkbenchInfo.setCreateTime(localDateTime);
            operationWorkbenchInfo.setUpdateTime(localDateTime);

            OperationWorkbenchInfo info = baseMapper.selectOperationWorkbenchInfoNew(tenantId, userInfoId, maxLocalDateTime);
            if (null != info) {
                operationWorkbenchInfo.setId(info.getId());
                baseMapper.updateById(operationWorkbenchInfo);
            } else {
                baseMapper.insert(operationWorkbenchInfo);
            }
        }

        log.info("initOperationWorkbenchInfoData enca data end :" + (System.currentTimeMillis() - startTime));
    }

    private Map<Long, Long> listToMap(List<WorkbenchDto> list) {
        Map<Long, Long> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (WorkbenchDto workbenchDto : list) {
                map.put(workbenchDto.getTenantId(), workbenchDto.getCount());
            }
        }

        return map;
    }

    private Map<Long, Double> listToMapDouble(List<WorkbenchDto> list) {
        Map<Long, Double> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (WorkbenchDto workbenchDto : list) {
                map.put(workbenchDto.getTenantId(), workbenchDto.getPrice());
            }
        }

        return map;
    }

    private Long getMapValue(Map<Long, Long> map, Long tenantId) {
        return map.getOrDefault(tenantId, 0L) != null ? map.getOrDefault(tenantId, 0L) : 0L;
    }

    private Long getMapValueDouble(Map<Long, Double> map, Long tenantId) {
        Double doubleValue = new Double(0);
        return new Double((map.getOrDefault(tenantId, doubleValue) != null ? map.getOrDefault(tenantId, doubleValue) : doubleValue) * 100).longValue();
    }

    private Long getListToValueByTUId(List<WorkbenchDto> workbenchDtoList, Long tenantId, Long userInfoId) {
        if (CollectionUtils.isEmpty(workbenchDtoList)) {
            return 0L;
        }
        Optional<WorkbenchDto> optional = workbenchDtoList.stream().filter(item -> item.getTenantId().equals(tenantId) && item.getUserInfoId().equals(userInfoId)).findFirst();
        if (optional.isPresent()) {
            return optional.get().getCount();
        } else {
            return 0L;
        }
    }

    private Long getListToValueByTId(List<WorkbenchDto> workbenchDtoList, Long tenantId) {
        if (CollectionUtils.isEmpty(workbenchDtoList)) {
            return 0L;
        }
        Optional<WorkbenchDto> optional = workbenchDtoList.stream().filter(item -> item.getTenantId().equals(tenantId)).findFirst();
        if (optional.isPresent()) {
            return optional.get().getCount();
        } else {
            return 0L;
        }
    }

    private Long getListToValueByUId(List<WorkbenchDto> workbenchDtoList, Long userInfoId) {
        if (CollectionUtils.isEmpty(workbenchDtoList)) {
            return 0L;
        }
        Optional<WorkbenchDto> optional = workbenchDtoList.stream().filter(item -> item.getUserInfoId().equals(userInfoId)).findFirst();
        if (optional.isPresent()) {
            return optional.get().getCount();
        } else {
            return 0L;
        }
    }

    @Override
    public OperationWorkbenchInfo getOperationWorkbenchInfo(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = -1L;
        Long userId = loginInfo.getUserInfoId();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        if (sysTenantDef != null && sysTenantDef.getId() != null) {
            tenantId = sysTenantDef.getId();
        } else {
            tenantId = loginInfo.getTenantId();
        }

        LambdaQueryWrapper<OperationWorkbenchInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OperationWorkbenchInfo::getTenantId, tenantId);
        queryWrapper.eq(OperationWorkbenchInfo::getUserInfoId, userId);
        queryWrapper.orderByDesc(OperationWorkbenchInfo::getCreateTime);
        queryWrapper.last("limit 1");

        OperationWorkbenchInfo operationWorkbenchInfo = baseMapper.selectOne(queryWrapper);
        if (operationWorkbenchInfo == null) {
            operationWorkbenchInfo = new OperationWorkbenchInfo();
            operationWorkbenchInfo.setVehicleAnnualReviewAmount(0L);
            operationWorkbenchInfo.setTrailerAnnualReviewAmount(0L);
            operationWorkbenchInfo.setDriverLicenseReviewAmount(0L);
            operationWorkbenchInfo.setOccupationRequireReviewAmount(0L);
            operationWorkbenchInfo.setCompulsoryInsuranceReviewAmount(0L);
            operationWorkbenchInfo.setCommercialInsuranceReviewAmount(0L);
            operationWorkbenchInfo.setImportFailReviewAmount(0L);
            operationWorkbenchInfo.setCarSumReviewAmount(0L);
            operationWorkbenchInfo.setOwnCarSumReviewAmount(0L);
            operationWorkbenchInfo.setTemporaryCarSumReviewAmount(0L);
            operationWorkbenchInfo.setAttractCarSumReviewAmount(0L);
            operationWorkbenchInfo.setTrailerCarSumReviewAmount(0L);
            operationWorkbenchInfo.setMaintenanceReviewAmount(0L);
            operationWorkbenchInfo.setMaintenanceMeReviewAmount(0L);
            operationWorkbenchInfo.setVehicleCostReviewAmount(0L);
            operationWorkbenchInfo.setVehicleCostMeReviewAmount(0L);
            operationWorkbenchInfo.setManageCostReviewAmount(0L);
            operationWorkbenchInfo.setManageCostMeReviewAmount(0L);
            operationWorkbenchInfo.setCostBorrowReviewAmount(0L);
            operationWorkbenchInfo.setCostBorrowMeReviewAmount(0L);
            operationWorkbenchInfo.setModifyExamineReviewAmount(0L);
            operationWorkbenchInfo.setInvalidExamineReviewAmount(0L);
            operationWorkbenchInfo.setExceptionExamineReviewAmount(0L);
            operationWorkbenchInfo.setReceiptExamineReviewAmount(0L);
            operationWorkbenchInfo.setBillTodayReviewAmount(0L);
            operationWorkbenchInfo.setPrepaidOrderReviewAmount(0L);
            operationWorkbenchInfo.setIntransitOrderReviewAmount(0L);
            operationWorkbenchInfo.setRetrialOrderReviewAmount(0L);
            operationWorkbenchInfo.setCustomerReviewAmount(0L);
            operationWorkbenchInfo.setLineReviewAmount(0L);
            operationWorkbenchInfo.setDriverReviewAmount(0L);
            operationWorkbenchInfo.setCarReviewAmount(0L);
            operationWorkbenchInfo.setManagerReviewAmount(0L);
            operationWorkbenchInfo.setManagerSiteReviewAmount(0L);
        }

        return operationWorkbenchInfo;
    }

    @Override
    public void initFinancialWorkbenchInfoData() {

        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime maxLocalDateTime = financialWorkbenchInfoMapper.maxLocalDateTime();
        maxLocalDateTime = maxLocalDateTime == null ? localDateTime : maxLocalDateTime;

        // 平台剩余金额
        Map<Long, Double> platformSurplusAmountMap = listToMapDouble(iBankAccountService.getTableFinancialPlatformSurplusAmount());

        // 平台已用金额
        Map<Long, Long> platformUsedAmountMap = listToMap(iBankAccountService.getTableFinacialPlatformUsedAmount());

        // 平台剩余金额
        Map<Long, Double> surplusAmountMap = platformSurplusAmountMap;

        // 油账户已用
        Map<Long, Long> oilUsedAmountMap = listToMap(iOilAccountService.getTableFinancialOilUsedAmount());

        // 油账户剩余
        Map<Long, Long> oilSurpleAmountMap = listToMap(iOilAccountService.getTableFinacialOilSurpleAmount());

        // 应收账户  已收金额
        Map<Long, Long> receivableReceivedAmountMap = listToMap(iOrderInfoThreeService.getTableFinancialReceivableReceivedAmount());

        // 应收账户  剩余金额
        Map<Long, Long> receivableSurplusAmountMap = listToMap(iOrderInfoThreeService.getTableFinancialReceivableSurplusAmount());

        // 应付账户已付
        Map<Long, Long> payablePaidAmountMap = listToMap(iPayoutIntfThreeService.getTableFinancialPayablePaidAmount());

        // 应付账户剩余
        Map<Long, Long> payableSurplusAmountMap = listToMap(iPayoutIntfThreeService.getTableFinancialPayableSurplusAmount());

        // 应收逾期金额
        List<WorkbenchDto> overdueReceivablesAmount = iPayoutIntfThreeService.getTableFinancialOverdueReceivablesAmount();

        // 应付逾期金额
        List<WorkbenchDto> overduePayableAmount = iPayoutIntfThreeService.getTableFinancialOverduePayableAmount();

        // 今日充值
        Map<Long, Long> rechargeTodayAmountMap = listToMap(iBankAccountService.getTableFinacialRechargeTodayAmount());

        // 待付款
        Map<Long, Long> pendingPaymentAmountMap = listToMap(iPayoutIntfThreeService.getTableFinancialPendingPaymentAmount());

        List<SysUser> sysUserAll = iSysUserService.getSysUserAll();
        for (SysUser sysUser : sysUserAll) {
            Long tenantId = -1L;
            Long userInfoId = sysUser.getUserInfoId();
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userInfoId);
            if (sysTenantDef != null && sysTenantDef.getId() != null) {
                tenantId = sysTenantDef.getId();
            } else {
                tenantId = sysUser.getTenantId();
            }
            FinancialWorkbenchInfo financialWorkbenchInfo = new FinancialWorkbenchInfo();
            financialWorkbenchInfo.setTenantId(tenantId);
            financialWorkbenchInfo.setUserInfoId(userInfoId);
            financialWorkbenchInfo.setPlatformSurplusAmount(getMapValueDouble(platformSurplusAmountMap, tenantId));
            financialWorkbenchInfo.setPlatformUsedAmount(getMapValue(platformUsedAmountMap, tenantId));
            financialWorkbenchInfo.setSurplusAmount(getMapValueDouble(surplusAmountMap, tenantId));
            financialWorkbenchInfo.setOilUsedAmount(getMapValue(oilUsedAmountMap, tenantId));
            financialWorkbenchInfo.setOilSurplusAmount(getMapValue(oilSurpleAmountMap, tenantId));
            financialWorkbenchInfo.setReceivableReceivedAmount(getMapValue(receivableReceivedAmountMap, tenantId));
            financialWorkbenchInfo.setReceivableSurplusAmount(getMapValue(receivableSurplusAmountMap, tenantId));
            financialWorkbenchInfo.setPayablePaidAmount(getMapValue(payablePaidAmountMap, tenantId));
            financialWorkbenchInfo.setPayableSurplusAmount(getMapValue(payableSurplusAmountMap, tenantId));
            financialWorkbenchInfo.setOverdueReceivablesAmount(getListToValueByTUId(overdueReceivablesAmount, tenantId, userInfoId));
            financialWorkbenchInfo.setOverduePayableAmount(getListToValueByTUId(overduePayableAmount, tenantId, userInfoId));
            financialWorkbenchInfo.setRechargeTodayAmount(getMapValue(rechargeTodayAmountMap, tenantId));
            financialWorkbenchInfo.setPendingPaymentAmount(getMapValue(pendingPaymentAmountMap, tenantId));
            financialWorkbenchInfo.setCreateTime(localDateTime);
            financialWorkbenchInfo.setUpdateTime(localDateTime);

            FinancialWorkbenchInfo info = financialWorkbenchInfoMapper.selectFinancialWorkbenchInfoNew(tenantId, userInfoId, maxLocalDateTime);
            if (info != null) {
                financialWorkbenchInfo.setId(info.getId());
                financialWorkbenchInfoMapper.updateById(financialWorkbenchInfo);
            } else {
                financialWorkbenchInfoMapper.insert(financialWorkbenchInfo);
            }
        }
    }

    @Override
    public FinancialWorkbenchInfoDto getFinancialWorkbenchInfo(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = -1L;
        Long userInfoId = loginInfo.getUserInfoId();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userInfoId);
        if (sysTenantDef != null && sysTenantDef.getId() != null) {
            tenantId = sysTenantDef.getId();
        } else {
            tenantId = loginInfo.getTenantId();
        }
        LambdaQueryWrapper<FinancialWorkbenchInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FinancialWorkbenchInfo::getTenantId, tenantId);
        queryWrapper.eq(FinancialWorkbenchInfo::getUserInfoId, userInfoId);
        queryWrapper.orderByDesc(FinancialWorkbenchInfo::getCreateTime);
        queryWrapper.last("limit 1");

        FinancialWorkbenchInfo financialWorkbenchInfo = financialWorkbenchInfoMapper.selectOne(queryWrapper);
        OperationWorkbenchInfo operationWorkbenchInfo = getOperationWorkbenchInfo(accessToken);

        FinancialWorkbenchInfoDto financialWorkbenchInfoDto = new FinancialWorkbenchInfoDto();

        BeanUtil.copyProperties(financialWorkbenchInfo, financialWorkbenchInfoDto);

        financialWorkbenchInfoDto.setCostBorrowReviewAmount(operationWorkbenchInfo.getCostBorrowReviewAmount());
        financialWorkbenchInfoDto.setCostBorrowMeReviewAmount(operationWorkbenchInfo.getCostBorrowMeReviewAmount());
        financialWorkbenchInfoDto.setMaintenanceMeReviewAmount(operationWorkbenchInfo.getMaintenanceMeReviewAmount());
        financialWorkbenchInfoDto.setMaintenanceReviewAmount(operationWorkbenchInfo.getMaintenanceReviewAmount());
        financialWorkbenchInfoDto.setVehicleCostMeReviewAmount(operationWorkbenchInfo.getVehicleCostMeReviewAmount());
        financialWorkbenchInfoDto.setVehicleCostReviewAmount(operationWorkbenchInfo.getVehicleCostReviewAmount());
        financialWorkbenchInfoDto.setManageCostReviewAmount(operationWorkbenchInfo.getManageCostReviewAmount());
        financialWorkbenchInfoDto.setManageCostMeReviewAmount(operationWorkbenchInfo.getManageCostMeReviewAmount());
        return financialWorkbenchInfoDto;
    }

    @Override
    public void initBossWorkbenchInfoData() {
        LocalDateTime localDateTime = LocalDateTime.now();
        // 付款审核
        Map<Long, Long> paymentReviewAmountMap = listToMap(iPayoutIntfThreeService.getTableBossPaymentReviewAmount());

        // 每日运营报表
        List<BossWorkbenchDayInfo> businessDayInfoList = statementDepartmentDayService.getTableBossBusinessDayInfo();

        // 每月运营报表
        List<BossWorkbenchMonthInfo> businessMonthInfoList = statementDepartmentMonthService.getTableBossBusinessMonthInfo();

        // 车辆费用
        List<BossWorkbenchInfo> bossWorkbenchCarInfoList = vehicleReportService.getTableVehicleList();

        // 客户收入排行
        List<BossWorkbenchCustomerInfo> customerInfoList = iStatementCustomerDayService.getTableBossCustomerInfo();

        LocalDateTime maxBossWorkbenchInfoTime = bossWorkbenchInfoMapper.maxLocalDateTime();
        maxBossWorkbenchInfoTime = maxBossWorkbenchInfoTime == null ? localDateTime : maxBossWorkbenchInfoTime;

        List<SysUser> sysUserAll = iSysUserService.getSysUserAll();

        List<Long> tenantIds = new ArrayList<>();
        for (SysUser sysUser : sysUserAll) {
            Long tenantId = -1L;
            Long userInfoId = sysUser.getUserInfoId();
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userInfoId);
            if (sysTenantDef != null && sysTenantDef.getId() != null) {
                tenantId = sysTenantDef.getId();
            } else {
                tenantId = sysUser.getTenantId();
            }

            if (!tenantIds.contains(tenantId)) {
                tenantIds.add(tenantId);
            }
        }

        for (Long tenantId : tenantIds) {

            BossWorkbenchInfo workbenchInfo = new BossWorkbenchInfo();
            workbenchInfo.setTenantId(tenantId);
            workbenchInfo.setPaymentReviewAmount(getMapValue(paymentReviewAmountMap, tenantId));
            workbenchInfo.setCarCostEtc(new BigDecimal(0));
            workbenchInfo.setCarCostOil(new BigDecimal(0));
            workbenchInfo.setCarCostMaintenance(new BigDecimal(0));
            workbenchInfo.setCarCostOther(new BigDecimal(0));

            for (BossWorkbenchInfo carInfo : bossWorkbenchCarInfoList) {
                if (tenantId == carInfo.getTenantId()) {
                    workbenchInfo.setCarCostEtc(carInfo.getCarCostEtc());
                    workbenchInfo.setCarCostOil(carInfo.getCarCostOil());
                    workbenchInfo.setCarCostMaintenance(carInfo.getCarCostMaintenance());
                    workbenchInfo.setCarCostOther(carInfo.getCarCostOther());
                }
            }
            workbenchInfo.setCreateTime(localDateTime);
            workbenchInfo.setUpdateTime(localDateTime);

            BossWorkbenchInfo info = bossWorkbenchInfoMapper.selectBossWorkbenchInfoNew(tenantId, maxBossWorkbenchInfoTime);
            if (info != null) {
                workbenchInfo.setId(info.getId());
                bossWorkbenchInfoMapper.updateById(workbenchInfo);
            } else {
                bossWorkbenchInfoMapper.insert(workbenchInfo);
            }


        }

        for (BossWorkbenchDayInfo bossWorkbenchDayInfo : businessDayInfoList) {
            bossWorkbenchDayInfo.setCreateTime(localDateTime);
            bossWorkbenchDayInfo.setUpdateTime(localDateTime);

            BossWorkbenchDayInfo info = bossWorkbenchDayInfoMapper.selectBossWorkbenchDayInfoNew(bossWorkbenchDayInfo.getTenantId(), bossWorkbenchDayInfo.getTime(), maxBossWorkbenchInfoTime);
            if (info != null) {
                bossWorkbenchDayInfo.setId(info.getId());
                bossWorkbenchDayInfoMapper.updateById(bossWorkbenchDayInfo);
            } else {
                bossWorkbenchDayInfoMapper.insert(bossWorkbenchDayInfo);
            }
        }

        for (BossWorkbenchMonthInfo bossWorkbenchMonthInfo : businessMonthInfoList) {
            bossWorkbenchMonthInfo.setCreateTime(localDateTime);
            bossWorkbenchMonthInfo.setUpdateTime(localDateTime);

            BossWorkbenchMonthInfo info = bossWorkbenchMonthInfoMapper.selectBossWorkbenchMonthInfoNew(bossWorkbenchMonthInfo.getTenantId(), bossWorkbenchMonthInfo.getTime(), maxBossWorkbenchInfoTime);
            if (info != null) {
                bossWorkbenchMonthInfo.setId(info.getId());
                bossWorkbenchMonthInfoMapper.updateById(bossWorkbenchMonthInfo);
            } else {
                bossWorkbenchMonthInfoMapper.insert(bossWorkbenchMonthInfo);
            }
        }

        for (BossWorkbenchCustomerInfo customerInfo : customerInfoList) {
            customerInfo.setCreateTime(localDateTime);
            customerInfo.setUpdateTime(localDateTime);

            BossWorkbenchCustomerInfo info = bossWorkbenchCustomerInfoMapper.selectBossWorkbenchCustomerInfoNew(customerInfo.getTenantId(), customerInfo.getCustomerId(), maxBossWorkbenchInfoTime);
            if (info != null) {
                customerInfo.setId(info.getId());
                bossWorkbenchCustomerInfoMapper.updateById(customerInfo);
            } else {
                bossWorkbenchCustomerInfoMapper.insert(customerInfo);
            }
        }

    }

    @Override
    public BossWorkbenchInfoDto getBossWorkbenchInfo(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = -1L;
        Long userInfoId = loginInfo.getUserInfoId();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userInfoId);
        if (sysTenantDef != null && sysTenantDef.getId() != null) {
            tenantId = sysTenantDef.getId();
        } else {
            tenantId = loginInfo.getTenantId();
        }

        BigDecimal zero = new BigDecimal(0.00);

        BossWorkbenchInfoDto bossWorkbenchInfoDto = new BossWorkbenchInfoDto();

        LambdaQueryWrapper<BossWorkbenchInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BossWorkbenchInfo::getTenantId, tenantId);
        queryWrapper.orderByDesc(BossWorkbenchInfo::getCreateTime);
        queryWrapper.last("limit 1");

        // 老板工作台数据不存在  给默认值
        BossWorkbenchInfo workbenchInfo = bossWorkbenchInfoMapper.selectOne(queryWrapper);
        if (workbenchInfo == null) {
            workbenchInfo = new BossWorkbenchInfo();
            workbenchInfo.setPaymentReviewAmount(0L);
            workbenchInfo.setCarCostOil(new BigDecimal(0));
            workbenchInfo.setCarCostEtc(new BigDecimal(0));
            workbenchInfo.setCarCostMaintenance(new BigDecimal(0));
            workbenchInfo.setCarCostOther(new BigDecimal(0));
        } else {
            workbenchInfo.setCarCostOil(CommonUtil.getDoubleFormatLongMoneyBigDecimal(workbenchInfo.getCarCostOil() != null ? workbenchInfo.getCarCostOil() : zero, 2));
            workbenchInfo.setCarCostEtc(CommonUtil.getDoubleFormatLongMoneyBigDecimal(workbenchInfo.getCarCostEtc() != null ? workbenchInfo.getCarCostEtc() : zero, 2));
            workbenchInfo.setCarCostMaintenance(CommonUtil.getDoubleFormatLongMoneyBigDecimal(workbenchInfo.getCarCostMaintenance() != null ? workbenchInfo.getCarCostMaintenance() : zero, 2));
            workbenchInfo.setCarCostOther(CommonUtil.getDoubleFormatLongMoneyBigDecimal(workbenchInfo.getCarCostOther() != null ? workbenchInfo.getCarCostOther() : zero, 2));
        }

        bossWorkbenchInfoDto.setInfo(workbenchInfo);

        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String endTime = LocalDate.now().minusDays(1).format(dayFormat);
        String startTime = LocalDate.now().minusDays(7).format(dayFormat);

        List<String> tiemList = new ArrayList<>();
        for (int i = 7; i >= 1; i--) {
            String time = LocalDate.now().minusDays(i).format(dayFormat);
            tiemList.add(time);
        }


        List<BossWorkbenchDayInfo> bossWorkbenchDayInfoList = new ArrayList<>();
        List<BossWorkbenchDayInfo> bossWorkbenchDayInfos = bossWorkbenchDayInfoMapper.getBossWorkbenchDayInfoList(tenantId, startTime, endTime);
        for (String time : tiemList) {
            Optional<BossWorkbenchDayInfo> optioanal = bossWorkbenchDayInfos.stream().filter(item -> Objects.equals(item.getTime(), time)).findFirst();
            if (optioanal.isPresent()) {
                bossWorkbenchDayInfoList.add(optioanal.get());
            } else {
                BossWorkbenchDayInfo bossWorkbenchDayInfo = new BossWorkbenchDayInfo();
                bossWorkbenchDayInfo.setBusinessCost(zero);
                bossWorkbenchDayInfo.setBusinessIncome(zero);
                bossWorkbenchDayInfo.setBusinessProfit(zero);
                bossWorkbenchDayInfo.setTime(time);
                bossWorkbenchDayInfoList.add(bossWorkbenchDayInfo);
            }
        }


        bossWorkbenchInfoDto.setDayInfoList(bossWorkbenchDayInfoList);


        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        String endMonth = LocalDate.now().minusMonths(1).format(monthFormat);
        String startMonth = LocalDate.now().minusMonths(7).format(monthFormat);

        List<String> monthList = new ArrayList<>();
        for (int i = 7; i >= 1; i--) {
            String month = LocalDate.now().minusMonths(i).format(monthFormat);
            monthList.add(month);
        }

        List<BossWorkbenchMonthInfo> bossWorkbenchMonthInfoList = new ArrayList<>();
        List<BossWorkbenchMonthInfo> bossWorkbenchMonthInfos = bossWorkbenchMonthInfoMapper.getBossWorkbenchMonthInfoList(tenantId, startMonth, endMonth);
        for (String month : monthList) {
            Optional<BossWorkbenchMonthInfo> optioanal = bossWorkbenchMonthInfos.stream().filter(item -> Objects.equals(item.getTime(), month)).findFirst();
            if (optioanal.isPresent()) {
                bossWorkbenchMonthInfoList.add(optioanal.get());
            } else {
                BossWorkbenchMonthInfo bossWorkbenchDayInfo = new BossWorkbenchMonthInfo();
                bossWorkbenchDayInfo.setBusinessCost(zero);
                bossWorkbenchDayInfo.setBusinessIncome(zero);
                bossWorkbenchDayInfo.setBusinessProfit(zero);
                bossWorkbenchDayInfo.setTime(month);
                bossWorkbenchMonthInfoList.add(bossWorkbenchDayInfo);
            }
        }
        bossWorkbenchInfoDto.setMonthInfoList(bossWorkbenchMonthInfoList);

        List<BossWorkbenchCustomerInfo> customerInfoList = bossWorkbenchCustomerInfoMapper.getTableCustomerInfoList(tenantId);
        // 按照收入正序排序
        bossWorkbenchInfoDto.setCustomerInfoList(customerInfoList.stream().sorted((item1, item2) -> item1.getBusinessIncome().compareTo(item2.getBusinessIncome())).collect(Collectors.toList()));


        LambdaQueryWrapper<OperationWorkbenchInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationWorkbenchInfo::getTenantId, tenantId);
        wrapper.eq(OperationWorkbenchInfo::getUserInfoId, userInfoId);
        wrapper.orderByDesc(OperationWorkbenchInfo::getCreateTime);
        wrapper.last("limit 1");
        OperationWorkbenchInfo operationWorkbenchInfo = baseMapper.selectOne(wrapper);

        bossWorkbenchInfoDto.setCostBorrowReviewAmount(operationWorkbenchInfo != null ? operationWorkbenchInfo.getCostBorrowReviewAmount() : 0L);
        bossWorkbenchInfoDto.setCostBorrowMeReviewAmount(operationWorkbenchInfo != null ? operationWorkbenchInfo.getCostBorrowMeReviewAmount() : 0L);
        bossWorkbenchInfoDto.setMaintenanceMeReviewAmount(operationWorkbenchInfo != null ? operationWorkbenchInfo.getMaintenanceMeReviewAmount() : 0L);
        bossWorkbenchInfoDto.setMaintenanceReviewAmount(operationWorkbenchInfo != null ? operationWorkbenchInfo.getMaintenanceReviewAmount() : 0L);
        bossWorkbenchInfoDto.setVehicleCostMeReviewAmount(operationWorkbenchInfo != null ? operationWorkbenchInfo.getVehicleCostMeReviewAmount() : 0L);
        bossWorkbenchInfoDto.setVehicleCostReviewAmount(operationWorkbenchInfo != null ? operationWorkbenchInfo.getVehicleCostReviewAmount() : 0L);
        bossWorkbenchInfoDto.setManageCostReviewAmount(operationWorkbenchInfo != null ? operationWorkbenchInfo.getManageCostReviewAmount() : 0L);
        bossWorkbenchInfoDto.setManageCostMeReviewAmount(operationWorkbenchInfo != null ? operationWorkbenchInfo.getManageCostMeReviewAmount() : 0L);

        LambdaQueryWrapper<FinancialWorkbenchInfo> financialWrapper = new LambdaQueryWrapper<>();
        financialWrapper.eq(FinancialWorkbenchInfo::getTenantId, tenantId);
        financialWrapper.eq(FinancialWorkbenchInfo::getUserInfoId, userInfoId);
        financialWrapper.orderByDesc(FinancialWorkbenchInfo::getCreateTime);
        financialWrapper.last("limit 1");
        FinancialWorkbenchInfo financialWorkbenchInfo = financialWorkbenchInfoMapper.selectOne(financialWrapper);
        bossWorkbenchInfoDto.setPlatformSurplusAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getPlatformSurplusAmount() : 0L);
        bossWorkbenchInfoDto.setPlatformUsedAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getPlatformUsedAmount() : 0L);
        bossWorkbenchInfoDto.setSurplusAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getSurplusAmount() : 0L);
        bossWorkbenchInfoDto.setOilSurplusAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getOilSurplusAmount() : 0L);
        bossWorkbenchInfoDto.setOilUsedAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getOilUsedAmount() : 0L);
        bossWorkbenchInfoDto.setReceivableReceivedAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getReceivableReceivedAmount() : 0L);
        bossWorkbenchInfoDto.setReceivableSurplusAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getReceivableSurplusAmount() : 0L);
        bossWorkbenchInfoDto.setPayablePaidAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getPayablePaidAmount() : 0L);
        bossWorkbenchInfoDto.setPayableSurplusAmount(financialWorkbenchInfo != null ? financialWorkbenchInfo.getPayableSurplusAmount() : 0L);

        return bossWorkbenchInfoDto;
    }

    @Override
    public void initWechatOperationWorkInfoDayData() {
        LocalDateTime time = LocalDateTime.now();

        LocalDateTime maxLocalDateTime = wechatOperationWorkbenchInfoMapper.maxLocalDateTime(1);
        maxLocalDateTime = maxLocalDateTime == null ? time : maxLocalDateTime;

        List<WechatOperationWorkbenchInfo> tableWechatOperationWorkbenchInfo = statementDepartmentDayService.getTableWechatOperationWorkbenchInfo();
        for (WechatOperationWorkbenchInfo wechatOperationWorkbenchInfo : tableWechatOperationWorkbenchInfo) {
            wechatOperationWorkbenchInfo.setCreateTime(time);
            wechatOperationWorkbenchInfo.setUpdateTime(time);
            wechatOperationWorkbenchInfo.setType(1); //日报

            WechatOperationWorkbenchInfo info = wechatOperationWorkbenchInfoMapper.getWechatOperationWorkbenchInfoNew(wechatOperationWorkbenchInfo.getTenantId(), wechatOperationWorkbenchInfo.getType(), wechatOperationWorkbenchInfo.getTime(), maxLocalDateTime);
            if (info != null) {
                wechatOperationWorkbenchInfo.setId(info.getId());
                wechatOperationWorkbenchInfoMapper.updateById(wechatOperationWorkbenchInfo);
            } else {
                wechatOperationWorkbenchInfoMapper.insert(wechatOperationWorkbenchInfo);
            }
        }
    }

    @Override
    public void initWechatOperationWorkInfoMonthData() {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime maxLocalDateTime = wechatOperationWorkbenchInfoMapper.maxLocalDateTime(2);
        maxLocalDateTime = maxLocalDateTime == null ? time : maxLocalDateTime;

        List<WechatOperationWorkbenchInfo> tableWechatOperationWorkbenchInfo = statementDepartmentMonthService.getTableWechatOperationWorkbenchInfo();
        for (WechatOperationWorkbenchInfo wechatOperationWorkbenchInfo : tableWechatOperationWorkbenchInfo) {
            wechatOperationWorkbenchInfo.setCreateTime(time);
            wechatOperationWorkbenchInfo.setUpdateTime(time);
            wechatOperationWorkbenchInfo.setType(2); //月

            WechatOperationWorkbenchInfo info = wechatOperationWorkbenchInfoMapper.getWechatOperationWorkbenchInfoNew(wechatOperationWorkbenchInfo.getTenantId(), wechatOperationWorkbenchInfo.getType(), wechatOperationWorkbenchInfo.getTime(), maxLocalDateTime);
            if (info != null) {
                wechatOperationWorkbenchInfo.setId(info.getId());
                wechatOperationWorkbenchInfoMapper.updateById(wechatOperationWorkbenchInfo);
            } else {
                wechatOperationWorkbenchInfoMapper.insert(wechatOperationWorkbenchInfo);
            }
        }
    }

    @Override
    public WechatOperationWorkbenchInfoDto getWechatOperationWorkbenchInfo(String accessToken, Integer type) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userInfoId = loginInfo.getUserInfoId();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userInfoId);
        Long tenantId = sysTenantDef.getId();

        BigDecimal zero = new BigDecimal(0.00);

        String time = "";
        WechatOperationWorkbenchInfoDto wechatOperationWorkbenchInfoDto = new WechatOperationWorkbenchInfoDto();
        if (type == 1) {//日报
            DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String endTime = LocalDate.now().minusDays(1).format(dayFormat);//结束时间为当天的前一天
            String startTime = LocalDate.now().minusDays(7).format(dayFormat);//开始时间为当天的前7天

            List<String> dayList = new ArrayList<>();
            for (int i = 7; i >= 1; i--) {
                String day = LocalDate.now().minusDays(i).format(dayFormat);
                dayList.add(day);
            }

            List<BossWorkbenchDayInfo> bossWorkbenchDayInfoList = new ArrayList<>();
            List<BossWorkbenchDayInfo> bossWorkbenchDayInfos = bossWorkbenchDayInfoMapper.getBossWorkbenchDayInfoList(tenantId, startTime, endTime);
            for (String day : dayList) {
                Optional<BossWorkbenchDayInfo> optioanal = bossWorkbenchDayInfos.stream().filter(item -> Objects.equals(item.getTime(), day)).findFirst();
                if (optioanal.isPresent()) {
                    bossWorkbenchDayInfoList.add(optioanal.get());
                } else {
                    BossWorkbenchDayInfo bossWorkbenchDayInfo = new BossWorkbenchDayInfo();
                    bossWorkbenchDayInfo.setBusinessCost(zero);
                    bossWorkbenchDayInfo.setBusinessIncome(zero);
                    bossWorkbenchDayInfo.setBusinessProfit(zero);
                    bossWorkbenchDayInfo.setTime(day);
                    bossWorkbenchDayInfoList.add(bossWorkbenchDayInfo);
                }
            }

            wechatOperationWorkbenchInfoDto.setDayInfoList(bossWorkbenchDayInfoList);

            time = endTime;
        } else if (type == 2) {//月报
            DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");
            String endMonth = LocalDate.now().minusMonths(1).format(monthFormat);//结束时间为当月的前一月
            String startMonth = LocalDate.now().minusMonths(7).format(monthFormat);//结束时间为月的前七月

            List<String> monthList = new ArrayList<>();
            for (int i = 7; i >= 1; i--) {
                String month = LocalDate.now().minusMonths(i).format(monthFormat);
                monthList.add(month);
            }

            List<BossWorkbenchMonthInfo> bossWorkbenchMonthInfoList = new ArrayList<>();
            List<BossWorkbenchMonthInfo> bossWorkbenchMonthInfos = bossWorkbenchMonthInfoMapper.getBossWorkbenchMonthInfoList(tenantId, startMonth, endMonth);
            for (String month : monthList) {
                Optional<BossWorkbenchMonthInfo> optioanal = bossWorkbenchMonthInfos.stream().filter(item -> Objects.equals(item.getTime(), month)).findFirst();
                if (optioanal.isPresent()) {
                    bossWorkbenchMonthInfoList.add(optioanal.get());
                } else {
                    BossWorkbenchMonthInfo bossWorkbenchDayInfo = new BossWorkbenchMonthInfo();
                    bossWorkbenchDayInfo.setBusinessCost(zero);
                    bossWorkbenchDayInfo.setBusinessIncome(zero);
                    bossWorkbenchDayInfo.setBusinessProfit(zero);
                    bossWorkbenchDayInfo.setTime(month);
                    bossWorkbenchMonthInfoList.add(bossWorkbenchDayInfo);
                }
            }
            wechatOperationWorkbenchInfoDto.setMonthInfoList(bossWorkbenchMonthInfoList);

            time = endMonth;
        }

        // 日报  显示昨天的数据  月报  显示上个月的数据
        WechatOperationWorkbenchInfo wechatOperationWorkbenchInfo = wechatOperationWorkbenchInfoMapper.getWechatOperationWorkbenchInfo(tenantId, type, time);
        if (wechatOperationWorkbenchInfo == null) {
            wechatOperationWorkbenchInfo = new WechatOperationWorkbenchInfo();
            wechatOperationWorkbenchInfo.setTime(time);
            wechatOperationWorkbenchInfo.setOwnIncome(zero);
            wechatOperationWorkbenchInfo.setOwnCost(zero);
            wechatOperationWorkbenchInfo.setOwnGrossMargin(zero);
            wechatOperationWorkbenchInfo.setOilFee(zero);
            wechatOperationWorkbenchInfo.setTollFee(zero);
            wechatOperationWorkbenchInfo.setWageFee(zero);
            wechatOperationWorkbenchInfo.setDiversionIncome(zero);
            wechatOperationWorkbenchInfo.setDiversionCost(zero);
            wechatOperationWorkbenchInfo.setDiversionGrossMargin(zero);
            wechatOperationWorkbenchInfo.setMerchantsIncome(zero);
            wechatOperationWorkbenchInfo.setMerchantsCost(zero);
            wechatOperationWorkbenchInfo.setMerchantsGrossMargin(zero);
        }
        wechatOperationWorkbenchInfoDto.setWechatOperationWorkbenchInfo(wechatOperationWorkbenchInfo);

        return wechatOperationWorkbenchInfoDto;
    }
}
