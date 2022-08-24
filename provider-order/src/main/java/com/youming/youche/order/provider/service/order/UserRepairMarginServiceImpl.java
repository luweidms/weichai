package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.commons.util.DateUtil;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.ITenantProductRelService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IUserRepairMarginService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.commons.PinganIntefaceConst;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.UserRepairMargin;
import com.youming.youche.order.dto.UserRepairMarginDto;
import com.youming.youche.order.dto.order.AccountPayRepairOutDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.dto.order.PayReturnDto;
import com.youming.youche.order.dto.order.RepairAmountOutDto;
import com.youming.youche.order.provider.mapper.order.UserRepairMarginMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import com.youming.youche.record.api.service.IServiceRepairOrderService;
import com.youming.youche.record.api.service.IServiceRepairOrderVerService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.finance.commons.util.DateUtil.DATETIME12_FORMAT2;


/**
 * <p>
 * 维修保养记录表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-24
 */
@DubboService(version = "1.0.0")
@Service
public class UserRepairMarginServiceImpl extends BaseServiceImpl<UserRepairMarginMapper, UserRepairMargin> implements IUserRepairMarginService {
    @Resource
    UserRepairMarginMapper userRepairMarginMapper;

    @Resource
    IPayoutIntfService iPayoutIntfService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;
    @DubboReference(version = "1.0.0")
     ISysUserService sysUserService;
    @DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService1;
    @DubboReference(version = "1.0.0")
    IServiceProductService serviceProductService;
    @DubboReference(version = "1.0.0")
    ITenantProductRelService iTenantProductRelService;
    @Resource
    IOrderAccountService iOrderAccountService;
    @Resource
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @Resource
    IAccountDetailsService iAccountDetailsService;
    @Resource
    LoginUtils loginUtils;
    @Resource
    IAccountBankRelService iAccountBankRelService;

    @Resource
    IBaseBusiToOrderService iBaseBusiToOrderService;
    @Resource
    ITenantServiceRelService iTenantServiceRelService;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService tenantVehicleRelService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService vehicleDataInfoService;
    @DubboReference(version = "1.0.0")
    IServiceRepairOrderService serviceRepairOrderService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    IServiceRepairOrderVerService serviceRepairOrderVerService;
    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;
    @DubboReference(version = "1.0.0")
    ISysCfgService iSysCfgService;
    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @Override
    public Page<UserRepairMargin> queryUserRepairMargins(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize) {
        Page<UserRepairMargin> userRepairMarginPage = userRepairMarginMapper.queryUserRepairMargins(new Page<>(pageNum, pageSize), advanceExpireOutVo);
        return userRepairMarginPage;
    }

    @Override
    public UserRepairMargin getUserRepairMargin(Long flowId) {
        UserRepairMargin userRepairMargin = userRepairMarginMapper.getUserRepairMargin(flowId);
        return userRepairMargin;
    }

    @Override
    public UserRepairMargin getUserRepairMarginByRepairCode(String repairCode) {
        LambdaQueryWrapper<UserRepairMargin> lambda= Wrappers.lambdaQuery();
        lambda.eq(UserRepairMargin::getOrderId,repairCode)
                .orderByDesc(UserRepairMargin::getId)
                .last("limit 0,1");
        return this.getOne(lambda);
    }

    @Override
    public List<UserRepairMargin> getUserRepairMargin(Date getDate, Integer[] states) {
        LambdaQueryWrapper<UserRepairMargin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.le(UserRepairMargin::getGetDate,getDate);
        lambdaQueryWrapper.in(UserRepairMargin::getState,states);
       // lambdaQueryWrapper.eq(UserRepairMargin::getExpireType,1);
        List<UserRepairMargin> list = super.list(lambdaQueryWrapper);
        return list;
    }

    @Override
    public AccountPayRepairOutDto payForRepairFee(UserRepairInfo userRepairInfo, String payWay, String selectType,String accessToken) {
        if (userRepairInfo == null) {
            throw new BusinessException("维修记录不存在");
        }
        if (StringUtils.isEmpty(payWay)) {
            throw new BusinessException("请输入支付方式");
        }
        if (StringUtils.isEmpty(selectType)) {
            throw new BusinessException("请输入是否勾选维修基金");
        }
        Long userId = userRepairInfo.getUserId();
        Long serviceUserId = userRepairInfo.getServiceUserId();
        Long productId = userRepairInfo.getProductId();
        Long amountFee = userRepairInfo.getTotalFee();
        LocalDateTime getDate = userRepairInfo.getGetDate();
        String serviceCharge = userRepairInfo.getServiceCharge();
        if (StringUtils.isEmpty(serviceCharge)) {
            serviceCharge = "0";
        }

        // TODO 加锁
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payForRepairFee" + userId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
//
        if (userId == null || userId <= 0L) {
            throw new BusinessException("请输入消费用户id");
        }
        if (serviceUserId == null || serviceUserId <= 0L) {
            throw new BusinessException("请输入服务商用户id");
        }
        if (productId == null || productId <= 0L) {
            throw new BusinessException("请输入产品id");
        }
        if (amountFee == null || amountFee <= 0L) {
            throw new BusinessException("请输入消费金额");
        }
        if (getDate == null) {
            throw new BusinessException("未到期转到期时间不能为空");
        }
        // 通过用户id获取用户信息
        UserDataInfo user = iUserDataInfoService.getUserDataInfo(userId);
        if (user == null) {
            throw new BusinessException("没找到用户信息");
        }
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到操作用户信息!");
        }
        //根据服务商id查询租户与服务商关系
        UserDataInfo serviceUser = iUserDataInfoService.getUserDataInfo(serviceUserId);
        if (serviceUser == null) {
            throw new BusinessException("没找到服务商用户信息");
        }
        SysUser sysOtherOperator = sysUserService.getSysOperatorByUserIdOrPhone(serviceUserId, null, 0L);
        if (sysOtherOperator == null) {
            throw new BusinessException("没有找到服务商操作用户信息!");
        }
        ServiceInfo serviceInfo = iServiceInfoService1.getServiceInfoById(serviceUserId);
        if (serviceInfo == null) {
            throw new BusinessException("未找到服务商信息");
        }
        //根据产品id查询产品信息
        ServiceProduct serviceProduct = serviceProductService.getServiceProduct(productId);
        if (serviceProduct == null) {
            throw new BusinessException("根据产品id：" + productId + ",未找到产品信息");
        } else {
            if (serviceProduct.getState() == null || serviceProduct.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2) {
                throw new BusinessException("产品id为：" + productId + "的产品状态无效");
            }
        }
        //服务商能否开票
        boolean isBill = false;
        //是否开票（1：开，2：不开）
        Integer isNeedBill = userRepairInfo.getIsBill();
        if (isNeedBill != null && isNeedBill == 1) {
            isBill = true;
            isNeedBill = OrderAccountConst.ORDER_IS_NEED_BILL.YES;
        } else {
            isBill = false;
            isNeedBill = OrderAccountConst.ORDER_IS_NEED_BILL.NO;
        }
		/*if (serviceInfo.getIsBillAbility() != null && serviceInfo.getIsBillAbility() == BILL_ABILITY.ENABLE) {
			isBill = true;
			isNeedBill = ORDER_IS_NEED_BILL.YES;
		} else {
			isBill = false;
			isNeedBill = ORDER_IS_NEED_BILL.NO;
		}*/
        //根据产品id查询产品与租户信息
        List<TenantProductRel> tenantProductRelList = iTenantProductRelService.getTenantProductRelList(productId);
        if (tenantProductRelList == null) {
            throw new BusinessException("根据产品id：" + productId + ",未找到产品与租户信息");
        }
        //目前没有维修基金，但保留口子方便以后加进来
        List<OrderAccount> accountList = iOrderAccountService.getOrderAccountQuey(userId, OrderAccountConst.ORDER_BY.REPAIR_FUND,
                -1L, SysStaticDataEnum.USER_TYPE.DRIVER_USER);

        //查询产品是否共享
        List<Long> sourceTenantIdList = new ArrayList<Long>();
        Map<String, TenantProductRel> tenantProductRelMap = new HashMap<String, TenantProductRel>();
        boolean isSharePorduct = false;
        //查询产品是否是车队的合作产品
        boolean isBelongToTenant = false;
        for (TenantProductRel tpr : tenantProductRelList) {
            if (tpr.getState() == null || tpr.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2 || tpr.getAuthState() == null || tpr.getAuthState() != 2) {
                continue;
            }
            //如果tenantId 为1，则属于共享产品
            if (tpr.getTenantId() == SysStaticDataEnum.PT_TENANT_ID) {
                isSharePorduct = true;
            }
            tenantProductRelMap.put(String.valueOf(tpr.getTenantId()), tpr);
            //如果tenantId 为1，则属于共享产品
            if (tpr.getTenantId() != SysStaticDataEnum.PT_TENANT_ID  && user.getTenantId() != null && String.valueOf(user.getTenantId()).equals(String.valueOf(tpr.getTenantId()))) {
                isBelongToTenant = true;
                sourceTenantIdList.add(user.getTenantId());
            }
        }
        //对资金账户特殊处理
        List<OrderAccount> accountList1 = new ArrayList<OrderAccount>();
        List<OrderAccount> accountList2 = new ArrayList<OrderAccount>();
        for (OrderAccount ac : accountList) {
            if (sourceTenantIdList.contains(ac.getSourceTenantId())) {
                accountList1.add(ac);
            } else {
                accountList2.add(ac);
            }
        }
        accountList1.addAll(accountList2);
        long totalRepair = 0;
        long totalBalance = 0;
        // TODO 待实现
        RepairAmountOutDto repairAmountOut  = this.queryAdvanceFeeByRepair(userId,serviceUserId,productId,amountFee,user.getTenantId());
        if (repairAmountOut != null) {
            totalRepair = (repairAmountOut.getRepairFund() == null ? 0L : repairAmountOut.getRepairFund());
            totalBalance = (repairAmountOut.getCanUseAmount() == null ? 0L : repairAmountOut.getCanUseAmount());
        }
        //判断是否需要用到可用
        long canUseRepair = 0;
        long canUseBalance = 0;
        long tempAmount = amountFee;
        long tempUserAccountRepair = 0;
        //勾选维修基金 且有维修基金
        long soNbr = CommonUtil.createSoNbr();
        //维修保养平台服务费
        if (EnumConsts.REPAIR_TYPE.SELECT_TYPE0.equals(selectType) && totalRepair > 0) {
            if (tempAmount >= totalRepair) {
                canUseRepair = totalRepair;
                tempAmount -= totalRepair;
            } else {
                canUseRepair = tempAmount;
                tempAmount = 0;
            }
            tempUserAccountRepair = canUseRepair;
            List<UserRepairMargin> repairFlowList = new ArrayList<UserRepairMargin>();
            Map<String, Object> inParm = new HashMap<String, Object>();
            // TODO 待实现
            this.operationOrderAccountRepair(userRepairInfo, serviceProduct, sysOperator, sysOtherOperator,
                    accountList1, repairFlowList, sourceTenantIdList, inParm, serviceCharge,
                    canUseRepair, isSharePorduct, isBill, isBelongToTenant, isNeedBill,soNbr,accessToken);
        } else {
            totalRepair = amountFee;
        }
        if (tempAmount > 0) {
            canUseBalance = tempAmount;
        }
        AccountPayRepairOutDto out = new AccountPayRepairOutDto();
        //支付方式 -- 账户
        if (EnumConsts.REPAIR_TYPE.SELECT_PAYWAY2.equals(payWay)) {
            if (totalBalance < canUseBalance) {
                throw new BusinessException("账户自付余额不足于支付此维修费用");
            }
            if (canUseBalance > 0) {
                //司机用可用支付维修保养流水
                List<BusiSubjectsRel> busiBalanceList = new ArrayList<BusiSubjectsRel>();
                BusiSubjectsRel consumeBalanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.BALANCE_PAY_FOR_REPAIR_SUB, canUseBalance);
                busiBalanceList.add(consumeBalanceSub);
                List<BusiSubjectsRel> balanceRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_REPAIR, busiBalanceList);
                iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.CONSUME_CODE,EnumConsts.PayInter.PAY_FOR_REPAIR,
                        userId, serviceUserId, sysOtherOperator.getOpName(), balanceRelList, soNbr, 0L, -1L,
                        com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.DRIVER_USER);

                //维修商收入可用余额消费油
                List<BusiSubjectsRel> serviceBusiBalanceList = new ArrayList<BusiSubjectsRel>();
                BusiSubjectsRel serviceBalanceSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.INCOME_BALANCE_PAY_FOR_REPAIR_SUB, canUseBalance);
                serviceBusiBalanceList.add(serviceBalanceSub);
                List<BusiSubjectsRel> serviceList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.INCOME_REPAIR, serviceBusiBalanceList);
                iAccountDetailsService.createAccountDetails(EnumConsts.BusiType.INCOMING_CODE,EnumConsts.PayInter.INCOME_REPAIR,serviceUserId, userId,
                        sysOperator.getOpName(), serviceList, soNbr, 0L, -1L,
                        com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                //记录维修商流水收入
                UserRepairMargin userRepairMargin = this.createUserRepairMargin(serviceUserId,
                        sysOtherOperator.getBillId(), sysOtherOperator.getOpName(),
                        OrderAccountConst.CONSUME_COST_TYPE.REPAIR_TYPE1, "0",
                        canUseBalance, sysOperator.getUserInfoId(), sysOperator.getBillId(),
                        sysOperator.getOpName(), null, -1L, productId, isNeedBill,accessToken);
                userRepairMargin.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS1);//已到期
                //如果在共享维修站维修则立即到期
                if (isSharePorduct) {
                    //计算平台服务费
                    userRepairMargin.setPlatformAmount(new Double(userRepairMargin.getAmount() * Double.parseDouble(serviceCharge)/100).longValue());
                }
                Date date = DateUtil.addMinis(new Date(), 0);
                userRepairMargin.setGetDate(DateUtil.localDateTime(date));
                userRepairMargin.setRepairId(userRepairInfo.getId());
                userRepairMargin.setOrderId(userRepairInfo.getRepairCode());
                userRepairMargin.setGetResult("已到期");
                userRepairMargin.setUndueAmount(0L);
                userRepairMargin.setExpiredAmount(canUseBalance);
                userRepairMargin.setServiceCharge(0L);
                userRepairMargin.setProductName(serviceProduct.getProductName());
                userRepairMargin.setAddress(serviceProduct.getAddress());
                userRepairMargin.setServiceCall(serviceProduct.getServiceCall());
                userRepairMargin.setRepairBalance(0L);
                userRepairMargin.setBalance(canUseBalance);
                userRepairMargin.setMarginBalance(0L);
                userRepairMargin.setOilAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
                userRepairMargin.setSubjectsId(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR);
                this.save(userRepairMargin);
                if (isSharePorduct && userRepairMargin.getPlatformAmount() != null) {
                    //平台服务费金额
                    // TODO 待释放 不清楚是否需要
//                    PlatformServiceCharge pfsc = platformServiceChargeSV.createPlatformServiceCharge(userRepairMargin.getUserId(), userRepairMargin.getUserName(), userRepairMargin.getUserBill(), userRepairMargin.getAmount(), userRepairMargin.getPlatformAmount(),
//                            userRepairMargin.getPlatformAmount(), OrderAccountConst.IS_VERIFICATION.NO_VERIFICATION, OrderAccountConst.PLATFORM_SERVICE_CHARGE_TYPE.OIL_TYPE);
//                    pfsc.setConsumeFlowId(userRepairMargin.getId());
//                    platformServiceChargeSV.saveOrUpdate(pfsc);
                }
                //调用平安接口，司机平安账户往油老板虚拟账户划拨转账
                PayoutIntf payoutIntf = iPayoutIntfService.createPayoutIntf(serviceUserId, OrderAccountConst.PAY_TYPE.SERVICE, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE,
                        canUseBalance, -1L, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0, userRepairMargin.getId(),
                        -1L, OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1, OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1,
                        userId, OrderAccountConst.PAY_TYPE.USER, EnumConsts.PayInter.PAY_FOR_REPAIR,
                        EnumConsts.SubjectIds.BALANCE_PAY_REPAIR, OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0,
                        SysStaticDataEnum.USER_TYPE.DRIVER_USER,SysStaticDataEnum.USER_TYPE.SERVICE_USER,0L,accessToken);
                payoutIntf.setObjId(Long.valueOf(sysOtherOperator.getBillId()));
                payoutIntf.setPayTime(LocalDateTime.now());
                iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntf,accessToken);

                //对私收款
                //如果使用的是可提现金额，如果服务商没有绑定对私帐户，则支付的现金转移到对公帐户(绑定实体卡为准)
                int inbankType = EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT;//对公收款
                //不要票，且服务商有绑定对私账户，付款到服务商对私账户
                UserDataInfo userDataInfo = new UserDataInfo();
                userDataInfo.setUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                if ((isNeedBill == OrderAccountConst.ORDER_IS_NEED_BILL.NO) && iAccountBankRelService.isUserBindCard(serviceUserId,userDataInfo,EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0)) {
                    inbankType = EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT;//收款对私
                } else if (!iAccountBankRelService.isUserBindCard(serviceUserId,userDataInfo,EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1)) {
                    throw new BusinessException("维修商商没有绑定对公银行卡，不能支付!");
                } else {
                    AccountBankRel bank = iAccountBankRelService.getDefaultAccountBankRel(serviceUserId,
                            EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_1, SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                    if (bank != null) {
                        payoutIntf.setAccNo(bank.getPinganCollectAcctId());
                    }
                }
                PayReturnDto payReturnOut = iBaseBusiToOrderService.payMemberTransaction(userId, serviceUserId,
                        canUseBalance, EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT, inbankType,
                        payoutIntf.getId()+"",true,"","",
                        SysStaticDataEnum.USER_TYPE.DRIVER_USER,SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                if (payReturnOut == null) {
                    throw new BusinessException("用可用余额加油失败");
                }
                //网络超时
                if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts1) {
                    payoutIntf.setRespCode(HttpsMain.netTimeOutFail);
                    payoutIntf.setRespMsg("网络超时");
                    payoutIntf.setRespBankCode(PinganIntefaceConst.RESP_BANK_CODE.BANK_OTHER_ERROR);
                }
                //成功
                if (payReturnOut.getReqCode() == EnumConsts.PAY_RESULT_STS.payResultSts0) {
                    payoutIntf.setRespCode(HttpsMain.respCodeSuc);
                    payoutIntf.setRespMsg("成功");
                    payoutIntf.setPayTime(LocalDateTime.now());
                    payoutIntf.setCompleteTime(DateUtil.formatDate(new Date(),DATETIME12_FORMAT2));
                }
                String thirdLogNo = payReturnOut.getThirdLogNo();
                payoutIntf.setRemark("可用余额支付维修保养成功，交易流水号:" + thirdLogNo);
                payoutIntf.setSerialNumber(thirdLogNo);
                payoutIntf.setBusiCode(userRepairInfo.getRepairCode());
                payoutIntf.setPlateNumber(userRepairInfo.getPlateNumber());
                iPayoutIntfService.saveOrUpdate(payoutIntf);
            }

        } else if (EnumConsts.REPAIR_TYPE.SELECT_PAYWAY4.equals(payWay)){ //现金
            //自付现金服务费
            if (userRepairInfo.getServiceCharge() != null && Double.parseDouble(userRepairInfo.getServiceCharge()) > 0) {
                //平台服务费金额
                Long platformServiceCharge = new Double((amountFee - tempUserAccountRepair) * Double.parseDouble(serviceCharge)/100).longValue();
                // TODO 待释放 暂时不清楚是否需要
//                PlatformServiceCharge pfsc = platformServiceChargeSV.createPlatformServiceCharge(serviceUserId, sysOtherOperator.getOpName(),
//                        sysOtherOperator.getBillId(), amountFee, platformServiceCharge,
//                        platformServiceCharge, OrderAccountConst.IS_VERIFICATION.NO_VERIFICATION,
//                        OrderAccountConst.PLATFORM_SERVICE_CHARGE_TYPE.REPAIR_TYPE);
//                platformServiceChargeSV.saveOrUpdate(pfsc);
            }
        }
        out.setRepairFund(tempUserAccountRepair);
        out.setCash(amountFee - tempUserAccountRepair);
        out.setTotalAmount(amountFee);
        return out;
    }

    /**
     * 可以消费的金额，包含维修基金+可提现（目前没有维修基金）
     * 可以消费的金额：可提现（在HA的金额除外）
     */
    @Override
    public RepairAmountOutDto queryAdvanceFeeByRepair(Long userId, Long serviceUserId, Long productId, Long amountFee, Long tenantId) {
        if (userId < 1) {
            throw new BusinessException("请输入用户编号");
        }
        if (amountFee <= 0) {
            throw new BusinessException("维修金额必须为正数!");
        }
        if (serviceUserId <= 0) {
            throw new BusinessException("请输入服务商用户编号!");
        }
        if (productId <= 0) {
            throw new BusinessException("请输入维修站id!");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[] { DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2) });
        RepairAmountOutDto out = new RepairAmountOutDto();
        // 通过用户id获取用户信息
        UserDataInfo user = iUserDataInfoService.getUserDataInfo(userId);
        if (user == null) {
            throw new BusinessException("没找到用户信息");
        }
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到操作用户信息!");
        }
        //根据产品id查询产品信息
        ServiceProduct serviceProduct = serviceProductService.getServiceProduct(productId);
        if (serviceProduct == null) {
            throw new BusinessException("根据产品id：" + productId + ",未找到产品信息");
        } else {
            if (serviceProduct.getState() == null || serviceProduct.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2) {
                throw new BusinessException("产品id为：" + productId + "的产品状态无效");
            }
        }
        //根据服务商id查询租户与服务商关系
        UserDataInfo serviceUser = iUserDataInfoService.getUserDataInfo(serviceUserId);
        if (serviceUser == null) {
            throw new BusinessException("没找到服务商用户信息");
        }
        SysUser sysOtherOperator = sysUserService.getSysOperatorByUserIdOrPhone(serviceUserId, null, 0L);
        if (sysOtherOperator == null) {
            throw new BusinessException("没有找到服务商操作用户信息!");
        }
        ServiceInfo  serviceInfo = iServiceInfoService1.getServiceInfoById(serviceUserId);
        if (serviceInfo == null) {
            throw new BusinessException("未找到服务商信息");
        }
        //服务商能否开票
        boolean isBill = false;
        if (serviceInfo.getIsBillAbility() != null && serviceInfo.getIsBillAbility() == SysStaticDataEnum.BILL_ABILITY.ENABLE) {
            isBill = true;
        } else {
            isBill = false;
        }
        //根据产品id查询产品与租户信息
        List<TenantProductRel> tenantProductRelList = iTenantProductRelService.getTenantProductRelList(productId);
        if (tenantProductRelList == null) {
            throw new BusinessException("根据产品id：" + productId + ",未找到产品与租户信息");
        }
        BankBalanceInfo pingAnInfo = iBaseBusiToOrderService.getBankBalanceToUserIdNo(userId,
                EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT,"",SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        //目前没有维修基金，但保留口子方便以后加进来
        List<OrderAccount> accountList = iOrderAccountService.getOrderAccountQuey(userId, OrderAccountConst.ORDER_BY.BALANCE,
                -1L,SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        //查询产品是否共享
        List<Long> sourceTenantIdList = new ArrayList<Long>();
        Map<String, TenantProductRel> tenantProductRelMap = new HashMap<String, TenantProductRel>();
        boolean isSharePorduct = false;
        //查询产品是否是车队的合作油站
        boolean isBelongToTenant = false;
        for (TenantProductRel tpr : tenantProductRelList) {
            if (tpr.getState() == null || tpr.getState() == OrderAccountConst.SERVICE_PRODUCT.STATE2 || tpr.getAuthState() == null || tpr.getAuthState() != 2) {
                continue;
            }
            //如果tenantId 为1，则属于共享产品
            if (tpr.getTenantId() == SysStaticDataEnum.PT_TENANT_ID) {
                isSharePorduct = true;
            }
            tenantProductRelMap.put(String.valueOf(tpr.getTenantId()), tpr);
            //如果tenantId 为1，则属于共享产品
            if (tpr.getTenantId() != SysStaticDataEnum.PT_TENANT_ID  && user.getTenantId() != null && String.valueOf(user.getTenantId()).equals(String.valueOf(tpr.getTenantId()))) {
                isBelongToTenant = true;
                sourceTenantIdList.add(user.getTenantId());
            }
        }
        //对资金账户特殊处理
        List<OrderAccount> accountList1 = new ArrayList<OrderAccount>();
        List<OrderAccount> accountList2 = new ArrayList<OrderAccount>();
        for (OrderAccount ac : accountList) {
            if (sourceTenantIdList.contains(ac.getSourceTenantId())) {
                accountList1.add(ac);
            } else {
                accountList2.add(ac);
            }
        }
        accountList1.addAll(accountList2);
        //查找平安银行可提现金额
        long totalBalance = 0;

        if (pingAnInfo != null) {
            //司机在平安可用提现余额
            Double pinganTotalTranOutAmount = pingAnInfo.getBalance();
            if (pinganTotalTranOutAmount != null && pinganTotalTranOutAmount > 0) {
                totalBalance = new Double(pinganTotalTranOutAmount * 100).longValue() ;
            }
        }
        long totalRepair = 0;
        //是否共享产品
        if (isSharePorduct || isBelongToTenant) {
            //服务商是否能开票
            if (isBill) {
                //司机最多能加油钱数
                for (OrderAccount ac : accountList) {
                    if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE0 || ac.getSourceTenantId() <= 0 || ac.getRepairFund() <= 0) {
                        continue;
                    }
                    if (ac.getRepairFund() > 0L) {
                        totalRepair += ac.getRepairFund();
                    }
                }
            } else {
                //司机最多能消费钱数
                for (OrderAccount ac : accountList) {
                    if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE0 || ac.getSourceTenantId() <= 0 || ac.getRepairFund() <= 0) {
                        continue;
                    }
                    //不能开票的钱
                    if (ac.getRepairFund() > 0L) {
                        if (OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ac.getVehicleAffiliation())) {
                            totalRepair += ac.getRepairFund();
                        }
                    }
                }
            }
        }
        out.setRepairFund(totalRepair);
        out.setCanUseAmount(totalBalance);
        out.setConsumeAmount(amountFee);
        out.setTotalConsumeAmount(amountFee);
        return out;
    }

    @Override
    public Map<String, Object> operationOrderAccountRepair(UserRepairInfo userRepairInfo,
                                                           ServiceProduct serviceProduct,
                                                           SysUser sysOperator,
                                                           SysUser sysOtherOperator,
                                                           List<OrderAccount> accountList,
                                                           List<UserRepairMargin> repairFlowList,
                                                           List<Long> sourceTenantIdList,
                                                           Map<String, Object> inParm, String serviceCharge,
                                                           long canUseRepair, boolean isSharePorduct,
                                                           boolean isBill, boolean isBelongToTenant,
                                                           int isNeedBill, long soNbr,String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        Long serviceUserId = serviceProduct.getServiceUserId();
        Long productId = serviceProduct.getId();
        long tempPlatformServiceCharge = 0;
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE0 || ac.getSourceTenantId() <= 0) {
                continue;
            }
            if (canUseRepair == 0 || ac.getRepairFund() <= 0) {
                continue;
            }
            if (OrderAccountConst.ORDER_IS_NEED_BILL.YES == isNeedBill) {
                //不需要开票的资金
                if (OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ac.getVehicleAffiliation())) {
                    continue;
                }
            } else {
                //需要开票的资金
                if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ac.getVehicleAffiliation())) {
                    continue;
                }
            }

            List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
            String vehicleAffiliation = ac.getVehicleAffiliation();
            String oilAffiliation = ac.getOilAffiliation();
            long sourceTenantId = ac.getSourceTenantId();
            //维修基金
            long tempRepariFund = 0;
            long tempFund = 0;

            if (isNeedBill == OrderAccountConst.ORDER_IS_NEED_BILL.YES) {
                if (!ac.getVehicleAffiliation().equals(String.valueOf(OrderAccountConst.ORDER_BILL_TYPE.notNeedBill))) {
                    tempRepariFund += ac.getRepairFund();
                }
            } else {
                if (ac.getVehicleAffiliation().equals(String.valueOf(OrderAccountConst.ORDER_BILL_TYPE.notNeedBill))) {
                    tempRepariFund += ac.getRepairFund();
                }
            }
            //是否共享产品
            if (isSharePorduct) {
                //能否开票
                if (isBill) {
                    if (tempRepariFund >= canUseRepair) {
                        // 消费维修基金
                        BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR, canUseRepair);
                        amountFeeSubjectsRel.setOtherId(sysOtherOperator.getUserInfoId());
                        amountFeeSubjectsRel.setOtherName(sysOtherOperator.getOpName());
                        busiList.add(amountFeeSubjectsRel);
                        if (sourceTenantIdList.contains(ac.getSourceTenantId())) {

                        } else if (isSharePorduct) {
                            //平台才有利润
                            amountFeeSubjectsRel.setIncomeTenantId(SysStaticDataEnum.PT_TENANT_ID);
                        }
                        tempFund = canUseRepair;
                        canUseRepair = 0;
                    } else {
                        if (tempRepariFund > 0) {
                            // 消费维修基金
                            BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR, tempRepariFund);
                            amountFeeSubjectsRel.setOtherId(sysOtherOperator.getUserInfoId());
                            amountFeeSubjectsRel.setOtherName(sysOtherOperator.getOpName());
                            busiList.add(amountFeeSubjectsRel);
                            if (sourceTenantIdList.contains(ac.getSourceTenantId())) {
                                //租户利润
                            } else if (isSharePorduct) {
                                //平台利润
                                amountFeeSubjectsRel.setIncomeTenantId(SysStaticDataEnum.PT_TENANT_ID);
                            }
                            canUseRepair -= tempRepariFund;
                            tempFund = tempRepariFund;

                        }
                    }
                } else {
                    //服务商不能开票，司机不要票
                    if (OrderAccountConst.ORDER_IS_NEED_BILL.NO == isNeedBill) {
                        if (tempRepariFund  >= canUseRepair) {
                            // 消费维修基金
                            BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR, canUseRepair);
                            amountFeeSubjectsRel.setOtherId(sysOtherOperator.getUserInfoId());
                            amountFeeSubjectsRel.setOtherName(sysOtherOperator.getOpName());
                            busiList.add(amountFeeSubjectsRel);
                            if (sourceTenantIdList.contains(ac.getSourceTenantId())) {
                                //租户利润
                            } else if (isSharePorduct) {
                                //平台利润
                                amountFeeSubjectsRel.setIncomeTenantId(SysStaticDataEnum.PT_TENANT_ID);
                            }
                            tempFund = canUseRepair;
                            canUseRepair = 0;
                        } else {
                            if (tempRepariFund > 0) {
                                // 消费维修基金
                                BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR, tempRepariFund);
                                amountFeeSubjectsRel.setOtherId(sysOtherOperator.getUserInfoId());
                                amountFeeSubjectsRel.setOtherName(sysOtherOperator.getOpName());
                                busiList.add(amountFeeSubjectsRel);
                                if (sourceTenantIdList.contains(ac.getSourceTenantId())) {
                                    //租户利润
                                } else if (isSharePorduct) {
                                    //平台利润
                                    amountFeeSubjectsRel.setIncomeTenantId(SysStaticDataEnum.PT_TENANT_ID);
                                }
                                canUseRepair -= tempRepariFund;
                                tempFund = tempRepariFund;
                            }
                        }
                    }
                }
            } else {//非共享
                if (!isBelongToTenant) {
                    continue;
                }
                //能否开票
                if (isBill) {
                    if (tempRepariFund >= canUseRepair) {
                        // 消费油卡账户
                        BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR, canUseRepair);
                        amountFeeSubjectsRel.setOtherId(sysOtherOperator.getUserInfoId());
                        amountFeeSubjectsRel.setOtherName(sysOtherOperator.getOpName());
                        busiList.add(amountFeeSubjectsRel);
                        if (sourceTenantIdList.contains(ac.getSourceTenantId())) {

                        } else if (isSharePorduct) {
                            //平台才有利润
                            amountFeeSubjectsRel.setIncomeTenantId(SysStaticDataEnum.PT_TENANT_ID);
                        }
                        tempFund = canUseRepair;
                        canUseRepair = 0;
                    } else {
                        if (tempRepariFund > 0) {
                            // 消费油卡账户
                            BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR, tempRepariFund);
                            amountFeeSubjectsRel.setOtherId(sysOtherOperator.getUserInfoId());
                            amountFeeSubjectsRel.setOtherName(sysOtherOperator.getOpName());
                            busiList.add(amountFeeSubjectsRel);
                            if (sourceTenantIdList.contains(ac.getSourceTenantId())) {
                                //租户利润
                            } else if (isSharePorduct) {
                                //平台利润
                                amountFeeSubjectsRel.setIncomeTenantId(SysStaticDataEnum.PT_TENANT_ID);
                            }
                            canUseRepair -= tempRepariFund;
                            tempFund = tempRepariFund;
                        }
                    }
                } else {
                    if (OrderAccountConst.ORDER_IS_NEED_BILL.NO == isNeedBill) {
                        if (tempRepariFund  >= canUseRepair) {
                            // 消费油卡账户
                            BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR, canUseRepair);
                            amountFeeSubjectsRel.setOtherId(sysOtherOperator.getUserInfoId());
                            amountFeeSubjectsRel.setOtherName(sysOtherOperator.getOpName());
                            busiList.add(amountFeeSubjectsRel);
                            if (sourceTenantIdList.contains(ac.getSourceTenantId())) {
                                //租户利润
                            } else if (isSharePorduct) {
                                //平台利润
                                amountFeeSubjectsRel.setIncomeTenantId(SysStaticDataEnum.PT_TENANT_ID);
                            }
                            tempFund = canUseRepair;
                            canUseRepair = 0;
                        } else {
                            if (tempRepariFund > 0) {
                                // 消费油卡账户
                                BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR, tempRepariFund);
                                amountFeeSubjectsRel.setOtherId(sysOtherOperator.getUserInfoId());
                                amountFeeSubjectsRel.setOtherName(sysOtherOperator.getOpName());
                                busiList.add(amountFeeSubjectsRel);
                                if (sourceTenantIdList.contains(ac.getSourceTenantId())) {
                                    //租户利润
                                } else if (isSharePorduct) {
                                    //平台利润
                                    amountFeeSubjectsRel.setIncomeTenantId(SysStaticDataEnum.PT_TENANT_ID);
                                }
                                canUseRepair -= tempRepariFund;
                                tempFund = tempRepariFund;
                            }
                        }
                    }
                }
            }
            if (busiList == null || busiList.size() <= 0) {
                continue;
            }
            // 计算费用集合
            List<BusiSubjectsRel> busiSubjectsList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAY_FOR_REPAIR, busiList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE,EnumConsts.PayInter.PAY_FOR_REPAIR, sysOtherOperator.getUserInfoId(),
                    sysOtherOperator.getOpName(), ac, busiSubjectsList, soNbr, 0L,sysOperator.getOpName(),
                    null, ac.getSourceTenantId(), String.valueOf(productId), serviceProduct.getProductName(), null, vehicleAffiliation,loginInfo);

            //维修服务商收入
            OrderAccount accountReapari = iOrderAccountService.queryOrderAccount(serviceUserId, vehicleAffiliation,0L,
                    sourceTenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.SERVICE_USER);
            List<BusiSubjectsRel> busiRepairList = new ArrayList<BusiSubjectsRel>();
            List<UserRepairMargin> tempList = new ArrayList<UserRepairMargin>();
            for (BusiSubjectsRel bs : busiSubjectsList) {
                if (bs.getSubjectsId() == EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR) {
                    BusiSubjectsRel consumeOilSub = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.REPAIR_FEE_MARGIN, bs.getAmountFee());
                    busiRepairList.add(consumeOilSub);
                    UserRepairMargin userRepairMargin = this.createUserRepairMargin(serviceUserId,
                            sysOtherOperator.getBillId(), sysOtherOperator.getOpName(),
                            OrderAccountConst.CONSUME_COST_TYPE.REPAIR_TYPE1, "0",
                            bs.getAmountFee(), sysOperator.getUserInfoId(), sysOperator.getBillId(),
                            sysOperator.getOpName(), vehicleAffiliation, sourceTenantId, productId, isNeedBill,accessToken);
                    userRepairMargin.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS0);//未到期
                    //如果在共享维修站维修则立即到期
                    if (bs.getIncomeTenantId() != null && String.valueOf(SysStaticDataEnum.PT_TENANT_ID).equals(String.valueOf(bs.getIncomeTenantId()))) {
                        userRepairMargin.setGetDate(DateUtil.localDateTime(DateUtil.addMinis(new Date(), 30)));
                        //计算平台服务费
                        userRepairMargin.setPlatformAmount(new Double(userRepairMargin.getAmount() * Double.parseDouble(serviceCharge)/100).longValue());
                    } else {
                        TenantServiceRel tenantServiceRel = iTenantServiceRelService.getTenantServiceRel(ac.getSourceTenantId(), serviceUserId);
                        int paymentDay = 0;
                        if (tenantServiceRel != null) {
                            paymentDay = (tenantServiceRel.getPaymentDays() == null ? 0 : tenantServiceRel.getPaymentDays());
                        }
                        userRepairMargin.setGetDate(DateUtil.localDateTime(DateUtil.addDate(new Date(), paymentDay)));
                    }
                    userRepairMargin.setRepairId(userRepairInfo.getId());
                    userRepairMargin.setOrderId(userRepairInfo.getRepairCode());
                    userRepairMargin.setGetResult("未到期");
                    userRepairMargin.setUndueAmount(bs.getAmountFee());
                    userRepairMargin.setExpiredAmount(0L);
                    userRepairMargin.setServiceCharge(0L);
                    userRepairMargin.setProductName(serviceProduct.getProductName());
                    userRepairMargin.setAddress(serviceProduct.getAddress());
                    userRepairMargin.setServiceCall(serviceProduct.getServiceCall());
                    userRepairMargin.setRepairBalance(tempFund);
                    userRepairMargin.setSubjectsId(EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR);
                    userRepairMargin.setOilAffiliation(oilAffiliation);
                    this.save(userRepairMargin);
                    repairFlowList.add(userRepairMargin);
                    tempList.add(userRepairMargin);
                }
            }
            List<BusiSubjectsRel> busiResultSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.INCOME_REPAIR, busiRepairList);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE,EnumConsts.PayInter.INCOME_REPAIR, sysOperator.getUserInfoId(),
                    sysOperator.getOpName(), accountReapari, busiResultSubjectsRelList, soNbr, 0L,
                    "",null, sourceTenantId, null, "", null, vehicleAffiliation,loginInfo);
            // 写入订单限制表和订单资金流向表

            for (UserRepairMargin cof : tempList) {
                BigDecimal b1 = new BigDecimal(Long.toString(tempPlatformServiceCharge));
                if (cof.getPlatformAmount() != null && cof.getPlatformAmount() > 0L) {
                    BigDecimal b2 = new BigDecimal(Long.toString(cof.getPlatformAmount()));
                    tempPlatformServiceCharge = b1.add(b2).longValue();
                    //平台服务费金额
                     // 待释放 暂时不清楚 是否需要
//                    PlatformServiceCharge pfsc = platformServiceChargeSV.createPlatformServiceCharge(cof.getUserId(), cof.getUserName(), cof.getUserBill(), cof.getAmount(), cof.getPlatformAmount(),
//                            cof.getPlatformAmount(), OrderAccountConst.IS_VERIFICATION.NO_VERIFICATION, OrderAccountConst.PLATFORM_SERVICE_CHARGE_TYPE.REPAIR_TYPE);
//                    pfsc.setConsumeFlowId(cof.getFlowId());
//                    platformServiceChargeSV.saveOrUpdate(pfsc);
                }
            }
        }
        return inParm;
    }

    @Override
    public UserRepairMargin createUserRepairMargin(Long userId, String userBill,
                                                   String userName, Integer costType,
                                                   String orderId, Long amount,
                                                   Long otherUserId, String otherUserBill,
                                                   String otherName, String vehicleAffiliation,
                                                   Long tenantId, Long productId, Integer isNeedBill,
                                                   String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        UserRepairMargin cof = new UserRepairMargin();
        cof.setUserId(userId);
        cof.setUserBill(userBill);
        cof.setUserName(userName);
        cof.setCostType(costType);
        cof.setOrderId(orderId);
        cof.setAmount(amount);
        cof.setOtherUserId(otherUserId);
        cof.setOtherUserBill(otherUserBill);
        cof.setOtherName(otherName);
        cof.setVehicleAffiliation(vehicleAffiliation);
        cof.setProductId(productId);
        cof.setTenantId(tenantId);
        cof.setIsNeedBill(isNeedBill);
        cof.setCreateTime(LocalDateTime.now());
        if ( loginInfo!= null) {
            cof.setOpId(loginInfo.getId());
        } else {
            cof.setOpId(-1L);
        }
        return cof;
    }

    @Override
    public UserRepairMarginDto queryRepairMarginDetail(Long repairId) {
        LambdaQueryWrapper<UserRepairMargin> wrapper  = new LambdaQueryWrapper<>();
        wrapper.eq(UserRepairMargin::getId,repairId);
        List<UserRepairMargin> list = baseMapper.selectList(wrapper);
        Long advanceFee = 0L;//预支手续费 ADVANCE_FEE
        Long marginBalance = 0L;//即将到期余额 MARGIN_BALANCE
        Long balance = 0L;//可提现余额 BALANCE
        Long repairBalance = 0L;//维修基金余额 --使用维修基金金额(分)REPAIR_BALANCE
        //即将到期余额=使用未到期金额(分)-预支手续费(分)
        if(list!=null&&list.size()>0){
            for(UserRepairMargin userRepairMargin:list){
                advanceFee += userRepairMargin.getAdvanceFee()==null?0L:userRepairMargin.getAdvanceFee();
                marginBalance += userRepairMargin.getMarginBalance()==null?0L:userRepairMargin.getMarginBalance();
                balance += userRepairMargin.getBalance()==null?0L:userRepairMargin.getBalance();
                repairBalance += userRepairMargin.getRepairBalance()==null?0L:userRepairMargin.getRepairBalance();
            }
        }
        UserRepairMarginDto dto = new UserRepairMarginDto();
        dto.setAdvanceFee(advanceFee);
        dto.setMarginBalance(marginBalance-advanceFee);
        dto.setBalance(balance);
        dto.setRepairBalance(repairBalance);
        dto.setSumAmount(marginBalance+balance+repairBalance);
        return dto;
    }


}
