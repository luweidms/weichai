package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.finance.constant.PayOutIntfUtil;
import com.youming.youche.order.api.order.IEtcMaintainService;
import com.youming.youche.order.api.order.IOrderCostOtherReportService;
import com.youming.youche.order.api.order.IOrderCostReportService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderMainReportService;
import com.youming.youche.order.api.order.IOrderOilCardInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.EtcMaintain;
import com.youming.youche.order.domain.order.OrderCostDetailReport;
import com.youming.youche.order.domain.order.OrderCostOtherReport;
import com.youming.youche.order.domain.order.OrderCostReport;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderMainReport;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.dto.order.OrderCostDetailReportDto;
import com.youming.youche.order.dto.order.OrderCostReportCardDto;
import com.youming.youche.order.dto.order.OrderCostReportDto;
import com.youming.youche.order.provider.mapper.order.OrderCostReportMapper;
import com.youming.youche.order.provider.mapper.order.OrderMainReportMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.util.BeanUtils;
import com.youming.youche.order.vo.OrderCostOtherReportVO;
import com.youming.youche.order.vo.OrderCostOtherTypeVO;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysExpenseService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysExpense;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.order.commons.OrderConsts.ORDER_COST_REPORT.AUDIT_PASS;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OrderCostReportServiceImpl extends BaseServiceImpl<OrderCostReportMapper, OrderCostReport> implements IOrderCostReportService {

    @Resource
    private OrderCostReportMapper orderCostReportMapper;
    @Resource
    LoginUtils loginUtils;
    @Resource
    ReadisUtil redisUtils;
    @Resource
    private OrderMainReportMapper orderMainReportMapper;
    @Resource
    IOrderSchedulerService iOrderSchedulerService;
    @Lazy
    @Resource
    IOrderInfoService iOrderInfoService;
    @DubboReference(version = "1.0.0")
    ISysAttachService iSysAttachService;
    @Resource
    IOrderOilCardInfoService iOrderOilCardInfoService;
    @Resource
    IEtcMaintainService iEtcMaintainService;
    @DubboReference(version = "1.0.0")
    ISysExpenseService sysExpenseService;
    @Resource
    IOrderMainReportService iOrderMainReportService;
    @Resource
    IOrderCostOtherReportService iOrderCostOtherReportService;
    @DubboReference(version = "1.0.0")
    IAuditService auditService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService iSysOperLogService;
    @Resource
    IOrderSchedulerHService orderSchedulerHService;

    @Resource
    IOrderCostReportService OrderCostReportService;


    @Override
    public List<OrderCostReport> getOrderCostReportByOrderId(Long orderId) {
        QueryWrapper<OrderCostReport> orderCostReportQueryWrapper = new QueryWrapper<>();
        orderCostReportQueryWrapper.eq("order_id", orderId);
        List<OrderCostReport> orderCostReports = orderCostReportMapper.selectList(orderCostReportQueryWrapper);
        return orderCostReports;
    }

    /***
     * 查询订单无上报或者上报已提交或者保存未提交标识
     * state 0未提交 3审核中 4审核不通过 5审核通过
     * OrderConsts.ORDER_COST_REPORT
     * @param orders
     * @return orderId , state
     */
    @Override
    public List<OrderMainReport> getCostReportStateByOrderIds(List<Long> orders) {
        QueryWrapper<OrderMainReport> wrapper = new QueryWrapper<>();
        List<OrderMainReport> orderMainReportList = new ArrayList<>();
        wrapper.in("order_id", orders);
        orderMainReportList = orderMainReportMapper.selectList(wrapper);
        return orderMainReportList;
    }

    @Override
    public OrderCostReportCardDto queryAll(String accessToken, Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("请输入订单号");
        }

        OrderCostReportCardDto out = new OrderCostReportCardDto();
        //获取预付款时订单输入的油卡和车辆绑定的油卡
        List<String> oilCardList = iOrderOilCardInfoService.queryOrdercConsumeCard(orderId, accessToken);
        //获取订单车辆上的etc卡号
        String etcCardNum = iOrderSchedulerService.queryCarEtcCardInfo(orderId, accessToken);
        out.setOilCardNum(oilCardList);
        out.setEtcCardNum(etcCardNum);
        OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
        if (null == orderScheduler) {
            OrderSchedulerH orderSchedulerH = iOrderSchedulerService.getOrderSchedulerH(orderId);
            if (null != orderSchedulerH) {
                orderScheduler = new OrderScheduler();
                BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            }
        }
        List<EtcMaintain> etcMaintainList = iEtcMaintainService.queryEtcMaintainByVehicleCode(orderScheduler.getVehicleCode(), null, null);
        List etcCardNumList = new ArrayList();
        for (int i = 0; i < etcMaintainList.size(); i++) {
            EtcMaintain etc = etcMaintainList.get(i);
            etcCardNumList.add(etc.getEtcId());
        }
        out.setEtcCardNumList(etcCardNumList);

        return out;
    }

    @Override
    public List<OrderCostOtherTypeVO> getOrderCostOtherTypes(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OrderCostOtherTypeVO> list = OrderCostReportService.getOrderCostOtherTypeList(-1L, null);
        list.addAll(OrderCostReportService.getOrderCostOtherTypeList(loginInfo.getTenantId(), null));
        return list;
    }

    @Override
    public List<OrderCostOtherTypeVO> getOrderCostOtherTypeList(long tenantId, String typeName) {
        return baseMapper.OrderCostOtherTypeList(tenantId, typeName);

    }

    @Override
    public OrderCostReportDto getOrderCostDetailReportByOrderId(Long orderId, String accessToken) {
        if (orderId < 0) {
            throw new BusinessException("订单号错误！");
        }
        //OrderCostDetailReportDto orderCostDetailReportDto = new OrderCostDetailReportDto();
        OrderCostReportDto orderCostDetailReportDto = new OrderCostReportDto();
        //查询司机
        OrderScheduler orderScheduler = iOrderSchedulerService.getOrderSchedulerByOrderId(orderId);
        if (orderScheduler != null && orderScheduler.getId() != null) {
            if (StrUtil.isEmpty(orderScheduler.getCarDriverPhone()) || StrUtil.isEmpty(orderScheduler.getCarDriverMan()) || StrUtil.isEmpty(orderScheduler.getPlateNumber())) {
                //  throw new BusinessException("订单信息错误！");
            } else {
                orderCostDetailReportDto.setDriverPhone(orderScheduler.getCarDriverPhone());
                orderCostDetailReportDto.setCarDriverMan(orderScheduler.getCarDriverMan());
                orderCostDetailReportDto.setPlateNumber(orderScheduler.getPlateNumber());
            }
        }else{
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            orderCostDetailReportDto.setDriverPhone(orderSchedulerH.getCarDriverPhone());
            orderCostDetailReportDto.setCarDriverMan(orderSchedulerH.getCarDriverMan());
            orderCostDetailReportDto.setPlateNumber(orderSchedulerH.getPlateNumber());
        }

        OrderInfo orderInfo = iOrderInfoService.getOrder(orderId);
        if (null == orderInfo) {
            OrderInfoH oh = iOrderInfoService.getOrderH(orderId);
            orderInfo = new OrderInfo();
            BeanUtil.copyProperties(oh, orderInfo);
        }
        Integer sourceRegion = orderInfo.getSourceRegion();
        Integer desRegion = orderInfo.getDesRegion();
        if (sourceRegion != null) {
            orderCostDetailReportDto.setSourceRegionName((redisUtils.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_CITY, sourceRegion + "")).getCodeName());
        }
        if (desRegion != null) {
            orderCostDetailReportDto.setDesRegionName((redisUtils.getSysStaticData(EnumConsts.SysStaticDataAL.SYS_CITY, desRegion + "")).getCodeName());
        }
        orderCostDetailReportDto.setOrderStateName((redisUtils.getSysStaticData("ORDER_STATE", orderInfo.getOrderState() + "")).getCodeName());
        OrderMainReport orderMainReport = iOrderMainReportService.getObjectByOrderId(orderId);
        if (null == orderMainReport) {
            orderCostDetailReportDto.setIsDoSave(isDoSave(orderInfo));
            return orderCostDetailReportDto;
        }
        OrderCostReportDto orderCostDetailReportDtos = getOrderCostDetailReport(orderMainReport);
        orderCostDetailReportDtos.setIsDoSave(isDoSave(orderInfo));
        BeanUtil.copyProperties(orderCostDetailReportDtos, orderCostDetailReportDto, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        return orderCostDetailReportDto;
    }

    private OrderCostReportDto getOrderCostDetailReport(OrderMainReport orderMainReport) {
        List<OrderCostReportDto> resultList = OrderCostReportService.getListById(orderMainReport.getOrderId());
        if (null == resultList || resultList.isEmpty()) {
            return null;
        }
        OrderCostReportDto OrderCostReport = resultList.get(0);
        //主表id
        OrderCostReport.setId(orderMainReport.getId());
        OrderCostReport.setIsAuditPass(orderMainReport.getIsAuditPass());
        if (OrderCostReport.getStartKm() > 0) {
            OrderCostReport.setStartKmStr(String.valueOf(CommonUtil.divide(OrderCostReport.getStartKm(), 100)));
        }
        if (OrderCostReport.getLoadingKm() > 0) {
            OrderCostReport.setLoadingKmStr(String.valueOf(CommonUtil.divide(OrderCostReport.getLoadingKm(), 100)));
        }
        if (OrderCostReport.getUnloadingKm() > 0) {
            OrderCostReport.setUnloadingKmStr((String.valueOf(CommonUtil.divide(OrderCostReport.getUnloadingKm(), 100))));
        }

        if (OrderCostReport.getEndKm() > 0) {
            OrderCostReport.setEndKmStr((String.valueOf(CommonUtil.divide(OrderCostReport.getEndKm(), 100))));
        }

        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
            if (StringUtils.isNotBlank(OrderCostReport.getStartKmUrl())) {
                OrderCostReport.setStartKmUrlPath(client.getHttpURL(OrderCostReport.getStartKmUrl()).split("\\?")[0]);
            } else if (OrderCostReport.getStartKmFile()!=null && OrderCostReport.getStartKmFile() > 0) {
                SysAttach sysAttach = iSysAttachService.getById(OrderCostReport.getStartKmFile());
                if (null != sysAttach) {
                    OrderCostReport.setStartKmUrlPath(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
                }
            }
            if (StringUtils.isNotBlank(OrderCostReport.getLoadingKmUrl())) {
                OrderCostReport.setLoadingKmUrlPath(client.getHttpURL(OrderCostReport.getLoadingKmUrl()).split("\\?")[0]);
            } else if (OrderCostReport.getLoadingKmFile()!=null && OrderCostReport.getLoadingKmFile() > 0) {
                SysAttach sysAttach = iSysAttachService.getById(OrderCostReport.getLoadingKmFile());
                if (null != sysAttach) {
                    OrderCostReport.setLoadingKmUrlPath(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
                }
            }
            if (StringUtils.isNotBlank(OrderCostReport.getUnloadingKmUrl())) {
                OrderCostReport.setUnloadingKmUrlPath(client.getHttpURL(OrderCostReport.getUnloadingKmUrl()).split("\\?")[0]);
            } else if (OrderCostReport.getUnloadingKmFile()!=null && OrderCostReport.getUnloadingKmFile() > 0) {
                SysAttach sysAttach = iSysAttachService.getById(OrderCostReport.getUnloadingKmFile());
                if (null != sysAttach) {
                    OrderCostReport.setUnloadingKmUrlPath(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
                }
            }
            if (StringUtils.isNotBlank(OrderCostReport.getEndKmUrl())) {
                OrderCostReport.setEndKmUrlPath(client.getHttpURL(OrderCostReport.getEndKmUrl()).split("\\?")[0]);
            } else if (OrderCostReport.getEndKmFile()!=null && OrderCostReport.getEndKmFile() > 0) {
                SysAttach sysAttach = iSysAttachService.getById(OrderCostReport.getEndKmFile());
                if (null != sysAttach) {
                    OrderCostReport.setEndKmUrlPath(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
                }
            }
            int state = orderMainReport.getState();
            OrderCostReport.setStateName(redisUtils.getSysStaticData("ORDER_COST_REPORT_STATE", state + "").getCodeName());
            OrderCostReport.setState(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (OrderCostReport.getIsFullOil()!=null && OrderCostReport.getIsFullOil() >= 0) {

            OrderCostReport.setIsFullOilName(redisUtils.getSysStaticData("IS_FULL_OIL", OrderCostReport.getIsFullOil() + "").getCodeName());
        } else {
            OrderCostReport.setIsFullOilName("未选择");
        }
//        List<OrderCostDetailReport> orderCostDetailReportList = iOrderCostReportService.getOrderCostDetailReport(orderMainReport.getId(), false);
//        List<Map> oilCostDataList = new ArrayList<>();
//        List<Map> etcCostDataList = new ArrayList<>();
//
//        //油总费用
//        long oilTotalAmount = 0;
//        //etc总费用
//        long etcTotalAmount = 0;
//        if (null != orderCostDetailReportList) {
//            for (int i = 0; i < orderCostDetailReportList.size(); i++) {
//                OrderCostDetailReport orderCostDetailReport = orderCostDetailReportList.get(i);
//                Map orderCostDetailReportMap = null;
//                try {
//                    orderCostDetailReportMap = BeanUtils.convertBean2Map(orderCostDetailReport);
//                } catch (IntrospectionException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//                long amount = DataFormat.getLongKey(orderCostDetailReportMap, "amount");
//                if (amount > 0) {
//                    orderCostDetailReportMap.put("amount", CommonUtil.divide(amount));
//                }
//                int paymentWay = DataFormat.getIntKey(orderCostDetailReportMap, "paymentWay");
//                orderCostDetailReportMap.put("paymentWayName", redisUtils.getSysStaticData("REPORT_PAYMENT_WAY", paymentWay + ""));
//
//                int stateTmp = DataFormat.getIntKey(orderCostDetailReportMap, "state");
//                orderCostDetailReportMap.put("stateName", redisUtils.getSysStaticData("ORDER_COST_REPORT_STATE", stateTmp + ""));
//
//                if (null != orderCostDetailReport.getTableType() && orderCostDetailReport.getTableType() == 1) {
//                    oilCostDataList.add(orderCostDetailReportMap);
//                    if (amount > 0) {
//                        oilTotalAmount += amount;
//                    }
//                    orderCostDetailReportMap.put("oilMileage", orderCostDetailReport.getOilMileage() != null ? CommonUtil.divide(orderCostDetailReport.getOilMileage()) : null);
//                } else if (null != orderCostDetailReport.getTableType() && orderCostDetailReport.getTableType() == 2) {
//                    etcCostDataList.add(orderCostDetailReportMap);
//                    if (amount > 0) {
//                        etcTotalAmount += amount;
//                    }
//                }
//
//
//                try {
//                    String fileUrl = DataFormat.getStringKey(orderCostDetailReportMap, "fileUrl");
//                    if (StringUtils.isNotBlank(fileUrl)) {
//
//                        orderCostDetailReportMap.put("fileUrlPath", client.getHttpURL(fileUrl).split("\\?")[0]);
//                    } else if (null != orderCostDetailReport.getFileId()) {
//                        SysAttach sysAttach = iSysAttachService.getById(orderCostDetailReport.getFileId());
//                        if (null != sysAttach) {
//                            orderCostDetailReportMap.put("fileUrlPath", client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
//                        }
//                    }
//                    String fileUrl1 = DataFormat.getStringKey(orderCostDetailReportMap, "fileUrl1");
//                    if (StringUtils.isNotBlank(fileUrl1)) {
//                        orderCostDetailReportMap.put("fileUrl1Path", client.getHttpURL(fileUrl1).split("\\?")[0]);
//                    } else if (null != orderCostDetailReport.getFileId1()) {
//                        SysAttach sysAttach = iSysAttachService.getById(orderCostDetailReport.getFileId1());
//                        if (null != sysAttach) {
//                            orderCostDetailReportMap.put("fileUrl1Path", client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
//                        }
//                    }
//
//                    String fileUrl2 = DataFormat.getStringKey(orderCostDetailReportMap, "fileUrl2");
//                    if (StringUtils.isNotBlank(fileUrl2)) {
//                        orderCostDetailReportMap.put("fileUrl2Path", client.getHttpURL(fileUrl2).split("\\?")[0]);
//                    } else if (null != orderCostDetailReport.getFileId2()) {
//                        SysAttach sysAttach = iSysAttachService.getById(orderCostDetailReport.getFileId2());
//                        if (null != sysAttach) {
//                            orderCostDetailReportMap.put("fileUrl2Path", client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        //查询其他费用
        long otherTotalAmount = 0;
        List<OrderCostOtherReportVO> orderCostOtherReportVOList = OrderCostReportService.getOrderCostOtherReport(orderMainReport.getId(), false);
        List<OrderCostOtherReport> rtnOrderCostOtherReportVOList = new ArrayList<OrderCostOtherReport>();
        if (orderCostOtherReportVOList != null) {
            String fileUrl1 = null;
            String fileUrl2 = null;
            String fileUrl3 = null;
            String fileUrl4 = null;
            String fileUrl5 = null;
            for (OrderCostOtherReportVO orderCostOtherReportVO : orderCostOtherReportVOList) {
                OrderCostOtherReport orderCostOtherReportMap = new OrderCostOtherReport();
                //orderCostOtherReportMap = BeanUtils.convertBean2Map(orderCostOtherReportVO);
                BeanUtil.copyProperties(orderCostOtherReportVO, orderCostOtherReportMap);
                if (orderCostOtherReportMap.getOilMileage() != null && orderCostOtherReportMap.getOilMileage() > 0L) {
                    orderCostOtherReportMap.setOilMileageStr(String.valueOf(CommonUtil.divide(orderCostOtherReportMap.getOilMileage(), 1000)));
                }
                fileUrl1 = orderCostOtherReportVO.getFileUrl1();
                fileUrl2 = orderCostOtherReportVO.getFileUrl2();
                fileUrl3 = orderCostOtherReportVO.getFileUrl3();
                fileUrl4 = orderCostOtherReportVO.getFileUrl4();
                fileUrl5 = orderCostOtherReportVO.getFileUrl5();
                if (StringUtils.isNotBlank(fileUrl1)) {
                    orderCostOtherReportMap.setFileUrl1(fileUrl1);
                }
                if (StringUtils.isNotBlank(fileUrl2)) {
                    orderCostOtherReportMap.setFileUrl2(fileUrl2);
                }
                if (StringUtils.isNotBlank(fileUrl3)) {
                    orderCostOtherReportMap.setFileUrl3(fileUrl3);
                }
                if (StringUtils.isNotBlank(fileUrl4)) {
                    orderCostOtherReportMap.setFileUrl4(fileUrl4);
                }
                if (StringUtils.isNotBlank(fileUrl5)) {
                    orderCostOtherReportMap.setFileUrl5(fileUrl5);
                }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                orderCostOtherReportMap.setConsumeFeeStr(orderCostOtherReportVO.getConsumeFee() != null ? String.valueOf(CommonUtil.divide(orderCostOtherReportVO.getConsumeFee(), 100)) : null);
                //    orderCostOtherReportMap.put("consumeNum", CommonUtil.divide(orderCostOtherReportVO.getConsumeNum()));
                //     orderCostOtherReportMap.put("priceUnit", orderCostOtherReportVO.getPriceUnit() != null ? CommonUtil.divide(orderCostOtherReportVO.getPriceUnit()) : null);
                orderCostOtherReportMap.setStateName(redisUtils.getSysStaticData("ORDER_COST_REPORT_STATE", String.valueOf(orderCostOtherReportVO.getState())).getCodeName());
                otherTotalAmount += orderCostOtherReportVO.getConsumeFee() == null ? 0 : orderCostOtherReportVO.getConsumeFee();
                rtnOrderCostOtherReportVOList.add(orderCostOtherReportMap);
            }
        }

        //   OrderCostReport.setOilTotalAmount(CommonUtil.divide(oilTotalAmount));
        //    OrderCostReport.setEtcTotalAmount(CommonUtil.divide(etcTotalAmount));
        //    OrderCostReport.setOilCostDataList(oilCostDataList);
        //   OrderCostReport.setEtcCostDataList(etcCostDataList);
        OrderCostReport.setOtherTotalAmount(CommonUtil.divide(otherTotalAmount));
        OrderCostReport.setOtherCostDataListStr(rtnOrderCostOtherReportVOList);

        return OrderCostReport;
    }

    private boolean isDoSave(OrderInfo orderInfo) {
        if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.FINISH) {
            return false;
        } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.CANCELLED) {
            String day = redisUtils.getSysCfg("ORDER_COST_REPORT_DAY", "-1").getCfgValue();
            OrderScheduler orderScheduler = getOrderScheduler(orderInfo.getOrderId());
            LocalDateTime date = orderScheduler.getDependTime();
            Date date1 = Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
            Date newDate = PayOutIntfUtil.getDay(date1, Integer.parseInt(day));
            Date nowDate = new Date();
            if (nowDate.getTime() > newDate.getTime()) {
                return false;
            }
        }
        return true;
    }

    private OrderScheduler getOrderScheduler(Long orderId) {
        OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
        if (null == orderScheduler || orderScheduler.getId() == null) {
            OrderSchedulerH orderSchedulerH = iOrderSchedulerService.getOrderSchedulerH(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("未查询到订单靠台时间!");
            }
            orderScheduler = new OrderScheduler();
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
        }
        return orderScheduler;
    }

    @Override
    public OrderMainReport getObjectByOrderId(Long orderId) {
        return baseMapper.getObjectByOrderId(orderId);
    }

    @Override
    public List<OrderCostReportDto> getListById(Long orderId) {
        return baseMapper.getListById(orderId);

    }

    @Override
    public List<OrderCostDetailReport> getOrderCostDetailReport(Long id, boolean b) {
        List<OrderCostDetailReport> list = null;

        if (b) {
            list = baseMapper.getOrderCostDetailReport(id);

        } else {
            list = baseMapper.getOrderCostDetailReports(id);
        }
        return list;
    }

    @Override
    public List<OrderCostOtherReportVO> getOrderCostOtherReport(Long id, boolean b) {
        List<OrderCostOtherReportVO> list = null;
        if (b) {
            list = baseMapper.getOrderCostOtherReport(id);

        } else {
            list = baseMapper.getOrderCostOtherReports(id);
        }
        return list;
    }

    @Override
    public Long addOrderCostOtherType(Long id, String typeName, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isBlank(typeName)) {
            throw new BusinessException("请输入类型名称！");
        }
        if (typeName.length() > 50) {
            throw new BusinessException("类型名称过长！");
        }

        SysExpense vo = null;
        if (id != null && id > 0) {
            vo = sysExpenseService.getById(id);
            if (vo == null) {
                throw new BusinessException("未找到需要修改的类型！");
            }
            if (vo.getTenantId() == null || vo.getTenantId() == -1) {
                throw new BusinessException("类型名称[" + vo.getName() + "]不能修改！");
            }

            if (typeName.equals(vo.getName())) {
                return vo.getId();
            }
        }

//        List<OrderCostOtherType> list = iOrderCostOtherTypeService.getOrderCostOtherTypeList(-1L, typeName);
//        if (list.size() > 0) {
//            throw new BusinessException("类型名称[" + typeName + "]已经存在！");
//        }
//        list = iOrderCostOtherTypeService.getOrderCostOtherTypeList(loginInfo.getTenantId(), typeName);
//        if (list.size() > 0) {
//            throw new BusinessException("类型名称[" + typeName + "]已经存在！");
//        }

//        if (vo == null) {
//            Integer maxNum = Integer.parseInt(redisUtils.getSysCfg("ORDER_COST_OTHER_TYPE_NUM_MAX", 0 + "").toString());
//            if (maxNum != null && maxNum >= 0) {
//                if (maxNum <= iOrderCostOtherTypeService.getOrderCostOtherTypeCount(loginInfo.getTenantId())) {
//                    throw new BusinessException("上报其他费用类型已存在" + maxNum + "项，不允许新增！");
//                }
//            }
//            vo = new OrderCostOtherType(typeName, loginInfo.getTenantId(), 1L);
//        } else {
//            vo.setTypeName(typeName);
//        }
//        iOrderCostOtherTypeService.saveOrUpdate(vo);

        return vo.getId();
    }

    @Override
    public void doSaveOrUpdateNew(OrderCostReportDto orderCostReportDto, String accessToken) {

        if (orderCostReportDto.getOrderId() < 0) {
            throw new BusinessException("订单号错误！");
        }
        OrderCostDetailReportDto orderCostDetailReportDto = isDoSaves(orderCostReportDto);
        boolean isDoSave = orderCostDetailReportDto.getIsDoSave();
//        if (!isDoSave) {
//            throw new BusinessException("当前订单状态不允许上报！");
//        }
        OrderScheduler orderScheduler = getOrderScheduler(orderCostReportDto.getOrderId());
        long userId = orderScheduler.getCarDriverId();
        orderCostReportDto.setUserId(userId);
        orderCostReportDto.setPlateNumber(orderScheduler.getPlateNumber());
        orderCostReportDto.setTenantId(orderScheduler.getTenantId());
        // long id = -1L;
        OrderMainReport orderMainReport = null;
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderCostReportDto.getId() != null && orderCostReportDto.getId() > 0) {
            orderMainReport = iOrderMainReportService.getById(orderCostReportDto.getId());
            orderCostReportDto.setOrderMainReport(orderMainReport);
            if (null == orderMainReport) {
                throw new BusinessException("该订单上报信息不存在！");
            }
            if (null != orderMainReport.getState() && orderMainReport.getState() == 3) {
                throw new BusinessException("审核中的费用上报数据不允许修改！");
            }
//            List<OrderCostDetailReport> orderCostDetailReportList = iOrderCostReportService.getOrderCostDetailReport(orderMainReport.getId(), true);
//
//            Map<Long, OrderCostDetailReport> orderCostDetailReportMap = new HashMap<>();
//            if (null != orderCostDetailReportList) {
//                for (int i = 0; i < orderCostDetailReportList.size(); i++) {
//                    OrderCostDetailReport orderCostDetailReport = orderCostDetailReportList.get(i);
//                    orderCostDetailReportMap.put(orderCostDetailReport.getId(), orderCostDetailReport);
//                }
//            }
            LambdaQueryWrapper<OrderCostReport> lambda = Wrappers.lambdaQuery();
            lambda.eq(OrderCostReport::getOrderId, orderCostReportDto.getOrderId());
            lambda.ne(OrderCostReport::getState, OrderConsts.ORDER_COST_REPORT.AUDIT);
            lambda.ne(OrderCostReport::getState, AUDIT_PASS);
            List<OrderCostReport> orderCostReportList = OrderCostReportService.list(lambda);
            Map<Long, OrderCostReport> orderCostReportMap = new HashMap<>();
            if (null != orderCostReportList) {
                for (int i = 0; i < orderCostReportList.size(); i++) {
                    OrderCostReport orderCostReport = orderCostReportList.get(i);
                    orderCostReportMap.put(orderCostReport.getId(), orderCostReport);
                }
            }
            dealDataNew(orderCostReportDto, orderCostReportMap, accessToken);
            iSysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.orderCostReport, orderMainReport.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "修改了费用信息");// 添加日志
        } else {
           /* orderMainReport = orderCostReportSV.getObjectByOrderId(orderId);
            if(null != orderCostReport){
                throw new BusinessException("上报数据异常！");
            }*/
            OrderMainReport report = iOrderMainReportService.getObjectByOrderId(orderCostReportDto.getOrderId());
            if (null != report) {
                throw new BusinessException("该订单的上报记录已存在");
            }
            orderMainReport = dealDataNew(orderCostReportDto, new HashMap(), accessToken);
            iSysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.orderCostReport, orderMainReport.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Add, "新增费用上报");// 添加日志
        }
        String operation = orderCostReportDto.getOperation();
        //提交需要启动审核流程
        if (operation.equals("submit")) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", orderMainReport.getId());
            boolean bool = auditService.startProcess(AuditConsts.AUDIT_CODE.ORDER_COST_REPORT, orderMainReport.getId(), SysOperLogConst.BusiCode.orderCostReport, params, accessToken, orderScheduler.getTenantId());
            if (!bool) {
                throw new BusinessException("启动审核流程失败！");
            }
        }
    }

    private OrderMainReport dealDataNew(OrderCostReportDto orderCostReportDto, Map<Long, OrderCostReport> orderCostReportMap, String accessToken) {
        String operation = orderCostReportDto.getOperation();
        if (StringUtils.isBlank(operation)) {
            throw new BusinessException("操作类型错误！");
        }
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Date date = new Date();
        OrderMainReport orderMainReport = orderCostReportDto.getOrderMainReport();

        if (null == orderMainReport) {
            orderMainReport = new OrderMainReport();
        }
        //处理油费上报
        // List<OrderCostDetailReport> orderCostDetailReportList = dealOrderCostDetailReport(orderCostReportDto, orderCostDetailReportMap, date, orderCostReportDto.getTenantId(), accessToken);
        // doDelData(orderCostDetailReportMap);

        //处理其他费用上报
        List<OrderCostOtherReport> otherCostDataList = orderCostReportDto.getOtherCostDataListStr();
        List<OrderCostOtherReportVO> orderCostOtherReportList = null;
        if (otherCostDataList != null) {
            this.checkOrderCostOtherReport(otherCostDataList, orderCostReportDto.getTenantId());
            orderCostOtherReportList = this.dealOrderCostOtherReport(orderMainReport.getId(), orderCostReportDto.getUserId(), otherCostDataList, operation, loginInfo);
        }
        //处理公里数数据
        List<OrderCostReport> orderCostReportList = dealOrderCostlReport(orderCostReportDto, orderCostReportMap, date, loginInfo);
        //校验是否有修改数据或删除数据
        if (orderCostReportList.size() == 0 && orderCostOtherReportList == null) {
            throw new BusinessException("没有更改上报费用数据！");
        }

        long id = -1L;
        long orderId = orderCostReportDto.getOrderId();
        orderMainReport.setOrderId(orderId);
        if (id < 0) {
            orderMainReport.setIsAuditPass(0);
        }
        //保存
        if (operation.equals("save")) {
            //横向校验
            orderMainReport.setOpId(loginInfo.getId());
            orderMainReport.setOpName(loginInfo.getName());
            if (null == orderMainReport.getCreateTime()) {
                orderMainReport.setCreateTime(date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
            }
            orderMainReport.setState(0);
            orderMainReport.setIsAudit(0);
            orderMainReport.setTenantId(orderCostReportDto.getTenantId() > 0 ? orderCostReportDto.getTenantId() : loginInfo.getTenantId());
        }
        //提交
        else if (operation.equals("submit")) {
            //提交校验
            orderMainReport.setOpId(loginInfo.getId());
            orderMainReport.setOpName(loginInfo.getName());
            orderMainReport.setSubUserId(loginInfo.getId());
            orderMainReport.setSubUserName(loginInfo.getName());
            if (null == orderMainReport.getCreateTime()) {
                orderMainReport.setCreateTime(date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime());
            }
            orderMainReport.setSubTime(date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
            orderMainReport.setState(3);
            orderMainReport.setIsAudit(1);
            orderMainReport.setTenantId(orderCostReportDto.getTenantId() > 0 ? orderCostReportDto.getTenantId() : loginInfo.getTenantId());
        }
        iOrderMainReportService.saveOrUpdate(orderMainReport);
//        for (int i = 0; i < orderCostDetailReportList.size(); i++) {
//            OrderCostDetailReport orderCostDetailReport = orderCostDetailReportList.get(i);
//            orderCostDetailReport.setRelId(orderMainReport.getId());
//            orderCostDetailReport.setOrderId(orderMainReport.getOrderId());
//            if (StringUtils.isNotBlank(orderCostDetailReport.getRemark())
//                    && orderCostDetailReport.getRemark().length() > 255) {
//                String type = "油费";
//                if (orderCostDetailReport.getTableType() != null
//                        && orderCostDetailReport.getTableType().intValue() == 2) {
//                    type = "路桥费";
//                }
//                throw new BusinessException(type + "费用备注超长，字数不能超过255个！");
//            }
//            iOrderCostDetailReportService.saveOrUpdate(orderCostDetailReport);
//        }
        //保存新增或修改上报的其他费用
        if (orderCostOtherReportList != null) {
            for (OrderCostOtherReportVO orderCostOtherReportVO : orderCostOtherReportList) {
                OrderCostOtherReport orderCostOtherReport = new OrderCostOtherReport();
                orderCostOtherReportVO.setRelId(orderMainReport.getId());
                orderCostOtherReportVO.setOrderId(orderMainReport.getOrderId());
                if (StringUtils.isNotBlank(orderCostOtherReportVO.getRemark())
                        && orderCostOtherReportVO.getRemark().length() > 255) {
                    throw new BusinessException("费用备注超长，字数不能超过255个！");
                }
                BeanUtil.copyProperties(orderCostOtherReportVO, orderCostOtherReport);
                orderCostOtherReport.setSubTime(orderCostOtherReportVO.getSubTime());
                if (loginInfo.getOrgIds() != null && loginInfo.getOrgIds().size() > 0) {
                    orderCostOtherReport.setOrgId(loginInfo.getOrgIds().get(0));
                }
                iOrderCostOtherReportService.saveOrUpdate(orderCostOtherReport);
            }
        }

        for (int i = 0; i < orderCostReportList.size(); i++) {
            OrderCostReport orderCostReport = orderCostReportList.get(i);
            orderCostReport.setOrderId(orderMainReport.getOrderId());
            OrderCostReport orderCostReport1 = new OrderCostReport();
            BeanUtil.copyProperties(orderCostReport, orderCostReport1);
            Long costReportOrderId = orderCostReport.getOrderId();
            OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(costReportOrderId);
            orderCostReport1.setCheckTime(orderScheduler.getDependTime());
            OrderCostReportService.saveOrUpdate(orderCostReport1);
        }
        return orderMainReport;
    }

    private List<OrderCostReport> dealOrderCostlReport(OrderCostReportDto orderCostReportDto, Map<Long, OrderCostReport> orderCostReportMap, Date date, LoginInfo loginInfo){
        String operation = orderCostReportDto.getOperation();
        String plateNumber = orderCostReportDto.getPlateNumber();
        List<Map> costDataList = orderCostReportDto.getCostDataList();
        if (null == costDataList) {
            costDataList = new ArrayList<>();
            OrderCostReport or = new OrderCostReport();
            BeanUtil.copyProperties(orderCostReportDto, or);
            long costId = -1L;
            if (costId > 0) {

                or.setId(costId);
            }
            Map outVerMap = BeanUtils.convertBean2Map(or);
            Long startKm = orderCostReportDto.getStartKm();
            Long loadingKm = orderCostReportDto.getLoadingKm();
            Long unloadingKm = orderCostReportDto.getUnloadingKm();
            Long endKm = orderCostReportDto.getEndKm();
            outVerMap.put("startKm", startKm);
            outVerMap.put("loadingKm", loadingKm);
            outVerMap.put("unloadingKm", unloadingKm);
            outVerMap.put("endKm", endKm);
            costDataList.add(outVerMap);
        }
        List<OrderCostReport> orderCostReportList = new ArrayList<>();
        if (null != costDataList && !costDataList.isEmpty()) {
            for (int i = 0; i < costDataList.size(); i++) {
                List<OrderCostOtherReport> otherCostDataListStr = orderCostReportDto.getOtherCostDataListStr();
                OrderCostOtherReport orderCostOtherReport =null;
                if(otherCostDataListStr!=null && otherCostDataListStr.size()>0) {
                    orderCostOtherReport = otherCostDataListStr.get(i);
                }
                Map dataMap = costDataList.get(i);
                List<String> keyList = new ArrayList<>();
                keyList.add("startKm");
                keyList.add("loadingKm");
                keyList.add("unloadingKm");
                keyList.add("endKm");
                dealUnitData(keyList, dataMap, "100");
                long id = DataFormat.getLongKey(dataMap, "id");
                OrderCostReport report = null;
                if (id > 0) {
                    report = orderCostReportMap.get(id);
                    if (null == report || (null != report.getState() && report.getState() == 5)) {
                        continue;
                    }
                } else {
                    report = new OrderCostReport();
//                    report.setUserId(userId);
                }
                BeanUtil.copyProperties(dataMap, report, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                if (StringUtils.isBlank(report.getPlateNumber())) {
                    report.setPlateNumber(plateNumber);
                }
                //保存
                if (operation.equals("save")) {
                    //横向校验
//                    report.checkData();
                    report.setOpId(loginInfo.getId());
                    report.setOpName(loginInfo.getName());
                    if (null == report.getCreateTime()) {
                        report.setCreateTime(date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime());
                    }
                    report.setState(0);
                    report.setIsAudit(0);
                    report.setTenantId(loginInfo.getTenantId());
//                    report.setSubTime(orderCostOtherReport.getUpdateTime());
                }
                //提交
                else if (operation.equals("submit")) {
                    //提交校验
                    report.submitCheckData();
                    report.setOpId(loginInfo.getId());
                    report.setOpName(loginInfo.getName());
                    report.setSubUserId(loginInfo.getId());
                    report.setSubUserName(loginInfo.getName());
                    if (null == report.getCreateTime()) {
                        report.setCreateTime(date.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime());
                    }
                    report.setSubTime(date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime());
                    report.setState(3);
                    report.setIsAudit(1);
                    report.setTenantId(loginInfo.getTenantId());
                    report.setSubTime(orderCostOtherReport.getUpdateTime());
                }
                orderCostReportList.add(report);
            }
        }
        return orderCostReportList;
    }

    private void dealUnitData(List<String> keyList, Map map, String unit) {
        BigDecimal dv = new BigDecimal(unit);
        try {
            for (String string : keyList) {
                String value = DataFormat.getStringKey(map, string);
                if (StringUtils.isNotBlank(value) && CommonUtil.isNumber(value)) {
                    map.put(string, new BigDecimal(value).longValue());
                }

            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private List<OrderCostOtherReportVO> dealOrderCostOtherReport(Long relId, Long userId, List<OrderCostOtherReport> otherCostDataList, String operation, LoginInfo loginInfo) {
        //原未审核数据
        List<OrderCostOtherReportVO> oriOrderCostOtherReportList = null;
        if (relId != null && relId > 0) {
            oriOrderCostOtherReportList = iOrderCostOtherReportService.getOrderCostOtherReport(relId, true);
        }
        //新增/修改的数据
        List<OrderCostOtherReportVO> orderCostOtherReportList = new ArrayList<OrderCostOtherReportVO>();
        Long tenantId = loginInfo.getTenantId();
        if (null != otherCostDataList && !otherCostDataList.isEmpty()) {
            OrderCostOtherReport otherCostDataMap = null;
            long id = -1L;
            String tmpStr = null;
            SysExpense orderCostOtherType = null;
            for (int i = 0; i < otherCostDataList.size(); i++) {
                otherCostDataMap = otherCostDataList.get(i);
                tmpStr = otherCostDataMap.getState() == null ? "" : String.valueOf(otherCostDataMap.getState());
                if (StringUtils.isNotBlank(tmpStr) && tmpStr.equals(5)) {
                    continue;
                }
                if (otherCostDataMap.getId() != null) {
                    id = otherCostDataMap.getId();
                }
                tmpStr = otherCostDataMap.getConsumeFeeStr();
                otherCostDataMap.setConsumeFee(tmpStr == null ? null : Long.valueOf(tmpStr));
                OrderCostOtherReportVO report = null;
                if (id > 0) {
                    report = new OrderCostOtherReportVO(id);
                    int index = oriOrderCostOtherReportList.indexOf(report);
                    if (index < 0) {
                        continue;
                    } else {
                        report = oriOrderCostOtherReportList.get(index);
                        BeanUtil.copyProperties(otherCostDataMap, report, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                        report.setTypeName(otherCostDataMap.getTypeName());
                    }
                } else {
                    report = new OrderCostOtherReportVO();
                    BeanUtil.copyProperties(otherCostDataMap, report, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
                    report.setUserId(userId);
                    report.setCreateTime(new Date());
                }

                report.setSubUserId(userId);
                report.setSubUserName(loginInfo.getName());
                if (otherCostDataMap.getTableType() != null && otherCostDataMap.getTableType() == 1) {
                    report.setOilMileage(StringUtils.isNotBlank(otherCostDataMap.getOilMileageStr()) && CommonUtil.isNumber(otherCostDataMap.getOilMileageStr()) ? (long) (Double.valueOf(otherCostDataMap.getOilMileageStr()) * 1000) : null);
                }
                if (StringUtils.isBlank(report.getTypeName())) {
                    orderCostOtherType = sysExpenseService.getById(report.getTypeId());
                    if (orderCostOtherType == null) {
                        throw new BusinessException("未找到消费类型！");
                    }
                    report.setTypeName(orderCostOtherType.getName());
                }
                //保存
                if ("save".equals(operation)) {
                    report.setState(0);
                    report.setIsAudit(0);
                } else if ("submit".equals(operation)) {
                    report.setState(3);
                    report.setIsAudit(1);
                }
                report.setUpdateTime(otherCostDataMap.getUpdateTime());
                report.setSubTime(otherCostDataMap.getUpdateTime());
                report.setTenantId(otherCostDataMap.getTenantId() != null && otherCostDataMap.getTenantId() > 0 ? otherCostDataMap.getTenantId() : tenantId);
                orderCostOtherReportList.add(report);
            }
        }

        //删除不存在的其他费用
        boolean isHasDel = false;
        if (oriOrderCostOtherReportList != null && oriOrderCostOtherReportList.size() > 0) {
            for (OrderCostOtherReportVO vo : oriOrderCostOtherReportList) {
                if (orderCostOtherReportList.indexOf(vo) < 0) {
                    iOrderCostOtherReportService.removeById(vo);
                    isHasDel = true;
                }
            }
        }

        return orderCostOtherReportList.size() > 0 || isHasDel ? orderCostOtherReportList : null;
    }

    private void checkOrderCostOtherReport(List<OrderCostOtherReport> otherCostDataList, long tenantId) {
        int index = 0;
        String tmpStr = null;
        if (otherCostDataList!=null&&otherCostDataList.size()>0) {
            for (OrderCostOtherReport map : otherCostDataList) {
                index++;
                tmpStr = map.getConsumeFeeStr();
                if (StringUtils.isBlank(tmpStr) || !CommonUtil.isNumber(tmpStr) || Double.valueOf(tmpStr) < 0) {
                    throw new BusinessException("费用" + index + "中金额不能为空或填写不正确！");
                }
//            tmpStr= DataFormat.getStringKey(map, "consumeNum");
//            if(StringUtils.isBlank(tmpStr)||!CommonUtil.isNumber(tmpStr)||Double.valueOf(tmpStr)<0) {
//                throw new BusinessException("其他费用"+index+"中消费数量不能为空或填写不正确！");
//            }
//            tmpStr=DataFormat.getStringKey(map, "priceUnit");
//            if(StringUtils.isBlank(tmpStr)||!CommonUtil.isNumber(tmpStr)||Double.valueOf(tmpStr)<0) {
//                throw new BusinessException("其他费用"+index+"中单价不能为空或填写不正确！");
//            }
                Long typeId = map.getTypeId();
                if (typeId == null || typeId <= 0L) {
                    throw new BusinessException("费用" + index + "中类型不能为空！");
                }
//            if (map.getFileId5() == null && map.getFileId1() == null && map.getFileId2() == null && map.getFileId3() == null && map.getFileId4() == null) {
//                throw new BusinessException("费用" + index + "中至少需上传一个附件！");
//            }
                map.setTenantId(tenantId);
            }
        }
    }

    private void doDelData(Map<Long, OrderCostDetailReport> orderCostDetailReportMap) {

        for (OrderCostDetailReport orderCostDetailReport : orderCostDetailReportMap.values()) {
            if (null != orderCostDetailReport && null != orderCostDetailReport.getIsDel() && orderCostDetailReport.getIsDel()) {
                OrderCostReportService.removeById(orderCostDetailReport);
            }
        }
    }

    private List<OrderCostDetailReport> dealOrderCostDetailReport(OrderCostReportDto orderCostReportDto, Map<Long, OrderCostDetailReport> orderCostDetailReportMap, Date date, Long tenantId, String accessToken) {
        long userId = orderCostReportDto.getUserId();
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String operation = orderCostReportDto.getOperation();
        //油费信息
        List<OrderCostDetailReport> oilCostDataList = orderCostReportDto.getOilCostDataList();
        List<OrderCostDetailReport> orderCostDetailReportList = new ArrayList<>();
        if (null != oilCostDataList && !oilCostDataList.isEmpty()) {
            for (int i = 0; i < oilCostDataList.size(); i++) {
                OrderCostDetailReport oilDataMap = oilCostDataList.get(i);
                long id = oilDataMap.getId();
                String amount = oilDataMap.getAmountStr();//金额
                oilDataMap.setAmount(Long.valueOf(CommonUtil.multiply(amount, 100 + "")));
                amount = oilDataMap.getOilMileageStr();//加油里程
                oilDataMap.setOilMileage(StringUtils.isNotBlank(amount) && CommonUtil.isNumber(amount) ? (long) (Double.valueOf(amount) * 1000) : null);
                OrderCostDetailReport report = null;
                if (id > 0) {
                    report = orderCostDetailReportMap.get(id);
                    if (null == report || 5 == report.getState()) {
                        continue;
                    }
                    BeanUtil.copyProperties(oilDataMap, report);
                    report.setIsDel(false);
                } else {
                    report = new OrderCostDetailReport();
                    BeanUtil.copyProperties(oilDataMap, report);
                    report.setUserId(userId);
                }
                if (report == null) {
                    continue;
                }
                report.setSubInfo(operation, date, loginInfo);
                report.setSortId((i + 1));
                report.checkData(1, "油费", tenantId);
                report.setTableType(1);
                if (report.getPaymentWay() == 3) {
                    throw new BusinessException("支付方式错误");
                }
                orderCostDetailReportList.add(report);
            }
        }

        //ETC费用信息
        //List<Map> etcCostDataList = orderCostReportDto.getEtcCostDataList();
//        if(null != etcCostDataList && !etcCostDataList.isEmpty()){
//            for (int i = 0; i < etcCostDataList.size(); i++) {
//                Map etcDataMap = etcCostDataList.get(i);
//                long id = DataFormat.getLongKey(etcDataMap,"id");
//                String amount = DataFormat.getStringKey(etcDataMap,"amount");//金额
//                etcDataMap.put("amount",CommonUtil.multiply(amount,100+""));
//                OrderCostDetailReport report = null;
//                if(id > 0){
//                    report = orderCostDetailReportMap.get(id);
//                    if(null == report||5==report.getState()){
//                        continue;
//                    }
//                    BeanUtil.copyProperties(etcDataMap,report);
//                    report.setIsDel(false);
//                }else{
//                    report = new OrderCostDetailReport();
//                    BeanUtil.copyProperties(etcDataMap,report);
//                    report.setUserId(userId);
//                }
//
//                if(report==null){
//                    continue;
//                }
//                report.setSubInfo(operation,date,loginInfo);
//                report.setSortId((i+1));
//                report.checkData(2,"路桥费",tenantId);
//                report.setTenantId(orderCostReportDto.getTenantId()>0?orderCostReportDto.getTenantId():loginInfo.getTenantId());
//                report.setTableType(2);
//                if(report.getPaymentWay() == 1){
//                    throw new BusinessException("支付方式错误");
//                }
//                orderCostDetailReportList.add(report);
//            }
//        }
        return orderCostDetailReportList;
    }

    @Override
    public List<OrderCostReport> getOrderCostReportByOrderIds(Long orderId, boolean b) {

        List<OrderCostReport> list = null;
        if (b) {
            list = baseMapper.getOrderCostReportByOrderId(orderId);
        }
        return list;
    }

    @Override
    public Map<Long, long[]> getKilometreByOrderIds(List<Long> orders) {
        StringBuffer str = new StringBuffer();
        for (Long orderId : orders) {
            str.append("'").append(orderId).append("',");
        }
        List<Map> retList = orderCostReportMapper.getKilometreByOrderIds(str.substring(0, str.length() - 1));
        Map rtnMap = new HashMap();
        for (int i = 0; i < retList.size(); i++) {
            Map retMap = retList.get(i);
            //出车仪表盘公里数
            long startKm = DataFormat.getLongKey(retMap, "startKm");
            //装货仪表盘公里数
            long loadingKm = DataFormat.getLongKey(retMap, "loadingKm");
            //卸货仪表盘公里数
            long unloadingKm = DataFormat.getLongKey(retMap, "unloadingKm");
            //交车仪表盘公里数
            long endKm = DataFormat.getLongKey(retMap, "endKm");
            //校准载重里程
            long loadMileage = DataFormat.getLongKey(retMap, "loadMileage");
            //校准空载距离
            long capacityLoadMileage = DataFormat.getLongKey(retMap, "capacityLoadMileage");
            /***
             * 空载公里数：空载里程 =【装货仪表盘公里数 - 出车仪表盘公里数】+ 【交车仪表盘公里数 - 卸货仪表盘公里数】
             载重公里数：载重里程 = 卸货仪表盘公里数 - 装货仪表盘公里数

             如果装货仪表盘公里数和卸货仪表盘公里数为空，则空载里程为0，载重里程 =交车仪表盘公里数 - 出车仪表盘公里数
             若装货仪表盘公里数为空，卸货仪表盘公里数不为空，则空载里程=交车仪表盘公里数 - 卸货仪表盘公里数，载重里程 =卸货仪表盘公里数 - 出车仪表盘公里数
             若装货仪表盘公里数不为空，卸货仪表盘公里数为空，则空载里程=装货仪表盘公里数 - 出车仪表盘公里数，载重里程 =交车仪表盘公里数 - 装货仪表盘公里数

             */
            long nullLoadKm = 0;//空载里程
            long loadKm = 0;//载重公里数
            /**
             * 如果存在校准里程先取校准里程，否则取上报数据
             */
            if (loadMileage >= 0 && capacityLoadMileage >= 0) {
                nullLoadKm = capacityLoadMileage;
                loadKm = loadMileage;
            } else {
                //如果装货仪表盘公里数和卸货仪表盘公里数为空，则空载里程为0，载重里程 =交车仪表盘公里数 - 出车仪表盘公里数
                if (loadingKm < 0 && unloadingKm < 0) {
                    nullLoadKm = 0;
                    loadKm = endKm - startKm;
                }
                //若装货仪表盘公里数为空，卸货仪表盘公里数不为空，则空载里程=交车仪表盘公里数 - 卸货仪表盘公里数，载重里程 =卸货仪表盘公里数 - 出车仪表盘公里数
                else if (loadingKm < 0 && unloadingKm >= 0) {
                    nullLoadKm = endKm - unloadingKm;
                    loadKm = unloadingKm - startKm;
                }
                //若装货仪表盘公里数不为空，卸货仪表盘公里数为空，则空载里程=装货仪表盘公里数 - 出车仪表盘公里数，载重里程 =交车仪表盘公里数 - 装货仪表盘公里数
                else if (loadingKm >= 0 && unloadingKm < 0) {
                    nullLoadKm = loadingKm - startKm;
                    loadKm = endKm - loadingKm;
                } else {
                    //空载里程 =【装货仪表盘公里数 - 出车仪表盘公里数】+ 【交车仪表盘公里数 - 卸货仪表盘公里数】
                    nullLoadKm = (loadingKm - startKm) + (endKm - unloadingKm);
                    //载重公里数：载重里程 = 卸货仪表盘公里数 - 装货仪表盘公里数
                    loadKm = unloadingKm - loadingKm;
                }
            }

            long kms[] = new long[2];
            kms[0] = nullLoadKm;
            kms[1] = loadKm;
            long orderId = DataFormat.getLongKey(retMap, "orderId");
            rtnMap.put(orderId, kms);
        }

        return rtnMap;
    }

    /**
     * 判断订单费用上报 是否已经完成审核：true订单上报的费用审核完成，false订单上报的费用还有审核未完成
     *
     * @param orderId 订单号
     * @return
     */
    @Override
    public boolean judgeOrderFeeIsExamineFinish(Long orderId, String accessToken) {
        boolean isFinish = true;
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("请输入订单号");
        }
        OrderCostReportDto orderCostDetailReportByOrder = OrderCostReportService.getOrderCostDetailReportByOrderId(orderId, accessToken);
        List<OrderCostOtherReport> list = orderCostDetailReportByOrder.getOtherCostDataListStr();
        if (list != null && list.size() > 0) {
            for (OrderCostOtherReport detail : list) {
                if (detail.getState() != null && detail.getState() != AUDIT_PASS) {
                    isFinish = false;
                    break;
                }
            }
        }
        return isFinish;
    }

    private OrderCostDetailReportDto isDoSaves(OrderCostReportDto orderCostReportDto) {

        if (orderCostReportDto.getOrderId() < 0) {
            throw new BusinessException("订单号错误！");
        }
        OrderInfo orderInfo = iOrderInfoService.getOrder(orderCostReportDto.getOrderId());
        if (null == orderInfo) {
            OrderInfoH oh = iOrderInfoService.getOrderH(orderCostReportDto.getOrderId());
            orderInfo = new OrderInfo();
            BeanUtil.copyProperties(oh, orderInfo);
        }
        if (null == orderInfo) {
            throw new BusinessException("订单不存在");
        }
        OrderMainReport orderMainReport = iOrderMainReportService.getById(orderCostReportDto.getOrderId());
        OrderCostDetailReportDto orderCostDetailReportDto = new OrderCostDetailReportDto();
        orderCostDetailReportDto.setIsDoSave(isDoSave(orderInfo));
        if (null != orderMainReport) {
            orderCostDetailReportDto.setState(orderMainReport.getState());
        }
        return orderCostDetailReportDto;
    }


    @Override
    public Long getOrderCostOtherReportAmountByOrderId(Long orderId) {
        Long orderCostOtherReportAmountByOrderId = orderCostReportMapper.getOrderCostOtherReportAmountByOrderId(orderId);
        return orderCostOtherReportAmountByOrderId;
    }
}
