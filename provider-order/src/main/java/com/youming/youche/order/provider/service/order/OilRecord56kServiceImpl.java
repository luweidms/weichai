package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IConsumeOilFlowExtService;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsFlowService;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOilRecord56kService;
import com.youming.youche.order.api.order.IOilSourceRecordService;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;
import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import com.youming.youche.order.domain.order.OilRecord56k;
import com.youming.youche.order.domain.order.OilSourceRecord;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.OilRecord56kMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.MatchAmountUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.system.api.ISysTenantDefService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class OilRecord56kServiceImpl extends BaseServiceImpl<OilRecord56kMapper, OilRecord56k> implements IOilRecord56kService {
    private static final Logger log = LoggerFactory.getLogger(OilRecord56kServiceImpl.class);
    @Autowired
    private IOilSourceRecordService oilSourceRecordService;
    @Autowired
    private IOilRechargeAccountDetailsService oilRechargeAccountDetailsService;
    @Autowired
    private IConsumeOilFlowExtService consumeOilFlowExtService;
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @Autowired
    private IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;
    @Resource
    private ReadisUtil readisUtil;
    @Autowired
    private IBillPlatformService billPlatformService;
    @Autowired
    private IOilRechargeAccountService oilRechargeAccountService;
    @Resource
    private MatchAmountUtil matchAmountUtil;


    @Override
    public void consumeOil(String orderNum, Long amount, Long tenantId, ParametersNewDto inParam,LoginInfo user) {

        if (tenantId == null || tenantId < 1) {
            throw new BusinessException("???????????????id");
        }
        if (amount <= 0) {
            throw new BusinessException("?????????????????????");
        }
        if (StringUtils.isBlank(orderNum)) {
            throw new BusinessException("???????????????????????????");
        }
        if (inParam == null) {
            throw new BusinessException("????????????????????????");
        }
        ConsumeOilFlow cof =inParam.getConsumeOilFlow();
        if (cof == null) {
            throw new BusinessException("???????????????????????????");
        }
        ConsumeOilFlowExt ext =inParam.getConsumeOilFlowExt();
        if (ext == null) {
            throw new BusinessException("?????????????????????????????????");
        }
        List<OilSourceRecord> list = oilSourceRecordService.getOilSourceRecordNoPayBalance(orderNum, tenantId);
        if (list == null || list.size() <= 0) {
            throw new BusinessException("??????????????????" + orderNum + " ??????id???" + tenantId + " ??????????????????????????????????????????");
        }
        if (inParam == null) {
            throw new BusinessException("????????????????????????");
        }
        long userId = cof.getOtherUserId();
        long subjectsId = cof.getSubjectsId();
        long matchAmount = 0;
        long matchNoPayOil = 0;
        long matchNoRebateOil = 0;
        long matchNoCreditOil = 0;
        String orderId = "";
        Long teantId = 0l;
        Long sourceTenantId = 0l;
        OrderOilSource oilSource = inParam.getOilSource();
        RechargeOilSource rechargeOilSource = inParam.getRechargeOilSource();
        if (oilSource != null) {
            OrderOilSource source = oilSource;
            matchAmount = source.getMatchAmount();
            matchNoPayOil = source.getMatchNoPayOil() == null ? 0 : source.getMatchNoPayOil();
            matchNoRebateOil = source.getMatchNoRebateOil() == null ? 0 : source.getMatchNoRebateOil();
            matchNoCreditOil = source.getMatchNoCreditOil() == null ? 0 : source.getMatchNoCreditOil();
            orderId = source.getSourceOrderId() + "";
            teantId = source.getTenantId();
            sourceTenantId = source.getSourceTenantId();
        } else if (rechargeOilSource != null) {
            RechargeOilSource source = rechargeOilSource;
            matchAmount = source.getMatchAmount();
            matchNoPayOil = source.getMatchNoPayOil() == null ? 0 : source.getMatchNoPayOil();
            matchNoRebateOil = source.getMatchNoRebateOil() == null ? 0 : source.getMatchNoRebateOil();
            matchNoCreditOil = source.getMatchNoCreditOil() == null ? 0 : source.getMatchNoCreditOil();
            orderId = source.getSourceOrderId() + "";
            teantId = source.getTenantId();
            sourceTenantId = source.getSourceTenantId();
        } else {
            throw new BusinessException("?????????????????????");
        }
        if (matchAmount != amount) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        List<OilSourceRecord> noPayOilList = new ArrayList<OilSourceRecord>();
        List<OilSourceRecord> noRebateOilList = new ArrayList<OilSourceRecord>();
        List<OilSourceRecord> noCreditOilList = new ArrayList<OilSourceRecord>();
        for (OilSourceRecord osr : list) {
            OilRechargeAccountDetails detail = oilRechargeAccountDetailsService.getById(osr.getRechargeId());
            if (detail == null) {
                throw new BusinessException("??????id???" + osr.getRechargeId() +  " ???????????????????????????");
            }
            if (detail.getSourceType() != null && detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1) {//??????
                noRebateOilList.add(osr);
            } else if (detail.getSourceType() != null && (detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2
                    || detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5 || detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6)) {//??????
                noPayOilList.add(osr);
            } else if (detail.getSourceType() != null && detail.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {//??????
                noCreditOilList.add(osr);
            }
        }
        Long oilComUserId = null;
        matchAmountUtil.matchAmountOilSourceRecord(matchNoPayOil, 0, 0, noPayOilList);
        matchAmountUtil.matchAmountOilSourceRecord(matchNoRebateOil, 0, 0,  noRebateOilList);
        matchAmountUtil.matchAmountOilSourceRecord(matchNoCreditOil, 0, 0, noCreditOilList);
        Map<String, Long> oilMap = new HashMap<String, Long>();
        for (OilSourceRecord osr : list) {
            if (osr.getMatchAmount() != null && osr.getMatchAmount().longValue() > 0) {
                OilRechargeAccountDetails detail = oilRechargeAccountDetailsService.getById(osr.getRechargeId());
                if (detail == null) {
                    throw new BusinessException("??????id???" + osr.getRechargeId() +  " ???????????????????????????");
                }
                oilComUserId = detail.getSourceUserId();
                osr.setNoPayBalance(osr.getNoPayBalance() - osr.getMatchAmount());
                osr.setPaidBalance((osr.getPaidBalance() == null ? 0l : osr.getPaidBalance()) + osr.getMatchAmount());
                osr.setUpdateDate(LocalDateTime.now());
                oilSourceRecordService.saveOrUpdate(osr);
                oilMap.put(String.valueOf(osr.getRechargeId()), osr.getMatchAmount());
            }
        }
        ext.setOilComUserId(oilComUserId);
        consumeOilFlowExtService.saveOrUpdate(ext);
        if (teantId.longValue() != sourceTenantId.longValue()) {
            userId =  sysTenantDefService.getSysTenantDef(teantId).getAdminUser();
            if (userId <= 0L) {
                throw new BusinessException("????????????id???" + teantId + "???????????????????????????id!");
            }
        }
        Long sourceUserId = sysTenantDefService.getSysTenantDef(teantId).getAdminUser();
        if (sourceUserId == null || sourceUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        this.consumeRechargeOil(userId, sourceUserId, oilMap,amount, orderNum, subjectsId,cof.getOrderId(),user);

    }


    public void consumeRechargeOil(Long userId, Long sourceUserId, Map<String,Long> sourceAccountMap,
                                   Long amount, String orderNum, Long subjectsId, String busiCode, LoginInfo user){
        log.info("?????????????????????Id:" + userId + ",????????????Id???" + sourceUserId + ",?????????????????????" + sourceAccountMap + ",???????????????"
                + amount + ",????????????" + orderNum+",?????????"+subjectsId+",???????????????"+busiCode);
        long waitMatchAmt = amount;
        long batchId = CommonUtil.createSoNbr();

        List<OilRechargeAccountDetailsFlow> detailsFlows = oilRechargeAccountDetailsFlowService.getOrderDetailsFlows(userId, orderNum, -1, 1);
        Map<String,Long> accountMap = new HashMap();
        Map flowDetails = new HashMap();
        for(OilRechargeAccountDetailsFlow detailsFlow:detailsFlows){
            if(waitMatchAmt<=0L){
                continue;
            }
            long doAmount = detailsFlow.getUnMatchAmount();
            if(waitMatchAmt<=doAmount){
                doAmount=waitMatchAmt;
            }
            detailsFlow.setMatchAmount(detailsFlow.getMatchAmount()==null?0L:detailsFlow.getMatchAmount()+ doAmount);
            detailsFlow.setUnMatchAmount(detailsFlow.getUnMatchAmount()- doAmount);
            oilRechargeAccountDetailsFlowService.saveOrUpdate(detailsFlow);
            long dealAmount = accountMap.get(detailsFlow.getRelId() + "") != null
                    ? Math.abs(accountMap.get(detailsFlow.getRelId() + "")) + doAmount : doAmount;
            accountMap.put(detailsFlow.getRelId()+"", -dealAmount);
            waitMatchAmt-=doAmount;
            if(dealAmount>0L){
                flowDetails.put(detailsFlow.getId(), -dealAmount);
            }

        }

        oilRechargeAccountService.insertOilAccount(userId, accountMap, -amount, orderNum,
                SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE5, batchId, subjectsId, busiCode,flowDetails,user);

        /**
         * ?????????????????????????????????????????????????????????????????????????????????????????????
         */
        Iterator<Map.Entry<String, Long>> entries = sourceAccountMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Long> entry = entries.next();
            String key = entry.getKey();// ????????????????????????????????????Id
            Long value = entry.getValue();// ?????????????????????
            OilRechargeAccountDetails details =  oilRechargeAccountDetailsService.getById(Long.valueOf(key));
            if(details!=null){
                details.setUnUseredBalance(details.getUnUseredBalance()-value);
                oilRechargeAccountDetailsService.saveOrUpdate(details);
            }
            //??????????????????????????????????????????56K???????????????????????????
            if(details.getSourceType()== SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5){
                String vehicleAffiliation = details.getVehicleAffiliation();
                String sysParame56K = readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.BILL_FORM_STATIC, SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K+"").getCodeName();
                String sysPre = billPlatformService.getPrefixByUserId(Long.valueOf(vehicleAffiliation));
                if(!sysParame56K.equals(sysPre)){
                    continue;
                }
                OilRecord56k oilRecord56k = new OilRecord56k();
                oilRecord56k.setBusiCode(busiCode);
                oilRecord56k.setBusiType(EnumConsts.BUSI_TYPE.BUSI_TYPE2);//??????
                oilRecord56k.setOrderId(Long.valueOf(orderNum));
                oilRecord56k.setDealDate(LocalDateTime.now());
                oilRecord56k.setAmount(amount);
                oilRecord56k.setSyncState(EnumConsts.SYNC_STATE.SYNC_STATE0);
                BillPlatform billPlatform = billPlatformService.queryAllBillPlatformByUserId(Long.valueOf(vehicleAffiliation));
                if(billPlatform!=null){
                    String billHead = billPlatform.getBillHead();
                    if("??????????????????????????????????????????".equals(billHead)){
                        oilRecord56k.setIdentifier(0);//0 ?????? 1 ??????
                    }else if("???????????????????????????????????????".equals(billHead)){
                        oilRecord56k.setIdentifier(1);//0 ?????? 1 ??????
                    }
                }else{
                    log.error("????????????????????????56K?????????????????????????????????????????????????????????"+vehicleAffiliation);
                    oilRecord56k.setSyncState(EnumConsts.SYNC_STATE.SYNC_STATE2);
                }
                oilRecord56k.setSyncMsg("?????????");
                this.save(oilRecord56k);
            }
        }

//		opOilRechargeAccountTF.insertOilAccount(sourceUserId, sourceAccountMap, 0L, orderNum, OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE5, batchId, subjectsId, busiCode);
    }
}
