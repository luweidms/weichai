package com.youming.youche.finance.provider.service.munual;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IAccountStatementService;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.api.IRechargeInfoRecordService;
import com.youming.youche.finance.api.ac.IAcBusiOrderLimitRelService;
import com.youming.youche.finance.api.ac.IApplyOpenBillService;
import com.youming.youche.finance.api.base.IBeidouPaymentRecordService;
import com.youming.youche.finance.api.etc.IEtcBillInfoService;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.api.order.IOrderBillInfoService;
import com.youming.youche.finance.api.order.IOrderFeeStatementHService;
import com.youming.youche.finance.api.payable.IPayByCashService;
import com.youming.youche.finance.api.payable.IPlatformPayService;
import com.youming.youche.finance.api.tenant.ITenantServiceRelDetailsService;
import com.youming.youche.finance.api.tyre.ITyreSettlementBillService;
import com.youming.youche.finance.api.voucher.IVoucherInfoService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.domain.ac.AcBusiOrderLimitRel;
import com.youming.youche.finance.domain.munual.MunualPaymentInfo;
import com.youming.youche.finance.domain.munual.MunualPaymentSumInfo;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.domain.order.OrderBillCheckInfo;
import com.youming.youche.finance.domain.tyre.TyreSettlementBill;
import com.youming.youche.finance.dto.DueDateDetailsCDDto;
import com.youming.youche.finance.dto.OverdueReceivableDto;
import com.youming.youche.finance.dto.PayableDayReportFinanceDto;
import com.youming.youche.finance.dto.PayableMonthReportFinanceDto;
import com.youming.youche.finance.dto.PayoutInfoOutDto;
import com.youming.youche.finance.dto.order.PayoutInfoDto;
import com.youming.youche.finance.provider.mapper.munual.PayoutIntfExpansionMapper;
import com.youming.youche.finance.provider.mapper.munual.PayoutIntfMapper;
import com.youming.youche.finance.provider.utils.PayOutIntfUtil;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.munual.QueryPayoutIntfsVo;
import com.youming.youche.finance.vo.order.QueryDouOverdueVo;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.IPayManagerService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IAdditionalFeeService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsFlowService;
import com.youming.youche.order.api.order.IOilRechargeAccountFlowService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.api.order.IRateService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.api.service.IServiceInfoService;
import com.youming.youche.order.domain.AgentServiceInfo;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.AdditionalFee;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;
import com.youming.youche.order.domain.order.PayoutOrder;
import com.youming.youche.order.dto.AgentServiceDto;
import com.youming.youche.order.dto.PinganBankInfoOutDto;
import com.youming.youche.order.dto.UserDataInfoDto;
import com.youming.youche.order.dto.order.PayReturnDto;
import com.youming.youche.order.dto.order.PayoutIntfDto;
import com.youming.youche.record.api.service.IServiceRepairOrderService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.api.mycenter.IBankAccountTranService;
import com.youming.youche.system.api.tenant.IBillSettingService;
import com.youming.youche.system.api.tenant.IServiceChargeReminderService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.domain.tenant.BillSetting;
import com.youming.youche.system.domain.tenant.ServiceChargeReminder;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.system.vo.mycenter.AccountTransferVo;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE;
import static com.youming.youche.conts.EnumConsts.PayInter.BANK_PAYMENT_OUT;
import static com.youming.youche.conts.EnumConsts.PayInter.BEFORE_PAY_CODE;
import static com.youming.youche.conts.EnumConsts.PayInter.CANCEL_THE_ORDER;
import static com.youming.youche.conts.EnumConsts.PayInter.CAR_DRIVER_SALARY;
import static com.youming.youche.conts.EnumConsts.PayInter.CONSUME_ETC_CODE;
import static com.youming.youche.conts.EnumConsts.PayInter.DRIVER_EXPENSE_ABLE;
import static com.youming.youche.conts.EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE;
import static com.youming.youche.conts.EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH;
import static com.youming.youche.conts.EnumConsts.PayInter.PAYFOR_CODE;
import static com.youming.youche.conts.EnumConsts.PayInter.PAY_FOR_OIL_CODE;
import static com.youming.youche.conts.EnumConsts.PayInter.PAY_FOR_REPAIR;
import static com.youming.youche.conts.EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD;
import static com.youming.youche.conts.EnumConsts.PayInter.TUBE_EXPENSE_ABLE;
import static com.youming.youche.conts.EnumConsts.PayInter.UPDATE_THE_ORDER;
import static com.youming.youche.conts.EnumConsts.SubjectIds.BALANCE_CONSUME_OIL_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.BALANCE_PAY_REPAIR;
import static com.youming.youche.conts.EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_ETC_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.EXCEPTION_OUT_PAYABLE_OVERDUE_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1800;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1802;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1816;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS2302;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS50070;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS50072;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW;
import static com.youming.youche.conts.OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
import static com.youming.youche.conts.OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
import static com.youming.youche.conts.OrderAccountConst.PAY_TYPE.HAVIR;
import static com.youming.youche.conts.OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1;
import static com.youming.youche.finance.commons.util.DateUtil.DATETIME12_FORMAT2;
import static com.youming.youche.finance.commons.util.HttpsMain.netTimeOutFail;
import static com.youming.youche.finance.commons.util.HttpsMain.respCodeZero;
import static com.youming.youche.finance.constant.OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0;
import static com.youming.youche.finance.constant.OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_1;
import static com.youming.youche.finance.constant.OrderConsts.PayOutVerificationState.VERIFICATION_STATE;
import static com.youming.youche.finance.provider.utils.PinganIntefaceConst.RESP_BANK_CODE.BALANCE_NOT_ENOUGH;
import static com.youming.youche.finance.provider.utils.PinganIntefaceConst.RESP_BANK_CODE.BANK_OTHER_ERROR;
import static com.youming.youche.finance.provider.utils.PinganIntefaceConst.RESP_BANK_CODE.LOCAL_BUSI_ERROR;
import static com.youming.youche.record.common.EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE;
import static com.youming.youche.record.common.EnumConsts.SubjectIds.SUBJECTIDS1131;

/**
 * @author zengwen
 * @date 2022/4/7 12:51
 */
@DubboService(version = "1.0.0")
public class PayoutIntfThreeServiceImpl extends BaseServiceImpl<PayoutIntfMapper, PayoutIntf> implements IPayoutIntfThreeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayoutIntfThreeServiceImpl.class);

    /**
     * 提现成功(打款成功)
     */
    public static final String respCodeSuc = "3";

    @Resource
    RedisUtil redisUtil;

    @Resource
    PayOutIntfUtil payOutIntfUtil;

    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;

    @DubboReference(version = "1.0.0")
    IBillPlatformService billPlatformService;

    @DubboReference(version = "1.0.0")
    IBillSettingService billSettingService;

    @DubboReference(version = "1.0.0")
    IRateService rateService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService auditSettingService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @DubboReference(version = "1.0.0")
    IBaseBusiToOrderService iBaseBusiToOrderService;

    @DubboReference(version = "1.0.0")
    IOrderFeeStatementService iOrderFeeStatementService;

    @DubboReference(version = "1.0.0")
    IAdditionalFeeService iAdditionalFeeService;

    @DubboReference(version = "1.0.0")
    IPayoutOrderService iPayoutOrderService;

    @DubboReference(version = "1.0.0")
    IOrderLimitService iOrderLimitService;

    @DubboReference(version = "1.0.0")
    IOrderFundFlowService iOrderFundFlowService;

    @Resource
    IOaLoanThreeService iOaLoanService;

    @DubboReference(version = "1.0.0")
    IOilCardManagementService iOilCardManagementService;

    @Resource
    IVoucherInfoService iVoucherInfoService;

    @Resource
    ITyreSettlementBillService iTyreSettlementBillService;

    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;

    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;

    @DubboReference(version = "1.0.0")
    IAccountDetailsService iAccountDetailsService;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService iAccountBankRelService;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @DubboReference(version = "1.0.0")
    IServiceChargeReminderService iServiceChargeReminderService;

    @DubboReference(version = "1.0.0")
    IPayManagerService iPayManagerService;

    @Resource
    IEtcBillInfoService iEtcBillInfoService;

    @Resource
    IApplyOpenBillService iApplyOpenBillService;

    @Resource
    IAccountStatementService iAccountStatementService;

    @Resource
    ITenantServiceRelDetailsService iTenantServiceRelDetailsService;

    @Resource
    IBeidouPaymentRecordService iBeidouPaymentRecordService;

    @Resource
    IRechargeInfoRecordService iRechargeInfoRecordService;

    @DubboReference(version = "1.0.0")
    IOilRechargeAccountDetailsFlowService iOilRechargeAccountDetailsFlowService;

    @DubboReference(version = "1.0.0")
    IOilRechargeAccountFlowService iOilRechargeAccountFlowService;

    @DubboReference(version = "1.0.0")
    IServiceRepairOrderService iServiceRepairOrderService;

    @Resource
    IAcBusiOrderLimitRelService acBusiOrderLimitRelService;

    @Resource
    IPayByCashService payByCashService;

    @DubboReference(version = "1.0.0")
    IBankAccountTranService bankAccountTranService;

    @DubboReference(version = "1.0.0")
    IOrderAccountService iOrderAccountService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfService iPayoutIntfService;

    @DubboReference(version = "1.0.0")
    IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;

    @DubboReference(version = "1.0.0",check = false)
    IServiceInfoService serviceInfoService;

    @DubboReference(version = "1.0.0")
    IOrderFeeService orderFeeService;

    @Resource
    PayoutIntfExpansionMapper payoutIntfExpansionMapper;

    @Resource
    IPlatformPayService iPlatformPayService;

    @Resource
    IOrderBillInfoService iOrderBillInfoService;

    @Lazy
    @Resource
    IOrderFeeStatementHService orderFeeStatementHService;

    @DubboReference(version = "1.0.0")
    IBankAccountService bankAccountService;

    @DubboReference(version = "1.0.0")
    IOrderGoodsService orderGoodsService;


    @Override
    public IPage<MunualPaymentInfo> getPayOutIntfs(String accessToken, QueryPayoutIntfsVo queryPayoutIntfsVo) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);

        // 处理参数
        queryPayoutIntfsVo.setPayTenantId(loginInfo.getTenantId());
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        queryPayoutIntfsVo.setPayObjId(sysTenantDef.getAdminUser());

        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getStartTime())) {
            queryPayoutIntfsVo.setStartTime(queryPayoutIntfsVo.getStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getEndTime())) {
            queryPayoutIntfsVo.setEndTime(queryPayoutIntfsVo.getEndTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getDependTimeStart())) {
            queryPayoutIntfsVo.setDependTimeStart(queryPayoutIntfsVo.getDependTimeStart() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getDependTimeEnd())) {
            queryPayoutIntfsVo.setDependTimeEnd(queryPayoutIntfsVo.getDependTimeEnd() + " 23:59:59");
        }

        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getVerificationStartTime())) {
            queryPayoutIntfsVo.setVerificationStartTime(queryPayoutIntfsVo.getVerificationStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getVerificationEndTime())) {
            queryPayoutIntfsVo.setVerificationEndTime(queryPayoutIntfsVo.getVerificationEndTime() + " 23:59:59");
        }

        // 查询现金打款数据
        List<MunualPaymentInfo> payOutIntfsList = baseMapper.getPayOutIntfsList(queryPayoutIntfsVo);
        Integer payOutIntfsCount = baseMapper.getPayOutIntfsCount(queryPayoutIntfsVo);
        List<Long> flowIds = new ArrayList<>();
        for (MunualPaymentInfo munualPaymentInfo : payOutIntfsList) {
            flowIds.add(munualPaymentInfo.getFlowId());
        }

        Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
        Map<Long, Map<String, Object>> out = new HashMap<>();

        if (flowIds != null && flowIds.size() > 0) {
            // 判断权限等数据
            hasPermissionMap = auditOutService.isHasPermission(accessToken, AuditConsts.AUDIT_CODE.PAY_CASH_CODE, flowIds);
            out = auditOutService.queryAuditRealTimeOperation(loginInfo.getUserInfoId(), AuditConsts.AUDIT_CODE.PAY_CASH_CODE, flowIds, loginInfo.getTenantId());
        }

        for (MunualPaymentInfo munualPaymentInfo : payOutIntfsList) {
            String respCode = munualPaymentInfo.getRespCode();

            if (munualPaymentInfo.getTxnAmt() == null || munualPaymentInfo.getTxnAmt() <= 0) {
                munualPaymentInfo.setAccName(null);
                munualPaymentInfo.setAccNo(null);
            }

            boolean isTrue = false;
            if (hasPermissionMap.get(munualPaymentInfo.getFlowId()) != null && hasPermissionMap.get(munualPaymentInfo.getFlowId())) {
                isTrue = true;
            }
            if (out.get(munualPaymentInfo.getFlowId()) != null) {
                Map<String, Object> p = out.get(munualPaymentInfo.getFlowId());
                Boolean b = (Boolean) p.get("isFinallyNode");
                if (b) {
                    munualPaymentInfo.setIsFinallyNode(true);
                } else {
                    munualPaymentInfo.setIsFinallyNode(false);
                }
            }

            //判断是否有审核权限
            if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) && munualPaymentInfo.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0) && isTrue) {
                munualPaymentInfo.setHasPermission(true);
            }
            if (munualPaymentInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.RECEIVER_USER) || munualPaymentInfo.getSubjectsId() == EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB || munualPaymentInfo.getSubjectsId() == EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB) {
                munualPaymentInfo.setVirtualState(SysStaticDataEnum.VIRTUAL_TENANT_STATE.IS_VIRTUAL);
            }
            if (munualPaymentInfo.getBusiId() == EnumConsts.PayInter.PAY_FOR_REPAIR && munualPaymentInfo.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                munualPaymentInfo.setBusiIdName("支付轮胎租赁费");
            }


            if (munualPaymentInfo.getVehicleAffiliation() != null && munualPaymentInfo.getVehicleAffiliation() > 10
                    && munualPaymentInfo.getSubjectsId() != EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB
                    && munualPaymentInfo.getSubjectsId() != EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB) {
                // 获取服务费
                Map feeMap = this.doQueryServiceFee(accessToken, munualPaymentInfo.getTxnAmt(), munualPaymentInfo.getFlowId());
                String serviceFee = DataFormat.getStringKey(feeMap, "ServiceFee");
                munualPaymentInfo.setBillServiceFeeDouble(CommonUtil.getDoubleByObject(serviceFee));
            }


            if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) && munualPaymentInfo.getIsAutoMatic().equals(AUTOMATIC0)) {
                munualPaymentInfo.setPayConfirmName("未打款");
            } else if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && munualPaymentInfo.getIsAutoMatic().equals(AUTOMATIC0)) {
                munualPaymentInfo.setPayConfirmName("确认中");
            } else if (munualPaymentInfo.getIsAutoMatic().equals(AUTOMATIC1) && !Objects.equals(respCode, HttpsMain.respCodeCollection)) {
                munualPaymentInfo.setPayConfirmName("打款中");
            } else if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)) {
                munualPaymentInfo.setPayConfirmName("已打款");
            } else if (munualPaymentInfo.getIsAutoMatic().equals(AUTOMATIC1) && Objects.equals(respCode, HttpsMain.respCodeCollection)) {
                //munualPaymentInfo.setPayConfirmName("未提现");
                munualPaymentInfo.setPayConfirmName("已打款");
            }
            if (Objects.equals(munualPaymentInfo.getPayConfirm(), EnumConsts.PAY_CONFIRM.withdraw) && !Objects.equals(munualPaymentInfo.getIsNeedBill(), OrderConsts.IS_NEED_BILL.TERRACE_BILL)) {
//            if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) && munualPaymentInfo.getIsNeedBill() != OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                munualPaymentInfo.setUpdateDate(null);
                munualPaymentInfo.setBankTypeName(null);
                munualPaymentInfo.setIsAutomaticName(null);
                munualPaymentInfo.setAccName(null);
                munualPaymentInfo.setAccNo(null);
            }

            if (munualPaymentInfo.getBusiId() != null) {
                munualPaymentInfo.setBusiIdName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "BUSINESS_NUMBER_TYPE", String.valueOf(munualPaymentInfo.getBusiId())));
                if (munualPaymentInfo.getSubjectsId() != null && munualPaymentInfo.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                    munualPaymentInfo.setBusiIdName("支付轮胎租赁费");
                }
            }

            if (munualPaymentInfo.getBankType() != null && munualPaymentInfo.getPayConfirm() != 0) {
                munualPaymentInfo.setBankTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "BANK_TYPE_STATE", munualPaymentInfo.getBankType() + "").getCodeName());
            }

            if (munualPaymentInfo.getIsAutoMatic() != null && munualPaymentInfo.getPayConfirm() != 0) {
                munualPaymentInfo.setIsAutomaticName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "IS_AUTOMATIC", munualPaymentInfo.getIsAutoMatic() + "").getCodeName());
            }

            if (munualPaymentInfo.getIsNeedBill() != null) {
                munualPaymentInfo.setIsNeedBillName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "FUNDS_IS_NEED_BILL", munualPaymentInfo.getIsNeedBill() + "").getCodeName());
            }

            if (munualPaymentInfo.getSourceRegion() != null) {
                munualPaymentInfo.setSourceRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil, "SYS_CITY", munualPaymentInfo.getSourceRegion() + "").getName());
            }

            if (munualPaymentInfo.getDesRegion() != null) {
                munualPaymentInfo.setDesRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil, "SYS_CITY", munualPaymentInfo.getDesRegion() + "").getName());
            }

            if (respCode != null) {
                munualPaymentInfo.setRespCodeName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "RESP_CODE_TYPE", respCode + ""));
            }

            if (munualPaymentInfo.getBankType() != null && munualPaymentInfo.getPayConfirm() != 0) {
                munualPaymentInfo.setBankTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "BANK_TYPE_STATE", munualPaymentInfo.getBankType() + "").getCodeName());
            }

            if (munualPaymentInfo.getIsAutoMatic() != null && munualPaymentInfo.getPayConfirm() != 0) {
                munualPaymentInfo.setIsAutomaticName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "IS_AUTOMATIC", munualPaymentInfo.getIsAutoMatic() + "").getCodeName());
            }
        }

        IPage<MunualPaymentInfo> page = new Page<>();
        page.setCurrent(queryPayoutIntfsVo.getPageNum() == null ? 1 : queryPayoutIntfsVo.getPageNum() + 1);
        page.setRecords(payOutIntfsList);
        page.setSize(queryPayoutIntfsVo.getPageNum() == null ? 1 : queryPayoutIntfsVo.getPageSize());
        page.setTotal(payOutIntfsCount);
        if (queryPayoutIntfsVo.getPageSize() == null || queryPayoutIntfsVo.getPageNum() == null) {
            page.setPages(1);
        } else {
            page.setPages(payOutIntfsCount % queryPayoutIntfsVo.getPageSize() == 0 ? payOutIntfsCount / queryPayoutIntfsVo.getPageSize() : (payOutIntfsCount / queryPayoutIntfsVo.getPageSize() + 1));
        }
        return page;
    }

    @Override
    public Map doQueryServiceFee(String accessToken, Long cash, Long flowId) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);

        Long tenantId = loginInfo.getTenantId();
        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请传入业务单号");
        }

        PayoutIntf payout = baseMapper.selectById(flowId);
        Long openUserId = Long.valueOf(payout.getVehicleAffiliation());

        //如果费用科目是服务费，则不计算服务费
        if (payout.getSubjectsId() == EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB || payout.getSubjectsId() == EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB) {
            return new HashMap();
        }
        if (cash <= 0) {
            cash = payout.getTxnAmt();
        }
        if (openUserId == null || openUserId <= 10) {
            return new HashMap();
        }
        if (cash == null || cash < 0) {
            throw new BusinessException("现金金额不合法");
        }

        // 查询车队开票设置信息
        BillSetting billSetting = billSettingService.getBillSetting(tenantId);
        if (billSetting == null) {
            throw new BusinessException("根据租户ID: " + tenantId + " 未找到车队开票信息");
        }
        if (billSetting.getRateId() == null || billSetting.getRateId() <= 0) {
            throw new BusinessException("根据租户ID: " + tenantId + " 未找到车队开票费率设置");
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        // 根据用户编号查询票据档案信息,启用或停用的都查出来
        BillPlatform bpf = billPlatformService.queryAllBillPlatformByUserId(openUserId);

        if (bpf == null) {
            throw new BusinessException("根据开票平台用户id：" + openUserId + " 未找到开票平台信息");
        }
        if (bpf.getServiceFeeFormula() == null) {
            throw new BusinessException("根据开票平台用户id： " + openUserId + " 未找到开票服务计算公式");
        }

        // 服务费计算公司  ( d + o + e ) * ( 1 - r ) - o * 8.5% - e * 1.5% - ( d + o +e)
        String tempFormula = bpf.getServiceFeeFormula();
        String formula = tempFormula.replace("（", "(").replace("）", ")").replace("%", "/100.0");
        // 票据成本  [t/(1+10%)*10% - o/(1+16%)*16% - e/(1+3%)*3% - s/(1+16%)*16%]*(1-45%)+ m+a
        String tempHaCostFormula = bpf.getHaCost();
        String haCostFormula = tempHaCostFormula.replace("（", "(").replace("）", ")").replace("%", "/100.0");

        // 开票率
        Double billRate = rateService.getRateValue(billSetting.getRateId(), cash);

        if (billRate == null) {
            throw new BusinessException("根据车队费率id： " + billSetting.getRateId() + " 开票金额" + (double) cash / 100 + " 未找到开票费率");
        }
        long oil = 0;
        long etc = 0;
        engine.put("d", cash);
        engine.put("o", oil);
        engine.put("e", etc);
        engine.put("r", billRate / 100);
        Double serviceFee = null;
        Double serviceCost = null;
        try {

            // 得到服务费
            serviceFee = (Double) engine.eval(formula);

            // 得到票据成本
            serviceCost = (Double) engine.eval(haCostFormula);
        } catch (ScriptException ex) {
            throw new BusinessException("ScriptEngine转换异常");
        }
        long billServiceFee = Math.round(serviceFee);//serviceFee.longValue();//截取
        long billServiceCost = serviceCost.longValue();//截取
        long lugeBillServiceFee = Math.round(serviceFee);//四舍五入
        long lugeBillServiceCost = Math.round(serviceCost);//四舍五入
        //56k服务费算法直接截取，路歌服务费四舍五入，比如123.678分，56K: 123分，路歌：124分
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ServiceFee", CommonUtil.divide(billServiceFee));
        result.put("56kServiceFee", billServiceFee);
        result.put("ServiceCost", CommonUtil.divide(billServiceCost));
        result.put("billServiceFee", CommonUtil.divide(billServiceFee));
        result.put("billServiceCost", CommonUtil.divide(billServiceCost));
        result.put("lugeBillServiceFee", CommonUtil.divide(lugeBillServiceFee));
        result.put("lugeBillServiceCost", CommonUtil.divide(lugeBillServiceCost));
        result.put("billRate", billRate);
        return result;
    }

    @Override
    public Map queryPayoutIntfsSum(String accessToken, QueryPayoutIntfsVo queryPayoutIntfsVo) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);

        if (queryPayoutIntfsVo.getPayTenantId() == null) {
            queryPayoutIntfsVo.setPayTenantId(loginInfo.getTenantId());
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(queryPayoutIntfsVo.getPayTenantId());
        queryPayoutIntfsVo.setPayObjId(sysTenantDef.getAdminUser());

        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getStartTime())) {
            queryPayoutIntfsVo.setStartTime(queryPayoutIntfsVo.getStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getEndTime())) {
            queryPayoutIntfsVo.setEndTime(queryPayoutIntfsVo.getEndTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getDependTimeStart())) {
            queryPayoutIntfsVo.setDependTimeStart(queryPayoutIntfsVo.getDependTimeStart() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getDependTimeEnd())) {
            queryPayoutIntfsVo.setDependTimeEnd(queryPayoutIntfsVo.getDependTimeEnd() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getVerificationStartTime())) {
            queryPayoutIntfsVo.setVerificationStartTime(queryPayoutIntfsVo.getVerificationStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayoutIntfsVo.getVerificationEndTime())) {
            queryPayoutIntfsVo.setVerificationEndTime(queryPayoutIntfsVo.getVerificationEndTime() + " 23:59:59");
        }


        List<MunualPaymentSumInfo> payOutIntfsSum = baseMapper.getPayOutIntfsSum(queryPayoutIntfsVo);
        Map maps = new HashMap<>();
        for (MunualPaymentSumInfo munualPaymentSumInfo : payOutIntfsSum) {
            Long sumMoney = munualPaymentSumInfo.getSummoney() == null ? 0L : munualPaymentSumInfo.getSummoney();
            Long sumBillServiceMoney = munualPaymentSumInfo.getSumBillServiceFee() == null ? 0L : munualPaymentSumInfo.getSumBillServiceFee();
            Long sumAppendFreight = munualPaymentSumInfo.getSumAppendFreight() == null ? 0L : munualPaymentSumInfo.getSumAppendFreight();
            sumMoney = sumMoney + sumBillServiceMoney + sumAppendFreight;
            // 待核销状态
            if (OrderConsts.PayOutVerificationState.INIT == Integer.valueOf(munualPaymentSumInfo.getVerificationState())) {
                maps.put("noVerificatMoney", sumMoney.longValue()); // 未核销总金额
            }

            // 已核销状态
            if (VERIFICATION_STATE == Integer.valueOf(munualPaymentSumInfo.getVerificationState()) || OrderConsts.PayOutVerificationState.VERIFICATION_STATE_5 == Integer.valueOf(munualPaymentSumInfo.getVerificationState())) {
                if (maps.get("verificatMoney") != null) {
                    sumMoney = sumMoney + (Long) maps.get("verificatMoney");
                }
                maps.put("verificatMoney", sumMoney.longValue()); // 核销总金额
            }
        }
        return maps;
    }

    @Async
    @Override
    public void downloadExcelFile(String accessToken, QueryPayoutIntfsVo queryPayoutIntfsVo, ImportOrExportRecords importOrExportRecords) {
        IPage<MunualPaymentInfo> page = this.getPayOutIntfs(accessToken, queryPayoutIntfsVo);

        List<MunualPaymentInfo> paymentInfoList = page.getRecords();
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "流水", "应付时间", "业务方",
                    "车牌号码", "业务类型", "业务单号",
                    "要求靠台时间", "客户名称", "线路名称",
                    "代收名称", "订单备注", "票据类型",
                    "收票主体", "业务金额", "票据服务费",
                    "附加运费", "支付总金额", "打款状态",
                    "核销时间", "付款方式", "收款账户名",
                    "收款帐号", "备注"};
            resourceFild = new String[]{
                    "getFlowId", "getCreateDate", "getUserName",
                    "getPlateNumber", "getBusiIdName", "getBusiCode",
                    "getDependTime", "getCustomName", "getSourceName",
                    "getCollectionUserName", "getOrderRemark", "getIsNeedBillName",
                    "getBillLookUp", "getTxnAmtDouble", "getBillServiceFeeDouble",
                    "getAppendFreightDouble", "getSumTxnFeeDouble", "getPayConfirmName",
                    "getUpdateDate", "getIsAutomaticName", "getAccName",
                    "getAccNo", "getRemark"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(paymentInfoList, showName, resourceFild, MunualPaymentInfo.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "现金打款.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            importOrExportRecords.setMediaUrl(path);
            importOrExportRecords.setState(2);
            importOrExportRecordsService.update(importOrExportRecords);
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public IPage<PayoutInfoDto> doQueryDouOverdue(Integer pageNumber, Integer pageSize, QueryDouOverdueVo queryDouOverdueVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
//        if (StringUtils.isNotEmpty(queryDouOverdueVo.getPayName())) {
//            queryDouOverdueVo.setPayName("'" + queryDouOverdueVo.getPayName() + "'");
//        }
//        if(StringUtils.isNotEmpty(queryDouOverdueVo.getPayConFirm())){
//            if(!queryDouOverdueVo.getPayConFirm().equals("1")){
//                queryDouOverdueVo.setPayConFirm("0,3");
//            }
//        }
        LOGGER.info("查看参数1："+queryDouOverdueVo.getPayConFirm());
        LOGGER.info("查看参数2："+queryDouOverdueVo.getType());
        if(StringUtils.isNotEmpty(queryDouOverdueVo.getPayConFirm())){
            queryDouOverdueVo.setPayConFirms(Arrays.asList(queryDouOverdueVo.getPayConFirm().split(",")));
        }
        if(StringUtils.isNotEmpty(queryDouOverdueVo.getType())){
            queryDouOverdueVo.setTypes(Arrays.asList(queryDouOverdueVo.getType().split(",")));
        }
        List<OverdueReceivableDto> lists = baseMapper.doQueryDouOverdues(queryDouOverdueVo, tenantId);

        List<OverdueReceivableDto> infoOuts = new ArrayList<>();

        for (OverdueReceivableDto dto : lists) {
            // 处理订单应收日期
            if (dto.getType() == 1) {
                if (dto.getBalanceType() == 1) { // 预付全款
                    if (StringUtils.isBlank(dto.getCreateTime())) {
                        continue;
                    }
                    dto.setReceivableDate(dto.getCreateTime());
                } else if (dto.getBalanceType() == 2) { // 预付 尾款账期
                    if (StringUtils.isNotBlank(dto.getUpdateTime())) {
                        dto.setReceivableDate(getAccountPeriodDateStr(dto.getUpdateTime(), dto.getCollectionTime()));
                    } else {
                        continue;
                    }

                } else if (dto.getBalanceType() == 3) { // 预付 尾款月结
                    if (StringUtils.isNotBlank(dto.getCarDependDate())) {
                        dto.setReceivableDate(getMonthlyDateStr(dto.getCarDependDate(), dto.getCollectionMonth(), dto.getCollectionDay()));
                    } else {
                        continue;
                    }

                }
            } else {
                // 处理借支应收日期
                if (StringUtils.isNotBlank(dto.getUpdateTime())) {
                    //打款第二天算逾期
                    dto.setReceivableDate(dto.getUpdateTime());
                } else {
                    continue;
                }
//                if(queryDouOverdueVo.getUserId() != null && queryDouOverdueVo.getUserId() > 0){
//                    //司机小程序应付
//                    dto.setTxnAmt(dto.getTxnAmt() - dto.getPaid());
//                    dto.getTxnAmt();
//                }
            }
            // 判断是否逾期，是否继续执行操作
            if (!equalTwoTimeStr(dto.getReceivableDate())) {
                continue;
            }
            if (dto.getType().equals(1)) {
                dto.setStateName("订单收入");
            } else if (dto.getType().equals(2)) {
                dto.setStateName("司机借支");
            } else if (dto.getType().equals(3)) {
                dto.setStateName("员工借支");
            } else {
                dto.setStateName("订单收入");
            }
            if (dto.getPayConfirm().equals(0)) {
                dto.setPayConfirmName("未退款");
            } else if (dto.getPayConfirm().equals(1)) {
                dto.setPayConfirmName("已线下退款");
            } else if (dto.getPayConfirm().equals(3)) {
                dto.setPayConfirmName("车队确认中");
            } else {
                dto.setPayConfirmName("未退款");
            }
            if (dto.getSourceRegion() != null) {
                dto.setSourceRegionName(getSysStaticData("SYS_CITY", dto.getSourceRegion() + "").getCodeName());
            }
            if (dto.getDesRegion() != null) {
                dto.setDesRegionName(getSysStaticData("SYS_CITY", dto.getDesRegion() + "").getCodeName());
            }
            infoOuts.add(dto);
        }

        return listToPage(infoOuts, pageNumber, pageSize);
    }

    @Override
    public void confirmPayment(Long flowId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        PayoutIntf payoutIntf = this.getById(flowId);
//        if (payoutIntf.getIsAutomatic() != null && OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1 == payoutIntf.getIsAutomatic()) {
//            throw new BusinessException("不允许确认线上付款数据!");
//        }
//        if (payoutIntf.getVehicleAffiliation() != null && payoutIntf.getVehicleAffiliation().equals(OrderConsts.PayOutVerificationState.VERIFICATION_STATE)) {
//            throw new BusinessException("此条记录已核销不允许继续确认收款!");
//        }
//        if (payoutIntf.getSubjectsId() != null && payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS50044) {
//            throw new BusinessException("不允许确认司机借支!");
//        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String datestr = sdf.format(new Date());
//        com.youming.youche.order.domain.order.PayoutIntf payoutIntfs = new com.youming.youche.order.domain.order.PayoutIntf();
//        BeanUtil.copyProperties(payoutIntf, payoutIntfs);
//        iPayoutIntfService.doPay200(payoutIntfs, null, null, false, loginInfo, accessToken);
//        payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.transfer);
//        payoutIntf.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
//        payoutIntf.setVerificationDate(new Date());
//        payoutIntf.setPayTime(new Date());
//        payoutIntf.setRespCode("3");
//        payoutIntf.setRespMsg("确认收款成功");
//        payoutIntf.setRemark("确认收款成功");
//        payoutIntf.setCompleteTime(datestr);
//        this.saveOrUpdate(payoutIntf);
//        //业务回调
//        this.callBack(payoutIntf, accessToken);
//        if (payoutIntf.getPayTenantId() > 0) {
//            Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), payoutIntf.getPayTenantId());
//            if (b) {
//                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), null, AuditConsts.RESULT.SUCCESS, null, AuditConsts.OperType.TASK, payoutIntf.getPayTenantId(), accessToken);
//            }
//        }
        //核销
        try {
            QueryDouOverdueVo queryDouOverdueVo = new QueryDouOverdueVo();
            queryDouOverdueVo.setFlowId(flowId);
            //OverdueReceivable overdueReceivable = new OverdueReceivable();
            List<OverdueReceivableDto> list = baseMapper.doQueryDouOverdues(queryDouOverdueVo, loginInfo.getTenantId());
            if (list == null || list.size() <= 0) {
                throw new BusinessException("流水号数据不存在，请联系管理员清理脏数据");
            }
            if (list.get(0).getType() == 1) {
                //订单收入
                if (list.get(0).getOrderId() != null) {
                    confirmPaymentWriteOffOrderId(String.valueOf(list.get(0).getOrderId()), String.valueOf(list.get(0).getTxnAmt() - list.get(0).getPaid()), accessToken);
                }

            } else {
                //借支
                String businessNumber = list.get(0).getBusinessNumber();
                if (StringUtils.isEmpty(businessNumber)) {
                    throw new BusinessException("流水号数据不存在，请联系管理员清理脏数据");
                }
                OaLoan oaLoan = iOaLoanService.selectByNumber(businessNumber,list.get(0).getUserId());
                Long billAmount = list.get(0).getTxnAmt() - list.get(0).getPaid();
                String billAmountStr = "";
                if (billAmount != null) {
                    billAmountStr = CommonUtil.divide(billAmount);
                }
                if (oaLoan != null) {
                    iOaLoanService.verificationOaLoanCar(String.valueOf(oaLoan.getId()), "0", billAmountStr, null, null, "确认收款-已核销", 0, accessToken);
                }

            }
            String msg = "[" + loginInfo.getName() + "]" + "确认收款";
            saveSysOperLog(SysOperLogConst.BusiCode.DueOverdue, SysOperLogConst.OperType.Audit, msg, accessToken, Long.valueOf(flowId));
        } catch (BusinessException e) {
            e.printStackTrace();
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public void confirmPaymentNew(Long flowId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        PayoutIntf payoutIntf = this.getById(flowId);
        if (payoutIntf.getIsAutomatic() != null && OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1 == payoutIntf.getIsAutomatic()) {
            throw new BusinessException("不允许确认线上付款数据!");
        }
        if (payoutIntf.getVehicleAffiliation() != null && payoutIntf.getVehicleAffiliation().equals(OrderConsts.PayOutVerificationState.VERIFICATION_STATE)) {
            throw new BusinessException("此条记录已核销不允许继续确认收款!");
        }
        if (payoutIntf.getSubjectsId() != null && payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS50044) {
            throw new BusinessException("不允许确认司机借支!");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datestr = sdf.format(new Date());
        com.youming.youche.order.domain.order.PayoutIntf payoutIntfs = new com.youming.youche.order.domain.order.PayoutIntf();
        BeanUtil.copyProperties(payoutIntf, payoutIntfs);
        iPayoutIntfService.doPay200(payoutIntfs, null, null, false, loginInfo, accessToken);
        payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.transfer);
        payoutIntf.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
        payoutIntf.setVerificationDate(new Date());
        payoutIntf.setPayTime(new Date());
        payoutIntf.setRespCode("3");
        payoutIntf.setRespMsg("确认收款成功");
        payoutIntf.setRemark("确认收款成功");
        payoutIntf.setCompleteTime(datestr);
        this.saveOrUpdate(payoutIntf);
        //业务回调
        this.callBack(payoutIntf, accessToken);
        if (payoutIntf.getPayTenantId() > 0) {
            Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), payoutIntf.getPayTenantId());
            if (b) {
                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), null, AuditConsts.RESULT.SUCCESS, null, AuditConsts.OperType.TASK, payoutIntf.getPayTenantId(), accessToken);
            }
        }
    }

    /**
     * 确认收款后新增功能：应收逾期确认收款后核销逾期金额到订单
     * 1、订单无账单：
     * 先创建账单，然后核销逾期金额
     * 2、订单有账单：
     * 核销金额
     */
    private void confirmPaymentWriteOffOrderId(String orderId, String fee, String accessToken) {
        // 判断是否有账单
        String billNumber = orderFeeStatementHService.judgeDoesItExistOrderId(orderId);
        if (StringUtils.isEmpty(billNumber)) { // 无账单
            // 创建账单
            billNumber = iOrderBillInfoService.createBill(orderId, accessToken);
        }
        // 创建核销记录 类型为其它核销 核销金额为当前逾期记录的交易金额
        List<OrderBillCheckInfo> orderBillCheckInfos = new ArrayList<OrderBillCheckInfo>();
        OrderBillCheckInfo c = new OrderBillCheckInfo();
        c.setCheckType(5);

        c.setId(null);
        if ("null".equals(c.getFileIds()) || "\"null\"".equals(c.getFileIds())) {
            c.setFileIds("");
            c.setFileUrls("");
        }
        if ("null".equals(c.getCheckDesc()) || "\"null\"".equals(c.getCheckDesc())) {
            c.setCheckDesc("");
        }

        if (org.apache.commons.lang.StringUtils.isEmpty(fee)) {
            fee = "0";
        }
        long checkFee = Long.valueOf(fee);
        c.setCheckFee(checkFee);

        c.setBillNumber(billNumber);
        orderBillCheckInfos.add(c);

        // 保存核销
        iOrderBillInfoService.saveChecks(billNumber, orderBillCheckInfos, accessToken);

    }

    @Override
    public boolean doPay200(PayoutIntf payoutIntf, List<AcBusiOrderLimitRel> busiOrderLimitRels, Boolean isOnline, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
/*
        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payoutIntfFor200" + payoutIntf.getFlowId(), 3, 5);
*/
//        boolean isLock = true;
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        if (PAY_FOR_OIL_CODE == payoutIntf.getBusiId()
                || (PAY_FOR_REPAIR == payoutIntf.getBusiId()
                && EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 != payoutIntf.getSubjectsId()
                && EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 != payoutIntf.getSubjectsId())
                || SUBJECTIDS50070 == payoutIntf.getSubjectsId()
                || BALANCE_CONSUME_OIL_SUB == payoutIntf.getSubjectsId()
                || BALANCE_PAY_REPAIR == payoutIntf.getSubjectsId()) {//不需要操作业务
            return true;
        }
        if (BANK_PAYMENT_OUT == payoutIntf.getBusiId()) {
            String sysParame56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K + "").getCodeName();
            BillPlatform billPlatform = billPlatformService.queryAllBillPlatformByUserId(payoutIntf.getPayObjId());
            String sysPre = null == billPlatform ? null : billPlatform.getSysPre();
            if (!(sysParame56K.equals(sysPre) && EnumConsts.SubjectIds.SUBJECTIDS1900 == payoutIntf.getSubjectsId())) {
                return true;
            }
        }

        if (null == payoutIntf.getTxnAmt()) {
            throw new BusinessException("交易金额不能为空");
        }
        //获取付款用户信息
        UserDataInfo payUserDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getPayObjId());
        if (null == payUserDataInfo) {
            throw new BusinessException("付款用户信息不存在,FLOW_ID : " + payoutIntf.getId());
        }
        //获取收款用户信息
        UserDataInfo receivablesUserDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        if (null == receivablesUserDataInfo) {
            throw new BusinessException("收款用户信息不存在,FLOW_ID : " + payoutIntf.getId());
        }

        UserDataInfoDto[] userDataInfoArr = new UserDataInfoDto[2];
        UserDataInfoDto payUserDataInfoDto = new UserDataInfoDto();
        UserDataInfoDto receivablesUserDataInfoDto = new UserDataInfoDto();

        BeanUtil.copyProperties(payUserDataInfo, payUserDataInfoDto);
        BeanUtil.copyProperties(receivablesUserDataInfo, receivablesUserDataInfoDto);

        userDataInfoArr[0] = payUserDataInfoDto;
        userDataInfoArr[1] = receivablesUserDataInfoDto;

        PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
        BeanUtil.copyProperties(payoutIntf, payoutIntfDto);

        //1.支付预付款
        if (BEFORE_PAY_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (payoutIntf.getSubjectsId() == BEFORE_RECEIVABLE_OVERDUE_SUB) {
                iOrderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(), payoutIntf.getTxnAmt(), accessToken);
                //20191015 56K附加运费
                if (payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight() > 0) {
                    this.dealAdditionalFee(payoutIntf);
                }
            }
        }
        //2.尾款(现金，油，维修保养)到期
        else if (PAYFOR_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (payoutIntf.getSubjectsId() == FINAL_PAYFOR_RECEIVABLE_IN) {
                iOrderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(), payoutIntf.getTxnAmt(), accessToken);
            }
        }
        //2.预支尾款
        else if (ADVANCE_PAY_MARGIN_CODE == payoutIntf.getBusiId()) {
            //20190815修改 预支尾款操作限定表
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (null == payoutIntf.getOrderId() || payoutIntf.getOrderId() <= 0) {
                this.doOrderLimtByFlowId(payoutIntf);
            }
        }
        //3.油转现金  4.ETC转现金
        else if (OIL_AND_ETC_TURN_CASH == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //5.司机借支
        else if (EnumConsts.PayInter.OA_LOAN_AVAILABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
            iBaseBusiToOrderService.dealBusiOaLoanAvailable(payoutIntfDto, userDataInfoArr, accessToken);
        }
        //6.司机报销
        else if (DRIVER_EXPENSE_ABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //7.车管借支
        else if (OA_LOAN_AVAILABLE_TUBE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
            iOaLoanService.setPayFlowIdAfterPay(payoutIntf.getId());
        }
        //8.车管报销
        else if (TUBE_EXPENSE_ABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //9.工资发放
        else if (CAR_DRIVER_SALARY == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //10.异常补偿
        else if (EnumConsts.PayInter.EXCEPTION_FEE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //11.异常扣减
        else if (EnumConsts.PayInter.EXCEPTION_FEE_OUT == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, accessToken);
        }
        //12.撤单
        else if (CANCEL_THE_ORDER == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, accessToken);
        }

        //13.ETC消费欠款
        else if (CONSUME_ETC_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, accessToken);
        }

        //14.油卡抵押(释放)
        else if (PLEDGE_RELEASE_OILCARD == payoutIntf.getBusiId()) {
            if (OIL_CARD_RELEASE_RECEIVABLE_IN == payoutIntf.getSubjectsId()) {
                iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else {
                //调用订单中心的油卡释放接口
                iOilCardManagementService.releaseOilCardByOrderId(payoutIntf.getOrderId(), payoutIntf.getTenantId(), accessToken);
                //调用资金模块的油卡释放接口
                iOilCardManagementService.pledgeOrReleaseOilCardAmount(payoutIntf.getPayObjId(), payoutIntf.getVehicleAffiliation(), payoutIntf.getTxnAmt(), payoutIntf.getOrderId(), payoutIntf.getTenantId(), 1, loginInfo, accessToken);
            }
        } else if (UPDATE_THE_ORDER == payoutIntf.getBusiId()) {
            if (payoutIntf.getIsDriver() == HAVIR) {
                iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else {
                iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, accessToken);
            }

        }
        //票據服务费
        else if (BILL_56K_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId() || BILL_SERVICE_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //油品公司油卡充值
        else if (RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId()) {
            isOnline = iVoucherInfoService.judgeRechargeIsNeedWithdrawal(payoutIntf.getBusiCode(), accessToken);
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //支付到付款
        else if (EnumConsts.PayInter.PAY_ARRIVE_CHARGE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //支付ETC账单
        else if (SUBJECTIDS2302 == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //招商车挂靠车对账单
        else if (EnumConsts.PayInter.ACCOUNT_STATEMENT == payoutIntf.getBusiId()) {
            //未到期结算金额
            if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE == payoutIntf.getSubjectsId()) {
                iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == payoutIntf.getSubjectsId()) {//车辆费用
                iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, false, accessToken);
            }
        }
        //北斗缴费
        else if (EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, false, accessToken);
        }
        //车队充值油费（非运输专票）时，资金从车队平安账户付款油品公司的平安虚拟账户，系统自动将资金从油品公司的平安账户的提现到实体银行卡账户
        else if (EnumConsts.SubjectIds.SUBJECTIDS1131 == payoutIntf.getSubjectsId()) {//非运输专票
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //代收服务商维修保养 20190820
        else if (EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, true, accessToken);
        }
        //代收服务商轮胎租赁 20191125
        else if (EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 == payoutIntf.getSubjectsId()) {
            TyreSettlementBill tyreSettlementBill = iTyreSettlementBillService.getTyreSettlementBillVOByBusiCode(payoutIntf.getBusiCode());
            if (isOnline) {
                iTyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBill.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "支付完成", accessToken);
            } else {
                iTyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBill.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "确认收款", accessToken);
            }
            tyreSettlementBill.setPayClass(isOnline ? IS_TURN_AUTOMATIC_1 : IS_TURN_AUTOMATIC_0);
            iTyreSettlementBillService.saveOrUpdate(tyreSettlementBill);
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        } else if (EnumConsts.SubjectIds.SUBJECTIDS1900 == payoutIntf.getSubjectsId()) {//如果是56K打款的，自动帮收款方提现
            String sysParame56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K + "").getCodeName();
            String sysPre = billPlatformService.getPrefixByUserId(payoutIntf.getPayObjId());
            if (sysParame56K.equals(sysPre)) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);
            }
        }
        //与订单无关的业务操作，只操作账户表和账户明细表
        else {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, EnumConsts.PAY_OUT_OPER.ORDER, accessToken);
            if (SUBJECTIDS1816 == payoutIntf.getSubjectsId() && isOnline) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
            }
        }

        if (payoutIntf.getIsTurnAutomatic() != null && payoutIntf.getIsTurnAutomatic() == OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_3) {//自动发起提现
            //判断这条流水是否已经生成了300的提现记录，如果没有，发起提现
            PayoutIntf payoutIntf300 = this.getPayOutIntfByXid(payoutIntf.getId());
            if (payoutIntf300 == null) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);
            }
        }
        return true;
    }

    @Override
    public boolean doPay200Two(PayoutIntf payoutIntf, List<AcBusiOrderLimitRel> busiOrderLimitRels, Boolean isOnline, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
/*
        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payoutIntfFor200" + payoutIntf.getFlowId(), 3, 5);
*/
//        boolean isLock = true;
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        if (PAY_FOR_OIL_CODE == payoutIntf.getBusiId()
                || (PAY_FOR_REPAIR == payoutIntf.getBusiId()
                && EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 != payoutIntf.getSubjectsId()
                && EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 != payoutIntf.getSubjectsId())
                || SUBJECTIDS50070 == payoutIntf.getSubjectsId()
                || BALANCE_CONSUME_OIL_SUB == payoutIntf.getSubjectsId()
                || BALANCE_PAY_REPAIR == payoutIntf.getSubjectsId()) {//不需要操作业务
            return true;
        }
        if (BANK_PAYMENT_OUT == payoutIntf.getBusiId()) {
            String sysParame56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K + "").getCodeName();
            BillPlatform billPlatform = billPlatformService.queryAllBillPlatformByUserId(payoutIntf.getPayObjId());
            String sysPre = null == billPlatform ? null : billPlatform.getSysPre();
            if (!(sysParame56K.equals(sysPre) && EnumConsts.SubjectIds.SUBJECTIDS1900 == payoutIntf.getSubjectsId())) {
                return true;
            }
        }

        if (null == payoutIntf.getTxnAmt()) {
            throw new BusinessException("交易金额不能为空");
        }
        //获取付款用户信息
        UserDataInfo payUserDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getPayObjId());
        if (null == payUserDataInfo) {
            throw new BusinessException("付款用户信息不存在,FLOW_ID : " + payoutIntf.getId());
        }
        //获取收款用户信息
        UserDataInfo receivablesUserDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        if (null == receivablesUserDataInfo) {
            throw new BusinessException("收款用户信息不存在,FLOW_ID : " + payoutIntf.getId());
        }

        UserDataInfoDto[] userDataInfoArr = new UserDataInfoDto[2];
        UserDataInfoDto payUserDataInfoDto = new UserDataInfoDto();
        UserDataInfoDto receivablesUserDataInfoDto = new UserDataInfoDto();

        BeanUtil.copyProperties(payUserDataInfo, payUserDataInfoDto);
        BeanUtil.copyProperties(receivablesUserDataInfo, receivablesUserDataInfoDto);

        userDataInfoArr[0] = payUserDataInfoDto;
        userDataInfoArr[1] = receivablesUserDataInfoDto;

        PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
        BeanUtil.copyProperties(payoutIntf, payoutIntfDto);

        //1.支付预付款
        if (BEFORE_PAY_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (payoutIntf.getSubjectsId() == BEFORE_RECEIVABLE_OVERDUE_SUB) {
                iOrderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(), payoutIntf.getTxnAmt(), accessToken);
                //20191015 56K附加运费
                if (payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight() > 0) {
                    this.dealAdditionalFee(payoutIntf);
                }
            }
        }
        //2.尾款(现金，油，维修保养)到期
        else if (PAYFOR_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (payoutIntf.getSubjectsId() == FINAL_PAYFOR_RECEIVABLE_IN) {
                iOrderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(), payoutIntf.getTxnAmt(), accessToken);
            }
        }
        //2.预支尾款
        else if (ADVANCE_PAY_MARGIN_CODE == payoutIntf.getBusiId()) {
            //20190815修改 预支尾款操作限定表
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (null == payoutIntf.getOrderId() || payoutIntf.getOrderId() <= 0) {
                this.doOrderLimtByFlowId(payoutIntf);
            }
        }
        //3.油转现金  4.ETC转现金
        else if (OIL_AND_ETC_TURN_CASH == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //5.司机借支
        else if (EnumConsts.PayInter.OA_LOAN_AVAILABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
            iBaseBusiToOrderService.dealBusiOaLoanAvailable(payoutIntfDto, userDataInfoArr, accessToken);
        }
        //6.司机报销
        else if (DRIVER_EXPENSE_ABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //7.车管借支
        else if (OA_LOAN_AVAILABLE_TUBE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
            iOaLoanService.setPayFlowIdAfterPay(payoutIntf.getId());
        }
        //8.车管报销
        else if (TUBE_EXPENSE_ABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //9.工资发放
        else if (CAR_DRIVER_SALARY == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //10.异常补偿
        else if (EnumConsts.PayInter.EXCEPTION_FEE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //11.异常扣减
        else if (EnumConsts.PayInter.EXCEPTION_FEE_OUT == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, accessToken);
        }
        //12.撤单
        else if (CANCEL_THE_ORDER == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, accessToken);
        }

        //13.ETC消费欠款
        else if (CONSUME_ETC_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, accessToken);
        }

        //14.油卡抵押(释放)
        else if (PLEDGE_RELEASE_OILCARD == payoutIntf.getBusiId()) {
            if (OIL_CARD_RELEASE_RECEIVABLE_IN == payoutIntf.getSubjectsId()) {
                iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else {
                //调用订单中心的油卡释放接口
                iOilCardManagementService.releaseOilCardByOrderId(payoutIntf.getOrderId(), payoutIntf.getTenantId(), accessToken);
                //调用资金模块的油卡释放接口
                iOilCardManagementService.pledgeOrReleaseOilCardAmount(payoutIntf.getPayObjId(), payoutIntf.getVehicleAffiliation(), payoutIntf.getTxnAmt(), payoutIntf.getOrderId(), payoutIntf.getTenantId(), 1, loginInfo, accessToken);
            }
        } else if (UPDATE_THE_ORDER == payoutIntf.getBusiId()) {
            if (payoutIntf.getIsDriver() == HAVIR) {
                iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else {
                iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, accessToken);
            }

        }
        //票據服务费
        else if (BILL_56K_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId() || BILL_SERVICE_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //油品公司油卡充值
        else if (RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId()) {
            isOnline = iVoucherInfoService.judgeRechargeIsNeedWithdrawal(payoutIntf.getBusiCode(), accessToken);
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //支付到付款
        else if (EnumConsts.PayInter.PAY_ARRIVE_CHARGE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //支付ETC账单
        else if (SUBJECTIDS2302 == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //招商车挂靠车对账单
        else if (EnumConsts.PayInter.ACCOUNT_STATEMENT == payoutIntf.getBusiId()) {
            //未到期结算金额
            if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE == payoutIntf.getSubjectsId()) {
                iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == payoutIntf.getSubjectsId()) {//车辆费用
                iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, false, accessToken);
            }
        }
        //北斗缴费
        else if (EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, false, accessToken);
        }
        //车队充值油费（非运输专票）时，资金从车队平安账户付款油品公司的平安虚拟账户，系统自动将资金从油品公司的平安账户的提现到实体银行卡账户
        else if (EnumConsts.SubjectIds.SUBJECTIDS1131 == payoutIntf.getSubjectsId()) {//非运输专票
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //代收服务商维修保养 20190820
        else if (EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, true, accessToken);
        }
        //代收服务商轮胎租赁 20191125
        else if (EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 == payoutIntf.getSubjectsId()) {
            TyreSettlementBill tyreSettlementBill = iTyreSettlementBillService.getTyreSettlementBillVOByBusiCode(payoutIntf.getBusiCode());
            if (isOnline) {
                iTyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBill.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "支付完成", accessToken);
            } else {
                iTyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBill.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "确认收款", accessToken);
            }
            tyreSettlementBill.setPayClass(isOnline ? IS_TURN_AUTOMATIC_1 : IS_TURN_AUTOMATIC_0);
            iTyreSettlementBillService.saveOrUpdate(tyreSettlementBill);
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        } else if (EnumConsts.SubjectIds.SUBJECTIDS1900 == payoutIntf.getSubjectsId()) {//如果是56K打款的，自动帮收款方提现
            String sysParame56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K + "").getCodeName();
            String sysPre = billPlatformService.getPrefixByUserId(payoutIntf.getPayObjId());
            if (sysParame56K.equals(sysPre)) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);
            }
        }
        //与订单无关的业务操作，只操作账户表和账户明细表
        else {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, EnumConsts.PAY_OUT_OPER.ORDER, accessToken);
            if (SUBJECTIDS1816 == payoutIntf.getSubjectsId() && isOnline) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
            }
        }

        if (payoutIntf.getIsTurnAutomatic() != null && payoutIntf.getIsTurnAutomatic() == OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_3) {//自动发起提现
            //判断这条流水是否已经生成了300的提现记录，如果没有，发起提现
            PayoutIntf payoutIntf300 = this.getPayOutIntfByXid(payoutIntf.getId());
            if (payoutIntf300 == null) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);
            }
        }
        return true;
    }

    @Override
    public void dealAdditionalFee(PayoutIntf pay) {
        if (pay == null) {
            throw new BusinessException("入参有误");
        }
        if (pay.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB) {
            if (pay.getAppendFreight() != null && pay.getAppendFreight() > 0) {
                AdditionalFee af = iAdditionalFeeService.getAdditionalFeeByOrderId(pay.getOrderId());
                if (af == null) {
                    throw new BusinessException("根据订单号：" + pay.getOrderId() + " 未找到该打款记录【" + pay.getId() + "】的附加运费记录");
                }
                if (pay.getIsAutomatic() == AUTOMATIC1) {
                    af.setState(OrderAccountConst.ADDITIONAL_FEE.STATE2);
                } else {
                    af.setState(OrderAccountConst.ADDITIONAL_FEE.STATE4);
                    af.setDealState(OrderAccountConst.ADDITIONAL_FEE_DEAL_STATE.STATE3);
                    af.setDealRemark("线下打款_撤回");
                }

                af.setPayTime(getDateToLocalDateTime(pay.getPayTime()));
                af.setUpdateTime(LocalDateTime.now());
                iAdditionalFeeService.saveOrUpdate(af);
            }
        }
    }

    @Override
    public void doOrderLimtByFlowId(PayoutIntf payoutIntf) {
        if (payoutIntf.getUserType() != SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
            List<Long> batchIds = new ArrayList<Long>();
            batchIds.add(payoutIntf.getId());
            List<PayoutOrder> orderList = iPayoutOrderService.getPayoutOrderList(batchIds);
            for (PayoutOrder payoutOrder : orderList) {
                OrderLimit ol = iOrderLimitService.getOrderLimitByUserIdAndOrderId(payoutIntf.getUserId(), payoutOrder.getOrderId(), -1);
                if (ol == null) {
                    throw new BusinessException("订单号不存在");
                }
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(payoutOrder.getAmount());//交易金额（单位分）
                off.setOrderId(payoutOrder.getOrderId());//订单ID
                off.setUserId(payoutOrder.getUserId());
                off.setBusinessId(payoutIntf.getBusiId());//业务类型
                off.setBusinessName(getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//业务名称
                off.setBookTypeName(getSysStaticData("ACCOUNT_BOOK_TYPE", String.valueOf(off.getBookType())).getCodeName());//账户类型
                off.setSubjectsId(payoutIntf.getSubjectsId());//科目ID
                //			off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(payoutOrder.getBatchId());//业务流水ID
                off.setTenantId(payoutIntf.getTenantId());
                off.setInoutSts("out");//收支状态:收in支out转io
                //订单流向表
                iOrderFundFlowService.saveOrUpdate(off);
                ol.setNoPayCash(ol.getNoPayCash() - payoutOrder.getAmount());
                ol.setPaidCash(ol.getPaidCash() + payoutOrder.getAmount());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() - payoutOrder.getAmount());
                ol.setWithdrawCash(ol.getWithdrawCash() + payoutOrder.getAmount());
                iOrderLimitService.save(ol);
            }
        }
    }

    @Override
    public void callBack(PayoutIntf payout, String accessToken) {
        try {
            //违章代缴业务
            if (SUBJECTIDS50070 == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    //回调支付违章服务费接口
                    PayoutIntf newPayoutIntf = this.dealPayServiceFee(payout, accessToken);
                }
            }//平台服务费业务
            else if (SUBJECTIDS1800 == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    //回调支付违章服务费接口
                    this.dealPaySuccess(payout);
                }
            } else if (SUBJECTIDS1816 == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iPayManagerService.updatePayManagerState(payout.getId());
                }
            } else if (BILL_SERVICE_RECEIVABLE_OVERDUE_SUB == payout.getSubjectsId().longValue() || BILL_56K_RECEIVABLE_OVERDUE_SUB == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iApplyOpenBillService.payForBillServiceWriteBack(payout, accessToken);
                }
            } else if (SUBJECTIDS2302 == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iEtcBillInfoService.payForEtcBillWriteBack(payout);
                }
            } else if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iAccountStatementService.payForCarFeeWriteBack(payout.getBusiCode());
                }
            } else if (EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
                    BeanUtil.copyProperties(payout, payoutIntfDto);
                    iBusiSubjectsRelService.paySucessOilCallBack(payoutIntfDto);
                    //授信已用金额扣减
                    iTenantServiceRelDetailsService.oilExpireWriteBack(payout);
                }
            } else if (EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iBeidouPaymentRecordService.beidouPaymentWriteBack(payout);
                }
            } else if (EnumConsts.SubjectIds.SERVICE_RETREAT_RECEIVABLE_OVERDUE_SUB == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iRechargeInfoRecordService.createOilEntityInfo(payout.getBusiCode(), false, accessToken);
                }
            } else if (EnumConsts.SubjectIds.SUBJECTIDS1131 == payout.getSubjectsId().longValue()
                    && VEHICLE_AFFILIATION1.equals(payout.getVehicleAffiliation())) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iOilRechargeAccountDetailsFlowService.payRechargeSucess(payout.getBusiCode(), payout.getIsAutomatic());
                }
            } else if (EnumConsts.SubjectIds.SUBJECTIDS1133 == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
                    BeanUtil.copyProperties(payout, payoutIntfDto);
                    iOilRechargeAccountFlowService.payWithdrawSucess(payoutIntfDto, accessToken);
                }
            }//代收服务商维修保养
            else if (EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 == payout.getSubjectsId().longValue()) {
                //支付成功回调
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //回调之前先将前面的业务提交
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iServiceRepairOrderService.payRepairFeeCallBack(payout.getBusiCode(), accessToken);
                }
            }
        } catch (Exception e) {
//            log.info("flow_id["+payout.getFlowId()+"]支付成功，回调时报错",e);
            payout.setRespMsg("支付成功,回调时报错" + e.getMessage());
        }
    }

    @Override
    public PayoutIntf dealPayServiceFee(PayoutIntf violationFeePayoutIntf, String accessToken) {
        PayoutIntf payoutIntf = null;
        try {
            payoutIntf = payServiceFee(violationFeePayoutIntf, accessToken);
            //服务费支付失败，将实时消费记录变成逾期记录，推给task异步处理
            if (!respCodeSuc.equals(payoutIntf.getRespCode()) && !netTimeOutFail.equals(payoutIntf.getRespCode())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                payoutIntf.setId(null);
                payoutIntf.setSubjectsId(SUBJECTIDS1802);
                //会员体系改造开始

                //会员体系改造结束
                this.saveOrUpdate(payoutIntf);
                log.error("[" + payoutIntf.getId() + "]服务费支付失败，将实时消费记录变成逾期记录：" + payoutIntf.getRespMsg());
                PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
                BeanUtil.copyProperties(payoutIntf, payoutIntfDto);
                iBaseBusiToOrderService.doSavePayPlatformServiceFee(payoutIntfDto, accessToken);
                iBaseBusiToOrderService.doSavePayoutInfoExpansion(payoutIntfDto, accessToken);
            }
        } catch (Exception e) {
            //操作业务时失败，生成逾期记录，推给task异步处理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (null == payoutIntf) {
                log.error("支付服务费发生异常", e);
                return null;
            }
            PayoutIntf payoutIntfNew = new PayoutIntf();
            payoutIntfNew.setPayObjId(violationFeePayoutIntf.getUserId());
            //会员体系改造开始
            payoutIntfNew.setUserType(payoutIntfNew.getPayUserType());
            payoutIntfNew.setPayUserType(payoutIntf.getUserType());
            //会员体系改造结束
            payoutIntfNew.setTxnAmt(violationFeePayoutIntf.getPlatformServiceFee());//付款金额
            payoutIntfNew.setPayType(OrderAccountConst.PAY_TYPE.SERVICE);
            payoutIntfNew.setBusiCode(violationFeePayoutIntf.getBusiCode());
            payoutIntfNew.setPlateNumber(violationFeePayoutIntf.getPlateNumber());
            payoutIntfNew.setBusiId(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE);
            payoutIntfNew.setSubjectsId(SUBJECTIDS1802);
            payoutIntfNew.setPayAccNo(payoutIntf.getPayAccNo());
            payoutIntfNew.setAccountType(payoutIntf.getAccountType());
            payoutIntfNew.setIsNeedBill(OrderConsts.IS_NEED_BILL.NOT_NEED_BILL);

            PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
            BeanUtil.copyProperties(payoutIntf, payoutIntfDto);
            iBaseBusiToOrderService.doSavePayPlatformServiceFee(payoutIntfDto, accessToken);
            log.error("服务费支付失败,生成服务费逾期记录[" + payoutIntf.getId() + "]", e);
        } finally {
            platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
        }
        return payoutIntf;
    }

    @Override
    public PayoutIntf getPayOutIntfByXid(long xid) {
        LambdaQueryWrapper<PayoutIntf> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayoutIntf::getXid, xid);
        List<PayoutIntf> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public void dealPaySuccess(PayoutIntf payoutIntf) {
        if (StringUtils.isNotBlank(payoutIntf.getBusiCode())) {
            Date date = new Date();
            if (payoutIntf.getPayTime() != null) {
                date = payoutIntf.getPayTime();
            }
            String reminderId = payoutIntf.getBusiCode();
            ServiceChargeReminder serviceChargeReminder = iServiceChargeReminderService.getServiceChargeReminder(reminderId);
            serviceChargeReminder.setState(3);//修改为平台服务费支付成功
            serviceChargeReminder.setUpdateTime(getDateToLocalDateTime(date));
            iServiceChargeReminderService.saveOrUpdate(serviceChargeReminder);
            //车队档案添加字段--缴费日期回填
            java.sql.Date date1 = new java.sql.Date(date.getTime());
            sysTenantDefService.updatePayDate(serviceChargeReminder.getTenantId(), date1);
            //更新状态为已付款
            sysTenantDefService.updatePayState(serviceChargeReminder.getTenantId(), SysStaticDataEnum.TENANT_PAY_STATE.PAYED);
        }
    }

    /**
     * 根据流水号和租户id查询提现记录
     *
     * @param flowId   流水号
     * @param tenantId 租户id
     * @return PayoutIntf
     * @throws Exception
     */
    @Override
    public PayoutIntf getPayoutIntfByFlowId(Long flowId, Long tenantId) {

//        Session session = getEntityManager(true);
//        BaseUser baseUser = SysContexts.getCurrentOperator();
//        Criteria ca = session.createCriteria(PayoutIntf.class);
//        ca.add(Restrictions.eq("flowId", flowId));
        QueryWrapper<PayoutIntf> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant_id", tenantId)
                .eq("id", flowId);
        PayoutIntf payoutIntf = baseMapper.selectOne(wrapper);
        //TODO:数据库中的租户Id全部都是-1，查看下怎么解决！先注释租户ID的条件，让用户核销通过
//		if(tenantId>-1){
//			ca.add(Restrictions.eq("tenantId", tenantId));
//		}else{
//			ca.add(Restrictions.eq("tenantId", baseUser.getTenantId()));
//		}
//        return (PayoutIntf)ca.uniqueResult();
        return payoutIntf;
    }

    @Override
    public IPage<DueDateDetailsCDDto> getDueDateDetailsCD(String busiCode, String name, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        SysTenantDef def = sysTenantDefService.getSysTenantDef(tenantId);
        Boolean isTenant = false;
        if (def != null) {
            isTenant = true;
        }

        Page<DueDateDetailsCDDto> page = new Page<>(pageNum, pageSize);
        Page<DueDateDetailsCDDto> ipage = baseMapper.getDueDateDetailsCD(page, busiCode, name, isTenant, def.getId(), isTenant ? SysStaticDataEnum.USER_TYPE.ADMIN_USER : SysStaticDataEnum.USER_TYPE.RECEIVER_USER);

        for (DueDateDetailsCDDto record : ipage.getRecords()) {
            if (record.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && record.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
                record.setRespCodeName("车队已打款");
            } else if (record.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1)) {
                record.setRespCodeName("线上打款中");
            }

            if (record.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)) {
                record.setStateName("已确认线下收款");
            } else {
                record.setStateName("已逾期");
            }

            if (record.getBusiId() != null) {
                record.setBusiIdName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "BUSINESS_NUMBER_TYPE", String.valueOf(record.getBusiId())));
                if (record.getSubjectsId() != null && record.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                    record.setBusiIdName("支付轮胎租赁费");
                }
            }
        }
        return ipage;
    }

    @Override
    public void checkOverdueAcc(PayoutIntf payoutIntf, String accessToken) {
        //部分科目不需要校验
        if (PAY_FOR_OIL_CODE == payoutIntf.getBusiId().longValue() || PAY_FOR_REPAIR == payoutIntf.getBusiId().longValue()
                || BANK_PAYMENT_OUT == payoutIntf.getBusiId().longValue() || SUBJECTIDS50070 == payoutIntf.getSubjectsId()
                || BALANCE_CONSUME_OIL_SUB == payoutIntf.getSubjectsId() || BALANCE_PAY_REPAIR == payoutIntf.getSubjectsId()) {//不需要操作业务也就不需要校验账户
            return;
        }
        //如果付款给自己，则不操作账户
        if (null != payoutIntf.getPayObjId() && null != payoutIntf.getUserId() && payoutIntf.getPayObjId().longValue() == payoutIntf.getUserId().longValue()) {
            return;
        }
        //查询付款方账户
        Long tenantId = payoutIntf.getPayTenantId();
        if (payoutIntf.getPayType() != OrderAccountConst.PAY_TYPE.TENANT) {
            tenantId = payoutIntf.getTenantId();
        }
        if (getForPayTenantId(payoutIntf.getSubjectsId())) {
            tenantId = payoutIntf.getTenantId();
        }
        OrderAccount payAccount = iOrderAccountService.getOrderAccount(payoutIntf.getPayObjId(), payoutIntf.getVehicleAffiliation(), tenantId, payoutIntf.getOilAffiliation(), payoutIntf.getPayUserType());
        OrderAccount receivablesAccount = iOrderAccountService.getOrderAccount(payoutIntf.getUserId(), payoutIntf.getVehicleAffiliation(), tenantId, payoutIntf.getOilAffiliation(), payoutIntf.getUserType());
        //应付逾期
        Long currentPayableBalance = payAccount.getPayableOverdueBalance();
        //应收逾期
        Long currentReceivableBalance = receivablesAccount.getReceivableOverdueBalance();

        if (null == currentPayableBalance || currentPayableBalance.longValue() <= 0) {
            throw new BusinessException("付款账户" + payAccount.getId() + "应付逾期为：" + currentPayableBalance + "账户核销失败");
        }
        long appendFreight = payoutIntf.getAppendFreight() == null ? 0L : payoutIntf.getAppendFreight().longValue();
        long billServiceFee = payoutIntf.getBillServiceFee() == null ? 0L : payoutIntf.getBillServiceFee().longValue();
        if (currentPayableBalance.longValue() < (payoutIntf.getTxnAmt().longValue() + billServiceFee + appendFreight)) {
            throw new BusinessException("付款账户" + payAccount.getId() + "应付逾期少于付款金额账户核销失败");
        }
        if ((null == currentReceivableBalance || currentReceivableBalance.longValue() <= 0) && payoutIntf.getTxnAmt().longValue() > 0L) {
            throw new BusinessException("收款账户" + receivablesAccount.getId() + "应收逾期为 " + currentReceivableBalance + "账户核销失败");
        }

    }

    @Override
    public void paymentCheck(PayoutIntf payoutIntf, PayoutIntfExpansion payoutIntfExpansion, String accessToken) {
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
        AccountBankRel receiptRel = null;
        AccountBankRel paymentRel = null;
        String accId = null;
        Integer userType = payoutIntf.getUserType();
        if (sysTenantDef == null) {
            return;
        }
        if (payoutIntf == null) {
            throw new BusinessException("付款信息不存在");
        }
        if (payoutIntf.getRespCode() != null && payoutIntf.getRespCode().equals(HttpsMain.respCodeInvalid)) {
            throw new BusinessException("流水号" + payoutIntf.getId() + "该条付款记录已失效！");
        }
        if (payoutIntfExpansion == null) {
            throw new BusinessException("付款信息扩展记录不存在");
        }
        if (payoutIntfExpansion.getIsNeedBill().equals(OrderConsts.IS_NEED_BILL.TERRACE_BILL)) {
            accId = payoutIntf.getPinganCollectAcctId();
        } else {
            accId = payoutIntf.getAccNo();
        }
        if (StringUtils.isNotEmpty(accId)) {
            receiptRel = iAccountBankRelService.getAcctNo(accId);
            //自有车开平台票校验一下是否满足绑卡
            if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE) {
                payOutIntfUtil.getRandomPinganBankInfoOut(payoutIntf.getUserId(), payoutIntf.getPayTenantId());
            }
        }
        if (StringUtils.isNotEmpty(payoutIntf.getPayAccNo())) {
            paymentRel = iAccountBankRelService.getAcctNo(payoutIntf.getPayAccNo());
        }
        if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
            if (receiptRel == null || paymentRel == null) {
                throw new BusinessException("付款方或者收款方未绑定银行卡只能选择线下打款!");
            }
            if (!org.apache.commons.lang.StringUtils.isNotEmpty(receiptRel.getAcctNo()) || !org.apache.commons.lang.StringUtils.isNotEmpty(paymentRel.getAcctNo())) {
                throw new BusinessException("付款方或者收款方未绑定银行卡只能选择线下打款!");
            }
            if (!payoutIntf.getUserId().equals(receiptRel.getUserId()) || !payoutIntf.getPayObjId().equals(paymentRel.getUserId())) {
                throw new BusinessException("付款方或者收款方传入的银行卡信息有误!");
            }
        }
        //平台票只能对对公付款
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            if (!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("平台开票付款方必须对公付款!");
            }
        }
        //平台票服务商收款必须对公收款
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                && (userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER || userType == SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER
                || userType == SysStaticDataEnum.USER_TYPE.BILL_SERVER_USER)) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("平台开票收款方为服务商必须对公收款!");
            }
        }
        //北斗缴费
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN)) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("北斗缴费收款方必须对公收款!");
            }
        }
        //平台票车队||司机 || 员工 收款必须对私收款
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                && (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER || userType == SysStaticDataEnum.USER_TYPE.DRIVER_USER
                || userType == SysStaticDataEnum.USER_TYPE.ADMIN_USER || userType == SysStaticDataEnum.USER_TYPE.RECEIVER_USER)) {
            if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                    throw new BusinessException("平台开票收款方为车队||司机||员工 || 代收 必须对私收款!");
                }
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB)) {
            if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.COMMON_CARRIER_BILL) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("油卡充值票据类型是承运商开票必须对公收款!");
                }
            } else {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                    throw new BusinessException("油卡充值票据类型是不需要开票必须对私收款!");
                }
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302)) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("ETC账单类型必须对公收款!");
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE) && sysTenantDef.getActualController() != null) {
            if (receiptRel.getAcctName().equals(sysTenantDef.getActualController())) {
                throw new BusinessException("收款账户不能选择账户名与当前付款车队实际控制人姓名相同的账户!");
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS1131)) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) || !paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("油账户充值必须对公收付款!");
            }

            OilRechargeAccountDetailsFlow oi = oilRechargeAccountDetailsFlowService.getRechargeDetailsFlows(payoutIntf.getBusiCode(), SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2);
            if (oi != null && StringUtils.isNotEmpty(oi.getBankAccName()) && !paymentRel.getAcctName().equals(oi.getBankAccName())) {
                throw new BusinessException("付款账户名需要与充值选定的账户名一致,充值选定的账户名：" + oi.getBankAccName() + "!");
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) && payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) || !paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("共享油付款必须对公收付款!");
            }
        }
        //针对司机车管借支报销如果金额超过5W判断是否绑定支行
        if (payoutIntf.getBusiId() == EnumConsts.PayInter.TUBE_EXPENSE_ABLE
                || payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE
                || payoutIntf.getBusiId() == EnumConsts.PayInter.PAY_MANGER
                || payoutIntf.getBusiId() == EnumConsts.PayInter.DRIVER_EXPENSE_ABLE
                || payoutIntf.getBusiId() == EnumConsts.PayInter.OA_LOAN_AVAILABLE) {
            String amountFee = (String) this.getCfgVal(SysStaticDataEnum.JUDEGE_AMOUNT.AMOUNT_FEN, 0, String.class);
            long amountFeeFen = Long.parseLong(amountFee);
            if (payoutIntf.getTxnAmt() > amountFeeFen) {
                boolean flg = iAccountBankRelService.JudgeAmount(receiptRel.getPinganCollectAcctId());
                if (!flg) {
                    throw new BusinessException("打款失败!支付金额大于5万,按银行要求,收款方必须填写银行卡支行信息,请收款方填写支行信息在付款!");
                }
            }
        }
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            if (!payByCashService.payBill(paymentRel.getAcctName())) {
                throw new BusinessException("开票信息不完整,请前往车队档案完善开票信息!");
            }
            //平台票判断司机信息和车辆信息完整性
            if (payoutIntf.getOrderId() != null && payoutIntf.getOrderId() > 0) {
                String status = payByCashService.vehicleOrDriverBill(payoutIntf.getOrderId());
                if (status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1)) {
                    throw new BusinessException("司机信息不完整,请前往司机档案完善信息!");
                }
                if (status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2)) {
                    throw new BusinessException("车辆信息不完整,请前往车辆档案完善信息!");
                }
                //平台票需要判断付款方是否与订单开票抬头一致
                String lookUpName = payByCashService.judgebillLookUp(payoutIntf.getOrderId());
                if (org.apache.commons.lang.StringUtils.isNotEmpty(lookUpName) && !paymentRel.getAcctName().equals(lookUpName)) {
                    throw new BusinessException("付款账户名需要与订单选定的开票抬头一致,订单选定的开票抬头：" + lookUpName + "!");
                }
            }

            com.youming.youche.order.domain.order.PayoutIntf payout = new com.youming.youche.order.domain.order.PayoutIntf();
            BeanUtils.copyProperties(payoutIntf, payout);
            if (!iPayoutIntfService.judge(payout, payoutIntfExpansion)) {
                Map map = iPayoutIntfService.judgeName(payout, payoutIntfExpansion);
                String payBankAccName = DataFormat.getStringKey(map, "payBankAccName");
                String receivablesBankAccName = DataFormat.getStringKey(map, "receivablesBankAccName");
                if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
                    if (org.apache.commons.lang.StringUtils.isEmpty(receivablesBankAccName)) {
                        throw new BusinessException("流水号：[" + payoutIntf.getId() + "]该业务要求平台开票，因此付款账户必须与前一次的付款账户名相同。前一次付款账户名为:[" + payBankAccName + "]");
                    } else {
                        throw new BusinessException("流水号：[" + payoutIntf.getId() + "]该业务要求平台开票，因此收付款账户必须与前一次的收付款账户名相同。前一次付款账户名为:[" + payBankAccName + "]" + "收款账户名为：[" + receivablesBankAccName + "]");
                    }
                }
            }
            //如果是走路哥开票，校验本地业务校验会员认证 再调用璐哥风控接口  如果成功了才打款，不成功直接抛异常
            LoginInfo loginInfo = loginUtils.get(accessToken);
            Long tenantId = loginInfo.getTenantId();
            String sysParameLuge = SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE + "");
            String sysPre = billPlatformService.getPrefixByUserId(Long.valueOf(payoutIntf.getVehicleAffiliation()));
            if (sysPre != null && sysPre.equals(sysParameLuge)) {
                if (SUBJECTIDS1131 == payoutIntf.getSubjectsId().longValue()) {
                    //如果是充值油的，校验油站信息是否有效
                    AgentServiceDto agentServiceDto = serviceInfoService.getAgentService(payoutIntf.getPayTenantId(), ServiceConsts.AGENT_SERVICE_TYPE.OIL);
                    if (agentServiceDto == null) {
                        throw new BusinessException("本车队还没与油供应商签订协议，暂不能充值 ");
                    }

                    AgentServiceInfo agentServiceInfo = agentServiceDto.getAgentServiceInfo();
                    if (StringUtils.isBlank(agentServiceInfo.getLgOilStationId())) {
                        throw new BusinessException("获取车队关联油品公司维护的路哥油站Id为空 ");
                    }
                    long gasStationId = Long.valueOf(agentServiceInfo.getLgOilStationId());//路哥油站Id
                    //TODO 暂无对接第三方(油站)
//                    String stationRsp = "";//TODO待填写
//                    Map<String, Object> stationRspMap = JsonHelper.parseJSON2Map(stationRsp);
//                    boolean buyOilLimit =false;
//                    if ("0".equals(stationRspMap.get("reCode")+"")){
//                        if(stationRspMap.get("buyOilLimit")!=null&&"0".equals(stationRspMap.get("buyOilLimit")+"")){//可用
//                            buyOilLimit = true;
//                        }else{
//                            payoutIntf.setBatchSeq(payoutIntf.getId()+"");
//                            payoutIntf.setRespCode(HttpsMain.respCodeFail);
//                            payoutIntf.setPayTime(new Date());
//                            payoutIntf.setRespMsg("该油品公司油站不可用");
//                            throw new BusinessException("该油品公司油站不可用");
//                        }
//                    }else{
//                        String errorMsg = stationRspMap.get("reInfo")+"";
//                        payoutIntf.setBatchSeq(payoutIntf.getId()+"");
//                        payoutIntf.setRespCode(HttpsMain.respCodeFail);
//                        payoutIntf.setPayTime(new Date());
//                        payoutIntf.setRespMsg(errorMsg);
//                        throw new BusinessException(errorMsg);
//                    }
                } else {
                    String xid = orderFeeService.getXid(payoutIntf.getOrderId());
                    if (payoutIntf.getRespCode() != null && HttpsMain.netTimeOutFail.equals(payoutIntf.getRespCode())) {
                        //TODO 第三方暂未对接
//                        String rsp = SyncLugeUtil.qrpd(xid, "", "0", payoutIntf.getPayTenantId());
//                        Map<String, Object> rspMap = JsonHelper.parseJSON2Map(rsp);
//                        if(!"0".equals(rspMap.get("reCode")+"")){//返回0表示成功
//                            String errorMsg = rspMap.get("reInfo")+"";
//                            throw new BusinessException("此笔打款申请支付网络超时，查询申请支付结果失败："+errorMsg);
//                        }
//                        List<Map<String, String>> payList = (List<Map<String, String>>) rspMap.get("pay_list");
//                        if (payList == null || payList.size() <= 0) {
//                            throw new BusinessException("未找到此笔打款申请支付单号");
//                        }
//                        List<String> serialNumbers = new ArrayList<>();
//                        Map<String, String> serialNumbersMap = new HashMap<>();
//                        for (Map<String, String> map : payList) {
//                            serialNumbers.add(map.get("serialNumber"));
//                            serialNumbersMap.put(map.get("serialNumber"), map.get("pay_amount"));
//                        }
//                        List<PayoutIntfExpansion> expansions = payoutIntfSV.getPayoutIntfExpansion(serialNumbers);
//                        List<String> expansionSerialNumbers = new ArrayList<>();
//                        for (PayoutIntfExpansion pe : expansions) {
//                            expansionSerialNumbers.add(pe.getBillSerialNumber());
//                        }
//                        serialNumbers.removeAll(expansionSerialNumbers);
//                        if (serialNumbers.size() != 1) {
//                            throw new BusinessException("无法确定此笔打款申请支付单号，路歌返回支付单号集合=" + JsonHelper.toJson(rspMap.get("pay_list")));
//                        }
//                        long payActualMoney = com.business.utils.CommonUtil.getLongByString(serialNumbersMap.get(serialNumbers.get(0)));
//                        long billServiceFee = payoutIntf.getBillServiceFee();//业务计算出来的服务费
//                        long amount = payoutIntf.getTxnAmt();//付款金额
//                        String billSerialNumber = serialNumbersMap.get(serialNumbers.get(0));//路哥返回的支付单号，保存到拓展表
//                        payoutIntfExpansion.setBillSerialNumber(billSerialNumber);
//                        payoutIntfSV.saveOrUpdate(payoutIntfExpansion);
//                        if (payActualMoney != (billServiceFee + amount)) {//如果风控返回的总金额≠业务金额+业务计算出来的服务费，跑出异常
//                            //20190927修改
//                            //throw new BusinessException("路哥付款申请失败：计算的服务费与路哥风控返回的服务费金额不一致，路歌总支付金额：" + rspMap.get("payActualMoney"));
//                            paymentTF.dealLugeServiceFeeDifference(payoutIntf, payActualMoney);
//                        }
                    } else {
                        if (org.apache.commons.lang.StringUtils.isBlank(payoutIntfExpansion.getBillSerialNumber())) {
                            String payType = "0";//付款类型 0：现金付（默认） 2：油卡付
                            String pinganCollectAcctId = payoutIntf.getPinganCollectAcctId();//最终收款方虚拟卡号
                            AccountBankRel accountBankRel = iAccountBankRelService.getAcctNo(pinganCollectAcctId);
                            String payName = accountBankRel.getAcctName();//收款人姓名 现金付必填
                            String bankName = accountBankRel.getBankName();//银行名称
                            String bankProvince = accountBankRel.getProvinceName();//银行省份
                            String bankCity = accountBankRel.getCityName();//银行城市
                            String bankCardNo = accountBankRel.getAcctNo();//银行卡号
                            String payeeIdCard = accountBankRel.getIdentification();//收款人身份证号
                            String oilCardNo = "";//油卡号 油卡付必填
                            String payAmount = CommonUtil.getDoubleFormatLongMoney(payoutIntf.getTxnAmt());//支付金额  必须格式化为两位小数
                            //TODO 第三方暂未对接
//                            String rsp=SyncLugeUtil.anpy(payType, xid, payName, bankName, bankProvince, bankCity, bankCardNo, payeeIdCard, oilCardNo, payAmount,payoutIntf.getPayTenantId());
//                            Map<String, Object> rspMap = JsonHelper.parseJSON2Map(rsp);
//                            if(!"0".equals(rspMap.get("reCode")+"")){//返回0表示成功
//                                String errorMsg = rspMap.get("reInfo")+"";
//                                throw new BusinessException("路哥付款申请失败："+errorMsg);
//                            }
//                            //long payActualMoney = Long.valueOf(rspMap.get("payActualMoney")+"");//路哥返回的总金额
//                            long payActualMoney = com.business.utils.CommonUtil.getLongByString(rspMap.get("payActualMoney")+"");
//                            long billServiceFee = payoutIntf.getBillServiceFee();//业务计算出来的服务费
//                            long amount = payoutIntf.getTxnAmt();//付款金额
//                            String billSerialNumber = rspMap.get("serialNumber")+"";//路哥返回的支付单号，保存到拓展表
//                            payoutIntfExpansion.setBillSerialNumber(billSerialNumber);
//                            payoutIntfSV.saveOrUpdate(payoutIntfExpansion);
//                            if(payActualMoney!=(billServiceFee+amount)){//如果风控返回的总金额≠业务金额+业务计算出来的服务费，跑出异常
//                                //20190927修改
//                                //throw new BusinessException("路哥付款申请失败：计算的服务费与路哥风控返回的服务费金额不一致,路歌总支付金额：" + rspMap.get("payActualMoney"));
//                                paymentTF.dealLugeServiceFeeDifference(payoutIntf, payActualMoney);
//                            }
                        } else {
                            if (payoutIntf.getRemark().indexOf("计算的服务费与路哥风控返回的服务费金额不一致") != -1) {
                                int startIndex = payoutIntf.getRemark().indexOf("路歌总支付金额：");
                                int endIndex = payoutIntf.getRemark().length();
                                String payActualMoneyStr = payoutIntf.getRemark().substring(startIndex, endIndex);
                                long billServiceFee = payoutIntf.getBillServiceFee();//业务计算出来的服务费
                                long amount = payoutIntf.getTxnAmt();//付款金额
                                long payActualMoney = CommonUtil.getLongByString(payActualMoneyStr);
                                if (payActualMoney != (billServiceFee + amount)) {//如果风控返回的总金额≠业务金额+业务计算出来的服务费，跑出异常
                                    throw new BusinessException(payoutIntf.getRemark());
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public boolean getForPayTenantId(Long subjectsId) {
        if (EXCEPTION_OUT_PAYABLE_OVERDUE_SUB == subjectsId.longValue()) {
            return true;
        }
        if (CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN == subjectsId.longValue()) {
            return true;
        }
        if (CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN == subjectsId.longValue()) {
            return true;
        }
        if (CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN == subjectsId.longValue()) {
            return true;
        }
        if (CANCEL_ORDER_CASH_PAYABLE_IN == subjectsId.longValue()) {
            return true;
        }
        if (CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN == subjectsId.longValue()) {
            return true;
        }
        if (CANCEL_ORDER_ETC_PAYABLE_IN == subjectsId.longValue()) {
            return true;
        }
        if (UPDATE_ORDER_PAYABLE_CASH_LOW == subjectsId.longValue()) {
            return true;
        }
        if (UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW == subjectsId.longValue()) {
            return true;
        }
        if (UPDATE_ORDER_PAYABLE_ETC_LOW == subjectsId.longValue()) {
            return true;
        }
        if (UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW == subjectsId.longValue()) {
            return true;
        }
        if (UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW == subjectsId.longValue()) {
            return true;
        }
        if (UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW == subjectsId.longValue()) {
            return true;
        }
        return false;
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

    /**
     * 记录日志
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }

    public static List<Map> dealBusiOrderLimitRel(List<AcBusiOrderLimitRel> list) {
        List<Map> mapList = new ArrayList<>();
        Map<Long, List<AcBusiOrderLimitRel>> rtnMap = new HashMap<>();
        Map<Long, List<AcBusiOrderLimitRel>> rtnMap1 = new HashMap<>();
        List<AcBusiOrderLimitRel> busiOrderLimitRels = new ArrayList<>();
        List<AcBusiOrderLimitRel> busiOrderLimitRels1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            AcBusiOrderLimitRel busiOrderLimitRel = list.get(i);
            if (busiOrderLimitRel.getRelType() == 0) {
                busiOrderLimitRels.add(busiOrderLimitRel);
            } else if (busiOrderLimitRel.getRelType() == 1) {
                busiOrderLimitRels1.add(busiOrderLimitRel);
            }
        }
        List<AcBusiOrderLimitRel> busiOrderLimitRelsTmp = null;
        for (int i = 0; i < busiOrderLimitRels.size(); i++) {
            AcBusiOrderLimitRel busiOrderLimitRel = busiOrderLimitRels.get(i);
            busiOrderLimitRelsTmp = rtnMap.get(busiOrderLimitRel.getSubjectsId());
            if (null == busiOrderLimitRelsTmp) {
                busiOrderLimitRelsTmp = new ArrayList<>();
                rtnMap.put(busiOrderLimitRel.getSubjectsId(), busiOrderLimitRelsTmp);
            }
            busiOrderLimitRelsTmp.add(busiOrderLimitRel);

        }

        List<AcBusiOrderLimitRel> busiOrderLimitRelsTmp1 = null;

        for (int i = 0; i < busiOrderLimitRels1.size(); i++) {
            AcBusiOrderLimitRel busiOrderLimitRel = busiOrderLimitRels1.get(i);
            busiOrderLimitRelsTmp1 = rtnMap1.get(busiOrderLimitRel.getSubjectsId());
            if (null == busiOrderLimitRelsTmp1) {
                busiOrderLimitRelsTmp1 = new ArrayList<>();
                rtnMap1.put(busiOrderLimitRel.getSubjectsId(), busiOrderLimitRelsTmp1);
            }
            busiOrderLimitRelsTmp1.add(busiOrderLimitRel);
        }

        mapList.add(rtnMap);
        mapList.add(rtnMap1);
        return mapList;
    }

    public static Map dealBusiSubjectsRel(List<BusiSubjectsRel> list) {
        Map rtnMap = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            BusiSubjectsRel busiSubjectsRel = list.get(i);
            rtnMap.put(busiSubjectsRel.getSubjectsId(), busiSubjectsRel);
        }
        return rtnMap;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
        Date date;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        date = Date.from(zdt.toInstant());
        return date;
    }

    /***
     * 支付服务费
     */
    @Transactional
    public PayoutIntf payServiceFee(PayoutIntf violationFeePayoutIntf, String accessToken) {
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(violationFeePayoutIntf.getUserId(), null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到操作用户信息!");
        }
        //平台收取服务费用户
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(SysStaticDataEnum.PT_TENANT_ID);
        Long inUserId = sysTenantDef.getAdminUser();
        Long tenant = sysTenantDef.getId();
        SysUser sysOtherOperator = iSysUserService.getSysOperatorByUserIdOrPhone(inUserId, null, 0L);
        if (sysOtherOperator == null) {
            throw new BusinessException("没有找到操作用户信息!");
        }
        long soNbr = CommonUtil.createSoNbr();
        //服务商支付违章服务费用明细
        List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel consumeBalanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(SUBJECTIDS50072, violationFeePayoutIntf.getPlatformServiceFee());
        busiBalanceList.add(consumeBalanceSub);
        List<BusiSubjectsRel> balanceRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE, busiBalanceList);
        iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.CONSUME_CODE, EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE, violationFeePayoutIntf.getUserId(), inUserId, sysOtherOperator.getOpName(), balanceRelList, soNbr, 0L, -1L, violationFeePayoutIntf.getUserType());

        //平台收入违章代缴服务费用明细
        List<BusiSubjectsRel> serviceBusiBalanceList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel serviceBalanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50073, violationFeePayoutIntf.getPlatformServiceFee());
        serviceBusiBalanceList.add(serviceBalanceSub);
        List<BusiSubjectsRel> serviceBalanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE, serviceBusiBalanceList);
        iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE, inUserId, violationFeePayoutIntf.getUserId(), sysOperator.getOpName(), serviceBalanceList, soNbr, 0L, -1L, SysStaticDataEnum.USER_TYPE.ADMIN_USER);

        int accountType = BUSINESS_PAYABLE_ACCOUNT;//对公付款
        if (violationFeePayoutIntf.getBankType().intValue() == PRIVATE_RECEIVABLE_ACCOUNT) {
            accountType = PRIVATE_PAYABLE_ACCOUNT;
        }
        AccountBankRel accountBankRel = iAccountBankRelService.getAcctNo(violationFeePayoutIntf.getAccNo());
        if (null == accountBankRel) {
            throw new BusinessException("银行账号不存在");
        }
        int inbankType = BUSINESS_RECEIVABLE_ACCOUNT;//对公收款
        PayoutIntf payoutIntf = new PayoutIntf();
        payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.SERVICE);
        payoutIntf.setTenantId(tenant);
        payoutIntf.setPayObjId(violationFeePayoutIntf.getUserId());
        payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
        payoutIntf.setUserId(inUserId);
        //会员体系改造开始
        payoutIntf.setUserType(violationFeePayoutIntf.getPayUserType());
        payoutIntf.setPayUserType(violationFeePayoutIntf.getUserType());
        //会员体系改造结束
        payoutIntf.setVehicleAffiliation(VEHICLE_AFFILIATION1);
        payoutIntf.setOilAffiliation(VEHICLE_AFFILIATION1);
        payoutIntf.setIsAutomatic(AUTOMATIC1);
        if (null != sysOtherOperator.getBillId()) {
            payoutIntf.setObjId(Long.parseLong(sysOtherOperator.getBillId()));
        }
        payoutIntf.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
        payoutIntf.setTxnAmt(violationFeePayoutIntf.getPlatformServiceFee());
        payoutIntf.setBusiId(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE);
        payoutIntf.setSubjectsId(SUBJECTIDS50072);
        payoutIntf.setBusiCode(violationFeePayoutIntf.getBusiCode());
        payoutIntf.setPlateNumber(violationFeePayoutIntf.getPlateNumber());
        payoutIntf.setAccountType(accountType);
        payoutIntf.setPayAccNo(accountBankRel.getPinganPayAcctId());
        payoutIntf.setBankType(inbankType);
        payoutIntf.setIsNeedBill(OrderConsts.IS_NEED_BILL.NOT_NEED_BILL);

        PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
        BeanUtil.copyProperties(payoutIntf, payoutIntfDto);
        iBaseBusiToOrderService.doSavePayOutIntfForOA(payoutIntfDto, accessToken);
        initBank();
        PayReturnDto payReturnOut = iBaseBusiToOrderService.payMemberTransaction(payoutIntf.getPayObjId(), payoutIntf.getUserId(), payoutIntf.getTxnAmt(), payoutIntf.getAccountType(), payoutIntf.getBankType(), payoutIntf.getId() + "", false, "", "", payoutIntf.getPayUserType(), payoutIntf.getUserType());
        if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts1) {
            //记录日志
            payoutIntf.setRespCode(netTimeOutFail);
            payoutIntf.setRespBankCode(BANK_OTHER_ERROR);
            payoutIntf.setRespMsg(payReturnOut.getReqMess());
        }
        //余额不足
        else if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts2) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            payoutIntf.setRespCode(HttpsMain.respCodeFail);
            payoutIntf.setRespBankCode(BALANCE_NOT_ENOUGH);
            payoutIntf.setRespMsg(payReturnOut.getReqMess());
        }
        //未绑定银行卡
        else if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts3) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            payoutIntf.setRespCode(HttpsMain.respCodeFail);
            payoutIntf.setRespBankCode(LOCAL_BUSI_ERROR);
            payoutIntf.setRespMsg(payReturnOut.getReqMess());
        }
        //其他异常
        else if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts4) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            payoutIntf.setRespCode(HttpsMain.respCodeFail);
            payoutIntf.setRespBankCode(BANK_OTHER_ERROR);
            payoutIntf.setRespMsg(payReturnOut.getReqMess());
        } else {
            //打款成功
            payoutIntf.setVerificationState(VERIFICATION_STATE);
            payoutIntf.setRespCode(respCodeSuc);
            payoutIntf.setRespMsg("支付成功");
        }
        Date now = new Date();
        payoutIntf.setVerificationDate(now);
        payoutIntf.setPayTime(now);
        payoutIntf.setCompleteTime(DateUtil.formatDate(now, DATETIME12_FORMAT2));
        payoutIntf.setSerialNumber(payReturnOut.getThirdLogNo());
        return payoutIntf;
    }

    public void initBank() {
        SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat("BANK_INIT"));
        if (sysCfg == null) {
            throw new BusinessException("没有配置银行初始参数!");
        }
//        if(sysCfg.getCfgValue().equals("1")){
//            return (IBankCallTF)SysContexts.getBean("pingAnBankTF");
//        }
    }

    @Override
    public void cmbDealOnlineToOffline(String accessToken) {
        LOGGER.info("------------处理线上转线下付款调用开始-------------");
        int allRow = baseMapper.getOnlineToOfflineCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("处理线上转线下付款记录[" + allRow + "]条,总共" + totalPage + "页");
        int currentPage = 0;
        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getOnlineToOfflineList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, currentPage, pageSize);
            LOGGER.info("处理线上转线下付款记录:第" + (i + 1) + "页:" + payoutIntf.size());
            cmbDeal(payoutIntf, accessToken);
            currentPage += pageSize;
        }
        LOGGER.info("------------处理线上转线下付款调用结束-------------");
    }

    @Override
    public void dealOnlineToOffline(String accessToken) {
        LOGGER.info("------------处理线上转线下付款调用开始-------------");
        int allRow = baseMapper.getOnlineToOfflineCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("处理线上转线下付款记录[" + allRow + "]条,总共" + totalPage + "页");
        int currentPage = 0;
        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getOnlineToOfflineList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, currentPage, pageSize);
            LOGGER.info("处理线上转线下付款记录:第" + (i + 1) + "页:" + payoutIntf.size());
            cmbDeal(payoutIntf, accessToken);
            currentPage += pageSize;
        }
        LOGGER.info("------------处理线上转线下付款调用结束-------------");
    }

    public void cmbDeal(List<PayoutIntf> payoutIntf, String accessToken) {
        for (PayoutIntf payout : payoutIntf) {
            try {
                payout.setIsTurnAutomatic(IS_TURN_AUTOMATIC_0);
                payout.setIsAutomatic(0);
                payout.setRemark("成功转为非自动打款");
                if (payout.getPayTenantId() > 0) {
                    Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payout.getId(), payout.getPayTenantId());
                    if (!b) {
                        startAuditProcess(payout, accessToken);
                    }
                }
                baseMapper.updateById(payout);
            } catch (Exception e) {
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOGGER.info("FLOW_ID:[" + payout.getId() + "]线上转线下付款失败", e);
                payout.setRemark("线上转线下付款失败" + e.getMessage());
                this.saveOrUpdate(payout);
            } finally {
//                platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
            }
        }
    }

    public void deal(List<PayoutIntf> payoutIntf, String accessToken) {
        for (PayoutIntf payout : payoutIntf) {
            try {
                if (StringUtils.isNotBlank(payout.getSerialNumber())) {

                }
            } catch (Exception e) {

            }
        }
    }

    public void startAuditProcess(PayoutIntf payoutIntf, String accessToken) {
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("flowId", payoutIntf.getId());
        boolean bool = false;
        if (payoutIntf.getPayTenantId() > 0) {
            bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), com.youming.youche.commons.constant.SysOperLogConst.BusiCode.Payoutchunying, params, accessToken, payoutIntf.getPayTenantId());
        } else {
            //如果付款不是车队，不需要启动审核流程
            bool = true;
        }

        if (!bool) {
            throw new BusinessException("启动现金付款审核流程失败！");
        }
    }


    @Override
    public void payOutToBusi() {
        LOGGER.info("------------银行发起业务支付调用开始-------------");

        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue());
        if (loginInfo == null) {
            loginInfo = new LoginInfo();
            loginInfo.setName(((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_USER_NAME))).getCfgValue());
            loginInfo.setUserInfoId(Long.valueOf(((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_USER_ID))).getCfgValue()));
            loginInfo.setTenantId(-1L);

            redisUtil.set("loginInfo:" + ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue(), loginInfo);
        }

        String accessToken = ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue();

        String createDate = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        //先处理线上打款成功转线下手工打款的数据  处理银行付完款后修改状态
//        this.cmbDealOnlineToOffline(accessToken);

        int allRow = baseMapper.getPayoutInfoBusiCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("PayOutToBusi200Task 处理200业务业务支付记录[" + allRow + "]条,总共" + totalPage + "页");
        int currentPage = 0;
        List<AcBusiOrderLimitRel> busiOrderLimitRelList = acBusiOrderLimitRelService.getBaseMapper().selectList(null);
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.getBusiSubjectsRelList();

        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getPayoutInfoBusiList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate, currentPage, pageSize);
            LOGGER.info("PayOutToBusi200Task 200业务发起业务支付记录:第" + (i + 1) + "页:" + payoutIntf.size());
            payOutToBusiPayInfo(payoutIntf, busiOrderLimitRelList, busiSubjectsRelList, accessToken);
            currentPage += pageSize;
        }
        LOGGER.info("------------200发起业务支付调用结束-------------");
    }


    public void payOutToBusiPayInfo(List<PayoutIntf> payoutIntf, List<AcBusiOrderLimitRel> busiOrderLimitRelList, List<BusiSubjectsRel> busiSubjectsRelList, String accessToken) {
        if (null != payoutIntf && !payoutIntf.isEmpty()) {
            //完成一笔业务支付 提交一次事务
            for (PayoutIntf payout : payoutIntf) {
                try {
                    payOutToBusiPay(busiOrderLimitRelList, payout, accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                    //遇到异常，回滚业务
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                    LOGGER.error("FLOW_ID:[" + payout.getId() + "]业务支付失败", e);
                    String errorMessage = e.getMessage();
                    if (StringUtils.isNoneBlank(errorMessage) && errorMessage.length() > 200) {
                        errorMessage = errorMessage.substring(0, 200) + "...";
                    }
                    payout.setRespMsg("业务支付失败:" + errorMessage);
                    payout.setPayTime(new Date());
                    baseMapper.updateById(payout);
                } finally {
//                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                }
            }
        }
    }

    private void payOutToBusiPay(List<AcBusiOrderLimitRel> busiOrderLimitRelList, PayoutIntf payout, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        com.youming.youche.order.domain.order.PayoutIntf payoutIntfs = new com.youming.youche.order.domain.order.PayoutIntf();
        BeanUtil.copyProperties(payout, payoutIntfs);
        iPayoutIntfService.doPay200(payoutIntfs, null, null, false, loginInfo, accessToken);
//        this.doPay200Two(payout, busiOrderLimitRelList, true, accessToken);
        //打款成功
        payout.setRespCode(HttpsMain.respCodeSuc);
        payout.setRespMsg("支付成功");
        Date now = new Date();
        payout.setVerificationDate(now);
        payout.setPayTime(now);
        payout.setCompleteTime(DateUtil.formatDate(now, DateUtil.DATETIME12_FORMAT2));
        baseMapper.updateById(payout);

        this.callBack(payout, accessToken);
        payByCashService.confirmationSMS(EnumConsts.SmsTemplate.PAY_CASH_SMS, payout);
        if (payout.getPayTenantId() > 0) {
            Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payout.getId(), payout.getPayTenantId());
            if (b) {
                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payout.getId(), null, AuditConsts.RESULT.SUCCESS, null, AuditConsts.OperType.TASK, payout.getPayTenantId(), accessToken);
            }
        }
    }

    @Override
    public void payOutToBank() {
        LOGGER.info("------------银行发起支付调用开始-------------");

        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue());
        if (loginInfo == null) {
            loginInfo = new LoginInfo();
            loginInfo.setName(((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_USER_NAME))).getCfgValue());
            loginInfo.setUserInfoId(Long.valueOf(((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_USER_ID))).getCfgValue()));
            loginInfo.setTenantId(-1L);

            redisUtil.set("loginInfo:" + ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue(), loginInfo);
        }

        String accessToken = ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue();

        String createDate = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");

        this.dealOnlineToOffline(accessToken);
        int allRow = baseMapper.getPayoutInfoCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("PayOutToBank200Task 处理200银行支付记录[" + allRow + "]条,总共" + totalPage + "页");
        int currentPage = 0;
        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getPayoutInfoList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate, currentPage, pageSize);
            LOGGER.info("PayOutToBank200Task 200银行发起支付记录:第" + (i + 1) + "页:" + payoutIntf.size());
            payOutToBankPay(payoutIntf, accessToken);
            currentPage += pageSize;
        }

        LOGGER.info("------------200发起支付调用结束-------------");
    }

    public void payOutToBankPay(List<PayoutIntf> payoutIntf, String accessToken) {
        if (null != payoutIntf && !payoutIntf.isEmpty()) {
            //完成一笔支付 提交一次事务
            for (PayoutIntf payout : payoutIntf) {
                PayoutIntfExpansion payoutIntfExpansion = null;
                try {
                    LambdaQueryWrapper<PayoutIntfExpansion> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(PayoutIntfExpansion::getFlowId, payout.getId());

                    payoutIntfExpansion = payoutIntfExpansionMapper.selectOne(queryWrapper);

                    payOutToBankPay(payout, payoutIntfExpansion, accessToken);
                } catch (Exception e) {
                    //遇到异常，回滚业务
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                    LOGGER.error("FLOW_ID:[" + payout.getId() + "]银行支付失败", e);
                    payout.setRespCode(HttpsMain.respCodeFail);
                    payout.setRespBankCode(LOCAL_BUSI_ERROR);
//                    payout.setRespMsg("银行支付失败:" + e.getMessage());
                    payout.setRespMsg("银行支付失败");
                    payout.setPayTime(new Date());
                    baseMapper.updateById(payout);
                    if (payoutIntfExpansion != null) {
                        payoutIntfExpansionMapper.updateById(payoutIntfExpansion);
                    }
                } finally {
//                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                }
            }
        }
    }

    //重新生成打款记录  现在的逻辑不需要
    private void payOutToBankCreatePay(PayoutIntf payout, String accessToken) {
        com.youming.youche.order.domain.order.PayoutIntf newPayoutIntf = new com.youming.youche.order.domain.order.PayoutIntf();
        BeanUtils.copyProperties(payout, newPayoutIntf);
        newPayoutIntf.setId(null);
        newPayoutIntf.setRespCode(null);
        newPayoutIntf.setSerialNumber(null);
        newPayoutIntf.setRespBankCode(null);
        newPayoutIntf.setRespMsg(null);
        newPayoutIntf.setReFlowId(payout.getId());
        newPayoutIntf.setCreateTime(LocalDateTime.now());
        newPayoutIntf.setCompleteTime("");
        newPayoutIntf.setPayTime(null);

        iPayoutIntfService.doSavePayOutIntfVirToVir(newPayoutIntf, accessToken);
        //将报错的失效掉
        payout.setRespCode(HttpsMain.respCodeInvalid);
        baseMapper.updateById(payout);
    }

    private void payOutToBankPay(PayoutIntf payout, PayoutIntfExpansion payoutIntfExpansion, String accessToken) {
        this.checkOverdueAcc(payout, accessToken);
        if (payout.getSubjectsId() == BEFORE_HA_TOTAL_FEE) {
            PinganBankInfoOutDto pinganBankInfoOut = payOutIntfUtil.getRandomPinganBankInfoOut(payout.getUserId(), payout.getPayTenantId());
            payout.setReceivablesBankAccName(pinganBankInfoOut.getPrivateAcctName());
            payout.setReceivablesBankAccNo(pinganBankInfoOut.getPrivateAcctNo());
            payout.setBankCode(pinganBankInfoOut.getPrivateBankCode());
            payout.setPinganCollectAcctId(pinganBankInfoOut.getPrivatePinganAcctIdM());
            baseMapper.updateById(payout);
        }
        try {
            this.paymentCheck(payout, payoutIntfExpansion, accessToken);
        } catch (Exception ex) {
            payout.setIsAutomatic(IS_TURN_AUTOMATIC_0);
            payout.setRemark("打款规则校验不通过，自动转到非自动打款：" + ex.getMessage());
            payout.setRemark(ex.getMessage());
            payoutIntfExpansion.setRemark(ex.getMessage());
            baseMapper.updateById(payout);
            payoutIntfExpansionMapper.updateById(payoutIntfExpansion);
            LoginInfo loginInfo = loginUtils.get(accessToken);
            sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.Payoutchunying, payout.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "打款规则校验不通过，自动转到非自动打款");

            if (payout.getPayTenantId() > 0) {
                Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payout.getId(), payout.getPayTenantId());
                if (b) {
                    this.startAuditProcess(payout, accessToken);
                }
            }

            return;
        }

        AccountTransferVo accountTransferVo = new AccountTransferVo();
        accountTransferVo.setMbrNo(payout.getPayAccNo());//付款人卡编号
        accountTransferVo.setMbrName(payout.getPayAccName());//付款方名称
        accountTransferVo.setUserId(payout.getUserId());//操作用户ID

        // 加上票据服务费，如果没有则取0  加上56k附加运费
        long billserviceFee = payout.getBillServiceFee() != null ? payout.getBillServiceFee() : 0L;
        long appendFreight = payout.getAppendFreight() == null ? 0L : payout.getAppendFreight();
        Double d = Double.valueOf(payout.getTxnAmt() + billserviceFee + appendFreight);
        String money = String.valueOf(d / 100);
        accountTransferVo.setTranAmt(money);//金额
        accountTransferVo.setRecvMbrName(payout.getAccName());//收款人卡编号
        AccountBankRel accountBankRel = iAccountBankRelService.getAcctNo(payout.getPinganCollectAcctId());
        accountTransferVo.setRecvMbrNo(accountBankRel.getPinganPayAcctId());//收款方名称
        accountTransferVo.setRemark(payout.getRemark());//备注
        accountTransferVo.setPayoutId(payout.getId());// 付款表ID

        accountTransferVo.setPayAccountId(bankAccountService.getByMbrNo(payout.getPayAccNo()).getId());//付款方ID
        accountTransferVo.setRecvAccountId(bankAccountService.getByMbrNo(accountBankRel.getPinganPayAcctId()).getId());// 收款方ID

        try {
            bankAccountTranService.transfer(accountTransferVo);
            Date now = new Date();
            payout.setVerificationState(VERIFICATION_STATE);
            payout.setRespCode(respCodeZero);
            payout.setRespMsg("银行支付发起成功");
            payout.setVerificationDate(now);
            payout.setPayTime(now);
            payout.setCompleteTime(DateUtil.formatDate(now, DATETIME12_FORMAT2));

            baseMapper.updateById(payout);
            payoutIntfExpansionMapper.updateById(payoutIntfExpansion);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("FLOW_ID:[" + payout.getId() + "]银行支付失败", e);
            payout.setRespCode(HttpsMain.respCodeFail);
            payout.setRespBankCode(LOCAL_BUSI_ERROR);
            payout.setRespMsg("银行支付失败:" + e.getMessage());
            payout.setPayTime(new Date());
            baseMapper.updateById(payout);
            if (payoutIntfExpansion != null) {
                payoutIntfExpansionMapper.updateById(payoutIntfExpansion);
            }
        }
    }

    @Override
    public void confirmMoney() {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue());
        if (loginInfo == null) {
            loginInfo = new LoginInfo();
            loginInfo.setName(((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_USER_NAME))).getCfgValue());
            loginInfo.setUserInfoId(Long.valueOf(((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_USER_ID))).getCfgValue()));
            loginInfo.setTenantId(-1L);

            redisUtil.set("loginInfo:" + ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue(), loginInfo);
        }

        String accessToken = ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue();

        try {
            LOGGER.info("------------到期自动确认收款开始-------------");

            String timeSMS = (String) getCfgVal(SysStaticDataEnum.MATURITY.MATURITY1, 0, String.class);
            int dateSMS = Integer.valueOf(timeSMS);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - dateSMS);
            Date today = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String resultStart = format.format(today);

            //到期自动确认线下收款总数
            int allRow = baseMapper.doQueryConfirmMoney(resultStart + "235959");
            int pageSize = 1000;
            int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
            LOGGER.info("ConfirmMoneyTask 到期自动确认收款结果记录[" + allRow + "]条,总共" + totalPage + "页");
            int currentPage = 0;
            for (int i = 0; i < totalPage; i++) {
                List<PayoutIntf> payoutIntf = baseMapper.doQueryConfirmMoneyList(resultStart + "235959", currentPage * pageSize, pageSize);
                LOGGER.info("ConfirmMoneyTask 到期自动确认收款结果记录:第" + (i + 1) + "页:" + payoutIntf.size());
                if (payoutIntf != null && payoutIntf.size() > 0) {
                    for (PayoutIntf payoutIntfInfo : payoutIntf) {
                        this.confirmation(payoutIntfInfo, accessToken);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("到期自动确认收款报错错误信息:" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } finally {
//            platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
        }
        LOGGER.info("------------到期自动确认收款结束-------------");

    }

    public void confirmation(PayoutIntf payoutIntf, String accessToken) {
        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        String timeSMS = (String) getCfgVal(SysStaticDataEnum.MATURITY.MATURITY1, 0, String.class);
        String time = (String) getCfgVal(SysStaticDataEnum.MATURITY.MATURITY2, 0, String.class);
        int dateSMS = Integer.valueOf(timeSMS);
        int dateConfirmation = Integer.valueOf(time);
        if (StringUtils.isNotEmpty(payoutIntf.getCompleteTime())) {
            StringBuffer sb = new StringBuffer(payoutIntf.getCompleteTime());
            String completfTime = sb.replace(8, 14, "000000").toString();
            Date date = DateUtil.parseDate(completfTime, "yyyyMMddHHmmss");
            Date date1 = new Date();
            int days = DateUtil.differentDaysByMillisecond(date, date1);
            //发送短信
            if (payoutIntf.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER) && days == dateSMS) {
                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
                String now = new SimpleDateFormat("yyyy年MM月dd日").format(date);
                Calendar date2 = Calendar.getInstance();
                date2.setTime(date);
                date2.set(Calendar.DATE, date2.get(Calendar.DATE) + dateConfirmation);
                Date date3 = date2.getTime();
                String now1 = new SimpleDateFormat("yyyy年MM月dd日").format(date3);
                Map<String, Object> smsMap = new HashMap<String, Object>();
                Map<String, Object> paraMap = new HashMap<String, Object>();
                paraMap.put("userName", userDataInfo.getLinkman());
                paraMap.put("date", now);
                paraMap.put("tenantName", sysTenantDef.getName());
                paraMap.put("amount", String.valueOf(CommonUtil.getDoubleFormatLongMoney(payoutIntf.getTxnAmt(), 2)));
                paraMap.put("time", now1);
                SysSmsSend sysSmsSend = new SysSmsSend();
                sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.PAY_CASH_SMS5);
                sysSmsSend.setBillId(userDataInfo.getMobilePhone());
                sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.OPERATIONAL_QUALIFIVATION);
                sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
                sysSmsSend.setObjId(userDataInfo.getMobilePhone());
                sysSmsSend.setParamMap(paraMap);
                sysSmsSendService.sendSms(sysSmsSend);
            }

            //确认收款
            if (days >= dateConfirmation) {
                this.doPay200(payoutIntf, null, false, accessToken);
                payoutIntf.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
                payoutIntf.setVerificationDate(new Date());
                payoutIntf.setPayTime(new Date());
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.transfer);
                payoutIntf.setRemark("超期系统自动确认");
                baseMapper.updateById(payoutIntf);
                if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS1816) {
                    baseMapper.updatePayManagerState(payoutIntf.getId());
                } else if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013) {
                    iServiceRepairOrderService.payRepairFeeCallBack(payoutIntf.getBusiCode(), accessToken);
                }
                if (payoutIntf.getPayTenantId() > 0) {
                    Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), payoutIntf.getPayTenantId());
                    if (b) {
                        auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), null, AuditConsts.RESULT.SUCCESS, null, AuditConsts.OperType.TASK, payoutIntf.getPayTenantId(), accessToken);
                    }
                }
            }
        }
    }

    @Override
    public void cmbOutToBank() {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue());
        if (loginInfo == null) {
            loginInfo = new LoginInfo();
            loginInfo.setName(((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_USER_NAME))).getCfgValue());
            loginInfo.setUserInfoId(Long.valueOf(((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_USER_ID))).getCfgValue()));
            loginInfo.setTenantId(-1L);

            redisUtil.set("loginInfo:" + ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue(), loginInfo);
        }

        String accessToken = ((SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue();

        String createDate = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        this.dealOnlineToOffline(accessToken);
        LOGGER.info("------------银行发起支付调用开始-------------");

        int allRow = baseMapper.getPayoutInfoCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("PayOutToBank200Task 处理200银行支付记录[" + allRow + "]条,总共" + totalPage + "页");
        int currentPage = 0;
        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getPayoutInfoList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate, currentPage, pageSize);
            LOGGER.info("PayOutToBank200Task 200银行发起支付记录:第" + (i + 1) + "页:" + payoutIntf.size());
            cmbOutToBankPayInfo(payoutIntf, accessToken);
            currentPage += pageSize;
        }

        LOGGER.info("------------200发起支付调用结束-------------");
    }

    public Object getCfgVal(String cfgName, int system, Class type) {
        SysCfg cfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
        if (cfg != null && (system == -1 || system == cfg.getCfgSystem())) {
            if (type.equals(Integer.class)) {
                return Integer.valueOf(cfg.getCfgValue());
            } else if (type.equals(Double.class)) {
                return Double.parseDouble(cfg.getCfgValue());
            } else if (type.equals(Float.class)) {
                return Float.parseFloat(cfg.getCfgValue());
            } else {
                return !type.equals(Boolean.class) ? String.valueOf(cfg.getCfgValue()) : "1".equals(cfg.getCfgValue()) || "true".equals(cfg.getCfgValue().toLowerCase());
            }
        } else {
            return null;
        }
    }

    //2004
    public void cmbOutToBankPayInfo(List<PayoutIntf> payoutIntf, String accessToken) {
        if (null != payoutIntf && !payoutIntf.isEmpty()) {
            AccountBankRel accountBankRel = null;
            AccountTransferVo accountTransferVo = null;
            for (PayoutIntf payout : payoutIntf) {
                if (!StringUtils.isNotEmpty(payout.getPinganCollectAcctId())) {
                    continue;
                }

                accountTransferVo = new AccountTransferVo();
                accountBankRel = new AccountBankRel();
                // 获取银行卡信息
                accountBankRel = iAccountBankRelService.getAcctNo(payout.getPinganCollectAcctId());
                CmbBankAccountInfo bankAccountInfo = bankAccountService.getByMerchNo(payout.getPinganCollectAcctId());
                if (accountBankRel != null && !StringUtils.isNotEmpty(accountBankRel.getPinganPayAcctId())) {
                    continue;
                }
                accountTransferVo.setMbrNo(payout.getPayAccNo());//付款人卡编号
                accountTransferVo.setMbrName(payout.getPayAccName());//付款方名称
                if (!StringUtils.isNotEmpty(payout.getPayAccNo())) {
                    continue;
                }
                Double d = Double.valueOf(payout.getTxnAmt());
                String money = String.valueOf(d / 100);
                LOGGER.info("MbrNo:[" + payout.getPayAccNo() + "]银行支付金额" + money);
                //业务金额+开票服务+附加运费
//                String money=String.valueOf(payout.getTxnAmt()/100+ (payout.getAppendFreight() !=null? 0: payout.getAppendFreight()/100) +(payout.getBillServiceFee()!=null ? 0:payout.getBillServiceFee() /100));
                if (d / 100 <= 0) {
                    continue;
                }
                accountTransferVo.setUserId(payout.getUserId());//操作用户ID
                accountTransferVo.setTranAmt(money);//金额
                accountTransferVo.setRecvMbrName(payout.getAccName());//收款人卡编号
                accountTransferVo.setRecvMbrNo(accountBankRel != null ? accountBankRel.getPinganPayAcctId() : bankAccountInfo.getMbrNo());//收款方名称
                accountTransferVo.setRemark(payout.getRemark());//备注
                accountTransferVo.setPayoutId(payout.getId());
                accountTransferVo.setPayAccountId(bankAccountService.getByMbrNo(payout.getPayAccNo()).getId());//付款方ID
                accountTransferVo.setRecvAccountId(accountBankRel != null ? bankAccountService.getByMbrNo(accountBankRel.getPinganPayAcctId()).getId() : bankAccountInfo.getId());// 收款方ID
                try {
                    // 发起支付时间
                    payout.setPayTime(new Date());
                    //转账
                    bankAccountTranService.transfer(accountTransferVo);
                    payout.setRespMsg("银行支付成功");
                    //支付完成時間
                    Date now = new Date();
                    payout.setCompleteTime(DateUtil.formatDate(now, DATETIME12_FORMAT2));
                    payout.setRespCode(HttpsMain.respCodeCollection);//银行打款成功
                    payout.setVerificationDate(now);
                    payout.setRespMsg("支付成功");
                    payout.setPayConfirm(2);
                    payout.setIsAutomatic(1);
                    payout.setVerificationState(5);

                    //查询支付结果 修改状态
                    baseMapper.updateById(payout);

                    //修改支付結果狀態
                    baseMapper.updatePayManagerState(payout.getId());

                    //打款完成之后部分业务需要回调
                    this.callBack(payout, accessToken);

                    LOGGER.info("MbrNo:[" + payout.getPayAccNo() + "]银行支付成功");

                    OrderAccount account = iOrderAccountService.getOrderAccount(accountBankRel != null ? accountBankRel.getUserId() : bankAccountInfo.getUserId());
                    upOrderAccount(account, payout.getTxnAmt());
                } catch (Exception e) {
                    //遇到异常，回滚业务
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    //本地业务异常
                    LOGGER.error("FLOW_ID:[" + payout.getId() + "]银行支付失败", e);
                    payout.setRespCode(HttpsMain.respCodeFail);
                    payout.setRespBankCode(LOCAL_BUSI_ERROR);
                    payout.setRespMsg("银行支付失败:" + e.getMessage());
                    payout.setPayTime(new Date());
                    this.saveOrUpdate(payout);
                } finally {
                    //提交事务
//                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                }
            }
        }
    }

    //2004
    public void upOrderAccount(OrderAccount account, Long TxnAmt) {
        //处理用户的账户应收逾期金额
        if (account.getReceivableOverdueBalance() < TxnAmt) {
            iOrderAccountService.upOrderAccountReceivableOverdueBalance(account.getId(), 0L);
            TxnAmt = TxnAmt - account.getReceivableOverdueBalance();
            account = iOrderAccountService.getOrderAccount(account.getUserId());
            upOrderAccount(account, TxnAmt);
        } else {
            iOrderAccountService.upOrderAccountReceivableOverdueBalance(account.getId(), account.getReceivableOverdueBalance() - TxnAmt);
        }
    }

    @Override
    public Page<PayoutInfoDto> getOverdueCD(String orderId, String name, String businessNumber, String state, Long userId, Integer pageSize, Integer pageNum, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        // 处理参数信息
        SysTenantDef def = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        Boolean isTenant = false;
        if (def != null) {
            isTenant = true;
        }

        Long payObjId = 0L;
        Integer payUserType = 0;
        Long payTenantId = 0L;
        if (isTenant) {
            payObjId = def.getAdminUser();
            payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
            payTenantId = def.getId();
        } else {
            payObjId = loginInfo.getUserInfoId();
            payUserType = SysStaticDataEnum.USER_TYPE.RECEIVER_USER;
        }
        Page<PayoutInfoDto> page = new Page<>(pageNum, pageSize);
        List<String> busiIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(businessNumber)) {
            busiIds = Arrays.stream(businessNumber.split(",")).collect(Collectors.toList());
        }

        List<String> states = new ArrayList<>();
        if (StringUtils.isNotEmpty(state)) {
            states = Arrays.stream(state.split(",")).collect(Collectors.toList());
        }

        // 查询应付逾期订单信息
        Page<PayoutInfoDto> ipage = baseMapper.getOverdueCD(page, payObjId, userId, name, orderId, businessNumber, busiIds, isTenant, payTenantId, payUserType, state, states);
        List<PayoutInfoDto> list = ipage.getRecords();

        List<Long> busiIdList = new ArrayList<>();
        //查询是否有审核权限
        for (PayoutInfoDto dto : list) {
            busiIdList.add(dto.getFlowId());
        }

        // 权限信息
        Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
        Map<Long, Map<String, Object>> out = new HashMap<>();

        if (busiIdList != null && busiIdList.size() > 0) {
            hasPermissionMap = auditOutService.isHasPermission(accessToken, AuditConsts.AUDIT_CODE.PAY_CASH_CODE, busiIdList);
            out = auditOutService.queryAuditRealTimeOperation(loginInfo.getUserInfoId(), AuditConsts.AUDIT_CODE.PAY_CASH_CODE, busiIdList, loginInfo.getTenantId());
        }

        boolean isNeedPassword = false;
        String needPassword = "";
        if (isTenant) {
            // 是否需要输入密码
            needPassword = iPlatformPayService.doQueryCheckPasswordErr(accessToken);
        }

        for (PayoutInfoDto dto : list) {
            String respCode = dto.getRespCode();
            if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)) {
                dto.setRespCodeName("");
            } else if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
                dto.setRespCodeName("收款确认中");
            } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && dto.getBusiId() != EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD && !HttpsMain.respCodeCollection.equals(respCode)) {
                dto.setRespCodeName("线上打款中");
            } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && !HttpsMain.respCodeCollection.equals(respCode)) {
                dto.setRespCodeName("未提现");
            }

            if(userId != null && userId > 0){
                if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
                    dto.setStateName("确认中");
                } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && HttpsMain.respCodeCollection.equals(respCode)) {
                    dto.setStateName("车队已经打款");
                } else if (dto.getVerificationState() == 2) {
                    dto.setStateName("车队已经打款");
                } else {
                    dto.setStateName("已逾期");
                }
            }else {
                if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
                    dto.setStateName("确认中");
                } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && !HttpsMain.respCodeCollection.equals(respCode)) {
                    dto.setStateName("打款中");
                } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && HttpsMain.respCodeCollection.equals(respCode)) {
                    //dto.setStateName("未提现");
                    dto.setStateName("已打款");
                } else if (dto.getVerificationState() == 2) {
                    dto.setStateName("已打款");
                } else {
                    dto.setStateName("已逾期");
                }
            }

            boolean isTrue = false;
            if (hasPermissionMap.get(dto.getFlowId()) != null && hasPermissionMap.get(dto.getFlowId())) {
                isTrue = true;
            }

            if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) && dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0) && isTrue) {
                dto.setHasPermission(true); // 审核权限
            }
            if (needPassword.equals(SysStaticDataEnum.PWD_STATUS.PWD_STATUS2)) {//需要
                isNeedPassword = true;
            }

            if (out.get(dto.getFlowId()) != null) {
                Map<String, Object> p = out.get(dto.getFlowId());

                // 最后节点
                Boolean b = (Boolean) p.get("isFinallyNode");
                if (b) {
                    dto.setIsFinallyNode(true);
                } else {
                    dto.setIsFinallyNode(false);
                }
            }
            dto.setIsNeedPassword(isNeedPassword);

            if (dto.getBankType() != null && dto.getPayConfirm() != 0) {
                dto.setBankTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "BANK_TYPE_STATE", dto.getBankType() + "").getCodeName());
            }

            if (dto.getIsAutoMatic() != null && dto.getPayConfirm() != 0) {
                dto.setIsAutomaticName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "IS_AUTOMATIC", dto.getIsAutoMatic() + "").getCodeName());
            }

            if (dto.getIsNeedBill() != null) {
                dto.setIsNeedBillName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "FUNDS_IS_NEED_BILL", dto.getIsNeedBill() + "").getCodeName());
            }

            if (dto.getSourceRegion() != null) {
                dto.setSourceRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil, "SYS_CITY", dto.getSourceRegion() + "").getName());
            }

            if (dto.getDesRegion() != null) {
                dto.setDesRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil, "SYS_CITY", dto.getDesRegion() + "").getName());
            }

            if (respCode != null) {
                dto.setRespCodeName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "RESP_CODE_TYPE", respCode + ""));
            }

            if (dto.getBusiId() != null) {
                dto.setBusiIdName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "BUSINESS_NUMBER_TYPE", dto.getBusiId() + ""));
                if (dto.getSubjectsId() != null && dto.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                    dto.setBusiIdName("支付轮胎租赁费");
                }
            }
        }

        return ipage;
    }

    @Override
    public Long getOverdueCDSum(Long orderId, String name, String businessNumber, String state, Long userId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        // 处理参数信息
        SysTenantDef def = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        Boolean isTenant = false;
        if (def != null) {
            isTenant = true;
        }

        Long payObjId = 0L;
        Integer payUserType = 0;
        Long payTenantId = 0L;
        if (isTenant) {
            payObjId = def.getAdminUser();
            payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
            payTenantId = def.getId();
        } else {
            payObjId = loginInfo.getUserInfoId();
            payUserType = SysStaticDataEnum.USER_TYPE.RECEIVER_USER;
        }
        List<String> busiIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(businessNumber)) {
            busiIds = Arrays.stream(businessNumber.split(",")).collect(Collectors.toList());
        }

        List<String> states = new ArrayList<>();
        if (StringUtils.isNotEmpty(state)) {
            states = Arrays.stream(state.split(",")).collect(Collectors.toList());
        }

        Long sum = baseMapper.getOverdueCDSum(payObjId, userId, name, orderId, businessNumber, busiIds, isTenant, payTenantId, payUserType, state, states);

        return sum == null ? 0 : sum;
    }

    @Override
    public Long getOverdueSum(Long userId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        SysTenantDef def = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        Boolean isTenant = false;
        if (def != null) {
            isTenant = true;
        }

        Long payObjId = 0L;
        Integer payUserType = 0;
        Long payTenantId = 0L;
        if (isTenant) {
            payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
            payTenantId = def.getId();
        } else {
            payUserType = SysStaticDataEnum.USER_TYPE.RECEIVER_USER;
        }

        return baseMapper.getOverdueSum(userId, isTenant, payTenantId, payUserType);
    }

    @Override
    public List<WorkbenchDto> getTableFinancialPayablePaidAmount() {
        return baseMapper.getTableFinancialPayablePaidAmount();
    }

    @Override
    public List<WorkbenchDto> getTableFinancialPayableSurplusAmount() {
        return baseMapper.getTableFinancialPayableSurplusAmount();
    }

    @Override
    public List<WorkbenchDto> getTableFinancialOverdueReceivablesAmount() {
        return baseMapper.getTableFinancialOverdueReceivablesAmount();
    }

    @Override
    public List<WorkbenchDto> getTableFinancialOverduePayableAmount() {
        return baseMapper.getTableFinancialOverduePayableAmount();
    }

    @Override
    public List<WorkbenchDto> getTableFinancialPendingPaymentAmount() {
        return baseMapper.getTableFinancialPendingPaymentAmount();
    }

    @Override
    public List<WorkbenchDto> getTableBossPaymentReviewAmount() {
        return baseMapper.getTableBossPaymentReviewAmount();
    }

    @Override
    public List<PayableDayReportFinanceDto> getPayableDayReport() {
        return baseMapper.getPayableDayReport();
    }

    @Override
    public List<PayableMonthReportFinanceDto> getPayableMonthReport() {
        return baseMapper.getPayableMonthReport();
    }

    @Override
    public IPage<DueDateDetailsCDDto> getDueDateDetailsCDs(Long orderId, String name, String businessNumbers, String state, Long userId, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        SysTenantDef def = sysTenantDefService.getSysTenantDef(user.getTenantId());
        Boolean isTenant = false;
        if (def != null) {
            isTenant = true;
        }
        List<String> states = null;
        if (state != null) {
            states = Arrays.stream(state.split(",")).collect(Collectors.toList());
        }
        List<String> busiId = null;
        if (businessNumbers != null) {
            busiId = Arrays.stream(businessNumbers.split(",")).collect(Collectors.toList());
        }
        Page<DueDateDetailsCDDto> dueDateDetailsCDDtoPage = baseMapper.selectOr(orderId, name, isTenant, businessNumbers, user.getTenantId(), busiId, state, states, userId, new Page<>(pageNum, pageSize));

        List<PayoutInfoOutDto> infoOuts = new ArrayList<>();
        List<DueDateDetailsCDDto> list = dueDateDetailsCDDtoPage.getRecords();
        for (DueDateDetailsCDDto map : list) {
            PayoutInfoOutDto infoOut = new PayoutInfoOutDto();
            BeanUtil.copyProperties(map, infoOut);
            infoOut.setCreateDate(map.getCreateDate());
            if (infoOut.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && infoOut.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
                infoOut.setRespCodeName("车队已打款");
            } else if (infoOut.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1)) {
                infoOut.setRespCodeName("线上打款中");
            }
            if (infoOut.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)) {
                infoOut.setStateName("已确认线下收款");
            } else {
                infoOut.setStateName("已逾期");
            }

            infoOuts.add(infoOut);

        }
        Page page = new Page();
        page.setTotal(dueDateDetailsCDDtoPage.getTotal());
        page.setSize(dueDateDetailsCDDtoPage.getSize());
        page.setPages(dueDateDetailsCDDtoPage.getPages());
        page.setCurrent(dueDateDetailsCDDtoPage.getCurrent());
        page.setRecords(infoOuts);

        return page;
    }

    @Override
    public IPage<DueDateDetailsCDDto> getDueDateDetails(Long userId, Long orderId, String name, String businessNumbers, String state, Long sourceTenantId, String userType, Integer pageNum, Integer pageSize, String accessToken) {

        if (StringUtils.isNotBlank(businessNumbers) && "220000001".equals(businessNumbers)) {
            businessNumbers = "22000040";
        }

        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isBlank(userType)) {
            userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER + "";
        }

        if (sourceTenantId == null) {
            sourceTenantId = loginInfo.getTenantId();
        }
        if (userId < 0) {
            throw new BusinessException("未传入userId!");
        }

        List<String> states = null;
        if (state != null) {
            states = Arrays.stream(state.split(",")).collect(Collectors.toList());
        }
        List<String> busiId = null;
        if (businessNumbers != null) {
            busiId = Arrays.stream(businessNumbers.split(",")).collect(Collectors.toList());
            boolean contains = busiId.contains("220000001");
            if (contains) {
                busiId.add("22000040");
            }
        }

        Page<DueDateDetailsCDDto> dueDateDetailsCDDtoPage = baseMapper.selectOrS(userId, orderId, name, businessNumbers, state, states, sourceTenantId, userType, busiId, new Page<>(pageNum, pageSize));

        List<PayoutInfoOutDto> infoOuts = new ArrayList<>();
        List<DueDateDetailsCDDto> list = dueDateDetailsCDDtoPage.getRecords();
        for (DueDateDetailsCDDto map : list) {
            PayoutInfoOutDto infoOut = new PayoutInfoOutDto();
            BeanUtil.copyProperties(map, infoOut);
            String respCode = infoOut.getRespCode();
            infoOut.setCreateDate(map.getCreateDate());
            if (String.valueOf(EnumConsts.PAY_CONFIRM.recharge).equals(infoOut.getPayConfirm()) && String.valueOf(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0).equals(infoOut.getIsAutoMatic())) {
                infoOut.setRespCodeName("车队已打款");
            } else if (String.valueOf(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1).equals(infoOut.getIsAutoMatic()) && !HttpsMain.respCodeCollection.equals(respCode)) {
                infoOut.setRespCodeName("线上打款中");
            } else if (String.valueOf(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1).equals(infoOut.getIsAutoMatic()) && HttpsMain.respCodeCollection.equals(respCode)) {
                infoOut.setRespCodeName("未提现");
            }

            if (EnumConsts.PAY_CONFIRM.transfer == infoOut.getPayConfirm()) {
                infoOut.setStateName("已确认线下收款");
            } else if ("6".equals(infoOut.getRespCode()) || EnumConsts.PAY_CONFIRM.recharge == infoOut.getPayConfirm()) {
                infoOut.setStateName("车队已打款");
            } else {
//				infoOut.setStateName("已逾期");
                infoOut.setStateName("未打款");
            }
            if (StringUtils.isNotBlank(infoOut.getFileUrl())) {
                infoOut.setShowFile(true);
            } else {
                infoOut.setShowFile(false);
            }

            if (infoOut.getBusiId() != null) {
                infoOut.setBusiIdName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "BUSINESS_NUMBER_TYPE", infoOut.getBusiId() + ""));
                if (infoOut.getSubjectsId() != null && infoOut.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                    infoOut.setBusiIdName("支付轮胎租赁费");
                }
            }
            if (infoOut.getRespCode() != null) {
                infoOut.setRespCodeName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "RESP_CODE_TYPE", infoOut.getRespCode() + ""));
            }

            if (infoOut.getBankType() != null && infoOut.getPayConfirm() != 0) {
                infoOut.setBankTypeName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "BANK_TYPE_STATE", infoOut.getBankType() + ""));
            }
            if (infoOut.getIsAutoMatic() != null && infoOut.getPayConfirm() != 0) {
                infoOut.setIsAutomaticName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "IS_AUTOMATIC", infoOut.getIsAutoMatic() + ""));
            }
            if (infoOut.getIsNeedBill() != null) {
                infoOut.setIsNeedBillName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "FUNDS_IS_NEED_BILL", infoOut.getIsNeedBill() + ""));
            }

            if (infoOut.getSourceRegion() != null) {
                infoOut.setSourceRegionName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "SYS_CITY", infoOut.getSourceRegion() + ""));
            }
            if (infoOut.getDesRegion() != null) {
                infoOut.setDesRegionName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "SYS_CITY", infoOut.getDesRegion() + ""));
            }
            if (infoOut.getRespCode() != null) {
                infoOut.setRespCodeName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "RESP_CODE_TYPE", infoOut.getRespCode() + ""));
            }

            infoOuts.add(infoOut);

        }
        Page page = new Page();
        page.setCurrent(dueDateDetailsCDDtoPage.getCurrent());
        page.setPages(dueDateDetailsCDDtoPage.getPages());
        page.setTotal(dueDateDetailsCDDtoPage.getTotal());
        page.setRecords(infoOuts);
        return page;
    }


    @Override
    public Long getDueDateDetailsSum(Long userId, Long orderId, String name, String businessNumbers, String state, Long sourceTenantId, String userType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isBlank(userType)) {
            userType = SysStaticDataEnum.USER_TYPE.DRIVER_USER + "";
        }

        if (userId < 0) {
            throw new BusinessException("未传入userId!");
        }

        List<String> states = null;
        if (state != null) {
            states = Arrays.stream(state.split(",")).collect(Collectors.toList());
        }
        List<String> busiId = null;
        if (businessNumbers != null) {
            busiId = Arrays.stream(state.split(",")).collect(Collectors.toList());
        }

        Long sum = baseMapper.selectOrSsum(userId, orderId, name, businessNumbers, state, states, sourceTenantId, userType, busiId);

        return sum == null ? 0L : sum;
    }


    /**
     * 计算 预付-尾款账期 应收日期
     *
     * @param data 审核通过时间 yyyy-mm-dd
     * @param num  收款期限
     * @return 收款期限几天后的时间 yyyy-mm-dd
     */
    private String getAccountPeriodDateStr(String data, Integer num) {
        String[] split = data.split("-");

        Integer year = Integer.valueOf(split[0]);
        Integer month = Integer.valueOf(split[1]);
        Integer day = Integer.valueOf(split[2]);

        Calendar cld = Calendar.getInstance();
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONDAY, month);
        cld.set(Calendar.DATE, day);

        //调用Calendar类中的add()，增加时间量
        cld.add(Calendar.DATE, num);

        return cld.get(Calendar.YEAR) + "-" + cld.get(Calendar.MONTH) + "-" + cld.get(Calendar.DATE);
    }

    /**
     * 计算 预付 尾款月结 应收日期
     *
     * @param data            订单可靠时间 yyyy-mm-dd
     * @param collectionMonth 收款月（几个月以后）
     * @param collectionDay   收款天（几个月后的几号）
     * @return 收款期限几天后的时间 yyyy-mm-dd
     */
    private String getMonthlyDateStr(String data, Integer collectionMonth, Integer collectionDay) {
        String[] split = data.split("-");

        Integer year = Integer.valueOf(split[0]);
        Integer month = Integer.valueOf(split[1]);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONDAY, month-1);


        //调用Calendar类中的add()，增加时间量
        cld.add(Calendar.MONDAY, collectionMonth);
        cld.set(Calendar.DAY_OF_MONTH, collectionDay);
        return cld.get(Calendar.YEAR) + "-" + (cld.get(Calendar.MONTH)+1) + "-" + cld.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return true 前面的时间大于后面的，false 前面小于后面的
     */
    private Boolean equalTwoTimeStr(String dateStr) {
        if (dateStr.length() != 10) {
            String[] split = dateStr.split("-");
            dateStr = split[0];
            if (split[1].length() != 2) {
                dateStr += "-0" + split[1];
            } else {
                dateStr += "-" + split[1];
            }
            if (split[2].length() != 2) {
                dateStr += "-0" + split[2];
            } else {
                dateStr += "-" + split[2];
            }
        }
        //  System.out.println(dateStr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(new Date());
        int dateFlag = dateNowStr.compareTo(dateStr);
        return dateFlag > 0;
    }

    public static IPage listToPage(List list, int pageNum, int pageSize) {
        List pageList = new ArrayList<>();
        int curIdx = pageNum > 1 ? (pageNum - 1) * pageSize : 0;
        for (int i = 0; i < pageSize && curIdx + i < list.size(); i++) {
            pageList.add(list.get(curIdx + i));
        }
        IPage page = new Page<>(pageNum, pageSize);
        page.setRecords(pageList);
        page.setTotal(list.size());
        return page;
    }

    public static void main(String[] args) throws  Exception{
//        Date dateAdd = DateUtil.addMonth(new Date(), 1);
//        Date dateEnd = DateUtil.formatStringToDate(DateUtil.formatDate(dateAdd, "yyyy-MM-32" + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
//        int paymentDay = DateUtil.diffDate(dateEnd, new Date());
//        // 计划处理时间(尾款到期时间):签收时间+帐期
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(TimeUtil.getDataTime());
//        cal.add(Calendar.DATE, Integer.valueOf(paymentDay));
//        System.out.println(DateUtil.formatDate(cal.getTime(),DateUtil.DATETIME_FORMAT));
//        PayoutIntfThreeServiceImpl payoutIntfThreeService = new PayoutIntfThreeServiceImpl();
//        System.out.println(payoutIntfThreeService.getMonthlyDateStr("2022-07-11",1,31));
        //System.out.println(payoutIntfThreeService.getAccountPeriodDateStr("2022-07-04", 0));
        PayoutIntfThreeServiceImpl payoutIntfThreeService = new PayoutIntfThreeServiceImpl();
        System.out.println(payoutIntfThreeService.equalTwoTimeStr("2022-07-13"));
    }

}
