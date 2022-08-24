package com.youming.youche.order.provider.utils;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.order.IAcBusiOrderLimitRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IOaLoanService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AcBusiOrderLimitRel;
import com.youming.youche.order.domain.order.AccountDetails;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OaLoan;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1;
import static com.youming.youche.conts.EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT;
import static com.youming.youche.conts.EnumConsts.SubjectIds.BILL_SERVICE_RECEIVABLE_OVERDUE_SUB;
import static com.youming.youche.conts.EnumConsts.SubjectIds.OA_LOAN_AVAILABLE;
import static com.youming.youche.conts.EnumConsts.SubjectIds.SUBJECTIDS50044;
import static com.youming.youche.conts.OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;
import static com.youming.youche.conts.OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1;
import static com.youming.youche.conts.SysStaticDataEnum.SysStaticData.ACCOUNT_DETAILS_BUSINESS_NUMBER;
import static com.youming.youche.order.constant.BaseConstant.ACCOUNT_BOOK_TYPE;
import static com.youming.youche.order.provider.utils.CommonUtil.createSoNbr;

@Component
public class BaseBusiToOrder {
    private static final Log log = LogFactory.getLog(BaseBusiToOrder.class);
    private Map<Long,List<AcBusiOrderLimitRel>> limitMap;
    private Map<Long,List<AcBusiOrderLimitRel>> subjectMap;
    private Map<Long, BusiSubjectsRel> subjectStrMap;

    public void setBaseBusiToOrder(List<Map> mapList){
        limitMap = mapList.get(0);
        subjectMap = mapList.get(1);
        subjectStrMap = mapList.get(2);
    }
    @Resource
    private IOrderAccountService orderAccountService;
    @Resource
    private PayOutIntfUtil payOutIntfUtil;
    @Autowired
    private IAccountDetailsService accountDetailsService;
    @Autowired
    private IOrderFundFlowService orderFundFlowService;
    @Resource
    private ReadisUtil readisUtil;
    @Autowired
    private IAcBusiOrderLimitRelService acBusiOrderLimitRelService;
    @Autowired
    private IPayoutIntfService payoutIntfService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @Autowired
    IOaLoanService oaLoanService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @Resource
    IPayoutIntfService iPayoutIntfService;

    @Resource
    IOaLoanService iOaLoanService;

    //（已收已付）
    public OrderAccount[] doUpdOrderAccount(PayoutIntf payoutIntf){

        //查询付款方账户
        Long tenantId = payoutIntf.getPayTenantId();
        if(payoutIntf.getPayType() != OrderAccountConst.PAY_TYPE.TENANT){
            tenantId = payoutIntf.getTenantId();
        }
        if(payOutIntfUtil.getForPayTenantId(payoutIntf.getSubjectsId())){
            tenantId = payoutIntf.getTenantId();
        }
        OrderAccount payAccount = orderAccountService.queryOrderAccount(payoutIntf.getPayObjId(),
                payoutIntf.getVehicleAffiliation(),0l, tenantId,
                payoutIntf.getOilAffiliation(),payoutIntf.getPayUserType());
        //查询收款方账户
        /*Long receivablesTenantId = payoutIntf.getPayTenantId();
        if(payoutIntf.getIsDriver() != OrderAccountConst.PAY_TYPE.TENANT){
            receivablesTenantId = payoutIntf.getPayTenantId();
        }*/
        OrderAccount receivablesAccount = orderAccountService.queryOrderAccount(payoutIntf.getUserId(),
                payoutIntf.getVehicleAffiliation(),0l, tenantId,
                payoutIntf.getOilAffiliation(),payoutIntf.getUserType());
        OrderAccount[] orderAccountArr = new OrderAccount[2];
        orderAccountArr[0] = payAccount;
        orderAccountArr[1] = receivablesAccount;
        //应付逾期
        Long currentPayableBalance = payAccount.getPayableOverdueBalance();
        //应收逾期
        Long currentReceivableBalance = receivablesAccount.getReceivableOverdueBalance();
        // 可用金额
        Long currentAmount = receivablesAccount.getBalance();
        //20190727 路歌票据服务费
        Long billServiceFee = (payoutIntf.getBillServiceFee() == null ? 0L : payoutIntf.getBillServiceFee());
        Long appendFreight = (payoutIntf.getAppendFreight() == null ? 0L : payoutIntf.getAppendFreight());

//        // 这里修改提示id为账户名 == 用户的名称
//        if (null == currentPayableBalance || currentPayableBalance.longValue() <= 0) {
//            throw new BusinessException("付款账户：" + iUserDataInfoService.getUserDataInfo(payAccount.getUserId()).getLinkman() + "，应付逾期为：" + currentPayableBalance + " 账户核销失败");
//        }
//        payAccount.setPayableOverdueBalance(currentPayableBalance.longValue() - payoutIntf.getTxnAmt().longValue() - billServiceFee.longValue() - appendFreight.longValue());
//        if (currentPayableBalance.longValue() < 0L) {
//            throw new BusinessException("付款账户：" + iUserDataInfoService.getUserDataInfo(payAccount.getUserId()).getLinkman() + "，应付逾期少于付款金额账户核销失败");
//        }
//        if ((currentReceivableBalance == null || currentReceivableBalance.longValue() < 0) && payoutIntf.getTxnAmt().longValue() > 0L) {//应收逾期账户为空或者账户余额小于等于0，并且业务金额大于0
//            throw new BusinessException("收款账户：" + iUserDataInfoService.getUserDataInfo(receivablesAccount.getUserId()).getLinkman() + "，应收逾期少于付款金额账户核销失败");
//        } else {
//            receivablesAccount.setReceivableOverdueBalance(currentReceivableBalance.longValue() - payoutIntf.getTxnAmt().longValue());
//            if (currentReceivableBalance.longValue() < 0) {
//                throw new BusinessException("收款账户：" + iUserDataInfoService.getUserDataInfo(receivablesAccount.getUserId()).getLinkman() + "，应收逾期少于付款金额账户核销失败");
//            }
//        }

        //如果需要开票，则收款方账户表的可用金额加上付款金额 保证系统账户与HA对应上
        orderAccountService.update(payAccount);
        orderAccountService.update(receivablesAccount);
        return orderAccountArr;
    }



    //（应收应付）
    public OrderAccount[] doUpdOrderAccount(PayoutIntf payoutIntf,boolean boolFlg){

        //查询付款方账户
        Long tenantId = payoutIntf.getPayTenantId();
        if(payoutIntf.getPayType() != OrderAccountConst.PAY_TYPE.TENANT){
            tenantId = payoutIntf.getTenantId();
        }
        if(payOutIntfUtil.getForPayTenantId(payoutIntf.getSubjectsId())){
            tenantId = payoutIntf.getTenantId();
        }
        OrderAccount payAccount = orderAccountService.queryOrderAccount(payoutIntf.getPayObjId(), payoutIntf.getVehicleAffiliation(),0l, tenantId,payoutIntf.getOilAffiliation(),payoutIntf.getPayUserType());
        //查询收款方账户
        /*Long receivablesTenantId = payoutIntf.getPayTenantId();
        if(payoutIntf.getIsDriver() != OrderAccountConst.PAY_TYPE.TENANT){
            receivablesTenantId = payoutIntf.getPayTenantId();
        }*/
        OrderAccount receivablesAccount = orderAccountService.queryOrderAccount(payoutIntf.getUserId(), payoutIntf.getVehicleAffiliation(),0l, tenantId,payoutIntf.getOilAffiliation(),payoutIntf.getUserType());
        OrderAccount[] orderAccountArr = new OrderAccount[2];
        orderAccountArr[0] = payAccount;
        orderAccountArr[1] = receivablesAccount;
        //应付逾期
        Long currentPayableBalance = payAccount.getPayableOverdueBalance();
        //应收逾期
        Long currentReceivableBalance = receivablesAccount.getReceivableOverdueBalance();
        // 可用金额
        Long currentAmount = receivablesAccount.getBalance();
        //20190727 路歌票据服务费
        Long billServiceFee = (payoutIntf.getBillServiceFee() == null ? 0L : payoutIntf.getBillServiceFee());
        Long appendFreight = (payoutIntf.getAppendFreight() == null ? 0L : payoutIntf.getAppendFreight());

        if(boolFlg){
            if(null == currentPayableBalance || currentPayableBalance.longValue() <= 0){
                throw new BusinessException("付款账户"+payAccount.getId() +"应付逾期为：" + currentPayableBalance+"账户核销失败");
            }
            payAccount.setPayableOverdueBalance(currentPayableBalance.longValue() - payoutIntf.getTxnAmt().longValue() - billServiceFee.longValue() - appendFreight.longValue());

            if(currentPayableBalance.longValue() < 0){
                throw new BusinessException("付款账户"+payAccount.getId() +"应付逾期少于付款金额账户核销失败");
            }
            if((currentReceivableBalance==null||currentReceivableBalance.longValue() < 0)&&payoutIntf.getTxnAmt().longValue()>0L) {//应收逾期账户为空或者账户余额小于等于0，并且业务金额大于0
                throw new BusinessException("收款账户"+payAccount.getId() +"应收逾期少于付款金额账户核销失败");
            }else {
                receivablesAccount.setReceivableOverdueBalance(currentReceivableBalance.longValue() - payoutIntf.getTxnAmt().longValue());
                if(currentReceivableBalance.longValue() < 0 ){
                    throw new BusinessException("收款账户"+payAccount.getId() +"应收逾期少于付款金额账户核销失败");
                }
            }

        }else{
            payAccount.setPayableOverdueBalance((currentPayableBalance != null ? currentPayableBalance.longValue() : 0L) + payoutIntf.getTxnAmt().longValue() + billServiceFee.longValue() + appendFreight.longValue());
            receivablesAccount.setReceivableOverdueBalance((currentPayableBalance != null ? currentPayableBalance.longValue() : 0L) + payoutIntf.getTxnAmt().longValue());
        }
        //如果需要开票，则收款方账户表的可用金额加上付款金额 保证系统账户与HA对应上
        //撤单业务不需要加，因为付款账户和收款账户之间不能互转所以不走HA撤回，直接生成逾期记录
        if(!payOutIntfUtil.isBackOrder(payoutIntf.getBusiId(),payoutIntf.getSubjectsId()) && !payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION0) && !payoutIntf.getVehicleAffiliation().equals(VEHICLE_AFFILIATION1)){
            receivablesAccount.setBalance(currentAmount.longValue() + payoutIntf.getTxnAmt().longValue() );
        }
        orderAccountService.update(payAccount);
        orderAccountService.update(receivablesAccount);
        return orderAccountArr;
    }


    /***
     * 根据不同业务处理order_limit表
     * @param payoutIntf
     * @throws Exception
     */
    public void doUpdOrderLimit(PayoutIntf payoutIntf){
        if(null == payoutIntf.getOrderId() || payoutIntf.getOrderId().longValue() <=0 ){
            return;
        }
        LambdaQueryWrapper<AcBusiOrderLimitRel> lambda= Wrappers.lambdaQuery();
        lambda.eq(AcBusiOrderLimitRel::getSubjectsId,payoutIntf.getSubjectsId());
        List<AcBusiOrderLimitRel> list = acBusiOrderLimitRelService.list(lambda);
        UpdateWrapper<OrderLimit> lambdaUpdate=new UpdateWrapper<>();
        if(null != list && !list.isEmpty()){
            for (int i = 0; i < list.size(); i++) {
                AcBusiOrderLimitRel busiOrderLimitRel = list.get(i);
                if("ACCOUNT_BALANCE".equals(busiOrderLimitRel.getRelObj().toUpperCase()) && payoutIntf.getSubjectsId().longValue() != BILL_SERVICE_RECEIVABLE_OVERDUE_SUB){
                    continue;
                }
                lambdaUpdate.set(busiOrderLimitRel.getRelObj(),busiOrderLimitRel.getRelObj()
                        + "+("+busiOrderLimitRel.getOperType()+payoutIntf.getTxnAmt()+")" );
            }
        }else{
            return ;
        }
        lambdaUpdate.eq("ORDER_ID",payoutIntf.getOrderId());
        //撤单业务，取付款的user_id
        if(payOutIntfUtil.isBackOrder(payoutIntf.getBusiId(),payoutIntf.getSubjectsId())){
            lambdaUpdate.eq("USER_ID",payoutIntf.getPayObjId());
        }else{
            lambdaUpdate.eq("USER_ID",payoutIntf.getUserId());
        }
    }
    /***（应收应付）
     * 记录账户明细表
     * @param payoutIntf
     * @param orderAccountArr
     * @throws Exception
     */
    public void doSaveAccountDetail(PayoutIntf payoutIntf, OrderAccount[] orderAccountArr,
                                    UserDataInfo[] userDataInfoArr, boolean boolFlg
                                     , LoginInfo user) {
        //付款账户
        OrderAccount payAccount = orderAccountArr[0];
        //收款账户
        OrderAccount receivablesAccount = orderAccountArr[1];
        //付款用户信息
        UserDataInfo payUserDataInfo = userDataInfoArr[0];
        //收款用户信息
        UserDataInfo receivablesUserDataInfo = userDataInfoArr[1];
        //根据处理的费用科目，记录实际的账户操作明细
        List<AcBusiOrderLimitRel> list = subjectMap.get(payoutIntf.getSubjectsId());
        if(null == list || list.size() < 2){
            throw new BusinessException("科目映射配置不全");
        }

        long soNbr = createSoNbr();

        for (int i = 0; i < list.size(); i++) {
            AccountDetails accDet = new AccountDetails();
            AcBusiOrderLimitRel busiOrderLimitRel = list.get(i);
            Long relObjId = Long.parseLong(busiOrderLimitRel.getRelObj());
            BusiSubjectsRel busiSubjectsRel = subjectStrMap.get(relObjId);
            accDet.setCreateTime(LocalDateTime.now());
            //20190727 路歌票据服务费
            Long billServiceFee = (payoutIntf.getBillServiceFee() == null ? 0L : payoutIntf.getBillServiceFee());
            Long appendFreight = (payoutIntf.getAppendFreight() == null ? 0L : payoutIntf.getAppendFreight());
            if(!boolFlg){
                if("-".equals(busiOrderLimitRel.getOperType())){
//                accDet.setCurrentAmount(payAccount.getBalance());
                    accDet.setCurrentAmount(payAccount.getPayableOverdueBalance());
                    accDet.setAccountId(payAccount.getId());
                    accDet.setAmount(payoutIntf.getTxnAmt()+billServiceFee.longValue()+appendFreight.longValue());
                    accDet.setUserId(payoutIntf.getPayObjId());
                    //会员体系改造开始
                    accDet.setUserType(payoutIntf.getPayUserType());
                    //会员体系改造结束
                    //设置对方用户信息
                    accDet.setOtherUserId(payoutIntf.getUserId());
                    accDet.setOtherName(receivablesUserDataInfo.getLinkman());

                }else{
                    accDet.setCurrentAmount(receivablesAccount.getReceivableOverdueBalance());
                    accDet.setAccountId(receivablesAccount.getId());
                    accDet.setOtherUserId(payUserDataInfo.getId());
                    accDet.setOtherName(payUserDataInfo.getLinkman());
                    accDet.setAmount(payoutIntf.getTxnAmt());
                    accDet.setUserId(payoutIntf.getUserId());
                    //会员体系改造开始
                    accDet.setUserType(payoutIntf.getUserType());
                    //会员体系改造结束
                }
            }
            accDet.setBookType(Long.parseLong(busiSubjectsRel.getBookType()));//账户类型
        //    accDet.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE",busiSubjectsRel.getBookType()).getCodeName());//账户类型
            accDet.setSoNbr(soNbr);
            accDet.setBusinessTypes(EnumConsts.BusiType.ACCOUNT_INOUT_CODE);
            accDet.setBusinessNumber(busiOrderLimitRel.getId());
            accDet.setSubjectsId(Long.parseLong(busiOrderLimitRel.getRelObj()));
            accDet.setSubjectsName(subjectStrMap.get(relObjId).getRemarks());
            accDet.setCostType(Integer.parseInt(COST_TYPE2));
       //     accDet.setCostTypeName(SysStaticDataUtil.getSysStaticData("COST_TYPE", COST_TYPE2).getCodeName());
            if("+".equals(busiOrderLimitRel.getOperType())){
                accDet.setCostType(Integer.parseInt(COST_TYPE2));
   //             accDet.setCostTypeName(SysStaticDataUtil.getSysStaticData("COST_TYPE", COST_TYPE2).getCodeName());
            }else if("-".equals(busiOrderLimitRel.getOperType())){
                accDet.setCostType(Integer.parseInt(COST_TYPE1));
       //         accDet.setCostTypeName(SysStaticDataUtil.getSysStaticData("COST_TYPE", COST_TYPE1).getCodeName());
            }
            accDet.setNote(subjectStrMap.get(relObjId).getRemarks());
            accDet.setVehicleAffiliation(payoutIntf.getVehicleAffiliation());
            if(null != payoutIntf.getOrderId()){

                accDet.setOrderId(payoutIntf.getOrderId()+"");
            }else{
                accDet.setOrderId(null);
            }
            accDet.setAccountId(payAccount.getId());
            accDet.setCreateTime(LocalDateTime.now());
            accDet.setUpdateTime(LocalDateTime.now());
            accDet.setUpdateOpId(user.getId());
            accDet.setOpId(user.getId());
            accountDetailsService.saveAccountDetails(accDet);
            //如果是撤单业务，不会有账户可用余额变化，所以不用记录流水
            if(payOutIntfUtil.isBackOrder(payoutIntf.getBusiId(),payoutIntf.getSubjectsId())){
                continue;
            }
            if("+".equals(busiOrderLimitRel.getOperType()) && !VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation()) &&
                    !VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation())){
                AccountDetails accDet1 = new AccountDetails();
                BeanUtils.copyProperties(accDet,accDet1);
                accDet1.setCurrentAmount(receivablesAccount.getBalance());
                accDet1.setCostType(Integer.parseInt(COST_TYPE2));
      //          accDet1.setCostTypeName(SysStaticDataUtil.getSysStaticData("COST_TYPE", COST_TYPE2).getCodeName());
                accDet1.setNote("可用余额变化");
                accountDetailsService.saveAccountDetails(accDet1);
            }
        }

    }





    public void doSaveOrderFundflow(PayoutIntf payoutIntf,UserDataInfo[] userDataInfoArr,
                                    String flg,LoginInfo user){
        List<AcBusiOrderLimitRel> list = subjectMap.get(payoutIntf.getSubjectsId());
        if(null == list || list.size() < 2){
            throw new BusinessException("科目映射配置不全");
        }

        //付款用户信息
        UserDataInfo payUserDataInfo = userDataInfoArr[0];
        //收款用户信息
        UserDataInfo receivablesUserDataInfo = userDataInfoArr[1];

        String batchId = CommonUtil.createSoNbr() + "";
        for (int i = 0; i < list.size(); i++) {
            AcBusiOrderLimitRel busiOrderLimitRel = list.get(i);
            Long relObj = Long.parseLong(busiOrderLimitRel.getRelObj());
            BusiSubjectsRel busiSubjectsRel = subjectStrMap.get(relObj);
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(payoutIntf.getTxnAmt());//交易金额（单位分）
            off.setOrderId(payoutIntf.getOrderId());//订单ID
            off.setBusinessId(busiOrderLimitRel.getId());//业务类型
            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(busiOrderLimitRel.getId())).getCodeName());//业务名称
            off.setBookType(Long.parseLong(busiSubjectsRel.getBookType()));//账户类型
            off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,busiSubjectsRel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(relObj);//科目ID
            off.setSubjectsName(subjectStrMap.get(relObj).getRemarks());//科目名称
            off.setBusiTable("PAYOUT_INTF");//业务对象表
            off.setBusiKey(payoutIntf.getId());//业务流水ID
            off.setBatchAmount(payoutIntf.getTxnAmt());
            off.setVehicleAffiliation(payoutIntf.getVehicleAffiliation());
            off.setBatchId(batchId);
            off.setOpDate(LocalDateTime.now());
            off.setOpId(user.getId());
            off.setOpName(user.getName());
            off.setUserName(user.getName());
            off.setUpdateTime(LocalDateTime.now());
            off.setUpdateOpId(user.getId());
            off.setCreateTime(LocalDateTime.now());
//        off.setTenantId(ol.getTenantId());
            if("-".equals(busiOrderLimitRel.getOperType())){
                off.setUserId(payoutIntf.getPayObjId());
                off.setUserName(payUserDataInfo.getLinkman());
                off.setBillId(payUserDataInfo.getMobilePhone());
                off.setInoutSts(flg);
            }else{
                off.setUserId(payoutIntf.getUserId());
                off.setUserName(receivablesUserDataInfo.getLinkman());
                off.setBillId(receivablesUserDataInfo.getMobilePhone());
                off.setInoutSts(flg);

                if(!VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation()) && !VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation())){
                    OrderFundFlow off1 = new OrderFundFlow();
                    BeanUtils.copyProperties(off1,off);
                    off1.setInoutSts("in");
                    orderFundFlowService.save(off1);
                }
            }
            orderFundFlowService.save(off);
        }



    }


    /***（已收已付）
     * 记录账户明细表
     * @param payoutIntf
     * @param orderAccountArr
     * @throws Exception
     */
    public void doSaveAccountDetail(PayoutIntf payoutIntf,OrderAccount[] orderAccountArr,UserDataInfo[] userDataInfoArr, LoginInfo user) {
        //付款账户
        OrderAccount payAccount = orderAccountArr[0];
        //收款账户
        OrderAccount receivablesAccount = orderAccountArr[1];
        //付款用户信息
        UserDataInfo payUserDataInfo = userDataInfoArr[0];
        //收款用户信息
        UserDataInfo receivablesUserDataInfo = userDataInfoArr[1];
        //根据处理的费用科目，记录实际的账户操作明细
        List<AcBusiOrderLimitRel> list = subjectMap.get(payoutIntf.getSubjectsId());
        if(null == list || list.size() < 2){
            throw new BusinessException("科目映射配置不全");
        }
        long soNbr = createSoNbr();
        Date date = new Date();
        for (int i = 0; i < list.size(); i++) {
            AccountDetails accDet = new AccountDetails();
            AcBusiOrderLimitRel busiOrderLimitRel = list.get(i);
            Long relObjId = Long.parseLong(busiOrderLimitRel.getRelObj());
            BusiSubjectsRel busiSubjectsRel = subjectStrMap.get(relObjId);
            accDet.setCreateTime(LocalDateTime.now());
            //20190727 路歌票据服务费
            Long billServiceFee = (payoutIntf.getBillServiceFee() == null ? 0L : payoutIntf.getBillServiceFee());
            Long appendFreight = (payoutIntf.getAppendFreight() == null ? 0L : payoutIntf.getAppendFreight());
            if("-".equals(busiOrderLimitRel.getOperType())){
//                accDet.setCurrentAmount(payAccount.getBalance());
                accDet.setCurrentAmount(payAccount.getPayableOverdueBalance());
                accDet.setAccountId(payAccount.getId());
                accDet.setAmount(-(payoutIntf.getTxnAmt()+billServiceFee.longValue()+appendFreight.longValue()));
                accDet.setUserId(payoutIntf.getPayObjId());
                //会员体系改造开始
                accDet.setUserType(payoutIntf.getPayUserType());
                //会员体系改造结束
                //设置对方用户信息
                accDet.setOtherUserId(payoutIntf.getUserId());
                accDet.setOtherName(receivablesUserDataInfo.getLinkman());
                accDet.setNote("应付账户余额变化");

            }else{
                accDet.setCurrentAmount(receivablesAccount.getReceivableOverdueBalance());
                accDet.setAccountId(receivablesAccount.getId());
                accDet.setOtherUserId(payUserDataInfo.getId());
                accDet.setOtherName(payUserDataInfo.getLinkman());
                accDet.setAmount(-payoutIntf.getTxnAmt());
                accDet.setUserId(payoutIntf.getUserId());
                //会员体系改造开始
                accDet.setUserType(payoutIntf.getUserType());
                //会员体系改造结束
                accDet.setNote("应收账户余额变化");
            }
            accDet.setBookType(Long.parseLong(busiSubjectsRel.getBookType()));//账户类型
            //accDet.setBookTypeName(readisUtil.getSysStaticData("ACCOUNT_BOOK_TYPE",busiSubjectsRel.getBookType()).getCodeName());//账户类型
            accDet.setSoNbr(soNbr);
            accDet.setBusinessTypes(EnumConsts.BusiType.ACCOUNT_INOUT_CODE);
            accDet.setBusinessNumber(busiOrderLimitRel.getId());
            accDet.setSubjectsId(Long.parseLong(busiOrderLimitRel.getRelObj()));
            accDet.setSubjectsName(subjectStrMap.get(relObjId).getRemarks());
            accDet.setCostType(Integer.parseInt(COST_TYPE1));
            //accDet.setCostTypeName(SysStaticDataUtil.getSysStaticData("COST_TYPE", COST_TYPE1).getCodeName());
//            accDet.setNote(subjectStrMap.get(relObjId).getRemarks());

            accDet.setVehicleAffiliation(payoutIntf.getVehicleAffiliation());
            if(null != payoutIntf.getOrderId()){
                accDet.setOrderId(payoutIntf.getOrderId()+"");
            }else{
                accDet.setOrderId(null);
            }
            accDet.setAccountId(payAccount.getId());
            accDet.setCreateTime(LocalDateTime.now());
            accDet.setUpdateTime(LocalDateTime.now());
            accDet.setUpdateOpId(user.getId());
            accDet.setOpId(user.getId());
            accountDetailsService.saveAccountDetails(accDet);
            //如果是撤单业务，不会有账户可用余额变化，所以不用记录流水
            if(payOutIntfUtil.isBackOrder(payoutIntf.getBusiId(),payoutIntf.getSubjectsId())){
                continue;
            }
            if("+".equals(busiOrderLimitRel.getOperType()) && !VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation()) && !VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation())){
                AccountDetails accDet1 = new AccountDetails();
                BeanUtils.copyProperties(accDet1,accDet);
                accDet1.setAmount(payoutIntf.getTxnAmt());
                accDet1.setCurrentAmount(receivablesAccount.getBalance());
                accDet1.setCostType(Integer.parseInt(COST_TYPE2));
                //accDet1.setCostTypeName(SysStaticDataUtil.getSysStaticData("COST_TYPE", COST_TYPE2).getCodeName());
                accDet1.setNote("可用余额变化");
                accountDetailsService.saveAccountDetails(accDet1);
            }
        }

    }

    public void doSaveOrderFundflow(PayoutIntf payoutIntf,UserDataInfo[] userDataInfoArr, LoginInfo user){
        List<AcBusiOrderLimitRel> list = subjectMap.get(payoutIntf.getSubjectsId());
        if(null == list || list.size() < 2){
            throw new BusinessException("科目映射配置不全");
        }
        //付款用户信息
        UserDataInfo payUserDataInfo = userDataInfoArr[0];
        //收款用户信息
        UserDataInfo receivablesUserDataInfo = userDataInfoArr[1];
        Date date = new Date();
        String batchId = CommonUtil.createSoNbr() + "";
        for (int i = 0; i < list.size(); i++) {
            AcBusiOrderLimitRel busiOrderLimitRel = list.get(i);
            Long relObj = Long.parseLong(busiOrderLimitRel.getRelObj());
            BusiSubjectsRel busiSubjectsRel = subjectStrMap.get(relObj);
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(payoutIntf.getTxnAmt());//交易金额（单位分）
            off.setOrderId(payoutIntf.getOrderId());//订单ID
            off.setBusinessId(busiOrderLimitRel.getId());//业务类型
            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(busiOrderLimitRel.getId())).getCodeName());//业务名称
            off.setBookType(Long.parseLong(busiSubjectsRel.getBookType()));//账户类型
            off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,busiSubjectsRel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(relObj);//科目ID
            off.setSubjectsName(subjectStrMap.get(relObj).getRemarks());//科目名称
            off.setBusiTable("PAYOUT_INTF");//业务对象表
            off.setBusiKey(payoutIntf.getId());//业务流水ID
            off.setBatchAmount(payoutIntf.getTxnAmt());
            off.setVehicleAffiliation(payoutIntf.getVehicleAffiliation());
            off.setBatchId(batchId);
            off.setOpDate(LocalDateTime.now());
            off.setOpId(user.getId());
            off.setOpName(user.getName());
            off.setUserName(user.getName());
            off.setUpdateTime(LocalDateTime.now());
            off.setUpdateOpId(user.getId());
            off.setCreateTime(LocalDateTime.now());
//        off.setTenantId(ol.getTenantId());
            if("-".equals(busiOrderLimitRel.getOperType())){
                off.setUserId(payoutIntf.getPayObjId());
                off.setUserName(payUserDataInfo.getLinkman());
                off.setBillId(payUserDataInfo.getMobilePhone());
                off.setInoutSts("out");
            }else{
                off.setUserId(payoutIntf.getUserId());
                off.setUserName(receivablesUserDataInfo.getLinkman());
                off.setBillId(receivablesUserDataInfo.getMobilePhone());
                off.setInoutSts("out");

                if(!VEHICLE_AFFILIATION0.equals(payoutIntf.getVehicleAffiliation()) && !VEHICLE_AFFILIATION1.equals(payoutIntf.getVehicleAffiliation())){
                    OrderFundFlow off1 = new OrderFundFlow();
                    BeanUtils.copyProperties(off1,off);
                    off1.setInoutSts("in");
                    orderFundFlowService.save(off1);
                }
            }
            orderFundFlowService.save(off);
        }



    }

    public void dealBusiOaLoanAvailable(PayoutIntf payoutIntf,UserDataInfo[] userDataInfoArr,LoginInfo loginInfo,String token){
        PayoutIntf payoutIntf1 = new PayoutIntf();
        BeanUtil.copyProperties(payoutIntf, payoutIntf1);
        payoutIntf1.setId(null);
        payoutIntf1.setPayTenantId(0L);
        payoutIntf1.setPayType(payoutIntf.getIsDriver());
        payoutIntf1.setPayObjId(payoutIntf.getUserId());
        payoutIntf1.setPayAccNo(payoutIntf.getAccNo());
        payoutIntf1.setPayAccName(payoutIntf.getAccName());
        payoutIntf1.setAccNo(payoutIntf.getPayAccNo());
        payoutIntf1.setAccName(payoutIntf.getAccName());
        payoutIntf1.setPayBankAccNo(payoutIntf.getReceivablesBankAccNo());
        payoutIntf1.setPayBankAccName(payoutIntf.getReceivablesBankAccName());
        payoutIntf1.setReceivablesBankAccNo(payoutIntf.getPayBankAccNo());
        payoutIntf1.setReceivablesBankAccName(payoutIntf.getPayBankAccName());
        payoutIntf1.setVerificationDate(null);
        payoutIntf1.setVerificationState(OrderConsts.PayOutVerificationState.INIT);
        payoutIntf1.setCreateDate(LocalDateTime.now());
        payoutIntf1.setUpdateTime(LocalDateTime.now());
        payoutIntf1.setPayTime(null);
        SysTenantDef def = sysTenantDefService.getSysTenantDef(payoutIntf.getPayTenantId());
        if(null != def && CommonUtil.isNumber(def.getLinkPhone())){
            payoutIntf1.setObjId(Long.parseLong(def.getLinkPhone()));
        }
        payoutIntf1.setAccountType(PRIVATE_PAYABLE_ACCOUNT);
        payoutIntf1.setBankType(BUSINESS_RECEIVABLE_ACCOUNT);
        payoutIntf1.setTenantId(payoutIntf.getPayTenantId());
        payoutIntf1.setIsDriver(payoutIntf.getPayType());
        payoutIntf1.setUserId(payoutIntf.getPayObjId());
        //会员体系改造开始
        payoutIntf1.setUserType(payoutIntf.getPayUserType());
        payoutIntf1.setPayUserType(payoutIntf.getUserType());
        //会员体系改造结束
        payoutIntf1.setPayConfirm(EnumConsts.PAY_CONFIRM.withdraw);
        payoutIntf1.setCompleteTime(null);
        payoutIntf1.setIsAutomatic(0);
        payoutIntf1.setRespCode(null);
        payoutIntf1.setRespMsg(null);
        payoutIntf1.setBusiId(EnumConsts.PayInter.OA_LOAN_AVAILABLE);
        payoutIntf1.setSubjectsId(SUBJECTIDS50044);
        payoutIntf1.setSerialNumber(null);
        payoutIntfService.save(payoutIntf1);
        iOaLoanService.updOaLoanPayFlowId(payoutIntf1.getId(), payoutIntf.getId());
        OrderAccount[] orderAccounts = doUpdOrderAccount(payoutIntf1,false);
        doSaveAccountDetail(payoutIntf1,orderAccounts,userDataInfoArr,false,loginInfo);
        payoutIntfService.doSavePayoutInfoExpansion(payoutIntf1,token);
    }


}
