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
 * ??????????????? ???????????????
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
            throw new BusinessException("???????????????id");
        }
        OrderAccountBalanceDto orderAccountBalance = this.getOrderAccountBalance(userId, OrderAccountConst.ORDER_BY.OIL_BALANCE, tenantId, userType);
        if (orderAccountBalance == null) {
            throw new BusinessException("???????????????????????????");
        }

        OrderAccountOutVo out = orderAccountBalance.getOa();
        long oilBalance = 0;
        if (out != null) {
            oilBalance = out.getTotalOilBalance() == null ? 0L : out.getTotalOilBalance();
        } else {
            throw new BusinessException("???????????????????????????");
        }
        return oilBalance;
    }

    @Override
    public OrderAccountBalanceDto getOrderAccountBalance(Long userId, String orderByType, Long tenantId, Integer userType) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("?????????????????????");
        }
        // ????????????id??????????????????
        UserDataInfo user = userDataInfoService.getById(userId);
        if (user == null) {
            throw new BusinessException("?????????????????????");
        }
        List<OrderAccount> accountList = getOrderAccountQuey(userId, orderByType, -1L, userType);
        // ????????????????????????
        long totalBalance = 0;
        // ???????????????
        long totalShareBalance = 0;
        // ???????????????
        long totalHaveBalance = 0;
        // ??????
        long totalOilBalance = 0;
        // ???ETC
        long totalEtcBalance = 0;
        // ????????????
        long totalMarginBalance = 0;
        //???????????????
        long totalRepairFund = 0;
        // ???????????????
        long totalDebtAmount = 0;
        // ?????????????????????
        long frozenMarginBalance = 0;
        // ????????????
        long frozenBalance = 0;
        // ?????????
        long frozenOilBalance = 0;
        // ??????etc
        long frozenEtcBalance = 0;
        //??????????????????
        long frozenRepairFund = 0;
        // ???????????????
        long canAdvance = 0;
        // ??????????????????
        long cannotAdvance = 0;
        // ???????????????
        long canUseBalance = 0;
        //??????????????????
        long cannotUseBalance = 0;
        // ????????????
        long canUseOilBalance = 0;
        // ?????????ETC
        long canUseEtcBalance = 0;
        //?????????????????????
        long canUseRepairFund = 0;
        //??????(??????????????????)
        long depositBalance = 0;
        //????????????
        long receivableOverdueBalance = 0;
        //????????????
        long payableOverdueBalance = 0;
        //???????????????
        long oilBalance = 0;
        //???????????????
        long rechargeOilBalance = 0;
        //??????????????????
        long oilRechargeBalance = 0;
        //???????????????
        long oilInvoicedBalance = 0;
        for (OrderAccount ac : accountList) {
            if (ac.getSourceTenantId() <= 0) {
                continue;
            }
            CreditRatingRule rating = creditRatingRuleService.getCreditRatingRule(userId, ac.getSourceTenantId());
//            if (rating == null) {
            log.info("??????????????????????????????????????????");
//                throw new BusinessException("??????????????????????????????????????????");
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
        //????????????
        SumQuotaAmtDto creditMap = tenantAgentServiceRelService.getSumQuotaAmtListByTenantId(tenantId, ServiceConsts.AGENT_SERVICE_TYPE.OIL);
        Long totalQuotaAmt = 0L;
        Long totalUseQuotaAmt = 0L;
        if (creditMap != null) {
//            totalQuotaAmt = creditMap.getTotalQuotaAmt() == null ? 0L : Long.valueOf(creditMap.getTotalQuotaAmt() + "");
//            totalUseQuotaAmt = creditMap.getTotalUseQuotaAmt() == null ? 0L : Long.valueOf(creditMap.getTotalQuotaAmt() + "");
            // TODO 2022-7-9 ??????
            totalQuotaAmt = creditMap.getTotalQuotaAmt() == null ? 0L : creditMap.getTotalQuotaAmt();
            totalUseQuotaAmt = creditMap.getTotalUseQuotaAmt() == null ? 0L: creditMap.getTotalUseQuotaAmt();
        }

        //????????????

        long lockBalance = pinganLockInfoService.getAccountLockSum(userId);
        //??????????????????
        OilRechargeAccount oilRechargeAccount = oilRechargeAccountService.getOilRechargeAccount(userId);
        if (oilRechargeAccount != null) {
            oilRechargeBalance += oilRechargeAccount.getCashRechargeBalance() == null ? 0L : oilRechargeAccount.getCashRechargeBalance();//????????????(????????????)
            oilRechargeBalance += oilRechargeAccount.getCreditRechargeBalance() == null ? 0L : oilRechargeAccount.getCreditRechargeBalance();//????????????
            oilRechargeBalance += oilRechargeAccount.getInvoiceOilBalance() == null ? 0L : oilRechargeAccount.getInvoiceOilBalance();//???????????????

            oilInvoicedBalance += oilRechargeAccount.getRebateRechargeBalance() == null ? 0L : oilRechargeAccount.getRebateRechargeBalance();//????????????
            oilInvoicedBalance += oilRechargeAccount.getTransferOilBalance() == null ? 0L : oilRechargeAccount.getTransferOilBalance();//??????????????????
        }
        oilInvoicedBalance += totalOilBalance;//?????????
        //????????????????????? ??????/??????
        List<OrderOilSource> oilSourceList = orderOilSourceService.getOrderOilSourceByUserId(userId, userType);
        List<RechargeOilSource> rechargeList = rechargeOilSourceService.getRechargeOilSourceByUserId(userId, userType);
        for (OrderOilSource source : oilSourceList) {
            Long noPayOil = source.getNoPayOil() == null ? 0L : source.getNoPayOil();
            Long noCreditOil = source.getNoCreditOil() == null ? 0L : source.getNoCreditOil();
            Long noRebateOil = source.getNoRebateOil() == null ? 0L : source.getNoRebateOil();
            Long sumOil = noPayOil + noCreditOil + noRebateOil;
            if (source.getOilConsumer() == OrderConsts.OIL_CONSUMER.SELF) {//??????
                totalHaveBalance += sumOil;
            }
            if (source.getOilConsumer() == OrderConsts.OIL_CONSUMER.SHARE) {//??????
                totalShareBalance += sumOil;
            }
        }
        for (RechargeOilSource source : rechargeList) {
            Long noPayOil = source.getNoPayOil() == null ? 0L : source.getNoPayOil();
            Long noCreditOil = source.getNoCreditOil() == null ? 0L : source.getNoCreditOil();
            Long noRebateOil = source.getNoRebateOil() == null ? 0L : source.getNoRebateOil();
            Long sumOil = noPayOil + noCreditOil + noRebateOil;
            if (source.getOilConsumer() == OrderConsts.OIL_CONSUMER.SELF) {//??????
                totalHaveBalance += sumOil;
            }
            if (source.getOilConsumer() == OrderConsts.OIL_CONSUMER.SHARE) {//??????
                totalShareBalance += sumOil;
            }
        }
        //???????????????????????????
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
        oa.setCreditLine(totalQuotaAmt);//????????????
        oa.setUsedCreditLine(totalUseQuotaAmt);//??????????????????
        oa.setLockBalance(lockBalance);//????????????
        oa.setElectronicOilCard(oilInvoicedBalance + totalQuotaAmt + oilRechargeBalance);//????????????=???????????????+????????????+????????????
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
        log.info("userId=" + userId + "??????????????????=" + vehicleAffiliation + "tenantId=" + tenantId + "sourceTenantId=" + sourceTenantId);
        if (userId <= 0L) {
            throw new BusinessException("??????id??????");
        }
        if (sourceTenantId <= 0L) {
            throw new BusinessException("???????????????????????????id");
        }
        if (StringUtils.isBlank(vehicleAffiliation)) {
            throw new BusinessException("?????????????????????");
        }
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("????????????????????????");
        }
        if (userType == null || userType <= 0) {
            throw new BusinessException("?????????????????????");
        }
        // ??????userid??????????????????
        UserDataInfo user = userDataInfoService.getById(userId);
        if (user == null) {
            throw new BusinessException("?????????????????????!");
        }
        OrderAccount orderAccount = this.queryOrderAccount(userId, vehicleAffiliation, sourceTenantId, oilAffiliation, userType);
        if (orderAccount == null) {
            OrderAccount newOrderAccount = new OrderAccount();
            newOrderAccount.setUserId(userId);
            //????????????????????????
            newOrderAccount.setUserType(userType);
            //????????????????????????
            newOrderAccount.setVehicleAffiliation(vehicleAffiliation);
            newOrderAccount.setAccState(1);//??????
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
        // ca.setLockMode(LockMode.PESSIMISTIC_WRITE);//??????
        return this.list(lambda);
    }

    @Override
    public List<OrderOilSource> dealTemporaryFleetOil(OrderAccount fleetAccount, OrderOilSource oilSource, Long fleetUserId, long orderId, long fleetTenantId, int isNeedBill, String vehicleAffiliation, long driverUserId, Long businessId, Long subjectsId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Date date = new Date();
        // ????????????ID???????????????????????????????????????
        List<OrderOilSource> sourceList = new ArrayList<OrderOilSource>();
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(fleetUserId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(subjectsId, oilSource.getMatchAmount());
        busiList.add(amountFeeSubjectsRel);
        // ??????????????????
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


        log.info("????????????:userId=" + userId + "amountFee=" + amountFee + "orderId=" + orderId);
        if (userId < 1) {
            throw new BusinessException("?????????????????????");
        }
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("?????????????????????");
        }
        if (orderId <= 0) {
            throw new BusinessException("??????????????????");
        }
        if (tenantId == null || tenantId <= -1) {
            throw new BusinessException("???????????????id");
        }
        // todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payForException" + orderId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[]{DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2)});
        // ????????????id?????????????????????id
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        SysUser tenantUser = iSysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);

//        UserDataInfo tenantUser = userSV.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        // ??????????????????
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //????????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        // ????????????ID???????????????????????????????????????
        OrderLimit ol = orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId, -1);
        if (ol == null) {
            throw new BusinessException("??????????????????" + orderId + " ??????ID???" + userId + " ???????????????????????????");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("??????????????????????????????");
        }
        OrderAccount account = this.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId, oilAffiliation, ol.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        Long soNbr = CommonUtil.createSoNbr();
        if (amountFee > 0) {
            // ??????????????????
            BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB, amountFee);
            busiList.add(amountFeeSubjectsRel);
            //???????????? ????????? 20190717  ?????????????????????????????????
            long serviceFee = 0;
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), amountFee, 0L, 0L, amountFee, tenantId, null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            // ??????????????????
            List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.EXCEPTION_FEE, busiList);
            // ????????????????????????????????????????????????
            accountDetailsService.insetAccDet(EnumConsts.BusiType.EXCEPTION_CODE, EnumConsts.PayInter.EXCEPTION_FEE, tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

            //??????????????????
            OrderAccount fleetAccount = this.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId, ol.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_IN_PAYABLE_OVERDUE_SUB, amountFee);
            fleetBusiList.add(payableOverdueRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4005, serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }

            // ??????????????????
            List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.EXCEPTION_FEE, fleetBusiList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.EXCEPTION_CODE, EnumConsts.PayInter.EXCEPTION_FEE,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getOrgName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

            // ?????????????????????????????????????????????
            Map<String, String> inParam = this.setParameters(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                    EnumConsts.PayInter.EXCEPTION_FEE, orderId, amountFee, vehicleAffiliation, "");
            inParam.put("tenantUserId", String.valueOf(tenantUserId));
            inParam.put("tenantBillId", tenantSysOperator.getBillId());
            inParam.put("tenantUserName", tenantUser.getLinkMan());
            busiSubjectsRelList.addAll(fleetSubjectsRelList);
            busiToOrderUtils.busiToOrder(inParam, busiSubjectsRelList);
            //??????????????????
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
            payoutIntf.setRemark("????????????");
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
                //??????payout_order
                payoutOrderService.createPayoutOrder(userId, amountFee, OrderAccountConst.FEE_TYPE.CASH_TYPE,
                        payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
            }
        }
        // ????????????
        String remark = "???????????????" + "????????????" + orderId + " ?????????????????????" + amountFee;
        sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.PayForException, soNbr, com.youming.youche.commons.constant.SysOperLogConst.OperType.Add, loginInfo.getName() + remark);

    }

    /**
     * @param userId             ??????id
     * @param billId             ???????????????
     * @param businessId         ??????id
     * @param orderId            ??????id
     * @param amount             ??????
     * @param vehicleAffiliation ????????????
     * @param finalPlanDate      ??????????????????
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
        // TODO ???????????????????????? ??????
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
//                //?????????,????????????????????????
//                if (consumeOilFlow.getState() == 1) {
//                    uo.setMarginBalanceD(0);
//                } else {
//                    uo.setMarginBalanceD(CommonUtil.getDoubleFormatLongMoney(uo.getMarginBalance(), 2));
//                }
//            }
            // ????????????????????????????????????
//            //????????????????????????
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
                uo.setShortName("???????????????");
//                userAccountVoList.add(uo);
            } else {
                uo.setShortName("???????????????");
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
            log.info("??????????????????");
            return new OrderAccountBalanceDto();
//            throw new BusinessException("??????????????????");
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
                uo.setShortName("???????????????");
            } else {
                uo.setShortName("???????????????");
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
                    "????????????", "????????????", "????????????",
                    "???????????????", "????????????", "ETC??????",
                    "????????????", "????????????",
                    "????????????", "??????"};
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
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
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
            throw new BusinessException("?????????????????????id");
        }
        if (turnMonth == null || turnMonth.isEmpty()) {
            throw new BusinessException("?????????????????????????????????");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            if (user.getTenantId() == null || user.getTenantId() <= 0) {
                throw new BusinessException("?????????????????????????????????id");
            }
            try {
                Date turnMonthDate = sdf.parse(turnMonth);
                //????????????????????????????????????
                String firstDay = TimeUtil.getMonthFirstDay(turnMonthDate);
                //???????????????????????????????????????
                String lastDay = TimeUtil.getMonthLastDay(turnMonthDate);
                oilExcDto.setCreateTime(firstDay + " 00:00:00");
                oilExcDto.setUpdateTime(lastDay + " 23:59:59");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //???????????????????????????
        //todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "saveOilAndEtcTurnCash" + userId + turnMonth , 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        String serviceName = null;
        if (turnType == null || turnType.isEmpty()) {//1????????????  ,2ETC??????
            throw new BusinessException("?????????????????????");
        } else {
            if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {
                if (turnOilType == null || turnOilType.isEmpty()) {//1????????????  ,2????????????
                    throw new BusinessException("?????????????????????????????????????????????????????????!");
                } else if (EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(turnOilType)) {
                    if (StringUtils.isEmpty(turnEntityOilCard)) {
                        throw new BusinessException("????????????????????????!");
                    } else {
                        //?????????????????????????????????
                        boolean exists = iOilCardManagementService.verifyOilCardNumIsExists(turnEntityOilCard, accessToken);
                        if (!exists) {
                            throw new BusinessException("??????????????????????????????????????????????????????!");
                        }
                        List<OilCardManagement> carNumList = iOilCardManagementService.findByOilCardNum(turnEntityOilCard, user.getTenantId());
                        if (carNumList != null && carNumList.size() > 0) {
                            OilCardManagement oilCardManagement = carNumList.get(0);
                            if (oilCardManagement.getUserId() == null) {
                                throw new BusinessException("?????????????????????????????????????????????????????????!");
                            }
                            if (oilCardManagement != null) {
                                ServiceInfo serviceInfo = iServiceInfoService.getServiceUserId(oilCardManagement.getUserId() == null ? 0L :
                                        oilCardManagement.getUserId());
                                if (serviceInfo != null) {
                                    serviceName = serviceInfo.getServiceName();
                                }
                            }
                        } else {
                            throw new BusinessException("??????????????????" + turnEntityOilCard + " ??????ID???" + user.getTenantId() + " ???????????????????????????");
                        }
                    }
                }
            }
        }
        if (turnBalance == null || turnBalance <= 0L) {
            throw new BusinessException("?????????????????????!");
        }
        if (turnDiscountDouble == null || turnDiscountDouble < 0L) {
            throw new BusinessException("?????????????????????!");
        }
        String sign = null;
        long amount = 0;
        List<OrderOilSource> newSourceList = new ArrayList<OrderOilSource>();
        List<OrderLimit> newLimitList = new ArrayList<OrderLimit>();
        List<OrderLimit> list = null;
        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {//????????????
            //???????????????????????????
            List<OrderOilSource> sourceList = orderOilSourceService.getOrderOilSource(oilExcDto);
            //????????????????????????
            for (OrderOilSource ol : sourceList) {
                if (ol.getNoPayOil().longValue() == 0L && ol.getNoCreditOil().longValue() == 0L && ol.getNoRebateOil().longValue() == 0L) {
                    continue;
                }
                boolean isCollection = false;
                OrderSchedulerH orderSchedulerH = orderSchedulerService.getOrderSchedulerH(ol.getOrderId());
                if (orderSchedulerH != null) {
                    //?????????
                    if (orderSchedulerH.getIsCollection() != null && orderSchedulerH.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                        isCollection = true;
                    }
                } else {
                    OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(ol.getOrderId());
                    if (orderScheduler != null) {
                        //?????????
                        if (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                            isCollection = true;
                        }
                    }
                }
                if (!isCollection) {
                    amount += (ol.getNoPayOil() + ol.getNoCreditOil() + ol.getNoRebateOil());//?????????
                    newSourceList.add(ol);
                }
            }
        } else if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(turnType)) {//ETC??????
            list = orderLimitService.queryOilAndEtcBalance(oilExcDto);
            if (list != null && list.size() > 0) {
                for (OrderLimit ol : list) {
                    if (ol.getNoPayEtc().longValue() <= 0) {
                        continue;
                    }
                    amount += ol.getNoPayEtc();//??????ETC
                    newLimitList.add(ol);
                }
            }
        }

        if (turnBalance > amount) {
            throw new BusinessException("???????????????????????? " + " ?????????????????????" + ((double) amount) / 100 + "???");
        }

        Long tempTurnBalance = turnBalance;
        //??????????????????

        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {//????????????
            if (newSourceList != null && newSourceList.size() > 0) {
                for (OrderOilSource source : newSourceList) {
                    if (tempTurnBalance <= 0) {
                        break;
                    }
                    OrderAccount ac = orderAccountService.queryOrderAccount(source.getUserId(), source.getVehicleAffiliation(),
                            0L, source.getSourceTenantId(), source.getOilAffiliation(), source.getUserType());
                    Long tempAmount = (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil());//?????????
                    if (ac.getOilBalance() < tempAmount) {
                        throw new BusinessException("????????????????????????????????????");
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
                    //??????????????????
                    if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType) && EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(turnOilType)) {
                        this.saveOilEntity(oilExcDto, serviceName, accessToken);
                    }
                }
            }
        } else if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(turnType)) {//ETC??????
            if (newLimitList != null && newLimitList.size() > 0) {
                for (OrderLimit source : newLimitList) {
                    if (tempTurnBalance <= 0) {
                        break;
                    }
                    OrderAccount ac = orderAccountService.queryOrderAccount(source.getUserId(), source.getVehicleAffiliation(),
                            0L, source.getTenantId(), source.getOilAffiliation(), source.getUserType());
                    long tempAmount = (source.getNoPayEtc());//?????????
                    if (ac.getEtcBalance() < tempAmount) {
                        throw new BusinessException("????????????????????????????????????");
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
            throw new BusinessException("???????????????????????????????????????????????????");
        }
        // ????????????
        String remark = "?????????" + " ???????????????" + new BigDecimal((float) turnBalance / 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString() +
                " ???????????????" + new BigDecimal((float) turnDiscountDouble / 10000).setScale(4, BigDecimal.ROUND_HALF_UP).toString()
                + " ???????????????" + turnMonth;
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.AccountQuery, userId,
                SysOperLogConst.OperType.Add, user.getName() + remark, user.getTenantId());
        sign = "????????????";
        return sign;
    }

    private void saveOilEntity(OilExcDto oilExcDto, String serviceName, String accessToken) {
        TurnCashLog turnCashLog = oilExcDto.getTurnCashLog();
        LoginInfo user = loginUtils.get(accessToken);
        List<TurnCashOrder> turnCashOrderList = oilExcDto.getTurnCashOrder();
        if (turnCashLog == null || turnCashOrderList == null || turnCashOrderList.size() <= 0) {
            throw new BusinessException("?????????????????????!");
        }
        if (turnCashLog.getDiscountAmount() > 0) {
            //??????G7??????????????????????????????

            //????????????
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
                oilTurnEntityOperLog.setRemark("?????????");
                iOilTurnEntityOperLogService.saveOrUpdate(oilTurnEntityOperLog);
            }
            //???????????????????????????
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
                oilTurnEntity.setState(1);//?????????
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
            throw new BusinessException("???????????????????????????id!");
        }
        UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //????????????id??????????????????
        UserDataInfo users = iUserDataInfoService.getUserDataInfo(user.getUserInfoId());
        if (users == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        SysUser sysOperator = iSysUserService.getSysOperatorByUserId(user.getUserInfoId());
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //???????????????
        if (user == null) {
            throw new BusinessException("?????????????????????");
        }
        if (orderAccount == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("???????????????id!");
        }
        //????????????
        Long balance = orderAccount.getBalance();
        //???????????????
        Long marginBalance = orderAccount.getMarginBalance();
        //????????????
        Long oilBalance = orderAccount.getOilBalance();
        //etc??????
        Long etcBalance = orderAccount.getEtcBalance();
        //?????????
        Long orderOil = 0L;
        //??????ETC
        Long orderEtc = 0L;
        //???????????????
        Long noPayOil = 0L;
        //????????????ETC
        Long noPayEtc = 0L;
        //???????????????
        Long paidOil = 0L;
        //????????????ETC
        Long paidEtc = 0L;
        //???????????????????????????????????????????????????
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
        //????????????????????????20??????
        Calendar cd = Calendar.getInstance();
        cd.add(Calendar.MINUTE, 20);
        //???????????????????????????
        OrderOilSource orderOilSource = null;
        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(oilExcDto.getTurnType())) {
            orderOilSource = oilExcDto.getSourcrList();
            if (orderOilSource == null) {
                throw new BusinessException("???????????????????????????!");
            }
            noPayOil += (orderOilSource.getNoPayOil() + orderOilSource.getNoCreditOil() + orderOilSource.getNoRebateOil());//?????????
            orderOil += (orderOilSource.getSourceAmount() + orderOilSource.getCreditOil() + orderOilSource.getRebateOil());//?????????
            paidOil += (orderOilSource.getPaidOil() + orderOilSource.getPaidCreditOil() + orderOilSource.getPaidRebateOil());//?????????
        }
        //????????????????????????etc
        OrderLimit orderLimit = null;
        if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(oilExcDto.getTurnType())) {
            orderLimit = oilExcDto.getOrderLimit();
            if (orderLimit == null) {
                throw new BusinessException("??????ETC??????????????????!");
            }
            orderEtc += orderLimit.getOrderEtc();//??????ETC
            noPayEtc += orderLimit.getNoPayEtc();//??????ETC
            paidEtc += orderLimit.getPaidEtc();//??????ETC
        }

        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, oilExcDto.getVehicleAffiliation(),
                0L, tenantId, oilExcDto.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        Long soNbr = CommonUtil.createSoNbr();
        //????????????
        TurnCashLog turnCashLog = iTurnCashLogService.createTurnCashLog(user.getId(), soNbr, balance, marginBalance, oilBalance, etcBalance, orderOil,
                orderEtc, oilExcDto.getTurnBalance(), oilExcDto.getTurnDiscountDouble(),
                oilExcDto.getTurnType(), oilExcDto.getTurnMonth(), oilExcDto.getVehicleAffiliation(), tenantId, oilExcDto.getUserType(), accessToken);

        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(oilExcDto.getTurnType())) {//?????????
            turnCashLog.setConsumeOrderBalance(paidOil);
            turnCashLog.setCanTurnBalance(noPayOil);
            turnCashLog.setOpRemark("????????????");
            turnCashLog.setOaLoanOil(oaLoanOilAmout);
        } else {//ETC??????
            turnCashLog.setConsumeOrderBalance(paidEtc);
            turnCashLog.setCanTurnBalance(noPayEtc);
            turnCashLog.setOpRemark("ETC?????????");
        }
        if (EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(oilExcDto.getTurnOilType())) {//??????????????????
            turnCashLog.setOilCardNumber(oilExcDto.getTurnEntityOilCard());
            turnCashLog.setOpRemark("??????????????????");
        }
        if (oaLoanOilAmout > 0L) {//????????????(????????????????????????)
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

        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(oilExcDto.getTurnType())) {//?????????
            this.oilTurnCashNew(oilExcDto, turnCashLog, orderAccount, fleetAccount, noPayOil, tenantId, soNbr, orderOilSource, turnCashOrderList, accessToken);
        } else if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(oilExcDto.getTurnType())) {// ETC??????
            this.etcTurnCashNew(oilExcDto, turnCashLog, orderAccount, fleetAccount, noPayEtc, tenantId, soNbr, orderLimit, turnCashOrderList, accessToken);
        }
    }

    private OilExcDto etcTurnCashNew(OilExcDto oilExcDto, TurnCashLog turnCashLog, OrderAccount orderAccount,
                                     OrderAccount fleetAccount, Long noPayEtc, Long tenantId, long soNbr, OrderLimit ol,
                                     List<TurnCashOrder> turnCashOrderList, String accessToken) {

        LoginInfo user = loginUtils.get(accessToken);
        if (oilExcDto.getTurnBalance() > noPayEtc) {
            throw new BusinessException("?????????????????????????????????ETC??????!");
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
        SysUser tenantSysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //????????????id??????????????????
        SysUser sysOperator = iSysOperatorService.getSysOperatorByUserIdOrPhone(oilExcDto.getUserId(), null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //???????????????
        if (user == null) {
            throw new BusinessException("?????????????????????");
        }
        //??????????????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(user.getId());
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        //??????????????????????????????
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
            //???????????? ????????? 20190717 ?????????????????????????????????
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
            //???????????? ????????? 20190717
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4004,
                        serviceFee);
                fleetBusiList.add(payableServiceFeeSubjectsRel);
            }
            //??????
            // ??????????????????
            List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, fleetBusiList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, 0L,
                    tenantSysOperator.getName(), null, tenantId,
                    null, "", null, oilExcDto.getVehicleAffiliation(), user);
        }
        BusiSubjectsRel serviceSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_SERVICE,
                turnCashLog.getIncome());
        busiList.add(serviceSubjectsRel);
        //??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, busiList);

        //????????????????????????????????????????????????
        accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), orderAccount, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getName(), LocalDateTime.now(), user.getTenantId(), "", "", null,
                oilExcDto.getVehicleAffiliation(), user);


        //???????????????????????????????????????
        long tempTurnBalance = turnCashLog.getTurnBalance();
        long tempDeductibleMargin = (turnCashLog.getDeductibleMargin() == null ? 0L : turnCashLog.getDeductibleMargin());
        //???????????????
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
            throw new BusinessException("?????????????????????????????????");
        }

        List<BusiSubjectsRel> orderBusiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetOrderBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel orderEtcSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_ETC,
                turnCashOrder.getTurnBalance());
        orderEtcSubjectsRel.setIncome(turnCashOrder.getIncome());
        orderBusiList.add(orderEtcSubjectsRel);
        if (turnCashOrder.getDiscountAmount() != null && turnCashOrder.getDiscountAmount() > 0L) {
            //??????????????????
            PayoutIntf payoutIntfNew = payoutIntfService.createPayoutIntf(oilExcDto.getUserId(), OrderAccountConst.PAY_TYPE.USER,
                    OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, turnCashOrder.getDiscountAmount(), -1L,
                    oilExcDto.getVehicleAffiliation(), ol.getOrderId(),
                    tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT,
                    EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN,
                    oilExcDto.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, ol.getUserType(), serviceFee, accessToken);
            payoutIntfNew.setObjId(Long.valueOf(sysOperator.getBillId()));
            payoutIntfNew.setRemark("etc??????");
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
                    if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//???????????????
                        payoutIntfNew.setIsDriver(OrderAccountConst.PAY_TYPE.USER);
                    }
                }
            }
            payoutIntfService.doSavePayOutIntfVirToVir(payoutIntfNew, accessToken);

            BusiSubjectsRel orderCashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN,
                    turnCashOrder.getDiscountAmount());
            orderBusiList.add(orderCashSubjectsRel);
            //??????
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
					//???????????????????????????56K????????????????????????
					if(!sysPre.equals(sysParame56K)) {
						//??????????????????
						this.synOilAndEtcToPaycenter(payoutOrder, turnType);
					}
				}
			}*/
        }
        //??????????????????
        List<BusiSubjectsRel> orderBusiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, orderBusiList);
        ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, ol.getOrderId(), oilExcDto.getTurnBalance(), ol.getVehicleAffiliation(), "");
        parametersNewDto.setFlowId(turnCashLog.getId());
        parametersNewDto.setBatchId(String.valueOf(soNbr));//??????????????????
        parametersNewDto.setTenantId(tenantId);//??????????????????
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
            throw new BusinessException("????????????????????????????????????????????????!");
        }
        Long tenantUserId = iSysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
        SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //????????????id??????????????????
        SysUser sysOperator = iSysUserService.getSysOperatorByUserId(oilExcDto.getUserId());
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //???????????????
        if (user == null) {
            throw new BusinessException("?????????????????????");
        }
        //??????????????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(oilExcDto.getUserId());
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        //??????????????????????????????
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(tenantId);
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        if (EnumConsts.TURN_CASH.TURN_OIL_TYPE1.equals(oilExcDto.getTurnOilType())) {//???????????????
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
        } else if (EnumConsts.TURN_CASH.TURN_OIL_TYPE2.equals(oilExcDto.getTurnOilType())) {//?????????????????????
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
        //??????????????????
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, busiList);

        //????????????????????????????????????????????????
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderOilSource(ol);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.ACCOUNT_INOUT_CODE, EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), orderAccount, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getName(), LocalDateTime.now(), user.getTenantId(), "", "", orderResponseDto,
                oilExcDto.getVehicleAffiliation(), user);
        //???????????????????????????????????????
        long tempTurnBalance = turnCashLog.getTurnBalance();
        long tempDeductibleLoanOil = (turnCashLog.getDeductibleLoanOil() == null ? 0L : turnCashLog.getDeductibleLoanOil());
        //???????????????
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
            throw new BusinessException("?????????????????????????????????");
        }
        //???????????????
        boolean isInheritOil = false;
        if (ol.getOrderId().longValue() != ol.getSourceOrderId().longValue()) {
            isInheritOil = true;
            List<BusiSubjectsRel> fleetOilList = new ArrayList<BusiSubjectsRel>();

            OrderAccount tenantAccount = orderAccountService.queryOrderAccount(tenantUserId, ol.getVehicleAffiliation(),
                    ol.getSourceTenantId(), ol.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            BusiSubjectsRel fleetOilSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_OIL_TRUN_CASH,
                    oilExcDto.getTurnBalance());
            fleetOilList.add(fleetOilSubjectsRel);
            // ??????????????????
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

                //??????
                BusiSubjectsRel orderCashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN,
                        turnCashOrder.getDiscountAmount());
                orderBusiList.add(orderCashSubjectsRel);
                //???????????? ????????? 20190717 ?????????????????????????????????
                long serviceFee = 0;
                boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(oilExcDto.getVehicleAffiliation()),
                        String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
                if (isLuge) {
                    Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(oilExcDto.getVehicleAffiliation()),
                            turnCashOrder.getDiscountAmount(), 0L, 0L, turnCashOrder.getDiscountAmount(), tenantId, null);
                    serviceFee = (Long) result.get("lugeBillServiceFee");
                }
                //??????
                BusiSubjectsRel fleetOrderCashSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.OIL_TURN_CASH_PAYABLE_IN,
                        turnCashOrder.getDiscountAmount());
                fleetOrderBusiList.add(fleetOrderCashSubjectsRel);
                //???????????? ????????? 20190717
                if (serviceFee > 0) {
                    BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4003, serviceFee);
                    fleetOrderBusiList.add(payableServiceFeeSubjectsRel);
                }
                //??????????????????
                List<BusiSubjectsRel> limitDriverRel = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, orderBusiList);
                List<BusiSubjectsRel> limitFleetRel = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, fleetOrderBusiList);
                busiSubjectsRelList.addAll(limitDriverRel);
                busiSubjectsRelList.addAll(limitFleetRel);
                if (ol.getOrderId().longValue() != ol.getSourceOrderId().longValue()) {
                    //????????????????????????
                    OrderLimit limit = orderLimitService.getOrderLimitByUserIdAndOrderId(oilExcDto.getUserId(), ol.getOrderId(), -1);
                    if (limit == null) {
                        throw new BusinessException("????????????id???" + oilExcDto.getUserId() + "????????????" + ol.getOrderId() + "????????????????????????");
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
                //??????????????????
                PayoutIntf payoutIntfNew = payoutIntfService.createPayoutIntf(oilExcDto.getUserId(), OrderAccountConst.PAY_TYPE.USER,
                        OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, turnCashOrder.getDiscountAmount(), -1L,
                        oilExcDto.getVehicleAffiliation(), ol.getOrderId(),
                        tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT,
                        EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN,
                        oilExcDto.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, ol.getUserType(), serviceFee, accessToken);
                payoutIntfNew.setObjId(Long.valueOf(sysOperator.getBillId()));
                payoutIntfNew.setRemark("?????????");
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
                        if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//???????????????
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
            //???????????????
            if (ol.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && !isInheritOil) {
                //???????????????????????????????????????
                Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                if (ol != null && ol.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 &&
                        ol.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                    Long oilFee = ol.getMatchAmount() == null ? 0L : ol.getMatchAmount();
                    List<BaseBillInfo> baseBillInfoList = iBaseBillInfoService.getBaseBillInfo(ol.getOrderId());
                    if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                        BaseBillInfo bbi = baseBillInfoList.get(0);
                        if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//???????????????
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
            parametersNewDto.setBatchId(String.valueOf(soNbr));//??????????????????
            parametersNewDto.setTenantId(tenantId);//??????id
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

            //???????????????
            if (ol.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && !isInheritOil) {
                //???????????????????????????????????????
                Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                if (ol != null && ol.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 &&
                        ol.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                    Long oilFee = ol.getMatchAmount() == null ? 0L : ol.getMatchAmount();
                    List<BaseBillInfo> baseBillInfoList = iBaseBillInfoService.getBaseBillInfo(ol.getOrderId());
                    if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                        BaseBillInfo bbi = baseBillInfoList.get(0);
                        if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//???????????????
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
            //??????????????????
            ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                    EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH, ol.getOrderId(), oilExcDto.getTurnBalance(), ol.getVehicleAffiliation(), "");
            parametersNewDto.setFlowId(turnCashLog.getId());
            parametersNewDto.setBatchId(String.valueOf(soNbr));//??????????????????
            parametersNewDto.setTenantId(tenantId);//??????id
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
        // ????????????id?????????????????????id
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


        // ?????????????????????????????????  ???????????????????????????????????????
        List<String> names = list.stream().map(ReceivableOverdueBalanceDto::getName).collect(Collectors.toList());
        for (ReceivableOverdueBalanceDto receivableOverdueBalanceDto : payableList) {
            if (!names.contains(receivableOverdueBalanceDto.getName())) {
                list.add(receivableOverdueBalanceDto);
            }
        }

        return listToPage(list,pageNum,pageSize);
    }

    private void countOverdueBalance(String tenantUserId, String userId, ReceivableOverdueBalanceDto receivableOverdueBalanceDto, Integer userType) {
        // ????????????id???????????????id
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
        oa.setStatementNumber(statementNumber);;//????????????????????????
        oa.setPayrollNumber(payrollNumber);//????????????????????????
        oa.setTotalBalance(Math.round(totalBalance * 100));//?????????(???)
        oa.setIncomeBalance(Math.round(incomeBalance * 100));//??????????????????(???)
        oa.setPayBalance(Math.round(payBalance * 100));//??????????????????(???)
        oa.setBusinessReceivableAccount(Math.round(incomeBalance1 * 100));//????????????
        oa.setPrivateReceivableAccount(Math.round(incomeBalance2 * 100));//????????????
        oa.setBusinessPayableAccount(Math.round(payBalance1 * 100));//????????????
        oa.setPrivatePayableAccount(Math.round(payBalance2 * 100));//????????????
        oa.setBusinessAccount(Math.round((payBalance1 + incomeBalance1) * 100));//??????
        oa.setPrivateAccount(Math.round((payBalance2 + incomeBalance2) * 100));//??????

        oa.setTotalMarginBalance(oa.getTotalMarginBalance() == null ? 0 : oa.getTotalMarginBalance());
        log.info("======999999=======userType================" + userType);
        if (this.isUserBindCard(userId, userType, 0) || this.isUserBindCard(userId, userType, 1)) {
            oa.setIsUserBindCard(true);
        } else {
            oa.setIsUserBindCard(false);
        }

        log.info("======999999=======IsUserBindCard==========" + oa.getIsUserBindCard());
        Long incomeUnDoBalance = payoutIntfService.getPayUnDoReceiveAccount(userId, userType);//????????????????????????????????????????????????????????????
        oa.setTotalBalance(oa.getTotalBalance() + incomeUnDoBalance);
        oa.setIncomeBalance(oa.getIncomeBalance() + incomeUnDoBalance);

        // ???????????????????????????????????????????????????????????????????????????
        Long payableOverdueBalance = 0L;
        try {
          //  payableOverdueBalance = payoutIntfThreeService.getOverdueCDSum(null, null, null, null, user.getUserInfoId(), accessToken);
            payableOverdueBalance =  overdueReceivableService.sumOverdueReceivable(user.getTenantId(),null,userId,2);
        } catch (Exception e) {
            log.info("user???{}", user);
            log.info("token?????????tenantId?????????????????????????????????");
        }
        oa.setPayableOverdueBalance(payableOverdueBalance);

        Long receivableOverdueBalance = 0L;
        try {
            // ?????????????????????
            //receivableOverdueBalance = payoutIntfThreeService.getDueDateDetailsSum(user.getUserInfoId(), null, null, null, null, null, String.valueOf(ADMIN_USER), accessToken);
            QueryPayoutIntfsVo queryPayoutIntfsVo = new QueryPayoutIntfsVo();
            queryPayoutIntfsVo.setSourceUserId(userId);
            Map m = payoutIntfThreeService.queryPayoutIntfsSum(accessToken,queryPayoutIntfsVo);
            if(m.get("noVerificatMoney") != null) {
                String noVerificatMoney = String.valueOf(m.get("noVerificatMoney"));
                receivableOverdueBalance = Long.parseLong(noVerificatMoney);
            }
        } catch (Exception e) {
            log.info("??????????????????:" + e.getMessage());
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
        // ??????????????????????????????
        List<OrderAccount> accountList = orderAccountService.getOrderAccountQuey(userId, null, -1L, user.getUserType());
        List<OrderAccountOutDto> orderAccountOuts = new ArrayList<>();
        HashSet hs = new HashSet<>();
        for (OrderAccount ac : accountList) {
            hs.add(ac.getSourceTenantId());
        }
        Iterator it = hs.iterator();
        while (it.hasNext()) {
            Long curId = (Long) it.next();
            // ?????????
            Long totalBalance = 0l;
            // ??????
            Long totalOilBalance = 0l;
            // ???ETC
            Long totalEtcBalance = 0l;
            // ????????????
            Long totalMarginBalance = 0l;
            //???????????????
            Long totalRepairFund = 0l;
            // ???????????????
            Long totalDebtAmount = 0l;
            //??????(??????????????????)
            Long depositBalance = 0l;

            //????????????
            Integer accState = null;
            //??????????????????
            String accStateName = null;

            //????????????????????????
            String marginStateName = "";

            //????????????
            Long receivableOverdueBalance = 0l;
            //??????
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
                throw new BusinessException("??????????????????????????????????????????");
            }
            if (totalMarginBalance > 0) {
                if (rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES) {
                    marginStateName = "";
                } else {
                    marginStateName = "????????????";
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
        List<SysStaticData> dataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));//????????????
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
            oLoanOut.setNopayedAmount(oaloan.getAmount() - (oaloan.getPayedAmount() == null ? 0L : oaloan.getPayedAmount()));// ???????????????
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
            // ??????
            oLoanOut.setStateName(oLoanOut.getState() == null ? "" : getSysStaticData("LOAN_STATE",
                    String.valueOf(oLoanOut.getState())).getCodeName());

            // ??????
            oLoanOut.setStsName(oLoanOut.getSts() == null ? "" : getSysStaticData("LOAN_STATE", String.valueOf(
                    oLoanOut.getSts())).getCodeName());

            // ????????????
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
        String settleMonth = sdf.format(sdf1.parse(settleMonthData));//????????????
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
            m.setCopilotAmount(0L);//???/????????????

            if (m.getOrderId() != null) {
                OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(m.getOrderId());
                if (orderScheduler != null && orderScheduler.getCopilotUserId() != null && orderScheduler.getCopilotUserId() > 0) {
                    Long mainAmount = m.getAmount() / 2;
                    m.setMainAmount(mainAmount);
                    m.setCopilotAmount(mainAmount);//???/????????????
                }
            }
            m.setSubjectId(5);//????????????-????????????
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
