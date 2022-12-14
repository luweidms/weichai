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
     * ????????????(????????????)
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

        // ????????????
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

        // ????????????????????????
        List<MunualPaymentInfo> payOutIntfsList = baseMapper.getPayOutIntfsList(queryPayoutIntfsVo);
        Integer payOutIntfsCount = baseMapper.getPayOutIntfsCount(queryPayoutIntfsVo);
        List<Long> flowIds = new ArrayList<>();
        for (MunualPaymentInfo munualPaymentInfo : payOutIntfsList) {
            flowIds.add(munualPaymentInfo.getFlowId());
        }

        Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
        Map<Long, Map<String, Object>> out = new HashMap<>();

        if (flowIds != null && flowIds.size() > 0) {
            // ?????????????????????
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

            //???????????????????????????
            if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) && munualPaymentInfo.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0) && isTrue) {
                munualPaymentInfo.setHasPermission(true);
            }
            if (munualPaymentInfo.getUserType().equals(SysStaticDataEnum.USER_TYPE.RECEIVER_USER) || munualPaymentInfo.getSubjectsId() == EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB || munualPaymentInfo.getSubjectsId() == EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB) {
                munualPaymentInfo.setVirtualState(SysStaticDataEnum.VIRTUAL_TENANT_STATE.IS_VIRTUAL);
            }
            if (munualPaymentInfo.getBusiId() == EnumConsts.PayInter.PAY_FOR_REPAIR && munualPaymentInfo.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                munualPaymentInfo.setBusiIdName("?????????????????????");
            }


            if (munualPaymentInfo.getVehicleAffiliation() != null && munualPaymentInfo.getVehicleAffiliation() > 10
                    && munualPaymentInfo.getSubjectsId() != EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB
                    && munualPaymentInfo.getSubjectsId() != EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB) {
                // ???????????????
                Map feeMap = this.doQueryServiceFee(accessToken, munualPaymentInfo.getTxnAmt(), munualPaymentInfo.getFlowId());
                String serviceFee = DataFormat.getStringKey(feeMap, "ServiceFee");
                munualPaymentInfo.setBillServiceFeeDouble(CommonUtil.getDoubleByObject(serviceFee));
            }


            if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) && munualPaymentInfo.getIsAutoMatic().equals(AUTOMATIC0)) {
                munualPaymentInfo.setPayConfirmName("?????????");
            } else if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && munualPaymentInfo.getIsAutoMatic().equals(AUTOMATIC0)) {
                munualPaymentInfo.setPayConfirmName("?????????");
            } else if (munualPaymentInfo.getIsAutoMatic().equals(AUTOMATIC1) && !Objects.equals(respCode, HttpsMain.respCodeCollection)) {
                munualPaymentInfo.setPayConfirmName("?????????");
            } else if (munualPaymentInfo.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)) {
                munualPaymentInfo.setPayConfirmName("?????????");
            } else if (munualPaymentInfo.getIsAutoMatic().equals(AUTOMATIC1) && Objects.equals(respCode, HttpsMain.respCodeCollection)) {
                //munualPaymentInfo.setPayConfirmName("?????????");
                munualPaymentInfo.setPayConfirmName("?????????");
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
                    munualPaymentInfo.setBusiIdName("?????????????????????");
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
            throw new BusinessException("?????????????????????");
        }

        PayoutIntf payout = baseMapper.selectById(flowId);
        Long openUserId = Long.valueOf(payout.getVehicleAffiliation());

        //??????????????????????????????????????????????????????
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
            throw new BusinessException("?????????????????????");
        }

        // ??????????????????????????????
        BillSetting billSetting = billSettingService.getBillSetting(tenantId);
        if (billSetting == null) {
            throw new BusinessException("????????????ID: " + tenantId + " ???????????????????????????");
        }
        if (billSetting.getRateId() == null || billSetting.getRateId() <= 0) {
            throw new BusinessException("????????????ID: " + tenantId + " ?????????????????????????????????");
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        // ??????????????????????????????????????????,??????????????????????????????
        BillPlatform bpf = billPlatformService.queryAllBillPlatformByUserId(openUserId);

        if (bpf == null) {
            throw new BusinessException("????????????????????????id???" + openUserId + " ???????????????????????????");
        }
        if (bpf.getServiceFeeFormula() == null) {
            throw new BusinessException("????????????????????????id??? " + openUserId + " ?????????????????????????????????");
        }

        // ?????????????????????  ( d + o + e ) * ( 1 - r ) - o * 8.5% - e * 1.5% - ( d + o +e)
        String tempFormula = bpf.getServiceFeeFormula();
        String formula = tempFormula.replace("???", "(").replace("???", ")").replace("%", "/100.0");
        // ????????????  [t/(1+10%)*10% - o/(1+16%)*16% - e/(1+3%)*3% - s/(1+16%)*16%]*(1-45%)+ m+a
        String tempHaCostFormula = bpf.getHaCost();
        String haCostFormula = tempHaCostFormula.replace("???", "(").replace("???", ")").replace("%", "/100.0");

        // ?????????
        Double billRate = rateService.getRateValue(billSetting.getRateId(), cash);

        if (billRate == null) {
            throw new BusinessException("??????????????????id??? " + billSetting.getRateId() + " ????????????" + (double) cash / 100 + " ?????????????????????");
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

            // ???????????????
            serviceFee = (Double) engine.eval(formula);

            // ??????????????????
            serviceCost = (Double) engine.eval(haCostFormula);
        } catch (ScriptException ex) {
            throw new BusinessException("ScriptEngine????????????");
        }
        long billServiceFee = Math.round(serviceFee);//serviceFee.longValue();//??????
        long billServiceCost = serviceCost.longValue();//??????
        long lugeBillServiceFee = Math.round(serviceFee);//????????????
        long lugeBillServiceCost = Math.round(serviceCost);//????????????
        //56k??????????????????????????????????????????????????????????????????123.678??????56K: 123???????????????124???
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
            // ???????????????
            if (OrderConsts.PayOutVerificationState.INIT == Integer.valueOf(munualPaymentSumInfo.getVerificationState())) {
                maps.put("noVerificatMoney", sumMoney.longValue()); // ??????????????????
            }

            // ???????????????
            if (VERIFICATION_STATE == Integer.valueOf(munualPaymentSumInfo.getVerificationState()) || OrderConsts.PayOutVerificationState.VERIFICATION_STATE_5 == Integer.valueOf(munualPaymentSumInfo.getVerificationState())) {
                if (maps.get("verificatMoney") != null) {
                    sumMoney = sumMoney + (Long) maps.get("verificatMoney");
                }
                maps.put("verificatMoney", sumMoney.longValue()); // ???????????????
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
                    "??????", "????????????", "?????????",
                    "????????????", "????????????", "????????????",
                    "??????????????????", "????????????", "????????????",
                    "????????????", "????????????", "????????????",
                    "????????????", "????????????", "???????????????",
                    "????????????", "???????????????", "????????????",
                    "????????????", "????????????", "???????????????",
                    "????????????", "??????"};
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
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
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
        LOGGER.info("????????????1???"+queryDouOverdueVo.getPayConFirm());
        LOGGER.info("????????????2???"+queryDouOverdueVo.getType());
        if(StringUtils.isNotEmpty(queryDouOverdueVo.getPayConFirm())){
            queryDouOverdueVo.setPayConFirms(Arrays.asList(queryDouOverdueVo.getPayConFirm().split(",")));
        }
        if(StringUtils.isNotEmpty(queryDouOverdueVo.getType())){
            queryDouOverdueVo.setTypes(Arrays.asList(queryDouOverdueVo.getType().split(",")));
        }
        List<OverdueReceivableDto> lists = baseMapper.doQueryDouOverdues(queryDouOverdueVo, tenantId);

        List<OverdueReceivableDto> infoOuts = new ArrayList<>();

        for (OverdueReceivableDto dto : lists) {
            // ????????????????????????
            if (dto.getType() == 1) {
                if (dto.getBalanceType() == 1) { // ????????????
                    if (StringUtils.isBlank(dto.getCreateTime())) {
                        continue;
                    }
                    dto.setReceivableDate(dto.getCreateTime());
                } else if (dto.getBalanceType() == 2) { // ?????? ????????????
                    if (StringUtils.isNotBlank(dto.getUpdateTime())) {
                        dto.setReceivableDate(getAccountPeriodDateStr(dto.getUpdateTime(), dto.getCollectionTime()));
                    } else {
                        continue;
                    }

                } else if (dto.getBalanceType() == 3) { // ?????? ????????????
                    if (StringUtils.isNotBlank(dto.getCarDependDate())) {
                        dto.setReceivableDate(getMonthlyDateStr(dto.getCarDependDate(), dto.getCollectionMonth(), dto.getCollectionDay()));
                    } else {
                        continue;
                    }

                }
            } else {
                // ????????????????????????
                if (StringUtils.isNotBlank(dto.getUpdateTime())) {
                    //????????????????????????
                    dto.setReceivableDate(dto.getUpdateTime());
                } else {
                    continue;
                }
//                if(queryDouOverdueVo.getUserId() != null && queryDouOverdueVo.getUserId() > 0){
//                    //?????????????????????
//                    dto.setTxnAmt(dto.getTxnAmt() - dto.getPaid());
//                    dto.getTxnAmt();
//                }
            }
            // ?????????????????????????????????????????????
            if (!equalTwoTimeStr(dto.getReceivableDate())) {
                continue;
            }
            if (dto.getType().equals(1)) {
                dto.setStateName("????????????");
            } else if (dto.getType().equals(2)) {
                dto.setStateName("????????????");
            } else if (dto.getType().equals(3)) {
                dto.setStateName("????????????");
            } else {
                dto.setStateName("????????????");
            }
            if (dto.getPayConfirm().equals(0)) {
                dto.setPayConfirmName("?????????");
            } else if (dto.getPayConfirm().equals(1)) {
                dto.setPayConfirmName("???????????????");
            } else if (dto.getPayConfirm().equals(3)) {
                dto.setPayConfirmName("???????????????");
            } else {
                dto.setPayConfirmName("?????????");
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
//            throw new BusinessException("?????????????????????????????????!");
//        }
//        if (payoutIntf.getVehicleAffiliation() != null && payoutIntf.getVehicleAffiliation().equals(OrderConsts.PayOutVerificationState.VERIFICATION_STATE)) {
//            throw new BusinessException("????????????????????????????????????????????????!");
//        }
//        if (payoutIntf.getSubjectsId() != null && payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS50044) {
//            throw new BusinessException("???????????????????????????!");
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
//        payoutIntf.setRespMsg("??????????????????");
//        payoutIntf.setRemark("??????????????????");
//        payoutIntf.setCompleteTime(datestr);
//        this.saveOrUpdate(payoutIntf);
//        //????????????
//        this.callBack(payoutIntf, accessToken);
//        if (payoutIntf.getPayTenantId() > 0) {
//            Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), payoutIntf.getPayTenantId());
//            if (b) {
//                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), null, AuditConsts.RESULT.SUCCESS, null, AuditConsts.OperType.TASK, payoutIntf.getPayTenantId(), accessToken);
//            }
//        }
        //??????
        try {
            QueryDouOverdueVo queryDouOverdueVo = new QueryDouOverdueVo();
            queryDouOverdueVo.setFlowId(flowId);
            //OverdueReceivable overdueReceivable = new OverdueReceivable();
            List<OverdueReceivableDto> list = baseMapper.doQueryDouOverdues(queryDouOverdueVo, loginInfo.getTenantId());
            if (list == null || list.size() <= 0) {
                throw new BusinessException("????????????????????????????????????????????????????????????");
            }
            if (list.get(0).getType() == 1) {
                //????????????
                if (list.get(0).getOrderId() != null) {
                    confirmPaymentWriteOffOrderId(String.valueOf(list.get(0).getOrderId()), String.valueOf(list.get(0).getTxnAmt() - list.get(0).getPaid()), accessToken);
                }

            } else {
                //??????
                String businessNumber = list.get(0).getBusinessNumber();
                if (StringUtils.isEmpty(businessNumber)) {
                    throw new BusinessException("????????????????????????????????????????????????????????????");
                }
                OaLoan oaLoan = iOaLoanService.selectByNumber(businessNumber,list.get(0).getUserId());
                Long billAmount = list.get(0).getTxnAmt() - list.get(0).getPaid();
                String billAmountStr = "";
                if (billAmount != null) {
                    billAmountStr = CommonUtil.divide(billAmount);
                }
                if (oaLoan != null) {
                    iOaLoanService.verificationOaLoanCar(String.valueOf(oaLoan.getId()), "0", billAmountStr, null, null, "????????????-?????????", 0, accessToken);
                }

            }
            String msg = "[" + loginInfo.getName() + "]" + "????????????";
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
            throw new BusinessException("?????????????????????????????????!");
        }
        if (payoutIntf.getVehicleAffiliation() != null && payoutIntf.getVehicleAffiliation().equals(OrderConsts.PayOutVerificationState.VERIFICATION_STATE)) {
            throw new BusinessException("????????????????????????????????????????????????!");
        }
        if (payoutIntf.getSubjectsId() != null && payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS50044) {
            throw new BusinessException("???????????????????????????!");
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
        payoutIntf.setRespMsg("??????????????????");
        payoutIntf.setRemark("??????????????????");
        payoutIntf.setCompleteTime(datestr);
        this.saveOrUpdate(payoutIntf);
        //????????????
        this.callBack(payoutIntf, accessToken);
        if (payoutIntf.getPayTenantId() > 0) {
            Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), payoutIntf.getPayTenantId());
            if (b) {
                auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), null, AuditConsts.RESULT.SUCCESS, null, AuditConsts.OperType.TASK, payoutIntf.getPayTenantId(), accessToken);
            }
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????
     * 1?????????????????????
     * ??????????????????????????????????????????
     * 2?????????????????????
     * ????????????
     */
    private void confirmPaymentWriteOffOrderId(String orderId, String fee, String accessToken) {
        // ?????????????????????
        String billNumber = orderFeeStatementHService.judgeDoesItExistOrderId(orderId);
        if (StringUtils.isEmpty(billNumber)) { // ?????????
            // ????????????
            billNumber = iOrderBillInfoService.createBill(orderId, accessToken);
        }
        // ?????????????????? ????????????????????? ????????????????????????????????????????????????
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

        // ????????????
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
//            throw new BusinessException("????????????????????????????????????!");
//        }
        if (PAY_FOR_OIL_CODE == payoutIntf.getBusiId()
                || (PAY_FOR_REPAIR == payoutIntf.getBusiId()
                && EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 != payoutIntf.getSubjectsId()
                && EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 != payoutIntf.getSubjectsId())
                || SUBJECTIDS50070 == payoutIntf.getSubjectsId()
                || BALANCE_CONSUME_OIL_SUB == payoutIntf.getSubjectsId()
                || BALANCE_PAY_REPAIR == payoutIntf.getSubjectsId()) {//?????????????????????
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
            throw new BusinessException("????????????????????????");
        }
        //????????????????????????
        UserDataInfo payUserDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getPayObjId());
        if (null == payUserDataInfo) {
            throw new BusinessException("???????????????????????????,FLOW_ID : " + payoutIntf.getId());
        }
        //????????????????????????
        UserDataInfo receivablesUserDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        if (null == receivablesUserDataInfo) {
            throw new BusinessException("???????????????????????????,FLOW_ID : " + payoutIntf.getId());
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

        //1.???????????????
        if (BEFORE_PAY_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (payoutIntf.getSubjectsId() == BEFORE_RECEIVABLE_OVERDUE_SUB) {
                iOrderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(), payoutIntf.getTxnAmt(), accessToken);
                //20191015 56K????????????
                if (payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight() > 0) {
                    this.dealAdditionalFee(payoutIntf);
                }
            }
        }
        //2.??????(???????????????????????????)??????
        else if (PAYFOR_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (payoutIntf.getSubjectsId() == FINAL_PAYFOR_RECEIVABLE_IN) {
                iOrderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(), payoutIntf.getTxnAmt(), accessToken);
            }
        }
        //2.????????????
        else if (ADVANCE_PAY_MARGIN_CODE == payoutIntf.getBusiId()) {
            //20190815?????? ???????????????????????????
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (null == payoutIntf.getOrderId() || payoutIntf.getOrderId() <= 0) {
                this.doOrderLimtByFlowId(payoutIntf);
            }
        }
        //3.????????????  4.ETC?????????
        else if (OIL_AND_ETC_TURN_CASH == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //5.????????????
        else if (EnumConsts.PayInter.OA_LOAN_AVAILABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
            iBaseBusiToOrderService.dealBusiOaLoanAvailable(payoutIntfDto, userDataInfoArr, accessToken);
        }
        //6.????????????
        else if (DRIVER_EXPENSE_ABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //7.????????????
        else if (OA_LOAN_AVAILABLE_TUBE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
            iOaLoanService.setPayFlowIdAfterPay(payoutIntf.getId());
        }
        //8.????????????
        else if (TUBE_EXPENSE_ABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //9.????????????
        else if (CAR_DRIVER_SALARY == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //10.????????????
        else if (EnumConsts.PayInter.EXCEPTION_FEE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //11.????????????
        else if (EnumConsts.PayInter.EXCEPTION_FEE_OUT == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, accessToken);
        }
        //12.??????
        else if (CANCEL_THE_ORDER == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, accessToken);
        }

        //13.ETC????????????
        else if (CONSUME_ETC_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, accessToken);
        }

        //14.????????????(??????)
        else if (PLEDGE_RELEASE_OILCARD == payoutIntf.getBusiId()) {
            if (OIL_CARD_RELEASE_RECEIVABLE_IN == payoutIntf.getSubjectsId()) {
                iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else {
                //???????????????????????????????????????
                iOilCardManagementService.releaseOilCardByOrderId(payoutIntf.getOrderId(), payoutIntf.getTenantId(), accessToken);
                //???????????????????????????????????????
                iOilCardManagementService.pledgeOrReleaseOilCardAmount(payoutIntf.getPayObjId(), payoutIntf.getVehicleAffiliation(), payoutIntf.getTxnAmt(), payoutIntf.getOrderId(), payoutIntf.getTenantId(), 1, loginInfo, accessToken);
            }
        } else if (UPDATE_THE_ORDER == payoutIntf.getBusiId()) {
            if (payoutIntf.getIsDriver() == HAVIR) {
                iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else {
                iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, accessToken);
            }

        }
        //???????????????
        else if (BILL_56K_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId() || BILL_SERVICE_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //????????????????????????
        else if (RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId()) {
            isOnline = iVoucherInfoService.judgeRechargeIsNeedWithdrawal(payoutIntf.getBusiCode(), accessToken);
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //???????????????
        else if (EnumConsts.PayInter.PAY_ARRIVE_CHARGE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //??????ETC??????
        else if (SUBJECTIDS2302 == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //???????????????????????????
        else if (EnumConsts.PayInter.ACCOUNT_STATEMENT == payoutIntf.getBusiId()) {
            //?????????????????????
            if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE == payoutIntf.getSubjectsId()) {
                iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == payoutIntf.getSubjectsId()) {//????????????
                iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, false, accessToken);
            }
        }
        //????????????
        else if (EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, false, accessToken);
        }
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        else if (EnumConsts.SubjectIds.SUBJECTIDS1131 == payoutIntf.getSubjectsId()) {//???????????????
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //??????????????????????????? 20190820
        else if (EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, true, accessToken);
        }
        //??????????????????????????? 20191125
        else if (EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 == payoutIntf.getSubjectsId()) {
            TyreSettlementBill tyreSettlementBill = iTyreSettlementBillService.getTyreSettlementBillVOByBusiCode(payoutIntf.getBusiCode());
            if (isOnline) {
                iTyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBill.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "????????????", accessToken);
            } else {
                iTyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBill.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "????????????", accessToken);
            }
            tyreSettlementBill.setPayClass(isOnline ? IS_TURN_AUTOMATIC_1 : IS_TURN_AUTOMATIC_0);
            iTyreSettlementBillService.saveOrUpdate(tyreSettlementBill);
            iBaseBusiToOrderService.dealOABusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        } else if (EnumConsts.SubjectIds.SUBJECTIDS1900 == payoutIntf.getSubjectsId()) {//?????????56K????????????????????????????????????
            String sysParame56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K + "").getCodeName();
            String sysPre = billPlatformService.getPrefixByUserId(payoutIntf.getPayObjId());
            if (sysParame56K.equals(sysPre)) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);
            }
        }
        //?????????????????????????????????????????????????????????????????????
        else {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, EnumConsts.PAY_OUT_OPER.ORDER, accessToken);
            if (SUBJECTIDS1816 == payoutIntf.getSubjectsId() && isOnline) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
            }
        }

        if (payoutIntf.getIsTurnAutomatic() != null && payoutIntf.getIsTurnAutomatic() == OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_3) {//??????????????????
            //???????????????????????????????????????300?????????????????????????????????????????????
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
//            throw new BusinessException("????????????????????????????????????!");
//        }
        if (PAY_FOR_OIL_CODE == payoutIntf.getBusiId()
                || (PAY_FOR_REPAIR == payoutIntf.getBusiId()
                && EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 != payoutIntf.getSubjectsId()
                && EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 != payoutIntf.getSubjectsId())
                || SUBJECTIDS50070 == payoutIntf.getSubjectsId()
                || BALANCE_CONSUME_OIL_SUB == payoutIntf.getSubjectsId()
                || BALANCE_PAY_REPAIR == payoutIntf.getSubjectsId()) {//?????????????????????
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
            throw new BusinessException("????????????????????????");
        }
        //????????????????????????
        UserDataInfo payUserDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getPayObjId());
        if (null == payUserDataInfo) {
            throw new BusinessException("???????????????????????????,FLOW_ID : " + payoutIntf.getId());
        }
        //????????????????????????
        UserDataInfo receivablesUserDataInfo = iUserDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        if (null == receivablesUserDataInfo) {
            throw new BusinessException("???????????????????????????,FLOW_ID : " + payoutIntf.getId());
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

        //1.???????????????
        if (BEFORE_PAY_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (payoutIntf.getSubjectsId() == BEFORE_RECEIVABLE_OVERDUE_SUB) {
                iOrderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(), payoutIntf.getTxnAmt(), accessToken);
                //20191015 56K????????????
                if (payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight() > 0) {
                    this.dealAdditionalFee(payoutIntf);
                }
            }
        }
        //2.??????(???????????????????????????)??????
        else if (PAYFOR_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (payoutIntf.getSubjectsId() == FINAL_PAYFOR_RECEIVABLE_IN) {
                iOrderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(), payoutIntf.getTxnAmt(), accessToken);
            }
        }
        //2.????????????
        else if (ADVANCE_PAY_MARGIN_CODE == payoutIntf.getBusiId()) {
            //20190815?????? ???????????????????????????
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            if (null == payoutIntf.getOrderId() || payoutIntf.getOrderId() <= 0) {
                this.doOrderLimtByFlowId(payoutIntf);
            }
        }
        //3.????????????  4.ETC?????????
        else if (OIL_AND_ETC_TURN_CASH == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //5.????????????
        else if (EnumConsts.PayInter.OA_LOAN_AVAILABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
            iBaseBusiToOrderService.dealBusiOaLoanAvailable(payoutIntfDto, userDataInfoArr, accessToken);
        }
        //6.????????????
        else if (DRIVER_EXPENSE_ABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //7.????????????
        else if (OA_LOAN_AVAILABLE_TUBE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
            iOaLoanService.setPayFlowIdAfterPay(payoutIntf.getId());
        }
        //8.????????????
        else if (TUBE_EXPENSE_ABLE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //9.????????????
        else if (CAR_DRIVER_SALARY == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //10.????????????
        else if (EnumConsts.PayInter.EXCEPTION_FEE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //11.????????????
        else if (EnumConsts.PayInter.EXCEPTION_FEE_OUT == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, accessToken);
        }
        //12.??????
        else if (CANCEL_THE_ORDER == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, accessToken);
        }

        //13.ETC????????????
        else if (CONSUME_ETC_CODE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, accessToken);
        }

        //14.????????????(??????)
        else if (PLEDGE_RELEASE_OILCARD == payoutIntf.getBusiId()) {
            if (OIL_CARD_RELEASE_RECEIVABLE_IN == payoutIntf.getSubjectsId()) {
                iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else {
                //???????????????????????????????????????
                iOilCardManagementService.releaseOilCardByOrderId(payoutIntf.getOrderId(), payoutIntf.getTenantId(), accessToken);
                //???????????????????????????????????????
                iOilCardManagementService.pledgeOrReleaseOilCardAmount(payoutIntf.getPayObjId(), payoutIntf.getVehicleAffiliation(), payoutIntf.getTxnAmt(), payoutIntf.getOrderId(), payoutIntf.getTenantId(), 1, loginInfo, accessToken);
            }
        } else if (UPDATE_THE_ORDER == payoutIntf.getBusiId()) {
            if (payoutIntf.getIsDriver() == HAVIR) {
                iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else {
                iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, accessToken);
            }

        }
        //???????????????
        else if (BILL_56K_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId() || BILL_SERVICE_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //????????????????????????
        else if (RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId()) {
            isOnline = iVoucherInfoService.judgeRechargeIsNeedWithdrawal(payoutIntf.getBusiCode(), accessToken);
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //???????????????
        else if (EnumConsts.PayInter.PAY_ARRIVE_CHARGE == payoutIntf.getBusiId()) {
            iBaseBusiToOrderService.dealBusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
        }
        //??????ETC??????
        else if (SUBJECTIDS2302 == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //???????????????????????????
        else if (EnumConsts.PayInter.ACCOUNT_STATEMENT == payoutIntf.getBusiId()) {
            //?????????????????????
            if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE == payoutIntf.getSubjectsId()) {
                iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10, isOnline, accessToken);
            } else if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == payoutIntf.getSubjectsId()) {//????????????
                iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, false, accessToken);
            }
        }
        //????????????
        else if (EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, false, accessToken);
        }
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        else if (EnumConsts.SubjectIds.SUBJECTIDS1131 == payoutIntf.getSubjectsId()) {//???????????????
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        }
        //??????????????????????????? 20190820
        else if (EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 == payoutIntf.getSubjectsId()) {
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, true, accessToken);
        }
        //??????????????????????????? 20191125
        else if (EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 == payoutIntf.getSubjectsId()) {
            TyreSettlementBill tyreSettlementBill = iTyreSettlementBillService.getTyreSettlementBillVOByBusiCode(payoutIntf.getBusiCode());
            if (isOnline) {
                iTyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBill.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "????????????", accessToken);
            } else {
                iTyreSettlementBillService.updTyreSettlementBillState(tyreSettlementBill.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "????????????", accessToken);
            }
            tyreSettlementBill.setPayClass(isOnline ? IS_TURN_AUTOMATIC_1 : IS_TURN_AUTOMATIC_0);
            iTyreSettlementBillService.saveOrUpdate(tyreSettlementBill);
            iBaseBusiToOrderService.dealOABusiTwo(payoutIntfDto, userDataInfoArr, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5, isOnline, accessToken);
        } else if (EnumConsts.SubjectIds.SUBJECTIDS1900 == payoutIntf.getSubjectsId()) {//?????????56K????????????????????????????????????
            String sysParame56K = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K + "").getCodeName();
            String sysPre = billPlatformService.getPrefixByUserId(payoutIntf.getPayObjId());
            if (sysParame56K.equals(sysPre)) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);
            }
        }
        //?????????????????????????????????????????????????????????????????????
        else {
            iBaseBusiToOrderService.dealBusi(payoutIntfDto, userDataInfoArr, EnumConsts.PAY_OUT_OPER.ORDER, accessToken);
            if (SUBJECTIDS1816 == payoutIntf.getSubjectsId() && isOnline) {
                iBaseBusiToOrderService.doSavePayoutIntf(payoutIntfDto, OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
            }
        }

        if (payoutIntf.getIsTurnAutomatic() != null && payoutIntf.getIsTurnAutomatic() == OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_3) {//??????????????????
            //???????????????????????????????????????300?????????????????????????????????????????????
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
            throw new BusinessException("????????????");
        }
        if (pay.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB) {
            if (pay.getAppendFreight() != null && pay.getAppendFreight() > 0) {
                AdditionalFee af = iAdditionalFeeService.getAdditionalFeeByOrderId(pay.getOrderId());
                if (af == null) {
                    throw new BusinessException("??????????????????" + pay.getOrderId() + " ???????????????????????????" + pay.getId() + "????????????????????????");
                }
                if (pay.getIsAutomatic() == AUTOMATIC1) {
                    af.setState(OrderAccountConst.ADDITIONAL_FEE.STATE2);
                } else {
                    af.setState(OrderAccountConst.ADDITIONAL_FEE.STATE4);
                    af.setDealState(OrderAccountConst.ADDITIONAL_FEE_DEAL_STATE.STATE3);
                    af.setDealRemark("????????????_??????");
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
                    throw new BusinessException("??????????????????");
                }
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(payoutOrder.getAmount());//???????????????????????????
                off.setOrderId(payoutOrder.getOrderId());//??????ID
                off.setUserId(payoutOrder.getUserId());
                off.setBusinessId(payoutIntf.getBusiId());//????????????
                off.setBusinessName(getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(off.getBusinessId())).getCodeName());//????????????
                off.setBookTypeName(getSysStaticData("ACCOUNT_BOOK_TYPE", String.valueOf(off.getBookType())).getCodeName());//????????????
                off.setSubjectsId(payoutIntf.getSubjectsId());//??????ID
                //			off.setSubjectsName(rel.getSubjectsName());//????????????
                off.setBusiTable("ACCOUNT_DETAILS");//???????????????
                off.setBusiKey(payoutOrder.getBatchId());//????????????ID
                off.setTenantId(payoutIntf.getTenantId());
                off.setInoutSts("out");//????????????:???in???out???io
                //???????????????
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
            //??????????????????
            if (SUBJECTIDS50070 == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    //?????????????????????????????????
                    PayoutIntf newPayoutIntf = this.dealPayServiceFee(payout, accessToken);
                }
            }//?????????????????????
            else if (SUBJECTIDS1800 == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    //?????????????????????????????????
                    this.dealPaySuccess(payout);
                }
            } else if (SUBJECTIDS1816 == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iPayManagerService.updatePayManagerState(payout.getId());
                }
            } else if (BILL_SERVICE_RECEIVABLE_OVERDUE_SUB == payout.getSubjectsId().longValue() || BILL_56K_RECEIVABLE_OVERDUE_SUB == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iApplyOpenBillService.payForBillServiceWriteBack(payout, accessToken);
                }
            } else if (SUBJECTIDS2302 == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iEtcBillInfoService.payForEtcBillWriteBack(payout);
                }
            } else if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iAccountStatementService.payForCarFeeWriteBack(payout.getBusiCode());
                }
            } else if (EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
                    BeanUtil.copyProperties(payout, payoutIntfDto);
                    iBusiSubjectsRelService.paySucessOilCallBack(payoutIntfDto);
                    //????????????????????????
                    iTenantServiceRelDetailsService.oilExpireWriteBack(payout);
                }
            } else if (EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iBeidouPaymentRecordService.beidouPaymentWriteBack(payout);
                }
            } else if (EnumConsts.SubjectIds.SERVICE_RETREAT_RECEIVABLE_OVERDUE_SUB == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iRechargeInfoRecordService.createOilEntityInfo(payout.getBusiCode(), false, accessToken);
                }
            } else if (EnumConsts.SubjectIds.SUBJECTIDS1131 == payout.getSubjectsId().longValue()
                    && VEHICLE_AFFILIATION1.equals(payout.getVehicleAffiliation())) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iOilRechargeAccountDetailsFlowService.payRechargeSucess(payout.getBusiCode(), payout.getIsAutomatic());
                }
            } else if (EnumConsts.SubjectIds.SUBJECTIDS1133 == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
                    BeanUtil.copyProperties(payout, payoutIntfDto);
                    iOilRechargeAccountFlowService.payWithdrawSucess(payoutIntfDto, accessToken);
                }
            }//???????????????????????????
            else if (EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 == payout.getSubjectsId().longValue()) {
                //??????????????????
                if (respCodeSuc.equals(payout.getRespCode())) {
                    //???????????????????????????????????????
                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                    iServiceRepairOrderService.payRepairFeeCallBack(payout.getBusiCode(), accessToken);
                }
            }
        } catch (Exception e) {
//            log.info("flow_id["+payout.getFlowId()+"]??????????????????????????????",e);
            payout.setRespMsg("????????????,???????????????" + e.getMessage());
        }
    }

    @Override
    public PayoutIntf dealPayServiceFee(PayoutIntf violationFeePayoutIntf, String accessToken) {
        PayoutIntf payoutIntf = null;
        try {
            payoutIntf = payServiceFee(violationFeePayoutIntf, accessToken);
            //????????????????????????????????????????????????????????????????????????task????????????
            if (!respCodeSuc.equals(payoutIntf.getRespCode()) && !netTimeOutFail.equals(payoutIntf.getRespCode())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                payoutIntf.setId(null);
                payoutIntf.setSubjectsId(SUBJECTIDS1802);
                //????????????????????????

                //????????????????????????
                this.saveOrUpdate(payoutIntf);
                log.error("[" + payoutIntf.getId() + "]??????????????????????????????????????????????????????????????????" + payoutIntf.getRespMsg());
                PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
                BeanUtil.copyProperties(payoutIntf, payoutIntfDto);
                iBaseBusiToOrderService.doSavePayPlatformServiceFee(payoutIntfDto, accessToken);
                iBaseBusiToOrderService.doSavePayoutInfoExpansion(payoutIntfDto, accessToken);
            }
        } catch (Exception e) {
            //???????????????????????????????????????????????????task????????????
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (null == payoutIntf) {
                log.error("???????????????????????????", e);
                return null;
            }
            PayoutIntf payoutIntfNew = new PayoutIntf();
            payoutIntfNew.setPayObjId(violationFeePayoutIntf.getUserId());
            //????????????????????????
            payoutIntfNew.setUserType(payoutIntfNew.getPayUserType());
            payoutIntfNew.setPayUserType(payoutIntf.getUserType());
            //????????????????????????
            payoutIntfNew.setTxnAmt(violationFeePayoutIntf.getPlatformServiceFee());//????????????
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
            log.error("?????????????????????,???????????????????????????[" + payoutIntf.getId() + "]", e);
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
            serviceChargeReminder.setState(3);//????????????????????????????????????
            serviceChargeReminder.setUpdateTime(getDateToLocalDateTime(date));
            iServiceChargeReminderService.saveOrUpdate(serviceChargeReminder);
            //????????????????????????--??????????????????
            java.sql.Date date1 = new java.sql.Date(date.getTime());
            sysTenantDefService.updatePayDate(serviceChargeReminder.getTenantId(), date1);
            //????????????????????????
            sysTenantDefService.updatePayState(serviceChargeReminder.getTenantId(), SysStaticDataEnum.TENANT_PAY_STATE.PAYED);
        }
    }

    /**
     * ????????????????????????id??????????????????
     *
     * @param flowId   ?????????
     * @param tenantId ??????id
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
        //TODO:?????????????????????Id????????????-1??????????????????????????????????????????ID?????????????????????????????????
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
                record.setRespCodeName("???????????????");
            } else if (record.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1)) {
                record.setRespCodeName("???????????????");
            }

            if (record.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)) {
                record.setStateName("?????????????????????");
            } else {
                record.setStateName("?????????");
            }

            if (record.getBusiId() != null) {
                record.setBusiIdName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "BUSINESS_NUMBER_TYPE", String.valueOf(record.getBusiId())));
                if (record.getSubjectsId() != null && record.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                    record.setBusiIdName("?????????????????????");
                }
            }
        }
        return ipage;
    }

    @Override
    public void checkOverdueAcc(PayoutIntf payoutIntf, String accessToken) {
        //???????????????????????????
        if (PAY_FOR_OIL_CODE == payoutIntf.getBusiId().longValue() || PAY_FOR_REPAIR == payoutIntf.getBusiId().longValue()
                || BANK_PAYMENT_OUT == payoutIntf.getBusiId().longValue() || SUBJECTIDS50070 == payoutIntf.getSubjectsId()
                || BALANCE_CONSUME_OIL_SUB == payoutIntf.getSubjectsId() || BALANCE_PAY_REPAIR == payoutIntf.getSubjectsId()) {//????????????????????????????????????????????????
            return;
        }
        //??????????????????????????????????????????
        if (null != payoutIntf.getPayObjId() && null != payoutIntf.getUserId() && payoutIntf.getPayObjId().longValue() == payoutIntf.getUserId().longValue()) {
            return;
        }
        //?????????????????????
        Long tenantId = payoutIntf.getPayTenantId();
        if (payoutIntf.getPayType() != OrderAccountConst.PAY_TYPE.TENANT) {
            tenantId = payoutIntf.getTenantId();
        }
        if (getForPayTenantId(payoutIntf.getSubjectsId())) {
            tenantId = payoutIntf.getTenantId();
        }
        OrderAccount payAccount = iOrderAccountService.getOrderAccount(payoutIntf.getPayObjId(), payoutIntf.getVehicleAffiliation(), tenantId, payoutIntf.getOilAffiliation(), payoutIntf.getPayUserType());
        OrderAccount receivablesAccount = iOrderAccountService.getOrderAccount(payoutIntf.getUserId(), payoutIntf.getVehicleAffiliation(), tenantId, payoutIntf.getOilAffiliation(), payoutIntf.getUserType());
        //????????????
        Long currentPayableBalance = payAccount.getPayableOverdueBalance();
        //????????????
        Long currentReceivableBalance = receivablesAccount.getReceivableOverdueBalance();

        if (null == currentPayableBalance || currentPayableBalance.longValue() <= 0) {
            throw new BusinessException("????????????" + payAccount.getId() + "??????????????????" + currentPayableBalance + "??????????????????");
        }
        long appendFreight = payoutIntf.getAppendFreight() == null ? 0L : payoutIntf.getAppendFreight().longValue();
        long billServiceFee = payoutIntf.getBillServiceFee() == null ? 0L : payoutIntf.getBillServiceFee().longValue();
        if (currentPayableBalance.longValue() < (payoutIntf.getTxnAmt().longValue() + billServiceFee + appendFreight)) {
            throw new BusinessException("????????????" + payAccount.getId() + "????????????????????????????????????????????????");
        }
        if ((null == currentReceivableBalance || currentReceivableBalance.longValue() <= 0) && payoutIntf.getTxnAmt().longValue() > 0L) {
            throw new BusinessException("????????????" + receivablesAccount.getId() + "??????????????? " + currentReceivableBalance + "??????????????????");
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
            throw new BusinessException("?????????????????????");
        }
        if (payoutIntf.getRespCode() != null && payoutIntf.getRespCode().equals(HttpsMain.respCodeInvalid)) {
            throw new BusinessException("?????????" + payoutIntf.getId() + "??????????????????????????????");
        }
        if (payoutIntfExpansion == null) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (payoutIntfExpansion.getIsNeedBill().equals(OrderConsts.IS_NEED_BILL.TERRACE_BILL)) {
            accId = payoutIntf.getPinganCollectAcctId();
        } else {
            accId = payoutIntf.getAccNo();
        }
        if (StringUtils.isNotEmpty(accId)) {
            receiptRel = iAccountBankRelService.getAcctNo(accId);
            //???????????????????????????????????????????????????
            if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE) {
                payOutIntfUtil.getRandomPinganBankInfoOut(payoutIntf.getUserId(), payoutIntf.getPayTenantId());
            }
        }
        if (StringUtils.isNotEmpty(payoutIntf.getPayAccNo())) {
            paymentRel = iAccountBankRelService.getAcctNo(payoutIntf.getPayAccNo());
        }
        if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
            if (receiptRel == null || paymentRel == null) {
                throw new BusinessException("??????????????????????????????????????????????????????????????????!");
            }
            if (!org.apache.commons.lang.StringUtils.isNotEmpty(receiptRel.getAcctNo()) || !org.apache.commons.lang.StringUtils.isNotEmpty(paymentRel.getAcctNo())) {
                throw new BusinessException("??????????????????????????????????????????????????????????????????!");
            }
            if (!payoutIntf.getUserId().equals(receiptRel.getUserId()) || !payoutIntf.getPayObjId().equals(paymentRel.getUserId())) {
                throw new BusinessException("??????????????????????????????????????????????????????!");
            }
        }
        //??????????????????????????????
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            if (!paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("???????????????????????????????????????!");
            }
        }
        //??????????????????????????????????????????
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                && (userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER || userType == SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER
                || userType == SysStaticDataEnum.USER_TYPE.BILL_SERVER_USER)) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("???????????????????????????????????????????????????!");
            }
        }
        //????????????
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN)) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("???????????????????????????????????????!");
            }
        }
        //???????????????||?????? || ?????? ????????????????????????
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL
                && (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER || userType == SysStaticDataEnum.USER_TYPE.DRIVER_USER
                || userType == SysStaticDataEnum.USER_TYPE.ADMIN_USER || userType == SysStaticDataEnum.USER_TYPE.RECEIVER_USER)) {
            if (!((payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                    throw new BusinessException("??????????????????????????????||??????||?????? || ?????? ??????????????????!");
                }
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB)) {
            if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.COMMON_CARRIER_BILL) {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("????????????????????????????????????????????????????????????!");
                }
            } else {
                if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                    throw new BusinessException("????????????????????????????????????????????????????????????!");
                }
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS2302)) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("ETC??????????????????????????????!");
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE) && sysTenantDef.getActualController() != null) {
            if (receiptRel.getAcctName().equals(sysTenantDef.getActualController())) {
                throw new BusinessException("??????????????????????????????????????????????????????????????????????????????????????????!");
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.SUBJECTIDS1131)) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) || !paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("????????????????????????????????????!");
            }

            OilRechargeAccountDetailsFlow oi = oilRechargeAccountDetailsFlowService.getRechargeDetailsFlows(payoutIntf.getBusiCode(), SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2);
            if (oi != null && StringUtils.isNotEmpty(oi.getBankAccName()) && !paymentRel.getAcctName().equals(oi.getBankAccName())) {
                throw new BusinessException("??????????????????????????????????????????????????????,???????????????????????????" + oi.getBankAccName() + "!");
            }
        }
        if (payoutIntf.getSubjectsId().equals(EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) && payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            if (!receiptRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) || !paymentRel.getBankType().equals(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("????????????????????????????????????!");
            }
        }
        //????????????????????????????????????????????????5W????????????????????????
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
                    throw new BusinessException("????????????!??????????????????5???,???????????????,??????????????????????????????????????????,???????????????????????????????????????!");
                }
            }
        }
        if (payoutIntfExpansion.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            if (!payByCashService.payBill(paymentRel.getAcctName())) {
                throw new BusinessException("?????????????????????,???????????????????????????????????????!");
            }
            //???????????????????????????????????????????????????
            if (payoutIntf.getOrderId() != null && payoutIntf.getOrderId() > 0) {
                String status = payByCashService.vehicleOrDriverBill(payoutIntf.getOrderId());
                if (status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE1)) {
                    throw new BusinessException("?????????????????????,?????????????????????????????????!");
                }
                if (status.equals(SysStaticDataEnum.PAY_CASH_TYPE.PAY_TYPE2)) {
                    throw new BusinessException("?????????????????????,?????????????????????????????????!");
                }
                //???????????????????????????????????????????????????????????????
                String lookUpName = payByCashService.judgebillLookUp(payoutIntf.getOrderId());
                if (org.apache.commons.lang.StringUtils.isNotEmpty(lookUpName) && !paymentRel.getAcctName().equals(lookUpName)) {
                    throw new BusinessException("?????????????????????????????????????????????????????????,??????????????????????????????" + lookUpName + "!");
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
                        throw new BusinessException("????????????[" + payoutIntf.getId() + "]????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????:[" + payBankAccName + "]");
                    } else {
                        throw new BusinessException("????????????[" + payoutIntf.getId() + "]??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????:[" + payBankAccName + "]" + "?????????????????????[" + receivablesBankAccName + "]");
                    }
                }
            }
            //??????????????????????????????????????????????????????????????? ???????????????????????????  ???????????????????????????????????????????????????
            LoginInfo loginInfo = loginUtils.get(accessToken);
            Long tenantId = loginInfo.getTenantId();
            String sysParameLuge = SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE + "");
            String sysPre = billPlatformService.getPrefixByUserId(Long.valueOf(payoutIntf.getVehicleAffiliation()));
            if (sysPre != null && sysPre.equals(sysParameLuge)) {
                if (SUBJECTIDS1131 == payoutIntf.getSubjectsId().longValue()) {
                    //??????????????????????????????????????????????????????
                    AgentServiceDto agentServiceDto = serviceInfoService.getAgentService(payoutIntf.getPayTenantId(), ServiceConsts.AGENT_SERVICE_TYPE.OIL);
                    if (agentServiceDto == null) {
                        throw new BusinessException("???????????????????????????????????????????????????????????? ");
                    }

                    AgentServiceInfo agentServiceInfo = agentServiceDto.getAgentServiceInfo();
                    if (StringUtils.isBlank(agentServiceInfo.getLgOilStationId())) {
                        throw new BusinessException("???????????????????????????????????????????????????Id?????? ");
                    }
                    long gasStationId = Long.valueOf(agentServiceInfo.getLgOilStationId());//????????????Id
                    //TODO ?????????????????????(??????)
//                    String stationRsp = "";//TODO?????????
//                    Map<String, Object> stationRspMap = JsonHelper.parseJSON2Map(stationRsp);
//                    boolean buyOilLimit =false;
//                    if ("0".equals(stationRspMap.get("reCode")+"")){
//                        if(stationRspMap.get("buyOilLimit")!=null&&"0".equals(stationRspMap.get("buyOilLimit")+"")){//??????
//                            buyOilLimit = true;
//                        }else{
//                            payoutIntf.setBatchSeq(payoutIntf.getId()+"");
//                            payoutIntf.setRespCode(HttpsMain.respCodeFail);
//                            payoutIntf.setPayTime(new Date());
//                            payoutIntf.setRespMsg("??????????????????????????????");
//                            throw new BusinessException("??????????????????????????????");
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
                        //TODO ?????????????????????
//                        String rsp = SyncLugeUtil.qrpd(xid, "", "0", payoutIntf.getPayTenantId());
//                        Map<String, Object> rspMap = JsonHelper.parseJSON2Map(rsp);
//                        if(!"0".equals(rspMap.get("reCode")+"")){//??????0????????????
//                            String errorMsg = rspMap.get("reInfo")+"";
//                            throw new BusinessException("????????????????????????????????????????????????????????????????????????"+errorMsg);
//                        }
//                        List<Map<String, String>> payList = (List<Map<String, String>>) rspMap.get("pay_list");
//                        if (payList == null || payList.size() <= 0) {
//                            throw new BusinessException("???????????????????????????????????????");
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
//                            throw new BusinessException("???????????????????????????????????????????????????????????????????????????=" + JsonHelper.toJson(rspMap.get("pay_list")));
//                        }
//                        long payActualMoney = com.business.utils.CommonUtil.getLongByString(serialNumbersMap.get(serialNumbers.get(0)));
//                        long billServiceFee = payoutIntf.getBillServiceFee();//??????????????????????????????
//                        long amount = payoutIntf.getTxnAmt();//????????????
//                        String billSerialNumber = serialNumbersMap.get(serialNumbers.get(0));//????????????????????????????????????????????????
//                        payoutIntfExpansion.setBillSerialNumber(billSerialNumber);
//                        payoutIntfSV.saveOrUpdate(payoutIntfExpansion);
//                        if (payActualMoney != (billServiceFee + amount)) {//?????????????????????????????????????????????+?????????????????????????????????????????????
//                            //20190927??????
//                            //throw new BusinessException("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????" + rspMap.get("payActualMoney"));
//                            paymentTF.dealLugeServiceFeeDifference(payoutIntf, payActualMoney);
//                        }
                    } else {
                        if (org.apache.commons.lang.StringUtils.isBlank(payoutIntfExpansion.getBillSerialNumber())) {
                            String payType = "0";//???????????? 0???????????????????????? 2????????????
                            String pinganCollectAcctId = payoutIntf.getPinganCollectAcctId();//???????????????????????????
                            AccountBankRel accountBankRel = iAccountBankRelService.getAcctNo(pinganCollectAcctId);
                            String payName = accountBankRel.getAcctName();//??????????????? ???????????????
                            String bankName = accountBankRel.getBankName();//????????????
                            String bankProvince = accountBankRel.getProvinceName();//????????????
                            String bankCity = accountBankRel.getCityName();//????????????
                            String bankCardNo = accountBankRel.getAcctNo();//????????????
                            String payeeIdCard = accountBankRel.getIdentification();//?????????????????????
                            String oilCardNo = "";//????????? ???????????????
                            String payAmount = CommonUtil.getDoubleFormatLongMoney(payoutIntf.getTxnAmt());//????????????  ??????????????????????????????
                            //TODO ?????????????????????
//                            String rsp=SyncLugeUtil.anpy(payType, xid, payName, bankName, bankProvince, bankCity, bankCardNo, payeeIdCard, oilCardNo, payAmount,payoutIntf.getPayTenantId());
//                            Map<String, Object> rspMap = JsonHelper.parseJSON2Map(rsp);
//                            if(!"0".equals(rspMap.get("reCode")+"")){//??????0????????????
//                                String errorMsg = rspMap.get("reInfo")+"";
//                                throw new BusinessException("???????????????????????????"+errorMsg);
//                            }
//                            //long payActualMoney = Long.valueOf(rspMap.get("payActualMoney")+"");//????????????????????????
//                            long payActualMoney = com.business.utils.CommonUtil.getLongByString(rspMap.get("payActualMoney")+"");
//                            long billServiceFee = payoutIntf.getBillServiceFee();//??????????????????????????????
//                            long amount = payoutIntf.getTxnAmt();//????????????
//                            String billSerialNumber = rspMap.get("serialNumber")+"";//????????????????????????????????????????????????
//                            payoutIntfExpansion.setBillSerialNumber(billSerialNumber);
//                            payoutIntfSV.saveOrUpdate(payoutIntfExpansion);
//                            if(payActualMoney!=(billServiceFee+amount)){//?????????????????????????????????????????????+?????????????????????????????????????????????
//                                //20190927??????
//                                //throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????????????????,????????????????????????" + rspMap.get("payActualMoney"));
//                                paymentTF.dealLugeServiceFeeDifference(payoutIntf, payActualMoney);
//                            }
                        } else {
                            if (payoutIntf.getRemark().indexOf("??????????????????????????????????????????????????????????????????") != -1) {
                                int startIndex = payoutIntf.getRemark().indexOf("????????????????????????");
                                int endIndex = payoutIntf.getRemark().length();
                                String payActualMoneyStr = payoutIntf.getRemark().substring(startIndex, endIndex);
                                long billServiceFee = payoutIntf.getBillServiceFee();//??????????????????????????????
                                long amount = payoutIntf.getTxnAmt();//????????????
                                long payActualMoney = CommonUtil.getLongByString(payActualMoneyStr);
                                if (payActualMoney != (billServiceFee + amount)) {//?????????????????????????????????????????????+?????????????????????????????????????????????
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
     * ????????????
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
     * ???????????????
     */
    @Transactional
    public PayoutIntf payServiceFee(PayoutIntf violationFeePayoutIntf, String accessToken) {
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(violationFeePayoutIntf.getUserId(), null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        //???????????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(SysStaticDataEnum.PT_TENANT_ID);
        Long inUserId = sysTenantDef.getAdminUser();
        Long tenant = sysTenantDef.getId();
        SysUser sysOtherOperator = iSysUserService.getSysOperatorByUserIdOrPhone(inUserId, null, 0L);
        if (sysOtherOperator == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        long soNbr = CommonUtil.createSoNbr();
        //???????????????????????????????????????
        List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel consumeBalanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(SUBJECTIDS50072, violationFeePayoutIntf.getPlatformServiceFee());
        busiBalanceList.add(consumeBalanceSub);
        List<BusiSubjectsRel> balanceRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE, busiBalanceList);
        iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.CONSUME_CODE, EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE, violationFeePayoutIntf.getUserId(), inUserId, sysOtherOperator.getOpName(), balanceRelList, soNbr, 0L, -1L, violationFeePayoutIntf.getUserType());

        //??????????????????????????????????????????
        List<BusiSubjectsRel> serviceBusiBalanceList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel serviceBalanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.SUBJECTIDS50073, violationFeePayoutIntf.getPlatformServiceFee());
        serviceBusiBalanceList.add(serviceBalanceSub);
        List<BusiSubjectsRel> serviceBalanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE, serviceBusiBalanceList);
        iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE, inUserId, violationFeePayoutIntf.getUserId(), sysOperator.getOpName(), serviceBalanceList, soNbr, 0L, -1L, SysStaticDataEnum.USER_TYPE.ADMIN_USER);

        int accountType = BUSINESS_PAYABLE_ACCOUNT;//????????????
        if (violationFeePayoutIntf.getBankType().intValue() == PRIVATE_RECEIVABLE_ACCOUNT) {
            accountType = PRIVATE_PAYABLE_ACCOUNT;
        }
        AccountBankRel accountBankRel = iAccountBankRelService.getAcctNo(violationFeePayoutIntf.getAccNo());
        if (null == accountBankRel) {
            throw new BusinessException("?????????????????????");
        }
        int inbankType = BUSINESS_RECEIVABLE_ACCOUNT;//????????????
        PayoutIntf payoutIntf = new PayoutIntf();
        payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.SERVICE);
        payoutIntf.setTenantId(tenant);
        payoutIntf.setPayObjId(violationFeePayoutIntf.getUserId());
        payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
        payoutIntf.setUserId(inUserId);
        //????????????????????????
        payoutIntf.setUserType(violationFeePayoutIntf.getPayUserType());
        payoutIntf.setPayUserType(violationFeePayoutIntf.getUserType());
        //????????????????????????
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
            //????????????
            payoutIntf.setRespCode(netTimeOutFail);
            payoutIntf.setRespBankCode(BANK_OTHER_ERROR);
            payoutIntf.setRespMsg(payReturnOut.getReqMess());
        }
        //????????????
        else if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts2) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            payoutIntf.setRespCode(HttpsMain.respCodeFail);
            payoutIntf.setRespBankCode(BALANCE_NOT_ENOUGH);
            payoutIntf.setRespMsg(payReturnOut.getReqMess());
        }
        //??????????????????
        else if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts3) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            payoutIntf.setRespCode(HttpsMain.respCodeFail);
            payoutIntf.setRespBankCode(LOCAL_BUSI_ERROR);
            payoutIntf.setRespMsg(payReturnOut.getReqMess());
        }
        //????????????
        else if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts4) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            payoutIntf.setRespCode(HttpsMain.respCodeFail);
            payoutIntf.setRespBankCode(BANK_OTHER_ERROR);
            payoutIntf.setRespMsg(payReturnOut.getReqMess());
        } else {
            //????????????
            payoutIntf.setVerificationState(VERIFICATION_STATE);
            payoutIntf.setRespCode(respCodeSuc);
            payoutIntf.setRespMsg("????????????");
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
            throw new BusinessException("??????????????????????????????!");
        }
//        if(sysCfg.getCfgValue().equals("1")){
//            return (IBankCallTF)SysContexts.getBean("pingAnBankTF");
//        }
    }

    @Override
    public void cmbDealOnlineToOffline(String accessToken) {
        LOGGER.info("------------???????????????????????????????????????-------------");
        int allRow = baseMapper.getOnlineToOfflineCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("?????????????????????????????????[" + allRow + "]???,??????" + totalPage + "???");
        int currentPage = 0;
        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getOnlineToOfflineList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, currentPage, pageSize);
            LOGGER.info("?????????????????????????????????:???" + (i + 1) + "???:" + payoutIntf.size());
            cmbDeal(payoutIntf, accessToken);
            currentPage += pageSize;
        }
        LOGGER.info("------------???????????????????????????????????????-------------");
    }

    @Override
    public void dealOnlineToOffline(String accessToken) {
        LOGGER.info("------------???????????????????????????????????????-------------");
        int allRow = baseMapper.getOnlineToOfflineCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("?????????????????????????????????[" + allRow + "]???,??????" + totalPage + "???");
        int currentPage = 0;
        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getOnlineToOfflineList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, currentPage, pageSize);
            LOGGER.info("?????????????????????????????????:???" + (i + 1) + "???:" + payoutIntf.size());
            cmbDeal(payoutIntf, accessToken);
            currentPage += pageSize;
        }
        LOGGER.info("------------???????????????????????????????????????-------------");
    }

    public void cmbDeal(List<PayoutIntf> payoutIntf, String accessToken) {
        for (PayoutIntf payout : payoutIntf) {
            try {
                payout.setIsTurnAutomatic(IS_TURN_AUTOMATIC_0);
                payout.setIsAutomatic(0);
                payout.setRemark("???????????????????????????");
                if (payout.getPayTenantId() > 0) {
                    Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payout.getId(), payout.getPayTenantId());
                    if (!b) {
                        startAuditProcess(payout, accessToken);
                    }
                }
                baseMapper.updateById(payout);
            } catch (Exception e) {
//                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOGGER.info("FLOW_ID:[" + payout.getId() + "]???????????????????????????", e);
                payout.setRemark("???????????????????????????" + e.getMessage());
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
            //??????????????????????????????????????????????????????
            bool = true;
        }

        if (!bool) {
            throw new BusinessException("???????????????????????????????????????");
        }
    }


    @Override
    public void payOutToBusi() {
        LOGGER.info("------------????????????????????????????????????-------------");

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
        //?????????????????????????????????????????????????????????  ????????????????????????????????????
//        this.cmbDealOnlineToOffline(accessToken);

        int allRow = baseMapper.getPayoutInfoBusiCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("PayOutToBusi200Task ??????200????????????????????????[" + allRow + "]???,??????" + totalPage + "???");
        int currentPage = 0;
        List<AcBusiOrderLimitRel> busiOrderLimitRelList = acBusiOrderLimitRelService.getBaseMapper().selectList(null);
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.getBusiSubjectsRelList();

        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getPayoutInfoBusiList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate, currentPage, pageSize);
            LOGGER.info("PayOutToBusi200Task 200??????????????????????????????:???" + (i + 1) + "???:" + payoutIntf.size());
            payOutToBusiPayInfo(payoutIntf, busiOrderLimitRelList, busiSubjectsRelList, accessToken);
            currentPage += pageSize;
        }
        LOGGER.info("------------200??????????????????????????????-------------");
    }


    public void payOutToBusiPayInfo(List<PayoutIntf> payoutIntf, List<AcBusiOrderLimitRel> busiOrderLimitRelList, List<BusiSubjectsRel> busiSubjectsRelList, String accessToken) {
        if (null != payoutIntf && !payoutIntf.isEmpty()) {
            //???????????????????????? ??????????????????
            for (PayoutIntf payout : payoutIntf) {
                try {
                    payOutToBusiPay(busiOrderLimitRelList, payout, accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                    //???????????????????????????
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                    LOGGER.error("FLOW_ID:[" + payout.getId() + "]??????????????????", e);
                    String errorMessage = e.getMessage();
                    if (StringUtils.isNoneBlank(errorMessage) && errorMessage.length() > 200) {
                        errorMessage = errorMessage.substring(0, 200) + "...";
                    }
                    payout.setRespMsg("??????????????????:" + errorMessage);
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
        //????????????
        payout.setRespCode(HttpsMain.respCodeSuc);
        payout.setRespMsg("????????????");
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
        LOGGER.info("------------??????????????????????????????-------------");

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
        LOGGER.info("PayOutToBank200Task ??????200??????????????????[" + allRow + "]???,??????" + totalPage + "???");
        int currentPage = 0;
        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getPayoutInfoList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate, currentPage, pageSize);
            LOGGER.info("PayOutToBank200Task 200????????????????????????:???" + (i + 1) + "???:" + payoutIntf.size());
            payOutToBankPay(payoutIntf, accessToken);
            currentPage += pageSize;
        }

        LOGGER.info("------------200????????????????????????-------------");
    }

    public void payOutToBankPay(List<PayoutIntf> payoutIntf, String accessToken) {
        if (null != payoutIntf && !payoutIntf.isEmpty()) {
            //?????????????????? ??????????????????
            for (PayoutIntf payout : payoutIntf) {
                PayoutIntfExpansion payoutIntfExpansion = null;
                try {
                    LambdaQueryWrapper<PayoutIntfExpansion> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(PayoutIntfExpansion::getFlowId, payout.getId());

                    payoutIntfExpansion = payoutIntfExpansionMapper.selectOne(queryWrapper);

                    payOutToBankPay(payout, payoutIntfExpansion, accessToken);
                } catch (Exception e) {
                    //???????????????????????????
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                    LOGGER.error("FLOW_ID:[" + payout.getId() + "]??????????????????", e);
                    payout.setRespCode(HttpsMain.respCodeFail);
                    payout.setRespBankCode(LOCAL_BUSI_ERROR);
//                    payout.setRespMsg("??????????????????:" + e.getMessage());
                    payout.setRespMsg("??????????????????");
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

    //????????????????????????  ????????????????????????
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
        //?????????????????????
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
            payout.setRemark("????????????????????????????????????????????????????????????" + ex.getMessage());
            payout.setRemark(ex.getMessage());
            payoutIntfExpansion.setRemark(ex.getMessage());
            baseMapper.updateById(payout);
            payoutIntfExpansionMapper.updateById(payoutIntfExpansion);
            LoginInfo loginInfo = loginUtils.get(accessToken);
            sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.Payoutchunying, payout.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "?????????????????????????????????????????????????????????");

            if (payout.getPayTenantId() > 0) {
                Boolean b = iAuditNodeInstService.isAudited(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payout.getId(), payout.getPayTenantId());
                if (b) {
                    this.startAuditProcess(payout, accessToken);
                }
            }

            return;
        }

        AccountTransferVo accountTransferVo = new AccountTransferVo();
        accountTransferVo.setMbrNo(payout.getPayAccNo());//??????????????????
        accountTransferVo.setMbrName(payout.getPayAccName());//???????????????
        accountTransferVo.setUserId(payout.getUserId());//????????????ID

        // ??????????????????????????????????????????0  ??????56k????????????
        long billserviceFee = payout.getBillServiceFee() != null ? payout.getBillServiceFee() : 0L;
        long appendFreight = payout.getAppendFreight() == null ? 0L : payout.getAppendFreight();
        Double d = Double.valueOf(payout.getTxnAmt() + billserviceFee + appendFreight);
        String money = String.valueOf(d / 100);
        accountTransferVo.setTranAmt(money);//??????
        accountTransferVo.setRecvMbrName(payout.getAccName());//??????????????????
        AccountBankRel accountBankRel = iAccountBankRelService.getAcctNo(payout.getPinganCollectAcctId());
        accountTransferVo.setRecvMbrNo(accountBankRel.getPinganPayAcctId());//???????????????
        accountTransferVo.setRemark(payout.getRemark());//??????
        accountTransferVo.setPayoutId(payout.getId());// ?????????ID

        accountTransferVo.setPayAccountId(bankAccountService.getByMbrNo(payout.getPayAccNo()).getId());//?????????ID
        accountTransferVo.setRecvAccountId(bankAccountService.getByMbrNo(accountBankRel.getPinganPayAcctId()).getId());// ?????????ID

        try {
            bankAccountTranService.transfer(accountTransferVo);
            Date now = new Date();
            payout.setVerificationState(VERIFICATION_STATE);
            payout.setRespCode(respCodeZero);
            payout.setRespMsg("????????????????????????");
            payout.setVerificationDate(now);
            payout.setPayTime(now);
            payout.setCompleteTime(DateUtil.formatDate(now, DATETIME12_FORMAT2));

            baseMapper.updateById(payout);
            payoutIntfExpansionMapper.updateById(payoutIntfExpansion);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("FLOW_ID:[" + payout.getId() + "]??????????????????", e);
            payout.setRespCode(HttpsMain.respCodeFail);
            payout.setRespBankCode(LOCAL_BUSI_ERROR);
            payout.setRespMsg("??????????????????:" + e.getMessage());
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
            LOGGER.info("------------??????????????????????????????-------------");

            String timeSMS = (String) getCfgVal(SysStaticDataEnum.MATURITY.MATURITY1, 0, String.class);
            int dateSMS = Integer.valueOf(timeSMS);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - dateSMS);
            Date today = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String resultStart = format.format(today);

            //????????????????????????????????????
            int allRow = baseMapper.doQueryConfirmMoney(resultStart + "235959");
            int pageSize = 1000;
            int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
            LOGGER.info("ConfirmMoneyTask ????????????????????????????????????[" + allRow + "]???,??????" + totalPage + "???");
            int currentPage = 0;
            for (int i = 0; i < totalPage; i++) {
                List<PayoutIntf> payoutIntf = baseMapper.doQueryConfirmMoneyList(resultStart + "235959", currentPage * pageSize, pageSize);
                LOGGER.info("ConfirmMoneyTask ????????????????????????????????????:???" + (i + 1) + "???:" + payoutIntf.size());
                if (payoutIntf != null && payoutIntf.size() > 0) {
                    for (PayoutIntf payoutIntfInfo : payoutIntf) {
                        this.confirmation(payoutIntfInfo, accessToken);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("??????????????????????????????????????????:" + e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } finally {
//            platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
        }
        LOGGER.info("------------??????????????????????????????-------------");

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
            //????????????
            if (payoutIntf.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER) && days == dateSMS) {
                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
                String now = new SimpleDateFormat("yyyy???MM???dd???").format(date);
                Calendar date2 = Calendar.getInstance();
                date2.setTime(date);
                date2.set(Calendar.DATE, date2.get(Calendar.DATE) + dateConfirmation);
                Date date3 = date2.getTime();
                String now1 = new SimpleDateFormat("yyyy???MM???dd???").format(date3);
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

            //????????????
            if (days >= dateConfirmation) {
                this.doPay200(payoutIntf, null, false, accessToken);
                payoutIntf.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
                payoutIntf.setVerificationDate(new Date());
                payoutIntf.setPayTime(new Date());
                payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.transfer);
                payoutIntf.setRemark("????????????????????????");
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
        LOGGER.info("------------??????????????????????????????-------------");

        int allRow = baseMapper.getPayoutInfoCount(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate);
        int pageSize = 50;
        int totalPage = allRow % pageSize == 0 ? allRow / pageSize : allRow / pageSize + 1;
        LOGGER.info("PayOutToBank200Task ??????200??????????????????[" + allRow + "]???,??????" + totalPage + "???");
        int currentPage = 0;
        for (int i = 0; i < totalPage; i++) {
            List<PayoutIntf> payoutIntf = baseMapper.getPayoutInfoList(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, createDate, currentPage, pageSize);
            LOGGER.info("PayOutToBank200Task 200????????????????????????:???" + (i + 1) + "???:" + payoutIntf.size());
            cmbOutToBankPayInfo(payoutIntf, accessToken);
            currentPage += pageSize;
        }

        LOGGER.info("------------200????????????????????????-------------");
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
                // ?????????????????????
                accountBankRel = iAccountBankRelService.getAcctNo(payout.getPinganCollectAcctId());
                CmbBankAccountInfo bankAccountInfo = bankAccountService.getByMerchNo(payout.getPinganCollectAcctId());
                if (accountBankRel != null && !StringUtils.isNotEmpty(accountBankRel.getPinganPayAcctId())) {
                    continue;
                }
                accountTransferVo.setMbrNo(payout.getPayAccNo());//??????????????????
                accountTransferVo.setMbrName(payout.getPayAccName());//???????????????
                if (!StringUtils.isNotEmpty(payout.getPayAccNo())) {
                    continue;
                }
                Double d = Double.valueOf(payout.getTxnAmt());
                String money = String.valueOf(d / 100);
                LOGGER.info("MbrNo:[" + payout.getPayAccNo() + "]??????????????????" + money);
                //????????????+????????????+????????????
//                String money=String.valueOf(payout.getTxnAmt()/100+ (payout.getAppendFreight() !=null? 0: payout.getAppendFreight()/100) +(payout.getBillServiceFee()!=null ? 0:payout.getBillServiceFee() /100));
                if (d / 100 <= 0) {
                    continue;
                }
                accountTransferVo.setUserId(payout.getUserId());//????????????ID
                accountTransferVo.setTranAmt(money);//??????
                accountTransferVo.setRecvMbrName(payout.getAccName());//??????????????????
                accountTransferVo.setRecvMbrNo(accountBankRel != null ? accountBankRel.getPinganPayAcctId() : bankAccountInfo.getMbrNo());//???????????????
                accountTransferVo.setRemark(payout.getRemark());//??????
                accountTransferVo.setPayoutId(payout.getId());
                accountTransferVo.setPayAccountId(bankAccountService.getByMbrNo(payout.getPayAccNo()).getId());//?????????ID
                accountTransferVo.setRecvAccountId(accountBankRel != null ? bankAccountService.getByMbrNo(accountBankRel.getPinganPayAcctId()).getId() : bankAccountInfo.getId());// ?????????ID
                try {
                    // ??????????????????
                    payout.setPayTime(new Date());
                    //??????
                    bankAccountTranService.transfer(accountTransferVo);
                    payout.setRespMsg("??????????????????");
                    //??????????????????
                    Date now = new Date();
                    payout.setCompleteTime(DateUtil.formatDate(now, DATETIME12_FORMAT2));
                    payout.setRespCode(HttpsMain.respCodeCollection);//??????????????????
                    payout.setVerificationDate(now);
                    payout.setRespMsg("????????????");
                    payout.setPayConfirm(2);
                    payout.setIsAutomatic(1);
                    payout.setVerificationState(5);

                    //?????????????????? ????????????
                    baseMapper.updateById(payout);

                    //????????????????????????
                    baseMapper.updatePayManagerState(payout.getId());

                    //??????????????????????????????????????????
                    this.callBack(payout, accessToken);

                    LOGGER.info("MbrNo:[" + payout.getPayAccNo() + "]??????????????????");

                    OrderAccount account = iOrderAccountService.getOrderAccount(accountBankRel != null ? accountBankRel.getUserId() : bankAccountInfo.getUserId());
                    upOrderAccount(account, payout.getTxnAmt());
                } catch (Exception e) {
                    //???????????????????????????
//                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    //??????????????????
                    LOGGER.error("FLOW_ID:[" + payout.getId() + "]??????????????????", e);
                    payout.setRespCode(HttpsMain.respCodeFail);
                    payout.setRespBankCode(LOCAL_BUSI_ERROR);
                    payout.setRespMsg("??????????????????:" + e.getMessage());
                    payout.setPayTime(new Date());
                    this.saveOrUpdate(payout);
                } finally {
                    //????????????
//                    platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
                }
            }
        }
    }

    //2004
    public void upOrderAccount(OrderAccount account, Long TxnAmt) {
        //???????????????????????????????????????
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

        // ??????????????????
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

        // ??????????????????????????????
        Page<PayoutInfoDto> ipage = baseMapper.getOverdueCD(page, payObjId, userId, name, orderId, businessNumber, busiIds, isTenant, payTenantId, payUserType, state, states);
        List<PayoutInfoDto> list = ipage.getRecords();

        List<Long> busiIdList = new ArrayList<>();
        //???????????????????????????
        for (PayoutInfoDto dto : list) {
            busiIdList.add(dto.getFlowId());
        }

        // ????????????
        Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
        Map<Long, Map<String, Object>> out = new HashMap<>();

        if (busiIdList != null && busiIdList.size() > 0) {
            hasPermissionMap = auditOutService.isHasPermission(accessToken, AuditConsts.AUDIT_CODE.PAY_CASH_CODE, busiIdList);
            out = auditOutService.queryAuditRealTimeOperation(loginInfo.getUserInfoId(), AuditConsts.AUDIT_CODE.PAY_CASH_CODE, busiIdList, loginInfo.getTenantId());
        }

        boolean isNeedPassword = false;
        String needPassword = "";
        if (isTenant) {
            // ????????????????????????
            needPassword = iPlatformPayService.doQueryCheckPasswordErr(accessToken);
        }

        for (PayoutInfoDto dto : list) {
            String respCode = dto.getRespCode();
            if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)) {
                dto.setRespCodeName("");
            } else if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
                dto.setRespCodeName("???????????????");
            } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && dto.getBusiId() != EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD && !HttpsMain.respCodeCollection.equals(respCode)) {
                dto.setRespCodeName("???????????????");
            } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && !HttpsMain.respCodeCollection.equals(respCode)) {
                dto.setRespCodeName("?????????");
            }

            if(userId != null && userId > 0){
                if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
                    dto.setStateName("?????????");
                } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && HttpsMain.respCodeCollection.equals(respCode)) {
                    dto.setStateName("??????????????????");
                } else if (dto.getVerificationState() == 2) {
                    dto.setStateName("??????????????????");
                } else {
                    dto.setStateName("?????????");
                }
            }else {
                if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
                    dto.setStateName("?????????");
                } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && !HttpsMain.respCodeCollection.equals(respCode)) {
                    dto.setStateName("?????????");
                } else if (dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && HttpsMain.respCodeCollection.equals(respCode)) {
                    //dto.setStateName("?????????");
                    dto.setStateName("?????????");
                } else if (dto.getVerificationState() == 2) {
                    dto.setStateName("?????????");
                } else {
                    dto.setStateName("?????????");
                }
            }

            boolean isTrue = false;
            if (hasPermissionMap.get(dto.getFlowId()) != null && hasPermissionMap.get(dto.getFlowId())) {
                isTrue = true;
            }

            if (dto.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) && dto.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0) && isTrue) {
                dto.setHasPermission(true); // ????????????
            }
            if (needPassword.equals(SysStaticDataEnum.PWD_STATUS.PWD_STATUS2)) {//??????
                isNeedPassword = true;
            }

            if (out.get(dto.getFlowId()) != null) {
                Map<String, Object> p = out.get(dto.getFlowId());

                // ????????????
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
                    dto.setBusiIdName("?????????????????????");
                }
            }
        }

        return ipage;
    }

    @Override
    public Long getOverdueCDSum(Long orderId, String name, String businessNumber, String state, Long userId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        // ??????????????????
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
                infoOut.setRespCodeName("???????????????");
            } else if (infoOut.getIsAutoMatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1)) {
                infoOut.setRespCodeName("???????????????");
            }
            if (infoOut.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)) {
                infoOut.setStateName("?????????????????????");
            } else {
                infoOut.setStateName("?????????");
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
            throw new BusinessException("?????????userId!");
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
                infoOut.setRespCodeName("???????????????");
            } else if (String.valueOf(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1).equals(infoOut.getIsAutoMatic()) && !HttpsMain.respCodeCollection.equals(respCode)) {
                infoOut.setRespCodeName("???????????????");
            } else if (String.valueOf(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1).equals(infoOut.getIsAutoMatic()) && HttpsMain.respCodeCollection.equals(respCode)) {
                infoOut.setRespCodeName("?????????");
            }

            if (EnumConsts.PAY_CONFIRM.transfer == infoOut.getPayConfirm()) {
                infoOut.setStateName("?????????????????????");
            } else if ("6".equals(infoOut.getRespCode()) || EnumConsts.PAY_CONFIRM.recharge == infoOut.getPayConfirm()) {
                infoOut.setStateName("???????????????");
            } else {
//				infoOut.setStateName("?????????");
                infoOut.setStateName("?????????");
            }
            if (StringUtils.isNotBlank(infoOut.getFileUrl())) {
                infoOut.setShowFile(true);
            } else {
                infoOut.setShowFile(false);
            }

            if (infoOut.getBusiId() != null) {
                infoOut.setBusiIdName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "BUSINESS_NUMBER_TYPE", infoOut.getBusiId() + ""));
                if (infoOut.getSubjectsId() != null && infoOut.getSubjectsId() == EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                    infoOut.setBusiIdName("?????????????????????");
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
            throw new BusinessException("?????????userId!");
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
     * ?????? ??????-???????????? ????????????
     *
     * @param data ?????????????????? yyyy-mm-dd
     * @param num  ????????????
     * @return ?????????????????????????????? yyyy-mm-dd
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

        //??????Calendar?????????add()??????????????????
        cld.add(Calendar.DATE, num);

        return cld.get(Calendar.YEAR) + "-" + cld.get(Calendar.MONTH) + "-" + cld.get(Calendar.DATE);
    }

    /**
     * ?????? ?????? ???????????? ????????????
     *
     * @param data            ?????????????????? yyyy-mm-dd
     * @param collectionMonth ??????????????????????????????
     * @param collectionDay   ????????????????????????????????????
     * @return ?????????????????????????????? yyyy-mm-dd
     */
    private String getMonthlyDateStr(String data, Integer collectionMonth, Integer collectionDay) {
        String[] split = data.split("-");

        Integer year = Integer.valueOf(split[0]);
        Integer month = Integer.valueOf(split[1]);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONDAY, month-1);


        //??????Calendar?????????add()??????????????????
        cld.add(Calendar.MONDAY, collectionMonth);
        cld.set(Calendar.DAY_OF_MONTH, collectionDay);
        return cld.get(Calendar.YEAR) + "-" + (cld.get(Calendar.MONTH)+1) + "-" + cld.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return true ?????????????????????????????????false ?????????????????????
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
//        // ??????????????????(??????????????????):????????????+??????
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
