package com.youming.youche.order.provider.service.order.other;

import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IAdditionalFeeService;
import com.youming.youche.order.api.order.IBaseBillInfoService;
import com.youming.youche.order.api.order.IBillAgreementService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOilSourceRecordService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.api.order.other.IUpdateOrderService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AdditionalFee;
import com.youming.youche.order.domain.order.BaseBillInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.CancelTheOrderInDto;
import com.youming.youche.order.dto.DriverOrderOilOutDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.OrderLimitFeeOutDto;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.UpdateTheOrderInDto;
import com.youming.youche.order.dto.UpdateTheOwnCarOrderInDto;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.BusiToOrderUtils;
import com.youming.youche.order.provider.utils.HttpsMainUtils;
import com.youming.youche.order.provider.utils.MatchAmountUtil;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.order.vo.QueryDriverOilByOrderIdVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.domain.SysTenantDef;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.order.commons.OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0;

/**
 * 订单修改相关接口实现类
 * */
@Slf4j
@DubboService(version = "1.0.0")
@Service
public class UpdateOrderServiceImpl implements IUpdateOrderService {

    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    private ISysUserService sysUserService;
    @Resource
    private IOrderAccountService orderAccountService;

    @Resource
    private IOrderSchedulerService orderSchedulerService;
    @Resource
    private IOrderSchedulerHService orderSchedulerHService;
    @Resource
    private IOrderOilSourceService orderOilSourceService;
    @Resource
    private IBusiSubjectsRelService busiSubjectsRelService;
    @Resource
    private IBillPlatformService billPlatformService;
    @Resource
    private IBillAgreementService billAgreementService;
    @Resource
    private IPayFeeLimitService payFeeLimitService;
    @Resource
    private IPayoutIntfService payoutIntfService;
    @Resource
    private IAccountDetailsService accountDetailsService;
    @Resource
    private IOrderLimitService orderLimitService;

    @Resource
    private IOperationOilService operationOilService;
    @Resource
    private IPayoutOrderService payoutOrderService;
    @Resource
    private IBaseBillInfoService baseBillInfoService;
    @Resource
    private IOilSourceRecordService oilSourceRecordService;
    @Resource
    private IOilRechargeAccountService oilRechargeAccountService;
    @Resource
    private ISysUserService sysOperatorService;

    @Resource
    private BusiToOrderUtils busiToOrderUtils;
    @Resource
    private IAdditionalFeeService additionalFeeService;
    @DubboReference(version = "1.0.0")
    private IAuditOutService auditOutService;
    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;



    @Override
    public void cancelTheOrder(long userId, String vehicleAffiliation, long amountFee, long virtualOilFee,
                               long entityOilFee, long EtcFee, long orderId, Long tenantId, int isNeedBill) throws Exception {

    }

    @Override
    public void cancelTheOrder(CancelTheOrderInDto in,LoginInfo loginInfo,String token){

        Long userId = in.getUserId();//用户编号
        String vehicleAffiliation = in.getVehicleAffiliation(); //资金渠道
        Long amountFee = in.getAmountFee() == null ? 0L : in.getAmountFee();//可用金额单位(分)
        Long virtualOilFee = in.getVirtualOilFee() == null ? 0L : in.getVirtualOilFee();//虚拟油卡金额单位(分)
        Long entityOilFee = in.getEntityOilFee() == null ? 0L : in.getEntityOilFee(); //实体油卡金额单位(分)
        Long etcFee = in.getEtcFee() == null ? 0L : in.getEtcFee() ;//ETCFee金额单位(分)
        Long orderId = in.getOrderId(); //订单编号
        Long tenantId = in.getTenantId(); //订单开单租户id
        //Integer isNeedBill = in.getIsNeedBill();//是否开票(0不开票，1承运方开票，2平台票)
        Long arriveFee = in.getArriveFee() == null ? 0L : in.getArriveFee();//到付款
        Integer isPayArriveFee = in.getIsPayArriveFee();//是否已经支付了到付款(0未支付，1已支付)
        log.info("撤单接口:userId=" + userId + " vehicleAffiliation=" + vehicleAffiliation + " amountFee=" + amountFee
                + " virtualOilFee" + virtualOilFee + " oilFee=" + entityOilFee + " etcFee=" + etcFee + " 订单号="+ orderId + " arriveFee="+arriveFee + " isPayArriveFee"+isPayArriveFee);
        if (userId == null || userId < 0L) {
            throw new BusinessException("请输入用户编号");
        }
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException("请输入租户id");
        }
        if (orderId == null || orderId <= 0L) {
            throw new BusinessException("请输入订单号");
        }
        if (amountFee < 0L) {
            throw new BusinessException("请输入现金");
        }
        if (virtualOilFee < 0L) {
            throw new BusinessException("请输入虚拟油");
        }
        if (entityOilFee < 0L) {
            throw new BusinessException("请输入实体油");
        }
        if (etcFee < 0L) {
            throw new BusinessException("请输入etc");
        }
        if (arriveFee < 0L) {
            throw new BusinessException("请输入到付款");
        }
        if (isPayArriveFee == null || isPayArriveFee < 0) {
            throw new BusinessException("请输入到付款是否已经支付了");
        }
        // todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "cancelTheOrder" + orderId + userId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        // 通过租户id，找到租户用户id
        Long tenantUserId = sysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        SysUser tenantUser = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = sysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 通过用户id获取用户信息
        SysUser sysOperator = sysOperatorService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //查询用户是否车队
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        List<OrderOilSource> sourceList = orderOilSourceService.getOrderOilSourceByUserIdAndOrderId(userId, orderId,-1);
        // 是否开票 0不开票，1开票(不开票油可以用其他订单油支付)
        // 根据用户ID和资金渠道类型获取账户信息
        OrderAccount tenantAccount = null;
        if (virtualOilFee > 0 ) {
            List<BusiSubjectsRel> oilList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_CANCEL_ORDER);
            for (OrderOilSource source : sourceList) {
                if (orderId.longValue() != source.getSourceOrderId().longValue()) {
                    tenantAccount = orderAccountService.queryOrderAccount(tenantUserId, source.getVehicleAffiliation(),source.getSourceTenantId(),source.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                    amountFeeSubjectsRel.setAmountFee(source.getNoPayOil() + source.getNoRebateOil() + source.getNoCreditOil());
                    oilList.add(amountFeeSubjectsRel);
                    // 计算费用集合
                    List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CANCEL_THE_ORDER, oilList);
                    long soNbr = CommonUtil.createSoNbr();
                    accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CANCEL_THE_ORDER,
                            0L, "", tenantAccount, busiSubjectsRelList, soNbr, orderId,"", null, tenantId, null, "", null, tenantAccount.getVehicleAffiliation(),loginInfo);
                    oilList.clear();
                }
            }
        }
        //查询订单限制数据
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("订单信息不存在!");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入订单油资金渠道");
        }
        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        Long noPayEtc = ol.getNoPayEtc() == null ? 0L : ol.getNoPayEtc();
        OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,ol.getUserType());
        //long marginBalance = account.getMarginBalance();
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetList = new ArrayList<BusiSubjectsRel>();
        //查询附加运费
        AdditionalFee af = additionalFeeService.getAdditionalFeeByOrderId(orderId);
        Long appendFreight =0L;
        if(af!=null&&af.getState()== OrderAccountConst.ADDITIONAL_FEE.STATE1) {//待付款状态
            List<Long> subjectsId = new ArrayList<Long>();
            subjectsId.add(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB);
            List<PayoutIntf> list = payoutIntfService.queryPayoutIntf(subjectsId, orderId);
            if (list != null && list.size() > 0) {
                PayoutIntf pay = list.get(0);
                String respCode = pay.getRespCode();
                if(respCode == null || HttpsMainUtils.respCodeFail.equals(respCode) || HttpsMainUtils.respCodeCollection.equals(respCode)) {
                    appendFreight=af.getAppendFreight();
                    af.setState(OrderAccountConst.ADDITIONAL_FEE.STATE4);//已撤销
                    af.setDealState(OrderAccountConst.ADDITIONAL_FEE_DEAL_STATE.STATE3);
                    af.setDealRemark("撤销订单_已撤销");
                    additionalFeeService.saveOrUpdate(af);
                }
            }
        }

        //现金撤单
        //查询支付预付款提现记录
        // 提现记录更改状态
        Long billServiceFee = 0L;
        if (amountFee > 0L||appendFreight>0L) {
            //PayoutIntf payResult = null;
            //List<PayoutIntf> list = payoutIntfSV.queryPayoutIntf(EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB, orderId);
            List<Long> subjectsId = new ArrayList<Long>();
            subjectsId.add(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB);
            subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP);
            subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP);
            subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP);
            //subjectsId.add(EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB);//20190305到付款
            List<PayoutIntf> list = payoutIntfService.queryPayoutIntf(subjectsId, orderId);

            if (list != null && list.size() > 0) {
                Long tempAmountFee = amountFee;
                for (PayoutIntf pay : list) {
                    if (tempAmountFee <= 0L&&appendFreight==0L) {
                        continue;
                    }
                    String respCode = pay.getRespCode();
                    if (respCode == null || HttpsMainUtils.respCodeFail.equals(respCode) || HttpsMainUtils.respCodeCollection.equals(respCode)) {
                        if (tempAmountFee >= pay.getTxnAmt()) {
                            pay.setRespCode(HttpsMainUtils.respCodeInvalid);
                            pay.setRespMsg("撤单成功");
                            payoutIntfService.saveOrUpdate(pay);
                            tempAmountFee -= pay.getTxnAmt();
                            billServiceFee += (pay.getBillServiceFee() == null ? 0L : pay.getBillServiceFee());
                            //取消审核流程
                            if (pay.getTxnType() == OrderAccountConst.TXN_TYPE.XX_TXN_TYPE && pay.getIsAutomatic() == IS_TURN_AUTOMATIC_0) {
                                try {
                                    auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, pay.getId(), pay.getPayTenantId());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            pay.setRespMsg("撤单_扣减部分现金");
                            pay.setTxnAmt(pay.getTxnAmt() - tempAmountFee);
                            pay.setAppendFreight(pay.getAppendFreight()==null?0L:pay.getAppendFreight()-appendFreight);
                            //路歌开票 服务费 20190717
//                            boolean isLuge = billManageTF.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
//                            if (isLuge) {
//                                Map<String, Object> result = billManageTF.calculationServiceFee(Long.parseLong(vehicleAffiliation), pay.getTxnAmt(), 0L, 0L, pay.getTxnAmt(), tenantId,null);
//                                long serviceFee = (Long) result.get("lugeBillServiceFee");
//                                billServiceFee += (pay.getBillServiceFee() == null ? 0L : pay.getBillServiceFee() - serviceFee);
//                                pay.setBillServiceFee(serviceFee);
//                            }
                            payoutIntfService.saveOrUpdate(pay);
                            tempAmountFee = 0L;
                        }
                    }
                }
                //司机应收逾期扣减
                BusiSubjectsRel receivableOut = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_CASH_RECEIVABLE_OUT, amountFee - tempAmountFee);
                busiList.add(receivableOut);

                //车队应付逾期扣减
                BusiSubjectsRel payableOut = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_OUT, amountFee + appendFreight - tempAmountFee);
                fleetList.add(payableOut);
                if (tempAmountFee > 0) {
                    //司机应付逾期
                    BusiSubjectsRel payableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_IN, tempAmountFee);
                    busiList.add(payableIN);

                    //车队应收逾期
                    BusiSubjectsRel receivableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_CASH_RECEIVABLE_IN, appendFreight+tempAmountFee);
                    fleetList.add(receivableIN);
                }

            } else {
                throw new BusinessException("未找到订单号为：" + orderId + " 支付预付相关记录");
            }
        }
        BusiSubjectsRel OilFeeSubjectsRelEntity = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ENTITY_OIL, entityOilFee);
        busiList.add(OilFeeSubjectsRelEntity);
        //回退共享油
        if (virtualOilFee > 0 ) {
            for (OrderOilSource source : sourceList) {
                if (source.getOrderId().longValue() == source.getSourceOrderId().longValue() && source.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE) {
                    Long tempNoPayOil = source.getNoPayOil();
                    Long tempNoRebateOil = source.getNoRebateOil();
                    Long tempNoCreditOil = source.getNoCreditOil();
                    long oilFee = (tempNoPayOil + tempNoRebateOil + tempNoCreditOil);
                    if (oilFee <= 0) {
                        continue;
                    }
                    //原路返回还是回退到转移账户
                    Integer recallType = EnumConsts.OIL_RECHARGE_RECALL_TYPE.RECALL_TYPE1;
                    if (source.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 && source.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                        List<BaseBillInfo> baseBillInfoList = baseBillInfoService.getBaseBillInfo(orderId);
                        if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                            BaseBillInfo bbi = baseBillInfoList.get(0);
                            if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                                recallType = EnumConsts.OIL_RECHARGE_RECALL_TYPE.RECALL_TYPE2;
                            } else {
                                bbi.setOil(bbi.getOil() - oilFee);
                                bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                                bbi.setUpdateTime(LocalDateTime.now());
                                baseBillInfoService.saveOrUpdate(bbi);
                                recallType = EnumConsts.OIL_RECHARGE_RECALL_TYPE.RECALL_TYPE1;
                            }
                        }
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("noPayOil", tempNoPayOil);
                    map.put("noRebateOil", tempNoRebateOil);
                    map.put("noCreditOil", tempNoCreditOil);
                    oilSourceRecordService.recallOil(userId, String.valueOf(orderId), tenantUserId, EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL, tenantId, map,recallType,loginInfo);
                }
            }
        }
        if (noPayOil >= virtualOilFee) {
            BusiSubjectsRel OilFeeSubjectsRelVirtual = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL, virtualOilFee);
            busiList.add(OilFeeSubjectsRelVirtual);
            MatchAmountUtil.matchAmounts(virtualOilFee, 0, 0, "noPayOil","noRebateOil","noCreditOil", sourceList);
        } else {
            BusiSubjectsRel OilFeeSubjectsRelVirtual = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL, noPayOil);
            busiList.add(OilFeeSubjectsRelVirtual);
            MatchAmountUtil.matchAmounts(noPayOil, 0, 0, "noPayOil","noRebateOil","noCreditOil", sourceList);
            long debtOil = (virtualOilFee - noPayOil);
            //司机应付逾期
            BusiSubjectsRel payableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN, debtOil );
            busiList.add(payableIN);

            //车队应收逾期
            BusiSubjectsRel receivableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_RECEIVABLE_IN, debtOil);
            fleetList.add(receivableIN);
        }
        if (noPayEtc >= etcFee) {
            BusiSubjectsRel amountETCFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ETC, etcFee);
            busiList.add(amountETCFeeSubjectsRel);
        } else {
            BusiSubjectsRel amountETCFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ETC, noPayEtc);
            busiList.add(amountETCFeeSubjectsRel);
            long debtETC = (etcFee - noPayEtc);
            //司机应付逾期
            BusiSubjectsRel payableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ETC_PAYABLE_IN, debtETC );
            busiList.add(payableIN);

            //车队应收逾期
            BusiSubjectsRel receivableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ETC_RECEIVABLE_IN, debtETC);
            fleetList.add(receivableIN);
        }
        //到付款
        if (isPayArriveFee == OrderConsts.AMOUNT_FLAG.ALREADY_PAY && arriveFee > 0) {
            long tempArriveFee = arriveFee;
            List<Long> subjectsId = new ArrayList<Long>();
            subjectsId.add(EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB);//20190305到付款
            subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP);//20190305到付款
            List<PayoutIntf> list = payoutIntfService.queryPayoutIntf(subjectsId, orderId);

            for (PayoutIntf pay : list) {
                if (tempArriveFee <= 0L) {
                    continue;
                }
                String respCode = pay.getRespCode();
                if (respCode == null || HttpsMainUtils.respCodeFail.equals(respCode) || HttpsMainUtils.respCodeCollection.equals(respCode)) {
                    if (tempArriveFee >= pay.getTxnAmt()) {
                        pay.setRespCode(HttpsMainUtils.respCodeInvalid);
                        pay.setRespMsg("撤单成功");
                        payoutIntfService.saveOrUpdate(pay);
                        tempArriveFee -= pay.getTxnAmt();
                        billServiceFee += (pay.getBillServiceFee() == null ? 0L : pay.getBillServiceFee());
                        //取消审核流程
                        if (pay.getTxnType() == OrderAccountConst.TXN_TYPE.XX_TXN_TYPE && pay.getIsAutomatic() == IS_TURN_AUTOMATIC_0) {
                            try {
                                auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, pay.getId(), pay.getPayTenantId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        pay.setRespMsg("撤单_扣减部分现金");
                        pay.setTxnAmt(pay.getTxnAmt() - tempArriveFee);
                        //路歌开票 服务费 20190717
//                        boolean isLuge = billManageTF.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
//                        if (isLuge) {
//                            Map<String, Object> result = billManageTF.calculationServiceFee(Long.parseLong(vehicleAffiliation), pay.getTxnAmt(), 0L, 0L, pay.getTxnAmt(), tenantId,null);
//                            long serviceFee = (Long) result.get("lugeBillServiceFee");
//                            billServiceFee += (pay.getBillServiceFee() == null ? 0L : pay.getBillServiceFee() - serviceFee);
//                            pay.setBillServiceFee(serviceFee);
//                        }
                        payoutIntfService.saveOrUpdate(pay);
                        tempArriveFee = 0L;
                    }
                }
            }
            //司机应收逾期扣减
            BusiSubjectsRel receivableArriveOut = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_RECEIVABLE_OUT, arriveFee - tempArriveFee);
            busiList.add(receivableArriveOut);
            //车队应付逾期扣减
            BusiSubjectsRel payableArriveOut = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_PAYABLE_OUT, arriveFee - tempArriveFee);
            fleetList.add(payableArriveOut);
            if (tempArriveFee > 0) {
                //司机应付逾期
                BusiSubjectsRel payableArriveIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_PAYABLE_IN, tempArriveFee);
                busiList.add(payableArriveIN);
                //车队应收逾期
                BusiSubjectsRel receivableArriveIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_RECEIVABLE_IN, tempArriveFee);
                fleetList.add(receivableArriveIN);
            }
        }
        //服务费
        if (billServiceFee > 0) {
            BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4002, billServiceFee);
            fleetList.add(payableServiceFeeSubjectsRel);
        }
        List<BusiSubjectsRel> busiSubjectsRelList = null;
        long soNbr = CommonUtil.createSoNbr();
        if (busiList != null && busiList.size() > 0) {
            // 计算费用集合
            busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CANCEL_THE_ORDER, busiList);
            // 写入账户明细表并修改账户金额费用
            OrderResponseDto param = new OrderResponseDto();
            param.setSourceList(sourceList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CANCEL_THE_ORDER,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", param, vehicleAffiliation,loginInfo);
        }

        //车队应收应付
        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        // 计算费用集合
        List<BusiSubjectsRel> fleetSubjectsRelList = null;
        if (fleetList != null && fleetList.size() > 0) {
            fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CANCEL_THE_ORDER, fleetList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CANCEL_THE_ORDER,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
        }

        // 操作日志
        String remark = "撤单回退接口："  + "userId=" + userId + "订单号：" + orderId + " 可用金额：" + amountFee + " 油卡金额：" + virtualOilFee + entityOilFee+ " ETC金额：" + etcFee + " 到付款金额：" + arriveFee;
        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.CancelTheOrder, soNbr,  SysOperLogConst.OperType.Add, loginInfo.getName() + remark,loginInfo.getTenantId());

        // 写入订单限制表和订单资金流向表
        ParametersNewDto inParamNew = busiToOrderUtils.setParametersNew(userId, sysOperator.getBillId(),EnumConsts.PayInter.CANCEL_THE_ORDER, orderId, amountFee + virtualOilFee + entityOilFee + etcFee,vehicleAffiliation, "");

        inParamNew.setTotalFee(String.valueOf(amountFee + virtualOilFee + entityOilFee + etcFee));
        inParamNew.setTenantUserId(tenantUserId);
        inParamNew.setTenantBillId(tenantSysOperator.getBillId());
        inParamNew.setTenantUserName(tenantSysOperator.getName());
        inParamNew.setOrderLimitBase(ol);
        inParamNew.setSourceList(sourceList);
        inParamNew.setIsNeedBill(in.getIsNeedBill());
        if (fleetSubjectsRelList != null) {
            busiSubjectsRelList.addAll(fleetSubjectsRelList);
        }
        if (busiSubjectsRelList != null) {
            busiToOrderUtils.busiToOrderNew(inParamNew, busiSubjectsRelList,loginInfo);
        }

        //生成应收应付逾期
        //是否自动打款
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
        Integer isAutomatic = null;

        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        for (BusiSubjectsRel rel : busiList) {
            if (rel.getAmountFee() <= 0) {
                continue;
            }
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC_PAYABLE_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_PAYABLE_IN) {
                PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), tenantId, vehicleAffiliation, orderId,
                        -1L, isAutomatic, isAutomatic, userId, OrderAccountConst.PAY_TYPE.USER, EnumConsts.PayInter.CANCEL_THE_ORDER, rel.getSubjectsId(),oilAffiliation,ol.getUserType(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,0L,token);
                payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
                payoutIntf.setRemark("撤单");
                if (isTenant) {
                    payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                    payoutIntf.setPayTenantId(sysTenantDef.getId());
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
                //撤单不需要校验，直接从付款方到收款方
				/*if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
						!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
					payoutIntf.setIsDriver(PAY_TYPE.HAVIR);
				}*/
                payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
            }
        }

    }

    @Override
    public void cancelTheOwnCarOrder(Long masterUserId, String vehicleAffiliation, Long entiyOilFee, Long fictitiousOilFee, Long bridgeFee, Long masterSubsidy, Long slaveSubsidy, Long slaveUserId, Long orderId, Long tenantId, int isNeedBill,LoginInfo loginInfo,String token){

        if (masterUserId < 0L) {
            throw new BusinessException("请输入用户编号");
        }
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException("请输入租户id");
        }
        if (orderId <= 0L) {
            throw new BusinessException("请输入订单号");
        }
        // todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "cancelTheOwnCarOrder" + orderId + masterUserId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        // 通过租户id，找到租户用户id
        Long tenantUserId = sysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        SysUser tenantUser = sysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = sysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 通过用户id获取用户信息
        SysUser user = sysOperatorService.getSysOperatorByUserIdOrPhone(masterUserId, null, 0L);
        if (user == null) {
            throw new BusinessException("没有找到用户信息");
        }
        SysUser sysOperator = sysOperatorService.getSysOperatorByUserIdOrPhone(masterUserId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }

        List<OrderOilSource> sourceList = orderOilSourceService.getOrderOilSourceByUserIdAndOrderId(masterUserId, orderId,-1);
        // 是否开票 0不开票，1开票(不开票油可以用其他订单油支付)
        // 根据用户ID和资金渠道类型获取账户信息
        OrderAccount tenantAccount = null;
        //OrderOilSource oos = null;
        if (fictitiousOilFee > 0) {
            List<BusiSubjectsRel> oilList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_CANCEL_ORDER);
            for (OrderOilSource source : sourceList) {
                if (source.getOrderId().longValue() != source.getSourceOrderId().longValue()) {
                    tenantAccount = orderAccountService.queryOrderAccount(tenantUserId, source.getVehicleAffiliation(),source.getSourceTenantId(),source.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                    amountFeeSubjectsRel.setAmountFee(source.getNoPayOil() + source.getNoRebateOil() + source.getNoCreditOil());
                    oilList.add(amountFeeSubjectsRel);
                    // 计算费用集合
                    List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CANCEL_THE_ORDER, oilList);
                    long soNbr = CommonUtil.createSoNbr();
                    accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CANCEL_THE_ORDER,
                            0L, "", tenantAccount, busiSubjectsRelList, soNbr, orderId,"",
                            null, tenantId, null, "", null,
                            tenantAccount.getVehicleAffiliation(),loginInfo);
                    oilList.clear();
                }
            }

        }
        //查询订单限制数据
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(masterUserId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("订单信息不存在!");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入订单油资金渠道");
        }
        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        OrderAccount account = orderAccountService.queryOrderAccount(masterUserId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),ol.getUserType());
        //long marginBalance = account.getMarginBalance();
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetList = new ArrayList<BusiSubjectsRel>();
        //现金撤单
        //查询支付预付款提现记录
        //PayoutIntf payResult = null;
        //List<PayoutIntf> list = payoutIntfSV.queryPayoutIntf(EnumConsts.PayInter.BEFORE_PAY_CODE, EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB, orderId);
        List<Long> subjectsId = new ArrayList<Long>();
        subjectsId.add(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP);
        List<PayoutIntf> list = payoutIntfService.queryPayoutIntf(subjectsId, orderId);
        if (masterSubsidy > 0) {
            if (list != null && list.size() > 0) {
                Long tempMasterSubsidy = masterSubsidy;
                for (PayoutIntf pay : list) {
                    if (pay.getUserId() != null && pay.getUserId() == masterUserId) {
                        if (tempMasterSubsidy <= 0L) {
                            continue;
                        }
                        String respCode = pay.getRespCode();
                        if (respCode == null || HttpsMainUtils.respCodeFail.equals(respCode) || HttpsMainUtils.respCodeCollection.equals(respCode)) {
                            if (tempMasterSubsidy >= pay.getTxnAmt()) {
                                pay.setRespCode(HttpsMainUtils.respCodeInvalid);
                                pay.setRespMsg("撤单成功");
                                payoutIntfService.saveOrUpdate(pay);
                                tempMasterSubsidy -= pay.getTxnAmt();
                                //取消审核流程
                                if (pay.getTxnType() == OrderAccountConst.TXN_TYPE.XX_TXN_TYPE && pay.getIsAutomatic() == IS_TURN_AUTOMATIC_0) {
                                    try {
                                        auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, pay.getId(), pay.getPayTenantId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }								}
                            } else {
                                pay.setRespMsg("撤单_扣减部分现金");
                                pay.setTxnAmt(pay.getTxnAmt() - tempMasterSubsidy);
                                payoutIntfService.saveOrUpdate(pay);
                                tempMasterSubsidy = 0L;
                            }
                        }
                    }
                }
                //司机应收逾期扣减
                BusiSubjectsRel receivableOut = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_RECEIVABLE_OUT, masterSubsidy - tempMasterSubsidy);
                busiList.add(receivableOut);
                //车队应付逾期扣减
                BusiSubjectsRel payableOut = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_OUT, masterSubsidy - tempMasterSubsidy);
                fleetList.add(payableOut);
                if (tempMasterSubsidy > 0) {
                    //司机应付逾期
                    BusiSubjectsRel payableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN, tempMasterSubsidy);
                    busiList.add(payableIN);

                    //车队应收逾期
                    BusiSubjectsRel receivableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_RECEIVABLE_IN, tempMasterSubsidy);
                    fleetList.add(receivableIN);
                }
            } else {
                throw new BusinessException("未找到订单号为：" + orderId + " 支付预付款提现记录");
            }
        }
        BusiSubjectsRel OilFeeSubjectsRelEntity = new BusiSubjectsRel();
        OilFeeSubjectsRelEntity.setSubjectsId(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_ENTITY_OIL);
        OilFeeSubjectsRelEntity.setAmountFee(entiyOilFee);
        busiList.add(OilFeeSubjectsRelEntity);
        //回退共享油
        if (fictitiousOilFee > 0 ) {
            for (OrderOilSource source : sourceList) {
                if (orderId == source.getSourceOrderId().longValue() && source.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE) {
                    Long tempNoPayOil = source.getNoPayOil();
                    Long tempNoRebateOil = source.getNoRebateOil();
                    Long tempNoCreditOil = source.getNoCreditOil();
                    long oilFee = (tempNoPayOil + tempNoRebateOil + tempNoCreditOil);
                    if (oilFee <= 0) {
                        continue;
                    }
                    //原路返回还是回退到转移账户
                    Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                    if (source.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 && source.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                        List<BaseBillInfo> baseBillInfoList = baseBillInfoService.getBaseBillInfo(orderId);
                        if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                            BaseBillInfo bbi = baseBillInfoList.get(0);
                            if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                                recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                            } else {
                                bbi.setOil(bbi.getOil() - oilFee);
                                bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                                bbi.setUpdateTime(LocalDateTime.now());
                                baseBillInfoService.saveOrUpdate(bbi);
                                recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                            }
                        }
                    }

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("noPayOil", tempNoPayOil);
                    map.put("noRebateOil", tempNoRebateOil);
                    map.put("noCreditOil", tempNoCreditOil);
                    oilSourceRecordService.recallOil(masterUserId, String.valueOf(orderId), tenantUserId,
                            EnumConsts.SubjectIds.CANCEL_ORDER_FICTITIOUS_OIL, tenantId, map,recallType,loginInfo);
                }
            }
        }
        if (noPayOil >= fictitiousOilFee) {
            BusiSubjectsRel OilFeeSubjectsRelVirtual = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_FICTITIOUS_OIL, fictitiousOilFee);
            busiList.add(OilFeeSubjectsRelVirtual);
            MatchAmountUtil.matchAmounts(fictitiousOilFee, 0, 0, "noPayOil","noRebateOil","noCreditOil", sourceList);
        } else {
            BusiSubjectsRel OilFeeSubjectsRelVirtual = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_FICTITIOUS_OIL, noPayOil);
            busiList.add(OilFeeSubjectsRelVirtual);
            MatchAmountUtil.matchAmounts(noPayOil, 0, 0, "noPayOil","noRebateOil","noCreditOil", sourceList);
            long debtOil = (fictitiousOilFee - noPayOil);
            //司机应付逾期
            BusiSubjectsRel payableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN, debtOil );
            busiList.add(payableIN);

            //车队应收逾期
            BusiSubjectsRel receivableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_RECEIVABLE_IN, debtOil);
            fleetList.add(receivableIN);
        }

        BusiSubjectsRel amountETCFeeSubjectsRel = new BusiSubjectsRel();
        amountETCFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.CANCEL_ORDER_BRIDGE);
        amountETCFeeSubjectsRel.setAmountFee(bridgeFee);
        busiList.add(amountETCFeeSubjectsRel);

        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = null;
        long soNbr = CommonUtil.createSoNbr();
        if (busiList != null && busiList.size() > 0) {
            busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CANCEL_THE_ORDER, busiList);
            // 写入账户明细表并修改账户金额费用
            OrderResponseDto param = new OrderResponseDto();
            param.setSourceList(sourceList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CANCEL_THE_ORDER,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", param, vehicleAffiliation,loginInfo);
        }

        List<BusiSubjectsRel> fleetSubjectsRelList = null;
        //车队应收应付
        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        if (fleetList != null && fleetList.size() > 0) {
            // 计算费用集合
            fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CANCEL_THE_ORDER, fleetList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CANCEL_THE_ORDER,
                    sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                    tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
        }

        // 写入订单限制表和订单资金流向表
        ParametersNewDto inParamNew = busiToOrderUtils.setParametersNew(masterUserId, sysOperator.getBillId(),
                EnumConsts.PayInter.CANCEL_THE_ORDER, orderId,
                masterSubsidy + fictitiousOilFee + entiyOilFee + bridgeFee,vehicleAffiliation, "");

        inParamNew.setTotalFee(String.valueOf(masterSubsidy + fictitiousOilFee + entiyOilFee + bridgeFee));
        inParamNew.setTenantUserId(tenantUserId);
        inParamNew.setTenantBillId(tenantSysOperator.getBillId());
        inParamNew.setTenantUserName(tenantSysOperator.getName());
        inParamNew.setOrderLimitBase(ol);
        inParamNew.setSourceList(sourceList);
        inParamNew.setIsNeedBill(isNeedBill);
        if (fleetSubjectsRelList != null) {
            busiSubjectsRelList.addAll(fleetSubjectsRelList);
        }
        if (busiSubjectsRelList != null) {
            busiToOrderUtils.busiToOrderNew(inParamNew, busiSubjectsRelList,loginInfo);
        }
        //生成应收应付逾期
        //是否自动打款
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
        Integer isAutomatic = null;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }
        for (BusiSubjectsRel rel : busiList) {
            if (rel.getAmountFee() <= 0) {
                continue;
            }
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN) {
                PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantUserId,
                        OrderAccountConst.PAY_TYPE.TENANT, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE,
                        rel.getAmountFee(), tenantId, vehicleAffiliation, orderId,
                        -1L, isAutomatic, isAutomatic, masterUserId, OrderAccountConst.PAY_TYPE.USER,
                        EnumConsts.PayInter.CANCEL_THE_ORDER, rel.getSubjectsId(),
                        oilAffiliation,ol.getUserType(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,0L,token);
                payoutIntf.setRemark("撤单");
                payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
                payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
            }
        }
        //副驾驶
        if (slaveUserId > 0) {
            // 通过userID获取用户信息
            SysUser sysOperator1 = sysOperatorService.getSysOperatorByUserIdOrPhone(slaveUserId, null, 0L);
            if (sysOperator1 == null) {
                throw new BusinessException("没有找到副驾驶用户信息!");
            }
            //查询订单限制数据
            OrderLimit slaveOrder =  orderLimitService.getOrderLimitByUserIdAndOrderId(slaveUserId, orderId,-1);
            if (slaveOrder == null) {
                throw new BusinessException("订单信息不存在!");
            }
            // 根据用户ID和资金渠道类型获取账户信息
            OrderAccount account1 = orderAccountService.queryOrderAccount(sysOperator1.getUserInfoId(), vehicleAffiliation,0L, tenantId,slaveOrder.getOilAffiliation(),slaveOrder.getUserType());
            //long marginBalance1 = account1.getMarginBalance();
            // 预付款费用（副驾驶）
            List<BusiSubjectsRel> busiList1 = new ArrayList<BusiSubjectsRel>();
            List<BusiSubjectsRel> fleetList1 = new ArrayList<BusiSubjectsRel>();
            //现金撤单
            //查询支付预付款提现记录
            if (slaveSubsidy > 0) {
                //PayoutIntf payResult1 = null;
                if (list != null && list.size() > 0) {
                    Long tempSlaveSubsidy = slaveSubsidy;
                    for (PayoutIntf pay : list) {
                        if (pay.getUserId() != null && pay.getUserId() == slaveUserId) {
                            if (tempSlaveSubsidy <= 0L) {
                                continue;
                            }
                            String respCode = pay.getRespCode();
                            if (respCode == null || HttpsMainUtils.respCodeFail.equals(respCode) || HttpsMainUtils.respCodeCollection.equals(respCode)) {
                                if (tempSlaveSubsidy >= pay.getTxnAmt()) {
                                    pay.setRespCode(HttpsMainUtils.respCodeInvalid);
                                    pay.setRespMsg("撤单成功");
                                    payoutIntfService.saveOrUpdate(pay);
                                    tempSlaveSubsidy -= pay.getTxnAmt();
                                    //取消审核流程
                                    if (pay.getTxnType() == OrderAccountConst.TXN_TYPE.XX_TXN_TYPE && pay.getIsAutomatic() == IS_TURN_AUTOMATIC_0) {
                                        try {
                                            auditOutService.cancelProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, pay.getId(), pay.getPayTenantId());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }									}
                                } else {
                                    pay.setRespMsg("撤单_扣减部分现金");
                                    pay.setTxnAmt(pay.getTxnAmt() - tempSlaveSubsidy);
                                    payoutIntfService.saveOrUpdate(pay);
                                    tempSlaveSubsidy = 0L;
                                }
                            }
                        }
                    }
                    //司机应收逾期扣减
                    BusiSubjectsRel receivableOut = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_RECEIVABLE_OUT, slaveSubsidy - tempSlaveSubsidy);
                    busiList1.add(receivableOut);
                    //车队应付逾期扣减
                    BusiSubjectsRel payableOut = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_OUT, slaveSubsidy - tempSlaveSubsidy);
                    fleetList1.add(payableOut);
                    if (tempSlaveSubsidy > 0) {
                        //司机应付逾期
                        BusiSubjectsRel payableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN, tempSlaveSubsidy);
                        busiList1.add(payableIN);

                        //车队应收逾期
                        BusiSubjectsRel receivableIN = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_RECEIVABLE_IN, tempSlaveSubsidy);
                        fleetList1.add(receivableIN);
                    }
                } else {
                    throw new BusinessException("未找到订单号为：" + orderId + " 支付预付款提现记录");
                }
            }
            // 计算费用集合
            List<BusiSubjectsRel> busiSubjectsRelList1 = null;
            if (busiList1 != null && busiList1.size() > 0) {
                busiSubjectsRelList1 = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CANCEL_THE_ORDER, busiList1);
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CANCEL_THE_ORDER,
                        sysOperator1.getUserInfoId(), sysOperator1.getName(), account1, busiSubjectsRelList1,
                        soNbr, orderId, sysOperator1.getName(), null,tenantId, null, "", null,vehicleAffiliation,loginInfo);
            }
            //车队
            // 计算费用集合
            List<BusiSubjectsRel> fleetSubjectsRelList1 = null;
            if (fleetList1 != null && fleetList1.size() > 0) {
                fleetSubjectsRelList1 = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CANCEL_THE_ORDER, fleetList1);
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CANCEL_THE_ORDER,
                        sysOperator1.getUserInfoId(), sysOperator1.getName(), fleetAccount, fleetSubjectsRelList1, soNbr, orderId,
                        tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,loginInfo);
            }

            // 写入订单限制表和订单资金流向表

            ParametersNewDto slaveInParam = busiToOrderUtils.setParametersNew(slaveUserId, sysOperator1.getBillId(),
                    EnumConsts.PayInter.CANCEL_THE_ORDER, orderId,
                    masterSubsidy + fictitiousOilFee + entiyOilFee + bridgeFee,vehicleAffiliation, "");

            slaveInParam.setTotalFee(String.valueOf(masterSubsidy + fictitiousOilFee + entiyOilFee + bridgeFee));
            slaveInParam.setTenantUserId(tenantUserId);
            slaveInParam.setTenantBillId(tenantSysOperator.getBillId());
            slaveInParam.setTenantUserName(tenantSysOperator.getName());
            slaveInParam.setOrderLimitBase(ol);
            slaveInParam.setSourceList(sourceList);
            slaveInParam.setIsNeedBill(isNeedBill);

            if (fleetSubjectsRelList1 != null) {
                busiSubjectsRelList1.addAll(fleetSubjectsRelList1);
            }
            if (busiSubjectsRelList1 != null) {
                busiToOrderUtils.busiToOrderNew(slaveInParam, busiSubjectsRelList1,loginInfo);
            }
            for (BusiSubjectsRel rel : busiList1) {
                if (rel.getAmountFee() <= 0) {
                    continue;
                }
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN ) {
                    PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantUserId,
                            OrderAccountConst.PAY_TYPE.TENANT, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE,
                            rel.getAmountFee(), tenantId, vehicleAffiliation, orderId,
                            -1L, isAutomatic, isAutomatic, slaveUserId, OrderAccountConst.PAY_TYPE.USER,
                            EnumConsts.PayInter.CANCEL_THE_ORDER, rel.getSubjectsId(),oilAffiliation,
                            slaveOrder.getUserType(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,0L,token);
                    payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
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
                    payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                }
            }
        }
    }

    @Override
    public void updateTheOrder(UpdateTheOrderInDto inParam,LoginInfo user,String token) {
        Long userId = inParam.getUserId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long originalAmountFee = inParam.getOriginalAmountFee();
        Long updateAmountFee = inParam.getUpdateAmountFee();
        Long originalVirtualOilFee = inParam.getOriginalVirtualOilFee();
        Long updatelongVirtualOilFee = inParam.getUpdatelongVirtualOilFee();
        Long originalEntityOilFee = inParam.getOriginalEntityOilFee();
        Long updateEntityOilFee = inParam.getUpdateEntityOilFee();
        Long originalEtcFee = inParam.getOriginalEtcFee();
        Long updateEtcFee = inParam.getUpdateEtcFee();
        Long orderId = inParam.getOrderId();
        Long tenantId = inParam.getTenantId();
        Integer isNeedBill = inParam.getIsNeedBill();
        int oilUserType = inParam.getOilUserType();
        Long originalArriveFee = inParam.getOriginalArriveFee();
        Long updateArriveFee = inParam.getUpdateArriveFee();
        Integer isPayArriveFee = inParam.getIsPayArriveFee();
		/*Integer originalOilConsumer = inParam.getOriginalOilConsumer();
		Integer updateOilConsumer = inParam.getUpdateOilConsumer();*/
        Integer originalOilAccountType = inParam.getOriginalOilAccountType();
        Integer updateOilAccountType = inParam.getUpdateOilAccountType();
        Integer originalOilBillType = inParam.getOriginalOilBillType();
        Integer updateOilBillType = inParam.getUpdateOilBillType();
        if (userId == null || userId < 0) {
            throw new BusinessException("请输入用户编号");
        }
        if (tenantId == null || tenantId <= 0L) {
            throw new BusinessException("请输入租户id");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("请输入订单号");
        }
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("请输入订单资金渠道");
        }
        if (originalAmountFee == null || updateAmountFee == null || originalAmountFee < 0 || updateAmountFee < 0) {
            throw new BusinessException("订单现金不能小于0");
        }
        if (originalVirtualOilFee == null || updatelongVirtualOilFee == null || originalVirtualOilFee < 0 || updatelongVirtualOilFee < 0) {
            throw new BusinessException("订单虚拟油不能小于0");
        }
        if (originalEntityOilFee == null || updateEntityOilFee == null || originalEntityOilFee < 0 || updateEntityOilFee < 0) {
            throw new BusinessException("订单实体油不能小于0");
        }
        if (originalEtcFee == null || updateEtcFee == null || originalEtcFee < 0 || updateEtcFee < 0) {
            throw new BusinessException("订单ETC不能小于0");
        }
        if (originalArriveFee == null || updateArriveFee == null || originalArriveFee < 0 || updateArriveFee < 0) {
            throw new BusinessException("到付款小于0");
        }
        if (isPayArriveFee == null || isPayArriveFee < 0) {
            throw new BusinessException("到付款状态不合法");
        }
        if (originalOilAccountType == null || updateOilAccountType == null || originalOilAccountType <= 0 || updateOilAccountType <= 0) {
            throw new BusinessException("请输入油来源账户类型");
        }
        if (originalOilBillType == null || updateOilBillType == null || originalOilBillType <= 0 || updateOilBillType <= 0) {
            throw new BusinessException("请输入油票据类型");
        }
        //todo 加锁
        //  boolean isLock = SysContexts.getLock(this.getClass().getName() + "updateTheOrder" + orderId + userId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        // 通过租户id，找到租户用户id
        Long tenantUserId = sysTenantDefService.getTenantById(tenantId).getAdminUser();
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 通过用户id获取用户信息
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        //查询用户是否车队
        SysTenantDef sysTenantDef = sysTenantDefService.selectByAdminUser(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        boolean isOwnCarUser = false;
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        OrderSchedulerH orderSchedulerH = null;
        if (orderScheduler == null) {
            orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("根据订单号：" + orderId + " 没有找到订单信息");
            }
            if (null != orderSchedulerH.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderSchedulerH.getVehicleClass()) {
                isOwnCarUser = true;
            }
        } else {
            if (null != orderScheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
                isOwnCarUser = true;
            }
        }
        boolean isUpdateOilAccountType = false;
        if (originalOilAccountType.intValue() != updateOilAccountType.intValue()) {//修改订单改了消费对象
            isUpdateOilAccountType = true;
            boolean isConsumeOil =  operationOilService.getOrderIsConsumeOil(orderId);
            if (isConsumeOil) {
                throw new BusinessException("单号为：" + orderId + " 已经加油过了，不允许更改油来源类型");
            }
        }
        //查询订单限制数据
        List<OrderOilSource> sourceList = orderOilSourceService.getOrderOilSourceByUserIdAndOrderId(userId, orderId,-1);
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("订单信息不存在!");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入订单油资金渠道");
        }
        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        Long noPayEtc = ol.getNoPayEtc() == null ? 0L : ol.getNoPayEtc();
        OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),ol.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetList = new ArrayList<BusiSubjectsRel>();
        long serviceFee = 0;
        long arriveServiceFee = 0;
        //预付款现金
        if (originalAmountFee  > updateAmountFee ) {
            Long cash = originalAmountFee - updateAmountFee;
            //司机应付逾期
            BusiSubjectsRel cashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW, cash);
            busiList.add(cashRel);
            //车队应收逾期
            BusiSubjectsRel fleetCashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_LOW, cash);
            fleetList.add(fleetCashRel);
        } else if (originalAmountFee <  updateAmountFee) {
            Long cash = updateAmountFee - originalAmountFee;
            //司机应收逾期
            BusiSubjectsRel cashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP, cash);
            busiList.add(cashRel);
            //路歌开票 服务费 20190717  司机应收账户不记服务费
            boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
            if (isLuge) {
                Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), cash, 0L, 0L, cash, tenantId,null);
                serviceFee = (Long) result.get("lugeBillServiceFee");
            }
            //车队应付逾期
            BusiSubjectsRel fleetCashRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_UPP, cash);
            fleetList.add(fleetCashRel);
            if (serviceFee > 0) {
                BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4001, serviceFee);
                fleetList.add(payableServiceFeeSubjectsRel);
            }
        }
        //虚拟油
        this.updateOilAccountType(sourceList, ol, inParam, busiList,fleetList,
                isUpdateOilAccountType,isOwnCarUser,null,user);
        //实体油
        if (originalEntityOilFee > updateEntityOilFee) {
            Long entityOil = originalEntityOilFee - updateEntityOilFee;
            BusiSubjectsRel entityRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_LOW, entityOil);
            busiList.add(entityRel);
        } else if (originalEntityOilFee < updateEntityOilFee) {
            Long entityOil = updateEntityOilFee - originalEntityOilFee;
            BusiSubjectsRel entityRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP, entityOil);
            busiList.add(entityRel);
            BusiSubjectsRel entityOutRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP_OUT, entityOil);
            busiList.add(entityOutRel);

        }
        //ETC
        if (originalEtcFee > updateEtcFee) {
            Long etcFee = originalEtcFee - updateEtcFee;
            //可以撤回未使用的etc
            Long backUpEtc = 0L;
            //司机应付逾期
            Long payableEtc = 0L;
            if (noPayEtc >= etcFee) {
                backUpEtc = etcFee;
            } else {
                backUpEtc = noPayEtc;
                payableEtc = etcFee - noPayEtc;
            }
            if (backUpEtc > 0) {
                BusiSubjectsRel etcRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ETC_LOW, backUpEtc);
                busiList.add(etcRel);
            }
            if (payableEtc > 0) {
                //司机应付逾期
                BusiSubjectsRel payableEtcRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW, payableEtc);
                busiList.add(payableEtcRel);
                //车队应付逾期
                BusiSubjectsRel fleetPayableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ETC_LOW, payableEtc);
                fleetList.add(fleetPayableOilRel);
            }
        } else if (originalEtcFee < updateEtcFee) {
            Long etcFee = updateEtcFee - originalEtcFee;
            BusiSubjectsRel etcRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_ETC_UPP, etcFee);
            busiList.add(etcRel);
        }
        //到付款
        if (isPayArriveFee == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            if (originalArriveFee > updateArriveFee) {
                Long arriveFee = originalArriveFee - updateArriveFee;
                //司机应付逾期
                BusiSubjectsRel arriveRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW, arriveFee);
                busiList.add(arriveRel);
                //车队应收逾期
                BusiSubjectsRel fleetArriveRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_LOW, arriveFee);
                fleetList.add(fleetArriveRel);
            } else if (originalArriveFee < updateArriveFee) {
                Long arriveFee = updateArriveFee - originalArriveFee;
                //司机应收逾期
                BusiSubjectsRel arriveRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP, arriveFee);
                busiList.add(arriveRel);
                //路歌开票 服务费 20190717  司机应收账户不记服务费
                boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
                if (isLuge) {
                    Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), arriveFee, 0L, 0L, arriveFee, tenantId,null);
                    arriveServiceFee = (Long) result.get("lugeBillServiceFee");
                }
                //车队应付逾期
                BusiSubjectsRel fleetArriveRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_UPP, arriveFee);
                fleetList.add(fleetArriveRel);
                if (arriveServiceFee > 0) {
                    BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4008, arriveServiceFee);
                    fleetList.add(payableServiceFeeSubjectsRel);
                }
            }
        }

        List<BusiSubjectsRel> busiSubjectsRelList = null;
        if (busiList != null && busiList.size() > 0) {
            // 计算费用集合
            busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, busiList);
            // 写入账户明细表并修改账户金额费用
            long soNbr = CommonUtil.createSoNbr();
            OrderResponseDto param = new OrderResponseDto();
            param.setSourceList(sourceList);
            accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId,
                    sysOperator.getName(), null, tenantId, null, "", param, vehicleAffiliation,user);

            if (fleetList != null && fleetList.size() > 0) {
                //车队应收应付
                OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId,ol.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                // 计算费用集合
                List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, fleetList);
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                        sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                        tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation,user);
                busiSubjectsRelList.addAll(fleetSubjectsRelList);
            }
            //是否自动打款
            boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
            Integer isAutomatic = null;
            if (isAutoTransfer) {
                isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
            } else {
                isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
            }
            //生成提现记录
            for (BusiSubjectsRel rel : busiSubjectsRelList) {
                if (rel.getAmountFee() <= 0) {
                    continue;
                }
                //应收
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP) {
                    PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), -1L, vehicleAffiliation, orderId,
                            tenantId, isAutomatic, isAutomatic, tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.UPDATE_THE_ORDER, rel.getSubjectsId(),oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,ol.getUserType(),0L,token);
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP ) {
                        payoutIntf.setBillServiceFee(serviceFee);
                    } else if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP) {
                        payoutIntf.setBillServiceFee(arriveServiceFee);
                    }
                    payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
                    payoutIntf.setRemark("修改订单");
                    if (isTenant) {
                        payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                        payoutIntf.setTenantId(sysTenantDef.getId());
                    }
                    if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                            !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                        payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
                    }
                    payoutIntf.setBusiCode(String.valueOf(orderId));
                    if (orderScheduler != null) {
                        payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                    } else {
                        if (orderSchedulerH != null) {
                            payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                        }
                    }
                    payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                    if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                            !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                        //写入payout_order
                        payoutOrderService.createPayoutOrder(userId, rel.getAmountFee(), OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
                    }
                }
                //应付
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW
                        || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW) {
                    PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.TENANT, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, rel.getAmountFee(), tenantId, vehicleAffiliation, orderId,
                            -1L, isAutomatic, isAutomatic, userId, OrderAccountConst.PAY_TYPE.USER, EnumConsts.PayInter.UPDATE_THE_ORDER, rel.getSubjectsId(),oilAffiliation,ol.getUserType(),SysStaticDataEnum.USER_TYPE.ADMIN_USER,0L,token);
                    payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
                    payoutIntf.setRemark("修改订单");
                    if (isTenant) {
                        payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                        payoutIntf.setPayTenantId(sysTenantDef.getId());
                    }
                    payoutIntf.setBusiCode(String.valueOf(orderId));
                    if (orderScheduler != null) {
                        payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                    } else {
                        if (orderSchedulerH != null) {
                            payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                        }
                    }
                    payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,token);
                }
            }
        }
        //操作订单限制表
        ParametersNewDto inParamNew = busiToOrderUtils.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),EnumConsts.PayInter.UPDATE_THE_ORDER, orderId, updateAmountFee + updatelongVirtualOilFee + updateEntityOilFee + updateEtcFee, vehicleAffiliation,"");
        inParamNew.setTotalFee(String.valueOf(updateAmountFee + updatelongVirtualOilFee + updateEntityOilFee + updateEtcFee));
        inParamNew.setTenantUserId(tenantUserId);
        inParamNew.setTenantBillId(tenantSysOperator.getBillId());
        inParamNew.setTenantUserName(tenantSysOperator.getName());
        inParamNew.setOrderLimitBase(ol);
        inParamNew.setSourceList(sourceList);
        inParamNew.setIsNeedBill(isNeedBill);
        if (busiSubjectsRelList != null) {
            busiToOrderUtils.busiToOrderNew(inParamNew, busiSubjectsRelList,user);
        }


    }

    @Override
    public void updateTheOwnCarOrder(UpdateTheOwnCarOrderInDto inParam) throws Exception {

    }


    @Override
    public OrderLimitFeeOutDto getOrderLimitFeeOut(Long orderId, long originalAmountFee, long originalVirtualOilFee, long originalEtcFee, Long tenantId) {

        if (orderId == null || orderId <= 0) {
            throw new BusinessException("请输入订单单号!");
        }
        if (originalAmountFee < 0) {
            throw new BusinessException("请输入订单原预付现金金额!");
        }
        if (originalVirtualOilFee < 0) {
            throw new BusinessException("请输入订单原虚拟油金额!");
        }
        if (originalEtcFee < 0) {
            throw new BusinessException("请输入订单原etc!");
        }
        long useCash = 0;
        long useOil = 0;
        long useEtc = 0;
        long noPayEtc = 0;
        long noPayOil = 0;
        long noPayCash = 0;
        long useFinal = 0;
        long noPayFinal = 0;
        List<OrderLimit> orderLimits = orderLimitService.getOrderLimit(orderId, tenantId,-1);
        if (orderLimits == null || orderLimits.size() <= 0) {
            throw new BusinessException("根据订单号：" + orderId + "开单方租户id：" + tenantId + " 未找到订单限制表!");
        }
        for (OrderLimit ol : orderLimits) {
            useEtc += (ol.getPaidEtc() == null ? 0L : ol.getPaidEtc());
            noPayOil += (ol.getNoPayOil() == null ? 0L : ol.getNoPayOil());
            noPayEtc += (ol.getNoPayEtc() == null ? 0L : ol.getNoPayEtc());
            noPayFinal += (ol.getNoPayFinal() == null ? 0L : ol.getNoPayFinal());
            noPayFinal += (ol.getPledgeOilcardFee() == null ? 0L : ol.getPledgeOilcardFee());
        }
        List<Long> subjectsId = new ArrayList<Long>();
        subjectsId.add(EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB);//预付款
        subjectsId.add(EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB);//异常补偿
        subjectsId.add(EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN);//预支
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP);
        subjectsId.add(EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB);
        subjectsId.add(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP);
        subjectsId.add(EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN);
        subjectsId.add(EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN);
        subjectsId.add(EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE);
        subjectsId.add(EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN);
        subjectsId.add(EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN);
        List<PayoutIntf> PayoutIntfList = payoutIntfService.queryPayoutIntf(subjectsId, orderId);
        if (PayoutIntfList != null && PayoutIntfList.size() > 0) {
            for (PayoutIntf pay : PayoutIntfList) {
                String respCode = pay.getRespCode();
                //预付现金
                if (pay.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB ) {
                    if (respCode != null && respCode != HttpsMainUtils.respCodeInvalid ) {
                        useCash += (pay.getTxnAmt() == null ? 0L : pay.getTxnAmt());
                    }
                    if (respCode == null) {
                        noPayCash += (pay.getTxnAmt() == null ? 0L : pay.getTxnAmt());
                    }
                }

                //尾款
                if (pay.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE
                        || pay.getSubjectsId() == EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN) {
                    if (respCode != null && respCode != HttpsMainUtils.respCodeInvalid ) {
                        useFinal += (pay.getTxnAmt() == null ? 0L : pay.getTxnAmt());
                    }
                    if (respCode == null) {
                        noPayFinal += (pay.getTxnAmt() == null ? 0L : pay.getTxnAmt());
                    }
                }
            }
        }
        OrderLimitFeeOutDto out = new OrderLimitFeeOutDto();
        out.setOrderId(orderId);
        out.setUseCash(useCash);
        out.setUseOil(useOil);
        out.setUseEtc(useEtc);
        out.setNoPayOil(noPayOil);
        out.setNoPayEtc(noPayEtc);
        out.setNoPayCash(noPayCash);
        out.setUseFinal(useFinal);
        out.setNoPayFinal(noPayFinal);
        return out;
    }

    @Override
    public Map<String, Object> updateOrderAccountOil(Long userId,long orderId,Long tenantUserId, Long tenantId, int isNeedBill, Long oilFee,
                                                     List<OrderOilSource> sourceList,LoginInfo user)  {
        boolean isOwnCarUser = false;
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("根据订单号：" + orderId + " 没有找到订单信息");
            }
            if (null != orderSchedulerH.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderSchedulerH.getVehicleClass()) {
                isOwnCarUser = true;
            }
        } else {
            if (null != orderScheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == orderScheduler.getVehicleClass()) {
                isOwnCarUser = true;
            }
        }

        // 根据用户ID和资金渠道类型获取账户信息
        OrderAccount tenantAccount = null;
        if (oilFee > 0 && isOwnCarUser) {
            List<BusiSubjectsRel> oilList = new ArrayList<BusiSubjectsRel>();
            BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_UPP);
            for (OrderOilSource source : sourceList) {
                if (orderId != source.getSourceOrderId().longValue() && source.getMatchAmount() != null && source.getMatchAmount() > 0) {
                    tenantAccount = orderAccountService.queryOrderAccount(tenantUserId, source.getVehicleAffiliation(),source.getSourceTenantId(),source.getOilAffiliation(),SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                    amountFeeSubjectsRel.setAmountFee(source.getMatchAmount());
                    oilList.add(amountFeeSubjectsRel);
                    // 计算费用集合
                    List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.UPDATE_THE_ORDER, oilList);
                    long soNbr = CommonUtil.createSoNbr();
                    accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.UPDATE_THE_ORDER,
                            0L, "", tenantAccount, busiSubjectsRelList,
                            soNbr, orderId,"", null, tenantId, null,
                            "", null, tenantAccount.getVehicleAffiliation(),user);
                    oilList.clear();
                }
            }

        }
        Map<String, Object> out = new HashMap<String, Object>();
        return out;
    }


    @Override
    public List<DriverOrderOilOutDto> queryDriverOilByOrderId(Long orderId){

        if (orderId == null || orderId <= 0L) {
            throw new BusinessException("请输入订单号");
        }
        List<QueryDriverOilByOrderIdVo> list = orderOilSourceService.queryDriverOilByOrderId(orderId,-1);
        List<DriverOrderOilOutDto> out = new ArrayList<DriverOrderOilOutDto>();
        if (list != null && list.size() > 0) {
            for (QueryDriverOilByOrderIdVo obj : list) {
                Long amount = 0L;
                Long userId = 0L;
                if (obj != null) {
                    DriverOrderOilOutDto doo = new DriverOrderOilOutDto();
                    if (obj.getSourceAmountSum() != null) {
                        amount = Long.valueOf(obj.getSourceAmountSum() + "");
                    }
                    if (obj.getUserId() != null) {
                        userId = Long.valueOf(obj.getUserId() + "");
                    }
                    doo.setOrderOil(amount);
                    doo.setUserId(userId);
                    out.add(doo);
                }
            }
        }
        return out;
    }

    /**
     *
     * @param sourceList
     * @param ol
     * @param inParam
     * @throws Exception
     */
    public void updateOilAccountType(List<OrderOilSource> sourceList, OrderLimit ol, UpdateTheOrderInDto inParam, List<BusiSubjectsRel> busiList, List<BusiSubjectsRel> fleetList, Boolean isUpdateOilAccountType,
                                     Boolean isOwnCarUser, UpdateTheOwnCarOrderInDto inParam1, LoginInfo user) {


        Long userId = null;
        Long originalVirtualOilFee = null;
        Long updatelongVirtualOilFee = null;
        Long orderId = null;
        Long tenantId = null;
        Integer originalOilAccountType = null;
        Integer updateOilAccountType = null;
        Integer originalOilBillType = null;
        Integer updateOilBillType = null;
        Integer isNeedBill = null;
        String vehicleAffiliation = null;
        if (inParam != null) {
            userId = inParam.getUserId();
            originalVirtualOilFee = inParam.getOriginalVirtualOilFee();
            updatelongVirtualOilFee = inParam.getUpdatelongVirtualOilFee();
            orderId = inParam.getOrderId();
            tenantId = inParam.getTenantId();
            originalOilAccountType = inParam.getOriginalOilAccountType();
            updateOilAccountType = inParam.getUpdateOilAccountType();
            originalOilBillType = inParam.getOriginalOilBillType();
            updateOilBillType = inParam.getUpdateOilBillType();
            isNeedBill = inParam.getIsNeedBill();
            vehicleAffiliation = inParam.getVehicleAffiliation();
        }
        if (inParam1 != null) {
            userId = inParam1.getMasterUserId();
            originalVirtualOilFee = inParam1.getOriginalFictitiousOilFee();
            updatelongVirtualOilFee = inParam1.getUpdateFictitiousOilFee();
            orderId = inParam1.getOrderId();
            tenantId = inParam1.getTenantId();
            originalOilAccountType = inParam1.getOriginalOilAccountType();
            updateOilAccountType = inParam1.getUpdateOilAccountType();
            originalOilBillType = inParam1.getOriginalOilBillType();
            updateOilBillType = inParam1.getUpdateOilBillType();
            isNeedBill = inParam1.getIsNeedBill();
            vehicleAffiliation = inParam1.getVehicleAffiliation();
        }

        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        Long tenantUserId = sysTenantDefService.getTenantById(tenantId).getAdminUser();
        //虚拟油
        OrderOilSource oos = null;
        if (originalVirtualOilFee.longValue() > updatelongVirtualOilFee.longValue() && !isUpdateOilAccountType) {//虚拟油减少
            Long virtualOil = originalVirtualOilFee - updatelongVirtualOilFee;
            //可以撤回未使用的虚拟油
            Long backUpOil = 0L;
            //司机应付逾期
            Long payableOil = 0L;
            if (noPayOil >= virtualOil) {
                backUpOil = virtualOil;
            } else {
                backUpOil = noPayOil;
                payableOil = virtualOil - noPayOil;
            }
            if (backUpOil > 0) {
                //    matchAmountUtil.matchAmountOilSourceRecord(backUpOil, 0, 0, "noPayOil","noRebateOil","noCreditOil", sourceList);
                this.updateOrderAccountOil(userId, orderId, tenantUserId,tenantId, isNeedBill, backUpOil, sourceList,user);
                BusiSubjectsRel oilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW, backUpOil);
                busiList.add(oilRel);
                for (OrderOilSource source : sourceList) {
                    if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                        oos = source;
                        break;
                    }
                }
                //原路返回还是回退到转移账户
                Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                if (oos != null && oos.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 && oos.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
                    Long oilFee = oos.getMatchAmount() == null ? 0L : oos.getMatchAmount();
                    List<BaseBillInfo> baseBillInfoList = baseBillInfoService.getBaseBillInfo(orderId);
                    if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                        BaseBillInfo bbi = baseBillInfoList.get(0);
                        if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                            recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                        } else {
                            bbi.setOil(bbi.getOil() - oilFee);
                            bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                            bbi.setUpdateTime(LocalDateTime.now());
                            baseBillInfoService.saveOrUpdate(bbi);
                            recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                        }
                    }
                }
                //回退共享油
                if (oos != null && oos.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && backUpOil > 0) {
                    Long tempNoPayOil = oos.getMatchNoPayOil() == null ? 0L : oos.getMatchNoPayOil();
                    Long tempNoRebateOil = oos.getMatchNoRebateOil() == null ? 0L : oos.getMatchNoRebateOil();
                    Long tempNoCreditOil = oos.getMatchNoCreditOil() == null ? 0L : oos.getMatchNoCreditOil();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("noPayOil", tempNoPayOil);
                    map.put("noRebateOil", tempNoRebateOil);
                    map.put("noCreditOil", tempNoCreditOil);
                    oilSourceRecordService.recallOil(userId, String.valueOf(orderId), tenantUserId,
                            EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW, tenantId, map,recallType,user);
                }
                long matchNoPayOil = oos.getMatchNoPayOil() == null ? 0 : oos.getMatchNoPayOil();
                long matchNoRebateOil = oos.getMatchNoRebateOil() == null ? 0 : oos.getMatchNoRebateOil();
                long matchNoCreditOil = oos.getMatchNoCreditOil() == null ? 0 : oos.getMatchNoCreditOil();
                oos.setNoPayOil(oos.getNoPayOil() - matchNoPayOil);
                oos.setSourceAmount(oos.getSourceAmount() - matchNoPayOil);
                oos.setNoCreditOil(oos.getNoCreditOil() - matchNoCreditOil);
                oos.setCreditOil(oos.getCreditOil() - matchNoCreditOil);
                oos.setNoRebateOil(oos.getNoRebateOil() - matchNoRebateOil);
                oos.setRebateOil(oos.getRebateOil() - matchNoRebateOil);
                orderOilSourceService.saveOrUpdate(oos);
            }
            if (payableOil > 0) {
                //司机应付逾期
                BusiSubjectsRel payableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW, payableOil);
                busiList.add(payableOilRel);
                //车队应付逾期
                BusiSubjectsRel fleetPayableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_VIRTUALOIL_LOW, payableOil);
                fleetList.add(fleetPayableOilRel);
            }
        }
        if (originalVirtualOilFee.longValue() < updatelongVirtualOilFee.longValue() && !isUpdateOilAccountType) {//虚拟油增大
            Long virtualOil = updatelongVirtualOilFee - originalVirtualOilFee;
            long tempVirtualOilFee = virtualOil;
            BusiSubjectsRel oilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP, virtualOil);
            busiList.add(oilRel);
            if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2 && virtualOil > 0 && isOwnCarUser) {

                OrderAccountBalanceDto oilBlaceMap = orderAccountService.getOrderAccountBalance(tenantUserId,"oilBalance",tenantId,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                OrderAccountOutVo orderAccountOut =oilBlaceMap.getOa();
                if (null == orderAccountOut) {
                    throw new BusinessException("查询车队油账户错误");
                }
                //车队可用油账户余额
                Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
                if (null == canUseOilBalance) {
                    canUseOilBalance = 0L;
                }
                if (canUseOilBalance.longValue() > 0) {
                    if (virtualOil > canUseOilBalance.longValue()) {
                        List<OrderOilSource> sourceListNew = orderOilSourceService.matchOrderAccountToOrderLimit(canUseOilBalance.longValue(), tenantUserId,
                                orderId,tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.UPDATE_THE_ORDER,
                                EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW,user);
                        if (sourceListNew == null || sourceListNew.size() <= 0) {
                            throw new BusinessException("车队油账户分配出错");
                        } else {
                            long totalMatchAmount = 0;
                            for (OrderOilSource ros : sourceListNew) {
                                totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                            }
                            if (totalMatchAmount != canUseOilBalance) {
                                throw new BusinessException("充值油与车队账户油分配不一致");
                            }
                        }
                        tempVirtualOilFee -= canUseOilBalance;
                    } else {
                        List<OrderOilSource> sourceListNew = orderOilSourceService.matchOrderAccountToOrderLimit(virtualOil, tenantUserId, orderId,
                                tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.UPDATE_THE_ORDER,
                                EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW,user);
                        if (sourceListNew == null || sourceListNew.size() <= 0) {
                            throw new BusinessException("车队油账户分配出错");
                        } else {
                            long totalMatchAmount = 0;
                            for (OrderOilSource ros : sourceListNew) {
                                totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                            }
                            if (totalMatchAmount != virtualOil) {
                                throw new BusinessException("充值油与车队账户油分配不一致");
                            }
                        }
                        tempVirtualOilFee = 0L;
                    }
                }
            }
            for (OrderOilSource source : sourceList) {
                if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                    source.setSourceAmount(source.getSourceAmount() + virtualOil);
                    source.setNoPayOil(source.getNoPayOil() + virtualOil);
                    orderOilSourceService.saveOrUpdate(source);
                    oos = source;
                    break;
                }
            }
            if (oos == null) {
                oos = orderOilSourceService.saveOrderOilSource(userId, orderId, orderId, tempVirtualOilFee,
                        tempVirtualOilFee, 0L, tenantId,LocalDateTime.now(), user.getId(), isNeedBill,
                        vehicleAffiliation,ol.getOrderDate(),ol.getOilAffiliation(),ol.getOilConsumer(),
                        0L,0L,0L,0L,0L,0L,
                        ol.getUserType(),updateOilAccountType,updateOilBillType,user);
            }
            Integer distributionType = null;
            if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
                distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
            } else if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
                if (oos.getOilBillType() ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//获取油票
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
                } else if (oos.getOilBillType() ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//获取运输专票
                    distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE2;
                }
            }

            //共享油费母卡充值油分配
            if (tempVirtualOilFee > 0 && oos.getOilConsumer() == OrderConsts.OIL_CONSUMER.SHARE) {
                Map<String, Object> resultMap = oilRechargeAccountService.distributionOil(userId, tenantUserId,
                        tempVirtualOilFee, String.valueOf(orderId),
                        EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP,
                        tenantId, OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1,distributionType,user);
                Long tempNoPayOil = (Long) resultMap.get("noPayOil");
                Long noRebateOil = (Long) resultMap.get("noRebateOil");
                Long noCreditOil = (Long) resultMap.get("noCreditOil");
                if (tempNoPayOil == null) {
                    throw new BusinessException("充值现金不能为空");
                }
                if (noRebateOil == null) {
                    throw new BusinessException("返利不能为空");
                }
                if (noCreditOil == null) {
                    throw new BusinessException("授信不能为空");
                }
                oos.setSourceAmount(oos.getSourceAmount() + tempNoPayOil - virtualOil);
                oos.setNoPayOil(oos.getNoPayOil() + tempNoPayOil - virtualOil);
                oos.setCreditOil(oos.getCreditOil() + noCreditOil);
                oos.setNoCreditOil(oos.getNoCreditOil() + noCreditOil);
                oos.setRebateOil(oos.getRebateOil() + noRebateOil);
                oos.setNoRebateOil(oos.getNoRebateOil() + noRebateOil);
                orderOilSourceService.saveOrUpdate(oos);
            }
        }
        if (originalVirtualOilFee.longValue() > updatelongVirtualOilFee.longValue() && isUpdateOilAccountType) {//改小
            Long virtualOil = originalVirtualOilFee - updatelongVirtualOilFee;
            //可以撤回未使用的虚拟油
            Long backUpOil = 0L;
            //司机应付逾期
            Long payableOil = 0L;
            if (noPayOil >= virtualOil) {
                backUpOil = virtualOil;
            } else {
                backUpOil = noPayOil;
                payableOil = virtualOil - noPayOil;
            }
            BusiSubjectsRel oilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW, backUpOil);
            busiList.add(oilRel);
            if (payableOil > 0) {
                //司机应付逾期
                BusiSubjectsRel payableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW, payableOil);
                busiList.add(payableOilRel);
                //车队应付逾期
                BusiSubjectsRel fleetPayableOilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_VIRTUALOIL_LOW, payableOil);
                fleetList.add(fleetPayableOilRel);
            }
            this.dealUpdateOil(sourceList, ol, inParam, isUpdateOilAccountType, isOwnCarUser,inParam1,user);
        }
        if (originalVirtualOilFee.longValue() < updatelongVirtualOilFee.longValue() && isUpdateOilAccountType) {//改大
            Long virtualOil = updatelongVirtualOilFee - originalVirtualOilFee;
            BusiSubjectsRel oilRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP, virtualOil);
            busiList.add(oilRel);
            this.dealUpdateOil(sourceList, ol, inParam, isUpdateOilAccountType, isOwnCarUser,inParam1,user);
        }
        if (originalVirtualOilFee.longValue() == updatelongVirtualOilFee.longValue() && isUpdateOilAccountType) {//不变，消费对象改变
            this.dealUpdateOil(sourceList, ol, inParam, isUpdateOilAccountType, isOwnCarUser,inParam1,user);
        }
    }

    public void dealUpdateOil(List<OrderOilSource> sourceList,OrderLimit ol,UpdateTheOrderInDto inParam,Boolean isUpdateOilAccountType,
                              Boolean isOwnCarUser,UpdateTheOwnCarOrderInDto inParam1,LoginInfo user) {

        Long userId = null;
        Long originalVirtualOilFee = null;
        Long updatelongVirtualOilFee = null;
        Long orderId = null;
        Long tenantId = null;
        Integer originalOilAccountType = null;
        Integer updateOilAccountType = null;
        Integer originalOilBillType = null;
        Integer updateOilBillType = null;
        Integer isNeedBill = null;
        String vehicleAffiliation = null;
        if (inParam != null) {
            userId = inParam.getUserId();
            originalVirtualOilFee = inParam.getOriginalVirtualOilFee();
            updatelongVirtualOilFee = inParam.getUpdatelongVirtualOilFee();
            orderId = inParam.getOrderId();
            tenantId = inParam.getTenantId();
            originalOilAccountType = inParam.getOriginalOilAccountType();
            updateOilAccountType = inParam.getUpdateOilAccountType();
            originalOilBillType = inParam.getOriginalOilBillType();
            updateOilBillType = inParam.getUpdateOilBillType();
            isNeedBill = inParam.getIsNeedBill();
            vehicleAffiliation = inParam.getVehicleAffiliation();
        }
        if (inParam1 != null) {
            userId = inParam1.getMasterUserId();
            originalVirtualOilFee = inParam1.getOriginalFictitiousOilFee();
            updatelongVirtualOilFee = inParam1.getUpdateFictitiousOilFee();
            orderId = inParam1.getOrderId();
            tenantId = inParam1.getTenantId();
            originalOilAccountType = inParam1.getOriginalOilAccountType();
            updateOilAccountType = inParam1.getUpdateOilAccountType();
            originalOilBillType = inParam1.getOriginalOilBillType();
            updateOilBillType = inParam1.getUpdateOilBillType();
            isNeedBill = inParam1.getIsNeedBill();
            vehicleAffiliation = inParam1.getVehicleAffiliation();
        }

        Long noPayOil = ol.getNoPayOil() == null ? 0L : ol.getNoPayOil();
        Long tenantUserId = sysTenantDefService.getTenantById(tenantId).getAdminUser();

        OrderOilSource oos = null;
        //  MatchAmountUtil.matchAmounts(originalVirtualOilFee, 0, 0, "noPayOil","noRebateOil","noCreditOil", sourceList);
        this.updateOrderAccountOil(userId, orderId, tenantUserId,tenantId, isNeedBill, originalVirtualOilFee, sourceList,user);
        for (OrderOilSource source : sourceList) {
            if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                oos = source;
                break;
            }
        }
        //原路返回还是回退到转移账户
        Integer recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
        if (oos != null && oos.getOilAccountType().intValue() == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3 && oos.getOilBillType().intValue() == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {
            Long oilFee = oos.getMatchAmount() == null ? 0L : oos.getMatchAmount();
            List<BaseBillInfo> baseBillInfoList = baseBillInfoService.getBaseBillInfo(orderId);
            if (baseBillInfoList != null && baseBillInfoList.size() > 0) {
                BaseBillInfo bbi = baseBillInfoList.get(0);
                if (bbi.getBillState() != null && bbi.getBillState() >= OrderAccountConst.BASE_BILL_INFO.BILL_STATE3) {//订单已开票
                    recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE2;
                } else {
                    bbi.setOil(bbi.getOil() - oilFee);
                    bbi.setWithdrawAmount(bbi.getWithdrawAmount() - oilFee);
                    bbi.setUpdateTime(LocalDateTime.now());
                    baseBillInfoService.saveOrUpdate(bbi);
                    recallType = EnumConsts.OIL_RECHARGE_ACCOUNT_TYPE.ACCOUNT_TYPE1;
                }
            }
        }
        //回退共享油
        if (oos != null && oos.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE && originalVirtualOilFee > 0) {
            Long tempNoPayOil = oos.getMatchNoPayOil() == null ? 0L : oos.getMatchNoPayOil();
            Long tempNoRebateOil = oos.getMatchNoRebateOil() == null ? 0L : oos.getMatchNoRebateOil();
            Long tempNoCreditOil = oos.getMatchNoCreditOil() == null ? 0L : oos.getMatchNoCreditOil();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("noPayOil", tempNoPayOil);
            map.put("noRebateOil", tempNoRebateOil);
            map.put("noCreditOil", tempNoCreditOil);
            oilSourceRecordService.recallOil(userId, String.valueOf(orderId), tenantUserId,
                    EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW, tenantId, map,recallType,user);
        }
        Long virtualOil = updatelongVirtualOilFee - originalVirtualOilFee;
        Long tempVirtualOilFee = virtualOil + noPayOil;
        if (tempVirtualOilFee < 0){
            tempVirtualOilFee = 0L;
        }
        Long tempOil = tempVirtualOilFee;
        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2 && updatelongVirtualOilFee > 0 && isOwnCarUser) {
            OrderAccountBalanceDto oilBlaceMap = orderAccountService.getOrderAccountBalance(tenantUserId,"oilBalance",tenantId,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            OrderAccountOutVo orderAccountOut = oilBlaceMap.getOa ();
            if (null == orderAccountOut) {
                throw new BusinessException("查询车队油账户错误");
            }
            //车队可用油账户余额
            Long canUseOilBalance = orderAccountOut.getCanUseOilBalance();
            if(null == canUseOilBalance){
                canUseOilBalance = 0L;
            }
            if (canUseOilBalance.longValue() > 0) {
                if (tempVirtualOilFee > canUseOilBalance.longValue()) {
                    List<OrderOilSource> sourceListNew = orderOilSourceService.matchOrderAccountToOrderLimit(canUseOilBalance.longValue(), tenantUserId, orderId,
                            tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.UPDATE_THE_ORDER,
                            EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW,user);
                    if (sourceListNew == null || sourceListNew.size() <= 0) {
                        throw new BusinessException("车队油账户分配出错");
                    } else {
                        long totalMatchAmount = 0;
                        for (OrderOilSource ros : sourceListNew) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != canUseOilBalance) {
                            throw new BusinessException("充值油与车队账户油分配不一致");
                        }
                    }
                    tempVirtualOilFee -= canUseOilBalance;
                } else {
                    List<OrderOilSource> sourceListNew = orderOilSourceService.matchOrderAccountToOrderLimit(tempVirtualOilFee, tenantUserId,
                            orderId,tenantId, isNeedBill, vehicleAffiliation, userId,EnumConsts.PayInter.UPDATE_THE_ORDER,
                            EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW,user);
                    if (sourceListNew == null || sourceListNew.size() <= 0) {
                        throw new BusinessException("车队油账户分配出错");
                    } else {
                        long totalMatchAmount = 0;
                        for (OrderOilSource ros : sourceListNew) {
                            totalMatchAmount += (ros.getMatchAmount() == null ? 0L : ros.getMatchAmount());
                        }
                        if (totalMatchAmount != tempVirtualOilFee) {
                            throw new BusinessException("充值油与车队账户油分配不一致");
                        }
                    }
                    tempVirtualOilFee = 0L;
                }
            }
        }
        for (OrderOilSource source : sourceList) {
            if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                oos = source;
                break;
            }
        }
        if (oos == null) {
            oos = orderOilSourceService.saveOrderOilSource(userId, orderId, orderId, tempOil, tempOil, 0L, tenantId,LocalDateTime.now(),user.getId(), isNeedBill, vehicleAffiliation,ol.getOrderDate(),ol.getOilAffiliation(),ol.getOilConsumer(),
                    0L,0L,0L,0L,0L,0L,
                    ol.getUserType(),updateOilAccountType,updateOilBillType,user);
        }
        Integer distributionType = null;
        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {
            distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE3;
        } else if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {
            if (oos.getOilBillType() ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//获取油票
                distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE1;
            } else if (oos.getOilBillType() ==  OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//获取运输专票
                distributionType = EnumConsts.OIL_RECHARGE_BILL_TYPE.BILL_TYPE2;
            }
        }
        //共享油费母卡充值油分配
        if (tempVirtualOilFee > 0 && updateOilAccountType != OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {
            Map<String, Object> resultMap = oilRechargeAccountService.distributionOil(userId, tenantUserId,
                    tempVirtualOilFee, String.valueOf(orderId), EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP,
                    tenantId, OrderAccountConst.OIL_SOURCE_RECORD.SOURCE_RECORD_TYPE1,distributionType,user);
            Long tempNoPayOil = (Long) resultMap.get("noPayOil");
            Long noRebateOil = (Long) resultMap.get("noRebateOil");
            Long noCreditOil = (Long) resultMap.get("noCreditOil");
            if (tempNoPayOil == null) {
                throw new BusinessException("充值现金不能为空");
            }
            if (noRebateOil == null) {
                throw new BusinessException("返利不能为空");
            }
            if (noCreditOil == null) {
                throw new BusinessException("授信不能为空");
            }
            oos.setSourceAmount(tempNoPayOil);
            oos.setNoPayOil(tempNoPayOil);
            oos.setCreditOil(noCreditOil);
            oos.setNoCreditOil(noCreditOil);
            oos.setRebateOil(noRebateOil);
            oos.setNoRebateOil(noRebateOil);
            orderOilSourceService.saveOrUpdate(oos);
        }

        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1) {//改为授信
            oos.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
            oos.setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
            oos.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            ol.setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
            ol.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
            ol.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            oos.setSourceAmount(tempOil);
            oos.setNoPayOil(tempOil);
            oos.setCreditOil(0L);
            oos.setNoCreditOil(0L);
            oos.setRebateOil(0L);
            oos.setNoRebateOil(0L);
            oos.setMatchNoCreditOil(0L);
            oos.setMatchNoPayOil(0L);
            oos.setMatchNoRebateOil(0L);
        }
        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2) {//改为已开票
            oos.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2);
            oos.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            oos.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            ol.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            ol.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE2);
            ol.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
        }
        if (updateOilAccountType == OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3) {//改为充值
            oos.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3);
            oos.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            ol.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
            ol.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE3);
            if (updateOilBillType == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1) {//油票
                oos.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
                ol.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE1);
            }
            if (updateOilBillType == OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2) {//运输
                oos.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2);
                ol.setOilBillType(OrderAccountConst.OIL_BILL_TYPE.OIL_BILL_TYPE2);
            }
        }
        orderOilSourceService.saveOrUpdate(oos);
        orderLimitService.saveOrUpdate(ol);
    }

}
