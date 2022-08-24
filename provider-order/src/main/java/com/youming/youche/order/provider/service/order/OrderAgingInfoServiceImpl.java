package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IOrderAgingAppealInfoService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderReportService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderAgingAppealInfo;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderReport;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.dto.AgingInfoDto;
import com.youming.youche.order.dto.OrderAgingInfoDto;
import com.youming.youche.order.dto.OrderAgingListDto;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.OrderReportDto;
import com.youming.youche.order.dto.QueryAuditAgingDto;
import com.youming.youche.order.dto.order.OrderAgingAppealInfoDto;
import com.youming.youche.order.dto.order.OrderAgingInfoOutDto;
import com.youming.youche.order.dto.order.OrderAgingListOutDto;
import com.youming.youche.order.dto.order.SysOperLogDto;
import com.youming.youche.order.provider.mapper.order.OrderAgingAppealInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderAgingInfoMapper;
import com.youming.youche.order.provider.transfer.OrderAgingInfoDtoTransfer;
import com.youming.youche.order.provider.transfer.OrderAgingListOutDtoTransfer;
import com.youming.youche.order.provider.utils.LocalDateTimeUtil;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.order.vo.OrderAgingListInVo;
import com.youming.youche.order.vo.SaveAppealInfoVo;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserOrgRelService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.HtmlEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>
 * 时效罚款表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OrderAgingInfoServiceImpl extends BaseServiceImpl<OrderAgingInfoMapper, OrderAgingInfo> implements IOrderAgingInfoService {


    @Resource
    private OrderAgingInfoMapper orderAgingInfoMapper;

    @Resource
    private OrderAgingAppealInfoMapper orderAgingAppealInfoMapper;

    @Resource
    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;
    @DubboReference(version = "1.0.0")
    ISysRoleService sysRoleService;
    @DubboReference(version = "1.0.0")
    ISysUserOrgRelService sysUserOrgRelService;
    @DubboReference(version = "1.0.0")
    private IAuditNodeInstService auditNodeInstService;
    @DubboReference(version = "1.0.0")
    private IAuditSettingService auditSettingService;
    @Autowired
    private IOrderSchedulerService orderSchedulerService;

    @Autowired
    private IOrderSchedulerHService orderSchedulerHService;
    @Resource
    private OrderAgingInfoDtoTransfer orderAgingInfoDtoTransfer;
    @Lazy
    @Autowired
    private IOrderInfoService orderinfoservice;
    @Autowired
    private IOrderFeeService orderFeeService;
    @Autowired
    private IOrderInfoExtService orderInfoExtService;
    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @Autowired
    private IOrderInfoExtService orderinfoextservice;
    @DubboReference(version = "1.0.0")
    private IAuditService auditService;
    @Autowired
    private IOrderInfoHService orderInfoHService;
    @Resource
    private OrderAgingListOutDtoTransfer orderAgingListOutDtoTransfer;
    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Resource
    IOrderReportService iOrderReportService;
    @Resource
    IOrderAgingAppealInfoService orderAgingAppealInfoService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @Resource
    IOrderReportService orderReportService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Override
    public boolean isExistAgingInfo(Long orderId) {

        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号为空!");
        }
        QueryWrapper<OrderAgingInfo> orderAgingInfoQueryWrapper = new QueryWrapper<>();
        orderAgingInfoQueryWrapper.eq("order_id", orderId)
                .in("audit_sts", new Integer[]{SysStaticDataEnum.EXPENSE_STATE.AUDIT, SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING, SysStaticDataEnum.EXPENSE_STATE.END})
                .orderByDesc("create_time");
        List<OrderAgingInfo> list = orderAgingInfoMapper.selectList(orderAgingInfoQueryWrapper);
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<OrderAgingInfo> queryAgingInfoByOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号为空!");
        }
        QueryWrapper<OrderAgingInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        wrapper.orderByDesc("create_time");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Page<OrderAgingInfoDto> queryOrderAgingList(OrderAgingListInVo orderAgingListInVo,
                                                       String accessToken, Integer pageSize,
                                                       Integer pageNum) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderAgingListInVo.getTodo() != null && orderAgingListInVo.getTodo()) {
            List<Long> agingIds = auditNodeInstService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_AGING_CODE,
                    loginInfo.getUserInfoId(), loginInfo.getTenantId(), 500);
            List<Long> appealIds = auditNodeInstService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.AGING_APPEAL_CODE,
                    loginInfo.getUserInfoId(), loginInfo.getTenantId(), 500);
            orderAgingListInVo.setAgingIds(agingIds);
            orderAgingListInVo.setAppealIds(appealIds);
        }
        boolean hasAllData = sysRoleService.hasAllData(loginInfo);
        orderAgingListInVo.setHasAllData(hasAllData);
        List<Long> orgIdList = null;
        if (orderAgingListInVo.getIsWx() != null && orderAgingListInVo.getIsWx()) {
            orgIdList = sysUserOrgRelService.selectIdByUserInfoIdAndTenantId(loginInfo.getUserInfoId(), loginInfo.getTenantId());
        } else {
            // 判断部门权限--不考虑部门权限
            //            orgIdList = orderSchedulerTF.getSubOrgList(hasAllData);

        }
        orderAgingListInVo.setOrgIdList(orgIdList);
        boolean isAging = true;
        boolean isAgingAppeal = true;
        if (orderAgingListInVo.getSelectType() != null && orderAgingListInVo.getSelectType() > 0) {
            if (orderAgingListInVo.getSelectType() == 1) {// 时效罚款
                isAgingAppeal = false;
            } else if (orderAgingListInVo.getSelectType() == 2) {// 司机申诉
                isAging = false;
            }
        }
        Page<OrderAgingInfoDto> page = new Page<>(pageNum, pageSize);
        Page<OrderAgingInfoDto> orderAgingInfoDtoPage = orderAgingInfoMapper.queryOrderAgingQuery(page, orderAgingListInVo,
                loginInfo.getTenantId(), isAging, isAgingAppeal);
        List<OrderAgingInfoDto> infoDtos = orderAgingInfoDtoPage.getRecords();
        List<OrderAgingInfoDto> listOut = new ArrayList<OrderAgingInfoDto>();
        List<Long> appealBusiIdList = new ArrayList<Long>();
        List<Long> agingBusiIdList = new ArrayList<Long>();
        for (OrderAgingInfoDto map : infoDtos) {
            appealBusiIdList.add(Long.parseLong(String.valueOf(map.getAppealId())));
            agingBusiIdList.add(Long.parseLong(String.valueOf(map.getAgingId())));
        }
        Map<Long, Boolean> appealJurisdictionMap = new ConcurrentHashMap<Long, Boolean>();
        if (appealBusiIdList.size() > 0) {
            appealJurisdictionMap = auditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.AGING_APPEAL_CODE,
                    appealBusiIdList, accessToken);
        }
        Map<Long, Boolean> agingJurisdictionMap = new ConcurrentHashMap<Long, Boolean>();
        if (agingBusiIdList.size() > 0) {
            agingJurisdictionMap = auditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.ORDER_AGING_CODE,
                    agingBusiIdList, accessToken);
        }
        for (OrderAgingInfoDto infoDto : infoDtos) {
            OrderAgingInfoDto out = new OrderAgingInfoDto();
            BeanUtils.copyProperties(infoDto, out);
            OrderInfoDto orderLineMap = orderSchedulerService.queryOrderLineString(out.getOrderId());
            out.setOrderLine(orderLineMap.getOrderLine());
            out.setIsTransitLine(orderLineMap.getIsTransitLine());
            out.setAgingJurisdiction(agingJurisdictionMap.get(out.getAgingId()));
            out.setAppealJurisdiction(appealJurisdictionMap.get(out.getAppealId()));
            if (out.getSelectType() != null) {
                if (out.getSelectType() == 1) {
                    out.setSelectTypeName("时效罚款");
                } else if (out.getSelectType() == 2) {
                    out.setSelectTypeName("司机申诉");
                }
            }
            listOut.add(out);
        }
        List<OrderAgingInfoDto> orderAgingInfoDtos = orderAgingInfoDtoTransfer.toOrderAgingInfoDto(listOut);
        orderAgingInfoDtoPage.setRecords(orderAgingInfoDtos);
        return orderAgingInfoDtoPage;
    }

    @Override
    public Boolean saveOrUpdateOrderAgingInfo(AgingInfoDto inParam, String accessToken) {
        String orderArriveDate1 = inParam.getOrderArriveDate();
        // 参数修改
        if (StringUtils.isNotEmpty(orderArriveDate1) && orderArriveDate1.length() == 16) {
            orderArriveDate1 = orderArriveDate1 + ":00";
            inParam.setOrderArriveDate(orderArriveDate1);
        }
        String orderStartDate1 = inParam.getOrderStartDate();
        if (StringUtils.isNotEmpty(orderStartDate1) && orderStartDate1.length() == 16) {
            orderStartDate1 = orderStartDate1 + ":00";
            inParam.setOrderStartDate(orderStartDate1);
        }
        String lineNode1 = inParam.getLineNode();
        if (StringUtils.isNotBlank(lineNode1)) {
            inParam.setLineNode(HtmlEncoder.decode(lineNode1));
        }


        LoginInfo user = loginUtils.get(accessToken);
        Long orderId = inParam.getOrderId();
        Long agingId = inParam.getId();
        String orderStartDateStr = inParam.getOrderStartDate();
        String orderArriveDateStr = inParam.getOrderArriveDate();
        String arriveTimeStr = inParam.getArriveTime();
        String finePriceStr = inParam.getFinePrice();
        String remark = inParam.getRemark();
        String arriveHourStr = inParam.getArriveHour();

        Long userId = inParam.getUserId();
        String userName = inParam.getUserName();
        String userPhone = inParam.getUserPhone();
        String lineNode = inParam.getLineNode();
        String nand = inParam.getNand();
        String eand = inParam.getEand();
        String nandDes = inParam.getNandDes();
        String eandDes = inParam.getEandDes();

        Integer sourceRegion = inParam.getSourceRegion();
        Integer desRegion = inParam.getDesRegion();

        Long finePrice = 0L;

        // 校验参数
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空，请联系客服！");
        }
        if (sourceRegion == null || sourceRegion <= 0) {
            throw new BusinessException("起始城市不能为空!");
        }
        if (desRegion == null || desRegion <= 0) {
            throw new BusinessException("目的城市不能为空!");
        }
        if (StringUtils.isBlank(nand) || StringUtils.isBlank(eand)) {
            throw new BusinessException("起始经纬度不能为空!");
        }
        if (StringUtils.isBlank(nandDes) || StringUtils.isBlank(eandDes)) {
            throw new BusinessException("目的经纬度不能为空!");
        }
        if (StringUtils.isBlank(lineNode)) {
            throw new BusinessException("请选择线路!");
        }
        if (StringUtils.isBlank(orderStartDateStr)) {
            throw new BusinessException("请填写离台时间!");
        }
        if (StringUtils.isBlank(orderArriveDateStr)) {
            throw new BusinessException("请填写到达时间!");
        }
        if (StringUtils.isBlank(remark)) {
            throw new BusinessException("请填写描述!");
        }
        if (StringUtils.isEmpty(finePriceStr)) {
            throw new BusinessException("请输入罚款金额！");
        } else if (!CommonUtil.isNumber(finePriceStr)) {
            throw new BusinessException("请输入正确的金额！");
        } else {
            Double convertNum = Double.parseDouble(finePriceStr) * 100;
            finePrice = convertNum.longValue();
        }
        if (StringUtils.isBlank(arriveTimeStr)) {
            throw new BusinessException("运输时限计算失败,请重新填写时间信息!");
        }
        OrderInfo orderInfo = orderinfoservice.getOrder(orderId);
        OrderFee orderFee = orderFeeService.getOrderFee(orderId);
        OrderInfoExt orderInfoExt = orderinfoextservice.getOrderInfoExt(orderId);
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderInfo == null) {
            throw new BusinessException("未找到订单号[" + orderId + "]订单信息!");
        }
        LocalDateTime orderArriveDate = null;
        LocalDateTime orderStartDate = null;
        try {
            orderArriveDate = LocalDateTimeUtil.convertStringToDate(orderArriveDateStr);
            orderStartDate = LocalDateTimeUtil.convertStringToDate(orderStartDateStr);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("时间格式有误!");
        }
        if (orderArriveDate.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli() <
                orderStartDate.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli()) {
            throw new BusinessException("离台时间不能大于到达时间!");
        }
        double arriveTime = (DateUtil.asDate(orderArriveDate).getTime()
                - DateUtil.asDate(orderStartDate).getTime()) / (1000 * 60 * 60.0);

        Long arriveHour = 0L;
        if (StringUtils.isNotBlank(arriveHourStr)) {
            arriveHour = CommonUtil.getDoubleFormat(Double.parseDouble(arriveHourStr) * 100, 2)
                    .longValue();
        }
        OrderAgingInfo orderAgingInfo = new OrderAgingInfo();
        String desc = "[" + user.getName() + "]";
        SysOperLogConst.OperType operType = SysOperLogConst.OperType.Add;
        // 判断时效罚款是否存在
        boolean isExist = this.isExistAgingInfoByLocation(orderId, nand, eand, eandDes, nandDes, agingId);
        if (isExist) {
            throw new BusinessException("该路段已有时效信息，不能重复添加！");
        }
        // 时效罚款存在
        boolean state = false;
        if (agingId != null && agingId > 0) {
            orderAgingInfo = this.getOrderAgingInfo(agingId);
            if (orderAgingInfo == null) {
                log.error("未查询到该时效信息，请联系客服！时效ID[" + agingId + "]");
                throw new BusinessException("未查询到该时效信息，请联系客服！");
            }
            if (orderAgingInfo.getAuditSts() != SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                    && orderAgingInfo.getAuditSts() != SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN) {
                throw new BusinessException("该信息不能修改!");
            }
            //校验扣减项是否超过尾款
            orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, finePrice, agingId, -1L);
            desc += "修改";
            operType = SysOperLogConst.OperType.Update;
        } else {
            //校验扣减项是否超过尾款
            orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, finePrice, -1L, -1L);
            orderAgingInfo.setOpId(user.getId());
            orderAgingInfo.setOpName(user.getName());
            orderAgingInfo.setOrderId(orderId);
            orderAgingInfo.setTenantId(user.getTenantId());
            desc += "添加";
            state = true;
        }
        orderAgingInfo.setOrderArriveDate(orderArriveDate);
        orderAgingInfo.setOrderStartDate(orderStartDate);
        orderAgingInfo.setRemark(remark);
        orderAgingInfo.setArriveHour(arriveHour);
        orderAgingInfo.setArriveTime(CommonUtil.getDoubleFormat(arriveTime * 100, 2).longValue());
        orderAgingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
        orderAgingInfo.setFinePrice(finePrice);
        orderAgingInfo.setUserId(userId);
        orderAgingInfo.setUserName(userName);
        orderAgingInfo.setUserPhone(userPhone);
        orderAgingInfo.setLineNode(lineNode);
        orderAgingInfo.setSourceRegion(sourceRegion);
        orderAgingInfo.setDesRegion(desRegion);
        orderAgingInfo.setNand(nand);
        orderAgingInfo.setEand(eand);
        orderAgingInfo.setEandDes(eandDes);
        orderAgingInfo.setNandDes(nandDes);
        desc += "时效罚款";
        this.saveOrUpdate(orderAgingInfo);
        // 登记异常操作日志

        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.OrderInfo, orderId, SysOperLogConst.OperType.Update, desc);

        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.AgingInfo, orderAgingInfo.getId(), operType, desc);

        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("busiId", orderAgingInfo.getId());
        boolean isAudit = auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, orderAgingInfo.getId(),
                SysOperLogConst.BusiCode.AgingInfo, params, accessToken);
        if (!isAudit) {// 无审核 直接通过审核
            this.verifyPass(orderAgingInfo.getId(), "无审核流程,直接通过", true, user);
        }
        return state;
    }

    /**
     * 审核通过
     *
     * @param agingId    主键
     * @param verifyDesc 审核描述
     * @param isFirst    是否一次审核
     * @throws Exception
     */
    public void verifyPass(Long agingId, String verifyDesc, Boolean isFirst, LoginInfo user) {
        OrderAgingInfo orderAgingInfo = this.getOrderAgingInfo(agingId);
        if (orderAgingInfo != null) {
            orderAgingInfo.setAuditDate(LocalDateTime.now());
            orderAgingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.END);
            this.saveOrUpdate(orderAgingInfo);
            OrderInfo orderInfo = orderinfoservice.getOrder(orderAgingInfo.getOrderId());
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderAgingInfo.getOrderId());
            OrderInfoExt orderInfoExt = orderinfoextservice.getOrderInfoExt(orderAgingInfo.getOrderId());
            OrderFee orderFee = orderFeeService.getOrderFee(orderAgingInfo.getOrderId());
            //校验扣减项是否超过尾款
            orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, orderAgingInfo.getFinePrice(), orderAgingInfo.getId(), -1L);
            // 同步支付中心
            orderFeeService.synPayCenterUpdateOrderOrProblemInfo(orderInfo, orderScheduler);
            if (orderInfo != null && orderScheduler != null) {
                //自有车 或者 纯C外调车订单才发送短信
                if (orderScheduler.getVehicleClass() != null
                        && (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        || ((orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                        && (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0)))) {
                    //TODO 短信
                    try {
//                        Map<String, Object> paraMap = new HashMap<String, Object>();
//                        paraMap.put("tenantName", orderInfo.getTenantName());
//                        paraMap.put("orderId", orderInfo.getOrderId());
//                        paraMap.put("info", "时效罚款");
//                        SysSmsSend sysSmsSend = new SysSmsSend();
//                        sysSmsSend.setBillId(orderScheduler.getCarDriverPhone());
//                        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT);
//                        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.ORDER_PRO_AGING);
//                        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.ORDER_AGING));
//                        sysSmsSend.setParamMap(paraMap);
//                        sysSmsSendService.sendSms(sysSmsSend);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!isFirst) {
                StringBuffer buffer = new StringBuffer("[" + user.getName() + "]审核时效罚款通过 备注:" + verifyDesc);
                sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OrderInfo,
                        orderAgingInfo.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update,
                        buffer.toString(), user.getTenantId());
                StringBuffer buffer1 = new StringBuffer("[" + user.getName() + "]审核时效罚款通过 备注:" + verifyDesc);
                sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.AgingInfo,
                        orderAgingInfo.getOrderId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Audit,
                        buffer1.toString(), user.getTenantId());
                StringBuffer buffer2 = new StringBuffer("[" + user.getName() + "]审核时效罚款通过 备注:" + verifyDesc);
                sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OrderInfo,
                        orderAgingInfo.getOrderId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Audit,
                        buffer2.toString(), user.getTenantId());
            }
        } else {
            log.error("未找到该时效信息，请联系客服！时效ID[" + agingId + "]");
            throw new BusinessException("未找到该时效信息，请联系客服！");
        }
    }

    /**
     * 审核通过
     *
     * @param agingId    主键
     * @param verifyDesc 审核描述
     * @param isFirst    是否一次审核
     * @throws Exception
     */
    @Override
    public void verifyPass(Long agingId, String verifyDesc, Boolean isFirst, String token) {
        LoginInfo user = loginUtils.get(token);
        OrderAgingInfo orderAgingInfo = this.getOrderAgingInfo(agingId);
        if (orderAgingInfo != null) {
            orderAgingInfo.setAuditDate(LocalDateTime.now());
            orderAgingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.END);
            this.saveOrUpdate(orderAgingInfo);
            OrderInfo orderInfo = orderinfoservice.getOrder(orderAgingInfo.getOrderId());
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderAgingInfo.getOrderId());
            OrderInfoExt orderInfoExt = orderinfoextservice.getOrderInfoExt(orderAgingInfo.getOrderId());
            OrderFee orderFee = orderFeeService.getOrderFee(orderAgingInfo.getOrderId());
            //校验扣减项是否超过尾款
            orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, orderAgingInfo.getFinePrice(), orderAgingInfo.getId(), -1L);
            // 同步支付中心
            orderFeeService.synPayCenterUpdateOrderOrProblemInfo(orderInfo, orderScheduler);
            if (orderInfo != null && orderScheduler != null) {
                //自有车 或者 纯C外调车订单才发送短信
                if (orderScheduler.getVehicleClass() != null
                        && (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        || ((orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                        && (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0)))) {
                    //TODO 短信
                    try {
//                        Map<String, Object> paraMap = new HashMap<String, Object>();
//                        paraMap.put("tenantName", orderInfo.getTenantName());
//                        paraMap.put("orderId", orderInfo.getOrderId());
//                        paraMap.put("info", "时效罚款");
//                        SysSmsSend sysSmsSend = new SysSmsSend();
//                        sysSmsSend.setBillId(orderScheduler.getCarDriverPhone());
//                        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT);
//                        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.ORDER_PRO_AGING);
//                        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.ORDER_AGING));
//                        sysSmsSend.setParamMap(paraMap);
//                        sysSmsSendService.sendSms(sysSmsSend);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!isFirst) {
//                sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.OrderInfo, orderAgingInfo.getOrderId(),
//                        SysOperLogConst.OperType.Update, user.getName() + "审核时效罚款通过,[" + verifyDesc + "]");
                StringBuffer buffer2 = new StringBuffer("[" + user.getName() + "]审核时效罚款通过 备注:" + verifyDesc);
                sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OrderInfo,
                        orderAgingInfo.getOrderId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Audit,
                        buffer2.toString(), user.getTenantId());
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.AgingInfo, orderAgingInfo.getId(),
                        SysOperLogConst.OperType.Audit, "[" + user.getName() + "]" + "审核时效罚款通过,原因:[" + verifyDesc + "]");
            }
        } else {
            log.error("未找到该时效信息，请联系客服！时效ID[" + agingId + "]");
            throw new BusinessException("未找到该时效信息，请联系客服！");
        }
    }

    @Override
    public List<WorkbenchDto> getTableInvalidExamineCount() {
        return baseMapper.getTableInvalidExamineCount();
    }

    @Override
    public Boolean isExistAgingInfoByLocation(Long orderId, String nand, String eand,
                                              String eandDes, String nandDes, Long agingId) {
        if (orderId == null || orderId <= 0) {

            throw new BusinessException("订单号为空!");
        }
        LambdaQueryWrapper<OrderAgingInfo> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderAgingInfo::getOrderId, orderId)
                .eq(OrderAgingInfo::getNand, nand)
                .eq(OrderAgingInfo::getEandDes, eandDes)
                .eq(OrderAgingInfo::getNandDes, nandDes);

        if (agingId != null && agingId > 0) {
            lambda.ne(OrderAgingInfo::getId, agingId);
        }
        lambda.in(OrderAgingInfo::getAuditSts, new Integer[]{SysStaticDataEnum.EXPENSE_STATE.AUDIT,
                        SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING,
                        SysStaticDataEnum.EXPENSE_STATE.END})
                .orderByDesc(OrderAgingInfo::getCreateTime);
        List<OrderAgingInfo> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public OrderAgingInfo getOrderAgingInfo(Long id) {
        return this.getById(id);
    }

    @Override
    public Boolean auditAging(Long agingId, String verifyDesc, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderAgingInfo orderAgingInfo = this.getOrderAgingInfo(agingId);
        if (orderAgingInfo != null) {
            orderAgingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
            this.saveOrUpdate(orderAgingInfo);
            AuditCallbackDto auditCallbackDto = auditSettingService.sure(AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, orderAgingInfo.getId(), verifyDesc,
                    AuditConsts.RESULT.SUCCESS, accessToken);
//            if (null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
//                verifyPass(auditCallbackDto.getBusiId(),auditCallbackDto.getDesc(),false,loginInfo);
//            }
            // 新加审核通过后发送
            OrderInfo orderInfo = orderinfoservice.getOrder(orderAgingInfo.getOrderId());
            OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderAgingInfo.getOrderId());
            Map<String, Object> paraMap = new HashMap<String, Object>();
            paraMap.put("tenantName", orderInfo.getTenantName());
            paraMap.put("orderId", scheduler.getOrderId());
            paraMap.put("info", "时效罚款");
            try {
                sysSmsSendService.sendSms(scheduler.getCarDriverPhone(), EnumConsts.SmsTemplate.ORDER_PRO_AGING,
                        com.youming.youche.conts.SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT,
                        com.youming.youche.conts.SysStaticDataEnum.OBJ_TYPE.ORDER_AGING, String.valueOf(orderAgingInfo.getOrderId()), paraMap, accessToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("未找到该时效信息，请联系客服！时效ID[" + agingId + "]");
            throw new BusinessException("未找到该时效信息，请联系客服！");
        }
        return true;
    }

    @Override
    public Boolean cancleAgingInfo(Long agingId, String accessToken) {
        if (agingId == null || agingId <= 0) {
            throw new BusinessException("时效ID不能为空，请联系客服！");
        }

        OrderAgingInfo agingInfo = this.getOrderAgingInfo(agingId);
        LoginInfo user = loginUtils.get(accessToken);
        if (agingInfo == null) {
            throw new BusinessException("找不到该时效信息!");
        } else {

            agingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.CANCEL);
            this.saveOrUpdate(agingInfo);

            try {
                auditOutService.cancelProcess(com.youming.youche.system.constant.AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, agingInfo.getId(), agingInfo.getTenantId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            StringBuffer buffer = new StringBuffer("[" + user.getName() + "]取消时效罚款");
            saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.AgingInfo,
                    agingInfo.getId(), buffer, user.getTenantId());
            saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OrderInfo,
                    agingInfo.getOrderId(),
                    buffer, user.getTenantId());
        }
        return true;
    }

    public void saveSysOperLog(LoginInfo user, SysOperLogConst.BusiCode busiCode, Long orderId,
                               StringBuffer stringBuffer, Long tenantId) {
        SysOperLog operLog = new SysOperLog();
        operLog.setOpName(user.getName());
        operLog.setOpId(user.getId());
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(orderId);
        operLog.setOperType(19);
        operLog.setOperTypeName("取消");
        operLog.setOperComment(stringBuffer.toString());
        operLog.setTenantId(tenantId);
        sysOperLogService.save(operLog);
    }

    /**
     * 获取时效信息
     *
     * @param id
     * @param accessToken
     * @return
     */
    @Override
    public OrderAgingInfo getAgingInfo(Long id, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<OrderAgingInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderAgingInfo::getId, id)
                .eq(OrderAgingInfo::getTenantId, loginInfo.getTenantId());
        OrderAgingInfo orderAgingInfo = baseMapper.selectOne(queryWrapper);
        OrderAgingInfo orderAgingInfoDto1 = orderAgingListOutDtoTransfer.toOrderAging(orderAgingInfo);

        return orderAgingInfoDto1;

    }

    /**
     * 查询 订单报备
     *
     * @param orderId 订单号
     * @return
     */
    @Override
    public List<OrderReportDto> queryOrderReport(Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OrderReportDto> reportDtoList = iOrderReportService.queryOrderReportList(orderId, null, null, accessToken);
        return reportDtoList;
    }

    @Override
    public List<OrderAgingListOutDto> queryOrderAgingInfoList(Long orderId, Integer selectType) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空，请联系客服！");
        }
        List<OrderAgingListOutDto> orderAgingListOuts = new ArrayList<OrderAgingListOutDto>();

        if (selectType != null) {
            if (selectType == 1) {//承运方时效
                List<QueryAuditAgingDto> list = orderAgingInfoMapper.queryOrderAgingList(orderId, null);
                if (list != null && list.size() > 0) {
                    for (QueryAuditAgingDto map : list) {
                        OrderAgingListOutDto orderAgingListOut = new OrderAgingListOutDto();
                        BeanUtils.copyProperties(map, orderAgingListOut);
                        orderAgingListOut.setSelectType(selectType);
                        if (orderAgingListOut.getAppealSts() != null) {
                            orderAgingListOut.setAppealStsName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(EnumConsts.SysStaticData.PRO_STATE, orderAgingListOut.getAppealSts() + ""));
                        }
                        orderAgingListOuts.add(orderAgingListOut);
                    }
                }
                // 数据转换
                List<OrderAgingListOutDto> orderAgingListOutDtos = orderAgingListOutDtoTransfer.
                        toOrderAgingListOutDtoList(orderAgingListOuts);
                return orderAgingListOutDtos;
            } else {//客户时效
                OrderInfo orderInfo = orderinfoservice.getOrder(orderId);
                Long fromOrderId = null;
                if (orderInfo == null) {
                    OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
                    if (orderInfoH != null) {
                        fromOrderId = orderInfoH.getFromOrderId() == null ? orderInfoH.getOrderId() : orderInfoH.getFromOrderId();
                    } else {
                        throw new BusinessException("找不到[" + orderId + "]该订单信息!");
                    }
                } else {
                    fromOrderId = orderInfo.getFromOrderId() == null ? orderInfo.getOrderId() : orderInfo.getFromOrderId();
                }
                // 订单来源编号
                if (fromOrderId != null && fromOrderId > 0) {
                    List<QueryAuditAgingDto> list = orderAgingInfoMapper.queryOrderAgingList(fromOrderId, SysStaticDataEnum.EXPENSE_STATE.END);
                    if (list != null && list.size() > 0) {
                        for (QueryAuditAgingDto map : list) {
                            OrderAgingListOutDto orderAgingListOut = new OrderAgingListOutDto();
                            BeanUtils.copyProperties(map, orderAgingListOut);
                            orderAgingListOut.setSelectType(selectType);
                            if (orderAgingListOut.getAppealSts() != null) {
                                orderAgingListOut.setAppealStsName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(EnumConsts.SysStaticData.PRO_STATE, orderAgingListOut.getAppealSts() + ""));
                            }
                            orderAgingListOuts.add(orderAgingListOut);
                        }
                    }
                    List<OrderAgingListOutDto> listOutDtos = orderAgingListOutDtoTransfer.
                            toOrderAgingListOutDtoList(orderAgingListOuts);
                    return listOutDtos;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public OrderAgingListOutDto queryOrderAgingInfo(Long agingId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        // 查询时效罚款记录
        OrderAgingInfo orderAgingInfo = orderAgingInfoMapper.selectById(agingId);
        OrderAgingListOutDto out = null;
        if (orderAgingInfo != null) {
            out = new OrderAgingListOutDto();
            BeanUtil.copyProperties(orderAgingInfo, out);

            out.setOrderArriveDate(DateUtil.formatLocalDateTime(orderAgingInfo.getOrderArriveDate(), DateUtil.DATETIME_FORMAT));
            out.setOrderStartDate(DateUtil.formatLocalDateTime(orderAgingInfo.getOrderStartDate(), DateUtil.DATETIME_FORMAT));
            out.setAgingId(orderAgingInfo.getId());
            out.setAgingSts(orderAgingInfo.getAuditSts());
            OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderAgingInfo.getOrderId());
            out.setAppealType(0);//可申诉

            // 查询时效罚款申诉信息
            LambdaQueryWrapper<OrderAgingAppealInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OrderAgingAppealInfo::getAgingId, agingId);
            wrapper.last("limit 1");
            OrderAgingAppealInfo appeal = orderAgingAppealInfoMapper.selectOne(wrapper);
            if (appeal != null) {
                out.setAppealType(1);//不可申诉
                out.setAppealId(appeal.getId());
                out.setAppealSts(appeal.getAuditSts());
            }

            if (scheduler == null) {
                OrderSchedulerH schedulerH = orderSchedulerHService.getOrderSchedulerH(orderAgingInfo.getOrderId());
                if (schedulerH != null) {
                    out.setAppealType(1);//不可申诉
                }
                LambdaQueryWrapper<OrderAgingAppealInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(OrderAgingAppealInfo::getAgingId, agingId);
                queryWrapper.last("limit 1");

                // 查询时效罚款申诉信息
                OrderAgingAppealInfo appealInfo = orderAgingAppealInfoMapper.selectOne(queryWrapper);
                if (appealInfo != null) {
                    out.setAppealType(1);//不可申诉
                    out.setAppealId(appealInfo.getId());
                    out.setAppealSts(appealInfo.getAuditSts());
                }

                // 查询时效罚款日志信息
                List<SysOperLog> list = sysOperLogService.querySysOperLog(SysOperLogConst.BusiCode.AgingInfo,
                        out.getAgingId(), true, tenantId, com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, null);

                List<SysOperLogDto> sysOperLogDtoList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(list)) {
                    for (SysOperLog sysOperLog : list) {
                        SysOperLogDto sysOperLogDto = new SysOperLogDto();
                        BeanUtil.copyProperties(sysOperLog, sysOperLogDto);
                        sysOperLogDtoList.add(sysOperLogDto);
                    }
                }
                out.setOperLogs(sysOperLogDtoList);
            }
        }

        if (out != null) {
            if (out.getOrderType() != null && out.getOrderType() > 0) {
                out.setOrderTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("ORDER_TYPE", out.getOrderType() + ""));
            }

            if (out.getSourceRegion() != null && (out.getSourceRegion()) != 0) {
                out.setSourceRegionName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("SYS_CITY", out.getSourceRegion() + ""));
            }

            if (out.getDesRegion() != null && (out.getDesRegion()) != 0) {
                out.setDesRegionName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("SYS_CITY", out.getDesRegion() + ""));
            }

            if (out.getAuditState() != null && out.getAuditState() >= 0) {
                out.setAuditStateName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(EnumConsts.SysStaticData.PRO_STATE, out.getAuditState() + ""));
            }

            if (out.getOrderState() != null && out.getOrderState() > 0) {
                out.setOrderStateName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("ORDER_STATE", out.getOrderState() + ""));
            }

            if (out.getAgingSts() != null && out.getAgingSts() >= 0) {
                out.setAgingStsName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(EnumConsts.SysStaticData.PRO_STATE, out.getAgingSts() + ""));
            }

            if (out.getAppealSts() != null && out.getAppealSts() >= 0) {
                out.setAppealStsName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(EnumConsts.SysStaticData.PRO_STATE, out.getAppealSts() + ""));
            } else {
                out.setAuditStateName("无");
            }
        }
        return out;
    }


    @Override
    public List<OrderAgingInfo> queryAgingInfoByUserId(Long orderId, Long userId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号为空!");
        }
        LambdaQueryWrapper<OrderAgingInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderAgingInfo::getOrderId, orderId);
        if (userId != null && userId > 0) {
            queryWrapper.eq(OrderAgingInfo::getUserId, userId);
        }
        queryWrapper.orderByDesc(OrderAgingInfo::getCreateTime);
        List<OrderAgingInfo> list = this.baseMapper.selectList(queryWrapper);
        if (list != null && list.size() > 0) {
            return list;
        } else {
            return null;
        }
    }

    @Override
    public void verifyFirst(Long agingId, String verifyDesc, String accessToken) {
        if (agingId == null || agingId <= 0) {
            throw new BusinessException("时效ID不能为空！");
        }
        OrderAgingInfo orderAgingInfo = baseMapper.selectById(agingId);
        if (orderAgingInfo != null) {
            orderAgingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
            baseMapper.updateById(orderAgingInfo); // 修改审核状态

            // 审核流程
            AuditCallbackDto auditCallbackDto = auditSettingService.sure(AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, orderAgingInfo.getId(),
                    verifyDesc, AuditConsts.RESULT.SUCCESS, accessToken);

//            if (null != auditCallbackDto && auditCallbackDto.getIsAudit()) {
//                // 审核成功
//                this.sucess(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), auditCallbackDto.getParamsMap(), accessToken);
//            }

        } else {
            log.error("未找到该时效信息，请联系客服！时效ID[" + agingId + "]");
            throw new BusinessException("未找到该时效信息，请联系客服！");
        }
    }

    @Override
    public void sucess(Long agingId, String desc, Map paramsMap, String accessToken) {
        this.success(agingId, desc, false, accessToken);
    }

    @Override
    public void success(Long agingId, String verifyDesc, Boolean isFirst, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        OrderAgingInfo orderAgingInfo = baseMapper.selectById(agingId);
        if (orderAgingInfo != null) {
            orderAgingInfo.setAuditDate(LocalDateTime.now());
            orderAgingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.END);
            // 修改时效罚款记录表：审核时间 和 审核状态
            baseMapper.updateById(orderAgingInfo);
            OrderInfo orderInfo = orderinfoservice.getOrder(orderAgingInfo.getOrderId());
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderAgingInfo.getOrderId());
            OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderAgingInfo.getOrderId());
            OrderFee orderFee = orderFeeService.getOrderFee(orderAgingInfo.getOrderId());
            //校验扣减项是否超过尾款
            orderFeeService.verifyFeeOut(orderScheduler, orderFee, orderInfoExt, orderAgingInfo.getFinePrice(), orderAgingInfo.getId(), -1L);
            // 同步支付中心
            orderFeeService.synPayCenterUpdateOrderOrProblemInfo(orderInfo, orderScheduler);
            if (orderInfo != null && orderScheduler != null) {
                //自有车 或者 纯C外调车订单才发送短信
                if (orderScheduler.getVehicleClass() != null
                        && (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        || ((orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                        && (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0)))) {
                    Map<String, Object> paraMap = new HashMap<String, Object>();
                    paraMap.put("tenantName", orderInfo.getTenantName());
                    paraMap.put("orderId", String.valueOf(orderInfo.getOrderId()));
                    paraMap.put("info", "时效罚款");
                    SysSmsSend sysSmsSend = new SysSmsSend();
                    sysSmsSend.setBillId(orderScheduler.getCarDriverPhone());
                    sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT);
                    sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.ORDER_PRO_AGING);
                    sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.ORDER_AGING));
                    sysSmsSend.setObjId(String.valueOf(orderInfo.getOrderId()));
                    sysSmsSend.setParamMap(paraMap);
                    sysSmsSendService.sendSms(sysSmsSend);
                }
            }
            if (!isFirst) {
                sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OrderInfo, orderAgingInfo.getOrderId(), SysOperLogConst.OperType.Update, loginInfo.getName() + " 过,[" + (StringUtils.isNotEmpty(verifyDesc) ? verifyDesc : "") + "]");
                sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.AgingInfo, orderAgingInfo.getId(), SysOperLogConst.OperType.Audit, loginInfo.getName() + "审核时效罚款通过,[" + (StringUtils.isNotEmpty(verifyDesc) ? verifyDesc : "") + "]");
            }
        } else {
            log.error("未找到该时效信息，请联系客服！时效ID[" + agingId + "]");
            throw new BusinessException("未找到该时效信息，请联系客服！");
        }
    }

    @Override
    public boolean saveAppealInfo(SaveAppealInfoVo saveAppealInfoVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isBlank(saveAppealInfoVo.getRemark())) {
            throw new BusinessException("请填写申诉原因!");
        }
        if (StringUtils.isBlank(saveAppealInfoVo.getImgUrls()) || StringUtils.isBlank(saveAppealInfoVo.getImgUrls())) {
            throw new BusinessException("请上传附件!");
        }
        if (saveAppealInfoVo.getAgingId() == null || saveAppealInfoVo.getAgingId() <= 0) {
            throw new BusinessException("未找到时效罚款信息，请联系客服！");
        }
        OrderAgingInfo agingInfo = this.getOrderAgingInfo(saveAppealInfoVo.getAgingId());
        if (agingInfo == null) {
            log.error("未找到时效罚款信息，请联系客服！时效ID[" + saveAppealInfoVo.getAgingId() + "]");
            throw new BusinessException("未找到时效罚款信息，请联系客服！");
        }
        OrderAgingAppealInfo agingAppealInfo = orderAgingAppealInfoService.getAppealInfoBYAgingId(saveAppealInfoVo.getAgingId(), false);
        if (agingAppealInfo != null) {
            throw new BusinessException("该时效罚款已申诉,不能重复申诉!");
        }
        if (agingInfo.getDeductionFee() != null && agingInfo.getDeductionFee() > 0) {
            throw new BusinessException("该时效罚款已经从到付款中扣除，不能可申诉！");
        }
        OrderAgingAppealInfo info = new OrderAgingAppealInfo();
        info.setAgingId(saveAppealInfoVo.getAgingId());
        info.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING);
        info.setCreateTime(LocalDateTime.now());
        info.setImgIds(saveAppealInfoVo.getImgIds());
        info.setImgUrls(saveAppealInfoVo.getImgUrls());
        info.setRemark(saveAppealInfoVo.getRemark());
        info.setOpId(loginInfo.getId());
        info.setOpName(loginInfo.getName());
        info.setTenantId(agingInfo.getTenantId());
        //  String tenantName = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId()).getName();
        orderAgingAppealInfoService.saveOrUpdate(info);
        //根据传入的业务编码保存日志
//        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.AgingInfo, saveAppealInfoVo.getAgingId(), SysOperLogConst.OperType.Update,
//                (saveAppealInfoVo.getIsCustomerAging() != null && saveAppealInfoVo.getIsCustomerAging() ? tenantName : loginInfo.getName()) + "添加申诉");
        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.AgingInfo, saveAppealInfoVo.getAgingId(), SysOperLogConst.OperType.Update,
                loginInfo.getName() + "添加申诉", agingInfo.getTenantId());
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("busiId", info.getId());
        boolean isAudit = auditService.startProcess(AuditConsts.AUDIT_CODE.AGING_APPEAL_CODE, info.getId(), SysOperLogConst.BusiCode.AgingAppeal, params, accessToken, agingInfo.getTenantId());
        if (!isAudit) {//无审核 直接通过审核
            this.verifyPass(info.getId(), "无审核流程,直接通过", true, loginInfo);
        }
        return true;
    }

    @Override
    public List<OrderAgingListDto> getOrderAgingInfoList(Long orderId, Long userId) {
        List<OrderAgingInfo> orderAgingInfos = this.queryAgingInfoByUserId(orderId, userId);
        List<OrderAgingListDto> orderAgingListOuts = new ArrayList<>();
        if (orderAgingInfos != null && orderAgingInfos.size() > 0) {
            for (OrderAgingInfo orderAgingInfo : orderAgingInfos) {
                if (orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
                    OrderAgingListDto out = new OrderAgingListDto();
                    BeanUtils.copyProperties(orderAgingInfo, out);
                    out.setAgingId(orderAgingInfo.getId());
                    out.setAgingSts(orderAgingInfo.getAuditSts());
                    if (out.getAppealSts() != null && out.getAppealSts() >= 0) {
                        out.setAppealStsName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(EnumConsts.SysStaticData.PRO_STATE, out.getAppealSts() + ""));
                    }
                    if (out.getAgingSts() != null && out.getAgingSts() >= 0) {
                        out.setAgingStsName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(EnumConsts.SysStaticData.PRO_STATE, out.getAgingSts() + ""));
                    }
                    OrderScheduler scheduler = orderSchedulerService.getOrderScheduler(orderAgingInfo.getOrderId());
                    out.setAppealType(0);//可申诉
                    if (scheduler == null) {
                        OrderSchedulerH schedulerH = orderSchedulerService.getOrderSchedulerH(orderAgingInfo.getOrderId());
                        if (schedulerH != null) {
                            out.setAppealType(1);//不可申诉
                        }
                    }
                    OrderAgingAppealInfo appealInfo = orderAgingAppealInfoService.getAppealInfoBYAgingId(orderAgingInfo.getId());
                    if (appealInfo != null) {
                        out.setAppealType(1);//不可申诉
                        out.setAppealId(appealInfo.getId());
                        out.setAppealSts(appealInfo.getAuditSts());
                    }
                    orderAgingListOuts.add(out);
                }
            }
        }
        return orderAgingListOuts;
    }

    @Override
    public OrderAgingAppealInfoDto queryAppealInfo(Long agingId, String accessToken) {
        //根据时效查询申诉
        OrderAgingAppealInfo info = orderAgingAppealInfoService.getAppealInfoBYAgingId(agingId, true);
        List<SysOperLog> listOut = new ArrayList<>();
        if (info == null) {
            info = new OrderAgingAppealInfo();
        } else {

        }
        //查询业务的操作日志
        List<SysOperLog> list = sysOperLogService.querySysOperLogIntf(300002, agingId, false, AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, 300002L, accessToken);
//        for (SysOperLog sysOperLog : list) {
//            if (sysOperLog.getOperType().intValue() == SysOperLogConst.OperType.AuditUser.getCode()
//                    || sysOperLog.getOperType().intValue() == SysOperLogConst.OperType.Audit.getCode()) {
//                listOut.add(sysOperLog);
//            }
//        }
        info.setOperLogs(list);
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info.getImgUrls() != null) {
            String[] imgUrlArr = info.getImgUrls().split(",");
            for (int i = 0; i < imgUrlArr.length; i++) {
                try {
                    imgUrlArr[i] = client.getHttpURL(imgUrlArr[i]).split("\\?")[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            info.setImgUrlArr(imgUrlArr);
        } else {
            info.setImgUrlArr(new String[]{});
        }

        OrderAgingAppealInfoDto dto = new OrderAgingAppealInfoDto();
        BeanUtil.copyProperties(info, dto);

        if (dto.getAuditSts() != null) {
            dto.setAuditStsName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String(EnumConsts.SysStaticData.PRO_STATE, dto.getAuditSts() + ""));
        }
        return dto;
    }

    @Override
    public void cancelOrderAgingInfo(Long agingId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (agingId == null || agingId <= 0) {
            throw new BusinessException("时效ID不能为空，请联系客服！");
        }
        OrderAgingInfo agingInfo = baseMapper.selectById(agingId);
        if (agingInfo == null) {
            throw new BusinessException("找不到该时效信息!");
        } else {
            agingInfo.setAuditSts(SysStaticDataEnum.EXPENSE_STATE.CANCEL);
            baseMapper.updateById(agingInfo);
            // 取消审核流程
            auditOutService.cancelProcess(com.youming.youche.conts.AuditConsts.AUDIT_CODE.ORDER_AGING_CODE, agingInfo.getId(), agingInfo.getTenantId());
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.AgingInfo, agingInfo.getId(), SysOperLogConst.OperType.Update, "[" + loginInfo.getName() + "]取消时效罚款");
        }
    }

    @Override
    public boolean saveOrderReport(Long userId, Long orderId, String imgIds, String imgUrls, String reportDesc, Integer reportType) {
        OrderReport orderReport = new OrderReport();
        if (StringUtils.isNotBlank(imgIds)) {
            orderReport.setImgIds(imgIds);
        }
        if (StringUtils.isNotBlank(imgUrls)) {
            orderReport.setImgUrls(imgUrls);
        }
        if (orderId != null && orderId > 0) {
            orderReport.setOrderId(orderId);
        } else {
            throw new BusinessException("订单号为空，请联系客服！");
        }
        OrderInfo orderInfo = orderinfoservice.getOrder(orderId);
        if (orderInfo == null) {
            throw new BusinessException("未找到[" + orderId + "]订单!");
        }
        if (orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.ARRIVED) {
            throw new BusinessException("订单到达后不能再添加报备!");
        }
        if (StringUtils.isNotBlank(reportDesc)) {
            orderReport.setReportDesc(reportDesc);
        } else {
            throw new BusinessException("请输入报备描述!");
        }
        if (reportType != null && reportType > 0) {
            orderReport.setReportType(reportType);
        } else {
            throw new BusinessException("请选择报备类型!");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException("报备人不能为空，请联系客服!");
        }
        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(userId);
        if (userDataInfo == null) {
            log.error("未找到该报备人，请联系客服！用户ID[" + userId + "]");
            throw new BusinessException("未找到该报备人，请联系客服！");
        }
        orderReport.setCreateTime(LocalDateTime.now());
        orderReport.setReportUserId(userId);
        orderReport.setReportUserName(userDataInfo.getLinkman());
        orderReport.setReportUserPhone(userDataInfo.getMobilePhone());
        orderReport.setTenantId(orderInfo.getTenantId());
        orderReportService.save(orderReport);
        return true;
    }

    @Override
    public List<OrderAgingInfoOutDto> doQueryTimeLimitFine(Long orderId, Long tenantId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号为空!");
        }

        LambdaQueryWrapper<OrderAgingInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderAgingInfo::getOrderId, orderId);
        queryWrapper.orderByDesc(OrderAgingInfo::getCreateTime);

        // 查询订单时效罚款
        List<OrderAgingInfo> orderAgingInfoList = baseMapper.selectList(queryWrapper);

        List<OrderAgingInfoOutDto> orderAgingInfoOutList = new ArrayList<>();
        if (orderAgingInfoList != null && orderAgingInfoList.size() > 0) {
            for (OrderAgingInfo orderAgingInfo : orderAgingInfoList) {
                OrderAgingInfoOutDto orderAgingInfoOut = new OrderAgingInfoOutDto();
                BeanUtils.copyProperties(orderAgingInfo, orderAgingInfoOut);
                orderAgingInfoOut.setArriveTimeDouble(CommonUtil.getDoubleFormatLongMoney(orderAgingInfo.getArriveTime(), 2));
                orderAgingInfoOut.setArriveHourDouble(CommonUtil.getDoubleFormatLongMoney(orderAgingInfo.getArriveHour(), 2));
                orderAgingInfoOut.setFinePriceDouble(-(CommonUtil.getDoubleFormatLongMoney(orderAgingInfo.getFinePrice(), 2)));
                if (orderAgingInfo.getAuditSts().equals(3)) {
                    orderAgingInfoOutList.add(orderAgingInfoOut);
                }
            }
        }
        return orderAgingInfoOutList;
    }

    @Override
    public Integer getStatisticsStatuteOfLimitations(LoginInfo loginInfo) {
        List<Long> agingIds = auditNodeInstService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.ORDER_AGING_CODE,
                loginInfo.getUserInfoId(), loginInfo.getTenantId(), 500);
        List<Long> appealIds = auditNodeInstService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.AGING_APPEAL_CODE,
                loginInfo.getUserInfoId(), loginInfo.getTenantId(), 500);

        StringBuffer agingIdBuf = new StringBuffer();
        if (agingIds != null && agingIds.size() > 0) {
            for (Long agingId : agingIds) {
                agingIdBuf.append("'").append(agingId).append("',");
            }
        }

        StringBuffer appealIdBuf = new StringBuffer();
        if (appealIds != null && appealIds.size() > 0) {
            for (Long appealId : appealIds) {
                appealIdBuf.append("'").append(appealId).append("',");
            }
        }
        String agingIdStr = "";
        if (agingIdBuf != null && agingIdBuf.length() > 0) {
            agingIdStr = agingIdBuf.substring(0, agingIdBuf.length() - 1);
        }
        String appealIdStr = "";
        if (appealIdBuf != null && appealIdBuf.length() > 0) {
            appealIdStr = appealIdBuf.substring(0, appealIdBuf.length() - 1);
        }

        return baseMapper.getStatisticsStatuteOfLimitations(agingIdStr, appealIdStr, loginInfo.getTenantId());
    }

}
