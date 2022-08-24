package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IAcBusiOrderLimitRelService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.AcBusiOrderLimitRel;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.PinganBankInfoOutDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.youming.youche.conts.EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.PayInter.CANCEL_THE_ORDER;
import static com.youming.youche.conts.EnumConsts.PayInter.CONSUME_ETC_CODE;
import static com.youming.youche.conts.EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH;
import static com.youming.youche.conts.EnumConsts.PayInter.UPDATE_THE_ORDER;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_ETC_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN;
import static com.youming.youche.conts.EnumConsts.SubjectIds.EXCEPTION_FEE_OUT;
import static com.youming.youche.conts.EnumConsts.SubjectIds.EXCEPTION_OUT_PAYABLE_OVERDUE_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS1814;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP;
import static com.youming.youche.conts.EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP;
import static com.youming.youche.conts.OrderAccountConst.PAY_TYPE.HAEN;
import static com.youming.youche.conts.OrderAccountConst.PAY_TYPE.HAVIR;
import static com.youming.youche.conts.OrderAccountConst.TXN_TYPE.XS_TXN_TYPE;
import static com.youming.youche.conts.OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;
import static com.youming.youche.order.constant.BaseConstant.NEED_BILL_BANK_CARD_QUANTITY_LIMIT;

@Component
public class PayOutIntfUtil {
    @Resource
    private ReadisUtil readisUtil;
    @Autowired
    private IAccountBankRelService accountBankRelService;
    @Autowired
    private IBusiSubjectsRelService busiSubjectsRelService;
    @Autowired
    private IAcBusiOrderLimitRelService acBusiOrderLimitRelService;

    @Resource
    IPayoutIntfService iPayoutIntfService;

    public void checkPayOutInfToEnData(PayoutIntf payoutIntf){
        if(payoutIntf.getUserId() <= 0){
            throw new BusinessException("收款方信息不全[userId]");
        }
        if((null == payoutIntf.getTxnAmt() || payoutIntf.getTxnAmt() <= 0) && (null == payoutIntf.getAppendFreight() || payoutIntf.getAppendFreight() <= 0)){
            throw new BusinessException("打款金额错误");
        }
        if(StringUtils.isBlank(payoutIntf.getVehicleAffiliation())){
            throw new BusinessException("是否开票不能为空");
        }
        if(null == payoutIntf.getIsAutomatic()){
            throw new BusinessException("是否系统自动打款不能为空");
        }
        if(null == payoutIntf.getPayType()){
            throw new BusinessException("付款对象类型不能为空");
        }
        if(null == payoutIntf.getBusiId()){
            throw new BusinessException("业务ID不能为空");
        }
        if(null == payoutIntf.getSubjectsId()){
            throw new BusinessException("科目ID不能为空");
        }
        if(null == payoutIntf.getPriorityLevel()){
            payoutIntf.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL10);
        }
        //保存进来全部都是待核销状态
        payoutIntf.setVerificationState(OrderConsts.PayOutVerificationState.INIT);
        if(payoutIntf.getAccountType() == null){
            payoutIntf.setAccountType(getBankType(payoutIntf.getVehicleAffiliation(),payoutIntf.getPayType()));
        }
        if(null == payoutIntf.getBankType()){
            if(payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION0) || OrderAccountConst.PAY_TYPE.USER == payoutIntf.getIsDriver()){
                payoutIntf.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
            }else {
                payoutIntf.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
            }
        }
    }
    public boolean  getForPayTenantId(Long subjectsId){
        if(EXCEPTION_OUT_PAYABLE_OVERDUE_SUB == subjectsId.longValue()){
            return true;
        }
        if(CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN == subjectsId.longValue()){
            return true;
        }
        if(CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN == subjectsId.longValue()){
            return true;
        }
        if(CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN == subjectsId.longValue()){
            return true;
        }
        if(CANCEL_ORDER_CASH_PAYABLE_IN == subjectsId.longValue()){
            return true;
        }
        if(CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN == subjectsId.longValue()){
            return true;
        }
        if(CANCEL_ORDER_ETC_PAYABLE_IN == subjectsId.longValue()){
            return true;
        }
        if(UPDATE_ORDER_PAYABLE_CASH_LOW == subjectsId.longValue()){
            return true;
        }
        if(UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW == subjectsId.longValue()){
            return true;
        }
        if(UPDATE_ORDER_PAYABLE_ETC_LOW == subjectsId.longValue()){
            return true;
        }
        if(UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW == subjectsId.longValue()){
            return true;
        }
        if(UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW == subjectsId.longValue()){
            return true;
        } if(UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW == subjectsId.longValue()){
            return true;
        }
        return false;
    }
    public  boolean isBackOrder(Long busiId,Long subjectsId){
        if(EXCEPTION_FEE_OUT == busiId.longValue() ){
            return true;
        }else if(CANCEL_THE_ORDER == busiId.longValue()){
            return true;
        }else if(OIL_AND_ETC_TURN_CASH == busiId.longValue()){
            return true;
        }else if(CONSUME_ETC_CODE == busiId.longValue()){
            return true;
        }else if(UPDATE_THE_ORDER == busiId.longValue()){
            if(UPDATE_ORDER_RECEIVABLE_CASH_UPP == subjectsId.longValue() || UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP == subjectsId.longValue() || UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP == subjectsId.longValue()){
                return false;
            }
            return true;

        }else if(SUBJECTIDS1814 == subjectsId.longValue()){
            return true;
        }
        return false;
    }
    public  Integer getBankType(String vehicleAffiliation,int payType){
        if(payType == OrderAccountConst.PAY_TYPE.USER){
            return PRIVATE_PAYABLE_ACCOUNT;
        }
        if(VEHICLE_AFFILIATION0.equals(vehicleAffiliation)){
            return PRIVATE_PAYABLE_ACCOUNT;
        }else{
            return BUSINESS_PAYABLE_ACCOUNT;
        }
    }
    @Resource
    private BaseBusiToOrder baseBusiToOrder;
    public  BaseBusiToOrder newBaseBusiToOrder(){
        List<AcBusiOrderLimitRel> busiOrderLimitRels = acBusiOrderLimitRelService.list();
        List<BusiSubjectsRel> busiSubjectsRels = busiSubjectsRelService.list();
        List<Map> mapList = PayOutIntfUtil.dealBusiOrderLimitRel(busiOrderLimitRels,2);
        Map rtnMap = PayOutIntfUtil.dealBusiSubjectsRel(busiSubjectsRels);
        mapList.add(rtnMap);
        baseBusiToOrder.setBaseBusiToOrder(mapList);
        return baseBusiToOrder;
    }

    public  BaseBusiToOrder newBaseBusiToOrderTwo(){
        List<AcBusiOrderLimitRel> busiOrderLimitRels = acBusiOrderLimitRelService.list();
        List<BusiSubjectsRel> busiSubjectsRels = busiSubjectsRelService.list();
        List<Map> mapList = PayOutIntfUtil.dealBusiOrderLimitRel(busiOrderLimitRels, null);
        Map rtnMap = PayOutIntfUtil.dealBusiSubjectsRel(busiSubjectsRels);
        mapList.add(rtnMap);
        baseBusiToOrder.setBaseBusiToOrder(mapList);
        return baseBusiToOrder;
    }

    public static List<Map> dealBusiOrderLimitRel(List<AcBusiOrderLimitRel> list,Integer type){
        if(null == type){
            type = 1;
        }
        int relType = 0;
        if(type == 2){
            relType = 3;
        }
        List<Map> mapList = new ArrayList<>();
        Map<Long,List<AcBusiOrderLimitRel>> rtnMap = new HashMap<>();
        Map<Long,List<AcBusiOrderLimitRel>> rtnMap1 = new HashMap<>();
        List<AcBusiOrderLimitRel> busiOrderLimitRels = new ArrayList<>();
        List<AcBusiOrderLimitRel> busiOrderLimitRels1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            AcBusiOrderLimitRel busiOrderLimitRel = list.get(i);
            if(busiOrderLimitRel.getRelType() == relType){
                busiOrderLimitRels.add(busiOrderLimitRel);
            }else if(busiOrderLimitRel.getRelType() == type){
                busiOrderLimitRels1.add(busiOrderLimitRel);
            }
        }
        List<AcBusiOrderLimitRel> busiOrderLimitRelsTmp = null;
        for (int i = 0; i < busiOrderLimitRels.size(); i++) {
            AcBusiOrderLimitRel busiOrderLimitRel = busiOrderLimitRels.get(i);
            busiOrderLimitRelsTmp = rtnMap.get(busiOrderLimitRel.getSubjectsId());
            if(null == busiOrderLimitRelsTmp){
                busiOrderLimitRelsTmp = new ArrayList<>();
                rtnMap.put(busiOrderLimitRel.getSubjectsId(),busiOrderLimitRelsTmp);
            }
            busiOrderLimitRelsTmp.add(busiOrderLimitRel);

        }

        List<AcBusiOrderLimitRel> busiOrderLimitRelsTmp1 = null;

        for (int i = 0; i < busiOrderLimitRels1.size(); i++) {
            AcBusiOrderLimitRel busiOrderLimitRel = busiOrderLimitRels1.get(i);
            busiOrderLimitRelsTmp1 = rtnMap1.get(busiOrderLimitRel.getSubjectsId());
            if(null == busiOrderLimitRelsTmp1){
                busiOrderLimitRelsTmp1 = new ArrayList<>();
                rtnMap1.put(busiOrderLimitRel.getSubjectsId(),busiOrderLimitRelsTmp1);
            }
            busiOrderLimitRelsTmp1.add(busiOrderLimitRel);
        }

        mapList.add(rtnMap);
        mapList.add(rtnMap1);
        return mapList;
    }
    public static Map  dealBusiSubjectsRel(List<BusiSubjectsRel> list){
        Map rtnMap = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            BusiSubjectsRel busiSubjectsRel = list.get(i);
            rtnMap.put(busiSubjectsRel.getSubjectsId(),busiSubjectsRel);
        }
        return rtnMap;
    }
    public  PinganBankInfoOutDto getRandomPinganBankInfoOut(Long userId,Long tenantId) {
        //List<AccountBankRel> accountBankRelList = accountBankRelSV.getBankInfo(userId,BANK_TYPE_0);
        List<AccountBankRel> accountBankRelList = accountBankRelService.getCollectAmount(tenantId);//观华提供
        if (accountBankRelList != null && accountBankRelList.size() > 0) {
            PinganBankInfoOutDto pinganBankInfoOut = new PinganBankInfoOutDto();
            int bankSize = accountBankRelList.size();
            Double needBillBankCardQuantityLimit = Double.parseDouble(String.valueOf(readisUtil.getSysCfg(NEED_BILL_BANK_CARD_QUANTITY_LIMIT,"0").getCfgValue()));
            if (bankSize < needBillBankCardQuantityLimit) {
                throw new BusinessException("请前往银行卡管理界面绑定至少"+needBillBankCardQuantityLimit+"张不同账户名的银行卡！");
            }
            Random ra =new Random();
            int index = ra.nextInt(bankSize);
            AccountBankRel o = accountBankRelList.get(index);
            if (o.getBankType() == BANK_TYPE_0) {
                pinganBankInfoOut.setPrivatePinganAcctIdM(o.getPinganCollectAcctId());
                pinganBankInfoOut.setPrivatePinganAcctIdN(o.getPinganPayAcctId());
                pinganBankInfoOut.setPrivateAcctName(o.getAcctName());
                pinganBankInfoOut.setPrivateAcctNo(o.getAcctNo());
                pinganBankInfoOut.setPrivateBankCode(o.getBankName());
            }
            return pinganBankInfoOut;
        } else {
            throw new BusinessException("车队没有绑定对私收款账户，请检查");
        }
    }

    public PayoutIntf createPayoutIntfForWithdraw(PayoutIntf payoutIntf, int level) {
        PayoutIntf newPayoutIntf = new PayoutIntf();
        newPayoutIntf.setRelUserId(payoutIntf.getUserId());
        newPayoutIntf.setUserId(payoutIntf.getUserId());
        //会员体系改造开始
        newPayoutIntf.setPayUserType(payoutIntf.getUserType());
        newPayoutIntf.setUserType(payoutIntf.getUserType());
        //会员体系改造结束
        newPayoutIntf.setIsDriver(payoutIntf.getIsDriver());
        newPayoutIntf.setTxnAmt(payoutIntf.getTxnAmt());
        newPayoutIntf.setBillServiceFee(payoutIntf.getBillServiceFee());
        newPayoutIntf.setAppendFreight(payoutIntf.getAppendFreight());
        newPayoutIntf.setVehicleAffiliation(payoutIntf.getVehicleAffiliation());
        newPayoutIntf.setOilAffiliation(payoutIntf.getOilAffiliation());
        newPayoutIntf.setOrderId(payoutIntf.getOrderId());
        newPayoutIntf.setIsAutomatic(payoutIntf.getIsAutomatic());
        newPayoutIntf.setObjId(payoutIntf.getObjId());
        newPayoutIntf.setObjType(payoutIntf.getObjType());
        newPayoutIntf.setPayObjId(payoutIntf.getUserId());
        newPayoutIntf.setPayType(payoutIntf.getIsDriver());
        newPayoutIntf.setXid(payoutIntf.getId());
        newPayoutIntf.setPayTenantId(payoutIntf.getPayTenantId());
        newPayoutIntf.setBankType(payoutIntf.getBankType());
        newPayoutIntf.setAccountType(payoutIntf.getAccountType());
        newPayoutIntf.setPinganCollectAcctId(payoutIntf.getPinganCollectAcctId());
        //HA虚拟付款到HA实体，设置HA实体卡号信息
        if (payoutIntf.getIsDriver() == HAVIR) {
            newPayoutIntf.setPayType(HAVIR);
            newPayoutIntf.setIsDriver(HAEN);
            newPayoutIntf.setPayTenantId(-1L);
            newPayoutIntf.setPayObjId(Long.parseLong(payoutIntf.getVehicleAffiliation()));
            newPayoutIntf.setUserId(Long.parseLong(payoutIntf.getVehicleAffiliation()));
            //会员体系改造开始
            newPayoutIntf.setPayUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
            newPayoutIntf.setUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
            //会员体系改造结束
            PinganBankInfoOutDto bankAccInfo = iPayoutIntfService.getPinganBankInfoOutRemoteCall(Long.parseLong(payoutIntf.getVehicleAffiliation()), payoutIntf.getAccNo(), newPayoutIntf.getPayUserType());
            if (null == bankAccInfo) {
                throw new BusinessException("票据服务商userid[" + payoutIntf.getVehicleAffiliation() + "]没有查到银行卡信息");
            }
            //对公平安收款账户
            newPayoutIntf.setPayAccNo(bankAccInfo.getCorporatePinganAcctIdM());
            newPayoutIntf.setPayAccName(bankAccInfo.getCorporateAcctName());
            //对公银行卡号
            newPayoutIntf.setPayBankAccNo(bankAccInfo.getCorporateAcctNo());
            //对公账户名称
            newPayoutIntf.setPayBankAccName(bankAccInfo.getCorporateAcctName());

            //对公银行卡号
            newPayoutIntf.setReceivablesBankAccNo(bankAccInfo.getCorporateAcctNo());
            //对公账户名称
            newPayoutIntf.setReceivablesBankAccName(bankAccInfo.getCorporateAcctName());

            newPayoutIntf.setAccNo(bankAccInfo.getCorporateAcctNo());
            newPayoutIntf.setAccName(bankAccInfo.getCorporateAcctName());
            newPayoutIntf.setBankCode(bankAccInfo.getCorporateBankCode());

            newPayoutIntf.setAccountType(BUSINESS_RECEIVABLE_ACCOUNT);
            newPayoutIntf.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
        } else if (payoutIntf.getIsDriver() == HAEN) {
            PayoutIntf payoutIntfTmp = iPayoutIntfService.getById(payoutIntf.getXid());
            if (null == payoutIntfTmp) {
                throw new BusinessException("FLOW_ID[" + payoutIntf.getId() + "]记录不存在！");
            }
            newPayoutIntf.setPayType(HAEN);
            newPayoutIntf.setPayTenantId(-1L);
            newPayoutIntf.setUserId(payoutIntfTmp.getUserId());
            //会员体系改造开始
            newPayoutIntf.setPayUserType(payoutIntf.getPayUserType());
            newPayoutIntf.setUserType(payoutIntfTmp.getUserType());
            //会员体系改造结束
            newPayoutIntf.setPayObjId(Long.parseLong(payoutIntf.getVehicleAffiliation()));
            //PinganBankInfoOut bankAccInfo =  getPinganBankInfoOut(Long.parseLong(payoutIntf.getVehicleAffiliation()),"",accountBankRelSV,newPayoutIntf.getPayUserType());
            //对公银行卡号
            newPayoutIntf.setPayBankAccNo(payoutIntf.getReceivablesBankAccNo());
            newPayoutIntf.setPayAccNo(payoutIntf.getReceivablesBankAccNo());
            //对公账户名称
            newPayoutIntf.setPayBankAccName(payoutIntf.getReceivablesBankAccName());
            newPayoutIntf.setPayAccName(payoutIntf.getReceivablesBankAccName());
            newPayoutIntf.setBankCode(payoutIntf.getBankCode());
            PinganBankInfoOutDto bankAccInfo1 = iPayoutIntfService.getPinganBankInfoOutRemoteCall(newPayoutIntf.getUserId(), newPayoutIntf.getPinganCollectAcctId(), newPayoutIntf.getUserType());
            if (newPayoutIntf.getUserType() != SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
                if (null == bankAccInfo1 || StringUtils.isBlank(bankAccInfo1.getPrivateAcctNo()) || StringUtils.isBlank(bankAccInfo1.getPrivateAcctName())) {
                    throw new BusinessException("userid[" + newPayoutIntf.getUserId() + "]用户类型[" + newPayoutIntf.getUserType() + "]没有查到对私银行卡信息");
                }
                //对私银行卡号
                newPayoutIntf.setReceivablesBankAccNo(bankAccInfo1.getPrivateAcctNo());
                //对私账户名称
                newPayoutIntf.setReceivablesBankAccName(bankAccInfo1.getPrivateAcctName());

                newPayoutIntf.setAccNo(bankAccInfo1.getPrivateAcctNo());
                newPayoutIntf.setAccName(bankAccInfo1.getPrivateAcctName());
                newPayoutIntf.setBankCode(bankAccInfo1.getPrivateBankCode());

                newPayoutIntf.setAccountType(BUSINESS_RECEIVABLE_ACCOUNT);
                newPayoutIntf.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
            } else {
                if (null == bankAccInfo1 || StringUtils.isBlank(bankAccInfo1.getCorporateAcctNo()) || StringUtils.isBlank(bankAccInfo1.getCorporateAcctName())) {
                    throw new BusinessException("userid[" + newPayoutIntf.getUserId() + "]用户类型[" + newPayoutIntf.getUserType() + "]没有查到对公银行卡信息");
                }
                //对私银行卡号
                newPayoutIntf.setReceivablesBankAccNo(bankAccInfo1.getCorporateAcctNo());
                //对私账户名称
                newPayoutIntf.setReceivablesBankAccName(bankAccInfo1.getCorporateAcctName());

                newPayoutIntf.setAccNo(bankAccInfo1.getCorporateAcctNo());
                newPayoutIntf.setAccName(bankAccInfo1.getCorporateAcctName());
                newPayoutIntf.setBankCode(bankAccInfo1.getCorporateBankCode());

                newPayoutIntf.setAccountType(BUSINESS_RECEIVABLE_ACCOUNT);
                newPayoutIntf.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
            }
        } else {
            if (XS_TXN_TYPE.equals(payoutIntf.getTxnType())) {
                throw new BusinessException("FLOW_ID[" + payoutIntf.getId() + "]不能生成提现数据！");
            }
            if (payoutIntf.getBankType() == BUSINESS_RECEIVABLE_ACCOUNT) {

                PinganBankInfoOutDto bankAccInfo = iPayoutIntfService.getPinganBankInfoOutRemoteCall(newPayoutIntf.getPayObjId(), payoutIntf.getAccNo(), newPayoutIntf.getPayUserType());
                if (null == bankAccInfo || StringUtils.isBlank(bankAccInfo.getCorporateAcctNo())) {
                    payoutIntf.setRemark("未绑定对公银行卡,无法生成自动提现");
                    newPayoutIntf.setPayType(-1);
                    return newPayoutIntf;
                }
                newPayoutIntf.setPayAccNo(bankAccInfo.getCorporatePinganAcctIdM());
                newPayoutIntf.setPayAccName(bankAccInfo.getCorporateAcctName());
                newPayoutIntf.setPayBankAccNo(bankAccInfo.getCorporateAcctNo());
                newPayoutIntf.setPayBankAccName(bankAccInfo.getCorporateAcctName());
                newPayoutIntf.setAccountType(BUSINESS_RECEIVABLE_ACCOUNT);
                if (payoutIntf.getIsDriver() == OrderAccountConst.PAY_TYPE.USER) {
                    newPayoutIntf.setReceivablesBankAccNo(bankAccInfo.getPrivateAcctNo());
                    newPayoutIntf.setReceivablesBankAccName(bankAccInfo.getPrivateAcctName());
                    newPayoutIntf.setAccNo(bankAccInfo.getPrivateAcctNo());
                    newPayoutIntf.setAccName(bankAccInfo.getPrivateAcctName());
                    newPayoutIntf.setBankCode(bankAccInfo.getPrivateBankCode());
                    newPayoutIntf.setBankType(PRIVATE_RECEIVABLE_ACCOUNT);
                    newPayoutIntf.setAccountType(PRIVATE_RECEIVABLE_ACCOUNT);
                } else {
                    newPayoutIntf.setReceivablesBankAccNo(bankAccInfo.getCorporateAcctNo());
                    newPayoutIntf.setReceivablesBankAccName(bankAccInfo.getCorporateAcctName());
                    newPayoutIntf.setAccNo(bankAccInfo.getCorporateAcctNo());
                    newPayoutIntf.setAccName(bankAccInfo.getCorporateAcctName());
                    newPayoutIntf.setBankCode(bankAccInfo.getCorporateBankCode());
                }
            } else {
                PinganBankInfoOutDto bankAccInfo = iPayoutIntfService.getPinganBankInfoOutRemoteCall(newPayoutIntf.getPayObjId(), payoutIntf.getAccNo(), newPayoutIntf.getPayUserType());
                if (null == bankAccInfo || StringUtils.isBlank(bankAccInfo.getPrivateAcctNo())) {
                    payoutIntf.setRemark("未绑定对私银行卡,无法生成自动提现");
                    newPayoutIntf.setPayType(-1);
                    return newPayoutIntf;
                }
                newPayoutIntf.setPayAccNo(bankAccInfo.getPrivatePinganAcctIdM());
                newPayoutIntf.setPayBankAccNo(bankAccInfo.getPrivateAcctNo());
                newPayoutIntf.setPayAccName(bankAccInfo.getPrivateAcctName());
                newPayoutIntf.setPayBankAccName(bankAccInfo.getPrivateAcctName());
                newPayoutIntf.setReceivablesBankAccNo(bankAccInfo.getPrivateAcctNo());
                newPayoutIntf.setReceivablesBankAccName(bankAccInfo.getPrivateAcctName());
                newPayoutIntf.setAccNo(bankAccInfo.getPrivateAcctNo());
                newPayoutIntf.setAccName(bankAccInfo.getPrivateAcctName());
                newPayoutIntf.setBankCode(bankAccInfo.getPrivateBankCode());
                newPayoutIntf.setAccountType(PRIVATE_RECEIVABLE_ACCOUNT);
            }

        }
        newPayoutIntf.setSerialNumber(null);
        newPayoutIntf.setPriorityLevel(level);
        newPayoutIntf.setBusiId(payoutIntf.getBusiId());
        newPayoutIntf.setSubjectsId(payoutIntf.getSubjectsId());
        newPayoutIntf.setBusiCode(payoutIntf.getBusiCode());
        newPayoutIntf.setBankRemark(payoutIntf.getBankRemark());
        return newPayoutIntf;
    }

}
