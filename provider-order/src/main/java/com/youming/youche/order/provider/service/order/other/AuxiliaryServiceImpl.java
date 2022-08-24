package com.youming.youche.order.provider.service.order.other;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.other.IAuxiliaryService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.utils.AccountOilAllot;
import com.youming.youche.order.provider.utils.CancelTheOrder;
import com.youming.youche.order.provider.utils.ClearAccountOil;
import com.youming.youche.order.provider.utils.ConsumeOilNew;
import com.youming.youche.order.provider.utils.ForceZhangPingNew;
import com.youming.youche.order.provider.utils.GrantSalary;
import com.youming.youche.order.provider.utils.OilAndEtcTurnCashNew;
import com.youming.youche.order.provider.utils.PayExpenseInfo;
import com.youming.youche.order.provider.utils.PayForRepairNew;
import com.youming.youche.order.provider.utils.PayOaLoan;
import com.youming.youche.order.provider.utils.PayVehicleExpenseInfo;
import com.youming.youche.order.provider.utils.PledgeReleaseOilcard;
import com.youming.youche.order.provider.utils.PrePayNew;
import com.youming.youche.order.provider.utils.RechargeAccountOilAllot;
import com.youming.youche.order.provider.utils.UpdateOrder;
import com.youming.youche.order.provider.utils.WithdrawNew;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@DubboService(version = "1.0.0")
@Service
public class AuxiliaryServiceImpl implements IAuxiliaryService {

    @Lazy
    @Resource
    private ConsumeOilNew consumeOilNew;
    @Resource
    private PrePayNew prePayNew;
    @Resource
    private OilAndEtcTurnCashNew oilAndEtcTurnCashNew;

    @Resource
    private WithdrawNew withdrawNew;

    @Resource
    private ForceZhangPingNew forceZhangPingNew;
    @Resource
    private CancelTheOrder cancelTheOrder;

    @Resource
    private PledgeReleaseOilcard pledgeReleaseOilcard;
    @Resource
    private AccountOilAllot accountOilAllot;
    @Resource
    private PayOaLoan payOaLoan;
    @Resource
    private PayExpenseInfo payExpenseInfo;
    @Resource
    @Lazy
    private PayVehicleExpenseInfo payVehicleExpenseInfo;
    @Resource
    private GrantSalary grantSalary;
    @Resource
    private IOrderFundFlowService orderFundFlowService;
    @Resource
    private PayForRepairNew payForRepairNew;
    @Resource
    private UpdateOrder updateOrder;
    @Resource
    private RechargeAccountOilAllot rechargeAccountOilAllot;

    @Resource
    private ClearAccountOil clearAccountOil;


    /**
     * @param userId 用户id
     * @param billId 用户手机号
     * @param businessId 业务id
     * @param orderId 订单id
     * @param amount 费用
     * @param vehicleAffiliation 资金渠道
     * @param finalPlanDate 尾款到账日期
     */
    @Override
    public ParametersNewDto setParametersNew(Long userId, String billId, Long businessId, Long orderId, Long amount, String vehicleAffiliation, String finalPlanDate) {
        ParametersNewDto map = new ParametersNewDto();
        map.setUserId(userId);
        map.setBillId(billId);
        map.setBusinessId(businessId);
        map.setOrderId(orderId);
        map.setAmount(amount);
        map.setVehicleAffiliation(vehicleAffiliation);
        map.setFinalPlanDate(finalPlanDate);
        return map;
    }

    @Override
    public List busiToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        long userId = inParam.getUserId();
        List results = new ArrayList();
        if (userId == 0L) {
            throw new BusinessException("用户ID不能为空!");
        }
        long businessId = inParam.getBusinessId();
        if (businessId == 0L) {
            throw new BusinessException("业务ID不能为空!");
        }
        String billId = inParam.getBillId();
        if (StringUtils.isEmpty(billId)) {
            throw new BusinessException("司机手机不能为空!");
        }
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("资金渠道不能为空!");
        }
        //todo 加锁
        //数据锁定逻辑，避免不同方法操作同样数据
        //     boolean isLock = SysContexts.getLock(this.getClass().getName() + userId + vehicleAffiliation + businessId, 3, 5);
//        if (!isLock)	//未或得资源锁，不做任何操作
//            throw new BusinessException("账户资金操作中，请稍后重试!");
        //消费油
        if(businessId== EnumConsts.PayInter.PAY_FOR_OIL_CODE){
            results = consumeOilNew.dealToOrderNew(inParam ,rels,user);
        }

        //预支
        if(businessId==EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE){
            results = prePayNew.dealToOrderNew(inParam, rels,user);
        }
        //油和ETC转现(功能化)
        if(businessId==EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH){
            results = oilAndEtcTurnCashNew.dealToOrderNew(inParam, rels,user);
        }
        //提现
        if(businessId==EnumConsts.PayInter.WITHDRAWALS_CODE){
            results = withdrawNew.dealToOrderNew(inParam, rels,user);
        }
        //强制平账
        if (businessId == EnumConsts.PayInter.FORCE_ZHANG_PING) {
            results = forceZhangPingNew.dealToOrderNew(inParam, rels,user);
        }
        //撤单
        if (businessId == EnumConsts.PayInter.CANCEL_THE_ORDER) {
            results = cancelTheOrder.dealToOrderNew(inParam, rels,user);
        }
        //油卡抵押释放
        if (businessId == EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD) {
            results = pledgeReleaseOilcard.dealToOrderNew(inParam, rels,user);
        }
        //支付预付款、修改订单油账户分配
        if (businessId == EnumConsts.PayInter.BEFORE_PAY_CODE || businessId == EnumConsts.PayInter.UPDATE_THE_ORDER) {
            results = accountOilAllot.dealToOrderNew(inParam, rels,user);
        }
        //借支审核通过
        if(businessId == EnumConsts.PayInter.OA_LOAN_AVAILABLE ||businessId == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE ){
            results = payOaLoan.dealToOrderNew(inParam, rels,user);
        }
        //报销反写-司机
        if (businessId == EnumConsts.PayInter.DRIVER_EXPENSE_ABLE) {
            results = payExpenseInfo.dealToOrderNew(inParam, rels,user);
        }
        //报销反写-车管
        if (businessId == EnumConsts.PayInter.TUBE_EXPENSE_ABLE) {
            results = payVehicleExpenseInfo.dealToOrderNew(inParam, rels,user);
        }
        //发放工资
        if (businessId == EnumConsts.PayInter.CAR_DRIVER_SALARY) {
            results = grantSalary.dealToOrderNew(inParam, rels,user);
        }
        //支付维修保养(新)
        if (businessId == EnumConsts.PayInter.PAY_FOR_REPAIR) {
            results = payForRepairNew.dealToOrderNew(inParam, rels,user);
        }
        //修改订单
        if (businessId == EnumConsts.PayInter.UPDATE_THE_ORDER) {
            results = updateOrder.dealToOrderNew(inParam, rels,user);
        }
        //充值油订单油账户分配
        if (businessId == EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL) {
            results = rechargeAccountOilAllot.dealToOrderNew(inParam, rels,user);
        }
        //清零油账户
        if (businessId == EnumConsts.PayInter.CLEAR_ACCOUNT_OIL) {
            results = clearAccountOil.dealToOrderNew(inParam, rels,user);
        }
        //未到期转已到期
        if(businessId == EnumConsts.PayInter.PAYFOR_CODE){
            results = orderFundFlowService.dealToOrderNewCode(inParam, rels,user);
        }
        //到付款
        if (businessId == EnumConsts.PayInter.PAY_ARRIVE_CHARGE) {
            results = orderFundFlowService.dealToOrderNewCharge(inParam, rels,user);
        }
        //招商车挂靠车对账单
        if (businessId == EnumConsts.PayInter.ACCOUNT_STATEMENT) {
            results = orderFundFlowService.dealToOrderNew(inParam, rels,user);
        }
        //todo 解锁
//        if (isLock) {
//            SysContexts.releaseLockByKey(this.getClass().getName() + userId + vehicleAffiliation + businessId );
//        }
        return results;
    }
}
