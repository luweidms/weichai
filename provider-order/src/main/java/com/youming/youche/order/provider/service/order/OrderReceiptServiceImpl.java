package com.youming.youche.order.provider.service.order;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.IPayManagerService;
import com.youming.youche.order.api.order.IClaimExpenseInfoService;
import com.youming.youche.order.api.order.IOrderAgingAppealInfoService;
import com.youming.youche.order.api.order.IOrderCostReportService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilCardInfoService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoService;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderReceiptService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoHService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.api.order.IOverdueReceivableService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.other.IAuxiliaryService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.PayForFinalChargeIn;
import com.youming.youche.order.domain.order.OrderAgingAppealInfo;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOpRecord;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.OrderReceipt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;
import com.youming.youche.order.domain.order.OverdueReceivable;
import com.youming.youche.order.dto.OrderDriverSubsidyDto;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.OrderListAppOutDto;
import com.youming.youche.order.dto.OrderReceiptDto;
import com.youming.youche.order.dto.QueryDriverOrderDto;
import com.youming.youche.order.provider.mapper.OrderFeeMapper;
import com.youming.youche.order.provider.mapper.order.OrderAgingInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderFeeExtMapper;
import com.youming.youche.order.provider.mapper.order.OrderGoodsMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoExtMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderOpRecordMapper;
import com.youming.youche.order.provider.mapper.order.OrderReceiptMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.order.vo.OrderReceiptVo;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.HtmlEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * 订单-回单 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OrderReceiptServiceImpl extends BaseServiceImpl<OrderReceiptMapper, OrderReceipt> implements IOrderReceiptService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    private ReadisUtil readisUtil;
    @Resource
    private OrderGoodsMapper orderGoodsMapper;

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private OrderSchedulerMapper orderSchedulerMapper;

    @Resource
    private OrderInfoExtMapper orderInfoExtMapper;

    @Resource
    private OrderOpRecordMapper opRecordMapper;

    @Resource
    private OrderFeeMapper orderFeeMapper;

    @Resource
    private IOrderFeeService iOrderFeeService;

    @Resource
    private OrderFeeExtMapper orderFeeExtMapper;

    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @Lazy
    @Resource
    private IOrderInfoService iOrderInfoService;

    @Resource
    private IOrderGoodsService iOrderGoodsService;

    @Resource
    private  IPayFeeLimitService iPayFeeLimitService;

    @Resource
    private  IOrderLimitService iOrderLimitService;

    @Resource
    private  IOrderInfoHService iOrderInfoHService;

    @Resource
    private  IOrderGoodsHService iOrderGoodsHService;

    @Resource
    private  IOrderFeeStatementService iOrderFeeStatementService;

    @Resource
    private  IOrderCostReportService iOrderCostReportService;

    @Resource
    private IOilCardManagementService iOilCardManagementService;

    @Resource
    private IPayManagerService iPayManagerService;

    @Resource
    private  IOrderFeeExtService iOrderFeeExtService;

    @Resource
    private  IOrderProblemInfoService iOrderProblemInfoService;

    @Resource
    private  OrderAgingInfoMapper orderAgingInfoMapper;

    @Resource
    private  IOrderAgingAppealInfoService iOrderAgingAppealInfoService;

    @Resource
    private  IClaimExpenseInfoService iClaimExpenseInfoService;

    @Resource
    private IOrderDriverSubsidyService iOrderDriverSubsidyService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Resource
    private  IOrderPaymentDaysInfoService iOrderPaymentDaysInfoService;

    @Resource
    private IOverdueReceivableService overdueReceivableService;

    @Resource
    private  IOrderOilCardInfoService iOrderOilCardInfoService;

    @Resource
    private  IOperationOilService iOperationOilService;

    @Resource
    OrderReceiptMapper orderReceiptMapper;

    @Resource
    IOrderSchedulerService orderSchedulerService;

    @Resource
    IOrderTransitLineInfoHService orderTransitLineInfohService;

    @Resource
    IOrderTransitLineInfoService orderTransitLineInfoService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    /**
     * 保存 上传回单
     *
     * @param
     * @param
     * @param accessToken
     */
    @Override
    public void saveLoadRecive(OrderReceiptVo orderReceiptVo, boolean isUpdateState, String accessToken) {
        List<OrderReceipt> orderRecipts = new ArrayList<>();
        orderRecipts = orderReceiptVo.getOrderRecipts();
        Long orderIds = orderRecipts.size()>0 ? orderRecipts.get(0).getOrderId() : null;
        if (orderIds == null || orderIds <= 0) {
            throw new BusinessException("缺少订单号");
        }
        if (orderReceiptVo.getOrderRecipts().size() > 20) {
            throw new BusinessException("最多允许上传20张回单!");
        }
        QueryWrapper<OrderReceipt> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderIds);
        List<OrderReceipt> preOrderReceipts = baseMapper.selectList(wrapper);
        List<OrderReceipt> removes = new ArrayList<OrderReceipt>();//需要移除的回单
        List<OrderReceipt> adds = new ArrayList<OrderReceipt>();//需要新增的回单
        List<OrderReceipt> updates = new ArrayList<OrderReceipt>();//需要更新的回单

        if (orderRecipts == null || orderRecipts.size() == 0) {
            removes = preOrderReceipts;
        } else {
            int index = -1;
            OrderReceipt tmp = null;
            if (preOrderReceipts == null) {
                preOrderReceipts = new ArrayList<OrderReceipt>();
            }

            for (OrderReceipt orderReceipt : orderRecipts) {
                if (StringUtils.isBlank(orderReceipt.getFlowId()) || StringUtils.isBlank(orderReceipt.getFlowUrl())
                        || StringUtils.isBlank(orderReceipt.getReciveNumber())) {
                    continue;
                }
                if ((StringUtils.isBlank(orderReceipt.getFlowId()) || StringUtils.isBlank(orderReceipt.getFlowUrl()))
                        && StringUtils.isNotBlank(orderReceipt.getReciveNumber())) {
                    throw new BusinessException("存在回单编号不为空，没有上传回单图片的数据！");
                }
                if (StringUtils.isNotBlank(orderReceipt.getFlowId()) && StringUtils.isNotBlank(orderReceipt.getFlowUrl())
                        && StringUtils.isBlank(orderReceipt.getReciveNumber())) {
                    throw new BusinessException("存在回单图片已上传，未填写回单编号的数据！");
                }

                index = preOrderReceipts.indexOf(orderReceipt);
                if (index < 0) {
                    adds.add(orderReceipt);
                }
            }

            if (preOrderReceipts != null && preOrderReceipts.size() > 0) {
                for (OrderReceipt orderReceipt : preOrderReceipts) {
                    index = orderRecipts.indexOf(orderReceipt);
                    if (index >= 0) {
                        tmp = orderRecipts.get(index);
                        orderReceipt.setReciveNumber(tmp.getReciveNumber());
                        orderReceipt.setFlowId(tmp.getFlowId());
                        orderReceipt.setFlowUrl(tmp.getFlowUrl());
                        updates.add(orderReceipt);
                    } else {
                        removes.add(orderReceipt);
                    }
                }
//                for (OrderReceipt orderReceipt : orderRecipts) {
//                    index=preOrderReceipts.indexOf(orderReceipt);
//                    if(index<0) {
//                        adds.add(orderReceipt);
//                    }
//                }
            }
        }

        //移除回单
        for (OrderReceipt orderReceipt : removes) {
            QueryWrapper<OrderReceipt> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", orderIds);
            if (orderReceipt.getFlowId() != null) {
                queryWrapper.eq("flow_id", orderReceipt.getFlowId());
            }
            baseMapper.delete(queryWrapper);
        }
        //更新回单
        for (OrderReceipt orderReceipt : updates) {
            this.saveOrUpdate(orderReceipt);
        }
        //新增回单
        for (OrderReceipt orderReceipt : adds) {
            orderReceipt.setOrderId(orderIds);
            baseMapper.insert(orderReceipt);
        }
//        修改回单状态
        if (isUpdateState) {
            QueryWrapper<OrderGoods> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", orderIds);
            List<OrderGoods> orderGood = orderGoodsMapper.selectList(queryWrapper);
            Set<OrderGoods> set = new HashSet<>(orderGood);
            if (set != null && set.size() > 0) {
                Iterator<OrderGoods> it = set.iterator();
                while (it.hasNext()) {
                    OrderGoods goodsInfo = it.next();
                    goodsInfo.setReciveState(adds.size() == 0 && updates.size() == 0 ? OrderConsts.ReciveState.NOT_UPLOAD : OrderConsts.ReciveState.NOT_VERIFY);
                    iOrderGoodsService.update(goodsInfo);//订单货物
                }
            }
        }
    }

    /**
     * 查看回单
     *
     * @param orderId 订单号
     * @return
     */
    @Override
    public List<OrderReceipt> findOrderReceipts(Long orderId, String accessToken, String flowId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("缺少订单号！");
        }
        QueryWrapper<OrderReceipt> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        if (!StringUtils.isEmpty(flowId)) {
            wrapper.eq("flow_id", flowId);
        }
        List<OrderReceipt> orderReceipts = baseMapper.selectList(wrapper);
        return orderReceipts;
    }


    /**
     * 聂杰伟
     * 图片上传 合同上传／回单上传
     *
     * @param orderId
     * @param type      business 合同
     *                  recive 回单
     * @param loadUrl
     * @param loadPicId
     */
    @Override
    public void upLoadPic(Long orderId, String type, String loadUrl, String loadPicId, String receiptsNumber, Long receiptId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
        if (orderInfo == null) {
            throw new BusinessException("未找到订单[" + orderId + "]信息");
        }
        if (StringUtils.isBlank(type)) {
            throw new BusinessException("图片上传异常！");
        }
        if (StringUtils.isBlank(loadPicId)) {
            throw new BusinessException("图片上传异常，请重新上传！");
        }
        if (StringUtils.isBlank(loadUrl)) {
            throw new BusinessException("图片上传异常，请重新上传！");
        }
        QueryWrapper<OrderGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("order_id", orderId);
        List<OrderGoods> orderGoods = orderGoodsMapper.selectList(goodsQueryWrapper);
        Set<OrderGoods> set = new HashSet<>(orderGoods);//去重
        if (set == null || set.size() <= 0) {
            throw new BusinessException("未找到订单货物[" + orderId + "]信息！");
        }
        // 操作日志
        String desc = "";
        if (type.equals("business")) {
            desc = "上传合同";
        } else if (type.equals("recive")) {
            desc = "上传回单";
        }
        Iterator it = (Iterator) set.iterator();
        while (it.hasNext()) {
            OrderGoods goodsInfo = (OrderGoods) it.next();
            if (type.equals("business")) {//合同上传

                goodsInfo.setContractUrl(loadUrl);
                goodsInfo.setContractId(Long.valueOf(loadPicId));
                goodsInfo.setLoadState(OrderConsts.ReciveState.NOT_VERIFY);
            } else if (type.equals("recive")) { //recive 回单上传
                if (StringUtils.isBlank(receiptsNumber)) {
                    throw new BusinessException("请填写回单编号！");
                }
                if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0
                        && (goodsInfo.getReciveState() == null || goodsInfo.getReciveState() == OrderConsts.ReciveState.NOT_UPLOAD)) {
                    QueryWrapper<OrderInfo> infoQueryWrapper = new QueryWrapper<>();
                    infoQueryWrapper.eq("from_order_id", orderInfo.getFromOrderId());
                    OrderInfo fromOrder = orderInfoMapper.selectOne(infoQueryWrapper);
                    if (fromOrder != null) {
                        //上传订单回单
                        this.uploadRecive(null, fromOrder.getOrderId(), loadPicId, loadUrl, receiptsNumber);
                        QueryWrapper<OrderGoods> orderGoodsQueryWrapper = new QueryWrapper<>();
                        goodsQueryWrapper.eq("order_id", orderId);
                        List<OrderGoods> goods = orderGoodsMapper.selectList(orderGoodsQueryWrapper);
                        Set<OrderGoods> orderGoodsSetet = new HashSet<>(goods);//去重

                        Iterator setFromIt = (Iterator) orderGoodsSetet.iterator();
                        while (setFromIt.hasNext()) {
                            OrderGoods goodsInfoFrom = (OrderGoods) setFromIt.next();
                            goodsInfoFrom.setReciveState(OrderConsts.ReciveState.NOT_VERIFY);
                            iOrderGoodsService.saveOrUpdate(goodsInfoFrom);
                        }
                        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getFromOrderId(),
                                SysOperLogConst.OperType.Update, "[" + orderInfo.getTenantName() + "]" + desc);
                        if (fromOrder.getFromOrderId() != null && fromOrder.getFromOrderId() > 0) {// 上层来源
                            // 最多两层
                            QueryWrapper<OrderGoods> orderGoodsQueryWrapper1 = new QueryWrapper<>();
                            goodsQueryWrapper.eq("order_id", orderId);
                            List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(orderGoodsQueryWrapper1);
                            Set<OrderGoods> orderGoodsSet = new HashSet<>(orderGoodsList);//去重

                            QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
                            queryWrapper.eq("order_id", fromOrder.getFromOrderId());
                            OrderInfo fromTwoOrder = orderInfoMapper.selectOne(queryWrapper);
                            if (fromTwoOrder != null) {
                                //上传订单回单
                                this.uploadRecive(null, fromTwoOrder.getOrderId(), loadPicId, loadUrl, receiptsNumber);
                                Iterator setFromTwoIt = (Iterator) orderGoodsSet.iterator();
                                while (setFromTwoIt.hasNext()) {
                                    OrderGoods goodsInfoFrom = (OrderGoods) setFromTwoIt.next();
                                    iOrderGoodsService.saveOrUpdate(goodsInfoFrom);

                                }
                                sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OrderInfo, fromOrder.getFromOrderId(),
                                        SysOperLogConst.OperType.Update, "[" + fromOrder.getTenantName() + "]" + desc, fromTwoOrder.getTenantId());
                            }
                        }
                    }
                }
                if (orderInfo.getToOrderId() != null && orderInfo.getToOrderId() > 0
                        && (goodsInfo.getReciveState() == null || goodsInfo.getReciveState() == OrderConsts.ReciveState.NOT_UPLOAD)) {
                    // 有转单订单 并且未上传回单
                    QueryWrapper<OrderGoods> orderGoodsQueryWrapper1 = new QueryWrapper<>();
                    goodsQueryWrapper.eq("order_id", orderInfo.getToOrderId());//转出订单编号
                    List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(orderGoodsQueryWrapper1);
                    Set<OrderGoods> setTo = new HashSet<>(orderGoodsList);//去重
                    QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("order_id", orderInfo.getToOrderId());
                    OrderInfo toInfo = orderInfoMapper.selectOne(queryWrapper);
                    if (toInfo != null) {
                        //上传订单回单
                        this.uploadRecive(null, orderInfo.getToOrderId(), loadPicId, loadUrl, receiptsNumber);
                        toInfo.setOrderState(OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT);
                        iOrderInfoService.saveOrUpdate(toInfo);
                        Iterator setToIt = (Iterator) setTo.iterator();
                        while (setToIt.hasNext()) {
                            OrderGoods goodsInfoTo = (OrderGoods) setToIt.next();
                            goodsInfoTo.setReciveState(OrderConsts.ReciveState.NOT_VERIFY);
                            iOrderGoodsService.saveOrUpdate(goodsInfo);
                        }
                        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getFromOrderId(),
                                SysOperLogConst.OperType.Update, "[" + orderInfo.getTenantName() + "]" + desc);
                        if (toInfo.getToOrderId() != null && toInfo.getToOrderId() > 0) {
//                            Set<OrderGoods> lastTo = orderGoodsSV.getOrderGoodsSet(orderInfo.getToOrderId());
                            QueryWrapper<OrderGoods> orderGoodsQueryWrapper = new QueryWrapper<>();
                            goodsQueryWrapper.eq("order_id", orderInfo.getToOrderId());//转出订单编号
                            List<OrderGoods> goods = orderGoodsMapper.selectList(orderGoodsQueryWrapper);
                            Set<OrderGoods> lastTo = new HashSet<>(goods);//去重

                            QueryWrapper<OrderInfo> infoQueryWrapper = new QueryWrapper<>();
                            infoQueryWrapper.eq("order_id", orderInfo.getToOrderId());
                            OrderInfo lastInfo = orderInfoMapper.selectOne(infoQueryWrapper);
                            if (lastInfo != null) {
                                lastInfo.setOrderState(OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT);
                                iOrderInfoService.saveOrUpdate(lastInfo);
                                Iterator lastToIt = (Iterator) lastTo.iterator();
                                while (lastToIt.hasNext()) {
                                    OrderGoods goodsInfoTo = (OrderGoods) lastToIt.next();
                                    goodsInfoTo.setReciveState(OrderConsts.ReciveState.NOT_VERIFY);
                                    iOrderGoodsService.saveOrUpdate(goodsInfoTo);
                                }
                            }
                            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OrderInfo, toInfo.getToOrderId(),
                                    SysOperLogConst.OperType.Update, "[" + toInfo.getTenantName() + "]" + desc);
                        }
                    }
                }
                //上传订单回单
                this.uploadRecive(receiptId, orderInfo.getOrderId(), loadPicId, loadUrl, receiptsNumber);
                goodsInfo.setReciveState(OrderConsts.ReciveState.NOT_VERIFY);
                if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.RECIVE_AUDIT_NOT) {
                    orderInfo.setOrderState(OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT);
                }
            }
            iOrderGoodsService.saveOrUpdate(goodsInfo);
        }
        String userName = "司机";
        if (loginInfo != null) {
            userName = loginInfo.getName();
        }
        //保存日志
        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OrderInfo, orderInfo.getFromOrderId(),
                SysOperLogConst.OperType.Update, "[" + userName + "]" + desc);
        iOrderInfoService.update(orderInfo);
    }

    /**
     * @param orderId        订单号
     * @param load           合同
     * @param receipt        回单
     * @param verifyString   审核备注
     * @param receiptsNumber 回单号
     * @param type
     * @return
     */
    @Override
    public void verifyRece(String orderId, Boolean load, Boolean receipt, String verifyString, String receiptsNumber, Integer type, String accessToken, boolean... isNeedLog) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
        if (orderInfo == null) {
            throw new BusinessException("找不到订单信息");
        }
        if (orderInfo.getOrderState() != null && orderInfo.getOrderState() != OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT
                && orderInfo.getOrderState() != OrderConsts.ORDER_STATE.RECIVE_AUDIT_NOT) {
            throw new BusinessException("订单不是回单审核状态，不能操作");
        }
//        Set<OrderGoods> set = orderGoodsSV.getOrderGoodsSet(orderId);
        QueryWrapper<OrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        List<OrderGoods> set = orderGoodsMapper.selectList(queryWrapper);
        if (set == null || set.size() <= 0) {
            throw new BusinessException("未找到货物信息!!");
        }
//        OrderScheduler scheduler = orderSchedulerSV.getOrderScheduler(orderId);
        QueryWrapper<OrderScheduler> orderSchedulerQueryWrapper = new QueryWrapper<>();
        orderSchedulerQueryWrapper.eq("order_id", orderId);
        OrderScheduler scheduler = orderSchedulerMapper.selectOne(orderSchedulerQueryWrapper);
        if (scheduler == null) {
            throw new BusinessException("未找到调度信息");
        }
//        OrderInfoExt orderInfoExt = orderInfoSV.getOrderInfoExt(orderId);
        QueryWrapper<OrderInfoExt> infoExtQueryWrapper = new QueryWrapper<>();
        infoExtQueryWrapper.eq("order_id", orderId);
        OrderInfoExt orderInfoExt = orderInfoExtMapper.selectOne(infoExtQueryWrapper);
        Iterator<OrderGoods> it = (Iterator<OrderGoods>) set.iterator();
        while (it.hasNext()) {
            OrderGoods goodsInfo = (OrderGoods) it.next();
            if (!receipt) {
                // 通过
                goodsInfo.setReciveState(OrderConsts.ReciveState.PASS);
            } else {
                goodsInfo.setReciveState(OrderConsts.ReciveState.NOT_PASS);
            }
            if (!load) {
                goodsInfo.setReciveState(OrderConsts.ReciveState.PASS);
            } else {
                goodsInfo.setReciveState(OrderConsts.ReciveState.NOT_PASS);
            }
        }

        //更改回单单号
        if (StringUtils.isNotBlank(receiptsNumber)) {
            List<OrderReceipt> orderRecipts = new ArrayList<>();
            OrderReceiptVo orderReceiptVos = new OrderReceiptVo();
            receiptsNumber = HtmlEncoder.decode(receiptsNumber);
            orderRecipts = JSON.parseArray(receiptsNumber, OrderReceipt.class);
            Long orderIds = Long.valueOf(orderId);
            orderReceiptVos.setOrderId(orderIds);
            orderReceiptVos.setOrderRecipts(orderRecipts);
            this.saveLoadRecive(orderReceiptVos, false, null);
        }

        if (!receipt && !load) {
            OrderOpRecord orderOpRecord = new OrderOpRecord();
            orderOpRecord.setOrderId(Long.valueOf(orderId));
            orderOpRecord.setOpType(OrderConsts.OrderOpType.RECEIPT);
            orderOpRecord.setOpId(loginInfo.getId());
            Instant instant = new Date().toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
            orderOpRecord.setUpdateTime(localDateTime);
            opRecordMapper.insert(orderOpRecord);
            verify(orderInfo, scheduler, orderInfoExt, verifyString, type,accessToken);
            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(),
                    SysOperLogConst.OperType.Audit, "[" + loginInfo.getName() + "]审核回单，审核通过"+(StringUtils.isNotBlank(verifyString)?"(审核意见:" + verifyString + ")":""));
        } else {
            // 操作日志
            orderInfo.setOrderState(OrderConsts.ORDER_STATE.RECIVE_AUDIT_NOT);
            String desc = "";
            if (receipt && load) {
                desc = "合同/回单";
            } else if (receipt) {
                desc = "回单";
            } else if (load) {
                desc = "合同";
            }
//            if (isNeedLog == null || isNeedLog.length == 0 || isNeedLog[0]) {
                sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(),
                        SysOperLogConst.OperType.Audit, "[" + loginInfo.getName() + "]审核回单，审核"
                                + desc + "不通过"+(StringUtils.isNotBlank(verifyString)?"(审核意见:" + verifyString + ")":""));
//            }
            iOrderInfoService.saveOrUpdate(orderInfo);
        }
    }


    /**
     * 财务审核订单
     *
     * @param orderInfo
     * @throws Exception
     */
    @Override
    public void verify(OrderInfo orderInfo, OrderScheduler scheduler, OrderInfoExt orderInfoExt, String verifyString, Integer reciveType,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderInfo == null) {
            throw new BusinessException("找不到订单信息");
        }
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "verify" + orderInfo.getOrderId(), 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        if (StringUtils.isBlank(verifyString)) {
            verifyString = "无";
        }
//        OrderFee orderFee = orderFeeSV.getOrderFee(orderInfo.getOrderId());
        OrderFee orderFee = iOrderFeeService.getOrderFee(orderInfo.getOrderId());
//        OrderFeeExt orderFeeExt = orderFeeSV.getOrderFeeExt(orderInfo.getOrderId());
        OrderFeeExt orderFeeExt = iOrderFeeExtService.getOrderFeeExt(orderInfo.getOrderId());
        // 权限判断
        if (orderInfoExt.getPreAmountFlag() == null || orderInfoExt.getPreAmountFlag() != OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
            throw new BusinessException("没有支付预付款，不能回单审核!");
        }
        if((( scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || scheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )
                || (scheduler.getVehicleClass()==SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT
        ))){
            if (orderFee.getArrivePaymentFee() != null && orderFee.getArrivePaymentFee() > 0
                    && (orderFee.getArrivePaymentState() == null || orderFee.getArrivePaymentState() != OrderConsts.AMOUNT_FLAG.ALREADY_PAY)) {
                throw new BusinessException("没有支付到付款，不能回单审核!");
            }
        }
//        IOrderProblemInfoSV infoSV = (IOrderProblemInfoSV) SysContexts.getBean("orderProblemSV");
//        BaseUser baseUser = SysContexts.getCurrentOperator();
//        List<OrderProblemInfo> list = infoSV.getOrderProblemInfoByOrderId(orderInfo.getOrderId(),  baseUser.getTenantId());
        List<OrderProblemInfo> list=iOrderProblemInfoService.getOrderProblemInfoByOrderId(orderInfo.getOrderId(),loginInfo.getTenantId());

        if (list != null && list.size() > 0) {
            for (OrderProblemInfo problemInfo : list) {
                if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                    if (problemInfo.getState() != SysStaticDataEnum.EXPENSE_STATE.CANCEL) {
                        if (problemInfo.getState() == null
                                || (problemInfo.getState() != SysStaticDataEnum.EXPENSE_STATE.CANCEL
                                && problemInfo.getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.END
                                && problemInfo.getState()
                                .intValue() != SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN)) {
                            throw new BusinessException("还有异常未处理完毕，不能回单审核！");
                        }
                    }
                } else {
                    // 收入异常
                    if (problemInfo.getState() == null
                            || (problemInfo.getState() != SysStaticDataEnum.EXPENSE_STATE.CANCEL
                            && problemInfo.getState() != SysStaticDataEnum.EXPENSE_STATE.END && problemInfo
                            .getState().intValue() != SysStaticDataEnum.EXPENSE_STATE.TURN_DOWN)) {
                        throw new BusinessException("还有异常未处理完毕，不能回单审核！");
                    }
                }
            }
        }
        /** 时效罚款处理 **/
//        IOrderAgingInfoTF orderAgingInfoTF = (IOrderAgingInfoTF) SysContexts.getBean("orderAgingInfoTF");
//        List<OrderAgingInfo> agingInfos = orderAgingInfoTF.getOrderAgingInfo(orderInfo.getOrderId());
        QueryWrapper<OrderAgingInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderInfo.getOrderId())
                .eq("tenant_id",loginInfo.getTenantId());
        List<OrderAgingInfo> agingInfos = orderAgingInfoMapper.selectList(wrapper);
        List<OrderAgingInfo> agingInfosOut = new ArrayList<>();
        long agingPrice = 0;
        if (agingInfos != null && agingInfos.size() > 0) {
            for (OrderAgingInfo orderAgingInfo : agingInfos) {
                if (orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                        || orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.AUDIT) {
                    throw new BusinessException("还有时效罚款未处理完毕，不能回单审核！");
                } else if (orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
//                    IOrderAgingAppealInfoTF orderAgingAppealInfoTF = (IOrderAgingAppealInfoTF) SysContexts
//                            .getBean("orderAgingAppealInfoTF");
//                    OrderAgingAppealInfo appealInfo = orderAgingAppealInfoTF.getAppealInfoBYAgingId(orderAgingInfo.getId(),
//                            false);
                    OrderAgingAppealInfo appealInfo =  iOrderAgingAppealInfoService.getAppealInfoBYAgingId(orderAgingInfo.getId(),false);
                    if (appealInfo != null) {
                        if (appealInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING
                                || appealInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.AUDIT) {
                            throw new BusinessException("还有时效申诉未处理完毕，不能回单审核！");
                        }
                    }
                    agingPrice += (orderAgingInfo.getFinePrice() == null ? 0 : orderAgingInfo.getFinePrice())
                            - (orderAgingInfo.getDeductionFee() == null ? 0 : orderAgingInfo.getDeductionFee());
                    agingInfosOut.add(orderAgingInfo);
                }
            }
        }
//        IClaimExpenseInfoTF claimExpenseInfoTF = (IClaimExpenseInfoTF) SysContexts.getBean("claimExpenseInfoTF");
//        Long expenseSum = claimExpenseInfoTF.countWaitAduitExpense(orderInfo.getOrderId());
         //
        Long expenseSum = iClaimExpenseInfoService.countWaitAduitExpense(orderInfo.getOrderId());
        if (expenseSum != null && expenseSum > 0) {
            throw new BusinessException("还有报销信息未处理完毕，不能回单审核！");
        }
        String vehicleAffiliation = orderFee.getVehicleAffiliation();
        if(scheduler.getVehicleClass() != null && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1){
            vehicleAffiliation = "0";
        }
        if (scheduler.getVehicleClass() != null
                && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null) {
            if (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE) {
//                IOrderCostReportTF  orderCostReportTF = (IOrderCostReportTF) SysContexts.getBean("orderCostReportTF");
//                boolean isFinish =  orderCostReportTF.judgeOrderFeeIsExamineFinish(orderInfo.getOrderId());
                boolean isFinish = iOrderCostReportService.judgeOrderFeeIsExamineFinish(orderInfo.getOrderId(), accessToken);
                if (!isFinish) {
                    throw new BusinessException("还有上报费用未处理完毕，不能回单审核！");
                }
            }else if(orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST){
//                IOrderDriverSubsidySV orderDriverSubsidySV =  (IOrderDriverSubsidySV) SysContexts.getBean("orderDriverSubsidySV");
//                List<Map> orderDriverSubsidys =  orderDriverSubsidySV.findDriverNoPaySubsidys(orderInfo.getOrderId(),
//                        null, scheduler.getCarDriverId(), scheduler.getCopilotUserId(), orderInfo.getTenantId(), false);
                List<OrderDriverSubsidyDto> driverNoPaySubsidys = iOrderDriverSubsidyService.findDriverNoPaySubsidys(orderInfo.getOrderId(),
                        null, scheduler.getCarDriverId(), scheduler.getCopilotUserId(), orderInfo.getTenantId(), false);
                if (driverNoPaySubsidys != null && driverNoPaySubsidys.size() > 0) {
                    throw new BusinessException("还有切换司机补贴未支付，不能回单审核！");
                }
            }
        }
//        IPaymentTF payMentTF = (IPaymentTF) SysContexts.getBean("paymentTF");
        if ((orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES) || // 外发单
                (scheduler.getVehicleClass() != null
                        && scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                        && (orderInfoExt.getPaymentWay() != null
                        && orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT))
                || // 自有车承包订单
                (scheduler.getVehicleClass() != null
                        && ( scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                        || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                        || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                )
                ) // 内部单
            // C端社会司机
        ) {
            List<OrderProblemInfo> problemInfos = new ArrayList<>();
            long exceptionFee=0;
            for (OrderProblemInfo Info : list) {
                if (Info.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST
                        && Info.getState() == SysStaticDataEnum.EXPENSE_STATE.END && Info.getProblemDealPrice() < 0) {
                    problemInfos.add(Info);
                    exceptionFee += (Info.getProblemDealPrice() == null ? 0 : Info.getProblemDealPrice()) -
                            (Info.getDeductionFee() == null ? 0 : Info.getDeductionFee());
                }
            }
            Long userId = scheduler.getCarDriverId();
            if (orderInfo.getIsTransit() != null && orderInfo.getIsTransit() == OrderConsts.IsTransit.TRANSIT_YES
                    && orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {// 有归属租户外发订单
//                ITenantSV tenantSV = (ITenantSV) SysContexts.getBean("tenantSV");
//                SysTenantDef tenantDef = tenantSV.getSysTenantDef(orderInfo.getToTenantId(),true);
                SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(orderInfo.getToTenantId(), true);
                if (sysTenantDef == null) {
                    log.error("未找到车队信息! 车队ID["+orderInfo.getToTenantId()+"]");
                    throw new BusinessException("未找到车队信息!");
                }
                userId = sysTenantDef.getAdminUser();
            }else if(scheduler.getIsCollection() != null && scheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES){
                userId = scheduler.getCollectionUserId();
            }
            // 修改
            //账期字段变更
//            IOrderPaymentDaysInfoSV orderPaymentDaysInfoSV = (IOrderPaymentDaysInfoSV) SysContexts.getBean("orderPaymentDaysInfoSV");
//            IOrderPaymentDaysInfoTF orderPaymentDaysInfoTF = (IOrderPaymentDaysInfoTF) SysContexts.getBean("orderPaymentDaysInfoTF");
//            OrderPaymentDaysInfo costPaymentDaysInfo = orderPaymentDaysInfoSV.queryOrderPaymentDaysInfo(orderFee.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
            OrderPaymentDaysInfo orderPaymentDaysInfo = iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderFee.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
            //计算收款账期
//            Integer paymentDays = orderPaymentDaysInfoTF.calculatePaymentDays(orderPaymentDaysInfo,scheduler.getDependTime(),OrderConsts.CALCULATE_TYPE.COLLECTION);
            Integer paymentDays = iOrderPaymentDaysInfoService.calculatePaymentDays(orderPaymentDaysInfo, scheduler.getDependTime(), OrderConsts.CALCULATE_TYPE.COLLECTION);
//            PayForFinalChargeIn in = new PayForFinalChargeIn();
            PayForFinalChargeIn in = new PayForFinalChargeIn();
            in.setFinalFee(orderFee.getFinalFee() == null ? 0 : orderFee.getFinalFee());
            in.setInsuranceFee(orderFee.getInsuranceFee() == null ? 0 : orderFee.getInsuranceFee());
            in.setOrderId(orderInfo.getOrderId());
            in.setPaymentDay(paymentDays == null ? 0 : paymentDays);
            in.setTenantId(orderInfo.getTenantId());
            in.setProblemInfos(problemInfos);
            in.setAgingInfos(agingInfosOut);
            in.setUserId(userId);
            in.setVehicleAffiliation(vehicleAffiliation);
            // TODO 支付尾款
            iPayManagerService.payForFinalCharge(in,accessToken);

            /**
             * 生成抵押油卡流水 hp
             */
            //外调车才抵押油卡
            if (( scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            )) {

                /**
                 * 如果 油卡押金>尾款-异常款-时效罚款,取尾款-异常款-时效罚款，否则取油卡押金
                 * 油卡押金取自资金风控
                 */
//                IOrderOilCardInfoSV orderOilCardInfoSV = (IOrderOilCardInfoSV) SysContexts.getBean("orderOilCardInfoSV");
//                List<OrderOilCardInfo> orderOilCards=orderOilCardInfoSV.queryOrderOilCardInfoByOrderId(orderFee.getOrderId(), null);
                List<OrderOilCardInfo> orderOilCardInfos = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderFee.getOrderId(), null);
                if(orderOilCardInfos!=null&&orderOilCardInfos.size()>0) {
//                    IOperationOilTF operationOilTF = (IOperationOilTF) SysContexts.getBean("operationOilTF");
                    //判断该订单之前是否已经产生押金流水（主要是兼容之前是支付预付款就产生押金流水的情况）
                    if(!iOperationOilService.judgeOrderIsPledgeOilCard(orderFeeExt.getOrderId())) {
//                        IPayFeeLimitTF limtTF = (IPayFeeLimitTF)SysContexts.getBean("payFeeLimitTF");
                        //获取抵扣押金金额
//                        long oilCardDeposit=limtTF.getAmountLimitCfgVal(SysContexts.getCurrentOperator().getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OIL_CARD_DEPOSIT_303);
                        Long oilCardDeposit = iPayFeeLimitService.getAmountLimitCfgVal(loginInfo.getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OIL_CARD_DEPOSIT_303);
                        oilCardDeposit = oilCardDeposit < 0 ? 0 : oilCardDeposit;
                        Long fee=(orderFee.getFinalFee()!=null?orderFee.getFinalFee():0)-agingPrice+exceptionFee-(orderFee.getInsuranceFee() == null ? 0 : orderFee.getInsuranceFee());
                        Long pledgeFee=oilCardDeposit>=fee?fee:oilCardDeposit;
                        if(pledgeFee>0) {
//                            operationOilTF.pledgeOrReleaseOilCardAmount(userId, vehicleAffiliation, pledgeFee, orderFeeExt.getOrderId()
//                                    , orderFee.getTenantId(), OrderAccountConst.PLEDGE_RELEASE_TYPE.PLEDGE);
                            iOilCardManagementService.pledgeOrReleaseOilCardAmount(userId,vehicleAffiliation,pledgeFee,orderInfoExt.getOrderId(),orderFee.getTenantId(),OrderAccountConst.PLEDGE_RELEASE_TYPE.PLEDGE,loginInfo,accessToken);

                        }
                        //更新油卡抵押押金
                        orderFeeExt.setOilDepositActual(pledgeFee.intValue());
                        orderFeeExt.setOilDepositStandard(oilCardDeposit.intValue());
//                        orderFeeSV.update(orderFeeExt);
                        QueryWrapper<OrderFeeExt> feeQueryWrapper = new QueryWrapper<>();
                        feeQueryWrapper.eq("id",orderFeeExt.getId());
                        orderFeeExtMapper.update(orderFeeExt,feeQueryWrapper);
                    }

                    //判断是否已经释放油卡
//                    IOilCardManagementTF oilCardManagementTF = (IOilCardManagementTF) SysContexts.getBean("oilCardManagementTF");
//                    List<Map<String, Object>> oilCards=oilCardManagementTF.getOilCardByOrderId(orderFee.getOrderId());
                    List<Map<String, Object>> oilCards = iOilCardManagementService.getOilCardByOrderIds(orderFee.getOrderId(), accessToken);
                    boolean isReclaim=true;
                    if(oilCards!=null){
                        for (Map<String, Object> map : oilCards) {
                            boolean isReclaimTmp= DataFormat.getBooleanKey(map, "isReclaim");
                            if(!isReclaimTmp) {
//                                OilCardManagement oilCardManagementByCard = oilCardManagementTF.getOilCardManagement();
//                                oilCardManagementByCard.setPledgeFee(orderFeeExt.getOilDepositActual());
                                OilCardManagement oilCardManagementByCard = iOilCardManagementService.getOilCardManagement(DataFormat.getLongKey(map, "cardId"));
                                oilCardManagementByCard.setPledgeFee(Long.valueOf(orderFeeExt.getOilDepositActual()));
//                                orderFeeSV.saveOrUpdate(oilCardManagementByCard);
                                iOilCardManagementService.saveOrUpdate(oilCardManagementByCard);
                                isReclaim=false;
                            }
                        }
                    }

                    if(isReclaim&& orderFeeExt.getOilDepositActual() != null && orderFeeExt.getOilDepositActual()>0) {
//                        operationOilTF.pledgeOrReleaseOilCardAmount(userId, vehicleAffiliation, orderFeeExt.getOilDepositActual(), orderFee.getOrderId()
//                                , orderFeeExt.getTenantId(), OrderAccountConst.PLEDGE_RELEASE_TYPE.RELEASE);
                        // TODO 油卡抵押释放押金（新）
                        iOilCardManagementService.pledgeOrReleaseOilCardAmount(userId, vehicleAffiliation,  orderFeeExt.getOilDepositActual().longValue(), orderFee.getOrderId()
                                , orderFeeExt.getTenantId(), OrderAccountConst.PLEDGE_RELEASE_TYPE.RELEASE,loginInfo,accessToken);
                    }
                }
            }

        } else {
            //自有车时效罚款扣减
            if (agingPrice > 0) {
//                payMentTF.payForExceptionOut(scheduler.getCarDriverId(), vehicleAffiliation,
//                        -agingPrice, -1,
//                        scheduler.getTenantId(), scheduler.getOrderId());
                iOrderLimitService.payForExceptionOut(scheduler.getCarDriverId(), vehicleAffiliation,
                        -agingPrice, -1L,
                        scheduler.getTenantId(), scheduler.getOrderId(),loginInfo,accessToken);
            }
            // 自有车异常扣减
            for (OrderProblemInfo problemInfo : list) {
                // 成本异常
                if (problemInfo.getProblemCondition() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
                    // 已完成状态
                    if (problemInfo.getState() != null
                            && problemInfo.getState() == SysStaticDataEnum.EXPENSE_STATE.END) {
                        SysStaticData sysStaticData = readisUtil.getSysStaticData(
                                EnumConsts.SysStaticData.COST_PROBLEM_TYPE, problemInfo.getProblemType());

                        if (sysStaticData == null) {
                            log.error("异常类型非法，请联系客服！["+EnumConsts.SysStaticData.COST_PROBLEM_TYPE+"]");
                            throw new BusinessException("异常类型非法，请联系客服！");
                        }
                        EnumConsts.Exception_Deal_Type exceptionDealType = EnumConsts.Exception_Deal_Type
                                .getType(Integer.parseInt(sysStaticData.getCodeId() + ""));
                        if (exceptionDealType == null) {
                            log.error("异常金额非法，请联系客服！["+sysStaticData.getCodeId()+"]");
                            throw new BusinessException("异常金额非法，请联系客服！");
                        }
                        // 扣款项
                        if (EnumConsts.Exception_Deal_Type.REDUCEMONEY == exceptionDealType) {
//                            payMentTF.payForExceptionOut(scheduler.getCarDriverId(), vehicleAffiliation,
//                                    problemInfo.getProblemDealPrice(), problemInfo.getProblemId(),
//                                    problemInfo.getTenantId(), problemInfo.getOrderId());
                            iOrderLimitService.payForExceptionOut(scheduler.getCarDriverId(), vehicleAffiliation,
                                    problemInfo.getProblemDealPrice(), problemInfo.getId(),
                                    problemInfo.getTenantId(), problemInfo.getOrderId(),loginInfo,accessToken);

                        }
                    }
                }
            }
        }
        //同步轨迹
//        orderFeeTF.syncOrderTrackTo56K(orderInfo, orderFee);
        iOrderFeeService.syncOrderTrackTo56K(orderInfo,orderFee,accessToken);
        //开票订单同步回单信息
//        orderFeeTF.syncBillForm(orderInfo, orderFee,OrderConsts.SYNC_TYPE.RECIVE);
        iOrderFeeService.syncBillForm(orderInfo,orderFee,OrderConsts.SYNC_TYPE.RECIVE,accessToken);
        // 订单移历史
        orderInfo.setOrderState(OrderConsts.ORDER_STATE.FINISH);
        orderFee.setFinalFeeFlag(OrderConsts.AMOUNT_FLAG.ALREADY_PAY);
//        IOrderFeeTF feeTF = (IOrderFeeTF) SysContexts.getBean("orderFeeTF");
        // 同步支付中心
//        feeTF.synPayCenterUpdateOrderOrProblemInfo(orderInfo, scheduler);
        iOrderFeeService.synPayCenterUpdateOrderOrProblemInfo(orderInfo,scheduler);
//        IOrderPaymentDaysInfoSV orderPaymentDaysInfoSV = (IOrderPaymentDaysInfoSV) SysContexts.getBean("orderPaymentDaysInfoSV");
//        OrderPaymentDaysInfo costPaymentDaysInfo = orderPaymentDaysInfoSV.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
        OrderPaymentDaysInfo costPaymentDaysInfo=iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(),OrderConsts.PAYMENT_DAYS_TYPE.COST);
        if (costPaymentDaysInfo == null) {
            throw new BusinessException("成本账期信息有误，请联系客服！");
        }
//        OrderPaymentDaysInfo incomePaymentDaysInfo = orderPaymentDaysInfoSV.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        OrderPaymentDaysInfo incomePaymentDaysInfo =iOrderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        if (incomePaymentDaysInfo == null) {
            throw new BusinessException("收入账期信息有误，请联系客服！");
        }
        //更新订单应收应付时间
//        orderInfoTF.updateSettleDueDate(scheduler, orderInfo,new Date());


        //  TODO 待释放
        iOrderInfoService.updateSettleDueDate(scheduler, orderInfo, DateUtil.asLocalDateTime(new Date()), loginInfo);
        //更新接单应收时间
        if (orderInfo.getToOrderId() != null && orderInfo.getToOrderId() > 0) {
//            IOrderFeeStatementTF orderFeeStatementTF = (IOrderFeeStatementTF) SysContexts.getBean("orderFeeStatementTF");
//            IOrderPaymentDaysInfoTF orderPaymentDaysInfoTF = (IOrderPaymentDaysInfoTF) SysContexts.getBean("orderPaymentDaysInfoTF");
            Date shouldPayDate = new Date();
//            Integer collectionTime = orderPaymentDaysInfoTF.calculatePaymentDays(costPaymentDaysInfo, scheduler.getDependTime(), OrderConsts.CALCULATE_TYPE.COLLECTION);
            Integer collectionTime =iOrderPaymentDaysInfoService.calculatePaymentDays(costPaymentDaysInfo, scheduler.getDependTime(), OrderConsts.CALCULATE_TYPE.COLLECTION);
            if (collectionTime != null) {
                shouldPayDate = DateUtil.addDate(shouldPayDate, collectionTime);
            }
            //反写订单应收时间 开单方应付时间 == 接单方应收时间
//          orderFeeStatementTF.updateReceiveSettleDueDate(orderInfo.getToOrderId(), shouldPayDate);
            iOrderFeeStatementService.updatePaySettleDueDate(orderInfo.getToOrderId(), DateUtil.asLocalDateTime(shouldPayDate));
        }
//        OrderInfoH orderInfoH = orderInfoTF.getHisOrderInfo(orderInfo, orderInfoExt, orderFee, orderFeeExt, scheduler,costPaymentDaysInfo,
//                incomePaymentDaysInfo);
        OrderInfoH orderInfoH =iOrderInfoHService.getHisOrderInfo(orderInfo, orderInfoExt, orderFee, orderFeeExt, scheduler,costPaymentDaysInfo,
                incomePaymentDaysInfo,accessToken);
//        IOrderGoodsSV goodsSV = (IOrderGoodsSV) SysContexts.getBean("orderGoodsSV");
//        OrderGoodsH goods = goodsSV.getOrderGoodsH(orderInfoH.getOrderId());
        OrderGoodsH goods = iOrderGoodsHService.getOneGoodInfoH(orderInfoH.getOrderId());
        goods.setReceiptsUserName(loginInfo.getName());
        goods.setReceiptsUserId(loginInfo.getId());
//        goodsSV.saveOrUpdate(goods);
        iOrderGoodsHService.saveOrUpdate(goods);

        //同步应收账单表
        try {
            //新增订单应收业务逻辑
            OverdueReceivable overdueReceivable = new OverdueReceivable();
            overdueReceivable.setOrderId(orderFee.getOrderId());
            overdueReceivable.setDependTime(scheduler.getCarDependDate());
            overdueReceivable.setSourceRegion(orderInfo.getSourceRegion());
            overdueReceivable.setType(1);
            overdueReceivable.setDesRegion(orderInfo.getDesRegion());
            overdueReceivable.setName(goods.getCompanyName());
            overdueReceivable.setPaid(0L);
            overdueReceivable.setTxnAmt(orderFee.getCostPrice() + orderFee.getIncomeExceptionFee());
            overdueReceivable.setPayConfirm(0);
            overdueReceivable.setBusinessNumber(String.valueOf(orderFee.getOrderId()));
            overdueReceivable.setTenantId(loginInfo.getTenantId());
            if(orderInfoH.getFromTenantId() != null && orderInfoH.getFromTenantId()>-1){
               SysTenantDef sysTenantDef = sysTenantDefService.getById(orderInfoH.getFromTenantId());
               if(sysTenantDef.getAdminUser() != null) {
                   overdueReceivable.setAdminUserId(sysTenantDef.getAdminUser());
               }
            }
            overdueReceivableService.save(overdueReceivable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 批量审核
     * 聂杰伟
     *
     * @param orderIdsStr 订单号集合
     * @param load
     * @param receipt
     * @param verifyDesc
     * @param accessToken
     * @return
     */
    @Override
    public List<OrderReceiptDto> batchAuditOrder(List<Long> orderIdsStr, Boolean load, Boolean receipt, String verifyDesc, String accessToken) {
        Integer successNum = 0;//成功条数
        StringBuffer failResult = new StringBuffer();//失败原因
        List<OrderReceiptDto> dtoList = new ArrayList<>();//返回值
        OrderReceiptDto orderReceiptDto = new OrderReceiptDto();
        for (Long orderId : orderIdsStr) {
            if (orderId == null || orderId <= 0) {
                continue;
            }
            try {
                if (!load && !receipt) {
                    iOrderFeeService.reciveVerifyPayPass(orderId, verifyDesc, null, OrderConsts.RECIVE_TYPE.BATCH, accessToken);
                } else {
                    iOrderFeeService.reciveVerifyPayFail(orderId, verifyDesc, load, receipt, accessToken);
                }
//                this.verifyRece(orderId.toString(), false, false, "", null,OrderConsts.RECIVE_TYPE.BATCH,accessToken);

                successNum++;
            } catch (BusinessException e) {
                failResult.append("订单" + orderId + e.getMessage());
            } catch (Exception e) {
                failResult.append("订单" + orderId + "回单审核失败，请联系客服");
                log.error("订单" + orderId + "回单审核失败!", e);
            }
        }
        orderReceiptDto.setFailResult(failResult.toString());
        orderReceiptDto.setSuccessNum(successNum);
        orderReceiptDto.setFailNum(orderIdsStr.size() - successNum);
        dtoList.add(orderReceiptDto);
        return dtoList;
    }

    @Override
    public List<OrderReceipt> orderReceipt(Long order_id, String receiptsNumber, String accessToken) {
        QueryWrapper<OrderReceipt> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", order_id)
                .like("recive_number", receiptsNumber);
        List<OrderReceipt> orderReceipts = baseMapper.selectList(wrapper);
        return orderReceipts;
    }

    @Override
    public void removeRecive(Long orderId, String flowId) {
        if(orderId==null||orderId<=0) {
            throw new BusinessException("缺少订单号！");
        }
        if(StringUtils.isBlank(flowId)) {
            throw new BusinessException("缺少回单图片！");
        }

        // 删除订单回单记录
        baseMapper.deleteOrderRecipt(orderId, flowId);

        LambdaQueryWrapper<OrderReceipt> queryWrapper = new LambdaQueryWrapper<>();
        if (orderId != null && orderId > 0) {
            queryWrapper.eq(OrderReceipt::getOrderId, orderId);
        }

        List<OrderReceipt> preOrderReceipts = baseMapper.selectList(queryWrapper);
        if(preOrderReceipts==null||preOrderReceipts.size()==0) { // 该订单下没有回单记录
            Set<OrderGoods> set = iOrderGoodsService.getOrderGoodsSet(orderId);
            if(set!=null&&set.size()>0) {
                Iterator<OrderGoods> it = (Iterator<OrderGoods>) set.iterator();
                while (it.hasNext()) {
                    OrderGoods goodsInfo = (OrderGoods) it.next();
                    goodsInfo.setReciveState(OrderConsts.ReciveState.NOT_UPLOAD);
                    // 修改订单商品表  商品回单状态(未回单)
                    iOrderGoodsService.update(goodsInfo);
                }
            }
        }
    }


    /**
     * 保存上传回单
     *
     * @param orderId
     * @param flowId
     * @param flowUrl
     * @param receiveNumber
     * @throws Exception
     */
    private Long uploadRecive(Long receiptId, Long orderId, String flowId, String flowUrl, String receiveNumber) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("缺少订单号！");
        }
        OrderReceipt orderReceipt = null;
        if (receiptId != null && receiptId > 0) {
            QueryWrapper<OrderReceipt> wrapper = new QueryWrapper<>();
            wrapper.eq("id", receiptId);
            orderReceipt = baseMapper.selectOne(wrapper);

        }

        if (orderReceipt == null) {
//            List<OrderReceipt> preOrderReceipts=orderReciptSV.findOrderRecipts(orderId, flowId);
            List<OrderReceipt> preOrderReceipts = this.findOrderReceipts(orderId, null, flowId);
            if (preOrderReceipts != null && preOrderReceipts.size() > 0) {
                orderReceipt = preOrderReceipts.get(0);
            } else {
                List<OrderReceipt> list = this.findOrderReceipts(orderId, null, flowId);
                if (list != null && list.size() >= 20) {
                    throw new BusinessException("最多允许上传20张回单！");
                }
                orderReceipt = new OrderReceipt();
                orderReceipt.setOrderId(orderId);
                orderReceipt.setFlowId(flowId);
            }
        }
        orderReceipt.setFlowId(flowId);
        orderReceipt.setReciveNumber(receiveNumber);
        orderReceipt.setFlowUrl(flowUrl);
//        baseMapper.updateById(orderReceipt);
        this.saveOrUpdate(orderReceipt);
        return orderReceipt.getId();
    }

    @Override
    public Page<OrderListAppOutDto> queryCooperationOrderList(Long vehicleCode, Long tenantId,Integer pageNum,Integer pageSize,  String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        Long userId=null;
        if (loginInfo != null) {
            userId = loginInfo.getUserInfoId();
        }
        if (vehicleCode <= 0) {
            throw new BusinessException("未找到车辆信息，请联系客服！");
        }
        if (tenantId <= 0) {
            throw new BusinessException("未找到车队信息，请联系客服！");
        }

        List<OrderListAppOutDto> listOut = new ArrayList<OrderListAppOutDto>();
        Page<OrderListAppOutDto> orderListAppOutDtoPage = orderReceiptMapper.queryCooperationOrderList(new Page<>(pageNum, pageSize), vehicleCode, userId, tenantId);
        List<OrderListAppOutDto> list = orderListAppOutDtoPage.getRecords();
        for (OrderListAppOutDto dto : list) {
            OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(dto.getOrderId());
            dto.setOrderLine(orderInfoDto.getOrderLine());
            dto.setIsTransitLine(orderInfoDto.getIsTransitLine());
            List<OrderTransitLineInfo> transitLineInfos = null;
            if (dto.getIsHis() != null && dto.getIsHis().intValue() == 1) {
                List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfohService.queryOrderTransitLineInfoHByOrderId(dto.getOrderId());
                if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                    transitLineInfos = new ArrayList<>();
                    for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                        OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                        BeanUtils.copyProperties(orderTransitLineInfoH,transitLineInfo);
                        transitLineInfos.add(transitLineInfo);
                    }
                }
            }else{
                transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(dto.getOrderId());
            }
            dto.setTransitLineInfos(transitLineInfos);
            Integer expenseNum = 0;
            Integer loadNum = 0;
            SysTenantDef tenantDef = sysTenantDefService.getSysTenantDef(dto.getTenantId(),true);
            if (tenantDef != null) {
                dto.setLogoUrl(tenantDef.getLogo());
            }
            dto.setLoadNum(loadNum);
            dto.setExpenseNum(expenseNum);
            dto.setOrderStateName(dto.getOrderState() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ORDER_STATE", dto.getOrderState() + "").getCodeName());
            dto.setDesRegionName(dto.getDesRegion() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", dto.getDesRegion() + "").getCodeName());
            dto.setReciveTypeName(dto.getReciveType()==null?""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("RECIVE_TYPE",dto.getReciveType()+"").getCodeName());
            dto.setSourceRegionName(dto.getSourceRegion() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("SYS_CITY", dto.getSourceRegion() + "").getCodeName());
            dto.setPaymentWayName(dto.getPaymentWay() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("PAYMENT_WAY", dto.getPaymentWay() + "").getCodeName());
            dto.setArrivePaymentStateName(dto.getArrivePaymentState() == null ? ""
                    : sysStaticDataRedisUtils.getSysStaticDataByCodeValue("AMOUNT_FLAG", dto.getArrivePaymentState()+"").getCodeName());


            if (dto.getPreTotalFee() == null) {
                dto.setPreTotalFee(0L);
            }
            if (dto.getFinalFee() == null) {
                dto.setFinalFee(0L);
            }
            if (dto.getTotalFee() == null) {
                dto.setTotalFee(0L);
            }
            if (dto.getPreOilVirtualFee() == null) {
                dto.setPreOilVirtualFee(0L);
            }
            if (dto.getPreOilFee() == null) {
                dto.setPreOilFee(0L);
            }
            if (dto.getSalary() == null) {
                dto.setSalary(0L);
            }
            if (dto.getCopilotSalary() == null) {
                dto.setCopilotSalary(0L);
            }
            dto.setSalarySum(dto.getCopilotSalary() + dto.getSalary() + dto.getDriverSwitchSubsidy());
            listOut.add(dto);
        }
        orderListAppOutDtoPage.setRecords(listOut);
        return orderListAppOutDtoPage;
    }

    @Override
    public List<QueryDriverOrderDto> queryDriverOrderPlateNumber(Long userId) {
        List<QueryDriverOrderDto> listOut=new ArrayList<>();
        List<QueryDriverOrderDto> queryDriverOrderDto = orderReceiptMapper.queryDriverOrderPlateNumber(userId);
        List<QueryDriverOrderDto> queryDriverOrderDto1 = orderReceiptMapper.queryDriverOrderPlateNumberState(userId);
        if (queryDriverOrderDto!=null){
            listOut.addAll(queryDriverOrderDto);
        }
        if (queryDriverOrderDto1!=null) {
            listOut.addAll(queryDriverOrderDto1);
        }
        Set<QueryDriverOrderDto> set = new HashSet<>();
        set.addAll(listOut);
        listOut = new ArrayList<>(set);
        Map<String, Boolean> properties = new HashMap<>();
        properties.put("dependTime", true);
        CommonUtil.sort(listOut, properties);
        return listOut;
    }


}
