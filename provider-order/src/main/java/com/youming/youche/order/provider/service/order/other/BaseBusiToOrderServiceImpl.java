package com.youming.youche.order.provider.service.order.other;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.PinganBankInfoOutDto;
import com.youming.youche.order.dto.UserDataInfoDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.dto.order.PayReturnDto;
import com.youming.youche.order.dto.order.PayoutIntfDto;
import com.youming.youche.order.provider.mapper.order.AccountBankRelMapper;
import com.youming.youche.order.provider.utils.BaseBusiToOrder;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.PayOutIntfUtil;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0;
import static com.youming.youche.conts.EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1;
import static com.youming.youche.conts.EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH;
import static com.youming.youche.conts.OrderAccountConst.PAY_TYPE.HAEN;
import static com.youming.youche.conts.OrderAccountConst.PAY_TYPE.HAVIR;

/**
 * @author hzx
 * @date 2022/4/14 15:02
 */
@DubboService(version = "1.0.0")
public class BaseBusiToOrderServiceImpl implements IBaseBusiToOrderService {

    private static final Log log = LogFactory.getLog(BaseBusiToOrderServiceImpl.class);

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    PayOutIntfUtil payOutIntfUtil;

    @Resource
    IPayoutIntfService iPayoutIntfService;

    @Resource
    IBusiSubjectsRelService iBusiSubjectsRelService;

    @Resource
    IAccountDetailsService iAccountDetailsService;

    @Resource
    IPayoutOrderService iPayoutOrderService;

    @Resource
    IAccountBankRelService iAccountBankRelService;

    @Resource
    IOrderAccountService iOrderAccountService;

    @Resource
    AccountBankRelMapper accountBankRelMapper;

    @Override
    public void dealBusi(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int level, Boolean isOnline, String accessToken) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        UserDataInfo[] userDataInfoArr = dtoToBean(userDataInfoDtos);
        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrder();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf, orderAccounts, userDataInfoArr, loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf, userDataInfoArr, "out", loginInfo);
        UserDataInfo receivablesUserDataInfo = userDataInfoArr[1];
        if (isOnline && (payoutIntf.getIsDriver() == HAVIR
                || SysStaticDataEnum.QUICK_FLAG.IS_QUICK.equals(receivablesUserDataInfo.getQuickFlag()))) {//????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //?????????????????????????????????????????????
            this.doSavePayoutIntf(payoutIntfDto, level);
        }
    }

    @Override
    public void dealBusiTwo(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int level, Boolean isOnline, String accessToken) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        UserDataInfo[] userDataInfoArr = dtoToBean(userDataInfoDtos);
        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrderTwo();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf, orderAccounts, userDataInfoArr, loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf, userDataInfoArr, "out", loginInfo);
        UserDataInfo receivablesUserDataInfo = userDataInfoArr[1];
        if (isOnline && (payoutIntf.getIsDriver() == HAVIR
                || SysStaticDataEnum.QUICK_FLAG.IS_QUICK.equals(receivablesUserDataInfo.getQuickFlag()))) {//????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //?????????????????????????????????????????????
            this.doSavePayoutIntf(payoutIntfDto, level);
        }
    }

    @Override
    public void doSavePayoutIntf(PayoutIntfDto payoutIntfDto, int level) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        //20191023 ??????56k????????????????????????????????????300????????????????????????100????????????
        if (OrderAccountConst.TXN_TYPE.XS_TXN_TYPE.equals(payoutIntf.getTxnType()) && (payoutIntf.getTxnAmt() == null || payoutIntf.getTxnAmt().longValue() <= 0) && (payoutIntf.getAppendFreight() != null && payoutIntf.getAppendFreight().longValue() > 0)) {
            return;
        }
        PayoutIntf payoutIntf1 = payOutIntfUtil.createPayoutIntfForWithdraw(payoutIntf, level);
        if (payoutIntf1.getPayType() == HAVIR) {
            /**
             * ?????????????????????????????????????????????????????????
             */
            PayoutIntf dealWithdrawFlow = iPayoutIntfService.getPayOUtIntfByXid(payoutIntf.getId());
            if (dealWithdrawFlow != null) {
                return;
            }
            iPayoutIntfService.doSavePayOutIntfHAToHAEn(payoutIntf1);
        } else if (payoutIntf1.getPayType() == HAEN) {
            iPayoutIntfService.doSavePayOutIntfEnToEn(payoutIntf1);
            //????????????????????????????????????
            List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel balanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_AUTO, payoutIntf1.getTxnAmt());
            balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
            busiBalanceList.add(balanceSub);
            List<BusiSubjectsRel> balanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
            long soNbr = CommonUtil.createSoNbr();
            iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, payoutIntf1.getUserId(), payoutIntf1.getUserId(), payoutIntf1.getAccName(), balanceList, soNbr, 0L, payoutIntf1.getTenantId(), payoutIntf1.getUserType());
        }
        //??????????????????????????????????????????????????????
        else if (payoutIntf1.getPayType() == -1) {
            return;
        } else {
            iPayoutIntfService.doSavePayOutIntfVirToEn(payoutIntf1);
            //????????????????????????????????????
            List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel balanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.WITHDRAWALS_AUTO, payoutIntf1.getTxnAmt());
            balanceSub.setSubjectsType(EnumConsts.PayInter.FEE_OUT);
            busiBalanceList.add(balanceSub);
            List<BusiSubjectsRel> balanceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.WITHDRAWALS_CODE, busiBalanceList);
            long soNbr = CommonUtil.createSoNbr();
            iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.WITHDRAWALS_CODE, payoutIntf1.getUserId(), payoutIntf1.getUserId(), payoutIntf1.getAccName(), balanceList, soNbr, 0L, payoutIntf1.getTenantId(), payoutIntf1.getUserType());

        }
        if (payoutIntf1.getPayType() == HAVIR || payoutIntf1.getPayType() == HAEN || OIL_AND_ETC_TURN_CASH == payoutIntf.getBusiId().longValue()) {
//            log.info("??????payout_order????????????id["+payoutIntf.getId()+"],?????????["+payoutIntf1.getId()+"]");
            iPayoutOrderService.updPayoutOrder(payoutIntf1.getId(), payoutIntf.getId());
        }
    }

    @Override
    public List busiToOrder(Map<String, String> inParam, List<BusiSubjectsRel> rels) {
        long userId = DataFormat.getLongKey(inParam, "userId");
        List results = new ArrayList();
        if (userId == 0L) {
            throw new BusinessException("??????ID????????????!");
        }
        long businessId = DataFormat.getLongKey(inParam, "businessId");
        if (businessId == 0L) {
            throw new BusinessException("??????ID????????????!");
        }
        String billId = DataFormat.getStringKey(inParam, "billId");
        if (StringUtils.isEmpty(billId)) {
            throw new BusinessException("????????????????????????!");
        }
        String vehicleAffiliation = DataFormat.getStringKey(inParam, "vehicleAffiliation");
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("????????????????????????!");
        }
        long amount = DataFormat.getLongKey(inParam, "amount");

        return results;
    }

    @Override
    public void dealOABusi(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int level, boolean isOnline, String accessToken) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        UserDataInfo[] userDataInfoArr = dtoToBean(userDataInfoDtos);
        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrder();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //??????????????????????????????
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf, orderAccounts, userDataInfoArr, loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf, userDataInfoArr, "out", loginInfo);
        if (isOnline) {
            //?????????????????????????????????????????????
            doSavePayoutIntf(payoutIntfDto, level);
        }
    }

    @Override
    public void dealOABusiTwo(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int level, boolean isOnline, String accessToken) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        UserDataInfo[] userDataInfoArr = dtoToBean(userDataInfoDtos);
        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrderTwo();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //??????????????????????????????
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf, orderAccounts, userDataInfoArr, loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf, userDataInfoArr, "out", loginInfo);
        if (isOnline) {
            //?????????????????????????????????????????????
            doSavePayoutIntf(payoutIntfDto, level);
        }
    }

    @Override
    public void dealBusiOaLoanAvailable(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, String accessToken) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        UserDataInfo[] userDataInfoArr = dtoToBean(userDataInfoDtos);
        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrder();
        baseBusiToOrder.dealBusiOaLoanAvailable(payoutIntf, userDataInfoArr, loginUtils.get(accessToken), accessToken);
    }

    @Override
    public void dealBusi(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, String accessToken) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        UserDataInfo[] userDataInfoArr = dtoToBean(userDataInfoDtos);
        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrder();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf, orderAccounts, userDataInfoArr, loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf, userDataInfoArr, "out", loginInfo);
    }

    @Override
    public void dealBusiTwo(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, String accessToken) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        UserDataInfo[] userDataInfoArr = dtoToBean(userDataInfoDtos);
        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrderTwo();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
        baseBusiToOrder.doSaveAccountDetail(payoutIntf, orderAccounts, userDataInfoArr, loginInfo);
        baseBusiToOrder.doSaveOrderFundflow(payoutIntf, userDataInfoArr, "out", loginInfo);
    }

    @Override
    public void dealBusi(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int flg, String accessToken) {
        PayoutIntf payoutIntf = getPayoutIntf(payoutIntfDto);
        UserDataInfo[] userDataInfoArr = dtoToBean(userDataInfoDtos);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        BaseBusiToOrder baseBusiToOrder = payOutIntfUtil.newBaseBusiToOrder();
        if (flg == EnumConsts.PAY_OUT_OPER.ORDER) {
            OrderAccount[] orderAccounts = baseBusiToOrder.doUpdOrderAccount(payoutIntf);
            baseBusiToOrder.doSaveAccountDetail(payoutIntf, orderAccounts, userDataInfoArr, loginInfo);
        } else if (flg == EnumConsts.PAY_OUT_OPER.ACCOUNT) {
            baseBusiToOrder.doSaveOrderFundflow(payoutIntf, userDataInfoArr, "out", loginInfo);
            baseBusiToOrder.doUpdOrderLimit(payoutIntf);
        }
    }

    @Override
    public void doSavePayOutIntfForOA(PayoutIntfDto payoutIntfDto,String token) {
        PayoutIntf payoutIntf = new PayoutIntf();
        BeanUtil.copyProperties(payoutIntfDto, payoutIntf);
        iPayoutIntfService.doSavePayOutIntfForOA(payoutIntf,token);
    }

    @Override
    public PayReturnDto payMemberTransaction(long outUserId, long inUserId, long tranAmount, int outbankType, int inbankType, String thirdLogNo, boolean isException, String accountNoOut, String accountNoIn, Integer outUserType, Integer inUserType) {
        PayReturnDto payReturnOut = new PayReturnDto();
        try {
            BankBalanceInfo bankBalanceInfoOut = getBankBalanceToUserId(outUserId, outbankType, accountNoOut, outUserType);
            if (bankBalanceInfoOut == null || bankBalanceInfoOut.getAccId() == null || bankBalanceInfoOut.getAccId().equals("") || StringUtils.isBlank(bankBalanceInfoOut.getCustName())) {
                if (isException) {
                    throw new BusinessException("???????????????????????????????????????");
                } else {
                    payReturnOut.setReqCode(EnumConsts.PAY_RESULT_STS.payResultSts3);
                    payReturnOut.setReqMess("???????????????????????????????????????");
                    return payReturnOut;
                }
            }
            Double tranFee = Double.parseDouble((((double) tranAmount) / 100.0) + "");
            if (bankBalanceInfoOut.getBankBalance() <= 0 || bankBalanceInfoOut.getBankBalance() < tranFee) {
                if (isException) {
                    throw new BusinessException("????????????(" + bankBalanceInfoOut.getAccId() + ")???????????????");
                } else {
                    payReturnOut.setReqCode(EnumConsts.PAY_RESULT_STS.payResultSts2);
                    payReturnOut.setReqMess("????????????(" + bankBalanceInfoOut.getAccId() + ")???????????????");
                    return payReturnOut;
                }

            }
            BankBalanceInfo bankBalanceInfoIn = getBankBalanceToUserId(inUserId, inbankType, accountNoIn, inUserType);
            if (bankBalanceInfoIn == null || bankBalanceInfoIn.getAccId() == null || bankBalanceInfoIn.getAccId().equals("") || StringUtils.isBlank(bankBalanceInfoIn.getCustName())) {
                if (isException) {
                    throw new BusinessException("???????????????????????????????????????");
                } else {
                    payReturnOut.setReqCode(EnumConsts.PAY_RESULT_STS.payResultSts3);
                    payReturnOut.setReqMess("???????????????????????????????????????");
                    return payReturnOut;
                }
            }
            thirdLogNo = this.payMemberTransaction(inUserId, outUserId, bankBalanceInfoOut, bankBalanceInfoIn, tranFee, thirdLogNo, inUserType, outUserType);
            payReturnOut.setReqCode(EnumConsts.PAY_RESULT_STS.payResultSts0);
            payReturnOut.setThirdLogNo(thirdLogNo);
        } catch (SocketTimeoutException e) {
            payReturnOut.setReqCode(EnumConsts.PAY_RESULT_STS.payResultSts1);
            payReturnOut.setReqMess(e.getMessage());
            if (thirdLogNo.length() == 20) {//????????????????????????????????????????????????????????????????????????
                payReturnOut.setThirdLogNo(thirdLogNo);
            } else {
                payReturnOut.setThirdLogNo("");
            }
        } catch (Exception e) {
            if (isException) {
                throw new BusinessException(e.getMessage());
            } else {
                payReturnOut.setReqCode(EnumConsts.PAY_RESULT_STS.payResultSts4);
                payReturnOut.setReqMess(e.getMessage());
                if (thirdLogNo.length() == 20) {//????????????????????????????????????????????????????????????????????????
                    payReturnOut.setThirdLogNo(thirdLogNo);
                } else {
                    payReturnOut.setThirdLogNo("");
                }
            }
        }
        return payReturnOut;
    }

    @Override
    public BankBalanceInfo getBankBalanceToUserId(long userId, int bankType, String accountNo, Integer userType) {
        PinganBankInfoOutDto pinganBankInfoOut = iPayoutIntfService.getBindBankAc(userId, accountNo, userType);
        if (pinganBankInfoOut == null) {
            BankBalanceInfo bankBalanceInfo = new BankBalanceInfo();
            bankBalanceInfo.setAccId("");
            bankBalanceInfo.setBalance(0D);
            bankBalanceInfo.setCustName("");
            bankBalanceInfo.setThirdCustId("");
            bankBalanceInfo.setAcctNo("");
            bankBalanceInfo.setBankCode("");
            bankBalanceInfo.setBankBalance(0D);
            return bankBalanceInfo;
        }
        String corporatePinganAcctIdM = pinganBankInfoOut.getCorporatePinganAcctIdM();
        String corporatePinganAcctIdN = pinganBankInfoOut.getCorporatePinganAcctIdN();
        String privatePinganAcctIdM = pinganBankInfoOut.getPrivatePinganAcctIdM();
        String privatePinganAcctIdN = pinganBankInfoOut.getPrivatePinganAcctIdN();
        BankBalanceInfo bankBalanceInfo = new BankBalanceInfo();
        if (bankType == 1) {//1:????????????????????????
            if (corporatePinganAcctIdM != null && !corporatePinganAcctIdM.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(corporatePinganAcctIdM, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getCorporateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
            } else {
                bankBalanceInfo.setAccId("");
                bankBalanceInfo.setBalance(0D);
                bankBalanceInfo.setCustName("");
                bankBalanceInfo.setThirdCustId("");
                bankBalanceInfo.setAcctNo("");
                bankBalanceInfo.setBankBalance(0D);
            }
        } else if (bankType == 11) {//11:????????????????????????
            if (corporatePinganAcctIdN != null && !corporatePinganAcctIdN.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(corporatePinganAcctIdN, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getCorporateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
                bankBalanceInfo.setPayUnDoBalance(bankBalance.getPayUnDoBalance());
            } else {
                bankBalanceInfo.setAccId("");
                bankBalanceInfo.setBalance(0D);
                bankBalanceInfo.setCustName("");
                bankBalanceInfo.setThirdCustId("");
                bankBalanceInfo.setAcctNo("");
                bankBalanceInfo.setBankBalance(0D);
            }
        } else if (bankType == 2) {//2???????????????????????????+??????????????????
            if (privatePinganAcctIdM != null && !privatePinganAcctIdM.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(privatePinganAcctIdM, userId, bankType, userType);
                Double fee = bankBalance.getBalance() + getPcBalance(userId, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(fee);
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getPrivateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
            }
        } else if (bankType == 22) {//22????????????????????????
            if (privatePinganAcctIdN != null && !privatePinganAcctIdN.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(privatePinganAcctIdN, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getPrivateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
                bankBalanceInfo.setPayUnDoBalance(bankBalance.getPayUnDoBalance());
            }
        }
        if (bankBalanceInfo.getBalance() == null || bankBalanceInfo.getBalance() < 0) {
            bankBalanceInfo.setBalance(0D);
        }
        return bankBalanceInfo;
    }

    @Override
    public void doSavePayoutInfoExpansion(PayoutIntfDto payoutIntfDto,String token) {
        PayoutIntf payoutIntf = new PayoutIntf();
        BeanUtil.copyProperties(payoutIntfDto, payoutIntf);
        iPayoutIntfService.doSavePayoutInfoExpansion(payoutIntf,token);
    }

    @Override
    public void doSavePayPlatformServiceFee(PayoutIntfDto payoutIntfDto, String accessToken) {
        PayoutIntf payoutIntf = new PayoutIntf();
        BeanUtil.copyProperties(payoutIntfDto, payoutIntf);
        iPayoutIntfService.doSavePayPlatformServiceFee(payoutIntf, accessToken);
    }

    private UserDataInfo[] dtoToBean(UserDataInfoDto[] userDataInfoDtos) {
        //??????????????????
        UserDataInfoDto payUserDataInfoDto = userDataInfoDtos[0];
        //??????????????????
        UserDataInfoDto receivablesUserDataInfoDto = userDataInfoDtos[1];

        UserDataInfo[] userDataInfoArr = new UserDataInfo[2];
        UserDataInfo payUserDataInfo = new UserDataInfo();
        UserDataInfo receivablesUserDataInfo = new UserDataInfo();

        BeanUtil.copyProperties(payUserDataInfoDto, payUserDataInfo);
        BeanUtil.copyProperties(receivablesUserDataInfoDto, receivablesUserDataInfo);

        userDataInfoArr[0] = payUserDataInfo;
        userDataInfoArr[1] = receivablesUserDataInfo;

        return userDataInfoArr;
    }

    /**
     * ??????redis??????????????????(???????????????????????????300???????????????)
     * ????????????balanceCacheWwitch
     */
    private BankBalanceInfo getBankBalance(String custAcctId, long userId, int bankType, Integer userType) {
        BankBalanceInfo bankBalanceInfo = null;
        if (isCheckCacheBalance()) {
            String balanceSTR = redisUtil.get(EnumConsts.RemoteCache.PINGAN_BALANCE + custAcctId).toString();
            if (StringUtils.isNotBlank(balanceSTR)) {
                AccountBankRel accountBankRel = iAccountBankRelService.getAcctNo(custAcctId);
                if (accountBankRel == null) {
                    throw new BusinessException("????????????????????????");
                }
                bankBalanceInfo = new BankBalanceInfo();
                bankBalanceInfo.setCustName(accountBankRel.getAcctName());
                if (custAcctId.equals(accountBankRel.getPinganCollectAcctId())) {
                    bankBalanceInfo.setThirdCustId(accountBankRel.getPinganMoutId());
                } else {
                    bankBalanceInfo.setThirdCustId(accountBankRel.getPinganNoutId());
                }
                bankBalanceInfo.setAccId(custAcctId);
                long fee = iPayoutIntfService.getPayoutIntfBalanceForRedis(userId, bankType, new String[]{OrderAccountConst.TXN_TYPE.XS_TXN_TYPE}, custAcctId, -1, -1);
                long balanceLong = CommonUtil.multiply(balanceSTR);
                long lockBalance = iPayoutIntfService.getAccountLockSum(custAcctId, -1);//????????????
                long payUnDoBalance = iPayoutIntfService.getPayUnDoPayAccount(custAcctId);//????????????????????????????????????????????????????????????
                bankBalanceInfo.setBalance(new Double(CommonUtil.divide(balanceLong - fee - lockBalance - payUnDoBalance)));//????????????????????????
                bankBalanceInfo.setBankBalance(new Double(CommonUtil.divide(balanceLong - lockBalance - payUnDoBalance)));//?????????????????????????????????,??????????????????????????????
                bankBalanceInfo.setPayUnDoBalance(new Double(CommonUtil.divide(payUnDoBalance)));
            } else {
                bankBalanceInfo = this.getBalance(custAcctId, userId, bankType, userType);
            }
        } else {
            bankBalanceInfo = this.getBalance(custAcctId, userId, bankType, userType);
        }
        return bankBalanceInfo;
    }

    public boolean isCheckCacheBalance() {
        SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.SysCfg.BALANCE_CACHE_SWITCH));
        if (sysCfg != null && !sysCfg.getCfgValue().equals("")) {
            String codeValue = sysCfg.getCfgValue();
            if (SysStaticDataEnum.BALANCE_CACHE_SWITCH.open.equals(codeValue)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ?????????????????????????????????????????????
     */
    private BankBalanceInfo getBalance(String custAcctId, long userId, int bankType, Integer userType) {
        Double balance = 0D;
        Double bankBalance = 0D;
        BankBalanceInfo bankBalanceInfo = new BankBalanceInfo();
        Map parmaKeyDict = new HashMap();//????????????????????????????????????????????????
        parmaKeyDict.put("TranFunc", "6010");   //????????????
        parmaKeyDict.put("SelectFlag", "2");  //???????????? 2???????????????????????? 3??????????????????
        parmaKeyDict.put("CustAcctId", custAcctId);
        parmaKeyDict.put("PageNum", "1");  //???????????? ?????????
        parmaKeyDict.put("Reserve", ""); //?????????
//        Map response = PinganReqUtil.SendTranMessage(parmaKeyDict); todo
        Map response = Maps.newHashMap();
        if (!"000000".equals(response.get("RspCode"))) {
            throw new BusinessException((String) response.get("RspMsg"));
        } else {
            String bodyMsg = (String) response.get("BodyMsg");
            List reqList = (List) response.get("ArrayContent");
            if (reqList.size() > 0) {
                Map reqMap = (Map) reqList.get(0);
                balance = Double.parseDouble(reqMap.get("TotalTranOutAmount") + "");
                bankBalance = balance;
                long fee = iPayoutIntfService.getPayoutIntfBalance(userId, bankType, custAcctId, -1, -1);//????????????
                long lockBalance = iPayoutIntfService.getAccountLockSum(custAcctId, -1);//????????????
                long payUnDoBalance = iPayoutIntfService.getPayUnDoPayAccount(custAcctId);//????????????????????????????????????????????????????????????
                long balanceLong = CommonUtil.multiply(balance + "");
                balanceLong -= fee;
                balanceLong -= lockBalance;
                balanceLong -= payUnDoBalance;
                balance = new Double(CommonUtil.divide(balanceLong - fee));
                bankBalanceInfo.setPayUnDoBalance(new Double(CommonUtil.divide(payUnDoBalance)));

                String CustName = DataFormat.getStringKey(reqMap, "CustName");
                bankBalanceInfo.setBalance(balance);
                bankBalanceInfo.setCustName(CustName);
                bankBalanceInfo.setThirdCustId(reqMap.get("ThirdCustId") + "");
                bankBalanceInfo.setAccId(reqMap.get("CustAcctId") + "");
                bankBalanceInfo.setBankBalance(bankBalance);
                if (isCheckCacheBalance()) {
                    redisUtil.setex(EnumConsts.RemoteCache.PINGAN_BALANCE + custAcctId, 24 * 60 * 60, bankBalanceInfo.getBankBalance().longValue());
                }
            }
        }
        return bankBalanceInfo;
    }

    private String payMemberTransaction(long inUserId, long outUserId, BankBalanceInfo bankBalanceInfoOut, BankBalanceInfo bankBalanceInfoIn, Double tranAmount, String thirdLogNo, Integer inUserType, Integer outUserType) throws Exception {
        Map parmaKeyDict = new HashMap();//????????????????????????????????????????????????
        parmaKeyDict.put("TranFunc", "6034");   //????????????
        parmaKeyDict.put("FuncFlag", "9");
        parmaKeyDict.put("OutCustAcctId", bankBalanceInfoOut.getAccId());
        parmaKeyDict.put("OutThirdCustId", bankBalanceInfoOut.getThirdCustId());
        parmaKeyDict.put("OutCustName", bankBalanceInfoOut.getCustName());
        parmaKeyDict.put("InCustAcctId", bankBalanceInfoIn.getAccId());
        parmaKeyDict.put("InThirdCustId", bankBalanceInfoIn.getThirdCustId());
        parmaKeyDict.put("InCustName", bankBalanceInfoIn.getCustName());
        parmaKeyDict.put("TranAmount", String.valueOf(tranAmount + ""));
        parmaKeyDict.put("TranFee", "0.0");
        parmaKeyDict.put("TranType", "01");
        parmaKeyDict.put("CcyCode", "RMB");
        parmaKeyDict.put("relId", thirdLogNo + "");
        parmaKeyDict.put("ThirdLogNo", thirdLogNo + "");
//        Map response = PinganReqUtil.SendTranMessage(parmaKeyDict);
        Map response = Maps.newHashMap();
        if (!redisUtil.equals(response.get("RspCode"))) {
            throw new BusinessException((String) response.get("RspMsg"));
        }
        String accountNoIn = bankBalanceInfoIn.getAccId();
        String accountNoOut = bankBalanceInfoOut.getAccId();
        //???????????????????????????????????????????????????????????????????????????????????????
        if (isCheckCacheBalance()) {
            try {
                int inBankType = EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT;//????????????
                if (bankBalanceInfoIn.getBankType() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) {//??????
                    inBankType = EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT;//??????????????????
                }
                int outBankType = EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT;//????????????
                if (bankBalanceInfoOut.getBankType() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1) {//??????
                    outBankType = EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT;//????????????
                }

                //????????????????????????????????????????????????????????????????????????
                String balanceSTRIn = redisUtil.get(EnumConsts.RemoteCache.PINGAN_BALANCE + accountNoIn).toString();
                String balanceSTROut = redisUtil.get(EnumConsts.RemoteCache.PINGAN_BALANCE + accountNoOut).toString();
                if (StringUtils.isBlank(balanceSTRIn)) {
                    log.info("??????redis????????????-" + accountNoIn + "?????????????????????????????????redis??????");
                    this.getBalance(accountNoIn, inUserId, inBankType, inUserType);
                    balanceSTRIn = redisUtil.get(EnumConsts.RemoteCache.PINGAN_BALANCE + accountNoIn).toString();
                    log.info("???????????????????????????redis????????????-" + accountNoIn + "???????????????" + balanceSTRIn + "???");
                } else {
                    log.info("??????redis????????????-" + accountNoIn + "???????????????" + balanceSTRIn + "???");
                    long balanceSTRInLong = (long) (CommonUtil.multiply(balanceSTRIn) + CommonUtil.multiply(tranAmount + ""));
                    log.info("?????????????????????????????????-" + accountNoIn + "???????????????" + CommonUtil.divide(balanceSTRInLong) + "???");
                    redisUtil.setex(EnumConsts.RemoteCache.PINGAN_BALANCE + accountNoIn, 24 * 60 * 60, balanceSTRInLong);
                }
                if (StringUtils.isBlank(balanceSTROut)) {
                    log.info("??????redis????????????-" + accountNoOut + "?????????????????????????????????redis??????");
                    this.getBalance(accountNoOut, outUserId, outBankType, outUserType);
                    balanceSTROut = redisUtil.get(EnumConsts.RemoteCache.PINGAN_BALANCE + accountNoOut).toString();
                    log.info("???????????????????????????redis????????????-" + accountNoOut + "???????????????" + balanceSTROut + "???");
                } else {
                    log.info("??????redis????????????-" + accountNoOut + "???????????????" + balanceSTROut + "???");
                    long balanceSTROutLong = CommonUtil.multiply(balanceSTROut) - CommonUtil.multiply(tranAmount + "");
                    log.info("?????????????????????????????????-" + accountNoOut + "???????????????" + CommonUtil.divide(balanceSTROutLong) + "???");
                    redisUtil.setex(EnumConsts.RemoteCache.PINGAN_BALANCE + accountNoOut, 24 * 60 * 60, balanceSTROutLong);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //?????????????????????redis??????
                redisUtil.del(EnumConsts.RemoteCache.PINGAN_BALANCE + accountNoIn);
                redisUtil.del(EnumConsts.RemoteCache.PINGAN_BALANCE + accountNoOut);
            }
        }
        return parmaKeyDict.get("ThirdLogNo") + "";
    }

    public Double getPcBalance(long userId, Integer userType) {
        long fee = 0;
        List<OrderAccount> orderAccountList = iOrderAccountService.getOrderAccountQuey(userId, null, 0L, userType);
        if (orderAccountList != null && orderAccountList.size() > 0) {
            for (OrderAccount orderAccount : orderAccountList) {
                if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(orderAccount.getVehicleAffiliation())
                        && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(orderAccount.getVehicleAffiliation())) {
                    //??????????????????????????????
                    fee += orderAccount.getBalance();
                }
            }
        }
        return Double.parseDouble(String.valueOf(fee / 100.0));
    }

    @Override
    public BankBalanceInfo getBankBalanceToUserIdNo(long userId, int bankType, String accountNo, String accountName, String acctNo, int bankType1, Integer userType) {
        PinganBankInfoOutDto pinganBankInfoOut = this.getBindBankAc(userId, accountNo, accountName, acctNo, bankType1, userType);
        if (pinganBankInfoOut == null) {
            BankBalanceInfo bankBalanceInfo = new BankBalanceInfo();
            bankBalanceInfo.setAccId("");
            bankBalanceInfo.setBalance(0D);
            bankBalanceInfo.setCustName("");
            bankBalanceInfo.setThirdCustId("");
            bankBalanceInfo.setAcctNo("");
            bankBalanceInfo.setBankCode("");
            bankBalanceInfo.setBankBalance(0D);
            return bankBalanceInfo;
        }
        String corporatePinganAcctIdM = pinganBankInfoOut.getCorporatePinganAcctIdM();
        String corporatePinganAcctIdN = pinganBankInfoOut.getCorporatePinganAcctIdN();
        String privatePinganAcctIdM = pinganBankInfoOut.getPrivatePinganAcctIdM();
        String privatePinganAcctIdN = pinganBankInfoOut.getPrivatePinganAcctIdN();
        BankBalanceInfo bankBalanceInfo = new BankBalanceInfo();
        bankBalanceInfo.setIsDefaultAcct(pinganBankInfoOut.getIsDefaultAcct());
        if (bankType == 1) {//1:????????????????????????
            if (corporatePinganAcctIdM != null && !corporatePinganAcctIdM.equals("")) {
                BankBalanceInfo bankBalance = this.getBankBalance(corporatePinganAcctIdM, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getCorporateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
            } else {
                bankBalanceInfo.setAccId("");
                bankBalanceInfo.setBalance(0D);
                bankBalanceInfo.setCustName("");
                bankBalanceInfo.setThirdCustId("");
                bankBalanceInfo.setAcctNo("");
                bankBalanceInfo.setBankCode("");
                bankBalanceInfo.setBankBalance(0D);
            }
        } else if (bankType == 11) {//11:????????????????????????
            if (corporatePinganAcctIdN != null && !corporatePinganAcctIdN.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(corporatePinganAcctIdN, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getCorporateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
                bankBalanceInfo.setPayUnDoBalance(bankBalance.getPayUnDoBalance());
            } else {
                bankBalanceInfo.setAccId("");
                bankBalanceInfo.setBalance(0D);
                bankBalanceInfo.setCustName("");
                bankBalanceInfo.setThirdCustId("");
                bankBalanceInfo.setAcctNo("");
                bankBalanceInfo.setBankCode("");
                bankBalanceInfo.setBankBalance(0D);
            }
        } else if (bankType == 2) {//2???????????????????????????
            if (privatePinganAcctIdM != null && !privatePinganAcctIdM.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(privatePinganAcctIdM, userId, bankType, userType);
                Double fee = bankBalance.getBalance();
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(fee);
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getPrivateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
            }
        } else if (bankType == 22) {//22????????????????????????
            if (privatePinganAcctIdN != null && !privatePinganAcctIdN.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(privatePinganAcctIdN, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getPrivateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
                bankBalanceInfo.setPayUnDoBalance(bankBalance.getPayUnDoBalance());
            }
        }
        if (bankBalanceInfo.getBalance() == null || bankBalanceInfo.getBalance() < 0) {
            bankBalanceInfo.setBalance(0D);
        }
        return bankBalanceInfo;
    }

    /**
     * PayoutIntfDto payoutIntfDto -> PayoutIntf payoutIntf
     */
    private PayoutIntf getPayoutIntf(PayoutIntfDto payoutIntfDto) {
        PayoutIntf payoutIntf = new PayoutIntf();
        BeanUtil.copyProperties(payoutIntfDto, payoutIntf);
        return payoutIntf;
    }

    private PinganBankInfoOutDto getBindBankAc(long userId, String accountNo, String accountName, String acctNo, int bankType, Integer userType) {
        return this.getPinganBankInfoOut(userId, accountNo, accountName, acctNo, bankType, userType);
    }

    public PinganBankInfoOutDto getPinganBankInfoOut(Long userId, String accountNo, String accountName, String acctNo, int bankType, Integer userType) {
        List<AccountBankRel> accountBankRelList = accountBankRelMapper.getBankInfo(userId, accountName, acctNo, bankType, userType);
        PinganBankInfoOutDto pinganBankInfoOut = new PinganBankInfoOutDto();
        if (accountBankRelList != null && !accountBankRelList.isEmpty()) {
            if (StringUtils.isNotBlank(accountNo)) {
                pinganBankInfoOut = this.getPinganBankInfoOutByNo(accountNo, accountBankRelList);
            }
        }
        return pinganBankInfoOut;
    }

    public PinganBankInfoOutDto getPinganBankInfoOutByNo(String accountNo, List<AccountBankRel> accountBankRelList) {
        PinganBankInfoOutDto pinganBankInfoOut = new PinganBankInfoOutDto();
        for (AccountBankRel o : accountBankRelList) {
            if (accountNo.equals(o.getPinganCollectAcctId()) || accountNo.equals(o.getPinganPayAcctId())) {
                pinganBankInfoOut.setIsDefaultAcct(o.getIsDefaultAcct());
                if (o.getBankType().intValue() == BANK_TYPE_0) {
                    pinganBankInfoOut.setPrivatePinganAcctIdM(o.getPinganCollectAcctId());
                    pinganBankInfoOut.setPrivatePinganAcctIdN(o.getPinganPayAcctId());
                    pinganBankInfoOut.setPrivateAcctName(o.getAcctName());
                    pinganBankInfoOut.setPrivateAcctNo(o.getAcctNo());
                    pinganBankInfoOut.setPrivateBankCode(o.getBankName());
                } else if (o.getBankType().intValue() == BANK_TYPE_1) {
                    pinganBankInfoOut.setCorporatePinganAcctIdM(o.getPinganCollectAcctId());
                    pinganBankInfoOut.setCorporatePinganAcctIdN(o.getPinganPayAcctId());
                    pinganBankInfoOut.setCorporateAcctName(o.getAcctName());
                    pinganBankInfoOut.setCorporateAcctNo(o.getAcctNo());
                    pinganBankInfoOut.setCorporateBankCode(o.getBankName());
                } else if (o.getBankType().intValue() == EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_SERVICE) {
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

    @Override
    public BankBalanceInfo getBankBalanceToUserIdNo(long userId, int bankType, String accountNo, Integer userType) {
        PinganBankInfoOutDto pinganBankInfoOut = iPayoutIntfService.getBindBankAc(userId, accountNo, userType);
        if (pinganBankInfoOut == null) {
            BankBalanceInfo bankBalanceInfo = new BankBalanceInfo();
            bankBalanceInfo.setAccId("");
            bankBalanceInfo.setBalance(0D);
            bankBalanceInfo.setCustName("");
            bankBalanceInfo.setThirdCustId("");
            bankBalanceInfo.setAcctNo("");
            bankBalanceInfo.setBankCode("");
            bankBalanceInfo.setBankBalance(0D);
            return bankBalanceInfo;
        }
        String corporatePinganAcctIdM = pinganBankInfoOut.getCorporatePinganAcctIdM();
        String corporatePinganAcctIdN = pinganBankInfoOut.getCorporatePinganAcctIdN();
        String privatePinganAcctIdM = pinganBankInfoOut.getPrivatePinganAcctIdM();
        String privatePinganAcctIdN = pinganBankInfoOut.getPrivatePinganAcctIdN();
        BankBalanceInfo bankBalanceInfo = new BankBalanceInfo();
        bankBalanceInfo.setIsDefaultAcct(pinganBankInfoOut.getIsDefaultAcct());
        if (bankType == 1) {//1:????????????????????????
            if (corporatePinganAcctIdM != null && !corporatePinganAcctIdM.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(corporatePinganAcctIdM, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getCorporateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
            } else {
                bankBalanceInfo.setAccId("");
                bankBalanceInfo.setBalance(0D);
                bankBalanceInfo.setCustName("");
                bankBalanceInfo.setThirdCustId("");
                bankBalanceInfo.setAcctNo("");
                bankBalanceInfo.setBankCode("");
                bankBalanceInfo.setBankBalance(0D);
            }
        } else if (bankType == 11) {//11:????????????????????????
            if (corporatePinganAcctIdN != null && !corporatePinganAcctIdN.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(corporatePinganAcctIdN, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getCorporateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getCorporateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
                bankBalanceInfo.setPayUnDoBalance(bankBalance.getPayUnDoBalance());
            } else {
                bankBalanceInfo.setAccId("");
                bankBalanceInfo.setBalance(0D);
                bankBalanceInfo.setCustName("");
                bankBalanceInfo.setThirdCustId("");
                bankBalanceInfo.setAcctNo("");
                bankBalanceInfo.setBankCode("");
                bankBalanceInfo.setBankBalance(0D);
            }
        } else if (bankType == 2) {//2???????????????????????????
            if (privatePinganAcctIdM != null && !privatePinganAcctIdM.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(privatePinganAcctIdM, userId, bankType, userType);
                Double fee = bankBalance.getBalance();
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(fee);
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getPrivateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
            }
        } else if (bankType == 22) {//22????????????????????????
            if (privatePinganAcctIdN != null && !privatePinganAcctIdN.equals("")) {
                BankBalanceInfo bankBalance = getBankBalance(privatePinganAcctIdN, userId, bankType, userType);
                bankBalanceInfo.setAccId(bankBalance.getAccId());
                bankBalanceInfo.setBalance(bankBalance.getBalance());
                bankBalanceInfo.setCustName(bankBalance.getCustName());
                bankBalanceInfo.setThirdCustId(bankBalance.getThirdCustId());
                bankBalanceInfo.setAcctNo(pinganBankInfoOut.getPrivateAcctNo());
                bankBalanceInfo.setBankCode(pinganBankInfoOut.getPrivateBankCode());
                bankBalanceInfo.setBankBalance(bankBalance.getBankBalance());
                bankBalanceInfo.setPayUnDoBalance(bankBalance.getPayUnDoBalance());
            }
        }
        if (bankBalanceInfo.getBalance() == null || bankBalanceInfo.getBalance() < 0) {
            bankBalanceInfo.setBalance(0D);
        }
        return bankBalanceInfo;
    }
}
