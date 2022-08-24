package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IAccountDetailsSummaryService;
import com.youming.youche.order.api.order.IOrderAccountOilSourceService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AccountDetails;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderAccountOilSource;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.AppAccountDetailsDto;
import com.youming.youche.order.dto.AppAccountDetailsOutDto;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.dto.SubjectsOutDto;
import com.youming.youche.order.provider.mapper.order.AccountDetailsMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.LocalDateTimeUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.vo.AppAccountDetailsVo;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.vo.SysTenantVo;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class AccountDetailsServiceImpl extends BaseServiceImpl<AccountDetailsMapper, AccountDetails> implements IAccountDetailsService {
    private static final Logger log = LoggerFactory.getLogger(AccountDetailsServiceImpl.class);
    @Resource
    private IOrderAccountService orderAccountService;
    @Autowired
    private IOrderAccountOilSourceService orderAccountOilSourceService;
    @Resource
    private ReadisUtil readisUtil;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @Autowired
    private IAccountDetailsSummaryService accountDetailsSummaryService;
    @Resource
    LoginUtils loginUtils;
    @Resource
    AccountDetailsMapper accountDetailsMapper;
    @Resource
    IOrderSchedulerService orderSchedulerService;
    @DubboReference(version = "1.0.0")
    ISysStaticDataService sysStaticDataService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @Resource
    IOrderInfoService orderInfoService;
    @Resource
    IOrderInfoHService orderInfoHService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService vehicleDataInfoService;

    @Override
    public Long insetAccDet(Integer businessTypes, Long businessCode, Long otherUserId, String otherName, OrderAccount account, List<BusiSubjectsRel> subjectsList, Long soNbr,
                            Long orderId, String toUserName, LocalDateTime happenDate, Long tenantId, String etcNumber,
                            String plateNumber, OrderResponseDto param, String vehicleAffiliation, LoginInfo user) {

        if (account != null) {
            log.info("账户明细操作-" + account.getUserId());
        }
        log.info("操作前账户信息:" + account);
        log.info("操作前业务科目关系:" + subjectsList);
        long amountFeeSum = 0;
        // 可用金额
        long currentAmount = account.getBalance() == null ? 0 : account.getBalance();
        // 未到期金额
        long currentMarginBalance = account.getMarginBalance() == null ? 0 : account.getMarginBalance();
        // 预支金额
        long currentBeforePay = account.getBeforePay() == null ? 0 : account.getBeforePay();
        // 油卡账户
        long currentOilBalance = account.getOilBalance() == null ? 0 : account.getOilBalance();
        // ETC账户
        long currentEtcBalance = account.getEtcBalance() == null ? 0 : account.getEtcBalance();
        // 维修基金账户
        long repairFund = account.getRepairFund() == null ? 0 : account.getRepairFund();
        //油卡抵押金
        long pledgeOilCardFee = (account.getPledgeOilCardFee() == null ? 0L : account.getPledgeOilCardFee());
        //应收逾期
        long currentReceivableBalance = account.getReceivableOverdueBalance() == null ? 0 : account.getReceivableOverdueBalance();
        //应付逾期
        long currentPayableBalance = account.getPayableOverdueBalance() == null ? 0 : account.getPayableOverdueBalance();
        //充值油账户
        long currentRechargeOilBalance = account.getRechargeOilBalance() == null ? 0 : account.getRechargeOilBalance();
        //用户类型
        int userType = account.getUserType() == null ? 0 : account.getUserType();

        if (subjectsList != null && subjectsList.size() > 0) {
            for (int i = 0; i < subjectsList.size(); i++) {
                BusiSubjectsRel busiSubjectsRel = subjectsList.get(i);
                // 写入账户明细表
                AccountDetails accDet = new AccountDetails();
                accDet.setAccountId(account.getId());
                accDet.setUserId(account.getUserId());
                //会员体系开始
                accDet.setUserType(userType);
                //会员体系结束
                accDet.setBusinessTypes(businessTypes);
                accDet.setBusinessNumber(businessCode);
                accDet.setSubjectsId(busiSubjectsRel.getSubjectsId());
                accDet.setSubjectsName(busiSubjectsRel.getSubjectsName());
                accDet.setVehicleAffiliation(vehicleAffiliation);
                // 支出
                if (busiSubjectsRel.getSubjectsType() != null && busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_OUT) {
                    accDet.setAmount(-busiSubjectsRel.getAmountFee());
                    amountFeeSum = amountFeeSum - busiSubjectsRel.getAmountFee();
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.ACCOUNT_CODE) {
                        // 可用金额科目
                        currentAmount = currentAmount - busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentAmount);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.OIL_CODE) {
                        // 油卡金额科目
                        //修改订单，撤单油卡特殊处理
                        boolean flag = false;
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_FICTITIOUS_OIL
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_LOW) {
                            currentOilBalance -= busiSubjectsRel.getAmountFee();
                            List<OrderOilSource> sourceList = param.getSourceList();
                            flag = true;
                            long tempAmount = 0;
                            long transferAmount = 0;
                            for (OrderOilSource source : sourceList) {
                                if (source.getMatchAmount() == null || source.getMatchAmount() <= 0) {
                                    continue;
                                }
                                if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                                    continue;
                                }
                                flag = false;
                                tempAmount += source.getMatchAmount();
                                transferAmount += source.getMatchAmount();
                                OrderAccount ac = orderAccountService.queryOrderAccount(account.getUserId(), source.getVehicleAffiliation(), 0L, source.getSourceTenantId(), source.getOilAffiliation(), source.getUserType());
                                ac.setOilBalance(ac.getOilBalance() - source.getMatchAmount());
                                orderAccountService.saveOrUpdate(ac);
                                //账户油资金继承来源关系
                                OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(ac.getId(), ac.getUserId(), account.getSourceTenantId(), ac.getUserType());
                                if (oaos != null) {
                                    oaos.setOilBalance((oaos.getOilBalance() == null ? 0L : oaos.getOilBalance()) - source.getMatchAmount());
                                    oaos.setUpdateTime(LocalDateTime.now());
                                } else {
                                    String oilSourceDate = readisUtil.getSysCfg(OrderAccountConst.OIL_SOURCE_RECORD.OIL_SOURCE_DATE, "0").getCfgValue();
                                    if (StringUtils.isBlank(oilSourceDate)) {
                                        throw new BusinessException("虚拟油时间点");
                                    }
                                    if (source.getCreateTime().isBefore(LocalDateTimeUtil.convertStringToDate(oilSourceDate))) {
                                        OrderAccountOilSource orderAccountOilSource = orderAccountOilSourceService.getOrderAccountOilSource(ac.getId(), ac.getUserId(), source.getSourceTenantId(), ac.getUserType());
                                        if (orderAccountOilSource != null) {
                                            orderAccountOilSource.setOilBalance((orderAccountOilSource.getOilBalance() == null ? 0L : orderAccountOilSource.getOilBalance()) - source.getMatchAmount());
                                            orderAccountOilSource.setUpdateTime(LocalDateTime.now());
                                        }
                                    } else {
                                        throw new BusinessException("根据accId：" + ac.getId() + "userId: " + ac.getUserId() + " tenantId: " + account.getSourceTenantId() + " 未找到油账户来源关系信息");
                                    }
                                }
                                orderAccountOilSourceService.saveOrUpdate(oaos);
                            }
                            currentOilBalance += tempAmount;
                            accDet.setTransferAmount(transferAmount);
                        } else {
                            if (busiSubjectsRel.getSubjectsId() != EnumConsts.SubjectIds.CANCEL_ORDER_ENTITY_OIL && busiSubjectsRel.getSubjectsId() != EnumConsts.SubjectIds.CANCEL_ORDER_OWN_ENTITY_OIL
                                    && busiSubjectsRel.getSubjectsId() != EnumConsts.SubjectIds.CONSUME_BEFORE_Entity_SUB && busiSubjectsRel.getSubjectsId() != EnumConsts.SubjectIds.CONSUME_BEFORE_ENTIY_OIL_FEE) {
                                currentOilBalance = currentOilBalance - busiSubjectsRel.getAmountFee();
                                if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CLEAR_ORDER_ACCOUNT_OIL_FEE || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_OIL
                                        || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL
                                ) {
                                    if (param == null) {
                                        throw new BusinessException("请除油账户时，入参不正确");
                                    }
                                    OrderOilSource orderOilSource = param.getOrderOilSource();
                                    if (orderOilSource == null) {
                                        throw new BusinessException("请除油账户时，入参不正确");
                                    }
                                    Long orderOilSourceTenanId = orderOilSource.getTenantId();
                                    //账户油资金继承来源关系
                                    OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), orderOilSourceTenanId, account.getUserType());
                                    if (oaos != null) {
                                        oaos.setOilBalance((oaos.getOilBalance() == null ? 0L : oaos.getOilBalance()) - busiSubjectsRel.getAmountFee());
                                        oaos.setUpdateTime(LocalDateTime.now());
                                        orderAccountOilSourceService.saveOrUpdate(oaos);
                                    } else {
                                        String oilSourceDate = readisUtil.getSysCfg(OrderAccountConst.OIL_SOURCE_RECORD.OIL_SOURCE_DATE, "0").getCfgValue();
                                        if (StringUtils.isBlank(oilSourceDate)) {
                                            throw new BusinessException("虚拟油时间点");
                                        }
                                        LocalDateTime time = null;
                                        if (oilSourceDate != null && !oilSourceDate.equals("")) {
                                            time = LocalDateTimeUtil.convertStringToDate(oilSourceDate);
                                        }
                                        if (time != null && orderOilSource.getCreateTime().isBefore(time)) {
                                            OrderAccountOilSource orderAccountOilSource = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), orderOilSource.getSourceTenantId(), account.getUserType());
                                            if (orderAccountOilSource != null) {
                                                orderAccountOilSource.setOilBalance((orderAccountOilSource.getOilBalance() == null ? 0L : orderAccountOilSource.getOilBalance()) - busiSubjectsRel.getAmountFee());
                                                orderAccountOilSource.setUpdateTime(LocalDateTime.now());
                                            }
                                        } else {
                                            throw new BusinessException("根据accId：" + account.getId() + "userId: " + account.getUserId() + " tenantId: " + account.getSourceTenantId() + " 未找到油账户来源关系信息");
                                        }
                                    }
                                }
                                //司机小费油记录产品id
                                if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_OIL_SUB || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.BUY_OIL_SUB) {
                                    accDet.setEtcNumber(etcNumber);//油站id
                                    accDet.setPlateNumber(plateNumber);//油站地址
                                }
                                if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_OIL_SUB) {//消费订单虚拟油
                                    if (param == null) {
                                        throw new BusinessException("请除油账户时，入参不正确");
                                    }
                                    OrderOilSource orderOilSource = param.getOilSource();
                                    if (orderOilSource == null) {
                                        throw new BusinessException("请除油账户时，入参不正确");
                                    }
                                    Long orderOilSourceTenanId = orderOilSource.getTenantId();
                                    //账户油资金继承来源关系
                                    OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), orderOilSourceTenanId, account.getUserType());
                                    if (oaos != null) {
                                        oaos.setOilBalance((oaos.getOilBalance() == null ? 0L : oaos.getOilBalance()) - busiSubjectsRel.getAmountFee());
                                        oaos.setUpdateTime(LocalDateTime.now());
                                        orderAccountOilSourceService.saveOrUpdate(oaos);
                                    } else {
                                        String oilSourceDate = readisUtil.getSysCfg(OrderAccountConst.OIL_SOURCE_RECORD.OIL_SOURCE_DATE, "0").getCfgValue();
                                        if (StringUtils.isBlank(oilSourceDate)) {
                                            throw new BusinessException("虚拟油时间点");
                                        }

                                        if (orderOilSource.getCreateTime().isBefore(LocalDateTimeUtil.convertStringToDate(oilSourceDate))) {
                                            OrderAccountOilSource orderAccountOilSource = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), orderOilSource.getSourceTenantId(), account.getUserType());
                                            if (orderAccountOilSource != null) {
                                                orderAccountOilSource.setOilBalance((orderAccountOilSource.getOilBalance() == null ? 0L : orderAccountOilSource.getOilBalance()) - busiSubjectsRel.getAmountFee());
                                                orderAccountOilSource.setUpdateTime(LocalDateTime.now());
                                            }
                                        } else {
                                            throw new BusinessException("根据accId：" + account.getId() + "userId: " + account.getUserId() + " tenantId: " + account.getSourceTenantId() + " 未找到油账户来源关系信息");
                                        }
                                    }
                                    if (orderOilSource.getOrderId().longValue() != orderOilSource.getSourceOrderId().longValue()) {
                                        Long tenantUserId = sysTenantDefService.getSysTenantDef(orderOilSourceTenanId).getAdminUser();
                                        OrderAccount sourceOrderAccount = orderAccountService.getOrderAccount(tenantUserId, account.getVehicleAffiliation(), account.getSourceTenantId(), account.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                                        if (sourceOrderAccount != null) {
                                            OrderAccountOilSource sourceOaos = orderAccountOilSourceService.getOrderAccountOilSource(sourceOrderAccount.getId(), sourceOrderAccount.getUserId(), sourceOrderAccount.getSourceTenantId(), sourceOrderAccount.getUserType());
                                            if (sourceOaos != null) {
                                                sourceOaos.setOilBalance((sourceOaos.getOilBalance() == null ? 0L : sourceOaos.getOilBalance()) - busiSubjectsRel.getAmountFee());
                                                sourceOaos.setUpdateTime(LocalDateTime.now());
                                                orderAccountOilSourceService.saveOrUpdate(sourceOaos);

                                            }
                                        }
                                    }
                                }
                            }
                        }
                        accDet.setCurrentAmount(currentOilBalance);
                        if (flag && (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_FICTITIOUS_OIL
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_LOW)) {
                            //账户油资金继承来源关系
                            OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), account.getSourceTenantId(), account.getUserType());
                            if (oaos != null) {
                                oaos.setOilBalance((oaos.getOilBalance() == null ? 0L : oaos.getOilBalance()) - busiSubjectsRel.getAmountFee());
                                oaos.setUpdateTime(LocalDateTime.now());
                            } else {
                                throw new BusinessException("根据accId：" + account.getId() + "userId: " + account.getUserId() + " tenantId: " + account.getSourceTenantId() + " 未找到油账户来源关系信息");
                            }
                            orderAccountOilSourceService.saveOrUpdate(oaos);
                        }
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.ETC_CODE) {
                        // ETC金额科目
                        if (busiSubjectsRel.getSubjectsId() != EnumConsts.SubjectIds.CANCEL_ORDER_BRIDGE) {
                            currentEtcBalance = currentEtcBalance - busiSubjectsRel.getAmountFee();
                        }
                        accDet.setCurrentAmount(currentEtcBalance);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.BOND_CODE) {
                        // 未到期金额科目
                        //发放工资抵扣订单欠款特殊处理(如果有欠款，说明账户尾款是负数的，此时发放工资抵扣)
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_AWARD16) {
                            currentMarginBalance = currentMarginBalance + busiSubjectsRel.getAmountFee();
                        } else {
                            currentMarginBalance = currentMarginBalance - busiSubjectsRel.getAmountFee();
                        }
                        accDet.setCurrentAmount(currentMarginBalance);
                        //油卡抵押特殊处理
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_MARGIN) {
                            pledgeOilCardFee = pledgeOilCardFee + busiSubjectsRel.getAmountFee();
                        }

                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.BEFOREPAY_CODE) {
                        currentBeforePay = currentBeforePay - busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentBeforePay);
                    }
                    // 维修基金
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.REPAIR_FUND_CODE) {
                        repairFund = repairFund - busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(repairFund);
                    }
                    //应收逾期
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.RECEIVABLE_OVERDUE_CODE) {
                        currentReceivableBalance = currentReceivableBalance - busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentReceivableBalance);
                    }
                    //应付逾期
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.PAYABLE_OVERDUE_CODE) {
                        currentPayableBalance = currentPayableBalance - busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentPayableBalance);
                        //油卡释放特殊处理
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_OUT) {
                            pledgeOilCardFee = pledgeOilCardFee - busiSubjectsRel.getAmountFee();
                        }
                    }
                    //充值油账户
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.RECHARGE_OIL_CODE) {
                        currentRechargeOilBalance = currentRechargeOilBalance - busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentRechargeOilBalance);
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CLEAR_RECHARGE_ACCOUNT_OIL_FEE || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_RECHARGE_OIL_OUT) {
                            if (param == null) {
                                throw new BusinessException("请除油账户时，入参不正确");
                            }
                            RechargeOilSource rechargeOilSource = param.getRechargeOilSource();
                            if (rechargeOilSource == null) {
                                throw new BusinessException("请除油账户时，入参不正确");
                            }
                            Long rechargeOilSourceTenanId = rechargeOilSource.getTenantId();
                            //账户油资金继承来源关系
                            OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), rechargeOilSourceTenanId, account.getUserType());
                            if (oaos != null) {
                                oaos.setRechargeOilBalance((oaos.getRechargeOilBalance() == null ? 0L : oaos.getRechargeOilBalance()) - busiSubjectsRel.getAmountFee());
                                oaos.setUpdateTime(LocalDateTime.now());
                                orderAccountOilSourceService.saveOrUpdate(oaos);
                            } else {
                                String oilSourceDate = readisUtil.getSysCfg(OrderAccountConst.OIL_SOURCE_RECORD.OIL_SOURCE_DATE, "0").getCfgValue();
                                if (StringUtils.isBlank(oilSourceDate)) {
                                    throw new BusinessException("虚拟油时间点");
                                }
                                if (rechargeOilSource.getCreateTime().isBefore(LocalDateTimeUtil.convertStringToDate(oilSourceDate))) {
                                    OrderAccountOilSource orderAccountOilSource = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), rechargeOilSource.getSourceTenantId(), account.getUserType());
                                    if (orderAccountOilSource != null) {
                                        orderAccountOilSource.setRechargeOilBalance((orderAccountOilSource.getRechargeOilBalance() == null ? 0L : orderAccountOilSource.getRechargeOilBalance()) - busiSubjectsRel.getAmountFee());
                                        orderAccountOilSource.setUpdateTime(LocalDateTime.now());
                                    }
                                } else {
                                    throw new BusinessException("根据accId：" + account.getId() + "userId: " + account.getUserId() + " tenantId: " + account.getSourceTenantId() + " 未找到油账户来源关系信息");
                                }
                            }
                        }
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_RECHARGE_OIL_SUB) {//消费账户充值油
                            if (param == null) {
                                throw new BusinessException("请除油账户时，入参不正确");
                            }
                            RechargeOilSource rechargeOilSource = param.getRechargeOilSource();
                            if (rechargeOilSource == null) {
                                throw new BusinessException("请除油账户时，入参不正确");
                            }
                            Long rechargeOilSourceTenanId = rechargeOilSource.getTenantId();
                            //账户油资金继承来源关系
                            OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), rechargeOilSourceTenanId, account.getUserType());
                            if (oaos != null) {
                                oaos.setRechargeOilBalance((oaos.getRechargeOilBalance() == null ? 0L : oaos.getRechargeOilBalance()) - busiSubjectsRel.getAmountFee());
                                oaos.setUpdateTime(LocalDateTime.now());
                                orderAccountOilSourceService.saveOrUpdate(oaos);
                            } else {
//                                 rdeis 拿到的时间是2019年 和当前的时间  永远都是抛出异常
                                String oilSourceDate = readisUtil.getSysCfg(OrderAccountConst.OIL_SOURCE_RECORD.OIL_SOURCE_DATE, "0").getCfgValue();
                                if (StringUtils.isBlank(oilSourceDate)) {
                                    throw new BusinessException("虚拟油时间点");
                                }
                                if (rechargeOilSource.getCreateTime().isBefore(LocalDateTimeUtil.convertStringToDate(oilSourceDate))) {
                                    OrderAccountOilSource orderAccountOilSource = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), rechargeOilSource.getSourceTenantId(), account.getUserType());
                                    if (orderAccountOilSource != null) {
                                        orderAccountOilSource.setRechargeOilBalance((orderAccountOilSource.getRechargeOilBalance() == null ? 0L : orderAccountOilSource.getRechargeOilBalance()) - busiSubjectsRel.getAmountFee());
                                        orderAccountOilSource.setUpdateTime(LocalDateTime.now());
                                    }
                                }
                                else {
                                    throw new BusinessException("根据accId：" + account.getId() + "userId: " + account.getUserId() + " tenantId: " + account.getSourceTenantId() + " 未找到油账户来源关系信息");
                                }
                            }
                            if (!rechargeOilSource.getRechargeOrderId().equals(rechargeOilSource.getSourceOrderId())) {
                                Long tenantUserId = sysTenantDefService.getSysTenantDef(rechargeOilSourceTenanId).getAdminUser();
                                OrderAccount sourceOrderAccount = orderAccountService.getOrderAccount(tenantUserId, account.getVehicleAffiliation(), account.getSourceTenantId(), account.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                                if (sourceOrderAccount != null) {
                                    OrderAccountOilSource sourceOaos = orderAccountOilSourceService.getOrderAccountOilSource(sourceOrderAccount.getId(), sourceOrderAccount.getUserId(), sourceOrderAccount.getSourceTenantId(), sourceOrderAccount.getUserType());
                                    if (sourceOaos != null) {
                                        sourceOaos.setOilBalance((sourceOaos.getOilBalance() == null ? 0L : sourceOaos.getOilBalance()) - busiSubjectsRel.getAmountFee());
                                        sourceOaos.setUpdateTime(LocalDateTime.now());
                                        orderAccountOilSourceService.saveOrUpdate(sourceOaos);
                                    }
                                }
                            }
                        }
                    }
                }
                // 支入
                if (busiSubjectsRel.getSubjectsType()  != null && busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_IN) {
                    accDet.setAmount(busiSubjectsRel.getAmountFee());
                    amountFeeSum = amountFeeSum + busiSubjectsRel.getAmountFee();
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.ACCOUNT_CODE) {
                        // 可用金额科目
                        currentAmount = currentAmount + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentAmount);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.OIL_CODE) {
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_Virtual_SUB || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_CANCEL_ORDER
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_UPP || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_UPP || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.CLEAR_ACCOUNT_OIL_ALLOT
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_OIL_TRUN_CASH || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_ZHANG_PING_OIL_BALANCE) {
                            currentOilBalance = currentOilBalance + busiSubjectsRel.getAmountFee();
                        }
                        //修改订单，订单油特殊处理
                        long oilAmountFee = 0;
                        if (param != null && param.getSourceList() != null && param.getSourceList().size() > 0 && (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_Virtual_SUB || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_UPP)) {
                            List<OrderOilSource> sourceList = param.getSourceList();
                            long tempAmount = 0;
                            for (OrderOilSource source : sourceList) {
                                if (source.getMatchAmount() == null || source.getMatchAmount() <= 0) {
                                    continue;
                                }
                                if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                                    continue;
                                }
                                tempAmount += source.getMatchAmount();
                                OrderAccount ac = orderAccountService.queryOrderAccount(account.getUserId(), source.getVehicleAffiliation(), 0L, source.getSourceTenantId(), source.getOilAffiliation(), source.getUserType());
                                ac.setOilBalance(ac.getOilBalance() + source.getMatchAmount());
                                orderAccountService.saveOrUpdate(ac);
                                //账户油资金继承来源关系
                                OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(ac.getId(), ac.getUserId(), account.getSourceTenantId(), ac.getUserType());
                                if (oaos != null) {
                                    oaos.setOilBalance((oaos.getOilBalance() == null ? 0L : oaos.getOilBalance()) + source.getMatchAmount());
                                    oaos.setUpdateTime(LocalDateTime.now());
                                } else {
                                    oaos = orderAccountOilSourceService.createOrderAccountOilSource(null, ac.getId(), ac.getUserId(), source.getMatchAmount(), 0L, account.getSourceTenantId(), ac.getUserType(), user);
                                }
                                orderAccountOilSourceService.saveOrUpdate(oaos);
                            }
                            oilAmountFee = tempAmount;
                            currentOilBalance -= tempAmount;
                            accDet.setTransferAmount(tempAmount);
                        }
                        if ((busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_Virtual_SUB || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP
                                || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_UPP)) {
                            //账户油资金继承来源关系
                            OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), account.getSourceTenantId(), account.getUserType());
                            if (oaos != null) {
                                oaos.setOilBalance((oaos.getOilBalance() == null ? 0L : oaos.getOilBalance()) + busiSubjectsRel.getAmountFee() - oilAmountFee);
                                oaos.setUpdateTime(LocalDateTime.now());
                            } else {
                                oaos = orderAccountOilSourceService.createOrderAccountOilSource(null, account.getId(), account.getUserId(), busiSubjectsRel.getAmountFee() - oilAmountFee, 0L, account.getSourceTenantId(), account.getUserType(), user);
                            }
                            //注释掉，影响油账号问题，订单审核那边单独加
                            orderAccountOilSourceService.saveOrUpdate(oaos);
                        }
                        accDet.setCurrentAmount(currentOilBalance);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.ETC_CODE) {
                        // ETC金额科目
                        if (busiSubjectsRel.getSubjectsId() != EnumConsts.SubjectIds.BEFOREPAY_BRIDGE) {
                            currentEtcBalance = currentEtcBalance + busiSubjectsRel.getAmountFee();
                        }
                        accDet.setCurrentAmount(currentEtcBalance);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.BOND_CODE) {
                        // 未到期金额科目
                        currentMarginBalance = currentMarginBalance + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentMarginBalance);
                        //油卡释放特殊处理
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_MARGIN) {
                            pledgeOilCardFee = pledgeOilCardFee - busiSubjectsRel.getAmountFee();
                        }
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.CHANGE_CODE) {
                        // 未到期转可用（未到期金额操作）
                        currentMarginBalance = currentMarginBalance - busiSubjectsRel.getAmountFee();
                        currentAmount = currentAmount + busiSubjectsRel.getAmountFee();
                        accDet.setAmount(busiSubjectsRel.getAmountFee());
                        accDet.setCurrentAmount(currentAmount);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.BEFOREPAY_CODE) {
                        currentBeforePay = currentBeforePay + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentBeforePay);
                    }
                    // 维修基金
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.REPAIR_FUND_CODE) {
                        repairFund = repairFund + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(repairFund);
                    }
                    //应收逾期
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.RECEIVABLE_OVERDUE_CODE) {
                        currentReceivableBalance = currentReceivableBalance + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentReceivableBalance);
                        //油卡释放特殊处理
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN) {
                            pledgeOilCardFee = pledgeOilCardFee - busiSubjectsRel.getAmountFee();
                        }
                    }
                    //应付逾期
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.PAYABLE_OVERDUE_CODE) {
                        currentPayableBalance = currentPayableBalance + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentPayableBalance);
                        //油卡抵押特殊处理
                        if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN) {
                            pledgeOilCardFee = pledgeOilCardFee + busiSubjectsRel.getAmountFee();
                        }
                    }
                    //充值油账户
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.RECHARGE_OIL_CODE) {
                        currentRechargeOilBalance = currentRechargeOilBalance + busiSubjectsRel.getAmountFee();
                        //使用客户油充值特殊处理
                        long oilAmountFee = 0;
                        if (param != null && param.getRechargeOilSourceList() != null && param.getRechargeOilSourceList().size() > 0) {
                            List<RechargeOilSource> sourceList = param.getRechargeOilSourceList();
                            long tempAmount = 0;
                            if (sourceList != null && sourceList.size() > 0) {
                                for (RechargeOilSource source : sourceList) {
                                    if (source.getMatchAmount() == null || source.getMatchAmount() <= 0) {
                                        continue;
                                    }
                                    if (String.valueOf(source.getRechargeOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                                        continue;
                                    }
                                    tempAmount += source.getMatchAmount();
                                    OrderAccount ac = orderAccountService.queryOrderAccount(account.getUserId(), source.getVehicleAffiliation(), 0L, source.getSourceTenantId(), source.getOilAffiliation(), account.getUserType());
                                    ac.setRechargeOilBalance(ac.getRechargeOilBalance() + source.getMatchAmount());
                                    orderAccountService.saveOrUpdate(ac);
                                    //账户油资金继承来源关系
                                    OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(ac.getId(), ac.getUserId(), account.getSourceTenantId(), ac.getUserType());
                                    if (oaos != null) {
                                        oaos.setRechargeOilBalance((oaos.getRechargeOilBalance() == null ? 0L : oaos.getRechargeOilBalance()) + source.getMatchAmount());
                                        oaos.setUpdateTime(LocalDateTime.now());
                                    } else {
                                        oaos = orderAccountOilSourceService.createOrderAccountOilSource(null, ac.getId(), ac.getUserId(), 0L, source.getMatchAmount(), account.getSourceTenantId(), ac.getUserType(), user);
                                    }
                                    orderAccountOilSourceService.saveOrUpdate(oaos);
                                }
                                oilAmountFee = tempAmount;
                                currentRechargeOilBalance -= tempAmount;
                                accDet.setTransferAmount(tempAmount);
                            }
                        }
                        accDet.setCurrentAmount(currentRechargeOilBalance);
                        //账户油资金继承来源关系
                        OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), account.getSourceTenantId(), account.getUserType());
                        if (oaos != null) {
                            oaos.setRechargeOilBalance((oaos.getRechargeOilBalance() == null ? 0L : oaos.getRechargeOilBalance()) + busiSubjectsRel.getAmountFee() - oilAmountFee);
                            oaos.setUpdateTime(LocalDateTime.now());
                        } else {
                            oaos = orderAccountOilSourceService.createOrderAccountOilSource(null, account.getId(), account.getUserId(), 0L, busiSubjectsRel.getAmountFee() - oilAmountFee, account.getSourceTenantId(), account.getUserType(), user);
                        }
                        orderAccountOilSourceService.saveOrUpdate(oaos);
                    }
                }
                accDet.setCostType(busiSubjectsRel.getSubjectsType());
                // 3根据金额改变
                if (busiSubjectsRel.getSubjectsType()  != null && busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_INOUT) {
                    accDet.setAmount(busiSubjectsRel.getAmountFee());
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.ACCOUNT_CODE) {
                        // 可用金额科目
                        currentAmount = currentAmount + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentAmount);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.BOND_CODE) {
                        // 可用金额科目
                        currentMarginBalance = currentMarginBalance + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentMarginBalance);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.RECEIVABLE_OVERDUE_CODE) {
                        // 应收逾期金额科目
                        currentReceivableBalance = currentReceivableBalance + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentReceivableBalance);
                    }
                    if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.PAYABLE_OVERDUE_CODE) {
                        // 应付逾期金额科目
                        currentPayableBalance = currentPayableBalance + busiSubjectsRel.getAmountFee();
                        accDet.setCurrentAmount(currentPayableBalance);
                    }
                    if (busiSubjectsRel.getAmountFee() >= 0) {
                        accDet.setCostType(EnumConsts.PayInter.FEE_IN);
                    } else {
                        accDet.setCostType(EnumConsts.PayInter.FEE_OUT);
                    }
                }
                if (happenDate != null) {
                    accDet.setHappenDate(happenDate);
                }
                accDet.setCreateTime(LocalDateTime.now());
                accDet.setNote(busiSubjectsRel.getSubjectsName());
                if (busiSubjectsRel.getSubjectsId() != null && busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_FINE_FEE) {
                    accDet.setSoNbr(CommonUtil.createSoNbr());
                } else {
                    accDet.setSoNbr(soNbr);
                }
                accDet.setOrderId(orderId + "");
                accDet.setOtherUserId(otherUserId);
                accDet.setOtherName(otherName);
                accDet.setBookType(StringUtils.isNotBlank(busiSubjectsRel.getBookType())?Long.parseLong(busiSubjectsRel.getBookType()):0L);
                accDet.setTenantId(tenantId);
                accDet.setPlateNumber(plateNumber);
                if (etcNumber != null && !"".equals(etcNumber)) {
                    accDet.setEtcNumber(etcNumber);
                }

                this.saveAccountDetails(accDet);
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.ACCOUNT_CODE) {
                    // 可用金额科目
                    account.setBalance(currentAmount);
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.OIL_CODE) {
                    // 油卡金额科目
                    account.setOilBalance(currentOilBalance);
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.ETC_CODE) {
                    // 油卡金额科目
                    account.setEtcBalance(currentEtcBalance);
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.BOND_CODE) {
                    // 未到期金额科目
                    account.setMarginBalance(currentMarginBalance);
                    //油卡抵押释放特殊处理
                    if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_MARGIN || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_MARGIN) {
                        account.setPledgeOilCardFee(pledgeOilCardFee);
                    }
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.CHANGE_CODE) {
                    // 未到期转可用
                    account.setBalance(currentAmount);
                    account.setMarginBalance(currentMarginBalance);
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.BEFOREPAY_CODE) {
                    // 预支账户操作
                    account.setBeforePay(currentBeforePay);
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.REPAIR_FUND_CODE) {
                    // 维修基金
                    account.setRepairFund(repairFund);
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.RECEIVABLE_OVERDUE_CODE) {
                    //应收逾期
                    account.setReceivableOverdueBalance(currentReceivableBalance);
                    //油卡抵押释放特殊处理
                    if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN) {
                        account.setPledgeOilCardFee(pledgeOilCardFee);
                    }
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.PAYABLE_OVERDUE_CODE) {
                    //应付逾期
                    account.setPayableOverdueBalance(currentPayableBalance);
                    //油卡抵押释放特殊处理
                    if (busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_OUT || busiSubjectsRel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN) {
                        account.setPledgeOilCardFee(pledgeOilCardFee);
                    }
                }
                if (busiSubjectsRel.getBookType() != null && Long.parseLong(busiSubjectsRel.getBookType()) == EnumConsts.PayInter.RECHARGE_OIL_CODE) {
                    //充值油账户
                    account.setRechargeOilBalance(currentRechargeOilBalance);
                }
                // ETC充值业务，无account
                if (account != null) {
                    log.info("操作后账户信息:" + account);
                    orderAccountService.saveOrUpdate(account);
                    //如果没有油费，也生成一条order_account_oil_source
                    //账户油资金继承来源关系
                    OrderAccountOilSource oaos = orderAccountOilSourceService.getOrderAccountOilSource(account.getId(), account.getUserId(), account.getSourceTenantId(), account.getUserType());
                    if (oaos == null) {
                        oaos = orderAccountOilSourceService.createOrderAccountOilSource(null, account.getId(), account.getUserId(), 0L, 0L, account.getSourceTenantId(), account.getUserType(), user);
                        orderAccountOilSourceService.saveOrUpdate(oaos);
                    }
                }
            }
        }
        return amountFeeSum;
    }

    @Override
    public void saveAccountDetails(AccountDetails details) {
//        this.save(details);
        String time = com.youming.youche.util.DateUtil.formatDate(new Date(), com.youming.youche.util.DateUtil.YEAR_MONTH_FORMAT2);
        String tableName="account_details_"+time;
        Integer tableIsExist = baseMapper.tableIsExist(tableName);
        if (tableIsExist != null && tableIsExist > 0) {//表存在
            if (details.getId() == null) { //记录不存在
                baseMapper.insertTable(details, tableName);
            } else {
                baseMapper.updateTable(details, tableName);
            }
        } else {
            // 新建表
            baseMapper.createTable(tableName);
            baseMapper.insertTable(details, tableName);
        }
        accountDetailsSummaryService.saveAccountDetailsSummary(details);

    }

    @Override
    public void createAccountDetailsNew(int businessTypes, long businessCode, Long userId, Long otherUserId, String otherName, List<BusiSubjectsRel> subjectsList, long soNbr, Long orderId, Long tenantId, int userType, Long time) {
        if (subjectsList != null && subjectsList.size() > 0) {
            for (int i = 0; i < subjectsList.size(); i++) {
                BusiSubjectsRel busiSubjectsRel = subjectsList.get(i);
                // 写入账户明细表
                AccountDetails accDet = new AccountDetails();
                accDet.setUserId(userId);
                //会员体系开始
                accDet.setUserType(userType);
                //会员体系结束
                accDet.setBusinessTypes(businessTypes);
                accDet.setBusinessNumber(businessCode);
                accDet.setSubjectsId(busiSubjectsRel.getSubjectsId());
                accDet.setSubjectsName(busiSubjectsRel.getSubjectsName());
                // 支出
                if (busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_OUT) {
                    accDet.setAmount(-busiSubjectsRel.getAmountFee());
                }
                // 支入
                if (busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_IN) {
                    accDet.setAmount(busiSubjectsRel.getAmountFee());
                }
                accDet.setCostType(busiSubjectsRel.getSubjectsType());
                accDet.setCreateTime(LocalDateTime.now());
                accDet.setNote(busiSubjectsRel.getSubjectsName());
                accDet.setSoNbr(time);
                accDet.setOrderId(orderId + "");
                accDet.setOtherUserId(otherUserId);
                accDet.setOtherName(otherName);
                accDet.setBookType(Long.parseLong(busiSubjectsRel.getBookType()));
                accDet.setTenantId(tenantId);
                accDet.setAccountId(soNbr);
                saveAccountDetails(accDet);
            }
        }
    }

    @Override
    public void createAccountDetails(int businessTypes, long businessCode, Long userId, Long otherUserId, String otherName, List<BusiSubjectsRel> subjectsList, long soNbr, Long orderId, Long tenantId, int userType) {
        if (subjectsList != null && subjectsList.size() > 0) {
            for (int i = 0; i < subjectsList.size(); i++) {
                BusiSubjectsRel busiSubjectsRel = subjectsList.get(i);
                // 写入账户明细表
                AccountDetails accDet = new AccountDetails();
                accDet.setUserId(userId);
                //会员体系开始
                accDet.setUserType(userType);
                //会员体系结束
                accDet.setBusinessTypes(businessTypes);
                accDet.setBusinessNumber(businessCode);
                accDet.setSubjectsId(busiSubjectsRel.getSubjectsId());
                accDet.setSubjectsName(busiSubjectsRel.getSubjectsName());
                // 支出
                if (busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_OUT) {
                    accDet.setAmount(-busiSubjectsRel.getAmountFee());
                }
                // 支入
                if (busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_IN) {
                    accDet.setAmount(busiSubjectsRel.getAmountFee());
                }
                accDet.setCostType(busiSubjectsRel.getSubjectsType());
                accDet.setCreateTime(LocalDateTime.now());
                accDet.setNote(busiSubjectsRel.getSubjectsName());
                accDet.setSoNbr(soNbr);
                accDet.setOrderId(orderId + "");
                accDet.setOtherUserId(otherUserId);
                accDet.setOtherName(otherName);
                accDet.setBookType(Long.parseLong(busiSubjectsRel.getBookType()));
                accDet.setTenantId(tenantId);
                saveAccountDetails(accDet);
            }
        }
    }

    @Override
    public AppAccountDetailsDto getAccountDetail(AppAccountDetailsVo appAccountDetailsVo, String accessToken) {
        AppAccountDetailsDto dto = getAccountDetailNew(appAccountDetailsVo, accessToken);

        List<AppAccountDetailsOutDto> items = dto.getItems();
        if (items != null && items.size() > 400) {
            List<AppAccountDetailsOutDto> newList = items.subList(0, 400);
            dto.setItems(newList);
        }

        return dto;
    }


    @Override
    public List<AccountDetails> queryAccountDetailsMerge(AppAccountDetailsVo appAccountDetailsVo, Integer userType) {
        List<AccountDetails> list = accountDetailsMapper.queryAccountDetailsMerge(appAccountDetailsVo, userType);

        if (list != null && list.size() > 0 && Integer.parseInt(appAccountDetailsVo.getMonth()) >= 201901) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date bt = null;
            try {
                bt = sdf.parse("2019-01-14 16:30:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                AccountDetails detail = list.get(i);
                LocalDateTime localDateTime = detail.getCreateTime();
                Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                if (detail.getCreateTime() != null && bt.before(date)) {
                    if (detail.getSubjectsId() == EnumConsts.SubjectIds.EXPENSE_FEE_OUT
                            || detail.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS1816
                            || detail.getSubjectsId() == EnumConsts.SubjectIds.EXPENSE_RECEIVED
                            || detail.getSubjectsId() == EnumConsts.SubjectIds.OA_LOAN_AVAILABLE_OUT
                            || detail.getSubjectsId() == EnumConsts.SubjectIds.OA_LOAN_RECEIVED
                            || detail.getSubjectsId() == EnumConsts.SubjectIds.SALARY_RECEIVED) {
                        list.remove(i);
                    }
                }
            }
        }
        if (appAccountDetailsVo.getMonth().equals(String.valueOf(EnumConsts.APP_ACCOUNT_DETAILS_OUT.LAST_MONTH))) {
            return list;
        }
        List<AccountDetails> copyList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            list.forEach(accountDetails -> {
                try {
                    copyList.add(accountDetails.deepClone());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return copyList;
    }

    @Override
    public AppAccountDetailsDto getAccountDetailNew(AppAccountDetailsVo appAccountDetailsVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(appAccountDetailsVo.getRequestType()) && org.apache.commons.lang3.StringUtils.equals("1", appAccountDetailsVo.getRequestType())) {//	1-小程序驻场号
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
            if (sysTenantDef != null) {
                appAccountDetailsVo.setUserId(sysTenantDef.getAdminUser());
            } else {
                appAccountDetailsVo.setUserId(loginInfo.getUserInfoId());
            }
        }
        if (StringUtils.isNotBlank(appAccountDetailsVo.getMonth()) && Long.valueOf(appAccountDetailsVo.getMonth()).compareTo(EnumConsts.APP_ACCOUNT_DETAILS_OUT.LAST_MONTH) < 0) {
            throw new BusinessException("不存在该月份之前的数据!");
        }
        if (appAccountDetailsVo.getUserId() <= 0L) {
            throw new BusinessException("用户ID为空!");
        }
        if (appAccountDetailsVo.getType().isEmpty()) {
            throw new BusinessException("请输入查询类型！");
        }
        // 判断是否存在该月份的数据 -- 判断这个月的分表是否存在
        String tableName = baseMapper.showTableName("'account_details_" + appAccountDetailsVo.getMonth()+"'");
        if (StringUtils.isBlank(tableName)) {
            throw new BusinessException("不存在该月份的数据！");
        }

        //借支查询没有直接返回，其他查询没有，再查询前一个月份数据，RECHARGE_ACCOUNT_OIL_FEE如有数据就返回，直到查询到201805
        String codeType = "APP_ACCOUNT_DETAILS_OUT";

        Integer userType = userDataInfoService.selectUserType(loginInfo.getUserInfoId()).getUserType();
        List<AccountDetails> list = this.queryAccountDetailsMerge(appAccountDetailsVo,userType);
        Map existMap = new HashMap();
        if (list != null && list.size() > 0) {
            for (int i = list.size() - 1; i >= 0; i--) {
                AccountDetails detail = list.get(i);
                //把金额为0的流水去掉 \撤单油卡流水去掉 去掉所有逾期科目
                if (detail.getAmount() == 0 || detail.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ENTITY_OIL
                        || detail.getSubjectsId() == EnumConsts.SubjectIds.BEFOREPAY_BRIDGE
                        || detail.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS50136 //已收待提现
                        || detail.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS50138 //已收已撤回
                        || detail.getSubjectsId() == EnumConsts.SubjectIds.SUBJECTIDS50140 //已收已支付
                ) {
                    list.remove(i);
                }
                if (EnumConsts.PayInter.PAY_CASH == detail.getBusinessNumber() || EnumConsts.PayInter.PAY_WITHDRAW == detail.getBusinessNumber() ||
                        EnumConsts.PayInter.PAY_DRIVER == detail.getBusinessNumber()) {
                    String key = detail.getSubjectsId() + "" + detail.getSoNbr();
                    if (existMap.containsKey(key)) {
                        list.remove(i);
                    } else {
                        existMap.put(key, key);
                    }
                }
                //油卡支出 特殊处理
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_BEFORE_ENTIY_OIL_FEE) {
                    detail.setBusinessNumber(210000021520L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_BEFORE_Entity_SUB) {
                    detail.setBusinessNumber(210000021519L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT) {
                    detail.setBusinessNumber(210000021521L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.PRESCRIPTION_FINE) {
                    detail.setBusinessNumber(210000141522L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_FINE_FEE) {
                    detail.setBusinessNumber(210000141082L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_ENTITY_CARD_OUT) {
                    detail.setBusinessNumber(210000253516L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB) {
                    detail.setBusinessNumber(210000021632L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_PAYABLE_OVERDUE_SUB) {
                    detail.setBusinessNumber(220000281633L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_EXCEPTION) {
                    detail.setBusinessNumber(220000281634L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_PRESCRIPTION) {
                    detail.setBusinessNumber(220000281635L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE) {
                    detail.setBusinessNumber(210000021662L);
                }
                if (detail.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_DEBT || detail.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OIL_DEBT || detail.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC_DEBT) {
                    detail.setSubjectsId(EnumConsts.SubjectIds.CANCEL_ORDER_OWN_DEBT);
                }
                //油卡押金写死符号
                //油卡抵押、平台服务费(扣减)
                if (EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN == detail.getSubjectsId() || EnumConsts.SubjectIds.OIL_CARD_PLEDGE_MARGIN == detail.getSubjectsId()
                        || EnumConsts.SubjectIds.SUBJECTIDS1800 == detail.getSubjectsId() || EnumConsts.SubjectIds.SUBJECTIDS50070 == detail.getSubjectsId()
                        || EnumConsts.SubjectIds.SUBJECTIDS1804 == detail.getSubjectsId() || EnumConsts.SubjectIds.SUBJECTIDS50072 == detail.getSubjectsId()
                        || EnumConsts.SubjectIds.SUBJECTIDS1802 == detail.getSubjectsId() || EnumConsts.SubjectIds.SUBJECTIDS1806 == detail.getSubjectsId()
                        || EnumConsts.SubjectIds.SUBJECTIDS1808 == detail.getSubjectsId()) {
                    detail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                }
                //油卡返还
                if (EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_OUT == detail.getSubjectsId() || EnumConsts.SubjectIds.OIL_CARD_RELEASE_MARGIN == detail.getSubjectsId() || EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN == detail.getSubjectsId()) {
                    detail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                }
            }
        }
        Map<String, AccountDetails> detailMap = new HashMap<String, AccountDetails>();
        List<AppAccountDetailsOutDto> appList = new ArrayList<AppAccountDetailsOutDto>();
        List<AccountDetails> countList = new ArrayList<AccountDetails>();
        Map<String, Object> countMap = new HashMap<String, Object>();
        countList.addAll(list);
        for (AccountDetails detail : list) {
            if (detail.getAmount() == 0) {
                continue;
            }
            String accountDetailsId = detail.getId().toString();
            if (!detailMap.containsKey(accountDetailsId)) {
                Long sumAmount = 0L;
                Long tempSumAmout = 0L;
                Long tempSumAmout1 = 0L;
                String detailBusinessNumber = detail.getBusinessNumber().toString();
                LocalDateTime localDateTime = detail.getCreateTime();
                Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                long detailCreateTime = date.getTime();
                long detailSubjectsId = detail.getSubjectsId();
                String detailSoNbr = detail.getSoNbr().toString();
                String accountDatailsId = detail.getId().toString();
                AppAccountDetailsOutDto appDetail = new AppAccountDetailsOutDto();
                appDetail.setShowType("0");
                List<SubjectsOutDto> subList = new ArrayList<>();
                Iterator<AccountDetails> it = countList.iterator();
                while (it.hasNext()) {
                    AccountDetails tempDetail = it.next();
                    if (tempDetail.getAmount() == 0) {
                        continue;
                    }
                    String tempBusinessNumber = tempDetail.getBusinessNumber().toString();
                    long tempSubjectsId = tempDetail.getSubjectsId();
                    String tempSoNbr = tempDetail.getSoNbr().toString();
                    String tempAccountDatailsId = tempDetail.getId().toString();
                    LocalDateTime localDate = tempDetail.getCreateTime();
                    Date time = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
                    long tempCreateTime = time.getTime();

                    /** 违章费用流水
                     50070 支付违章费用
                     1804 逾期(司机违章费用)
                     1805 应收逾期(司机违章费用)

                     50072 支付违章服务费用
                     1803 应收逾期(违章代缴服务费)
                     1802 应付逾期(违章代缴服务费)

                     1806 应付逾期(违章代缴服务费退还)
                     1807 应收逾期(违章代缴服务费退还)
                     1808 应付逾期(违章代缴费退还)
                     1809 应收逾期(违章代缴费退还)
                     */
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber)
                            && (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.VIOLATION_FEE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE)))) {
                        if ((EnumConsts.SubjectIds.SUBJECTIDS50070 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50071 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS1804 == detailSubjectsId
                                || EnumConsts.SubjectIds.SUBJECTIDS1805 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50072 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50073 == detailSubjectsId
                                || EnumConsts.SubjectIds.SUBJECTIDS1803 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS1802 == detailSubjectsId
                                || EnumConsts.SubjectIds.SUBJECTIDS1806 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS1807 == detailSubjectsId
                                || EnumConsts.SubjectIds.SUBJECTIDS1808 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS1809 == detailSubjectsId)
                                && (EnumConsts.SubjectIds.SUBJECTIDS50070 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50071 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS1804 == tempSubjectsId
                                || EnumConsts.SubjectIds.SUBJECTIDS1805 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50072 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50073 == tempSubjectsId
                                || EnumConsts.SubjectIds.SUBJECTIDS1803 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS1802 == tempSubjectsId
                                || EnumConsts.SubjectIds.SUBJECTIDS1806 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS1807 == tempSubjectsId
                                || EnumConsts.SubjectIds.SUBJECTIDS1808 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS1809 == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }

                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber)
                            && (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.BEFORE_PAY_CODE))
                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.UPDATE_THE_ORDER))
                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CANCEL_THE_ORDER))
                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH))
                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.FORCE_ZHANG_PING))
                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL))
                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL))
                    )) {
                        if ((
                                EnumConsts.SubjectIds.OIL_FEE_PRESTORE_LOCK_1664 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_LOCK_1665 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1666 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1667 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1668 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1669 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1670 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1671 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_LOCK_1672 == detailSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1673 == detailSubjectsId
                        )
                                && (
                                EnumConsts.SubjectIds.OIL_FEE_PRESTORE_LOCK_1664 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_LOCK_1665 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1666 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1667 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1668 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1669 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1670 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1671 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_LOCK_1672 == tempSubjectsId
                                        || EnumConsts.SubjectIds.OIL_FEE_PRESTORE_RELEASE_1673 == tempSubjectsId

                        )) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }

                    //支付预付款
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.BEFORE_PAY_CODE))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) && (EnumConsts.SubjectIds.BEFORE_PAY_SUB == detailSubjectsId
                                || EnumConsts.SubjectIds.BEFORE_Virtual_SUB == detailSubjectsId || EnumConsts.SubjectIds.BEFORE_Entity_SUB == detailSubjectsId
                                || EnumConsts.SubjectIds.BEFORE_OILPAY_SUB == detailSubjectsId || EnumConsts.SubjectIds.BEFORE_ETC_SUB == detailSubjectsId
                                || EnumConsts.SubjectIds.BEFOREPAY_MASTER_SUBSIDY == detailSubjectsId || EnumConsts.SubjectIds.BEFOREPAY_SLAVE_SUBSIDY == detailSubjectsId
                                || EnumConsts.SubjectIds.BEFORE_ENTIY_OIL_FEE == detailSubjectsId || EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE == detailSubjectsId
                                || EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB == detailSubjectsId || EnumConsts.SubjectIds.BEFOREPAY_BRIDGE == detailSubjectsId)
                                && (EnumConsts.SubjectIds.BEFORE_PAY_SUB == tempSubjectsId || EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB == tempSubjectsId
                                || EnumConsts.SubjectIds.BEFORE_Virtual_SUB == tempSubjectsId || EnumConsts.SubjectIds.BEFORE_Entity_SUB == tempSubjectsId
                                || EnumConsts.SubjectIds.BEFORE_OILPAY_SUB == tempSubjectsId || EnumConsts.SubjectIds.BEFORE_ETC_SUB == tempSubjectsId
                                || EnumConsts.SubjectIds.BEFOREPAY_MASTER_SUBSIDY == tempSubjectsId || EnumConsts.SubjectIds.BEFOREPAY_SLAVE_SUBSIDY == tempSubjectsId
                                || EnumConsts.SubjectIds.BEFORE_ENTIY_OIL_FEE == tempSubjectsId || EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE == tempSubjectsId || EnumConsts.SubjectIds.BEFOREPAY_BRIDGE == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                            it.remove();
                        }
                        //油卡抵押
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) &&
                                (EnumConsts.SubjectIds.PLEDEG_OIL_CARD_TYPE1 == detailSubjectsId && EnumConsts.SubjectIds.PLEDEG_OIL_CARD_TYPE1 == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                            it.remove();
                        }
                        //油卡返还
                        if (tempDetail.getOrderId().equals(detail.getOrderId())
                                && (EnumConsts.SubjectIds.PLEDEG_OIL_CARD_TYPE2 == detailSubjectsId && EnumConsts.SubjectIds.PLEDEG_OIL_CARD_TYPE2 == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                            it.remove();
                        }
                        //预付信息费
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) && (EnumConsts.SubjectIds.BROKER_BEFORE_PAY == detailSubjectsId) && (EnumConsts.SubjectIds.BROKER_BEFORE_PAY == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                            it.remove();
                        }
                    }

                    //实体油卡支出(预付款)
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(210000021520L)) && EnumConsts.SubjectIds.CONSUME_BEFORE_ENTIY_OIL_FEE == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //油卡实体金支出（预付款）
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(210000021519L)) && EnumConsts.SubjectIds.CONSUME_BEFORE_Entity_SUB == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //油账户分配(预付款)
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(210000021521L)) && EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //平台服务费(扣减)
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && EnumConsts.SubjectIds.SUBJECTIDS1800 == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //付款申请
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber)
                            && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_MANGER))) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(tempDetail.getCostType());
                        it.remove();
                    }
                    //时效罚款(支付尾款)
//                    public static final long PRESCRIPTION_FINE=1522L;
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(210000141522L)) && EnumConsts.SubjectIds.PRESCRIPTION_FINE == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }

                    //到付款  大类： 22000028  小类：1632（到付款金额）、1634（异常罚款）、1635（时效罚款）、1633（应付逾期(到付款)）
                    //到付款-金额
                    if (tempAccountDatailsId.equals(accountDatailsId) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(210000021632L))
                            && EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                        it.remove();
                    }
                    //到付款-时效罚款
                    if (tempAccountDatailsId.equals(accountDatailsId) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(220000281633L))
                            && EnumConsts.SubjectIds.ARRIVE_CHARGE_PAYABLE_OVERDUE_SUB == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //到付款-异常罚款
                    if (tempAccountDatailsId.equals(accountDatailsId) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(220000281634L))
                            && EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_EXCEPTION == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //到付款-时效罚款
                    if (tempAccountDatailsId.equals(accountDatailsId) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(220000281635L))
                            && EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_PRESCRIPTION == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //到付款-开票总运费
                    if (tempAccountDatailsId.equals(accountDatailsId) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(210000021662L))
                            && EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE_RECEIVABLE == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                        it.remove();
                    }

                    //异常扣减要单独一条流水
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(210000141082L)) && EnumConsts.SubjectIds.EXCEPTION_FINE_FEE == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //油卡支出要单独一条流水
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(210000253516L))
                            && EnumConsts.SubjectIds.OIL_TURN_ENTITY_CARD_OUT == tempSubjectsId) {
                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //撤单订单回退金额
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CANCEL_THE_ORDER))) {
                        if (EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_PAYABLE_IN == detailSubjectsId || EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_PAYABLE_IN == tempSubjectsId) {
                            tempDetail.setAmount(-tempDetail.getAmount());
                            tempDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        }

                        sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        it.remove();
                    }
                    //异常罚款
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.EXCEPTION_FEE_OUT))) {
                        if ((EnumConsts.SubjectIds.EXCEPTION_FEE_OUT == detailSubjectsId) && (EnumConsts.SubjectIds.EXCEPTION_FEE_OUT == tempSubjectsId)) {
                            sumAmount = (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                        } else {
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                            it.remove();
                        }
                    }
                    //抵押释放油卡金额
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD))) {
                        //油卡抵押
                        if (tempDetail.getOrderId().equals(detail.getOrderId())
                                && (EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN == detailSubjectsId || EnumConsts.SubjectIds.OIL_CARD_PLEDGE_MARGIN == detailSubjectsId)
                                && (EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN == tempSubjectsId || EnumConsts.SubjectIds.OIL_CARD_PLEDGE_MARGIN == tempSubjectsId)
                        ) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                            it.remove();
                        }
                        //油卡返还
                        if (tempDetail.getOrderId().equals(detail.getOrderId())
                                && (EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_OUT == detailSubjectsId || EnumConsts.SubjectIds.OIL_CARD_RELEASE_MARGIN == detailSubjectsId
                                || EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN == detailSubjectsId)
                                && (EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_OUT == tempSubjectsId || EnumConsts.SubjectIds.OIL_CARD_RELEASE_MARGIN == tempSubjectsId
                                || EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN == tempSubjectsId)
                        ) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                            it.remove();
                        }

                    }

                    //尾款
                    if (tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FINAL))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId())
                                && (EnumConsts.SubjectIds.FINAL_CHARGE == detailSubjectsId || EnumConsts.SubjectIds.INSURANCE_SUB == detailSubjectsId)
                                && (EnumConsts.SubjectIds.FINAL_CHARGE == tempSubjectsId || EnumConsts.SubjectIds.INSURANCE_SUB == tempSubjectsId)
                        ) {
                            if (EnumConsts.SubjectIds.FINAL_CHARGE == tempSubjectsId) {
                                sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            } else {
                                tempSumAmout += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            }
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                            it.remove();
                        }
                        //尾款信息费
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) && (EnumConsts.SubjectIds.BROKER_FINAL_PAY == detailSubjectsId) && (EnumConsts.SubjectIds.BROKER_FINAL_PAY == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }
                    //异常补偿
                    if (tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.EXCEPTION_FEE))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) &&
                                (((EnumConsts.SubjectIds.EXCEPTION_FEE == detailSubjectsId) && (EnumConsts.SubjectIds.EXCEPTION_FEE == tempSubjectsId))
                                        || ((EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB == detailSubjectsId) && (EnumConsts.SubjectIds.EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB == tempSubjectsId)))
                        ) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }

                    //平安账户资金转移
                    if (tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PINGAN_ACCOUNT_TURN_CASH))) {
                        if (tempDetail.getSoNbr().equals(detail.getSoNbr())
                                && (EnumConsts.SubjectIds.RECEIVABLES_ACCOUNT_REDUCE_CASH == detailSubjectsId || EnumConsts.SubjectIds.PAYMENT_ACCOUNT_REDUCE_CASH == detailSubjectsId
                                || EnumConsts.SubjectIds.RECEIVABLES_ACCOUNT_ADD_CASH == detailSubjectsId || EnumConsts.SubjectIds.PAYMENT_ACCOUNT_ADD_CASH == detailSubjectsId)
                                && (EnumConsts.SubjectIds.RECEIVABLES_ACCOUNT_REDUCE_CASH == tempSubjectsId || EnumConsts.SubjectIds.PAYMENT_ACCOUNT_REDUCE_CASH == tempSubjectsId
                                || EnumConsts.SubjectIds.RECEIVABLES_ACCOUNT_ADD_CASH == tempSubjectsId || EnumConsts.SubjectIds.PAYMENT_ACCOUNT_ADD_CASH == tempSubjectsId)
                        ) {
                            if (EnumConsts.SubjectIds.RECEIVABLES_ACCOUNT_REDUCE_CASH == tempDetail.getSubjectsId() || EnumConsts.SubjectIds.PAYMENT_ACCOUNT_REDUCE_CASH == tempDetail.getSubjectsId()) {
                                sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            }
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE3));
                            it.remove();
                        }
                    }

                    //修改订单
                    if ((tempCreateTime >= (detailCreateTime - 20 * 1000)) && (tempCreateTime <= (detailCreateTime + 20 * 1000)) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.UPDATE_ORDER_PRICE))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) && (EnumConsts.SubjectIds.CASH_UPDATE_ORDER_MINIFY == detailSubjectsId
                                || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_LARGEN == detailSubjectsId || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_DEPT == detailSubjectsId
                                || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ENTITY_OIL_MINIFY == detailSubjectsId || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ENTITY_OIL_LARGEN == detailSubjectsId
                                || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_VIRTUAL_OIL_MINIFY == detailSubjectsId || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_VIRTUAL_OIL_LARGEN == detailSubjectsId
                                || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ETC_MINIFY == detailSubjectsId || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ETC_LARGEN == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUAL_OIL_ARREARS == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ETC_ARREARS == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_CASH_ARREARS == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PONTAGE_MINIFY == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PONTAGE_LARGEN == detailSubjectsId)
                                && (EnumConsts.SubjectIds.CASH_UPDATE_ORDER_MINIFY == tempSubjectsId
                                || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_LARGEN == tempSubjectsId || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_DEPT == tempSubjectsId
                                || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ENTITY_OIL_MINIFY == tempSubjectsId || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ENTITY_OIL_LARGEN == tempSubjectsId
                                || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_VIRTUAL_OIL_MINIFY == tempSubjectsId || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_VIRTUAL_OIL_LARGEN == tempSubjectsId
                                || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ETC_MINIFY == tempSubjectsId || EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ETC_LARGEN == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUAL_OIL_ARREARS == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ETC_ARREARS == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_CASH_ARREARS == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PONTAGE_MINIFY == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PONTAGE_LARGEN == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }

                    //修改订单(新)
                    if (tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.UPDATE_THE_ORDER))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) && tempDetail.getSoNbr().equals(detail.getSoNbr())
                                && (EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_ETC_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ETC_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW == detailSubjectsId || EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP_OUT == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP_OUT == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP == detailSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_UPP == detailSubjectsId)
                                && (EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_ETC_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ETC_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW == tempSubjectsId || EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP_OUT == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP_OUT == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW == tempSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP == tempSubjectsId
                                || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_UPP == tempSubjectsId)) {
                            if (EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW == detailSubjectsId || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW == tempSubjectsId) {
                                tempDetail.setAmount(-Math.abs(tempDetail.getAmount()));
                            }
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //ETC消费
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CONSUME_ETC_CODE))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId())
                                && (EnumConsts.SubjectIds.ETC_CONSUME_FEE == detailSubjectsId
                                || EnumConsts.SubjectIds.ORDER_ETC_CONSUME_FEE == detailSubjectsId || EnumConsts.SubjectIds.ADVANCE_BEFORE_ETC_CONSUME_FEE == detailSubjectsId || EnumConsts.SubjectIds.ETC_ARREARS_FEE == detailSubjectsId
                                || EnumConsts.SubjectIds.ARREARS_ETC_CONSUME_FEE == detailSubjectsId || EnumConsts.SubjectIds.ORDER_CASH_CONSUME_FEE == detailSubjectsId || EnumConsts.SubjectIds.POUNDAGE_SUB == detailSubjectsId)
                                && (EnumConsts.SubjectIds.ETC_CONSUME_FEE == tempSubjectsId
                                || EnumConsts.SubjectIds.ORDER_ETC_CONSUME_FEE == tempSubjectsId || EnumConsts.SubjectIds.ADVANCE_BEFORE_ETC_CONSUME_FEE == tempSubjectsId || EnumConsts.SubjectIds.ETC_ARREARS_FEE == tempSubjectsId
                                || EnumConsts.SubjectIds.ARREARS_ETC_CONSUME_FEE == tempSubjectsId || EnumConsts.SubjectIds.ORDER_CASH_CONSUME_FEE == tempSubjectsId || EnumConsts.SubjectIds.POUNDAGE_SUB == tempSubjectsId)) {
                            if (EnumConsts.SubjectIds.ETC_ARREARS_FEE == tempDetail.getSubjectsId()) {
                                sumAmount -= (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            } else {
                                sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            }
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //支付预付款时ETC充值消费
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ETC_PAY_CODE))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId())
                                && (EnumConsts.SubjectIds.ETC_PAY_QIAN_TONG_OUT == detailSubjectsId || EnumConsts.SubjectIds.ETC_PAY_QIAN_TONG_DEBT == detailSubjectsId)
                                && (EnumConsts.SubjectIds.ETC_PAY_QIAN_TONG_OUT == tempSubjectsId || EnumConsts.SubjectIds.ETC_PAY_QIAN_TONG_DEBT == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }
                    //加油消费
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FOR_OIL_CODE))) {
                        if ((EnumConsts.SubjectIds.CONSUME_OIL_SUB == detailSubjectsId || EnumConsts.SubjectIds.BUY_OIL_SUB == detailSubjectsId
                                || EnumConsts.SubjectIds.CONSUME_OIL_CASHBACK == detailSubjectsId || EnumConsts.SubjectIds.CONSUME_OIL == detailSubjectsId
                                || EnumConsts.SubjectIds.DISCOUNT_FEE == detailSubjectsId || EnumConsts.SubjectIds.BUY_OIL_DISCOUNT == detailSubjectsId
                                || EnumConsts.SubjectIds.BALANCE_CONSUME_OIL_SUB == detailSubjectsId || EnumConsts.SubjectIds.CONSUME_RECHARGE_OIL_SUB == detailSubjectsId)
                                && (EnumConsts.SubjectIds.CONSUME_OIL_SUB == tempSubjectsId || EnumConsts.SubjectIds.BUY_OIL_SUB == tempSubjectsId
                                || EnumConsts.SubjectIds.CONSUME_OIL_CASHBACK == tempSubjectsId || EnumConsts.SubjectIds.CONSUME_OIL == tempSubjectsId
                                || EnumConsts.SubjectIds.BALANCE_CONSUME_OIL_SUB == tempSubjectsId || EnumConsts.SubjectIds.CONSUME_RECHARGE_OIL_SUB == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }

                    //油账户充值
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL))) {
                        if ((EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_FEE == detailSubjectsId || EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_ALLOT == detailSubjectsId || EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_FEE_1687 == detailSubjectsId)
                                && (EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_FEE == tempSubjectsId || EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_ALLOT == tempSubjectsId || EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_FEE_1687 == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }
                    //油账户清零
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL))) {
                        if ((EnumConsts.SubjectIds.CLEAR_RECHARGE_ACCOUNT_OIL_FEE == detailSubjectsId || EnumConsts.SubjectIds.CLEAR_ORDER_ACCOUNT_OIL_FEE == detailSubjectsId
                                || EnumConsts.SubjectIds.CLEAR_ACCOUNT_OIL_ALLOT == detailSubjectsId)
                                && (EnumConsts.SubjectIds.CLEAR_RECHARGE_ACCOUNT_OIL_FEE == tempSubjectsId || EnumConsts.SubjectIds.CLEAR_ORDER_ACCOUNT_OIL_FEE == tempSubjectsId
                                || EnumConsts.SubjectIds.CLEAR_ACCOUNT_OIL_ALLOT == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                            it.remove();
                        }
                    }
                    //预支
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE))) {
                        if ((EnumConsts.SubjectIds.BEFOREPAY_SUB == detailSubjectsId || EnumConsts.SubjectIds.COLLECT_IN_ADVANCE == detailSubjectsId
                                || EnumConsts.SubjectIds.POUNDAGE_SUB == detailSubjectsId || EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN == detailSubjectsId)
                                && (EnumConsts.SubjectIds.BEFOREPAY_SUB == tempSubjectsId || EnumConsts.SubjectIds.COLLECT_IN_ADVANCE == tempSubjectsId
                                || EnumConsts.SubjectIds.POUNDAGE_SUB == tempSubjectsId || EnumConsts.SubjectIds.ADVANCE_PAY_RECEIVABLE_IN == tempSubjectsId)) {
                            if (EnumConsts.SubjectIds.BEFOREPAY_SUB == tempSubjectsId) {
                                sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            }
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE3));
                            it.remove();
                        }
                    }
                    //托管出账
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.TRANSFER_TRUST_CODE))) {
                        if ((EnumConsts.SubjectIds.TRANSFER_TRUST_SUB == detailSubjectsId) && (EnumConsts.SubjectIds.TRANSFER_TRUST_SUB == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }
                    //托管入账
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ENTRUST_CODE))) {
                        if ((EnumConsts.SubjectIds.ENTRUST_SUB == detailSubjectsId) && (EnumConsts.SubjectIds.ENTRUST_SUB == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }
                    //提现
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.WITHDRAWALS_CODE))) {
                        if ((EnumConsts.SubjectIds.WITHDRAWALS_SUB == detailSubjectsId || EnumConsts.SubjectIds.WITHDRAWALS_AUTO == detailSubjectsId)
                                && (EnumConsts.SubjectIds.WITHDRAWALS_SUB == tempSubjectsId || EnumConsts.SubjectIds.WITHDRAWALS_AUTO == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }
                    //回退提现款
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.BACK_RECHARGE))) {
                        if ((EnumConsts.SubjectIds.BACK_RECHARGE == detailSubjectsId) && (EnumConsts.SubjectIds.BACK_RECHARGE == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(tempDetail.getCostType());
                            it.remove();
                        }
                    }
                    //未到期转可用
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAYFOR_CODE))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId())
                                && (EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_REDUCE == detailSubjectsId || EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_ADD == detailSubjectsId
                                || EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN == detailSubjectsId || EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVED_IN == detailSubjectsId
                                || EnumConsts.SubjectIds.MARGIN_TURN_CASH_BACK_4016 == detailSubjectsId)
                                && (EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_REDUCE == tempSubjectsId || EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_ADD == tempSubjectsId
                                || EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN == tempSubjectsId || EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVED_IN == tempSubjectsId
                                || EnumConsts.SubjectIds.MARGIN_TURN_CASH_BACK_4016 == tempSubjectsId)) {
                            if (EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_ADD == tempSubjectsId || EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN == tempSubjectsId || EnumConsts.SubjectIds.MARGIN_TURN_CASH_BACK_4016 == tempSubjectsId) {
                                sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            }
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE3));
                            it.remove();
                        }
                    }
                    //借支报销
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) &&
                            (
                                    detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OA_LOAN_AVAILABLE))
                                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE))
                                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.DRIVER_EXPENSE_ABLE))
                                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.TUBE_EXPENSE_ABLE))
                            )
                    ) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) &&
                                (EnumConsts.SubjectIds.OA_LOAN_RECEIVABLE == detailSubjectsId
                                        || EnumConsts.SubjectIds.EXPENSE_RECEIVABLE == detailSubjectsId
                                        || EnumConsts.SubjectIds.OA_LOAN_TUBE_RECEIVABLE == detailSubjectsId
                                        || EnumConsts.SubjectIds.EXPENSE_TUBE_RECEIVABLE == detailSubjectsId)
                                && (EnumConsts.SubjectIds.OA_LOAN_RECEIVABLE == tempSubjectsId
                                || EnumConsts.SubjectIds.EXPENSE_RECEIVABLE == tempSubjectsId
                                || EnumConsts.SubjectIds.OA_LOAN_TUBE_RECEIVABLE == tempSubjectsId
                                || EnumConsts.SubjectIds.EXPENSE_TUBE_RECEIVABLE == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                            it.remove();
                        }
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) &&
                                (EnumConsts.SubjectIds.OA_LOAN_HANDLE == detailSubjectsId
                                        || EnumConsts.SubjectIds.OA_LOAN_TUBE_HANDLE == detailSubjectsId
                                        || EnumConsts.SubjectIds.EXPENSE_HANDLE == detailSubjectsId
                                        || EnumConsts.SubjectIds.EXPENSE_TUBE_HANDLE == detailSubjectsId)
                                && (EnumConsts.SubjectIds.OA_LOAN_HANDLE == tempSubjectsId
                                || EnumConsts.SubjectIds.OA_LOAN_TUBE_HANDLE == tempSubjectsId
                                || EnumConsts.SubjectIds.EXPENSE_HANDLE == tempSubjectsId
                                || EnumConsts.SubjectIds.EXPENSE_TUBE_HANDLE == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                            it.remove();
                        }
                    }
                    //维修保养消费
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FOR_REPAIR))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId())
                                && (EnumConsts.SubjectIds.BALANCE_PAY_FOR_REPAIR_SUB == detailSubjectsId || EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR == detailSubjectsId)
                                && (EnumConsts.SubjectIds.BALANCE_PAY_FOR_REPAIR_SUB == tempSubjectsId || EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(detail.getCostType());
                            it.remove();
                        }
                    }
                    //服务商维修保养收入
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.INCOME_REPAIR))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) && (EnumConsts.SubjectIds.REPAIR_FEE_MARGIN == detailSubjectsId) && (EnumConsts.SubjectIds.REPAIR_FEE_MARGIN == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(detail.getCostType());
                            it.remove();
                        }
                    }
                    //油和ETC转现(功能化)
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH))) {
                        if ((EnumConsts.SubjectIds.OIL_TURN_CASH_OIL == detailSubjectsId || EnumConsts.SubjectIds.OIL_TURN_CASH_CASH == detailSubjectsId
                                || EnumConsts.SubjectIds.OIL_TURN_CASH_SERVICE == detailSubjectsId || EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL == detailSubjectsId
                                || EnumConsts.SubjectIds.OIL_TURN_ENTITY_CARD == detailSubjectsId || EnumConsts.SubjectIds.OIL_TURN_ENTITY_SERVICE == detailSubjectsId
                                || EnumConsts.SubjectIds.ETC_TURN_CASH_ETC == detailSubjectsId || EnumConsts.SubjectIds.ETC_TURN_CASH_CASH == detailSubjectsId
                                || EnumConsts.SubjectIds.ETC_TURN_CASH_SERVICE == detailSubjectsId || EnumConsts.SubjectIds.TURN_CASH_DEDUCTIBLE_LOAN_OIL == detailSubjectsId
                                || EnumConsts.SubjectIds.TURN_ENTITY_DEDUCTIBLE_LOAN_OIL == detailSubjectsId || EnumConsts.SubjectIds.OIL_TURN_CASH_DEDUCTIBLE_MARGIN == detailSubjectsId
                                || EnumConsts.SubjectIds.OIL_TURN_ENTITY_DEDUCTIBLE_MARGIN == detailSubjectsId || EnumConsts.SubjectIds.ETC_TURN_CASH_DEDUCTIBLE_MARGIN == detailSubjectsId
                                || EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN == detailSubjectsId || EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN == detailSubjectsId)
                                && (EnumConsts.SubjectIds.OIL_TURN_CASH_OIL == tempSubjectsId || EnumConsts.SubjectIds.OIL_TURN_CASH_CASH == tempSubjectsId
                                || EnumConsts.SubjectIds.OIL_TURN_CASH_SERVICE == tempSubjectsId || EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL == tempSubjectsId
                                || EnumConsts.SubjectIds.OIL_TURN_ENTITY_CARD == tempSubjectsId || EnumConsts.SubjectIds.OIL_TURN_ENTITY_SERVICE == tempSubjectsId
                                || EnumConsts.SubjectIds.ETC_TURN_CASH_ETC == tempSubjectsId || EnumConsts.SubjectIds.ETC_TURN_CASH_CASH == tempSubjectsId
                                || EnumConsts.SubjectIds.ETC_TURN_CASH_SERVICE == tempSubjectsId || EnumConsts.SubjectIds.TURN_CASH_DEDUCTIBLE_LOAN_OIL == tempSubjectsId
                                || EnumConsts.SubjectIds.TURN_ENTITY_DEDUCTIBLE_LOAN_OIL == tempSubjectsId || EnumConsts.SubjectIds.OIL_TURN_CASH_DEDUCTIBLE_MARGIN == tempSubjectsId
                                || EnumConsts.SubjectIds.OIL_TURN_ENTITY_DEDUCTIBLE_MARGIN == tempSubjectsId || EnumConsts.SubjectIds.ETC_TURN_CASH_DEDUCTIBLE_MARGIN == tempSubjectsId
                                || EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN == tempSubjectsId || EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN == tempSubjectsId)) {
                            if (EnumConsts.SubjectIds.OIL_TURN_CASH_OIL == tempSubjectsId || EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL == tempSubjectsId
                                    || EnumConsts.SubjectIds.ETC_TURN_CASH_ETC == tempSubjectsId) {
                                sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            }
                            if (EnumConsts.SubjectIds.OIL_TURN_CASH_SERVICE == tempSubjectsId || EnumConsts.SubjectIds.ETC_TURN_CASH_SERVICE == tempSubjectsId) {
                                tempSumAmout += Math.abs((tempDetail.getAmount() == null ? 0L : tempDetail.getAmount()));
                            }
                            if (EnumConsts.SubjectIds.OIL_TURN_CASH_CASH == tempSubjectsId || EnumConsts.SubjectIds.ETC_TURN_CASH_CASH == tempSubjectsId) {
                                tempSumAmout1 += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            }
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE3));
                            it.remove();
                        }
                    }
                    //核销借支
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OA_LOAN_VERIFICATION))) {
                        if (tempDetail.getOrderId().equals(detail.getOrderId()) && (EnumConsts.SubjectIds.OA_LOAN_VERIFICATION == detailSubjectsId) && (EnumConsts.SubjectIds.OA_LOAN_VERIFICATION == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE3));
                            it.remove();
                        }
                    }
                    //平账
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.FORCE_ZHANG_PING))) {
                        if ((EnumConsts.SubjectIds.ZHANG_PING_BALANCE_IN == detailSubjectsId || EnumConsts.SubjectIds.ZHANG_PING_BALANCE_OUT == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHANG_PING_OIL_IN == detailSubjectsId || EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHANG_PING_ETC_IN == detailSubjectsId || EnumConsts.SubjectIds.ZHANG_PING_ETC_OUT == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHANG_PING_MARGIN_IN == detailSubjectsId || EnumConsts.SubjectIds.ZHANG_PING_MARGIN_OUT == detailSubjectsId
                                || EnumConsts.SubjectIds.SELECT_ZHANG_PING_MARGIN_IN == detailSubjectsId || EnumConsts.SubjectIds.SELECT_ZHANG_PING_MARGIN_OUT == detailSubjectsId)
                                && (EnumConsts.SubjectIds.ZHANG_PING_BALANCE_IN == tempSubjectsId || EnumConsts.SubjectIds.ZHANG_PING_BALANCE_OUT == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHANG_PING_OIL_IN == tempSubjectsId || EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHANG_PING_ETC_IN == tempSubjectsId || EnumConsts.SubjectIds.ZHANG_PING_ETC_OUT == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHANG_PING_MARGIN_IN == tempSubjectsId || EnumConsts.SubjectIds.ZHANG_PING_MARGIN_OUT == tempSubjectsId
                                || EnumConsts.SubjectIds.SELECT_ZHANG_PING_MARGIN_IN == tempSubjectsId || EnumConsts.SubjectIds.SELECT_ZHANG_PING_MARGIN_OUT == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //招商车挂靠车对账单
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ACCOUNT_STATEMENT))) {
                        if ((EnumConsts.SubjectIds.ACCOUNT_STATEMENT_DEDUCTION == detailSubjectsId || EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE == detailSubjectsId || EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == detailSubjectsId
                        ) && (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_DEDUCTION == tempSubjectsId || EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE == tempSubjectsId || EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == tempSubjectsId)) {
                            appDetail.setCostType(detail.getCostType());
                            if (EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE == detailSubjectsId) {
                                appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                            }
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //北斗缴费
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.BEIDOU_PAYMENT))) {
                        if (EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == detailSubjectsId && EnumConsts.SubjectIds.BEIDOU_PAYMENT_RECEIVABLE_IN == tempSubjectsId) {
                            appDetail.setCostType(detail.getCostType());
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //招商车扣费
                    if ((tempCreateTime >= (detailCreateTime - 60 * 1000)) && (tempCreateTime <= (detailCreateTime + 60 * 1000)) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ZHAOSHANG_FEE))) {
                        if ((EnumConsts.SubjectIds.ZHAOSHANG_FUELFILLING == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_RENT == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_ADMINISTRATIVE == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_REPAIR == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_VEHICLETRAVELTAX == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_TYRE == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_TRAILERCERTIFICATION == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_MOTORVEHICLES == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_TRAILERINSURANCEPREMIUM == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_ILLEGAL == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_SPARE1 == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_SPARE2 == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_SPARE3 == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_SPARE4 == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_SPARE5 == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_SPARE6 == detailSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_SPARE7 == detailSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_TRIPCOMPENSATE == detailSubjectsId)
                                && (EnumConsts.SubjectIds.ZHAOSHANG_FUELFILLING == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_RENT == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_ADMINISTRATIVE == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_REPAIR == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_VEHICLETRAVELTAX == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_TYRE == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_TRAILERCERTIFICATION == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_MOTORVEHICLES == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_TRAILERINSURANCEPREMIUM == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_ILLEGAL == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_SPARE1 == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_SPARE2 == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_SPARE3 == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_SPARE4 == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_SPARE5 == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_SPARE6 == tempSubjectsId
                                || EnumConsts.SubjectIds.ZHAOSHANG_SPARE7 == tempSubjectsId || EnumConsts.SubjectIds.ZHAOSHANG_TRIPCOMPENSATE == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //工资发放
                    if ((tempCreateTime >= (detailCreateTime - 60 * 1000)) && (tempCreateTime <= (detailCreateTime + 60 * 1000)) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CAR_DRIVER_SALARY))) {
                        appDetail.setShowType("2");
                        if ((EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION == detailSubjectsId || EnumConsts.SubjectIds.SALARY_EXCEPTION_DEDUCTION == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_INS_DEDUCTION == detailSubjectsId || EnumConsts.SubjectIds.SALARY_TAX_DEDUCTION == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_OTHER_ADD == detailSubjectsId || EnumConsts.SubjectIds.SALARY_OTHER_DEDUCTION == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_WAIT_DEDUCTION == detailSubjectsId || EnumConsts.SubjectIds.SALARY_SUB_DEDUCTION == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AGING_DEDUCTION == detailSubjectsId || EnumConsts.SubjectIds.SALARY_FUND_DEDUCTION == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_RISK_DEDUCTION == detailSubjectsId || EnumConsts.SubjectIds.SALARY_VIOLATION_DEDUCTION == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION3 == detailSubjectsId || EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION4 == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION6 == detailSubjectsId || EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION8 == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AWARD9 == detailSubjectsId || EnumConsts.SubjectIds.SALARY_AWARD10 == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AWARD11 == detailSubjectsId || EnumConsts.SubjectIds.SALARY_AWARD12 == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AWARD13 == detailSubjectsId || EnumConsts.SubjectIds.SALARY_AWARD14 == detailSubjectsId || EnumConsts.SubjectIds.SALARY_RECEIVABLE == detailSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AWARD15 == detailSubjectsId || EnumConsts.SubjectIds.SALARY_AWARD16 == detailSubjectsId)
                                && (EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION == tempSubjectsId || EnumConsts.SubjectIds.SALARY_EXCEPTION_DEDUCTION == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_INS_DEDUCTION == tempSubjectsId || EnumConsts.SubjectIds.SALARY_TAX_DEDUCTION == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_OTHER_ADD == tempSubjectsId || EnumConsts.SubjectIds.SALARY_OTHER_DEDUCTION == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_WAIT_DEDUCTION == tempSubjectsId || EnumConsts.SubjectIds.SALARY_SUB_DEDUCTION == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AGING_DEDUCTION == tempSubjectsId || EnumConsts.SubjectIds.SALARY_FUND_DEDUCTION == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_RISK_DEDUCTION == tempSubjectsId || EnumConsts.SubjectIds.SALARY_VIOLATION_DEDUCTION == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION3 == tempSubjectsId || EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION4 == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION6 == tempSubjectsId || EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION8 == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AWARD9 == tempSubjectsId || EnumConsts.SubjectIds.SALARY_AWARD10 == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AWARD11 == tempSubjectsId || EnumConsts.SubjectIds.SALARY_AWARD12 == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AWARD13 == tempSubjectsId || EnumConsts.SubjectIds.SALARY_AWARD14 == tempSubjectsId || EnumConsts.SubjectIds.SALARY_RECEIVABLE == tempSubjectsId
                                || EnumConsts.SubjectIds.SALARY_AWARD15 == tempSubjectsId || EnumConsts.SubjectIds.SALARY_AWARD16 == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //维修基金收入(发放工资)
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CAR_DRIVER_SALARY))) {
                        if ((EnumConsts.SubjectIds.REPAIR_FUND_SALARY == detailSubjectsId) && (EnumConsts.SubjectIds.REPAIR_FUND_SALARY == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(detail.getCostType());
                            it.remove();
                        }
                    }
                    //(工资提现)
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CAR_DRIVER_SALARY))) {
                        if ((EnumConsts.SubjectIds.SALARY_RECEIVED == detailSubjectsId) && (EnumConsts.SubjectIds.SALARY_RECEIVED == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(detail.getCostType());
                            it.remove();
                        }
                    }
                    //维修基金转现(提现)
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.REPAIR_FUND_TURN_CASH))) {
                        if ((EnumConsts.SubjectIds.REPAIR_FUND_WITHDRAWALS == detailSubjectsId) && (EnumConsts.SubjectIds.REPAIR_FUND_WITHDRAWALS == tempSubjectsId)) {
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            appDetail.setCostType(detail.getCostType());
                            it.remove();
                        }
                    }
                    //已付已撤回
                    if (tempSoNbr.equals(detailSoNbr) && tempBusinessNumber.equals(detailBusinessNumber) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_WITHDRAW))) {
                        if ((EnumConsts.SubjectIds.SUBJECTIDS50137 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50143 == detailSubjectsId)
                                && (EnumConsts.SubjectIds.SUBJECTIDS50137 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50143 == tempSubjectsId)) {
                            appDetail.setCostType(detail.getCostType());
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //已付已支付
                    if (tempSoNbr.equals(detailSoNbr) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_CASH))) {
                        if ((EnumConsts.SubjectIds.SUBJECTIDS50139 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50145 == detailSubjectsId)
                                && (EnumConsts.SubjectIds.SUBJECTIDS50139 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50145 == tempSubjectsId)) {
                            appDetail.setCostType(detail.getCostType());
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                    //已付待提现
                    if (tempSoNbr.equals(detailSoNbr) && detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_DRIVER))) {
                        if ((EnumConsts.SubjectIds.SUBJECTIDS50135 == detailSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50141 == detailSubjectsId)
                                && (EnumConsts.SubjectIds.SUBJECTIDS50135 == tempSubjectsId || EnumConsts.SubjectIds.SUBJECTIDS50141 == tempSubjectsId)) {
                            appDetail.setCostType(detail.getCostType());
                            sumAmount += (tempDetail.getAmount() == null ? 0L : tempDetail.getAmount());
                            this.countSubsOut(tempDetail, appDetail, subList, detailMap, countMap);
                            it.remove();
                        }
                    }
                }
                //AppAccountDetailsOut对象包装
                String codeTypeAlias = (String) countMap.get("codeTypeAlias");
                String tempOrderId = "";
                if (detail.getOrderId() != null) {
                    tempOrderId = detail.getOrderId();
                }
                appDetail.setOrderId(detail.getOrderId());
                appDetail.setAmount(sumAmount);
                appDetail.setBusinessNumber(detail.getBusinessNumber());
                appDetail.setSubList(subList);
                appDetail.setUserId(appAccountDetailsVo.getUserId());
                appDetail.setCreateTime(detail.getCreateTime());

                //提现subList合成一条
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.WITHDRAWALS_CODE))) {
                    appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                    if (subList.size() > 1) {
                        List<SubjectsOutDto> subListTemp = new ArrayList<>();
                        SubjectsOutDto out = subList.get(0);
                        out.setSubjectsAmount(sumAmount);
                        subListTemp.add(out);
                        appDetail.setSubList(subListTemp);
                    }
                }
                //油账户清零subList合成一条
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL))) {
                    appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                    if (subList.size() > 1) {
                        List<SubjectsOutDto> subListTemp = new ArrayList<>();
                        SubjectsOutDto out = subList.get(0);
                        out.setSubjectsAmount(sumAmount);
                        subListTemp.add(out);
                        appDetail.setSubList(subListTemp);
                    }
                }
                //撤销订单--即将到期、欠款  合成一条记录
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CANCEL_THE_ORDER))) {
                    if (subList.size() > 1) {
                        List<SubjectsOutDto> subListTemp = new ArrayList<>();
                        Iterator<SubjectsOutDto> subIt = subList.iterator();
                        SubjectsOutDto debtOut = new SubjectsOutDto();
                        Long debtAmount = 0L;
                        boolean hasBebt = false;

                        SubjectsOutDto ExpiringOut = new SubjectsOutDto();
                        Long ExpiringAmount = 0L;
                        boolean hasExpiring = false;
                        while (subIt.hasNext()) {
                            SubjectsOutDto subject = subIt.next();
                            //欠款
                            if (subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_IN
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC_PAYABLE_IN
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN) {
                                debtAmount += subject.getSubjectsAmount();
                                BeanUtils.copyProperties(subject, debtOut);
                                hasBebt = true;
                                //即将到期
                            } else if (subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_DEDUCTION
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_DEDUCTION
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC_DEDUCTION
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_DEDUCTION
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_DEDUCTION
                                    || subject.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_DEDUCTION) {
                                ExpiringAmount += subject.getSubjectsAmount();
                                BeanUtils.copyProperties(subject, ExpiringOut);
                                hasExpiring = true;
                            } else {
                                subListTemp.add(subject);
                            }
                        }
                        if (hasBebt) {
                            debtOut.setSubjectsAmount(debtAmount);
                            debtOut.setSubjectsName("欠款");
                            debtOut.setSubCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                            subListTemp.add(debtOut);
                        }
                        if (hasExpiring) {
                            ExpiringOut.setSubjectsAmount(ExpiringAmount);
                            ExpiringOut.setSubjectsName("即将到期");
                            ExpiringOut.setSubCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                            subListTemp.add(ExpiringOut);
                        }
                        appDetail.setSubList(subListTemp);
                    }
                }

                //尾款特殊处理
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FINAL))) {
                    for (SubjectsOutDto out : subList) {
                        if (out.getSubjectsId() == EnumConsts.SubjectIds.FINAL_CHARGE) {
                            out.setSubjectsAmount(out.getSubjectsAmount() + tempSumAmout);
                        }
                    }
                }
                //修改订单特殊处理
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.UPDATE_ORDER_PRICE))) {
                    Iterator<SubjectsOutDto> subIt = subList.iterator();
                    long amount = 0L;
                    while (subIt.hasNext()) {
                        SubjectsOutDto out = subIt.next();
                        long subjectsId = out.getSubjectsId();
                        if (subjectsId == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUAL_OIL_ARREARS || subjectsId == EnumConsts.SubjectIds.UPDATE_ORDER_ETC_ARREARS || subjectsId == EnumConsts.SubjectIds.UPDATE_ORDER_CASH_ARREARS) {
                            amount += out.getSubjectsAmount();
                            subIt.remove();
                        }
                    }
                    if (amount != 0L) {
                        SubjectsOutDto subjectsOut = new SubjectsOutDto();
                        subjectsOut.setSubjectsId(0L);
                        subjectsOut.setSubCostType(1);
                        subjectsOut.setSubjectsAmount(amount);
                        subjectsOut.setSubjectsName("未到期");
                        subList.add(subjectsOut);
                        //判断是否自有车
                        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(Long.parseLong(detail.getOrderId()));
                        Integer carUserType = null;
                        if (orderScheduler != null) {
                            carUserType = orderScheduler.getCarUserType();
                        } else {
                            OrderSchedulerH orderSchedulerh = orderSchedulerService.getOrderSchedulerH(Long.parseLong(detail.getOrderId()));
                            if (orderSchedulerh != null) {
                                carUserType = orderSchedulerh.getCarUserType();
                            }
                        }
                        if (carUserType != null && carUserType == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR) {
                            subjectsOut.setSubjectsName("工资");
                        }
                    }
                    if (sumAmount == 0L) {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE3));
                    } else if (sumAmount > 0L) {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                    } else {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                    }
                }

                //ETC消费特殊处理
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CONSUME_ETC_CODE))) {
                    appDetail.setAmount(sumAmount);
                    appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                    Iterator<SubjectsOutDto> subIt = subList.iterator();
                    long amount = 0L;
                    while (subIt.hasNext()) {
                        SubjectsOutDto out = subIt.next();
                        long subjectsId = out.getSubjectsId();
                        if (subjectsId == EnumConsts.SubjectIds.ETC_CONSUME_FEE) {
                            appDetail.setAmount(out.getSubjectsAmount());
                            subIt.remove();
                        }
                        if (subjectsId == EnumConsts.SubjectIds.ADVANCE_BEFORE_ETC_CONSUME_FEE || subjectsId == EnumConsts.SubjectIds.ARREARS_ETC_CONSUME_FEE) {
                            amount += out.getSubjectsAmount();
                            subIt.remove();
                        }
                    }
                    if (amount != 0L) {
                        SysStaticData sysStaticData = sysStaticDataService.getSysStaticData(codeType, String.valueOf(EnumConsts.SubjectIds.ADVANCE_BEFORE_ETC_CONSUME_FEE));
                        SubjectsOutDto subjectsOut = new SubjectsOutDto();
                        subjectsOut.setSubjectsId(EnumConsts.SubjectIds.ADVANCE_BEFORE_ETC_CONSUME_FEE);
                        subjectsOut.setSubCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        subjectsOut.setSubjectsAmount(amount);
                        if (sysStaticData != null) {
                            subjectsOut.setSubjectsName(sysStaticData.getCodeName());
                        }
                        subList.add(subjectsOut);
                    }
                }

                //加油消费特殊处理
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FOR_OIL_CODE))) {
                    appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                    Iterator<SubjectsOutDto> subIt = subList.iterator();
                    long oilAmount = 0L;
                    boolean hasOil = false;
                    SubjectsOutDto subjectsOut = new SubjectsOutDto();
                    while (subIt.hasNext()) {
                        SubjectsOutDto out = subIt.next();
                        long subjectsId = out.getSubjectsId();
                        if (subjectsId == EnumConsts.SubjectIds.CONSUME_OIL) {
                            appDetail.setAmount(out.getSubjectsAmount());
                            subIt.remove();
                        }
                        if (subjectsId == EnumConsts.SubjectIds.DISCOUNT_FEE || subjectsId == EnumConsts.SubjectIds.BUY_OIL_DISCOUNT) {
                            subIt.remove();
                        }
                        //加油消费同一笔的，显示一个油账户
                        if (out.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_OIL_SUB) {
                            oilAmount += out.getSubjectsAmount();
                            BeanUtils.copyProperties(out, subjectsOut);
                            hasOil = true;
                            subIt.remove();
                        }
                    }
                    if (hasOil) {
                        SysStaticData sysStaticData = sysStaticDataService.getSysStaticData("APP_ACCOUNT_DETAILS_OUT", EnumConsts.SubjectIds.CONSUME_OIL_SUB + "");
                        subjectsOut.setSubjectsId(EnumConsts.SubjectIds.CONSUME_OIL_SUB);
                        subjectsOut.setSubCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                        if (sysStaticData != null) {
                            subjectsOut.setSubjectsName(sysStaticData.getCodeName());
                        }
                        subjectsOut.setSubjectsAmount(oilAmount);
                        subList.add(subjectsOut);
                    }
                }
                //平账特殊处理
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.FORCE_ZHANG_PING))) {
                    if (sumAmount > 0L) {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                    } else {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                    }
                }
                //招商车扣费特殊处理
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ZHAOSHANG_FEE))) {
                    appDetail.setShowType("1");
                    if (sumAmount > 0L) {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                    } else {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                    }
                    SysStaticData sysStaticData = null;
                    Iterator<SubjectsOutDto> subIt = subList.iterator();
                    long fuelFilling = 0L;//加油
                    long rent = 0L;//租金
                    long adminisTrative = 0L;//管理服务费
                    long repair = 0L;//维修
                    long vehicleTravelTax = 0L;//车船税
                    long tyre = 0L;//轮胎
                    long trailerCertification = 0L;//挂车审验
                    long motorVehicles = 0L;//车辆审验
                    long trailerInsurancePremium = 0L;//挂车贷柜保险费
                    long illegal = 0L;//违章罚款
                    long spare1 = 0L;//挂车租赁费
                    long spare2 = 0L;//其他配件
                    long spare3 = 0L;//交强险
                    long spare4 = 0L;//商业险
                    long spare5 = 0L;//挂车车船税
                    long spare6 = 0L;//挂车轮胎磨损
                    long spare7 = 0L;//其他费用
                    long tripcompensate = 0L;//行程补偿
                    while (subIt.hasNext()) {
                        SubjectsOutDto out = subIt.next();
                        long subjectsId = out.getSubjectsId();
                        if (EnumConsts.SubjectIds.ZHAOSHANG_FUELFILLING == subjectsId) {
                            fuelFilling += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_RENT == subjectsId) {
                            rent += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_ADMINISTRATIVE == subjectsId) {
                            adminisTrative += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_REPAIR == subjectsId) {
                            repair += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_VEHICLETRAVELTAX == subjectsId) {
                            vehicleTravelTax += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_TYRE == subjectsId) {
                            tyre += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_TRAILERCERTIFICATION == subjectsId) {
                            trailerCertification += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_MOTORVEHICLES == subjectsId) {
                            motorVehicles += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_TRAILERINSURANCEPREMIUM == subjectsId) {
                            trailerInsurancePremium += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_ILLEGAL == subjectsId) {
                            illegal += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_SPARE1 == subjectsId) {
                            spare1 += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_SPARE2 == subjectsId) {
                            spare2 += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_SPARE3 == subjectsId) {
                            spare3 += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_SPARE4 == subjectsId) {
                            spare4 += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_SPARE5 == subjectsId) {
                            spare5 += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_SPARE6 == subjectsId) {
                            spare6 += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_SPARE7 == subjectsId) {
                            spare7 += out.getSubjectsAmount();
                        }
                        if (EnumConsts.SubjectIds.ZHAOSHANG_TRIPCOMPENSATE == subjectsId) {
                            tripcompensate += out.getSubjectsAmount();
                        }
                        subIt.remove();
                    }
                    SubjectsOutDto subjectsOut = new SubjectsOutDto();
                    subjectsOut.setSubjectsId(0L);
                    subjectsOut.setSubCostType(appDetail.getCostType());
                    subjectsOut.setSubjectsAmount(sumAmount);
                    subjectsOut.setSubjectsName("未到期(总)");
                    subList.add(subjectsOut);
                    if (fuelFilling != 0L) {
                        this.createSubjectsOut(fuelFilling, EnumConsts.SubjectIds.ZHAOSHANG_FUELFILLING, codeType, subList);
                    }
                    if (rent != 0L) {
                        this.createSubjectsOut(rent, EnumConsts.SubjectIds.ZHAOSHANG_RENT, codeType, subList);
                    }
                    if (adminisTrative != 0L) {
                        this.createSubjectsOut(adminisTrative, EnumConsts.SubjectIds.ZHAOSHANG_ADMINISTRATIVE, codeType, subList);
                    }
                    if (vehicleTravelTax != 0L) {
                        this.createSubjectsOut(vehicleTravelTax, EnumConsts.SubjectIds.ZHAOSHANG_VEHICLETRAVELTAX, codeType, subList);
                    }
                    if (tyre != 0L) {
                        this.createSubjectsOut(tyre, EnumConsts.SubjectIds.ZHAOSHANG_TYRE, codeType, subList);
                    }
                    if (trailerCertification != 0L) {
                        this.createSubjectsOut(trailerCertification, EnumConsts.SubjectIds.ZHAOSHANG_TRAILERCERTIFICATION, codeType, subList);
                    }
                    if (motorVehicles != 0L) {
                        this.createSubjectsOut(motorVehicles, EnumConsts.SubjectIds.ZHAOSHANG_MOTORVEHICLES, codeType, subList);
                    }
                    if (trailerInsurancePremium != 0L) {
                        this.createSubjectsOut(trailerInsurancePremium, EnumConsts.SubjectIds.ZHAOSHANG_TRAILERINSURANCEPREMIUM, codeType, subList);
                    }
                    if (spare1 != 0L) {
                        this.createSubjectsOut(spare1, EnumConsts.SubjectIds.ZHAOSHANG_SPARE1, codeType, subList);
                    }
                    if (spare2 != 0L) {
                        this.createSubjectsOut(spare2, EnumConsts.SubjectIds.ZHAOSHANG_SPARE2, codeType, subList);
                    }
                    if (spare3 != 0L) {
                        this.createSubjectsOut(spare3, EnumConsts.SubjectIds.ZHAOSHANG_SPARE3, codeType, subList);
                    }
                    if (spare4 != 0L) {
                        this.createSubjectsOut(spare4, EnumConsts.SubjectIds.ZHAOSHANG_SPARE4, codeType, subList);
                    }
                    if (spare5 != 0L) {
                        this.createSubjectsOut(spare5, EnumConsts.SubjectIds.ZHAOSHANG_SPARE5, codeType, subList);
                    }
                    if (spare6 != 0L) {
                        this.createSubjectsOut(spare6, EnumConsts.SubjectIds.ZHAOSHANG_SPARE6, codeType, subList);
                    }
                    if (spare7 != 0L) {
                        this.createSubjectsOut(spare7, EnumConsts.SubjectIds.ZHAOSHANG_SPARE7, codeType, subList);
                    }
                    if (tripcompensate != 0L) {
                        this.createSubjectsOut(tripcompensate, EnumConsts.SubjectIds.ZHAOSHANG_TRIPCOMPENSATE, codeType, subList);
                    }
                }
                //发放工资特殊处理
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CAR_DRIVER_SALARY))) {
                    if (sumAmount > 0L) {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                    } else {
                        appDetail.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                    }
                    SubjectsOutDto subjectsOut = new SubjectsOutDto();
                    subjectsOut.setSubjectsId(0L);
                    subjectsOut.setSubCostType(appDetail.getCostType());
                    subjectsOut.setSubjectsAmount(sumAmount);
                    subjectsOut.setSubjectsName("可用(总)");
                    subList.add(subjectsOut);
                }
                appDetail.setBussinessName(codeTypeAlias);
                //大项名称特殊处理
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CANCEL_THE_ORDER)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FINAL)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.BEFORE_PAY_CODE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.EXCEPTION_FEE_OUT))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CONSUME_ETC_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ETC_PAY_CODE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAYFOR_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OA_LOAN_AVAILABLE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.EXPENSE_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OA_LOAN_VERIFICATION))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.EXCEPTION_FEE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.UPDATE_THE_ORDER))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.UPDATE_ORDER_PRICE)) || detailBusinessNumber.equals(String.valueOf(210000021520L))
                        || detailBusinessNumber.equals(String.valueOf(210000021519L)) || detailBusinessNumber.equals(String.valueOf(210000141522L))
                        || detailBusinessNumber.equals(String.valueOf(210000141082L)) || detailBusinessNumber.equals(String.valueOf(210000021521L))
                        || detailBusinessNumber.equals(String.valueOf(210000021632L)) || detailBusinessNumber.equals(String.valueOf(220000281634L)) || detailBusinessNumber.equals(String.valueOf(220000281635L)) || detailBusinessNumber.equals(String.valueOf(210000021662L))
                ) {
                    appDetail.setSubtitle("订单" + tempOrderId);
                } else if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.TRANSFER_TRUST_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ENTRUST_CODE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.EXCEPTION_FEE_OUT))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CONSUME_ETC_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ETC_PAY_CODE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAYFOR_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OA_LOAN_AVAILABLE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.EXPENSE_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.OA_LOAN_VERIFICATION))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.EXCEPTION_FEE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.UPDATE_THE_ORDER))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.UPDATE_ORDER_PRICE)) || detailBusinessNumber.equals(String.valueOf(210000021520L))
                        || detailBusinessNumber.equals(String.valueOf(210000021519L)) || detailBusinessNumber.equals(String.valueOf(210000141522L))
                        || detailBusinessNumber.equals(String.valueOf(210000141082L)) || detailBusinessNumber.equals(String.valueOf(210000021521L))
                        || detailBusinessNumber.equals(String.valueOf(210000021632L)) || detailBusinessNumber.equals(String.valueOf(220000281634L)) || detailBusinessNumber.equals(String.valueOf(220000281635L)) || detailBusinessNumber.equals(String.valueOf(210000021662L))
                ) {
                    appDetail.setSubtitle("订单" + tempOrderId);
                } else if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.TRANSFER_TRUST_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ENTRUST_CODE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FOR_REPAIR)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.INCOME_REPAIR))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FOR_OIL_CODE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.FORCE_ZHANG_PING))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CAR_DRIVER_SALARY))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.VIOLATION_FEE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL))) {
                    String aliasName = "";
                    //加油消费
                    if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FOR_OIL_CODE))) {
                        aliasName = detail.getPlateNumber();
                    }
                    //托管出账
                    if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.TRANSFER_TRUST_CODE))) {
                        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(detail.getOtherUserId());
                        if (userDataInfo != null) {
                            aliasName = userDataInfo.getLinkman() + userDataInfo.getMobilePhone();
                        }
                    }
                    //托管入账
                    if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.ENTRUST_CODE))) {
                        LambdaQueryWrapper<VehicleDataInfo> queryWrapper = new LambdaQueryWrapper<>();
                        List<VehicleDataInfo> list1 = vehicleDataInfoService.getBaseMapper().selectList(queryWrapper);
                        if (list1 != null && list1.size() > 0) {
                            VehicleDataInfo vd = list1.get(0);
                            aliasName = vd.getPlateNumber();
                        }
                    }
                    //维修保养消费、维修保养收入
                    if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.PAY_FOR_REPAIR))) {
                        aliasName = detail.getOtherName();
                    }

                    /** 违章费用流水
                     50070 支付违章费用
                     50071 收入违章费用
                     1804 应付逾期(司机违章费用)
                     1805 应收逾期(司机违章费用)

                     50072 支付违章服务费用
                     50073 收入违章服务费用
                     1803 应收逾期(违章代缴服务费)
                     1802 应付逾期(违章代缴服务费)

                     1806 应付逾期(违章代缴服务费退还)
                     1807 应收逾期(违章代缴服务费退还)
                     1808 应付逾期(违章代缴费退还)
                     1809 应收逾期(违章代缴费退还)
                     */
                    if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.VIOLATION_FEE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE))
                            || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL))) {
                        List<Map> resultList = accountDetailsMapper.getAliasName(detail.getUserId());
                        if (resultList != null && resultList.size() > 0) {
                            aliasName = DataFormat.getStringKey(resultList.get(0), "name");
                        }
                    }

                    //选择账户平账
                    if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.FORCE_ZHANG_PING))) {
                        if (EnumConsts.SubjectIds.SELECT_ZHANG_PING_MARGIN_IN == detailSubjectsId) {
                            String otherName = detail.getOtherName();
                            String plateNumber = "";
                            JSONArray jsonArray = JSONArray.fromObject(otherName);
                            List<Long> userIdList = (List) JSONArray.toCollection(jsonArray, Long.class);
                            for (Long tempUserId : userIdList) {
                                LambdaQueryWrapper<VehicleDataInfo> queryWrapper = new LambdaQueryWrapper<>();
                                List<VehicleDataInfo> list1 = vehicleDataInfoService.getBaseMapper().selectList(queryWrapper);
                                if (list1 != null && list1.size() > 0) {
                                    VehicleDataInfo vd = list1.get(0);
                                    plateNumber = vd.getPlateNumber();
                                }
                                if (userIdList.size() > 1) {
                                    if (org.apache.commons.lang3.StringUtils.equals(tempUserId + "", userIdList.get(userIdList.size() - 1) + "")) {
                                        aliasName += plateNumber;
                                    } else {
                                        aliasName += "车牌号" + plateNumber + ",";
                                    }
                                } else {
                                    aliasName += "车牌号" + plateNumber;
                                }

                            }
                        }
                        if (EnumConsts.SubjectIds.SELECT_ZHANG_PING_MARGIN_OUT == detailSubjectsId) {
                            Long tempUserId = detail.getOtherUserId();
                            String plateNumber = "";
                            LambdaQueryWrapper<VehicleDataInfo> queryWrapper = new LambdaQueryWrapper<>();
                            List<VehicleDataInfo> list1 = vehicleDataInfoService.getBaseMapper().selectList(queryWrapper);
                            if (list1 != null && list1.size() > 0) {
                                VehicleDataInfo vd = list1.get(0);
                                plateNumber = vd.getPlateNumber();
                            }
                            aliasName += "车牌号" + plateNumber;
                        }
                    }
                    //工资发放
                    if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CAR_DRIVER_SALARY))) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月");
                        Calendar ca = Calendar.getInstance();
                        ca.setTime(Date.from(detail.getCreateTime().atZone(ZoneId.systemDefault()).toInstant()));
                        ca.add(Calendar.MONTH, -1);
                        String carDriverSalaryDate = df.format(ca.getTime());
                        aliasName = carDriverSalaryDate;
                    }
                    appDetail.setSubtitle(aliasName);
                }

                if (org.apache.commons.lang3.StringUtils.isNotBlank(appDetail.getOrderId()) && !org.apache.commons.lang3.StringUtils.equals("null", appDetail.getOrderId())) {
                    Long tenantId = -1L;
                    OrderInfo oi = orderInfoService.getOrder(Long.parseLong(appDetail.getOrderId()));
                    if (oi != null) {
                        tenantId = oi.getTenantId();
                    } else {
                        OrderInfoH oih = orderInfoHService.getOrderH(Long.parseLong(appDetail.getOrderId()));
                        if (oih != null) {
                            tenantId = oih.getTenantId();
                        }
                    }
                    if (tenantId >= 0) {
                        SysTenantVo tenantById = sysTenantDefService.getTenantById(tenantId);
                        if (tenantById != null) {
                            appDetail.setVehicleAffiliation(tenantById.getShortName());
                        }
                    }
                }
                if (detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.VIOLATION_FEE)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.VERIFICATION_SERVICE_CHARGE))
                        || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL)) || detailBusinessNumber.equals(String.valueOf(EnumConsts.PayInter.CLEAR_ACCOUNT_OIL))) {
                    List<Map> resultList = accountDetailsMapper.getAliasName(detail.getUserId());
                    if (resultList != null && resultList.size() > 0) {
                        appDetail.setVehicleAffiliation(DataFormat.getStringKey(resultList.get(0), "name"));
                    }
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(appDetail.getVehicleAffiliation()) && null != detail.getTenantId()) {
                    SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(detail.getTenantId());
                    if (sysTenantDef != null) {
                        appDetail.setVehicleAffiliation(sysTenantDef.getShortName());
                    }
                }
                appList.add(appDetail);
            } else {
                continue;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
        try {
            appAccountDetailsVo.setMonth(sdf.format(sdf1.parse(appAccountDetailsVo.getMonth())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //去除不必要的流水
        Iterator<AppAccountDetailsOutDto> tempIt = appList.iterator();
        while (tempIt.hasNext()) {
            AppAccountDetailsOutDto tempDetailsOut = tempIt.next();
            Long number = tempDetailsOut.getBusinessNumber();
            //修改订单costType根据金额设置
            if (org.apache.commons.lang3.StringUtils.equals(String.valueOf(EnumConsts.PayInter.UPDATE_THE_ORDER), number + "")) {
                if (tempDetailsOut.getAmount() > 0) {
                    tempDetailsOut.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                } else if (tempDetailsOut.getAmount() < 0) {
                    tempDetailsOut.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                } else {
                    tempDetailsOut.setCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE3));
                }

                List<SubjectsOutDto> subList = tempDetailsOut.getSubList();
                Long amount = 0L;
                for (int i = (subList.size() - 1); i >= 0; i--) {
                    SubjectsOutDto out = subList.get(i);
                    if (EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW == out.getSubjectsId() || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_UPP == out.getSubjectsId()
                            || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW == out.getSubjectsId() || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW == out.getSubjectsId()
                            || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW == out.getSubjectsId() || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_UPP == out.getSubjectsId()
                            || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW == out.getSubjectsId() || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW == out.getSubjectsId()
                            || EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_UPP == out.getSubjectsId()) {
                        amount += out.getSubjectsAmount();
                        subList.remove(i);
                    }
                }
                if (amount > 0 || amount < 0) {
                    SubjectsOutDto out = new SubjectsOutDto();
                    if (amount > 0) {
                        out.setSubCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
                    } else if (amount < 0) {
                        out.setSubCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
                    }
                    out.setSubjectsAmount(amount);
                    out.setSubCostType(Integer.parseInt(EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW + ""));
                    SysStaticData sysStaticData = sysStaticDataService.getSysStaticData(codeType, EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW + "");
                    if (sysStaticData != null) {
                        out.setSubjectsName(sysStaticData.getCodeName());
                    }
                    subList.add(out);
                }
            }

            if (org.apache.commons.lang3.StringUtils.equals("0", appAccountDetailsVo.getType())) {//全部
                if (number == null || number <= 0L) {
                    tempIt.remove();
                } else {
                    tempDetailsOut.setDetailMonth(appAccountDetailsVo.getMonth());
                }
            } else {
                if (tempDetailsOut.getCostType() == null || Integer.parseInt(appAccountDetailsVo.getType()) != tempDetailsOut.getCostType() || number == null || number <= 0L) {
                    tempIt.remove();
                } else {
                    tempDetailsOut.setDetailMonth(appAccountDetailsVo.getMonth());
                }
            }

        }

        List<AppAccountDetailsOutDto> items = new ArrayList<>();
        if (appList != null && appList.size() > 0) {
            for (AppAccountDetailsOutDto detail : appList) {
                if (detail.getAmount().longValue() != 0) {
                    items.add(detail);
                }
            }
        }
        AppAccountDetailsDto dto = new AppAccountDetailsDto();
        dto.setItems(items);
        dto.setMonth(appAccountDetailsVo.getMonth());

        return dto;
    }

    /**
     * 计算科目费用项
     * @param
     */
    public  Map<String, Object> countSubsOut(AccountDetails tempDetail,AppAccountDetailsOutDto appDetail,List<SubjectsOutDto> subList,Map<String, AccountDetails> detailMap,Map<String, Object> countMap) {
        SubjectsOutDto subjectsOut = new SubjectsOutDto();
        String accountDetailsId = tempDetail.getId().toString();
        String codeType = "APP_ACCOUNT_DETAILS_OUT";
        String codeTypeAlias = "";
        SysStaticData sysStaticData = readisUtil.getSysStaticData(codeType, tempDetail.getSubjectsId().toString());
        subjectsOut.setSubjectsId(tempDetail.getSubjectsId());
        subjectsOut.setSubCostType(tempDetail.getCostType());
        subjectsOut.setSubjectsAmount(tempDetail.getAmount());
        if (sysStaticData != null) {
            subjectsOut.setSubjectsName(sysStaticData.getCodeName());
            codeTypeAlias = sysStaticData.getCodeTypeAlias();
        }
        if (tempDetail.getAmount() != null && tempDetail.getAmount() != 0L) {
            subList.add(subjectsOut);
        }
        detailMap.put(accountDetailsId, tempDetail);
        countMap.put("codeTypeAlias", codeTypeAlias);
        return countMap;
    }

    /**
     * 创建科目费用项
     * @param amount
     * @param subjectsId
     * @param subList
     * @return
     * @throws Exception
     */
    public void createSubjectsOut (Long amount, Long subjectsId,String codeType, List<SubjectsOutDto> subList) {
        SysStaticData sysStaticData = sysStaticDataService.getSysStaticData(codeType, String.valueOf(subjectsId));
        SubjectsOutDto out = new SubjectsOutDto();
        out.setSubjectsId(Long.parseLong(sysStaticData.getCodeValue()));
        if (amount > 0L) {
            out.setSubCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE2));
        } else {
            out.setSubCostType(Integer.parseInt(EnumConsts.APP_ACCOUNT_DETAILS_OUT.COST_TYPE1));
        }
        out.setSubjectsAmount(amount);
        if (sysStaticData != null) {
            out.setSubjectsName(sysStaticData.getCodeName());
        }
        subList.add(out);
    }
}
