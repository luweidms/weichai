package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.encrypt.K;
import com.youming.youche.finance.api.IAccountStatementService;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewService;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.api.payable.IPayByCashService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.api.IPayManagerService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountBankUserTypeRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOverdueReceivableService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IServiceMatchOrderService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.commons.PinganIntefaceConst;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.AccountBankUserTypeRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.ServiceMatchOrder;
import com.youming.youche.order.dto.BankReceiptOutDto;
import com.youming.youche.order.dto.DirverxDto;
import com.youming.youche.order.dto.MarginBalanceDetailsOut;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.PayoutInfosOutDto;
import com.youming.youche.order.dto.PingAnBalanceDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.dto.order.BillingDetailsOut;
import com.youming.youche.order.provider.mapper.order.AccountBankRelMapper;
import com.youming.youche.order.provider.mapper.order.OrderAccountMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysCfgRedisUtils;
import com.youming.youche.order.vo.AccountBankRelVo;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.mycenter.IBankAccountTranService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.mycenter.BankFlowDetailsAppDto;
import com.youming.youche.system.dto.mycenter.BankFlowDetailsAppOutDto;
import com.youming.youche.system.vo.mycenter.BankFlowDetailsAppVo;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PRIVATE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.RECEIVABLE_ACCOUNT;


/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class AccountBankRelServiceImpl extends BaseServiceImpl<AccountBankRelMapper, AccountBankRel> implements IAccountBankRelService {

    private static final Logger log = LoggerFactory.getLogger(AccountBankRelServiceImpl.class);

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Resource
    AccountBankRelMapper accountBankRelMapper;

    @Resource
    IAccountBankUserTypeRelService accountBankUserTypeRelService;

//    @DubboReference(version = "1.0.0")
//    IUserDataInfoRecordService iUserDataInfoService;

    @Resource
    LoginUtils loginUtils;

    @Resource
    IOrderAccountService iOrderAccountService;
    @Lazy
    @Resource
    IPayoutIntfService iPayoutIntfService;
    @Lazy
    @Resource
    IBaseBusiToOrderService iBaseBusiToOrderService;

    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;
    @Lazy
    @Resource
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @Lazy
    @Resource
    IAccountDetailsService iAccountDetailsService;

    @DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService1;

    @DubboReference(version = "1.0.0")
    IPayByCashService iPayByCashService;

    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;
    @Lazy
    @Resource
    IOrderLimitService iOrderLimitService;
    @Lazy
    @Resource
    IOrderOilSourceService iOrderOilSourceService;
    @Lazy
    @Resource
    IServiceMatchOrderService iServiceMatchOrderService;

    @Resource
    IOpAccountService iOpAccountService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Lazy
    @Resource
    IPayManagerService iPayManagerService;
    @Resource
    OrderAccountMapper orderAccountMapper;

    @Lazy
    @Resource
    ICreditRatingRuleService iCreditRatingRuleService;
    @DubboReference(version = "1.0.0")
    IBankAccountTranService iBankAccountTranService;

    @Resource
    ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    ICmSalaryInfoNewService iCmSalaryInfoNewService;

    @DubboReference(version = "1.0.0")
    IAccountStatementService iAccountStatementService;
    @DubboReference(version = "1.0.0")
    ISysCfgService  iSysCfgService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService payoutIntfThreeService;

    @Resource
    private IOverdueReceivableService overdueReceivableService;

    @Override
    public List<AccountBankRel> getCollectAmount(Long tenantId) {
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId, true);
        if (null == sysTenantDef) {
            throw new BusinessException("??????????????????");
        }
        LambdaQueryWrapper<AccountBankRel> lambda = new QueryWrapper<AccountBankRel>().lambda();
        lambda.eq(AccountBankRel::getUserId, sysTenantDef.getAdminUser())
                .isNotNull(AccountBankRel::getIdentification)
                .ne(AccountBankRel::getIdentification, "")
                .isNotNull(AccountBankRel::getAcctNo)
                .ne(AccountBankRel::getAcctNo, "")
                .eq(AccountBankRel::getIsCollectAmount, 1);
        return this.list(lambda);
    }

    @Override
    public List<AccountBankRel> getBankInfo(Long userId, String accountName, String acctNo, Integer bankType, Integer userType) {
        return accountBankRelMapper.getBankInfo(userId, accountName, acctNo, bankType, userType);
    }

    @Override
    public AccountBankRel getDefaultAccountBankRel(Long userId, Integer bankType, Integer userType) {
        if (userId < 0 || userType < 0 || bankType < 0) {
            throw new BusinessException("????????????");
        }

        LambdaQueryWrapper<AccountBankUserTypeRel> lambda = Wrappers.lambdaQuery();
        lambda.eq(AccountBankUserTypeRel::getUserId, userId)
                .eq(AccountBankUserTypeRel::getBankType, bankType)
                .eq(AccountBankUserTypeRel::getUserType, userType)
                .eq(AccountBankUserTypeRel::getIsDefault, 1);
        AccountBankUserTypeRel userTypeRel = accountBankUserTypeRelService.getOne(lambda);
        if (userTypeRel == null) {
            return null;
        }
        AccountBankRel acctBankRel = this.getById(userTypeRel.getBankRelId());
        acctBankRel.setIsDefaultAcct(1);
        return acctBankRel;
    }

    @Override
    public List<AccountBankRel> getAccountBankList(Long tenantId, String billLookUp) {
        if (tenantId == null || billLookUp == null) {
            throw new BusinessException("??????????????? tenantId=" + tenantId + "|billLookUp=" + billLookUp);
        }
        return accountBankRelMapper.getAccountBankList(tenantId, billLookUp);
    }

    @Override
    public List<AccountBankRel> queryAccountBankRel(Long userId, Integer userType, Integer bankType) {
        List<AccountBankRel> resultList = accountBankRelMapper.queryAccountBankRel(userId, userType, bankType);

        if (resultList != null) {

            for (AccountBankRel accountBankRel : resultList) {

                if (accountBankRel != null) {
                    LambdaQueryWrapper<AccountBankUserTypeRel> lambda = Wrappers.lambdaQuery();
                    if (bankType != null && bankType > 0) {
                        lambda.eq(AccountBankUserTypeRel::getBankType, bankType);
                    }
                    if (userType != null && userType > 0) {
                        lambda.eq(AccountBankUserTypeRel::getUserType, userType);
                    }

                    List<AccountBankUserTypeRel> acctUserTypeRelList = accountBankUserTypeRelService.list(lambda);
                    accountBankRel.setAcctUserTypeRelList(acctUserTypeRelList);
                }
            }
        }
        return resultList;
    }

    @Override
    public AccountBankRel getAcctNo(String pinganAcctNo) {
        LambdaQueryWrapper<AccountBankRel> lambda = Wrappers.lambdaQuery();
        lambda.eq(AccountBankRel::getPinganCollectAcctId, pinganAcctNo).or()
                .eq(AccountBankRel::getPinganPayAcctId, pinganAcctNo).last("limit 1");
        return this.getOne(lambda);
    }

    @Override
    public Boolean isUserBindCardAll(Long userId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(user.getUserInfoId());
        boolean isBindPri = isUserBindCard(userId, userDataInfo, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0);
        boolean isBindPub = isUserBindCard(userId, userDataInfo, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1);

        if (isBindPri && isBindPub) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean JudgeAmount(String accNo) {
        AccountBankRel accountBankRel = this.getAcctNo(accNo);
        if (accountBankRel == null) {
            return false;
        }
        if (!StringUtils.isNotEmpty(accountBankRel.getBranchId())) {
            return false;
        }
        return true;
    }

    /**
     * ????????????????????????????????????,???????????????????????????
     *
     * @param userId   ????????????
     * @param bankType ???????????? 0-????????????,1-????????????
     * @return true-????????????false-?????????
     * @throws Exception
     */
    @Override
    public boolean isUserBindCard(Long userId, UserDataInfo userDataInfo, Integer bankType) {

        List<AccountBankRel> accountBankRel = accountBankRelMapper.selectBankCard(userId, userDataInfo.getUserType(), bankType);
//        StringBuffer sqlBuffer = new StringBuffer();
//        sqlBuffer.append(" SELECT abr.ACCT_NO FROM account_bank_user_type_rel abutr LEFT JOIN account_bank_rel abr ON abutr.BANK_REL_ID = abr.REL_SEQ ");
//        sqlBuffer.append(" WHERE abutr.USER_ID = :userId AND abutr.USER_TYPE = :userType and abutr.BANK_TYPE = :bankType ");
//        sqlBuffer.append(" AND abr.ACCT_NO IS NOT NULL AND TRIM(abr.ACCT_NO) != '' ");
//
//        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("userId", userId);
//        paramMap.put("userType", userType);
//        paramMap.put("bankType", bankType);

//        Query priQuery = SysContexts.getEntityManager(true).createSQLQuery(sqlBuffer.toString()).setProperties(paramMap);
//        priQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

        //List acctList = priQuery.list();

        if (accountBankRel != null && accountBankRel.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<AccountBankRel> getBankListByUserId(Long userId) {
        QueryWrapper<AccountBankRel> accountBankRelQueryWrapper = new QueryWrapper<>();
        accountBankRelQueryWrapper.eq("user_id", userId);
        List<AccountBankRel> list = this.list(accountBankRelQueryWrapper);

        return list;
    }


    /**
     * ?????????
     * WX??????-?????????????????????
     * niejiewei
     *
     * @return
     */
    @Override
    public PingAnBalanceDto turnVirAmountUIByWx(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        Integer userType = loginInfo.getUserType();
        Long tenantId = loginInfo.getTenantId();
        SysTenantDef sysTenantDef = null;
        if (userType!=null){
            if (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????***
                sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
                if (sysTenantDef != null) {
                    userId = sysTenantDef.getAdminUser();
                    userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
                }
            }
        }
        List<AccountBankRel> bankList = this.queryAccountBankRel(userId, userType, null);
        if (bankList == null || bankList.size() <= 0) {
//			throw new BusinessException("????????????id???" + userId + "????????????????????????????????????");
            throw new BusinessException("???????????????????????????????????????????????????????????????????????????????????????");
        }
        OrderAccountBalanceDto orderAccountBalance = iOrderAccountService.getOrderAccountBalance(userId, OrderAccountConst.ORDER_BY.BALANCE, -1L, userType);
        if (orderAccountBalance == null) {
            throw new BusinessException("????????????id???" + userId + "??????????????????????????????");
        }
//        OrderAccountOut orderAccountOut = (OrderAccountOut) map.get(OrderAccountConst.ACCOUNT_KEY.orderAccountOut);
//        if (orderAccountOut == null) {
//            throw new BusinessException("????????????id???" + userId + "????????????????????????????????????");
//        }
        PingAnBalanceDto pingAnBalanceDto = this.queryTurnVirAmount(userId, userType);
        pingAnBalanceDto.setUserId(userId);
        return pingAnBalanceDto;
    }

    /**
     * ???????????????
     * WX??????-????????????
     * niejiewei
     *
     * @param
     * @return
     */
    @Override
    public String turnVirAmount(Long userId, Long tranAmount, Integer accountType, String mesCode,
                                String serialNo, String accountNo, Integer userType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        BaseUser currUser = SysContexts.getCurrentOperator();
//        Long userId = currUser.getUserId();
//        int userType = currUser.getUserType();
        userId = loginInfo.getUserInfoId();
        userType = loginInfo.getUserType();
        int bankType = EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1;

        if (accountType.equals(BUSINESS_RECEIVABLE_ACCOUNT) || accountType.equals(BUSINESS_PAYABLE_ACCOUNT)) {
            UserDataInfo userDataInfo = new UserDataInfo();
            userDataInfo.setUserType(userType);
            if (!this.isUserBindCard(userId, userDataInfo, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
            }
            bankType = EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1;
        } else {
            UserDataInfo userDataInfo = new UserDataInfo();
            userDataInfo.setUserType(userType);
            if (!this.isUserBindCard(userId, userDataInfo, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                throw new BusinessException("??????????????????????????????????????????????????????????????????????????????");
            }
            bankType = EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0;
        }
        accountNo = this.getIfNullAccountNO(userId, accountType, accountNo, userType);
//        SysOperator sysOperator = operatorSV.getSysOperatorByUserIdOrPhone(userId, null);
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null);
        Long orderId = new Date().getTime();
        // TODO ????????? ???????????? ??????????????????????????????
//        String thirdLogNo = bankCallTF.payAlikeTransaction(userId, orderId+"", tranAmount, mesCode, serialNo, accountType,accountNo,userType);
        String thirdLogNo = null;
        Long soNbr = CommonUtil.createSoNbr();
        Long turnOutSubjectsId = 0L;
        Long turnInSubjectsId = 0L;
        if (accountType.equals(BUSINESS_RECEIVABLE_ACCOUNT) || accountType.equals(PRIVATE_RECEIVABLE_ACCOUNT)) {//??????????????????
            turnOutSubjectsId = EnumConsts.SubjectIds.RECEIVABLES_ACCOUNT_REDUCE_CASH;
            turnInSubjectsId = EnumConsts.SubjectIds.PAYMENT_ACCOUNT_ADD_CASH;
        } else {
            turnOutSubjectsId = EnumConsts.SubjectIds.PAYMENT_ACCOUNT_REDUCE_CASH;
            turnInSubjectsId = EnumConsts.SubjectIds.RECEIVABLES_ACCOUNT_ADD_CASH;
        }
        //??????zhuan?????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        Long tenantId = 0L;
        if (sysTenantDef != null) {
//            tenantId = sysTenantDef.getTenantId();
            tenantId = sysTenantDef.getId();
        }
        //????????????????????????
        List<BusiSubjectsRel> busiBalanceReduceList = new ArrayList<BusiSubjectsRel>();
        // ??????????????????
        BusiSubjectsRel balanceReduceSub = iBusiSubjectsRelService.createBusiSubjectsRel(turnOutSubjectsId, tranAmount);
        balanceReduceSub.setSubjectsType(EnumConsts.PayInter.FEE_INOUT);
        busiBalanceReduceList.add(balanceReduceSub);
        //????????????(??????????????????)
        List<BusiSubjectsRel> balanceReduceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PINGAN_ACCOUNT_TURN_CASH, busiBalanceReduceList);
        //??????????????????????????????
        iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PINGAN_ACCOUNT_TURN_CASH, userId, userId, sysOperator.getOpName(), balanceReduceList, soNbr, 0L, tenantId, userType);
        //????????????????????????
        List<BusiSubjectsRel> busiBalanceAddList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel balanceAddSub = iBusiSubjectsRelService.createBusiSubjectsRel(turnInSubjectsId, tranAmount);
        balanceAddSub.setSubjectsType(EnumConsts.PayInter.FEE_INOUT);
        busiBalanceAddList.add(balanceAddSub);
        // ????????????(??????????????????)
        List<BusiSubjectsRel> balanceAddList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PINGAN_ACCOUNT_TURN_CASH, busiBalanceAddList);
        // ??????????????????????????????
        iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PINGAN_ACCOUNT_TURN_CASH, userId, userId, sysOperator.getOpName(), balanceAddList, soNbr, 0L, tenantId, userType);
        return thirdLogNo;

    }

    /**
     * niejiewei
     * ???????????????
     * 50014
     * ????????????-????????????
     *
     * @return
     */
    @Override
    public PingAnBalanceDto withdrawCashUIByWx(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        Integer userType = loginInfo.getUserType();
        if (userId <= 0) {
            throw new BusinessException("???????????????id");
        }
        //???????????????
        ServiceInfo serviceInfo = iServiceInfoService1.getServiceInfoById(userId);
        // ?????????????????????????????????
        if (serviceInfo == null) {//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????***
            Long tenantId = loginInfo.getTenantId();
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
            userId = sysTenantDef.getAdminUser();
            userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
        }
        List<AccountBankRel> bankList = this.queryAccountBankRel(userId, userType, null);
        if (bankList == null || bankList.size() <= 0) {
            throw new BusinessException("???????????????????????????????????????????????????????????????????????????????????????");
        }
//        Map<String,Object> map = paymentTF.getOrderAccountBalance(userId, OrderAccountConst.ORDER_BY.BALANCE,-1L,userType );
        OrderAccountBalanceDto orderAccountOut = iOrderAccountService.getOrderAccountBalance(userId, OrderAccountConst.ORDER_BY.BALANCE, -1L, userType);
//        if (map == null) {
//            throw new BusinessException("????????????id???" + userId + "??????????????????????????????");
//        }
//        OrderAccountOut orderAccountOut = (OrderAccountOut) map.get(OrderAccountConst.ACCOUNT_KEY.orderAccountOut);
        if (orderAccountOut == null) {
            throw new BusinessException("????????????id???" + userId + "????????????????????????????????????");
        }
        //1:???????????????????????????11:???????????????????????????2???????????????????????????+?????????????????????22????????????????????????
        //1:???????????????????????????11:???????????????????????????2???????????????????????????+?????????????????????22????????????????????????
        BankBalanceInfo bankBalanceInfo1 = iBaseBusiToOrderService.getBankBalanceToUserId(userId, 1, "", userType);//????????????????????????
        BankBalanceInfo bankBalanceInfo2 = iBaseBusiToOrderService.getBankBalanceToUserId(userId, 2, "", userType);//????????????????????????
        BankBalanceInfo bankBalanceInfo11 = iBaseBusiToOrderService.getBankBalanceToUserId(userId, 11, "", userType);//????????????????????????
        BankBalanceInfo bankBalanceInfo22 = iBaseBusiToOrderService.getBankBalanceToUserId(userId, 22, "", userType);//????????????????????????
        Double payHA = iBaseBusiToOrderService.getPcBalance(userId, userType);//?????????????????????
        Double incomeBalance1 = 0.00;//????????????????????????
        Double incomeBalance2 = 0.00;//????????????????????????
        //???????????????
        if (serviceInfo != null) {
            incomeBalance1 += payHA;
        } else {
            incomeBalance2 += payHA;
        }
        Double payBalance1 = 0.00;//????????????????????????
        Double payBalance2 = 0.00;//????????????????????????

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
        PingAnBalanceDto dto = new PingAnBalanceDto();
        dto.setUserId(userId);
        dto.setIncomeBalance1(Math.round(incomeBalance1 * 100));//????????????????????????(???)
        dto.setIncomeBalance2(Math.round(incomeBalance2 * 100));//????????????????????????(???)
        dto.setPayBalance1(Math.round(payBalance1 * 100));//????????????????????????(???)
        dto.setPayBalance2(Math.round(payBalance2 * 100));//????????????????????????(???)
        return dto;
    }

    @Override
    public PingAnBalanceDto withdrawCashByWx(String accessToken, AccountBankRelVo accountBankRelVo) {

//        Double amountDouble =  org.apache.commons.lang.StringUtils.isNotBlank(DataFormat.getStringKey(inParam, "amount")) ?
//                Double.parseDouble(DataFormat.getStringKey(inParam, "amount")) : 0;
        long amount = accountBankRelVo.getAmount();
        String accountType = accountBankRelVo.getAccountType().toString();
        String payPasswd = accountBankRelVo.getPayPasswd();//????????????
        String accountNo = accountBankRelVo.getAccountNo();//????????????
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        Long userId = loginInfo.getUserId();
        Long userId = loginInfo.getUserInfoId();
        int userType = loginInfo.getUserType();
        Long tenantId = loginInfo.getTenantId();
        SysTenantDef sysTenantDef = null;
        if (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????***
            sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
            if (sysTenantDef != null) {
                userId = sysTenantDef.getAdminUser();
                userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
            }
        }
        if (amount <= 0) {
            throw new BusinessException("?????????????????????0");
        }
        if (org.apache.commons.lang.StringUtils.isBlank(accountType)) {
            throw new BusinessException("?????????????????????");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(payPasswd)) {
            throw new BusinessException("?????????????????????");
        }
        boolean isInit = userDataInfoService.doInit(userId);
        if (isInit) {
            throw new BusinessException("??????????????????????????????????????????????????????????????????!");
        }
        // TODO ?????????????????????????????????????????????????????????????????????
//        checkPasswordErrSV.DealPassError(userId,payPasswd);
        iPayByCashService.doWithdrawal(accessToken, payPasswd, null);
        // TODO ????????????
        PingAnBalanceDto dto = this.doWithdrawal(userId, amount, Integer.valueOf(accountType), "2", accountNo, userType, accessToken);
        dto.setFlag("y");
        return dto;
    }


    public PingAnBalanceDto queryTurnVirAmount(Long userId, int userType) {
        if (userId <= 0) {
            throw new BusinessException("???????????????id");
        }
        List<AccountBankRel> bankList = this.queryAccountBankRel(userId, userType, null);
        if (bankList == null || bankList.size() <= 0) {
            throw new BusinessException("????????????id???" + userId + "????????????????????????????????????");
        }
        //1:???????????????????????????11:???????????????????????????2??????????????????????????????22????????????????????????
        BankBalanceInfo bankBalanceInfo1 = iBaseBusiToOrderService.getBankBalanceToUserId(userId, 1, "", userType);//????????????????????????
        BankBalanceInfo bankBalanceInfo2 = iBaseBusiToOrderService.getBankBalanceToUserId(userId, 2, "", userType);//????????????????????????+??????????????????
        BankBalanceInfo bankBalanceInfo11 = iBaseBusiToOrderService.getBankBalanceToUserId(userId, 11, "", userType);//????????????????????????
        BankBalanceInfo bankBalanceInfo22 = iBaseBusiToOrderService.getBankBalanceToUserId(userId, 22, "", userType);//????????????????????????
        Double incomeBalance1 = 0.00;//????????????????????????
        Double incomeBalance2 = 0.00;//????????????????????????
        Double payBalance1 = 0.00;//????????????????????????
        Double payBalance2 = 0.00;//????????????????????????

        if (bankBalanceInfo1 != null) {
            incomeBalance1 = (bankBalanceInfo1.getBalance() == null ? 0.0 : bankBalanceInfo1.getBalance());
        }
        if (bankBalanceInfo2 != null) {
            incomeBalance2 = (bankBalanceInfo2.getBalance() == null ? 0.0 : bankBalanceInfo2.getBalance());
        }
        if (bankBalanceInfo11 != null) {
            payBalance1 = (bankBalanceInfo11.getBalance() == null ? 0.0 : bankBalanceInfo11.getBalance());
        }
        if (bankBalanceInfo22 != null) {
            payBalance2 = (bankBalanceInfo22.getBalance() == null ? 0.0 : bankBalanceInfo22.getBalance());
        }
        PingAnBalanceDto pingAnBalance = new PingAnBalanceDto();
        pingAnBalance.setUserId(userId);
        pingAnBalance.setBusinessPayableAccount(Math.round(payBalance1 * 100));
        pingAnBalance.setBusinessReceivableAccount(Math.round(incomeBalance1 * 100));
        pingAnBalance.setPrivatePayableAccount(Math.round(payBalance2 * 100));
        pingAnBalance.setPrivateReceivableAccount(Math.round(incomeBalance2 * 100));
        return pingAnBalance;
    }


    public String getIfNullAccountNO(Long userId, Integer accountType, String accountNo, Integer userType) {
//        IAccountBankRelSV accountBankRelSV = (IAccountBankRelSV) SysContexts.getBean("accountBankRelSV");
        int bankType = EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1;
        if (accountType.equals(BUSINESS_RECEIVABLE_ACCOUNT) || accountType.equals(BUSINESS_PAYABLE_ACCOUNT)) {
            bankType = EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1;
        } else {
            bankType = EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0;
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(accountNo)) {
//            AccountBankRel defalutAccountBankRel =accountBankRelSV.getDefaultAccountBankRel(userId, bankType,userType);
            AccountBankRel defalutAccountBankRel = this.getDefaultAccountBankRel(userId, bankType, userType);
            if (defalutAccountBankRel == null) {
                throw new BusinessException("?????????????????????????????????");
            }
            accountNo = defalutAccountBankRel.getPinganCollectAcctId();
            if (accountType.equals(BUSINESS_RECEIVABLE_ACCOUNT) || accountType.equals(PRIVATE_RECEIVABLE_ACCOUNT)) {
                accountNo = defalutAccountBankRel.getPinganCollectAcctId();
            } else if (accountType.equals(BUSINESS_PAYABLE_ACCOUNT) || accountType.equals(PRIVATE_PAYABLE_ACCOUNT)) {
                accountNo = defalutAccountBankRel.getPinganPayAcctId();
            }
        }
        if (org.apache.commons.lang.StringUtils.isBlank(accountNo)) {
            throw new BusinessException("??????????????????????????????");
        }
        return accountNo;
    }

    //?????????????????????????????????????????????accountType
    @Override
    public PingAnBalanceDto doWithdrawal(Long userId, Long amount, Integer accountType, String withdrawalsChannel,
                                         String accountNo, Integer userType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        log.info("----------------??????????????????--------------------");
        log.info("?????????userId=" + userId + ",amount" + amount + ",accountType" + accountType + ",withdrawalsChannel" + withdrawalsChannel);
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //???????????????
        int serviceType = 0;
        if (userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
            ServiceInfo serviceInfo = iServiceInfoService1.getServiceInfoById(userId);
            serviceType = serviceInfo.getServiceType();
        }

        String min = sysCfgRedisUtils.getCfgVal(-0L, EnumConsts.SysCfg.CASH_MIN_MONEY, 0, String.class, accessToken).toString();
        log.info("----------------?????????????????????" + min + "(?????????)--------------------");
        if (org.apache.commons.lang.StringUtils.isNotEmpty(min)) {
            long minAmount = Long.parseLong(min);
            if (amount < minAmount) {
                throw new BusinessException("????????????????????????" + ((double) minAmount) / 100 + "???");
            }
        }
        String max = sysCfgRedisUtils.getCfgVal(-0L, EnumConsts.SysCfg.PINGAN_MAX_PAY_MONEY, 0, String.class, accessToken).toString();
        long maxAmount = Long.parseLong(max);
        //??????ti?????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        Long tenantId = -1L;
        if (sysTenantDef != null) {
            tenantId = sysTenantDef.getId();
        }
        /**
         * ??????????????????????????????????????????????????????????????????????????????????????? start
         */
        accountNo = this.getIfNullAccountNO(userId, accountType, accountNo, userType);
        /**
         * ????????????5???,?????????????????????Id
         */
        //SysStaticDataEnum.JUDEGE_AMOUNT.AMOUNT_FEN
        String amountFee = sysCfgRedisUtils.getCfgVal(-0L, SysStaticDataEnum.JUDEGE_AMOUNT.AMOUNT_FEN, 0, String.class, accessToken).toString();
        long amountFeeFen = Long.parseLong(amountFee);
        if (amount > amountFeeFen) {
            boolean flg = this.JudgeAmount(accountNo);
            if (!flg) {
                AccountBankRel accountBankRel = this.getAcctNo(accountNo);
                String acctNo = accountBankRel.getAcctNo();
                acctNo = acctNo.substring(acctNo.length() - 4, acctNo.length());
                throw new BusinessException("?????????" + acctNo + "?????????????????????????????????????????????????????????5??????");
            }
        }

        /**
         * ??????????????????????????????????????????????????????????????????????????????????????? start
         */
        Boolean sys = false;
        if (sysTenantDef != null) {
            sys = true;
        }
        log.info("----------------??????????????????????????????" + sys + "--------------------");
        log.info("----------------??????????????????????????????userId=" + userId + ",accountType=" + accountType + ",accountNo=" + accountNo + "--------------------");
        //??????????????????????????????
        BankBalanceInfo bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(userId, accountType, accountNo, userType);
        if (bankBalanceInfo == null || org.apache.commons.lang.StringUtils.isBlank(bankBalanceInfo.getAcctNo())) {
            throw new BusinessException("???????????????????????????");
        }
        Double canAmount = bankBalanceInfo.getBalance();
        Double haCanAmount = 0.0;
        if (((userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER && accountType.equals(BUSINESS_RECEIVABLE_ACCOUNT) || userType != SysStaticDataEnum.USER_TYPE.SERVICE_USER && accountType.equals(PRIVATE_RECEIVABLE_ACCOUNT))
                && bankBalanceInfo != null && bankBalanceInfo.getIsDefaultAcct() != null && bankBalanceInfo.getIsDefaultAcct() == 1)) {//???????????????????????????????????????/???????????????????????????
            haCanAmount = iBaseBusiToOrderService.getPcBalance(userId, userType);
            canAmount += haCanAmount;
        }
        if (amount <= 0) {
            throw new BusinessException("????????????????????????");
        }
        if ((canAmount * 100) < amount) {
            throw new BusinessException("????????????????????????");
        }
        log.info("----------------??????????????????????????????????????????????????????????????????" + canAmount + "--------------------");
        long soNbr = CommonUtil.createSoNbr();
        String billId = sysOperator.getBillId();
        long pinganAmount = amount;
//        Map<String,Object> retMap = new HashMap<String,Object>();
        PingAnBalanceDto dto = new PingAnBalanceDto();
        if (accountType.equals(PRIVATE_RECEIVABLE_ACCOUNT)) {//????????????
            log.info("----------????????????????????????------------");
            //????????????????????????
            //??????amount????????????
            long amountFeeTemp = amount;

            long haAmount = 0L;//HA????????????
            if (userType != SysStaticDataEnum.USER_TYPE.SERVICE_USER && bankBalanceInfo != null && bankBalanceInfo.getIsDefaultAcct() != null && bankBalanceInfo.getIsDefaultAcct() == 1) {
                log.info("----------??????????????????????????????????????????????????????????????????????????????------------");
                haAmount = this.dealHAforCar(userId, amountFeeTemp, billId, withdrawalsChannel, bankBalanceInfo, tenantId, userType, accessToken);
            }
            if (haAmount < haCanAmount) {
                haAmount = (Math.round((haCanAmount) * 100));
                log.info("----------????????????????????????????????????" + (Math.round((haCanAmount) * 100 - haAmount)) + "------------");
            }
            //???????????????????????????????????????????????????
            pinganAmount -= haAmount;
            log.info("----------????????????????????????????????????????????????" + pinganAmount + "------------");
            //???????????????????????????
            if (pinganAmount > 0) {
                int pinganSize = Integer.parseInt(pinganAmount / maxAmount + "");//????????????????????????5W--????????????
                long tmpAmountSum = pinganAmount;
                for (int i = 0; i <= pinganSize; i++) {
                    long pinganTxnAmt = maxAmount;
                    //????????????5w????????????????????????????????????????????????????????????????????????????????????for??????
                    if (tmpAmountSum == 0L) {
                        continue;
                    }
                    if (tmpAmountSum < maxAmount) {
                        pinganTxnAmt = tmpAmountSum;
                    }
                    tmpAmountSum -= pinganTxnAmt;
                    log.info("----------???????????????????????????" + tmpAmountSum + "???????????????????????????5W??????------------");
                    this.dealBankAccount(userId, billId, pinganTxnAmt, tenantId, withdrawalsChannel, bankBalanceInfo, accountType, userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER, EnumConsts.SubjectIds.WITHDRAWALS_SUB, "", userType);
                    //???????????????????????????
                    List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
                    BusiSubjectsRel balanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_SUB, pinganTxnAmt);
                    balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
                    busiBalanceList.add(balanceSub);
                    List<BusiSubjectsRel> balanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
                    iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, userId, userId, sysOperator.getOpName(), balanceList, soNbr, 0L, tenantId, userType);
                    log.info("----------??????????????????------------");
                }
            }
        } else if (accountType.equals(BUSINESS_RECEIVABLE_ACCOUNT)) {//????????????
            log.info("----------????????????------------");
            long amountFeeTemp = amount;
            long haAmount = 0L;//HA????????????
            if (userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER && bankBalanceInfo != null && bankBalanceInfo.getIsDefaultAcct() != null && bankBalanceInfo.getIsDefaultAcct() == 1) {
                log.info("----------???????????????????????????????????????????????????????????????????????????------------");
                //????????????????????????????????????????????????????????????
                haAmount = this.dealHAforService(userId, amountFeeTemp, billId, withdrawalsChannel, bankBalanceInfo, tenantId, serviceType, userType, accessToken);
            }
            pinganAmount -= haAmount;
            //???????????????????????????
            if (pinganAmount > 0) {
                int pinganSize = Integer.parseInt(pinganAmount / maxAmount + "");//????????????????????????5W
                long tmpAmountSum = pinganAmount;
                for (int i = 0; i <= pinganSize; i++) {
                    long pinganTxnAmt = maxAmount;
                    if (tmpAmountSum == 0L) {
                        continue;
                    }
                    if (tmpAmountSum < maxAmount) {
                        pinganTxnAmt = tmpAmountSum;
                    }
                    tmpAmountSum -= pinganTxnAmt;
                    log.info("----------???????????????????????????" + tmpAmountSum + "???????????????????????????5W??????------------");
                    this.dealBankAccount(userId, billId, pinganTxnAmt, tenantId, withdrawalsChannel, bankBalanceInfo, accountType, userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER, EnumConsts.SubjectIds.WITHDRAWALS_SUB, "", userType);
                    //???????????????????????????
                    List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
                    BusiSubjectsRel balanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_SUB, pinganTxnAmt);
                    balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
                    busiBalanceList.add(balanceSub);
                    List<BusiSubjectsRel> balanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
                    iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, userId, userId, sysOperator.getOpName(), balanceList, soNbr, 0L, tenantId, userType);
                    log.info("----------??????????????????------------");
                }
            }
        } else if (accountType.equals(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT) || accountType.equals(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT)) {//????????????
            //????????????????????????????????????????????????
            int pinganSize = Integer.parseInt(pinganAmount / maxAmount + "");//????????????????????????5W
            long tmpAmountSum = pinganAmount;
            for (int i = 0; i <= pinganSize; i++) {
                long pinganTxnAmt = maxAmount;
                if (tmpAmountSum == 0L) {
                    continue;
                }
                if (tmpAmountSum < maxAmount) {
                    pinganTxnAmt = tmpAmountSum;
                }
                tmpAmountSum -= pinganTxnAmt;
                log.info("----------???????????????????????????" + tmpAmountSum + "???????????????????????????5W??????------------");
                this.dealBankAccount(userId, billId, pinganTxnAmt, tenantId, withdrawalsChannel, bankBalanceInfo, accountType, userType == SysStaticDataEnum.USER_TYPE.SERVICE_USER, EnumConsts.SubjectIds.WITHDRAWALS_SUB, "", userType);
                //???????????????????????????
                List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
                BusiSubjectsRel balanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_SUB, pinganTxnAmt);
                balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
                busiBalanceList.add(balanceSub);
                List<BusiSubjectsRel> balanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
                iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, userId, userId, sysOperator.getOpName(), balanceList, soNbr, 0L, tenantId, userType);

            }
        }
        String accNo = bankBalanceInfo.getAcctNo();
        String accStrNo = org.apache.commons.lang.StringUtils.isBlank(accNo) ? "" : accNo.substring(accNo.length() - 4, accNo.length());
        log.info("??????userId???" + userId + "?????????????????????" + accNo + "|???????????????4??????" + accStrNo);
        log.info("----------------??????????????????--------------------");
        dto.setAccNoStr(accStrNo);
        dto.setAmount(amount);
        return dto;
    }

    /**
     * niejiewei
     * ???????????????
     * ????????????-????????????
     * 50016
     *
     * @return
     */
    @Override
    public PingAnBalanceDto advanceUIByWx(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        Long tenantId = loginInfo.getTenantId();
        Integer userType = loginInfo.getUserType();
        SysTenantDef sysTenantDef = null;
        if (userType!=null){
            if (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????***
                sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
                if (sysTenantDef != null) {
                    userId = sysTenantDef.getAdminUser();
                    userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
                }
            }
        }
        Map<String, Object> map = iOpAccountService.getMarginBalanceUI(userId, userType);
        if (map == null) {
            throw new BusinessException("????????????id???" + userId + "????????????????????????");
        }
        Long canAdvance = (Long) map.get(OrderAccountConst.ACCOUNT_KEY.canAdvance);
        if (canAdvance == null) {
            throw new BusinessException("????????????id???" + userId + "?????????????????????????????????");
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(OrderAccountConst.ACCOUNT_KEY.canAdvance, canAdvance);
        PingAnBalanceDto dto = new PingAnBalanceDto();
        dto.setCanAdvance(canAdvance);
        return dto;
    }


    /**
     * niejiewei
     * ???????????????
     * ????????????-??????-?????????????????????
     * 50017
     *
     * @return
     */
    @Override
    public PingAnBalanceDto getAdvanceFeeByWx(String accessToken, Long amountFee) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        Integer userType = loginInfo.getUserType();
        Long tenantId = loginInfo.getTenantId();
        SysTenantDef sysTenantDef = null;
        if (userType!=null){
            if (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????***
                sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
                if (sysTenantDef != null) {
                    userId = sysTenantDef.getAdminUser();
                    userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
                }
            }
        }
        if (amountFee <= 0) {
            throw new BusinessException("?????????????????????!");
        }
        Long serviceCharge = iPayManagerService.getAdvanceServiceCharge(userId, amountFee, null, userType);
        PingAnBalanceDto dto = new PingAnBalanceDto();
        dto.setServiceCharge(serviceCharge);//???????????????????????????????????????
        return dto;
    }

    /**
     * niejiewei
     * ???????????????
     * ????????????-??????-??????
     * 50018
     *
     * @return
     */
    @Override
    public PingAnBalanceDto confirmAdvanceByWx(String accessToken, AccountBankRelVo accountBankRelVo) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        long amountFee = accountBankRelVo.getAdvanceAmount();
        String payPasswd = accountBankRelVo.getPayPasswd();//????????????
        Long userId = loginInfo.getUserInfoId();
        Long tenantId = loginInfo.getTenantId();
        int userType = loginInfo.getUserType();
        SysTenantDef sysTenantDef = null;
        if (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????***
            sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
            if (sysTenantDef != null) {
                userId = sysTenantDef.getAdminUser();
                userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
            }
        }
        if (amountFee <= 0) {
            throw new BusinessException("?????????????????????!");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(payPasswd)) {
            throw new BusinessException("?????????????????????");
        }
        boolean isInit = userDataInfoService.doInit(userId);
        if (isInit) {
            throw new BusinessException("??????????????????????????????????????????????????????????????????!");
        }
		/*Integer errPwdNum = checkPasswordErrSV.getPwdNum(userId, SysStaticDataEnum.PASSWORD_TYPE.PAY_ERR_TYPE_PASS);
		//???????????????????????????????????????????????????2???
		Integer maxSysCode = (Integer) getCfgVal("MAX_NEED_PAY_CODE", SysStaticDataEnum.SYSTEM_CFG.CFG_0, Integer.class);
		if (maxSysCode == null) {
			maxSysCode = 2;
		}
		if (errPwdNum != null) {
			if (errPwdNum >= maxSysCode) {
				if (StringUtils.isEmpty(payCode)) {
					throw new BusinessException("???????????????????????????????????????");
				}
				payCode = EncryPwd.pwdDecryption(payCode);
				boolean istrue = CommonUtil.checkCode(SysContexts.getCurrentOperator().getTelphone(), payCode, EnumConsts.RemoteCache.VALIDKEY_PAY);
				if (!istrue) {
					throw new BusinessException("?????????????????????!");
				}
			}
		}*/
        // TODO ?????????????????????????????????????????????????????????????????????
        iPayByCashService.doWithdrawal(accessToken, payPasswd, null);
        iPayManagerService.advancePayMarginBalance(userId, amountFee, 0L, -1L, userType, accessToken);
        PingAnBalanceDto dto = new PingAnBalanceDto();
        dto.setFlag("y");
        return dto;
    }

    @Override
    public List<MarginBalanceDetailsOut> getMarginBalance(String accessToken, Long userId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (userId == null || userId <= 1) {
            throw new BusinessException("??????id?????????");
        }
        // ??????????????????????????????
        List<MarginBalanceDetailsOut> list = new ArrayList<MarginBalanceDetailsOut>();
        Map<String, MarginBalanceDetailsOut> map = new HashMap<String, MarginBalanceDetailsOut>();
        List<OrderAccount> accountList = orderAccountMapper.findOrderAccount(userId, -1L, loginInfo.getUserType());
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE1 && ac.getSourceTenantId() > 0) {
                String tenantId = String.valueOf(ac.getSourceTenantId());
                MarginBalanceDetailsOut out = map.get(tenantId);
                if (out == null) {
                    out = new MarginBalanceDetailsOut();
                    map.put(tenantId, out);
                }
            }
        }
        //??????????????????
        long canAdvance = 0;
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE1 && ac.getSourceTenantId() > 0) {
                CreditRatingRule rating = iCreditRatingRuleService.getCreditRatingRule(userId, ac.getSourceTenantId());
                if (rating == null) {
                    throw new BusinessException("??????????????????????????????????????????");
                }
                if (ac.getMarginBalance() > 0) {
                    if (rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES) {
                        canAdvance += ac.getMarginBalance();
                        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(ac.getSourceTenantId());
                        if (sysTenantDef == null) {
                            throw new BusinessException("????????????id???" + ac.getSourceTenantId() + "?????????????????????");
                        }
                        String tenantId = String.valueOf(ac.getSourceTenantId());
                        MarginBalanceDetailsOut out = map.get(tenantId);
                        long tempAdvanceAmount = out.getAdvanceAmount() == null ? 0L : out.getAdvanceAmount();
                        out.setUserId(userId);
                        out.setAdvanceAmount(tempAdvanceAmount + ac.getMarginBalance());
                        out.setFleetName(sysTenantDef.getName());
                        Float advanceCharge = rating.getCounterFee();
                        if (advanceCharge == null) {
                            advanceCharge = 0F;
                        }
                        Double temp = com.youming.youche.record.common.CommonUtil.getDoubleFormat(new Double(100 * rating.getCounterFee()), 2);
                        String proportion = String.valueOf(temp) + "%";
                        out.setProportion(proportion);
                        out.setTenantId(ac.getSourceTenantId());
                        if (!list.contains(out)) {
                            list.add(out);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<BankBalanceInfo> queryBankBalanceInfoList(Integer userType, Long userId, Integer bankType,
                                                          String acctName, String acctNo, Integer bankType1, Integer isNeedHa) {
        List<AccountBankRel> accountBankRelList = null;
        if (bankType.equals(BUSINESS_ACCOUNT)) {
            accountBankRelList = this.queryAccountBankRel(userId, userType, OrderAccountConst.BANK_TYPE.TYPE1);
        } else if (bankType.equals(PRIVATE_ACCOUNT)) {
            accountBankRelList = this.queryAccountBankRel(userId, userType, OrderAccountConst.BANK_TYPE.TYPE0);
        } else {
            accountBankRelList = this.queryAccountBankRel(userId, userType, -1);
        }
        SysCfg sysCfg = readisUtil.getSysCfg("BANK_INIT", "0");
        if (sysCfg == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        List<BankBalanceInfo> bankBalanceInfoList = new ArrayList<>();
        if (null != accountBankRelList) {
            Double payHA = iBaseBusiToOrderService.getPcBalance(userId, userType);//?????????????????????
            long haBlance = CommonUtil.multiply(payHA + "");
            for (AccountBankRel o : accountBankRelList) {
                boolean isAddHa = false;
                if (null != o.getIsDefaultAcct() && o.getIsDefaultAcct() == 1 && (isNeedHa == -1 || isNeedHa == 1)) {
                    isAddHa = true;
                }
                BankBalanceInfo bankBalanceInfo = null;
                BankBalanceInfo bankBalanceInfoNew = null;
                if (bankType.equals(BUSINESS_PAYABLE_ACCOUNT) && o.getBankType() == 1) {
                    bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserIdNo(userId, bankType, o.getPinganPayAcctId(), acctName, acctNo, bankType1, userType);
                    if (null != bankBalanceInfo.getBalance()) {
                        bankBalanceInfo.setPayAmount(CommonUtil.multiply(bankBalanceInfo.getBalance() + ""));
                    }
                } else if (bankType.equals(PRIVATE_PAYABLE_ACCOUNT) && o.getBankType() == 0) {
                    bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserIdNo(userId, bankType, o.getPinganPayAcctId(), acctName, acctNo, bankType1, userType);
                    if (null != bankBalanceInfo.getBalance()) {
                        bankBalanceInfo.setPayAmount(CommonUtil.multiply(bankBalanceInfo.getBalance() + ""));
                    }
                } else if (bankType.equals(PAYABLE_ACCOUNT) && o.getBankType() == 1) {
                    bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserIdNo(userId, BUSINESS_PAYABLE_ACCOUNT, o.getPinganPayAcctId(), acctName, acctNo, bankType1, userType);
                    bankBalanceInfo.setPayAcctId(bankBalanceInfo.getAccId());
                    if (null != bankBalanceInfo.getBalance()) {
                        bankBalanceInfo.setPayAmount(CommonUtil.multiply(bankBalanceInfo.getBalance() + ""));
                    }
                } else if (bankType.equals(PAYABLE_ACCOUNT) && o.getBankType() == 0) {
                    bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserIdNo(userId, PRIVATE_PAYABLE_ACCOUNT, o.getPinganPayAcctId(), acctName, acctNo, bankType1, userType);
                    bankBalanceInfo.setPayAcctId(bankBalanceInfo.getAccId());
                    if (null != bankBalanceInfo.getBalance()) {
                        bankBalanceInfo.setPayAmount(CommonUtil.multiply(bankBalanceInfo.getBalance() + ""));
                    }
                } else if (bankType.equals(RECEIVABLE_ACCOUNT)) {
                    bankBalanceInfo = new BankBalanceInfo();
                    bankBalanceInfo.setAcctNo(o.getAcctNo());
                    bankBalanceInfo.setCustName(o.getAcctName());
                    bankBalanceInfo.setBankCode(o.getBankName());
                    bankBalanceInfo.setProvinceName(o.getProvinceName());
                    bankBalanceInfo.setCityName(o.getCityName());
                    bankBalanceInfo.setBranchName(o.getBranchName());
                    bankBalanceInfo.setAccId(o.getPinganCollectAcctId());
                    bankBalanceInfo.setReceAcctId(o.getPinganCollectAcctId());
                } else if (bankType.equals(BUSINESS_ACCOUNT) && o.getBankType() == 1) {
                    bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(userId, BUSINESS_PAYABLE_ACCOUNT, o.getPinganPayAcctId(), userType);
                    bankBalanceInfoNew = iBaseBusiToOrderService.getBankBalanceToUserId(userId, BUSINESS_RECEIVABLE_ACCOUNT, o.getPinganCollectAcctId(), userType);
                    if (null != bankBalanceInfo.getBalance()) {
                        bankBalanceInfo.setPayAmount(CommonUtil.multiply(bankBalanceInfo.getBalance() + ""));
                        bankBalanceInfo.setPayBankBalance(bankBalanceInfo.getBalance());
                    }
                    if (null != bankBalanceInfoNew.getBalance()) {
                        bankBalanceInfo.setReceAmount(CommonUtil.multiply(bankBalanceInfoNew.getBalance() + ""));
                        bankBalanceInfo.setReceBankBalance(bankBalanceInfoNew.getBalance());
                        if (isAddHa) {
                            bankBalanceInfo.setReceAmount(bankBalanceInfo.getReceAmount() + haBlance);
                        }
                        bankBalanceInfo.setReceAmountYuan(CommonUtil.getDoubleFormatLongMoney(bankBalanceInfo.getReceAmount(), 2) + "");
                    }
                    bankBalanceInfo.setPayAcctId(bankBalanceInfo.getAccId());
                    bankBalanceInfo.setReceAcctId(bankBalanceInfoNew.getAccId());
                } else if (bankType.equals(PRIVATE_ACCOUNT) && o.getBankType() == 0) {
                    bankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(userId, PRIVATE_PAYABLE_ACCOUNT, o.getPinganPayAcctId(), userType);
                    bankBalanceInfoNew = iBaseBusiToOrderService.getBankBalanceToUserId(userId, PRIVATE_RECEIVABLE_ACCOUNT, o.getPinganCollectAcctId(), userType);
                    if (null != bankBalanceInfo.getBalance()) {
                        bankBalanceInfo.setPayAmount(CommonUtil.multiply(bankBalanceInfo.getBalance() + ""));
                        bankBalanceInfo.setPayBankBalance(bankBalanceInfo.getBalance());
                    }
                    if (null != bankBalanceInfoNew.getBalance()) {
                        bankBalanceInfo.setReceAmount(CommonUtil.multiply(bankBalanceInfoNew.getBalance() + ""));
                        bankBalanceInfo.setReceBankBalance(bankBalanceInfoNew.getBalance());
                        if (isAddHa) {
                            bankBalanceInfo.setReceAmount(bankBalanceInfo.getReceAmount() + haBlance);
                        }
                        bankBalanceInfo.setReceAmountYuan(CommonUtil.getDoubleFormatLongMoney(bankBalanceInfo.getReceAmount(), 2) + "");
                    }
                    bankBalanceInfo.setPayAcctId(bankBalanceInfo.getAccId());
                    bankBalanceInfo.setReceAcctId(bankBalanceInfoNew.getAccId());
                }
                if (null != bankBalanceInfo) {
                    bankBalanceInfo.setIsDefaultAcct(o.getIsDefaultAcct());
                    bankBalanceInfo.setBankType(o.getBankType());
                    if (org.apache.commons.lang.StringUtils.isBlank(bankBalanceInfo.getBankCode())) {
                        bankBalanceInfo.setBankCode(o.getBankName());
                    }
                    if (org.apache.commons.lang.StringUtils.isBlank(bankBalanceInfo.getProvinceName())) {
                        bankBalanceInfo.setProvinceName(o.getProvinceName());
                    }
                    if (org.apache.commons.lang.StringUtils.isBlank(bankBalanceInfo.getCityName())) {
                        bankBalanceInfo.setCityName(o.getCityName());
                    }
                    if (org.apache.commons.lang.StringUtils.isBlank(bankBalanceInfo.getBranchName())) {
                        bankBalanceInfo.setBranchName(o.getBranchName());
                    }
                    if (org.apache.commons.lang.StringUtils.isBlank(bankBalanceInfo.getCustName())) {
                        bankBalanceInfo.setCustName(o.getAcctName());
                    }
                    bankBalanceInfo.setRelSeq(o.getRelSeq());
                    bankBalanceInfo.setProvinceId(o.getProvinceid());
                    bankBalanceInfo.setCityId(o.getCityid());
                    long lockSum = iPayoutIntfService.getAccountLockSum(o.getPinganPayAcctId(), -1);
                    bankBalanceInfo.setLockSum(CommonUtil.getDoubleFormatLongMoney(lockSum, 2));
                    bankBalanceInfoList.add(bankBalanceInfo);
                }
            }
        }
        return bankBalanceInfoList;
    }

    @Override
    public Page<BillingDetailsOut> billingDetailsByWx(Long userId, String month, String fleetName,
                                                      Integer userType, Integer payUserType, Integer pageNum, Integer pageSize, String accessToken) {
        if (userId <= 0) {
            throw new BusinessException("?????????????????????!");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(month)) {
            throw new BusinessException("?????????????????????!");
        }
        Page<PayoutInfosOutDto> list = iPayoutIntfService.billingDetailsByWx(userId, month, fleetName, userType, payUserType, pageNum, pageSize);
        List<BillingDetailsOut> listOut = new ArrayList<BillingDetailsOut>();
        if (list != null && list.getRecords().size() > 0) {
            for (PayoutInfosOutDto obj : list.getRecords()) {
                //?????????
                long amount = obj.getTxnAmt() == null ? 0 : obj.getTxnAmt();
                //???????????????
                long waitBillingAmount = obj.getWaitBillingAmount() == null ? 0 : obj.getWaitBillingAmount();
                //???????????????
                long alreadyBillingAmuont = obj.getAlreadyBillingAmount() == null ? 0 : obj.getAlreadyBillingAmount();
                //??????id
                long tenantId = obj.getTenantId() == null ? 0 : obj.getTenantId();
                BillingDetailsOut billingDetails = new BillingDetailsOut();
                billingDetails.setMonth(month);
                billingDetails.setAmount(amount);
                billingDetails.setWaitBillingAmount(waitBillingAmount);
                billingDetails.setAlreadyBillingAmuont(alreadyBillingAmuont);
                billingDetails.setTenantId(tenantId);
                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
                if (sysTenantDef != null) {
                    billingDetails.setFleetName(sysTenantDef.getName());
                }
                listOut.add(billingDetails);
            }
        }
        Page<BillingDetailsOut> outPage = new Page<>();
        outPage.setRecords(listOut);
        outPage.setTotal(listOut.size());
        outPage.setSize(pageSize);
        outPage.setCurrent(pageNum);
        return outPage;

    }

    @Override
    public Page<BankFlowDetailsAppOutDto> getBankDetailsToApp(Long userId, String month, String queryType, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (loginInfo.getUserType()!=null){
            if (loginInfo.getUserType() != SysStaticDataEnum.USER_TYPE.DRIVER_USER && loginInfo != null && loginInfo.getTenantId() != null) {
                //  ?????? ??????
                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
                if (sysTenantDef != null) {
                    userId = sysTenantDef.getAdminUser();
                }
            }
        }
        if (userId <= 0) {
            throw new BusinessException("???????????????id");
        }
        if (org.apache.commons.lang.StringUtils.isBlank(month)) {
            throw new BusinessException("?????????????????????");
        }
        BankFlowDetailsAppVo vo = new BankFlowDetailsAppVo();
        vo.setMonth(month);
        vo.setUserId(userId);
        vo.setQueryType(queryType);
        vo.setUserType(loginInfo.getUserType());
        // ??????????????????
        BankFlowDetailsAppDto bankDetailsToApp = iBankAccountTranService.getBankDetailsToApp(vo, accessToken);
        Page<BankFlowDetailsAppOutDto> outPage = new Page<>();
        outPage.setRecords(bankDetailsToApp.getOut());
        outPage.setTotal(bankDetailsToApp.getOut().size());
        outPage.setSize(pageSize);
        outPage.setCurrent(pageNum);
        return outPage;
    }

    @Override
    public List<BankBalanceInfo> queryBankBalanceInfoListWX(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        int userType = userDataInfoService.selectUserType(userId).getUserType();
        if (userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????***
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
            if (sysTenantDef != null) {
                userId = sysTenantDef.getAdminUser();
                userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
            }
        }

        return this.queryBankBalanceInfoList(userType, userId, null,
                null, null, null, null);
    }

    @Override
    public BankReceiptOutDto getBankReceiptToApp(String bankPreFlowNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (org.apache.commons.lang.StringUtils.isBlank(bankPreFlowNumber)) {
            throw new BusinessException("???????????????????????????????????????");
        }
        BankReceiptOutDto out = new BankReceiptOutDto();

        String url = iSysCfgService.getCfgVal(PinganIntefaceConst.RECONCILIATION_FILE.BANK_RECEIPT_URL, 0, String.class).toString();
        if (org.apache.commons.lang.StringUtils.isBlank(url)) {
            throw new BusinessException("??????????????????" + PinganIntefaceConst.RECONCILIATION_FILE.BANK_RECEIPT_URL + "?????????????????????");
        }
        // TODO ?????????
//        String code = bankCallTF.returnCode(bankPreFlowNumber);
//        if (org.apache.commons.lang.StringUtils.isBlank(code)) {
//            throw new BusinessException("??????????????????");
//        }
//        out.setCode(code);
        out.setUrl(url);
        out.setBankPreFlowNumber(bankPreFlowNumber);
        return out;
    }

    @Override
    public OrderAccountOutVo getAccountSumWX(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        Long tenantId = loginInfo.getTenantId();
        Integer userType = userDataInfoService.selectUserType(userId).getUserType();
        if (userType != null && userType == SysStaticDataEnum.USER_TYPE.CUSTOMER_USER) {
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????***
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
            if (sysTenantDef != null) {
                userId = sysTenantDef.getAdminUser();
                userType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
            }
        }
        SysCfg sysCfg = readisUtil.getSysCfg("BANK_INIT", "0");
        if (sysCfg == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        Double incomeBalance = 0.0;
        Double payBalance = 0.0;
        Double incomeBalance1 = 0.0;
        Double incomeBalance2 = 0.0;
        Double payBalance1 = 0.0;
        Double payBalance2 = 0.0;

        Double payHA = iBaseBusiToOrderService.getPcBalance(userId, userType);//?????????????????????
        incomeBalance2 += payHA;
        Double totalBalance = incomeBalance2 + payBalance;

        OrderAccountBalanceDto map = iOrderAccountService.getOrderAccountBalance(userId, null, tenantId, userType);

        OrderAccountOutVo oa = new OrderAccountOutVo();
        if (map != null) {
            oa = map.getOa();
        }

        Integer payrollNumber = iCmSalaryInfoNewService.getCmSalaryInfoCount(userId);
        Integer statementNumber = iAccountStatementService.getAccountStatementCount(userId);

        oa.setPayrollNumber(payrollNumber);//????????????????????????
        oa.setStatementNumber(statementNumber);
        ;//????????????????????????
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

        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setUserType(userType);
        oa.setIsUserBindCard(isUserBindCard(userId, userDataInfo, 0) || isUserBindCard(userId, userDataInfo, 1));

        // ???????????????????????????????????????????????????????????????   ?????????????????????????????????????????????????????????????????????
        Long payableOverdueBalance = payoutIntfThreeService.getOverdueCDSum(null, null, null, null, null, accessToken);
        oa.setPayableOverdueBalance(payableOverdueBalance);
        //???????????????????????????
        oa.setReceivableOverdueBalance(overdueReceivableService.sumOverdueReceivable(tenantId,null,null,null));
        return oa;
    }


    /**
     * ???????????????????????????????????? 50060
     * niejeiwei
     *
     */
    @Override
    public String getBankFlowDownloadUrl(AccountBankRelVo vo, String accessToken) {
        String downloadUrl = readisUtil.getSysCfg("BANK_FLOW_DOWNLOAD_URL", "0").getCfgValue();
        if (vo.getUserId() <= 0) {
            throw new BusinessException("??????id???????????????");
        }
        String beginDate = vo.getBeginDate();
        if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getBeginDate())) {
            try {
                 beginDate = DateUtil.formatDate(DateUtil.formatStringToDate(vo.getBeginDate(),
                        DateUtil.DATE_FORMAT), DateUtil.DATE_FORMAT);
            } catch (Exception e) {
                throw new BusinessException("????????????????????????!");
            }
        }
        String endDate = vo.getEndDate();
        if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getEndDate())) {
            try {
                 endDate = DateUtil.formatDate(DateUtil.formatStringToDate(vo.getEndDate(),
                        DateUtil.DATE_FORMAT), DateUtil.DATE_FORMAT);
            } catch (Exception e) {
                throw new BusinessException("????????????????????????!");
            }
        }
        Map<String, Object> jsonObject = new HashMap<>();
        jsonObject.put("userId", vo.getUserId());
        jsonObject.put("beginDate", org.apache.commons.lang.StringUtils.isNotBlank(beginDate) ? beginDate : null );
        jsonObject.put("endDate", org.apache.commons.lang.StringUtils.isNotBlank(endDate) ? endDate : null );
        jsonObject.put("acctIdIn", org.apache.commons.lang.StringUtils.isNotBlank(vo.getAcctIdIn()) ? vo.getAcctIdIn() : null);
        jsonObject.put("acctIdOut", org.apache.commons.lang.StringUtils.isNotBlank(vo.getAcctIdOut()) ? vo.getAcctIdOut() : null );
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            downloadUrl =  downloadUrl+"&param=" + K.j_s(jsonObject.toString());
        } catch (Exception e){
            throw new BusinessException("????????????!");
        }
        return downloadUrl;
    }


    public void dealBankAccount(Long userId, String billId, Long amount, Long tenantId, String withdrawalsChannel,
                                BankBalanceInfo bankBalanceInfo, Integer accountType, Boolean isService, Long subjectsId, String busiCode, Integer userType) {
        com.youming.youche.order.domain.order.PayoutIntf payOutIntfVirToEn = new PayoutIntf();
        payOutIntfVirToEn.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15????????????
        payOutIntfVirToEn.setObjId(Long.parseLong(billId));
        payOutIntfVirToEn.setBusiId(EnumConsts.PayInter.WITHDRAWALS_CODE);
        payOutIntfVirToEn.setSubjectsId(subjectsId);
        payOutIntfVirToEn.setUserId(userId);
        //????????????????????????
        payOutIntfVirToEn.setPayUserType(userType);
        payOutIntfVirToEn.setUserType(userType);
        //????????????????????????
        payOutIntfVirToEn.setPayObjId(userId);
        payOutIntfVirToEn.setTxnAmt(amount);
        payOutIntfVirToEn.setCreateDate(LocalDateTime.now());
        payOutIntfVirToEn.setTenantId(tenantId);
        payOutIntfVirToEn.setVerificationState(Integer.valueOf(OrderConsts.PayOutVerificationState.INIT));
        payOutIntfVirToEn.setWithdrawalsChannel(withdrawalsChannel);
        payOutIntfVirToEn.setRemark("??????????????????");
        int payType = 0;
        if (isService) {//?????????
            payType = OrderAccountConst.PAY_TYPE.SERVICE;
        } else {
            //?????????????????????
            boolean isTenant = false;

            //??????????????????????????????
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
            if (sysTenantDef != null) {
                isTenant = true;
            }

            if (!isTenant) {//??????
                payType = OrderAccountConst.PAY_TYPE.USER;
            } else {//??????
                payType = OrderAccountConst.PAY_TYPE.TENANT;
            }
        }
        payOutIntfVirToEn.setBusiCode(busiCode);
        payOutIntfVirToEn.setBankType(accountType);
        payOutIntfVirToEn.setBankCode(bankBalanceInfo.getBankCode());
        payOutIntfVirToEn.setIsDriver(payType);//0???????????????1???????????????,2???????????? ?????????
        payOutIntfVirToEn.setPayType(payType);
        payOutIntfVirToEn.setVehicleAffiliation("0");
        payOutIntfVirToEn.setOilAffiliation("0");
        payOutIntfVirToEn.setPayAccNo(bankBalanceInfo.getAccId());//????????????
        payOutIntfVirToEn.setPayAccName(bankBalanceInfo.getCustName());
        payOutIntfVirToEn.setPayBankAccNo(bankBalanceInfo.getAcctNo());
        payOutIntfVirToEn.setPayBankAccName(bankBalanceInfo.getCustName());
        payOutIntfVirToEn.setAccNo(bankBalanceInfo.getAcctNo());//???????????????
        payOutIntfVirToEn.setAccName(bankBalanceInfo.getCustName());
        payOutIntfVirToEn.setReceivablesBankAccNo(bankBalanceInfo.getAcctNo());
        payOutIntfVirToEn.setReceivablesBankAccName(bankBalanceInfo.getCustName());
        payOutIntfVirToEn.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
        payOutIntfVirToEn.setPayTenantId(tenantId);
        payOutIntfVirToEn.setIsAutomatic(1);
        payOutIntfVirToEn.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);// ??????(?????????)
        payOutIntfVirToEn.setAccountType(accountType);//???????????????????????????
        iPayoutIntfService.doSavePayOutIntfVirToEn(payOutIntfVirToEn);
    }

    public Long dealHAforCar(Long userId, Long amountFeeTemp, String billId, String withdrawalsChannel,
                             BankBalanceInfo bankBalanceInfo, Long tenantId, Integer userType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        log.info("--------------------??????????????????????????????----------------");
        log.info("?????????userId=" + userId + ",amountFeeTemp" + amountFeeTemp + ",billId:" + billId + ",withdrawalsChannel" + withdrawalsChannel + ",bankBalanceInfo:" + bankBalanceInfo + ",tenantId" + tenantId);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> busiSubjectsRelList = null;
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null);
        //???????????????????????????????????????????????????????????????
        //??????????????????????????????????????????orderLimit???account_balance??????order_account???balance??????
        //??????????????????payout_intf?????????????????????order_fund_flow???????????????????????????order_account_detail
//  		List<OrderAccount> outAccount = orderAccountSV.getOrderAccount(userId, OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION, -1);
        List<OrderAccount> outAccount = iOrderLimitService.matchHaOrderAccount(userId);
        log.info("--------------------??????????????????????????????" + (outAccount != null ? (outAccount.toArray()) : "?????????") + "----------------");
        long txnAmountSum = 0L;//???????????????????????????????????????
        long haAmount = 0L;
        long soNbr = CommonUtil.createSoNbr();
        String max = sysCfgRedisUtils.getCfgVal(-0L, EnumConsts.SysCfg.PINGAN_MAX_PAY_MONEY, 0, String.class, accessToken).toString();
        long maxAmount = Long.parseLong(max);
        log.info("--------------------??????????????????????????????????????????" + maxAmount + "(????????????)----------------");
        for (OrderAccount account : outAccount) {
            if (amountFeeTemp <= 0 || OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(account.getVehicleAffiliation())
                    || OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(account.getVehicleAffiliation())
                    || account.getAccState() == null || account.getAccState() == 0 || account.getSourceTenantId() <= 0
                    || account.getBalance() <= 0) {
                continue;
            }
            Long tenantUserId = sysTenantDefService.getTenantAdminUser(account.getSourceTenantId());
            if (tenantUserId == null || tenantUserId <= 0L) {
                log.error("--------------------???????????????????????????id!----------------");
                throw new BusinessException("???????????????????????????id!");
            }
            UserDataInfo tenantUser = userDataInfoService.getUserDataInfo(tenantUserId);
            if (tenantUser == null) {
                log.error("--------------------????????????????????????1----------------");
                throw new BusinessException("????????????????????????");
            }
            SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null);
            if (tenantSysOperator == null) {
                log.error("--------------------????????????????????????2----------------");
                throw new BusinessException("????????????????????????!");
            }
//  			CreditRatingRule rating = userCreditSV.getCreditRatingRule(userId, account.getSourceTenantId());
//  			if (rating == null) {
//  				throw new BusinessException("??????????????????????????????????????????");
//  			}
//  			if (rating.getIsWithdrawal() == null || rating.getIsWithdrawal() != OrderAccountConst.IS_ADVANCE.YES) {
//  				continue;
//  			}

            if (amountFeeTemp > account.getBalance()) {
                txnAmountSum = account.getBalance();
            } else {
                txnAmountSum = amountFeeTemp;
            }
            if (txnAmountSum == 0) {
                continue;
            }

            Map<String, Object> inParam = new HashMap<String, Object>();
            inParam.put(OrderAccountConst.COMMON_KEY.userId, userId);
            inParam.put(OrderAccountConst.COMMON_KEY.vehicleAffiliation, account.getVehicleAffiliation());
            inParam.put(OrderAccountConst.COMMON_KEY.amountFee, txnAmountSum);
            inParam.put(OrderAccountConst.COMMON_KEY.tenantId, account.getSourceTenantId());
            log.info("-------------------??????????????????????????????----------------");
            // ??????????????????????????????
            List<OrderLimit> orderLimitList = iOrderLimitService.matchOrderInfoWithdraw(inParam);
            if (orderLimitList == null || orderLimitList.size() <= 0) {
                throw new BusinessException("??????????????????????????????");
            } else {
                Calendar cd = Calendar.getInstance();
                long orderLimitAmountSum = 0L;
                for (OrderLimit ol : orderLimitList) {
                    if (ol.getAccountBalance() == null || ol.getAccountBalance() <= 0L) {
                        continue;
                    }
                    Long txnAmount = ol.getAccountBalance();
                    if (txnAmount > amountFeeTemp) {
                        txnAmount = amountFeeTemp;
                    }
                    orderLimitAmountSum += txnAmount;
                    amountFeeTemp -= txnAmount;
                    haAmount += txnAmount;//????????????->????????????
                }
                log.info("-------------------???????????????????????????" + haAmount + "----------------");
                log.info("-------------------??????????????????????????????----------------");
                //HA????????????
                BankBalanceInfo haBankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(Long.valueOf(account.getVehicleAffiliation()),
                        BUSINESS_RECEIVABLE_ACCOUNT, "", SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                if (haBankBalanceInfo == null) {
                    throw new BusinessException("??????????????????????????????!");
                }
                int pinganSize = Integer.parseInt(orderLimitAmountSum / maxAmount + "");//????????????????????????5W
                long tmpAmountSum = orderLimitAmountSum;
                log.info("-------------------?????????????????????5W???????????????----------------");
                for (int i = 0; i <= pinganSize; i++) {
                    long pinganTxnAmt = maxAmount;
                    if (tmpAmountSum == 0L) {
                        continue;
                    }
                    if (tmpAmountSum < maxAmount) {
                        pinganTxnAmt = tmpAmountSum;
                    }
                    tmpAmountSum -= pinganTxnAmt;
                    //??????1?????????300???????????????HA?????????????????????HA????????????
                    PayoutIntf payOutIntfHAToHAEn = new PayoutIntf();
                    payOutIntfHAToHAEn.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15????????????
                    payOutIntfHAToHAEn.setPayObjId(Long.valueOf(account.getVehicleAffiliation()));
                    payOutIntfHAToHAEn.setPayUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                    payOutIntfHAToHAEn.setObjId(Long.parseLong(billId));
                    payOutIntfHAToHAEn.setUserId(Long.valueOf(account.getVehicleAffiliation()));
                    payOutIntfHAToHAEn.setUserType(userType);
                    //????????????????????????

                    //????????????????????????
                    payOutIntfHAToHAEn.setBusiId(EnumConsts.PayInter.WITHDRAWALS_CODE);
                    payOutIntfHAToHAEn.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_HA);
                    payOutIntfHAToHAEn.setTxnAmt(pinganTxnAmt);
                    payOutIntfHAToHAEn.setCreateDate(LocalDateTime.now());
                    payOutIntfHAToHAEn.setTenantId(-1L);
                    payOutIntfHAToHAEn.setVerificationState(Integer.valueOf(OrderConsts.PayOutVerificationState.INIT));
                    payOutIntfHAToHAEn.setWithdrawalsChannel(withdrawalsChannel);
                    payOutIntfHAToHAEn.setIsDriver(OrderAccountConst.PAY_TYPE.HAEN);//0???????????????1???????????????,2???????????? ?????????
                    payOutIntfHAToHAEn.setPayType(OrderAccountConst.PAY_TYPE.HAVIR);
                    payOutIntfHAToHAEn.setVehicleAffiliation(account.getVehicleAffiliation());
                    payOutIntfHAToHAEn.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                    payOutIntfHAToHAEn.setPayTenantId(-1L);
                    payOutIntfHAToHAEn.setPayAccNo(haBankBalanceInfo.getAccId());//????????????
                    payOutIntfHAToHAEn.setPayAccName(haBankBalanceInfo.getCustName());
                    payOutIntfHAToHAEn.setPayBankAccNo(haBankBalanceInfo.getAcctNo());
                    payOutIntfHAToHAEn.setPayBankAccName(haBankBalanceInfo.getCustName());
                    payOutIntfHAToHAEn.setAccNo(haBankBalanceInfo.getAcctNo());//???????????????
                    payOutIntfHAToHAEn.setAccName(haBankBalanceInfo.getCustName());
                    payOutIntfHAToHAEn.setBankCode(haBankBalanceInfo.getBankCode());
                    payOutIntfHAToHAEn.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
                    payOutIntfHAToHAEn.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);// ????????????(?????????)
                    payOutIntfHAToHAEn.setAccountType(BUSINESS_RECEIVABLE_ACCOUNT);//HA?????????????????????HA??????
                    payOutIntfHAToHAEn.setRemark("????????????????????????");
                    payOutIntfHAToHAEn.setReceivablesBankAccNo(haBankBalanceInfo.getAcctNo());
                    payOutIntfHAToHAEn.setReceivablesBankAccName(haBankBalanceInfo.getCustName());
                    payOutIntfHAToHAEn.setOilAffiliation(account.getOilAffiliation());
                    iPayoutIntfService.doSavePayOutIntfHAToHAEn(payOutIntfHAToHAEn);
                    log.info("------------------300??????payOutIntfHAToHAEn(????????????????????????????????????????????????????????????)???" + payOutIntfHAToHAEn + "----------------");

                    // ??????HA -???????????????????????????????????????
                    List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
                    BusiSubjectsRel balanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_HA, pinganTxnAmt);
                    balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
                    busiBalanceList.add(balanceSub);
                    List<BusiSubjectsRel> balanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
                    iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, userId, userId, sysOperator.getOpName(), balanceList, soNbr, 0L, account.getSourceTenantId(), userType);
                    log.info("------------------??????????????????----------------");
                    // ??????
                    BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
                    amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);
                    amountFeeSubjectsRel.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
                    amountFeeSubjectsRel.setAmountFee(pinganTxnAmt);
                    busiList.add(amountFeeSubjectsRel);// ??????????????????
                    busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiList);
                    // ????????????????????????????????????????????????
                    iAccountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, tenantSysOperator.getOpUserId(),
                            tenantSysOperator.getOpName(), account, busiSubjectsRelList, soNbr, 0L, sysOperator.getOpName(),
                            null, account.getSourceTenantId(), null, "", null, account.getVehicleAffiliation(), loginInfo);
                    log.info("------------------????????????????????????????????????????????????----------------");
                    //??????????????????????????????????????? //?????????????????????????????????/????????????????????????
                    PayoutIntf payOutIntfHAEnToEn = new PayoutIntf();
                    payOutIntfHAEnToEn.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15????????????
                    payOutIntfHAEnToEn.setBusiId(EnumConsts.PayInter.WITHDRAWALS_CODE);
                    payOutIntfHAEnToEn.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);
                    payOutIntfHAEnToEn.setObjId(Long.parseLong(billId));
                    payOutIntfHAEnToEn.setXid(payOutIntfHAToHAEn.getId());
                    payOutIntfHAEnToEn.setUserId(userId);
                    //????????????????????????
                    payOutIntfHAEnToEn.setPayUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                    //????????????????????????
                    payOutIntfHAEnToEn.setTxnAmt(pinganTxnAmt);// ??????????????????
                    payOutIntfHAEnToEn.setCreateDate(LocalDateTime.now());
                    payOutIntfHAEnToEn.setTenantId(tenantId);
                    payOutIntfHAEnToEn.setCreateDate(LocalDateTime.now());
                    payOutIntfHAEnToEn.setVerificationState(Integer.valueOf(OrderConsts.PayOutVerificationState.INIT));
                    payOutIntfHAEnToEn.setWithdrawalsChannel(withdrawalsChannel);
                    payOutIntfHAEnToEn.setRemark("????????????????????????");
                    payOutIntfHAEnToEn.setPayObjId(Long.valueOf(account.getVehicleAffiliation()));
                    payOutIntfHAEnToEn.setOilAffiliation(account.getOilAffiliation());
                    //??????????????????????????????
                    SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
                    boolean isTenant = false;
                    if (sysTenantDef != null) {
                        isTenant = true;
                    }
                    if (isTenant) {
                        payOutIntfHAEnToEn.setUserType(com.youming.youche.record.common.SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                        payOutIntfHAEnToEn.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                    } else {
                        payOutIntfHAEnToEn.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        payOutIntfHAEnToEn.setIsDriver(OrderAccountConst.PAY_TYPE.USER);
                        payOutIntfHAEnToEn.setTenantId(-1L);
                    }
                    payOutIntfHAEnToEn.setPayType(OrderAccountConst.PAY_TYPE.HAEN);
                    payOutIntfHAEnToEn.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
                    payOutIntfHAEnToEn.setVehicleAffiliation(account.getVehicleAffiliation());
                    payOutIntfHAEnToEn.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                    payOutIntfHAEnToEn.setPayTenantId(account.getSourceTenantId());
                    payOutIntfHAEnToEn.setPayAccNo(haBankBalanceInfo.getAcctNo());//HA??????
                    payOutIntfHAEnToEn.setPayAccName(haBankBalanceInfo.getCustName());
                    payOutIntfHAEnToEn.setPayBankAccNo(haBankBalanceInfo.getAcctNo());
                    payOutIntfHAEnToEn.setPayBankAccName(haBankBalanceInfo.getCustName());
                    payOutIntfHAEnToEn.setAccNo(bankBalanceInfo.getAcctNo());//??????????????????
                    payOutIntfHAEnToEn.setAccName(bankBalanceInfo.getCustName());
                    payOutIntfHAEnToEn.setReceivablesBankAccNo(bankBalanceInfo.getAcctNo());
                    payOutIntfHAEnToEn.setReceivablesBankAccName(bankBalanceInfo.getCustName());
                    payOutIntfHAEnToEn.setBankCode(bankBalanceInfo.getBankCode());
                    payOutIntfHAEnToEn.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
                    payOutIntfHAEnToEn.setTxnType(OrderAccountConst.TXN_TYPE.PLATFORM_TXN_TYPE);// ????????????(?????????)
                    payOutIntfHAEnToEn.setAccountType(PRIVATE_RECEIVABLE_ACCOUNT);//??????/??????????????????????????????
                    iPayoutIntfService.doSavePayOutIntfHAToHAEn(payOutIntfHAEnToEn);
                    log.info("------------------100??????payOutIntfHAEnToEn(????????????????????????????????????????????????)???" + payOutIntfHAEnToEn + "----------------");
                    long haVirtoHaEn = pinganTxnAmt;
                    Map<String, Object> orderInParam = new HashMap<String, Object>();
                    orderInParam.put(OrderAccountConst.COMMON_KEY.userId, userId);
                    orderInParam.put(OrderAccountConst.COMMON_KEY.vehicleAffiliation, account.getVehicleAffiliation());
                    orderInParam.put(OrderAccountConst.COMMON_KEY.amountFee, haVirtoHaEn);
                    orderInParam.put(OrderAccountConst.COMMON_KEY.tenantId, account.getSourceTenantId());
                    log.info("------------------??????????????????orderFuncFlow,????????????payoutOrder----------------");
                    for (OrderLimit ol : orderLimitList) {
                        if (haVirtoHaEn <= 0L || ol.getAccountBalance() == null || ol.getAccountBalance() <= 0L) {
                            continue;
                        }
                        Long txnAmount = ol.getAccountBalance();
                        if (txnAmount > haVirtoHaEn) {
                            txnAmount = haVirtoHaEn;
                        }
                        haVirtoHaEn -= txnAmount;
                        //?????????????????? ???????????????
//                        orderInParam = busiToOrder.setParametersNew(sysOperator.getUserId(), sysOperator.getBillId(),EnumConsts.PayInter.WITHDRAWALS_CODE, 0L, txnAmount,account.getVehicleAffiliation(), "");
                        ParametersNewDto dto = iOrderOilSourceService.setParametersNew(sysOperator.getOpUserId(),
                                sysOperator.getBillId(), EnumConsts.PayInter.WITHDRAWALS_CODE, 0L, txnAmount, account.getVehicleAffiliation(), "");
                        orderInParam.put(OrderAccountConst.COMMON_KEY.payoutIntf, payOutIntfHAEnToEn);
                        orderInParam.put(OrderAccountConst.COMMON_KEY.orderId, ol.getOrderId());
                        dto.setPayoutIntf(payOutIntfHAEnToEn);
                        dto.setOrderId(ol.getOrderId());
                        iOrderOilSourceService.busiToOrderNew(dto, busiSubjectsRelList, loginInfo);

                    }
                }

            }
            log.info("--------------------????????????????????????" + haAmount + "(????????????)----------------");
            log.info("--------------------??????????????????????????????----------------");
        }
        return haAmount;
    }


    public Long dealHAforService(Long userId, Long amountFeeTemp, String billId, String withdrawalsChannel, BankBalanceInfo bankBalanceInfo,
                                 Long tenantId, int serviceType, int userType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> busiSubjectsRelList = null;
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(userId, null);
        //???????????????????????????????????????HA????????????
        //??????????????????????????????????????????orderLimit???account_balance??????order_account???balance??????
        //??????????????????payout_intf?????????????????????order_fund_flow???????????????????????????order_account_detail
//  		List<OrderAccount> outAccount = orderAccountSV.getOrderAccount(userId, OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION, -1);
        List<OrderAccount> outAccount = iOrderLimitService.matchHaOrderAccount(userId);
        long txnAmountSum = 0L;//???????????????????????????????????????
        long haAmount = 0L;
        long soNbr = CommonUtil.createSoNbr();
        for (OrderAccount account : outAccount) {
            if (amountFeeTemp <= 0 || OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(account.getVehicleAffiliation())
                    || OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(account.getVehicleAffiliation())
                    || account.getAccState() == null || account.getAccState() == 0 || account.getSourceTenantId() <= 0
                    || account.getBalance() <= 0) {
                continue;
            }
            Long tenantUserId = sysTenantDefService.getTenantAdminUser(account.getSourceTenantId());
            if (tenantUserId == null || tenantUserId <= 0L) {
                throw new BusinessException("???????????????????????????id!");
            }
            UserDataInfo tenantUser = userDataInfoService.getUserDataInfo(tenantUserId);
            if (tenantUser == null) {
                throw new BusinessException("????????????????????????");
            }
            SysUser tenantSysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null);
            if (tenantSysOperator == null) {
                throw new BusinessException("????????????????????????!");
            }
//  			CreditRatingRule rating = userCreditSV.getCreditRatingRule(userId, account.getSourceTenantId());
//  			if (rating == null) {
//  				throw new BusinessException("??????????????????????????????????????????");
//  			}
//  			if (rating.getIsWithdrawal() == null || rating.getIsWithdrawal() != OrderAccountConst.IS_ADVANCE.YES) {
//  				continue;
//  			}

            if (amountFeeTemp > account.getBalance()) {
                txnAmountSum = account.getBalance();
            } else {
                txnAmountSum = amountFeeTemp;
            }
            String max = sysCfgRedisUtils.getCfgVal(-0L, EnumConsts.SysCfg.PINGAN_MAX_PAY_MONEY, 0, String.class, accessToken).toString();
            long maxAmount = Long.parseLong(max);

            // ??????????????????????????????
            List<ServiceMatchOrder> serviceMatchOrders = iServiceMatchOrderService.getServiceMatchOrder(userId, 1, account.getVehicleAffiliation()
                    , account.getOilAffiliation(), account.getSourceTenantId());
            long serviceMatchOrderSum = 0L;
            for (ServiceMatchOrder ol : serviceMatchOrders) {
                if (amountFeeTemp <= 0L || ol.getAccountBalance() == null || ol.getAccountBalance() <= 0L) {
                    continue;
                }
                Long txnAmount = ol.getAccountBalance();
                if (amountFeeTemp < txnAmount) {
                    txnAmount = amountFeeTemp;
                }
                amountFeeTemp -= txnAmount;//??????????????????
                serviceMatchOrderSum += txnAmount;//???????????????????????????
            }
            //HA????????????
            BankBalanceInfo haBankBalanceInfo = iBaseBusiToOrderService.getBankBalanceToUserId(Long.valueOf(account.getVehicleAffiliation()), BUSINESS_RECEIVABLE_ACCOUNT, "", SysStaticDataEnum.USER_TYPE.SERVICE_USER);
            if (haBankBalanceInfo == null) {
                throw new BusinessException("??????????????????????????????!");
            }
            int pinganSize = Integer.parseInt(serviceMatchOrderSum / maxAmount + "");//????????????????????????5W
            long tmpAmountSum = serviceMatchOrderSum;
            for (int i = 0; i <= pinganSize; i++) {
                long pinganTxnAmt = maxAmount;
                if (tmpAmountSum == 0L) {
                    continue;
                }
                if (tmpAmountSum < maxAmount) {
                    pinganTxnAmt = tmpAmountSum;
                }
                tmpAmountSum -= pinganTxnAmt;
                // ??????HA -???????????????????????????????????????
                List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
                BusiSubjectsRel balanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_HA, txnAmountSum);
                balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
                busiBalanceList.add(balanceSub);
                List<BusiSubjectsRel> balanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
                iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, userId, userId,
                        sysOperator.getOpName(), balanceList, soNbr, 0L, account.getSourceTenantId(), userType);

                // ??????
                BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
                amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);
                amountFeeSubjectsRel.setAmountFee(pinganTxnAmt);
                amountFeeSubjectsRel.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
                busiList.add(amountFeeSubjectsRel);
                Calendar cd = Calendar.getInstance();
                Map<String, Object> inParam = new HashMap<String, Object>();
                inParam.put(OrderAccountConst.COMMON_KEY.userId, userId);
                inParam.put(OrderAccountConst.COMMON_KEY.vehicleAffiliation, account.getVehicleAffiliation());
                inParam.put(OrderAccountConst.COMMON_KEY.amountFee, pinganTxnAmt);
                inParam.put(OrderAccountConst.COMMON_KEY.tenantId, account.getSourceTenantId());
                // ??????????????????
                busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiList);
                //??????1?????????300???????????????HA?????????????????????HA????????????
                PayoutIntf payOutIntfHAToHAEn = new PayoutIntf();
                payOutIntfHAToHAEn.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15????????????
                payOutIntfHAToHAEn.setObjId(Long.parseLong(billId));
                payOutIntfHAToHAEn.setUserId(Long.valueOf(account.getVehicleAffiliation()));
                //????????????????????????
                payOutIntfHAToHAEn.setUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                payOutIntfHAToHAEn.setPayUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                //????????????????????????
                payOutIntfHAToHAEn.setPayObjId(Long.valueOf(account.getVehicleAffiliation()));
                payOutIntfHAToHAEn.setBusiId(EnumConsts.PayInter.WITHDRAWALS_CODE);
                payOutIntfHAToHAEn.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_HA);
                payOutIntfHAToHAEn.setTxnAmt(pinganTxnAmt);
                payOutIntfHAToHAEn.setCreateDate(LocalDateTime.now());
                payOutIntfHAToHAEn.setTenantId(-1L);
                payOutIntfHAToHAEn.setVerificationState(Integer.valueOf(OrderConsts.PayOutVerificationState.INIT));
                payOutIntfHAToHAEn.setWithdrawalsChannel(withdrawalsChannel);
                payOutIntfHAToHAEn.setIsDriver(OrderAccountConst.PAY_TYPE.HAEN);//0???????????????1???????????????,2???????????? ?????????
                payOutIntfHAToHAEn.setPayType(OrderAccountConst.PAY_TYPE.HAVIR);
                payOutIntfHAToHAEn.setVehicleAffiliation(account.getVehicleAffiliation());
                payOutIntfHAToHAEn.setPayAccNo(haBankBalanceInfo.getAccId());//????????????
                payOutIntfHAToHAEn.setPayAccName(haBankBalanceInfo.getCustName());
                payOutIntfHAToHAEn.setPayBankAccNo(haBankBalanceInfo.getAcctNo());
                payOutIntfHAToHAEn.setPayBankAccName(haBankBalanceInfo.getCustName());
                payOutIntfHAToHAEn.setAccNo(haBankBalanceInfo.getAcctNo());//???????????????
                payOutIntfHAToHAEn.setAccName(haBankBalanceInfo.getCustName());
                payOutIntfHAToHAEn.setBankCode(haBankBalanceInfo.getBankCode());
                payOutIntfHAToHAEn.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                payOutIntfHAToHAEn.setPayTenantId(-1L);
                if (serviceType == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
                    payOutIntfHAToHAEn.setFeeType(OrderAccountConst.FEE_TYPE.FICTITIOUS_OIL_TYPE);
                }
                payOutIntfHAToHAEn.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
                payOutIntfHAToHAEn.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);// ????????????(?????????)
                payOutIntfHAToHAEn.setWaitBillingAmount(payOutIntfHAToHAEn.getTxnAmt());
                payOutIntfHAToHAEn.setAccountType(BUSINESS_RECEIVABLE_ACCOUNT);//?????????????????????????????????
                payOutIntfHAToHAEn.setRemark("????????????????????????");
                payOutIntfHAToHAEn.setReceivablesBankAccNo(haBankBalanceInfo.getAcctNo());
                payOutIntfHAToHAEn.setReceivablesBankAccName(haBankBalanceInfo.getCustName());
                payOutIntfHAToHAEn.setOilAffiliation(account.getOilAffiliation());
                iPayoutIntfService.doSavePayOutIntfHAToHAEn(payOutIntfHAToHAEn);
                // ????????????????????????????????????????????????
                iAccountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, tenantSysOperator.getOpUserId(),
                        tenantSysOperator.getOpName(), account, busiSubjectsRelList, soNbr, 0L, sysOperator.getOpName(), null, account.getSourceTenantId(),
                        null, "", null, account.getVehicleAffiliation(), loginInfo);
                //HA?????????HA?????? //??????????????????????????????????????????????????????
                PayoutIntf payOutIntfHAEnToEn = new PayoutIntf();
                payOutIntfHAEnToEn.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15????????????
                payOutIntfHAEnToEn.setObjId(Long.parseLong(billId));
                payOutIntfHAEnToEn.setBusiId(EnumConsts.PayInter.WITHDRAWALS_CODE);
                payOutIntfHAEnToEn.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);
                payOutIntfHAEnToEn.setXid(payOutIntfHAToHAEn.getId());
                payOutIntfHAEnToEn.setUserId(userId);
                //????????????????????????
                payOutIntfHAEnToEn.setUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                payOutIntfHAEnToEn.setPayUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                //????????????????????????
                payOutIntfHAEnToEn.setTxnAmt(pinganTxnAmt);// ??????????????????
                payOutIntfHAEnToEn.setCreateDate(LocalDateTime.now());
                payOutIntfHAEnToEn.setTenantId(-1L);
                payOutIntfHAEnToEn.setVerificationState(Integer.valueOf(OrderConsts.PayOutVerificationState.INIT));
                payOutIntfHAEnToEn.setWithdrawalsChannel(withdrawalsChannel);
                payOutIntfHAEnToEn.setIsDriver(OrderAccountConst.PAY_TYPE.SERVICE);//???????????????
                payOutIntfHAEnToEn.setPayType(OrderAccountConst.PAY_TYPE.HAEN);
                payOutIntfHAEnToEn.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
                payOutIntfHAEnToEn.setVehicleAffiliation(account.getVehicleAffiliation());
                payOutIntfHAEnToEn.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                if (serviceType == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
                    payOutIntfHAEnToEn.setFeeType(OrderAccountConst.FEE_TYPE.FICTITIOUS_OIL_TYPE);
                }
                payOutIntfHAEnToEn.setPayTenantId(account.getSourceTenantId());
                payOutIntfHAEnToEn.setPayAccNo(haBankBalanceInfo.getAcctNo());//HA??????
                payOutIntfHAEnToEn.setPayAccName(haBankBalanceInfo.getCustName());
                payOutIntfHAEnToEn.setPayBankAccNo(haBankBalanceInfo.getAcctNo());
                payOutIntfHAEnToEn.setPayBankAccName(haBankBalanceInfo.getCustName());
                payOutIntfHAEnToEn.setAccNo(bankBalanceInfo.getAcctNo());//??????????????????
                payOutIntfHAEnToEn.setAccName(bankBalanceInfo.getCustName());
                payOutIntfHAEnToEn.setBankCode(bankBalanceInfo.getBankCode());
                payOutIntfHAEnToEn.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
                payOutIntfHAEnToEn.setTxnType(OrderAccountConst.TXN_TYPE.PLATFORM_TXN_TYPE);// ????????????(?????????)
                payOutIntfHAEnToEn.setAccountType(BUSINESS_RECEIVABLE_ACCOUNT);//?????????????????????????????????
                payOutIntfHAEnToEn.setRemark("????????????????????????");
                payOutIntfHAEnToEn.setPayObjId(Long.valueOf(account.getVehicleAffiliation()));
                payOutIntfHAEnToEn.setOilAffiliation(account.getOilAffiliation());
                iPayoutIntfService.save(payOutIntfHAEnToEn);
                long haVirtoHaEn = pinganTxnAmt;
                for (ServiceMatchOrder ol : serviceMatchOrders) {
                    if (haVirtoHaEn <= 0L || ol.getAccountBalance() == null || ol.getAccountBalance() <= 0L) {
                        continue;
                    }
                    Long txnAmount = ol.getAccountBalance();
                    if (txnAmount > haVirtoHaEn) {
                        txnAmount = haVirtoHaEn;
                    }
                    haVirtoHaEn -= txnAmount;
                    ParametersNewDto dto = iOrderOilSourceService.setParametersNew(sysOperator.getOpUserId(), sysOperator.getBillId(),
                            EnumConsts.PayInter.WITHDRAWALS_CODE, 0L, txnAmount, account.getVehicleAffiliation(), "");
                    //?????????????????? ???????????????
                    dto.setPayoutIntf(payOutIntfHAEnToEn);
                    dto.setOrderId(ol.getOrderId());
                    dto.setFlowId(ol.getId());
                    dto.setTenantId(account.getSourceTenantId());
                    iOrderOilSourceService.busiToOrderNew(dto, busiSubjectsRelList, loginInfo);
                }
                haAmount += serviceMatchOrderSum;//?????????????????????
            }
        }

        return haAmount;
    }

    @Override
    public List<DirverxDto> getAcctRelUserTypeList(String bankAcctNo, String pinganAcctNo, Long userId) {
        if(org.apache.commons.lang.StringUtils.isBlank(bankAcctNo)&& org.apache.commons.lang.StringUtils.isBlank(pinganAcctNo)&&userId<0){
            throw new BusinessException("????????????");
        }
        List<DirverxDto> list = baseMapper.selectS(bankAcctNo,pinganAcctNo,userId);
        return list;
    }
}
