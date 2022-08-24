package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilCardInfoService;
import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IProblemVerOptService;
import com.youming.youche.order.api.order.other.IOrderPayMethodService;
import com.youming.youche.order.api.order.other.IUpdateOrderService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderFeeStatement;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.ProblemVerOpt;
import com.youming.youche.order.dto.CancelTheOrderInDto;
import com.youming.youche.order.provider.mapper.order.ProblemVerOptMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.dto.AuditCallbackDto;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-29
 */
@DubboService(version = "1.0.0")
@Service
public class ProblemVerOptServiceImpl extends BaseServiceImpl<ProblemVerOptMapper, ProblemVerOpt> implements IProblemVerOptService {
    @Resource
    private LoginUtils loginUtils;
    @Autowired
    private IOrderProblemInfoService orderProblemInfoService;
    @Resource
    private ReadisUtil readisUtil;
    @Resource
    private IPayFeeLimitService payFeeLimitService;
    @Resource
    private IOrderInfoService orderInfoService;
    @Resource
    private IOrderInfoHService orderInfoHService;
    @Resource
    private IOrderSchedulerHService orderSchedulerHService;
    @Resource
    private IOrderInfoExtHService orderInfoExtHService;
    @Resource
    private IOrderFeeHService orderFeeHService;
    @Resource
    private IOrderSchedulerService orderSchedulerService;

    @Resource
    private IOrderInfoExtService orderInfoExtService;

    @Resource
    private IOrderFeeService orderFeeService;

    @Resource
    IOrderFeeStatementService orderFeeStatementService;

    @Resource
    IOrderFeeExtHService orderFeeExtHService;

    @Resource
    IOrderAgingInfoService orderAgingInfoService;

    @Resource
    IOrderFeeExtService orderFeeExtService;

    @Resource
    IOrderAccountService orderAccountService;

    @Resource
    IOrderLimitService orderLimitService;

    @Resource
    IUpdateOrderService updateOrderService;

    @Resource
    IOrderOilCardInfoService orderOilCardInfoService;

    @Resource
    IOilCardManagementService oilCardManagementService;

    @Resource
    IOrderPayMethodService orderPayMethodService;

    @Resource
    IOrderOpRecordService orderOpRecordService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService auditSettingService;

    @DubboReference(version = "1.0.0")
    IAuditService auditService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Resource
    RedisUtil redisUtil;


    @Override
    public List<ProblemVerOpt> getProblemVerOptByProId(Long proId, Long tenantId) {
        if (proId <= 0) {
            throw new BusinessException("异常ID不存在!");
        }
        List<ProblemVerOpt> list = new ArrayList<ProblemVerOpt>();
        QueryWrapper<ProblemVerOpt> problemVerOptQueryWrapper = new QueryWrapper<>();
        problemVerOptQueryWrapper.eq("PROBLEM_ID", proId);
        if (tenantId != null && tenantId > 0) {
            problemVerOptQueryWrapper.eq("tenant_id", tenantId);
        }
        problemVerOptQueryWrapper.orderByDesc("create_time");
        list = super.list(problemVerOptQueryWrapper);
        if (list != null && list.size() > 0) {
            return list;
        }
        return list;
    }

    @Override
    @GlobalTransactional(timeoutMills = 300000,rollbackFor = Exception.class)
    public Boolean verifyFirst(Long problemId, String verifyDesc, String problemDealPrice, String accessToken) {
        if (problemId == null || problemId <= 0) {
            throw new BusinessException("未找到异常信息，请联系客服！");
        }
        OrderProblemInfo problemInfo = orderProblemInfoService.getById(problemId);
        if (problemInfo == null) {
            log.error("未找到异常信息，请联系客服！异常ID[" + problemId + "]");
            throw new BusinessException("未找到异常信息，请联系客服！");
        }
        ProblemVerOpt verOpt = new ProblemVerOpt();
        LoginInfo user = loginUtils.get(accessToken);
        verOpt.setProblemId(problemId);
        verOpt.setCheckUserId(user.getId());
        verOpt.setCheckUserName(user.getName());
        verOpt.setTenantId(user.getTenantId());
        verOpt.setProblemState(problemInfo.getState());
        if (StringUtils.isNotBlank(verifyDesc)) {
            verOpt.setCheckRemark(verifyDesc);
            problemInfo.setVerifyDesc(verifyDesc);
        }
        if (StringUtils.isNotBlank(problemDealPrice)) {//有处理金额
            if (!CommonUtil.isNumberNotNull(problemDealPrice)) {
                throw new BusinessException("请输入正确的处理金额");
            }
            Double problemDealPriceDouble = Double.parseDouble(problemDealPrice) * 100;
            Long problemDealPriceNum = problemDealPriceDouble.longValue();
            if (EnumConsts.Exception_Deal_Type.ADDMONEY.getType() == readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE, problemInfo.getProblemType()).getCodeId()) {
                if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) { //成本异常
                    payFeeLimitService.judgePayFeeLimit(problemDealPriceNum, EnumConsts.PAY_FEE_LIMIT.TYPE1,
                            EnumConsts.PAY_FEE_LIMIT.SUB_TYPE2, problemInfo.getTenantId());
                } else if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
                    payFeeLimitService.judgePayFeeLimit(problemDealPriceNum, EnumConsts.PAY_FEE_LIMIT.TYPE1,
                            EnumConsts.PAY_FEE_LIMIT.SUB_TYPE3, problemInfo.getTenantId());
                }
                problemDealPriceNum = Math.abs(problemDealPriceNum);
            } else if (EnumConsts.Exception_Deal_Type.REDUCEMONEY.getType() == readisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE, problemInfo.getProblemType()).getCodeId()) {
                problemDealPriceNum = -(Math.abs(problemDealPriceNum));
            }
            verOpt.setProblemDealPrice(problemDealPriceNum);
            problemInfo.setProblemDealPrice(problemDealPriceNum);
        } else {
            throw new BusinessException("请输入处理金额!");
        }
        Long orderId = problemInfo.getOrderId();
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderInfo orderInfo = orderInfoService.getOrder(problemInfo.getOrderId());
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单号[" + orderId + "]信息！");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(orderId);
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
            BeanUtil.copyProperties(orderFeeH, orderFee);
        } else {
            orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            orderFee = orderFeeService.getOrderFee(orderId);
        }
        SysStaticData data = readisUtil.getSysStaticData(-1L,
                SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE, problemInfo.getProblemType() + "");
        //校验异常款项不能大于尾款
        if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST
                && data != null && data.getCodeId() == 1) {
            orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt,
                    problemInfo.getProblemDealPrice(), -1L, problemId);
        }
        problemInfo.setState(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
        if (problemInfo.getProblemSourceId() != null && problemInfo.getProblemSourceId() > 0) {//由外部生成的异常
            OrderProblemInfo problemSourceInfo = orderProblemInfoService.getById(problemInfo.getProblemSourceId());
            //需同步审核状态 不影响当前流程
            if (problemSourceInfo != null) {
                problemSourceInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
            }
        }
        AuditCallbackDto auditCallbackDto = auditSettingService.sure(AuditConsts.AUDIT_CODE.PROBLEM_CODE, problemInfo.getId(),
                verifyDesc, AuditConsts.RESULT.SUCCESS, accessToken);
        orderProblemInfoService.saveOrUpdate(problemInfo);
        if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()){
            sucess(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),auditCallbackDto.getParamsMap(),accessToken);
        }
        verOpt.setState(1);
        this.saveOrUpdate(verOpt);
        StringBuffer buffer = new StringBuffer("[" + user.getName() + "]异常审核通过");
        sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OrderInfo, orderId, com.youming.youche.commons.constant.SysOperLogConst.OperType.Audit, buffer.toString(), user.getTenantId());

//        // 新加审核通过后发送
//        try {
//            OrderInfo orderInfoR = orderInfoService.getOrder(orderId);
//            OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderId);
//            Map<String, Object> paraMap = new HashMap<String, Object>();
//            paraMap.put("tenantName", orderInfoR.getTenantName());
//            paraMap.put("orderId", scheduler.getOrderId());
//            paraMap.put("info", "异常信息");
//            sysSmsSendService.sendSms(scheduler.getCarDriverPhone(), EnumConsts.SmsTemplate.ORDER_PRO_AGING,
//                    com.youming.youche.conts.SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT,
//                    com.youming.youche.conts.SysStaticDataEnum.OBJ_TYPE.ORDER_EXCEPTION, String.valueOf(orderId), paraMap, accessToken);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        return true;
    }


    @Override
    public void fail(Long busiId, String desc, String accessToken,boolean flag) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(flag) {
            verifyFail(busiId, desc, loginInfo, accessToken);
        }else {
            verifyFailException(busiId,desc,accessToken);
        }
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap,String accessToken) {
        this.verifyPass(busiId, desc, DataFormat.getStringKey(paramsMap, "problemDealPrice"), false, DataFormat.getStringKey(paramsMap, AuditConsts.CallBackMapKey.NODE_NUM),accessToken);
    }

    /**
     * 审核通过
     *
     * @param problemId    主键
     * @param verifyDesc 审核描述
     * @param problemDealPrice
     * @throws Exception
     */
    public void verifyPass(Long problemId, String verifyDesc, String problemDealPrice, Boolean isSave, String nodeNum, String accessToken) {
        if (problemId == null || problemId <= 0) {
            throw new BusinessException("未找到异常信息，请联系客服！");
        }
        OrderProblemInfo problemInfo = orderProblemInfoService.getById(problemId);
        if (problemInfo == null) {
            log.error("未找到异常信息，请联系客服！异常ID[" + problemId + "]");
            throw new BusinessException("未找到异常信息，请联系客服！");
        }
        LoginInfo user = loginUtils.get(accessToken);
        Long orderId = problemInfo.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderFee orderFee = new OrderFee();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderScheduler orderScheduler = new OrderScheduler();
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单号[" + orderId + "]信息！");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerService.getOrderSchedulerH(orderId);
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(orderId);
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
            BeanUtil.copyProperties(orderFeeH, orderFee);
            isHis = true;
            Map<Long, Boolean> finalExpireMap = new ConcurrentHashMap<Long, Boolean>();
            List<Long> busiIdList = new ArrayList<>();
            busiIdList.add(orderId);
            finalExpireMap = orderSchedulerService.hasFinalOrderLimit(busiIdList);
            Boolean isAddProblem = finalExpireMap.get(orderInfo.getOrderId());
            isAddProblem = isAddProblem == null ? false : isAddProblem;
            if (!isAddProblem) {
                throw new BusinessException("尾款已到期，审核失败！");
            }
        } else {
            orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            orderFee = orderFeeService.getOrderFee(orderId);
        }
        //异常审核日志
        ProblemVerOpt verOpt = new ProblemVerOpt();
        verOpt.setProblemId(problemId);
        verOpt.setCheckUserId(user.getUserInfoId());
        verOpt.setCheckUserName(user.getName());
        verOpt.setTenantId(user.getTenantId());
        verOpt.setProblemState(problemInfo.getState());
        if (StringUtils.isNotBlank(verifyDesc)) {
            verOpt.setCheckRemark(verifyDesc);
            problemInfo.setVerifyDesc(verifyDesc);
        }
        if (isSave) {//新增通过 默认处理金额
            problemInfo.setProblemDealPrice(problemInfo.getProblemPrice());
        }
        //  IOrderFeeTF feeTF = (IOrderFeeTF) SysContexts.getBean("orderFeeTF");
        //审核结束
        problemInfo.setVerifyDate(LocalDateTime.now());
        problemInfo.setState(SysStaticDataEnum.EXPENSE_STATE.END);
        if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
            orderProblemInfoService.saveOrUpdate(problemInfo);
            //同步支付中心 历史单不同步扣减
            if (!isHis || (problemInfo.getProblemDealPrice() > 0)) {
                orderFeeService.synPayCenter(orderId, accessToken);
            }
        }
        if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) { //成本异常
            SysStaticData data = getSysStaticData(SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE, problemInfo.getProblemType() + "");
            if (data != null && data.getCodeId() == 1) {
                //成本扣减项  尾款校验
                orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, problemInfo.getProblemDealPrice(), -1L, problemId);
            }
            //去除订单待处理订单数
            orderInfo.setNoAuditExcNum((orderInfo.getNoAuditExcNum() == null ? 0 : orderInfo.getNoAuditExcNum()) - 1);
            if (isHis) {
                OrderInfoH orderInfoH = orderInfoService.getOrderH(orderId);
                orderInfoH.setNoAuditExcNum(orderInfo.getNoAuditExcNum());
                orderInfoHService.saveOrUpdate(orderInfoH);
            } else {
                orderInfoService.saveOrUpdate(orderInfo);
            }
            //外发单的成本异常将推送一条属于接单方已完成的收入异常 注明：外部生成的异常不需要再次推送
            if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {//外发单
                Boolean isPush = true;
                if (problemInfo.getSourceProblem() != null
                        && problemInfo.getSourceProblem()
                        == SysStaticDataEnum.SOURCE_PROBLEM.PUSH) {//由外部生成的异常
                    OrderProblemInfo problemSourceInfo = orderProblemInfoService.getById(problemInfo.getProblemSourceId());
                    //需同步审核状态 不影响当前流程
                    if (problemSourceInfo != null) {
                        problemSourceInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                        orderProblemInfoService.saveOrUpdate(problemSourceInfo);
                    }
                    isPush = false;//不推送
                }
                if (orderInfo.getToOrderId() == null || orderInfo.getToOrderId() <= 0) {
                    isPush = false;//不推送
                }
                if (isPush) {//推送操作
                    OrderProblemInfo info = new OrderProblemInfo();
                    BeanUtil.copyProperties(problemInfo,info);
                    //     ITenantSV tenantSV = (ITenantSV) SysContexts.getBean("tenantSV");
                    SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
                    info.setId(CommonUtil.createSoNbr());
                    info.setOrderId(orderInfo.getToOrderId());
                    info.setState(SysStaticDataEnum.EXPENSE_STATE.END);
                    info.setVerifyDate(LocalDateTime.now());
                    info.setRecordUserId(user.getTenantId());
                    info.setCarOwnerId(tenantDef.getAdminUser());
                    info.setCarOwnerName(tenantDef.getLinkMan());
                    info.setCarOwnerPhone(tenantDef.getLinkPhone());
                    info.setVerifyDesc(null);
                    info.setProblemCondition(SysStaticDataEnum.PROBLEM_CONDITION.INCOME);
                    info.setReportSts(0);
                    info.setReportDate(null);
                    info.setAppealReason(null);
                    info.setSourceProblem(SysStaticDataEnum.SOURCE_PROBLEM.PUSH);
                    info.setProblemSourceId(problemInfo.getId());
                    info.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                    OrderInfo fromOrderInfo = orderInfoService.getOrder(info.getOrderId());
                    if (fromOrderInfo != null) {
                        info.setTenantId(fromOrderInfo.getTenantId());
                        orderProblemInfoService.saveOrUpdate(info);
                        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                        params.put("busiId", info.getId());
                        problemInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                        problemInfo.setProblemSourceId(info.getId());
                        orderFeeService.updateOrderExceptionPrice(info, info.getProblemDealPrice());
                    }
                }
                //对接单方资金操作
                commonExceptionFlowPath(problemInfo, orderFee, true, accessToken);
            } else {
                //内部单的成本异常 || 外部单的纯C 将对司机进行资金操作
                commonExceptionFlowPath(problemInfo, orderFee, false, accessToken);
            }
            if (orderInfo != null && orderScheduler != null) {
                //指派完成状态为待装货   自有车 或者 纯C外调车订单才发送短信
                if (orderScheduler.getVehicleClass() != null && (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        || ((orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                        && (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0)))) {
                    Map paraMap = new HashMap();
                    paraMap.put("tenantName", orderInfo.getTenantName());
                    paraMap.put("orderId", orderInfo.getOrderId());
                    paraMap.put("info", "异常信息");
                    try {
                        sysSmsSendService.sendSms(orderScheduler.getCarDriverPhone(), EnumConsts.SmsTemplate.ORDER_PRO_AGING, SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT, SysStaticDataEnum.OBJ_TYPE.ORDER_EXCEPTION, String.valueOf(orderInfo.getOrderId()), paraMap, accessToken);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
            //去除订单收入异常待处理数
            orderInfo.setNoAuditExcNumIn((orderInfo.getNoAuditExcNumIn() == null ? 0 : orderInfo.getNoAuditExcNumIn()) - 1);
            if (isHis) {
                OrderInfoH orderInfoH = orderInfoService.getOrderH(orderId);
                orderInfoH.setNoAuditExcNumIn(orderInfo.getNoAuditExcNumIn());
                orderInfoHService.saveOrUpdate(orderInfoH);
            } else {
                orderInfoService.saveOrUpdate(orderInfo);
            }
            orderFeeService.updateOrderExceptionPrice(problemInfo, problemInfo.getProblemDealPrice());
            //如是接单方，收入异常审核通过，推送一条属于转单方的收入异常 注明：外部生成的异常不需要再次推送
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {//有来源订单 表示接单方
                boolean isPush = true;
                if (problemInfo.getSourceProblem() != null && problemInfo.getSourceProblem() == SysStaticDataEnum.SOURCE_PROBLEM.PUSH) {//由外部生成的异常
                    OrderProblemInfo problemSourceInfo = orderProblemInfoService.getById(problemInfo.getProblemSourceId());
                    //需同步审核状态 不影响当前流程
                    if (problemSourceInfo != null) {
                        problemSourceInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                        orderProblemInfoService.saveOrUpdate(problemSourceInfo);
                    }
                    isPush = false;//不推送
                } else {
                    OrderInfoH orderInfoH = orderInfoService.getOrderH(orderInfo.getFromOrderId());
                    if (orderInfoH != null) {//来源订单已进入历史
                        isPush = false;
                    }
                }
                if (isPush) {//推送操作
                    OrderProblemInfo info = new OrderProblemInfo();
                    BeanUtil.copyProperties(problemInfo,info);
                    info.setId(CommonUtil.createSoNbr());
                    info.setOrderId(orderInfo.getFromOrderId());
                    info.setState(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
                    info.setRecordUserId(user.getTenantId());
                    SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getFromTenantId(), true);
                    info.setCarOwnerId(tenantDef.getAdminUser());
                    info.setCarOwnerName(tenantDef.getLinkMan());
                    info.setCarOwnerPhone(tenantDef.getLinkPhone());
                    info.setVerifyDesc(null);
                    info.setVerifyDate(null);
                    info.setProblemCondition(SysStaticDataEnum.PROBLEM_CONDITION.COST);
                    info.setReportSts(0);
                    info.setReportDate(null);
                    info.setAppealReason(null);
                    info.setSourceProblem(SysStaticDataEnum.SOURCE_PROBLEM.PUSH);
                    info.setProblemSourceId(problemInfo.getId());
                    info.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                    OrderInfo fromOrderInfo = orderInfoService.getOrder(info.getOrderId());
                    if (fromOrderInfo != null) {
                        info.setTenantId(fromOrderInfo.getTenantId());
                        orderProblemInfoService.saveOrUpdate(info);
                        problemInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
                        problemInfo.setProblemSourceId(info.getId());
                        //  IAuditOutTF outTF = (IAuditOutTF) SysContexts.getBean("auditOutTF");
                        //启动审核
                        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                        params.put("busiId", info.getId());
                        auditService.startProcess(AuditConsts.AUDIT_CODE.PROBLEM_CODE, info.getId(), SysOperLogConst.BusiCode.ProblemInfo, params, accessToken, fromOrderInfo.getTenantId());
                        fromOrderInfo.setNoAuditExcNum(fromOrderInfo.getNoAuditExcNum() == null ? 1 : fromOrderInfo.getNoAuditExcNum() + 1);
                    }
                }
            }
        }
        orderOpRecordService.saveOrUpdate(problemInfo.getOrderId(), OrderConsts.OrderOpType.PROBLEM, accessToken);
        orderProblemInfoService.saveOrUpdate(problemInfo);
        if (StringUtils.isBlank(nodeNum) || !nodeNum.trim().equals("1")) {
            verOpt.setState(1);
            super.saveOrUpdate(verOpt);
        }
    }

    /**
     * 时效审核不通过
     *
     * @param agingId
     * @param verifyDesc
     * @throws Exception
     */
    public void verifyFail(Long agingId, String verifyDesc, LoginInfo user, String accessToken) {

        OrderAgingInfo orderAgingInfo = orderAgingInfoService.getOrderAgingInfo(agingId);
        if (orderAgingInfo != null) {
            orderAgingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN);
            sysOperLogService.save(SysOperLogConst.BusiCode.OrderInfo, orderAgingInfo.getOrderId(), SysOperLogConst.OperType.Update, "["+user.getName() + "] 审核时效罚款不通过 备注:" + verifyDesc, accessToken);
            sysOperLogService.save(SysOperLogConst.BusiCode.AgingInfo, orderAgingInfo.getId(), SysOperLogConst.OperType.Audit, "["+user.getName() + "] 审核时效罚款不通过 备注:" + verifyDesc, accessToken);
            orderAgingInfoService.saveOrUpdate(orderAgingInfo);
        } else {
            log.error("未找到该时效信息，请联系客服！时效ID[" + agingId + "]");
            throw new BusinessException("未找到该时效信息，请联系客服！");
        }
    }


    /**
     * 异常审核不通过
     *
     * @param problemId
     * @param verifyDesc
     * @throws Exception
     */
    public void verifyFailException(Long problemId,String verifyDesc, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if(problemId == null || problemId<=0){ throw new BusinessException("未找到异常信息，请联系客服！"); }
        OrderProblemInfo problemInfo =  orderProblemInfoService.getById(problemId);
        if (problemInfo == null){
            log.error("未找到异常信息，请联系客服！异常ID["+problemId+"]");
            throw new BusinessException("未找到异常信息，请联系客服！");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(problemInfo.getOrderId());
        if (orderInfo == null) {
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(problemInfo.getOrderId());
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单["+problemInfo.getOrderId()+"]信息");
            }
            if(problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST ){
                orderInfoH.setNoAuditExcNum((orderInfoH.getNoAuditExcNum() == null ? 0 : orderInfoH.getNoAuditExcNum()) -1);
            }else if(problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.INCOME){
                orderInfoH.setNoAuditExcNumIn((orderInfoH.getNoAuditExcNumIn() == null ? 0 : orderInfoH.getNoAuditExcNumIn()) -1);
            }
            orderInfoHService.saveOrUpdate(orderInfoH);
        }else{
            if(problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST ){
                orderInfo.setNoAuditExcNum((orderInfo.getNoAuditExcNum() == null ? 0 : orderInfo.getNoAuditExcNum()) -1);
            }else if(problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.INCOME){
                orderInfo.setNoAuditExcNumIn((orderInfo.getNoAuditExcNumIn() == null ? 0 : orderInfo.getNoAuditExcNumIn()) -1);
            }
            orderInfoService.saveOrUpdate(orderInfo);
        }
        //异常审核日志
        ProblemVerOpt verOpt = new ProblemVerOpt();
        verOpt.setProblemId(problemId);
        verOpt.setCheckUserId(user.getUserInfoId());
        verOpt.setCheckUserName(user.getName());
        verOpt.setTenantId(user.getTenantId());
        verOpt.setProblemState(problemInfo.getState());
        if(StringUtils.isNotBlank(verifyDesc)){
            verOpt.setCheckRemark(verifyDesc);
            problemInfo.setVerifyDesc(verifyDesc);
        }
        problemInfo.setVerifyDate(LocalDateTime.now());
        if (problemInfo.getProblemSourceId() != null && problemInfo.getProblemSourceId() > 0) {//由外部生成的异常
            OrderProblemInfo problemSourceInfo = orderProblemInfoService.getById(problemInfo.getProblemSourceId());
            //需同步审核状态 不影响当前流程
            if (problemSourceInfo != null) {
                problemSourceInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN);
            }
        }
        problemInfo.setState(SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN);
        orderProblemInfoService.saveOrUpdate(problemInfo);
        verOpt.setState(2);
        super.saveOrUpdate(verOpt);
        sysOperLogService.save(SysOperLogConst.BusiCode.ProblemInfo, orderInfo.getOrderId(), SysOperLogConst.OperType.Audit, "["+user.getName() + "] 审核异常审核不通过 备注:" + verifyDesc, accessToken);

        sysOperLogService.save(SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), SysOperLogConst.OperType.Audit, "["+user.getName() + "] 审核异常审核不通过 备注:" + verifyDesc, accessToken);
    }


    private SysStaticData getSysStaticData(String codeType, String codeValue) {
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

    /**
     * 异常审核通过流程
     * （1）更新异常处理状态
     * （2）支付异常款
     * （3）更新订单款支入、支出金额
     * （4）处理特殊异常司机毁单异常需要根据靠台时间复制订单, 所有异常处理完成，若存在已经审核通过的司机毁单并且已过定标时间或客户毁单，则删除订单
     */
    @Override
    public void commonExceptionFlowPath(OrderProblemInfo orderProblemInfo, OrderFee orderFee, Boolean IsTransit, String accessToken) {
        OrderFeeStatement feeStatement = orderFeeStatementService.getOrderFeeStatementById(orderProblemInfo.getOrderId());
        if (feeStatement != null) {
            if (StringUtils.isNotBlank(feeStatement.getBillNumber()) && SysStaticDataEnum.PROBLEM_CONDITION.INCOME == orderProblemInfo.getProblemCondition()) {
                throw new BusinessException("请先撤消BL账单再处理！");
            }
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        orderProblemInfoService.update(orderProblemInfo);
        orderFeeService.updateOrderExceptionPrice(orderProblemInfo, orderProblemInfo.getProblemDealPrice());
        payForException(orderProblemInfo, IsTransit, accessToken);
        specialExceptionDeal(orderProblemInfo, orderFee, IsTransit, loginInfo, accessToken);

    }

    @Override
    public void payForException(OrderProblemInfo orderProblemInfo, Boolean IsTransit, String accessToken) {
        if (orderProblemInfo == null || orderProblemInfo.getProblemCondition() != SysStaticDataEnum.PROBLEM_CONDITION.COST) {
            return;
        }
        List<SysStaticData> lists = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE));
        SysStaticData s = null;
        for (SysStaticData sysStaticData : lists) {
            if (orderProblemInfo.getProblemType().equals(sysStaticData.getCodeValue())) {
                s = sysStaticData;
                break;
            }
        }
        if (lists == null || lists.isEmpty() || s == null) {
            log.error("异常类型非法，请联系客服！[" + EnumConsts.SysStaticData.COST_PROBLEM_TYPE + "]");
            throw new BusinessException("异常类型非法，请联系客服！");
        }
        EnumConsts.Exception_Deal_Type exceptionDealType = EnumConsts.Exception_Deal_Type.getType(Integer.parseInt(s.getCodeId() + ""));

        if (exceptionDealType == null) {
            log.error("异常金额非法，请联系客服！[" + s.getCodeId() + "]");
            throw new BusinessException("异常金额非法，请联系客服！");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //7-11增加
        Long orderId = orderProblemInfo.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderScheduler scheduler = new OrderScheduler();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单号[" + orderId + "]信息！");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(orderId);
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(orderId);
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderSchedulerH, scheduler);
            BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
            BeanUtil.copyProperties(orderFeeH, orderFee);
            BeanUtil.copyProperties(orderFeeExtH, orderFeeExt);
            isHis = true;
        } else {
            scheduler = orderSchedulerService.getOrderScheduler(orderId);
            orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            orderFee = orderFeeService.getOrderFee(orderId);
            orderFeeExt = orderFeeExtService.getOrderFeeExt(orderId);
        }
        Long userId = orderProblemInfo.getCarOwnerId();
        if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {

            SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
            if (tenantDef == null) {
                log.error("未找到车队信息! 车队ID[" + orderInfo.getToTenantId() + "]");
                throw new BusinessException("未找到车队信息!");
            }
            userId = tenantDef.getAdminUser();
        } else if (scheduler.getIsCollection() != null && scheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES) {
            userId = scheduler.getCollectionUserId();
        }
        //历史单不进行设置
        if (!isHis && EnumConsts.Exception_Deal_Type.ADDMONEY == exceptionDealType
                || (EnumConsts.Exception_Deal_Type.REDUCEMONEY == exceptionDealType
                && OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType()))) {
            orderFeeService.setFirstPayFlag(orderInfoExt, scheduler, orderFee, orderInfo, orderFeeExt, accessToken);
            orderInfoExtService.saveOrUpdate(orderInfoExt);
            orderFeeService.saveOrUpdate(orderFee);
        }
        String vehicleAffiliation = orderFee.getVehicleAffiliation();
        if (scheduler.getVehicleClass() != null
                && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            vehicleAffiliation = "0";
        }
        if (EnumConsts.Exception_Deal_Type.ADDMONEY == exceptionDealType) {
            if (OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {//如果是客户毁单  如果有其他减项也要扣减
                if (orderProblemInfo.getProblemDealPrice() != null && orderProblemInfo.getProblemDealPrice().longValue() != 0) {
                    orderAccountService.payForException(userId, vehicleAffiliation, orderProblemInfo.getProblemDealPrice(), orderProblemInfo.getOrderId(), orderProblemInfo.getTenantId(), loginInfo,accessToken);
                }
                payOtherProblemInfo(scheduler, userId, vehicleAffiliation, loginInfo,accessToken);
            } else {
                orderAccountService.payForException(userId, vehicleAffiliation, orderProblemInfo.getProblemDealPrice(), orderProblemInfo.getOrderId(), orderProblemInfo.getTenantId(), loginInfo,accessToken);
            }
        } else if (EnumConsts.Exception_Deal_Type.REDUCEMONEY == exceptionDealType) {
            if (OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {
                if (orderProblemInfo.getProblemDealPrice() != null && orderProblemInfo.getProblemDealPrice().longValue() != 0) {
                    orderLimitService.payForExceptionOut(userId, vehicleAffiliation, orderProblemInfo.getProblemDealPrice(), orderProblemInfo.getId(), orderProblemInfo.getTenantId(), orderProblemInfo.getOrderId(), loginInfo,accessToken);
                }
                payOtherProblemInfo(scheduler, userId, vehicleAffiliation, loginInfo,accessToken);
            } else {
                if (isHis) {//历史订单异常 直接进行资金操作
                    orderLimitService.payForExceptionOut(userId, vehicleAffiliation, orderProblemInfo.getProblemDealPrice(), orderProblemInfo.getId(), orderProblemInfo.getTenantId(), orderId, loginInfo,accessToken);
                }
            }
        }
    }

    /**
     * 支付其他异常
     *
     * @param scheduler
     * @param userId
     * @param vehicleAffiliation
     * @throws Exception
     */
    private void payOtherProblemInfo(OrderScheduler scheduler, Long userId, String vehicleAffiliation, LoginInfo loginInfo,String token) {
        List<OrderAgingInfo> orderAgingInfos = orderAgingInfoService.queryAgingInfoByOrderId(scheduler.getOrderId());
        if (orderAgingInfos != null && orderAgingInfos.size() > 0) {
            for (OrderAgingInfo orderAgingInfo : orderAgingInfos) {
                if (orderAgingInfo != null && orderAgingInfo.getAuditSts() != null
                        && orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END
                        && orderAgingInfo.getFinePrice() != null && orderAgingInfo.getFinePrice() > 0) {
                    orderLimitService.payForExceptionOut(userId, vehicleAffiliation, -orderAgingInfo.getFinePrice(), -1L,
                            scheduler.getTenantId(), scheduler.getOrderId(), loginInfo,token);
                }
            }
        }
        List<OrderProblemInfo> list = orderProblemInfoService.getOrderProblemInfoByOrderId(scheduler.getOrderId(), scheduler.getTenantId());
        for (OrderProblemInfo p : list) {
            if (p.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                if (SysStaticDataEnum.EXPENSE_STATE.END == p.getState().intValue()
                        && !OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(p.getProblemType())
                        && !OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(p.getProblemType())
                        && !OrderConsts.PROBLEM_TYPE.IN_BALLAST_COMPENSATION.equals(p.getProblemType())
                        && !OrderConsts.PROBLEM_TYPE.ADD_STOP_POINT.equals(p.getProblemType())
                        && !OrderConsts.PROBLEM_TYPE.OVERWIGHT.equals(p.getProblemType())) {
                    orderLimitService.payForExceptionOut(userId, vehicleAffiliation, p.getProblemDealPrice(), p.getId(), p.getTenantId(), p.getOrderId(), loginInfo,token);
                }
            }
        }
    }


    private void specialExceptionDeal(OrderProblemInfo orderProblemInfo, OrderFee orderFee, Boolean IsTransit, LoginInfo baseUser, String accessToken) {
        boolean hasCancelOrder = false;// 存在审核通过，并且需要取消的订单标志
        boolean hasAllAuit = true; // 所有异常全部审核完成
        boolean flag = false; // 司机毁单 或者货主毁单
        if (OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {
            flag = true;
        } else if (OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {
            flag = true;
        }
        // IOrderProblemInfoSV problemInfoSV = (IOrderProblemInfoSV) SysContexts.getBean("orderProblemSV");
        List<OrderProblemInfo> list = orderProblemInfoService.getOrderProblemInfoByOrderId(orderProblemInfo.getOrderId(), baseUser.getTenantId());
        for (OrderProblemInfo p : list) {
            if (p.getState().intValue() == SysStaticDataEnum.EXPENSE_STATE.CANCEL) {
                continue;
            }
            if (p.getProblemCondition().intValue() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                if (p.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.END && p.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN) {
                    hasAllAuit = false;
                } else {
                    if (SysStaticDataEnum.EXPENSE_STATE.END == p.getState().intValue() && (OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(p.getProblemType()) || OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(p.getProblemType()))) {
                        hasCancelOrder = true;
                    }
                }
            } else {
                if (p.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.END && p.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN) {
                    hasAllAuit = false;
                }
            }
        }
        if (OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType()) || OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {
            if (!hasAllAuit) {
                throw new BusinessException("还有异常未处理,不能毁单！");
            }
        }
        long orderId = orderFee.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        boolean isHis = false;
        if (orderInfo == null) {
            isHis = true;
        }
        if (!isHis && hasAllAuit && (hasCancelOrder || flag)) {
            //撤单 调用费用接口
            OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            OrderFeeExt feeExt = orderFeeExtService.getOrderFeeExt(orderId);
            OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderId);
            if (orderInfoExt.getPreAmountFlag() != null && orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                if (scheduler.getVehicleClass() != null
                        && (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                        && orderInfo.getToOrderId() != null && orderInfo.getToOrderId() > 0
                        && scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                    //外调车代收人模式 需同步撤单
                    OrderInfo tempOrder = orderInfoService.getOrder(orderInfo.getToOrderId());
                    if (tempOrder != null) {
                        //未完成才撤单
                        OrderFee tempOrderFee = orderFeeService.getOrderFee(orderInfo.getToOrderId());
                        OrderInfoExt tempOrderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getToOrderId());
                        OrderScheduler tempScheduler = orderSchedulerService.getOrderScheduler(orderInfo.getToOrderId());
                        if (tempOrderInfoExt.getPreAmountFlag() != null && tempOrderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                            //释放油卡押金
                            //this.reclaimOilCard(tempOrder.getOrderId(), tempOrder.getTenantId());
                            //外调车代收人模式 资金撤单
                            CancelTheOrderInDto cancelTheOrderIn = new CancelTheOrderInDto();
                            cancelTheOrderIn.setAmountFee(tempOrderFee.getPreCashFee() == null ? 0 : tempOrderFee.getPreCashFee());
                            cancelTheOrderIn.setArriveFee(tempOrderFee.getArrivePaymentFee() == null ? 0 : tempOrderFee.getArrivePaymentFee());
                            cancelTheOrderIn.setEntityOilFee(tempOrderFee.getPreOilFee() == null ? 0 : tempOrderFee.getPreOilFee());
                            cancelTheOrderIn.setEtcFee(tempOrderFee.getPreEtcFee() == null ? 0 : tempOrderFee.getPreEtcFee());
                            cancelTheOrderIn.setIsNeedBill(tempOrder.getIsNeedBill());
                            cancelTheOrderIn.setIsPayArriveFee(tempOrderFee.getArrivePaymentState());
                            cancelTheOrderIn.setOrderId(tempOrder.getOrderId());
                            cancelTheOrderIn.setTenantId(tempOrderFee.getTenantId());
                            cancelTheOrderIn.setUserId(tempScheduler.getCarDriverId());
                            cancelTheOrderIn.setVehicleAffiliation(tempOrderFee.getVehicleAffiliation());
                            cancelTheOrderIn.setVirtualOilFee(tempOrderFee.getPreOilVirtualFee() == null ? 0 : tempOrderFee.getPreOilVirtualFee());
                            updateOrderService.cancelTheOrder(cancelTheOrderIn, baseUser,accessToken);
                        }
                        //临时车队成本单撤单
                        orderInfoHService.canel(tempScheduler.getOrderId(), orderProblemInfo.getId(), true, accessToken);
                    }
                }
                if (IsTransit && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {//外发单 走社会车流程
                    //释放油卡押金
                    this.reclaimOilCard(orderId, baseUser.getTenantId(), baseUser,accessToken);
                    SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
                    if (tenantDef == null) {
                        log.error("未找到车队信息! 车队ID[" + orderInfo.getToTenantId() + "]");
                        throw new BusinessException("未找到车队信息!");
                    }
                    CancelTheOrderInDto cancelTheOrderIn = new CancelTheOrderInDto();
                    cancelTheOrderIn.setAmountFee(orderFee.getPreCashFee() == null ? 0 : orderFee.getPreCashFee());
                    cancelTheOrderIn.setArriveFee(orderFee.getArrivePaymentFee() == null ? 0 : orderFee.getArrivePaymentFee());
                    cancelTheOrderIn.setEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
                    cancelTheOrderIn.setEtcFee(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
                    cancelTheOrderIn.setIsNeedBill(orderInfo.getIsNeedBill());
                    cancelTheOrderIn.setIsPayArriveFee(orderFee.getArrivePaymentState());
                    cancelTheOrderIn.setOrderId(orderId);
                    cancelTheOrderIn.setTenantId(orderFee.getTenantId());
                    cancelTheOrderIn.setUserId(tenantDef.getAdminUser());
                    cancelTheOrderIn.setVehicleAffiliation(orderFee.getVehicleAffiliation());
                    cancelTheOrderIn.setVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
                    updateOrderService.cancelTheOrder(cancelTheOrderIn, baseUser,accessToken);
                } else {
                    if (scheduler.getVehicleClass() == null || (
                            (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                                    || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                                    || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                            )
                                    || (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                                    && (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT)))) {//自有车承包模式
                        //释放油卡押金
                        this.reclaimOilCard(orderId, baseUser.getTenantId(), baseUser,accessToken);
                        orderPayMethodService.cancelTheOrderTransit(orderFee, scheduler, orderInfo, baseUser,accessToken);
                    } else if (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
//                        IOrderFeeTF feeTF = (IOrderFeeTF) SysContexts.getBean("orderFeeTF");
                        if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
                            //撤回切换司机支付补贴
                            orderFeeService.cancelDriverSwitchSubsidy(orderId, baseUser,accessToken);
                        } else if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                            //撤回切换司机订单油
                            orderFeeService.cancelDriverSwitchOilFee(orderId, baseUser,accessToken);
                        }
                        //自有车资金撤单
                        updateOrderService.cancelTheOwnCarOrder(scheduler.getCarDriverId(), "0", orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee(), orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee(), feeExt.getPontage() == null ? 0 : feeExt.getPontage(), feeExt.getSalary() == null ? 0 : feeExt.getSalary(), feeExt.getCopilotSalary() == null ? 0 : feeExt.getCopilotSalary(), scheduler.getCopilotUserId() == null ? 0 : scheduler.getCopilotUserId(), orderId, orderInfo.getTenantId(), orderInfo.getIsNeedBill(), baseUser,accessToken);
                    }
                }
            } else {
                if (scheduler.getVehicleClass() != null
                        && (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                        && scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES
                        && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                    //外调车代收人模式 需同步撤单未支付预付款无需执行资金回退
                    OrderInfo tempOrder = orderInfoService.getOrder(orderInfo.getToOrderId());
                    if (tempOrder != null) {
                        //未完成才撤单
                        OrderScheduler tempScheduler = orderSchedulerService.getOrderScheduler(orderInfo.getToOrderId());
                        //临时车队成本单撤单
                        orderInfoHService.canel(tempScheduler.getOrderId(), orderProblemInfo.getId(), true, accessToken);
                    }
                }
            }
            //现有订单撤单
            orderInfoHService.canel(scheduler.getOrderId(), orderProblemInfo.getId(), true, accessToken);
        }
    }

    /**
     * 是否油卡押金
     *
     * @param orderId  订单号
     * @param tenantId 车队ID
     * @throws Exception
     */
    private void reclaimOilCard(long orderId, long tenantId, LoginInfo loginInfo,String token) {
        List<OrderOilCardInfo> cards = orderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);
        //释放油卡押金
        if (cards != null && cards.size() > 0) {
            for (OrderOilCardInfo orderOilCardInfo : cards) {
                List<OilCardManagement> oilCards = oilCardManagementService.getOilCardManagementByCard(orderOilCardInfo.getOilCardNum(), tenantId);
                if (oilCards != null && oilCards.size() > 0) {
                    OilCardManagement oilCard = oilCards.get(0);
                    if (oilCard.getPledgeOrderId() != null && oilCard.getPledgeOrderId() > 0) {
                        oilCardManagementService.reclaim(oilCard.getPledgeOrderId() == null ? 0 : oilCard.getPledgeOrderId(), oilCard.getId(), tenantId, loginInfo,token);
                    } else {
                        continue;
                    }
                }
            }
        }
    }

}
