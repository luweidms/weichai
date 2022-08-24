package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IAccountStatementService;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.api.IOilTurnEntityService;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewService;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.domain.OilEntity;
import com.youming.youche.finance.domain.OilTurnEntity;
import com.youming.youche.finance.vo.munual.QueryPayoutIntfsVo;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOilTurnEntityOperLogService;
import com.youming.youche.order.api.ITurnCashLogService;
import com.youming.youche.order.api.ITurnCashOrderService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBaseBillInfoService;
import com.youming.youche.order.api.order.IBillAgreementService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOilSourceRecordService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOverdueReceivableService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.api.order.IPinganLockInfoService;
import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.api.order.ITenantAgentServiceRelService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.api.service.IServiceInfoService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OilTurnEntityOperLog;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.TurnCashLog;
import com.youming.youche.order.domain.TurnCashOrder;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BaseBillInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OilRechargeAccount;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.PayoutOrder;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.AccountDto;
import com.youming.youche.order.dto.LoanDetail;
import com.youming.youche.order.dto.OaLoanOutDto;
import com.youming.youche.order.dto.OaloanAndUserDateInfoDto;
import com.youming.youche.order.dto.OilExcDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.OrderAccountsDto;
import com.youming.youche.order.dto.OrderDebtDetail;
import com.youming.youche.order.dto.OrderLimitOutDto;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.PeccancDetailDto;
import com.youming.youche.order.dto.ReceivableOverdueBalanceDto;
import com.youming.youche.order.dto.SumQuotaAmtDto;
import com.youming.youche.order.provider.mapper.order.AccountBankRelMapper;
import com.youming.youche.order.provider.mapper.order.OrderAccountMapper;
import com.youming.youche.order.provider.utils.BusiToOrderUtils;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.MatchAmountUtil;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.order.vo.OrderAccountVO;
import com.youming.youche.order.vo.UserAccountOutVo;
import com.youming.youche.order.vo.UserAccountVo;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.SysTenantOutDto;
import com.youming.youche.system.dto.ac.OrderAccountOutDto;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 订单账户表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-18
 */
@DubboService(version = "1.0.0")
@Service
public class OrderAccountServiceImpl extends BaseServiceImpl<OrderAccountMapper, OrderAccount> implements IOrderAccountService {
    private static final Logger log = LoggerFactory.getLogger(OrderAccountServiceImpl.class);
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;
    @Resource
    private ICreditRatingRuleService creditRatingRuleService;
    @DubboReference(version = "1.0.0")
    private ITenantUserRelService tenantUserRelService;
    @Autowired
    private ITenantAgentServiceRelService tenantAgentServiceRelService;
    @Autowired
    private IPinganLockInfoService pinganLockInfoService;
    @Autowired
    private IOilRechargeAccountService oilRechargeAccountService;
    @Autowired
    private IRechargeOilSourceService rechargeOilSourceService;
    @Autowired
    private IOrderOilSourceService orderOilSourceService;
    @Resource
    private IOverdueReceivableService overdueReceivableService;

    @Resource
    IAccountDetailsService iAccountDetailsService;

    @Resource
    LoginUtils loginUtils;
    @Resource
    IOpAccountService iOpAccountService;

    @DubboReference(version = "1.0.0")
    ISysUserService iSysOperatorService;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Resource
    IBusiSubjectsRelService busiSubjectsRelService;
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService iSysTenantDefService;
    @DubboReference(version = "1.0.0")
    private ISysUserService sysUserService;
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @Resource
    private IOrderLimitService orderLimitService;
    @Resource
    private IBillPlatformService billPlatformService;
    @Resource
    private IBillAgreementService billAgreementService;
    @Resource
    private IAccountDetailsService accountDetailsService;
    @Lazy
    @Resource
    private BusiToOrderUtils busiToOrderUtils;
    @Resource
    private IPayFeeLimitService payFeeLimitService;
    @Resource
    private IPayoutIntfService payoutIntfService;
    @Resource
    private IOrderSchedulerService orderSchedulerService;
    @Lazy
    @Resource
    IOrderInfoService iorderInfoService;
    @Resource
    private IOrderSchedulerHService orderSchedulerHService;
    @Resource
    private IPayoutOrderService payoutOrderService;
    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @Resource
    IBaseBillInfoService iBaseBillInfoService;
    @Resource
    IOrderInfoHService iOrderInfoHService;
    @Resource
    OrderAccountMapper orderAccountMapper;
    @Resource
    IOilCardManagementService iOilCardManagementService;

    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @DubboReference(version = "1.0.0")
    IOaLoanThreeService iOaLoanService1;

    @Resource
    IServiceInfoService iServiceInfoService;

    @Resource
    ITurnCashLogService iTurnCashLogService;

    @Resource
    IOilTurnEntityOperLogService iOilTurnEntityOperLogService;

    @DubboReference(version = "1.0.0")
    IOilTurnEntityService iOilTurnEntityService;

    @Resource
    ITurnCashOrderService iTurnCashOrderService;

    @Resource
    IOilSourceRecordService iOilSourceRecordService;

    @Resource
    IOrderAccountService orderAccountService;

    @DubboReference(version = "1.0.0")
    ICmSalaryInfoNewService cmSalaryInfoNewService;
    @DubboReference(version = "1.0.0")
    IAccountStatementService accountStatementService;
    @Resource
    AccountBankRelMapper accountBankRelMapper;
    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;
    @DubboReference(version = "1.0.0")
    ISysAttachService sysAttachService;
    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;
    @Resource
    RedisUtil redisUtil;
    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService payoutIntfThreeService;

    //TODO
    @Override
    public Long queryOilBalance(Long userId, Long tenantId, Integer userType) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("请输入用户id");
        }
        OrderAccountBalanceDto orderAccountBalance = this.getOrderAccountBalance(userId, OrderAccountConst.ORDER_BY.OIL_BALANCE, tenantId, userType);
        if (orderAccountBalance == null) {
            throw new BusinessException("未找到用户账户信息");
        }

        OrderAccountOutVo out = orderAccountBalance.getOa();
        long oilBalance = 0;
        if (out != null) {
            oilBalance = out.getTotalOilBalance() == null ? 0L : out.getTotalOilBalance();
        } else {
            throw new BusinessException("未找到用户油卡余额");
        }
        return oilBalance;
    }

    @Override
    public OrderAccountBalanceDto getOrderAccountBalance(Long userId, String orderByType, Long tenantId, Integer userType) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("请输入用户编号");
        }
        // 通过用户id获取用户信息
        UserDataInfo user = userDataInfoService.getById(userId);
        if (user == null) {
            throw new BusinessException("没找到用户信息");
        }
        List<OrderAccount> accountList = getOrderAccountQuey(userId, orderByType, -1L, userType);
        // 总现金（客户油）
        long totalBalance = 0;
        // 共享客户油
        long totalShareBalance = 0;
        // 自有客户油
        long totalHaveBalance = 0;
        // 总油
        long totalOilBalance = 0;
        // 总ETC
        long totalEtcBalance = 0;
        // 总未到期
        long totalMarginBalance = 0;
        //总维修基金
        long totalRepairFund = 0;
        // 总欠款金额
        long totalDebtAmount = 0;
        // 冻结未到期金额
        long frozenMarginBalance = 0;
        // 冻结现金
        long frozenBalance = 0;
        // 冻结油
        long frozenOilBalance = 0;
        // 冻结etc
        long frozenEtcBalance = 0;
        //冻结维修基金
        long frozenRepairFund = 0;
        // 可预支金额
        long canAdvance = 0;
        // 不可预支金额
        long cannotAdvance = 0;
        // 可使用金额
        long canUseBalance = 0;
        //不可使用金额
        long cannotUseBalance = 0;
        // 可使用油
        long canUseOilBalance = 0;
        // 可使用ETC
        long canUseEtcBalance = 0;
        //可使用维修基金
        long canUseRepairFund = 0;
        //押金(不分冻不冻结)
        long depositBalance = 0;
        //应收逾期
        long receivableOverdueBalance = 0;
        //应付预期
        long payableOverdueBalance = 0;
        //订单油账户
        long oilBalance = 0;
        //充值油账户
        long rechargeOilBalance = 0;
        //母卡充值账户
        long oilRechargeBalance = 0;
        //已开票账户
        long oilInvoicedBalance = 0;
        for (OrderAccount ac : accountList) {
            if (ac.getSourceTenantId() <= 0) {
                continue;
            }
            CreditRatingRule rating = creditRatingRuleService.getCreditRatingRule(userId, ac.getSourceTenantId());
//            if (rating == null) {
            log.info("未找到车队的会员权益规则信息");
//                throw new BusinessException("未找到车队的会员权益规则信息");
//                return null;
//            }
            if (ac.getOilBalance() == null) {
                ac.setOilBalance(0L);
            }
            if (ac.getEtcBalance() == null) {
                ac.setEtcBalance(0L);
            }
            if (ac.getRepairFund() == null) {
                ac.setRepairFund(0L);
            }
            if (ac.getBalance() == null) {
                ac.setBalance(0L);
            }
            if (ac.getMarginBalance() == null) {
                ac.setMarginBalance(0L);
            }
            if (ac.getRechargeOilBalance() == null) {
                ac.setRechargeOilBalance(0L);
            }
            if (ac.getReceivableOverdueBalance() == null) {
                ac.setReceivableOverdueBalance(0L);
            }
            if (ac.getPayableOverdueBalance() == null) {
                ac.setPayableOverdueBalance(0L);
            }
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE0) {
                frozenBalance += ac.getBalance();
                frozenOilBalance += ac.getOilBalance();
                frozenEtcBalance += ac.getEtcBalance();
                frozenRepairFund += ac.getRepairFund();
                if (ac.getMarginBalance() > 0) {
                    frozenMarginBalance += ac.getMarginBalance();
                }
                if (ac.getMarginBalance() > 0) {
                    cannotAdvance += ac.getMarginBalance();
                }
                if (ac.getBalance() > 0) {
                    cannotUseBalance += ac.getBalance();
                }
            } else {
                canUseOilBalance += ac.getOilBalance();
                canUseEtcBalance += ac.getEtcBalance();
                canUseRepairFund += ac.getRepairFund();
                if (ac.getBalance() > 0) {
                    if (rating.getIsWithdrawal() != null && rating.getIsWithdrawal() == OrderAccountConst.IS_ADVANCE.YES) {
                        canUseBalance += ac.getBalance();
                    } else {
                        cannotUseBalance += ac.getBalance();
                    }
                }
                if (ac.getMarginBalance() > 0) {
                    if (rating != null) {
                        if (rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES) {
                            canAdvance += ac.getMarginBalance();
                        } else {
                            cannotAdvance += ac.getMarginBalance();
                        }
                    }
                }
            }

            totalBalance += ac.getBalance();
            oilBalance += ac.getOilBalance();
            rechargeOilBalance += ac.getRechargeOilBalance();
            totalOilBalance += (ac.getOilBalance() + ac.getRechargeOilBalance());
            totalEtcBalance += ac.getEtcBalance();
            receivableOverdueBalance += ac.getReceivableOverdueBalance();

            payableOverdueBalance += ac.getPayableOverdueBalance();
            totalRepairFund += ac.getRepairFund();
            if (ac.getPledgeOilCardFee() != null) {
                depositBalance += ac.getPledgeOilCardFee();
            }

            if (ac.getMarginBalance() > 0) {
                totalMarginBalance += ac.getMarginBalance();
            } else {
                totalDebtAmount += Math.abs(ac.getMarginBalance());
            }
        }
        //授信金额
        SumQuotaAmtDto creditMap = tenantAgentServiceRelService.getSumQuotaAmtListByTenantId(tenantId, ServiceConsts.AGENT_SERVICE_TYPE.OIL);
        Long totalQuotaAmt = 0L;
        Long totalUseQuotaAmt = 0L;
        if (creditMap != null) {
//            totalQuotaAmt = creditMap.getTotalQuotaAmt() == null ? 0L : Long.valueOf(creditMap.getTotalQuotaAmt() + "");
//            totalUseQuotaAmt = creditMap.getTotalUseQuotaAmt() == null ? 0L : Long.valueOf(creditMap.getTotalQuotaAmt() + "");
            // TODO 2022-7-9 修改
            totalQuotaAmt = creditMap.getTotalQuotaAmt() == null ? 0L : creditMap.getTotalQuotaAmt();
            totalUseQuotaAmt = creditMap.getTotalUseQuotaAmt() == null ? 0L: creditMap.getTotalUseQuotaAmt();
        }

        //锁定金额

        long lockBalance = pinganLockInfoService.getAccountLockSum(userId);
        //母卡充值账户
        OilRechargeAccount oilRechargeAccount = oilRechargeAccountService.getOilRechargeAccount(userId);
        if (oilRechargeAccount != null) {
            oilRechargeBalance += oilRechargeAccount.getCashRechargeBalance() == null ? 0L : oilRechargeAccount.getCashRechargeBalance();//现金充值(非抵扣票)
            oilRechargeBalance += oilRechargeAccount.getCreditRechargeBalance() == null ? 0L : oilRechargeAccount.getCreditRechargeBalance();//授信充值
            oilRechargeBalance += oilRechargeAccount.getInvoiceOilBalance() == null ? 0L : oilRechargeAccount.getInvoiceOilBalance();//抵扣票充值

            oilInvoicedBalance += oilRechargeAccount.getRebateRechargeBalance() == null ? 0L : oilRechargeAccount.getRebateRechargeBalance();//返利充值
            oilInvoicedBalance += oilRechargeAccount.getTransferOilBalance() == null ? 0L : oilRechargeAccount.getTransferOilBalance();//转移账户充值
        }
        oilInvoicedBalance += totalOilBalance;//客戶油
        //区分客户油类型 共享/自有
        List<OrderOilSource> oilSourceList = orderOilSourceService.getOrderOilSourceByUserId(userId, userType);
        List<RechargeOilSource> rechargeList = rechargeOilSourceService.getRechargeOilSourceByUserId(userId, userType);
        for (OrderOilSource source : oilSourceList) {
            Long noPayOil = source.getNoPayOil() == null ? 0L : source.getNoPayOil();
            Long noCreditOil = source.getNoCreditOil() == null ? 0L : source.getNoCreditOil();
            Long noRebateOil = source.getNoRebateOil() == null ? 0L : source.getNoRebateOil();
            Long sumOil = noPayOil + noCreditOil + noRebateOil;
            if (source.getOilConsumer() == OrderConsts.OIL_CONSUMER.SELF) {//自有
                totalHaveBalance += sumOil;
            }
            if (source.getOilConsumer() == OrderConsts.OIL_CONSUMER.SHARE) {//共享
                totalShareBalance += sumOil;
            }
        }
        for (RechargeOilSource source : rechargeList) {
            Long noPayOil = source.getNoPayOil() == null ? 0L : source.getNoPayOil();
            Long noCreditOil = source.getNoCreditOil() == null ? 0L : source.getNoCreditOil();
            Long noRebateOil = source.getNoRebateOil() == null ? 0L : source.getNoRebateOil();
            Long sumOil = noPayOil + noCreditOil + noRebateOil;
            if (source.getOilConsumer() == OrderConsts.OIL_CONSUMER.SELF) {//自有
                totalHaveBalance += sumOil;
            }
            if (source.getOilConsumer() == OrderConsts.OIL_CONSUMER.SHARE) {//共享
                totalShareBalance += sumOil;
            }
        }
        //车队应收逾期新逻辑
       // receivableOverdueBalance = overdueReceivableService.sumOverdueReceivable(tenantId,null,null);
        OrderAccountOutVo oa = new OrderAccountOutVo();
        oa.setTotalBalance(totalBalance);
        oa.setTotalHaveBalance(totalHaveBalance);
        oa.setTotalShareBalance(totalShareBalance);
        oa.setTotalOilBalance(totalOilBalance);
        oa.setTotalEtcBalance(totalEtcBalance);
        oa.setTotalMarginBalance(totalMarginBalance);
        oa.setTotalDebtAmount(totalDebtAmount);
        oa.setCanAdvance(canAdvance);
        oa.setCannotAdvance(cannotAdvance);
        oa.setCanUseBalance(canUseBalance);
        oa.setCannotUseBalance(cannotUseBalance);
        oa.setCanUseEtcBalance(canUseEtcBalance);
        oa.setCanUseOilBalance(canUseOilBalance);
        oa.setFrozenBalance(frozenBalance);
        oa.setFrozenEtcBalance(frozenEtcBalance);
        oa.setFrozenMarginBalance(frozenMarginBalance);
        oa.setFrozenOilBalance(frozenOilBalance);
        oa.setTotalRepairFund(totalRepairFund);
        oa.setFrozenRepairFund(frozenRepairFund);
        oa.setCanUseRepairFund(canUseRepairFund);
        oa.setDepositBalance(depositBalance);
        oa.setReceivableOverdueBalance(receivableOverdueBalance);
        oa.setPayableOverdueBalance(payableOverdueBalance);
        oa.setOilBalance(oilBalance);
        oa.setRechargeOilBalance(rechargeOilBalance);
        oa.setOilRechargeBalance(oilRechargeBalance);
        oa.setOilInvoicedBalance(oilInvoicedBalance);
        oa.setCreditLine(totalQuotaAmt);//授信额度
        oa.setUsedCreditLine(totalUseQuotaAmt);//已用授信额度
        oa.setLockBalance(lockBalance);//锁定金额
        oa.setElectronicOilCard(oilInvoicedBalance + totalQuotaAmt + oilRechargeBalance);//电子油卡=已开票账户+充值账户+授信额度
        OrderAccountBalanceDto map = new OrderAccountBalanceDto();
        map.setAccountList(accountList);
        map.setOa(oa);
        return map;
    }

    @Override
    public List<OrderAccount> getOrderAccountQuey(Long userId, String orderByType, Long sourceTenantId, Integer userType) {
        LambdaQueryWrapper<OrderAccount> lambda = new QueryWrapper<OrderAccount>().lambda();
        lambda.eq(OrderAccount::getUserId, userId);
        if (OrderAccountConst.ORDER_BY.BALANCE.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getBalance);
        } else if (OrderAccountConst.ORDER_BY.OIL_BALANCE.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getOilBalance);
        } else if (OrderAccountConst.ORDER_BY.ETC_BALANCE.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getEtcBalance);
        } else if (OrderAccountConst.ORDER_BY.MARGIN_BALANCE.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getMarginBalance);
        } else if (OrderAccountConst.ORDER_BY.REPAIR_FUND.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getRepairFund);
        } else if (OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getVehicleAffiliation);
        }
        if (sourceTenantId != null && sourceTenantId > 0) {
            lambda.eq(OrderAccount::getSourceTenantId, sourceTenantId);
        }
        if (userType != null && userType > 0) {
            lambda.eq(OrderAccount::getUserType, userType);
        }
        List<OrderAccount> list = list(lambda);
        return list;
    }

    @Override
    public OrderAccount queryOrderAccount(Long userId, String vehicleAffiliation, Long tenantId, Long sourceTenantId, String oilAffiliation, Integer userType) {
        log.info("userId=" + userId + "资金渠道类型=" + vehicleAffiliation + "tenantId=" + tenantId + "sourceTenantId=" + sourceTenantId);
        if (userId <= 0L) {
            throw new BusinessException("用户id有误");
        }
        if (sourceTenantId <= 0L) {
            throw new BusinessException("请输入资金来源租户id");
        }
        if (StringUtils.isBlank(vehicleAffiliation)) {
            throw new BusinessException("请输入资金渠道");
        }
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入油资金渠道");
        }
        if (userType == null || userType <= 0) {
            throw new BusinessException("请输入用户类型");
        }
        // 通过userid获取用户信息
        UserDataInfo user = userDataInfoService.getById(userId);
        if (user == null) {
            throw new BusinessException("没有找用户信息!");
        }
        OrderAccount orderAccount = this.queryOrderAccount(userId, vehicleAffiliation, sourceTenantId, oilAffiliation, userType);
        if (orderAccount == null) {
            OrderAccount newOrderAccount = new OrderAccount();
            newOrderAccount.setUserId(userId);
            //会员体系改造开始
            newOrderAccount.setUserType(userType);
            //会员体系改造结束
            newOrderAccount.setVehicleAffiliation(vehicleAffiliation);
            newOrderAccount.setAccState(1);//有效
            newOrderAccount.setCreateDate(LocalDateTime.now());
            newOrderAccount.setAccLevel(1);
            newOrderAccount.setTenantId(user.getTenantId());
            newOrderAccount.setSourceTenantId(sourceTenantId);
            newOrderAccount.setOilAffiliation(oilAffiliation);
            this.saveOrUpdate(newOrderAccount);
            return newOrderAccount;
        } else {
            return orderAccount;
        }
    }

    @Override
    public OrderAccount queryOrderAccount(Long userId, String vehicleAffiliation, Long sourceTenantId, String oilAffiliation, Integer userType) {
        LambdaQueryWrapper<OrderAccount> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderAccount::getUserId, userId)
                .eq(OrderAccount::getUserType, userType)
                .eq(OrderAccount::getVehicleAffiliation, vehicleAffiliation)
                .eq(OrderAccount::getSourceTenantId, sourceTenantId)
                .eq(OrderAccount::getOilAffiliation, oilAffiliation);
        return this.getOne(lambda);
    }

    @Override
    public OrderAccount getOrderAccount(Long userId, String vehicleAffiliation, Long sourceTenantId, String oilAffiliation, Integer userType) {
        LambdaQueryWrapper<OrderAccount> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderAccount::getUserId, userId);
        if (userType != null && userType > 0) {
            lambda.eq(OrderAccount::getUserType, userType);
        }
        lambda.eq(OrderAccount::getVehicleAffiliation, vehicleAffiliation)
                .eq(OrderAccount::getSourceTenantId, sourceTenantId)
                .eq(OrderAccount::getOilAffiliation, oilAffiliation);
        OrderAccount account = this.getOne(lambda);
        return account;
    }

    @Override
    public List<OrderAccount> getOrderAccount(Long userId, String orderByType, Long sourceTenantId, Integer userType) {
        LambdaQueryWrapper<OrderAccount> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderAccount::getUserId, userId);
        if (OrderAccountConst.ORDER_BY.BALANCE.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getBalance);
        } else if (OrderAccountConst.ORDER_BY.OIL_BALANCE.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getOilBalance);
        } else if (OrderAccountConst.ORDER_BY.ETC_BALANCE.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getEtcBalance);
        } else if (OrderAccountConst.ORDER_BY.MARGIN_BALANCE.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getMarginBalance);
        } else if (OrderAccountConst.ORDER_BY.REPAIR_FUND.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getRepairFund);
        } else if (OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION.equals(orderByType)) {
            lambda.orderByDesc(OrderAccount::getVehicleAffiliation);
        }
        if (sourceTenantId > 0) {
            lambda.eq(OrderAccount::getSourceTenantId, sourceTenantId);
        }
        if (userType > 0) {
            lambda.eq(OrderAccount::getUserType, userType);
        }
        // ca.setLockMode(LockMode.PESSIMISTIC_WRITE);//加锁
        return this.list(lambda);
    }

    @Override
    public List<OrderOilSource> dealTemporaryFleetOil(OrderAccount fleetAccount, OrderOilSource oilSource, Long fleetUserId, long orderId, long fleetTenantId, int isNeedBill, String vehicleAffiliation, long driverUserId, Long businessId, Long subjectsId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Date date = new Date();
        // 根据用户ID和资金渠道类型获取账户信息
        List<OrderOilSource> sourceList = new ArrayList<OrderOilSource>();
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(fleetUserId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(subjectsId, oilSource.getMatchAmount());
        busiList.add(amountFeeSubjectsRel);
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(businessId, busiList);
        long soNbr = CommonUtil.createSoNbr();
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, businessId,
                0L, "", fleetAccount, busiSubjectsRelList, soNbr, orderId, "", null, fleetTenantId, null, "", null, fleetAccount.getVehicleAffiliation(), loginInfo);

        long matchNoPayOil = oilSource.getMatchNoPayOil() == null ? 0 : oilSource.getMatchNoPayOil();
        long matchNoRebateOil = oilSource.getMatchNoRebateOil() == null ? 0 : oilSource.getMatchNoRebateOil();
        long matchNoCreditOil = oilSource.getMatchNoCreditOil() == null ? 0 : oilSource.getMatchNoCreditOil();
        OrderOilSource source = orderOilSourceService.saveOrderOilSource(driverUserId, orderId, oilSource.getSourceOrderId(), matchNoPayOil, matchNoPayOil, 0L, oilSource.getSourceTenantId(), getDateToLocalDateTime(date), loginInfo.getId(), oilSource.getIsNeedBill(), oilSource.getVehicleAffiliation(), oilSource.getOrderDate(), oilSource.getOilAffiliation(), oilSource.getOilConsumer(),
                matchNoRebateOil, matchNoRebateOil, 0L, matchNoCreditOil, matchNoCreditOil, 0L, SysStaticDataEnum.USER_TYPE.DRIVER_USER, oilSource.getOilAccountType(), oilSource.getOilBillType(), loginInfo);
        source.setMatchAmount(oilSource.getMatchAmount());
        sourceList.add(source);
        amountFeeSubjectsRel.setAmountFee(oilSource.getMatchAmount());
        busiSubjectsRelList = busiSubjectsRelService.feeCalculation(businessId, busiList);
        ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(oilSource.getUserId(), sysOperator.getBillId(), businessId, orderId, oilSource.getMatchAmount(), oilSource.getVehicleAffiliation(), "");
        orderOilSourceService.busiToOrderNew(parametersNewDto, busiSubjectsRelList, loginInfo);
        return sourceList;
    }

    @Override
    public void payForException(Long userId, String vehicleAffiliation, Long amountFee, Long orderId, Long tenantId, LoginInfo loginInfo, String token) {


        log.info("异常补偿:userId=" + userId + "amountFee=" + amountFee + "orderId=" + orderId);
        if (userId < 1) {
            throw new BusinessException("请输入用户编号");
        }
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("请输入资金渠道");
        }
        if (orderId <= 0) {
            throw new BusinessException("请输入订单号");
        }
        if (tenantId == null || tenantId <= -1) {
            throw new BusinessException("请输入租户id");
        }
        // todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payForException" + orderId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[]{DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2)});
        // 通过租户id，找到租户用户id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        SysUser tenantUser = iSysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);

//        UserDataInfo tenantUser = userSV.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 获取用户信息
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //查询用户是否车队
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        // 根据用户ID和资金渠道类型获取账户信息
        OrderLimit ol = orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId, -1);
        if (ol == null) {
            throw new BusinessException("根据订单号：" + orderId + " 用户ID：" + userId + " 未找到订单限制记录");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入订单油资金渠道");
        }
        OrderAccount account = this.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId, oilAffiliation, ol.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        Long soNbr = CommonUtil.createSoNbr();
        if (amountFee > 0) {
            // 异常补偿金额
            BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB, amountFee);
            busiList.add(amountFeeSubjectsRel);
            //路歌开票 服务费 20190717  司机应收账户不记服务费
            long serviceFee = 0;
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), amountFee, 0L, 0L, amountFee, tenantId, null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            // 计算费用集合
            List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.EXCEPTION_FEE, busiList);
            // 写入账户明细表并修改账户金额费用
            accountDetailsService.insetAccDet(EnumConsts.BusiType.EXCEPTION_CODE, EnumConsts.PayInter.EXCEPTION_FEE, tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

            //车队应付逾期
            OrderAccount fleetAccount = this.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId, ol.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_IN_PAYABLE_OVERDUE_SUB, amountFee);
            fleetBusiList.add(payableOverdueRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4005, serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }

            // 计算费用集合
            List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.EXCEPTION_FEE, fleetBusiList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.EXCEPTION_CODE, EnumConsts.PayInter.EXCEPTION_FEE,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getOrgName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

            // 写入订单限制表和订单资金流向表
            Map<String, String> inParam = this.setParameters(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                    EnumConsts.PayInter.EXCEPTION_FEE, orderId, amountFee, vehicleAffiliation, "");
            inParam.put("tenantUserId", String.valueOf(tenantUserId));
            inParam.put("tenantBillId", tenantSysOperator.getBillId());
            inParam.put("tenantUserName", tenantUser.getLinkMan());
            busiSubjectsRelList.addAll(fleetSubjectsRelList);
            busiToOrderUtils.busiToOrder(inParam, busiSubjectsRelList);
            //是否自动打款
            boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
            Integer isAutomatic = null;
            if (isAutoTransfer) {
                isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
            } else {
                isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
            }
            PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER,
                    OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, amountFee, -1L, vehicleAffiliation, orderId,
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT,
                    EnumConsts.PayInter.EXCEPTION_FEE, EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB,
                    oilAffiliation, SysStaticDataEnum.USER_TYPE.ADMIN_USER, ol.getUserType(), serviceFee, token);
            payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntf.setRemark("异常补偿");
            if (isTenant) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                payoutIntf.setTenantId(sysTenantDef.getId());
            }
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
            }
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            payoutIntf.setBusiCode(String.valueOf(orderId));
            if (orderScheduler != null) {
                payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
            } else {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                if (orderSchedulerH != null) {
                    payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                }
            }
            payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf, token);
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                //写入payout_order
                payoutOrderService.createPayoutOrder(userId, amountFee, OrderAccountConst.FEE_TYPE.CASH_TYPE,
                        payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            }
        }
        // 操作日志
        String remark = "异常补偿：" + "订单号：" + orderId + " 异常费用金额：" + amountFee;
        sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.PayForException, soNbr, com.youming.youche.commons.constant.SysOperLogConst.OperType.Add, loginInfo.getName() + remark);

    }

    /**
     * @param userId             用户id
     * @param billId             用户手机号
     * @param businessId         业务id
     * @param orderId            订单id
     * @param amount             费用
     * @param vehicleAffiliation 资金渠道
     * @param finalPlanDate      尾款到账日期
     */
    @Override
    public Map<String, String> setParameters(Long userId, String billId, Long businessId, Long orderId, Long amount, String vehicleAffiliation, String finalPlanDate) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", String.valueOf(userId));
        map.put("billId", String.valueOf(billId));
        map.put("businessId", String.valueOf(businessId));
        map.put("orderId", orderId <= 0 ? null : String.valueOf(orderId));
        map.put("amount", String.valueOf(amount));
        map.put("vehicleAffiliation", String.valueOf(vehicleAffiliation));
        map.put("finalPlanDate", String.valueOf(finalPlanDate));
        return map;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }


    @Override
    public IPage<UserAccountVo> doAccountQuery(UserAccountOutVo out, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        out.setTenantId(sysTenantDef.getId());
        out.setUserId(sysTenantDef.getAdminUser());
        // TODO 司机账户查询金额 数量
        if (!StringUtils.isBlank(out.getStroilBegin())){
            out.setStroilBeginDouble(Long.valueOf(out.getStroilBegin())*100.00);
        }
        if (!StringUtils.isBlank(out.getStroilEnd())){
            out.setStroilEndDouble(Long.valueOf(out.getStroilEnd())*100.00);
        }
        if (!StringUtils.isBlank(out.getStrbalanceBegin())){
            out.setStrbalanceBeginDouble(Long.valueOf(out.getStrbalanceBegin())*100.00);
        }
        if (!StringUtils.isBlank(out.getStrbalanceEnd())){
            out.setStrbalanceEndDouble(Long.valueOf(out.getStrbalanceEnd())*100.00);
        }
        if (!StringUtils.isBlank(out.getStrmarginBegin())){
            out.setStrmarginBeginDouble(Long.valueOf(out.getStrmarginBegin())*100.00);
        }
        if (!StringUtils.isBlank(out.getStrmarginEnd())){
            out.setStrmarginEndDouble(Long.valueOf(out.getStrmarginEnd())*100.00);
        }

        if (!StringUtils.isBlank(out.getStrRepairFundBegin())){
            out.setStrRepairFundBeginDouble(Long.valueOf(out.getStrRepairFundBegin())*100.00);
        }
        if (!StringUtils.isBlank(out.getStrRepairFundEnd())){
            out.setStrRepairFundEndDouble(Long.valueOf(out.getStrRepairFundEnd())*100.00);
        }

        if (!StringUtils.isBlank(out.getStretcBegin())){
            out.setStretcBeginDouble(Long.valueOf(out.getStretcBegin())*100.00);
        }
        if (!StringUtils.isBlank(out.getStretcEnd())){
            out.setStretcEndDouble(Long.valueOf(out.getStretcEnd())*100.00);
        }

        if (out.getBalanceBegin()!=null){
            out.setBalanceBeginDouble(out.getBalanceBegin()*100.00);
        }
        if (out.getBalanceEnd()!=null){
            out.setBalanceEndDouble(out.getBalanceEnd()*100.00);
        }

        if (out.getMarginBegin()!=null){
            out.setBalanceEndDouble(out.getMarginBegin()*100.00);
        }
        if (out.getMarginEnd()!=null){
            out.setBalanceEndDouble(out.getMarginEnd()*100.00);
        }
        if (out.getOilBegin()!=null){
            out.setOilBeginDouble(out.getOilBegin()*100.00);
        }
        if (out.getOilEnd()!=null){
            out.setOilEndDouble(out.getOilEnd()*100.00);
        }
        if (out.getEtcBegin()!=null){
            out.setEtcBeginDouble(out.getEtcBegin()*100.00);
        }
        if (out.getEtcEnd()!=null){
            out.setEtcEndDouble(out.getEtcEnd()*100.00);
        }
        if (!StringUtils.isBlank(out.getReceivableOverdueBalanceBeginStr())){
            out.setReceivableOverdueBalanceBeginStrDouble(Long.valueOf(out.getReceivableOverdueBalanceBeginStr())*100.00);
        }
        if (!StringUtils.isBlank(out.getReceivableOverdueBalanceEndStr())){
            out.setReceivableOverdueBalanceEndStrDouble(Long.valueOf(out.getReceivableOverdueBalanceEndStr())*100.00);
        }
        if (!StringUtils.isBlank(out.getPayableOverdueBalanceBeginStr())){
            out.setPayableOverdueBalanceBeginStrDouble(Long.valueOf(out.getPayableOverdueBalanceBeginStr())*100.00);
        }
        if (!StringUtils.isBlank(out.getPayableOverdueBalanceEndStr())){
            out.setPayableOverdueBalanceEndStrDouble(Long.valueOf(out.getPayableOverdueBalanceEndStr())*100.00);
        }
        //new Page<>(pageNum, pageSize),
        List<UserAccountVo> result = getBaseMapper().doAccountQuery(out);
        List<UserAccountVo> userAccountVoList = new ArrayList<>();
        for (UserAccountVo uo : result) {
            uo.setOilBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getOilBalance() == null ? 0 : uo.getOilBalance(), 2));
//            ConsumeOilFlow consumeOilFlow = consumeOilFlowService.getConsumeOilFlow(Long.valueOf(uo.getBillId()));
//            if(consumeOilFlow!=null) {
//                //已到期,则未到期資金清零
//                if (consumeOilFlow.getState() == 1) {
//                    uo.setMarginBalanceD(0);
//                } else {
//                    uo.setMarginBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getMarginBalance(), 2));
//                }
//            }
            // 老平台是没有这个操作的吧
//            //查询司机应收逾期
             Long overDue = overdueReceivableService.sumOverdueReceivable(loginInfo.getTenantId(),null,uo.getUserId(),2);
            if(overDue != null && overDue > -1){
               uo.setPayableOverdueBalance(overDue);
            }else{
                uo.setPayableOverdueBalance(0L);
            }
            if(out.getPayableOverdueBalanceBeginStrDouble() != null){
                if(uo.getPayableOverdueBalance() < out.getPayableOverdueBalanceBeginStrDouble()){
                    continue;
                }
            }
            if(out.getPayableOverdueBalanceEndStrDouble() != null){
                if(uo.getPayableOverdueBalance() > out.getPayableOverdueBalanceEndStrDouble()){
                    continue;
                }
            }
            overdueReceivableService.sumOverdueReceivable(loginInfo.getTenantId(),null,uo.getUserId(),2);
            uo.setMarginBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getMarginBalance(), 2));
            uo.setEtcBalanceDD(CommonUtil.getDoubleFormatLongMoney(uo.getEtcBalance(), 2));
            uo.setRepairFundD(CommonUtil.getDoubleFormatLongMoney(uo.getRepairFund() == null ? 0 : uo.getRepairFund(), 2));
            uo.setPledgeOilcardFeeD(CommonUtil.getDoubleFormatLongMoney(uo.getPledgeOilCardFee() == null ? 0 : uo.getPledgeOilCardFee(), 2));
            uo.setReceivableOverdueBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getReceivableOverdueBalance() == null ? 0 : uo.getReceivableOverdueBalance(), 2));
            uo.setPayableOverdueBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getPayableOverdueBalance() == null ? 0 : uo.getPayableOverdueBalance(), 2));
//            Long sourceTenantId = uo.getSourceTenantId();
//            SysTenantDef def = iSysTenantDefService.getSysTenantDef(sourceTenantId);
            uo.setAccStateName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACC_STATE", String.valueOf(uo.getAccState())).getCodeName());
            uo.setUserTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("USER_TYPE", String.valueOf(uo.getUserType())).getCodeName());
            uo.setStateName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("USER_STATE", String.valueOf(uo.getState())).getCodeName());
            uo.setUserTypeALias(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("USER_TYPE", String.valueOf(uo.getUserType())).getCodeName());
            if (uo.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
//                uo.setShortName(def.getShortName());
                uo.setShortName(sysTenantDef.getName());
//                userAccountVoList.add(uo);
            } else if (uo.getUserType() == SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
                uo.setShortName("合作服务商");
//                userAccountVoList.add(uo);
            } else {
                uo.setShortName("平台合作车");
//                userAccountVoList.add(uo);
            }
            userAccountVoList.add(uo);
        }
        return listToPage(userAccountVoList,pageNum,pageSize);

    }

    @Override
    public AccountDto doAccountQuerySum(UserAccountOutVo out, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        if (StringUtils.isNotEmpty(out.getStrRepairFundBegin())) {
            Long strRepairFundBegin = CommonUtil.getLongByString(out.getStrRepairFundBegin());
            out.setStrbalanceBegin(String.valueOf(strRepairFundBegin));
        }
        if (StringUtils.isNotEmpty(out.getStrRepairFundEnd())) {
            Long strRepairFundEnd = CommonUtil.getLongByString(out.getStrRepairFundEnd());
            out.setStrRepairFundEnd(String.valueOf(strRepairFundEnd));
        }
        if (StringUtils.isNotEmpty(out.getStrmarginBegin())) {
            Long strmarginBegin = CommonUtil.getLongByString(out.getStrmarginBegin());
            out.setStrmarginBegin(String.valueOf(strmarginBegin));
        }
        if (StringUtils.isNotEmpty(out.getStrmarginEnd())) {
            Long strmarginEnd = CommonUtil.getLongByString(out.getStrmarginEnd());
            out.setStrmarginEnd(String.valueOf(strmarginEnd));
        }
        if (StringUtils.isNotEmpty(out.getStroilBegin())) {
            Long stroilBegin = CommonUtil.getLongByString(out.getStroilBegin());
            out.setStroilBegin(String.valueOf(stroilBegin));
        }
        if (StringUtils.isNotEmpty(out.getStroilEnd())) {
            Long stroilEnd = CommonUtil.getLongByString(out.getStroilEnd());
            out.setStroilEnd(String.valueOf(stroilEnd));
        }
        if (StringUtils.isNotEmpty(out.getStretcBegin())) {
            Long stretcBegin = CommonUtil.getLongByString(out.getStretcBegin());
            out.setStretcBegin(String.valueOf(stretcBegin));
        }
        if (StringUtils.isNotEmpty(out.getStretcEnd())) {
            Long stretcEnd = CommonUtil.getLongByString(out.getStretcEnd());
            out.setStretcEnd(String.valueOf(stretcEnd));
        }
        if (StringUtils.isNotEmpty(out.getReceivableOverdueBalanceBeginStr())) {
            Long receivableOverdueBalanceBeginStr = CommonUtil.getLongByString(out.getReceivableOverdueBalanceBeginStr());
            out.setReceivableOverdueBalanceBeginStr(String.valueOf(receivableOverdueBalanceBeginStr));
        }
        if (StringUtils.isNotEmpty(out.getReceivableOverdueBalanceEndStr())) {
            Long receivableOverdueBalanceEndStr = CommonUtil.getLongByString(out.getReceivableOverdueBalanceEndStr());
            out.setReceivableOverdueBalanceEndStr(String.valueOf(receivableOverdueBalanceEndStr));
        }
        if (StringUtils.isNotEmpty(out.getPayableOverdueBalanceBeginStr())) {
            Long payableOverdueBalanceBeginStr = CommonUtil.getLongByString(out.getPayableOverdueBalanceBeginStr());
            out.setPayableOverdueBalanceBeginStr(String.valueOf(payableOverdueBalanceBeginStr));
        }
        if (StringUtils.isNotEmpty(out.getPayableOverdueBalanceEndStr())) {
            Long payableOverdueBalanceEndStr = CommonUtil.getLongByString(out.getPayableOverdueBalanceEndStr());
            out.setPayableOverdueBalanceEndStr(String.valueOf(payableOverdueBalanceEndStr));
        }
        out.setTenantId(sysTenantDef.getId());
        out.setUserId(sysTenantDef.getAdminUser());
        AccountDto dto = orderAccountMapper.doAccountQuerySum(out);
        return dto;
    }

    @Override
    public OrderAccountBalanceDto getDriverOil(Long userId, String orderByType, String accessToken, Integer userType) {
        if (userId == null || userId <= 0) {
            log.info("请选择司机！");
            return new OrderAccountBalanceDto();
//            throw new BusinessException("请选择司机！");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        OrderAccountBalanceDto orderAccountBalance = this.getOrderAccountBalance(userId, null, tenantId, userType);
        return orderAccountBalance;
    }

    @Override
    public void updateByA(OrderAccountVO orderAccountVO) {
        UpdateWrapper<OrderAccount> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", orderAccountVO.getUserId());
        updateWrapper.eq("source_tenant_id", orderAccountVO.getSourceTenantId());
        updateWrapper.eq("user_type", orderAccountVO.getUserType());
        updateWrapper.set("acc_state", orderAccountVO.getState());
        updateWrapper.set("remark", orderAccountVO.getRemark());
        this.update(updateWrapper);
    }

    @Override
    @Async
    public void OilCardList1(String accessToken, ImportOrExportRecords record, UserAccountOutVo out) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        out.setTenantId(sysTenantDef.getId());
        out.setUserId(sysTenantDef.getAdminUser());
        if (!StringUtils.isBlank(out.getStroilBegin())) {
            out.setStroilBeginDouble(Long.valueOf(out.getStroilBegin()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStroilEnd())) {
            out.setStroilEndDouble(Long.valueOf(out.getStroilEnd()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStrbalanceBegin())) {
            out.setStrbalanceBeginDouble(Long.valueOf(out.getStrbalanceBegin()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStrbalanceEnd())) {
            out.setStrbalanceEndDouble(Long.valueOf(out.getStrbalanceEnd()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStrmarginBegin())) {
            out.setStrmarginBeginDouble(Long.valueOf(out.getStrmarginBegin()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStrmarginEnd())) {
            out.setStrmarginEndDouble(Long.valueOf(out.getStrmarginEnd()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStrRepairFundBegin())) {
            out.setStrRepairFundBeginDouble(Long.valueOf(out.getStrRepairFundBegin()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStrRepairFundEnd())) {
            out.setStrRepairFundEndDouble(Long.valueOf(out.getStrRepairFundEnd()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStretcBegin())) {
            out.setStretcBeginDouble(Long.valueOf(out.getStretcBegin()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getStretcEnd())) {
            out.setStretcEndDouble(Long.valueOf(out.getStretcEnd()) * 100.00);
        }
        if (out.getBalanceBegin() != null) {
            out.setBalanceBeginDouble(out.getBalanceBegin() * 100.00);
        }
        if (out.getBalanceEnd() != null) {
            out.setBalanceEndDouble(out.getBalanceEnd() * 100.00);
        }
        if (out.getMarginBegin() != null) {
            out.setBalanceEndDouble(out.getMarginBegin() * 100.00);
        }
        if (out.getMarginEnd() != null) {
            out.setBalanceEndDouble(out.getMarginEnd() * 100.00);
        }
        if (out.getOilBegin() != null) {
            out.setOilBeginDouble(out.getOilBegin() * 100.00);
        }
        if (out.getOilEnd() != null) {
            out.setOilEndDouble(out.getOilEnd() * 100.00);
        }
        if (out.getEtcBegin() != null) {
            out.setEtcBeginDouble(out.getEtcBegin() * 100.00);
        }
        if (out.getEtcEnd() != null) {
            out.setEtcEndDouble(out.getEtcEnd() * 100.00);
        }
        if (!StringUtils.isBlank(out.getReceivableOverdueBalanceBeginStr())) {
            out.setReceivableOverdueBalanceBeginStrDouble(Long.valueOf(out.getReceivableOverdueBalanceBeginStr()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getReceivableOverdueBalanceEndStr())) {
            out.setReceivableOverdueBalanceEndStrDouble(Long.valueOf(out.getReceivableOverdueBalanceEndStr()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getPayableOverdueBalanceBeginStr())) {
            out.setPayableOverdueBalanceBeginStrDouble(Long.valueOf(out.getPayableOverdueBalanceBeginStr()) * 100.00);
        }
        if (!StringUtils.isBlank(out.getPayableOverdueBalanceEndStr())) {
            out.setPayableOverdueBalanceEndStrDouble(Long.valueOf(out.getPayableOverdueBalanceEndStr()) * 100.00);
        }
        List<UserAccountVo> list = new ArrayList<>();

        List<UserAccountVo> result = getBaseMapper().doAccountQueryS(out);
        for (UserAccountVo uo : result) {
            uo.setOilBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getOilBalance() == null ? 0 : uo.getOilBalance(), 2));
            uo.setMarginBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getMarginBalance(), 2));
            uo.setEtcBalanceDD(CommonUtil.getDoubleFormatLongMoney(uo.getEtcBalance(), 2));
            uo.setRepairFundD(CommonUtil.getDoubleFormatLongMoney(uo.getRepairFund() == null ? 0 : uo.getRepairFund(), 2));
            uo.setPledgeOilcardFeeD(CommonUtil.getDoubleFormatLongMoney(uo.getPledgeOilCardFee() == null ? 0 : uo.getPledgeOilCardFee(), 2));
            uo.setReceivableOverdueBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getReceivableOverdueBalance() == null ? 0 : uo.getReceivableOverdueBalance(), 2));
            uo.setPayableOverdueBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getPayableOverdueBalance() == null ? 0 : uo.getPayableOverdueBalance(), 2));
//            Long sourceTenantId = uo.getSourceTenantId();
//            SysTenantDef def = iSysTenantDefService.getSysTenantDef(sourceTenantId);
            uo.setAccStateName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACC_STATE", String.valueOf(uo.getAccState())).getCodeName());
            uo.setUserTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("USER_TYPE", String.valueOf(uo.getUserType())).getCodeName());
            uo.setStateName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("USER_STATE", String.valueOf(uo.getState())).getCodeName());
            uo.setUserTypeALias(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("USER_TYPE", String.valueOf(uo.getUserType())).getCodeName());
            if (uo.getUserType().equals(SysStaticDataEnum.USER_TYPE.DRIVER_USER)) {
                uo.setShortName(sysTenantDef.getName());
            } else if (uo.getUserType() == SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
                uo.setShortName("合作服务商");
            } else {
                uo.setShortName("平台合作车");
            }

            Long overDue = overdueReceivableService.sumOverdueReceivable(loginInfo.getTenantId(),null,uo.getUserId(),2);
            if(overDue != null && overDue > -1){
                uo.setPayableOverdueBalance(overDue);
            }else{
                uo.setPayableOverdueBalance(0L);
            }
            if(out.getPayableOverdueBalanceBeginStrDouble() != null){
                if(uo.getPayableOverdueBalance() < out.getPayableOverdueBalanceBeginStrDouble()){
                    continue;
                }
            }
            if(out.getPayableOverdueBalanceEndStrDouble() != null){
                if(uo.getPayableOverdueBalance() > out.getPayableOverdueBalanceEndStrDouble()){
                    continue;
                }
            }

            list.add(uo);
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{
                    "手机号码", "司机姓名", "归属车队",
                    "未到期资金", "油卡余额", "ETC余额",
                    "维修基金", "应收逾期",
                    "应付逾期", "状态"};
            resourceFild = new String[]{
                    "getBillId", "getLinkman", "getShortName",
                    "getMarginBalanceD", "getOilBalanceD", "getEtcBalanceDD",
                    "getRepairFundD", "getReceivableOverdueBalanceD",
                    "getPayableOverdueBalanceD", "getAccStateName"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, UserAccountVo.class, null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "司机账户.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public String saveOilAndEtcTurnCashNew(Long userId, String turnMonth, String turnType, String turnOilType,
                                           String turnEntityOilCard, Long turnBalance, Long turnDiscountDouble,
                                           Integer userType, String accessToken) {

        LoginInfo user = loginUtils.get(accessToken);

        turnMonth = turnMonth.replace("-", "");
        OilExcDto oilExcDto = new OilExcDto();
        oilExcDto.setUserId(userId);
        oilExcDto.setTenantId(user.getTenantId());
        oilExcDto.setUserType(userType);
        oilExcDto.setTurnOilType(turnOilType);
        oilExcDto.setTurnDiscountDouble(turnDiscountDouble);
        oilExcDto.setTurnEntityOilCard(turnEntityOilCard);
        oilExcDto.setTurnMonth(turnMonth);
        oilExcDto.setTurnType(turnType);

        if (userId == null || userId <= 0L) {
            throw new BusinessException("未找到转移用户id");
        }
        if (turnMonth == null || turnMonth.isEmpty()) {
            throw new BusinessException("未找到转移用户转移月份");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            if (user.getTenantId() == null || user.getTenantId() <= 0) {
                throw new BusinessException("请输入账户资金来源租户id");
            }
            try {
                Date turnMonthDate = sdf.parse(turnMonth);
                //获取指定日期所在月第一天
                String firstDay = TimeUtil.getMonthFirstDay(turnMonthDate);
                //获取指定日期所在月最后一天
                String lastDay = TimeUtil.getMonthLastDay(turnMonthDate);
                oilExcDto.setCreateTime(firstDay + " 00:00:00");
                oilExcDto.setUpdateTime(lastDay + " 23:59:59");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //加锁，防止重复提交
        //todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "saveOilAndEtcTurnCash" + userId + turnMonth , 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        String serviceName = null;
        if (turnType == null || turnType.isEmpty()) {//1油卡转现  ,2ETC转现
            throw new BusinessException("未找到转现类型");
        } else {
            if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {
                if (turnOilType == null || turnOilType.isEmpty()) {//1油卡转现  ,2油转实体
                    throw new BusinessException("请选择油转移到现金账户还是转成实体油卡!");
                } else if (EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(turnOilType)) {
                    if (StringUtils.isEmpty(turnEntityOilCard)) {
                        throw new BusinessException("请输入实体油卡号!");
                    } else {
                        //油卡号是否存在系统当中
                        boolean exists = iOilCardManagementService.verifyOilCardNumIsExists(turnEntityOilCard, accessToken);
                        if (!exists) {
                            throw new BusinessException("输入实体油卡号不存在系统当中或者无效!");
                        }
                        List<OilCardManagement> carNumList = iOilCardManagementService.findByOilCardNum(turnEntityOilCard, user.getTenantId());
                        if (carNumList != null && carNumList.size() > 0) {
                            OilCardManagement oilCardManagement = carNumList.get(0);
                            if (oilCardManagement.getUserId() == null) {
                                throw new BusinessException("该油卡未绑定服务商，不能油转到实体油卡!");
                            }
                            if (oilCardManagement != null) {
                                ServiceInfo serviceInfo = iServiceInfoService.getServiceUserId(oilCardManagement.getUserId() == null ? 0L :
                                        oilCardManagement.getUserId());
                                if (serviceInfo != null) {
                                    serviceName = serviceInfo.getServiceName();
                                }
                            }
                        } else {
                            throw new BusinessException("根据油卡号：" + turnEntityOilCard + " 租户ID：" + user.getTenantId() + " 未找到油卡相应信息");
                        }
                    }
                }
            }
        }
        if (turnBalance == null || turnBalance <= 0L) {
            throw new BusinessException("请输入转移金额!");
        }
        if (turnDiscountDouble == null || turnDiscountDouble < 0L) {
            throw new BusinessException("请输入转移折扣!");
        }
        String sign = null;
        long amount = 0;
        List<OrderOilSource> newSourceList = new ArrayList<OrderOilSource>();
        List<OrderLimit> newLimitList = new ArrayList<OrderLimit>();
        List<OrderLimit> list = null;
        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {//油卡转现
            //查询司机订单油来源
            List<OrderOilSource> sourceList = orderOilSourceService.getOrderOilSource(oilExcDto);
            //代收油不可以转现
            for (OrderOilSource ol : sourceList) {
                if (ol.getNoPayOil().longValue() == 0L && ol.getNoCreditOil().longValue() == 0L && ol.getNoRebateOil().longValue() == 0L) {
                    continue;
                }
                boolean isCollection = false;
                OrderSchedulerH orderSchedulerH = orderSchedulerService.getOrderSchedulerH(ol.getOrderId());
                if (orderSchedulerH != null) {
                    //代收单
                    if (orderSchedulerH.getIsCollection() != null && orderSchedulerH.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                        isCollection = true;
                    }
                } else {
                    OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(ol.getOrderId());
                    if (orderScheduler != null) {
                        //代收单
                        if (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                            isCollection = true;
                        }
                    }
                }
                if (!isCollection) {
                    amount += (ol.getNoPayOil() + ol.getNoCreditOil() + ol.getNoRebateOil());//未付油
                    newSourceList.add(ol);
                }
            }
        } else if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(turnType)) {//ETC转现
            list = orderLimitService.queryOilAndEtcBalance(oilExcDto);
            if (list != null && list.size() > 0) {
                for (OrderLimit ol : list) {
                    if (ol.getNoPayEtc().longValue() <= 0) {
                        continue;
                    }
                    amount += ol.getNoPayEtc();//未付ETC
                    newLimitList.add(ol);
                }
            }
        }

        if (turnBalance > amount) {
            throw new BusinessException("尊敬的用户您好， " + " 可转现余额为：" + ((double) amount) / 100 + "元");
        }

        Long tempTurnBalance = turnBalance;
        //查询银行卡号

        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {//油卡转现
            if (newSourceList != null && newSourceList.size() > 0) {
                for (OrderOilSource source : newSourceList) {
                    if (tempTurnBalance <= 0) {
                        break;
                    }
                    OrderAccount ac = orderAccountService.queryOrderAccount(source.getUserId(), source.getVehicleAffiliation(),
                            0L, source.getSourceTenantId(), source.getOilAffiliation(), source.getUserType());
                    Long tempAmount = (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil());//未付油
                    if (ac.getOilBalance() < tempAmount) {
                        throw new BusinessException("订单金额和账户金额不匹配");
                    }
                    if (tempAmount >= tempTurnBalance) {
                        oilExcDto.setTurnBalance(tempTurnBalance);
                        tempTurnBalance = 0L;
                    } else {
                        oilExcDto.setTurnBalance(tempAmount);
                        tempTurnBalance -= tempAmount;
                    }
                    oilExcDto.setOilAffiliation(ac.getOilAffiliation());
                    oilExcDto.setVehicleAffiliation(ac.getVehicleAffiliation());
                    oilExcDto.setSourcrList(source);
                    this.oilAndEtcTurnCashNew(oilExcDto, ac, user.getTenantId(), accessToken);
                    //油转实体油卡
                    if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType) && EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(turnOilType)) {
                        this.saveOilEntity(oilExcDto, serviceName, accessToken);
                    }
                }
            }
        } else if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(turnType)) {//ETC转现
            if (newLimitList != null && newLimitList.size() > 0) {
                for (OrderLimit source : newLimitList) {
                    if (tempTurnBalance <= 0) {
                        break;
                    }
                    OrderAccount ac = orderAccountService.queryOrderAccount(source.getUserId(), source.getVehicleAffiliation(),
                            0L, source.getTenantId(), source.getOilAffiliation(), source.getUserType());
                    long tempAmount = (source.getNoPayEtc());//未付油
                    if (ac.getEtcBalance() < tempAmount) {
                        throw new BusinessException("订单金额和账户金额不匹配");
                    }
                    if (tempAmount >= tempTurnBalance) {
                        oilExcDto.setTurnBalance(tempTurnBalance);
                        tempTurnBalance = 0L;
                    } else {
                        oilExcDto.setTurnBalance(tempAmount);
                        tempTurnBalance -= tempAmount;
                    }
                    oilExcDto.setOilAffiliation(ac.getOilAffiliation());
                    oilExcDto.setVehicleAffiliation(ac.getVehicleAffiliation());
                    oilExcDto.setOrderLimit(source);
                    this.oilAndEtcTurnCashNew(oilExcDto, ac, user.getTenantId(), accessToken);
                }
            }
        }
        if (tempTurnBalance > 0) {
            throw new BusinessException("转现失败，订单金额和账户金额不匹配");
        }
        // 操作日志
        String remark = "转现：" + " 转现金额：" + new BigDecimal((float) turnBalance / 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString() +
                " 转现折扣：" + new BigDecimal((float) turnDiscountDouble / 10000).setScale(4, BigDecimal.ROUND_HALF_UP).toString()
                + " 转现月份：" + turnMonth;
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.AccountQuery, userId,
                SysOperLogConst.OperType.Add, user.getName() + remark, user.getTenantId());
        sign = "操作成功";
        return sign;
    }

    private void saveOilEntity(OilExcDto oilExcDto, String serviceName, String accessToken) {
        TurnCashLog turnCashLog = oilExcDto.getTurnCashLog();
        LoginInfo user = loginUtils.get(accessToken);
        List<TurnCashOrder> turnCashOrderList = oilExcDto.getTurnCashOrder();
        if (turnCashLog == null || turnCashOrderList == null || turnCashOrderList.size() <= 0) {
            throw new BusinessException("油转实体油失败!");
        }
        if (turnCashLog.getDiscountAmount() > 0) {
            //现在G7和金润暂时不自动充值

            //其他油卡
            for (TurnCashOrder turnCashOrder : turnCashOrderList) {
                if (turnCashOrder.getDiscountAmount() <= 0) {
                    continue;
                }
                OilTurnEntityOperLog oilTurnEntityOperLog = new OilTurnEntityOperLog();
                oilTurnEntityOperLog.setBatchId(turnCashOrder.getBatchId());
                oilTurnEntityOperLog.setCreateDate(TimeUtil.getDataTime());
                oilTurnEntityOperLog.setOpId(user.getId());
                oilTurnEntityOperLog.setOpName(user.getName());
                oilTurnEntityOperLog.setOrderId(turnCashOrder.getOrderId());
                oilTurnEntityOperLog.setAmount(turnCashOrder.getDiscountAmount());
                oilTurnEntityOperLog.setTenantId(user.getTenantId());
                oilTurnEntityOperLog.setRemark("待充值");
                iOilTurnEntityOperLogService.saveOrUpdate(oilTurnEntityOperLog);
            }
            //油转实体油核销记录
            for (TurnCashOrder tco : turnCashOrderList) {
                if (tco.getDiscountAmount() <= 0) {
                    continue;
                }
                OilEntity oilEntity = new OilEntity();
                oilEntity.setOrderId(tco.getOrderId());

                OrderSchedulerH orderSchedulerH = null;
                OrderScheduler orderScheduler = null;
                OrderInfo orderInfo = null;
                OrderInfoH orderInfoH = null;
                orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(tco.getOrderId());
                orderInfoH = iOrderInfoHService.getOrderH(tco.getOrderId());
                OilTurnEntity oilTurnEntity = new OilTurnEntity();
                if (orderSchedulerH == null) {
                    orderScheduler = orderSchedulerService.getOrderScheduler(tco.getOrderId());
                    orderInfo = iorderInfoService.getOrder(tco.getOrderId());
                    oilTurnEntity.setSourceRegion(orderInfo.getSourceRegion());
                    oilTurnEntity.setDesRegion(orderInfo.getDesRegion());
                    oilTurnEntity.setVehicleLengh(orderScheduler.getCarLengh());
                    oilTurnEntity.setVehicleStatus(orderScheduler.getCarStatus());
                    oilTurnEntity.setCarUserType(orderScheduler.getVehicleClass());
                    oilTurnEntity.setVehicleCode(orderScheduler.getVehicleCode());
                    oilTurnEntity.setPlateNumber(orderScheduler.getPlateNumber());
                    oilTurnEntity.setDependTime(orderScheduler.getDependTime());
                    oilTurnEntity.setRootOrgId(Integer.parseInt(String.valueOf(orderInfo.getOrgId())));

                } else {
                    oilTurnEntity.setSourceRegion(orderInfoH.getSourceRegion());
                    oilTurnEntity.setDesRegion(orderInfoH.getDesRegion());
                    oilTurnEntity.setVehicleLengh(orderSchedulerH.getCarLengh());
                    oilTurnEntity.setVehicleStatus(orderSchedulerH.getCarStatus());
                    oilTurnEntity.setCarUserType(orderSchedulerH.getVehicleClass());
                    oilTurnEntity.setVehicleCode(orderSchedulerH.getVehicleCode());
                    oilTurnEntity.setPlateNumber(orderSchedulerH.getPlateNumber());
                    oilTurnEntity.setDependTime(orderSchedulerH.getDependTime());
                    oilTurnEntity.setRootOrgId(Integer.parseInt(String.valueOf(orderInfoH.getOrgId())));
                }
                oilTurnEntity.setCompanyName(serviceName);
                oilTurnEntity.setVehicleAffiliation(tco.getVehicleAffiliation());
                oilTurnEntity.setBatchId(tco.getBatchId());
                oilTurnEntity.setOrderId(tco.getOrderId());
                oilTurnEntity.setCarDriverId(tco.getUserId());
                oilTurnEntity.setOilCardNum(tco.getOilCardNumber());
                oilTurnEntity.setAssignTotal(tco.getDiscountAmount());
                oilTurnEntity.setNoVerificationAmount(tco.getDiscountAmount());
                oilTurnEntity.setState(1);//待核销
                oilTurnEntity.setTenantId(tco.getTenantId());
                oilTurnEntity.setCreateDate(LocalDateTime.now());
                iOilTurnEntityService.saveOrUpdate(oilTurnEntity);
            }
        }
    }

    private void oilAndEtcTurnCashNew(OilExcDto oilExcDto, OrderAccount orderAccount, Long tenantId, String accessToken) {

        LoginInfo user = loginUtils.get(accessToken);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[]{DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2)});
        Long tenantUserId = sysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //通过用户id获取用户信息
        UserDataInfo users = iUserDataInfoService.getUserDataInfo(user.getUserInfoId());
        if (users == null) {
            throw new BusinessException("没有找到用户基础信息!");
        }
        SysUser sysOperator = iSysUserService.getSysOperatorByUserId(user.getUserInfoId());
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //当前操作人
        if (user == null) {
            throw new BusinessException("请登陆后再使用");
        }
        if (orderAccount == null) {
            throw new BusinessException("没有找到资金账户信息!");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("请输入租户id!");
        }
        //可用金额
        Long balance = orderAccount.getBalance();
        //未到期金额
        Long marginBalance = orderAccount.getMarginBalance();
        //油卡金额
        Long oilBalance = orderAccount.getOilBalance();
        //etc金额
        Long etcBalance = orderAccount.getEtcBalance();
        //订单油
        Long orderOil = 0L;
        //订单ETC
        Long orderEtc = 0L;
        //订单未付油
        Long noPayOil = 0L;
        //订单未付ETC
        Long noPayEtc = 0L;
        //订单已付油
        Long paidOil = 0L;
        //订单已付ETC
        Long paidEtc = 0L;
        //查找司机当前月未被抵扣的借支油总和
        Long oaLoanOilAmout = 0L;
        List<OaLoan> oaLoanList = null;
        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(oilExcDto.getTurnType())) {
            OilExcDto oilExcDto1 = new OilExcDto();
            oilExcDto1.setCreateTime(oilExcDto.getCreateTime());
            oilExcDto1.setUpdateTime(oilExcDto.getUpdateTime());
            oilExcDto1.setUserId(user.getId());
            oilExcDto1.setTenantId(user.getTenantId());
            oaLoanList = iOaLoanService1.queryCarDriverOaLoan(oilExcDto1);
            if (oaLoanList != null && oaLoanList.size() > 0) {
                for (OaLoan oa : oaLoanList) {
                    oaLoanOilAmout += ((oa.getAmount() == null ? 0L : oa.getAmount()) - (oa.getDeductibleAmount() == null ? 0L : oa.getDeductibleAmount()));
                }
            }
        }
        //提现创建时间延迟20分钟
        Calendar cd = Calendar.getInstance();
        cd.add(Calendar.MINUTE, 20);
        //查询司机订单油来源
        OrderOilSource orderOilSource = null;
        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(oilExcDto.getTurnType())) {
            orderOilSource = oilExcDto.getSourcrList();
            if (orderOilSource == null) {
                throw new BusinessException("订单油入参不能为空!");
            }
            noPayOil += (orderOilSource.getNoPayOil() + orderOilSource.getNoCreditOil() + orderOilSource.getNoRebateOil());//未付油
            orderOil += (orderOilSource.getSourceAmount() + orderOilSource.getCreditOil() + orderOilSource.getRebateOil());//订单油
            paidOil += (orderOilSource.getPaidOil() + orderOilSource.getPaidCreditOil() + orderOilSource.getPaidRebateOil());//已付油
        }
        //查找司机未付油和etc
        OrderLimit orderLimit = null;
        if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(oilExcDto.getTurnType())) {
            orderLimit = oilExcDto.getOrderLimit();
            if (orderLimit == null) {
                throw new BusinessException("订单ETC入参不能为空!");
            }
            orderEtc += orderLimit.getOrderEtc();//订单ETC
            noPayEtc += orderLimit.getNoPayEtc();//未付ETC
            paidEtc += orderLimit.getPaidEtc();//已付ETC
        }

        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, oilExcDto.getVehicleAffiliation(),
                0L, tenantId, oilExcDto.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        Long soNbr = CommonUtil.createSoNbr();
        //操作日志
        TurnCashLog turnCashLog = iTurnCashLogService.createTurnCashLog(user.getId(), soNbr, balance, marginBalance, oilBalance, etcBalance, orderOil,
                orderEtc, oilExcDto.getTurnBalance(), oilExcDto.getTurnDiscountDouble(),
                oilExcDto.getTurnType(), oilExcDto.getTurnMonth(), oilExcDto.getVehicleAffiliation(), tenantId, oilExcDto.getUserType(), accessToken);

        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(oilExcDto.getTurnType())) {//油转现
            turnCashLog.setConsumeOrderBalance(paidOil);
            turnCashLog.setCanTurnBalance(noPayOil);
            turnCashLog.setOpRemark("油转现金");
            turnCashLog.setOaLoanOil(oaLoanOilAmout);
        } else {//ETC转现
            turnCashLog.setConsumeOrderBalance(paidEtc);
            turnCashLog.setCanTurnBalance(noPayEtc);
            turnCashLog.setOpRemark("ETC转现金");
        }
        if (EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(oilExcDto.getTurnOilType())) {//油转实体油卡
            turnCashLog.setOilCardNumber(oilExcDto.getTurnEntityOilCard());
            turnCashLog.setOpRemark("油转实体油卡");
        }
        if (oaLoanOilAmout > 0L) {//有借支油(折扣前抵扣借支油)
            if (oilExcDto.getTurnBalance() > oaLoanOilAmout) {
                turnCashLog.setDeductibleLoanOil(oaLoanOilAmout);
                turnCashLog.setIncome(
                        (oilExcDto.getTurnBalance() - oaLoanOilAmout) -
                                Math.round((oilExcDto.getTurnBalance() - oaLoanOilAmout) * oilExcDto.getTurnDiscountDouble() / 10000));
                turnCashLog.setDeductibleMargin(0L);
                turnCashLog.setDiscountAmount((long) Math.round((oilExcDto.getTurnBalance() - oaLoanOilAmout) * oilExcDto.getTurnDiscountDouble() / 10000));
                for (OaLoan oa : oaLoanList) {
                    oa.setDeductibleState(1);
                    oa.setDeductibleAmount(oa.getAmount());
                    oa.setDeductibleTime(LocalDateTime.now());
                    iOaLoanService1.saveOrUpdate(oa);
                }
            } else {
                turnCashLog.setDiscountAmount(0L);
                turnCashLog.setIncome(0L);
                turnCashLog.setDeductibleLoanOil(turnCashLog.getTurnBalance());
                Long tempDeductibleLoanOil = turnCashLog.getTurnBalance();
                for (OaLoan oa : oaLoanList) {
                    Long noDeductibleLoanOil = ((oa.getAmount() == null ? 0L : oa.getAmount()) -
                            (oa.getDeductibleAmount() == null ? 0L : oa.getDeductibleAmount()));
                    if (tempDeductibleLoanOil > noDeductibleLoanOil) {
                        oa.setDeductibleState(1);
                        oa.setDeductibleAmount(oa.getAmount());
                        oa.setDeductibleTime(LocalDateTime.now());
                        iOaLoanService1.saveOrUpdate(oa);
                        tempDeductibleLoanOil -= noDeductibleLoanOil;
                    } else {
                        oa.setDeductibleState(0);
                        oa.setDeductibleAmount((oa.getDeductibleAmount() == null ? 0L : oa.getDeductibleAmount()) + tempDeductibleLoanOil);
                        oa.setDeductibleTime(LocalDateTime.now());
                        iOaLoanService1.saveOrUpdate(oa);
                        break;
                    }
                }
            }
        } else {
            turnCashLog.setDeductibleLoanOil(0L);
            turnCashLog.setIncome(oilExcDto.getTurnBalance() - Math.round(oilExcDto.getTurnBalance() * oilExcDto.getTurnDiscountDouble() / 10000));
            turnCashLog.setDeductibleMargin(0L);
            turnCashLog.setDiscountAmount((long) Math.round(oilExcDto.getTurnBalance() * oilExcDto.getTurnDiscountDouble() / 10000));
        }
        iTurnCashLogService.saveOrUpdate(turnCashLog);
        List<TurnCashOrder> turnCashOrderList = new ArrayList<TurnCashOrder>();
        oilExcDto.setTurnCashLog(turnCashLog);
        oilExcDto.setTurnCashOrder(turnCashOrderList);

        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(oilExcDto.getTurnType())) {//油转现
            this.oilTurnCashNew(oilExcDto, turnCashLog, orderAccount, fleetAccount, noPayOil, tenantId, soNbr, orderOilSource, turnCashOrderList, accessToken);
        } else if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(oilExcDto.getTurnType())) {// ETC转现
            this.etcTurnCashNew(oilExcDto, turnCashLog, orderAccount, fleetAccount, noPayEtc, tenantId, soNbr, orderLimit, turnCashOrderList, accessToken);
        }
    }

    private OilExcDto etcTurnCashNew(OilExcDto oilExcDto, TurnCashLog turnCashLog, OrderAccount orderAccount,
                                     OrderAccount fleetAccount, Long noPayEtc, Long tenantId, long soNbr, OrderLimit ol,
                                     List<TurnCashOrder> turnCashOrderList, String accessToken) {

        LoginInfo user = loginUtils.get(accessToken);
        if (oilExcDto.getTurnBalance() > noPayEtc) {
            throw new BusinessException("要转现的金额大于订单的ETC金额!");
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //通过用户id获取用户信息
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(oilExcDto.getUserId(), null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //当前操作人
        if (user == null) {
            throw new BusinessException("请登陆后再使用");
        }
        //查询转现用户是否车队
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(user.getId());
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        //查找租户是否自动打款
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(tenantId);
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel etcSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_ETC, turnCashLog.getTurnBalance());
        busiList.add(etcSubjectsRel);
        long serviceFee = 0;
        if (turnCashLog.getDiscountAmount() != null && turnCashLog.getDiscountAmount() > 0L) {
            BusiSubjectsRel cashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN,
                    turnCashLog.getDiscountAmount());
            busiList.add(cashSubjectsRel);
            //路歌开票 服务费 20190717 司机应收账户不记服务费
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(oilExcDto.getVehicleAffiliation()),
                    String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(oilExcDto.getVehicleAffiliation()),
                        turnCashLog.getDiscountAmount(), 0L, 0L, turnCashLog.getDiscountAmount(), tenantId, null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }

            BusiSubjectsRel fleetCashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_PAYABLE_IN,
                    turnCashLog.getDiscountAmount());
            fleetBusiList.add(fleetCashSubjectsRel);
            //路歌开票 服务费 20190717
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4004,
                        serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }
            //车队
            // 计算费用集合
            List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, fleetBusiList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, 0L,
                    tenantSysOperator.getName(), null, tenantId,
                    null, "", null, oilExcDto.getVehicleAffiliation(), user);
        }
        BusiSubjectsRel serviceSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_SERVICE,
                turnCashLog.getIncome());
        busiList.add(serviceSubjectsRel);
        //计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, busiList);

        //写入账户明细表并修改账户金额费用
        accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), orderAccount, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getName(), LocalDateTime.now(), user.getTenantId(), "", "", null,
                oilExcDto.getVehicleAffiliation(), user);


        //写入订单限制表和资金流向表
        long tempTurnBalance = turnCashLog.getTurnBalance();
        long tempDeductibleMargin = (turnCashLog.getDeductibleMargin() == null ? 0L : turnCashLog.getDeductibleMargin());
        //转现订单表
        TurnCashOrder turnCashOrder = iTurnCashOrderService.createTurnCashOrder(ol.getUserId(), ol.getOrderId(),
                soNbr, orderAccount.getBalance(), 0L, orderAccount.getEtcBalance(), ol.getOrderEtc(), ol.getPaidEtc(), ol.getNoPayEtc(),
                oilExcDto.getTurnDiscountDouble(), oilExcDto.getVehicleAffiliation(), oilExcDto.getTurnMonth(), turnCashLog.getOilCardNumber(),
                oilExcDto.getTurnType(), tenantId, oilExcDto.getUserType());
        turnCashOrderList.add(turnCashOrder);

        turnCashOrder.setTurnBalance(tempTurnBalance);
        turnCashOrder.setDeductibleMargin(tempDeductibleMargin);
        turnCashOrder.setDiscountAmount((long) Math.round(tempTurnBalance * oilExcDto.getTurnDiscountDouble() / 10000) - tempDeductibleMargin);
        turnCashOrder.setIncome(tempTurnBalance - Math.round(tempTurnBalance * oilExcDto.getTurnDiscountDouble() / 10000));
        iTurnCashOrderService.saveOrUpdate(turnCashOrder);

        List<OrderLimit> sourceList = new ArrayList<OrderLimit>();
        sourceList.add(ol);
        try {
            MatchAmountUtil.matchAmount(oilExcDto.getTurnBalance(), 0, 0, "noPayEtc", sourceList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ol.getMatchAmount() == null || oilExcDto.getTurnBalance().longValue() != ol.getMatchAmount().longValue()) {
            throw new BusinessException("订单油与转现金额不匹配");
        }

        List<BusiSubjectsRel> orderBusiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetOrderBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel orderEtcSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_ETC,
                turnCashOrder.getTurnBalance());
        orderEtcSubjectsRel.setIncome(turnCashOrder.getIncome());
        orderBusiList.add(orderEtcSubjectsRel);
        if (turnCashOrder.getDiscountAmount() != null && turnCashOrder.getDiscountAmount() > 0L) {
            //是否自动打款
            PayoutIntf payoutIntfNew = payoutIntfService.createPayoutIntf(oilExcDto.getUserId(), OrderAccountConst.PAY_TYPE.USER,
                    OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, turnCashOrder.getDiscountAmount(), -1L,
                    oilExcDto.getVehicleAffiliation(), ol.getOrderId(),
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT,
                    EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN,
                    oilExcDto.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, ol.getUserType(), serviceFee, accessToken);
            payoutIntfNew.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntfNew.setRemark("etc转现");
            if (isTenant) {
                payoutIntfNew.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                payoutIntfNew.setTenantId(sysTenantDef.getId());
            }
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(ol.getOrderId());
            payoutIntfNew.setBusiCode(String.valueOf(ol.getOrderId()));
            if (orderScheduler != null) {
                payoutIntfNew.setPlateNumber(orderScheduler.getPlateNumber());
            } else {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(ol.getOrderId());
                payoutIntfNew.setPlateNumber(orderSchedulerH.getPlateNumber());
            }
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(oilExcDto.getVehicleAffiliation()) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(oilExcDto.getVehicleAffiliation())) {
                payoutIntfNew.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
                List<BaseBillInfo> baseBillInfoList = iBaseBillInfoService.getBaseBillInfo(ol.getOrderId());
                if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                    BaseBillInfo bbi = baseBillInfoList.get(0);
                    if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                        payoutIntfNew.setIsDriver(OrderAccountConst.PAY_TYPE.USER);
                    }
                }
            }
            payoutIntfService.doSavePayOutIntfVirToVir(payoutIntfNew, accessToken);

            BusiSubjectsRel orderCashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN,
                    turnCashOrder.getDiscountAmount());
            orderBusiList.add(orderCashSubjectsRel);
            //车队
            BusiSubjectsRel fleetOrderCashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_PAYABLE_IN,
                    turnCashOrder.getDiscountAmount());
            fleetOrderBusiList.add(fleetOrderCashSubjectsRel);
            Long txnAmount = turnCashOrder.getDiscountAmount();
            Long orderId = ol.getOrderId();
            PayoutOrder payoutOrder = new PayoutOrder();
            payoutOrder.setUserId(oilExcDto.getUserId());
            payoutOrder.setAmount(txnAmount);
            payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
            payoutOrder.setBatchId(payoutIntfNew.getId());
            payoutOrder.setOrderId(orderId);
            payoutOrder.setTenantId(ol.getTenantId());
            payoutOrder.setVehicleAffiliation(ol.getVehicleAffiliation());
            //payoutOrder.setOilAffiliation(ol.getOilAffiliation());
            payoutOrder.setCreateTime(LocalDateTime.now());
            payoutOrderService.save(payoutOrder);
			/*if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
				if (payoutIntfNew != null) {
					String sysParame56K = SysStaticDataUtil.getSysStaticDataCodeName(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K+"");
					String sysPre = BillPlatformCacheUtil.getPrefixByUserId(Long.valueOf(payoutOrder.getVehicleAffiliation()));
					//如果资金渠道类型是56K则不同步支付中心
					if(!sysPre.equals(sysParame56K)) {
						//同步支付中心
						this.synOilAndEtcToPaycenter(payoutOrder, turnType);
					}
				}
			}*/
        }
        //计算费用集合
        List<BusiSubjectsRel> orderBusiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, orderBusiList);
        ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, ol.getOrderId(), oilExcDto.getTurnBalance(), ol.getVehicleAffiliation(), "");
        parametersNewDto.setFlowId(turnCashLog.getId());
        parametersNewDto.setBatchId(String.valueOf(soNbr));//资金流向批次
        parametersNewDto.setTenantId(tenantId);//资金流向批次
        parametersNewDto.setOrderLimitBase(ol);
        parametersNewDto.setTurnType(EnumConsts.TURN_CASH.TURN_TYPE2);
        parametersNewDto.setTenantUserId(tenantUserId);
        parametersNewDto.setTenantBillId(tenantSysOperator.getBillId());
        parametersNewDto.setTenantUserName(tenantUser.getLinkman());

        orderOilSourceService.busiToOrderNew(parametersNewDto, orderBusiSubjectsRelList, user);
        return oilExcDto;
    }

    private OilExcDto oilTurnCashNew(OilExcDto oilExcDto, TurnCashLog turnCashLog, OrderAccount orderAccount, OrderAccount fleetAccount,
                                     Long noPayOil, Long tenantId, long soNbr, OrderOilSource ol,
                                     List<TurnCashOrder> turnCashOrderList, String accessToken) {


        LoginInfo user = loginUtils.get(accessToken);
        if (oilExcDto.getTurnBalance() > noPayOil) {
            throw new BusinessException("要转现的金额大于订单的虚拟油金额!");
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //通过用户id获取用户信息
        SysUser sysOperator = iSysUserService.getSysOperatorByUserId(oilExcDto.getUserId());
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //当前操作人
        if (user == null) {
            throw new BusinessException("请登陆后再使用");
        }
        //查询转现用户是否车队
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(oilExcDto.getUserId());
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        //查找租户是否自动打款
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(tenantId);
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        if (EnumConsts.TURN_CASH.TURN_OIL_TYPE1.equals(oilExcDto.getTurnOilType())) {//油转到现金
            BusiSubjectsRel oilSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_CASH_OIL,
                    turnCashLog.getTurnBalance());
            busiList.add(oilSubjectsRel);
            if (turnCashLog.getDeductibleLoanOil() != null && turnCashLog.getDeductibleLoanOil() > 0L) {
                BusiSubjectsRel oaLoanOilSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.TURN_CASH_DEDUCTIBLE_LOAN_OIL,
                        turnCashLog.getDeductibleLoanOil());
                busiList.add(oaLoanOilSubjectsRel);
            }
            BusiSubjectsRel serviceSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_CASH_SERVICE,
                    turnCashLog.getIncome());
            busiList.add(serviceSubjectsRel);
        } else if (EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(oilExcDto.getTurnOilType())) {//油转成实体油卡
            BusiSubjectsRel oilSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL,
                    turnCashLog.getTurnBalance());
            busiList.add(oilSubjectsRel);
            if (turnCashLog.getDeductibleLoanOil() != null && turnCashLog.getDeductibleLoanOil() > 0L) {
                BusiSubjectsRel oaLoanOilSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.TURN_ENTITY_DEDUCTIBLE_LOAN_OIL,
                        turnCashLog.getDeductibleLoanOil());
                busiList.add(oaLoanOilSubjectsRel);
            }
            if (turnCashLog.getDiscountAmount() != null && turnCashLog.getDiscountAmount() > 0L) {
                BusiSubjectsRel cashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_ENTITY_CARD,
                        turnCashLog.getDiscountAmount());
                busiList.add(cashSubjectsRel);

                BusiSubjectsRel orderEntityOilOutSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_ENTITY_CARD_OUT,
                        turnCashLog.getDiscountAmount());
                busiList.add(orderEntityOilOutSubjectsRel);
            }
            BusiSubjectsRel serviceSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_ENTITY_SERVICE,
                    turnCashLog.getIncome());
            busiList.add(serviceSubjectsRel);
        }
        //计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, busiList);

        //写入账户明细表并修改账户金额费用
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderOilSource(ol);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), orderAccount, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getName(), LocalDateTime.now(), user.getTenantId(), "", "", orderResponseDto,
                oilExcDto.getVehicleAffiliation(), user);
        //写入订单限制表和资金流向表
        long tempTurnBalance = turnCashLog.getTurnBalance();
        long tempDeductibleLoanOil = (turnCashLog.getDeductibleLoanOil() == null ? 0L : turnCashLog.getDeductibleLoanOil());
        //转现订单表
        TurnCashOrder turnCashOrder = iTurnCashOrderService.createTurnCashOrder(oilExcDto.getUserId(), ol.getOrderId(), soNbr,
                orderAccount.getBalance(), orderAccount.getOilBalance(), orderAccount.getEtcBalance(),
                ol.getSourceAmount() + ol.getRebateOil() + ol.getCreditOil(),
                ol.getPaidOil() + ol.getPaidCreditOil() + ol.getPaidRebateOil(),
                ol.getNoPayOil() + ol.getNoCreditOil() + ol.getNoRebateOil(),
                oilExcDto.getTurnDiscountDouble(), oilExcDto.getVehicleAffiliation(), oilExcDto.getTurnMonth(),
                turnCashLog.getOilCardNumber(), oilExcDto.getTurnType(), tenantId, oilExcDto.getUserType());
        turnCashOrderList.add(turnCashOrder);
        List<OrderOilSource> sourceList = new ArrayList<OrderOilSource>();
        sourceList.add(ol);
        MatchAmountUtil.matchAmounts(oilExcDto.getTurnBalance(), 0, 0, "noPayOil",
                "noRebateOil", "noCreditOil", sourceList);
        if (ol.getMatchAmount() == null || oilExcDto.getTurnBalance().longValue() != ol.getMatchAmount().longValue()) {
            throw new BusinessException("订单油与转现金额不匹配");
        }
        //继承油判断
        boolean isInheritOil = false;
        if (ol.getOrderId().longValue() != ol.getSourceOrderId().longValue()) {
            isInheritOil = true;
            List<BusiSubjectsRel> fleetOilList = new ArrayList<BusiSubjectsRel>();

            OrderAccount tenantAccount = orderAccountService.queryOrderAccount(tenantUserId, ol.getVehicleAffiliation(),
                    ol.getSourceTenantId(), ol.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            BusiSubjectsRel fleetOilSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_OIL_TRUN_CASH,
                    oilExcDto.getTurnBalance());
            fleetOilList.add(fleetOilSubjectsRel);
            // 计算费用集合
            List<BusiSubjectsRel> tempSubList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, fleetOilList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                    tenantUserId, "", tenantAccount, tempSubList, soNbr, ol.getSourceOrderId(),
                    "", null, tenantId, null, "",
                    null, tenantAccount.getVehicleAffiliation(), user);
        }
        if (EnumConsts.TURN_CASH.TURN_OIL_TYPE1.equals(oilExcDto.getTurnOilType())) {
            turnCashOrder.setTurnBalance(tempTurnBalance);
            turnCashOrder.setDiscountAmount((long) Math.round((tempTurnBalance - tempDeductibleLoanOil) * oilExcDto.getTurnDiscountDouble() / 10000));
            turnCashOrder.setIncome((tempTurnBalance - tempDeductibleLoanOil) - Math.round((tempTurnBalance - tempDeductibleLoanOil) *
                    oilExcDto.getTurnDiscountDouble() / 10000));
            turnCashOrder.setDeductibleLoanOil(tempDeductibleLoanOil);
            turnCashOrder.setDeductibleMargin(0L);
            iTurnCashOrderService.saveOrUpdate(turnCashOrder);

            List<BusiSubjectsRel> orderBusiList = new ArrayList<BusiSubjectsRel>();
            List<BusiSubjectsRel> fleetOrderBusiList = new ArrayList<BusiSubjectsRel>();
            if (turnCashOrder.getDiscountAmount() != null && turnCashOrder.getDiscountAmount() > 0L) {

                //司机
                BusiSubjectsRel orderCashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN,
                        turnCashOrder.getDiscountAmount());
                orderBusiList.add(orderCashSubjectsRel);
                //路歌开票 服务费 20190717 司机应收账户不记服务费
                long serviceFee = 0;
                boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(oilExcDto.getVehicleAffiliation()),
                        String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
                if (isLuge) {
                    Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(oilExcDto.getVehicleAffiliation()),
                            turnCashOrder.getDiscountAmount(), 0L, 0L, turnCashOrder.getDiscountAmount(), tenantId, null);
                    serviceFee = (Long) result.get("lugeBillServiceFee");
                }
                //车队
                BusiSubjectsRel fleetOrderCashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_CASH_PAYABLE_IN,
                        turnCashOrder.getDiscountAmount());
                fleetOrderBusiList.add(fleetOrderCashSubjectsRel);
                //路歌开票 服务费 20190717
                if (serviceFee > 0) {
                    BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4003, serviceFee);
                    fleetOrderBusiList.add(payableServiceFeeSubjectsRel);
                }
                //计算费用集合
                List<BusiSubjectsRel> limitDriverRel = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, orderBusiList);
                List<BusiSubjectsRel> limitFleetRel = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, fleetOrderBusiList);
                busiSubjectsRelList.addAll(limitDriverRel);
                busiSubjectsRelList.addAll(limitFleetRel);
                if (ol.getOrderId().longValue() != ol.getSourceOrderId().longValue()) {
                    //查询订单限制数据
                    OrderLimit limit = orderLimitService.getOrderLimitByUserIdAndOrderId(oilExcDto.getUserId(), ol.getOrderId(), -1);
                    if (limit == null) {
                        throw new BusinessException("根据用户id：" + oilExcDto.getUserId() + "订单号：" + ol.getOrderId() + "未找到限制表信息");
                    }
                    OrderAccount limitDriverAccount = orderAccountService.queryOrderAccount(oilExcDto.getUserId(), ol.getVehicleAffiliation(),
                            0L, ol.getTenantId(), ol.getOilAffiliation(), ol.getUserType());
                    OrderAccount limitFleetAccount = orderAccountService.queryOrderAccount(tenantUserId, ol.getVehicleAffiliation(),
                            0L, ol.getTenantId(), ol.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);

                    oilExcDto.setVehicleAffiliation(ol.getVehicleAffiliation());
                    oilExcDto.setOilAffiliation(ol.getOilAffiliation());

                    accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                            tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), limitDriverAccount,
                            limitDriverRel, soNbr, 0L, sysOperator.getName(), LocalDateTime.now(),
                            user.getTenantId(), "", "", null, oilExcDto.getVehicleAffiliation(), user);

                    accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                            sysOperator.getUserInfoId(), sysOperator.getName(), limitFleetAccount,
                            limitFleetRel, soNbr, 0L,
                            tenantSysOperator.getName(), null, tenantId, null, "",
                            null, oilExcDto.getVehicleAffiliation(), user);
                } else {
                    accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                            tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), orderAccount, limitDriverRel, soNbr,
                            0L, sysOperator.getName(), LocalDateTime.now(), user.getTenantId(), "", "",
                            null, oilExcDto.getVehicleAffiliation(), user);

                    accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                            sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, limitFleetRel, soNbr, 0L,
                            tenantSysOperator.getName(), null, tenantId, null, "",
                            null, oilExcDto.getVehicleAffiliation(), user);
                }
                //是否自动打款
                PayoutIntf payoutIntfNew = payoutIntfService.createPayoutIntf(oilExcDto.getUserId(), OrderAccountConst.PAY_TYPE.USER,
                        OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, turnCashOrder.getDiscountAmount(), -1L,
                        oilExcDto.getVehicleAffiliation(), ol.getOrderId(),
                        tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT,
                        EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN,
                        oilExcDto.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, ol.getUserType(), serviceFee, accessToken);
                payoutIntfNew.setObjId(Long.valueOf(sysOperator.getBillId()));
                payoutIntfNew.setRemark("油转现");
                if (isTenant) {
                    payoutIntfNew.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                    payoutIntfNew.setTenantId(sysTenantDef.getId());
                }
                payoutIntfNew.setBusiCode(String.valueOf(ol.getOrderId()));
                OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(ol.getOrderId());
                if (orderScheduler != null) {
                    payoutIntfNew.setPlateNumber(orderScheduler.getPlateNumber());
                } else {
                    OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(ol.getOrderId());
                    payoutIntfNew.setPlateNumber(orderSchedulerH.getPlateNumber());
                }
                if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(oilExcDto.getVehicleAffiliation()) &&
                        !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(oilExcDto.getVehicleAffiliation())) {
                    payoutIntfNew.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
                    List<BaseBillInfo> baseBillInfoList = iBaseBillInfoService.getBaseBillInfo(ol.getOrderId());
                    if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                        BaseBillInfo bbi = baseBillInfoList.get(0);
                        if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                            payoutIntfNew.setIsDriver(OrderAccountConst.PAY_TYPE.USER);
                        }
                    }
                }
                payoutIntfService.doSavePayOutIntfVirToVir(payoutIntfNew, accessToken);

                Long txnAmount = turnCashOrder.getDiscountAmount();
                Long orderId = ol.getSourceOrderId();
                PayoutOrder payoutOrder = new PayoutOrder();
                payoutOrder.setUserId(oilExcDto.getUserId());
                payoutOrder.setAmount(txnAmount);
                payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                payoutOrder.setBatchId(payoutIntfNew.getId());
                payoutOrder.setOrderId(orderId);
                payoutOrder.setTenantId(tenantId);
                payoutOrder.setVehicleAffiliation(oilExcDto.getVehicleAffiliation());
                //payoutOrder.setOilAffiliation(oilExcDto.getOilAffiliation());
                payoutOrder.setCreateTime(LocalDateTime.now());
                payoutOrderService.save(payoutOrder);
            }
            //回退共享油
            if (ol.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && !isInheritOil) {
                //原路返回还是回退到转移账户
                Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                if (ol != null && ol.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 &&
                        ol.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                    Long oilFee = ol.getMatchAmount() == null ? 0L : ol.getMatchAmount();
                    List<BaseBillInfo> baseBillInfoList = iBaseBillInfoService.getBaseBillInfo(ol.getOrderId());
                    if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                        BaseBillInfo bbi = baseBillInfoList.get(0);
                        if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                            recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                        } else {
                            bbi.setOil(bbi.getOil() - oilFee);
                            bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                            bbi.setUpdateTime(LocalDateTime.now());
                            iBaseBillInfoService.saveOrUpdate(bbi);
                            recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                        }
                    }
                }
                Long tempNoPayOil = ol.getMatchNoPayOil() == null ? 0 : ol.getMatchNoPayOil();
                Long tempNoRebateOil = ol.getMatchNoRebateOil() == null ? 0 : ol.getMatchNoRebateOil();
                Long tempNoCreditOil = ol.getMatchNoCreditOil() == null ? 0 : ol.getMatchNoCreditOil();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("noPayOil", tempNoPayOil);
                map.put("noRebateOil", tempNoRebateOil);
                map.put("noCreditOil", tempNoCreditOil);
                iOilSourceRecordService.recallOil(oilExcDto.getUserId(), String.valueOf(ol.getOrderId()), tenantUserId,
                        EnumConsts.SubjectIds.OIL_TURN_CASH_OIL, tenantId, map, recallType, user);
            }
            List<BusiSubjectsRel> fleetOrderBusiSubjectsRelList = null;
            if (fleetOrderBusiList != null && fleetOrderBusiList.size() > 0) {
                fleetOrderBusiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, fleetOrderBusiList);
            }
            ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                    EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, ol.getOrderId(), oilExcDto.getTurnBalance(),
                    ol.getVehicleAffiliation(), "");
            parametersNewDto.setFlowId(turnCashLog.getId());
            parametersNewDto.setBatchId(String.valueOf(soNbr));//资金流向批次
            parametersNewDto.setTenantId(tenantId);//租户id
            parametersNewDto.setOilSource(ol);
            parametersNewDto.setTurnType(EnumConsts.TURN_CASH.TURN_TYPE1);
            parametersNewDto.setTenantUserId(tenantUserId);
            parametersNewDto.setTenantBillId(tenantSysOperator.getBillId());
            parametersNewDto.setTenantUserName(tenantUser.getLinkman());
            if (fleetOrderBusiSubjectsRelList != null && fleetOrderBusiSubjectsRelList.size() > 0) {
                busiSubjectsRelList.addAll(fleetOrderBusiSubjectsRelList);
            }
            orderOilSourceService.busiToOrderNew(parametersNewDto, busiSubjectsRelList, user);
        } else if (EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(oilExcDto.getTurnOilType())) {
            turnCashOrder.setTurnBalance(tempTurnBalance);
            turnCashOrder.setDiscountAmount((long) Math.round((tempTurnBalance - tempDeductibleLoanOil) * oilExcDto.getTurnDiscountDouble() / 10000));
            turnCashOrder.setIncome((tempTurnBalance - tempDeductibleLoanOil) - Math.round((tempTurnBalance - tempDeductibleLoanOil) *
                    oilExcDto.getTurnDiscountDouble() / 10000));
            turnCashOrder.setDeductibleLoanOil(tempDeductibleLoanOil);
            turnCashOrder.setDeductibleMargin(0L);
            iTurnCashOrderService.saveOrUpdate(turnCashOrder);

            List<BusiSubjectsRel> orderBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel orderOilSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL,
                    turnCashOrder.getTurnBalance());
            orderBusiList.add(orderOilSubjectsRel);

            //回退共享油
            if (ol.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && !isInheritOil) {
                //原路返回还是回退到转移账户
                Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                if (ol != null && ol.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 &&
                        ol.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                    Long oilFee = ol.getMatchAmount() == null ? 0L : ol.getMatchAmount();
                    List<BaseBillInfo> baseBillInfoList = iBaseBillInfoService.getBaseBillInfo(ol.getOrderId());
                    if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                        BaseBillInfo bbi = baseBillInfoList.get(0);
                        if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                            recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                        } else {
                            bbi.setOil(bbi.getOil() - oilFee);
                            bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                            bbi.setUpdateTime(LocalDateTime.now());
                            iBaseBillInfoService.saveOrUpdate(bbi);
                            recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                        }
                    }
                }
                long tempNoPayOil = ol.getMatchNoPayOil() == null ? 0 : ol.getMatchNoPayOil();
                long tempNoRebateOil = ol.getMatchNoRebateOil() == null ? 0 : ol.getMatchNoRebateOil();
                long tempNoCreditOil = ol.getMatchNoCreditOil() == null ? 0 : ol.getMatchNoCreditOil();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("noPayOil", tempNoPayOil);
                map.put("noRebateOil", tempNoRebateOil);
                map.put("noCreditOil", tempNoCreditOil);
                iOilSourceRecordService.recallOil(oilExcDto.getUserId(), String.valueOf(ol.getOrderId()), tenantUserId,
                        EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL, tenantId, map, recallType, user);
            }
            //计算费用集合
            ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                    EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, ol.getOrderId(), oilExcDto.getTurnBalance(), ol.getVehicleAffiliation(), "");
            parametersNewDto.setFlowId(turnCashLog.getId());
            parametersNewDto.setBatchId(String.valueOf(soNbr));//资金流向批次
            parametersNewDto.setTenantId(tenantId);//租户id
            parametersNewDto.setOilSource(ol);
            parametersNewDto.setTurnType(EnumConsts.TURN_CASH.TURN_TYPE1);
            parametersNewDto.setTenantUserId(tenantUserId);
            parametersNewDto.setTenantBillId(tenantSysOperator.getBillId());
            parametersNewDto.setTenantUserName(tenantUser.getLinkman());
            orderOilSourceService.busiToOrderNew(parametersNewDto, busiSubjectsRelList, user);
        }
        return oilExcDto;
    }

    @Override
    public OrderAccount getOrderAccount(Long userId) {
        LambdaQueryWrapper<OrderAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderAccount::getUserId, userId);
        queryWrapper.orderByDesc(OrderAccount::getReceivableOverdueBalance);
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public void upOrderAccountReceivableOverdueBalance(Long id, Long account) {
        LambdaUpdateWrapper<OrderAccount> updateWrapper = new LambdaUpdateWrapper<OrderAccount>();
        updateWrapper.set(OrderAccount::getReceivableOverdueBalance, account);
        updateWrapper.eq(OrderAccount::getId, id);
        this.update(updateWrapper);
    }

    @Override
    public IPage<ReceivableOverdueBalanceDto> getAccountDetailsR(String name, String accState, Integer userType, Integer pageSize, Integer pageNum, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        // 通过租户id，找到租户用户id
        Long tenantUserId = user.getId();
       // String linkMan = user.getName();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(user.getTenantId());
        if (sysTenantDef != null) {
            tenantUserId = sysTenantDef.getAdminUser();
            //linkMan = sysTenantDef.getLinkMan();
            userType = com.youming.youche.record.common.SysStaticDataEnum.USER_TYPE.ADMIN_USER;
        }
        List<ReceivableOverdueBalanceDto> list = overdueReceivableService.accountDetails(user.getTenantId(),accessToken);

        List<ReceivableOverdueBalanceDto> payableList = overdueReceivableService.accountDetailPayable(user.getTenantId(), accessToken);


        // 因为车队的名称不能重复  所以通过车队名称来判断即可
        List<String> names = list.stream().map(ReceivableOverdueBalanceDto::getName).collect(Collectors.toList());
        for (ReceivableOverdueBalanceDto receivableOverdueBalanceDto : payableList) {
            if (!names.contains(receivableOverdueBalanceDto.getName())) {
                list.add(receivableOverdueBalanceDto);
            }
        }

        return listToPage(list,pageNum,pageSize);
    }

    private void countOverdueBalance(String tenantUserId, String userId, ReceivableOverdueBalanceDto receivableOverdueBalanceDto, Integer userType) {
        // 通过用户id，找到租户id
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(Long.parseLong(userId));
        if (sysTenantDef != null) {
            List<ReceivableOverdueBalanceDto> list = orderAccountMapper.selectOr(sysTenantDef.getId(), tenantUserId, userType);
            if (list != null && list.size() > 0) {
                Long receivableOverdueBalance = Long.parseLong(receivableOverdueBalanceDto.getReceivableOverdueBalance() + "");
                Long payableOverdueBalance = Long.parseLong(receivableOverdueBalanceDto.getPayableOverdueBalance() + "");
                for (ReceivableOverdueBalanceDto dto : list) {
                    receivableOverdueBalance += dto.getPayableOverdueBalance();
                    payableOverdueBalance += dto.getReceivableOverdueBalance();
                }
                receivableOverdueBalanceDto.setReceivableOverdueBalance(payableOverdueBalance);
                receivableOverdueBalanceDto.setPayableOverdueBalance(receivableOverdueBalance);
            }
        }
    }

    @Override
    public OrderAccountOutVo getAccountSum(String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Long userId = user.getUserInfoId();
        UserDataInfo userDataInfo = iUserDataInfoService.selectUserType(user.getUserInfoId());
        Integer userType = userDataInfo.getUserType();

        Double incomeBalance = 0.0;
        Double payBalance = 0.0;
        Double incomeBalance1 = 0.0;
        Double incomeBalance2 = 0.0;
        Double payBalance1 = 0.0;
        Double payBalance2 = 0.0;
        Double totalBalance = incomeBalance + payBalance;
        OrderAccountBalanceDto map = this.getOrderAccountBalance(userId, null, -1L, userType);
        OrderAccountOutVo oa = map.getOa();
        Integer payrollNumber = cmSalaryInfoNewService.getCmSalaryInfoCount(userId);
        Integer statementNumber = accountStatementService.getAccountStatementCount(userId);
        oa.setStatementNumber(statementNumber);;//待确认对账单数量
        oa.setPayrollNumber(payrollNumber);//待确认工资单数量
        oa.setTotalBalance(Math.round(totalBalance * 100));//可提现(分)
        oa.setIncomeBalance(Math.round(incomeBalance * 100));//收款账户余额(分)
        oa.setPayBalance(Math.round(payBalance * 100));//付款账户余额(分)
        oa.setBusinessReceivableAccount(Math.round(incomeBalance1 * 100));//对公收款
        oa.setPrivateReceivableAccount(Math.round(incomeBalance2 * 100));//对私收款
        oa.setBusinessPayableAccount(Math.round(payBalance1 * 100));//对公付款
        oa.setPrivatePayableAccount(Math.round(payBalance2 * 100));//对私付款
        oa.setBusinessAccount(Math.round((payBalance1 + incomeBalance1) * 100));//公户
        oa.setPrivateAccount(Math.round((payBalance2 + incomeBalance2) * 100));//私户

        oa.setTotalMarginBalance(oa.getTotalMarginBalance() == null ? 0 : oa.getTotalMarginBalance());
        log.info("======999999=======userType================" + userType);
        if (this.isUserBindCard(userId, userType, 0) || this.isUserBindCard(userId, userType, 1)) {
            oa.setIsUserBindCard(true);
        } else {
            oa.setIsUserBindCard(false);
        }

        log.info("======999999=======IsUserBindCard==========" + oa.getIsUserBindCard());
        Long incomeUnDoBalance = payoutIntfService.getPayUnDoReceiveAccount(userId, userType);//锁定，用于收款方未绑卡待收款方绑卡后支付
        oa.setTotalBalance(oa.getTotalBalance() + incomeUnDoBalance);
        oa.setIncomeBalance(oa.getIncomeBalance() + incomeUnDoBalance);

        // 原来的获取应付逾期的数据已经不适用于新系统的逻辑了
        Long payableOverdueBalance = 0L;
        try {
          //  payableOverdueBalance = payoutIntfThreeService.getOverdueCDSum(null, null, null, null, user.getUserInfoId(), accessToken);
            payableOverdueBalance =  overdueReceivableService.sumOverdueReceivable(user.getTenantId(),null,userId,2);
        } catch (Exception e) {
            log.info("user：{}", user);
            log.info("token中没有tenantId会造成查询应付逾期报错");
        }
        oa.setPayableOverdueBalance(payableOverdueBalance);

        Long receivableOverdueBalance = 0L;
        try {
            // 车队未打款数据
            //receivableOverdueBalance = payoutIntfThreeService.getDueDateDetailsSum(user.getUserInfoId(), null, null, null, null, null, String.valueOf(ADMIN_USER), accessToken);
            QueryPayoutIntfsVo queryPayoutIntfsVo = new QueryPayoutIntfsVo();
            queryPayoutIntfsVo.setSourceUserId(userId);
            Map m = payoutIntfThreeService.queryPayoutIntfsSum(accessToken,queryPayoutIntfsVo);
            if(m.get("noVerificatMoney") != null) {
                String noVerificatMoney = String.valueOf(m.get("noVerificatMoney"));
                receivableOverdueBalance = Long.parseLong(noVerificatMoney);
            }
        } catch (Exception e) {
            log.info("应收逾期数据:" + e.getMessage());
        }
        oa.setReceivableOverdueBalance(receivableOverdueBalance);
        return oa;
    }

    private boolean isUserBindCard(Long userId, Integer userType, Integer bankType) {
        List<AccountBankRel> accountBankRel = accountBankRelMapper.selectBankCard(userId, userType, bankType);
        if (accountBankRel != null && accountBankRel.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public OrderAccountsDto getAccountDetails(Long userId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        // 查询司机所有资金账户
        List<OrderAccount> accountList = orderAccountService.getOrderAccountQuey(userId, null, -1L, user.getUserType());
        List<OrderAccountOutDto> orderAccountOuts = new ArrayList<>();
        HashSet hs = new HashSet<>();
        for (OrderAccount ac : accountList) {
            hs.add(ac.getSourceTenantId());
        }
        Iterator it = hs.iterator();
        while (it.hasNext()) {
            Long curId = (Long) it.next();
            // 总现金
            Long totalBalance = 0l;
            // 总油
            Long totalOilBalance = 0l;
            // 总ETC
            Long totalEtcBalance = 0l;
            // 总未到期
            Long totalMarginBalance = 0l;
            //总维修基金
            Long totalRepairFund = 0l;
            // 总欠款金额
            Long totalDebtAmount = 0l;
            //押金(不分冻不冻结)
            Long depositBalance = 0l;

            //账户状态
            Integer accState = null;
            //账户状态名称
            String accStateName = null;

            //即将到期状态名称
            String marginStateName = "";

            //应收逾期
            Long receivableOverdueBalance = 0l;
            //预期
            Long payableOverdueBalance = 0l;

            for (OrderAccount ac : accountList) {
                if (ac.getSourceTenantId() == curId) {
                    totalBalance += ac.getBalance();
                    totalOilBalance += (ac.getOilBalance() + ac.getRechargeOilBalance());
                    totalEtcBalance += ac.getEtcBalance();
                    receivableOverdueBalance += ac.getReceivableOverdueBalance();
                    payableOverdueBalance += ac.getPayableOverdueBalance();
                    totalRepairFund += ac.getRepairFund();
                    if (ac.getPledgeOilCardFee() != null) {
                        depositBalance += ac.getPledgeOilCardFee();
                    }

                    if (ac.getMarginBalance() > 0) {
                        totalMarginBalance += ac.getMarginBalance();
                    } else {
                        totalDebtAmount += Math.abs(ac.getMarginBalance());
                    }

                    accState = ac.getAccState();
                    if (ac.getAccState() != null) {
                        ac.setAccStateName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACC_STATE", String.valueOf(accState)).getCodeName());
                    }
                    accStateName = ac.getAccStateName();
                }
            }
            OrderAccountOutDto oa = new OrderAccountOutDto();
            oa.setTotalBalance(totalBalance);
            oa.setTotalOilBalance(totalOilBalance);
            oa.setTotalEtcBalance(totalEtcBalance);
            oa.setReceivableOverdueBalance(receivableOverdueBalance);
            oa.setPayableOverdueBalance(payableOverdueBalance);
            oa.setTotalMarginBalance(totalMarginBalance);

            oa.setTotalDebtAmount(totalDebtAmount);
            oa.setTotalRepairFund(totalRepairFund);
            oa.setDepositBalance(depositBalance);

            oa.setAccState(accState);
            oa.setAccStateName(accStateName);

            oa.setSourceTenantId(curId);
            SysTenantOutDto st = sysTenantDefService.getSysTenantDefById(curId);
            if (st != null) {
                oa.setName(st.getCompanyName());
            }

            CreditRatingRule rating = creditRatingRuleService.getCreditRatingRule(userId, curId);
            if (rating == null) {
                throw new BusinessException("未找到司机的会员权益规则信息");
            }
            if (totalMarginBalance > 0) {
                if (rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES) {
                    marginStateName = "";
                } else {
                    marginStateName = "不可预支";
                }
            }
            oa.setMarginStateName(marginStateName);
            oa.setMobilePhone(st.getLinkPhone());
            orderAccountOuts.add(oa);
        }
        OrderAccountsDto orderAccountsDto = new OrderAccountsDto();
        orderAccountsDto.setItems(orderAccountOuts);
        orderAccountsDto.setUserId(userId);
        return orderAccountsDto;
    }

    private List<String> getLoanBelongDriverSubjectList() {
        List loanSubjects = new ArrayList();
        List<SysStaticData> dataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));//司机借支
        for (SysStaticData data : dataList) {
            loanSubjects.add(data.getCodeValue());
        }
        return loanSubjects;
    }

    @Override
    public Page<OaLoanOutDto> getOaLoanList(Long userId, String queryMonth, String loanSubjectList, String stateList,
                                            Long orderId, Integer pageSize, Integer pageNum, String accessToken) {
        List<String> loanSubjectLists = null;
        if (loanSubjectList != null) {
            loanSubjectLists = Arrays.stream(loanSubjectList.split(",")).collect(Collectors.toList());
        }
        List<String> stateLists = null;
        if (stateList != null) {
            stateLists = Arrays.stream(stateList.split(",")).collect(Collectors.toList());
        }
        List<String> subjectList = this.getLoanBelongDriverSubjectList();
        Page<OaloanAndUserDateInfoDto> oaloanAndUserDateInfoDtoPage = orderAccountMapper.selectOrs(userId, queryMonth, subjectList, loanSubjectList, loanSubjectLists, stateList, stateLists, orderId, new Page<>(pageNum, pageSize));
        List<OaloanAndUserDateInfoDto> list = oaloanAndUserDateInfoDtoPage.getRecords();
        List<OaLoanOutDto> list1 = new ArrayList<OaLoanOutDto>();
        Page<OaLoanOutDto> page = new Page<>();
        page.setTotal(oaloanAndUserDateInfoDtoPage.getTotal());
        page.setSize(oaloanAndUserDateInfoDtoPage.getSize());
        for (OaloanAndUserDateInfoDto dto : list) {
            com.youming.youche.order.domain.order.OaLoan oaloan = dto.getOaloan();
            com.youming.youche.capital.domain.UserDataInfo userDataInfo = dto.getUserDataInfo();
            OaLoanOutDto oLoanOut = new OaLoanOutDto();
            oLoanOut.setId(oaloan.getId());
            oLoanOut.setOaLoanId(oaloan.getOaLoanId());
            oLoanOut.setLoanSubject(oaloan.getLoanSubject());
            //  oLoanOut.setUserId(oaloan.getUserId());
            oLoanOut.setVerifyOpId(oaloan.getVerifyOpId());
            oLoanOut.setVerifyOrgId(oaloan.getVerifyOrgId());
            oLoanOut.setUserName(oaloan.getUserName());
            oLoanOut.setAppDate(oaloan.getAppDate());
            oLoanOut.setOrderId(oaloan.getOrderId());
            oLoanOut.setPlateNumber(oaloan.getPlateNumber());
            oLoanOut.setFlowId(oaloan.getFlowId());
            oLoanOut.setMobilePhone(userDataInfo.getMobilePhone());
            oLoanOut.setClassify(oaloan.getClassify());
            oLoanOut.setAccName(oaloan.getAccName());
            oLoanOut.setAccNo(oaloan.getAccNo());
            oLoanOut.setAmount(oaloan.getAmount());
            oLoanOut.setIsNeedBill(oaloan.getIsNeedBill());
            oLoanOut.setUserInfoName(userDataInfo.getLinkman());
//            try {
//                oLoanOut.setIsNeedBillName(sysStaticDataService.getSysStaticDataCodeName("IS_NEED_BILL_OA", oaloan.getIsNeedBill() + "").getCodeName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            oLoanOut.setIsNeedBillName(oLoanOut.getIsNeedBill()==null?"":
                    getSysStaticData("IS_NEED_BILL_OA",String.valueOf(oLoanOut.getIsNeedBill())).getCodeName());
            oLoanOut.setLoanSubjectName(oLoanOut.getLoanSubject()==null?"":
                    getSysStaticData("LOAN_SUBJECT",String.valueOf(oLoanOut.getLoanSubject())).getCodeName());
            oLoanOut.setNopayedAmount(oaloan.getAmount() - (oaloan.getPayedAmount() == null ? 0L : oaloan.getPayedAmount()));// 待核销金额
//            try {
//                oLoanOut.setLoanSubjectName(sysStaticDataService.getSysStaticDataCodeName("LOAN_SUBJECT", oaloan.getLoanSubject() + "").getCodeName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            oLoanOut.setSts(oaloan.getSts());
//            try {
//                oLoanOut.setStsName(sysStaticDataService.getSysStaticDataCodeName("LOAN_STATE", oaloan.getSts().toString()).getCodeName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            // 状态
            oLoanOut.setStateName(oLoanOut.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                    String.valueOf(oLoanOut.getState())).getCodeName());

            // 状态
            oLoanOut.setStsName(oLoanOut.getSts() == null ? "" : getSysStaticData("LOAN_STATE", String.valueOf(
                    oLoanOut.getSts())).getCodeName());

            // 资金归属
            if (oaloan.getFundAllocationOrgId() != null) {
                oLoanOut.setFundAllocation(iSysOrganizeService.getCurrentTenantOrgNameById(oaloan.getTenantId(), oaloan.getFundAllocationOrgId()));
            } else {
                oLoanOut.setFundAllocation(oaloan.getUserName());
            }
            oLoanOut.setAmountDouble(CommonUtil.getDoubleFormatLongMoney(oaloan.getAmount(), 2));
            if (oaloan.getPayedAmount() != null) {
                oLoanOut.setPayedAmountDouble(CommonUtil.getDoubleFormatLongMoney(oaloan.getPayedAmount(), 2));
            } else {
                oLoanOut.setPayedAmountDouble(0.0);
            }
            if (oaloan.getOrgId() != null) {
                String orgName = iSysOrganizeService.getCurrentTenantOrgNameById(oaloan.getTenantId(), oaloan.getOrgId());
                oLoanOut.setOrgName(orgName);
            }

            oLoanOut.setCurrentUserId(userId);
            oLoanOut.setStateName(oLoanOut.getStsName());
            oLoanOut.setHaveOrgEntity(false);
            list1.add(oLoanOut);
        }
        page.setRecords(list1);
        return page;
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
    public Page<OrderLimitOutDto> getAccountDetailsPledge(Long userId, String tenantId, Integer userType, Integer pageSize, Integer pageNum, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        UserDataInfo userDataInfo = iUserDataInfoService.selectUserType(user.getUserInfoId());
        if (userDataInfo.getUserType() < 0 || userDataInfo.getUserType() == null) {
            userType = user.getUserType();
        }
        Page<OrderLimit> orderLimitPage = orderAccountMapper.selectOrsTwo(userId, userType, tenantId, new Page<>(pageNum, pageSize));
        List<OrderLimit> orderLimits = orderLimitPage.getRecords();
        List<OrderLimitOutDto> outs = new ArrayList<>();
        Page<OrderLimitOutDto> page = new Page<>();
        page.setTotal(orderLimitPage.getTotal());
        page.setSize(orderLimitPage.getSize());
        if (orderLimits != null && orderLimits.size() > 0) {
            for (OrderLimit ol : orderLimits) {
                OrderLimitOutDto out = new OrderLimitOutDto();
                out.setOrderId(ol.getOrderId());
                out.setPledgeOilcardFee(ol.getPledgeOilcardFee());
                setOrderCommonInfo(out, ol.getTenantId());
                outs.add(out);
            }
        }

        page.setRecords(outs);
        return page;
    }

    @Override
    public Page<LoanDetail> queryLoanDetail(Long tenantId, Long userId, String settleMonth,
                                            Integer pageNum, Integer pageSize) {
        Page<LoanDetail> page = new Page<>(pageNum, pageSize);
        return orderAccountMapper.queryLoanDetail(page, tenantId, userId, settleMonth);
    }

    @Override
    public Map<String, Object> queryOrderDebtDetail(Long userId, String settleMonthData) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
        String settleMonth = sdf.format(sdf1.parse(settleMonthData));//查询月份
        Date oneDayTime = CommonUtil.getOneDayTime(settleMonth, 1);
        String date = DateUtil.formatDate(oneDayTime, "yyyy-MM-DD hh:mm:ss");
        List<OrderDebtDetail> debtDetails = orderAccountMapper.queryOrderDebtDetail(userId, date);
        if (debtDetails != null && debtDetails.size() > 0) {
            Long sumDebtMoney = 0L;
            Long sumPaidDebt = 0L;
            Long sumNoPayDebt = 0L;
            for (OrderDebtDetail debtDetail : debtDetails) {
                sumDebtMoney += debtDetail.getDebtMoney() == null ? 0L : debtDetail.getDebtMoney();
                sumPaidDebt += debtDetail.getPaidDebt() == null ? 0L : debtDetail.getPaidDebt();
                sumNoPayDebt += debtDetail.getNoPayDebt() == null ? 0L : debtDetail.getNoPayDebt();
            }
            Map<String, Object> retMap = new HashMap<String, Object>();
            retMap.put("settleMonth", settleMonth);
            retMap.put("sumDebtMoney", sumDebtMoney);
            retMap.put("sumPaidDebt", sumPaidDebt);
            retMap.put("sumNoPayDebt", sumNoPayDebt);
            retMap.put("items", debtDetails);
            return retMap;
        }
        return null;
    }

    @Override
    public List<PeccancDetailDto> queryPeccancDetail(Long userId, String settleMonth) {
        List<PeccancDetailDto> peccancDetailDtos = orderAccountMapper.queryPeccancDetail(userId, settleMonth);
        for (PeccancDetailDto m : peccancDetailDtos) {
            m.setMainAmount(m.getAmount());
            m.setCopilotAmount(0L);//主/副驾平摊

            if (m.getOrderId() != null) {
                OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(m.getOrderId());
                if (orderScheduler != null && orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
                    Long mainAmount = m.getAmount() / 2;
                    m.setMainAmount(mainAmount);
                    m.setCopilotAmount(mainAmount);//主/副驾平摊
                }
            }
            m.setSubjectId(5);//借支类型-违章罚款
        }
        return peccancDetailDtos;
    }

    private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
        Date date;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        date = Date.from(zdt.toInstant());
        return date;
    }

    public void setOrderCommonInfo(OrderLimitOutDto out, Long tenantId) {
        OrderInfo oi = iorderInfoService.getOrder(out.getOrderId());

        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        if (oi != null) {
            OrderScheduler os = orderSchedulerService.getOrderScheduler(out.getOrderId());
            out.setCreateDate(String.valueOf(os.getDependTime()));
            out.setPlateNumber(os.getPlateNumber());
            out.setSourceRegionName(oi.getSourceRegionName());
            out.setDesRegionName(oi.getDesRegionName());
            List<OilCardManagement> oilCardList = iOilCardManagementService.getOilCardByOrderId(out.getOrderId(), tenantId);
            out.setOilCarList(oilCardList);
        } else {
            OrderInfoH oih = iOrderInfoHService.getOrderH(out.getOrderId());
            if (oih != null) {
                OrderSchedulerH osh = orderSchedulerHService.getOrderSchedulerH(out.getOrderId());
                out.setCreateDate(String.valueOf(osh.getDependTime()));
                out.setPlateNumber(osh.getPlateNumber());
                out.setSourceRegionName(String.valueOf(oih.getSourceRegion()));
                out.setDesRegionName(String.valueOf(oih.getDesRegion()));
                List<OilCardManagement> oilCardList = iOilCardManagementService.getOilCardByOrderId(out.getOrderId(), tenantId);
                out.setOilCarList(oilCardList);
            }
        }


        SysTenantDef std = sysTenantDefService.getSysTenantDef(tenantId);
        String logo = null;
        if (std != null && org.apache.commons.lang3.StringUtils.isNotBlank(std.getLogo())) {
            SysAttach sa = sysAttachService.getAttachByFlowId(Long.parseLong(std.getLogo()));
            if (sa != null) {
                logo = sa.getFullPathUrl();
            }
        }
        out.setLogo(logo);
        out.setSourceName(std.getName());

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
}
