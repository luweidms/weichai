package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderFeeStatementHService;
import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IProblemVerOptService;
import com.youming.youche.order.api.order.other.IOrderPayMethodService;
import com.youming.youche.order.api.order.other.IUpdateOrderService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderFeeStatement;
import com.youming.youche.order.domain.order.OrderFeeStatementH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.ProblemVerOpt;
import com.youming.youche.order.dto.CancelTheOrderInDto;
import com.youming.youche.order.dto.OrderInfoListDto;
import com.youming.youche.order.dto.OrderProblemInfoDto;
import com.youming.youche.order.dto.OrderProblemInfoOutDto;
import com.youming.youche.order.dto.SaveProblemInfoDto;
import com.youming.youche.order.provider.mapper.order.OrderInfoHMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderProblemInfoMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.vo.QueryOrderProblemInfoQueryVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>
 * ????????????????????? ???????????????
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OrderProblemInfoServiceImpl extends BaseServiceImpl<OrderProblemInfoMapper, OrderProblemInfo> implements IOrderProblemInfoService {

    @Resource
    private OrderProblemInfoMapper orderProblemInfoMapper;

    @Resource
    OrderInfoMapper orderInfoMapper;
    @Resource
    OrderInfoHMapper orderInfoHMapper;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Resource
    IOrderSchedulerService orderSchedulerService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;

    @Resource
    private IOrderFeeService orderFeeService;
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
    private IOrderInfoExtService orderInfoExtService;
    @Resource
    private IOrderFeeStatementHService orderFeeStatementHService;
    @Resource
    private IOrderFeeStatementService orderFeeStatementService;
    @DubboReference(version = "1.0.0")
    ISysStaticDataService sysStaticDataService;
    @Resource
    private IPayFeeLimitService payFeeLimitService;
    @DubboReference(version = "1.0.0")
    IAuditService auditService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogServicesystem;
    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;
    @Resource
    private IProblemVerOptService problemVerOptService;
    @Resource
    private IOrderOpRecordService orderOpRecordService;
    @Resource
    private IOrderFeeExtHService orderFeeExtHService;
    @Resource
    private IOrderFeeExtService orderFeeExtService;
    @Resource
    private ReadisUtil orderRedisUtil;
    @Resource
    private IOrderAgingInfoService orderAgingInfoService;

    @Resource
    private LoginUtils loginUtils;
    @Resource
    private IOrderAccountService orderAccountService;
    @Resource
    private IOrderLimitService orderLimitService;
    @Resource
    private IUpdateOrderService updateOrderService;
    @Resource
    private IOrderPayMethodService orderPayMethodService;


    @Override
    public List<OrderProblemInfo> getOrderProblemInfoByOrderId(Long orderId, Long tenantId) {

        QueryWrapper<OrderProblemInfo> orderProblemInfoQueryWrapper = new QueryWrapper<>();
        orderProblemInfoQueryWrapper.eq("order_id", orderId)
                .eq("tenant_id", tenantId);
        List<OrderProblemInfo> orderProblemInfos = orderProblemInfoMapper.selectList(orderProblemInfoQueryWrapper);
        return orderProblemInfos;
    }

    @Override
    public boolean isExistProblemInfoInfo(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("???????????????!");
        }
        QueryWrapper<OrderProblemInfo> orderProblemInfoQueryWrapper = new QueryWrapper<>();
        orderProblemInfoQueryWrapper.eq("order_id", orderId)
                .in("state", new Integer[]{SysStaticDataEnum.EXPENSE_STATE.AUDIT,
                        SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, SysStaticDataEnum.EXPENSE_STATE.END})
                .eq("problem_condition", SysStaticDataEnum.PROBLEM_CONDITION.COST)
                .orderByDesc("create_time");
        List<OrderProblemInfo> orderProblemInfos = orderProblemInfoMapper.selectList(orderProblemInfoQueryWrapper);
        if (orderProblemInfos != null && orderProblemInfos.size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * ??????????????????
     *
     * @param tenantId    ???????????????ID
     * @param orderId     ???????????????ID
     * @param fromOrderId ???????????????ID
     * @throws Exception
     */
    @Override
    public void saveTransferInfoProblem(Long tenantId, Long orderId, Long fromOrderId) throws Exception {
        SysTenantDef tenantDef = sysTenantDefService.get(tenantId);
        List<OrderProblemInfo> orderProblemInfos = orderProblemInfoMapper.selectList(new QueryWrapper<OrderProblemInfo>().eq("order_id", orderId).eq("state", SysStaticDataEnum.EXPENSE_STATE.END).eq("problem_condition", SysStaticDataEnum.PROBLEM_CONDITION.COST));
        if (orderProblemInfos != null && orderProblemInfos.size() > 0) {
            for (OrderProblemInfo orderProblemInfo : orderProblemInfos) {
                OrderProblemInfo info = new OrderProblemInfo();
                BeanUtils.copyProperties(info, orderProblemInfo);
                info.setId(CommonUtil.createSoNbr());
                info.setOrderId(orderId);
                info.setState(SysStaticDataEnum.EXPENSE_STATE.END);
                info.setVerifyDate(LocalDateTimeUtil.now());
                info.setRecordUserId(orderProblemInfo.getTenantId());
                info.setCarOwnerId(tenantDef.getAdminUser());
                info.setCarOwnerName(tenantDef.getLinkMan());
                info.setCarOwnerPhone(tenantDef.getLinkPhone());
                info.setVerifyDesc(null);
                info.setProblemCondition(SysStaticDataEnum.PROBLEM_CONDITION.INCOME);
                info.setReportSts(0);
                info.setReportDate(null);
                info.setAppealReason(null);
                info.setSourceProblem(SysStaticDataEnum.SOURCE_PROBLEM.PUSH);
                info.setProblemSourceId(orderProblemInfo.getId());
                info.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                OrderInfo orderInfo = orderInfoMapper.selectOne(new QueryWrapper<OrderInfo>().eq("order_id", info.getOrderId()));
                if (orderInfo != null) {
                    info.setTenantId(orderInfo.getTenantId());
                    saveOrUpdate(info);
                    orderProblemInfo.setProblemSourceId(info.getId());
                    saveOrUpdate(orderProblemInfo);
                    //????????????????????????
                    orderFeeService.updateOrderExceptionPrice(info, info.getProblemDealPrice());
                }
            }
        }
    }

    @Override
    public Page<OrderProblemInfo> queryOrderProblemInfoPag(Long orderId, Long problemId, String state, String problemType, Integer problemCondition, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<OrderProblemInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (loginInfo != null) {
            queryWrapper.eq(OrderProblemInfo::getTenantId, loginInfo.getTenantId());
        }
        if (orderId != null && orderId > 0) {
            queryWrapper.eq(OrderProblemInfo::getOrderId, orderId);
        }
        queryWrapper.eq(problemId != null && problemId > 0, OrderProblemInfo::getId, problemId);
        if (StringUtils.isNotBlank(state)) {
            String[] states = state.split(",");
            Object[] statesArr = new Object[states.length];
            for (int i = 0; i < states.length; i++) {
                statesArr[i] = Integer.parseInt(states[i]);
            }
            queryWrapper.in(OrderProblemInfo::getState, statesArr);
        }
        if (StringUtils.isNotBlank(problemType)) {
            queryWrapper.eq(OrderProblemInfo::getProblemType, problemType);
        }
        if (problemCondition != null && problemCondition > 0) {
            queryWrapper.eq(OrderProblemInfo::getProblemCondition, problemCondition);
        }
        queryWrapper.orderByDesc(OrderProblemInfo::getCreateTime);
        Page<OrderProblemInfo> orderProblemInfoPage = new Page<>(pageNum, pageSize);
        super.page(orderProblemInfoPage, queryWrapper);
        if (orderProblemInfoPage != null && orderProblemInfoPage.getRecords() != null && orderProblemInfoPage.getRecords().size() > 0) {
            List<OrderProblemInfo> list = orderProblemInfoPage.getRecords();
            List<Long> busiIdList = new ArrayList<Long>();
            for (OrderProblemInfo orderProblemInfo : list) {
                busiIdList.add(orderProblemInfo.getId());
            }
            Map<Long, Boolean> map = iAuditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.PROBLEM_CODE, busiIdList, accessToken);
            for (OrderProblemInfo orderProblemInfo : list) {
                orderProblemInfo.setIsJurisdiction(map.get(orderProblemInfo.getId()));
                orderProblemInfo.setStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getState() + "").getCodeName());
                orderProblemInfo.setProblemTypeName(getSysStaticData(EnumConsts.SysStaticData.COST_PROBLEM_TYPE, orderProblemInfo.getProblemType() + "").getCodeName());
                orderProblemInfo.setSourceProblemName(getSysStaticData(EnumConsts.SysStaticData.SOURCE_PROBLEM, orderProblemInfo.getSourceProblem() + "").getCodeName());
                orderProblemInfo.setProblemExternalStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getProblemExternalState() + "").getCodeName());
                if (orderProblemInfo.getChargeType() != null && orderProblemInfo.getChargeType().intValue() > 0) {
                    orderProblemInfo.setChargeTypeName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getProblemExternalState() + "").getCodeName());
                }
            }
        }
        return orderProblemInfoPage;
    }

    @Override
    public OrderInfoListDto getOrder(Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderScheduler orderScheduler = new OrderScheduler();
        boolean isHis = false;
        if (orderInfo == null) {
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("???????????????[" + orderId + "]???????????????????????????");
            }
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            orderInfo = new OrderInfo();
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            isHis = true;
        } else {
            orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        }

        OrderInfoListDto orderInfoListDto = new OrderInfoListDto();
        orderInfoListDto.setOrderId(orderInfo.getOrderId());
        orderInfoListDto.setOrderStateCode(orderInfo.getOrderState());
        orderInfoListDto.setOrderState(getSysStaticData(
                EnumConsts.SysStaticData.ORDER_STATE, String.valueOf(orderInfo.getOrderState())).getCodeName());
        orderInfoListDto.setOrderType(getSysStaticData(
                EnumConsts.SysStaticData.ORDER_TYPE, String.valueOf(orderInfo.getOrderType())).getCodeName());
        orderInfoListDto.setPaymentWay(orderInfoExt.getPaymentWay());
        orderInfoListDto.setArriveTime(orderScheduler.getArriveTime());
        orderInfoListDto.setIsTransit(orderInfo.getIsTransit());
        orderInfoListDto.setDependTime(orderScheduler.getDependTime());
        orderInfoListDto.setSourceName(orderScheduler.getSourceName());
        orderInfoListDto.setCarDriverPhone(orderScheduler.getCarDriverPhone());
        orderInfoListDto.setCarDriverMan(orderScheduler.getCarDriverMan());
        Boolean isAddProblem = false;
        if (isHis) {//?????????????????????
            Map<Long, Boolean> finalExpireMap = new ConcurrentHashMap<Long, Boolean>();
            List<Long> busiIdList = new ArrayList<>();
            busiIdList.add(orderInfo.getOrderId());
            finalExpireMap = orderSchedulerService.hasFinalOrderLimit(busiIdList);
            if (finalExpireMap != null) {
                isAddProblem = finalExpireMap.get(orderInfo.getOrderId());
            }
            isAddProblem = isAddProblem == null ? false : isAddProblem;
        } else {
            if (orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.TO_BE_LOAD) {
                isAddProblem = true;
            }
        }
        orderInfoListDto.setAddProblem(isAddProblem);
        //???????????????
        if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == 1) {
            //??????????????????
            Long tenantId = orderInfo.getToTenantId();
            if (tenantId != null && tenantId > 0) {
                Map tenant = sysTenantDefService.getTenantInfo(tenantId);
                if (tenant != null) {
                    orderInfoListDto.setReceiveTenantId(tenantId);
                    orderInfoListDto.setReceiveTenantName(DataFormat.getStringKey(tenant, "tenantName"));
                }
            }
        }

        //??????????????????
        Long tenantId = orderInfo.getFromTenantId();
        if (tenantId != null && tenantId > 0) {
            Map tenant = sysTenantDefService.getTenantInfo(tenantId);
            if (tenant != null) {
                orderInfoListDto.setFromTenantId(tenantId);
                orderInfoListDto.setFromTenantName(DataFormat.getStringKey(tenant, "tenantName"));
            }
        }
        //????????????
        orderInfoListDto.setSourceName(getSysStaticData(
                EnumConsts.SysStaticData.ORDER_STATE, String.valueOf(orderInfo.getOrderState())).getCodeName());
        //????????????
        orderInfoListDto.setOrderTypeName(getSysStaticData(
                EnumConsts.SysStaticData.ORDER_TYPE, String.valueOf(orderInfo.getOrderType())).getCodeName());
        return orderInfoListDto;
    }

    @Override
    public OrderProblemInfoDto getOrderProblemInfo(Long problemId) {
        OrderProblemInfo orderProblemInfo = orderProblemInfoMapper.selectById(problemId);
        if (orderProblemInfo == null) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        OrderProblemInfoDto dto = new OrderProblemInfoDto();
        BeanUtils.copyProperties(orderProblemInfo, dto);
        //????????????
        dto.setProblemSourceName(getSysStaticData(
                EnumConsts.SysStaticData.SOURCE_PROBLEM, String.valueOf(orderProblemInfo.getProblemSourceId())).getCodeName());
        //??????????????????
        dto.setProblemTypeDesc(getSysStaticData(
                orderProblemInfo.getProblemCondition() == 1 ? EnumConsts.SysStaticData.COST_PROBLEM_TYPE : EnumConsts.SysStaticData.INCOME_PROBLEM_TYPE
                , String.valueOf(orderProblemInfo.getProblemType()))
                .getCodeDesc());
        dto.setProblemTypeName(getSysStaticData(EnumConsts.SysStaticData.COST_PROBLEM_TYPE, orderProblemInfo.getProblemType() + "").getCodeName());
        dto.setStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getState() + "").getCodeName());

        return dto;
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }

        return new SysStaticData();
    }

    @Override
    public List<ProblemVerOpt> getProblemVerOptByProId(Long problemId, String accessToken) {
        if (problemId == null || problemId < 0) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderProblemInfo orderProblemInfo = this.getById(problemId);
        List<ProblemVerOpt> problemVerOpts = problemVerOptService.getProblemVerOptByProId(problemId,null);
        if (orderProblemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                || orderProblemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.AUDIT) {
            List<SysOperLog> list = sysOperLogServicesystem.querySysOperLog(SysOperLogConst.BusiCode.ProblemInfo, problemId, false, orderProblemInfo.getTenantId(), AuditConsts.AUDIT_CODE.PROBLEM_CODE, null);
            if (list != null && list.size() > 0) {
                SysOperLog sysOperLog = list.get(0);
                if (sysOperLog.getOperType().intValue() == SysOperLogConst.OperType.AuditUser.getCode()) {
                    if (problemVerOpts == null) {
                        problemVerOpts = new ArrayList<>();
                    }
                    ProblemVerOpt verOpt = new ProblemVerOpt();
                    verOpt.setProblemId(problemId);
                    verOpt.setCheckUserName(sysOperLog.getOperComment());
                    problemVerOpts.add(0, verOpt);
                }
            }
        }
        return problemVerOpts;
    }


//***************************************  ?????????    *************************************************************/

    @Override
    @Transactional
    public boolean  saveOrUpdateOrderProblemInfo(SaveProblemInfoDto saveProblemInfoDto, String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        //??????????????????
        Long orderId = saveProblemInfoDto.getOrderId();
        Long problemId = saveProblemInfoDto.getProblemId();
        Integer problemType = saveProblemInfoDto.getProblemType();
        Long carOwnerId = saveProblemInfoDto.getCarOwnerId();
        String carOwnerName = saveProblemInfoDto.getCarOwnerName();
        String carOwnerPhone = saveProblemInfoDto.getCarOwnerPhone();
        Integer problemCondition = saveProblemInfoDto.getProblemCondition();
        String problemPriceString = saveProblemInfoDto.getProblemPrice();
        String problemDesc = saveProblemInfoDto.getProblemDesc();
        String responsiblePartyName = saveProblemInfoDto.getResponsiblePartyName();
        Long problemPrice = 0L;
        if (orderId <= 0) {
            throw new BusinessException("????????????????????????????????????");
        }

        if (StringUtils.isEmpty(problemPriceString)) {
            throw new BusinessException("????????????????????????");
        } else if (!CommonUtil.isNumber(problemPriceString)) {
            throw new BusinessException("?????????????????????????????????");
        } else {
            Double convertNum = Double.parseDouble(problemPriceString) * 100;
            problemPrice = convertNum.longValue();
        }
        if (problemType <= 0) {
            throw new BusinessException("????????????????????????");
        }

        if (problemCondition <= 0) {
            throw new BusinessException("?????????????????????");
        }

        if (problemCondition != SysStaticDataEnum.PROBLEM_CONDITION.COST && problemCondition != SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
            throw new BusinessException("?????????????????????");
        }
        //????????????????????????????????????
        if (OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(problemType + "") || OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(problemType + "")) {
            List<OrderProblemInfo> proList = this.getOrderProblemInfoByOrderId(orderId, loginInfo.getTenantId());
            if (proList != null && proList.size() > 0) {
                for (OrderProblemInfo op : proList) {
                    if (op.getProblemType() != null
                            && (op.getProblemCondition() != null && op.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST)
                            && (op.getState() != SysStaticDataEnum.EXPENSE_STATE.CANCEL && op.getState() != SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN)
                            && (op.getProblemType().equals(OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER) || op.getProblemType().equals(OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER))) {
                        throw new BusinessException("??????????????????,??????????????????!");
                    }
                }
            }
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("??????????????????[" + orderId + "]?????????");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(orderId);
            BeanUtils.copyProperties(orderInfo, orderInfoH);
            BeanUtils.copyProperties(orderScheduler, orderSchedulerH);
            BeanUtils.copyProperties(orderInfoExt, orderInfoExtH);
            BeanUtils.copyProperties(orderFee, orderFeeH);
            isHis = true;
            Map<Long, Boolean> finalExpireMap = new ConcurrentHashMap<Long, Boolean>();
            List<Long> busiIdList = new ArrayList<>();
            busiIdList.add(orderId);
            finalExpireMap = orderSchedulerService.hasFinalOrderLimit(busiIdList);
            Boolean isAddProblem = finalExpireMap.get(orderInfo.getOrderId());
            isAddProblem = isAddProblem == null ? false : isAddProblem;
            if (!isAddProblem) {
                throw new BusinessException("???????????????????????????????????????");
            }

        } else {
            orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            orderFee = orderFeeService.getOrderFee(orderId);
        }
        if (carOwnerId == null) {
            carOwnerId = 0L;
        }
        if (carOwnerId <= 0) {
            carOwnerId = orderScheduler.getCarDriverId();
            carOwnerName = orderScheduler.getCarDriverMan();
            carOwnerPhone = orderScheduler.getCarDriverPhone();
        }
        if ((OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(problemType + "")
                || OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(problemType + "")) && isHis) {
            throw new BusinessException("???????????????????????????????????????");
        }
        if (orderInfo.getOrderState() <= OrderConsts.ORDER_STATE.DISPATCH_ING) {
            throw new BusinessException("?????????[" + orderId + "]??????????????????????????????");
        }
        if (isHis) {
            OrderFeeStatementH feeStatement = orderFeeStatementHService.getOrderFeeStatementH(orderId);
            if (feeStatement != null) {
                if (!StringUtils.isEmpty(feeStatement.getBillNumber()) && problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
                    throw new BusinessException("??????BL??????????????????????????????");
                }
            }
        } else {
            OrderFeeStatement feeStatement = orderFeeStatementService.getOrderFeeStatementById(orderId);
            if (feeStatement != null) {
                if (!StringUtils.isEmpty(feeStatement.getBillNumber()) && problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
                    throw new BusinessException("??????BL??????????????????????????????");
                }
            }
        }
        String codeType = SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE;
        List<SysStaticData> lists = sysStaticDataService.get(codeType);
        Long price = null;
        boolean isReduce = false;//???????????????
        for (SysStaticData sysStaticData : lists) {
            if (sysStaticData.getCodeValue() == null || problemType != Integer.parseInt(sysStaticData.getCodeValue())) {
                continue;
            }

            if (EnumConsts.Exception_Deal_Type.ADDMONEY.getType() == sysStaticData.getCodeId()) {
                price = Math.abs(problemPrice);
                break;
            } else if (EnumConsts.Exception_Deal_Type.REDUCEMONEY.getType() == sysStaticData.getCodeId()) {
                isReduce = true;
                price = -(Math.abs(problemPrice));
                break;
            }
        }

        if (lists == null || lists.isEmpty() || price == null) {
            throw new BusinessException("?????????????????????");
        }
        if (problemId!=null && problemId >= 0) {
            OrderProblemInfo orderProblemInfo = this.getById(problemId);
            if (orderProblemInfo == null) {
                log.error("????????????????????????????????????????????????ID[" + problemId + "]");
                throw new BusinessException("??????????????????????????????????????????");
            }
            this.judgePayFeeLimit(problemCondition, isReduce, orderScheduler, problemPrice, price, loginInfo.getTenantId());
            //???????????????  ????????????
            if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.COST && isReduce) {
                orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, price, -1L, problemId);
            }
            //????????????
            orderProblemInfo.setProblemPrice(price);
            orderProblemInfo.setProblemType(problemType + "");
            orderProblemInfo.setProblemDesc(problemDesc);
            //??????????????????
            if (problemPrice > 0) {
                orderProblemInfo.setChargeType(SysStaticDataEnum.CHARGE_TYPE.GOODER_DUTY);//1??????????????? 2???????????????
            } else {
                orderProblemInfo.setChargeType(SysStaticDataEnum.CHARGE_TYPE.CARER_DUTY);//1??????????????? 2???????????????
            }
            orderProblemInfo.setOpId(loginInfo.getId());
            //??????????????????????????????????????????
            if (orderProblemInfo.getProblemCondition() != problemCondition.intValue()) {
                if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                    orderInfo.setNoAuditExcNumIn((orderInfo.getNoAuditExcNumIn() == null ? 0 : (orderInfo.getNoAuditExcNumIn()) - 1 < 0 ? 0 : (orderInfo.getNoAuditExcNumIn()) - 1));
                    orderInfo.setNoAuditExcNum((orderInfo.getNoAuditExcNum() == null ? 0 : orderInfo.getNoAuditExcNum()) + 1);
                } else if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
                    orderInfo.setNoAuditExcNum((orderInfo.getNoAuditExcNum() == null ? 0 : orderInfo.getNoAuditExcNum()) - 1 < 0 ? 0 : (orderInfo.getNoAuditExcNum()) - 1);
                    orderInfo.setNoAuditExcNumIn((orderInfo.getNoAuditExcNumIn() == null ? 0 : orderInfo.getNoAuditExcNumIn()) + 1);
                }
                if (isHis) {
                    OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
                    orderInfoH.setNoAuditExcNum(orderInfo.getNoAuditExcNum());
                    orderInfoH.setNoAuditExcNumIn(orderInfo.getNoAuditExcNumIn());
                    orderInfoHService.saveOrUpdate(orderInfoH);
                } else {
                    orderInfoService.saveOrUpdate(orderInfo);
                }
            }
            orderProblemInfo.setCarOwnerId(carOwnerId);
            orderProblemInfo.setCarOwnerName(carOwnerName);
            orderProblemInfo.setCarOwnerPhone(carOwnerPhone);
            orderProblemInfo.setProblemCondition(problemCondition);
            orderProblemInfo.setResponaiblePartyName(responsiblePartyName);
            this.saveOrUpdate(orderProblemInfo);
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("busiId", orderProblemInfo.getId());
            boolean isAudit = auditService.startProcess(AuditConsts.AUDIT_CODE.PROBLEM_CODE, orderProblemInfo.getId(), SysOperLogConst.BusiCode.ProblemInfo, params, accessToken, loginInfo.getTenantId());
            StringBuffer buffer = new StringBuffer("[" + loginInfo.getName() + "]??????????????????");
            //????????????????????????
            sysOperLogServicesystem.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OrderInfo, orderId, com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, buffer.toString(), loginInfo.getTenantId());

            if (!isAudit) {//????????? ??????????????????
                this.verifyPass(orderProblemInfo.getId(), "???????????????,??????", orderProblemInfo.getProblemPrice() + "", true, null, loginInfo, accessToken);
            }
            return  false;
        } else {
            this.judgePayFeeLimit(problemCondition, isReduce, orderScheduler, problemPrice, price, loginInfo.getTenantId());
            OrderProblemInfo orderProblemInfo = new OrderProblemInfo();
            orderProblemInfo.setId(CommonUtil.createSoNbr());
            orderProblemInfo.setTenantId(loginInfo.getTenantId());
            orderProblemInfo.setState(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
            orderProblemInfo.setOrderId(orderId);
            orderProblemInfo.setProblemPrice(price);
            orderProblemInfo.setProblemType(problemType + "");
            orderProblemInfo.setProblemDesc(problemDesc);
            orderProblemInfo.setCreateTime(LocalDateTime.now());
            orderProblemInfo.setRecordUserId(loginInfo.getUserInfoId());
            orderProblemInfo.setCarOwnerId(carOwnerId);
            orderProblemInfo.setCarOwnerName(carOwnerName);
            orderProblemInfo.setCarOwnerPhone(carOwnerPhone);
            orderProblemInfo.setProblemCondition(problemCondition);
            orderProblemInfo.setResponsiblePartyName(responsiblePartyName);
            orderProblemInfo.setResponaiblePartyName(responsiblePartyName);
            orderProblemInfo.setSourceProblem(SysStaticDataEnum.SOURCE_PROBLEM.INSIDE);
            orderProblemInfo.setUpdateTime(LocalDateTime.now());
            //?????????????????? 1??????????????? 2???????????????
            orderProblemInfo.setChargeType(problemPrice > 0 ? SysStaticDataEnum.CHARGE_TYPE.GOODER_DUTY : SysStaticDataEnum.CHARGE_TYPE.CARER_DUTY);

            if (orderProblemInfo.getProblemType().equals(OrderConsts.PROBLEM_TYPE.CARRIER_BREAK_CONTRACT) || orderProblemInfo.getProblemType().equals(OrderConsts.PROBLEM_TYPE.SHIPPER_BREAK_CONTRACT) || orderProblemInfo.getProblemType().equals(OrderConsts.PROBLEM_TYPE.FREIGHT_LOSS_DAMAGE)) {
                //??????????????????????????? ????????????
                if (orderProblemInfo.getChargeType() == SysStaticDataEnum.CHARGE_TYPE.CARER_DUTY && orderInfo.getOrderState() != null && OrderConsts.ORDER_STATE.DISPATCH_ING == orderInfo.getOrderState()) {
                    throw new BusinessException("??????????????????????????????????????????");
                }
            }
            //???????????????  ????????????
            if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.COST && isReduce) {
                orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, price, -1L, -1L);
            }
            StringBuffer buffer = new StringBuffer("[" + loginInfo.getName() + "]????????????");
            if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                orderInfo.setNoAuditExcNum((orderInfo.getNoAuditExcNum() == null ? 0 : orderInfo.getNoAuditExcNum()) + 1);
            } else if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
                orderInfo.setNoAuditExcNumIn((orderInfo.getNoAuditExcNumIn() == null ? 0 : orderInfo.getNoAuditExcNumIn()) + 1);
            }
            if (isHis) {
                OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
                orderInfoH.setNoAuditExcNum(orderInfo.getNoAuditExcNum());
                orderInfoH.setNoAuditExcNumIn(orderInfo.getNoAuditExcNumIn());
                orderInfoHService.saveOrUpdate(orderInfoH);
            } else {
                orderInfoService.saveOrUpdate(orderInfo);
            }
            //????????????????????????
            sysOperLogServicesystem.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OrderInfo, orderId, com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, buffer.toString(), loginInfo.getTenantId());

            orderProblemInfo.setOpId(loginInfo.getId());
            this.saveOrUpdate(orderProblemInfo);
            Map<String, Object> params = new ConcurrentHashMap<String, Object>();
            params.put("busiId", orderProblemInfo.getId());
            boolean isAudit = auditService.startProcess(AuditConsts.AUDIT_CODE.PROBLEM_CODE, orderProblemInfo.getId(), SysOperLogConst.BusiCode.ProblemInfo, params, accessToken, loginInfo.getTenantId());
            if (!isAudit) {//????????? ??????????????????
                this.verifyPass(orderProblemInfo.getId(), "???????????????,??????", orderProblemInfo.getProblemPrice() + "", true, null, loginInfo, accessToken);
            }
            return true;
        }


    }

    /**
     * ??????????????????
     *
     * @param problemCondition
     * @param isReduce
     * @param orderScheduler
     * @param problemPrice
     * @param absPrice
     */
    private void judgePayFeeLimit(int problemCondition, boolean isReduce, OrderScheduler orderScheduler, Long problemPrice, Long absPrice, Long tenantId) {
        if (!isReduce) {
            if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.COST) { //????????????
                payFeeLimitService.judgePayFeeLimit(problemPrice, EnumConsts.PAY_FEE_LIMIT.TYPE1, EnumConsts.PAY_FEE_LIMIT.SUB_TYPE2, orderScheduler.getTenantId());
            } else if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
                payFeeLimitService.judgePayFeeLimit(problemPrice, EnumConsts.PAY_FEE_LIMIT.TYPE1, EnumConsts.PAY_FEE_LIMIT.SUB_TYPE3, orderScheduler.getTenantId());
            }
            //???????????????
            if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {//????????????
                    long maxProPrice = payFeeLimitService.getAmountLimitCfgVal(tenantId, SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_INMOME_EXCEPT_103);
                    if (maxProPrice > 0 && absPrice > maxProPrice) {
                        throw new BusinessException("??????????????????????????????" + CommonUtil.getDoubleFormatLongMoney(maxProPrice, 2) + "???");
                    }
                } else if (problemCondition == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                    long maxProPrice = payFeeLimitService.getAmountLimitCfgVal(tenantId, SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_COST_EXCEPT_102);
                    if (maxProPrice > 0 && absPrice > maxProPrice) {
                        throw new BusinessException("??????????????????????????????" + CommonUtil.getDoubleFormatLongMoney(maxProPrice, 2) + "???");
                    }
                }
            }
        }
    }

    /**
     * ????????????
     *
     * @param problemId        ??????ID
     * @param verifyDesc       ????????????
     * @param problemDealPrice ????????????
     * @param isSave           ?????????????????????
     * @throws Exception
     */
    @Override
    public void verifyPass(Long problemId, String verifyDesc, String problemDealPrice, Boolean isSave, String nodeNum, LoginInfo loginInfo, String accessToken) {

        if (problemId == null || problemId <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        OrderProblemInfo problemInfo = this.getById(problemId);
        if (problemInfo == null) {
            log.error("????????????????????????????????????????????????ID[" + problemId + "]");
            throw new BusinessException("??????????????????????????????????????????");
        }

        Long orderId = problemInfo.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderFee orderFee = new OrderFee();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderScheduler orderScheduler = new OrderScheduler();
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("??????????????????[" + orderId + "]?????????");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(orderId);
            BeanUtils.copyProperties(orderInfo, orderInfoH);
            BeanUtils.copyProperties(orderScheduler, orderSchedulerH);
            BeanUtils.copyProperties(orderInfoExt, orderInfoExtH);
            BeanUtils.copyProperties(orderFee, orderFeeH);
            isHis = true;
            Map<Long, Boolean> finalExpireMap = new ConcurrentHashMap<Long, Boolean>();
            List<Long> busiIdList = new ArrayList<>();
            busiIdList.add(orderId);
            finalExpireMap = orderSchedulerService.hasFinalOrderLimit(busiIdList);
            Boolean isAddProblem = finalExpireMap.get(orderInfo.getOrderId());
            isAddProblem = isAddProblem == null ? false : isAddProblem;
            if (!isAddProblem) {
                throw new BusinessException("?????????????????????????????????");
            }
        } else {
            orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            orderFee = orderFeeService.getOrderFee(orderId);
        }
        //??????????????????
        ProblemVerOpt verOpt = new ProblemVerOpt();
        verOpt.setProblemId(problemId);
        verOpt.setCheckUserId(loginInfo.getUserInfoId());
        verOpt.setCheckUserName(loginInfo.getName());
        verOpt.setTenantId(loginInfo.getTenantId());
        verOpt.setProblemState(problemInfo.getState());
        if (StringUtils.isNotBlank(verifyDesc)) {
            verOpt.setCheckRemark(verifyDesc);
            problemInfo.setVerifyDesc(verifyDesc);
        }
        if (isSave) {//???????????? ??????????????????
            problemInfo.setProblemDealPrice(problemInfo.getProblemPrice());
        }
        //????????????
        problemInfo.setVerifyDate(LocalDateTime.now());
        problemInfo.setState(SysStaticDataEnum.EXPENSE_STATE.END);
        if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
            this.saveOrUpdate(problemInfo);
            //?????????????????? ????????????????????????
            if (!isHis || (problemInfo.getProblemDealPrice() > 0)) {
//                feeTF.synPayCenter(orderId,accessToken,null);
            }
        }
        if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) { //????????????
            com.youming.youche.commons.domain.SysStaticData data = orderRedisUtil.getSysStaticData(SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE, problemInfo.getProblemType());
            if (data != null && data.getCodeId() == 1) {
                //???????????????  ????????????
                orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, problemInfo.getProblemDealPrice(), -1L, problemId);
            }
            //??????????????????????????????
            orderInfo.setNoAuditExcNum((orderInfo.getNoAuditExcNum() == null ? 0 : orderInfo.getNoAuditExcNum()) - 1);
            if (isHis) {
                OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
                orderInfoH.setNoAuditExcNum(orderInfo.getNoAuditExcNum());
                orderInfoHService.saveOrUpdate(orderInfoH);
            } else {
                orderInfoService.saveOrUpdate(orderInfo);
            }
            //?????????????????????????????????????????????????????????????????????????????? ???????????????????????????????????????????????????
            if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {//?????????
                Boolean isPush = true;
                if (problemInfo.getSourceProblem() != null
                        && problemInfo.getSourceProblem()
                        == SysStaticDataEnum.SOURCE_PROBLEM.PUSH) {//????????????????????????
                    OrderProblemInfo problemSourceInfo = this.getById(problemInfo.getProblemSourceId());
                    //????????????????????? ?????????????????????
                    if (problemSourceInfo != null) {
                        problemSourceInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                        this.saveOrUpdate(problemSourceInfo);
                    }
                    isPush = false;//?????????
                }
                if (orderInfo.getToOrderId() == null || orderInfo.getToOrderId() <= 0) {
                    isPush = false;//?????????
                }
                if (isPush) {//????????????
                    OrderProblemInfo info = new OrderProblemInfo();
                    BeanUtils.copyProperties(info, problemInfo);
                    SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
                    info.setId(CommonUtil.createSoNbr());
                    info.setOrderId(orderInfo.getToOrderId());
                    info.setState(SysStaticDataEnum.EXPENSE_STATE.END);
                    info.setVerifyDate(LocalDateTime.now());
                    info.setRecordUserId(loginInfo.getTenantId());
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
                        this.saveOrUpdate(info);
                        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                        params.put("busiId", info.getId());
                        problemInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                        problemInfo.setProblemSourceId(info.getId());
                        orderFeeService.updateOrderExceptionPrice(info, info.getProblemDealPrice());
                    }
                }
                //????????????????????????
                commonExceptionFlowPath(problemInfo, orderFee, true, accessToken, loginInfo);
            } else {
                //???????????????????????? || ???????????????C ??????????????????????????????
                commonExceptionFlowPath(problemInfo, orderFee, false, accessToken, loginInfo);
            }
            if (orderInfo != null && orderScheduler != null) {
                //??????????????????????????????   ????????? ?????? ???C??????????????????????????????
                if (orderScheduler.getVehicleClass() != null && (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        || ((orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                        && (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0)))) {
                    Map<String, String> paraMap = new HashMap<String, String>();
                    paraMap.put("tenantName", orderInfo.getTenantName());
                    paraMap.put("orderId", orderInfo.getOrderId() + "");
                    SysSmsSend sysSmsSend = new SysSmsSend();
                    sysSmsSend.setBillId(orderScheduler.getCarDriverPhone());
                    sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT);
                    sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.ORDER_PRO_AGING);
                    sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.ORDER_EXCEPTION));
                    sysSmsSend.setObjId(orderInfo.getOrderId() + "");
                    sysSmsSend.setParamMap(paraMap);
                    sysSmsSendService.sendSms(sysSmsSend);
                }
            }
        } else if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
            //????????????????????????????????????
            orderInfo.setNoAuditExcNumIn((orderInfo.getNoAuditExcNumIn() == null ? 0 : orderInfo.getNoAuditExcNumIn()) - 1);
            if (isHis) {
                OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
                orderInfoH.setNoAuditExcNumIn(orderInfo.getNoAuditExcNumIn());
                orderInfoHService.saveOrUpdate(orderInfoH);
            } else {
                orderInfoService.saveOrUpdate(orderInfo);
            }
            orderFeeService.updateOrderExceptionPrice(problemInfo, problemInfo.getProblemDealPrice());
            //??????????????????????????????????????????????????????????????????????????????????????? ???????????????????????????????????????????????????
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {//??????????????? ???????????????
                boolean isPush = true;
                if (problemInfo.getSourceProblem() != null && problemInfo.getSourceProblem() == SysStaticDataEnum.SOURCE_PROBLEM.PUSH) {//????????????????????????
                    OrderProblemInfo problemSourceInfo = this.getById(problemInfo.getProblemSourceId());
                    //????????????????????? ?????????????????????
                    if (problemSourceInfo != null) {
                        problemSourceInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.END);
                        this.saveOrUpdate(problemSourceInfo);
                    }
                    isPush = false;//?????????
                } else {
                    OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderInfo.getFromOrderId());
                    if (orderInfoH != null) {//???????????????????????????
                        isPush = false;
                    }
                }
                if (isPush) {//????????????
                    OrderProblemInfo info = new OrderProblemInfo();
                    BeanUtils.copyProperties(info, problemInfo);
                    info.setId(CommonUtil.createSoNbr());
                    info.setOrderId(orderInfo.getFromOrderId());
                    info.setState(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
                    info.setRecordUserId(loginInfo.getTenantId());
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
                        this.saveOrUpdate(info);
                        problemInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
                        problemInfo.setProblemSourceId(info.getId());
                        //????????????
                        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
                        params.put("busiId", info.getId());
                        auditService.startProcess(AuditConsts.AUDIT_CODE.PROBLEM_CODE, info.getId(), SysOperLogConst.BusiCode.ProblemInfo, params, String.valueOf(fromOrderInfo.getTenantId()));
                        fromOrderInfo.setNoAuditExcNum(fromOrderInfo.getNoAuditExcNum() == null ? 1 : fromOrderInfo.getNoAuditExcNum() + 1);
                    }
                }
            }
        }
        orderOpRecordService.saveOrUpdate(problemInfo.getOrderId(), OrderConsts.OrderOpType.PROBLEM, accessToken);
        this.saveOrUpdate(problemInfo);
        if (StringUtils.isBlank(nodeNum) || !nodeNum.trim().equals("1")) {
            verOpt.setState(1);
            problemVerOptService.saveOrUpdate(verOpt);
        }
    }

    @Override
    public void commonExceptionFlowPath(OrderProblemInfo orderProblemInfo, OrderFee orderFee, Boolean IsTransit, String accessToken, LoginInfo loginInfo) {

        OrderFeeStatement feeStatement = orderFeeStatementService.getOrderFeeStatementById(orderProblemInfo.getOrderId());
        if (feeStatement != null) {
            if (StringUtils.isNotBlank(feeStatement.getBillNumber()) && SysStaticDataEnum.PROBLEM_CONDITION.INCOME == orderProblemInfo.getProblemCondition()) {
                throw new BusinessException("????????????BL??????????????????");
            }
        }
        this.update(orderProblemInfo);
        orderFeeService.updateOrderExceptionPrice(orderProblemInfo, orderProblemInfo.getProblemDealPrice());

        payForException(orderProblemInfo, IsTransit, accessToken, loginInfo);

        specialExceptionDeal(orderProblemInfo, orderFee, IsTransit, loginInfo, accessToken);
    }

    /**
     * ????????????
     *
     * @param problemId
     * @param accessToken
     */
    @Override
    @Transactional
    public void cancelProblem(Long problemId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (problemId == null || problemId <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        LambdaQueryWrapper<OrderProblemInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderProblemInfo::getId, problemId)
                .eq(OrderProblemInfo::getTenantId, loginInfo.getTenantId());
        OrderProblemInfo orderProblemInfo = baseMapper.selectOne(wrapper);
        if (orderProblemInfo == null) {
            log.error("?????????????????????????????????????????? ??????ID[" + problemId + "]");
            throw new BusinessException("??????????????????????????????????????????");
        }

        if (orderProblemInfo.getState() == null
                || (SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING != orderProblemInfo.getState()
        )) {
            throw new BusinessException("?????????????????????");
        }
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getOrderId, orderProblemInfo.getOrderId())
                .eq(OrderInfo::getTenantId, loginInfo.getTenantId());
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            LambdaQueryWrapper<OrderInfoH> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderInfoH::getOrderId, orderProblemInfo.getOrderId())
                    .eq(OrderInfoH::getTenantId, loginInfo.getTenantId());
            OrderInfoH orderInfoH = orderInfoHMapper.selectOne(lambdaQueryWrapper);
            if (orderInfoH == null) {
                throw new BusinessException("??????????????????[" + orderProblemInfo.getOrderId() + "]?????????");
            } else {
                BeanUtils.copyProperties(orderInfo, orderInfoH);
            }
            isHis = true;
        }
        orderProblemInfo.setVerifyDate(DateUtil.asLocalDateTime(new Date()));
        orderProblemInfo.setState(SysStaticDataEnum.EXPENSE_STATE.CANCEL);
        this.saveOrUpdate(orderProblemInfo);

        if (orderProblemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) { //????????????
            //????????????????????????
            if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {//?????????
                if (orderProblemInfo.getSourceProblem() != null && orderProblemInfo.getSourceProblem() == SysStaticDataEnum.SOURCE_PROBLEM.PUSH) {//????????????????????????
                    LambdaQueryWrapper<OrderProblemInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(OrderProblemInfo::getProblemSourceId, orderProblemInfo.getProblemSourceId());
                    OrderProblemInfo problemSourceInfo = baseMapper.selectOne(lambdaQueryWrapper);
                    //????????????????????? ?????????????????????
                    if (problemSourceInfo != null) {
                        problemSourceInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.CANCEL);
                        this.saveOrUpdate(problemSourceInfo);
                    }
                }
            }
        } else if (orderProblemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
            //??????????????????????????????????????????????????????????????????????????????????????? ???????????????????????????????????????????????????
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {//??????????????? ???????????????
                if (orderProblemInfo.getSourceProblem() != null && orderProblemInfo.getSourceProblem() == SysStaticDataEnum.SOURCE_PROBLEM.PUSH) {//????????????????????????
                    LambdaQueryWrapper<OrderProblemInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(OrderProblemInfo::getProblemSourceId, orderProblemInfo.getProblemSourceId());
                    OrderProblemInfo problemSourceInfo = baseMapper.selectOne(lambdaQueryWrapper);
                    //????????????????????? ?????????????????????
                    if (problemSourceInfo != null) {
                        problemSourceInfo.setProblemExternalState(SysStaticDataEnum.EXPENSE_STATE.CANCEL);
                        this.saveOrUpdate(problemSourceInfo);
                    }
                }
            }
        }
//        IAuditOutTF auditOutTF = (IAuditOutTF) SysContexts.getBean("auditOutTF");
//        auditOutTF.cancelProcess(AuditConsts.AUDIT_CODE.PROBLEM_CODE, orderProblemInfo.getProblemId(), orderProblemInfo.getTenantId());
        // TODO ??????????????????
        //????????????
        StringBuffer message = new StringBuffer("[" + loginInfo.getName() + "]????????????");
        if (orderProblemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
            orderInfo.setNoAuditExcNum((orderInfo.getNoAuditExcNum() == null ? 0 : orderInfo.getNoAuditExcNum()) - 1);
        } else if (orderProblemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.INCOME) {
            orderInfo.setNoAuditExcNumIn((orderInfo.getNoAuditExcNum() == null ? 0 : orderInfo.getNoAuditExcNum()) - 1);
        }
        if (isHis) {
            LambdaQueryWrapper<OrderInfoH> hLambdaQueryWrapper = new LambdaQueryWrapper<>();
            hLambdaQueryWrapper.eq(OrderInfoH::getOrderId, orderProblemInfo.getOrderId())
                    .eq(OrderInfoH::getTenantId, loginInfo.getTenantId());
            OrderInfoH orderInfoH = orderInfoHMapper.selectOne(hLambdaQueryWrapper);
            orderInfoH.setNoAuditExcNum(orderInfo.getNoAuditExcNum());
            orderInfoH.setNoAuditExcNumIn(orderInfo.getNoAuditExcNumIn());
            orderInfoHService.saveOrUpdate(orderInfoH);
        } else {
            orderInfoService.saveOrUpdate(orderInfo);
        }
        ProblemVerOpt verOpt = new ProblemVerOpt();
        verOpt.setProblemId(problemId);
        verOpt.setCheckUserId(loginInfo.getUserInfoId());
        verOpt.setCheckUserName(loginInfo.getName());
        verOpt.setTenantId(loginInfo.getTenantId());
        verOpt.setProblemState(orderProblemInfo.getState());
        verOpt.setState(3);
        problemVerOptService.saveOrUpdate(verOpt);
        //????????????????????????
        sysOperLogServicesystem.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, message.toString());
    }

    @Override
    public void payForException(OrderProblemInfo orderProblemInfo, Boolean IsTransit, String accessToken, LoginInfo loginInfo) {
        if (orderProblemInfo == null || orderProblemInfo.getProblemCondition() != SysStaticDataEnum.PROBLEM_CONDITION.COST) {
            return;
        }
        List<SysStaticData> lists = new ArrayList<>();
        try {

            lists = sysStaticDataService.get(SysStaticDataEnum.SysStaticData.COST_RECRIVE_ORDER_PROBLEM_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SysStaticData s = null;
        for (SysStaticData sysStaticData : lists) {
            if (orderProblemInfo.getProblemType().equals(sysStaticData.getCodeValue())) {
                s = sysStaticData;
                break;
            }
        }

        if (lists == null || lists.isEmpty() || s == null) {
            log.error("???????????????????????????????????????[" + EnumConsts.SysStaticData.COST_PROBLEM_TYPE + "]");
            throw new BusinessException("???????????????????????????????????????");
        }
        EnumConsts.Exception_Deal_Type exceptionDealType = EnumConsts.Exception_Deal_Type.getType(Integer.parseInt(s.getCodeId() + ""));

        if (exceptionDealType == null) {
            log.error("???????????????????????????????????????[" + s.getCodeId() + "]");
            throw new BusinessException("???????????????????????????????????????");
        }

        //7-11??????
        Long orderId = orderProblemInfo.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderScheduler scheduler = new OrderScheduler();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("??????????????????[" + orderId + "]?????????");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            OrderFeeH orderFeeH = orderFeeHService.getOrderFeeH(orderId);
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOrderFeeExtH(orderId);
            BeanUtils.copyProperties(orderInfo, orderInfoH);
            BeanUtils.copyProperties(scheduler, orderSchedulerH);
            BeanUtils.copyProperties(orderInfoExt, orderInfoExtH);
            BeanUtils.copyProperties(orderFee, orderFeeH);
            BeanUtils.copyProperties(orderFeeExt, orderFeeExtH);
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
                log.error("?????????????????????! ??????ID[" + orderInfo.getToTenantId() + "]");
                throw new BusinessException("?????????????????????!");
            }
            userId = tenantDef.getAdminUser();
        } else if (scheduler.getIsCollection() != null && scheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES) {
            userId = scheduler.getCollectionUserId();
        }
        //????????????????????????
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
            if (OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {//?????????????????????  ?????????????????????????????????
                if (orderProblemInfo.getProblemDealPrice() != 0L) {
                    orderAccountService.payForException(userId, vehicleAffiliation, orderProblemInfo.getProblemDealPrice(), orderProblemInfo.getOrderId(), orderProblemInfo.getTenantId(), loginInfo, accessToken);
                }
                payOtherProblemInfo(scheduler, userId, vehicleAffiliation, loginInfo, accessToken);
            } else {
                orderAccountService.payForException(userId, vehicleAffiliation, orderProblemInfo.getProblemDealPrice(), orderProblemInfo.getOrderId(), orderProblemInfo.getTenantId(), loginInfo, accessToken);
            }
        } else if (EnumConsts.Exception_Deal_Type.REDUCEMONEY == exceptionDealType) {
            if (OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {
                if (orderProblemInfo.getProblemDealPrice() != 0L) {
                    orderLimitService.payForExceptionOut(userId, vehicleAffiliation, orderProblemInfo.getProblemDealPrice(), orderProblemInfo.getId(), orderProblemInfo.getTenantId(), orderProblemInfo.getOrderId(), loginInfo, accessToken);
                }
                payOtherProblemInfo(scheduler, userId, vehicleAffiliation, loginInfo, accessToken);
            } else {
                if (isHis) {//?????????????????? ????????????????????????
                    orderLimitService.payForExceptionOut(userId, vehicleAffiliation, orderProblemInfo.getProblemDealPrice(), orderProblemInfo.getId(), orderProblemInfo.getTenantId(), orderId, loginInfo, accessToken);
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param scheduler
     * @param userId
     * @param vehicleAffiliation
     * @throws Exception
     */
    private void payOtherProblemInfo(OrderScheduler scheduler, Long userId, String vehicleAffiliation, LoginInfo loginInfo, String token) {
        /** ?????????????????? **/
        List<OrderAgingInfo> orderAgingInfos = orderAgingInfoService.queryAgingInfoByOrderId(scheduler.getOrderId());
        if (orderAgingInfos != null && orderAgingInfos.size() > 0) {
            for (OrderAgingInfo orderAgingInfo : orderAgingInfos) {
                if (orderAgingInfo != null && orderAgingInfo.getAuditSts() != null
                        && orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END
                        && orderAgingInfo.getFinePrice() != null && orderAgingInfo.getFinePrice() > 0) {
                    orderLimitService.payForExceptionOut(userId, vehicleAffiliation, -orderAgingInfo.getFinePrice(), -1L,
                            scheduler.getTenantId(), scheduler.getOrderId(), loginInfo, token);
                }
            }
        }
        List<OrderProblemInfo> list = this.getOrderProblemInfoByOrderId(scheduler.getOrderId(), scheduler.getTenantId());
        for (OrderProblemInfo p : list) {
            if (p.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                if (SysStaticDataEnum.EXPENSE_STATE.END == p.getState().intValue()
                        && !OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(p.getProblemType())
                        && !OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(p.getProblemType())
                        && !OrderConsts.PROBLEM_TYPE.IN_BALLAST_COMPENSATION.equals(p.getProblemType())
                        && !OrderConsts.PROBLEM_TYPE.ADD_STOP_POINT.equals(p.getProblemType())
                        && !OrderConsts.PROBLEM_TYPE.OVERWIGHT.equals(p.getProblemType())) {
                    orderLimitService.payForExceptionOut(userId, vehicleAffiliation, p.getProblemDealPrice(), p.getId(), p.getTenantId(), p.getOrderId(), loginInfo, token);
                }
            }
        }
    }

    private void specialExceptionDeal(OrderProblemInfo orderProblemInfo, OrderFee orderFee, Boolean IsTransit, LoginInfo loginInfo, String accessToken) {
        boolean hasCancelOrder = false;// ??????????????????????????????????????????????????????
        boolean hasAllAuit = true; // ??????????????????????????????
        boolean flag = false; // ???????????? ??????????????????
        if (OrderConsts.PROBLEM_TYPE.CARRIER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {
            flag = true;
        } else if (OrderConsts.PROBLEM_TYPE.SHIPPER_DAMAGE_ORDER.equals(orderProblemInfo.getProblemType())) {
            flag = true;
        }
        List<OrderProblemInfo> list = this.getOrderProblemInfoByOrderId(orderProblemInfo.getOrderId(), loginInfo.getTenantId());
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
                throw new BusinessException("?????????????????????,???????????????");
            }
        }
        long orderId = orderFee.getOrderId();
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        boolean isHis = false;
        if (orderInfo == null) {
            isHis = true;
        }
        if (!isHis && hasAllAuit && (hasCancelOrder || flag)) {
            //?????? ??????????????????
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
                    //???????????????????????? ???????????????
                    OrderInfo tempOrder = orderInfoService.getOrder(orderInfo.getToOrderId());
                    if (tempOrder != null) {
                        //??????????????????
                        OrderFee tempOrderFee = orderFeeService.getOrderFee(orderInfo.getToOrderId());
                        OrderInfoExt tempOrderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getToOrderId());
                        OrderScheduler tempScheduler = orderSchedulerService.getOrderScheduler(orderInfo.getToOrderId());
                        if (tempOrderInfoExt.getPreAmountFlag() != null && tempOrderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                            //??????????????????
                            //this.reclaimOilCard(tempOrder.getOrderId(), tempOrder.getTenantId());
                            //???????????????????????? ????????????
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
                            updateOrderService.cancelTheOrder(cancelTheOrderIn, loginInfo, accessToken);
                        }
                        //???????????????????????????
                        orderInfoHService.canel(tempScheduler.getOrderId(), orderProblemInfo.getId(), true, accessToken);
                    }
                }
                if (IsTransit && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {//????????? ??????????????????
                    //??????????????????
//                    this.reclaimOilCard(orderId, loginInfo.getTenantId());
                    SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
                    if (tenantDef == null) {
                        log.error("?????????????????????! ??????ID[" + orderInfo.getToTenantId() + "]");
                        throw new BusinessException("?????????????????????!");
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

                    updateOrderService.cancelTheOrder(cancelTheOrderIn, loginInfo, accessToken);
                } else {
                    if (scheduler.getVehicleClass() == null || (
                            (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                                    || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                                    || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                            )
                                    || (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                                    && (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT)))) {//?????????????????????
                        //??????????????????
//                        this.reclaimOilCard(orderId, baseUser.getTenantId());
                        orderPayMethodService.cancelTheOrderTransit(orderFee, scheduler, orderInfo, loginInfo, accessToken);
                    } else if (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                        if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST) {
//                            ??????????????????????????????
                            orderFeeService.cancelDriverSwitchSubsidy(orderId, loginInfo, accessToken);
                        } else if (orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                            //???????????????????????????
                            orderFeeService.cancelDriverSwitchOilFee(orderId, loginInfo, accessToken);
                        }
                        //????????????????????? todo ?????????????????????????????????
                        updateOrderService.cancelTheOwnCarOrder(scheduler.getCarDriverId(), "0",
                                orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee(),
                                orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee(),
                                feeExt.getPontage() == null ? 0 : feeExt.getPontage(),
                                feeExt.getSalary() == null ? 0 : feeExt.getSalary(),
                                feeExt.getCopilotSalary() == null ? 0 : feeExt.getCopilotSalary(),
                                scheduler.getCopilotUserId() == null ? 0 : scheduler.getCopilotUserId(),
                                orderId, orderInfo.getTenantId(), orderInfo.getIsNeedBill(), loginInfo, accessToken);
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
                    //???????????????????????? ?????????????????????????????????????????????????????????
                    OrderInfo tempOrder = orderInfoService.getOrder(orderInfo.getToOrderId());
                    if (tempOrder != null) {
                        //??????????????????
                        OrderScheduler tempScheduler = orderSchedulerService.getOrderScheduler(orderInfo.getToOrderId());
                        //???????????????????????????
                        orderInfoHService.canel(tempScheduler.getOrderId(), orderProblemInfo.getId(), true, accessToken);
                    }
                }
            }
            //??????????????????
            orderInfoHService.canel(scheduler.getOrderId(), orderProblemInfo.getId(), true, accessToken);
        }
    }


    @Override
    public List<OrderProblemInfo> getOrderProblemInfoByUserId(Long orderId, Long userId) {
        LambdaQueryWrapper<OrderProblemInfo> queryWrapper=new LambdaQueryWrapper<>();
        if (orderId != null){
            queryWrapper.eq(OrderProblemInfo::getOrderId,orderId);
        }
        if (userId != null){
            queryWrapper.eq(OrderProblemInfo::getCarOwnerId,userId);
        }
        List<OrderProblemInfo> orderProblemInfos = this.baseMapper.selectList(queryWrapper);
        return orderProblemInfos;
    }

    @Override
    public Integer getOrderProblemInfoByOrderIds(List<Long> orderIds) {
        LambdaQueryWrapper<OrderProblemInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrderProblemInfo::getOrderId, orderIds);
        queryWrapper.in(OrderProblemInfo::getState, SysStaticDataEnum.EXPENSE_STATE.AUDIT, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
        List<OrderProblemInfo> list = this.list(queryWrapper);
        return list.size();
    }

    @Override
    public List<OrderProblemInfo> queryOrderProblemInfoList(Long orderId, boolean isAudit, boolean isWx,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId  = null;
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        if (loginInfo != null && !isWx) {
            userId = loginInfo.getUserInfoId();
        }
        List<OrderProblemInfo> listOut = new ArrayList<>();
        List<OrderProblemInfo> list = getOrderProblemInfoByUserId(orderId, userId);
        if (isAudit) {
            for (OrderProblemInfo orderProblemInfo : list) {
                orderProblemInfo.setStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getState()+"").getCodeName());
                orderProblemInfo.setProblemTypeName(getSysStaticData(EnumConsts.SysStaticData.COST_PROBLEM_TYPE, orderProblemInfo.getProblemType() + "").getCodeName());
                orderProblemInfo.setSourceProblemName(getSysStaticData(EnumConsts.SysStaticData.SOURCE_PROBLEM, orderProblemInfo.getSourceProblem() + "").getCodeName());
                orderProblemInfo.setProblemExternalStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getProblemExternalState() + "").getCodeName());
                orderProblemInfo.setChargeTypeName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getProblemExternalState() + "").getCodeName());
                if (orderProblemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.AUDIT
                        || orderProblemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING) {
                    listOut.add(orderProblemInfo);
                }
            }
        } else {
            if (isWx) {
                listOut = list;
                for (OrderProblemInfo orderProblemInfo : listOut) {
                    orderProblemInfo.setStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getState()+"").getCodeName());
                    orderProblemInfo.setProblemTypeName(getSysStaticData(EnumConsts.SysStaticData.COST_PROBLEM_TYPE, orderProblemInfo.getProblemType() + "").getCodeName());
                    orderProblemInfo.setSourceProblemName(getSysStaticData(EnumConsts.SysStaticData.SOURCE_PROBLEM, orderProblemInfo.getSourceProblem() + "").getCodeName());
                    orderProblemInfo.setProblemExternalStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getProblemExternalState() + "").getCodeName());
                    orderProblemInfo.setChargeTypeName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getProblemExternalState() + "").getCodeName());
                }
            }else{
                for (OrderProblemInfo orderProblemInfo : list) {
                    orderProblemInfo.setStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getState()+"").getCodeName());
                    orderProblemInfo.setProblemTypeName(getSysStaticData(EnumConsts.SysStaticData.COST_PROBLEM_TYPE, orderProblemInfo.getProblemType() + "").getCodeName());
                    orderProblemInfo.setSourceProblemName(getSysStaticData(EnumConsts.SysStaticData.SOURCE_PROBLEM, orderProblemInfo.getSourceProblem() + "").getCodeName());
                    orderProblemInfo.setProblemExternalStateName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getProblemExternalState() + "").getCodeName());
                    orderProblemInfo.setChargeTypeName(getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, orderProblemInfo.getProblemExternalState() + "").getCodeName());
                    if (orderProblemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.END
                            && orderProblemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                        listOut.add(orderProblemInfo);
                    }
                }
            }
        }
        return listOut;
    }

    @Override
    public List<OrderProblemInfoOutDto> doQueryAbnormalCompensation(Long orderId, Long tenantId) {
        QueryOrderProblemInfoQueryVo vo = new QueryOrderProblemInfoQueryVo();
        vo.setOrderId(orderId);
        vo.setCodeId("2");
        vo.setState("3");

        // ??????????????????????????????
        List<OrderProblemInfo> orderProblemInfoList = baseMapper.queryOrderProblemInfoQuery(vo);
        List<OrderProblemInfoOutDto> orderProblemInfoOutList = new ArrayList<>();
        if(orderProblemInfoList!= null && orderProblemInfoList.size() > 0){
            for (OrderProblemInfo orderProblemInfo:orderProblemInfoList ) {
                OrderProblemInfoOutDto orderProblemInfoOut = new OrderProblemInfoOutDto();
                BeanUtil.copyProperties(orderProblemInfo, orderProblemInfoOut);
                orderProblemInfoOut.setProblemPriceDouble(CommonUtil.getDoubleFormatLongMoney(orderProblemInfo.getProblemPrice(), 2));
                orderProblemInfoOut.setProblemDealPriceDouble(CommonUtil.getDoubleFormatLongMoney(orderProblemInfo.getProblemDealPrice(), 2));
                orderProblemInfoOutList.add(orderProblemInfoOut);
            }
        }
        return orderProblemInfoOutList;
    }

    @Override
    public List<OrderProblemInfoOutDto> doQueryAbnormalDeduction(Long orderId, Long tenantId) {
        QueryOrderProblemInfoQueryVo vo = new QueryOrderProblemInfoQueryVo();
        vo.setOrderId(orderId);
        vo.setCodeId("1");
        vo.setState("3");
        List<OrderProblemInfo> orderProblemInfoList = baseMapper.queryOrderProblemInfoQuery(vo);
        List<OrderProblemInfoOutDto> orderProblemInfoOutList = new ArrayList<>();
        if(orderProblemInfoList!= null && orderProblemInfoList.size() > 0){
            for (OrderProblemInfo orderProblemInfo:orderProblemInfoList ) {
                OrderProblemInfoOutDto orderProblemInfoOut = new OrderProblemInfoOutDto();
                BeanUtil.copyProperties(orderProblemInfo, orderProblemInfoOut);
                orderProblemInfoOut.setProblemPriceDouble(CommonUtil.getDoubleFormatLongMoney(orderProblemInfo.getProblemPrice(), 2));
                orderProblemInfoOut.setProblemDealPriceDouble(CommonUtil.getDoubleFormatLongMoney(orderProblemInfo.getProblemDealPrice(), 2));
                orderProblemInfoOutList.add(orderProblemInfoOut);
            }
        }
        return orderProblemInfoOutList;
    }

    @Override
    public List<OrderProblemInfo> queryOrderProblemInfoQueryList(QueryOrderProblemInfoQueryVo vo) {
        return orderProblemInfoMapper.queryOrderProblemInfoQuery(vo);
    }


}
