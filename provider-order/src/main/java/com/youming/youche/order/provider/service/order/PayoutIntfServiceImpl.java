package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.finance.dto.PayoutInfoOutDto;
import com.youming.youche.finance.vo.munual.QueryPayoutIntfsVo;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceInvitationService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.youka.IVoucherInfoService;
import com.youming.youche.market.commons.CommonUtil;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.vo.facilitator.ServiceInfoBasisVo;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.IPayManagerService;
import com.youming.youche.order.api.IPlatformServiceChargeService;
import com.youming.youche.order.api.order.IAcBusiOrderLimitRelService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountBankUserTypeRelService;
import com.youming.youche.order.api.order.IAdditionalFeeService;
import com.youming.youche.order.api.order.IBaseBillInfoService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBillSettingService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOaLoanService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfExpansionService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IRateService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.PayManager;
import com.youming.youche.order.domain.order.AcBusiOrderLimitRel;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.AdditionalFee;
import com.youming.youche.order.domain.order.BaseBillInfo;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.BillSetting;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;
import com.youming.youche.order.dto.AcOrderSubsidyInDto;
import com.youming.youche.order.dto.BankAccInfoDto;
import com.youming.youche.order.dto.BankDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.OrderLimitOutDto;
import com.youming.youche.order.dto.PayoutInfoDto;
import com.youming.youche.order.dto.PayoutInfosOutDto;
import com.youming.youche.order.dto.PinganBankInfoOutDto;
import com.youming.youche.order.dto.QueryPayManagerDto;
import com.youming.youche.order.dto.WXShopDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.provider.mapper.order.PayoutIntfMapper;
import com.youming.youche.order.provider.utils.BaseBusiToOrder;
import com.youming.youche.order.provider.utils.PayOutIntfUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysCfgRedisUtils;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.order.vo.QueryPayManagerVo;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.system.api.IServiceProviderBillService;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.system.dto.AuditOutDto;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.youming.youche.conts.EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0;
import static com.youming.youche.conts.EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1;
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
import static com.youming.youche.conts.EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1131;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1133;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1816;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS2302;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS50070;
import static com.youming.youche.conts.EnumConsts.SubjectIds.WITHDRAWALS_OUT;
import static com.youming.youche.conts.OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
import static com.youming.youche.conts.OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
import static com.youming.youche.conts.OrderAccountConst.PAY_TYPE.HAVIR;
import static com.youming.youche.conts.OrderAccountConst.TXN_TYPE.PLATFORM_TXN_TYPE;
import static com.youming.youche.conts.OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;
import static com.youming.youche.conts.OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1;
import static com.youming.youche.conts.SysStaticDataEnum.PAYMANAGER_STATE.PAYMANAGER_STATE0;
import static com.youming.youche.conts.SysStaticDataEnum.PAYMANAGER_STATE.PAYMANAGER_STATE1;
import static com.youming.youche.conts.SysStaticDataEnum.PAYMANAGER_STATE.PAYMANAGER_STATE5;
import static com.youming.youche.conts.SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OWN_CAR_TRANSFER_SALARY_402;
import static com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.ADMIN_USER;
import static com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.BILL_SERVER_USER;
import static com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.DRIVER_USER;
import static com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER;
import static com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.SERVICE_USER;
import static com.youming.youche.order.commons.AuditConsts.AUDIT_CODE.PAY_MANAGER;
import static com.youming.youche.order.commons.OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0;


/**
 * <p>
 * 支出接口表(提现接口表) 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class PayoutIntfServiceImpl extends BaseServiceImpl<PayoutIntfMapper, PayoutIntf> implements IPayoutIntfService {
    @Resource
    private PayOutIntfUtil payOutIntfUtil;
    @Autowired
    private IAccountBankRelService accountBankRelService;
    @Autowired
    private IOrderSchedulerService orderSchedulerService;
    @DubboReference(version = "1.0.0")
    private IServiceInfoService serviceInfoService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService payoutIntfThreeService;
    @Resource
    private ReadisUtil readisUtil;
    @Autowired
    private IOrderFeeExtService orderFeeExtService;
    @Autowired
    private IOrderFeeExtHService orderFeeExtHService;
    @Autowired
    private IBillPlatformService billPlatformService;
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    private ITenantVehicleRelService tenantVehicleRelService;
    @Lazy
    @Autowired
    private IOrderInfoService orderInfoService;
    @Resource
    private IOrderInfoHService orderInfoHService;
    @Autowired
    private IOrderSchedulerHService orderSchedulerHService;
    @Autowired
    private IOrderGoodsService orderGoodsService;
    @Resource
    private IOrderGoodsHService orderGoodsHService;
    @Autowired
    private IPayoutIntfExpansionService payoutIntfExpansionService;
    @Autowired
    private IPayFeeLimitService payFeeLimitService;
    @Autowired
    private IOrderLimitService orderLimitService;
    @DubboReference(version = "1.0.0")
    private ISysUserService sysUserService;
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;
    @Resource
    private BaseBusiToOrder baseBusiToOrder;
    @Resource
    private LoginUtils loginUtils;
    @Resource
    private PayoutIntfMapper payoutIntfMapper;
    @Resource
    private IPayoutIntfService iPayoutIntfService;
    @Resource
    private IOrderFeeService iOrderFeeService;
    @Resource
    private IOrderInfoExtService iOrderInfoExtService;
//    @Resource
//    private IOrderInfoService iOrderInfoService;
    @Resource
    private IBillSettingService iBillSettingService;
    @Resource
    private IRateService iRateService;
    @DubboReference(version = "1.0.0")
    private ISysStaticDataService sysStaticDataService;
    @Resource
    private IAcBusiOrderLimitRelService acBusiOrderLimitRelService;
    @Resource
    private IBusiSubjectsRelService busiSubjectsRelService;
    @Resource
    private IOrderFeeStatementService orderFeeStatementService;
    @Resource
    private IAdditionalFeeService additionalFeeService;

    @Resource
    IBaseBusiToOrderService iBaseBusiToOrderService;

    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;
    @Resource
    private IOaLoanService oaLoanService;

    @DubboReference(version = "1.0.0")
    IVoucherInfoService voucherInfoService;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @Resource
    IBaseBillInfoService baseBillInfoService;
    @Resource
    private IOilCardManagementService oilCardManagementService;


    @Resource
    IAccountBankUserTypeRelService iAccountBankUserTypeRelService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    IOrderAccountService iOrderAccountService;

    @DubboReference(version = "1.0.0")
    IServiceProductService iServiceProductService;

    @DubboReference(version = "1.0.0")
    IServiceInvitationService iServiceInvitationService;
    @Resource
    IPlatformServiceChargeService iPlatformServiceChargeService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;
    @DubboReference(version = "1.0.0")
    IAuditSettingService auditSettingService;
    @Resource
    IPayManagerService payManagerService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;
    @Resource
    IOrderFeeService orderFeeService;
    @Resource
    IOrderFeeHService orderFeeHService;
    @Resource
    OrderLimitMapper orderLimitMapper;
    @DubboReference(version = "1.0.0")
    ISysAttachService sysAttachService;

    @DubboReference(version = "1.0.0")
    IBankAccountService bankAccountService;

    @DubboReference(version = "1.0.0")
    IServiceProviderBillService iServiceProviderBillService;

    @DubboReference(version = "1.0.0")
    ISysRoleService sysRoleService;

    @Override
    public PayoutIntf createPayoutIntf(Long userId, Integer isDriver, String txnType, Long txnAmt, Long tenantId, String vehicleAffiliation, Long orderId, Long payTenantId, Integer isAutomatic, Integer isTurnAutomatic, Long payObjId, Integer payType, Long busiId, Long subjectsId, String oilAffiliation, Integer payUserType, Integer receUserType, Long billServiceFee,String token) {
        PayoutIntf pay = new PayoutIntf();
        pay.setUserId(userId);
        pay.setIsDriver(isDriver);
        pay.setTxnType(txnType);
        pay.setTxnAmt(txnAmt);
        pay.setTenantId(tenantId);
        pay.setVehicleAffiliation(vehicleAffiliation);
        pay.setOrderId(orderId);
        pay.setPayTenantId(payTenantId);
        pay.setIsAutomatic(isAutomatic);
        pay.setIsTurnAutomatic(isTurnAutomatic);
        pay.setPayObjId(payObjId);
        pay.setPayType(payType);
        pay.setBusiId(busiId);
        pay.setSubjectsId(subjectsId);
        pay.setOilAffiliation(oilAffiliation);
        pay.setBillServiceFee(billServiceFee);
        //会员体系开始
        pay.setUserType(receUserType);
        pay.setPayUserType(payUserType);
        //会员体系结束
        return pay;
    }

    @Override
    public void doSavePayOutIntfVirToVir(PayoutIntf payoutIntf,String accessToken) {
        //校验业务数据有效性
        payOutIntfUtil.checkPayOutInfToEnData(payoutIntf);
        payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);
        hasDefaultAcc(Long.parseLong(payoutIntf.getVehicleAffiliation()), SERVICE_USER);
        //获取打款账号
        BankAccInfoDto bankAccInfo = null;
        String accountNo = "";
        if((VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation()) || VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation()))
                && (payoutIntf.getSubjectsId().longValue() == Oil_PAYFOR_RECEIVABLE_IN
                || OIL_TURN_CASH_RECEIVABLE_IN == payoutIntf.getSubjectsId().longValue())){
            bankAccInfo = getVirtualAccInfo(payoutIntf.getPayObjId(),payoutIntf.getOilAffiliation(),0,payoutIntf.getPayType(),payoutIntf.getPayUserType());
        }else{
            bankAccInfo = getVirtualAccInfo(payoutIntf.getPayObjId(),payoutIntf.getVehicleAffiliation(),payoutIntf.getPayType(),0,payoutIntf.getPayTenantId(),payoutIntf.getOrderId(),payoutIntf.getIsDriver(),payoutIntf.getPayUserType());
        }
        if(SUBJECTIDS1131==payoutIntf.getSubjectsId().longValue()||SUBJECTIDS1133==payoutIntf.getSubjectsId().longValue()){
            AccountBankRel accountBankRel = accountBankRelService.getAcctNo(payoutIntf.getPayAccNo());
            if(accountBankRel!=null){
                payoutIntf.setPayAccNo(accountBankRel.getPinganPayAcctId());
                payoutIntf.setPayAccName(accountBankRel.getAcctName());
                payoutIntf.setPayBankAccName(accountBankRel.getAcctName());
                payoutIntf.setPayBankAccNo(accountBankRel.getAcctNo());
                payoutIntf.setAccountType(accountBankRel.getBankType()==BANK_TYPE_1?BUSINESS_PAYABLE_ACCOUNT:PRIVATE_PAYABLE_ACCOUNT);
            }
        }else{
            payoutIntf.setPayAccNo(bankAccInfo.getAccNo());
            payoutIntf.setPayAccName(bankAccInfo.getAccBankName());
            payoutIntf.setPayBankAccName(bankAccInfo.getAccBankName());
            payoutIntf.setPayBankAccNo(bankAccInfo.getAccBankNo());
            payoutIntf.setAccountType(bankAccInfo.getAccountType());
        }
        if(null != bankAccInfo.getIsAutomatic()){
            payoutIntf.setIsAutomatic(bankAccInfo.getIsAutomatic());
        }
        //获取收款账号
        String sysParameLuge = readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE+"").getCodeName();
        String sysParame56K = readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K+"").getCodeName();
        String sysPre = billPlatformService.getPrefixByUserId(Long.valueOf(payoutIntf.getVehicleAffiliation()));
        if(sysPre!=null&&(sysParameLuge.equals(sysPre)||sysParame56K.equals(sysPre)) && HAVIR == payoutIntf.getIsDriver()){//路歌的资金渠道,且资金走路歌

            if(SUBJECTIDS1131==payoutIntf.getSubjectsId().longValue()){
                if(sysParameLuge.equals(sysPre)){
                    List<AccountBankRel> bankList = accountBankRelService.getAccountBankList(payoutIntf.getPayTenantId(), payoutIntf.getPayAccName());
                    if(bankList==null||bankList.size()==0){
                        throw new BusinessException("获取不到路歌子账户信息，请检查");
                    }
                    payoutIntf.setAccNo(bankList.get(0).getPinganCollectAcctId());
                    payoutIntf.setAccName(bankList.get(0).getAcctName());
                    payoutIntf.setBankType(bankList.get(0).getBankType());
                    payoutIntf.setBankCode(bankList.get(0).getBankName());
                }else{
                    AccountBankRel accountBankRel = accountBankRelService.getDefaultAccountBankRel(Long.valueOf(payoutIntf.getVehicleAffiliation()), BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                    if(accountBankRel!=null){
                        payoutIntf.setAccNo(accountBankRel.getPinganCollectAcctId());
                        payoutIntf.setAccName(accountBankRel.getAcctName());
                        payoutIntf.setBankType(accountBankRel.getBankType());
                        payoutIntf.setBankCode(accountBankRel.getBankName());
                    }
                }
            }else{
                if(sysParameLuge.equals(sysPre)){
                    String billLookUp = "";//开票主体
                    OrderFeeExt orderFeeExt =  orderFeeExtService.getOrderFeeExt(payoutIntf.getOrderId());
                    OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(payoutIntf.getOrderId());
                    if(orderFeeExt!=null){
                        billLookUp = orderFeeExt.getBillLookUp();
                    }else{
                        billLookUp=orderFeeExtH.getBillLookUp();
                    }
                    List<AccountBankRel> bankList = accountBankRelService.getAccountBankList(payoutIntf.getPayTenantId(), billLookUp);
                    if(bankList==null||bankList.size()==0){
                        throw new BusinessException("获取不到路歌子账户信息，请检查");
                    }
                    payoutIntf.setAccNo(bankList.get(0).getPinganCollectAcctId());
                    payoutIntf.setAccName(bankList.get(0).getAcctName());
                    payoutIntf.setBankType(bankList.get(0).getBankType());
                    payoutIntf.setBankCode(bankList.get(0).getBankName());
                }else{
                    AccountBankRel accountBankRel = accountBankRelService.getDefaultAccountBankRel(Long.valueOf(payoutIntf.getVehicleAffiliation()), BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                    if(accountBankRel!=null){
                        payoutIntf.setAccNo(accountBankRel.getPinganCollectAcctId());
                        payoutIntf.setAccName(accountBankRel.getAcctName());
                        payoutIntf.setBankType(accountBankRel.getBankType());
                        payoutIntf.setBankCode(accountBankRel.getBankName());
                    }
                }
            }
        }else{
            if(SUBJECTIDS1131==payoutIntf.getSubjectsId().longValue()||SUBJECTIDS1133==payoutIntf.getSubjectsId().longValue()){
                if(VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation()) || VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation())){
                    AccountBankRel accountBankRel = accountBankRelService.getAcctNo(payoutIntf.getAccNo());
                    if(accountBankRel!=null){
                        payoutIntf.setAccNo(accountBankRel.getPinganCollectAcctId());
                        payoutIntf.setAccName(accountBankRel.getAcctName());
                        payoutIntf.setBankType(accountBankRel.getBankType());
                        payoutIntf.setBankCode(accountBankRel.getBankName());
                    }
                }
            }else{
                BankAccInfoDto bankAccInfo1 = null;
                if((VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation()) || VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation()))
                        &&  (payoutIntf.getSubjectsId().longValue() == Oil_PAYFOR_RECEIVABLE_IN
                        || OIL_TURN_CASH_RECEIVABLE_IN == payoutIntf.getSubjectsId().longValue())){
                    bankAccInfo1 = getVirtualAccInfo(payoutIntf.getUserId(),payoutIntf.getOilAffiliation(),1,payoutIntf.getIsDriver(),payoutIntf.getUserType());
                }else{
                    bankAccInfo1 = getVirtualAccInfo(payoutIntf.getUserId(),payoutIntf.getVehicleAffiliation(),payoutIntf.getIsDriver(),1,null,null,payoutIntf.getIsDriver(),payoutIntf.getUserType());
                }
                //        BankAccInfo bankAccInfo1 = getVirtualAccInfo(payoutIntf.getUserId(),payoutIntf.getVehicleAffiliation(),payoutIntf.getIsDriver(),1,null,null,payoutIntf.getIsDriver());
                payoutIntf.setAccNo(bankAccInfo1.getAccNo());
                payoutIntf.setAccName(bankAccInfo1.getAccBankName());
                payoutIntf.setBankType(bankAccInfo1.getBankType());
                payoutIntf.setBankCode(bankAccInfo1.getBankCode());
                if(null != bankAccInfo1.getIsAutomatic()){
                    payoutIntf.setIsAutomatic(bankAccInfo1.getIsAutomatic());
                }
            }
        }

        //获取收款方真实银行卡信息
        PinganBankInfoOutDto pinganBankInfoOut =  null;
        if(!VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation()) && !VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation())){
            pinganBankInfoOut = getPinganBankInfoOut(payoutIntf.getUserId(),"",payoutIntf.getUserType());
            //自有车开单要票打回车队随机获取车队绑定的对私收款账户 20190412加
            if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE) {
                pinganBankInfoOut = payOutIntfUtil.getRandomPinganBankInfoOut(payoutIntf.getUserId(),payoutIntf.getPayTenantId());
                if (pinganBankInfoOut == null) {
                    throw new BusinessException("收款方USER_ID["+payoutIntf.getUserId()+"]未绑定对私账户");
                }
            }else if (payoutIntf.getSubjectsId() == SUBJECTIDS1131
                    ||payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS1133) {
                pinganBankInfoOut = getPinganBankInfoOut(Long.valueOf(payoutIntf.getVehicleAffiliation()),payoutIntf.getAccNo(),payoutIntf.getUserType());
            }
        }else{
            pinganBankInfoOut = getPinganBankInfoOut(payoutIntf.getUserId(),"", SERVICE_USER);
        }
        if (null != pinganBankInfoOut) {
            String affiliation = payoutIntf.getVehicleAffiliation();//资金渠道
            if(payoutIntf.getUserType()== SERVICE_USER){//如果收款是油站服务商，需要判断油资金渠道
                ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(payoutIntf.getUserId());
                if(serviceInfo!=null&&serviceInfo.getServiceType()== SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL){
                    affiliation=payoutIntf.getOilAffiliation();
                }
            }
            if(VEHICLE_AFFILIATION1.equals(affiliation)){
                if(payoutIntf.getUserType() == DRIVER_USER ){
                    payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getPrivateAcctName());
                    payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getPrivateAcctNo());
                    payoutIntf.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                }else{
                    if(SUBJECTIDS1131==payoutIntf.getSubjectsId().longValue()||SUBJECTIDS1133==payoutIntf.getSubjectsId().longValue()){
                        AccountBankRel accountBankRel = accountBankRelService.getAcctNo(payoutIntf.getAccNo());
                        if(accountBankRel!=null){
                            payoutIntf.setReceivablesBankAccName(accountBankRel.getAcctNo());
                            payoutIntf.setReceivablesBankAccNo(accountBankRel.getAcctName());
                            payoutIntf.setBankCode(accountBankRel.getBankName());
                        }
                    }else{
                        payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getCorporateAcctName());
                        payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getCorporateAcctNo());
                        payoutIntf.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                    }
                }
            }else if(VEHICLE_AFFILIATION0.equals(affiliation)){
                payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getPrivateAcctName());
                payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getPrivateAcctNo());
                payoutIntf.setBankCode(pinganBankInfoOut.getPrivateBankCode());
            }else{
                if(payoutIntf.getUserType() == SERVICE_USER || payoutIntf.getUserType() == SERVER_CHILD_USER || payoutIntf.getUserType() == BILL_SERVER_USER){
                    payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getCorporateAcctName());
                    payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getCorporateAcctNo());
                    payoutIntf.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                    //20190412增加
                    payoutIntf.setPinganCollectAcctId(pinganBankInfoOut.getCorporatePinganAcctIdM());
                }else{
                    payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getPrivateAcctName());
                    payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getPrivateAcctNo());
                    payoutIntf.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                    //20190412增加
                    payoutIntf.setPinganCollectAcctId(pinganBankInfoOut.getPrivatePinganAcctIdM());
                }
//                if(StringUtils.isBlank(payoutIntf.getReceivablesBankAccNo())){
//                	throw new BusinessException("收款方没有绑定银行卡");
//                }
            }


        }
        //查票据服务商的联系方式
        if(!payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION0) && !payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION1)){
            BillPlatform billPlatform = billPlatformService.queryBillPlatformByUserId(Long.parseLong(payoutIntf.getVehicleAffiliation()));
            if(null != billPlatform){
                if(CommonUtil.isNumber(billPlatform.getLinkPhone())){
                    payoutIntf.setObjId(Long.parseLong(billPlatform.getLinkPhone()));
                }
            }
        }
        if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE ) {
            SysTenantDef tenantSysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
            if (payoutIntf.getReceivablesBankAccName() != null && payoutIntf.getReceivablesBankAccName().equals(tenantSysTenantDef.getActualController())) {
                payoutIntf.setIsAutomatic(AUTOMATIC0);
                payoutIntf.setIsTurnAutomatic(AUTOMATIC0);
            }
        }

        /**
         * 油到期，默认公对公
         */
        if(Oil_PAYFOR_RECEIVABLE_IN==payoutIntf.getSubjectsId().longValue()){
            AccountBankRel payAccountBankRel = accountBankRelService.getDefaultAccountBankRel(payoutIntf.getPayObjId(), OrderAccountConst.BANK_TYPE.TYPE1, payoutIntf.getPayUserType());
            if(payAccountBankRel!=null){
                payoutIntf.setPayAccNo(payAccountBankRel.getPinganPayAcctId());
                payoutIntf.setPayAccName(payAccountBankRel.getAcctName());
                payoutIntf.setPayBankAccName(payAccountBankRel.getAcctName());
                payoutIntf.setPayBankAccNo(payAccountBankRel.getAcctNo());
                payoutIntf.setAccountType(payAccountBankRel.getBankType()==1?BUSINESS_PAYABLE_ACCOUNT:PRIVATE_PAYABLE_ACCOUNT);
            }

            AccountBankRel receivaAccountBankRel = accountBankRelService.getDefaultAccountBankRel(payoutIntf.getUserId(), OrderAccountConst.BANK_TYPE.TYPE1, payoutIntf.getUserType());
            if(receivaAccountBankRel!=null){
                payoutIntf.setAccNo(receivaAccountBankRel.getPinganCollectAcctId());
                payoutIntf.setAccName(receivaAccountBankRel.getAcctName());
                payoutIntf.setBankType(receivaAccountBankRel.getBankType());
                payoutIntf.setBankCode(receivaAccountBankRel.getBankName());
                payoutIntf.setReceivablesBankAccName(receivaAccountBankRel.getAcctName());
                payoutIntf.setReceivablesBankAccNo(receivaAccountBankRel.getAcctNo());
            }

        }
        //判断如果付款账户没有绑卡，自动推到线下
        if(StringUtils.isBlank(payoutIntf.getPayBankAccNo())){
            payoutIntf.setIsAutomatic(AUTOMATIC0);
        }
        this.save(payoutIntf);
        this.doSavePayoutInfoExpansion(payoutIntf,accessToken);
    }

    @Override
    public void doSavePayoutInfoExpansion(PayoutIntf payoutIntf,String accessToken) {
        PayoutIntfExpansion payoutIntfExpansion  = payoutIntfExpansionService.getPayoutIntfExpansion(payoutIntf.getId());
        if(!payoutIntf.getUserType().equals(ADMIN_USER)){
            payoutIntf.setTenantId(-1L);
        }
        if(!payoutIntf.getPayUserType().equals(ADMIN_USER)){
            payoutIntf.setPayTenantId(-1L);
        }
        this.update(payoutIntf);
        if(payoutIntfExpansion == null){
            PayoutIntfExpansion expansion = new PayoutIntfExpansion();
            expansion.setFlowId(payoutIntf.getId());
            if(payoutIntf.getPlateNumber() != null){
                expansion.setPlateNumber(payoutIntf.getPlateNumber());
            }
            if(payoutIntf.getBusiCode() != null){
                expansion.setBusiCode(payoutIntf.getBusiCode());
            }
            //票据类型
            if(payoutIntf.getIsNeedBill() != null){
                expansion.setIsNeedBill(payoutIntf.getIsNeedBill());
            }else if(payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN || payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN){
                if(payoutIntf.getOilAffiliation().equals(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0) || payoutIntf.getOilAffiliation().equals(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1)){
                    expansion.setIsNeedBill(Integer.valueOf(payoutIntf.getOilAffiliation()));
                }else {
                    expansion.setIsNeedBill(Integer.valueOf(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION2));
                }
            }else {
                if(payoutIntf.getVehicleAffiliation().equals(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0) || payoutIntf.getVehicleAffiliation().equals(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1)){
                    expansion.setIsNeedBill(Integer.valueOf(payoutIntf.getVehicleAffiliation()));
                }else {
                    expansion.setIsNeedBill(Integer.valueOf(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION2));
                }
            }
            //订单相关信息
            if(payoutIntf.getOrderId() != null&&payoutIntf.getOrderId()>0L){
                OrderInfo order = orderInfoService.getOrder(payoutIntf.getOrderId());
                OrderInfoH orderH = orderInfoHService.getOrderH(payoutIntf.getOrderId());
                OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(payoutIntf.getOrderId());
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.selectByOrderId(payoutIntf.getOrderId());
                OrderGoods orderGoods = orderGoodsService.getOrderGoods(payoutIntf.getOrderId());
                OrderGoodsH orderGoodsH = orderGoodsHService.selectByOrderId(payoutIntf.getOrderId());
                OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(payoutIntf.getOrderId());
                OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(payoutIntf.getOrderId());
                if(order != null && order.getOrderId() != null){
                    if(StringUtils.isNotEmpty(order.getRemark())){
                        expansion.setOrderRemark(order.getRemark());
                    }
                    if(order.getOrgId() != null && order.getOrgId() > 0L){
                        expansion.setOrgId(order.getOrgId().longValue());
                    }
                    if(order.getSourceRegion() != null){
                        expansion.setSourceRegion(order.getSourceRegion());
                    }
                    if(order.getDesRegion() != null){
                        expansion.setDesRegion(order.getDesRegion());
                    }
                }
                else if(orderH!=null && orderH.getOrderId() != null) {
                    if(StringUtils.isNotEmpty(orderH.getRemark())){
                        expansion.setOrderRemark(orderH.getRemark());
                    }
                    if(orderH.getOrgId() != null && orderH.getOrgId() > 0L){
                        expansion.setOrgId(orderH.getOrgId().longValue());
                    }
                    if(orderH.getSourceRegion() != null){
                        expansion.setSourceRegion(orderH.getSourceRegion());
                    }
                    if(orderH.getDesRegion() != null){
                        expansion.setDesRegion(orderH.getDesRegion());
                    }
                }
                if(orderScheduler != null && orderScheduler.getOrderId() != null){
                    if(orderScheduler.getDependTime() != null){
                        expansion.setDependTime(orderScheduler.getDependTime());
                    }
                    if(StringUtils.isNotEmpty(orderScheduler.getSourceName())){
                        expansion.setSourceName(orderScheduler.getSourceName());
                    }
                    if(StringUtils.isNotEmpty(orderScheduler.getCollectionUserName())){
                        expansion.setCollectionUserName(orderScheduler.getCollectionUserName());
                    }
                }
                else if(orderSchedulerH != null && orderSchedulerH.getOrderId() != null){
                    if(orderSchedulerH.getDependTime() != null){
                        expansion.setDependTime(orderSchedulerH.getDependTime());
                    }
                    if(StringUtils.isNotEmpty(orderSchedulerH.getSourceName())){
                        expansion.setSourceName(orderSchedulerH.getSourceName());
                    }
                    if(StringUtils.isNotEmpty(orderSchedulerH.getCollectionUserName())){
                        expansion.setCollectionUserName(orderSchedulerH.getCollectionUserName());
                    }
                }
                if(orderGoods != null && orderGoods.getOrderId() !=null){
                    if(StringUtils.isNotEmpty(orderGoods.getCustomName())){
                        expansion.setCustomName(orderGoods.getCustomName());
                    }
                }
                else if(orderGoodsH != null && orderGoodsH.getOrderId() != null){
                    if(StringUtils.isNotEmpty(orderGoodsH.getCustomName())){
                        expansion.setCustomName(orderGoodsH.getCustomName());
                    }
                }
                if(orderFeeExt !=null && orderFeeExt.getOrderId() != null){
                    if(StringUtils.isNotEmpty(orderFeeExt.getBillLookUp())){
                        expansion.setBillLookUp(orderFeeExt.getBillLookUp());
                    }
                }
                else if(orderFeeExtH != null && orderFeeExtH.getOrderId() != null){
                    if(StringUtils.isNotEmpty(orderFeeExtH.getBillLookUp())){
                        expansion.setBillLookUp(orderFeeExtH.getBillLookUp());
                    }
                }
            }
            //车辆相关信息
            if(StringUtils.isNotEmpty(payoutIntf.getPlateNumber()) && payoutIntf.getPayTenantId() > 0){
                TenantVehicleRel tenantVehicleRel = tenantVehicleRelService.getTenantVehicleRel(payoutIntf.getPlateNumber(),payoutIntf.getPayTenantId());
                if(tenantVehicleRel!=null && tenantVehicleRel.getOrgId() !=null && tenantVehicleRel.getOrgId() >0){
                    expansion.setVehicleOrgId(tenantVehicleRel.getOrgId());
                }
            }
            payoutIntfExpansionService.save(expansion);
            //虚拟到虚拟并且是线下的打款，需要启动审核流程
            if(payoutIntf.getTxnType()== OrderAccountConst.TXN_TYPE.XX_TXN_TYPE&&payoutIntf.getIsAutomatic()==IS_TURN_AUTOMATIC_0){
                this.startAuditProcess(payoutIntf,accessToken);
            }
        }
    }

    @Override
    public List<PayoutIntf> queryPayoutIntf(Long businessId, Long subjectsId, Long orderId) {
        LambdaQueryWrapper<PayoutIntf> lambda= Wrappers.lambdaQuery();
        lambda.eq(PayoutIntf::getBusiId,businessId)
              .eq(PayoutIntf::getSubjectsId,subjectsId)
              .eq(PayoutIntf::getOrderId,orderId);
        return this.list(lambda);
    }

    @Override
    public List<PayoutIntf> queryPayoutIntf(List<Long> subjectsId, Long orderId) {
        LambdaQueryWrapper<PayoutIntf> lambda= Wrappers.lambdaQuery();
        lambda.eq(PayoutIntf::getTxnType,OrderAccountConst.TXN_TYPE.XX_TXN_TYPE)
                .in(PayoutIntf::getSubjectsId,subjectsId)
                .eq(PayoutIntf::getOrderId,orderId);
        return this.list(lambda);
    }
    @Override
    public void payAddSubsidy(AcOrderSubsidyInDto acOrderSubsidyIn, LoginInfo user,String token) {
        if(null == acOrderSubsidyIn.getDriverSubsidy() || acOrderSubsidyIn.getDriverSubsidy().longValue() == 0){
            throw new BusinessException("补贴金额错误");
        }
        //创建付款记录
        if(acOrderSubsidyIn.getDriverSubsidy().longValue() > 0){
            acOrderSubsidyIn.setSubjectId(EnumConsts.SubjectIds.SUBJECTIDS1812);
        }else if(acOrderSubsidyIn.getDriverSubsidy().longValue() < 0){
            long amt =  Math.abs(acOrderSubsidyIn.getDriverSubsidy().longValue());
            acOrderSubsidyIn.setDriverSubsidy(amt);
            acOrderSubsidyIn.setSubjectId(EnumConsts.SubjectIds.SUBJECTIDS1814);
        }
        PayoutIntf payoutIntf = createPayoutIntf(acOrderSubsidyIn,token);
        //操作账户
        this.createAccountDetailForPayout(payoutIntf,user);
    }

    @Override
    public void paySubsidy(AcOrderSubsidyInDto acOrderSubsidyIn, LoginInfo loginInfo,String token) {
        //创建order_limit记录
        orderLimitService.createOrderLimit(acOrderSubsidyIn);
        //创建付款记录
        acOrderSubsidyIn.setSubjectId(EnumConsts.SubjectIds.SUBJECTIDS1810);
        PayoutIntf payoutIntf = createPayoutIntf(acOrderSubsidyIn,token);
        //操作账户
        this.createAccountDetailForPayout(payoutIntf,loginInfo);
    }

    /**提现记录失效*/
    public static final String respCodeInvalid = "5";

    @Override
    public boolean judgeOrderIsPaid(Long orderId) {
        boolean flag = false;
        List<Long> subjectsId = new ArrayList<Long>();
        subjectsId.add(BEFORE_RECEIVABLE_OVERDUE_SUB);//预付款
        subjectsId.add(EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB);//异常补偿
        subjectsId.add(EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN);//预支
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP);
        subjectsId.add(EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP);
        subjectsId.add(EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN);
        subjectsId.add(EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN);
        subjectsId.add(EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE);
        subjectsId.add(EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN);
        subjectsId.add(EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN);
        List<PayoutIntf> list = this.queryPayoutIntf(subjectsId, orderId);
        for (PayoutIntf pay : list) {
            String respCode = pay.getRespCode();
            if (pay.getIsAutomatic() == AUTOMATIC1 && respCode != null && respCode != respCodeInvalid) {
                flag = true;
            }
        }
        return flag;
    }

    private void createAccountDetailForPayout(PayoutIntf payoutIntf, LoginInfo user){
        //获取付款用户信息
        UserDataInfo payUserDataInfo = userDataInfoService.get(payoutIntf.getPayObjId());
        if(null == payUserDataInfo){
            throw new BusinessException("付款用户信息不存在,USER_ID : " + payoutIntf.getPayObjId());
        }
        //获取收款用户信息
        UserDataInfo receivablesUserDataInfo = userDataInfoService.get(payoutIntf.getUserId());
        if(null == receivablesUserDataInfo){
            throw new BusinessException("收款用户信息不存在,USER_ID : " + payoutIntf.getUserId());
        }

        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrder();
        UserDataInfo[] userDataInfoArr = new UserDataInfo[2];
        userDataInfoArr[0] = payUserDataInfo;
        userDataInfoArr[1] = receivablesUserDataInfo;
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf,false);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf,orderAccounts,userDataInfoArr,false,user);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf,userDataInfoArr,"in",user);
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
    }

    //启动审核流程
    @Override
    public void startAuditProcess(PayoutIntf payoutIntf,String accessToken) {
        //启动审核流程
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("flowId", payoutIntf.getId());
        boolean bool = false;
        if(payoutIntf.getPayTenantId()>0L){
            //todo 审核
            bool = auditService.startProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, payoutIntf.getId(), SysOperLogConst.BusiCode.Payoutchunying, params,accessToken,payoutIntf.getPayTenantId());
        }else{
            bool = true;//如果付款不是车队，不需要启动审核流程
//	\
        }
        if (!bool) {
            throw new BusinessException("启动现金付款审核流程失败！");
        }
    }


    private void hasDefaultAcc(long userId,Integer userType){
        if(userId == 0 || userId == 1){
            return;
        }
        AccountBankRel accountBankRel = accountBankRelService.getDefaultAccountBankRel(userId,BANK_TYPE_1,userType);
        if(null == accountBankRel){
            log.error("收款方没有设置默认账户,userId:"+userId+",userType:"+userType);
            throw new BusinessException("收款方没有设置默认账户");
        }
    }

    /**
     *
     * @param userId
     * @param oilAffiliation
     * @param flg
     * @param isDriver
     * @param userType
     * @return
     * @throws Exception
     */
    public BankAccInfoDto getVirtualAccInfo(Long userId,String oilAffiliation,Integer flg,Integer isDriver,Integer userType){
        BankAccInfoDto bankAccInfo = new BankAccInfoDto();
        PinganBankInfoOutDto pinganBankInfoOut = null;
        pinganBankInfoOut = getPinganBankInfoOut(userId,"",userType);
        if(null != pinganBankInfoOut){
            if(VEHICLE_AFFILIATION0.equals(oilAffiliation)){
                if(0 == flg){

                    bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                }else{
                    if(isDriver == OrderAccountConst.PAY_TYPE.SERVICE){
                        //服务商到期打款时
                        //不需要平台开票的资金，如果服务商没有绑定对私帐户，则打给服务商对公帐户；如果服务商绑定了对私账户，则打款给对私账户。
                        if(StringUtils.isNotBlank(pinganBankInfoOut.getPrivatePinganAcctIdM())){
                            bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                        }else{
                            bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                        }
                    }else{

                        bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                    }
                }
            }else{
                if(0 == flg){

                    bankAccInfo.setCorporatePinganAcctIdN(pinganBankInfoOut);
                }else{
                    bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                }
            }
            if(isDriver == OrderAccountConst.PAY_TYPE.USER){
                //付款--取付款账号
                if(0 == flg){
                    bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                }
                //取收款账号
                else{
                    bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                }
            }

        }else{
            bankAccInfo.setIsAutomatic(AUTOMATIC0);
        }
        return bankAccInfo;
    }


    public BankAccInfoDto getVirtualAccInfoNew(Long userId,String oilAffiliation,Integer flg,Integer isDriver,Integer userType,String accountNo){
        BankAccInfoDto bankAccInfo = new BankAccInfoDto();
        PinganBankInfoOutDto pinganBankInfoOut = null;
        pinganBankInfoOut = getPinganBankInfoOut(userId,accountNo,userType);
        if(null != pinganBankInfoOut){
            if(VEHICLE_AFFILIATION0.equals(oilAffiliation)){
                if(0 == flg){

                    bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                }else{
                    if(isDriver == OrderAccountConst.PAY_TYPE.SERVICE){
                        //服务商到期打款时
                        //不需要平台开票的资金，如果服务商没有绑定对私帐户，则打给服务商对公帐户；如果服务商绑定了对私账户，则打款给对私账户。
                        if(StringUtils.isNotBlank(pinganBankInfoOut.getPrivatePinganAcctIdM())){
                            bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                        }else{
                            bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                        }
                    }else{

                        bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                    }
                }
            }else{
                if(0 == flg){

                    bankAccInfo.setCorporatePinganAcctIdN(pinganBankInfoOut);
                }else{
                    bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                }
            }
            if(isDriver == OrderAccountConst.PAY_TYPE.USER){
                //付款--取付款账号
                if(0 == flg){
                    bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                }
                //取收款账号
                else{
                    bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                }
            }

        }else{
            bankAccInfo.setIsAutomatic(AUTOMATIC0);
        }
        return bankAccInfo;
    }




    public BankAccInfoDto getVirtualAccInfo(Long userId,String vehicleAffiliation,Integer type,Integer flg,Long tenantId,Long orderId,Integer isDirver,Integer userType){
        BankAccInfoDto bankAccInfo = new BankAccInfoDto();
        PinganBankInfoOutDto pinganBankInfoOut = null;
        //获取司机虚拟账号
        if(type == OrderAccountConst.PAY_TYPE.USER){
            //不需要开票取对私账号
            pinganBankInfoOut = getPinganBankInfoOut(userId,"",userType);
            if(null != pinganBankInfoOut){
                //付款--取付款账号
                if(0 == flg){
                    bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                }
                //取收款账号
                else{
                    bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                }
            }else{
                bankAccInfo.setIsAutomatic(AUTOMATIC0);
                bankAccInfo.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
            }

        }else if(type == OrderAccountConst.PAY_TYPE.SERVICE){
            //不需要开票取对私账号
            pinganBankInfoOut = getPinganBankInfoOut(userId,"",userType);
            if(VEHICLE_AFFILIATION0.equals(vehicleAffiliation)){
                if(null != pinganBankInfoOut){
                    //付款--取付款账号
                    if(0 == flg){
                        bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                    }
                    //取收款账号
                    else{
                        //服务商到期打款时
                        //不需要平台开票的资金，如果服务商没有绑定对私帐户，则打给服务商对公帐户；如果服务商绑定了对私账户，则打款给对私账户。
                        if(StringUtils.isNotBlank(pinganBankInfoOut.getPrivatePinganAcctIdM())){
                            bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                        }else{
                            bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                        }
                    }

                }else{
                    bankAccInfo.setIsAutomatic(AUTOMATIC0);
                    bankAccInfo.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
                }
            }else{
                if(null != pinganBankInfoOut){
                    bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                }else{
                    bankAccInfo.setIsAutomatic(AUTOMATIC0);
                    bankAccInfo.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
                }
            }
        }else if(type == OrderAccountConst.PAY_TYPE.TENANT){
            //不需要开票取对私账号
            if(VEHICLE_AFFILIATION0.equals(vehicleAffiliation)){
                pinganBankInfoOut = getPinganBankInfoOut(userId,"",userType);
                if(null != pinganBankInfoOut){
                    //付款--取付款账号
                    if(0 == flg){
                        //支付自有车运费的时候取车队的默认付款账户
                        boolean els = false;
                        if(null != orderId && -1 != orderId.longValue() && 0!=orderId.longValue() && isDirver==OrderAccountConst.PAY_TYPE.USER){
                            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                            if(null != orderScheduler && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()){
                                els =  true;
                            }
                        }
                        if(els){
                            long accountType =  payFeeLimitService.getAmountLimitCfgVal(tenantId,OWN_CAR_TRANSFER_SALARY_402);
                            if(accountType == BANK_TYPE_0){
                                bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                            }else{
                                bankAccInfo.setCorporatePinganAcctIdN(pinganBankInfoOut);
                            }
                        }else{
                            bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                        }
                    }
                    //取收款账号
                    else{
                        bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                    }

                }else{
                    bankAccInfo.setIsAutomatic(AUTOMATIC0);
                    bankAccInfo.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
                }
            }
            //开票对公账号
            else{
                if(!VEHICLE_AFFILIATION1.equals(vehicleAffiliation)&&null != orderId && -1 != orderId.longValue() && 0!=orderId.longValue()){
                	/*IOrderLimitSV orderLimitSV = (IOrderLimitSV)SysContexts.getBean("orderLimitSV");
                	OrderLimit ol =orderLimitSV.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);*/
                    pinganBankInfoOut = getPinganBankInfoOut(userId,"",userType);
                }else{
                    pinganBankInfoOut = getPinganBankInfoOut(userId,"",userType);
                }
                if(null != pinganBankInfoOut){
                    //付款--取付款账号
                    if(0 == flg){
                        //取车队的付款账户,如用油费预存资金，取预存资金账户打款，否则取默认账户
                        bankAccInfo.setCorporatePinganAcctIdN(pinganBankInfoOut);
                    }
                    //取收款账号
                    else{
                        bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                    }
                }else{
                    bankAccInfo.setIsAutomatic(AUTOMATIC0);
                    bankAccInfo.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
                }
            }
        }

        //平台票据对公账号
        else if(type == HAVIR){
            userId = Long.parseLong(vehicleAffiliation);
            pinganBankInfoOut = getPinganBankInfoOut(userId,"", SERVICE_USER);
            if(null == pinganBankInfoOut){
                throw new BusinessException("获取票据服务商银行信息错误！");
            }
            //付款--取付款账号
            if(0 == flg){
                bankAccInfo.setCorporatePinganAcctIdN(pinganBankInfoOut);
            }
            //取收款账号
            else{
                bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
            }
        }
        return bankAccInfo;
    }

    @Override
    public  PinganBankInfoOutDto getPinganBankInfoOut(Long userId, String accountNo, Integer userType) {
        List<AccountBankRel> accountBankRelList = accountBankRelService.queryAccountBankRel(userId, userType, -1);
        if(accountBankRelList!=null&&!accountBankRelList.isEmpty()){
            if(StringUtils.isNotBlank(accountNo)){
                return getPinganBankInfoOutByNo(accountNo,accountBankRelList);
            }else{
                return getPinganBankInfoOutDefault(userId,userType);
            }
        }
        return null;
    }
    public  PinganBankInfoOutDto getPinganBankInfoOutByNo(String accountNo,List<AccountBankRel> accountBankRelList)  {
        PinganBankInfoOutDto pinganBankInfoOut = new PinganBankInfoOutDto();
        for (AccountBankRel o : accountBankRelList) {
            if(accountNo.equals(o.getPinganCollectAcctId()) || accountNo.equals(o.getPinganPayAcctId())){
                pinganBankInfoOut.setIsDefaultAcct(o.getIsDefaultAcct());
                if(o.getBankType().intValue()== BANK_TYPE_0){
                    pinganBankInfoOut.setPrivatePinganAcctIdM(o.getPinganCollectAcctId());
                    pinganBankInfoOut.setPrivatePinganAcctIdN(o.getPinganPayAcctId());
                    pinganBankInfoOut.setPrivateAcctName(o.getAcctName());
                    pinganBankInfoOut.setPrivateAcctNo(o.getAcctNo());
                    pinganBankInfoOut.setPrivateBankCode(o.getBankName());
                }else if(o.getBankType().intValue()== BANK_TYPE_1){
                    pinganBankInfoOut.setCorporatePinganAcctIdM(o.getPinganCollectAcctId());
                    pinganBankInfoOut.setCorporatePinganAcctIdN(o.getPinganPayAcctId());
                    pinganBankInfoOut.setCorporateAcctName(o.getAcctName());
                    pinganBankInfoOut.setCorporateAcctNo(o.getAcctNo());
                    pinganBankInfoOut.setCorporateBankCode(o.getBankName());
                }else if(o.getBankType().intValue()== EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_SERVICE){
                    pinganBankInfoOut.setServicePinganAcctIdM(o.getPinganCollectAcctId());
                    pinganBankInfoOut.setServicePinganAcctIdN(o.getPinganPayAcctId());
                    pinganBankInfoOut.setServiceAcctName(o.getAcctName());
                    pinganBankInfoOut.setServiceAcctNo(o.getAcctNo());
                    pinganBankInfoOut.setServiceBankCode(o.getBankName());
                }
                return pinganBankInfoOut;
            }
        }
        return pinganBankInfoOut;
    }
    public  PinganBankInfoOutDto getPinganBankInfoOutDefault(Long userId,Integer userType) {
        PinganBankInfoOutDto pinganBankInfoOut = new PinganBankInfoOutDto();
        AccountBankRel privateAccount = accountBankRelService.getDefaultAccountBankRel(userId, BANK_TYPE_0, userType);
        if(privateAccount!=null){
            pinganBankInfoOut.setPrivatePinganAcctIdM(privateAccount.getPinganCollectAcctId());
            pinganBankInfoOut.setPrivatePinganAcctIdN(privateAccount.getPinganPayAcctId());
            pinganBankInfoOut.setPrivateAcctName(privateAccount.getAcctName());
            pinganBankInfoOut.setPrivateAcctNo(privateAccount.getAcctNo());
            pinganBankInfoOut.setPrivateBankCode(privateAccount.getBankName());
        }
        AccountBankRel corporateAccount = accountBankRelService.getDefaultAccountBankRel(userId, BANK_TYPE_1, userType);
        if(corporateAccount!=null){
            pinganBankInfoOut.setCorporatePinganAcctIdM(corporateAccount.getPinganCollectAcctId());
            pinganBankInfoOut.setCorporatePinganAcctIdN(corporateAccount.getPinganPayAcctId());
            pinganBankInfoOut.setCorporateAcctName(corporateAccount.getAcctName());
            pinganBankInfoOut.setCorporateAcctNo(corporateAccount.getAcctNo());
            pinganBankInfoOut.setCorporateBankCode(corporateAccount.getBankName());
        }
        return pinganBankInfoOut;
    }


    private PayoutIntf createPayoutIntf(AcOrderSubsidyInDto acOrderSubsidyIn,String token){
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(acOrderSubsidyIn.getDriverUserId(),
                acOrderSubsidyIn.getOrderId(),-1);
        if (ol == null) {
            throw new BusinessException("根据订单号：" + acOrderSubsidyIn.getOrderId() + " 用户ID：" + acOrderSubsidyIn.getDriverUserId() + " 未找到订单限制记录");
        }
        //生成待打款支付记录
        //是否自动打款
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(acOrderSubsidyIn.getTenantId());
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        Long tenantUserId = sysTenantDefService.getSysTenantDef(acOrderSubsidyIn.getTenantId()).getAdminUser();
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        PayoutIntf payoutIntf = null;
        SysUser sysOperator = null;
        if(acOrderSubsidyIn.getSubjectId().longValue() == EnumConsts.SubjectIds.SUBJECTIDS1812 || acOrderSubsidyIn.getSubjectId().longValue() == EnumConsts.SubjectIds.SUBJECTIDS1810){
            payoutIntf = this.createPayoutIntf(acOrderSubsidyIn.getDriverUserId(), OrderAccountConst.PAY_TYPE.USER,
                    OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, acOrderSubsidyIn.getDriverSubsidy(), -1L, acOrderSubsidyIn.getVehicleAffiliation(),
                    acOrderSubsidyIn.getOrderId(), acOrderSubsidyIn.getTenantId(), isAutomatic, isAutomatic, tenantUserId,
                    OrderAccountConst.PAY_TYPE.TENANT, BEFORE_PAY_CODE, acOrderSubsidyIn.getSubjectId(),
                    acOrderSubsidyIn.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,ol.getUserType(),0L,token);
            sysOperator = sysUserService.getSysOperatorByUserId(acOrderSubsidyIn.getDriverUserId());
        }else if(acOrderSubsidyIn.getSubjectId().longValue() == EnumConsts.SubjectIds.SUBJECTIDS1814){
            payoutIntf = this.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.TENANT,
                    OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, acOrderSubsidyIn.getDriverSubsidy(),
                    acOrderSubsidyIn.getTenantId(), acOrderSubsidyIn.getVehicleAffiliation(),
                    acOrderSubsidyIn.getOrderId(),
                    -1L, isAutomatic, isAutomatic, acOrderSubsidyIn.getDriverUserId(),
                    OrderAccountConst.PAY_TYPE.USER,
                    BEFORE_PAY_CODE, acOrderSubsidyIn.getSubjectId(),
                    acOrderSubsidyIn.getOilAffiliation(),ol.getUserType(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,0L,token);
            sysOperator = sysUserService.getSysOperatorByUserId(tenantUserId);
        }
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
        payoutIntf.setRemark("现金(司机补贴)");

        if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(acOrderSubsidyIn.getVehicleAffiliation()) &&
                !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(acOrderSubsidyIn.getVehicleAffiliation())) {
            payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
        }
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(acOrderSubsidyIn.getOrderId());
        payoutIntf.setBusiCode(String.valueOf(acOrderSubsidyIn.getOrderId()));
        if (orderScheduler != null) {
            payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
        }
        this.doSavePayOutIntfVirToVir(payoutIntf,token);
        return payoutIntf;
    }

    /**
     * 付款信息
     */
    @Override
    public PayoutIntf getPayoutIntfPay(Long flowId, Long tenantId,String accessToken)  {
        LoginInfo user = loginUtils.get(accessToken);
        tenantId = user.getTenantId();
        if(user == null || user.getTenantId() == null){
            throw new BusinessException("用户未登录！");
        }
        QueryWrapper<PayoutIntf> queryWrapper = new QueryWrapper<>();
        if (tenantId>-1){
            queryWrapper.eq("id", flowId);
            queryWrapper.eq("pay_tenant_id", tenantId);
        }else {
            queryWrapper.eq("id", flowId);
            queryWrapper.eq("pay_tenant_id", user.getTenantId());
        }
        return payoutIntfMapper.selectOne(queryWrapper);
    }

    /**
     * 计算服务费
     * @param cash
     * @param flowId
     * @return
     * @throws Exception
     */
    @Override
    public Long queryOrdServiceFee(Long cash, Long flowId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (user == null || user.getTenantId() == null) {
            throw new BusinessException("用户未登录！");
        }

        PayoutIntf payout = this.getById(flowId);

        if (cash <= 0) {
            cash = payout.getTxnAmt();
        }
        //异常 油卡押金
        long billServiceFee = 0;
        /**
         * 查询该运单已打款运费金额
         */
        long fee = iPayoutIntfService.getPayoutIntfBySubFee(payout.getOrderId(),accessToken);
        long totalCash = 0;

        if (payout.getOrderId() == null) {
            return 0L;
        }
        long orderId = payout.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(payout.getOrderId());
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);


        if (payout.getVehicleAffiliation() != null && Long.valueOf(payout.getVehicleAffiliation()) <= 10L) {
            return 0L;
        }
        totalCash = iPayoutIntfService.getPayoutIntfBySubFee(payout.getOrderId(),accessToken);

        if ((fee + payout.getTxnAmt()) == totalCash) {
            //运单总服务费
            Map resMap = iPayoutIntfService.doQueryServiceFee(totalCash, flowId,accessToken);
            long serviceFee = DataFormat.getLongKey(resMap, "56kServiceFee");

            //已支付服务费
            long payervice = iPayoutIntfService.getPayoutIntfBySubFee(payout.getOrderId(),accessToken);
            if (serviceFee > payervice) {
                billServiceFee = serviceFee - payervice;
            }
        } else {
            Map resMap = iPayoutIntfService.doQueryServiceFee(cash, flowId,accessToken);
            String serviceFee = DataFormat.getStringKey(resMap, "ServiceFee");
            billServiceFee = com.youming.youche.util.CommonUtils.multiply(serviceFee);
        }
        return billServiceFee;

    }

    @Override
    public Map doQueryServiceFee(Long cash, Long flowId,String accessToken)   {
        LoginInfo user = loginUtils.get(accessToken);
        if (user == null || user.getTenantId() == null) {
            throw new BusinessException("用户未登录！");
        }
        Long tenantId = user.getTenantId();
        if(flowId == null || flowId <= 0){
            throw new BusinessException("请传入业务单号");
        }
        PayoutIntf payout = baseMapper.selectById(flowId);
        Long openUserId = Long.valueOf(payout.getVehicleAffiliation());
        //如果费用科目是服务费，则不计算服务费
        if(payout.getSubjectsId() == BILL_SERVICE_RECEIVABLE_OVERDUE_SUB || payout.getSubjectsId() == BILL_56K_RECEIVABLE_OVERDUE_SUB ){
            return new HashMap();
        }
        if(cash <= 0 ){
            cash = 	payout.getTxnAmt();
        }
        if (openUserId == null || openUserId <= 10) {
            return new HashMap();
        }
        if (cash == null || cash < 0) {
            throw new BusinessException("现金金额不合法");
        }

        BillSetting billSetting = iBillSettingService.getBillSetting(tenantId);
        if (billSetting == null) {
            throw new BusinessException("根据租户ID: " + tenantId + " 未找到车队开票信息");
        }
        if (billSetting.getRateId() == null || billSetting.getRateId() <= 0) {
            throw new BusinessException("根据租户ID: " + tenantId + " 未找到车队开票费率设置");
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        BillPlatform bpf = billPlatformService.queryAllBillPlatformByUserId(openUserId);
        if (bpf == null) {
            throw new BusinessException("根据开票平台用户id：" + openUserId + " 未找到开票平台信息");
        }
        if (bpf.getServiceFeeFormula() == null) {
            throw new BusinessException("根据开票平台用户id： " + openUserId + " 未找到开票服务计算公式");
        }
        String tempFormula = bpf.getServiceFeeFormula();
        String formula = tempFormula.replace("（", "(").replace("）", ")").replace("%", "/100.0");
        String tempHaCostFormula = bpf.getHaCost();
        String haCostFormula = tempHaCostFormula.replace("（", "(").replace("）", ")").replace("%", "/100.0");
        //开票率
        Double billRate = iRateService.getRateValue(billSetting.getRateId(), cash);
        if (billRate == null) {
            throw new BusinessException("根据车队费率id： " + billSetting.getRateId() + " 开票金额" + (double)cash/100 + " 未找到开票费率");
        }
        long oil = 0;
        long etc = 0;
        engine.put("d", cash);
        engine.put("o", oil);
        engine.put("e", etc);
        engine.put("r", billRate / 100);
        Double serviceFee = 0.0;
        try {
            serviceFee = (Double) engine.eval(formula);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Double serviceCost = 0.0;
        try {
            serviceCost = (Double) engine.eval(haCostFormula);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        long billServiceFee = Math.round(serviceFee);//serviceFee.longValue();//截取
        long billServiceCost = serviceCost.longValue();//截取
        long lugeBillServiceFee = Math.round(serviceFee);//四舍五入
        long lugeBillServiceCost = Math.round(serviceCost);//四舍五入
        //56k服务费算法直接截取，路歌服务费四舍五入，比如123.678分，56K: 123分，路歌：124分
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ServiceFee", com.youming.youche.util.CommonUtils.divide(billServiceFee) );
        result.put("56kServiceFee", billServiceFee);
        result.put("ServiceCost", com.youming.youche.util.CommonUtils.divide(billServiceCost));
        result.put("billServiceFee", com.youming.youche.util.CommonUtils.divide(billServiceFee));
        result.put("billServiceCost", com.youming.youche.util.CommonUtils.divide(billServiceCost));
        result.put("lugeBillServiceFee", com.youming.youche.util.CommonUtils.divide(lugeBillServiceFee));
        result.put("lugeBillServiceCost", com.youming.youche.util.CommonUtils.divide(lugeBillServiceCost));
        result.put("billRate", billRate);
        return result;
    }

    @Override
    public long getPayoutIntfBySubFee(Long orderId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if(user == null || user.getTenantId() == null){
            throw new BusinessException("用户未登录！");
        }
        List<Long> subjectsIds = new ArrayList<Long>();
        subjectsIds.add(BILL_56K_RECEIVABLE_OVERDUE_SUB);
        subjectsIds.add(BILL_SERVICE_RECEIVABLE_OVERDUE_SUB);
        QueryWrapper<PayoutIntf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        queryWrapper.notIn("subjects_id",subjectsIds);
        queryWrapper.eq("is_automatic",AUTOMATIC1);
        queryWrapper.isNull("resp_code").ne("resp_code","5");
        queryWrapper.eq("txn_type","200");
        //查询转线下打款
        List<PayoutIntf> intfList = this.list(queryWrapper);
        long ordTotalFee = 0;
        if(intfList!=null && intfList.size() > 0 ){
            for(PayoutIntf intf : intfList){
                ordTotalFee  += intf.getTxnAmt();
            }
        }
        return ordTotalFee;
    }

    @Override
    public List<PayoutIntf> getPayoutIntf(List<Long> flowIds) {
        LambdaQueryWrapper<PayoutIntf> queryWrapper=Wrappers.lambdaQuery();
        queryWrapper.in(PayoutIntf::getId,flowIds)
                .eq(PayoutIntf::getTxnType,OrderAccountConst.TXN_TYPE.XX_TXN_TYPE)
                .isNull(PayoutIntf::getRespCode);
        List<PayoutIntf> list =baseMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public PayoutIntf getPayoutIntf(Long orderId) {
        LambdaQueryWrapper<PayoutIntf> queryWrapper=Wrappers.lambdaQuery();
        queryWrapper.eq(PayoutIntf::getOrderId,orderId)
                .eq(PayoutIntf::getTxnType,OrderAccountConst.TXN_TYPE.XX_TXN_TYPE)
                .eq(PayoutIntf::getSubjectsId, OIL_CARD_RELEASE_RECEIVABLE_IN)
                .isNull(PayoutIntf::getRespCode);
        PayoutIntf pay =baseMapper.selectOne(queryWrapper);
        return pay;
    }

    @Override
    public PinganBankInfoOutDto getPinganBankInfoOutRemoteCall(Long userId, String accountNo, Integer userType) {
        return this.getPinganBankInfoOut(userId, accountNo, userType);
    }

    @Override
    public PayoutIntf getPayOUtIntfByXid(long xid) {
        LambdaQueryWrapper<PayoutIntf> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PayoutIntf::getXid, xid);
        queryWrapper.ne(PayoutIntf::getRespCode, 5);
        List<PayoutIntf> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new PayoutIntf();
    }

    @Override
    public void doSavePayOutIntfHAToHAEn(PayoutIntf payoutIntf) {
        //校验业务数据有效性
        payOutIntfUtil.checkPayOutInfToEnData(payoutIntf);
        payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);
        if (StringUtils.isBlank(payoutIntf.getAccNo())) {
            PinganBankInfoOutDto pinganBankInfoOut = getPinganBankInfoOut(Long.parseLong(payoutIntf.getVehicleAffiliation()), "", payoutIntf.getPayUserType());
            payoutIntf.setPayAccNo(pinganBankInfoOut.getCorporatePinganAcctIdM());
            payoutIntf.setPayAccName(pinganBankInfoOut.getCorporateAcctName());
            payoutIntf.setPayBankAccNo(pinganBankInfoOut.getCorporateAcctNo());
            payoutIntf.setPayBankAccName(pinganBankInfoOut.getCorporateAcctName());

            payoutIntf.setAccNo(pinganBankInfoOut.getCorporateAcctNo());
            payoutIntf.setAccName(pinganBankInfoOut.getCorporateAcctName());
            payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getCorporateAcctNo());
            payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getCorporateAcctName());
            payoutIntf.setBankCode(pinganBankInfoOut.getCorporateBankCode());
        }
        this.save(payoutIntf);
        //添加busiCode生成规则：subjectsId+flowId(用于记录在打款失败后生成新的打款记录的时候，标记同一笔打款)
        if (null == payoutIntf.getSubjectsId()) {
            payoutIntf.setBusiCode("" + payoutIntf.getId());
        } else {
            payoutIntf.setBusiCode("" + payoutIntf.getSubjectsId() + payoutIntf.getId());
        }
        this.saveOrUpdate(payoutIntf);
    }

    @Override
    public void doSavePayOutIntfEnToEn(PayoutIntf payoutIntf) {
        //校验业务数据有效性
        payOutIntfUtil.checkPayOutInfToEnData(payoutIntf);
        payoutIntf.setTxnType(PLATFORM_TXN_TYPE);
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        if (null == userDataInfo) {
            throw new BusinessException("USER_ID[" + payoutIntf.getUserId() + "]用户不存在！");
        }
        if (CommonUtil.isNumber(userDataInfo.getMobilePhone())) {
            payoutIntf.setObjId(Long.parseLong(userDataInfo.getMobilePhone()));
        }
        String sysParameLuge = getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE + "").getCodeName();
        String sysPre = billPlatformService.getPrefixByUserId(Long.valueOf(payoutIntf.getVehicleAffiliation()));
        if (sysPre != null && sysPre.equals(sysParameLuge)) {
            Calendar nowTime = Calendar.getInstance();//得到当前时间
            nowTime.add(Calendar.MINUTE, 10);//增加10分钟
            payoutIntf.setCreateDate(LocalDateTime.now());
        }
        this.save(payoutIntf);

    }

    @Override
    public void doSavePayOutIntfVirToEn(PayoutIntf payoutIntf) {
        //校验业务数据有效性
        payOutIntfUtil.checkPayOutInfToEnData(payoutIntf);
        payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);
        //获取打款账号
        if (StringUtils.isBlank(payoutIntf.getPayAccNo())) {
            BankAccInfoDto bankAccInfo = getVirtualAccInfo(payoutIntf.getPayObjId(), payoutIntf.getVehicleAffiliation(), payoutIntf.getPayType(), 0, null, null, payoutIntf.getIsDriver(), payoutIntf.getUserType());
            payoutIntf.setPayAccNo(bankAccInfo.getAccNo());
            payoutIntf.setPayBankAccNo(bankAccInfo.getAccBankNo());
            payoutIntf.setPayBankAccName(bankAccInfo.getAccBankName());
        }
        if (StringUtils.isBlank(payoutIntf.getAccNo())) {
            BankAccInfoDto bankAccInfo = getVirtualAccInfo(payoutIntf.getUserId(), payoutIntf.getVehicleAffiliation(), payoutIntf.getPayType(), 1, null, null, payoutIntf.getIsDriver(), payoutIntf.getUserType());
            payoutIntf.setAccNo(bankAccInfo.getAccBankNo());
            payoutIntf.setAccName(bankAccInfo.getAccBankName());
            payoutIntf.setReceivablesBankAccNo(bankAccInfo.getAccBankNo());
            payoutIntf.setReceivablesBankAccName(bankAccInfo.getAccBankName());
            payoutIntf.setBankCode(bankAccInfo.getBankCode());
        }
        //查票据服务商的联系方式
        if (!payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION0) && !payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION1)) {
            BillPlatform billPlatform = billPlatformService.queryBillPlatformByUserId(Long.parseLong(payoutIntf.getVehicleAffiliation()));
            if (null != billPlatform && CommonUtil.isNumber(billPlatform.getLinkPhone())) {
                payoutIntf.setObjId(Long.parseLong(billPlatform.getLinkPhone()));
            }
        }
        this.save(payoutIntf);
        //添加busiCode生成规则：subjectsId+flowId(用于记录在打款失败后生成新的打款记录的时候，标记同一笔打款)
        if (null == payoutIntf.getSubjectsId()) {
            payoutIntf.setBusiCode("" + payoutIntf.getId());
        } else {
            if (WITHDRAWALS_OUT != payoutIntf.getSubjectsId()) {
                payoutIntf.setBusiCode("" + payoutIntf.getSubjectsId() + payoutIntf.getId());
            }

        }
        this.saveOrUpdate(payoutIntf);
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
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
    public boolean doPay200(PayoutIntf payoutIntf, List<AcBusiOrderLimitRel> busiOrderLimitRels, List<BusiSubjectsRel> busiSubjectsRels, boolean isOnline,LoginInfo loginInfo,String token) {
        //TODO 后期统一处理
        //boolean isLock = SysContexts.getLock(this.getClass().getName() + "payoutIntfFor200" + payoutIntf.getFlowId(), 3, 5);
        boolean isLock =true;
        if (!isLock) {
            throw new BusinessException("请求过于频繁，请稍后再试!");
        }
        if(PAY_FOR_OIL_CODE == payoutIntf.getBusiId().longValue()
                ||(PAY_FOR_REPAIR == payoutIntf.getBusiId().longValue()
                && EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 != payoutIntf.getSubjectsId().longValue()
                &&EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 != payoutIntf.getSubjectsId().longValue())
                ||SUBJECTIDS50070==payoutIntf.getSubjectsId()
                ||BALANCE_CONSUME_OIL_SUB==payoutIntf.getSubjectsId()
                ||BALANCE_PAY_REPAIR==payoutIntf.getSubjectsId()){//不需要操作业务
            return true;
        }
        if(BANK_PAYMENT_OUT== payoutIntf.getBusiId().longValue()){
            //SysStaticDataRedisUtils.
            String sysParame56K = sysStaticDataService.getSysStaticDatas(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K);
            String sysPre = billPlatformService.getPrefixByUserId(payoutIntf.getPayObjId());
            if(!(sysParame56K.equals(sysPre)&&EnumConsts.SubjectIds.SUBJECTIDS1900 == payoutIntf.getSubjectsId().longValue())){
                return true;
            }
        }

        if(null == payoutIntf.getTxnAmt()){
            throw new BusinessException("交易金额不能为空");
        }
        if(null == busiOrderLimitRels){
            busiOrderLimitRels = acBusiOrderLimitRelService.getBaseMapper().selectList(null);

        }
        if(null == busiSubjectsRels){
            busiSubjectsRels = busiSubjectsRelService.getBaseMapper().selectList(null);
        }
        List<Map> mapList = PayOutIntfUtil.dealBusiOrderLimitRel(busiOrderLimitRels,null);
        Map rtnMap = PayOutIntfUtil.dealBusiSubjectsRel(busiSubjectsRels);
        mapList.add(rtnMap);
//        BaseBusiToOrder baseBusiToOrder = new BaseBusiToOrder();
//        BaseBusiToOrder baseBusiToOrderCopy = baseBusiToOrder;

        baseBusiToOrder.setBaseBusiToOrder(mapList);
        //判断是否需要做业务
       /* boolean isDoBusi = baseBusiToOrder.isDoBusi(payoutIntf.getSubjectsId());
        if(!isDoBusi){
            return true;
        }*/
        //获取付款用户信息
        UserDataInfo payUserDataInfo = userDataInfoService.getUserDataInfo(payoutIntf.getPayObjId());
        if(null == payUserDataInfo){
            throw new BusinessException("付款用户信息不存在,FLOW_ID : " + payoutIntf.getId());
        }
        //获取收款用户信息
        UserDataInfo receivablesUserDataInfo = userDataInfoService.getUserDataInfo(payoutIntf.getUserId());
        if(null == receivablesUserDataInfo){
            throw new BusinessException("收款用户信息不存在,FLOW_ID : " + payoutIntf.getId());
        }
        UserDataInfo[] userDataInfoArr = new UserDataInfo[2];
        userDataInfoArr[0] = payUserDataInfo;
        userDataInfoArr[1] = receivablesUserDataInfo;
        //1.支付预付款
        if(BEFORE_PAY_CODE == payoutIntf.getBusiId().longValue() ){
            //20190727 会员体系改造，自有车开票，车队账户也记应收应付
//            if(BEFORE_HA_TOTAL_FEE == payoutIntf.getSubjectsId().longValue()){
//                doSavePayoutIntf(payoutIntf,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
//            }else{
//                dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr);
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
            if(payoutIntf.getSubjectsId().longValue() == BEFORE_RECEIVABLE_OVERDUE_SUB){
                orderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(),payoutIntf.getTxnAmt());
                //20191015 56K附加运费
                if (payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0) {
                    dealAdditionalFee(payoutIntf);
                }

            }
//            }
        }
        //2.尾款(现金，油，维修保养)到期
        else if(PAYFOR_CODE == payoutIntf.getBusiId().longValue()){
//            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr);
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
            if(payoutIntf.getSubjectsId().longValue() == FINAL_PAYFOR_RECEIVABLE_IN){
                orderFeeStatementService.checkOrderAmountByProcess(payoutIntf.getOrderId(),payoutIntf.getTxnAmt());
            }
        }
        //2.预支尾款
        else if(ADVANCE_PAY_MARGIN_CODE == payoutIntf.getBusiId().longValue()){
            //dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,EnumConsts.PAY_OUT_OPER.ORDER,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline);
            //20190815修改 预支尾款操作限定表
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
            if (null == payoutIntf.getOrderId() || payoutIntf.getOrderId().longValue() <=0) {
                orderLimitService.doOrderLimtByFlowId(payoutIntf);
            }
        }
        //3.油转现金  4.ETC转现金
        else if(OIL_AND_ETC_TURN_CASH == payoutIntf.getBusiId().longValue()){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
        }
        //5.司机借支
        else if(EnumConsts.PayInter.OA_LOAN_AVAILABLE == payoutIntf.getBusiId().longValue()){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
            baseBusiToOrder.dealBusiOaLoanAvailable(payoutIntf,userDataInfoArr,loginInfo,token);
            //this.doSavePayoutInfoExpansion(payoutIntf);
        }
        //6.司机报销
        else if(DRIVER_EXPENSE_ABLE == payoutIntf.getBusiId().longValue() ){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
        }
        //7.车管借支
        else if(OA_LOAN_AVAILABLE_TUBE == payoutIntf.getBusiId().longValue()){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
            oaLoanService.setPayFlowIdAfterPay(payoutIntf.getId());
        }
        //8.车管报销
        else if(TUBE_EXPENSE_ABLE == payoutIntf.getBusiId().longValue() ){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
        }
        //9.工资发放
        else if(CAR_DRIVER_SALARY == payoutIntf.getBusiId().longValue() ){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
        }
        //10.异常补偿
        else if(EnumConsts.PayInter.EXCEPTION_FEE == payoutIntf.getBusiId().longValue() ){
//            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr);
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
        }
        //11.异常扣减
        else if(EnumConsts.PayInter.EXCEPTION_FEE_OUT == payoutIntf.getBusiId().longValue()){
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,loginInfo);
        }
        //12.撤单
        else if(CANCEL_THE_ORDER == payoutIntf.getBusiId().longValue()){
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,loginInfo);
        }

        //13.ETC消费欠款
        else if(CONSUME_ETC_CODE == payoutIntf.getBusiId().longValue()){
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,loginInfo);
        }

        //14.油卡抵押(释放)
        else if(PLEDGE_RELEASE_OILCARD == payoutIntf.getBusiId().longValue()){
            if(OIL_CARD_RELEASE_RECEIVABLE_IN == payoutIntf.getSubjectsId().longValue()){
//                dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr);
                dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
            }else{
//                //调用订单中心的油卡释放接口
                oilCardManagementService.releaseOilCardByOrderId(payoutIntf.getOrderId(),payoutIntf.getTenantId(),loginInfo);
//                //调用资金模块的油卡释放接口
                oilCardManagementService.pledgeOrReleaseOilCardAmount(payoutIntf.getPayObjId(),payoutIntf.getVehicleAffiliation(),payoutIntf.getTxnAmt(),payoutIntf.getOrderId(),payoutIntf.getTenantId(),1,loginInfo,token);
            }
        }else if(UPDATE_THE_ORDER == payoutIntf.getBusiId().longValue()){
            if(payoutIntf.getIsDriver() == HAVIR){
                dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
            }else{
                dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,loginInfo);
            }

        }
        //票據服务费
        else if(BILL_56K_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId().longValue()||BILL_SERVICE_RECEIVABLE_OVERDUE_SUB== payoutIntf.getSubjectsId().longValue()){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
        }
        //油品公司油卡充值
        else if(RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB == payoutIntf.getSubjectsId().longValue()){
//            IVoucherInfoTF voucherInfoTF = (IVoucherInfoTF ) SysContexts.getBean("voucherInfoTF");
//        	/*if(voucherInfoTF.judgeRechargeIsNeedWithdrawal(payoutIntf.getBusiCode())){
//        		dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline);
//        	}*/
            //isOnline = voucherInfoTF.judgeRechargeIsNeedWithdrawal(payoutIntf.getBusiCode());
            isOnline = voucherInfoService.judgeRechargeIsNeedWithdrawal(payoutIntf.getBusiCode(),loginInfo);
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
        }
        //支付到付款
        else if(EnumConsts.PayInter.PAY_ARRIVE_CHARGE == payoutIntf.getBusiId().longValue() ){
//            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr);
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
        }
        //支付ETC账单
        else if(SUBJECTIDS2302==payoutIntf.getSubjectsId().longValue()){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
        }
        //招商车挂靠车对账单
        else if(EnumConsts.PayInter.ACCOUNT_STATEMENT == payoutIntf.getBusiId().longValue()){
            //未到期结算金额
            if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE == payoutIntf.getSubjectsId().longValue()) {
                dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10,isOnline,loginInfo);
            } else if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == payoutIntf.getSubjectsId().longValue()) {//车辆费用
                dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,false,loginInfo);
            }
        }
        //北斗缴费
        else if(EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == payoutIntf.getSubjectsId().longValue()){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,false,loginInfo);
        }
        //车队充值油费（非运输专票）时，资金从车队平安账户付款油品公司的平安虚拟账户，系统自动将资金从油品公司的平安账户的提现到实体银行卡账户
        else if(EnumConsts.SubjectIds.SUBJECTIDS1131 == payoutIntf.getSubjectsId().longValue()){//非运输专票
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
        }
        //代收服务商维修保养 20190820
        else if(EnumConsts.SubjectIds.RECEIVABLE_IN_REPAIR_FEE_4013 == payoutIntf.getSubjectsId().longValue()){
            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,true,loginInfo);
        }
        //代收服务商轮胎租赁 20191125
        else if(EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020 == payoutIntf.getSubjectsId().longValue()){
//            TyreSettlementBillSV tyreSettlementBillSV = (TyreSettlementBillSV)SysContexts.getBean("tyreSettlementBillSV");
//            ITyreSettlementBillTF tyreSettlementBillTF = (ITyreSettlementBillTF)SysContexts.getBean("tyreSettlementBillTF");
//            TyreSettlementBillVO tyreSettlementBillVO = tyreSettlementBillSV.getTyreSettlementBillVOByBusiCode(payoutIntf.getBusiCode());
//            if(isOnline) {
//                tyreSettlementBillTF.updTyreSettlementBillState(tyreSettlementBillVO.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "支付完成");
//            }else {
//                tyreSettlementBillTF.updTyreSettlementBillState(tyreSettlementBillVO.getId(), ServiceConsts.TYRE_PAY_STATE.TYRE_PAY_STATE4, "确认收款");
//            }
//            tyreSettlementBillVO.setPayClass(isOnline?IS_TURN_AUTOMATIC_1:IS_TURN_AUTOMATIC_0);
//            tyreSettlementBillSV.saveOrUpdate(tyreSettlementBillVO);
//            dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,isOnline,loginInfo);
        }
        else if(EnumConsts.SubjectIds.SUBJECTIDS1900 == payoutIntf.getSubjectsId().longValue()){//如果是56K打款的，自动帮收款方提现
            String sysParame56K = sysStaticDataService.getSysStaticDatas(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K);
            String sysPre = billPlatformService.getPrefixByUserId(payoutIntf.getPayObjId());
            if(sysParame56K.equals(sysPre)){
//    			dealOABusi(baseBusiToOrder,payoutIntf,userDataInfoArr,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5,true);
                doSavePayoutIntf(payoutIntf,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);
            }
        }
        //与订单无关的业务操作，只操作账户表和账户明细表F
        else{
//            throw new BusinessException("不支持["+payoutIntf.getBusiId()+"]业务！");
            dealBusi(baseBusiToOrder,payoutIntf,userDataInfoArr,EnumConsts.PAY_OUT_OPER.ORDER,loginInfo);
            if(SUBJECTIDS1816 == payoutIntf.getSubjectsId().longValue()&&isOnline){
                doSavePayoutIntf(payoutIntf,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
            }
        }

        if(payoutIntf.getIsTurnAutomatic()!=null&&payoutIntf.getIsTurnAutomatic()== OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_3){//自动发起提现
            //判断这条流水是否已经生成了300的提现记录，如果没有，发起提现
            PayoutIntf payoutIntf300 = getPayOutIntfByXid(payoutIntf.getId());
            if(payoutIntf300==null){
                doSavePayoutIntf(payoutIntf,OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);
            }
        }
        return true;
    }

    public void dealBusi(BaseBusiToOrder baseBusiToOrder,PayoutIntf payoutIntf,UserDataInfo[] userDataInfoArr,int flg,LoginInfo loginInfo){
        //如果付款给自己，则不操作账户   20190727 会员体系改造，所有付款给自己的账户也记应收应付，固然也要操作账户
        /*if(null != payoutIntf.getPayObjId() && null != payoutIntf.getUserId() && payoutIntf.getPayObjId().longValue() == payoutIntf.getUserId().longValue()){
            return;
        }*/
        if(flg == EnumConsts.PAY_OUT_OPER.ORDER){
            OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
            baseBusiToOrder.doSaveAccountDetail(payoutIntf,orderAccounts,userDataInfoArr,loginInfo);
        }
        else if(flg == EnumConsts.PAY_OUT_OPER.ACCOUNT){
            baseBusiToOrder.doSaveOrderFundflow(payoutIntf,userDataInfoArr,loginInfo);
            baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        }
    }

    public void dealBusi(BaseBusiToOrder baseBusiToOrder,PayoutIntf payoutIntf,UserDataInfo[] userDataInfoArr,LoginInfo loginInfo){
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf,orderAccounts,userDataInfoArr,loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf,userDataInfoArr,loginInfo);
    }

    public void dealBusi(BaseBusiToOrder baseBusiToOrder,PayoutIntf payoutIntf,UserDataInfo[] userDataInfoArr,int level,boolean isOnline,LoginInfo loginInfo){
        //如果付款给自己，则不操作账户     20190727 会员体系改造，所有付款给自己的账户也记应收应付，固然也要操作账户
       /* if(null != payoutIntf.getPayObjId() && null != payoutIntf.getUserId() && payoutIntf.getPayObjId().longValue() == payoutIntf.getUserId().longValue()){
            return;
        }*/
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf,orderAccounts,userDataInfoArr,loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf,userDataInfoArr,loginInfo);
        UserDataInfo receivablesUserDataInfo  = userDataInfoArr[1];
        if(isOnline && (payoutIntf.getIsDriver() == HAVIR
                ||receivablesUserDataInfo.getQuickFlag().equals(SysStaticDataEnum.QUICK_FLAG.IS_QUICK))){//如果是开票或者收款人有快速通道新增的标识，则自动提现到对应的银行卡账户。
            //业务处理完成之后，马上发起提现
            doSavePayoutIntf(payoutIntf,level);
        }
    }

    public void dealOABusi(BaseBusiToOrder baseBusiToOrder,PayoutIntf payoutIntf,UserDataInfo[] userDataInfoArr,int level,boolean isOnline,LoginInfo loginInfo){
        //借支成功之后业务处理
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf,orderAccounts,userDataInfoArr,loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf,userDataInfoArr,loginInfo);
        if(isOnline){
            //业务处理完成之后，马上发起提现
            doSavePayoutIntf(payoutIntf,level);
        }
    }

    public PayoutIntf getPayOutIntfByXid(long xid){
        LambdaQueryWrapper<PayoutIntf> queryWrapper=Wrappers.lambdaQuery();
        queryWrapper.eq(PayoutIntf::getXid,xid);
        List<PayoutIntf> list = baseMapper.selectList(queryWrapper);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;

    }

    public void dealAdditionalFee(PayoutIntf pay){
        if (pay == null) {
            throw new BusinessException("入参有误");
        }
        if (pay.getSubjectsId().longValue() == EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB) {
            if (pay.getAppendFreight() != null && pay.getAppendFreight() > 0) {
                AdditionalFee af = additionalFeeService.getAdditionalFeeByOrderId(pay.getOrderId());
                if (af == null) {
                    throw new BusinessException("根据订单号：" + pay.getOrderId() + " 未找到该打款记录【" + pay.getId() + "】的附加运费记录");
                }
                if(pay.getIsAutomatic()==OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) {
                    af.setState(OrderAccountConst.ADDITIONAL_FEE.STATE2);
                }else {
                    af.setState(OrderAccountConst.ADDITIONAL_FEE.STATE4);
                    af.setDealState(OrderAccountConst.ADDITIONAL_FEE_DEAL_STATE.STATE3);
                    af.setDealRemark("线下打款_撤回");
                }

                af.setPayTime(pay.getPayTime());
                af.setUpdateTime(LocalDateTime.now());
                additionalFeeService.saveOrUpdate(af);
            }
        }
    }

    /***
     * 自动发起提现
     * @param payoutIntf
     * @throws Exception
     */
    public void doSavePayoutIntf(PayoutIntf payoutIntf,int level){
        //20191023 只有56k附加运费，没有业务金额，300打款成功后不生成100打款记录
        if (OrderAccountConst.TXN_TYPE.XS_TXN_TYPE.equals(payoutIntf.getTxnType()) && (payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && (payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
            return;
        }
//        PayoutIntf payoutIntf1 = payOutIntfUtil.createPayoutIntfForWithdraw(payoutIntf,level,accountBankRelSV);
//        if(payoutIntf1.getPayType() == HAVIR){
//            /**
//             * 如果已经存在有效的提现记录，不发起提现
//             */
//            IPayoutIntfSV payoutIntfSV = (IPayoutIntfSV)SysContexts.getBean("payoutIntfSV");
//            PayoutIntf dealWithdrawFlow = payoutIntfSV.getPayOUtIntfByXid(payoutIntf.getFlowId());
//            if(dealWithdrawFlow!=null){
//                return;
//            }
//            doSavePayOutIntfHAToHAEn(payoutIntf1);
//        }else if(payoutIntf1.getPayType() == HAEN){
//            doSavePayOutIntfEnToEn(payoutIntf1);
//            //记录账户流水，不操作账户
//            IBusiSubjectsRelTF busiSubjectsRelTF = (BusiSubjectsRelTF) SysContexts.getBean("busiSubjectsRelTF");
//            IAccountDetailsTF accountDetailsTF = (AccountDetailsTF) SysContexts.getBean("accountDetailsTF");
//            List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
//            BusiSubjectsRel balanceSub = busiSubjectsRelTF.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_AUTO, payoutIntf1.getTxnAmt());
//            balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
//            busiBalanceList.add(balanceSub);
//            List<BusiSubjectsRel> balanceList = busiSubjectsRelTF.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
//            long soNbr = com.business.utils.CommonUtil.createSoNbr();
//            accountDetailsTF.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE,EnumConsts.PayInter.WITHDRAWALS_CODE,payoutIntf1.getUserId(), payoutIntf1.getUserId(), payoutIntf1.getAccName(), balanceList, soNbr, 0L, payoutIntf1.getTenantId(),payoutIntf1.getUserType());
//        }
//        //未绑定实体银行卡不能生成自动提现记录
//        else if(payoutIntf1.getPayType() == -1){
//            return;
//        }
//        else{
//            doSavePayOutIntfVirToEn(payoutIntf1);
//            //记录账户流水，不操作账户
//            IBusiSubjectsRelTF busiSubjectsRelTF = (BusiSubjectsRelTF) SysContexts.getBean("busiSubjectsRelTF");
//            IAccountDetailsTF accountDetailsTF = (AccountDetailsTF) SysContexts.getBean("accountDetailsTF");
//            List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
//            BusiSubjectsRel balanceSub = busiSubjectsRelTF.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_AUTO, payoutIntf1.getTxnAmt());
//            balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
//            busiBalanceList.add(balanceSub);
//            List<BusiSubjectsRel> balanceList = busiSubjectsRelTF.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
//            long soNbr = com.business.utils.CommonUtil.createSoNbr();
//            accountDetailsTF.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE,EnumConsts.PayInter.WITHDRAWALS_CODE,payoutIntf1.getUserId(), payoutIntf1.getUserId(), payoutIntf1.getAccName(), balanceList, soNbr, 0L, payoutIntf1.getTenantId(),payoutIntf1.getUserType());
//
//        }
//       /* if(OIL_AND_ETC_TURN_CASH == payoutIntf.getBusiId().longValue() || BEFORE_HA_TOTAL_FEE ==  payoutIntf.getSubjectsId().longValue()){
//            log.info("修改payout_order记录：原id["+payoutIntf.getFlowId()+"],修改成["+payoutIntf1.getFlowId()+"]");
//            payOutIntfSV.updPayoutOrder(payoutIntf1.getFlowId(),payoutIntf.getFlowId());
//        }*/
//        if(payoutIntf1.getPayType() == HAVIR || payoutIntf1.getPayType() == HAEN || OIL_AND_ETC_TURN_CASH == payoutIntf.getBusiId().longValue()){
//            log.info("修改payout_order记录：原id["+payoutIntf.getFlowId()+"],修改成["+payoutIntf1.getFlowId()+"]");
//            payOutIntfSV.updPayoutOrder(payoutIntf1.getFlowId(),payoutIntf.getFlowId());
//        }
    }

    @Override
    public long getPayoutIntfBySubFee(Long orderId) {
        List<Long> subjectsIds = new ArrayList<Long>();
        subjectsIds.add(BILL_56K_RECEIVABLE_OVERDUE_SUB);
        subjectsIds.add(BILL_SERVICE_RECEIVABLE_OVERDUE_SUB);
        QueryWrapper<PayoutIntf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        queryWrapper.notIn("subjects_id",subjectsIds);
        queryWrapper.eq("is_automatic",AUTOMATIC1);
        queryWrapper.isNull("resp_code").ne("resp_code","5");
        queryWrapper.eq("txn_type","200");
        //查询转线下打款
        List<PayoutIntf> intfList = this.list(queryWrapper);
        long ordTotalFee = 0;
        if(intfList!=null && intfList.size() > 0 ){
            for(PayoutIntf intf : intfList){
                ordTotalFee  += intf.getTxnAmt();
            }
        }
        return ordTotalFee;
    }

    @Override
    public Map doQueryServiceFee(Long cash, Long flowId,LoginInfo user)   {
        if (user == null || user.getTenantId() == null) {
            throw new BusinessException("用户未登录！");
        }
        Long tenantId = user.getTenantId();
        if(flowId == null || flowId <= 0){
            throw new BusinessException("请传入业务单号");
        }
        PayoutIntf payout = baseMapper.selectById(flowId);
        Long openUserId = Long.valueOf(payout.getVehicleAffiliation());
        //如果费用科目是服务费，则不计算服务费
        if(payout.getSubjectsId() == BILL_SERVICE_RECEIVABLE_OVERDUE_SUB || payout.getSubjectsId() == BILL_56K_RECEIVABLE_OVERDUE_SUB ){
            return new HashMap();
        }
        if(cash <= 0 ){
            cash = 	payout.getTxnAmt();
        }
        if (openUserId == null || openUserId <= 10) {
            return new HashMap();
        }
        if (cash == null || cash < 0) {
            throw new BusinessException("现金金额不合法");
        }

        BillSetting billSetting = iBillSettingService.getBillSetting(tenantId);
        if (billSetting == null) {
            throw new BusinessException("根据租户ID: " + tenantId + " 未找到车队开票信息");
        }
        if (billSetting.getRateId() == null || billSetting.getRateId() <= 0) {
            throw new BusinessException("根据租户ID: " + tenantId + " 未找到车队开票费率设置");
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        BillPlatform bpf = billPlatformService.queryAllBillPlatformByUserId(openUserId);
        if (bpf == null) {
            throw new BusinessException("根据开票平台用户id：" + openUserId + " 未找到开票平台信息");
        }
        if (bpf.getServiceFeeFormula() == null) {
            throw new BusinessException("根据开票平台用户id： " + openUserId + " 未找到开票服务计算公式");
        }
        String tempFormula = bpf.getServiceFeeFormula();
        String formula = tempFormula.replace("（", "(").replace("）", ")").replace("%", "/100.0");
        String tempHaCostFormula = bpf.getHaCost();
        String haCostFormula = tempHaCostFormula.replace("（", "(").replace("）", ")").replace("%", "/100.0");
        //开票率
        Double billRate = iRateService.getRateValue(billSetting.getRateId(), cash);
        if (billRate == null) {
            throw new BusinessException("根据车队费率id： " + billSetting.getRateId() + " 开票金额" + (double)cash/100 + " 未找到开票费率");
        }
        long oil = 0;
        long etc = 0;
        engine.put("d", cash);
        engine.put("o", oil);
        engine.put("e", etc);
        engine.put("r", billRate / 100);
        Double serviceFee = 0.0;
        try {
            serviceFee = (Double) engine.eval(formula);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        Double serviceCost = 0.0;
        try {
            serviceCost = (Double) engine.eval(haCostFormula);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        long billServiceFee = Math.round(serviceFee);//serviceFee.longValue();//截取
        long billServiceCost = serviceCost.longValue();//截取
        long lugeBillServiceFee = Math.round(serviceFee);//四舍五入
        long lugeBillServiceCost = Math.round(serviceCost);//四舍五入
        //56k服务费算法直接截取，路歌服务费四舍五入，比如123.678分，56K: 123分，路歌：124分
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ServiceFee", com.youming.youche.util.CommonUtils.divide(billServiceFee) );
        result.put("56kServiceFee", billServiceFee);
        result.put("ServiceCost", com.youming.youche.util.CommonUtils.divide(billServiceCost));
        result.put("billServiceFee", com.youming.youche.util.CommonUtils.divide(billServiceFee));
        result.put("billServiceCost", com.youming.youche.util.CommonUtils.divide(billServiceCost));
        result.put("lugeBillServiceFee", com.youming.youche.util.CommonUtils.divide(lugeBillServiceFee));
        result.put("lugeBillServiceCost", com.youming.youche.util.CommonUtils.divide(lugeBillServiceCost));
        result.put("billRate", billRate);
        return result;
    }

    /**
     * 已支付服务费
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public long getPayoutIntfServiceFee(Long orderId) {
        List<Long> subIds = new ArrayList<>();
        subIds.add(EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB);
        subIds.add(EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB);
        QueryWrapper<PayoutIntf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        queryWrapper.notIn("subjects_id",subIds);
        queryWrapper.isNull("resp_code").ne("resp_code","5");
        queryWrapper.eq("txn_type","200");
        //查询转线下打款
        List<PayoutIntf> intfList = this.list(queryWrapper);
        long ordTotalFee = 0;
        if(intfList!=null && intfList.size() > 0 ){
            for(PayoutIntf intf : intfList){
                ordTotalFee  += intf.getTxnAmt();
            }
        }
        return ordTotalFee;
    }

    @Override
    public void doSavePayOutIntfVirToVirNew (PayoutIntf payoutIntf,PayoutIntf parentIntf,String token) {
        //校验业务数据有效性
        payOutIntfUtil.checkPayOutInfToEnData(payoutIntf);
        payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);
        hasDefaultAcc(Long.parseLong(payoutIntf.getVehicleAffiliation()), SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        //获取打款账号
        BankAccInfoDto bankAccInfo = null;
        String accountNo = parentIntf.getPayAccNo();
        if((VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation()) || VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation()))
                && (payoutIntf.getSubjectsId().longValue() == Oil_PAYFOR_RECEIVABLE_IN
                || OIL_TURN_CASH_RECEIVABLE_IN == payoutIntf.getSubjectsId().longValue())){
            bankAccInfo = getVirtualAccInfoNew(payoutIntf.getPayObjId(),payoutIntf.getOilAffiliation(),0,payoutIntf.getPayType(),payoutIntf.getPayUserType(),accountNo);
        }else{
            bankAccInfo = getVirtualAccInfoNew(payoutIntf.getPayObjId(),payoutIntf.getVehicleAffiliation(),payoutIntf.getPayType(),0,payoutIntf.getPayTenantId(),payoutIntf.getOrderId(),payoutIntf.getIsDriver(),payoutIntf.getPayUserType(),accountNo);
        }
        if(SUBJECTIDS1131==payoutIntf.getSubjectsId().longValue()||SUBJECTIDS1133==payoutIntf.getSubjectsId().longValue()){
            AccountBankRel accountBankRel = accountBankRelService.getAcctNo(payoutIntf.getPayAccNo());
            if(accountBankRel!=null){
                payoutIntf.setPayAccNo(accountBankRel.getPinganPayAcctId());
                payoutIntf.setPayAccName(accountBankRel.getAcctName());
                payoutIntf.setPayBankAccName(accountBankRel.getAcctName());
                payoutIntf.setPayBankAccNo(accountBankRel.getAcctNo());
                payoutIntf.setAccountType(accountBankRel.getBankType()==BANK_TYPE_1?BUSINESS_PAYABLE_ACCOUNT:PRIVATE_PAYABLE_ACCOUNT);
            }
        }else{
            payoutIntf.setPayAccNo(bankAccInfo.getAccNo());
            payoutIntf.setPayAccName(bankAccInfo.getAccBankName());
            payoutIntf.setPayBankAccName(bankAccInfo.getAccBankName());
            payoutIntf.setPayBankAccNo(bankAccInfo.getAccBankNo());
            payoutIntf.setAccountType(bankAccInfo.getAccountType());
        }
        if(null != bankAccInfo.getIsAutomatic()){
            payoutIntf.setIsAutomatic(bankAccInfo.getIsAutomatic());
        }
        //获取收款账号
        String sysParameLuge = sysStaticDataService.getSysStaticDatas(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE);
        String sysParame56K = sysStaticDataService.getSysStaticDatas(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K);
        String sysPre = billPlatformService.getPrefixByUserId(Long.valueOf(payoutIntf.getVehicleAffiliation()));
        if(sysPre!=null&&(sysParameLuge.equals(sysPre)||sysParame56K.equals(sysPre)) && HAVIR == payoutIntf.getIsDriver()){//路歌的资金渠道,且资金走路歌

            if(SUBJECTIDS1131==payoutIntf.getSubjectsId().longValue()){
                if(sysParameLuge.equals(sysPre)){
                    List<AccountBankRel> bankList = accountBankRelService.getAccountBankList(payoutIntf.getPayTenantId(), payoutIntf.getPayAccName());
                    if(bankList==null||bankList.size()==0){
                        throw new BusinessException("获取不到路歌子账户信息，请检查");
                    }
                    payoutIntf.setAccNo(bankList.get(0).getPinganCollectAcctId());
                    payoutIntf.setAccName(bankList.get(0).getAcctName());
                    payoutIntf.setBankType(bankList.get(0).getBankType());
                    payoutIntf.setBankCode(bankList.get(0).getBankName());
                }else{
                    AccountBankRel accountBankRel = accountBankRelService.getDefaultAccountBankRel(Long.valueOf(payoutIntf.getVehicleAffiliation()), BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                    if(accountBankRel!=null){
                        payoutIntf.setAccNo(accountBankRel.getPinganCollectAcctId());
                        payoutIntf.setAccName(accountBankRel.getAcctName());
                        payoutIntf.setBankType(accountBankRel.getBankType());
                        payoutIntf.setBankCode(accountBankRel.getBankName());
                    }
                }
            }else{
                if(sysParameLuge.equals(sysPre)){
                    OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(payoutIntf.getOrderId());
                    OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(payoutIntf.getOrderId());
                    String billLookUp = "";//开票主体
                    if(orderFeeExt!=null){
                        billLookUp = orderFeeExt.getBillLookUp();
                    }else{
                        billLookUp=orderFeeExtH.getBillLookUp();
                    }
                    List<AccountBankRel> bankList = accountBankRelService.getAccountBankList(payoutIntf.getPayTenantId(), billLookUp);
                    if(bankList==null||bankList.size()==0){
                        throw new BusinessException("获取不到路歌子账户信息，请检查");
                    }
                    payoutIntf.setAccNo(bankList.get(0).getPinganCollectAcctId());
                    payoutIntf.setAccName(bankList.get(0).getAcctName());
                    payoutIntf.setBankType(bankList.get(0).getBankType());
                    payoutIntf.setBankCode(bankList.get(0).getBankName());
                }else{
                    AccountBankRel accountBankRel = accountBankRelService.getDefaultAccountBankRel(Long.valueOf(payoutIntf.getVehicleAffiliation()), BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                    if(accountBankRel!=null){
                        payoutIntf.setAccNo(accountBankRel.getPinganCollectAcctId());
                        payoutIntf.setAccName(accountBankRel.getAcctName());
                        payoutIntf.setBankType(accountBankRel.getBankType());
                        payoutIntf.setBankCode(accountBankRel.getBankName());
                    }
                }
            }
        }else{
            if(SUBJECTIDS1131==payoutIntf.getSubjectsId().longValue()||SUBJECTIDS1133==payoutIntf.getSubjectsId().longValue()){
                if(VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation()) || VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation())){
                    AccountBankRel accountBankRel = accountBankRelService.getAcctNo(payoutIntf.getAccNo());
                    if(accountBankRel!=null){
                        payoutIntf.setAccNo(accountBankRel.getPinganCollectAcctId());
                        payoutIntf.setAccName(accountBankRel.getAcctName());
                        payoutIntf.setBankType(accountBankRel.getBankType());
                        payoutIntf.setBankCode(accountBankRel.getBankName());
                    }
                }
            }else{
                BankAccInfoDto bankAccInfo1 = null;
                if((VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation()) || VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation()))
                        &&  (payoutIntf.getSubjectsId().longValue() == Oil_PAYFOR_RECEIVABLE_IN
                        || OIL_TURN_CASH_RECEIVABLE_IN == payoutIntf.getSubjectsId().longValue())){
                    bankAccInfo1 = getVirtualAccInfo(payoutIntf.getUserId(),payoutIntf.getOilAffiliation(),1,payoutIntf.getIsDriver(),payoutIntf.getUserType());
                }else{
                    bankAccInfo1 = getVirtualAccInfo(payoutIntf.getUserId(),payoutIntf.getVehicleAffiliation(),payoutIntf.getIsDriver(),1,null,null,payoutIntf.getIsDriver(),payoutIntf.getUserType());
                }
                //        BankAccInfo bankAccInfo1 = getVirtualAccInfo(payoutIntf.getUserId(),payoutIntf.getVehicleAffiliation(),payoutIntf.getIsDriver(),1,null,null,payoutIntf.getIsDriver());
                payoutIntf.setAccNo(bankAccInfo1.getAccNo());
                payoutIntf.setAccName(bankAccInfo1.getAccBankName());
                payoutIntf.setBankType(bankAccInfo1.getBankType());
                payoutIntf.setBankCode(bankAccInfo1.getBankCode());
                if(null != bankAccInfo1.getIsAutomatic()){
                    payoutIntf.setIsAutomatic(bankAccInfo1.getIsAutomatic());
                }
            }
        }

        //获取收款方真实银行卡信息
        PinganBankInfoOutDto pinganBankInfoOut =  null;
        if(!VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation()) && !VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation())){
            pinganBankInfoOut = getPinganBankInfoOut(payoutIntf.getUserId(),"",payoutIntf.getUserType());
            //自有车开单要票打回车队随机获取车队绑定的对私收款账户 20190412加
            if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE) {
                pinganBankInfoOut = payOutIntfUtil.getRandomPinganBankInfoOut(payoutIntf.getUserId(),payoutIntf.getPayTenantId());
                if (pinganBankInfoOut == null) {
                    throw new BusinessException("收款方USER_ID["+payoutIntf.getUserId()+"]未绑定对私账户");
                }
            }else if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS1131
                    ||payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS1133) {
                pinganBankInfoOut = getPinganBankInfoOut(Long.valueOf(payoutIntf.getVehicleAffiliation()),payoutIntf.getAccNo(),payoutIntf.getUserType());
            }
        }else{
            pinganBankInfoOut = getPinganBankInfoOut(payoutIntf.getUserId(),"", SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        }
        if (null != pinganBankInfoOut) {
            String affiliation = payoutIntf.getVehicleAffiliation();//资金渠道
            if(payoutIntf.getUserType()== SysStaticDataEnum.USER_TYPE.SERVICE_USER){//如果收款是油站服务商，需要判断油资金渠道
                ServiceInfoBasisVo serviceInfo = serviceInfoService.getServiceInfo(payoutIntf.getUserId());
                if(serviceInfo!=null&&serviceInfo.getServiceType()== SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL){
                    affiliation=payoutIntf.getOilAffiliation();
                }
            }
            if(VEHICLE_AFFILIATION1.equals(affiliation)){
                if(payoutIntf.getUserType() == DRIVER_USER ){
                    payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getPrivateAcctName());
                    payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getPrivateAcctNo());
                    payoutIntf.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                }else{
                    if(SUBJECTIDS1131==payoutIntf.getSubjectsId().longValue()||SUBJECTIDS1133==payoutIntf.getSubjectsId().longValue()){
                        AccountBankRel accountBankRel = accountBankRelService.getAcctNo(payoutIntf.getAccNo());
                        if(accountBankRel!=null){
                            payoutIntf.setReceivablesBankAccName(accountBankRel.getAcctNo());
                            payoutIntf.setReceivablesBankAccNo(accountBankRel.getAcctName());
                            payoutIntf.setBankCode(accountBankRel.getBankName());
                        }
                    }else{
                        payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getCorporateAcctName());
                        payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getCorporateAcctNo());
                        payoutIntf.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                    }
                }
            }else if(VEHICLE_AFFILIATION0.equals(affiliation)){
                payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getPrivateAcctName());
                payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getPrivateAcctNo());
                payoutIntf.setBankCode(pinganBankInfoOut.getPrivateBankCode());
            }else{
                if(payoutIntf.getUserType() == SERVICE_USER || payoutIntf.getUserType() == SERVER_CHILD_USER || payoutIntf.getUserType() == BILL_SERVER_USER){
                    payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getCorporateAcctName());
                    payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getCorporateAcctNo());
                    payoutIntf.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                    //20190412增加
                    payoutIntf.setPinganCollectAcctId(pinganBankInfoOut.getCorporatePinganAcctIdM());
                }else{
                    payoutIntf.setReceivablesBankAccName(pinganBankInfoOut.getPrivateAcctName());
                    payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut.getPrivateAcctNo());
                    payoutIntf.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                    //20190412增加
                    payoutIntf.setPinganCollectAcctId(pinganBankInfoOut.getPrivatePinganAcctIdM());
                }
//                if(StringUtils.isBlank(payoutIntf.getReceivablesBankAccNo())){
//                	throw new BusinessException("收款方没有绑定银行卡");
//                }
            }


        }
        //查票据服务商的联系方式
        if(!payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION0) && !payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION1)){
            BillPlatform billPlatform = billPlatformService.queryBillPlatformByUserId(Long.parseLong(payoutIntf.getVehicleAffiliation()));
            if(null != billPlatform){
                if(CommonUtil.isNumber(billPlatform.getLinkPhone())){
                    payoutIntf.setObjId(Long.parseLong(billPlatform.getLinkPhone()));
                }
            }
        }
        if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE ) {
            SysTenantDef tenantSysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
            if (payoutIntf.getReceivablesBankAccName() != null && payoutIntf.getReceivablesBankAccName().equals(tenantSysTenantDef.getActualController())) {
                payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);
                payoutIntf.setIsTurnAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);
            }
        }

        /**
         * 油到期，默认公对公
         */
        if(Oil_PAYFOR_RECEIVABLE_IN==payoutIntf.getSubjectsId().longValue()){
            AccountBankRel payAccountBankRel = accountBankRelService.getDefaultAccountBankRel(payoutIntf.getPayObjId(), OrderAccountConst.BANK_TYPE.TYPE1, payoutIntf.getPayUserType());
            if(payAccountBankRel!=null){
                payoutIntf.setPayAccNo(payAccountBankRel.getPinganPayAcctId());
                payoutIntf.setPayAccName(payAccountBankRel.getAcctName());
                payoutIntf.setPayBankAccName(payAccountBankRel.getAcctName());
                payoutIntf.setPayBankAccNo(payAccountBankRel.getAcctNo());
                payoutIntf.setAccountType(payAccountBankRel.getBankType()==1?BUSINESS_PAYABLE_ACCOUNT:PRIVATE_PAYABLE_ACCOUNT);
            }

            AccountBankRel receivaAccountBankRel = accountBankRelService.getDefaultAccountBankRel(payoutIntf.getUserId(), OrderAccountConst.BANK_TYPE.TYPE1, payoutIntf.getUserType());
            if(receivaAccountBankRel!=null){
                payoutIntf.setAccNo(receivaAccountBankRel.getPinganCollectAcctId());
                payoutIntf.setAccName(receivaAccountBankRel.getAcctName());
                payoutIntf.setBankType(receivaAccountBankRel.getBankType());
                payoutIntf.setBankCode(receivaAccountBankRel.getBankName());
                payoutIntf.setReceivablesBankAccName(receivaAccountBankRel.getAcctName());
                payoutIntf.setReceivablesBankAccNo(receivaAccountBankRel.getAcctNo());
            }

        }
        //判断如果付款账户没有绑卡，自动推到线下
        if(StringUtils.isBlank(payoutIntf.getPayBankAccNo())){
            payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);
        }
        this.save(payoutIntf);
        this.doSavePayoutInfoExpansion(payoutIntf,token);

    }

    @Override
    public void doSavePayOutIntfForOA(PayoutIntf payoutIntf,String token) {
        //校验业务数据有效性
        payOutIntfUtil.checkPayOutInfToEnData(payoutIntf);
        payoutIntf.setTxnType(OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);
        hasDefaultAcc(Long.parseLong(payoutIntf.getVehicleAffiliation()), SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        if (StringUtils.isBlank(payoutIntf.getOilAffiliation())) {
            payoutIntf.setOilAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
        }
        //获取打款账号
        PinganBankInfoOutDto pinganBankInfoOut = getPinganBankInfoOut(payoutIntf.getPayObjId(), payoutIntf.getPayAccNo(), payoutIntf.getPayUserType());
        if (null != pinganBankInfoOut) {
            if (null == payoutIntf.getAccountType()) {
                throw new BusinessException("请选择付款账户类型");
            }
            if (BUSINESS_PAYABLE_ACCOUNT == payoutIntf.getAccountType().intValue()) {
                payoutIntf.setPayAccNo(pinganBankInfoOut.getCorporatePinganAcctIdN());
                payoutIntf.setPayAccName(pinganBankInfoOut.getCorporateAcctName());
                payoutIntf.setPayBankAccName(pinganBankInfoOut.getCorporateAcctName());
                payoutIntf.setPayBankAccNo(pinganBankInfoOut.getCorporateAcctNo());
            } else if (PRIVATE_PAYABLE_ACCOUNT == payoutIntf.getAccountType().intValue()) {
                payoutIntf.setPayAccNo(pinganBankInfoOut.getPrivatePinganAcctIdN());
                payoutIntf.setPayAccName(pinganBankInfoOut.getPrivateAcctName());
                payoutIntf.setPayBankAccName(pinganBankInfoOut.getPrivateAcctName());
                payoutIntf.setPayBankAccNo(pinganBankInfoOut.getPrivateAcctNo());
            }
        } else {
            payoutIntf.setIsAutomatic(AUTOMATIC0);
        }
        //获取收款账号
        PinganBankInfoOutDto pinganBankInfoOut1 = getPinganBankInfoOut(payoutIntf.getUserId(), payoutIntf.getAccNo(), payoutIntf.getUserType());
        if (null != pinganBankInfoOut1) {
            if (payoutIntf.getBankType() == PRIVATE_RECEIVABLE_ACCOUNT) {
                payoutIntf.setAccNo(pinganBankInfoOut1.getPrivatePinganAcctIdM());
                payoutIntf.setAccName(pinganBankInfoOut1.getPrivateAcctName());
                payoutIntf.setReceivablesBankAccName(pinganBankInfoOut1.getPrivateAcctName());
                payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut1.getPrivateAcctNo());
                payoutIntf.setBankCode(pinganBankInfoOut1.getPrivateBankCode());
            } else if (payoutIntf.getBankType() == BUSINESS_RECEIVABLE_ACCOUNT) {
                payoutIntf.setAccNo(pinganBankInfoOut1.getCorporatePinganAcctIdM());
                payoutIntf.setAccName(pinganBankInfoOut1.getCorporateAcctName());
                payoutIntf.setReceivablesBankAccName(pinganBankInfoOut1.getCorporateAcctName());
                payoutIntf.setReceivablesBankAccNo(pinganBankInfoOut1.getCorporateAcctNo());
                payoutIntf.setBankCode(pinganBankInfoOut1.getCorporateBankCode());
            }
        } else {
            payoutIntf.setIsAutomatic(AUTOMATIC0);
        }
        this.save(payoutIntf);
        this.doSavePayoutInfoExpansion(payoutIntf,token);
    }

    @Override
    public PayoutIntf getPayoutIntfId(String busiCode) {
        LambdaQueryWrapper<PayoutIntf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PayoutIntf::getBusiCode,busiCode);
        PayoutIntf payoutIntf = baseMapper.selectOne(wrapper);
        return payoutIntf;
    }

    @Override
    public PinganBankInfoOutDto getBindBankAc(long userId, String accountNo, Integer userType) {
        PinganBankInfoOutDto pinganBankInfoOut = getPinganBankInfoOut(userId, accountNo, userType);
        return pinganBankInfoOut;
    }

    @Override
    public Long getPayoutIntfBalanceForRedis(Long userId, Integer bankType, String[] txnTypes, String accountNo, Integer userType, Integer payUserType) {
        StringBuffer txnTypesStr = new StringBuffer();
        int length = txnTypes.length;
        for (int i = 0; i < length; i++) {
            txnTypesStr.append("'").append(txnTypes[i]).append("',");
        }
        Long payoutIntfBalanceForRedis = baseMapper.getPayoutIntfBalanceForRedis(userId, bankType, txnTypesStr.substring(0, txnTypesStr.length() - 1), accountNo, userType, payUserType);
        if (payoutIntfBalanceForRedis != null) {
            return payoutIntfBalanceForRedis;
        }
        return 0L;
    }

    @Override
    public Long getAccountLockSum(String custAcctId, int businessType) {
        Long accountLockSum = baseMapper.getAccountLockSum(custAcctId, businessType);
        if (accountLockSum != null) {
            return accountLockSum;
        }
        return 0L;
    }

    @Override
    public Long getPayUnDoPayAccount(String pinganAccId) {
        Long payUnDoPayAccount = baseMapper.getPayUnDoPayAccount(pinganAccId);
        if (payUnDoPayAccount != null) {
            return payUnDoPayAccount;
        }
        return null;
    }

    @Override
    public Long getPayoutIntfBalance(Long userId, int bankType, String accountNo, Integer userType, Integer payUserType) {
        Long payoutIntfBalance = baseMapper.getPayoutIntfBalance(userId, bankType, accountNo, userType, payUserType);
        if (payoutIntfBalance != null) {
            return payoutIntfBalance;
        }
        return 0L;
    }

    @Override
    public void doSavePayPlatformServiceFee(PayoutIntf payoutIntf, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //如果服务费已经支付过并且失败，只需要生成逾期记录，后台继续轮询支付，对调用着无感知
        if (null == payoutIntf.getId()) {
            //默认对公收付平台服务费
            if (StringUtils.isBlank(payoutIntf.getVehicleAffiliation())) {
                payoutIntf.setVehicleAffiliation(VEHICLE_AFFILIATION1);
                payoutIntf.setOilAffiliation(VEHICLE_AFFILIATION1);
            }
            //服务费默认由平台收取
            if (payoutIntf.getIsDriver() == null) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
            }
            //获取平台收款的userid
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(SysStaticDataEnum.PT_TENANT_ID);
            SysUser sysOtherOperator = sysUserService.getSysOperatorByUserIdOrPhone(sysTenantDef.getAdminUser(), null, 0L);
            if (null != sysOtherOperator && null != sysOtherOperator.getBillId()) {
                payoutIntf.setObjId(Long.parseLong(sysOtherOperator.getBillId()));
            }
            if (payoutIntf.getUserId() == null) {
                Long userId = sysTenantDef.getAdminUser();
                payoutIntf.setUserId(userId);
                payoutIntf.setTenantId(sysTenantDef.getId());
            }
            //会员体系改造开始
            payoutIntf.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            payoutIntf.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            //会员体系改造结束
            payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
            payoutIntf.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
            if (null == payoutIntf.getAccountType()) {
                payoutIntf.setAccountType(BUSINESS_PAYABLE_ACCOUNT);
            }
            if (null == payoutIntf.getBankType()) {
                payoutIntf.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
            }
            payoutIntf.setIsNeedBill(SysStaticDataEnum.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL3);//票据类型
            //车队没有对公账户是否可接受私人账户打款？---支持，但不开票
            if (payoutIntf.getPayType() == OrderAccountConst.PAY_TYPE.TENANT) {
                if (!iAccountBankUserTypeRelService.isUserTypeBindCard(payoutIntf.getPayObjId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    payoutIntf.setAccountType(PRIVATE_PAYABLE_ACCOUNT);
                }
            }
            this.doSavePayOutIntfForOA(payoutIntf,accessToken);
        }

        createAccountDetailForPayout(payoutIntf, loginInfo);
    }

    public BankAccInfoDto getVirtualAccInfoNew(Long userId,String vehicleAffiliation,int type,int flg,Long tenantId,Long orderId,int isDirver,Integer userType,String accountNo){
        BankAccInfoDto bankAccInfo = new BankAccInfoDto();
        PinganBankInfoOutDto pinganBankInfoOut = null;
        //获取司机虚拟账号
        if(type == OrderAccountConst.PAY_TYPE.USER){
            //不需要开票取对私账号
            pinganBankInfoOut = getPinganBankInfoOut(userId,"",userType);
            if(null != pinganBankInfoOut){
                //付款--取付款账号
                if(0 == flg){
                    bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                }
                //取收款账号
                else{
                    bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                }
            }else{
                bankAccInfo.setIsAutomatic(AUTOMATIC0);
                bankAccInfo.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
            }

        }else if(type == OrderAccountConst.PAY_TYPE.SERVICE){
            //不需要开票取对私账号
            pinganBankInfoOut = getPinganBankInfoOut(userId,accountNo,userType);
            if(VEHICLE_AFFILIATION0.equals(vehicleAffiliation)){
                if(null != pinganBankInfoOut){
                    //付款--取付款账号
                    if(0 == flg){
                        bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                    }
                    //取收款账号
                    else{
                        //服务商到期打款时
                        //不需要平台开票的资金，如果服务商没有绑定对私帐户，则打给服务商对公帐户；如果服务商绑定了对私账户，则打款给对私账户。
                        if(StringUtils.isNotBlank(pinganBankInfoOut.getPrivatePinganAcctIdM())){
                            bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                        }else{
                            bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                        }
                    }

                }else{
                    bankAccInfo.setIsAutomatic(AUTOMATIC0);
                    bankAccInfo.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
                }
            }else{
                if(null != pinganBankInfoOut){
                    bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                }else{
                    bankAccInfo.setIsAutomatic(AUTOMATIC0);
                    bankAccInfo.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
                }
            }
        }else if(type == OrderAccountConst.PAY_TYPE.TENANT){
            //不需要开票取对私账号
            if(VEHICLE_AFFILIATION0.equals(vehicleAffiliation)){
                pinganBankInfoOut = getPinganBankInfoOut(userId,accountNo,userType);
                if(null != pinganBankInfoOut){
                    //付款--取付款账号
                    if(0 == flg){
                        //支付自有车运费的时候取车队的默认付款账户
                        boolean els = false;
                        if(null != orderId && -1 != orderId.longValue() && 0!=orderId.longValue() && isDirver==OrderAccountConst.PAY_TYPE.USER){
                             OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                            if(null != orderScheduler && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()){
                                els =  true;
                            }
                        }
                        if(els){
                            long accountType = payFeeLimitService.getAmountLimitCfgVal(tenantId,OWN_CAR_TRANSFER_SALARY_402);
                            if(accountType == BANK_TYPE_0){
                                bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                            }else{
                                bankAccInfo.setCorporatePinganAcctIdN(pinganBankInfoOut);
                            }
                        }else{
                            bankAccInfo.setPrivatePinganAcctIdN(pinganBankInfoOut);
                        }
                    }
                    //取收款账号
                    else{
                        bankAccInfo.setPrivatePinganAcctIdM(pinganBankInfoOut);
                    }

                }else{
                    bankAccInfo.setIsAutomatic(AUTOMATIC0);
                    bankAccInfo.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
                }
            }
            //开票对公账号
            else{
                if(!VEHICLE_AFFILIATION1.equals(vehicleAffiliation)&&null != orderId && -1 != orderId.longValue() && 0!=orderId.longValue()){
                	/*IOrderLimitSV orderLimitSV = (IOrderLimitSV)SysContexts.getBean("orderLimitSV");
                	OrderLimit ol =orderLimitSV.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);*/
                    pinganBankInfoOut = getPinganBankInfoOut(userId,accountNo,userType);
                }else{
                    pinganBankInfoOut = getPinganBankInfoOut(userId,"",userType);
                }
                if(null != pinganBankInfoOut){
                    //付款--取付款账号
                    if(0 == flg){
                        //取车队的付款账户,如用油费预存资金，取预存资金账户打款，否则取默认账户
                        bankAccInfo.setCorporatePinganAcctIdN(pinganBankInfoOut);
                    }
                    //取收款账号
                    else{
                        bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
                    }
                }else{
                    bankAccInfo.setIsAutomatic(AUTOMATIC0);
                    bankAccInfo.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
                }
            }
        }

        //平台票据对公账号
        else if(type == HAVIR){
            userId = Long.parseLong(vehicleAffiliation);
            pinganBankInfoOut = getPinganBankInfoOut(userId,"", SysStaticDataEnum.USER_TYPE.SERVICE_USER);
            if(null == pinganBankInfoOut){
                throw new BusinessException("获取票据服务商银行信息错误！");
            }
            //付款--取付款账号
            if(0 == flg){
                bankAccInfo.setCorporatePinganAcctIdN(pinganBankInfoOut);
            }
            //取收款账号
            else{
                bankAccInfo.setCorporatePinganAcctIdM(pinganBankInfoOut);
            }
        }
        return bankAccInfo;
    }

    @Override
    public boolean judge(PayoutIntf payoutIntf, PayoutIntfExpansion payoutIntfExpansion) {
//        IAccountBankRelSV accountBankRelSV = (IAccountBankRelSV) SysContexts.getBean("accountBankRelSV");
//        ITenantTF tenantTF = CallerProxy.getSVBean(ITenantTF.class,"tenantTF");
//        Session session = SysContexts.getEntityManager();
        //   StringBuffer selectSql = new StringBuffer();
        if (payoutIntfExpansion.getIsNeedBill() != OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            return true;
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
        if (sysTenantDef == null) {
            return true;
        }
        Long adminUserId = sysTenantDef.getAdminUser();
        AccountBankRel receiptRel = accountBankRelService.getAcctNo(payoutIntf.getPinganCollectAcctId());
        AccountBankRel paymentRel = accountBankRelService.getAcctNo(payoutIntf.getPayAccNo());
        if (receiptRel == null || paymentRel == null) {
            return false;
        }
        //票据服务费与开票管理中的收付款账户名必须一致
        if (payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB || payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB) {
            LambdaQueryWrapper<BaseBillInfo> baseBillInfoLambdaQueryWrapper = Wrappers.lambdaQuery();
            baseBillInfoLambdaQueryWrapper.eq(BaseBillInfo::getApplyNum, payoutIntf.getBusiCode());
            List<BaseBillInfo> baseBillInfos = baseBillInfoService.list(baseBillInfoLambdaQueryWrapper);
            if (baseBillInfos != null && baseBillInfos.size() > 0) {
                BaseBillInfo baseBillInfo = baseBillInfos.get(0);
                if (baseBillInfo.getReceiveAcctName().equals(receiptRel.getAcctName()) && baseBillInfo.getPayAcctName().equals(paymentRel.getAcctName())) {
                    return true;
                }
            } else {
                return true;
            }
            return false;
        }
        //同一笔订单的付款记录收付款必须一致
        if (payoutIntf.getOrderId() == null || payoutIntf.getOrderId() <= 0) {
            return true;
        }
//        String sql = " select p.PAY_ACC_NAME payBankAccName,p.RECEIVABLES_BANK_ACC_NAME receivablesBankAccName,p.txn_amt txnAmt"
//                + " from payout_intf p where  p.TXN_TYPE=200 "
//                +" AND (p.RESP_CODE!=5 or p.RESP_CODE IS NULL) "
//                +" AND p.PAY_TENANT_ID=:payTenantId and p.PAY_OBJ_ID=:payObjId and p.ORDER_ID=:orderId "
//                +" AND p.IS_AUTOMATIC =1 and IS_DRIVER=3 and p.FLOW_ID !=:flowId  ";
        LambdaQueryWrapper<PayoutIntf> payoutIntfLambdaQueryWrapper = Wrappers.lambdaQuery();
        payoutIntfLambdaQueryWrapper.eq(PayoutIntf::getTxnType, 200).and(wq -> wq.ne(PayoutIntf::getRespCode, 5).or().eq(PayoutIntf::getRespCode, null))
                .eq(PayoutIntf::getPayTenantId, payoutIntf.getPayTenantId()).eq(PayoutIntf::getPayObjId, adminUserId)
                .eq(PayoutIntf::getOrderId, payoutIntf.getOrderId())
                .eq(PayoutIntf::getIsAutomatic, 1)
                .eq(PayoutIntf::getIsDriver, 3)
                .ne(PayoutIntf::getId, payoutIntf.getId());
//        Query query  = session.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//        query.setParameter("payTenantId", payoutIntf.getPayTenantId());
//        query.setParameter("payObjId", adminUserId);
//        query.setParameter("orderId", payoutIntf.getOrderId());
//        query.setParameter("flowId", payoutIntf.getFlowId());
        List<PayoutIntf> maps1 = super.list(payoutIntfLambdaQueryWrapper);
        if (maps1 != null && maps1.size() > 0) {
            PayoutIntf map = maps1.get(0);
            String payBankAccName = map.getPayBankAccName();
            if (!payBankAccName.equals(paymentRel.getAcctName())) {
                return false;
            }
        }
//        String sqlPay = " select p.PAY_ACC_NAME payBankAccName,p.RECEIVABLES_BANK_ACC_NAME receivablesBankAccName,p.txn_amt txnAmt"
//                + " from payout_intf p where  p.TXN_TYPE=200 "
//                + " AND (p.RESP_CODE!=5 or p.RESP_CODE IS NULL) "
//                + " AND p.PAY_TENANT_ID=:payTenantId and p.PAY_OBJ_ID=:payObjId and p.ORDER_ID=:orderId "
//                + " AND p.IS_AUTOMATIC =1 and IS_DRIVER=3 and p.FLOW_ID !=:flowId and p.user_id=:userId ";
//        Query query1  = session.createSQLQuery(sqlPay).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//        query1.setParameter("payTenantId", payoutIntf.getPayTenantId());
//        query1.setParameter("payObjId", adminUserId);
//        query1.setParameter("orderId", payoutIntf.getOrderId());
//        query1.setParameter("flowId", payoutIntf.getFlowId());
//        query1.setParameter("userId", payoutIntf.getUserId());
        payoutIntfLambdaQueryWrapper.eq(PayoutIntf::getUserId, payoutIntf.getUserId());
        List<PayoutIntf> maps = super.list(payoutIntfLambdaQueryWrapper);
        if (maps == null || maps.size() == 0) {
            return true;
        }
        PayoutIntf map = maps.get(0);
        String payBankAccName = map.getPayAccName();
        String receivablesBankAccName = map.getReceivablesBankAccName();
        if (map.getTxnAmt() == null || Long.valueOf(map.getTxnAmt() + "") == 0L) {
            if (!payBankAccName.equals(paymentRel.getAcctName())) {
                return false;
            } else {
                return true;
            }
        } else {
            for (PayoutIntf m : maps) {
                if (m.getTxnAmt() != null && Long.valueOf(m.getTxnAmt() + "") > 0L) {
                    payBankAccName = m.getPayAccName() == null ? "":m.getPayAccName();
                    receivablesBankAccName = m.getReceivablesBankAccName() == null ? "":m.getPayAccName();
                }
            }
        }
        if (payBankAccName.equals(paymentRel.getAcctName()) && receivablesBankAccName.equals(receiptRel.getAcctName())) {
            return true;
        }
        return false;
    }

    @Override
    public Map judgeName(PayoutIntf payoutIntf, PayoutIntfExpansion payoutIntfExpansion) {
        Map mapsp = new HashMap<>();
        mapsp.put("payBankAccName", "");
        mapsp.put("receivablesBankAccName", "");
        StringBuffer selectSql = new StringBuffer();
        if(payoutIntfExpansion.getIsNeedBill() != OrderConsts.IS_NEED_BILL.TERRACE_BILL){
            return mapsp;
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
        if(sysTenantDef == null){
            return mapsp;
        }
        Long adminUserId = sysTenantDef.getAdminUser();
        AccountBankRel receiptRel =  accountBankRelService.getAcctNo(payoutIntf.getPinganCollectAcctId());
        AccountBankRel paymentRel =  accountBankRelService.getAcctNo(payoutIntf.getPayAccNo());
        if(receiptRel == null || paymentRel == null){
            return mapsp;
        }
        //票据服务费与开票管理中的收付款账户名必须一致
        if(payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB||payoutIntf.getSubjectsId() == EnumConsts.SubjectIds.BILL_56K_RECEIVABLE_OVERDUE_SUB){
            LambdaQueryWrapper<BaseBillInfo> baseBillInfoLambdaQueryWrapper = Wrappers.lambdaQuery();
            baseBillInfoLambdaQueryWrapper.eq(BaseBillInfo::getApplyNum,payoutIntf.getBusiCode());
            List<BaseBillInfo> list = baseBillInfoService.list(baseBillInfoLambdaQueryWrapper);
            if(list != null && list.size()>0) {
                BaseBillInfo baseBillInfo = list.get(0);
                if (baseBillInfo.getReceiveAcctName().equals(receiptRel.getAcctName()) && baseBillInfo.getPayAcctName().equals(paymentRel.getAcctName())) {
                    return mapsp;
                }
                mapsp.put("payBankAccName", baseBillInfo.getPayAcctName());
                mapsp.put("receivablesBankAccName", baseBillInfo.getReceiveAcctName());
            }
            return mapsp;
        }
        //同一笔订单的付款记录收付款必须一致
        if(payoutIntf.getOrderId() == null || payoutIntf.getOrderId() <= 0){
            return mapsp;
        }
        LambdaQueryWrapper<PayoutIntf> payoutIntfLambdaQueryWrapper = Wrappers.lambdaQuery();
        payoutIntfLambdaQueryWrapper
                .eq(PayoutIntf::getTxnType, 200)
                .and(wq -> wq.ne(PayoutIntf::getRespCode, 5).or().eq(PayoutIntf::getRespCode, null))
                .eq(PayoutIntf::getPayTenantId, payoutIntf.getPayTenantId())
                .eq(PayoutIntf::getPayObjId, adminUserId)
                .eq(PayoutIntf::getOrderId, payoutIntf.getOrderId())
                .eq(PayoutIntf::getIsAutomatic, 1)
                .eq(PayoutIntf::getIsDriver, 3)
                .ne(PayoutIntf::getId, payoutIntf.getId());
        List<PayoutIntf> maps1 = super.list();
        if(maps1 != null && maps1.size() > 0){
            PayoutIntf map = maps1.get(0);
            String payBankAccName = map.getPayAccName();
            String payBankAccNo = map.getPayBankAccNo();
          //  String receivablesBankAccName = map.getReceivablesBankAccName();
            String receivablesBankAccNo = map.getReceivablesBankAccNo();
            String payAccNo = map.getPayAccNo();
            String payAccName = map.getPayAccName();
            if(!payBankAccName.equals(paymentRel.getAcctName())){
                mapsp.put("payAccNo", payAccNo);
                mapsp.put("payAccName", payAccName);
                mapsp.put("payBankAccName", payBankAccName);
                mapsp.put("payBankAccNo", payBankAccNo);
                //mapsp.put("receivablesBankAccName", receivablesBankAccName);
                mapsp.put("receivablesBankAccNo", receivablesBankAccNo);
                return mapsp;
            }
        }
        payoutIntfLambdaQueryWrapper.eq(PayoutIntf::getUserId,payoutIntf.getUserId());
        List<PayoutIntf> maps = super.list(payoutIntfLambdaQueryWrapper);
        if(maps == null || maps.size() == 0){
            return mapsp;
        }
        PayoutIntf map = maps.get(0);
        String payBankAccName = map.getPayAccName();
        String payBankAccNo = map.getPayBankAccNo();
        String receivablesBankAccName = map.getReceivablesBankAccName();
        String receivablesBankAccNo = map.getReceivablesBankAccNo();
        String payAccNo = map.getPayAccNo();
        String payAccName = map.getPayAccName();
        if(payBankAccName.equals(paymentRel.getAcctName()) && receivablesBankAccName.equals(receiptRel.getAcctName())){
            return mapsp;
        }
        mapsp.put("payAccNo", payAccNo);
        mapsp.put("payAccName", payAccName);
        mapsp.put("payBankAccName", payBankAccName);
        mapsp.put("payBankAccNo", payBankAccNo);
        mapsp.put("receivablesBankAccName", receivablesBankAccName);
        mapsp.put("receivablesBankAccNo", receivablesBankAccNo);
        return mapsp;
    }

    @Override
    public List<PayoutIntf> getPayoutIntfByBusiCode(String busiCode, String txnType) {
        QueryWrapper<PayoutIntf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("txn_type",txnType);
        queryWrapper.eq("busi_code",busiCode);
//        "select * from payout_intf where txt_type = 1 and busi_code = 2 and (resp_code is null or resp_code >1)";
        queryWrapper.or().isNull("resp_code").ne("resp_code", HttpsMain.respCodeInvalid);
        List<PayoutIntf> payoutIntfs = this.list(queryWrapper);
        return payoutIntfs;
    }

    @Override
    public IPage getDueDateDetailsWX(Long userId, Long flowId, String name, String state, Long sourceTenantId, Integer userType,
                                     Long pageSize,Long pageNum,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        List<String> states = null;
        if(userId < 0){
            throw new BusinessException("未传入userId!");
        }
        UserDataInfo userDataInfo = userDataInfoService.selectUserType(user.getUserInfoId());
        if(userDataInfo.getUserType() < 0){
            userType = user.getUserType();
        }
        if (state != null){
            states = Arrays.stream(state.split(",")).collect(Collectors.toList());
        }
        Page page = new Page(pageNum,pageSize);
        Page<PayoutInfoOutDto> payoutInfoOutDto = payoutIntfMapper.selectOrList(page,userId,flowId,name,sourceTenantId,userDataInfo.getUserType(),states,state);


        for (PayoutInfoOutDto record : payoutInfoOutDto.getRecords()) {

            if(EnumConsts.PAY_CONFIRM.recharge==record.getPayConfirm()
                    && OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0 == record.getIsAutoMatic()){
                record.setRespCodeName("车队已打款");
            }else if(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1==record.getIsAutoMatic()){
                record.setRespCodeName("线上打款中");
            }
            if(EnumConsts.PAY_CONFIRM.transfer ==record.getPayConfirm()){
                record.setStateName("已确认线下收款");
            }else {
                record.setStateName("已逾期");
            }
            if(org.apache.commons.lang3.StringUtils.isNotBlank(record.getFileUrl())){
                record.setShowFile(true);
            }
            else {
                record.setShowFile(false);
            }
            if(record.getBankType() != null && record.getPayConfirm() != 0){
                record.setBankTypeName(readisUtil.getSysStaticData("BANK_TYPE_STATE",record.getBankType()+"").getCodeName());
            }
            if(record.getIsAutoMatic() != null && record.getPayConfirm() != 0 ){
                record.setIsAutomaticName(readisUtil.getSysStaticData("IS_AUTOMATIC",record.getIsAutoMatic()+"").getCodeName());
            }
            if(record.getIsNeedBill() != null){
                record.setIsNeedBillName(readisUtil.getSysStaticData("FUNDS_IS_NEED_BILL",record.getIsNeedBill()+"").getCodeName());
            }
            if(record.getTxnAmt() != null){
                record.setTxnAmtDouble(CommonUtil.getDoubleFormatLongMoney(record.getTxnAmt(), 2));
            }
            if(record.getSourceRegion() != null){
                record.setSourceRegionName(readisUtil.getSysStaticData("SYS_CITY",record.getSourceRegion()+"").getCodeName());
            }
            if(record.getDesRegion() != null){
                record.setDesRegionName(readisUtil.getSysStaticData("SYS_CITY",record.getDesRegion()+"").getCodeName());
            }
            if(record.getRespCode() != null){
                record.setRespCodeName(readisUtil.getSysStaticData("RESP_CODE_TYPE",record.getRespCode()+"").getCodeName());
            }
            if(record.getBusiId() != null){
                record.setBusiIdName(readisUtil.getSysStaticData("BUSINESS_NUMBER_TYPE",record.getBusiId()+"").getCodeName());
                if(record.getSubjectsId()!=null&& record.getSubjectsId()== EnumConsts.SubjectIds.TIRE_RENTAL_FEE_4020) {
                    record.setBusiIdName("支付轮胎租赁费");
                }
            }
            if(record.getBillServiceFee() != null){
                record.setBillServiceFeeDouble(CommonUtil.getDoubleFormatLongMoney(record.getBillServiceFee(), 2));
            }
            if(record.getAppendFreight() != null){
                record.setAppendFreightDouble(CommonUtil.getDoubleFormatLongMoney(record.getAppendFreight(), 2));
            }
            record.setSumTxnFee(((record.getTxnAmt()==null?0L:record.getTxnAmt())+(record.getBillServiceFee()==null?0L:record.getBillServiceFee()) + (record.getAppendFreight()==null?0L:record.getAppendFreight())));
            if(record.getSumTxnFee() != null ){
                record.setSumTxnFeeDouble(CommonUtil.getDoubleFormatLongMoney(record.getSumTxnFee(), 2));
            }
        }
        return payoutInfoOutDto;
    }

    @Override
    public WXShopDto getServiceOverView(String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Long userId = user.getUserInfoId();
        Integer userType = userDataInfoService.selectUserType(user.getUserInfoId()).getUserType();

        OrderAccountBalanceDto orderAccountBalanceDto = new OrderAccountBalanceDto();
        //1:对公收款账户余额，11:对公付款账户余额，2：对私收款账户余额+支付中心金额，22对私付款账户余额
//        SysCfg sysCfg = sysCfgRedisUtils.getSysCfg("BANK_INIT",accessToken);
//        if(sysCfg==null){
//            throw new BusinessException("没有配置银行初始参数!");
//        }
        Double payHA = iBaseBusiToOrderService.getPcBalance(userId,userType);//支付中心的余额

        Double incomeBalance1 = 0.00;//对公收款账户余额
        Double incomeBalance2 = 0.00;//对私收款账户余额
        //是否服务商
        ServiceInfo serviceInfo = serviceInfoService.getServiceInfoById(userId);
        if(serviceInfo!=null){
            incomeBalance1 += payHA;
        }else{
            incomeBalance2 += payHA;
        }
        Double payBalance1 = 0.00;//对公付款账户余额
        Double payBalance2 = 0.00;//对私付款账户余额
        Double totalBalance1 = 0.00;//对公可提现
        Double totalBalance2 = 0.00;//对私可提现
        List<AccountBankRel> accountBankRelList = accountBankRelService.queryAccountBankRel(userId, userType, null);
        for (int i = 0; i < accountBankRelList.size(); i++) {
            AccountBankRel accountBankRel = accountBankRelList.get(i);
            BankBalanceInfo bankBalanceInfo1 = new BankBalanceInfo();
            BankBalanceInfo bankBalanceInfo11 = new BankBalanceInfo();
            BankBalanceInfo bankBalanceInfo22 = new BankBalanceInfo();
            BankBalanceInfo bankBalanceInfo2 = new BankBalanceInfo();

            if (bankBalanceInfo1 != null) {
                incomeBalance1 += (bankBalanceInfo1.getBalance() == null ? 0.0 : bankBalanceInfo1.getBalance());
            }
            if (bankBalanceInfo2 != null) {
                incomeBalance2 += (bankBalanceInfo2.getBalance() == null ? 0.0 : bankBalanceInfo2.getBalance());
            }
            if (bankBalanceInfo11 != null) {
                payBalance1 += (bankBalanceInfo11.getBalance() == null ? 0.0 : bankBalanceInfo11.getBalance());
            }
            if (bankBalanceInfo22 != null) {
                payBalance2 += (bankBalanceInfo22.getBalance() == null ? 0.0 : bankBalanceInfo22.getBalance());
            }
        }
        totalBalance1 = incomeBalance1+payBalance1;
        totalBalance2 = incomeBalance2+payBalance2;
        orderAccountBalanceDto = iOrderAccountService.getOrderAccountBalance(userId, null,-1L,userType);
        OrderAccountOutVo orderAccountOutDto = orderAccountBalanceDto.getOa();
        WXShopDto wxShopDto = new WXShopDto();
        wxShopDto.setTotalBalance(orderAccountOutDto.getTotalMarginBalance());//即将到期
//        Long receivableOverdueBalance = iServiceProviderBillService.getReceivableOverdueBalance(accessToken);
//        wxShopDto.setReceivableOverdueBalance(receivableOverdueBalance); // 应收逾期金额
        //应收逾期新逻辑
       // payoutIntfThreeService.getOverdueCDSum(null, null, null, null, user.getUserInfoId(), accessToken);
        QueryPayoutIntfsVo queryPayoutIntfsVo = new QueryPayoutIntfsVo();
        queryPayoutIntfsVo.setSourceUserId(userId);
        Map m = payoutIntfThreeService.queryPayoutIntfsSum(accessToken,queryPayoutIntfsVo);
        Long overDue = 0L;
        if(m.get("noVerificatMoney") != null) {
            String noVerificatMoney = String.valueOf(m.get("noVerificatMoney"));
            overDue = Long.parseLong(noVerificatMoney);
        }
        wxShopDto.setReceivableOverdueBalance(overDue); // 应收逾期金额
        wxShopDto.setPayableOverdueBalance(orderAccountOutDto.getPayableOverdueBalance());//应付逾期金额
        wxShopDto.setElectronicOilCard(orderAccountOutDto.getElectronicOilCard());//电子油卡
        wxShopDto.setTotalOilBalance(orderAccountOutDto.getTotalOilBalance());//客户油费
        wxShopDto.setLockBalance(orderAccountOutDto.getLockBalance());//预存资金
        wxShopDto.setRechargeOilBalance(orderAccountOutDto.getRechargeOilBalance());//充值账户
        wxShopDto.setCreditLine(orderAccountOutDto.getCreditLine());//授信额度
        wxShopDto.setUsedCreditLine(orderAccountOutDto.getUsedCreditLine());//已用
        List<Object> list = iPlatformServiceChargeService.getNoVerificationAmountByUserId(userId);
        Long platformServiceCharge=0L;
        if (list != null && list.size() > 0) {
            Object obj = list.get(0);
            platformServiceCharge = (obj == null ? 0 : ((Number)obj).longValue());
        }
        wxShopDto.setPlatformServiceCharge(platformServiceCharge);//平台服务费金额

        List<Object> list1 = iPlatformServiceChargeService.getVerificationPlatformAmountByUserId(userId);
        Long noVerificationAmount=0L;
        if (list1 != null && list1.size() > 0) {
            Object obj = list1.get(0);
            noVerificationAmount = (obj == null ? 0 : ((Number)obj).longValue());
        }
        wxShopDto.setNoVerificationAmount(noVerificationAmount);//未核销平台金额(分)


        int cooperationWaitAduitNum = iServiceInvitationService.countInvitationAuth(accessToken);//合作邀请待审核数量
        int productNum = iServiceProductService.countProductNum(accessToken);//产品管理数量
        wxShopDto.setProductNum(productNum);//产品管理数量
        wxShopDto.setCooperationWaitAduitNum(cooperationWaitAduitNum);//合作邀请待审核数量
        wxShopDto.setIsUserBindCard(accountBankRelService.isUserBindCardAll(userId,accessToken));//判断租户是否已绑定绑定银行卡
        return wxShopDto;
    }

    @Override
    public Page<PayoutInfosOutDto> getOverdue(Long userId, Long orderId, String userType, String name, String businessNumbers, String state, Long sourceTenantId, Integer pageSize, Integer pageNum, String accessToken) {
        if(StringUtils.isBlank(userType)){
            userType = SysStaticDataEnum.USER_TYPE.DRIVER_USER+"";
        }
        if(userId < 0){
            throw new BusinessException("未传入userId!");
        }
        List<String> busiIds = null;
        if (businessNumbers != null){
            busiIds = Arrays.stream(businessNumbers.split(",")).collect(Collectors.toList());
        }
        List<String> states = null;
        if (state != null){
            states = Arrays.stream(state.split(",")).collect(Collectors.toList());
        }
        Page<PayoutInfosOutDto> payoutInfosOutDto = payoutIntfMapper.selectBeOverdue(userId,orderId,userType,name,businessNumbers,busiIds,state,states,sourceTenantId,new Page<>(pageNum,pageSize));

        for (PayoutInfosOutDto infoOut: payoutInfosOutDto.getRecords()) {
            if(String.valueOf(EnumConsts.PAY_CONFIRM.transfer).equals(infoOut.getPayConfirm())){
                infoOut.setRespCodeName("已退款");
            }else if(String.valueOf(EnumConsts.PAY_CONFIRM.recharge).equals(infoOut.getPayConfirm()) && String.valueOf(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0).equals(infoOut.getIsAutoMatic())) {
                infoOut.setRespCodeName("车队确认中");
            }else if (String.valueOf(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1).equals(infoOut.getIsAutoMatic()) && infoOut.getBusiId() != EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD) {
                infoOut.setRespCodeName("线上打款中");
            }
            if(EnumConsts.PAY_CONFIRM.transfer==infoOut.getPayConfirm()){
                infoOut.setStateName("已线下退款");
            }else if(EnumConsts.PAY_CONFIRM.recharge==infoOut.getPayConfirm() &&  OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0==infoOut.getIsAutoMatic()){
                infoOut.setStateName("车队确认中");
            }else {
//				infoOut.setStateName("已逾期");
                infoOut.setStateName("未退款");
            }

        }
        return payoutInfosOutDto;
    }

    @Override
    public String confirmPaymentLine(Long flowId, String payAcctId, String receAcctId, String accessToken) {
        if(flowId < 0){
            throw new BusinessException("未传入流水号!");
        }
        PayoutIntf payoutIntf = this.getById(flowId);
        if(payoutIntf == null ){
            throw new BusinessException("未找到打款记录!");
        }
        if(payoutIntf.getIsAutomatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1)){
            throw new BusinessException("此记录状态已为线上退款!");
        }
        AccountBankRel paymentRel = null;
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(payAcctId)){ //兼容APP 没传拿默认
            //throw new BusinessException("未绑卡只能选择线下付款!");
            paymentRel  = accountBankRelService.getAcctNo(payAcctId);
        }else {
            paymentRel = accountBankRelService.getDefaultAccountBankRel(payoutIntf.getPayObjId(),OrderAccountConst.BANK_TYPE.TYPE0,payoutIntf.getPayUserType());
        }

        if(paymentRel == null){
            throw new BusinessException("您未绑卡只能选择线下打款!");
        }
        if(!org.apache.commons.lang3.StringUtils.isNotEmpty(paymentRel.getAcctNo())){
            throw new BusinessException("您未绑卡只能选择线下打款!");
        }

        if(org.apache.commons.lang3.StringUtils.isNotBlank(receAcctId)){//兼容员工账户
            AccountBankRel receRel  = accountBankRelService.getAcctNo(receAcctId);
            payoutIntf.setAccName(receRel.getAcctName());
            payoutIntf.setAccNo(receRel.getPinganCollectAcctId());
            payoutIntf.setReceivablesBankAccNo(receRel.getAcctNo());
            payoutIntf.setReceivablesBankAccName(receRel.getAcctName());
            payoutIntf.setBankType(receRel.getBankType());
        }else{
            //收款信息
            AccountBankRel receiptRelDs = accountBankRelService.getDefaultAccountBankRel(payoutIntf.getUserId(),OrderAccountConst.BANK_TYPE.TYPE0,payoutIntf.getUserType());
            AccountBankRel receiptRelDg = accountBankRelService.getDefaultAccountBankRel(payoutIntf.getUserId(),OrderAccountConst.BANK_TYPE.TYPE1,payoutIntf.getUserType());
            if(receiptRelDs != null && org.apache.commons.lang3.StringUtils.isNotEmpty(receiptRelDs.getAcctNo()) ){ //收款方默认对私
                if(receiptRelDs == null){
                    throw new BusinessException("收款方未绑卡只能选择线下打款!");
                }
                if(!org.apache.commons.lang3.StringUtils.isNotEmpty(receiptRelDs.getAcctNo())){
                    throw new BusinessException("收款方未绑卡只能选择线下打款!");
                }
                if(receiptRelDs != null){
                    payoutIntf.setAccName(receiptRelDs.getAcctName());
                    payoutIntf.setAccNo(receiptRelDs.getPinganCollectAcctId());
                    payoutIntf.setReceivablesBankAccNo(receiptRelDs.getAcctNo());
                    payoutIntf.setReceivablesBankAccName(receiptRelDs.getAcctName());
                    payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
                }
            }else if (receiptRelDg != null && org.apache.commons.lang3.StringUtils.isNotEmpty(receiptRelDg.getAcctNo())) {//收款方默认对公
                if(receiptRelDg == null){
                    throw new BusinessException("收款方未绑卡只能选择线下打款!");
                }
                if(!org.apache.commons.lang3.StringUtils.isNotEmpty(receiptRelDg.getAcctNo())){
                    throw new BusinessException("收款方未绑卡只能选择线下打款!");
                }
                if(receiptRelDg != null){
                    payoutIntf.setAccName(receiptRelDg.getAcctName());
                    payoutIntf.setAccNo(receiptRelDg.getPinganCollectAcctId());
                    payoutIntf.setReceivablesBankAccNo(receiptRelDg.getAcctNo());
                    payoutIntf.setReceivablesBankAccName(receiptRelDg.getAcctName());
                    payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT);
                }
            }else {
                throw new BusinessException("收款方未绑卡只能选择线下打款!");
            }
        }

        //付款信息
        if(paymentRel != null){
            payoutIntf.setPayBankAccName(paymentRel.getAcctName());
            payoutIntf.setPayBankAccNo(paymentRel.getAcctNo());
            payoutIntf.setPayAccName(paymentRel.getAcctName());
            payoutIntf.setPayAccNo(paymentRel.getPinganPayAcctId());
            payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);
        }
        payoutIntf.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
        payoutIntf.setIsTurnAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
        payoutIntf.setRemark("线上打款");
        this.saveOrUpdate(payoutIntf);
        return "Y";
    }

    @Override
    public PayoutInfoDto paymentDetails(Long flowId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        PayoutIntf payoutIntf = this.getById(flowId);
        PayoutInfoDto infoOut = new PayoutInfoDto();
        infoOut.setFlowId(flowId);
        if (payoutIntf.getCreateDate()!=null){
            infoOut.setCreateDate(String.valueOf(payoutIntf.getCreateDate()));
        }

        infoOut.setAccName(payoutIntf.getReceivablesBankAccName());
        infoOut.setOrderId(payoutIntf.getOrderId());
        infoOut.setBusiId(payoutIntf.getBusiId());
        infoOut.setIsAutoMatic(payoutIntf.getIsAutomatic());
        infoOut.setIsAutomaticName(readisUtil.getSysStaticData("IS_AUTOMATIC", payoutIntf.getIsAutomatic()+"").getCodeName());
        infoOut.setPayConfirm(payoutIntf.getPayConfirm());
        infoOut.setVerificationState(payoutIntf.getVerificationState());
        PayoutIntfExpansion expansion = payoutIntfExpansionService.getPayoutIntfExpansion(payoutIntf.getId());
        infoOut.setIsNeedBill(expansion.getIsNeedBill().longValue());
        infoOut.setTxnAmt(payoutIntf.getTxnAmt());
        infoOut.setIsAutoMatic(payoutIntf.getIsAutomatic());
        infoOut.setAccNo(payoutIntf.getReceivablesBankAccNo());
        if(payoutIntf.getVerificationDate()==null){
            infoOut.setVerificationDate(null);
        }else {
            infoOut.setVerificationDate(getLocalDateTimeToDate(payoutIntf.getVerificationDate()));
        }

        infoOut.setBillServiceFee(payoutIntf.getBillServiceFee());
        infoOut.setAppendFreight(payoutIntf.getAppendFreight());
        infoOut.setVehicleAffiliation(Long.valueOf(payoutIntf.getVehicleAffiliation()));
        if(payoutIntf.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.transfer)){
            infoOut.setRespCodeName("已付款");
        }else if(payoutIntf.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge) && payoutIntf.getIsAutomatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
            infoOut.setRespCodeName("收款确认中");
        }else if (payoutIntf.getIsAutomatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) && payoutIntf.getBusiId() != EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD) {
            infoOut.setRespCodeName("线上打款中");
        }else if (payoutIntf.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.withdraw) && payoutIntf.getIsAutomatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0)) {
            infoOut.setRespCodeName("已逾期");
        }
        List<SysOperLog> sysOperLogs = sysOperLogService.querySysOperLog(SysOperLogConst.BusiCode.Payoutchunying,
                flowId, false,user.getTenantId(), AuditConsts.AUDIT_CODE.PAY_CASH_CODE,null);
        infoOut.setSysOperLogs(sysOperLogs);

        return infoOut;
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
    public String confirmPaymentOffline(Long flowId, String accessToken) {
        if(flowId < 0){
            throw new BusinessException("未传入流水号!");
        }
        PayoutIntf payoutIntf = this.getById(flowId);
        if(payoutIntf.getIsAutomatic().equals(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1)){
            throw new BusinessException("不允许将线上付款数据转为线下退款!");
        }
        if(payoutIntf.getPayConfirm() != null && payoutIntf.getPayConfirm().equals(EnumConsts.PAY_CONFIRM.recharge)){
            throw new BusinessException("此条记录正在线下打款中!");
        }
        payoutIntf.setPayConfirm(EnumConsts.PAY_CONFIRM.recharge);
        payoutIntf.setRespCode("3");//线下打款成功
        payoutIntf.setRemark("线下打款");
        payoutIntf.setRespMsg("打款成功");
        this.saveOrUpdate(payoutIntf);
        return "Y";
    }

    @Override
    public Page<PayoutInfosOutDto> billingDetailsByWx(Long userId, String month,
                                                      String fleetName, Integer userType,
                                                      Integer payUserType, Integer pageNum,
                                                      Integer pageSize) {
        Page<PayoutInfosOutDto> page = new Page<>(pageNum, pageSize);
        List<Long> tenantId = new ArrayList<Long>();
        if (StringUtils.isNotEmpty(fleetName)) {
            //根据车队名称模糊查询
            List<SysTenantDef> list = sysTenantDefService.getSysTenantDefByName(fleetName);
            if (list == null || list.size() <= 0) {
                tenantId.add(-1L);
            } else {
                for (SysTenantDef sysTenantDef : list) {
                    tenantId.add(sysTenantDef.getId());
                }
            }
        }
        Page<PayoutInfosOutDto> payoutInfosOutDtoPage = baseMapper.billingDetailsByWx(page, userId, month, userType, payUserType, tenantId);
        return payoutInfosOutDtoPage;
    }

    @Override
    public BankDto balanceJudgmentCD(Long balance, Integer type, Integer userType, String payAcctId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        AccountBankRel receiptRel = null;
        Long userId = sysTenantDefService.getSysTenantDef(user.getTenantId()).getAdminUser();
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(payAcctId)){//兼容小程序不传拿默认
            //throw new BusinessException("未绑卡只能选择线下付款!");
            receiptRel = accountBankRelService.getAcctNo(payAcctId);
        }else if (type.equals(1)) {//对公账户余额
            receiptRel = accountBankRelService.getDefaultAccountBankRel(userId,OrderAccountConst.BANK_TYPE.TYPE1,userType);
        }else {//对私账户余额
            receiptRel = accountBankRelService.getDefaultAccountBankRel(userId,OrderAccountConst.BANK_TYPE.TYPE0,userType);
        }

        CmbBankAccountInfo receiptRelInfo = bankAccountService.getByMbrNo(payAcctId);

        if(receiptRel == null && receiptRelInfo == null){
            throw new BusinessException("您未绑卡只能选择线下打款!");
        }
        if((receiptRel != null && !StringUtils.isNotEmpty(receiptRel.getAcctNo())) && (receiptRelInfo != null && !StringUtils.isNotEmpty(receiptRelInfo.getCertNo()))){
            throw new BusinessException("未绑卡只能选择线下付款!");
        }
        BankDto bankDto = new BankDto();
        if(type.equals(1)){ //对公账户余额

            String b= bankAccountService.getBalance((receiptRel != null && StringUtils.isNotEmpty(receiptRel.getPinganCollectAcctId())) ? receiptRel.getPinganPayAcctId() : receiptRelInfo.getMbrNo());
            BankBalanceInfo balanceInfo = new BankBalanceInfo(); balanceInfo.setBalance(Double.valueOf(b));
            BankBalanceInfo balanceInfo2 =new BankBalanceInfo(); balanceInfo2.setBalance(0.00);
            if(balanceInfo.getBalance() >= CommonUtil.getDoubleFormatLongMoney(balance, 2)){
                bankDto.setState("1");
                return bankDto;
            }else if ((balanceInfo.getBalance() + balanceInfo2.getBalance()) >=CommonUtil.getDoubleFormatLongMoney(balance, 2)) {
                bankDto.setState("2");
                BigDecimal b1 = new BigDecimal(Double.toString(CommonUtil.getDoubleFormatLongMoney(balance, 2)));
                BigDecimal b2 = new BigDecimal(Double.toString(balanceInfo.getBalance()));
                bankDto.setAmount(b1.subtract(b2).doubleValue());
                return bankDto;
            }else {
                bankDto.setState("3");
                return bankDto;
            }
        }else {//对私账户余额
            String b=bankAccountService.getBalance((receiptRel != null && StringUtils.isNotEmpty(receiptRel.getPinganCollectAcctId())) ? receiptRel.getPinganPayAcctId() : receiptRelInfo.getMbrNo());
            BankBalanceInfo balanceInfo = new BankBalanceInfo(); balanceInfo.setBalance(Double.valueOf(b));
            BankBalanceInfo balanceInfo2 =new BankBalanceInfo(); balanceInfo2.setBalance(0.00);
            if(balanceInfo.getBalance() >= CommonUtil.getDoubleFormatLongMoney(balance, 2)){
                bankDto.setState("1");
                return bankDto;
            }else if ((balanceInfo.getBalance() + balanceInfo2.getBalance()) >=CommonUtil.getDoubleFormatLongMoney(balance, 2)) {
                bankDto.setState("2");
                BigDecimal b1 = new BigDecimal(Double.toString(CommonUtil.getDoubleFormatLongMoney(balance, 2)));
                BigDecimal b2 = new BigDecimal(Double.toString(balanceInfo.getBalance()));
                bankDto.setAmount(b1.subtract(b2).doubleValue());
                return bankDto;
            }else {
                bankDto.setState("3");
                return bankDto;
            }
        }
    }

    @Override
    public List<AuditOutDto> isLastPayCashAuditer(String busiIdString, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        String[] busiIdArray = busiIdString.split(",");
        List<Long> busiIds = new ArrayList<Long>();
        for(String busiId :busiIdArray){
            busiIds.add(Long.valueOf(busiId));
        }
        Map<Long,Map<String,Object>> out = auditOutService.queryAuditRealTimeOperation(user.getId(), com.youming.youche.record.common.AuditConsts.AUDIT_CODE.PAY_CASH_CODE, busiIds, user.getTenantId());
        List<AuditOutDto> list = new ArrayList<>();
        Map<String,Object> retMap = new HashMap<String,Object>();
        for(String busiId :busiIdArray){

            Map m = (Map<String,Object>)out.get(Long.valueOf(busiId));
            Boolean isFinallyNode = m==null||m.get("isFinallyNode")==null?null:(boolean) m.get("isFinallyNode");
            Boolean isAuditJurisdiction = m==null||m.get("isAuditJurisdiction")==null?false:(boolean) m.get("isAuditJurisdiction");
            String auditUserName = m==null||m.get("auditUserName")==null?"": m.get("auditUserName")+"";
            retMap.put("busiId", busiId);
            retMap.put("isFinallyNode", isFinallyNode);//是否最后节点
            retMap.put("isAuditJurisdiction", isAuditJurisdiction);//是否有审核权限
            retMap.put("auditUserName", auditUserName);//待审人
            AuditOutDto auditOutDto = new AuditOutDto();
            auditOutDto.setIsFinallyNode((Boolean) isFinallyNode);
            auditOutDto.setIsAuditJurisdiction((Boolean) isAuditJurisdiction);
            auditOutDto.setAuditUserName((String) auditUserName);
            list.add(auditOutDto);
        }
        return list;
    }

    @Override
    public String updatePayManagerState(String desc, Integer chooseResult, Long payId,Integer state, String accessToken) {
        this.updatePayManagerStates(state,payId,accessToken);
        if(chooseResult == 2 && StringUtils.isEmpty(desc)){
            throw new BusinessException("审核不通过时需要填写原因");
        }
        AuditCallbackDto auditCallbackDto = auditSettingService.sure(AuditConsts.AUDIT_CODE.PAY_MANAGER , payId, desc, chooseResult,accessToken);
        //  审核成功调用
        if (null != auditCallbackDto && !auditCallbackDto.getIsNext() && auditCallbackDto.getIsAudit() && chooseResult == com.youming.youche.system.constant.AuditConsts.RESULT.SUCCESS) {
            sucess(auditCallbackDto.getBusiId(), desc, auditCallbackDto.getParamsMap(), accessToken);
            //  审核失败调用
        } else if (com.youming.youche.system.constant.AuditConsts.RESULT.FAIL == chooseResult && null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
            fail(auditCallbackDto.getBusiId(), desc, auditCallbackDto.getParamsMap(), accessToken);
        }
        return "Y";
    }

    private void updatePayManagerStates(Integer state,Long payId,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        PayManager payManager = payManagerService.getById(payId);

        //只有状态为 0待审核、1审核不通过 才能修改
        if(!(payManager.getState() == PAYMANAGER_STATE0 ||payManager.getState() == PAYMANAGER_STATE1) && state==PAYMANAGER_STATE5){
            throw new BusinessException("该状态的申请不能取消！");
        }
        payManager.setState(state);
        payManagerService.saveOrUpdate(payManager);

        if(state==PAYMANAGER_STATE5){
            sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.payManager, payManager.getId(), SysOperLogConst.OperType.Update,
                    "取消付款申请",user.getTenantId());

            //结束审核流程
            try{
                auditOutService.cancelProcess(PAY_MANAGER, payManager.getId(),Long.valueOf(payManager.getTenantId()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public Long getPayoutIntfOverdueBalance(Long userId, Integer userType, Integer payUserType) {
        Long amount = payoutIntfMapper.selectSum(userId,userType,payUserType);
        Long txnAmt = payoutIntfMapper.selectSums(userId,userType,payUserType);
        if( amount == null){
            amount = 0L;
        }
        if( txnAmt == null){
            txnAmt = 0L;
        }
            amount = amount + txnAmt;
        return amount;
    }

    @Override
    public Page<QueryPayManagerDto> doQueryAllPayManager(QueryPayManagerVo queryPayManagerVo, Integer pageSize, Integer pageNum, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        List payTypeNameList = new ArrayList();
        if(queryPayManagerVo.getPayTypeName()!=null){
            String [] payTypeNames = queryPayManagerVo.getPayTypeName().split(",");
            for(String string:payTypeNames){
                payTypeNameList.add(string);
            }
        }
        queryPayManagerVo.setPayTypeNameList(payTypeNameList);
        if (queryPayManagerVo.getStateWX()!=null){
            String []stateWXs= queryPayManagerVo.getStateWX().split(",");
            List stateWXList = new ArrayList();
            for(String string:stateWXs){
                stateWXList.add(string);
            }
            queryPayManagerVo.setStateWXList(stateWXList);
        }

        boolean hasAllDataPermission = false;
        try {
            hasAllDataPermission  = sysRoleService.hasAllData(user);
            queryPayManagerVo.setHasAllDataPermission(hasAllDataPermission);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Long> lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.PAY_MANAGER, user.getUserInfoId(), queryPayManagerVo.getTenantId(),pageSize);
        queryPayManagerVo.setLids(lids);
        Page<QueryPayManagerDto> payManagerDtoPage = payoutIntfMapper.select(queryPayManagerVo,new Page<>(pageNum,pageSize));
        return payManagerDtoPage;
    }

    @Override
    public Page<OrderLimitOutDto> getAccountDetailsNoPay(String userId, String orderId, String startTime, String endTime,
                                                   String sourceRegion, String desRegion, Integer userType, Integer PageSize,Integer PageNum,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Long tenantUserId = null;
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(user.getTenantId());
        if(sysTenantDef != null ){
            tenantUserId  = sysTenantDef.getAdminUser();
            userType = com.youming.youche.record.common.SysStaticDataEnum.USER_TYPE.ADMIN_USER;
        }else {
            tenantUserId = user.getId();
        }
        Page<OrderLimit> page = orderLimitMapper.selectOr(userId,orderId,startTime,endTime,sourceRegion,desRegion,userType,user.getTenantId(),new Page<>(PageNum,PageSize));
        Page<OrderLimitOutDto> pageDto = new Page<>();
        List<OrderLimit> orderLimits = page.getRecords();
        List<OrderLimitOutDto> outs = new ArrayList<>();
        if(orderLimits!=null && orderLimits.size()>0){
            for(OrderLimit ol : orderLimits){
                OrderLimitOutDto out = new OrderLimitOutDto();
                OrderFee of = orderFeeService.getOrderFee(ol.getOrderId());
                if(of!=null){
                    out.setTotalFee(of.getTotalFee() == null ? 0 : of.getTotalFee());
                    out.setOrderPay(of.getPreTotalFee() == null ? 0 : of.getPreTotalFee());
                    out.setOrderFinal(of.getFinalFee() == null ? 0 : of.getFinalFee());
                }else{
                    OrderFeeH ofh = orderFeeHService.getOrderFeeH(ol.getOrderId());
                    out.setTotalFee(ofh.getTotalFee() == null ? 0 : ofh.getTotalFee());
                    out.setOrderPay(ofh.getPreTotalFee() == null ? 0 : ofh.getPreTotalFee());
                    out.setOrderFinal(ofh.getFinalFee() == null ? 0 : ofh.getFinalFee());
                }
                out.setOrderId(ol.getOrderId());
                out.setNoPayDebt(ol.getNoPayDebt());
                out.setOrderCash(ol.getOrderCash());
                out.setNoPayFinal(ol.getNoPayFinal());
                out.setFianlSts(ol.getFianlSts());
                out.setFinalPlanDate(new Date());
                out.setPaidFinalPay(out.getOrderFinal()-out.getNoPayFinal());
                setOrderCommonInfo(out,ol.getTenantId(),accessToken);
                outs.add(out);
            }
        }

        pageDto.setRecords(outs);
        pageDto.setTotal(page.getTotal());
        pageDto.setSize(page.getSize());
        pageDto.setCurrent(page.getCurrent());
        pageDto.setPages(page.getPages());
        return pageDto;
    }

    private void setOrderCommonInfo(OrderLimitOutDto out, Long tenantId,String accessToken) {

        OrderInfo oi = orderInfoService.getOrder(out.getOrderId());

        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        if(oi!=null){
            OrderScheduler os = orderSchedulerService.getOrderScheduler(out.getOrderId());
            out.setCreateDate(DateUtil.formatLocalDateTime(os.getDependTime(), DateUtil.DATETIME12_FORMAT));
            out.setPlateNumber(os.getPlateNumber());
            out.setSourceRegionName(oi.getSourceRegionName());
            out.setDesRegionName(oi.getDesRegionName());
            List<Map<String,Object>> oilCardList = oilCardManagementService.getOilCardByOrderIds(out.getOrderId(),accessToken);
            out.setOilCarList(oilCardList);
        }else{
            OrderInfoH oih = orderInfoHService.getOrderH(out.getOrderId());
            if(oih!=null){
                OrderSchedulerH osh = orderSchedulerHService.getOrderSchedulerH(out.getOrderId());
                out.setCreateDate(String.valueOf(osh.getDependTime()));
                out.setPlateNumber(osh.getPlateNumber());
                out.setSourceRegionName(String.valueOf(oih.getSourceRegion()));
                out.setDesRegionName(String.valueOf(oih.getDesRegion()));
                List<Map<String,Object>> oilCardList = oilCardManagementService.getOilCardByOrderIds(out.getOrderId(),accessToken);
                out.setOilCarList(oilCardList);
            }
        }
        SysTenantDef std = sysTenantDefService.getSysTenantDef(tenantId);
        String logo=null;
        if(std!=null && org.apache.commons.lang3.StringUtils.isNotBlank(std.getLogo())){
            SysAttach sa = sysAttachService.getAttachByFlowId(Long.parseLong(std.getLogo()));
            if(sa!=null){
                logo =sa.getFullPathUrl();
            }
        }
        out.setLogo(logo);
        out.setSourceName(std.getName());
    }

    @Override
    public Long getPayUnDoReceiveAccount(Long userId, Integer userType) {
        Long sumBalance = payoutIntfMapper.selectOr(userId,userType);
        return  sumBalance;
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        PayManager payManager = payManagerService.getById(busiId);
        payManager.setAuditRemark(desc);
        payManager.setIsAudit(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        payManager.setState(SysStaticDataEnum.PAYMANAGER_STATE.PAYMANAGER_STATE2);

        payManager.setAuditUserId(loginInfo.getUserInfoId());
        payManager.setAuditUserName(loginInfo.getName());
        payManager.setAuditTime(LocalDateTime.now());
        PayoutIntf payoutIntf = createPayMangerPayoutIntf(payManager,accessToken);
        //如果付款给自己，则不操作账户  -- 20190727 付款自己也要操作账户会员体系改造放开
        //if(null != payoutIntf.getPayObjId() && null != payoutIntf.getUserId() && payoutIntf.getPayObjId().longValue() != payoutIntf.getUserId().longValue()){
        this.createAccountDetailForPayout(payoutIntf,loginInfo);
        //}
        payManager.setPayNo(payoutIntf.getId());
        payManagerService.update(payManager);
    }

    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        PayManager payManager = payManagerService.getById(busiId);
        payManager.setAuditRemark(desc);
        payManager.setIsAudit(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        payManager.setState(PAYMANAGER_STATE1);
        payManager.setAuditUserId(loginInfo.getUserInfoId());
        payManager.setAuditUserName(loginInfo.getName());
        payManager.setAuditTime(LocalDateTime.now());
        payManagerService.update(payManager);
    }

    private PayoutIntf createPayMangerPayoutIntf(PayManager payManager,String accessToken){
        PayoutIntf newPayoutIntf = new PayoutIntf();
        newPayoutIntf.setPayType(payManager.getPayUserType());
        newPayoutIntf.setPayObjId(payManager.getPayUserId());
        newPayoutIntf.setPayTenantId(payManager.getPayTenantId());
        newPayoutIntf.setTxnAmt(payManager.getPayAmt());
        newPayoutIntf.setPayAccNo(payManager.getPayAccNo());
        if(StringUtils.isBlank(newPayoutIntf.getPayAccNo())){
            newPayoutIntf.setIsAutomatic(AUTOMATIC0);
        }else{
            //是否自动打款
            boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(payManager.getPayTenantId());
            if(isAutoTransfer){
                newPayoutIntf.setIsAutomatic(AUTOMATIC1);
            }else{
                newPayoutIntf.setIsAutomatic(AUTOMATIC0);
            }
        }
        newPayoutIntf.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
        newPayoutIntf.setBusiId(EnumConsts.PayInter.PAY_MANGER);
        newPayoutIntf.setSubjectsId(SUBJECTIDS1816);
        newPayoutIntf.setIsDriver(payManager.getReceUserType());
        newPayoutIntf.setTenantId(Long.parseLong(payManager.getTenantId()));
        newPayoutIntf.setUserId(payManager.getReceUserId());
        //会员体系改造开始
        newPayoutIntf.setUserType(payManager.getUserType());
        newPayoutIntf.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        //会员体系改造结束

        SysTenantDef receTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(payManager.getReceUserId());
        newPayoutIntf.setAccNo(payManager.getReceAccNo());
        newPayoutIntf.setBusiCode(payManager.getBusiCode());
        newPayoutIntf.setIsNeedBill(OrderConsts.IS_NEED_BILL.NOT_NEED_BILL);
        newPayoutIntf.setRemark(payManager.getPayRemark());
        if(null == payManager.getIsNeedBiil() || SysStaticDataEnum.PAYMANAGER_BILL.PAYMANAGER_BILL0 == payManager.getIsNeedBiil()){
            newPayoutIntf.setVehicleAffiliation(VEHICLE_AFFILIATION0);
            newPayoutIntf.setOilAffiliation(VEHICLE_AFFILIATION0);
            newPayoutIntf.setAccountType(PRIVATE_PAYABLE_ACCOUNT);
        }else if(SysStaticDataEnum.PAYMANAGER_BILL.PAYMANAGER_BILL1 == payManager.getIsNeedBiil()){
            newPayoutIntf.setVehicleAffiliation(VEHICLE_AFFILIATION1);
            newPayoutIntf.setOilAffiliation(VEHICLE_AFFILIATION0);
            newPayoutIntf.setAccountType(BUSINESS_PAYABLE_ACCOUNT);
        }
        if(null != payManager.getReceUserType() && payManager.getReceUserType() == OrderAccountConst.PAY_TYPE.TENANT){
            if(null != newPayoutIntf.getPayObjId() && null != newPayoutIntf.getUserId() && newPayoutIntf.getPayObjId().longValue() == newPayoutIntf.getUserId().longValue()){
                newPayoutIntf.setTenantId(newPayoutIntf.getPayTenantId());
            }
        }
        SysUser sysOtherOperator = sysUserService.getSysOperatorByUserIdOrPhone(newPayoutIntf.getUserId(), null, 0L);
        if(null != sysOtherOperator  && null != sysOtherOperator.getBillId()){
            newPayoutIntf.setObjId(Long.parseLong(sysOtherOperator.getBillId()));
        }
        AccountBankRel accountBankRel = accountBankRelService.getAcctNo(payManager.getReceAccNo());
        if(null == accountBankRel){
            throw new BusinessException("收款账号不存在！");
        }
        if(accountBankRel.getBankType() == OrderAccountConst.BANK_TYPE.TYPE0){
            newPayoutIntf.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
        }else {
            newPayoutIntf.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
        }
        if(StringUtils.isBlank(accountBankRel.getAcctNo())){
            newPayoutIntf.setIsAutomatic(AUTOMATIC0);
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(payManager.getPayTenantId());
        if(null != sysTenantDef){
            newPayoutIntf.setBankRemark("付款方:"+sysTenantDef.getName());
        }
        if(receTenantDef!=null){
            newPayoutIntf.setTenantId(receTenantDef.getId());
        }
        this.doSavePayOutIntfForOA(newPayoutIntf,accessToken);
        return newPayoutIntf;
    }
}