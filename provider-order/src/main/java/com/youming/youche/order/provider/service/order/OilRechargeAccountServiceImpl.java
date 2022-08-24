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
 * 服务实现类
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
        //各油来源账户余额

        Long userId = sysTenantDefService.getSysTenantDef(user.getTenantId()).getAdminUser();
        OilRechargeAccountDto oilRechargeAccount = this.getOilRechargeAccount(userId, user.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, billLookUp, user);
        //车队汇总
        Long nonDeductOilBalance =oilRechargeAccount.getNonDeductOilBalance();//非抵扣油余额
        oilRechargeAccount.setNonDeductOilBalance(nonDeductOilBalance == null || nonDeductOilBalance<0?0:nonDeductOilBalance);

        //收款账户
        Long billLookUpBalance=oilRechargeAccount.getBillLookUpBalance();//抵扣油余额
        oilRechargeAccount.setDeductOilBalance(billLookUpBalance == null || billLookUpBalance<0?0:billLookUpBalance);
        //不开票金额
        Long custOilBalance =oilRechargeAccount.getCustOilBalance();//已开票油余额=客户油+转移油+返利油
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
            throw new BusinessException("账户明细Id为空");
        }
        if (busiType == -1) {
            throw new BusinessException("请输入业务类型");
        }
        OilRechargeAccount oilRechargeAccount = this.getOilRechargeAccount(userId);
        if (oilRechargeAccount == null) {
            throw new BusinessException("查询不到子母卡");
        }
        String busiName= readisUtil.getSysStaticData(OIL_RECHARGE_BUSI_TYPE,busiType.toString()).getCodeName();
        Iterator<Map.Entry<Integer, Integer>> entries = accountMap.entrySet().iterator();
        Long turnAmountLong= Math.abs(turnAmount);
        String pinganAccId = "";//获取流水中一个非空的，set进母卡流水
        while (entries.hasNext()) {
            Map.Entry<Integer, Integer> entry = entries.next();
            String key = entry.getKey() + "";
            String value = entry.getValue() + "";

            long relId = Long.valueOf(key);
            long dealAmount = Long.valueOf(value);
            long valueLong = Math.abs(dealAmount);
            OilRechargeAccountDetails oilRechargeAccountDetails = oilRechargeAccountDetailsService.getById(relId);
            if (oilRechargeAccountDetails == null) {
                throw new BusinessException("查询不到账户明细[relId]:" + relId);
            }
//			if(busiType!=OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE5&&turnAmountLong>0L){
//				oilRechargeAccountDetails.setAccountBalance(oilRechargeAccountDetails.getAccountBalance() == null ? 0L : oilRechargeAccountDetails.getAccountBalance() + dealAmount);
//			}
            if(oilRechargeAccountDetails.getAccountBalance()<0L){
                throw new BusinessException("账户余额不足，请检查");
            }

            Iterator<Map.Entry<Integer, Integer>> entriesDetailsFlows = detailsFlows.entrySet().iterator();
            boolean isTooLong = detailsFlows.size()>2;//如果超过5个，表示太长，需要把匹配的账户信息入库
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
            log.error("业务单号【"+busiCode+"】虚拟油匹配流水："+otherMap.toString());
            if(!otherMap.isEmpty()) {
                detailsFlow.setOtherBatchId(isTooLong?"匹配账户信息太长了，请查询拓展表：OilRechargeAccountDetailsFlowExt":otherMap.toString());
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(oilRechargeAccountDetails.getPinganAccId())) {
                pinganAccId=oilRechargeAccountDetails.getPinganAccId();
                AccountBankRel accountBankRel = accountBankRelService1.getAcctNo(pinganAccId);
                detailsFlow.setBankAccName(accountBankRel.getAcctName());
            }

            if (busiType == BUSI_TYPE2) {
                // 线上打款成功反写为已核销，或者手动点确认变成已核销
                detailsFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE0);
            }
            detailsFlow.setSubjectsId(subjectsId);
            if (subjectsId > 0L) {
                SubjectsInfo subjects = subjectsInfoService.getSubjectsInfo(subjectsId, -1L);
                if (subjects != null) {
                    detailsFlow.setRemark(subjects.getSubjectsName());
                }
            }
            if(busiType==BUSI_TYPE3){//分配
                if(oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE4){//继承账户，只有增加

                }else{//其他账户，分配出去金额，分配出去为消费金额
                    oilRechargeAccountDetails.setDistributedAmount(oilRechargeAccountDetails.getDistributedAmount() == null ? 0L : oilRechargeAccountDetails.getDistributedAmount() + valueLong);
                    oilRechargeAccountDetails.setUnUseredBalance(oilRechargeAccountDetails.getUnUseredBalance() == null ? 0L : oilRechargeAccountDetails.getUnUseredBalance() + valueLong);
                }
            }

            if(busiType==BUSI_TYPE5){//加油消费
                if(oilRechargeAccountDetails.getSourceType() != SOURCE_TYPE4
                        &&oilRechargeAccountDetails.getAccountType()== EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1){//继承账户，减掉消费金额
                    //其他账户，分配出去金额，分配出去为消费金额
                    oilRechargeAccountDetails.setUnUseredBalance(oilRechargeAccountDetails.getUnUseredBalance() == null ? 0L : oilRechargeAccountDetails.getUnUseredBalance() - valueLong);
                }
                if(oilRechargeAccountDetails.getAccountType()== EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1){
                    detailsFlow.setAmount(0L);//消费对于母卡，没有减掉账户余额
                }
            }
            if(busiType==BUSI_TYPE7){//授信还款
                oilRechargeAccount.setUnRepaymentBalance(oilRechargeAccount.getUnRepaymentBalance()-valueLong);
            }
            detailsFlow.setBusiCode(busiCode);
            oilRechargeAccountDetails.setUpdateTime(LocalDateTime.now());
            if(!(busiType==BUSI_TYPE5&&turnAmountLong==0L)){
                if (oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE1) {// 返利充值
                    oilRechargeAccount.setRebateRechargeBalance(oilRechargeAccount.getRebateRechargeBalance() == null ? 0L : oilRechargeAccount.getRebateRechargeBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE2) {// 现金充值
                    oilRechargeAccount.setCashRechargeBalance(oilRechargeAccount.getCashRechargeBalance() == null ? 0L : oilRechargeAccount.getCashRechargeBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {// 授信充值
                    oilRechargeAccount.setCreditRechargeBalance(oilRechargeAccount.getCreditRechargeBalance() == null ? 0L : oilRechargeAccount.getCreditRechargeBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() ==SOURCE_TYPE4) {// 继承
                    oilRechargeAccount.setInheritBalance(oilRechargeAccount.getInheritBalance() == null ? 0L : oilRechargeAccount.getInheritBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE5) {// 专票充值
                    oilRechargeAccount.setInvoiceOilBalance(oilRechargeAccount.getInvoiceOilBalance() == null ? 0L : oilRechargeAccount.getInvoiceOilBalance() + dealAmount);
                } else if (oilRechargeAccountDetails.getSourceType() == SOURCE_TYPE6) {// 转移账户
                    oilRechargeAccount.setTransferOilBalance(oilRechargeAccount.getTransferOilBalance() == null ? 0L : oilRechargeAccount.getTransferOilBalance() + dealAmount);
                }
                oilRechargeAccountDetails.setAccountBalance(oilRechargeAccountDetails.getAccountBalance() == null ? 0L : oilRechargeAccountDetails.getAccountBalance() + dealAmount);
                oilRechargeAccount.setAccountBalance(oilRechargeAccount.getAccountBalance() == null ? 0L : oilRechargeAccount.getAccountBalance() + dealAmount);
            }
            oilRechargeAccount.setUpdateTime(LocalDateTime.now());
            detailsFlow.setUserId(userId);
            detailsFlow.setSourceType(oilRechargeAccountDetails.getSourceType());
            detailsFlow.setBusiType(busiType);
            detailsFlow.setBusiName(readisUtil.getSysStaticData(OIL_RECHARGE_BUSI_TYPE,busiType+"").getCodeName());// 分配
            detailsFlow.setCurrentAmount(oilRechargeAccountDetails.getAccountBalance());
            detailsFlow.setMatchAmount(0L);
            detailsFlow.setUnMatchAmount(0L);
            if(busiType==BUSI_TYPE3&&oilRechargeAccountDetails.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4){////分配 继承账户，只有增加
                detailsFlow.setUnMatchAmount(valueLong);
            }
            if(busiType==BUSI_TYPE6
            ){
                if(turnAmount>0L){//返利分配进来，类似充值
                    detailsFlow.setUnMatchAmount(valueLong);
                }
                if(turnAmount<0L){//母卡再分配出去
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
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.masterCardDetails,Long.valueOf(key),SysOperLogConst.OperType.Update , CommonUtil.divide(Long.valueOf(value))+"元");
        }
        // 记录母卡流水
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
            // 线上打款成功反写为已核销，或者手动点确认变成已核销
            oilRechargeAccountFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE0);
        }
        oilRechargeAccountFlow
                .setBusiName(busiName);// 分配
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
            throw new BusinessException("请输入租户id");
        }
        if (userId < 1) {
            throw new BusinessException("请输入用户id");
        }
        if (sourceUserId < 1) {
            throw new BusinessException("请输入资金来源用户id");
        }
        if (amount <= 0) {
            throw new BusinessException("输入的金额不合法");
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(orderNum)) {
            throw new BusinessException("输入的订单号不合法");
        }
        if (subjectsId < 1) {
            throw new BusinessException("请输入的科目");
        }
        if (distributionType == null || distributionType <= 0) {
            throw new BusinessException("请输入分配类型");
        }
        List<OilSourceRecord> recordList = oilSourceRecordService.getOilSourceRecordByOrderId(orderNum, tenantId);
        Map<String, Long> resultMap = this.judgeRechargeAccount(userId, sourceUserId, amount,
                orderNum, subjectsId,distributionType,baseUser);
        if (resultMap == null) {
            throw new BusinessException("母卡分配金额输出结果为空");
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
                throw new BusinessException("母卡分配明细账户主键为空");
            }
            if (value == null || value <= 0) {
                throw new BusinessException("母卡分配明细账户金额不合法");
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
                throw new BusinessException("根据id：" + key +  " 未找到母卡明细记录");
            }
            //
            if (detail.getSourceType() != null && detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1) {//返利
                noRebateOil += value;
            } else if (detail.getSourceType() != null && (detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2 ||
                    detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5 || detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6)) {//现金
                noPayOil += value;
            } else if (detail.getSourceType() != null && detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {//授信
                noCreditOil += value;
            }


        }
        if (amount != (noRebateOil + noPayOil + noCreditOil)) {
            throw new BusinessException("母卡分配明细账户金额总额不一致");
        }
        if (totalDistributionOil != amount) {
            throw new BusinessException("母卡分配明细账户金额不一致");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("noPayOil", noPayOil);
        map.put("noRebateOil", noRebateOil);
        map.put("noCreditOil", noCreditOil);
        return map;
    }

    /**
     * niejeiwei
     * 司机小程序
     * 客户油-小程序接口
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
        // 油账户明细
        Page<OilRechargeAccountDto> oilRechargeAccountDtoPage = baseMapper.queryOilAccountDetails(page, userId, billId, tenantName, userType);
        return oilRechargeAccountDtoPage;
    }

    /**
     * niejeiwei
     * 司机小程序
     * 授信列表-小程序接口
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
            // 固定金额
            if (tenantServiceRel.getQuotaAmtL() !=null) {
                Double q = tenantServiceRel.getQuotaAmtL() / 100.00;
                tenantServiceRel.setQuotaAmtStr(q.toString());
            } else {
                tenantServiceRel.setQuotaAmtL(null);
                tenantServiceRel.setQuotaAmtStr("不限额");
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
     * 司机小程序
     * 预存资金-小程序接口
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
            type=1;//1充值账户 2已开票账户
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
            //查询用户所有账户金额
            OrderAccountBalanceDto orderAccountBalance = iOrderAccountService.getOrderAccountBalance(sysTenantDef.getAdminUser(),
                    OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION, loginInfo.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            OrderAccountOutVo orderAccountOut = null;
            if(orderAccountBalance!=null){
                orderAccountOut =orderAccountBalance.getOa()!=null?orderAccountBalance.getOa():null;
            }
            OilRechargeAccountDetailsOutDto out = new OilRechargeAccountDetailsOutDto();
            long custOil = orderAccountOut.getTotalOilBalance();//客戶油
            if(custOil>0L){
                out.setAccountBalance(custOil);
                out.setSourceType(0);//客戶油标识
                out.setSourceTypeName("客户油");//客戶油标识
                listOut.add(out);
            }

        }
        for (OilRechargeAccountDto entity : lists) {
            OilRechargeAccountDetailsOutDto out = new OilRechargeAccountDetailsOutDto();
            BeanUtil.copyProperties( entity,out);
            if(org.apache.commons.lang3.StringUtils.isNotBlank(out.getPinganAccId())){
                //不需要修改
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
            throw new BusinessException("请选择要移除的账户");
        }
        OilRechargeAccountDetails oilRechargeAccountDetails = oilRechargeAccountDetailsService.getById(accountId);
        if (oilRechargeAccountDetails.getAccountBalance() > 0L) {
            throw new BusinessException("账户余额大于0，不允许移除");
        }
        if (oilRechargeAccountDetails.getUnUseredBalance() > 0L) {
            throw new BusinessException("账户已分配金额大于0，不允许移除");
        }
        //1查询作为收款 2 查询作为付款
        Long count = payoutIntfMapper
                .countOnFlowPayoutInfo(oilRechargeAccountDetails.getPinganAccId(),
                        new String[] { OrderAccountConst.TXN_TYPE.XX_TXN_TYPE }, new String[] { HttpsMain.respCodeZero,
                                HttpsMain.respCodeFail, HttpsMain.netTimeOutFail, HttpsMain.respCodeZero },
                        EnumConsts.SubjectIds.SUBJECTIDS1131, 2);

        if (count > 0L) {
            throw new BusinessException("账户充值中，不允许移除");
        }
        try {
            oilRechargeAccountDetails.setState(0);
            oilRechargeAccountDetailsService.saveOrUpdate(oilRechargeAccountDetails);
        } catch (Exception ex) {
            throw new BusinessException("移除账户异常：" + ex.getMessage());
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
            throw new BusinessException("业务升级，油充值业务暂时停用，后续启用另行通知!");
        }
        log.info("充值账户:" + vo.getPinganAccId() + ",充值金额：" + vo.getAmount() + ",其中现金充值：" + (vo.getAmount() - vo.getCreditAmount()) + ",授信充值："
                + vo.getCreditAmount() + ",票据类型（1油票,2运输专票）：" + vo.getOilRechargeBillType());
        OilRechargeAccount oilRechargeAccount = this.getOilRechargeAccount(userId);
        UserDataInfo fleetUser = iUserDataInfoService.getUserDataInfo(userId);// 车队用户信息
        if (fleetUser == null) {
            throw new BusinessException("查询不到车队用户信息");
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        if (sysTenantDef == null) {
            throw new BusinessException("查询不到车队信息");
        }
        long soNbr = CommonUtil.createSoNbr();
        String busiCode = AcUtil.createOilRechargePaymentnum("OP");
        if (oilRechargeAccount == null) {// 创建母卡
            oilRechargeAccount = new OilRechargeAccount();
            oilRechargeAccount.setUserId(userId);
            oilRechargeAccount.setUserName(fleetUser.getLinkman());
            oilRechargeAccount.setBillId(sysTenantDef.getLinkPhone());
            oilRechargeAccount.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1);// 母卡1
            // 子卡2
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
        if(vo.getOilRechargeBillType()== SysStaticDataEnum.OIL_RECHARGE_BILL_TYPE.STATE2&&vo.getCreditAmount()>0L){//专票不能用授信充值
            throw new BusinessException("专票不能用授信充值");
        }

        long currentAmount = oilRechargeAccount.getAccountBalance()+vo.getAmount();//操作后金额
        if (vo.getAmount() <= 0L) {
            throw new BusinessException("充值金额不大于0，请重新填写");
        }
        if (vo.getCreditAmount() > vo.getAmount()) {
            throw new BusinessException("授信额度不能大于充值金额");
        }
        long cashAmount = vo.getAmount() - vo.getCreditAmount();
        AgentServiceDto agentService = iServiceInfoService.getAgentService(sysTenantDef.getId(), ServiceConsts.AGENT_SERVICE_TYPE.OIL);
        if (agentService == null || (agentService.getTenantAgentServiceRel()==null && agentService.getAgentServiceInfo()==null && agentService.getServiceInfo()==null)) {
            throw new BusinessException("查询不到油品公司信息");
        }
        TenantAgentServiceRel tenantAgentServiceRel = agentService.getTenantAgentServiceRel();
        AgentServiceInfo agentServiceInfo = agentService.getAgentServiceInfo();
        if (tenantAgentServiceRel == null||agentServiceInfo==null) {
            throw new BusinessException("查询不到油品公司信息");
        }
        if (vo.getCreditAmount() > 0L) {// 授信额度充值
            long quotaAmt = tenantAgentServiceRel.getQuotaAmt() == null ? 0L : tenantAgentServiceRel.getQuotaAmt();
            long useQuotaAmt = tenantAgentServiceRel.getUseQuotaAmt() == null ? 0L
                    : tenantAgentServiceRel.getUseQuotaAmt();
            if (quotaAmt <= 0L || quotaAmt - useQuotaAmt <= 0L) {
                throw new BusinessException("信用额度不足，不允许信用充值");
            }
            // 充值账户增加
            rechargeInsetAccDet(oilRechargeAccount, userId, vo.getPinganAccId(), vo.getCreditAmount(),
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3, "", vo.getServiceId(), soNbr, busiCode,"",accessToken);
            // 已使用授信增加
            tenantAgentServiceRel.setUseQuotaAmt(useQuotaAmt + vo.getCreditAmount());
        }

        if (cashAmount > 0L) {// 现金充值，需要生成打款记录
            if(vo.getOilRechargeBillType()== SysStaticDataEnum.OIL_RECHARGE_BILL_TYPE.STATE2){
                BillSetting billSetting = iBillSettingService.getBillSetting(sysTenantDef.getId());
                String vehicleAffiliation = billSetting.getBillMethod()+"";// 开票平台
                //如果开票平台是luge的，判断luge油站是否配置，如果没有配置不允许充值
                String sysParameluge = readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC,
                        SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE+"").getCodeName();
                String sysPre = iBillPlatformService.getPrefixByUserId(Long.valueOf(vehicleAffiliation));
                if(sysParameluge.equals(sysPre)&& org.apache.commons.lang3.StringUtils.isBlank(agentServiceInfo.getLgOilStationId())){
                    throw new BusinessException("查询不到油品公司配置的路歌油站，请咨询客服人员！");
                }
                rechargeInsetAccDet(oilRechargeAccount, userId, vo.getPinganAccId(), cashAmount,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5, "", vo.getServiceId(), soNbr, busiCode,vehicleAffiliation,accessToken);
            }else{
                rechargeInsetAccDet(oilRechargeAccount, userId, vo.getPinganAccId(), cashAmount,
                        SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2, "", vo.getServiceId(), soNbr, busiCode,"1",accessToken);
            }
        }
        // 记录母卡流水
        OilRechargeAccountFlow oilRechargeAccountFlow = new OilRechargeAccountFlow();
        oilRechargeAccountFlow.setAccountId(oilRechargeAccount.getId());
        oilRechargeAccountFlow.setAccountType(oilRechargeAccount.getAccountType());
        oilRechargeAccountFlow.setPinganAccId(vo.getPinganAccId());
        oilRechargeAccountFlow.setBatchId(soNbr);
        oilRechargeAccountFlow.setUserId(userId);
        oilRechargeAccountFlow.setUserName(sysTenantDef.getName());
        oilRechargeAccountFlow.setBillId(sysTenantDef.getLinkPhone());
        oilRechargeAccountFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1);// 充值
        oilRechargeAccountFlow.setBusiName("充值");// 充值
        oilRechargeAccountFlow.setCurrentAmount(currentAmount);
        oilRechargeAccountFlow.setAmount(vo.getAmount());
        oilRechargeAccountFlowService.save(oilRechargeAccountFlow);
        oilRechargeAccountFlow.setBusiCode(busiCode);
        oilRechargeAccountFlowService.save(oilRechargeAccountFlow);
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.masterCardFlow,oilRechargeAccountFlow.getId(),SysOperLogConst.OperType.Add ,
                "充值："+CommonUtil.divide(vo.getAmount())+"元");
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.masterCard,
                oilRechargeAccount.getId(),SysOperLogConst.OperType.Update ,"充值"+CommonUtil.divide(vo.getAmount())+"元");
    }


    public  OilRechargeAccountDto getRechargeInfo(Long adminUser){
        // 对公账户列表和余额
        List<BankBalanceInfo> bankBalanceInfos = iAccountBankRelService.queryBankBalanceInfoList( SysStaticDataEnum.USER_TYPE.ADMIN_USER,adminUser,
                BUSINESS_PAYABLE_ACCOUNT,null,null,null,null);
        String oilRechargeLowest = readisUtil.getSysCfg(EnumConsts.SysCfg.OIL_RECHARGE_LOWEST, "0").getCfgValue();
        long lowest = Long.parseLong(oilRechargeLowest);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(adminUser);
        if (sysTenantDef == null) {
            throw new BusinessException("查询不到车队信息");
        }
        AgentServiceDto agentService = iServiceInfoService.getAgentService(sysTenantDef.getId(), ServiceConsts.AGENT_SERVICE_TYPE.OIL);
//        if (objects == null || objects.length == 0) {
        if (agentService == null || ( agentService.getTenantAgentServiceRel()==null && agentService.getAgentServiceInfo()==null && agentService.getServiceInfo()==null)) {
            throw new BusinessException("尊敬的【"+sysTenantDef.getName()+"】,本车队还没与油供应商签订协议，暂不能充值，请咨询客服人员。 ");
        }
        TenantAgentServiceRel tenantAgentServiceRel = agentService.getTenantAgentServiceRel();
        AgentServiceInfo agentServiceInfo = agentService.getAgentServiceInfo();
        ServiceInfo serviceInfo = agentService.getServiceInfo();
        OilRechargeAccountDto dto = new OilRechargeAccountDto();
        dto.setBankBalanceInfos(bankBalanceInfos);// 银行列表及每个账户对应余额
        dto.setLowestAmount(lowest);// 油卡充值最低限制
        AccountBankRel accountBankRel = iAccountBankRelService.getDefaultAccountBankRel(agentServiceInfo.getServiceUserId(),
                EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        if(accountBankRel==null){
            throw new BusinessException("油品没有绑定对公账户，暂不能充值，请咨询客服人员。");
        }
        dto.setCreditBalance((tenantAgentServiceRel.getQuotaAmt() == null ? 0L : tenantAgentServiceRel.getQuotaAmt())
                - (tenantAgentServiceRel.getUseQuotaAmt() == null ? 0L
                : tenantAgentServiceRel.getUseQuotaAmt()));// 可用授信额度
        dto.setServiceId(agentServiceInfo.getServiceUserId());// 服务商Id
        dto.setServiceName(serviceInfo.getServiceName());// 服务商名称
        dto.setReceivedBankInfo(accountBankRel);// 充值收款账户平安信息
        return dto;
    }
    @Override
    public OilRechargeAccountDto getOilRechargeAccount(Long userId, Long tenantId, Integer userType, String billLookUp, LoginInfo user) {
        OrderAccountBalanceDto map = orderAccountService.getOrderAccountBalance(userId, OrderAccountConst.ORDER_BY.OIL_BALANCE, tenantId, userType);
        if (map == null) {
            throw new BusinessException("未找到用户账户信息");
        }
        OilRechargeAccountDto retMap=new OilRechargeAccountDto();
        OilRechargeAccount oilRechargeAccount = this.getOilRechargeAccount(userId);
        OrderAccountOutVo out = map.getOa();
        if(out!=null){
            retMap.setCustOilBalance(out.getTotalOilBalance());//客户油（已开票）
        }
        if(oilRechargeAccount!=null){
            retMap.setNonDeductOilBalance((oilRechargeAccount.getCashRechargeBalance()==null?0L:oilRechargeAccount.getCashRechargeBalance())
                    +(oilRechargeAccount.getCreditRechargeBalance()==null?0L:oilRechargeAccount.getCreditRechargeBalance()));//查充值账户的非抵扣油票余额
            retMap.setRebateOilBalance(oilRechargeAccount.getRebateRechargeBalance()==null?0L:oilRechargeAccount.getRebateRechargeBalance());//返利油（已开票）
            retMap.setTransferOilBalance(oilRechargeAccount.getTransferOilBalance()==null?0L:oilRechargeAccount.getTransferOilBalance());//转移油余额（已开票）
            retMap.setInvoiceOilBalance(oilRechargeAccount.getInvoiceOilBalance()==null?0L:oilRechargeAccount.getInvoiceOilBalance());//抵扣油票余额(运输专票)
        }
        if(StringUtils.isNotBlank(billLookUp)){
            List<AccountBankRel> accountBankRels = accountBankRelService1.getBankInfo(user.getId(),
                    billLookUp, "", EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.ADMIN_USER);

            if(accountBankRels!=null&&accountBankRels.size()>0L){
                Long billLookUpBalance = 0L;//抵扣运输专票
                Long cashRechargeBalance = 0L;//非抵扣票现金充值
                Long creditRechargeBalance = 0L;//非抵扣票授信充值
                for(AccountBankRel accountBankRel:accountBankRels){
                    String pinganAccId = accountBankRel.getPinganPayAcctId();

                    List<OilRechargeAccountDetails> billLookUpDetail = oilRechargeAccountDetailsService.getOilRechargeAccountDetail(userId, pinganAccId, 0L, SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5,"");//抵扣票的账户明细
                    if(billLookUpDetail!=null&&billLookUpDetail.size()>0){
                        for(OilRechargeAccountDetails detail:billLookUpDetail){
                            billLookUpBalance+=(detail.getAccountBalance()==null?0L:detail.getAccountBalance());
                        }
                    }

                    List<OilRechargeAccountDetails> cashRechargeDetails = oilRechargeAccountDetailsService.getOilRechargeAccountDetail(userId, pinganAccId, 0L, SOURCE_TYPE2,"");//非抵扣票现金的账户明细
                    if(cashRechargeDetails!=null&&cashRechargeDetails.size()>0){
                        for(OilRechargeAccountDetails detail:cashRechargeDetails){
                            cashRechargeBalance+=(detail.getAccountBalance()==null?0L:detail.getAccountBalance());
                        }
                    }

                    List<OilRechargeAccountDetails> creditRechargeDetails = oilRechargeAccountDetailsService.getOilRechargeAccountDetail(userId, pinganAccId, 0L, SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3,"");//非抵扣票授信的账户明细
                    if(creditRechargeDetails!=null&&creditRechargeDetails.size()>0){
                        for(OilRechargeAccountDetails detail:creditRechargeDetails){
                            creditRechargeBalance+=(detail.getAccountBalance()==null?0L:detail.getAccountBalance());
                        }
                    }
                }
                retMap.setBillLookUpBalance(billLookUpBalance);//某收票主体充值账户抵扣油票余额
                retMap.setUnBillLookUpBalance(cashRechargeBalance+creditRechargeBalance);//某收票主体充值账户非抵扣油票余额
            }
        }

        return retMap;
    }




    public Map<String, Long> judgeRechargeAccount(long userId, long sourceUserId, long amount, String orderNum,
                                                  long subjectsId,Integer billType,LoginInfo user)  {//1.承运商开票账户，2.抵扣票账户，3.已开票账户

        OilRechargeAccount oilRechargeAccount = oilRechargeAccountService.getOilRechargeAccount(sourceUserId);
        if (oilRechargeAccount==null||oilRechargeAccount.getAccountBalance() <= 0L) {
            throw new BusinessException("电子油卡的充值账户余额不足，请先充值。");
        }
        long waitMatchAmount = amount;
        long batchId = CommonUtil.createSoNbr();
  //  List<OilRechargeAccountDetailsFlow> flowList = oilRechargeAccountDetailsFlowService.getUnMatchedFlowsASC(sourceUserId);
        String busiCode = AcUtil.createOilRechargePaymentnum("OD");
        Map matchAccount = new HashMap();
        Map flowDetails = new HashMap();
        if(billType==EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1){//承运商开票账户，包含现金充值和授信充值账户
            if(waitMatchAmount>0L){//先匹配授信
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
            if(waitMatchAmount>0L){//匹配现金充值
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
        }else if(billType==EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE2){//抵扣票账户
            if(waitMatchAmount>0L){//匹配抵扣票现金充值账户
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
            if(waitMatchAmount>0L){//匹配返利账户
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
            if(waitMatchAmount>0L){//匹配转移账户
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
            throw new BusinessException("油账户余额不足");
        }

        //资金接收方账户
        UserDataInfo userInfo = userDataInfoService.get(userId);// 车队用户信息
        if (userInfo == null) {
            throw new BusinessException("查询不到用户信息");
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

        // 对于司机来说，相当于继承充值
        oilRechargeAccountService.insertOilAccount(userId, receivableMap, amount, orderNum, BUSI_TYPE3,
                batchId, subjectsId, busiCode,addFlowDetails,user);

        if(billType==2){//抵扣票账户
            //判断资金渠道是56K的，调用56K接口(保存到表里，进程异步去同步)
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
                        oilRecord56k.setBusiType(EnumConsts.BUSI_TYPE.BUSI_TYPE1);// 分配
                        oilRecord56k.setOrderId(Long.valueOf(orderNum));
                        oilRecord56k.setDealDate(LocalDateTime.now());
                        oilRecord56k.setAmount(dealAmt);
                        oilRecord56k.setSyncState(EnumConsts.SYNC_STATE.SYNC_STATE0);
                        BillPlatform billPlatform = billPlatformService
                                .queryAllBillPlatformByUserId(Long.valueOf(vehicleAffiliation));
                        if (billPlatform != null) {
                            String billHead = billPlatform.getBillHead();
                            if ("湖北华石运通物流科技有限公司".equals(billHead)) {
                                oilRecord56k.setIdentifier(0);// 0 湖北 1 江西
                            } else if ("浮梁县华石物流科技有限公司".equals(billHead)) {
                                oilRecord56k.setIdentifier(1);// 0 湖北 1 江西
                            }
                        } else {
                            log.error("记录分配订单同步56K记录表失败：票据中心匹配不到资金渠道：" + vehicleAffiliation);
                            oilRecord56k.setSyncState(EnumConsts.SYNC_STATE.SYNC_STATE2);
                        }

                        oilRecord56k.setSyncMsg("初始化");
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
        String billLookUp="";//预订单开票主体匹配充值账户
        List<String> pinganAccIds = new ArrayList<String>();
        if(sourceType== SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5){//抵扣票
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
                        &&pinganAccIds.size()>0&&!pinganAccIds.contains(pinganAccId)){//开票主体不一致，不匹配
                    continue;
                }
                if (waitMatchAmount <= 0L||(org.apache.commons.lang3.StringUtils.isNoneBlank(billLookUp)&&!pinganAccIds.contains(pinganAccId))) {// 不要匹配继承的油余额
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

    // 充值账户增加
    public void rechargeInsetAccDet(OilRechargeAccount oilRechargeAccount, long userId, String pinganAcctId,
                                    long amount, int sourceType, String orderNum, Long serviceUserId, long soNbr,
                                    String busiCode,String vehicleAffiliation,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (amount == 0L) {
            throw new BusinessException("金额0，无法操作账户");
        }
        UserDataInfo fleetUser = iUserDataInfoService.getUserDataInfo(userId);// 车队用户信息

        if (fleetUser == null) {
            throw new BusinessException("查询不到车队用户信息");
        }
        long tenantId = -1L;
        boolean isTenant = false;
        if (sourceType != SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1
                && sourceType != SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4) {//如果是分配继承账户，可以能是司机
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
            if (sysTenantDef == null) {
                throw new BusinessException("查询不到车队信息");
            }
            tenantId = sysTenantDef.getId();
            isTenant = true;
        }


        String oilAffiliation = vehicleAffiliation;// 承运商开票
        List<OilRechargeAccountDetails> detailsList = iOilRechargeAccountDetailsServicel.getOilRechargeAccountDetail(userId,
                pinganAcctId, serviceUserId, sourceType, vehicleAffiliation);
        OilRechargeAccountDetailsFlow oilRechargeAccountDetailsFlow = new OilRechargeAccountDetailsFlow();
        if (oilRechargeAccount == null) {
            oilRechargeAccount = new OilRechargeAccount();
            oilRechargeAccount.setUserId(userId);
            if (isTenant) {
                oilRechargeAccount.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1);//母卡
            } else {
                oilRechargeAccount.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2);//子卡
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
            oilRechargeAccountDetail.setState(1);//有效
            oilRechargeAccountDetail.setTenantId(tenantId);
        }


        long currentAmount = oilRechargeAccountDetail.getAccountBalance() + amount;//操作后金额
        log.info("充值前字母卡明细：" + oilRechargeAccount);
        log.info("充值前账户明细：" + oilRechargeAccountDetail);

        if (sourceType != SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2
                && sourceType != SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5) {// 如果不是现金充值，账户上的金额直接加上；如果是现金充值，需要打款成功之后再把金额添加到账户上
            oilRechargeAccount.setAccountBalance(oilRechargeAccount.getAccountBalance() + amount);
            oilRechargeAccountDetail.setAccountBalance(oilRechargeAccountDetail.getAccountBalance() + amount);
        }

        if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1) {// 返利充值
            oilRechargeAccount.setRebateRechargeBalance(oilRechargeAccount.getRebateRechargeBalance() + amount);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE6);

        } else if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2) {// 现金充值

            this.payCashRecharge(pinganAcctId, fleetUser, serviceUserId, amount, vehicleAffiliation, oilAffiliation,
                    tenantId, soNbr, busiCode,accessToken);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1);
        } else if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5) {// 现金充值(专票)
            BillSetting billSetting = iBillSettingService.getBillSetting(tenantId);
            vehicleAffiliation = billSetting.getBillMethod() + "";// 开票平台
            oilAffiliation = billSetting.getBillMethod() + "";// 开票平台
            this.payCashRecharge(pinganAcctId, fleetUser, serviceUserId, amount, vehicleAffiliation, oilAffiliation,
                    tenantId, soNbr, busiCode,accessToken);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1);
        } else if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {// 授信充值
            if (org.apache.commons.lang3.StringUtils.isBlank(pinganAcctId)) {
                throw new BusinessException("授信充值虚拟账户不能为空");
            }
            oilRechargeAccount.setUnRepaymentBalance(oilRechargeAccount.getUnRepaymentBalance() + amount);
            oilRechargeAccount.setCreditRechargeBalance(oilRechargeAccount.getCreditRechargeBalance() + amount);
            oilRechargeAccountDetailsFlow.setBusiType(SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1);

        } else if (sourceType == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4) {// 继承
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
        oilRechargeAccountDetailsFlow.setRemark("油卡充值");
        oilRechargeAccountDetailsFlow.setBatchId(soNbr);
        oilRechargeAccountDetailsFlowService.save(oilRechargeAccountDetailsFlow);
        sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.masterCardDetails, oilRechargeAccountDetail.getId(),
                SysOperLogConst.OperType.Update, "充值" + CommonUtil.divide(amount) + "元");
        log.info("充值后字母卡明细：" + oilRechargeAccount);
        log.info("充值后账户明细：" + oilRechargeAccountDetail);
    }

    public void payCashRecharge(String pinganAcctId, UserDataInfo fleetUser, Long serviceUserId, Long amount,
                                String vehicleAffiliation, String oilAffiliation, Long tenantId, Long soNbr,
                                String busiCode,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        // 现金充值
        if (org.apache.commons.lang3.StringUtils.isBlank(pinganAcctId)) {
            throw new BusinessException("现金充值虚拟账户不能为空");
        }
        UserDataInfo serviceUser = iUserDataInfoService.getUserDataInfo(serviceUserId);// 服务商用户信息
        if (serviceUser == null) {
            throw new BusinessException("查询不到服务商用户信息");
        }
        // 现金充值需要打款成功之后反写
        // oilRechargeAccount.setCashRechargeBalance(oilRechargeAccount.getCashRechargeBalance()+amount);
        // 生成打款流水

        long receSubjectId = EnumConsts.SubjectIds.SUBJECTIDS1131;
        long paysubjectId = EnumConsts.SubjectIds.SUBJECTIDS1132;
        // 充值打款
        List<BusiSubjectsRel> busiSubjectsRelList = null;
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        // 收款方应收逾期增加
        BusiSubjectsRel add = new BusiSubjectsRel();
        add.setSubjectsId(receSubjectId);
        add.setAmountFee(amount);
        // 付款方应付逾期增加
        BusiSubjectsRel addPay = new BusiSubjectsRel();
        addPay.setSubjectsId(paysubjectId);
        addPay.setAmountFee(amount);

        busiList.add(add);
        busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_RECHARGE, busiList);

        int payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
        int receUserType = SysStaticDataEnum.USER_TYPE.SERVICE_USER;

        // 服务商应收
        OrderAccount serviceAccount = iOrderAccountService.queryOrderAccount(serviceUserId, vehicleAffiliation, 0L, tenantId,
                oilAffiliation,receUserType);
        // 写入账户明细表并修改账户金额费用
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.RECHARGE_TYPE_CODE, EnumConsts.PayInter.OIL_RECHARGE,
                serviceUser.getId(), serviceUser.getLinkman(), serviceAccount, busiSubjectsRelList, soNbr, 0L,
                serviceUser.getLinkman(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
        // 车队应付逾期
        OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(fleetUser.getId(), vehicleAffiliation, 0L,
                tenantId, oilAffiliation,payUserType);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();

        BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(paysubjectId, amount);
        fleetBusiList.add(payableOverdueRel);
        // 计算费用集合
        List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_RECHARGE,
                fleetBusiList);
        // 写入账户明细表并修改账户金额费用
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
        //会员体系改造开始
        payOutIntfVirToVir.setUserType(receUserType);
        payOutIntfVirToVir.setPayUserType(payUserType);
        //会员体系改造结束
        payOutIntfVirToVir.setTxnAmt(amount);
        payOutIntfVirToVir.setBusiId(EnumConsts.PayInter.OIL_RECHARGE);// 油卡充值账户
        payOutIntfVirToVir.setObjId(
                org.apache.commons.lang3.StringUtils.isNotBlank(serviceUser.getMobilePhone()) ? Long.valueOf(serviceUser.getMobilePhone()) : 0L);
        payOutIntfVirToVir.setObjType(SysStaticDataEnum.OBJ_TYPE.RECHARGE + "");
        payOutIntfVirToVir.setPayObjId(payAccount.getUserId());
        payOutIntfVirToVir.setCreateDate(LocalDateTime.now());
        payOutIntfVirToVir.setWaitBillingAmount(0L);// 默认是0
        payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.SERVICE);// 车队打给服务商
        payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
        payOutIntfVirToVir.setSubjectsId(receSubjectId);
        payOutIntfVirToVir.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
        if(VEHICLE_AFFILIATION1.equals(vehicleAffiliation)){
            payOutIntfVirToVir.setIsNeedBill(OrderConsts.FUNDS_IS_NEED_BILL.FUNDS_IS_NEED_BILL6);//油票
        }else{
            payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);//HA虚拟
        }
        payOutIntfVirToVir.setPayTenantId(tenantId);
        payOutIntfVirToVir.setOilAffiliation(oilAffiliation);
        payOutIntfVirToVir.setBusiCode(busiCode);
        payOutIntfVirToVir.setRemark("电子油卡充值");
        payOutIntfVirToVir.setIsTurnAutomatic(0);
        payOutIntfVirToVir.setPayAccNo(pinganAcctId);
        AccountBankRel accountBankRel = iAccountBankRelService.getDefaultAccountBankRel(serviceUserId,
                EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        if(accountBankRel!=null){
            payOutIntfVirToVir.setAccNo(accountBankRel.getPinganCollectAcctId());
        }
        payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//默认为手动
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
