package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.capital.api.IOilAccountService;
import com.youming.youche.capital.domain.TenantServiceRel;
import com.youming.youche.capital.vo.TenantServiceVo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.dto.order.OrderMainReportDto;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.api.service.IServiceInfoService;
import com.youming.youche.order.domain.AgentServiceInfo;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.AgentServiceDto;
import com.youming.youche.order.dto.OilRechargeAccountDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.PingAnBalanceDto;
import com.youming.youche.order.dto.order.AccountBankRelDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsOutDto;
import com.youming.youche.order.provider.mapper.order.OilRechargeAccountMapper;
import com.youming.youche.order.provider.mapper.order.PayoutIntfMapper;
import com.youming.youche.order.provider.utils.AcUtil;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.vo.OilRechargeAccountVo;
import com.youming.youche.order.vo.OrderAccountOutVo;

import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BUSI_TYPE.BUSI_TYPE2;
import static com.youming.youche.conts.OrderAccountConst.COMMON_KEY.tenantId;
import static com.youming.youche.conts.OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE3;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE5;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE6;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE7;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6;
import static com.youming.youche.order.constant.BaseConstant.DEFAULT_WARN_BLANCE;
import static com.youming.youche.order.constant.BaseConstant.OIL_RECHARGE_BUSI_TYPE;


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
public class OilRechargeAccountServiceImpl extends BaseServiceImpl<OilRechargeAccountMapper, OilRechargeAccount> implements IOilRechargeAccountService {

    private static final Logger log = LoggerFactory.getLogger(OilRechargeAccountServiceImpl.class);
    @Resource
    private IOrderAccountService orderAccountService;
    @Resource
    private IAccountBankRelService accountBankRelService1;
    @Autowired
    private IOilRechargeAccountDetailsService oilRechargeAccountDetailsService;
    @Autowired
    private IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;
    @Autowired
    private IBillInfoReceiveRelService billInfoReceiveRelService;
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @Resource
    private ReadisUtil readisUtil;
    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @Autowired
    private ISubjectsInfoService subjectsInfoService;
    @Autowired
    private IOilRechargeAccountFlowService oilRechargeAccountFlowService;
    @Autowired
    private IOilRechargeAccountDetailsFlowExtService oilRechargeAccountDetailsFlowExtService;
    @Autowired
    private IOilSourceRecordService oilSourceRecordService;
    @Autowired
    private IOilRechargeAccountService oilRechargeAccountService;
    @Autowired
    private IOrderFeeExtService orderFeeExtService;
    @Autowired
    private IOrderFeeExtHService orderFeeExtHService;
    @Autowired
    private IBillPlatformService billPlatformService;
    @Autowired
    private IOilRecord56kService oilRecord56kService;
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;
    @Resource
    LoginUtils loginUtils;
    @DubboReference(version = "1.0.0")
    IOilAccountService iOilAccountService;
    @Resource
    IPinganLockInfoService iPinganLockInfoService;
    @Resource
    IOrderAccountService iOrderAccountService;
    @Resource
    IAccountBankRelService iAccountBankRelService;
    @Resource
    PayoutIntfMapper payoutIntfMapper;
    @Resource
    IServiceInfoService iServiceInfoService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    IBillSettingService iBillSettingService;
    @Resource
    IBillPlatformService iBillPlatformService;
    @Resource
    IOilRechargeAccountDetailsService iOilRechargeAccountDetailsServicel;
    @Lazy
    @Resource
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @Resource
    IAccountDetailsService iAccountDetailsService;

    @Resource
    IPayoutIntfService iPayoutIntfService;
    @Override
    public OilRechargeAccount getOilRechargeAccount(Long userId) {
        LambdaQueryWrapper<OilRechargeAccount> lambdaQueryWrapper=new QueryWrapper<OilRechargeAccount>().lambda();
        lambdaQueryWrapper.eq(OilRechargeAccount::getUserId,userId);
        return this.getOne(lambdaQueryWrapper);
    }

    @Override
    public OilRechargeAccountDto querOilBalanceForOilAccountType(Long excludeOrderId,LoginInfo user) {
        String billLookUp = null;
        BillInfoReceiveRel billInfoReceiveRel= billInfoReceiveRelService.getDefaultBillInfoByTenantId(user.getTenantId());
        if(billInfoReceiveRel !=null && billInfoReceiveRel.getBillInfo() !=null
                &&StringUtils.isNotBlank(billInfoReceiveRel.getBillInfo().getBillLookUp())) {
            billLookUp = billInfoReceiveRel.getBillInfo().getBillLookUp();
        }
        //????????????????????????

        Long userId = sysTenantDefService.getSysTenantDef(user.getTenantId()).getAdminUser();
        OilRechargeAccountDto oilRechargeAccount = this.getOilRechargeAccount(userId, user.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, billLookUp, user);
        //????????????
        Long nonDeductOilBalance =oilRechargeAccount.getNonDeductOilBalance();//??????????????????
        oilRechargeAccount.setNonDeductOilBalance(nonDeductOilBalance == null || nonDeductOilBalance<0?0:nonDeductOilBalance);

        //????????????
        Long billLookUpBalance=oilRechargeAccount.getBillLookUpBalance();//???????????????
        oilRechargeAccount.setDeductOilBalance(billLookUpBalance == null || billLookUpBalance<0?0:billLookUpBalance);
        //???????????????
        Long custOilBalance =oilRechargeAccount.getCustOilBalance();//??????????????????=?????????+?????????+?????????
        custOilBalance = custOilBalance == null || custOilBalance<0?0:custOilBalance;
        Long rebateOilBalance =oilRechargeAccount.getRebateOilBalance() == null || oilRechargeAccount.getRebateOilBalance()<0
                ?0:oilRechargeAccount.getRebateOilBalance();
        Long transferOilBalance =oilRechargeAccount.getTransferOilBalance() == null || oilRechargeAccount.getTransferOilBalance()<0
                ?0:oilRechargeAccount.getTransferOilBalance();
        oilRechargeAccount.setCustOilBalance(custOilBalance);
        oilRechargeAccount.setRebateOilBalance(rebateOilBalance);
        oilRechargeAccount.setTransferOilBalance(transferOilBalance);
        return oilRechargeAccount;
    }

    @Override
    public void insertOilAccount(Long userId, Map accountMap, Long turnAmount, String orderNum,
                                 Integer busiType, Long batchId, Long subjectsId, String busiCode,
                                 Map detailsFlows,LoginInfo user) {
        if (accountMap.isEmpty()) {
            throw new BusinessException("????????????Id??????");
        }
        if (busiType == -1) {
            throw new BusinessException("?????????????????????");
        }
        OilRechargeAccount oilRechargeAccount = this.getOilRechargeAccount(userId);
        if (oilRechargeAccount == null) {
            throw new BusinessException("?????????????????????");
        }
        String busiName= readisUtil.getSysStaticData(OIL_RECHARGE_BUSI_TYPE,busiType.toString()).getCodeName();
        Iterator<Map.Entry<Integer, Integer>> entries = accountMap.entrySet().iterator();
        Long turnAmountLong= Math.abs(turnAmount);
        String pinganAccId = "";//?????????????????????????????????set???????????????
        while (entries.hasNext()) {
            Map.Entry<Integer, Integer> entry = entries.next();
            String key = entry.getKey() + "";
            String value = entry.getValue() + "";

            long relId = Long.valueOf(key);
            long dealAmount = Long.valueOf(value);
            long valueLong = Math.abs(dealAmount);
            OilRechargeAccountDetails oilRechargeAccountDetails = oilRechargeAccountDetailsService.getById(relId);
            if (oilRechargeAccountDetails == null) {
                throw new BusinessException("????????????????????????[relId]:" + relId);
            }
//			if(busiType!=OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE5&&turnAmountLong>0L){
//				oilRechargeAccountDetails.setAccountBalance(oilRechargeAccountDetails.getAccountBalance() == null ? 0L : oilRechargeAccountDetails.getAccountBalance() + dealAmount);
//			}
            if(oilRechargeAccountDetails.getAccountBalance()<0L){
                throw new BusinessException("??????????????????????????????");
            }

            Iterator<Map.Entry<Integer, Integer>> entriesDetailsFlows = detailsFlows.entrySet().iterator();
            boolean isTooLong = detailsFlows.size()>2;//????????????5?????????????????????????????????????????????????????????
            Map otherMap = new HashMap();
            while (entriesDetailsFlows.hasNext()) {
                Map.Entry<Integer, Integer> entryDetailsFlow = entriesDetailsFlows.next();
                long flowId = Long.valueOf(entryDetailsFlow.getKey() + "");
                long matchAmount = Long.valueOf(entryDetailsFlow.getValue() + "");
                OilRechargeAccountDetailsFlow matchDetailsFlow = oilRechargeAccountDetailsFlowService.getById(flowId);
                if(matchDetailsFlow!=null&&matchDetailsFlow.getRelId().longValue()==Long.valueOf(key)){
                    otherMap.put(flowId, matchAmount);
                }
            }
            OilRechargeAccountDetailsFlow detailsFlow = new OilRechargeAccountDetailsFlow();
            detailsFlow.setBatchId(batchId);
            detailsFlow.setRelId(relId);
            detailsFlow.setAccountType(oilRechargeAccountDetails.getAccountType());
            detailsFlow.setPinganAccId(oilRechargeAccountDetails.getPinganAccId());
            detailsFlow.setAmount(dealAmount);
            log.error("???????????????"+busiCode+"???????????????????????????"+otherMap.toString());
            if(!otherMap.isEmpty()) {
                detailsFlow.setOtherBatchId(isTooLong?"???????????????????????????????????????????????????OilRechargeAccountDetailsFlowExt":otherMap.toString());
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(oilRechargeAccountDetails.getPinganAccId())) {
                pinganAccId=oilRechargeAccountDetails.getPinganAccId();
                AccountBankRel accountBankRel = accountBankRelService1.getAcctNo(pinganAccId);
                detailsFlow.setBankAccName(accountBankRel.getAcctName());
            }

            if (busiType == BUSI_TYPE2) {
                // ???????????????????????????????????????????????????????????????????????????
                detailsFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE0);
            }
            detailsFlow.setSubjectsId(subjectsId);
            if (subjectsId > 0L) {
                SubjectsInfo subjects = subjectsInfoService.getSubjectsInfo(subjectsId, -1L);
                if (subjects != null) {
                    detailsFlow.setRemark(subjects.getSubjectsName());
                }
            }
            if(busiType==BUSI_TYPE3){//??????
                if(oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE4){//???????????????????????????

                }else{//???????????????????????????????????????????????????????????????
                    oilRechargeAccountDetails.setDistributedAmount(oilRechargeAccountDetails.getDistributedAmount() == null ? 0L : oilRechargeAccountDetails.getDistributedAmount() + valueLong);
                    oilRechargeAccountDetails.setUnUseredBalance(oilRechargeAccountDetails.getUnUseredBalance() == null ? 0L : oilRechargeAccountDetails.getUnUseredBalance() + valueLong);
                }
            }

            if(busiType==BUSI_TYPE5){//????????????
                if(oilRechargeAccountDetails.getSourceType() != SOURCE_TYPE4
                        &&oilRechargeAccountDetails.getAccountType()== EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1){//?????????????????????????????????
                    //???????????????????????????????????????????????????????????????
                    oilRechargeAccountDetails.setUnUseredBalance(oilRechargeAccountDetails.getUnUseredBalance() == null ? 0L : oilRechargeAccountDetails.getUnUseredBalance() - valueLong);
                }
                if(oilRechargeAccountDetails.getAccountType()== EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1){
                    detailsFlow.setAmount(0L);//?????????????????????????????????????????????
                }
            }
            if(busiType==BUSI_TYPE7){//????????????
                oilRechargeAccount.setUnRepaymentBalance(oilRechargeAccount.getUnRepaymentBalance()-valueLong);
            }
            detailsFlow.setBusiCode(busiCode);
            oilRechargeAccountDetails.setUpdateTime(LocalDateTime.now());
            if(!(busiType==BUSI_TYPE5&&turnAmountLong==0L)){
                if (oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE1) {// ????????????
                    oilRechargeAccount.setRebateRechargeBalance(oilRechargeAccount.getRebateRechargeBalance() == null ? 0L : oilRechargeAccount.getRebateRechargeBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE2) {// ????????????
                    oilRechargeAccount.setCashRechargeBalance(oilRechargeAccount.getCashRechargeBalance() == null ? 0L : oilRechargeAccount.getCashRechargeBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {// ????????????
                    oilRechargeAccount.setCreditRechargeBalance(oilRechargeAccount.getCreditRechargeBalance() == null ? 0L : oilRechargeAccount.getCreditRechargeBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() ==SOURCE_TYPE4) {// ??????
                    oilRechargeAccount.setInheritBalance(oilRechargeAccount.getInheritBalance() == null ? 0L : oilRechargeAccount.getInheritBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE5) {// ????????????
                    oilRechargeAccount.setInvoiceOilBalance(oilRechargeAccount.getInvoiceOilBalance() == null ? 0L : oilRechargeAccount.getInvoiceOilBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE6) {// ????????????
                    oilRechargeAccount.setTransferOilBalance(oilRechargeAccount.getTransferOilBalance() == null ? 0L : oilRechargeAccount.getTransferOilBalance() + dealAmount);
                }
                oilRechargeAccountDetails.setAccountBalance(oilRechargeAccountDetails.getAccountBalance() == null ? 0L : oilRechargeAccountDetails.getAccountBalance() + dealAmount);
                oilRechargeAccount.setAccountBalance(oilRechargeAccount.getAccountBalance() == null ? 0L : oilRechargeAccount.getAccountBalance() + dealAmount);
            }
            oilRechargeAccount.setUpdateTime(LocalDateTime.now());
            detailsFlow.setUserId(userId);
            detailsFlow.setSourceType(oilRechargeAccountDetails.getSourceType());
            detailsFlow.setBusiType(busiType);
            detailsFlow.setBusiName(readisUtil.getSysStaticData(OIL_RECHARGE_BUSI_TYPE,busiType+"").getCodeName());// ??????
            detailsFlow.setCurrentAmount(oilRechargeAccountDetails.getAccountBalance());
            detailsFlow.setMatchAmount(0L);
            detailsFlow.setUnMatchAmount(0L);
            if(busiType==BUSI_TYPE3&&oilRechargeAccountDetails.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4){////?????? ???????????????????????????
                detailsFlow.setUnMatchAmount(valueLong);
            }
            if(busiType==BUSI_TYPE6
            ){
                if(turnAmount>0L){//?????????????????????????????????
                    detailsFlow.setUnMatchAmount(valueLong);
                }
                if(turnAmount<0L){//?????????????????????
                    oilRechargeAccountDetails.setDistributedAmount(oilRechargeAccountDetails.getDistributedAmount() == null ? 0L : oilRechargeAccountDetails.getDistributedAmount() + valueLong);
                    oilRechargeAccountDetails.setUnUseredBalance(oilRechargeAccountDetails.getUnUseredBalance() == null ? 0L : oilRechargeAccountDetails.getUnUseredBalance() + valueLong);
                }
            }
            detailsFlow.setOrderNum(orderNum);
            detailsFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE1);
            oilRechargeAccountDetailsService.saveOrUpdate(oilRechargeAccountDetails);
            if(detailsFlow.getAmount()!=null&&detailsFlow.getAmount()!=0L){
                oilRechargeAccountDetailsFlowService.save(detailsFlow);
                Iterator<Map.Entry<Integer, Integer>> entriesFlows = detailsFlows.entrySet().iterator();
                while (entriesFlows.hasNext()) {
                    Map.Entry<Integer, Integer> entryDetailsFlow = entriesFlows.next();
                    long flowId = Long.valueOf(entryDetailsFlow.getKey() + "");
                    long matchAmount = Long.valueOf(entryDetailsFlow.getValue() + "");
                    OilRechargeAccountDetailsFlow matchDetailsFlow = oilRechargeAccountDetailsFlowService.getById(flowId);
                    if(isTooLong) {
                        Map extMap = new HashMap();
                        extMap.put(flowId, matchAmount);
                        OilRechargeAccountDetailsFlowExt detailsFlowExt = new OilRechargeAccountDetailsFlowExt();
                        detailsFlowExt.setRelId(detailsFlow.getId());
                        detailsFlowExt.setMatchInfo(extMap.toString());
                        oilRechargeAccountDetailsFlowExtService.save(detailsFlowExt);
                    }
                }
            }
            this.saveOrUpdate(oilRechargeAccount);
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.masterCardDetails,Long.valueOf(key),SysOperLogConst.OperType.Update , CommonUtil.divide(Long.valueOf(value))+"???");
        }
        // ??????????????????
        OilRechargeAccountFlow oilRechargeAccountFlow = new OilRechargeAccountFlow();
        oilRechargeAccountFlow.setAccountId(oilRechargeAccount.getId());
        oilRechargeAccountFlow.setAccountType(oilRechargeAccount.getAccountType());
        oilRechargeAccountFlow.setBatchId(batchId);
        oilRechargeAccountFlow.setUserId(userId);
        oilRechargeAccountFlow.setUserName(oilRechargeAccount.getUserName());
        oilRechargeAccountFlow.setBillId(oilRechargeAccount.getBillId());
        oilRechargeAccountFlow.setBusiType(busiType);
        oilRechargeAccountFlow.setPinganAccId(pinganAccId);
        oilRechargeAccountFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE1);
        if (busiType == BUSI_TYPE2) {
            // ???????????????????????????????????????????????????????????????????????????
            oilRechargeAccountFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE0);
        }
        oilRechargeAccountFlow
                .setBusiName(busiName);// ??????
        oilRechargeAccountFlow.setCurrentAmount(oilRechargeAccount.getAccountBalance() == null ? 0L : oilRechargeAccount.getAccountBalance());
        oilRechargeAccountFlow.setAmount(turnAmount);
        oilRechargeAccountFlow.setBusiCode(busiCode);
        if(oilRechargeAccountFlow.getAmount()!=null&&oilRechargeAccountFlow.getAmount()!=0L){
            oilRechargeAccountFlowService.save(oilRechargeAccountFlow);
        }
    }

    @Override
    public Map<String, Object> distributionOil(Long userId, Long sourceUserId, Long amount,
                                               String orderNum, Long subjectsId, Long tenantId,
                                               Integer sourceRecordType, Integer distributionType,LoginInfo baseUser) {
        if (tenantId == null || tenantId < 1) {
            throw new BusinessException("???????????????id");
        }
        if (userId < 1) {
            throw new BusinessException("???????????????id");
        }
        if (sourceUserId < 1) {
            throw new BusinessException("???????????????????????????id");
        }
        if (amount <= 0) {
            throw new BusinessException("????????????????????????");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(orderNum)) {
            throw new BusinessException("???????????????????????????");
        }
        if (subjectsId < 1) {
            throw new BusinessException("??????????????????");
        }
        if (distributionType == null || distributionType <= 0) {
            throw new BusinessException("?????????????????????");
        }
        List<OilSourceRecord> recordList = oilSourceRecordService.getOilSourceRecordByOrderId(orderNum, tenantId);
        Map<String, Long> resultMap = this.judgeRechargeAccount(userId, sourceUserId, amount,
                orderNum, subjectsId,distributionType,baseUser);
        if (resultMap == null) {
            throw new BusinessException("????????????????????????????????????");
        }
        Long noPayOil = 0L;
        Long noRebateOil = 0L;
        Long noCreditOil = 0L;
        long totalDistributionOil = 0;
        Iterator<Map.Entry<String, Long>> iterator = resultMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            String key = entry.getKey();
            Long value= Math.abs(entry.getValue());
            if (org.apache.commons.lang3.StringUtils.isEmpty(key)) {
                throw new BusinessException("????????????????????????????????????");
            }
            if (value == null || value <= 0) {
                throw new BusinessException("???????????????????????????????????????");
            }
            totalDistributionOil += value;
            if (recordList != null && recordList.size() > 0) {
                OilSourceRecord oilSourceRecord = null;
                for (OilSourceRecord osr : recordList) {
                    if (osr.getRechargeId().toString().equals(key)) {
                        oilSourceRecord = osr;
                        break;
                    }
                }
                if (oilSourceRecord != null) {
                    oilSourceRecord.setBalance(oilSourceRecord.getBalance() + value);
                    oilSourceRecord.setNoPayBalance(oilSourceRecord.getNoPayBalance() + value);
                    oilSourceRecord.setUpdateDate(LocalDateTime.now());
                    oilSourceRecord.setUpdateOpId(baseUser.getId());
                    oilSourceRecordService.saveOrUpdate(oilSourceRecord);
                } else {
                    OilSourceRecord record = oilSourceRecordService.createOilSourceRecord(Long.parseLong(key), orderNum, value, value, 0l,
                            sourceRecordType, tenantId,baseUser);
                    oilSourceRecordService.saveOrUpdate(record);
                }
            } else {
                OilSourceRecord record = oilSourceRecordService.createOilSourceRecord(Long.parseLong(key), orderNum, value, value, 0l,
                        sourceRecordType, tenantId,baseUser);
                oilSourceRecordService.saveOrUpdate(record);
            }
            OilRechargeAccountDetails detail = oilRechargeAccountDetailsService.getById(Long.parseLong(key));
            if (detail == null) {
                throw new BusinessException("??????id???" + key +  " ???????????????????????????");
            }
            //
            if (detail.getSourceType() != null && detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1) {//??????
                noRebateOil += value;
            } else if (detail.getSourceType() != null && (detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2 ||
                    detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5 || detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6)) {//??????
                noPayOil += value;
            } else if (detail.getSourceType() != null && detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {//??????
                noCreditOil += value;
            }


        }
        if (amount != (noRebateOil + noPayOil + noCreditOil)) {
            throw new BusinessException("?????????????????????????????????????????????");
        }
        if (totalDistributionOil != amount) {
            throw new BusinessException("???????????????????????????????????????");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("noPayOil", noPayOil);
        map.put("noRebateOil", noRebateOil);
        map.put("noCreditOil", noCreditOil);
        return map;
    }

    /**
     * niejeiwei
     * ???????????????
     * ?????????-???????????????
     * 50050
     * @return
     */
    @Override
    public Page<OilRechargeAccountDto> queryOilAccountDetails(Long userId, String billId, String tenantName,
                                                              Integer userType, Integer pageNum,
                                                              Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if( userId ==null){
            userId = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId()).getAdminUser();
        }
        Page<OilRechargeAccountDto> page = new Page<>();
        // ???????????????
        Page<OilRechargeAccountDto> oilRechargeAccountDtoPage = baseMapper.queryOilAccountDetails(page, userId, billId, tenantName, userType);
        return oilRechargeAccountDtoPage;
    }

    /**
     * niejeiwei
     * ???????????????
     * ????????????-???????????????
     * 50051
     * @return
     */
    @Override
    public Page<TenantServiceRel> queryCreditLineDetails(String accessToken, String serviceName, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        TenantServiceVo vo = new TenantServiceVo();
        vo.setTenantId(loginInfo.getTenantId());
        vo.setServiceName(serviceName);
        List<TenantServiceRel> tenantServiceRels = iOilAccountService.doQueryQuotaAmtListByTenantId(vo);
        for (TenantServiceRel tenantServiceRel :tenantServiceRels) {
            // ????????????
            if (tenantServiceRel.getQuotaAmtL() !=null) {
                Double q = tenantServiceRel.getQuotaAmtL() / 100.00;
                tenantServiceRel.setQuotaAmtStr(q.toString());
            } else {
                tenantServiceRel.setQuotaAmtL(null);
                tenantServiceRel.setQuotaAmtStr("?????????");
            }
        }
        Page<TenantServiceRel> page = new Page<>();
        page.setRecords(tenantServiceRels);
        page.setTotal(tenantServiceRels.size());
        page.setSize(pageSize);
        page.setCurrent(pageNum);
        return page;
    }

    /**
     * niejeiwei
     * ???????????????
     * ????????????-???????????????
     * 50052
     * @return
     */
    @Override
    public Page<AccountBankRelDto> queryLockBalanceDetails(Long userId,String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        if(sysTenantDef != null){
            userId = sysTenantDef.getAdminUser();
        }else {
            userId = loginInfo.getUserInfoId();
        }
        Page<AccountBankRelDto> page = iPinganLockInfoService.queryLockBalanceDetails(userId, pageNum, pageSize);
        return page;
    }

    @Override
    public Page<OilRechargeAccountDetailsOutDto> getOilRechargeAccountDetails(String accessToken,
                                                                              Integer type,Integer pageNum,Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(type==-1){
            type=1;//1???????????? 2???????????????
        }
        Long userId = null;
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        if(sysTenantDef != null){
            userId= sysTenantDef.getAdminUser();
        }else {
            userId = loginInfo.getUserInfoId();
        }
        Integer[] sourceTypes = null;
        if (type == 1) {
            sourceTypes = new Integer[]{SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2,
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3,
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5};
        } else {
            sourceTypes = new Integer[]{SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1,
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4,
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6};
        }
        Page<OilRechargeAccountDto> page = new Page<>(pageNum,pageSize);
        Page<OilRechargeAccountDto> page1 = baseMapper.doQuery(page, type, loginInfo.getTenantId(), userId, sourceTypes);
        List<OilRechargeAccountDto> lists = page1.getRecords();
        List<OilRechargeAccountDetailsOutDto> listOut = new ArrayList<>();
        if(type == 2){
            //??????????????????????????????
            OrderAccountBalanceDto orderAccountBalance = iOrderAccountService.getOrderAccountBalance(sysTenantDef.getAdminUser(),
                    OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION, loginInfo.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            OrderAccountOutVo orderAccountOut = null;
            if(orderAccountBalance!=null){
                orderAccountOut =orderAccountBalance.getOa()!=null?orderAccountBalance.getOa():null;
            }
            OilRechargeAccountDetailsOutDto out = new OilRechargeAccountDetailsOutDto();
            long custOil = orderAccountOut.getTotalOilBalance();//?????????
            if(custOil>0L){
                out.setAccountBalance(custOil);
                out.setSourceType(0);//???????????????
                out.setSourceTypeName("?????????");//???????????????
                listOut.add(out);
            }

        }
        for (OilRechargeAccountDto entity : lists) {
            OilRechargeAccountDetailsOutDto out = new OilRechargeAccountDetailsOutDto();
            BeanUtil.copyProperties( entity,out);
            if(org.apache.commons.lang3.StringUtils.isNotBlank(out.getPinganAccId())){
                //???????????????
                AccountBankRel accountBankRel =  iAccountBankRelService.getAcctNo(out.getPinganAccId());
                if(null != accountBankRel){
                    out.setAcctName(accountBankRel.getAcctName());
                    out.setAcctNo(accountBankRel.getAcctNo());
                }
            }
            listOut.add(out);
        }
        Page<OilRechargeAccountDetailsOutDto> page2 = new Page<>();
        page2.setRecords(listOut);
        page2.setTotal(listOut.size());
        page2.setSize(pageSize);
        page2.setCurrent(pageNum);
        return page2;
    }
    @Transactional
    @Override
    public PingAnBalanceDto removeRechargeAccount(Long id, String accessToken) {
        this.removeRechargeAccount(id);
        PingAnBalanceDto dto = new PingAnBalanceDto();
        dto.setFlag("y");
        return dto;
    }


    @Override
    public void removeRechargeAccount(Long accountId) {
        if (accountId == null || accountId <= 0L) {
            throw new BusinessException("???????????????????????????");
        }
        OilRechargeAccountDetails oilRechargeAccountDetails = oilRechargeAccountDetailsService.getById(accountId);
        if (oilRechargeAccountDetails.getAccountBalance() > 0L) {
            throw new BusinessException("??????????????????0??????????????????");
        }
        if (oilRechargeAccountDetails.getUnUseredBalance() > 0L) {
            throw new BusinessException("???????????????????????????0??????????????????");
        }
        //1?????????????????? 2 ??????????????????
        Long count = payoutIntfMapper
                .countOnFlowPayoutInfo(oilRechargeAccountDetails.getPinganAccId(),
                        new String[] { OrderAccountConst.TXN_TYPE.XX_TXN_TYPE }, new String[] { HttpsMain.respCodeZero,
                                HttpsMain.respCodeFail, HttpsMain.netTimeOutFail, HttpsMain.respCodeZero },
                        EnumConsts.SubjectIds.SUBJECTIDS1131, 2);

        if (count > 0L) {
            throw new BusinessException("?????????????????????????????????");
        }
        try {
            oilRechargeAccountDetails.setState(0);
            oilRechargeAccountDetailsService.saveOrUpdate(oilRechargeAccountDetails);
        } catch (Exception ex) {
            throw new BusinessException("?????????????????????" + ex.getMessage());
        }
    }

    @Override
    public OilRechargeAccountDto getRechargeInfo(Long userId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
         userId = null;
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        if(sysTenantDef != null){
            userId = sysTenantDef.getAdminUser();
        }else {
            userId = loginInfo.getUserInfoId();
        }
        OilRechargeAccountDto rechargeInfo = this.getRechargeInfo(userId);
        return rechargeInfo;
    }

    @Override
    public void confirmRecharge(OilRechargeAccountVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId()).getAdminUser();
        if(true) {
            throw new BusinessException("?????????????????????????????????????????????????????????????????????!");
        }
        log.info("????????????:" + vo.getPinganAccId() + ",???????????????" + vo.getAmount() + ",?????????????????????" + (vo.getAmount() - vo.getCreditAmount()) + ",???????????????"
                + vo.getCreditAmount() + ",???????????????1??????,2??????????????????" + vo.getOilRechargeBillType());
        OilRechargeAccount oilRechargeAccount = this.getOilRechargeAccount(userId);
        UserDataInfo fleetUser = iUserDataInfoService.getUserDataInfo(userId);// ??????????????????
        if (fleetUser == null) {
            throw new BusinessException("??????????????????????????????");
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        if (sysTenantDef == null) {
            throw new BusinessException("????????????????????????");
        }
        long soNbr = CommonUtil.createSoNbr();
        String busiCode = AcUtil.createOilRechargePaymentnum("OP");
        if (oilRechargeAccount == null) {// ????????????
            oilRechargeAccount = new OilRechargeAccount();
            oilRechargeAccount.setUserId(userId);
            oilRechargeAccount.setUserName(fleetUser.getLinkman());
            oilRechargeAccount.setBillId(sysTenantDef.getLinkPhone());
            oilRechargeAccount.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1);// ??????1
            // ??????2
            oilRechargeAccount.setAccountBalance(0L);
            oilRechargeAccount.setCashRechargeBalance(0L);
            oilRechargeAccount.setRebateRechargeBalance(0L);
            oilRechargeAccount.setCreditRechargeBalance(0L);
            oilRechargeAccount.setInheritBalance(0L);
            oilRechargeAccount.setInvoiceOilBalance(0L);
            oilRechargeAccount.setTransferOilBalance(0L);
            oilRechargeAccount.setProcessingBalance(0L);
            oilRechargeAccount.setUnRepaymentBalance(0L);
            this.save(oilRechargeAccount);
        }
        if(vo.getOilRechargeBillType()== SysStaticDataEnum.OIL_RECHARGE_BILL_TYPE.STATE2&&vo.getCreditAmount()>0L){//???????????????????????????
            throw new BusinessException("???????????????????????????");
        }

        long currentAmount = oilRechargeAccount.getAccountBalance()+vo.getAmount();//???????????????
        if (vo.getAmount() <= 0L) {
            throw new BusinessException("?????????????????????0??????????????????");
        }
        if (vo.getCreditAmount() > vo.getAmount()) {
            throw new BusinessException("????????????????????????????????????");
        }
        long cashAmount = vo.getAmount() - vo.getCreditAmount();
        AgentServiceDto agentService = iServiceInfoService.getAgentService(sysTenantDef.getId(), ServiceConsts.AGENT_SERVICE_TYPE.OIL);
        if (agentService == null || (agentService.getTenantAgentServiceRel()==null && agentService.getAgentServiceInfo()==null && agentService.getServiceInfo()==null)) {
            throw new BusinessException("??????????????????????????????");
        }
        TenantAgentServiceRel tenantAgentServiceRel = agentService.getTenantAgentServiceRel();
        AgentServiceInfo agentServiceInfo = agentService.getAgentServiceInfo();
        if (tenantAgentServiceRel == null||agentServiceInfo==null) {
            throw new BusinessException("??????????????????????????????");
        }
        if (vo.getCreditAmount() > 0L) {// ??????????????????
            long quotaAmt = tenantAgentServiceRel.getQuotaAmt() == null ? 0L : tenantAgentServiceRel.getQuotaAmt();
            long useQuotaAmt = tenantAgentServiceRel.getUseQuotaAmt() == null ? 0L
                    : tenantAgentServiceRel.getUseQuotaAmt();
            if (quotaAmt <= 0L || quotaAmt - useQuotaAmt <= 0L) {
                throw new BusinessException("??????????????????????????????????????????");
            }
            // ??????????????????
            rechargeInsetAccDet(oilRechargeAccount, userId, vo.getPinganAccId(), vo.getCreditAmount(),
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3, "", vo.getServiceId(), soNbr, busiCode,"",accessToken);
            // ?????????????????????
            tenantAgentServiceRel.setUseQuotaAmt(useQuotaAmt + vo.getCreditAmount());
        }

        if (cashAmount > 0L) {// ???????????????????????????????????????
            if(vo.getOilRechargeBillType()== SysStaticDataEnum.OIL_RECHARGE_BILL_TYPE.STATE2){
                BillSetting billSetting = iBillSettingService.getBillSetting(sysTenantDef.getId());
                String vehicleAffiliation = billSetting.getBillMethod()+"";// ????????????
                //?????????????????????luge????????????luge??????????????????????????????????????????????????????
                String sysParameluge = readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC,
                        SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE+"").getCodeName();
                String sysPre = iBillPlatformService.getPrefixByUserId(Long.valueOf(vehicleAffiliation));
                if(sysParameluge.equals(sysPre)&& org.apache.commons.lang3.StringUtils.isBlank(agentServiceInfo.getLgOilStationId())){
                    throw new BusinessException("????????????????????????????????????????????????????????????????????????");
                }
                rechargeInsetAccDet(oilRechargeAccount, userId, vo.getPinganAccId(), cashAmount,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5, "", vo.getServiceId(), soNbr, busiCode,vehicleAffiliation,accessToken);
            }else{
                rechargeInsetAccDet(oilRechargeAccount, userId, vo.getPinganAccId(), cashAmount,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2, "", vo.getServiceId(), soNbr, busiCode,"1",accessToken);
            }
        }
        // ??????????????????
        OilRechargeAccountFlow oilRechargeAccountFlow = new OilRechargeAccountFlow();
        oilRechargeAccountFlow.setAccountId(oilRechargeAccount.getId());
        oilRechargeAccountFlow.setAccountType(oilRechargeAccount.getAccountType());
        oilRechargeAccountFlow.setPinganAccId(vo.getPinganAccId());
        oilRechargeAccountFlow.setBatchId(soNbr);
        oilRechargeAccountFlow.setUserId(userId);
        oilRechargeAccountFlow.setUserName(sysTenantDef.getName());
        oilRechargeAccountFlow.setBillId(sysTenantDef.getLinkPhone());
        oilRechargeAccountFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1);// ??????
        oilRechargeAccountFlow.setBusiName("??????");// ??????
        oilRechargeAccountFlow.setCurrentAmount(currentAmount);
        oilRechargeAccountFlow.setAmount(vo.getAmount());
        oilRechargeAccountFlowService.save(oilRechargeAccountFlow);
        oilRechargeAccountFlow.setBusiCode(busiCode);
        oilRechargeAccountFlowService.save(oilRechargeAccountFlow);
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.masterCardFlow,oilRechargeAccountFlow.getId(),SysOperLogConst.OperType.Add ,
                "?????????"+CommonUtil.divide(vo.getAmount())+"???");
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.masterCard,
                oilRechargeAccount.getId(),SysOperLogConst.OperType.Update ,"??????"+CommonUtil.divide(vo.getAmount())+"???");
    }


    public  OilRechargeAccountDto getRechargeInfo(Long adminUser){
        // ???????????????????????????
        List<BankBalanceInfo> bankBalanceInfos = iAccountBankRelService.queryBankBalanceInfoList( SysStaticDataEnum.USER_TYPE.ADMIN_USER,adminUser,
                BUSINESS_PAYABLE_ACCOUNT,null,null,null,null);
        String oilRechargeLowest = readisUtil.getSysCfg(EnumConsts.SysCfg.OIL_RECHARGE_LOWEST, "0").getCfgValue();
        long lowest = Long.parseLong(oilRechargeLowest);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(adminUser);
        if (sysTenantDef == null) {
            throw new BusinessException("????????????????????????");
        }
        AgentServiceDto agentService = iServiceInfoService.getAgentService(sysTenantDef.getId(), ServiceConsts.AGENT_SERVICE_TYPE.OIL);
//        if (objects == null || objects.length == 0) {
        if (agentService == null || ( agentService.getTenantAgentServiceRel()==null && agentService.getAgentServiceInfo()==null && agentService.getServiceInfo()==null)) {
            throw new BusinessException("????????????"+sysTenantDef.getName()+"???,??????????????????????????????????????????????????????????????????????????????????????? ");
        }
        TenantAgentServiceRel tenantAgentServiceRel = agentService.getTenantAgentServiceRel();
        AgentServiceInfo agentServiceInfo = agentService.getAgentServiceInfo();
        ServiceInfo serviceInfo = agentService.getServiceInfo();
        OilRechargeAccountDto dto = new OilRechargeAccountDto();
        dto.setBankBalanceInfos(bankBalanceInfos);// ???????????????????????????????????????
        dto.setLowestAmount(lowest);// ????????????????????????
        AccountBankRel accountBankRel = iAccountBankRelService.getDefaultAccountBankRel(agentServiceInfo.getServiceUserId(),
                EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        if(accountBankRel==null){
            throw new BusinessException("???????????????????????????????????????????????????????????????????????????");
        }
        dto.setCreditBalance((tenantAgentServiceRel.getQuotaAmt() == null ? 0L : tenantAgentServiceRel.getQuotaAmt())
                - (tenantAgentServiceRel.getUseQuotaAmt() == null ? 0L
                : tenantAgentServiceRel.getUseQuotaAmt()));// ??????????????????
        dto.setServiceId(agentServiceInfo.getServiceUserId());// ?????????Id
        dto.setServiceName(serviceInfo.getServiceName());// ???????????????
        dto.setReceivedBankInfo(accountBankRel);// ??????????????????????????????
        return dto;
    }
    @Override
    public OilRechargeAccountDto getOilRechargeAccount(Long userId, Long tenantId, Integer userType, String billLookUp, LoginInfo user) {
        OrderAccountBalanceDto map = orderAccountService.getOrderAccountBalance(userId, OrderAccountConst.ORDER_BY.OIL_BALANCE, tenantId, userType);
        if (map == null) {
            throw new BusinessException("???????????????????????????");
        }
        OilRechargeAccountDto retMap=new OilRechargeAccountDto();
        OilRechargeAccount oilRechargeAccount = this.getOilRechargeAccount(userId);
        OrderAccountOutVo out = map.getOa();
        if(out!=null){
            retMap.setCustOilBalance(out.getTotalOilBalance());//????????????????????????
        }
        if(oilRechargeAccount!=null){
            retMap.setNonDeductOilBalance((oilRechargeAccount.getCashRechargeBalance()==null?0L:oilRechargeAccount.getCashRechargeBalance())
                    +(oilRechargeAccount.getCreditRechargeBalance()==null?0L:oilRechargeAccount.getCreditRechargeBalance()));//???????????????????????????????????????
            retMap.setRebateOilBalance(oilRechargeAccount.getRebateRechargeBalance()==null?0L:oilRechargeAccount.getRebateRechargeBalance());//????????????????????????
            retMap.setTransferOilBalance(oilRechargeAccount.getTransferOilBalance()==null?0L:oilRechargeAccount.getTransferOilBalance());//??????????????????????????????
            retMap.setInvoiceOilBalance(oilRechargeAccount.getInvoiceOilBalance()==null?0L:oilRechargeAccount.getInvoiceOilBalance());//??????????????????(????????????)
        }
        if(StringUtils.isNotBlank(billLookUp)){
            List<AccountBankRel> accountBankRels = accountBankRelService1.getBankInfo(user.getId(),
                    billLookUp, "", EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.ADMIN_USER);

            if(accountBankRels!=null&&accountBankRels.size()>0L){
                Long billLookUpBalance = 0L;//??????????????????
                Long cashRechargeBalance = 0L;//????????????????????????
                Long creditRechargeBalance = 0L;//????????????????????????
                for(AccountBankRel accountBankRel:accountBankRels){
                    String pinganAccId = accountBankRel.getPinganPayAcctId();

                    List<OilRechargeAccountDetails> billLookUpDetail = oilRechargeAccountDetailsService.getOilRechargeAccountDetail(userId, pinganAccId, 0L, SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5,"");//????????????????????????
                    if(billLookUpDetail!=null&&billLookUpDetail.size()>0){
                        for(OilRechargeAccountDetails detail:billLookUpDetail){
                            billLookUpBalance+=(detail.getAccountBalance()==null?0L:detail.getAccountBalance());
                        }
                    }

                    List<OilRechargeAccountDetails> cashRechargeDetails = oilRechargeAccountDetailsService.getOilRechargeAccountDetail(userId, pinganAccId, 0L, SOURCE_TYPE2,"");//?????????????????????????????????
                    if(cashRechargeDetails!=null&&cashRechargeDetails.size()>0){
                        for(OilRechargeAccountDetails detail:cashRechargeDetails){
                            cashRechargeBalance+=(detail.getAccountBalance()==null?0L:detail.getAccountBalance());
                        }
                    }

                    List<OilRechargeAccountDetails> creditRechargeDetails = oilRechargeAccountDetailsService.getOilRechargeAccountDetail(userId, pinganAccId, 0L, SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3,"");//?????????????????????????????????
                    if(creditRechargeDetails!=null&&creditRechargeDetails.size()>0){
                        for(OilRechargeAccountDetails detail:creditRechargeDetails){
                            creditRechargeBalance+=(detail.getAccountBalance()==null?0L:detail.getAccountBalance());
                        }
                    }
                }
                retMap.setBillLookUpBalance(billLookUpBalance);//?????????????????????????????????????????????
                retMap.setUnBillLookUpBalance(cashRechargeBalance+creditRechargeBalance);//????????????????????????????????????????????????
            }
        }

        return retMap;
    }




    public Map<String, Long> judgeRechargeAccount(long userId, long sourceUserId, long amount, String orderNum,
                                                  long subjectsId,Integer billType,LoginInfo user)  {//1.????????????????????????2.??????????????????3.???????????????

        OilRechargeAccount oilRechargeAccount = oilRechargeAccountService.getOilRechargeAccount(sourceUserId);
        if (oilRechargeAccount==null||oilRechargeAccount.getAccountBalance() <= 0L) {
            throw new BusinessException("?????????????????????????????????????????????????????????");
        }
        long waitMatchAmount = amount;
        long batchId = CommonUtil.createSoNbr();
  //  List<OilRechargeAccountDetailsFlow> flowList = oilRechargeAccountDetailsFlowService.getUnMatchedFlowsASC(sourceUserId);
        String busiCode = AcUtil.createOilRechargePaymentnum("OD");
        Map matchAccount = new HashMap();
        Map flowDetails = new HashMap();
        if(billType==EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1){//???????????????????????????????????????????????????????????????
            if(waitMatchAmount>0L){//???????????????
                Map<String,Object> retMap = matchOilRechargeAccount(sourceUserId,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3,
                        waitMatchAmount, orderNum, busiCode, batchId, subjectsId,user);
                if(!retMap.isEmpty()){
                    Map<String,Long> matchAccountMap = (Map<String,Long>)retMap.get("matchAccount");
                    Map<String,Long> flowDetailsMap = (Map<String,Long>)retMap.get("flowDetails");
                    Long dealAmount = Long.valueOf(retMap.get("dealAmount")+"");
                    waitMatchAmount-=dealAmount;
                    if(!matchAccountMap.isEmpty()){
                        matchAccount.putAll(matchAccountMap);
                    }
                    if(!flowDetailsMap.isEmpty()){
                        flowDetails.putAll(flowDetailsMap);
                    }
                }
            }
            if(waitMatchAmount>0L){//??????????????????
                Map<String,Object> retMap = matchOilRechargeAccount(sourceUserId,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2,
                        waitMatchAmount, orderNum, busiCode, batchId, subjectsId,user);
                if(!retMap.isEmpty()){
                    Map<String,Long> matchAccountMap = (Map<String,Long>)retMap.get("matchAccount");
                    Map<String,Long> flowDetailsMap = (Map<String,Long>)retMap.get("flowDetails");
                    Long dealAmount = Long.valueOf(retMap.get("dealAmount")+"");
                    waitMatchAmount-=dealAmount;
                    if(!matchAccountMap.isEmpty()){
                        matchAccount.putAll(matchAccountMap);
                    }
                    if(!flowDetailsMap.isEmpty()){
                        flowDetails.putAll(flowDetailsMap);
                    }
                }
            }
        }else if(billType==EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE2){//???????????????
            if(waitMatchAmount>0L){//?????????????????????????????????
                Map<String,Object> retMap = matchOilRechargeAccount(sourceUserId,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5,
                        waitMatchAmount, orderNum, busiCode, batchId, subjectsId,user);
                if(!retMap.isEmpty()){
                    Map<String,Long> matchAccountMap = (Map<String,Long>)retMap.get("matchAccount");
                    Map<String,Long> flowDetailsMap = (Map<String,Long>)retMap.get("flowDetails");
                    Long dealAmount = Long.valueOf(retMap.get("dealAmount")+"");
                    waitMatchAmount-=dealAmount;
                    if(!matchAccountMap.isEmpty()){
                        matchAccount.putAll(matchAccountMap);
                    }
                    if(!flowDetailsMap.isEmpty()){
                        flowDetails.putAll(flowDetailsMap);
                    }
                }
            }
        }else{
            if(waitMatchAmount>0L){//??????????????????
                Map<String,Object> retMap = matchOilRechargeAccount(sourceUserId,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1,
                        waitMatchAmount, orderNum, busiCode, batchId, subjectsId,user);
                if(!retMap.isEmpty()){
                    Map<String,Long> matchAccountMap = (Map<String,Long>)retMap.get("matchAccount");
                    Map<String,Long> flowDetailsMap = (Map<String,Long>)retMap.get("flowDetails");
                    Long dealAmount = Long.valueOf(retMap.get("dealAmount")+"");
                    waitMatchAmount-=dealAmount;
                    if(!matchAccountMap.isEmpty()){
                        matchAccount.putAll(matchAccountMap);
                    }
                    if(!flowDetailsMap.isEmpty()){
                        flowDetails.putAll(flowDetailsMap);
                    }
                }
            }
            if(waitMatchAmount>0L){//??????????????????
                Map<String,Object> retMap = matchOilRechargeAccount(sourceUserId,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6, waitMatchAmount,
                        orderNum, busiCode, batchId, subjectsId,user);
                if(!retMap.isEmpty()){
                    Map<String,Long> matchAccountMap = (Map<String,Long>)retMap.get("matchAccount");
                    Map<String,Long> flowDetailsMap = (Map<String,Long>)retMap.get("flowDetails");
                    Long dealAmount = Long.valueOf(retMap.get("dealAmount")+"");
                    waitMatchAmount-=dealAmount;
                    if(!matchAccountMap.isEmpty()){
                        matchAccount.putAll(matchAccountMap);
                    }
                    if(!flowDetailsMap.isEmpty()){
                        flowDetails.putAll(flowDetailsMap);
                    }
                }
            }
        }
        if(waitMatchAmount>0L){
            throw new BusinessException("?????????????????????");
        }

        //?????????????????????
        UserDataInfo userInfo = userDataInfoService.get(userId);// ??????????????????
        if (userInfo == null) {
            throw new BusinessException("????????????????????????");
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        OilRechargeAccount receivableAccount = oilRechargeAccountService.getOilRechargeAccount(userId);
        if(receivableAccount==null){
            receivableAccount = new OilRechargeAccount();
            if(sysTenantDef!=null){
                receivableAccount.setTenantId(sysTenantDef.getId());
                receivableAccount.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1);
            }else{
                receivableAccount.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2);
            }
            receivableAccount.setUserId(userId);
            receivableAccount.setUserName(userInfo.getLinkman());
            receivableAccount.setBillId(userInfo.getMobilePhone());
            receivableAccount.setAccountBalance(0L);
            receivableAccount.setCashRechargeBalance(0L);
            receivableAccount.setRebateRechargeBalance(0L);
            receivableAccount.setCreditRechargeBalance(0L);
            receivableAccount.setInheritBalance(0L);
            receivableAccount.setInvoiceOilBalance(0L);
            receivableAccount.setTransferOilBalance(0L);
            receivableAccount.setProcessingBalance(0L);
            receivableAccount.setUnRepaymentBalance(0L);
            oilRechargeAccountService.save(receivableAccount);
        }

        Map<String,Long> receivableMap = new HashMap<String,Long>();
        Iterator<Map.Entry<String, Long>> entries = matchAccount.entrySet().iterator();
        Map addFlowDetails = new HashMap();
        while (entries.hasNext()) {
            Map.Entry<String, Long> entry = entries.next();
            String key = entry.getKey() + "";
            long addAmount =Math.abs(entry.getValue());
            OilRechargeAccountDetails motherAccountDetails = oilRechargeAccountDetailsService.getById( Long.valueOf(key));

            OilRechargeAccountDetails receivableAccountDetails = oilRechargeAccountDetailsService.getOilRechargeAccountDetail(userId,
                    "", sourceUserId, motherAccountDetails.getPinganAccId(), SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4);
            if(receivableAccountDetails==null){
                receivableAccountDetails = new OilRechargeAccountDetails();
                if(sysTenantDef!=null){
                    receivableAccountDetails.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1);
                }else{
                    receivableAccountDetails.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2);
                }
                receivableAccountDetails.setUserId(userId);
                receivableAccountDetails.setAccountBalance(0L);
                receivableAccountDetails.setDistributedAmount(0L);
                receivableAccountDetails.setUnUseredBalance(0L);
                receivableAccountDetails.setSourceUserId(sourceUserId);
                receivableAccountDetails.setSourcePinganAccId(motherAccountDetails.getPinganAccId());
                receivableAccountDetails.setSourceType(SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4);
                if (sysTenantDef != null) {
                    receivableAccountDetails.setTenantId(sysTenantDef.getId());
                }
                receivableAccountDetails.setState(1);
                oilRechargeAccountDetailsService.save(receivableAccountDetails);
                addFlowDetails.put(motherAccountDetails.getId(), addAmount);
            }
            receivableMap.put(receivableAccountDetails.getId()+"",
                    receivableMap.get(receivableAccountDetails.getId()+"") == null ? Long.valueOf(addAmount)
                            : Long.valueOf(receivableMap.get(receivableAccountDetails.getId() + "") + "")+ Long.valueOf(addAmount));
        }

        // ??????????????????????????????????????????
        oilRechargeAccountService.insertOilAccount(userId, receivableMap, amount, orderNum, BUSI_TYPE3,
                batchId, subjectsId, busiCode,addFlowDetails,user);

        if(billType==2){//???????????????
            //?????????????????????56K????????????56K??????(???????????????????????????????????????)
            long waitSyncAmt = amount;
            Iterator<Map.Entry<String, Long>> flowEntries = flowDetails.entrySet().iterator();
            while (flowEntries.hasNext()) {
                Map.Entry<String, Long> entry = flowEntries.next();
                if(waitSyncAmt<=0L){
                    continue;
                }
                String key = entry.getKey();
                Long value = entry.getValue();
                OilRechargeAccountDetailsFlow detailsFlow = oilRechargeAccountDetailsFlowService.getById(Long.valueOf(key));
                if (detailsFlow != null && detailsFlow.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5) {
                    OilRechargeAccountDetails details = oilRechargeAccountDetailsService
                            .getById( detailsFlow.getRelId());
                    long dealAmt = waitSyncAmt;
                    if(dealAmt<detailsFlow.getUnMatchAmount()){
                        dealAmt = detailsFlow.getUnMatchAmount();
                    }
                    String vehicleAffiliation = details.getVehicleAffiliation();
                    if(org.apache.commons.lang3.StringUtils.isNoneBlank(vehicleAffiliation)){
                        String sysParame56K = readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC,
                                SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K + "").getCodeName();
                        String sysPre = billPlatformService.getPrefixByUserId(Long.valueOf(vehicleAffiliation));
                        if (!sysParame56K.equals(sysPre)) {
                            continue;
                        }
                        OilRecord56k oilRecord56k = new OilRecord56k();
                        oilRecord56k.setBusiCode(busiCode);
                        oilRecord56k.setBusiType(EnumConsts.BUSI_TYPE.BUSI_TYPE1);// ??????
                        oilRecord56k.setOrderId(Long.valueOf(orderNum));
                        oilRecord56k.setDealDate(LocalDateTime.now());
                        oilRecord56k.setAmount(dealAmt);
                        oilRecord56k.setSyncState(EnumConsts.SYNC_STATE.SYNC_STATE0);
                        BillPlatform billPlatform = billPlatformService
                                .queryAllBillPlatformByUserId(Long.valueOf(vehicleAffiliation));
                        if (billPlatform != null) {
                            String billHead = billPlatform.getBillHead();
                            if ("??????????????????????????????????????????".equals(billHead)) {
                                oilRecord56k.setIdentifier(0);// 0 ?????? 1 ??????
                            } else if ("???????????????????????????????????????".equals(billHead)) {
                                oilRecord56k.setIdentifier(1);// 0 ?????? 1 ??????
                            }
                        } else {
                            log.error("????????????????????????56K?????????????????????????????????????????????????????????" + vehicleAffiliation);
                            oilRecord56k.setSyncState(EnumConsts.SYNC_STATE.SYNC_STATE2);
                        }

                        oilRecord56k.setSyncMsg("?????????");
                        oilRecord56kService.save(oilRecord56k);
                        waitSyncAmt-=dealAmt;
                    }
                }

            }
        }
        return matchAccount;
    }
    public Map<String, Object> matchOilRechargeAccount(long userId,int sourceType,
                                                       long waitMatchAmount,
                                                       String orderNum,String busiCode,
                                                       long batchId,long subjectsId,
                                                       LoginInfo user){
        List<OilRechargeAccountDetailsFlow> flowList = oilRechargeAccountDetailsFlowService.getUnMatchedFlowsASC(userId,sourceType);
        Map matchAccount = new HashMap();
        String billLookUp="";//???????????????????????????????????????
        List<String> pinganAccIds = new ArrayList<String>();
        if(sourceType== SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5){//?????????
            OrderFeeExt orderFeeExt = orderFeeExtService.getOrderFeeExt(Long.valueOf(orderNum));
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(Long.valueOf(orderNum));
            if(orderFeeExt != null && orderFeeExt.getBillLookUp() != null){
                billLookUp = orderFeeExt.getBillLookUp();
            }
            if(orderFeeExtH != null && orderFeeExtH.getBillLookUp() != null){
                billLookUp = orderFeeExtH.getBillLookUp();
            }
            List<AccountBankRel> accountBankRels= accountBankRelService1.getBankInfo(userId, billLookUp,
                    "", EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1,
                    SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            for(AccountBankRel bankRel:accountBankRels){
                pinganAccIds.add(bankRel.getPinganPayAcctId());
            }
        }
        long dealAmount = 0L;
        Map flowDetails = new HashMap();
        if (flowList != null && flowList.size() > 0) {
            for (OilRechargeAccountDetailsFlow flow : flowList) {
                String pinganAccId = flow.getPinganAccId();
                if(sourceType== SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5&&pinganAccIds!=null
                        &&pinganAccIds.size()>0&&!pinganAccIds.contains(pinganAccId)){//?????????????????????????????????
                    continue;
                }
                if (waitMatchAmount <= 0L||(org.apache.commons.lang3.StringUtils.isNoneBlank(billLookUp)&&!pinganAccIds.contains(pinganAccId))) {// ??????????????????????????????
                    continue;
                }
                Long unMatchAmount = flow.getUnMatchAmount();
                long doAmount = 0L;
                if (waitMatchAmount > unMatchAmount) {
                    doAmount = unMatchAmount;

                } else {
                    doAmount = waitMatchAmount;
                }
                long matchAmount = matchAccount.get(flow.getRelId()) == null ? 0L
                        : Long.valueOf(matchAccount.get(flow.getRelId()) + "");
                matchAmount += doAmount;
                matchAccount.put(flow.getRelId()+ "", matchAccount.get(flow.getRelId()+"")==null?-matchAmount:Long.valueOf(matchAccount.get(flow.getRelId()+"")+"")-matchAmount);
                flow.setMatchAmount((flow.getMatchAmount() == null ? 0L : flow.getMatchAmount()) + doAmount);
                flow.setUnMatchAmount((flow.getUnMatchAmount() == null ? 0L : flow.getUnMatchAmount()) - doAmount);
                oilRechargeAccountDetailsFlowService.save(flow);
                waitMatchAmount -= doAmount;
                flowDetails.put(flow.getId()+ "", doAmount);
                dealAmount+=doAmount;
            }
        }
        Map<String,Object> retMap = new HashMap<String,Object>();
        if(dealAmount>0L&&!matchAccount.isEmpty()){
            oilRechargeAccountService.insertOilAccount(userId, matchAccount,
                    -dealAmount, orderNum, BUSI_TYPE3,
                    batchId, subjectsId, busiCode,flowDetails,user);
            retMap.put("matchAccount", matchAccount);
            retMap.put("flowDetails", flowDetails);
            retMap.put("dealAmount", dealAmount);
        }
        return retMap;
    }

    // ??????????????????
    public void rechargeInsetAccDet(OilRechargeAccount oilRechargeAccount, long userId, String pinganAcctId,
                                    long amount, int sourceType, String orderNum, Long serviceUserId, long soNbr,
                                    String busiCode,String vehicleAffiliation,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (amount == 0L) {
            throw new BusinessException("??????0?????????????????????");
        }
        UserDataInfo fleetUser = iUserDataInfoService.getUserDataInfo(userId);// ??????????????????

        if (fleetUser == null) {
            throw new BusinessException("??????????????????????????????");
        }
        long tenantId = -1L;
        boolean isTenant = false;
        if (sourceType != SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1
                && sourceType != SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4) {//????????????????????????????????????????????????
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
            if (sysTenantDef == null) {
                throw new BusinessException("????????????????????????");
            }
            tenantId = sysTenantDef.getId();
            isTenant = true;
        }


        String oilAffiliation = vehicleAffiliation;// ???????????????
        List<OilRechargeAccountDetails> detailsList = iOilRechargeAccountDetailsServicel.getOilRechargeAccountDetail(userId,
                pinganAcctId, serviceUserId, sourceType, vehicleAffiliation);
        OilRechargeAccountDetailsFlow oilRechargeAccountDetailsFlow = new OilRechargeAccountDetailsFlow();
        if (oilRechargeAccount == null) {
            oilRechargeAccount = new OilRechargeAccount();
            oilRechargeAccount.setUserId(userId);
            if (isTenant) {
                oilRechargeAccount.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1);//??????
            } else {
                oilRechargeAccount.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2);//??????
            }
            oilRechargeAccount.setUserName(fleetUser.getLinkman());
            oilRechargeAccount.setAccountBalance(0L);
            oilRechargeAccount.setCashRechargeBalance(0L);
            oilRechargeAccount.setRebateRechargeBalance(0L);
            oilRechargeAccount.setCreditRechargeBalance(0L);
            oilRechargeAccount.setInheritBalance(0L);
            oilRechargeAccount.setInvoiceOilBalance(0L);
            oilRechargeAccount.setTransferOilBalance(0L);
            oilRechargeAccount.setProcessingBalance(0L);
            oilRechargeAccount.setUnRepaymentBalance(0L);
            oilRechargeAccount.setTenantId(tenantId);
        }
        OilRechargeAccountDetails oilRechargeAccountDetail = null;
        if (detailsList != null && detailsList.size() > 0) {
            oilRechargeAccountDetail = detailsList.get(0);
        } else {
            oilRechargeAccountDetail = new OilRechargeAccountDetails();
            oilRechargeAccountDetail.setUserId(userId);
            oilRechargeAccountDetail.setVehicleAffiliation(vehicleAffiliation);
            oilRechargeAccountDetail.setAccountType(oilRechargeAccount.getAccountType());
            oilRechargeAccountDetail.setPinganAccId(pinganAcctId);
            oilRechargeAccountDetail.setSourceType(sourceType);
            oilRechargeAccountDetail.setAccountBalance(0L);
            oilRechargeAccountDetail.setDistributedAmount(0L);
            oilRechargeAccountDetail.setUnUseredBalance(0L);
            oilRechargeAccountDetail.setSourceUserId(serviceUserId);
            oilRechargeAccountDetail.setState(1);//??????
            oilRechargeAccountDetail.setTenantId(tenantId);
        }


        long currentAmount = oilRechargeAccountDetail.getAccountBalance() + amount;//???????????????
        log.info("???????????????????????????" + oilRechargeAccount);
        log.info("????????????????????????" + oilRechargeAccountDetail);

        if (sourceType != SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2
                && sourceType != SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5) {// ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            oilRechargeAccount.setAccountBalance(oilRechargeAccount.getAccountBalance() + amount);
            oilRechargeAccountDetail.setAccountBalance(oilRechargeAccountDetail.getAccountBalance() + amount);
        }

        if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1) {// ????????????
            oilRechargeAccount.setRebateRechargeBalance(oilRechargeAccount.getRebateRechargeBalance() + amount);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE6);

        } else if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2) {// ????????????

            this.payCashRecharge(pinganAcctId, fleetUser, serviceUserId, amount, vehicleAffiliation, oilAffiliation,
                    tenantId, soNbr, busiCode,accessToken);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1);
        } else if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5) {// ????????????(??????)
            BillSetting billSetting = iBillSettingService.getBillSetting(tenantId);
            vehicleAffiliation = billSetting.getBillMethod() + "";// ????????????
            oilAffiliation = billSetting.getBillMethod() + "";// ????????????
            this.payCashRecharge(pinganAcctId, fleetUser, serviceUserId, amount, vehicleAffiliation, oilAffiliation,
                    tenantId, soNbr, busiCode,accessToken);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1);
        } else if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {// ????????????
            if (org.apache.commons.lang3.StringUtils.isBlank(pinganAcctId)) {
                throw new BusinessException("????????????????????????????????????");
            }
            oilRechargeAccount.setUnRepaymentBalance(oilRechargeAccount.getUnRepaymentBalance() + amount);
            oilRechargeAccount.setCreditRechargeBalance(oilRechargeAccount.getCreditRechargeBalance() + amount);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1);

        } else if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4) {// ??????
            oilRechargeAccount.setInheritBalance(oilRechargeAccount.getInheritBalance() + amount);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE3);

        }
        oilRechargeAccountService.save(oilRechargeAccount);
        oilRechargeAccountDetailsService.save(oilRechargeAccountDetail);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(pinganAcctId)) {
            AccountBankRel accountBankRel = iAccountBankRelService.getAcctNo(pinganAcctId);
            oilRechargeAccountDetailsFlow.setPinganAccId(pinganAcctId);
            oilRechargeAccountDetailsFlow.setBankAccName(accountBankRel.getAcctName());

        }

        oilRechargeAccountDetailsFlow.setAccountType(oilRechargeAccountDetail.getAccountType());
        oilRechargeAccountDetailsFlow.setBusiName(getSysStaticData("OIL_RECHARGE_BUSI_TYPE",
                oilRechargeAccountDetailsFlow.getBusiType().toString() + "").getCodeName());
        oilRechargeAccountDetailsFlow.setSourceType(sourceType);
        oilRechargeAccountDetailsFlow.setRelId(oilRechargeAccountDetail.getId());
        oilRechargeAccountDetailsFlow.setUserId(userId);
        oilRechargeAccountDetailsFlow.setCurrentAmount(currentAmount);
        oilRechargeAccountDetailsFlow.setAmount(amount);
        oilRechargeAccountDetailsFlow.setUnMatchAmount(amount);
        oilRechargeAccountDetailsFlow.setSubjectsId(EnumConsts.SubjectIds.SUBJECTIDS1130);
        if (oilRechargeAccountDetailsFlow.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2
                || oilRechargeAccountDetailsFlow.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5) {
            oilRechargeAccountDetailsFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE0);
        } else {
            oilRechargeAccountDetailsFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE1);
        }
        oilRechargeAccountDetailsFlow.setBusiCode(busiCode);
        oilRechargeAccountDetailsFlow.setRemark("????????????");
        oilRechargeAccountDetailsFlow.setBatchId(soNbr);
        oilRechargeAccountDetailsFlowService.save(oilRechargeAccountDetailsFlow);
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.masterCardDetails, oilRechargeAccountDetail.getId(),
                SysOperLogConst.OperType.Update, "??????" + CommonUtil.divide(amount) + "???");
        log.info("???????????????????????????" + oilRechargeAccount);
        log.info("????????????????????????" + oilRechargeAccountDetail);
    }

    public void payCashRecharge(String pinganAcctId, UserDataInfo fleetUser, Long serviceUserId, Long amount,
                                String vehicleAffiliation, String oilAffiliation, Long tenantId, Long soNbr,
                                String busiCode,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        // ????????????
        if (org.apache.commons.lang3.StringUtils.isBlank(pinganAcctId)) {
            throw new BusinessException("????????????????????????????????????");
        }
        UserDataInfo serviceUser = iUserDataInfoService.getUserDataInfo(serviceUserId);// ?????????????????????
        if (serviceUser == null) {
            throw new BusinessException("?????????????????????????????????");
        }
        // ??????????????????????????????????????????
        // oilRechargeAccount.setCashRechargeBalance(oilRechargeAccount.getCashRechargeBalance()+amount);
        // ??????????????????

        long receSubjectId = EnumConsts.SubjectIds.SUBJECTIDS1131;
        long paysubjectId = EnumConsts.SubjectIds.SUBJECTIDS1132;
        // ????????????
        List<BusiSubjectsRel> busiSubjectsRelList = null;
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        // ???????????????????????????
        BusiSubjectsRel add = new BusiSubjectsRel();
        add.setSubjectsId(receSubjectId);
        add.setAmountFee(amount);
        // ???????????????????????????
        BusiSubjectsRel addPay = new BusiSubjectsRel();
        addPay.setSubjectsId(paysubjectId);
        addPay.setAmountFee(amount);

        busiList.add(add);
        busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_RECHARGE, busiList);

        int payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
        int receUserType = SysStaticDataEnum.USER_TYPE.SERVICE_USER;

        // ???????????????
        OrderAccount serviceAccount = iOrderAccountService.queryOrderAccount(serviceUserId, vehicleAffiliation, 0L, tenantId,
                oilAffiliation,receUserType);
        // ????????????????????????????????????????????????
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.RECHARGE_TYPE_CODE, EnumConsts.PayInter.OIL_RECHARGE,
                serviceUser.getId(), serviceUser.getLinkman(), serviceAccount, busiSubjectsRelList, soNbr, 0L,
                serviceUser.getLinkman(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
        // ??????????????????
        OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(fleetUser.getId(), vehicleAffiliation, 0L,
                tenantId, oilAffiliation,payUserType);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();

        BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(paysubjectId, amount);
        fleetBusiList.add(payableOverdueRel);
        // ??????????????????
        List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_RECHARGE,
                fleetBusiList);
        // ????????????????????????????????????????????????
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.RECHARGE_TYPE_CODE, EnumConsts.PayInter.OIL_RECHARGE,
                fleetUser.getId(), fleetUser.getLinkman(), fleetAccount, fleetSubjectsRelList, soNbr, 0L,
                fleetUser.getLinkman(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);

        Long adminUser = sysTenantDefService.getTenantAdminUser(tenantId);
        OrderAccount payAccount = iOrderAccountService.getOrderAccount(adminUser, vehicleAffiliation, tenantId,
                oilAffiliation,payUserType);
        Calendar cd = Calendar.getInstance();
        PayoutIntf payOutIntfVirToVir = new PayoutIntf();
        payOutIntfVirToVir.setVehicleAffiliation(vehicleAffiliation);
        payOutIntfVirToVir.setUserId(serviceUserId);
        //????????????????????????
        payOutIntfVirToVir.setUserType(receUserType);
        payOutIntfVirToVir.setPayUserType(payUserType);
        //????????????????????????
        payOutIntfVirToVir.setTxnAmt(amount);
        payOutIntfVirToVir.setBusiId(EnumConsts.PayInter.OIL_RECHARGE);// ??????????????????
        payOutIntfVirToVir.setObjId(
                org.apache.commons.lang3.StringUtils.isNotBlank(serviceUser.getMobilePhone()) ? Long.valueOf(serviceUser.getMobilePhone()) : 0L);
        payOutIntfVirToVir.setObjType(SysStaticDataEnum.OBJ_TYPE.RECHARGE + "");
        payOutIntfVirToVir.setPayObjId(payAccount.getUserId());
        payOutIntfVirToVir.setCreateDate(LocalDateTime.now());
        payOutIntfVirToVir.setWaitBillingAmount(0L);// ?????????0
        payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.SERVICE);// ?????????????????????
        payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
        payOutIntfVirToVir.setSubjectsId(receSubjectId);
        payOutIntfVirToVir.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
        if(VEHICLE_AFFILIATION1.equals(vehicleAffiliation)){
            payOutIntfVirToVir.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL6);//??????
        }else{
            payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);//HA??????
        }
        payOutIntfVirToVir.setPayTenantId(tenantId);
        payOutIntfVirToVir.setOilAffiliation(oilAffiliation);
        payOutIntfVirToVir.setBusiCode(busiCode);
        payOutIntfVirToVir.setRemark("??????????????????");
        payOutIntfVirToVir.setIsTurnAutomatic(0);
        payOutIntfVirToVir.setPayAccNo(pinganAcctId);
        AccountBankRel accountBankRel = iAccountBankRelService.getDefaultAccountBankRel(serviceUserId,
                EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        if(accountBankRel!=null){
            payOutIntfVirToVir.setAccNo(accountBankRel.getPinganCollectAcctId());
        }
        payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//???????????????
        iPayoutIntfService.doSavePayOutIntfVirToVir(payOutIntfVirToVir,accessToken);

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

}
