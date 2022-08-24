package com.youming.youche.finance.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.PermissionConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IAccountDetailsThreeService;
import com.youming.youche.finance.api.IClaimExpenseInfoService;
import com.youming.youche.finance.api.IOaFilesService;
import com.youming.youche.finance.api.IOaLoadVerificationService;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.api.IVehicleExpenseService;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.api.order.IOrderInfoThreeService;
import com.youming.youche.finance.api.order.IOrderMainReportService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.commons.util.DateUtil;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.constant.OaLoanData;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.OaFiles;
import com.youming.youche.finance.domain.OaLoadVerification;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.domain.sys.SysAttach;
import com.youming.youche.finance.dto.AccountBankRelDto;
import com.youming.youche.finance.dto.GetStatisticsDto;
import com.youming.youche.finance.dto.OaLoanOutDto;
import com.youming.youche.finance.dto.QueryOaloneDto;
import com.youming.youche.finance.dto.order.OrderSchedulerDto;
import com.youming.youche.finance.provider.mapper.OaFilesMapper;
import com.youming.youche.finance.provider.mapper.OaLoadVerificationMapper;
import com.youming.youche.finance.provider.mapper.OaLoanMapper;
import com.youming.youche.finance.provider.mapper.SysAttachMapper;
import com.youming.youche.finance.provider.mapper.VehicleExpenseMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.OaFilesVo;
import com.youming.youche.finance.vo.OaLoanVo;
import com.youming.youche.market.commons.OrderUtil;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.api.order.IOrderTransferInfoService;
import com.youming.youche.order.api.order.IOverdueReceivableService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderTransferInfo;
import com.youming.youche.order.domain.order.OverdueReceivable;
import com.youming.youche.order.dto.DirverxDto;
import com.youming.youche.order.dto.OilExcDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.SysTenantDefDto;
import com.youming.youche.order.util.BeanUtils;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.UserType;
import com.youming.youche.system.utils.excel.ExportExcel;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>
 * 借支信息表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-02-07
 */
@ToString
@DubboService(version = "1.0.0")
public class OaLoanThreeServiceImpl extends BaseServiceImpl<OaLoanMapper, OaLoan> implements IOaLoanThreeService {

    private final static String percentAge = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"; // 两位小数
    @Resource
    RedisUtil redisUtil;
    @Resource
    private LoginUtils loginUtils;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService IVehicleDataInfoService; //车辆表 服务类
    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;
    @DubboReference(version = "1.0.0")
    IOrderAccountService iOrderAccountService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;
    @Resource
    OaLoadVerificationMapper oaLoadVerificationMapper;
    @Resource
    IOaLoadVerificationService iOaLoadVerificationService;
    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;
    @DubboReference(version = "1.0.0")
    IOverdueReceivableService overdueReceivableService;
    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;
    @DubboReference(version = "1.0.0")
    IOrderInfoService orderInfoService;
    @DubboReference(version = "1.0.0")
    IOrderInfoHService orderInfoHService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @Resource
    OaFilesMapper oaFilesMapper;
    @Resource
    IOaFilesService iOaFilesService;
    @Resource
    SysAttachMapper sysAttachMapper;
    @DubboReference(version = "1.0.0")
    com.youming.youche.order.api.order.IBusiSubjectsRelService iBusiSubjectsRelService;
    @DubboReference(version = "1.0.0")
    com.youming.youche.order.api.order.IAccountDetailsService iAccountDetailsService;
    @Resource
    IAccountDetailsThreeService accountDetailsThreeService;
    @Resource
    IPayoutIntfThreeService payoutIntfThreeService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    IOpAccountService iOpAccountService;
    @DubboReference(version = "1.0.0")
    IAuditService auditService;
    @DubboReference(version = "1.0.0")
    IOrderLimitService iOrderLimitService;
    @DubboReference(version = "1.0.0")
    IAccountBankRelService iAccountBankRelService;
    @DubboReference(version = "1.0.0")
    IPayFeeLimitService iPayFeeLimitService;
    @DubboReference(version = "1.0.0")
    IPayoutIntfService iPayoutIntfService;
    @DubboReference(version = "1.0.0")
    IOrderOpRecordService iOrderOpRecordService;

    @DubboReference(version = "1.0.0")
    IOrderFeeService orderFeeService;
    @DubboReference(version = "1.0.0")
    IOrderFeeHService orderFeeHService;

    @DubboReference(version = "1.0.0")
    ISysRoleService iSysRoleService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @Resource
    IClaimExpenseInfoService iClaimExpenseInfoService;

    @DubboReference(version = "1.0.0")
    IApplyRecordService iApplyRecordService;

    @DubboReference(version = "1.0.0")
    IOrderTransferInfoService iOrderTransferInfoService;

    @Resource
    IOrderInfoThreeService iOrderInfoThreeService;

    @Resource
    IOrderMainReportService iOrderMainReportService;

    @Resource
    IOaLoadVerificationService oaLoadVerificationService;

    @Resource
    VehicleExpenseMapper vehicleExpenseMapper;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @DubboReference(version = "1.0.0")
    IUserReceiverInfoService userReceiverInfoService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;
    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;
    @DubboReference(version = "1.0.0")
    ITenantUserRelService tenantUserRelService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Resource
    IVehicleExpenseService iVehicleExpenseService;

    @DubboReference(version = "1.0.0")
    IOrderAgingInfoService orderAgingInfoService;

    @DubboReference(version = "1.0.0")
    IOrderOilSourceService orderOilSourceService;

    @Override
    public Page<OaLoanOutDto> selectAllByMore(Integer pageNum, Integer pageSize, Boolean waitDeal, Integer queryType,
                                              String oaLoanId, Integer state, Integer loanSubject, String userName,
                                              String orderId, String plateNumber, String startTime, String endTime,
                                              String flowId, List<Long> dataPermissionIds, List<Long> subOrgList,
                                              String acctName, String mobilePhone, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        List<Long> busiIdByUserId = new ArrayList<>();
//        //获取上级部门id
//        SysOrganize sysOrganize = sysOrganizeService.get(loginInfo.getOrgIds().get(0));
//        while (true) {
//            if (sysOrganize.getParentOrgId() != null && sysOrganize.getParentOrgId() != -1L) {
//                if (subOrgList != null) {
//                    subOrgList.add(sysOrganize.getId());
//                    sysOrganize = sysOrganizeService.get(sysOrganize.getId());
//                }
//            }else{
//                break;
//            }
//        }
        if (waitDeal) {
            // TODO: 2022/2/7 等审核模块完成处理
            if (queryType == OaLoanConsts.QUERY_TYPE.vehicle) {// 车管借支
                busiIdByUserId = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.TUBE_BORROW, loginInfo.getUserInfoId(), loginInfo.getTenantId());
            } else if (queryType == OaLoanConsts.QUERY_TYPE.driver) { // 司机借支
                busiIdByUserId = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.DRIVER_BORROW, loginInfo.getUserInfoId(), loginInfo.getTenantId());
            }
        }
        Boolean aBoolean = iSysRoleService.hasAllData(loginInfo);
//        //判断是否有所有数据权限(操作员如果只有查看部门数据的权限，则只能所有归属于本部门的车管借支；如果有查看所有数据的权限，则可以查看所有的车管借支。)
//        boolean isAll = false;
//        for (Long dataPermissionId : dataPermissionIds) {
//            if (dataPermissionId.equals(PermissionConst.MENU_BTN_ID.ALL_DATA)) {
//                isAll = true;
//                break;
//            }
//        }
//        if (isAll) {
//            subOrgList = null;
//        }

        List<String> subjects = Lists.newArrayList();
        if (queryType != null) {
            if (queryType == OaLoanConsts.QUERY_TYPE.vehicle) {
                subjects = getLoanBelongAdminSubjectList();
            } else if (queryType == OaLoanConsts.QUERY_TYPE.driver) {
                subjects = getLoanBelongDriverSubjectList();
            }
        }
        LocalDateTime startTime1 = null;
        LocalDateTime endTime1 = null;
        if (startTime != null && StringUtils.isNotEmpty(startTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = startTime + " 00:00:00";
            startTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
        }
        if (endTime != null && StringUtils.isNotEmpty(endTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endTime + " 23:59:59";
            endTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
        }
        Page<OaLoanOutDto> oaLoanPage = new Page<>(pageNum, pageSize);
        Page<OaLoanOutDto> oaLoanIPage = baseMapper.selectAllByMore(oaLoanPage, waitDeal, queryType, oaLoanId, state,
                loanSubject, userName, orderId, plateNumber, startTime1, endTime1, flowId, dataPermissionIds, subOrgList,
                subjects, loginInfo.getTenantId(), acctName, mobilePhone, busiIdByUserId, aBoolean);
        List<OaLoanOutDto> records = oaLoanIPage.getRecords();
        List<Long> longList = new ArrayList<>();
        for (OaLoanOutDto oaLoanOutDto : records) {
            if (oaLoanOutDto.getId() != null) {
                longList.add(oaLoanOutDto.getId());
            }
        }
        Map<Long, Boolean> hasPermissionMap = new HashMap<>();
        Map<Long, Boolean> hasDriverPermissionMap = new HashMap<>();
        if (longList != null && longList.size() > 0) {
            //判断当前的用户对传入的业务主键是否有审核权限
            hasPermissionMap = auditOutService.isHasPermission(accessToken, AuditConsts.AUDIT_CODE.TUBE_BORROW, longList);
            hasDriverPermissionMap = auditOutService.isHasPermission(accessToken, AuditConsts.AUDIT_CODE.DRIVER_BORROW, longList);
        }
        List<OaLoanOutDto> loanOutDtos = new ArrayList<>();
        for (OaLoanOutDto oaLoanOutDto : records) {
            if (oaLoanOutDto != null) {
                boolean isTrue = false;
//            && hasPermissionMap.get(oaLoanOutDto.getId())
                if (hasPermissionMap.get(oaLoanOutDto.getId()) != null) {
                    isTrue = true;
                }
//            &&hasDriverPermissionMap.get(oaLoanOutDto.getId())
                if (hasDriverPermissionMap.get(oaLoanOutDto.getId()) != null) {
                    isTrue = true;
                }
                if (oaLoanOutDto.getSts() != null) {
                    if ((oaLoanOutDto.getSts() == OaLoanConsts.STS.STS0 || oaLoanOutDto.getSts() == OaLoanConsts.STS.STS1) && isTrue) {
                        oaLoanOutDto.setIsHasPermission(true);
                    }
                }
                // 事故司机
                oaLoanOutDto.setAccidentTypeName(oaLoanOutDto.getAccidentDivide() == null ? "" : getSysStaticData(
                        "LOAN_ACCIDENT_TYPE", String.valueOf(oaLoanOutDto.getAccidentDivide())).getCodeName());

                // 事故原因
                oaLoanOutDto.setAccidentReasonName(oaLoanOutDto.getAccidentReason() == null ? "" : getSysStaticData(
                        "LOAN_ACCIDENT_REASON", String.valueOf(oaLoanOutDto.getAccidentReason())).getCodeName());

                // 责任划分
                oaLoanOutDto.setDutyDivideName(oaLoanOutDto.getDutyDivide() == null ? "" : getSysStaticData(
                        "LOAN_DUTY_DIVIDE", String.valueOf(oaLoanOutDto.getDutyDivide())).getCodeName());

                // 事故司机
                oaLoanOutDto.setAccidentDivideName(oaLoanOutDto.getAccidentDivide() == null ? "" : getSysStaticData(
                        "LOAN_ACCIDENT_DRIVER", String.valueOf(oaLoanOutDto.getAccidentDivide())).getCodeName());
                // 票据
                oaLoanOutDto.setIsNeedBillName(oaLoanOutDto.getIsNeedBill() == null ? "" : getSysStaticData(
                        "IS_NEED_BILL_OA", String.valueOf(oaLoanOutDto.getIsNeedBill())).getCodeName());
                // 状态
                oaLoanOutDto.setStateName(oaLoanOutDto.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                        String.valueOf(oaLoanOutDto.getState())).getCodeName());
                ///科目  名称
                oaLoanOutDto.setLoanSubjectName(oaLoanOutDto.getLoanSubject() == null ? "" : getSysStaticData("LOAN_SUBJECT",
                        String.valueOf(oaLoanOutDto.getLoanSubject())).getCodeName());
                // 状态
                oaLoanOutDto.setStsName(oaLoanOutDto.getSts() == null ? "" : getSysStaticData("LOAN_STATE", String.valueOf(
                        oaLoanOutDto.getSts())).getCodeName());
                oaLoanOutDto.setAmountDouble(oaLoanOutDto.getAmount() == null ? null : oaLoanOutDto.getAmount() / 100.00);
                oaLoanOutDto.setPayedAmountStr(oaLoanOutDto.getPayedAmount() != null ? oaLoanOutDto.getPayedAmount() / 100.00 : 0);//核销金额
                oaLoanOutDto.setPayedAmountDouble(oaLoanOutDto.getPayedAmount() != null ? oaLoanOutDto.getPayedAmount() / 100.00 : 0);
                oaLoanOutDto.setBusiness(true);
                //获取归属部门名称
                oaLoanOutDto.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), oaLoanOutDto.getOrgId()));
                VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(oaLoanOutDto.getPlateNumber());
                if (vehicleDataInfo != null) {
                    TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId());
                    if (tenantVehicleRel != null && tenantVehicleRel.getVehicleClass() != null) {
                        oaLoanOutDto.setVehicleCode(vehicleDataInfo.getId());
                        oaLoanOutDto.setVehicleClass(tenantVehicleRel.getVehicleClass());
                    }
                }
                loanOutDtos.add(oaLoanOutDto);
            }
        }
        return oaLoanIPage;
    }

    //借支列表导出
    @Override
    public void borrowing_Excel(Boolean waitDeal, Integer queryType, String oaLoanId, Integer state,
                                Integer loanSubject, String userName, String orderId, String plateNumber,
                                String startTime, String endTime, String flowId,
                                List<Long> dataPermissionIds, List<Long> subOrgList,
                                String acctName, String mobilePhone, String accessToken,
                                com.youming.youche.commons.domain.ImportOrExportRecords records) {
        IPage<OaLoanOutDto> oaLoanOutDtoIPage = selectAllByMore(1, 99999, waitDeal, queryType, oaLoanId, state,
                loanSubject, userName, orderId, plateNumber, startTime, endTime, flowId, dataPermissionIds, subOrgList,
                acctName, mobilePhone, accessToken);//借支数据

        List<OaLoanOutDto> records1 = oaLoanOutDtoIPage.getRecords();
        for (OaLoanOutDto oaLoanOutDto : records1) { //TODO 处理金额 小数
            // 状态
            oaLoanOutDto.setStateName(oaLoanOutDto.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                    String.valueOf(oaLoanOutDto.getState())).getCodeName());
            ///科目  名称
            oaLoanOutDto.setLoanSubjectName(oaLoanOutDto.getLoanSubject() == null ? "" : getSysStaticData("LOAN_SUBJECT",
                    String.valueOf(oaLoanOutDto.getLoanSubject())).getCodeName());
            // 状态
            oaLoanOutDto.setStsName(oaLoanOutDto.getSts() == null ? "" : getSysStaticData("LOAN_STATE", String.valueOf(
                    oaLoanOutDto.getSts())).getCodeName());
            oaLoanOutDto.setAmountDouble(oaLoanOutDto.getAmount() == null ? null : oaLoanOutDto.getAmount() / 100.00);
            // 申请时间
            oaLoanOutDto.setAppDates(com.youming.youche.util.DateUtil.asDate(oaLoanOutDto.getAppDate()));
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = "借支信息导出.xlsx";
            if (queryType == OaLoanConsts.QUERY_TYPE.vehicle) {//车管借支
                showName = new String[]{"借支号码", "归属部门", "申请人", "申请时间", "借支类型",
                        "车牌号码", "借支金额", "已核销金额", "借支票据", "借支状态", "支付流水号", "申请人手机号", "收款人", "收款人账户"};
                resourceFild = new String[]{"getOaLoanId", "getOrgName", "getUserInfoName", "getAppDates", "getLoanSubjectName",
                        "getPlateNumber", "getAmountDouble", "getPayedAmountDouble", "getIsNeedBillName", "getStsName", "getFlowId", "getMobilePhone", "getAccName", "getAccNo"};
                fileName = "员工借支导出.xlsx";
            } else if (queryType == OaLoanConsts.QUERY_TYPE.driver) {//司机借支
                showName = new String[]{"借支号码", "归属部门", "申请人", "申请时间", "借支类型",
                        "车牌号码", "借支金额", "已核销金额", "借支票据", "借支状态", "支付流水号", "申请人手机号", "收款人", "收款人账户"};
                resourceFild = new String[]{"getOaLoanId", "getOrgName", "getUserInfoName", "getAppDates", "getLoanSubjectName",
                        "getPlateNumber", "getAmountDouble", "getPayedAmountDouble", "getIsNeedBillName", "getStsName", "getFlowId", "getMobilePhone", "getAccName", "getAccNo"};
                fileName = "司机借支导出.xlsx";
            }
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records1, showName, resourceFild, OaLoanOutDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, fileName, inputStream.available());
            os.close();
            inputStream.close();
            records.setMediaUrl(path);
            records.setState(2);
            importOrExportRecordsService.saveOrUpdate(records);
        } catch (Exception e) {
            records.setState(3);
            importOrExportRecordsService.saveOrUpdate(records);
            e.printStackTrace();
        }
    }


    /**
     * 返回资金归属司机的借支类型
     *
     * @return
     */
    public List<String> getLoanBelongDriverSubjectList() {
        List<String> loanSubjects = Lists.newArrayList();
        List<SysStaticData> dataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));
//        List<SysStaticData> dataList = SysStaticDataUtil.getSysStaticDataList("LOAN_SUBJECT_APP");//司机借支
        for (SysStaticData data : dataList) {
            loanSubjects.add(data.getCodeValue());
        }
        return loanSubjects;
    }

    /**
     * 返回司机申请但资金归属车管的借支类型
     *
     * @return
     */
    public List<String> getLoanBelongAdminSubjectList() {
        List<String> result = new ArrayList();
        Set<String> extsubject = new HashSet();
        List<SysStaticData> ext = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));
        List<SysStaticData> total = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT"));
        for (SysStaticData data : ext) {
            extsubject.add(data.getCodeValue());
        }

        for (SysStaticData data : total) {
            if (!extsubject.contains(data.getCodeValue())) {
                result.add(data.getCodeValue());
            }
        }
        return result;
    }


    /**
     * 借支详情
     *
     * @param Id
     * @param busiCode
     * @param accessToken
     * @return
     */
    @Override
    public OaLoanOutDto queryOaLoanById(Long Id, String busiCode, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
//        if (null == Id || Id.equals("")) {
//            throw new BusinessException("借支号不能为空");
//        }
        List<Long> fileId = new ArrayList<Long>();// 附件id
        List<String> fileUrl = new ArrayList<String>();// 借支信息文件
        List<String> fileName = new ArrayList<String>();
        List<Long> fileIdV = new ArrayList<Long>();// 附件id
        List<String> fileUrlV = new ArrayList<String>();// 核销文件url
        List<String> fileNameV = new ArrayList<String>();// 核销文件名字
        OaLoan oaLoan = this.queryOaLoanById(Id, busiCode);
        Id = oaLoan.getId();
        ;
        QueryWrapper<OaLoadVerification> wrapper = new QueryWrapper<>();
        wrapper.eq("l_id", Id)
                .eq("tenant_id", loginInfo.getTenantId());
        List<OaLoadVerification> oaLoadVerifications = oaLoadVerificationMapper.selectList(wrapper);
        QueryWrapper<OaFiles> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("REL_ID", oaLoan.getId());
        List<OaFiles> oaFiles = oaFilesMapper.selectList(wrapper1);
        OaLoanOutDto oaLoanOut = new OaLoanOutDto();
        try {
            BeanUtils.copyProperties(oaLoanOut, oaLoan);
        } catch (Exception e) {
            throw new BusinessException("转换异常");
        }
        if (oaFiles != null) {
            for (OaFiles of : oaFiles) {
                if (of.getRelType().equals(OaLoanData.RELTYPE1)) {
                    fileId.add(of.getFileId());
                    fileUrl.add(of.getFileUrl());
                    fileName.add(of.getFileName());
                } else if (of.getRelType().equals(OaLoanData.RELTYPE2)) {
                    fileIdV.add(of.getFileId());
                    fileUrlV.add(of.getFileUrl());
                    fileNameV.add(of.getFileName());
                }
            }
        }
        if (oaLoadVerifications != null && oaLoadVerifications.size() > 0) {
            OaLoadVerification oaLoadVerification = oaLoadVerifications.get(0);
            if (oaLoadVerification.getCashAmount() != null) {
                oaLoanOut.setCashAmount(oaLoadVerification.getCashAmount());
            } else {
                oaLoanOut.setCashAmount(0l);
            }
            if (oaLoadVerification.getBillAmount() != null) {
                oaLoanOut.setBillAmount(oaLoadVerification.getBillAmount());
            } else {
                oaLoanOut.setBillAmount(0l);
            }

            oaLoanOut.setAllCashAmount(0.0f);
            oaLoanOut.setAllBillAmount(0.0f);
            for (OaLoadVerification verification : oaLoadVerifications) {
                if (verification.getCashAmount() != null) {
                    oaLoanOut.setAllCashAmount(verification.getCashAmount() + oaLoanOut.getAllCashAmount());
                }
                if (verification.getBillAmount() != null) {
                    oaLoanOut.setAllBillAmount(verification.getBillAmount() + oaLoanOut.getAllBillAmount());
                }
            }

        }
        oaLoanOut.setFileUrl(fileUrl);
        oaLoanOut.setFileName(fileName);
        oaLoanOut.setFileUrlV(fileUrlV);
        oaLoanOut.setFileNameV(fileNameV);
        oaLoanOut.setFileId(fileId);
        oaLoanOut.setFileIdV(fileIdV);
        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(oaLoan.getUserInfoId());
        oaLoanOut.setMobilePhone(userDataInfo.getMobilePhone());
//        oaLoanOut.setAccUserId(oaLoan.getBorrowUserId());
        oaLoanOut.setId(oaLoan.getId());// 主键
        oaLoanOut.setAccUserId(oaLoan.getBorrowUserInfoId());
        oaLoanOut.setAccName(oaLoan.getAccName());
        oaLoanOut.setAccNo(oaLoan.getAccNo());
        oaLoanOut.setBankName(oaLoan.getBankName());
        oaLoanOut.setBankBranch(oaLoan.getBankBranch());
        oaLoanOut.setPlateNumber(oaLoan.getPlateNumber()); // 车牌号
        oaLoanOut.setOaLoanId(oaLoan.getOaLoanId()); // 借支号
        oaLoanOut.setRemark(oaLoan.getRemark());// 借支说明
        oaLoanOut.setCarOwnerName(oaLoan.getCarOwnerName());// 司机姓名
        oaLoanOut.setCarPhone(oaLoan.getCarPhone()); // 司机手机号
        oaLoanOut.setAmount(oaLoan.getAmount());
        oaLoanOut.setAmountDouble(oaLoanOut.getAmount() / 100.00);//借支金额
        oaLoanOut.setRemark(oaLoan.getRemark());// 借支说明
        oaLoanOut.setLoanSubject(oaLoan.getLoanSubject());// 借支 类型
        oaLoanOut.setIsNeedBill(oaLoan.getIsNeedBill());// 借支票据
        oaLoanOut.setOrderId(oaLoan.getOrderId());// 订单号
        oaLoanOut.setLaunch(oaLoan.getLaunch());
        oaLoanOut.setAppReason(oaLoan.getAppReason()); // 借款说明
        oaLoanOut.setBorrowName(oaLoan.getBorrowName());
        oaLoanOut.setBorrowPhone(oaLoan.getBorrowPhone());
        oaLoanOut.setAccidentDate(oaLoan.getAccidentDate());// 事故时间
        oaLoanOut.setInsuranceDate(oaLoan.getInsuranceDate());// 出险时间
        oaLoanOut.setInsuranceFirm(oaLoan.getInsuranceFirm());//保险公司
        oaLoanOut.setInsuranceMoney(oaLoan.getInsuranceMoney());//理赔金额
        oaLoanOut.setReportNumber(oaLoan.getReportNumber());// 报案号
        oaLoanOut.setWeight(oaLoan.getWeight());//过磅费重量（千克）
        oaLoanOut.setUserInfoId(oaLoan.getUserInfoId());
        oaLoanOut.setUserInfoName(oaLoan.getUserInfoName());
        oaLoanOut.setLaunch(oaLoan.getLaunch());
        oaLoanOut.setClassify(oaLoan.getClassify());
        oaLoanOut.setAccidentType(oaLoan.getAccidentType());
        oaLoanOut.setPayedAmount(oaLoan.getPayedAmount());
        oaLoanOut.setLoanTransReason(oaLoan.getLoanTransReason());  //违章罚款原因1、因公 2、因私
        oaLoanOut.setVerifyRemark(oaLoan.getVerifyRemark()); // 核销备注
        oaLoanOut.setSts(oaLoan.getSts());// 状态
        oaLoanOut.setPayedAmountDouble(oaLoanOut.getPayedAmount() == null ? null : oaLoanOut.getPayedAmount() / 100.00);// 已核销金额
        // 事故司机
        oaLoanOut.setAccidentTypeName(oaLoan.getAccidentDivide() == null ? "" : getSysStaticData(
                "LOAN_ACCIDENT_TYPE", String.valueOf(oaLoan.getAccidentDivide())).getCodeName());
        oaLoanOut.setAccidentDivide(oaLoan.getAccidentDivide());
        // 事故原因
        oaLoanOut.setAccidentReasonName(oaLoan.getAccidentReason() == null ? "" : getSysStaticData(
                "LOAN_ACCIDENT_REASON", String.valueOf(oaLoan.getAccidentReason())).getCodeName());
        oaLoanOut.setAccidentReason(oaLoan.getAccidentReason());
        // 责任划分
        oaLoanOut.setDutyDivideName(oaLoan.getDutyDivide() == null ? "" : getSysStaticData(
                "LOAN_DUTY_DIVIDE", String.valueOf(oaLoan.getDutyDivide())).getCodeName());
        oaLoanOut.setDutyDivide(oaLoan.getDutyDivide());
        // 事故司机
        oaLoanOut.setAccidentDivideName(oaLoan.getAccidentDivide() == null ? "" : getSysStaticData(
                "LOAN_ACCIDENT_DRIVER", String.valueOf(oaLoan.getAccidentDivide())).getCodeName());
        oaLoanOut.setAccidentDivide(oaLoan.getAccidentDivide());
        // 开票 类型
        oaLoanOut.setIsNeedBillName(oaLoan.getIsNeedBill() == null ? "" : getSysStaticData(
                "IS_NEED_BILL_OA", String.valueOf(oaLoan.getIsNeedBill())).getCodeName());
        oaLoanOut.setIsNeedBill(oaLoan.getIsNeedBill());

        // 状态
        oaLoanOut.setStateName(oaLoan.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                String.valueOf(oaLoan.getState())).getCodeName());
        // 状态
        oaLoanOut.setStsName(oaLoanOut.getSts() == null ? "" : getSysStaticData("LOAN_STATE", String.valueOf(
                oaLoanOut.getSts())).getCodeName());
        oaLoanOut.setSts(oaLoan.getSts());
        oaLoanOut.setState(oaLoan.getState());
        ///科目  名称
        oaLoanOut.setLoanSubjectName(oaLoan.getLoanSubject() == null ? "" : getSysStaticData("LOAN_SUBJECT",
                String.valueOf(oaLoan.getLoanSubject())).getCodeName());
        oaLoanOut.setLoanSubject(oaLoan.getLoanSubject());
        //操作日志
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeBorrow;
        String auditCode = AuditConsts.AUDIT_CODE.TUBE_BORROW;
        if (checkLoanBelongDriver(oaLoan)) {
            auditCode = AuditConsts.AUDIT_CODE.DRIVER_BORROW;
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;
        }

        // TODO  2022-4-20  待确定
//        List<SysOperLog> sysOperLogs = sysOperLogTF.querySysOperLogAll(
//                busi_code.getCode(), LId, false,oaLoanOut.getTenantId(),
//                auditCode,LId);
//        oaLoanOut.setSysOperLogs(sysOperLogs);
        return oaLoanOut;

    }

    //核销查询
    @Override
    public IPage<OaLoanOutDto> selectCancelAll(Integer pageNum, Integer pageSize, Boolean waitDeal, Integer queryType, String oaLoanId,
                                               Integer state, Integer loanSubject, String userName, String orderId, String plateNumber,
                                               String startTime, String endTime, String flowId, List<Long> dataPermissionIds,
                                               List<Long> subOrgList, String acctName, String mobilePhone, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);

        List<Long> busiIdByUserId = new ArrayList<>();
        if (waitDeal) {
            // TODO: 2022/2/7 等审核模块完成处理
//            if (queryType== OaLoanConsts.QUERY_TYPE.vehicle){// 车管借支
            List<Long> busiIdByUserId1 = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.TUBE_BORROW, loginInfo.getUserInfoId(), loginInfo.getTenantId(), 1000);
//            }else if (queryType == OaLoanConsts.QUERY_TYPE.driver){
            List<Long> busiIdByUserId2 = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.DRIVER_BORROW, loginInfo.getUserInfoId(), loginInfo.getTenantId(), 1000);
//            }
            for (Long l : busiIdByUserId1) {
                busiIdByUserId.add(l);
            }
            for (Long l : busiIdByUserId2) {
                busiIdByUserId.add(l);
            }
        }
        //判断是否有所有数据权限(操作员如果只有查看部门数据的权限，则只能所有归属于本部门的车管借支；如果有查看所有数据的权限，则可以查看所有的车管借支。)
        boolean isAll = false;
        for (Long dataPermissionId : dataPermissionIds) {
            if (dataPermissionId.equals(PermissionConst.MENU_BTN_ID.ALL_DATA)) {
                isAll = true;
                break;
            }
        }
        if (isAll) {
            subOrgList = null;
        }

        List<String> subjects = Lists.newArrayList();
        if (queryType != null) {
            List<String> loanBelongDriverSubjectList = getLoanBelongDriverSubjectList();
            List<String> loanBelongAdminSubjectList = getLoanBelongAdminSubjectList();
            loanBelongAdminSubjectList.stream().forEach(s -> {
                subjects.add(s);
            });
            loanBelongDriverSubjectList.stream().forEach(s -> {
                subjects.add(s);
            });
        }

        LocalDateTime startTime1 = null;
        LocalDateTime endTime1 = null;
        if (startTime != null && StringUtils.isNotEmpty(startTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = startTime + " 00:00:00";
            startTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
        }
        if (endTime != null && StringUtils.isNotEmpty(endTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endTime + " 23:59:59";
            endTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
        }
        Page<OaLoanOutDto> oaLoanPage = new Page<>(pageNum, pageSize);
        IPage<OaLoanOutDto> oaLoanIPage = baseMapper.selectCancelAll(oaLoanPage, waitDeal, queryType, oaLoanId, state,
                loanSubject, userName, orderId, plateNumber, startTime1, endTime1, flowId, dataPermissionIds, subOrgList,
                subjects, loginInfo.getTenantId(), acctName, mobilePhone, busiIdByUserId);
        List<OaLoanOutDto> records = oaLoanIPage.getRecords();
        List<OaLoanOutDto> loanOutDtos = new ArrayList<>();
        for (OaLoanOutDto oaLoanOutDto : records) {
            // 事故司机
            oaLoanOutDto.setAccidentTypeName(oaLoanOutDto.getAccidentDivide() == null ? "" : getSysStaticData(
                    "LOAN_ACCIDENT_TYPE", String.valueOf(oaLoanOutDto.getAccidentDivide())).getCodeName());

            // 事故原因
            oaLoanOutDto.setAccidentReasonName(oaLoanOutDto.getAccidentReason() == null ? "" : getSysStaticData(
                    "LOAN_ACCIDENT_REASON", String.valueOf(oaLoanOutDto.getAccidentReason())).getCodeName());

            // 责任划分
            oaLoanOutDto.setDutyDivideName(oaLoanOutDto.getDutyDivide() == null ? "" : getSysStaticData(
                    "LOAN_DUTY_DIVIDE", String.valueOf(oaLoanOutDto.getDutyDivide())).getCodeName());

            // 事故司机
            oaLoanOutDto.setAccidentDivideName(oaLoanOutDto.getAccidentDivide() == null ? "" : getSysStaticData(
                    "LOAN_ACCIDENT_DRIVER", String.valueOf(oaLoanOutDto.getAccidentDivide())).getCodeName());
            // 票据
            oaLoanOutDto.setIsNeedBillName(oaLoanOutDto.getIsNeedBill() == null ? "" : getSysStaticData(
                    "IS_NEED_BILL_OA", String.valueOf(oaLoanOutDto.getIsNeedBill())).getCodeName());
            // 状态
            oaLoanOutDto.setStateName(oaLoanOutDto.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                    String.valueOf(oaLoanOutDto.getState())).getCodeName());
            ///科目  名称
            oaLoanOutDto.setLoanSubjectName(oaLoanOutDto.getLoanSubject() == null ? "" : getSysStaticData("LOAN_SUBJECT",
                    String.valueOf(oaLoanOutDto.getLoanSubject())).getCodeName());
            // 状态
            oaLoanOutDto.setStsName(oaLoanOutDto.getSts() == null ? "" : getSysStaticData("LOAN_STATE", String.valueOf(
                    oaLoanOutDto.getSts())).getCodeName());
            oaLoanOutDto.setAmountDouble(oaLoanOutDto.getAmount() / 100.00);//借支金额
            //获取归属部门名称
            oaLoanOutDto.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), oaLoanOutDto.getOrgId()));
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(oaLoanOutDto.getPlateNumber());
            if (vehicleDataInfo != null) {
                TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId());
                oaLoanOutDto.setVehicleCode(vehicleDataInfo.getId());
                oaLoanOutDto.setVehicleClass(tenantVehicleRel.getVehicleClass());
            }
//            // 卡控 数据类型
//            if (oaLoanOutDto.getSts()!=null){
//                if (oaLoanOutDto.getSts()==OaLoanConsts.STS.STS3  ||  oaLoanOutDto.getSts()  == OaLoanConsts.STS.STS4 || oaLoanOutDto.getSts()==OaLoanConsts.STS.STS5){
//                    loanOutDtos.add(oaLoanOutDto);
//                }
//            }
            oaLoanOutDto.setPayedAmountDouble(oaLoanOutDto.getPayedAmount() == null ? null : oaLoanOutDto.getPayedAmount() / 100.00);
        }
//        oaLoanIPage.setRecords(loanOutDtos);
//        oaLoanIPage.setTotal(loanOutDtos.size());
        return oaLoanIPage;
    }

    //方法说明 核销报表
    @Override
    public IPage<OaLoanOutDto> queryOaLoanTable(String startTime, String endTime, Integer loanType, Integer loanSubject,
                                                Integer loanClassify, String orderId, String oaLoanId, String userName,
                                                String mobilePhone, String acctName, Long amountStar, Long amountEnd,
                                                String plateNumber, Integer queryType, Integer state, String flowId,
                                                Long noPayedStar, Long noPayedEnd, String accessToken,
                                                List<Long> subOrgList, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        if (loanType == null || (state == null && state > 9 || state < 0) || (queryType > 4 && queryType < 0)) {
//            throw new BusinessException("输入有勿请重新输入");
//        }
        if (state != null && queryType != null) {
            if ((state > 9 || state < 0) || (queryType > 4 && queryType < 0)) {
                throw new BusinessException("输入有勿请重新输入");
            }
        }

        List<String> subjects = Lists.newArrayList();
        if (queryType != null) {
            if (queryType == OaLoanConsts.QUERY_TYPE.vehicle) {
                subjects = getLoanBelongAdminSubjectList();
            } else if (queryType == OaLoanConsts.QUERY_TYPE.driver) {
                subjects = getLoanBelongDriverSubjectList();
            }
        }
        LocalDateTime startTime1 = null;
        LocalDateTime endTime1 = null;
        if (startTime != null && StringUtils.isNotEmpty(startTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = startTime + " 00:00:00";
            startTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
        }
        if (endTime != null && StringUtils.isNotEmpty(endTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endTime + " 23:59:59";
            endTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
        }
        String flowIdStr = flowId.equals("") ? null : flowId;
        Page<OaLoanOutDto> oaLoanPage = new Page<>(pageNum, pageSize);
        IPage<OaLoanOutDto> loanOutDtoIPage = baseMapper.queryOaLoanTable(oaLoanPage, startTime1, endTime1, loanType, loanSubject
                , loanClassify, orderId, oaLoanId, userName, mobilePhone, acctName, amountStar, amountEnd, plateNumber,
                queryType, state, flowIdStr, noPayedStar, noPayedEnd, subOrgList, subjects, loginInfo.getTenantId());
        List<OaLoanOutDto> records = loanOutDtoIPage.getRecords();
        if (records != null || records.size() > 0) {
            for (OaLoanOutDto oaLoanOutDto : loanOutDtoIPage.getRecords()) {
                // 状态过滤
                if (oaLoanOutDto.getSts() != null) {
                    if (oaLoanOutDto.getSts() == OaLoanConsts.STS.STS3 || oaLoanOutDto.getSts() == OaLoanConsts.STS.STS4 || oaLoanOutDto.getSts() == OaLoanConsts.STS.STS5) {
                        oaLoanOutDto.setAmountDouble(oaLoanOutDto.getAmount() / 100.00);//借支金额
                        oaLoanOutDto.setNopayedAmount(oaLoanOutDto.getPayedAmount() != null ? oaLoanOutDto.getAmount() - oaLoanOutDto.getPayedAmount() : oaLoanOutDto.getAmount());//未报销金额
                        oaLoanOutDto.setNoPayedAmountStr(oaLoanOutDto.getPayedAmount() != null ? (oaLoanOutDto.getAmount() - oaLoanOutDto.getPayedAmount()) / 100.00 : oaLoanOutDto.getAmount() / 100.00);//未报销金额

                        oaLoanOutDto.setSalaryDeduction(oaLoanOutDto.getSalaryDeduction() != null ? oaLoanOutDto.getSalaryDeduction() : 0);//工资抵扣
                        oaLoanOutDto.setSalaryDeductionStr(oaLoanOutDto.getSalaryDeduction() != null ? oaLoanOutDto.getSalaryDeduction() / 100 : 0);//工资抵扣
                        oaLoanOutDto.setPayedAmount(oaLoanOutDto.getPayedAmount() != null ? oaLoanOutDto.getPayedAmount() : 0);//核销金额
                        oaLoanOutDto.setPayedAmountStr(oaLoanOutDto.getPayedAmount() != null ? oaLoanOutDto.getPayedAmount() / 100.00 : 0);//核销金额
                        oaLoanOutDto.setPayedAmountDouble(oaLoanOutDto.getPayedAmount() != null ? oaLoanOutDto.getPayedAmount() / 100.00 : 0);
                        oaLoanOutDto.setClassifyName(oaLoanOutDto.getLaunch() != null && oaLoanOutDto.getLaunch() == 2 ? "司机借支" : "员工借支");//借支入口
                        oaLoanOutDto.setLoanSubjectName(oaLoanOutDto.getLoanSubject() == null ? "" : getSysStaticData("LOAN_SUBJECT",
                                String.valueOf(oaLoanOutDto.getLoanSubject())).getCodeName());//借支类型
                        oaLoanOutDto.setStsName(oaLoanOutDto.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                                String.valueOf(oaLoanOutDto.getSts())).getCodeName());//状态
                        oaLoanOutDto.setPayIdStr(oaLoanOutDto.getFlowId() == null ? "" : oaLoanOutDto.getFileId() + "");
                        oaLoanOutDto.setMobilePhone(oaLoanOutDto.getMobilePhone());//申请人手机
                        oaLoanOutDto.setUserName(oaLoanOutDto.getUserInfoName());//申请人姓名
                        if (oaLoanOutDto.getClassify() != null) {
                            if (oaLoanOutDto.getClassify() == 1) {
                                oaLoanOutDto.setLaunchName("员工");//申请人属性
                            } else if (oaLoanOutDto.getClassify() == 2) {
                                oaLoanOutDto.setLaunchName(oaLoanOutDto.getLaunch() != null && oaLoanOutDto.getLaunch() == 2 ? "司机" : "员工");
                            }
                        }
                        SysStaticData sysStaticData = SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "LOAN_SUBJECT", oaLoanOutDto.getLoanSubject() + "");
                        if (sysStaticData.getCodeId() == OaLoanData.VERIFY_TYPE_1 && oaLoanOutDto.getLaunch() == OaLoanData.LAUNCH2) {
                            oaLoanOutDto.setOrderIds(oaLoanOutDto.getOrderId() == null || oaLoanOutDto.getOrderId() <= 0 ? null : oaLoanOutDto.getOrderId());//订单号
                            oaLoanOutDto.setBorrowName(oaLoanOutDto.getUserName());//借款人取司机姓名
                            oaLoanOutDto.setBorrowPhone(oaLoanOutDto.getMobilePhone());//借款人手机
                            if (oaLoanOutDto.getClassify() != null) {
                                if (oaLoanOutDto.getClassify() == 1) {
                                    oaLoanOutDto.setClassifyName("员工");//申请人属性
                                } else if (oaLoanOutDto.getClassify() == 2) {
                                    oaLoanOutDto.setClassifyName(oaLoanOutDto.getLaunch() != null && oaLoanOutDto.getLaunch() == 2 ? "司机" : "员工");
                                }
                            }
                            if (oaLoanOutDto.getOrderId() != null && oaLoanOutDto.getOrderId() > 0) {
                                LocalDateTime dependTime = oaLoanOutDto.getCarDependDate();
                                if (dependTime != null) {
                                    oaLoanOutDto.setCarDependDate(dependTime);//打印所需订单时间
                                }
                            }
                            oaLoanOutDto.setBorrowerTypeName(oaLoanOutDto.getBorrowType() == null ? "" :
                                    getSysStaticData("USER_TYPE", String.valueOf(oaLoanOutDto.getBorrowType())).getCodeName());//借款人用户类型
                        } else {
                            //车管所借支
//                            oaLoanOutDto.setBorrowName(StringUtils.isBlank(oaLoanOutDto.getAccName())?oaLoanOutDto.getBorrowName():oaLoanOutDto.getAccName());//借款人取司机姓名
//                rtnMap.put("borrowerPhone",oaLoan.getBorrowPhone());//借款人手机
                            oaLoanOutDto.setBorrowerTypeName(oaLoanOutDto.getBorrowType() == null ? "" :
                                    getSysStaticData("USER_TYPE", String.valueOf(oaLoanOutDto.getBorrowType())).getCodeName());//借款人用户类型
//                            // TODO 2022-6-22 新需求 借支人属性 和申请人同步
//                        oaLoanOutDto.setBorrowerTypeName(oaLoanOutDto.getLaunchName());
                        }
                        Long money = oaLoanOutDto.getAmount();
                        oaLoanOutDto.setMillion(money % 1000000000 / 100000000);
                        oaLoanOutDto.setLac(money % 100000000 / 10000000);
                        oaLoanOutDto.setMyriad(money % 10000000 / 1000000);
                        oaLoanOutDto.setThousand(money % 1000000 / 100000);
                        oaLoanOutDto.setHundred(money % 100000 / 10000);
                        oaLoanOutDto.setTen(money % 10000 / 1000);
                        oaLoanOutDto.setUnit(money % 1000 / 100);
                        oaLoanOutDto.setCorner(money % 100 / 10);
                        oaLoanOutDto.setCorners(money % 10);
                        String[] auditCodes = new String[]{AuditConsts.AUDIT_CODE.STAFF_BORROW, AuditConsts.AUDIT_CODE.TUBE_BORROW, AuditConsts.AUDIT_CODE.DRIVER_BORROW};
                        List<Map> auditList = new ArrayList<Map>();
                        for (int i = 0; i < auditCodes.length; i++) {
//                            Integer states = null;
//                            if (oaLoanOutDto.getSts()!=null){
//                                states = oaLoanOutDto.getSts();
//                            }
                            auditList = iAuditNodeInstService.getAuditNodeInstNew(auditCodes[i], oaLoanOutDto.getId(), loginInfo.getTenantId(), false, null);
                            if (auditList != null && auditList.size() > 0) {
                                break;
                            }
                        }
//            rtnMap.put("auditList",auditList);
                        oaLoanOutDto.setAuditList(auditList);//审核人信息
                        for (int i = 0; i < auditList.size(); i++) {
//                rtnMap.put("auditkman"+i, auditList.get(i).get("linkman"));
                            Object linkman = auditList.get(i).get("linkman");
                            if (linkman != null) {
                                oaLoanOutDto.setAuditkman(linkman.toString());
                            }
                        }
                        List<OaLoadVerification> oaLoadVerifications = iOaLoadVerificationService.queryOaLoadVerificationsById(oaLoanOutDto.getId());
                        oaLoanOutDto.setLastVerifAmount(0);//最近核销金额
                        if (oaLoadVerifications != null && !oaLoadVerifications.isEmpty()) {
                            OaLoadVerification oaLoadVerification = oaLoadVerifications.get(0);
                            oaLoanOutDto.setLastVerifDate(oaLoadVerification.getOpDate());
                            oaLoanOutDto.setLastVerifAmount(oaLoadVerification.getCashAmount() == null ? 0L : oaLoadVerification.getCashAmount() + (oaLoadVerification.getBillAmount() == null ? 0L : oaLoadVerification.getBillAmount()));
                            oaLoanOutDto.setLastVerifName(oaLoadVerification.getOpName());//最近核销人
                            oaLoanOutDto.setLastVerifAmountDouble(((oaLoadVerification.getCashAmount() == null ? 0L : oaLoadVerification.getCashAmount())
                                    + (oaLoadVerification.getBillAmount() == null ? 0L : oaLoadVerification.getBillAmount()))/ 100.00);
                        }
                        //  累计核销金额
                        oaLoanOutDto.setTotalAmount(oaLoanOutDto.getPayedAmount() - oaLoanOutDto.getSalaryDeduction());
                    }
                }
            }
        }
        return loanOutDtoIPage;
    }

    // 核销统计
    @Override
    public OaLoanOutDto queryOaLoanTableSum(String startTime, String endTime, Integer loanType,
                                            Integer loanSubject, Integer loanClassify,
                                            String orderId, String oaLoanId, String userName,
                                            String mobilePhone, String acctName, Long amountStar,
                                            Long amountEnd, String plateNumber, Integer queryType,
                                            Integer state, String flowId, Long noPayedStar,
                                            Long noPayedEnd, String accessToken, List<Long> subOrgList,
                                            Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<String> subjects = Lists.newArrayList();
        if (queryType != null) {
            if (queryType == OaLoanConsts.QUERY_TYPE.vehicle) {
                subjects = getLoanBelongAdminSubjectList();
            } else if (queryType == OaLoanConsts.QUERY_TYPE.driver) {
                subjects = getLoanBelongDriverSubjectList();
            }
        }
        LocalDateTime startTime1 = null;
        LocalDateTime endTime1 = null;
        if (startTime != null && StringUtils.isNotEmpty(startTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = startTime + " 00:00:00";
            startTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
        }
        if (endTime != null && StringUtils.isNotEmpty(endTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endTime + " 23:59:59";
            endTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
        }
        String flowIdStr = flowId.equals("") ? null : flowId;
        Page<OaLoanOutDto> Page = new Page<>(pageNum, pageSize);
        Page<OaLoanOutDto> page = baseMapper.queryOaLoanTableSum(Page, loginInfo.getTenantId(), noPayedStar, noPayedEnd,
                startTime1, userName, acctName, mobilePhone, amountStar, amountEnd, queryType, orderId, plateNumber, endTime1,
                loanSubject, oaLoanId, flowIdStr, state, subOrgList, subjects);
        Long sumAmount = 0L;// 金额金额
        Long sumPayedAmount = 0L;// 核销金额
        Long sumNoPayedAmount = 0L;
        Long sumSalaryDeduction = 0L;
        Long sumLastVerifAmount = 0L;
        List<OaLoanOutDto> records = page.getRecords();
        if (records != null && records.size() > 0) {
            for (OaLoanOutDto dto : records) {
//                Long amount = map.get("amount")==null?0L:Long.valueOf(map.get("amount")+"");
//                Long payedAmount = map.get("payedAmount")==null?0L:Long.valueOf(map.get("payedAmount")+"");
//                Long salaryDeduction = map.get("salaryDeduction")==null?0L:Long.valueOf(map.get("salaryDeduction")+"");
//                List<OaLoadVerification> oaLoadVerifications = oaLoanSV.queryOaLoadVerificationsById(LId);
                Long LId = dto.getId();
                Long amount = dto.getAmount() == null ? 0L : dto.getAmount();
                sumAmount += amount;
                Long payedAmount = dto.getPayedAmount() == null ? 0L : dto.getPayedAmount();
                sumPayedAmount += payedAmount;
                Long salaryDeduction = dto.getSalaryDeduction() == null ? 0L : dto.getSalaryDeduction();
                sumSalaryDeduction += salaryDeduction;
                List<OaLoadVerification> oaLoadVerifications = iOaLoadVerificationService.queryOaLoadVerificationsById(LId);
                if (oaLoadVerifications != null && !oaLoadVerifications.isEmpty()) {
                    OaLoadVerification oaLoadVerification = oaLoadVerifications.get(0);
                    Long lastVerifAmount = (oaLoadVerification.getCashAmount() == null ? 0L
                            : oaLoadVerification.getCashAmount())
                            + (oaLoadVerification.getBillAmount() == null ? 0L : oaLoadVerification.getBillAmount());
                    sumLastVerifAmount += lastVerifAmount;
                }
            }
        }
        OaLoanOutDto outDto = new OaLoanOutDto();
        outDto.setSumOaLoanAmount(sumAmount);
        outDto.setSumPayedAmount(sumPayedAmount);
        outDto.setSumNoPayedAmount(sumAmount - sumPayedAmount);
        outDto.setSumSalaryDeduction(sumSalaryDeduction);
        outDto.setSumLastVerifAmount(sumLastVerifAmount);
        outDto.setSumOaLoanAmountDouble(sumAmount / 100.00);
        outDto.setSumPayedAmountDouble(sumPayedAmount / 100.00);
        outDto.setSumNoPayedAmountDouble((sumAmount - sumPayedAmount) / 100.00);
        outDto.setSumSalaryDeductionDouble(sumSalaryDeduction / 100.00);
        outDto.setSumLastVerifAmountDouble(sumLastVerifAmount / 100.00);
        return outDto;
    }

    //方法说明 借支核销报表导出
    @Override
    public void excel_Report(String startTime, String endTime, Integer loanType, Integer loanSubject,
                             Integer loanClassify, String orderId, String oaLoanId, String userName,
                             String mobilePhone, String acctName, Long amountStar, Long amountEnd,
                             String plateNumber, Integer queryType, Integer state, String flowId,
                             Long noPayedStar, Long noPayedEnd, String accessToken,
                             List<Long> subOrgList, com.youming.youche.commons.domain.ImportOrExportRecords record) {

        IPage<OaLoanOutDto> oaLoanOutDtoPage = queryOaLoanTable(startTime, endTime, loanType, loanSubject,
                loanClassify, orderId, oaLoanId, userName, mobilePhone, acctName, amountStar, amountEnd, plateNumber, queryType, state, flowId
                , noPayedStar, noPayedEnd, accessToken, subOrgList, 1, 9999);//借支核销报表数据
        List<OaLoanOutDto> records = oaLoanOutDtoPage.getRecords();
        for (OaLoanOutDto dto : records) {
            dto.setStateName(dto.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                    String.valueOf(dto.getState())).getCodeName());
            // 申请时间
            dto.setAppDates(com.youming.youche.util.DateUtil.asDate(dto.getAppDate()));
            List<Map> auditList = dto.getAuditList();
            if (auditList.size() == 1) {
                dto.setLinkman(auditList.get(0).get("linkman").toString());
            }
            if (auditList.size() == 2) {
                dto.setLinkman(auditList.get(0).get("linkman").toString());
                dto.setLinkman1(auditList.get(1).get("linkman").toString());
            }
            if (auditList.size() == 3) {
                dto.setLinkman(auditList.get(0).get("linkman").toString());
                dto.setLinkman1(auditList.get(1).get("linkman").toString());
                dto.setLinkman2(auditList.get(2).get("linkman").toString());
            }
            if (auditList.size() == 4) {
                dto.setLinkman(auditList.get(0).get("linkman").toString());
                dto.setLinkman1(auditList.get(1).get("linkman").toString());
                dto.setLinkman2(auditList.get(2).get("linkman").toString());
                dto.setLinkman3(auditList.get(3).get("linkman").toString());
            }
            if (auditList.size() == 5) {
                dto.setLinkman(auditList.get(0).get("linkman").toString());
                dto.setLinkman1(auditList.get(1).get("linkman").toString());
                dto.setLinkman2(auditList.get(2).get("linkman").toString());
                dto.setLinkman3(auditList.get(3).get("linkman").toString());
                dto.setLinkman4(auditList.get(4).get("linkman").toString());
            }
//            dto.getLastVerifAmount();// 最近核销金额
//            dto.getPayedAmountDouble();// 累计核销金额
//            dto.getNoPayedAmountStr();// 未核销金额
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "借支流水号", "借支时间", "借支金额",
                    "支付流水号", "最近核销金额",
                    "未核销金额", "累计核销金额", "借支入口",
                    "借支科目", "订单号", "车牌号码",
                    "申请人属性", "申请姓名", "申请号码",
                    "借支人属性", "借支人姓名", "借支人手机号",
                    "核销状态",
                    "一级审核", "二级审核", "三级审核", "四级审核", "五级审核",
                    "核销人"}; //TODO 等待补全字段

            resourceFild = new String[]{
                    "getOaLoanId", "getAppDates", "getAmountDouble",
                    "getFlowId", "getLastVerifAmountDouble",
                    "getNoPayedAmountStr", "getPayedAmountDouble", "getClassifyName",
                    "getLoanSubjectName", "getOrderId", "getPlateNumber",
                    "getLaunchName", "getUserInfoName", "getMobilePhone",
                    "getBorrowerTypeName", "getBorrowName", "getBorrowPhone",
                    "getStateName",
                    "getLinkman", "getLinkman1", "getLinkman2", "getLinkman3", "getLinkman4",
                    "getLastVerifName"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, OaLoanOutDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "借支核销信息.xlsx", inputStream.available());
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

    // 方法说明 借支核销数据导出
    @Async
    @Override
    public void excel_Export(Boolean waitDeal, Integer queryType, String oaLoanId,
                             Integer state, Integer loanSubject, String userName, String orderId, String plateNumber,
                             String startTime, String endTime, String flowId, List<Long> dataPermissionIds,
                             List<Long> subOrgList, String acctName, String mobilePhone, String accessToken,
                             com.youming.youche.commons.domain.ImportOrExportRecords record) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        IPage<OaLoanOutDto> oaLoanOutDtoIPage = selectCancelAll(1, 9999, waitDeal, queryType, oaLoanId, state, loanSubject, userName, orderId, plateNumber,
                startTime, endTime, flowId, dataPermissionIds, subOrgList, acctName, mobilePhone, accessToken);//查询出借支核销数据

        List<OaLoanOutDto> records = oaLoanOutDtoIPage.getRecords();//借支核销数据
        //TODO 导出处理那些数据？暂时留下
        for (OaLoanOutDto dto : records) {
            dto.setStateName(dto.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                    String.valueOf(dto.getState())).getCodeName());
            dto.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), dto.getOrgId()));
            // 申请时间
            dto.setAppDates(com.youming.youche.util.DateUtil.asDate(dto.getAppDate()));
            dto.setPayedAmountDouble(dto.getPayedAmount() == null ? null : dto.getPayedAmount() / 100.00);

        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"借支号码", "归属部门", "申请人", "申请时间", "借支类型",
                    "订单号码", "车牌号码", "借支金额", "已核销金额", "借支票据", "借支状态", "支付流水号"};
            resourceFild = new String[]{"getOaLoanId", "getOrgName", "getUserInfoName", "getAppDates", "getLoanSubjectName",
                    "getOrderId", "getPlateNumber", "getAmountDouble", "getPayedAmountDouble", "getIsNeedBillName", "getStsName", "getFlowId"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, OaLoanOutDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "借支核销列表导出.xlsx", inputStream.available());
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

    // 借支车管 新增  修改
    @Transactional
    @Override
    public void saveOrUpdateOaLoanCar(OaLoanVo oaLoanVo, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        String operComment = "申请借支";
        OaLoanOutDto oaLoanOutDto = new OaLoanOutDto();
        com.youming.youche.commons.constant.SysOperLogConst.OperType operType = com.youming.youche.commons.constant.SysOperLogConst.OperType.Add;
        OaLoan oaLoan = new OaLoan();
        oaLoan.setBankType(oaLoanVo.getBankType() == null ? 1 : oaLoanVo.getBankType());
        oaLoan.setCollectAcctId(oaLoanVo.getCollectAcctId());
        oaLoan.setIsNeedBill(Integer.valueOf(oaLoanVo.getIsNeedBill()));
        oaLoan.setCarOwnerName(oaLoanVo.getCarOwnerName());
        oaLoan.setCarPhone(oaLoanVo.getCarPhone());
        oaLoan.setPlateNumber(oaLoanVo.getPlateNumber());
        oaLoan.setId(oaLoanVo.getId());
        oaLoan.setInsuranceMoney(oaLoanVo.getInsuranceMoney());
        oaLoan.setWeight(oaLoanVo.getWeight());//
        if (oaLoanVo.getLoanSubject() > 0 && oaLoanVo.getLoanSubject() == 5) {   // 违章罚款原因1、因公 2、因私
            oaLoan.setLoanTransReason(oaLoanVo.getLoanTransReason());
        } else {
            oaLoan.setLoanTransReason(-1);
        }
//        oaLoan.setBorrowUserId(oaLoanVo.getAccUserId());
        oaLoan.setBorrowUserInfoId(Long.valueOf(oaLoanVo.getAccUserId()));
        oaLoan.setLoanSubject(oaLoanVo.getLoanSubject());
        if (!StringUtils.isBlank(oaLoanVo.getOrderId())){
            oaLoan.setOrderId(Long.valueOf(oaLoanVo.getOrderId()));
        }else {
            // TODO 2022-7-4 订单号不必填
            oaLoan.setOrderId(null);
        }
        oaLoan.setAmount(com.youming.youche.order.util.OrderUtil.objToLongMul100(oaLoanVo.getAmount()));
        oaLoan.setAppReason(oaLoanVo.getAppReason());
        oaLoan.setAccName(oaLoanVo.getAccName() == null ? "" : oaLoanVo.getAccName());
        oaLoan.setAccNo(oaLoanVo.getAccNo() == null ? "" : oaLoanVo.getAccNo());
        oaLoan.setBankName(oaLoanVo.getBankName() == null ? "" : oaLoanVo.getBankName());
        oaLoan.setBankBranch(oaLoanVo.getBankBranch());
        oaLoan.setRemark(oaLoanVo.getRemark());
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        oaLoan.setAppDate(localDateTime);
        oaLoan.setLaunch(oaLoanVo.getLaunch());//借支发起（1车管中心，2司机)
        oaLoan.setIdentification(oaLoanVo.getIdentification()); //借支订单入口表示
        oaLoan.setClassify(oaLoanVo.getClassify()); //借支分类：1原有的借支申请(内部员工),2车管中心借支
        //申请时间
//        oaLoan.setFileUrl(oaLoanVo.getFileUrl());//文件路径
        oaLoan.setPayUserType(6);//付款方类型
        oaLoan.setReceUserType(6);// 收款方类型
        oaLoan.setBorrowName(oaLoanVo.getBorrowName());// 收款姓名
        oaLoan.setBorrowPhone(oaLoanVo.getBorrowPhone());// 收款手机
        oaLoan.setVerifyRemark(oaLoanVo.getVerifyRemark());// 核销备注
        if (oaLoanVo.getLoanSubject() != null && oaLoanVo.getLoanSubject() == OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT10) {//借支类型 :出险
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String accidentDate = oaLoanVo.getAccidentDate();
            LocalDateTime parse = LocalDateTime.parse(accidentDate, dateTimeFormatter);
            String insuranceDate = oaLoanVo.getInsuranceDate();
            LocalDateTime parse1 = LocalDateTime.parse(insuranceDate, dateTimeFormatter);
            oaLoan.setAccidentDate(parse);//事故时间
            oaLoan.setInsuranceDate(parse1);//出险时间

            oaLoan.setAccidentType(oaLoanVo.getAccidentType());//事故类型
            oaLoan.setAccidentReason(oaLoanVo.getAccidentReason());//事故原因
            oaLoan.setDutyDivide(oaLoanVo.getDutyDivide());
            oaLoan.setAccidentDivide(oaLoanVo.getAccidentDivide());//事故司机
            oaLoan.setInsuranceFirm(oaLoanVo.getInsuranceFirm());//保险公司
            oaLoan.setInsuranceMoney(Long.valueOf(oaLoanVo.getInsuranceMoney()));
            oaLoan.setReportNumber(oaLoanVo.getReportNumber());// 报案号
//          oaLoanOutDto.setAccidentExplain();//事故说明
        }
        oaLoan.setWeight(oaLoanVo.getWeight());
        oaLoan.setLaunch(OaLoanConsts.LAUNCH.LAUNCH1);

        if (oaLoan.getIsNeedBill() == null) {
            throw new BusinessException("请选择是否需要票据！");
        }
        if (oaLoan.getAmount() <= 0L) {
            throw new BusinessException("亲，申请金额不能为空！");
        }
        if (oaLoan.getLoanSubject() < 0) {
            throw new BusinessException("亲，申请科目不能为空！");
        }
        if (oaLoan.getLoanSubject() == OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT4) {
            if (org.apache.commons.lang3.StringUtils.isBlank(oaLoan.getWeight())) {
                throw new BusinessException("请输入过磅重量！");
            }
            try {
                Float.parseFloat(oaLoan.getWeight());
            } catch (NumberFormatException e) {
                throw new BusinessException("过磅重量不正确，请重新输入");
            }
        }
        // 车牌号不必填
//        if (org.apache.commons.lang3.StringUtils.isBlank(oaLoan.getPlateNumber())) {
//            throw new BusinessException("亲，请输入车牌号！");
//        }
//        if (org.apache.commons.lang3.StringUtils.isBlank(oaLoan.getAppReason())) {
//            throw new BusinessException("亲，借款原因不能为空！");
//        }
        if (oaLoan.getLaunch() == OaLoanConsts.LAUNCH.LAUNCH1) {//车管
            if (oaLoan.getLoanSubject() == 5) {
                if (oaLoan.getLoanTransReason() <= 0) {
                    throw new BusinessException("请选择事故原因");
                }
            }
            if (oaLoan.getLoanSubject() == OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT10) {
                if (null == oaLoan.getAccidentDate()) {
                    throw new BusinessException("亲，事故时间不能为空！");
                }
                if (null == oaLoan.getInsuranceDate()) {
                    throw new BusinessException("亲，出险时间不能为空！");
                }
                if (oaLoan.getAccidentType() == null || oaLoan.getAccidentType() <= 0) {
                    throw new BusinessException("亲，事故类型不能为空！");
                }
                if (oaLoan.getAccidentReason() == null || oaLoan.getAccidentReason() <= 0) {
                    throw new BusinessException("亲，事故原因不能为空！");
                }
                if (oaLoan.getDutyDivide() == null || oaLoan.getDutyDivide() <= 0) {
                    throw new BusinessException("亲，责任划分不能为空！");
                }
                if (oaLoan.getAccidentDivide() == null || oaLoan.getAccidentDivide() <= 0) {
                    throw new BusinessException("亲，事故司机不能为空！");
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(oaLoan.getInsuranceFirm())) {
                    throw new BusinessException("亲，保险公司不能为空！");
                }
            }
        }

//        oaLoan.setUserInfoName(loginInfo.getName());//用户名
//        oaLoan.setUserInfoId(loginInfo.getId());//用户id
//        oaLoan.setBorrowPhone(oaLoanVo.getBorrow_phone());//借支人姓名
//        oaLoan.setBankName(oaLoanVo.getBorrow_name());//借支人手机
//        oaLoan.setBorrowUserInfoId(oaLoanVo.getAccUserId());//借支人ID
//        oaLoan.setBorrowType(OaLoanConsts.QUERY_TYPE.driver);//借支人属性
//        oaLoan.setLoanSubject(oaLoanVo.getLongSubject());//借支类型
//        oaLoan.setAmount(Long.valueOf(oaLoanVo.getAmount()));//借支金额
//        oaLoan.setPlateNumber(oaLoanVo.getPlateNumber());//车牌号
        oaLoan.setState(0);//状态（待处理，部分核销，已核销）
//        oaLoan.setRemark(oaLoanVo.getRemark());//借款说明
//        oaLoan.setLaunch(oaLoanVo.getLaunch());//借支发起 :司机
//        oaLoan.setCollectAcctId(oaLoanVo.getCollectAcctId());
//        oaLoan.setIsNeedBill(Integer.valueOf(oaLoanVo.getIsNeedBill()));
////        oaLoanOutDto.setOaLoanId();//借支序列号
//        oaLoan.setCarOwnerName(oaLoanVo.getca);
//        Date date = new Date();
//        Instant instant = date.toInstant();
//        ZoneId zoneId = ZoneId.systemDefault();
//        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
//        oaLoan.setAppDate(localDateTime);//申请时间
////        oaLoan.setFileUrl(oaLoanVo.getFileUrl());//文件路径
//        oaLoan.setPayUserType(6);//付款方类型
//        oaLoan.setReceUserType(6);// 收款方类型

        if (oaLoan.getBorrowUserInfoId() == null) {
            throw new BusinessException("找不到收款用户信息");
        }
        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(oaLoan.getBorrowUserInfoId());
        if (userDataInfo == null) {
            throw new BusinessException("找不到收款用户信息");
        }
//        oaLoan.setBorrowPhone(userDataInfo.getMobilePhone());
//        oaLoan.setBorrowName(userDataInfo.getLoginName());
        oaLoan.setBorrowType(userDataInfo.getUserType());

        //如果有选择订单，则借支的归属部门为订单的归属部门；如果借支没有选择订单，则借支的归属部门为操作员的归属部门。
        if (oaLoan.getOrderId() != null && oaLoan.getOrderId() > -0) {
            long tenantId = -1;
            long orgId = -1;
            OrderInfo orderInfo = orderInfoService.getOrder(oaLoan.getOrderId());
            if (orderInfo == null) {
                OrderInfoH orderInfoH = orderInfoHService.getOrderH(oaLoan.getOrderId());
                if (orderInfoH == null) {
                    throw new BusinessException(" 找不到订单信息");
                }
                tenantId = orderInfoH.getTenantId();
                orgId = orderInfoH.getOrgId();
            } else {
                tenantId = orderInfo.getTenantId();
                orgId = orderInfo.getOrgId();
            }
            oaLoan.setTenantId(tenantId);
            //如果是司机则查询出该司机的归属部门
            if (oaLoanVo.getType() != null && oaLoanVo.getType() == 1) {
                TenantUserRel allTenantUserRelByUserId = tenantUserRelService.getAllTenantUserRelByUserId(oaLoan.getBorrowUserInfoId(), tenantId);
                if (allTenantUserRelByUserId == null) {
                    oaLoan.setOrgId(null);
                } else {
                    oaLoan.setOrgId(allTenantUserRelByUserId.getAttachedOrgId());
                }
            } else {
                oaLoan.setOrgId(orgId);
            }
        } else {
            oaLoan.setOrgId(loginInfo.getOrgIds().get(0));
        }

        oaLoan.setId(oaLoan.getId() == null ? 0 : oaLoan.getId());
        if (oaLoan.getId() < 1) {//新增借支
            oaLoan.setId(0L);
            // 借支序列号
            long incr = redisUtil.incr(EnumConsts.RemoteCache.EXPENSE_NUMBER);
            String oaloanId = CommonUtil.createOaloanId(incr);

            while (1 == 1) {
                //加一个接口判断借支号码是否重复
                Integer integer = this.checkOaLoanId(oaLoan.getOaLoanId(), accessToken);
                if (integer > 0) {
                    //借支序列号
                    oaloanId = CommonUtil.createOaloanId(incr);
                    continue;
                } else {
                    oaLoan.setOaLoanId("BM" + oaloanId);// 创建借支单号
                    break;
                }
            }
            oaLoan.setSts(OaLoanConsts.STS.STS0);// 待审核

            //待审核
            oaLoan.setAppDate(DateUtil.localDateTime(new Date()));// 申请时间
//            oaLoan.setUserInfoId(loginInfo.getUserInfoId());
            // 借支申请人  司机借支的话就是 司机信息  员工借支的话就是 当前登录人信息
            // 司机申请人
            if (oaLoanVo.getType() != null && oaLoanVo.getType() == 1) {
                oaLoan.setUserInfoId(userDataInfo.getId());
                oaLoan.setUserInfoName(userDataInfo.getLinkman());
            } else {
                // 员工借支申请人
                oaLoan.setUserInfoId(loginInfo.getUserInfoId());
                oaLoan.setUserInfoName(loginInfo.getName());
            }

            if (oaLoan.getLaunch() == OaLoanConsts.LAUNCH.LAUNCH1) {
                oaLoan.setTenantId(loginInfo.getTenantId());
            }
        } else {
            operType = com.youming.youche.commons.constant.SysOperLogConst.OperType.Update;
            operComment = "修改借支";
            QueryWrapper<OaLoan> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", oaLoan.getId())
                    .eq("tenant_id", loginInfo.getTenantId());
            OaLoan oaLoan1 = baseMapper.selectOne(queryWrapper);
            oaLoan.setSts(oaLoan1.getSts());
            oaLoan.setState(oaLoan1.getState());
            if (!(oaLoan1.getSts() == OaLoanConsts.STS.STS0 || oaLoan1.getSts() == OaLoanConsts.STS.STS2)) {
                throw new BusinessException("该借支状态已更新，请刷新后再试");
            }
            if (oaLoan1.getSts() == OaLoanConsts.STS.STS2) {//如果审核不通过，修改接口需要重启审核流程
                oaLoan.setSts(OaLoanConsts.STS.STS0);
            }
            oaLoan.setLaunch(oaLoan1.getLaunch());
            oaLoan.setOaLoanId(oaLoan1.getOaLoanId());
            oaLoan.setAppDate(oaLoan1.getAppDate());
            oaLoan.setUserInfoId(oaLoan1.getUserInfoId());// 用户id
            oaLoan.setUserInfoName(oaLoan1.getUserInfoName());// 用户名称
            oaLoan.setTenantId(oaLoan1.getTenantId());

        }
        oaLoan.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        oaLoan.setClassify(oaLoanVo.getClassify());  // 借支分类：1原有的借支申请(内部员工),2车管中心借支
        oaLoan.setLaunch(oaLoanVo.getLaunch());
        if (checkLoanBelongDriver(oaLoan)) {
            oaLoan.setReceUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        } else {
            // 取当前操作的用户类型
            UserDataInfo userDataInfo1 = iUserDataInfoService.getUserDataInfo(oaLoan.getBorrowUserInfoId());
            oaLoan.setReceUserType(userDataInfo1.getUserType());
        }
        this.saveOrUpdate(oaLoan);
        //   跟新附件
        this.saveFileForLoanExpense(oaLoan.getId(), loginInfo.getTenantId(), oaLoan.getUserInfoId(), oaLoanVo.getFileId(), OaLoanData.RELTYPE1);
        //  保持操作日志
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeBorrow;
        String auditCode = AuditConsts.AUDIT_CODE.TUBE_BORROW;

        if (checkLoanBelongDriver(oaLoan)) {
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;
            auditCode = AuditConsts.AUDIT_CODE.DRIVER_BORROW;
            operComment = "司机" + operComment;
        } else {
            operComment = "车管" + operComment;
        }

        sysOperLogService.saveSysOperLog(loginInfo,
                busi_code,
                oaLoan.getId(),
                operType,
                operComment, loginInfo.getTenantId());
        //  启动审批流程  TODO 审核流程 等待审核
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("oaLoanId", oaLoan.getOaLoanId());
        params.put("LId", oaLoan.getId());
        params.put("tenantId", oaLoan.getTenantId());
        boolean b = auditService.startProcess(auditCode, oaLoan.getId(),
                busi_code,
                params, accessToken, loginInfo.getTenantId());
        if (!b) {
            throw new BusinessException("启动审核流程失败！");
        }
    }

    //借支审核
    @Transactional
    @Override
    public int examineOaLoanCar(OaLoanVo oaLoanVo, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        OaLoan oaLoan = this.queryOaLoanById(oaLoanVo.getId());
        if (oaLoan == null) {
            throw new BusinessException("亲，不存在该借支信息！");
        }
        /**
         * 2018-12-07 需求确认，只有待审核和审核中状态才能审核
         */
        if (!(oaLoan.getSts() == OaLoanConsts.STS.STS0 || oaLoan.getSts() == OaLoanConsts.STS.STS1)) {
            throw new BusinessException("该借支状态已更新，请刷新后再试");
        }
//        if (oaLoanVo.getUserId() != null && oaLoanParams.getUserId() > 0) {
//            IUserSV userSV = (UserSV) SysContexts.getBean("userSV");
//            UserDataInfo userDataInfo = userSV.getUserDataInfo(oaLoan.getBorrowUserId());
//            if (userDataInfo == null) {
//                throw new BusinessException("找不到收款用户信息");
//            }
//        }
        if (oaLoanVo.getLoanSubject() > 0 && oaLoanVo.getLoanSubject() == 5) {
            if (oaLoanVo.getLoanTransReason() <= 0) {
                throw new BusinessException("请选择原因，因公或者因私");
            } else {
                oaLoan.setLoanTransReason(oaLoanVo.getLoanTransReason());
                oaLoan.setRemark(oaLoanVo.getRemark());
            }
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        //第一个审核人需要填写收款账户信息
        if (oaLoan.getSts() == OaLoanConsts.STS.STS0) {
            if (oaLoanVo.getAccidentDate() != null) {
                String accidentDate = oaLoanVo.getAccidentDate();
                LocalDateTime parse = LocalDateTime.parse(accidentDate, dateTimeFormatter);
                oaLoan.setAccidentDate(parse);//事故时间
            }
            if (oaLoanVo.getInsuranceDate() != null) {
                String insuranceDate = oaLoanVo.getInsuranceDate();
                LocalDateTime parse1 = LocalDateTime.parse(insuranceDate, dateTimeFormatter);
                ;
                oaLoan.setInsuranceDate(parse1);//出险时间
            }
            if (oaLoanVo.getAccidentType() != null) {
                oaLoan.setAccidentType(oaLoanVo.getAccidentType());
            }
            if (oaLoanVo.getAccidentReason() != null) {
                oaLoan.setAccidentReason(oaLoanVo.getAccidentReason());
            }
            if (oaLoanVo.getDutyDivide() != null) {
                oaLoan.setDutyDivide(oaLoanVo.getDutyDivide());
            }
            if (oaLoanVo.getAccidentDivide() != null) {
                oaLoan.setAccidentDivide(oaLoanVo.getAccidentDivide());
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(oaLoanVo.getInsuranceFirm())) {
                oaLoan.setInsuranceFirm(oaLoanVo.getInsuranceFirm());
            }
            if (oaLoanVo.getInsuranceMoney() != null) {
                oaLoan.setInsuranceMoney(oaLoanVo.getInsuranceMoney());
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(oaLoanVo.getReportNumber())) {
                oaLoan.setReportNumber(oaLoanVo.getReportNumber());
            }
            oaLoan.setAccName(oaLoanVo.getAccountName());
            oaLoan.setAccNo(oaLoanVo.getAccNo());
            oaLoan.setBankName(oaLoanVo.getBankName());
            oaLoan.setBankBranch(oaLoanVo.getBankBranch());
            oaLoan.setBankType(oaLoanVo.getBankType());
            oaLoan.setCollectAcctId(oaLoanVo.getCollectAcctId());

//            if(null != oaLoanParams.getUserId()){
//                oaLoan.setBorrowUserId(oaLoanParams.getUserId());
//            }
//            if(oaLoanParams.getBorrowUserId()!=null){
//                IUserSV userSV = (UserSV) SysContexts.getBean("userSV");
//                UserDataInfo userDataInfo = userSV.getUserDataInfo(oaLoanParams.getBorrowUserId());
//                if (userDataInfo == null) {
//                    throw new BusinessException("找不到收款用户信息");
//                }
//                oaLoan.setBorrowPhone(userDataInfo.getMobilePhone());
//                oaLoan.setBorrowName(userDataInfo.getLinkman());
//                oaLoan.setBorrowType(userDataInfo.getUserType());
//                oaLoan.setBorrowUserId(oaLoanParams.getBorrowUserId());
//            }
            // TODO  预留审批
            //修改状态为审核中
            oaLoan.setSts(OaLoanConsts.STS.STS1);
            oaLoan.setState(OaLoanConsts.STS.STS1);
//            this.saveOrUpdateOaLoanCar(oaLoanVo,accessToken);
            this.saveOrUpdate(oaLoan);
        }
        return 0;
    }

    // 方法说明  借支核销
    @Transactional
    @Override
    public void verificationOaLoanCar(String id, String cashAmountStr, String billAmountStr, List<String> fileUrl,
                                      String strfileId, String verifyRemark, Integer fromWx, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isBlank(cashAmountStr) && StringUtils.isBlank(billAmountStr)) {
            throw new BusinessException("亲，现金核销金额或票据核销金额不能同时为空！");
        }
        double cashAmount = 0;
        double billAmount = 0;
        if (StringUtils.isNotBlank(cashAmountStr)) {
            if (CommonUtil.isNumber(cashAmountStr) && cashAmountStr.matches(percentAge)) {
                Long cashAmountLong = OrderUtil.objToLongMul100(cashAmountStr);//核销金额乘以100
                cashAmount = Double.parseDouble(cashAmountLong.toString());
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(billAmountStr)) {
            if (CommonUtil.isNumber(billAmountStr) && billAmountStr.matches(percentAge)) {
                Long billAmountLong = OrderUtil.objToLongMul100(billAmountStr);// 票据金额乘100
                billAmount = Double.parseDouble(billAmountLong.toString());

            } else {
                throw new BusinessException("亲，请输入正确票据核销金额！");
            }
        }
        double cash = 0;
        double bill = 0;
        if (cashAmount >= 0) {
            cash = cashAmount;
        }
        if (billAmount >= 0) {
            bill = billAmount;
        }

        OaLoan oaLoan = this.queryOaLoanById(Long.valueOf(id));
        //   TODO 待释放  因为审核完 需要 银行 打款接口完善  然后释放
        if (oaLoan.getPayFlowId() == null || oaLoan.getPayFlowId() <= 0) {
            throw new BusinessException("该笔借支尚未打款成功，不能核销！");
        }
        oaLoan.setVerifyRemark(verifyRemark);
        if (oaLoan == null) {
            throw new BusinessException("亲，不存在该借支信息！");
        }
        /**
         * 2018-12-07 需求确认，只有未核销和核销中状态才能核销
         */

        if (!(oaLoan.getState() == OaLoanConsts.STS.STS3 || oaLoan.getState() == OaLoanConsts.STS.STS4)) {
            throw new BusinessException("该借支状态已更新，请刷新后再试");
        }

        if (oaLoan.getVerifyOrgId() != null && oaLoan.getVerifyOrgId() > 0 && oaLoan.getVerifyOpId().longValue() != loginInfo.getUserInfoId()) {
            throw new BusinessException("您没有权限操作");
        }
        int sts = oaLoan.getState();
        if (sts == OaLoanData.STS5) {
            throw new BusinessException("亲，该条借支已经核销完了，不能再核销了");
        }
        Long amount = oaLoan.getAmount();//借支金额
        Long payedAmountOld = 0l; ////已核销金额
        if (oaLoan.getPayedAmount() != null) {
            payedAmountOld = oaLoan.getPayedAmount();//已核销金额
        }

        Long payedAmount = 0l;
        if (cashAmount >= 0) {
            payedAmount += new Double(cashAmount).longValue();
        }
        if (billAmount >= 0) {
            payedAmount += new Double(billAmount).longValue();
        }
        if ((payedAmount + payedAmountOld) > amount) {
            throw new BusinessException("亲，核销金额不能大于待核销金额！");
        }

        OaLoadVerification oaLoadVerification = new OaLoadVerification();//借支报销审核表
        //TODO 通过科目获取 静态数据表
        SysStaticData sysStaticData = this.getSysStaticData("LOAN_SUBJECT", oaLoan.getLoanSubject() + "");
        boolean isWriteDetail = false;//是否司机借支
        if (sysStaticData.getCodeId() == OaLoanData.VERIFY_TYPE_1 && oaLoan.getLaunch() == OaLoanData.LAUNCH2) {
            isWriteDetail = true;
        }

        oaLoan.setPayedAmount((payedAmount + payedAmountOld));//一个核销流程结束才写入核销金额
        if (amount.equals((payedAmount + payedAmountOld))) {//如果借支金额全部核销完，则状态改为核销完成，否则继续核销
            oaLoan.setState(OaLoanData.STS5);
            oaLoan.setSts(OaLoanConsts.STS.STS5);
        } else {
            oaLoan.setState(OaLoanData.STS4);//核销中
            oaLoan.setSts(OaLoanConsts.STS.STS4);
        }
        oaLoan.setVerifyOpId(null);
        oaLoan.setVerifyUserId(loginInfo.getUserInfoId());//当前用户id
        oaLoan.setVerifyUserName(loginInfo.getName());//当前用户名称
        Long amountFee = new Double((cash + bill)).longValue();
        OverdueReceivable overdueReceivable = overdueReceivableService.getBusinessCode(oaLoan.getOaLoanId());
      //  overdueReceivable.setType(2);
        if (isWriteDetail) {
      //      overdueReceivable.setType(2);
            Long borrowUserInfoId = oaLoan.getBorrowUserInfoId();
//             //查看借支人
            UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(borrowUserInfoId);
            if (userDataInfo == null) {
                throw new BusinessException("借支人用户不存在");
            }
            String vehicleAffiliation = "0";
            if (isWriteDetail) {
                // TODO 待订单模块开发
                OrderInfo orderInfo = orderInfoService.getOrder(oaLoan.getOrderId());
                OrderFee orderFee = orderFeeService.getOrderFee(oaLoan.getOrderId());
                if (orderInfo == null) {
                    OrderInfoH orderInfoH = orderInfoHService.getOrderH(oaLoan.getOrderId());
                    if (orderInfoH == null) {
                        throw new BusinessException("找不到订单信息");
                    }
                    OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(oaLoan.getOrderId());
                    vehicleAffiliation = orderFeeH.getVehicleAffiliation();
                } else {
                    vehicleAffiliation = orderFee.getVehicleAffiliation();
                }
            }

            //根据司机id获取车辆信息
            VehicleDataInfo vd = IVehicleDataInfoService.getVehicleDataInfo(oaLoan.getPlateNumber());
            // 根据用户ID和资金渠道类型获取账户信息
            OrderAccount account = iOrderAccountService.queryOrderAccount(oaLoan.getBorrowUserInfoId(), vehicleAffiliation, 0L, oaLoan.getTenantId(), vehicleAffiliation, oaLoan.getReceUserType());
            List<com.youming.youche.order.domain.order.BusiSubjectsRel> busiList = new ArrayList<>();
            // 司机借支核销金额
            com.youming.youche.order.domain.order.BusiSubjectsRel amountFeeSubjectsRel = new com.youming.youche.order.domain.order.BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_VERIFICATION);
            amountFeeSubjectsRel.setAmountFee(amount);
            busiList.add(amountFeeSubjectsRel);
//            // 计算费用集合
            List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OA_LOAN_VERIFICATION, busiList);
//            // 写入账户明细表并修改账户金额费用
            long soNbr = CommonUtil.createSoNbr();
            // 资金账户、流水操作接口完成
            accountDetailsThreeService.insetAccDetOaLoan(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OA_LOAN_VERIFICATION,
                    oaLoan.getBorrowUserInfoId(), userDataInfo.getLoginName(), vehicleAffiliation, vd,
                    busiSubjectsRelList, soNbr, oaLoan.getOrderId(),
                    "", account, userDataInfo.getTenantId(), oaLoan.getReceUserType(), accessToken);
        }
        oaLoadVerification.setSts(OaLoanData.VERIFICATION_STS1);//核销流程结束，核销才生效
        oaLoadVerification.setAmount(amountFee);
        this.saveOrUpdate(oaLoan);//修改借支信息表
        oaLoadVerification.setLId(Long.valueOf(oaLoan.getId()));
        oaLoadVerification.setOrgId(oaLoan.getOrgId());
        oaLoadVerification.setAppName(oaLoan.getUserInfoName());

        oaLoadVerification.setTenantId(loginInfo.getTenantId());

        if (cashAmount >= 0) {
            oaLoadVerification.setCashAmount(new Double(cashAmount).longValue());
        } else {
            oaLoadVerification.setCashAmount(0l);
        }
        if (billAmount >= 0) {
            oaLoadVerification.setBillAmount(new Double(billAmount).longValue());
        } else {
            oaLoadVerification.setBillAmount(0l);
        }
        oaLoadVerification.setOpId(loginInfo.getUserInfoId());
        oaLoadVerification.setOpName(loginInfo.getName());
        oaLoadVerification.setOpDate(DateUtil.localDateTime(new Date()));
        iOaLoadVerificationService.saveOrUpdate(oaLoadVerification);
        // 保持附件
        if (strfileId != null && !"".equals(strfileId)) {
            QueryWrapper<OaFiles> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("REL_ID", oaLoan.getId());
            List<OaFiles> oaFiles = oaFilesMapper.selectList(wrapper1);
            String[] files = strfileId.split(",");
            List<String> oaListFile = new ArrayList<>();
            if (oaFiles != null && oaFiles.size() > 0) {
                for (int i = 0; i < oaFiles.size(); i++) {
                    oaListFile.add(oaFiles.get(i).getFileId() + "");
                }
            }
            for (int i = 0; i < files.length; i++) {
                if (!oaListFile.contains(files[i])) {
                    long fileId = Long.parseLong(files[i]);
                    QueryWrapper<SysAttach> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", fileId);
                    List<SysAttach> attach = sysAttachMapper.selectList(queryWrapper);
                    OaFiles of = new OaFiles();
                    try {
                        FastDFSHelper client = FastDFSHelper.getInstance();
                        of.setRelType(OaLoanData.RELTYPE2);//关联数据类型:1借支 2借支核销 3报销'
                        of.setRelId(oaLoan.getId());
                        of.setFileId(fileId);
                        of.setOpId(loginInfo.getId());
                        of.setTenantId(loginInfo.getTenantId());
                        of.setOpDate(DateUtil.localDateTime(new Date()));
                        of.setFileName(attach.get(0).getFileName());
                        if (attach.get(0).getFileName().indexOf("jpg") > -1
                                || attach.get(0).getFileName().indexOf("gif") > -1
                                || attach.get(0).getFileName().indexOf("png") > -1
                                || attach.get(0).getFileName().indexOf("bmp") > -1
                        ) {
                            of.setFileUrl(CommonUtil.getBigPicUrl(client.getHttpURL(attach.get(0).getStorePath())));
                        } else {
                            of.setFileUrl(client.getHttpURL(attach.get(0).getStorePath()));
                        }
                    } catch (Exception e) {
                        throw new BusinessException("借支" + e);
                    }
                    iOaFilesService.saveOrUpdate(of);
                }
            }
        }
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeBorrow;// 车管借支
        if (checkLoanBelongDriver(oaLoan)) {
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;
        }

        /**
         * 司机借支
         * 反写支付流水表和账户表
         * 2018-07-23 liujl
         */
        if (isWriteDetail) {
            //核销金额
            Long oaLoadVerificationAmount = new Double((cash + bill)).longValue();
            //TODO 1、反写支付流水表
            if (oaLoan.getPayFlowId() != null && oaLoan.getPayFlowId() > 0) {
                //根据流水号和租户id查询提现记录
//                PayoutIntf payoutIntf = payoutIntfSV.getPayoutIntfByFlowId(oaLoan.getPayFlowId(), oaLoan.getTenantId());
                PayoutIntf payoutIntf = payoutIntfThreeService.getPayoutIntfByFlowId(oaLoan.getPayFlowId(), oaLoan.getTenantId());
                payoutIntf.setTxnAmt(payoutIntf.getTxnAmt() - oaLoadVerificationAmount);
                if (oaLoan.getSts() == OaLoanData.STS5) {//已核销
                    payoutIntf.setVerificationDate(new Date());
                    payoutIntf.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
                    payoutIntf.setRespCode(HttpsMain.respCodeSuc);
                }
                payoutIntfThreeService.saveOrUpdate(payoutIntf);
            }
            //2、反写账户表和账户明细
            // 通过userid获取用户信息
            UserDataInfo userDateinfo = iUserDataInfoService.getPhone(oaLoan.getBorrowPhone());
            if (userDateinfo == null) {
                throw new BusinessException("找不到用户信息");
            }
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
            long soNbr = CommonUtil.createSoNbr();
            // 根据用户ID和资金渠道类型获取账户信息(司机)
            OrderAccount driverAccount = iOrderAccountService.queryOrderAccount(oaLoan.getUserInfoId(), OrderAccountConst.
                            VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, 0L, loginInfo.getTenantId(),
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, oaLoan.getReceUserType());
            List<com.youming.youche.order.domain.order.BusiSubjectsRel> busiList = new ArrayList<>();
            //TODO 订单模块
//            // 已付逾期(司机借支核销)
            com.youming.youche.order.domain.order.BusiSubjectsRel amountFeeSubjectsRel1 = new com.youming.youche.order.domain.order.BusiSubjectsRel();
            amountFeeSubjectsRel1.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_VERIFICATION_HANDLE);
            amountFeeSubjectsRel1.setAmountFee(oaLoadVerificationAmount);// 核销金额
            busiList.add(amountFeeSubjectsRel1);
//            // 计算费用集合
            List<com.youming.youche.order.domain.order.BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OA_LOAN_VERIFICATION, busiList);
            // 写入账户明细表并修改账户金额费用

            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OA_LOAN_VERIFICATION,
                    0l, "", driverAccount, busiSubjectsRelList, soNbr, oaLoan.getOrderId(),
                    userDateinfo.getLoginName(), null, loginInfo.getTenantId(), null, "",
                    null, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, loginInfo);
            // 根据用户ID和资金渠道类型获取账户信息(车队)
            OrderAccount tenantAccount = accountDetailsThreeService.queryOrderAccount(sysTenantDef.getAdminUser(),
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, 0L, loginInfo.getTenantId(),
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, oaLoan.getPayUserType(), accessToken);
            List<com.youming.youche.order.domain.order.BusiSubjectsRel> tenantBusiList = new ArrayList<>();
            // 已收逾期(司机借支核销)
            com.youming.youche.order.domain.order.BusiSubjectsRel amountFeeSubjectsRel = new com.youming.youche.order.domain.order.BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_VERIFICATION_RECEIVABLE);
            amountFeeSubjectsRel.setAmountFee(oaLoadVerificationAmount);
            tenantBusiList.add(amountFeeSubjectsRel);
            // 计算费用集合
            List<com.youming.youche.order.domain.order.BusiSubjectsRel> busiSubjectsRels = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OA_LOAN_VERIFICATION, tenantBusiList);
            // 写入账户明细表并修改账户金额费用
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OA_LOAN_VERIFICATION,
                    0l, "",
                    tenantAccount, busiSubjectsRels, soNbr, oaLoan.getOrderId(),
                    userDateinfo.getLoginName(), null, loginInfo.getTenantId(), null, "",
                    null, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, loginInfo);
        }
        try {
            //新增借支应收业务逻辑
            if (overdueReceivable != null && overdueReceivable.getId() != null) {
                overdueReceivable.setPaid(payedAmount + payedAmountOld);
                if(amount.equals((payedAmount + payedAmountOld))){
                    overdueReceivable.setPayConfirm(1);
                }
                overdueReceivableService.update(overdueReceivable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sysOperLogService.saveSysOperLog(loginInfo, busi_code, oaLoan.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "借支核销，金额:" + payedAmount / 100.00 + " 备注：" + verifyRemark);

    }


    /**
     * 查找司机某个时间段的借支油
     * 数据库枚举 'LOAN_SUBJECT'    LOAN_SUBJECT = 8标识借支油
     * STS in (3,4,5,7) 借支审核通过，油转现抵扣借支油状态：NULL未被抵扣，0部分被抵扣
     */
    @Override
    public List<OaLoan> queryCarDriverOaLoan(OilExcDto oilExcDto) {
        List<OaLoan> list = baseMapper.selectOr(oilExcDto);
//        List<OaLoan> oaLoanList = new ArrayList<OaLoan>();
//        if (list != null && list.size() > 0) {
//            for (Object object : list) {
//                if (object != null) {
//                    oaLoanList.add((OaLoan) object);
//                }
//            }
//        }
        if (list != null && list.size() > 0) {

            return list;
        } else {
            return new ArrayList<OaLoan>();
        }
    }

    @Override
    public void setPayFlowIdAfterPay(Long flowId) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(flowId + "") && flowId > 0) {
            LambdaQueryWrapper<OaLoan> qw = new LambdaQueryWrapper<>();
            qw.eq(OaLoan::getFlowId, flowId);
            List<OaLoan> lists = this.list(qw);
            if (lists != null && lists.size() == 1) {
                OaLoan oa = lists.get(0);
                oa.setPayFlowId(flowId);
                this.saveOrUpdate(oa);
            }
        }
    }

    /**
     * 查询收款人
     *
     * @param userName    名称
     * @param mobilePhone 手机号
     * @return
     */
    @Override
    public Page<AccountBankRelDto> getSelfAndBusinessBank(String userName,
                                                          String mobilePhone,
                                                          Integer pageNum,
                                                          Integer pageSize,
                                                          String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<AccountBankRelDto> page = new Page<>(pageNum, pageSize);
        Page<AccountBankRelDto> selfAndBusinessBank = baseMapper.getSelfAndBusinessBank(page, userName, mobilePhone, loginInfo.getTenantId());
        List<AccountBankRelDto> records = selfAndBusinessBank.getRecords();
        for (AccountBankRelDto accountBankRelDto : records) {
            accountBankRelDto.setBankTypeName("");
            if (EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0 == accountBankRelDto.getBankType()) {
                accountBankRelDto.setBankTypeName("私人账户");
            }
            if (EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1 == accountBankRelDto.getBankType()) {
                accountBankRelDto.setBankTypeName("对公账户");
            }
        }
        return selfAndBusinessBank;
    }

    // 查询订单
    @Override
    public Page<OrderSchedulerDto> queryOrderByCarDriver(Boolean isHis,
                                                         String userName,
                                                         String mobilePhone,
                                                         Integer pageNum,
                                                         Integer pageSize,
                                                         String accessToken,
                                                         List<Long> dataPermissionIds,
                                                         List<Long> subOrgList) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        //判断是否有所有数据权限(操作员如果只有查看部门数据的权限，则只能所有归属于本部门的车管借支；如果有查看所有数据的权限，则可以查看所有的车管借支。)
        boolean isAll = false;
        for (Long dataPermissionId : dataPermissionIds) {
            if (dataPermissionId.equals(PermissionConst.MENU_BTN_ID.ALL_DATA)) {
                isAll = true;
                break;
            }
        }
        if (isAll) {
            subOrgList = null;
        }

        Page<OrderSchedulerDto> dtoPage = new Page<>();
        Page<OrderSchedulerDto> page = new Page<>(pageNum, pageSize);
        if (isHis) {
            //历史
            dtoPage = baseMapper.queryOrderByCarDriverHis(page, userName, mobilePhone, subOrgList,
                    OrderConsts.ORDER_STATE.CANCELLED, loginInfo.getTenantId());
        } else {
            dtoPage = baseMapper.queryOrderByCarDriver(page, userName, mobilePhone, subOrgList,
                    OrderConsts.ORDER_STATE.CANCELLED, loginInfo.getTenantId());
        }
        List<OrderSchedulerDto> records = dtoPage.getRecords();
        for (OrderSchedulerDto orderSchedulerDto : records) {
            if (orderSchedulerDto.getDesRegion() != null) { // 到达市
                orderSchedulerDto.setDesRegionName(getSysStaticData("SYS_CITY", orderSchedulerDto.getDesRegion() + "").getCodeName());
            }
            if (orderSchedulerDto.getSourceRegion() != null) { //始发市
                orderSchedulerDto.setSourceRegionName(getSysStaticData("SYS_CITY", orderSchedulerDto.getSourceRegion() + "").getCodeName());
            }
        }
        return dtoPage;
    }

    @Override
    public OaLoan queryOaLoanById(Long LId, String... busiCode) {
        OaLoan oaLoan = null;
        QueryWrapper<OaLoan> wrapper = new QueryWrapper<>();
        if (LId !=null) {
//            ca.add(Restrictions.eq("LId", LId));
            wrapper.eq("id", LId);
        } else if (busiCode.length > 0) {
//            ca.add(Restrictions.eq("oaLoanId", busiCode[0]));
            wrapper.eq("oa_loan_id", busiCode[0]);
        } else {
            return null;
        }
//        List<OaLoan> lists = ca.list();
        List<OaLoan> lists = baseMapper.selectList(wrapper);
        if (lists.size() > 0) {
            oaLoan = (OaLoan) lists.get(0);
        }
        return oaLoan;

    }

    /**
     * 跟新借支 报销的文件
     *
     * @param id
     * @param tenantId
     * @param userId
     * @param strfileId
     * @param type      关联数据类型 1 借支  2 借支核销 3 报销
     */

    @Override
    public void saveFileForLoanExpense(Long id, Long tenantId, Long userId, String strfileId, Integer type) {
        //删除原有的图片
        try {
            FastDFSHelper client = FastDFSHelper.getInstance();
            QueryWrapper<OaFiles> wrapper = new QueryWrapper<>();
            wrapper.eq("REL_ID", id)
                    .eq("REL_TYPE", type)
                    .eq("TENANT_ID", tenantId);
            List<OaFiles> oaFiles = oaFilesMapper.selectList(wrapper);
            for (OaFiles oaFiles1 : oaFiles) {
                if (oaFiles1.getFileId() != null) { // 关联文件id // 不等与空而且 转来的字段中包含getFileId
                    if (StringUtils.isNotBlank(strfileId) && strfileId.contains("" + oaFiles1.getFileId())) {
                        continue;
                    }
                    QueryWrapper<SysAttach> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", oaFiles1.getFileId());
                    SysAttach sysAttach = sysAttachMapper.selectOne(queryWrapper);
                    if (sysAttach != null) {
                        client.delete(sysAttach.getStorePath()); // 删除路径
                    }
                }
            }
            // 先删除 然后保存
            oaFilesMapper.delete(wrapper);
            // 保存附件
            if (strfileId != null && !"".equals(strfileId)) {
                String[] files = strfileId.split(",");
                for (int i = 0; i < files.length; i++) {
                    long fileId = Long.parseLong(files[i]);
                    QueryWrapper<SysAttach> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", fileId);
                    List<SysAttach> attach = sysAttachMapper.selectList(queryWrapper);
                    OaFiles of = new OaFiles();
                    of.setRelType(type);
                    of.setRelId(id);
                    of.setFileId(fileId);
                    of.setOpId(userId);
                    of.setOpDate(DateUtil.localDateTime(new Date()));
                    of.setTenantId(tenantId);
                    of.setFileName(attach.get(0).getFileName());
                    if (attach.get(0).getFileName().indexOf("jpg") > -1
                            || attach.get(0).getFileName().indexOf("gif") > -1
                            || attach.get(0).getFileName().indexOf("png") > -1
                            || attach.get(0).getFileName().indexOf("bmp") > -1) {
                        of.setFileUrl(CommonUtil.getBigPicUrl(client.getHttpURL(attach.get(0).getStorePath())));
                    } else {
                        of.setFileUrl(client.getHttpURL(attach.get(0).getStorePath()));
                    }
                    // 删除或新增
                    iOaFilesService.saveOrUpdate(of);
                }
            }
        } catch (Exception e) {
            throw new BusinessException("操作失败");
        }
    }

    /**
     * 取消借支
     *
     * @param oaLoanVo
     * @param accessToken
     * @return
     */
    @Override
    public String cancelOaLoan(OaLoanVo oaLoanVo, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        OaLoan oaLoan = this.queryOaLoanById(oaLoanVo.getId());
        if (oaLoan != null) {
            if (oaLoan.getSts() == OaLoanConsts.STS.STS0 || oaLoan.getSts() == OaLoanConsts.STS.STS2) {
                oaLoan.setSts(8);
            } else {
                throw new BusinessException("该借支状态已更新，请刷新后再试");
            }
        } else {
            throw new BusinessException("查询不到借支信息");
        }
        this.saveOrUpdate(oaLoan);
        // 保存操作日志 老代码没有用到
//        ISysOperLogSV logSV = (ISysOperLogSV) SysContexts.getBean("sysOperLogSV");
//        List<SysStaticData> dataList = SysStaticDataUtil.getSysStaticDataList("LOAN_SUBJECT_APP");//司机借支
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeBorrow;//车管借支
        String auditCode = AuditConsts.AUDIT_CODE.TUBE_BORROW;
        if (checkLoanBelongDriver(oaLoan)) {
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;
            auditCode = AuditConsts.AUDIT_CODE.DRIVER_BORROW;
        }
        sysOperLogService.saveSysOperLog(loginInfo, busi_code, oaLoan.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Del, "借支取消");
        //结束审核流程
        try {
            auditOutService.cancelProcess(auditCode, oaLoan.getId(), loginInfo.getTenantId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "借支取消成功";
    }

    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        Long LId = DataFormat.getLongKey(paramsMap, "LId");
        Long tenantId = DataFormat.getLongKey(paramsMap, "tenantId");
        OaLoan oaLoan = this.queryOaLoanById(LId);

        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(tenantId);
        SysStaticData sysStaticData = SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "LOAN_SUBJECT", String.valueOf(oaLoan.getLoanSubject()));
//		if(StringUtils.equals(sysTenantDef.getAdminUser()+"",oaLoan.getBorrowUserInfoId()+"")){
//			throw new BusinessException("该笔借支存在异常，请驳回后，重新申请！");
//		}
//
        // 归属司机的费用都要反写 --违章罚款都属于车管借支
        if (sysStaticData.getCodeId() == OaLoanData.VERIFY_TYPE_1 && oaLoan.getLaunch() == OaLoanData.LAUNCH2) {
            Long payUserId = oaLoan.getUserInfoId();
            UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(payUserId);
            if (userDataInfo == null) {
                throw new BusinessException("借支人用户不存在");
            }
            OrderLimit ol = iOrderLimitService.getOrderLimitByUserIdAndOrderId(payUserId, oaLoan.getOrderId(), oaLoan.getReceUserType());
            if (ol == null) {
                throw new BusinessException("未找到用户id为:" + payUserId + " 订单号为:" + oaLoan.getOrderId() + "订单限制信息");
            }
            if (ol.getOrderDate() == null) {
                throw new BusinessException("订单号为:" + oaLoan.getOrderId() + "订单限制信息靠台时间为空");
            }

            Map payResult = new HashMap();
            String vehicleAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;
            Long soNbr = CommonUtil.createSoNbr();
            //获取司机对私默认收款账户
            AccountBankRel defalutAccountBankRel = iAccountBankRelService.getDefaultAccountBankRel(oaLoan.getUserInfoId(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, oaLoan.getReceUserType());
            // 根据用户ID和资金渠道类型获取账户信息(司机)
            OrderAccount driverAccount = iOrderAccountService.queryOrderAccount(oaLoan.getUserInfoId(), vehicleAffiliation, 0L, tenantId, vehicleAffiliation, oaLoan.getReceUserType());
            List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
            // 应收逾期(司机借支)
            BusiSubjectsRel amountFeeSubjectsRel1 = new BusiSubjectsRel();
            amountFeeSubjectsRel1.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_RECEIVABLE);
            amountFeeSubjectsRel1.setAmountFee(oaLoan.getAmount());
            busiList.add(amountFeeSubjectsRel1);
            // 计算费用集合
            List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OA_LOAN_AVAILABLE, busiList);
            // 写入账户明细表并修改账户金额费用
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OA_LOAN_AVAILABLE, sysTenantDef.getAdminUser(), sysTenantDef.getShortName(), driverAccount, busiSubjectsRelList, soNbr, oaLoan.getOrderId(), userDataInfo.getLoginName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);
            // 根据用户ID和资金渠道类型获取账户信息(车队)
            OrderAccount tenantAccount = iOrderAccountService.queryOrderAccount(sysTenantDef.getAdminUser(), vehicleAffiliation, 0L, tenantId, vehicleAffiliation, oaLoan.getPayUserType());
            List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
            // 应付逾期(司机借支)
            BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_HANDLE);
            amountFeeSubjectsRel.setAmountFee(oaLoan.getAmount());
            tenantBusiList.add(amountFeeSubjectsRel);
            // 计算费用集合
            List<BusiSubjectsRel> tenantSubjectsRels = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OA_LOAN_AVAILABLE, tenantBusiList);
            // 写入账户明细表并修改账户金额费用
            // TODO 待确定 userDataInfo.getUserId()
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OA_LOAN_AVAILABLE, userDataInfo.getAttachedUserId(), userDataInfo.getLinkman(), tenantAccount, tenantSubjectsRels, soNbr, oaLoan.getOrderId(), userDataInfo.getLoginName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);
            // 添加银行流水
            com.youming.youche.order.domain.order.PayoutIntf payoutIntf = new com.youming.youche.order.domain.order.PayoutIntf();
            payoutIntf.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15表示提现(志鸿)
            payoutIntf.setObjId(Long.parseLong(userDataInfo.getMobilePhone()));
            if (defalutAccountBankRel != null) {
                payoutIntf.setBusiCode(defalutAccountBankRel.getBankId() == null ? "" : getSysStaticData("BANK_TYPE", String.valueOf(defalutAccountBankRel.getBankId())).getCodeName());
                payoutIntf.setProvince(defalutAccountBankRel.getProvinceName());
                payoutIntf.setCity(defalutAccountBankRel.getCityName());
                payoutIntf.setAccNo(defalutAccountBankRel.getPinganCollectAcctId());
                payoutIntf.setAccName(defalutAccountBankRel.getAcctName());
            }
            payoutIntf.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
            payoutIntf.setTxnAmt(oaLoan.getAmount());
            payoutIntf.setUserId(oaLoan.getUserInfoId());
            //会员体系开始
            payoutIntf.setPayUserType(oaLoan.getPayUserType());
            payoutIntf.setUserType(oaLoan.getReceUserType());
            //会员体系结束
            payoutIntf.setCreateDate(DateUtil.localDateTime(new Date()));
            payoutIntf.setOrderId(oaLoan.getOrderId());
            //查询用户是否车队
            if (oaLoan.getReceUserType() == SysStaticDataEnum.USER_TYPE.ADMIN_USER) {
                // TODO  老表的getTenantId 是现在的id
                payoutIntf.setTenantId(sysTenantDefService.getSysTenantDefByAdminUserId(oaLoan.getUserInfoId()).getId());
            } else {
                payoutIntf.setTenantId(-1L);
            }
            payoutIntf.setPayTenantId(tenantId);
            payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);//虚拟-虚拟
            payoutIntf.setRemark("借支模块");
            payoutIntf.setSourceRemark(SysStaticDataEnum.SOURCE_REMARK.SOURCE_REMARK_2);
            payoutIntf.setPayObjId(sysTenantDef.getAdminUser());
            payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);//打款类型
            payoutIntf.setBusiId(EnumConsts.PayInter.OA_LOAN_AVAILABLE);//21000023L
            payoutIntf.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_RECEIVABLE);
            payoutIntf.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
            payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
            payoutIntf.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);//付款优先级别
            if (oaLoan.getIsNeedBill() == 0) {
                payoutIntf.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL4);
                payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//私人付款账户余额
            } else if (oaLoan.getIsNeedBill() == 1) {
                payoutIntf.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL5);
                payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//对公付款账户余额
            }
            //是否自动打款
            boolean isAutoTransfer = iPayFeeLimitService.isMemberAutoTransfer(tenantId);
            if (isAutoTransfer) {
                payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);//系统自动打款
            } else {
                payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//手动打款
            }
            payoutIntf.setBusiCode(oaLoan.getOaLoanId());
            payoutIntf.setPlateNumber(oaLoan.getPlateNumber());
            if (sysTenantDef != null) {
                payoutIntf.setBankRemark("付款方:" + sysTenantDef.getName());
            }
            iPayoutIntfService.doSavePayOutIntfForOA(payoutIntf, accessToken);
            // 根据借支号 查询支付id
            com.youming.youche.order.domain.order.PayoutIntf payoutIntfId = iPayoutIntfService.getPayoutIntfId(oaLoan.getOaLoanId());
            payResult.put("flowId", payoutIntfId.getId());
            // 写入订单限制表和订单资金流向表
            ParametersNewDto dto = orderOilSourceService.setParametersNew(userDataInfo.getId(), userDataInfo.getMobilePhone(), EnumConsts.PayInter.OA_LOAN_AVAILABLE, oaLoan.getOrderId(), oaLoan.getAmount(), vehicleAffiliation, "");
            dto.setOaLoanId(LId);
            dto.setFlowId(payoutIntfId.getId());
            dto.setTenantId(payoutIntfId.getPayTenantId());
            SysTenantDefDto sysTenantDefDto = new SysTenantDefDto();
            org.springframework.beans.BeanUtils.copyProperties(sysTenantDef, sysTenantDefDto);
            dto.setSysTenantDef(sysTenantDefDto);
            dto.setBatchId(soNbr.toString());
            busiSubjectsRelList.addAll(tenantSubjectsRels);
            orderOilSourceService.busiToOrderNew(dto, busiSubjectsRelList, loginInfo);
            oaLoan.setFlowId(payoutIntfId.getId());
        } else {
            UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(oaLoan.getBorrowUserInfoId());
            if (userDataInfo == null) {
                throw new BusinessException("根据用户id：" + oaLoan.getBorrowUserInfoId() + " 未找到用户信息");
            }
            com.youming.youche.order.domain.order.PayoutIntf payoutIntf = new com.youming.youche.order.domain.order.PayoutIntf();
            payoutIntf.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15表示提现(志鸿)
            if (org.apache.commons.lang3.StringUtils.isNotBlank(userDataInfo.getMobilePhone())) {
                payoutIntf.setObjId(Long.parseLong(userDataInfo.getMobilePhone()));
            }
            payoutIntf.setUserId(userDataInfo.getId());
            //会员体系开始
            payoutIntf.setUserType(oaLoan.getReceUserType());
            payoutIntf.setPayUserType(oaLoan.getPayUserType());
            //会员体系结束
            payoutIntf.setPinganCollectAcctId(oaLoan.getCollectAcctId());
            payoutIntf.setAccNo(oaLoan.getCollectAcctId());
            if (oaLoan.getBankType() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0) {
                payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
            } else if (oaLoan.getBankType() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) {
                payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT);
            }
            List<AccountBankRel> accountBankRels = iAccountBankRelService.queryAccountBankRel(oaLoan.getUserInfoId(), oaLoan.getReceUserType(), null);
            if (accountBankRels != null && accountBankRels.size() > 0) {
                for (AccountBankRel ac : accountBankRels) {
                    if (org.apache.commons.lang3.StringUtils.equals(oaLoan.getCollectAcctId(), ac.getPinganCollectAcctId()) && ac.getBankType() == oaLoan.getBankType()) {

                        payoutIntf.setBusiCode(ac.getBankId() == null ? "" : getSysStaticData("BANK_TYPE", String.valueOf(ac.getBankId())).getCodeName());
                        payoutIntf.setProvince(ac.getProvinceName());
                        payoutIntf.setCity(ac.getCityName());
                        payoutIntf.setAccName(ac.getAcctName());
                    }
                }
            }
            payoutIntf.setTxnAmt(oaLoan.getAmount());
            payoutIntf.setCreateDate(DateUtil.localDateTime(new Date()));
            payoutIntf.setOrderId(oaLoan.getOrderId());
            payoutIntf.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
            //查询用户是否车队
            SysTenantDef sysTenantDef_rece = sysTenantDefService.getSysTenantDefByAdminUserId(oaLoan.getBorrowUserInfoId());
            if (oaLoan.getReceUserType() == SysStaticDataEnum.USER_TYPE.ADMIN_USER && sysTenantDef_rece != null) {
                payoutIntf.setTenantId(sysTenantDef_rece.getId());
            } else {
                payoutIntf.setTenantId(-1L);
            }
            payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);//虚拟-虚拟
            payoutIntf.setRemark("借支模块");
            payoutIntf.setSourceRemark(SysStaticDataEnum.SOURCE_REMARK.SOURCE_REMARK_2);
            if (sysTenantDef != null) {
                payoutIntf.setPayObjId(sysTenantDef.getAdminUser());
            }
            payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);//打款类型
            payoutIntf.setPayTenantId(tenantId);
            payoutIntf.setBusiId(EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE);//21000113L
            payoutIntf.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_TUBE_RECEIVABLE);
            if (oaLoan.getIsNeedBill() == 0) {
                payoutIntf.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL4);
                payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//私人付款账户余额
            } else if (oaLoan.getIsNeedBill() == 1) {
                payoutIntf.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL5);
                payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//对公付款账户余额
            }
            if (oaLoan.getReceUserType() == SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.SERVICE);
            } else if (oaLoan.getReceUserType() == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.STAFF);
            } else {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.USER);
            }
            payoutIntf.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);//付款优先级别
            //是否自动打款
            Boolean isAutoTransfer = iPayFeeLimitService.isMemberAutoTransfer(oaLoan.getTenantId());
            /**
             * 版本更替 >> 2018-07-04 liujl
             * 判断车队是否勾选了自动打款
             * （1）、自动打款
             *       A、对公收款账户的余额转到对公付款账户。
             *       B、判断对公付款账户的余额是否大于预付金额，
             *         大于：转到司机的对私收款账户，在打款记录中增加一条已打款记录（payout_intf表）并已核销，同时触发提现接口。（若提现失败,记录提现失败记录到payout_intf表）
             *         小于：在打款记录中增加一条待打款记录（payout_intf表）并未核销，工资金额增加到车队的应付逾期账户。
             * （2）、不是自动打款
             *       在打款记录中增加一条待打款记录（payout_intf表）并未核销，工资金额增加到车队的应付逾期账户。
             */
            if (isAutoTransfer) {
                payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);//系统自动打款
            } else {
                payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//手动打款
            }
            payoutIntf.setBusiCode(oaLoan.getOaLoanId());
            payoutIntf.setPlateNumber(oaLoan.getPlateNumber());
            if (sysTenantDef != null) {
                payoutIntf.setBankRemark("付款方:" + sysTenantDef.getName());
            }
            //   TODO 订单 模块报错 待解开
            iPayoutIntfService.doSavePayOutIntfForOA(payoutIntf, accessToken);
            // 根据借支单号 获取到支付流水号
            com.youming.youche.order.domain.order.PayoutIntf payoutIntfId = iPayoutIntfService.getPayoutIntfId(oaLoan.getOaLoanId());
            // TODO  支付id
            oaLoan.setFlowId(payoutIntfId.getId());
            String vehicleAffiliation = "0";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("yyyyMM", new String[]{DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2)});
            Long soNbr = CommonUtil.createSoNbr();
            // 根据用户ID和资金渠道类型获取账户信息(司机)
            OrderAccount driverAccount = iOrderAccountService.queryOrderAccount(userDataInfo.getId(), vehicleAffiliation, 0L, tenantId, vehicleAffiliation, oaLoan.getReceUserType());
            List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
            // 应收逾期(车管借支)
            BusiSubjectsRel amountFeeSubjectsRel1 = new BusiSubjectsRel();
            amountFeeSubjectsRel1.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_TUBE_RECEIVABLE);
            amountFeeSubjectsRel1.setAmountFee(oaLoan.getAmount());
            busiList.add(amountFeeSubjectsRel1);
            // 计算费用集合
            List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE, busiList);
            // 写入账户明细表并修改账户金额费用
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE,
                    sysTenantDef.getAdminUser(), sysTenantDef.getShortName(), driverAccount, busiSubjectsRelList, soNbr, oaLoan.getOrderId(), userDataInfo.getLoginName(),
                    null, tenantId, null, "", null, vehicleAffiliation, loginInfo);
            // 根据用户ID和资金渠道类型获取账户信息(车队)
            OrderAccount tenantAccount = iOrderAccountService.queryOrderAccount(sysTenantDef.getAdminUser(), vehicleAffiliation, 0L, tenantId, vehicleAffiliation, oaLoan.getPayUserType());
            List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
            // 应付逾期（车管借支)
            BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.OA_LOAN_TUBE_HANDLE);
            amountFeeSubjectsRel.setAmountFee(oaLoan.getAmount());
            tenantBusiList.add(amountFeeSubjectsRel);
            // 计算费用集合
            List<BusiSubjectsRel> tenantSubjectsRels = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE, tenantBusiList);
            // 写入账户明细表并修改账户金额费用
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE, userDataInfo.getId(), userDataInfo.getLinkman(), tenantAccount, tenantSubjectsRels, soNbr, oaLoan.getOrderId(), userDataInfo.getLoginName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);
            //写入订单限制表和订单资金流向表
            ParametersNewDto dto = orderOilSourceService.setParametersNew(userDataInfo.getId(), userDataInfo.getMobilePhone(), EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE, oaLoan.getOrderId(), oaLoan.getAmount(), vehicleAffiliation, "");
            dto.setOaLoanId(oaLoan.getId());
            dto.setFlowId(payoutIntfId.getId());
            dto.setTenantId(tenantId);
            SysTenantDefDto sysTenantDefDto = new SysTenantDefDto();
            org.springframework.beans.BeanUtils.copyProperties(sysTenantDef, sysTenantDefDto);
            dto.setSysTenantDef(sysTenantDefDto);

            dto.setBatchId(soNbr.toString());
            busiSubjectsRelList.addAll(tenantSubjectsRels);
            orderOilSourceService.busiToOrderNew(dto, busiSubjectsRelList, loginInfo);
        }
        oaLoan.setVerifyDate(DateUtil.localDateTime(new Date()));
        oaLoan.setSts(OaLoanConsts.STS.STS3);
        oaLoan.setState(OaLoanConsts.STS.STS3);
        // 审核成功，修改状态为未核销
        this.saveOrUpdate(oaLoan);
        //反写操作对列
        if (oaLoan.getOrderId() != null && oaLoan.getOrderId() > 0) {
            iOrderOpRecordService.saveOrUpdate(oaLoan.getOrderId(), 221, accessToken);//借支
        }
        String operComment = "车管审核成功";
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeBorrow;
        if (checkLoanBelongDriver(oaLoan)) {
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;
            operComment = "司机审核成功";
        }

        sysOperLogService.saveSysOperLog(loginInfo,
                busi_code,
                oaLoan.getId(),
                SysOperLogConst.OperType.Audit,
                operComment, loginInfo.getTenantId());
    }

    /**
     * 流程结束，审核不通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
//        IOaLoanSV oaLoanSV = (IOaLoanSV) SysContexts.getBean("oaLoanSV");
        Long LId = DataFormat.getLongKey(paramsMap, "LId");
//        OaLoan oaLoan = oaLoanSV.queryOaLoanById(LId);
        OaLoan oaLoan = this.queryOaLoanById(LId);
        oaLoan.setVerifyDate(DateUtil.localDateTime(new Date()));
        oaLoan.setSts(OaLoanConsts.STS.STS2);
        oaLoan.setState(OaLoanConsts.STS.STS2);
        // 审核成功，修改状态为未审核未通过
//        oaLoanSV.saveOrUpdateOaLoan(oaLoan);
        this.saveOrUpdate(oaLoan);
      /*  String operComment = "车管审核不通过";
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code  = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeBorrow;
        if (checkLoanBelongDriver(oaLoan)){
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;
            operComment = "司机审核不通过";
        }
        sysOperLogService.saveSysOperLog(loginInfo,
                busi_code,
                oaLoan.getId(),
                SysOperLogConst.OperType.Audit,
                operComment,loginInfo.getTenantId());*/
    }

    @Override
    public List<WorkbenchDto> getTableCostBorrowCount() {
        List<String> adminSubjects = getLoanBelongAdminSubjectList();
        List<String> driverSubjects = getLoanBelongDriverSubjectList();
        List<WorkbenchDto> tableCostBorrowDriverCount = baseMapper.getTableCostBorrowDriverCount(driverSubjects);
        List<WorkbenchDto> tableCostBorrowStaffCount = baseMapper.getTableCostBorrowStaffCount(adminSubjects);

        Map<Long, Long> tableCostBorrowDriverCountMap = new HashMap<>();
        for (WorkbenchDto workbenchDto : tableCostBorrowStaffCount) {
            tableCostBorrowDriverCountMap.put(workbenchDto.getTenantId(), workbenchDto.getCount());
        }

        for (WorkbenchDto workbenchDto : tableCostBorrowDriverCount) {
            workbenchDto.setCount(workbenchDto.getCount() + tableCostBorrowDriverCountMap.getOrDefault(workbenchDto.getTenantId(), 0L));
        }


        List<WorkbenchDto> workbenchDtoList = new ArrayList<>();
        for (WorkbenchDto workbenchDto : tableCostBorrowDriverCount) {
            Optional<WorkbenchDto> optional = tableCostBorrowStaffCount.stream().filter(item -> item.getTenantId().equals(workbenchDto.getTenantId()) && item.getUserInfoId().equals(workbenchDto.getUserInfoId())).findFirst();
            if (optional.isPresent()) {
                workbenchDto.setCount(workbenchDto.getCount() + optional.get().getCount());
            }

            workbenchDtoList.add(workbenchDto);
        }

        for (WorkbenchDto workbenchDto : tableCostBorrowStaffCount) {
            Optional<WorkbenchDto> optional = workbenchDtoList.stream().filter(item -> item.getTenantId().equals(workbenchDto.getTenantId()) && item.getUserInfoId().equals(workbenchDto.getUserInfoId())).findFirst();
            if (!optional.isPresent()) {
                workbenchDtoList.add(workbenchDto);
            }
        }

        return workbenchDtoList;
    }

    @Override
    public List<WorkbenchDto> getTableCostBorrowMeCount() {
        List<String> adminSubjects = getLoanBelongDriverSubjectList();

        // 借支申请  只需要看员工借支里面的  我申请的
        List<WorkbenchDto> tableCostBorrowStaffCount = baseMapper.getTableCostBorrowMeStaffCount(adminSubjects);

        return tableCostBorrowStaffCount;
    }

    @Override
    public Long queryAdvanceUid(Long orgId, LocalDateTime startDate, LocalDateTime endDate, Integer lauach, Long tenantId) {
        LambdaQueryWrapper<OaLoan> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(OaLoan::getOrgId, orgId);
        lambdaQueryWrapper.eq(OaLoan::getTenantId, tenantId);
        lambdaQueryWrapper.eq(OaLoan::getLaunch, lauach);
        lambdaQueryWrapper.in(OaLoan::getSts, 3, 4, 5);
        lambdaQueryWrapper.between(OaLoan::getVerifyDate, startDate, endDate);
        //   lambdaQueryWrapper.eq(OaLoan::getLoanSubject,subject);
        List<OaLoan> loanList = super.list(lambdaQueryWrapper);
        Long amount = 0L;
        if (loanList != null && loanList.size() > 0) {
            for (OaLoan o : loanList
            ) {
                if (o.getAmount() != null) {
                    amount += o.getAmount();
                }
            }
        }
        return amount;
    }

    @Override
    public OaLoan selectByOrder(String orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        QueryWrapper<OaLoan> oaLoanQueryWrapper = new QueryWrapper<>();
        oaLoanQueryWrapper.eq("identification", 1)
                .eq("order_id", orderId)
                .eq("tenant_id", loginInfo.getTenantId());
        List<OaLoan> list = super.list(oaLoanQueryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public OaLoan selectByNumber(String number,Long userId) {
        LambdaQueryWrapper<OaLoan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OaLoan::getOaLoanId,number);
        if(userId != null) {
            lambdaQueryWrapper.eq(OaLoan::getBorrowUserInfoId, userId);
        }
        List<OaLoan> list = super.list(lambdaQueryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public GetStatisticsDto getStatistics(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        GetStatisticsDto dto = new GetStatisticsDto();

        dto.setOaLoanCout(this.queryOaloanCount(accessToken)); //借支-待审核、审核中

        dto.setClaimInfoCount(iClaimExpenseInfoService.queryClaimInfoCount(accessToken)); // 报销审核

        dto.setApplyCount(iApplyRecordService.queryApplyCount(accessToken)); // 邀请审核

        List<OrderTransferInfo> list = iOrderTransferInfoService.queryTransferInfoList(OrderConsts.TransferOrderState.TO_BE_RECIVE, loginInfo.getTenantId(), null);
        dto.setOnlineReceiptCount(list == null ? 0 : list.size()); // 在线接单
        dto.setOrderTrackingCount(iOrderInfoThreeService.queryOrderNumberByState(1, accessToken)); // 订单跟踪

        Integer number1 = iOrderInfoThreeService.queryOrderNumberByState(3, accessToken); // 价格审核
        Integer number2 = orderAgingInfoService.getStatisticsStatuteOfLimitations(loginInfo); // 时效审核
        Integer number3 = orderInfoService.getStatisticsOrderReview(accessToken, 3);// 异常审核
        Integer number4 = orderInfoService.getStatisticsOrderReview(accessToken, 2);// 回单审核
        dto.setOrderAuditCount((null == number1 ? 0 : number1)
                + (null == number2 ? 0 : number2)
                + (null == number3 ? 0 : number3)
                + (null == number4 ? 0 : number4)); // 订单审核

        dto.setCostReportAuditCount(iOrderMainReportService.getOrderCostReportAuditCount(accessToken)); // 费用审核

        dto.setPayManagerCount(this.doQueryAllPayManager(accessToken));

        dto.setCarCostReportAuditCount(iVehicleExpenseService.getStatisticsCarCost(accessToken)); // 车辆费用审核

        return dto;
    }

    @Override
    public Integer queryOaloanCount(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        Long userId = loginInfo.getId();
        List<Long> lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.TUBE_BORROW, userId, tenantId);
        List<Long> lids_driver = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.DRIVER_BORROW, userId, tenantId);

        if (lids != null && lids.size() > 0) {
            lids.addAll(lids_driver);
        } else {
            lids = lids_driver;
        }

        //判断是否有所有数据权限(操作员如果只有查看部门数据的权限，则只能所有归属于本部门的车管借支；如果有查看所有数据的权限，则可以查看所有的车管借支。)
        LambdaQueryWrapper<OaLoan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OaLoan::getTenantId, tenantId);
        queryWrapper.in(OaLoan::getSts, 0, 1);
        queryWrapper.in(lids != null && lids.size() > 0, OaLoan::getOaLoanId, lids);
        int count = this.count(queryWrapper);

        return count;

    }

    @Override
    public Integer doQueryAllPayManager(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        StringBuffer lidStr = new StringBuffer();
        String lidStrSub = "";
        StringBuffer orgStr = new StringBuffer();
        String orgStrSub = "";

        List<Long> lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.PAY_MANAGER, loginInfo.getId(), loginInfo.getTenantId());
        if (lids != null && lids.size() != 0) {
            for (Long lid : lids) {
                lidStr.append("'").append(lid).append("',");
            }
        }

        if (!iSysRoleService.hasAllData(loginInfo)) {
            List<Long> orgList = sysOrganizeService.getSubOrgList(loginInfo.getTenantId(), loginInfo.getOrgIds());
            for (Long aLong : orgList) {
                orgStr.append("'").append(aLong).append("',");
            }
        }

        if (lidStr.length() != 0) {
            lidStrSub = lidStr.substring(0, lidStr.length() - 1);
        }
        if (orgStr.length() != 0) {
            orgStrSub = orgStr.substring(0, orgStr.length() - 1);
        }

        return baseMapper.doQueryAllPayManager(loginInfo.getTenantId(), lidStrSub, orgStrSub);
    }

    @Override
    public Page<AccountBankRelDto> getSelfAndBusinessBankWx(String acctName, String mobilePhone, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        IPage<AccountBankRelDto> selfAndBusinessBank = baseMapper.getSelfAndBusinessBank(new Page<AccountBankRelDto>(pageNum, pageSize), acctName, mobilePhone, loginInfo.getTenantId());
        List<AccountBankRelDto> records = selfAndBusinessBank.getRecords();
        for (AccountBankRelDto record : records) {
            record.setBankTypeName("");
            if (EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0 == record.getBankType()) {
                record.setBankTypeName("私人账户");
            }
            if (EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1 == record.getBankType()) {
                record.setBankTypeName("对公账户");
            }
        }
        Page<AccountBankRelDto> page = new Page<AccountBankRelDto>();
        page.setRecords(records);
        page.setCurrent(selfAndBusinessBank.getCurrent());
        page.setTotal(selfAndBusinessBank.getTotal());
        page.setSize(selfAndBusinessBank.getSize());
        page.setPages(selfAndBusinessBank.getPages());

        return page;
    }

    @Override
    public void updatePayeeBankInfo(String accName, String accNo, Long accUserId, String bankName, String bankBranch, String bankType, String collectAcctId, Long id) {
        OaLoan oaLoan = this.queryOaLoanById(id);
        if (oaLoan == null) {
            throw new BusinessException("亲，不存在该借支信息！");
        }
        if (oaLoan.getSts() != OaLoanConsts.STS.STS0) {
            throw new BusinessException("该借支状态已更新，请刷新后再试");
        }
        if (accUserId != null && accUserId > 0) {
            UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(accUserId);
            if (userDataInfo == null) {
                throw new BusinessException("找不到收款用户信息");
            }
            oaLoan.setBorrowPhone(userDataInfo.getMobilePhone());
            oaLoan.setBorrowName(userDataInfo.getLinkman());
            oaLoan.setBorrowType(userDataInfo.getUserType());
            oaLoan.setBorrowUserInfoId(accUserId);
        } else {
            if (oaLoan.getBorrowUserInfoId() == null || oaLoan.getBorrowUserInfoId() <= 1) {
                oaLoan.setBorrowUserInfoId(oaLoan.getUserInfoId());
            }
        }
        oaLoan.setAccName(accName);
        oaLoan.setAccNo(accNo);
        oaLoan.setBankName(bankName);
        oaLoan.setBankBranch(bankBranch);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(bankType)) {
            oaLoan.setBankType(Integer.parseInt(bankType));
        }
        oaLoan.setCollectAcctId(collectAcctId);
        this.saveOrUpdate(oaLoan);

    }


    @Override
    public Integer checkOaLoanId(String oaLoanId, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        QueryWrapper<OaLoan> wrapper = new QueryWrapper<>();
        wrapper.eq("oa_loan_id", oaLoanId)
                .eq("tenant_id", loginInfo.getTenantId());
        List<OaLoan> oaLoans = baseMapper.selectList(wrapper);
        Integer count = 0;
        if (oaLoans != null && oaLoans.size() > 0) {
            count = 1;
        }
        return count;
    }


    /**
     * 上传借支图片
     *
     * @param oaFilesVo
     */
    @Override
    public void saveOaLoanPic(OaFilesVo oaFilesVo, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        if (oaFilesVo.getId() == null || oaFilesVo.getId() <= 0) {
            throw new BusinessException("借支id号不能为空");
        }
        QueryWrapper<OaLoan> wrapper = new QueryWrapper<>();
        wrapper.eq("id", oaFilesVo.getId());
        OaLoan oaLoan = baseMapper.selectOne(wrapper);
        if (oaLoan == null) {
            throw new BusinessException("找不到借支信息");
        }
        if (StringUtils.isBlank(oaFilesVo.getStrfileId())) {
            throw new BusinessException("图片信息不能为空");
        }
        QueryWrapper<OaFiles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("REL_ID", oaLoan.getId());
        List<OaFiles> oaFiles = oaFilesMapper.selectList(queryWrapper);
        String[] files = oaFilesVo.getStrfileId().split(",");
        List<String> oaListFile = new ArrayList<>();
        if (oaFiles != null && oaFiles.size() > 0) {
            for (int i = 0; i < oaFiles.size(); i++) {
                oaListFile.add(oaFiles.get(i).getFileId() + "");
            }
        }
        for (int i = 0; i < files.length; i++) {
            if (!oaListFile.contains(files[i])) {
                long fileId = Long.parseLong(files[i]);
//                Criteria pic = session.createCriteria(SysAttach.class);
//                pic.add(Restrictions.eq("flowId", fileId));
                QueryWrapper<com.youming.youche.finance.domain.sys.SysAttach> sysAttachQueryWrapper = new QueryWrapper<>();
                sysAttachQueryWrapper.eq("id", fileId);
                List<com.youming.youche.finance.domain.sys.SysAttach> sysAttaches = sysAttachMapper.selectList(sysAttachQueryWrapper);
//                List<SysAttach> attach = pic.list();
                OaFiles of = new OaFiles();
                try {
                    FastDFSHelper client = FastDFSHelper.getInstance();
                    of.setRelType(OaLoanData.RELTYPE2);//关联数据类型:1借支 2借支核销 3报销'
                    of.setRelId(oaLoan.getId());
                    of.setFileId(fileId);
                    of.setOpId(loginInfo.getId());
                    of.setOpDate(DateUtil.localDateTime(new Date()));
                    of.setTenantId(loginInfo.getTenantId());
                    if (sysAttaches.get(0).getFileName().length() > 50) {
                        throw new BusinessException("文件名太长，请控制在50字内");
                    }
                    of.setFileName(sysAttaches.get(0).getFileName());
                    if (sysAttaches.get(0).getFileName().indexOf("jpg") > -1
                            || sysAttaches.get(0).getFileName().indexOf("gif") > -1
                            || sysAttaches.get(0).getFileName().indexOf("png") > -1
                            || sysAttaches.get(0).getFileName().indexOf("bmp") > -1
                    ) {
                        of.setFileUrl(CommonUtil.getBigPicUrl(client.getHttpURL(sysAttaches.get(0).getStorePath())));
                    } else {
                        of.setFileUrl(client.getHttpURL(sysAttaches.get(0).getStorePath()));
                    }
                    iOaFilesService.saveOrUpdate(of);
                } catch (Exception e) {
                    throw new BusinessException("提示" + e);
                }
            }
        }
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.
                SysOperLogConst.BusiCode.TubeBorrow;//车管借支
        if (oaLoan.getClassify() == 1) {
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.StaffBorrow;//员工借支
        } else if (oaLoan.getClassify() == 2 && oaLoan.getLaunch() == 2) {
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;//司机借支
        }
        // 保存操作日志
        sysOperLogService.saveSysOperLog(loginInfo,
                busi_code,
                oaLoan.getId(),
                com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "上传核销图片",
                loginInfo.getTenantId()
        );

    }


    /**
     * 判断该借支类型是否归属司机
     *
     * @param oaLoan
     * @return
     */
    public boolean checkLoanBelongDriver(OaLoan oaLoan) {
        boolean flag = false;
        if (oaLoan.getClassify() != null && oaLoan.getLaunch() != null) {
            if (oaLoan.getClassify() == 2 && oaLoan.getLaunch() == 2) {
                List<SysStaticData> dataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));//司机借支
                for (SysStaticData data : dataList) {
                    if (org.apache.commons.lang3.StringUtils.equals(data.getCodeValue(), oaLoan.getLoanSubject() + "")) {
                        flag = true;
                    }
                }
            }
        }
        if (oaLoan.getClassify() == null && oaLoan.getLaunch() == null) {
            return flag;
        }
        return flag;
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

    @Override
    public List<QueryOaloneDto> queryOaLoanList(Long orderId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        // // 分页封装
        List<OaLoan> list = this.queryOaLoanLists(orderId, OaLoanConsts.LAUNCH.LAUNCH2, accessToken);//司机发起的借支
        List<QueryOaloneDto> listOut = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (OaLoan oaLoan : list) {
                QueryOaloneDto queryOaloneDto = new QueryOaloneDto();
                queryOaloneDto.setLId(oaLoan.getId());
                queryOaloneDto.setAmount(oaLoan.getAmount());
                queryOaloneDto.setLoanSubject(oaLoan.getLoanSubject());
                queryOaloneDto.setLoanSubjectName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, user.getTenantId(), "LOAN_SUBJECT", oaLoan.getLoanSubject() + ""));
                queryOaloneDto.setOaLoanId(oaLoan.getOaLoanId());
                queryOaloneDto.setPayedAmount(oaLoan.getPayedAmount());
                queryOaloneDto.setPlateNumber(oaLoan.getPlateNumber());
                queryOaloneDto.setSts(oaLoan.getSts());
                queryOaloneDto.setIsNeedBill(oaLoan.getIsNeedBill());
                queryOaloneDto.setIsNeedBillName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, user.getTenantId(), "IS_NEED_BILL_OA", oaLoan.getIsNeedBill() + ""));
                queryOaloneDto.setStsName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, user.getTenantId(), "LOAN_STATE", oaLoan.getSts() + ""));
                queryOaloneDto.setToWriteOff(oaLoan.getAmount() - (oaLoan.getPayedAmount() == null ? 0 : oaLoan.getPayedAmount()));
                queryOaloneDto.setUserName(oaLoan.getBorrowName());
                queryOaloneDto.setUserId(oaLoan.getUserId());
                queryOaloneDto.setAppDate(oaLoan.getAppDate());
                queryOaloneDto.setAppReason(oaLoan.getAppReason());
                listOut.add(queryOaloneDto);
            }
        }
        return listOut;
    }


    private List<OaLoan> queryOaLoanLists(Long orderId, int launch, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        QueryWrapper<OaLoan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_info_id", user.getUserInfoId());
        if (orderId != null && orderId > 0) {
            queryWrapper.eq("order_id", orderId);
        }
        if (launch > 0) {
            queryWrapper.eq("launch", launch);
        }
        queryWrapper.orderByAsc("app_date");
        List<OaLoan> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public String saveOrUpdateApp(Long LId, String amountString, String appReason, Integer loanSubject, String plateNumber, Long orderId, Long accUserId, String weight, String isNeedBill, String strfileId, String accessToken) {

        if (org.apache.commons.lang3.StringUtils.isBlank(isNeedBill)) {
            throw new BusinessException("请选择是否需要票据！");
        }
        Long amount = 0l;
        if (org.apache.commons.lang3.StringUtils.isEmpty(amountString)) {
            throw new BusinessException("亲，申请金额不能为空！");
        } else {
            if (CommonUtil.isNumber(amountString)) {
                Double convertNum = Double.parseDouble(amountString);
                amount = Math.round(convertNum);
            } else {
                throw new BusinessException("亲，请填写正确的申请金额！");
            }
        }
        if (loanSubject < 0) {
            throw new BusinessException("亲，申请科目不能为空！");
        }
        if (loanSubject == OaLoanData.LOANSUBJECT4) {
            if (org.apache.commons.lang3.StringUtils.isBlank(weight)) {
                throw new BusinessException("请输入过磅重量！");
            }
            try {
                Float.parseFloat(weight);
            } catch (NumberFormatException e) {
                throw new BusinessException("过磅重量不正确，请重新输入");
            }
        } else {
            weight = null;
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(plateNumber)) {
            throw new BusinessException("亲，请输入车牌号！");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(appReason)) {
            throw new BusinessException("亲，借款原因不能为空！");
        }
        if (orderId <= 0) {
            throw new BusinessException("请选择订单号");
        }
        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(accUserId);
        if (userDataInfo == null) {
            throw new BusinessException("找不到收款用户信息");
        }

        OaLoan oaLoan = new OaLoan();
        oaLoan.setLaunch(OaLoanConsts.LAUNCH.LAUNCH2);
        oaLoan.setClassify(OaLoanConsts.LOAN_CLASSIFY.LOAN_CLASSIFY2);
        oaLoan.setIsNeedBill(Integer.valueOf(isNeedBill));
        oaLoan.setUserId(userDataInfo.getId());
        oaLoan.setUserInfoId(userDataInfo.getId());
        oaLoan.setBorrowUserInfoId(userDataInfo.getId());
        oaLoan.setCarOwnerName(userDataInfo.getLinkman());
        oaLoan.setCarPhone(userDataInfo.getMobilePhone());
        oaLoan.setPlateNumber(plateNumber);
        oaLoan.setId(LId);

        oaLoan.setLoanSubject(loanSubject);
        oaLoan.setOrderId(orderId);
        oaLoan.setAmount(amount);
        oaLoan.setAppReason(appReason);

        //获取司机对私默认收款账户
        AccountBankRel defalutAccountBankRel = iAccountBankRelService.getDefaultAccountBankRel(accUserId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (defalutAccountBankRel != null) {
            oaLoan.setAccName(defalutAccountBankRel.getAcctName());
            oaLoan.setAccNo(defalutAccountBankRel.getAcctNo());
            oaLoan.setBankName(defalutAccountBankRel.getBankName());
            oaLoan.setBankBranch(defalutAccountBankRel.getBranchName());
            oaLoan.setCollectAcctId(defalutAccountBankRel.getPinganCollectAcctId());
            oaLoan.setBankType(defalutAccountBankRel.getBankType());
        }


        oaLoan.setWeight(weight);
        this.saveOrUpdateOaLoanCars(oaLoan, strfileId, accessToken);
        return "Y";
    }

    private void saveOrUpdateOaLoanCars(OaLoan oaLoan, String strfileId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);

        com.youming.youche.commons.constant.SysOperLogConst.OperType operType = com.youming.youche.commons.constant.SysOperLogConst.OperType.Add;
        String operComment = "申请借支";

        if (oaLoan.getIsNeedBill() == null) {
            throw new BusinessException("请选择是否需要票据！");
        }
        if (oaLoan.getAmount() <= 0L) {
            throw new BusinessException("亲，申请金额不能为空！");
        }
        if (oaLoan.getLoanSubject() < 0) {
            throw new BusinessException("亲，申请科目不能为空！");
        }
        if (oaLoan.getLoanSubject() == OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT4) {
            if (org.apache.commons.lang3.StringUtils.isBlank(oaLoan.getWeight())) {
                throw new BusinessException("请输入过磅重量！");
            }
            try {
                Float.parseFloat(oaLoan.getWeight());
            } catch (NumberFormatException e) {
                throw new BusinessException("过磅重量不正确，请重新输入");
            }
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(oaLoan.getPlateNumber())) {
            throw new BusinessException("亲，请输入车牌号！");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(oaLoan.getAppReason())) {
            throw new BusinessException("亲，借款原因不能为空！");
        }
        if (oaLoan.getLaunch() == OaLoanConsts.LAUNCH.LAUNCH1) {//车管
            if (oaLoan.getLoanSubject() == 5) {
                if (oaLoan.getLoanTransReason() <= 0) {
                    throw new BusinessException("请选择事故原因");
                }
            }
            if (oaLoan.getLoanSubject() == OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT10) {
                if (null == oaLoan.getAccidentDate()) {
                    throw new BusinessException("亲，事故时间不能为空！");
                }
                if (null == oaLoan.getInsuranceDate()) {
                    throw new BusinessException("亲，出险时间不能为空！");
                }
                if (oaLoan.getAccidentType() == null || oaLoan.getAccidentType() <= 0) {
                    throw new BusinessException("亲，事故类型不能为空！");
                }
                if (oaLoan.getAccidentReason() == null || oaLoan.getAccidentReason() <= 0) {
                    throw new BusinessException("亲，事故原因不能为空！");
                }
                if (oaLoan.getDutyDivide() == null || oaLoan.getDutyDivide() <= 0) {
                    throw new BusinessException("亲，责任划分不能为空！");
                }
                if (oaLoan.getAccidentDivide() == null || oaLoan.getAccidentDivide() <= 0) {
                    throw new BusinessException("亲，事故司机不能为空！");
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(oaLoan.getInsuranceFirm())) {
                    throw new BusinessException("亲，保险公司不能为空！");
                }
            }
        }
        if (oaLoan.getUserId() == null) {
            throw new BusinessException("找不到收款用户信息");
        }
        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(oaLoan.getUserId());
        if (userDataInfo == null) {
            throw new BusinessException("找不到收款用户信息");
        }
        oaLoan.setBorrowPhone(userDataInfo.getMobilePhone());
        oaLoan.setBorrowName(userDataInfo.getLinkman());
        oaLoan.setBorrowType(userDataInfo.getUserType());
        //如果有选择订单，则借支的归属部门为订单的归属部门；如果借支没有选择订单，则借支的归属部门为操作员的归属部门。
        if (oaLoan.getOrderId() != null && oaLoan.getOrderId() > 0) {
            long tenantId = -1;
            long orgId = -1;
            OrderInfo orderInfo = orderInfoService.getOrder(oaLoan.getOrderId());
            if (orderInfo == null) {
                OrderInfoH orderInfoH = orderInfoHService.getOrderH(oaLoan.getOrderId());
                if (orderInfoH == null) {
                    throw new BusinessException("找不到订单信息");
                }
                tenantId = orderInfoH.getTenantId();
                orgId = orderInfoH.getOrgId();
            } else {
                tenantId = orderInfo.getTenantId();
                orgId = orderInfo.getOrgId();
            }
            oaLoan.setTenantId(tenantId);
            oaLoan.setOrgId(orgId);
        } else {
            oaLoan.setOrgId(user.getOrgId());
        }

        if (oaLoan.getId() < 1) {//新增借支
            oaLoan.setId(0L);
            long incr = redisUtil.incr(EnumConsts.RemoteCache.EXPENSE_NUMBER);
            String oaloanId = CommonUtil.createOaloanId(incr);
            //借支号码是否存在
            while (1 == 1) {
                Integer isExist = this.checkOaLoanId(oaloanId, accessToken);
                if (isExist > 0) {//借支号码存在则重新生成
                    oaloanId = CommonUtil.createOaloanId(incr);
                    continue;
                } else {
                    oaLoan.setOaLoanId("BM" + oaloanId);// 创建借支单号
                    break;
                }
            }

            oaLoan.setSts(OaLoanConsts.STS.STS0);// 待审核
            oaLoan.setAppDate(LocalDateTime.now());// 申请时间
            oaLoan.setUserInfoId(user.getUserInfoId());
            oaLoan.setUserInfoName(user.getName());
            if (oaLoan.getLaunch() == OaLoanConsts.LAUNCH.LAUNCH1) {
                oaLoan.setTenantId(user.getTenantId());
            }
        } else {
            operType = SysOperLogConst.OperType.Update;
            operComment = "修改借支";
            OaLoan oldObj = this.queryOaLoanById(oaLoan.getId());
            oaLoan.setSts(oldObj.getSts());
            /**
             * 2018-12-07 需求确认，只有待审核状态才能修改
             */
            if (!(oldObj.getSts() == OaLoanConsts.STS.STS0 || oldObj.getSts() == OaLoanConsts.STS.STS2)) {
                throw new BusinessException("该借支状态已更新，请刷新后再试");
            }
            if (oldObj.getSts() == OaLoanConsts.STS.STS2) {//如果审核不通过，修改接口需要重启审核流程
                oaLoan.setSts(OaLoanConsts.STS.STS0);
            }
            oaLoan.setLaunch(oldObj.getLaunch());
            oaLoan.setOaLoanId(oldObj.getOaLoanId());// 创建借支单号
            oaLoan.setAppDate(oldObj.getAppDate());// 申请时间
            oaLoan.setUserId(oldObj.getUserId());
            oaLoan.setUserName(oldObj.getUserName());
            oaLoan.setTenantId(oldObj.getTenantId());
            //todo
            //session.evict(oldObj);//把session中同标识的对象移出(session.evict())，使他成为脱管的状态
        }
        oaLoan.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        if (checkLoanBelongDriver(oaLoan)) {
            oaLoan.setReceUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        } else {
            oaLoan.setReceUserType(getCurUserTpye(oaLoan.getUserId(), oaLoan.getCollectAcctId(), oaLoan.getAccNo(), oaLoan.getTenantId(), accessToken));
        }
        this.saveOrUpdate(oaLoan);

        //更新附件
        this.saveFileForLoanExpense(oaLoan.getId(), oaLoan.getTenantId(), oaLoan.getUserId(), strfileId, OaLoanData.RELTYPE1);

        // 保存操作日志
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeBorrow;
        String auditCode = AuditConsts.AUDIT_CODE.TUBE_BORROW;
        if (checkLoanBelongDriver(oaLoan)) {
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;
            auditCode = AuditConsts.AUDIT_CODE.DRIVER_BORROW;
            operComment = "司机" + operComment;
        } else {
            operComment = "车管" + operComment;
        }
        sysOperLogService.saveSysOperLog(user, busi_code, oaLoan.getId(), operType, operComment, user.getTenantId());
        //启动审核流程
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("oaLoanId", oaLoan.getOaLoanId());
        params.put("LId", oaLoan.getId());
        params.put("tenantId", oaLoan.getTenantId());
        Boolean bool = auditService.startProcess(auditCode, oaLoan.getId(), busi_code, params, accessToken, user.getTenantId());
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }
    }

    private Integer getCurUserTpye(Long userId, String pinganCollectAcctId, String accNo, Long tenantId, String accessToken) {

        int userType = 0;
        List<UserType> userTypes = null;

        if (org.apache.commons.lang3.StringUtils.isNoneBlank(accNo) || org.apache.commons.lang3.StringUtils.isNoneBlank(pinganCollectAcctId)) {
            List<DirverxDto> mapList = iAccountBankRelService.getAcctRelUserTypeList(accNo, pinganCollectAcctId, -1L);
            if (mapList != null && mapList.size() > 0) {
                List<UserType> userTypeList = sysUserService.getUserType(userId, tenantId);
                Set<Integer> userTypeSet = new HashSet();
                for (UserType userType1 : userTypeList) {
                    userTypeSet.add(userType1.getType());
                }
                userTypes = new ArrayList<>();
                for (DirverxDto dirverxDto : mapList) {
                    if (userTypeSet.contains(dirverxDto.getUserType().intValue())) {
                        UserType ut = new UserType();
                        ut.setType(dirverxDto.getUserType().intValue());
                        userTypes.add(ut);
                    }

                }
            } else {
                userTypes = sysUserService.getUserType(userId, tenantId);
                //throw new BusinessException("根据卡号：" + accNo + " 平安虚拟账户：" + pinganCollectAcctId + "未找到用户：" + userId + "引用该卡的用户类型");
            }
        } else {
            userTypes = sysUserService.getUserType(userId, tenantId);
        }
        if (userTypes != null && userTypes.size() > 0) {
            List<String> rolePriorityList = new ArrayList();//用户角色优先级定义：司机>员工>车队>服务商>收款人 =》 3 1 6 2 7
            rolePriorityList.add("3");
            rolePriorityList.add("1");
            rolePriorityList.add("6");
            rolePriorityList.add("2");
            rolePriorityList.add("7");
            for (String role : rolePriorityList) {
                for (UserType ut : userTypes) {
                    if (org.apache.commons.lang3.StringUtils.equals(role, ut.getType() + "")) {
                        userType = ut.getType();
                        break;
                    }
                }
                if (userType > 0) {
                    break;
                }
            }
        }

        if (userType < 1) {
            userReceiverInfoService.createTenantReceiverInfo(userId, tenantId, accessToken);
            userType = 7;
        }
        return userType;
    }

    @Override
    public String cancelOaLoans(Long id, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        OaLoan oaLoan = this.queryOaLoanById(id);
        if (oaLoan != null) {
            if (oaLoan.getSts() == OaLoanConsts.STS.STS0 || oaLoan.getSts() == OaLoanConsts.STS.STS2) {
                oaLoan.setSts(8);
            } else {
                throw new BusinessException("该借支状态已更新，请刷新后再试");
            }
        } else {
            throw new BusinessException("查询不到借支信息");
        }
        this.saveOrUpdate(oaLoan);
        // 保存操作日志 老代码没有用到
//        ISysOperLogSV logSV = (ISysOperLogSV) SysContexts.getBean("sysOperLogSV");
//        List<SysStaticData> dataList = SysStaticDataUtil.getSysStaticDataList("LOAN_SUBJECT_APP");//司机借支
        com.youming.youche.commons.constant.SysOperLogConst.BusiCode busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.TubeBorrow;//车管借支
        String auditCode = AuditConsts.AUDIT_CODE.TUBE_BORROW;
        if (checkLoanBelongDriver(oaLoan)) {
            busi_code = com.youming.youche.commons.constant.SysOperLogConst.BusiCode.DriverBorrow;
            auditCode = AuditConsts.AUDIT_CODE.DRIVER_BORROW;
        }
        sysOperLogService.saveSysOperLog(loginInfo, busi_code, oaLoan.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Del, "借支取消");
        //结束审核流程
        try {
            auditOutService.cancelProcess(auditCode, oaLoan.getId(), loginInfo.getTenantId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "成功";
    }

    @Override
    public OaLoanOutDto queryOaLoanByIds(Long id, String busiCode, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        List<Long> fileId = new ArrayList<Long>();// 附件id
        List<String> fileUrl = new ArrayList<String>();// 借支信息文件
        List<String> fileName = new ArrayList<String>();
        List<Long> fileIdV = new ArrayList<Long>();// 附件id
        List<String> fileUrlV = new ArrayList<String>();// 核销文件url
        List<String> fileNameV = new ArrayList<String>();// 核销文件名字
        OaLoan oaLoan = this.queryOaLoanById(id, busiCode);
        oaLoan.setVerifyUserName(user.getName());//当前用户名称
        id = oaLoan.getId();
        List<OaLoadVerification> oaLoadVerifications = oaLoadVerificationService.getOaLoadVerificationsByLId(id);
        List<OaFiles> oaFiles = this.queryOaFilesById(id);
        OaLoanOutDto oaLoanOut = new OaLoanOutDto();
        BeanUtil.copyProperties(oaLoan, oaLoanOut);
        if (oaFiles != null) {
            for (OaFiles of : oaFiles) {
                if (of.getRelType().equals(OaLoanData.RELTYPE1)) {
                    fileId.add(of.getFileId());
                    fileUrl.add(of.getFileUrl());
                    fileName.add(of.getFileName());
                } else if (of.getRelType().equals(OaLoanData.RELTYPE2)) {
                    fileIdV.add(of.getFileId());
                    fileUrlV.add(of.getFileUrl());
                    fileNameV.add(of.getFileName());
                }
            }
        }
        if (oaLoadVerifications != null && oaLoadVerifications.size() > 0) {
            OaLoadVerification oaLoadVerification = oaLoadVerifications.get(0);
            if (oaLoadVerification.getCashAmount() != null) {
                oaLoanOut.setCashAmount(oaLoadVerification.getCashAmount());
            } else {
                oaLoanOut.setCashAmount(0l);
            }
            if (oaLoadVerification.getBillAmount() != null) {
                oaLoanOut.setBillAmount(oaLoadVerification.getBillAmount());
            } else {
                oaLoanOut.setBillAmount(0l);
            }

            oaLoanOut.setAllCashAmount(0.0f);
            oaLoanOut.setAllBillAmount(0.0f);
            for (OaLoadVerification verification : oaLoadVerifications) {
                if (verification.getCashAmount() != null) {
                    oaLoanOut.setAllCashAmount(verification.getCashAmount() + oaLoanOut.getAllCashAmount());
                }
                if (verification.getBillAmount() != null) {
                    oaLoanOut.setAllBillAmount(verification.getBillAmount() + oaLoanOut.getAllBillAmount());
                }
            }

        }
        oaLoanOut.setFileUrl(fileUrl);
        oaLoanOut.setFileName(fileName);
        oaLoanOut.setFileUrlV(fileUrlV);
        oaLoanOut.setFileNameV(fileNameV);
        oaLoanOut.setFileId(fileId);
        oaLoanOut.setFileIdV(fileIdV);

        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(oaLoan.getUserInfoId());
        oaLoanOut.setMobilePhone(userDataInfo.getMobilePhone());
        oaLoanOut.setAccUserId(oaLoan.getBorrowUserInfoId());
        oaLoanOut.setAccName(oaLoan.getAccName());
        oaLoanOut.setAccNo(oaLoan.getAccNo());
        oaLoanOut.setBankName(oaLoan.getBankName());
        oaLoanOut.setBankBranch(oaLoan.getBankBranch());

        // TODO 2022-6-28 修改
        oaLoanOut.setId(oaLoan.getId());// 主键
        oaLoanOut.setAccUserId(oaLoan.getBorrowUserInfoId());
        oaLoanOut.setAccName(oaLoan.getAccName());
        oaLoanOut.setAccNo(oaLoan.getAccNo());
        oaLoanOut.setBankName(oaLoan.getBankName());
        oaLoanOut.setBankBranch(oaLoan.getBankBranch());
        oaLoanOut.setPlateNumber(oaLoan.getPlateNumber()); // 车牌号
        oaLoanOut.setOaLoanId(oaLoan.getOaLoanId()); // 借支号
        oaLoanOut.setRemark(oaLoan.getRemark());// 借支说明
        oaLoanOut.setCarOwnerName(oaLoan.getCarOwnerName());// 司机姓名
        oaLoanOut.setCarPhone(oaLoan.getCarPhone()); // 司机手机号
        oaLoanOut.setAmount(oaLoan.getAmount());
        oaLoanOut.setAmountDouble(oaLoanOut.getAmount() / 100.00);//借支金额
        oaLoanOut.setRemark(oaLoan.getRemark());// 借支说明
        oaLoanOut.setLoanSubject(oaLoan.getLoanSubject());// 借支 类型
        oaLoanOut.setIsNeedBill(oaLoan.getIsNeedBill());// 借支票据
        oaLoanOut.setOrderId(oaLoan.getOrderId());// 订单号
        oaLoanOut.setLaunch(oaLoan.getLaunch());
        oaLoanOut.setAppReason(oaLoan.getAppReason()); // 借款说明
        oaLoanOut.setBorrowName(oaLoan.getBorrowName());
        oaLoanOut.setBorrowPhone(oaLoan.getBorrowPhone());
        oaLoanOut.setAccidentDate(oaLoan.getAccidentDate());// 事故时间
        oaLoanOut.setInsuranceDate(oaLoan.getInsuranceDate());// 出险时间
        oaLoanOut.setInsuranceFirm(oaLoan.getInsuranceFirm());//保险公司
        oaLoanOut.setInsuranceMoney(oaLoan.getInsuranceMoney());//理赔金额
        oaLoanOut.setReportNumber(oaLoan.getReportNumber());// 报案号
        oaLoanOut.setWeight(oaLoan.getWeight());//过磅费重量（千克）
        oaLoanOut.setUserInfoId(oaLoan.getUserInfoId());
        oaLoanOut.setUserInfoName(oaLoan.getUserInfoName());
        oaLoanOut.setLaunch(oaLoan.getLaunch());
        oaLoanOut.setClassify(oaLoan.getClassify());
        oaLoanOut.setAccidentType(oaLoan.getAccidentType());
        oaLoanOut.setPayedAmount(oaLoan.getPayedAmount());
        oaLoanOut.setLoanTransReason(oaLoan.getLoanTransReason());  //违章罚款原因1、因公 2、因私
        oaLoanOut.setVerifyRemark(oaLoan.getVerifyRemark()); // 核销备注
        oaLoanOut.setSts(oaLoan.getSts());// 状态

        oaLoanOut.setClassifyName(oaLoanOut.getLaunch() != null && oaLoanOut.getLaunch() == 2 ? "司机借支" : "车管借支");//借支入口
        // 责任划分
        oaLoanOut.setDutyDivideName(oaLoanOut.getDutyDivide() == null ? "" : getSysStaticData(
                "LOAN_DUTY_DIVIDE", String.valueOf(oaLoanOut.getDutyDivide())).getCodeName());
        oaLoanOut.setIsNeedBillName(oaLoanOut.getIsNeedBill() == null ? "" : getSysStaticData(
                "IS_NEED_BILL_OA", String.valueOf(oaLoanOut.getIsNeedBill())).getCodeName());
        if (oaLoanOut.getClassify() == 1) {
            oaLoanOut.setLaunchName("员工");//申请人属性
        } else if (oaLoanOut.getClassify() == 2) {
            oaLoanOut.setLaunchName(oaLoanOut.getLaunch() != null && oaLoanOut.getLaunch() == 2 ? "司机" : "车管");
        }
        oaLoanOut.setLoanSubjectName(oaLoanOut.getLoanSubject() == null ? "" : getSysStaticData("LOAN_SUBJECT",
                String.valueOf(oaLoanOut.getLoanSubject())).getCodeName());//借支类型
        // 状态
        oaLoanOut.setStateName(oaLoanOut.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                String.valueOf(oaLoanOut.getState())).getCodeName());
        // 状态
        oaLoanOut.setStsName(oaLoanOut.getSts() == null ? "" : getSysStaticData("LOAN_STATE", String.valueOf(
                oaLoanOut.getSts())).getCodeName());
        oaLoanOut.setUserInfoName(oaLoan.getUserInfoName());
        //操作日志
        SysOperLogConst.BusiCode busi_code = SysOperLogConst.BusiCode.TubeBorrow;//车管借支
        String auditCode = AuditConsts.AUDIT_CODE.TUBE_BORROW;
        if (checkLoanBelongDriver(oaLoan)) {
            auditCode = AuditConsts.AUDIT_CODE.DRIVER_BORROW;
            busi_code = SysOperLogConst.BusiCode.DriverBorrow;
        }

        sysOperLogService.querySysOperLogAll(busi_code.getCode(), id, false, oaLoanOut.getTenantId(), auditCode, id);
        //oaLoanOut.setSysOperLogs(sysOperLogs);
        return oaLoanOut;
    }

    private List<OaFiles> queryOaFilesById(Long id) {
        LambdaQueryWrapper<OaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OaFiles::getRelId, id);
        List<OaFiles> oaFiles = oaFilesMapper.selectList(queryWrapper);
        return oaFiles;
    }

    @Override
    public void notice(Integer nodeIndex, Long busiId, Integer targetObjectType, String targetObjId, String accessToken) {
        if (targetObjectType == 2) {
            Arrays.asList(targetObjId.split(",")).forEach(userId -> {
                try {
                    UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(Long.parseLong(userId));
                    OaLoan out = queryOaLoanById(busiId);
                    Map paraMap = new HashMap();
                    paraMap.put("auditMan", userDataInfo.getLinkman());
                    paraMap.put("applyMan", out.getUserName());
                    paraMap.put("amount", CommonUtil.getDoubleFormatLongMoney(out.getAmount(), 2));
                    paraMap.put("busiId", out.getOaLoanId());
                    paraMap.put("busiName", "借支");
                    String url = "../ac/finance/loanManage_vehicle.html";
                    if (checkLoanBelongDriver(out)) {
                        url = "../ac/finance/loanManage_driver.html";
                    }
                    //推送到Web
                    sysSmsSendService.sendSms(userDataInfo.getMobilePhone(), EnumConsts.SmsTemplate.AUDIT_TIP, SysStaticDataEnum.WEB_SMS_TYPE.OPEN_TAB, 8, url, paraMap, accessToken);
                    //微信小程序推送
                    List<SysStaticData> staticData = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("WX_AUDIT_TEMP"));
                    if (staticData != null && staticData.size() > 0) {

                        Map map = new HashMap();
                        map.put("wxTemplateId", staticData.get(0).getCodeValue());
                        if (checkLoanBelongDriver(out)) {
                            map.put("page", "packages/loan/list/list?queryType=3&waitDeal=true");
                        } else {
                            map.put("page", "packages/loan/list/list?queryType=2&waitDeal=true");
                        }
                        ItemObj itemObj = new ItemObj();
                        itemObj.setValue("审核提醒");
                        map.put("first", itemObj);

                        ItemObj itemObj1 = new ItemObj();
                        itemObj1.setValue(out.getUserName());
                        map.put("keyword1", itemObj1);

                        ItemObj itemObj2 = new ItemObj();
                        itemObj2.setValue("借支");
                        map.put("keyword2", itemObj2);

                        ItemObj itemObj3 = new ItemObj();
                        itemObj3.setValue(CommonUtil.getDoubleFormatLongMoney(out.getAmount(), 2) + "");
                        map.put("keyword3", itemObj3);

                        ItemObj itemObj4 = new ItemObj();
                        itemObj4.setValue(out.getOaLoanId());
                        map.put("keyword4", itemObj4);

                        ItemObj itemObj5 = new ItemObj();
                        itemObj5.setValue("待您审核");
                        map.put("keyword5", itemObj5);

                        ItemObj itemObjR = new ItemObj();
                        itemObjR.setValue("待您审核");
                        map.put("remark", itemObjR);

                        sysSmsSendService.sendWxSms(userDataInfo.getMobilePhone(), EnumConsts.SmsTemplate.AUDIT_TIP, map, accessToken);
                    }
                } catch (Exception e) {
                    log.error("【推送借支审核提醒消息失败】" + e.getMessage());
                }
            });
        }

    }

    class ItemObj {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}
