package com.youming.youche.order.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IOilPriceProvinceService;
import com.youming.youche.market.domain.facilitator.OilPriceProvince;
import com.youming.youche.order.api.ICreditRatingRuleFeeService;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.order.ICreditRatingRuleDefService;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.domain.CreditRatingRuleFee;
import com.youming.youche.order.domain.order.CreditRatingRuleDef;
import com.youming.youche.order.dto.CreditRatingRuleDto;
import com.youming.youche.order.dto.OrderFeeDto;
import com.youming.youche.order.dto.SaveOrUpdateCreditRatingRuleDto;
import com.youming.youche.order.provider.mapper.CreditRatingRuleMapper;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.vo.CreditRatingRuleVo;
import com.youming.youche.order.vo.OrderFeeVo;
import com.youming.youche.order.vo.QueryMemberBenefitsVo;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelService;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.util.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.conts.SysStaticDataEnum.SYS_USER_STATE.AUDIT_APPROVE;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-04
 */
@DubboService(version = "1.0.0")
@Service
public class CreditRatingRuleServiceImpl extends ServiceImpl<CreditRatingRuleMapper, CreditRatingRule> implements ICreditRatingRuleService {

    @Resource
    LoginUtils loginUtils;
//    @DubboReference(version = "1.0.0")
//    ICreditRatingRuleService creditRatingRuleService;
    @Resource
    ICreditRatingRuleFeeService creditRatingRuleFeeService;

    @Autowired
    private ICreditRatingRuleDefService creditRatingRuleDefService;
    @Resource
    IOrderDriverSubsidyService iOrderDriverSubsidyService;

    @DubboReference(version = "1.0.0")
    ITenantUserRelService tenantUserRelService;
    @Resource
    CreditRatingRuleMapper creditRatingRuleMapper;
    @DubboReference(version = "1.0.0")
    com.youming.youche.system.api.ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    ITenantUserSalaryRelService tenantUserSalaryRelService;
    @Resource
    private OrderDateUtil orderDateUtil;
    @DubboReference(version = "1.0.0")
    private IOilPriceProvinceService oilPriceProvinceService;

    @Override
    public CreditRatingRuleVo getLevel(Integer carUserType, String accessToken, Float guideMerchant, Double guidePrice) {
        try {
            LoginInfo loginInfo = loginUtils.get(accessToken);
            Long tenantId = loginInfo.getTenantId();
            QueryWrapper<CreditRatingRule> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("car_user_type", carUserType)
                    .eq("tenant_id", tenantId);

            // 查询租户车辆类型的信用登记信息
            List<CreditRatingRule> creditRatingRules = creditRatingRuleMapper.selectList(queryWrapper);
            //carUserType   会员类型：1社会车、2招商车、3自有车、4、个体户、5、外调合同车
            if (creditRatingRules != null && creditRatingRules.size() > 0) {
                CreditRatingRule creditRatingRule = creditRatingRules.get(0);
                CreditRatingRuleVo creditRatingRuleVo = new CreditRatingRuleVo();
                if (carUserType == 2 || carUserType == 3 || carUserType == 5) {
                    //预付款总计比例
                    creditRatingRuleVo.setAdvanceCharge(creditRatingRule.getAdvanceCharge());
                    //预付款总计金额
                    Float advanceCharge = creditRatingRule.getAdvanceCharge();
                    if (advanceCharge == null) {
                        advanceCharge = 0F;
                    }
                    creditRatingRuleVo.setAdvanceCashValue(guideMerchant * advanceCharge);
                    //预付现金比例
                    //如果是自有车，预付现金比例=预付总比例-油比例-ETC比例，如果是外调车或者是业务招商车，则预付现金比例=预付总比例-油比例
                    if (carUserType == 3) {
                        float etcScale = 0f;
                        if (creditRatingRule.getEtcScale() != null) {
                            etcScale = creditRatingRule.getEtcScale();
                        }
                        float v = creditRatingRule.getAdvanceCharge() - creditRatingRule.getOilScale() - etcScale;
                        creditRatingRuleVo.setAdvanceCash(v);
                        //预付现金金额
                        creditRatingRuleVo.setAdvanceCashValue(guideMerchant * v);
                    } else {
                        float v = creditRatingRule.getAdvanceCharge() - creditRatingRule.getOilScale();
                        creditRatingRuleVo.setAdvanceCash(v);
                        //预付现金金额
                        creditRatingRuleVo.setAdvanceCashValue(guideMerchant * v);
                    }
                    //到付金额比例
                    creditRatingRuleVo.setArrivalPayment(0F);
                    //到付金额
                    creditRatingRuleVo.setArrivalPaymentValue(0F);
                    //尾款总金额比例
                    float v = 1 - creditRatingRule.getAdvanceCharge();
                    creditRatingRuleVo.setBalancePayment(v);
                    //尾款总金额
                    creditRatingRuleVo.setBalancePaymentValue(guideMerchant * v);

                    CreditRatingRuleFee fee1 = creditRatingRuleFeeService.getFees(creditRatingRule.getId(), 1, 1500);
                    CreditRatingRuleFee fee2 = creditRatingRuleFeeService.getFees(creditRatingRule.getId(), 1500, 3500);
                    CreditRatingRuleFee fee3 = creditRatingRuleFeeService.getFees(creditRatingRule.getId(), 3500, 8000);
                    CreditRatingRuleFee fee4 = creditRatingRuleFeeService.getFees(creditRatingRule.getId(), 8000, null);

                    //尾款里的现金,尾款总金额-保费
                    if (guideMerchant < 1500) {
                        creditRatingRuleVo.setBalanceValue(guideMerchant * v - fee1.getInsuranceFee());
                        creditRatingRuleVo.setInsuranceFee(fee1.getInsuranceFee());
                    } else if (guideMerchant < 3500) {
                        creditRatingRuleVo.setBalanceValue(guideMerchant * v - fee2.getInsuranceFee());
                        creditRatingRuleVo.setInsuranceFee(fee2.getInsuranceFee());
                    } else if (guideMerchant < 8000) {
                        creditRatingRuleVo.setBalanceValue(guideMerchant * v - fee3.getInsuranceFee());
                        creditRatingRuleVo.setInsuranceFee(fee3.getInsuranceFee());
                    } else {
                        creditRatingRuleVo.setBalanceValue(guideMerchant * v - fee4.getInsuranceFee());
                        creditRatingRuleVo.setInsuranceFee(fee4.getInsuranceFee());
                    }
                    //会员类型：1社会车、2招商车、3自有车、4、个体户、5、外调合同车
                    creditRatingRuleVo.setCarUserType(carUserType);
                    //ETC比例，如0.01
                    creditRatingRuleVo.setEtcScale(creditRatingRule.getEtcScale());
                    //ETC金额
                    Float etcScale = creditRatingRule.getEtcScale();
                    if (etcScale == null) {
                        etcScale = 0F;
                    }
                    creditRatingRuleVo.setEtcScaleValue(guideMerchant * etcScale);
                    //油比例
                    creditRatingRuleVo.setOilScale(creditRatingRule.getOilScale());
                    //油金额
                    Float oilScale = creditRatingRule.getOilScale();
                    if (oilScale == null) {
                        oilScale = 0F;
                    }
                    creditRatingRuleVo.setOilScaleValue(guideMerchant * oilScale);
                    //招商指导价格（对应单位分） 承包价
                    creditRatingRuleVo.setGuideMerchant(guideMerchant);
                    //社会指导价格（对应单位分） 拦标价
                    creditRatingRuleVo.setGuidePrice(guidePrice);
                    //结算方式 1-预付全款，2-预付+尾款账期，3-预付+尾款月结
                    creditRatingRuleVo.setSettleType(creditRatingRule.getSettleType());
                    //回单期限
                    creditRatingRuleVo.setReceiptPeriod(creditRatingRule.getReceiptPeriod());
                    //对账期限
                    creditRatingRuleVo.setCheckPeriod(creditRatingRule.getCheckPeriod());
                    //开票期限
                    creditRatingRuleVo.setBillPeriod(creditRatingRule.getBillPeriod());
                    //付款期限
                    creditRatingRuleVo.setPayPeriod(creditRatingRule.getPayPeriod());
                    return creditRatingRuleVo;
                }
            } else {
                return new CreditRatingRuleVo();
            }
            return new CreditRatingRuleVo();
        }catch (Exception e){
            e.printStackTrace();
            return new CreditRatingRuleVo();
        }
    }


    @Override
    public OrderFeeVo getEstimatedCosts(String accessToken,OrderFeeDto orderFeeDto, Long subsidy)  {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        float pontageFee;
        float oilCost1 = 0F;
        float oilCost2 = 0F;
        float oilPrice;
        OrderFeeVo orderFeeVo = new OrderFeeVo();
        //主驾驶和副驾驶不能是同一人
        Long carDriverId = orderFeeDto.getCarDriverId();
        Long copilotUserId = orderFeeDto.getCopilotUserId();
        //补贴  每天补贴的金额*天数  天数在order_driver_subsidy里拿

        //补贴天数有两种方式：一种是到达时间，靠台时间来计算时间，靠台时间默认是第二天中午12点，到达时间默认加0.5小时一个经停点
        //第二种方式如果之前有订单，暂不做考虑
        //获取补贴天数
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime LocalTime = LocalDateTime.parse(orderFeeDto.getDependTime(), df);
        LocalDateTime localDateTime = orderDateUtil.addHourAndMins(LocalTime, Float.valueOf(orderFeeDto.getArriveTime()) + 0.5F);

        Date date2 = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date date1 = Date.from(LocalTime.atZone(ZoneId.systemDefault()).toInstant());
        int subsidyDay = OrderDateUtil.getDifferDay(date1, date2) + 1;

        //获取主驾的补贴数据
        TenantUserSalaryRel tenantUserRalaryRelByUserId = tenantUserSalaryRelService.getTenantUserRalaryRelByUserId(carDriverId, tenantId);
        if (tenantUserRalaryRelByUserId == null || tenantUserRalaryRelByUserId.getSubsidy() == null) {
            orderFeeVo.setCarDriverSubsidyValue(0F);
        } else {
            Long subsidy1 = tenantUserRalaryRelByUserId.getSubsidy();
            long carDriverSubsidyValue = subsidy1 * subsidyDay;
            orderFeeVo.setCarDriverSubsidyValue((float) carDriverSubsidyValue);
        }

        //路桥费 路桥费单价*距离
        if (orderFeeDto.getDistance() == null || orderFeeDto.getPontagePer() == null) {
            pontageFee = 0F;
        } else {
            String pontagePer = orderFeeDto.getPontagePer();
            Float aFloat = Float.valueOf(pontagePer);
            pontageFee = orderFeeDto.getDistance() * aFloat;
        }


        //总耗油量 = 空载油耗*空载距离+载重油耗*载重距离
        if (orderFeeDto.getLoadEmptyOilCost() == null || orderFeeDto.getEmptyDistance() == null) {
            oilCost1 = 0L;
        } else {
            //空载油耗
            oilCost1 = orderFeeDto.getLoadEmptyOilCost() * orderFeeDto.getEmptyDistance()/100000;
        }


        if (orderFeeDto.getLoadFullOilCost() == null || orderFeeDto.getDistance() == null) {
            //载重油耗
            oilCost2 = 0F;
        } else {
            //载重油耗
            oilCost2 = orderFeeDto.getLoadFullOilCost() * orderFeeDto.getDistance()/100000;
        }
        if (orderFeeDto.getOilPrice() == null) {
            OilPriceProvince oilPriceProvince = oilPriceProvinceService.getOilPriceProvince(orderFeeDto.getProvinceId());
            if (oilPriceProvince != null) {
                oilPrice = oilPriceProvince.getOilPrice();
            } else {
                oilPrice = 0;
            }
        } else {
            oilPrice = orderFeeDto.getOilPrice();
        }
        //总耗油耗
        float oilCost = oilCost1 + oilCost2;
        //油费 总耗油量*单价
        float oilCostPrice = oilCost * oilPrice;
        //预估成本 补贴+路桥费+油费
        float estFee = pontageFee + oilCostPrice;
        orderFeeVo.setEstFee(estFee);
        orderFeeVo.setOilTotal(oilCost);
        orderFeeVo.setOilCostPrice(oilCostPrice);
        orderFeeVo.setPontageFee(pontageFee);
        orderFeeVo.setDriverSubsidyDays(subsidyDay);
        orderFeeVo.setOilPrice(oilPrice);
        return orderFeeVo;
    }

    @Override
    public QueryMemberBenefitsVo queryMemberBenefits(String accessToken) {

        LoginInfo loginInfo= loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        QueryMemberBenefitsVo queryMemberBenefitsVo = new QueryMemberBenefitsVo();
        // 自有车
        //会员类型：1社会车、2招商车、3自有车、4、个体户、5、外调合同车
        QueryWrapper<CreditRatingRule> creditRatingRuleQueryWrapper1 = new QueryWrapper<>();
        creditRatingRuleQueryWrapper1.eq("tenant_id", tenantId)
                .eq("car_user_type", 3);
        List<CreditRatingRule> creditRatingRules1 = creditRatingRuleMapper.selectList(creditRatingRuleQueryWrapper1);
        if(creditRatingRules1 != null && creditRatingRules1.size() > 0){
            queryMemberBenefitsVo.setCreditRatingRule1(creditRatingRules1.get(0));
            List<CreditRatingRuleFee> creditRatingRuleFees = creditRatingRuleFeeService.queryCreditRatingRuleFees(creditRatingRules1.get(0).getId());
            CreditRatingRule creditRatingRule = creditRatingRules1.get(0).setFeeList(creditRatingRuleFees);
            queryMemberBenefitsVo.setCreditRatingRule1(creditRatingRule);
        }else {
            queryMemberBenefitsVo.setCreditRatingRule1(new CreditRatingRule());
        }
        // 外调车
        QueryWrapper<CreditRatingRule> creditRatingRuleQueryWrapper2 = new QueryWrapper<>();
        creditRatingRuleQueryWrapper2.eq("tenant_id", tenantId)
                .eq("car_user_type", 5);
        List<CreditRatingRule> creditRatingRules2 = creditRatingRuleMapper.selectList(creditRatingRuleQueryWrapper2);
        if(creditRatingRules2 != null && creditRatingRules2.size() > 0){
            queryMemberBenefitsVo.setCreditRatingRule2(creditRatingRules2.get(0));
            List<CreditRatingRuleFee> creditRatingRuleFees = creditRatingRuleFeeService.queryCreditRatingRuleFees(creditRatingRules2.get(0).getId());
            CreditRatingRule creditRatingRule = creditRatingRules2.get(0).setFeeList(creditRatingRuleFees);
            queryMemberBenefitsVo.setCreditRatingRule2(creditRatingRule);
        }else {
            queryMemberBenefitsVo.setCreditRatingRule2(new CreditRatingRule());
        }
        // 招商车
        QueryWrapper<CreditRatingRule> creditRatingRuleQueryWrapper3 = new QueryWrapper<>();
        creditRatingRuleQueryWrapper3.eq("tenant_id", tenantId)
                .eq("car_user_type", 2);
        List<CreditRatingRule> creditRatingRules3 = creditRatingRuleMapper.selectList(creditRatingRuleQueryWrapper3);
        if(creditRatingRules3 != null && creditRatingRules3.size() > 0){
            queryMemberBenefitsVo.setCreditRatingRule3(creditRatingRules3.get(0));
            List<CreditRatingRuleFee> creditRatingRuleFees = creditRatingRuleFeeService.queryCreditRatingRuleFees(creditRatingRules3.get(0).getId());
            CreditRatingRule creditRatingRule = creditRatingRules3.get(0).setFeeList(creditRatingRuleFees);
            queryMemberBenefitsVo.setCreditRatingRule3(creditRatingRule);
        }else {
            queryMemberBenefitsVo.setCreditRatingRule3(new CreditRatingRule());
        }
        return queryMemberBenefitsVo;
    }

    @Override
    @Transactional
    public void saveOrUpdateCreditRatingRule(SaveOrUpdateCreditRatingRuleDto saveOrUpdateCreditRatingRuleDto, String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        this.updateCreditRatingRule(saveOrUpdateCreditRatingRuleDto.getCreditRatingRuleDto1(), tenantId);
        this.updateCreditRatingRule(saveOrUpdateCreditRatingRuleDto.getCreditRatingRuleDto2(), tenantId);
        this.updateCreditRatingRule(saveOrUpdateCreditRatingRuleDto.getCreditRatingRuleDto3(), tenantId);

        saveSysOperLog(SysOperLogConst.BusiCode.UserCredit,SysOperLogConst.OperType.Add,"新增司机权益成功",accessToken,tenantId);


    }

    @Override
    public Long saveUpdateCreditRatingRule(CreditRatingRule creditRatingRule) {
        //先删除后新增
        QueryWrapper<CreditRatingRule> creditRatingRuleQueryWrapper = new QueryWrapper<>();
        creditRatingRuleQueryWrapper.eq("tenant_id",creditRatingRule.getTenantId())
                .eq("car_user_type",creditRatingRule.getCarUserType());
        List<CreditRatingRule> creditRatingRules = creditRatingRuleMapper.selectList(creditRatingRuleQueryWrapper);
        if(creditRatingRules != null && creditRatingRules.size() > 0) {
            QueryWrapper<CreditRatingRule> creditRatingRuleQueryWrapper2 = new QueryWrapper<>();
            creditRatingRuleQueryWrapper2.eq("tenant_id",creditRatingRule.getTenantId())
                    .eq("car_user_type",creditRatingRule.getCarUserType());
            creditRatingRuleMapper.delete(creditRatingRuleQueryWrapper);
        }
        this.save(creditRatingRule);
        return creditRatingRule.getId();
    }

    @Override
    public CreditRatingRule getCreditRatingRule(Long userId, Long tenantId) {
        if (tenantId == 0 || userId == 0) {
            throw new BusinessException("获取司机权益传入参数错误");
        }

        int carUserType = SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR;
//        QueryWrapper<TenantUserRel> tenantUserRelQueryWrapper = new QueryWrapper<>();
//
//        tenantUserRelQueryWrapper.eq("user_id", userId)
//                .eq("tenant_id", tenantId)
//                .eq("state", AUDIT_APPROVE);
//        List<TenantUserRel> list = tenantUserRelService.list(tenantUserRelQueryWrapper);
//        if(list==null||list.isEmpty()){
//            return null;
//        }
//        if (list.size() > 1) {
//            throw new BusinessException("司机资料不全，请先核对司机资料");
//        }
//        TenantUserRel tenantUserRel =  list.get(0);
        TenantUserRel tenantUserRel = tenantUserRelService.getTenantUserRelList(userId, tenantId, AUDIT_APPROVE);

        if (tenantUserRel != null) {

            if (tenantUserRel.getCarUserType() == null) {
                throw new BusinessException("数据错误，司机类型未设置");
            }
            carUserType = tenantUserRel.getCarUserType();
        }

        LambdaQueryWrapper<CreditRatingRule> lambdaCreditRatingRule=new QueryWrapper<CreditRatingRule>().lambda();
        lambdaCreditRatingRule.eq(CreditRatingRule::getTenantId,tenantId)
                .eq(CreditRatingRule::getCarUserType,carUserType);
        //查询已经配置的列表
        List<CreditRatingRule> ruleList = this.list(lambdaCreditRatingRule);

        if (ruleList == null || ruleList.size() <= 0) {
            return null;
        }

        CreditRatingRule creditRatingRule = ruleList.get(0);
        Long ruleId = creditRatingRule.getId();

        //根据ruleId获取保费列表
        LambdaQueryWrapper<CreditRatingRuleFee>  lambdaQueryWrapper=new QueryWrapper<CreditRatingRuleFee>().lambda();
        lambdaQueryWrapper.eq(CreditRatingRuleFee::getRuleId,ruleId);
        List<CreditRatingRuleFee> feeList =creditRatingRuleFeeService.list(lambdaQueryWrapper);

        creditRatingRule.setFeeList(feeList);

        return creditRatingRule;
    }

    @Override
    public Integer updateCreditRatingRule(CreditRatingRuleDto creditRatingRuleDto, Long tenantId) {

        CreditRatingRule creditRatingRule = new CreditRatingRule();
//判断不可预支语字数，不能超过100字
        String noAdvanceHint = creditRatingRuleDto.getNoAdvanceHint();
        if(StringUtils.isNotBlank(noAdvanceHint)){
            if(noAdvanceHint.length()>100){
                throw new BusinessException("不可预支的提示语字数不能超过100");
            }
        }
        //判断是否可预支，0：不可预支  1：可预支
        Integer isAdvance = creditRatingRuleDto.getIsAdvance();
        if(isAdvance == null){
            isAdvance = 0;
        }
        if(isAdvance == 1){
            creditRatingRule.setIsAdvance(1);
            creditRatingRule.setCounterFee(creditRatingRuleDto.getCounterFee());
            creditRatingRule.setNoAdvanceHint(noAdvanceHint);
        }else {
            creditRatingRule.setIsAdvance(0);
            creditRatingRule.setCounterFee(0F);
        }
        //预付款比例
        creditRatingRule.setAdvanceCharge(creditRatingRuleDto.getAdvanceCharge());
        //油比例
        creditRatingRule.setOilScale(creditRatingRuleDto.getOilScale());
        //判断是否为自有车，如果是自有车（承包模式），有ETC比例、智能模式、报账模式，其他车型没有
        if(creditRatingRuleDto.getCarUserType() == 3){
            creditRatingRule.setEtcScale(creditRatingRuleDto.getEtcScale());
            creditRatingRule.setRtnAmtScaleZn(creditRatingRuleDto.getRtnAmtScaleZn());
            creditRatingRule.setRtnAmtScaleBz(creditRatingRuleDto.getRtnAmtScaleBz());
        }else {
            creditRatingRule.setRtnAmtScaleCb(creditRatingRuleDto.getRtnAmtScaleCb());
        }
        //判断结算方式是否是 2-预付+尾款账期
        if(creditRatingRuleDto.getSettleType() == 2){
            creditRatingRule.setSettleType(2);
            creditRatingRule.setReceiptPeriod(creditRatingRuleDto.getReceiptPeriod());
            creditRatingRule.setCheckPeriod(creditRatingRuleDto.getCheckPeriod());
            creditRatingRule.setBillPeriod(creditRatingRuleDto.getBillPeriod());
            creditRatingRule.setPayPeriod(creditRatingRuleDto.getPayPeriod());
            creditRatingRule.setReceiptPeriodDay(0);
            creditRatingRule.setCheckPeriodDay(0);
            creditRatingRule.setBillPeriodDay(0);
            creditRatingRule.setPaymentDay(0);
        }else {
            //3-预付+尾款月结
            creditRatingRule.setSettleType(3);
            creditRatingRule.setReceiptPeriod(creditRatingRuleDto.getReciveMonth());
            creditRatingRule.setCheckPeriod(creditRatingRuleDto.getCheckMonth());
            creditRatingRule.setBillPeriod(creditRatingRuleDto.getBillMonth());
            creditRatingRule.setPayPeriod(creditRatingRuleDto.getPayMonth());
            creditRatingRule.setReceiptPeriodDay(creditRatingRuleDto.getReceiptPeriodDay());
            creditRatingRule.setCheckPeriodDay(creditRatingRuleDto.getCheckPeriodDay());
            creditRatingRule.setBillPeriodDay(creditRatingRuleDto.getBillPeriodDay());
//            creditRatingRule.setPaymentDay(creditRatingRuleDto.getPayPeriodDay());
            creditRatingRule.setPayPeriodDay(creditRatingRuleDto.getPayPeriodDay());
        }
        creditRatingRule.setCarUserType(creditRatingRuleDto.getCarUserType());
        creditRatingRule.setState(1);
        creditRatingRule.setTenantId(tenantId);
        creditRatingRule.setCreateTime(LocalDateTime.now());
        creditRatingRule.setUpdateTime(LocalDateTime.now());

        //保存数据
        Long ruleId = saveUpdateCreditRatingRule(creditRatingRule);

        List<CreditRatingRuleFee> feeList = new ArrayList<CreditRatingRuleFee>();

        //设置保险费
        for (int j = 0; j < 4; j++) {

            CreditRatingRuleFee fee = new CreditRatingRuleFee();

            fee.setRuleId(ruleId);
            fee.setState(1);
            if (j == 0) {
                fee.setMinFee(1);
                fee.setMaxFee(1500);
                // 解决bug
                String premium1 = creditRatingRuleDto.getPremium1();

                //Long aFloat = Long.valueOf(premium1)*100;
                long l = CommonUtils.objToLongMul100(premium1);
                fee.setInsuranceFee(l);
            } else if (j == 1) {
                fee.setMinFee(1500);
                fee.setMaxFee(3500);
                String premium2 = creditRatingRuleDto.getPremium2();
                //Long aFloat = Long.valueOf(premium2)*100;
                long l = CommonUtils.objToLongMul100(premium2);
                fee.setInsuranceFee(l);
            } else if (j == 2) {
                fee.setMinFee(3500);
                fee.setMaxFee(8000);
                String premium3 = creditRatingRuleDto.getPremium3();
                //Long aFloat = Long.valueOf(premium3)*100;
                long l = CommonUtils.objToLongMul100(premium3);
                fee.setInsuranceFee(l);
            } else if (j == 3) {
                fee.setMinFee(8000);
                String premium4 = creditRatingRuleDto.getPremium4();
                //Long aFloat = Long.valueOf(premium4)*100;
                long l = CommonUtils.objToLongMul100(premium4);
                fee.setInsuranceFee(l);
            }


            feeList.add(fee);
        }
        //保存CreditRatingRuleFee数据
        Integer integer = creditRatingRuleFeeService.saveUpdateCreditRatingRuleFee(feeList);
        return integer;
    }

    @Override
    public CreditRatingRule queryCreditRatingRule(Long tenantId, Integer carUserType) {
        LambdaQueryWrapper<CreditRatingRule> lambda= Wrappers.lambdaQuery();
        if(tenantId != null){
            lambda.eq(CreditRatingRule::getTenantId,tenantId);
        }
        if(carUserType != null){
            lambda.eq(CreditRatingRule::getCarUserType,carUserType);
        }
        List<CreditRatingRule> ruleList = this.list(lambda);
        if (ruleList == null||ruleList.isEmpty()){
            return null;
        }
        CreditRatingRule creditRatingRule = ruleList.get(0);
        Long ruleId = creditRatingRule.getId();
        List<CreditRatingRuleFee> feeList = creditRatingRuleFeeService.queryCreditRatingRuleFees(ruleId);
        creditRatingRule.setFeeList(feeList);
        return creditRatingRule;
    }

    @Override
    public List<CreditRatingRule> queryMemberBenefits(Integer carUserType, Long tenantId) {

        LambdaQueryWrapper<CreditRatingRule> lambda=Wrappers.lambdaQuery();
        lambda.eq(CreditRatingRule::getCarUserType,carUserType)
              .eq(CreditRatingRule::getTenantId,tenantId);


        //查询已经配置的列表
        List<CreditRatingRule> ruleList = this.list(lambda);

        //输出列表
        List<CreditRatingRule> outRulelist = new ArrayList<CreditRatingRule>();


        //把已配置的会员信息放入输出列表
        for (CreditRatingRule creditRatingRule : ruleList) {

            if (creditRatingRule.getCheckPeriod() != null && creditRatingRule.getCheckPeriod() < 0) {
                creditRatingRule.setCheckPeriod(null);
            }

            if (creditRatingRule.getBillPeriod() != null && creditRatingRule.getBillPeriod() < 0) {
                creditRatingRule.setBillPeriod(null);
            }

            if (creditRatingRule.getCheckPeriodDay() != null && creditRatingRule.getCheckPeriodDay() < 0) {
                creditRatingRule.setCheckPeriodDay(null);
            }

            if (creditRatingRule.getBillPeriodDay() != null && creditRatingRule.getBillPeriodDay() < 0) {
                creditRatingRule.setBillPeriodDay(null);
            }

            outRulelist.add(creditRatingRule);
        }


        //查询出所有的会员类型
        List<CreditRatingRuleDef> defList = creditRatingRuleDefService.list();


        //对于未配置数据的会员类型初始化数据后放入输出列表
        for (CreditRatingRuleDef creditRatingRuleDef : defList) {

            boolean notExitConfig = true;

            for (CreditRatingRule creditRatingRule : ruleList) {
                if (creditRatingRule.getLevelNumber().equals(creditRatingRuleDef.getLevelNumber())) {
                    notExitConfig = false;
                }
            }

            if(notExitConfig){
                CreditRatingRule rule = new CreditRatingRule();
                rule.setLevelNumber(creditRatingRuleDef.getLevelNumber());
                rule.setRatingName(creditRatingRuleDef.getRatingName());
                rule.setTenantId(tenantId);
                outRulelist.add(rule);
            }
        }
        return outRulelist;
    }

    @Override
    public CreditRatingRule queryCreditRatingRuleWx(Long tenantId, Integer carUserType) {
        LambdaQueryWrapper<CreditRatingRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CreditRatingRule::getTenantId, tenantId);
        queryWrapper.eq(CreditRatingRule::getCarUserType, carUserType);
        List<CreditRatingRule> ruleList = this.list(queryWrapper);

        if (ruleList == null || ruleList.size() != 1) {
            throw new BusinessException("该租户司机权益配置错误");
        }

        CreditRatingRule creditRatingRule = ruleList.get(0);

        Integer carUserTypeOld = creditRatingRule.getCarUserType();
        if (null != carUserTypeOld) {
            if (carUserTypeOld == 3) {
                creditRatingRule.setCarUserType(1);
            } else if (carUserTypeOld == 2) {
                creditRatingRule.setCarUserType(2);
            } else if (carUserTypeOld == 5) {
                creditRatingRule.setCarUserType(3);
            }
        }

        List<CreditRatingRuleFee> creditRatingRuleFeesWx = creditRatingRuleFeeService.getCreditRatingRuleFeesWx(creditRatingRule.getId());
        creditRatingRule.setFeeList(creditRatingRuleFeesWx);

        return creditRatingRule;
    }

    @Override
    public List<CreditRatingRule> getCreditRatingRules(Long tenantId) {
        LambdaQueryWrapper<CreditRatingRule> creditRatingRuleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        creditRatingRuleLambdaQueryWrapper.eq(CreditRatingRule::getTenantId,tenantId);
        return this.list(creditRatingRuleLambdaQueryWrapper);
    }


    public void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }

    /**
     * 开始时间+多少个小时 的时间
     * @param orig  开始时间
     * @param incrFloat 单位小时
     * @return
     *    返回添加后的时间
     */
    public static Date addHourAndMins(Date orig, Float incrFloat){
        float incr = incrFloat == null ? 0f : incrFloat;
        int incrHour = (int)incr;
        int incrMins = new BigDecimal(incr).subtract(new BigDecimal(incrHour)).multiply(new BigDecimal(60)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        Date des = addHour(orig, incrHour);
        return addMinis(des, incrMins);
    }
    public static Date addHour(Date date, int hour) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(getMillis(date) + (long)hour * 3600L * 1000L);
            return c.getTime();
        }
    }
    public static Date addMinis(Date date, int minis) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(getMillis(date) + (long)minis * 60L * 1000L);
            return c.getTime();
        }
    }
    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 两个时间相差多少天
     * @param date1
     * @param date2
     * eg:2017-06-04 00:00:00    2017-06-06 09:00:00 返回3天
     * eg:2017-06-04 00:00:00    2017-06-04 09:00:00 返回1天
     * @throws ParseException
     */
    public static int getDifferDay(Date date1,Date date2) throws ParseException {
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(parseDate(formatDate(date1, "yyyy-MM-dd 00:00:00")));
        aft.setTime(parseDate(formatDate(date2, "yyyy-MM-dd 00:00:00")));
        long one = Math.abs((aft.getTime().getTime()/1000 - bef.getTime().getTime()/1000)/3600/24);
        return Integer.parseInt(one+"");
    }

    public static Date parseDate(String str) {
        if (str != null && str.trim().length() != 0) {
            if (str.length() == 10) {
                return parseDate(str, "yyyy-MM-dd");
            } else if (str.length() == 13) {
                return parseDate(str, "yyyy-MM-dd HH");
            } else if (str.length() == 16) {
                return parseDate(str, "yyyy-MM-dd HH:mm");
            } else if (str.length() == 19) {
                return parseDate(str, "yyyy-MM-dd HH:mm:ss");
            } else {
                return str.length() >= 21 ? parseDate(str, "yyyy-MM-dd HH:mm:ss.S") : null;
            }
        } else {
            return null;
        }
    }

    public static Date parseDate(String str, String format) {
        try {
            if (str != null && !str.equals("")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                return simpleDateFormat.parse(str);
            } else {
                return null;
            }
        } catch (Exception var3) {
            var3.printStackTrace();
            return new Date();
        }
    }

    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        } else {
            if (format.indexOf("h") > 0) {
                format = format.replace('h', 'H');
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        }
    }
}
