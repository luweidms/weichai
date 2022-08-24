package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsFlowService;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsService;
import com.youming.youche.order.api.order.IOilRechargeAccountFlowService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.ISubjectsInfoService;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.OilRechargeAccount;
import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import com.youming.youche.order.domain.order.OilRechargeAccountFlow;
import com.youming.youche.order.domain.order.SubjectsInfo;
import com.youming.youche.order.dto.order.PayoutIntfDto;
import com.youming.youche.order.provider.mapper.order.OilRechargeAccountFlowMapper;
import com.youming.youche.order.provider.utils.AcUtil;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.youming.youche.order.constant.BaseConstant.OIL_RECHARGE_BUSI_TYPE;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class OilRechargeAccountFlowServiceImpl extends BaseServiceImpl<OilRechargeAccountFlowMapper, OilRechargeAccountFlow> implements IOilRechargeAccountFlowService {
    @Autowired
    private IOilRechargeAccountService oilRechargeAccountService;
    @Autowired
    private IOilRechargeAccountDetailsService oilRechargeAccountDetailsService;
    @Autowired
    private IAccountBankRelService accountBankRelService;
    @Autowired
    private ISubjectsInfoService subjectsInfoService;
    @Autowired
    private IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;
    @Resource
    private ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    IOilRechargeAccountService iOilRechargeAccountService;

    @Override
    public Long recallOilAccount(Long userId, String orderNum, Long sourceUserId,
                                 Map sourceMap, Long subjectsId, Integer recallType,
                                 LoginInfo user) {
        // 获取母卡
        OilRechargeAccount motherOilRechargeAccount = oilRechargeAccountService.getOilRechargeAccount(sourceUserId);

        Iterator<Map.Entry<String, Long>> entries = sourceMap.entrySet().iterator();
        long batchId = CommonUtil.createSoNbr();
        int busiType = SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE4;// 撤回（减少）
        String busiCode = AcUtil.createOilRechargePaymentnum("OW");
        long turnAmout = 0L;
        String pinganAccId = "";
        while (entries.hasNext()) {
            Map.Entry<String, Long> entry = entries.next();
            String key = entry.getKey();// 撤回的账户明细Id
            Long value = entry.getValue();// 金额（正整数）
            turnAmout += value;
            OilRechargeAccountDetails details = oilRechargeAccountDetailsService.getById(Long.valueOf(key));
            details.setUnUseredBalance(details.getUnUseredBalance() - value);//已分配未使用
            details.setDistributedAmount(details.getDistributedAmount()-value);//已分配金额减去
            if(recallType== EnumConsts.OIL_RECHARGE_RECALL_TYPE.RECALL_TYPE2){//到转移账户，调用之前的接口
                oilRechargeAccountDetailsService.saveOrUpdate(details);
                long serviceUserId = details.getSourceUserId();
                details = oilRechargeAccountDetailsService.getOilRechargeAccountDetail(sourceUserId, "", 0L, "", SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6);

                if(details==null){
                    details = new OilRechargeAccountDetails();
                    details.setUserId(sourceUserId);
                    details.setAccountType(EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1);
                    details.setPinganAccId("");
                    details.setSourceType(SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6);
                    details.setAccountBalance(0L);
                    details.setDistributedAmount(0L);
                    details.setUnUseredBalance(0L);
                    details.setSourceUserId(serviceUserId);//服务商ID
                    details.setState(1);//有效
                    details.setTenantId(motherOilRechargeAccount.getTenantId());
                    oilRechargeAccountDetailsService.save(details);
                }
            }
            details.setAccountBalance(details.getAccountBalance() + value);
            motherOilRechargeAccount.setAccountBalance(motherOilRechargeAccount.getAccountBalance() + value);
            if (details.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1) {// 返利充值账户
                motherOilRechargeAccount.setRebateRechargeBalance((motherOilRechargeAccount.getRebateRechargeBalance() == null ? 0L : motherOilRechargeAccount.getRebateRechargeBalance()) + value);
            } else if (details.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2) {// 现金充值账户
                motherOilRechargeAccount.setCashRechargeBalance((motherOilRechargeAccount.getCashRechargeBalance() == null ? 0L : motherOilRechargeAccount.getCashRechargeBalance()) + value);
            } else if (details.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {// 授信充值账户
                motherOilRechargeAccount.setCreditRechargeBalance((motherOilRechargeAccount.getCreditRechargeBalance() == null ? 0L : motherOilRechargeAccount.getCreditRechargeBalance()) + value);
            } else if (details.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4) {// 继承账户
                motherOilRechargeAccount.setInheritBalance((motherOilRechargeAccount.getInheritBalance() == null ? 0L : motherOilRechargeAccount.getInheritBalance()) + value);
            } else if (details.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5){//专票账户
                motherOilRechargeAccount.setInvoiceOilBalance((motherOilRechargeAccount.getInvoiceOilBalance() == null ? 0L : motherOilRechargeAccount.getInvoiceOilBalance()) + value);
            } else if (details.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6){//转移账户
                motherOilRechargeAccount.setTransferOilBalance((motherOilRechargeAccount.getTransferOilBalance() == null ? 0L : motherOilRechargeAccount.getTransferOilBalance()) + value);
            }
            List<OilRechargeAccountDetailsFlow> accountFlowList = oilRechargeAccountDetailsFlowService.getOrderDetailsFlows(sourceUserId,"", SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE1,1);
            long otherBatchId = 0L;
            long matchAmt = value;
            Map otherMap = new HashMap();
            for(OilRechargeAccountDetailsFlow detailsFlow : accountFlowList){
                if(matchAmt==0L){
                    continue;
                }
                long unMatchAmount = detailsFlow.getUnMatchAmount();
                if(unMatchAmount<=0L){
                    continue;
                }
                if(unMatchAmount>matchAmt){
                    unMatchAmount=matchAmt;
                    matchAmt=0L;
                }else{
                    matchAmt-=unMatchAmount;
                }
                otherBatchId=detailsFlow.getId();
                otherMap.put(otherBatchId+"",unMatchAmount+"");
            }
            OilRechargeAccountDetailsFlow detailsFlow = new OilRechargeAccountDetailsFlow();
            detailsFlow.setRelId(details.getId()==0L?Long.valueOf(key):details.getId());
            detailsFlow.setUserId(sourceUserId);
            detailsFlow.setOrderNum(orderNum);
            detailsFlow.setBatchId(batchId);
            detailsFlow.setOtherBatchId(otherMap.toString());
            detailsFlow.setCurrentAmount(details.getAccountBalance());
            detailsFlow.setPinganAccId(details.getPinganAccId());
            if (StringUtils.isNotBlank(details.getPinganAccId())) {
                pinganAccId=details.getPinganAccId();
                AccountBankRel accountBankRel = accountBankRelService.getAcctNo(pinganAccId);
                detailsFlow.setBankAccName(accountBankRel.getAcctName());
            }
            detailsFlow.setSourceType(details.getSourceType());
            detailsFlow.setBusiName(readisUtil.getSysStaticData(OIL_RECHARGE_BUSI_TYPE,busiType + "").getCodeName());
            detailsFlow.setAmount(value);
            detailsFlow.setUnMatchAmount(value);
            detailsFlow.setBusiType(busiType);// 撤回
            detailsFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE1);
            detailsFlow.setSourceType(detailsFlow.getSourceType());
            detailsFlow.setSubjectsId(subjectsId);
            if (subjectsId > 0L) {
                SubjectsInfo subjects = subjectsInfoService.getSubjectsInfo(subjectsId, -1L);
                if (subjects != null) {
                    detailsFlow.setRemark(subjects.getSubjectsName());
                }
            }
            detailsFlow.setBusiCode(busiCode);
            oilRechargeAccountDetailsFlowService.save(detailsFlow);
            oilRechargeAccountService.save(motherOilRechargeAccount);
            oilRechargeAccountDetailsService.save(details);
        }
        // 记录母卡流水
        OilRechargeAccountFlow oilRechargeAccountFlow = new OilRechargeAccountFlow();
        oilRechargeAccountFlow.setAccountId(motherOilRechargeAccount.getId());
        oilRechargeAccountFlow.setAccountType(motherOilRechargeAccount.getAccountType());
        oilRechargeAccountFlow.setPinganAccId(pinganAccId);
        oilRechargeAccountFlow.setBatchId(batchId);
        oilRechargeAccountFlow.setSubjectsId(subjectsId);
        if (subjectsId > 0L) {
            SubjectsInfo subjects = subjectsInfoService.getSubjectsInfo(subjectsId, -1L);
            if (subjects != null) {
                oilRechargeAccountFlow.setRemark(subjects.getSubjectsName());
            }
        }
        oilRechargeAccountFlow.setUserId(sourceUserId);
        oilRechargeAccountFlow.setUserName(motherOilRechargeAccount.getUserName());
        oilRechargeAccountFlow.setBillId(motherOilRechargeAccount.getBillId());
        oilRechargeAccountFlow.setBusiType(busiType);// 撤回
        oilRechargeAccountFlow.setBusiName(readisUtil.getSysStaticData(OIL_RECHARGE_BUSI_TYPE,busiType+"").getCodeName());// 分配
        oilRechargeAccountFlow.setCurrentAmount(motherOilRechargeAccount.getAccountBalance());
        oilRechargeAccountFlow.setAmount(turnAmout);
        oilRechargeAccountFlow.setBusiCode(busiCode);
        this.save(oilRechargeAccountFlow);
        // 获取该订单子卡的流水
        List<OilRechargeAccountDetailsFlow> sonDetailsFlows = oilRechargeAccountDetailsFlowService.getOrderDetailsFlows(userId,
                orderNum, SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE3, 1);
        Map<String,Long> sonMap = new HashMap();
        long matchAmount = turnAmout;
        Map flowDetails = new HashMap();
        for(OilRechargeAccountDetailsFlow detailsFlow:sonDetailsFlows){
            if(matchAmount==0L){
                continue;
            }
            long relId=detailsFlow.getRelId();
            long amount = detailsFlow.getUnMatchAmount();
            if(amount<=0L){
                continue;
            }
            if(matchAmount>amount){
                matchAmount-=amount;
            }else{
                amount=matchAmount;
                matchAmount=0L;
            }
            detailsFlow.setUnMatchAmount(detailsFlow.getUnMatchAmount()-amount);
            detailsFlow.setMatchAmount(detailsFlow.getMatchAmount()+amount);//相当于子卡明细已经被消费（匹配）
            oilRechargeAccountDetailsFlowService.saveOrUpdate(detailsFlow);
            sonMap.put(relId+"",
                    sonMap.get(relId+"") == null ? Long.valueOf(-amount)
                            : Long.valueOf(sonMap.get(relId + "") + "")+ Long.valueOf(-amount));
            flowDetails.put(detailsFlow.getId(), -amount);
        }

        // 对于司机来说，账户减少
        oilRechargeAccountService.insertOilAccount(userId, sonMap, -turnAmout, orderNum,
                SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE4, batchId, subjectsId,
                busiCode,flowDetails,user);

        return turnAmout;
    }

    @Override
    public void payWithdrawSucess(PayoutIntfDto payoutIntfDto, String accessToken) {
        List<OilRechargeAccountFlow> accountFlows = this.getAccountFlows(payoutIntfDto.getBusiCode());
        OilRechargeAccount oilRechargeAccount = iOilRechargeAccountService.getOilRechargeAccount(accountFlows.get(0).getUserId());
        oilRechargeAccount.setProcessingBalance(oilRechargeAccount.getProcessingBalance() - payoutIntfDto.getTxnAmt());

        if (payoutIntfDto.getIsAutomatic() == 0) {// 手动打款，回调不处理
            return;
        }
        for (OilRechargeAccountFlow accountFlow : accountFlows) {
            accountFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE2);// 线上打款成功反写为已核销，或者手动点确认变成已核销
            this.saveOrUpdate(accountFlow);
            saveSysOperLog(SysOperLogConst.BusiCode.masterCard, SysOperLogConst.OperType.Update,
                    "系统自动打款,提现核销：" + CommonUtil.divide(accountFlow.getAmount()) + "元", accessToken, accountFlow.getId());
        }
    }

    @Override
    public List<OilRechargeAccountFlow> getAccountFlows(String busiCode) {
        LambdaQueryWrapper<OilRechargeAccountFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OilRechargeAccountFlow::getBusiCode, busiCode);
        return this.list(queryWrapper);
    }

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

}
