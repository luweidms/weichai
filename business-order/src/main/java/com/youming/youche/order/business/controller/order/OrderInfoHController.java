package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysOperLogConst;
import com.youming.youche.order.annotation.Dict;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderOilCardInfoService;
import com.youming.youche.order.api.order.IOrderReceiptService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.oil.CarLastOil;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderReceipt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.OrderCostRetrographyDto;
import com.youming.youche.order.dto.OrderListOut;
import com.youming.youche.order.dto.order.GetOrderDetailForUpdateDto;
import com.youming.youche.order.dto.order.HasPermissionDto;
import com.youming.youche.order.dto.order.OrderDetailsAppDto;
import com.youming.youche.order.dto.order.OrderDetailsDto;
import com.youming.youche.order.dto.order.OrderVerDetailsDto;
import com.youming.youche.order.vo.OrderListInVo;
import com.youming.youche.order.vo.UpdateOrderVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
@RestController
@RequestMapping("order/info/h")
public class OrderInfoHController extends BaseController<OrderInfoH, IOrderInfoHService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInfoHController.class);

    //??????-??????
    private static final String ACTION_DEPEND = "depend";
    //??????-??????
    private static final String ACTION_LEAVE = "leave";
    //??????-??????
    private static final String ACTION_ARRIVE = "arrive";
    //??????-??????
    private static final String ACTION_DEPARTURE = "departure";

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IOrderInfoHService orderInfoHService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService iSysOperLogService;

    @DubboReference(version = "1.0.0")
    IOrderOilCardInfoService iOrderOilCardInfoService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOrderInfoService iOrderInfoService;

    @DubboReference(version = "1.0.0")
    IOrderReceiptService findOrderReceipts;

    @DubboReference(version = "1.0.0")
    IOrderReceiptService orderReceiptService;


    @Override
    public IOrderInfoHService getService() {
        return orderInfoHService;
    }


    //************************************************hzx   ??????************************************

    /**
     * ??????????????????
     *
     * @param orderId ?????????
     */
    @PostMapping("queryOrderDetail")
    @Dict
    public ResponseResult queryOrderDetail(String orderId, Integer orderDetailsType) {
        if (StringUtils.isEmpty(orderId)) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderDetailsDto orderDetail = null;
        if (orderDetailsType <= 0) {
            orderDetail = orderInfoHService.queryOrderDetails(Long.parseLong(orderId), OrderConsts.orderDetailsType.SELECT, accessToken);
        } else if (orderDetailsType == OrderConsts.orderDetailsType.COPY) {
            orderDetail = orderInfoHService.queryOrderDetails(Long.parseLong(orderId), OrderConsts.orderDetailsType.COPY, accessToken);
        } else if (orderDetailsType == OrderConsts.orderDetailsType.UPDATE) {
            orderDetail = orderInfoHService.queryOrderDetails(Long.parseLong(orderId), OrderConsts.orderDetailsType.UPDATE, accessToken);
        }
        if (orderDetail == null) {
            throw new BusinessException("??????????????????[" + orderId + "]????????????????????????????????????");
        }
        return ResponseResult.success(orderDetail);
    }


    /**
     * ????????????????????????
     *
     * @return
     * @throws Exception
     */
    @GetMapping("queryOrderVerDetail")
    public ResponseResult queryOrderVerDetail(@RequestParam("orderId") Long orderId) {
        if (orderId == null || orderId.longValue() <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderVerDetailsDto orderVerDetailsDto = orderInfoHService.queryOrderVerDetails(orderId,accessToken);
        return ResponseResult.success(orderVerDetailsDto);
    }


    /**
     * ??????????????????????????????????????????
     *
     * @return
     * @throws Exception
     */
    @GetMapping("queryOrderLogList")
    public ResponseResult queryOrderLogList(String orderId, Integer querySource, String busiId) {
        orderId = StringUtils.isEmpty(orderId) ? busiId : orderId;
        if (StringUtils.isEmpty(orderId)) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysOperLog> sysOperLogs = iSysOperLogService.querySysOperLogOrderByCreateDate(SysOperLogConst.BusiCode.OrderInfo.getCode()
                , Long.parseLong(orderId), false, accessToken);

        /**????????????????????????*/
        if (querySource == 2) {//=2 ??????????????????????????????????????????????????????
            sysOperLogs.addAll(0, iSysOperLogService.queryAuditRealTimeOperation(Long.parseLong(orderId), new Long[]{Long.valueOf(AuditConsts.AUDIT_CODE.ORDER_PAY_FINAL_FEE_CODE)}, accessToken));
        } else {
            sysOperLogs.addAll(0, iSysOperLogService.queryAuditRealTimeOperation(Long.parseLong(orderId), new Long[]{Long.valueOf(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE)
                    , Long.valueOf(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE)}, accessToken));
        }
        return ResponseResult.success(sysOperLogs);
    }

    /**
     * ???????????????
     */
    @GetMapping("getOrderOilCard")
    public ResponseResult getOrderOilCard(Long orderId, String plateNumber) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("?????????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderOilCardInfo> list = iOrderOilCardInfoService.queryOrderOilCardInfoByOrderId(orderId, null);
        if ((list == null || list.size() == 0) && StringUtils.isNotBlank(plateNumber)) {
            CarLastOil oilCarNumberByPlateNumber = iOrderOilCardInfoService.getOilCarNumberByPlateNumber(plateNumber, accessToken);
            return ResponseResult.success(oilCarNumberByPlateNumber);
        }
        return ResponseResult.success(list);
    }

    /**
     * ??????
     */
    @PostMapping("optDepend")
    public ResponseResult optDepend(@RequestParam("orderId") String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderInfoHService.actionOrder(ACTION_DEPEND, orderId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ??????
     */
    @PostMapping("optLeave")
    public ResponseResult optLeave(@RequestParam("orderId") String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderInfoHService.actionOrder(ACTION_LEAVE, orderId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ??????
     */
    @PostMapping("optArrive")
    public ResponseResult optArrive(@RequestParam("orderId") String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderInfoHService.actionOrder(ACTION_ARRIVE, orderId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ??????
     */
    @PostMapping("optDeparture")
    public ResponseResult optDeparture(@RequestParam("orderId") String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderInfoHService.actionOrder(ACTION_DEPARTURE, orderId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ??????????????????
     */
    @PostMapping("verifyPayPass")
    public ResponseResult verifyPayPass(String verifyDesc, String auditCode, String receiptsStr, Long orderId, String oilCardNum) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        orderInfoHService.verifyPayPass(orderId, auditCode, verifyDesc,
                StringUtils.isBlank(oilCardNum) ? null : Arrays.asList(oilCardNum.split(",")),
                receiptsStr, OrderConsts.RECIVE_TYPE.SINGLE, accessToken);

        return ResponseResult.success("????????????");
    }


    /**
     * ????????????
     */
    @PostMapping("verifyPayFail")
    public ResponseResult verifyPayFail(String verifyDesc, String auditCode, Long orderId, boolean load, boolean receipt) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (StringUtils.isBlank(auditCode)) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }
        String flag = orderInfoHService.verifyPayFail(orderId, auditCode, verifyDesc, load, receipt, accessToken);
        if ("Y".equals(flag)) {
            return ResponseResult.success("???????????????");
        } else {
            return ResponseResult.failure("????????????");
        }

    }


    /**
     * ??????????????????
     *
     * @return
     * @throws Exception
     */
    @GetMapping("findOrderRecive")
    public ResponseResult findOrderRecive(@RequestParam("orderId") Long orderId, @RequestParam("optType") Long optType) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("??????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderReceipt> list = findOrderReceipts.findOrderReceipts(orderId, accessToken, null);
        if (optType == 2) {
            try {
                FastDFSHelper client = FastDFSHelper.getInstance();
                for (OrderReceipt orderReceipt : list) {
                    orderReceipt.setFlowUrl(client.getHttpURL(orderReceipt.getFlowUrl()).split("\\?")[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return ResponseResult.success(list);
    }


    /**
     * ?????????????????? ??????????????????
     */
    @PostMapping("getOrderSchedulerByOrderId")
    public ResponseResult getOrderSchedulerByOrderId(@RequestParam("orderId") Long orderId) {
        OrderScheduler orderScheduler = iOrderSchedulerService.getOrderSchedulerByOrderId(orderId);
        return ResponseResult.success(orderScheduler);
    }

    /**
     * 30062
     * ??????????????????
     */
    @PostMapping("currectOrderTrackDate")
    public ResponseResult currectOrderTrackDate(Long orderId, Integer trackType, String fileId, String verifyDateStr, String fileUrl) {
        Date verifyDate = null;
        if (StringUtils.isNotBlank(verifyDateStr)) {
            try {
                verifyDate = DateUtil.formatStringToDate(verifyDateStr, DateUtil.DATETIME_FORMAT);
            } catch (Exception e) {
                throw new BusinessException("??????????????????????????????");
            }
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderSchedulerService.currectOrderTrackDate(orderId, trackType, verifyDate,
                StringUtils.isBlank(fileId) ? null : Long.parseLong(fileId), fileUrl, accessToken);

        return ResponseResult.success("??????????????????");
    }

    /**
     * ??????????????????
     */
    @GetMapping("getPreFeeZeroCount")
    public ResponseResult getPreFeeZeroCount(OrderListInVo orderListIn,
                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        orderListIn.setSelectType(OrderConsts.SELECT_ORDER_TYPE.ORDER_TAIL_AFTER);
//        isOuter -- outerFlg
        String time = orderListIn.getBeginOrderTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setBeginOrderTime(time + " 00:00:00");
        }
        time = orderListIn.getEndOrderTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setEndOrderTime(time + " 23:59:59");
        }

        time = orderListIn.getBeginDependTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setBeginDependTime(time + " 00:00:00");
            } else if (time.length() == 16) {
                orderListIn.setBeginDependTime(time + ":00");
            }
        }
        time = orderListIn.getEndDependTime();
        if (StringUtils.isNotEmpty(time)) {
            orderListIn.setEndDependTime(time + " 23:59:59");

        }
        time = orderListIn.getBeginReceiveTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setBeginReceiveTime(time + " 00:00:00");
        }
        time = orderListIn.getEndReceiveTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setEndReceiveTime(time + " 23:59:59");
        }
        orderListIn.setAmountFlag(OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderListIn.setIsZeroPre(1);

        int count = 0;

        Page<OrderListOut> orderList = iOrderInfoService.getOrderList(orderListIn, accessToken, pageNum, pageSize);
        if (orderList.getTotal() != 0L) {
            return ResponseResult.success(orderList.getTotal());
        }

        return ResponseResult.success(count);
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping("getDealOrderList")
    public ResponseResult getDealOrderList(OrderListInVo orderListIn,
                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        orderListIn.setSelectType(OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST);
        orderListIn.setIsOuter(-1);
        return getOrderList(orderListIn, pageNum, pageSize);
    }

    /**
     * ????????????????????????
     */
    @GetMapping("getReciveOrderList")
    public ResponseResult getReciveOrderList(OrderListInVo orderListIn,
                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        orderListIn.setSelectType(OrderConsts.SELECT_ORDER_TYPE.RECEIPT_AUDIT);
        orderListIn.setIsOuter(-1);
        return getOrderList(orderListIn, pageNum, pageSize);
    }

    /**
     * ????????????????????????
     */
    @GetMapping("getOrderAuditList")
    public ResponseResult getOrderAuditList(OrderListInVo orderListIn,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        orderListIn.setSelectType(OrderConsts.SELECT_ORDER_TYPE.ORDER_AUDIT);
        orderListIn.setIsOuter(-1);
        return getOrderList(orderListIn, pageNum, pageSize);
    }

    /**
     * ????????????
     */
    @PostMapping("cancleOrder")
    public ResponseResult cancleOrder(Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        orderInfoHService.canel(orderId, null, true, accessToken);

        return ResponseResult.success();
    }

    /**
     * ??????????????????
     */
    @PostMapping("doSetPreFeeZeroToRedis")
    public ResponseResult doSetPreFeeZeroToRedis(@RequestBody OrderListInVo orderListIn,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        orderListIn.setSelectType(OrderConsts.SELECT_ORDER_TYPE.ORDER_TAIL_AFTER);
        // isOuter -- outerFlg
        String time = orderListIn.getBeginOrderTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setBeginOrderTime(time + " 00:00:00");
        }

        time = orderListIn.getEndOrderTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setEndOrderTime(time + " 23:59:59");
        }

        time = orderListIn.getBeginDependTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setBeginDependTime(time + " 00:00:00");
            } else if (time.length() == 16) {
                orderListIn.setBeginDependTime(time + ":00");
            }
        }

        time = orderListIn.getEndDependTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setEndDependTime(time + ":59");
            } else if (time.length() == 16) {
                orderListIn.setEndDependTime(time + ":59");
            }
        }

        time = orderListIn.getBeginReceiveTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setBeginReceiveTime(time + " 00:00:00");
        }

        time = orderListIn.getEndReceiveTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setEndReceiveTime(time + " 23:59:59");
        }
        orderListIn.setAmountFlag(OrderConsts.AMOUNT_FLAG.WILL_PAY);
        orderListIn.setIsZeroPre(1);
        Page<OrderListOut> pageList = iOrderInfoService.getOrderList(orderListIn, accessToken, pageNum, pageSize);
        orderInfoHService.doSetPreFeeZeroToRedis(pageList.getRecords(), accessToken);
        return ResponseResult.success();


    }


    /**
     * ??????????????????????????????????????????????????????
     */
    @PostMapping("getIncomeAndCostPermission")
    public ResponseResult getIncomeAndCostPermission() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        HasPermissionDto hasPermissionDto = orderInfoHService.getIncomeAndCostPermission(accessToken);
        return ResponseResult.success(hasPermissionDto);
    }

    /**
     * ????????????????????????
     */
    private ResponseResult getOrderList(OrderListInVo orderListIn,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        String time = orderListIn.getBeginOrderTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setBeginOrderTime(time + " 00:00:00");
        }

        time = orderListIn.getEndOrderTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setEndOrderTime(time + " 23:59:59");
        }

        time = orderListIn.getBeginDependTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setBeginDependTime(time + " 00:00:00");
            } else if (time.length() == 16) {
                orderListIn.setBeginDependTime(time + ":00");
            }
        }

        time = orderListIn.getEndDependTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setEndDependTime(time + " 23:59:59");
            }
        }

        time = orderListIn.getBeginReceiveTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setBeginReceiveTime(time + " 00:00:00");
        }

        time = orderListIn.getEndReceiveTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setEndReceiveTime(time + " 23:59:59");
        }

        time = orderListIn.getBeginProblemDealTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setBeginProblemDealTime(time + " 00:00:00");
            } else if (time.length() == 16) {
                orderListIn.setBeginProblemDealTime(time + ":00");
            }
        }

        time = orderListIn.getEndProblemDealTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setEndProblemDealTime(time + " 23:59:59");
            } else if (time.length() == 16) {
                orderListIn.setEndProblemDealTime(time + ":59");
            }
        }

        Page<OrderListOut> orderList = iOrderInfoService.getOrderList(orderListIn, accessToken, pageNum, pageSize);

        return ResponseResult.success(orderList);
    }

    /**
     * ????????????????????????
     * ????????????-????????????
     */
    @GetMapping("getOrderCostRetrography")
    public ResponseResult getOrderCostRetrography(Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("?????????????????????");
        }
        OrderCostRetrographyDto rtnMap = iOrderSchedulerService.orderCostRetrography(orderId, true, accessToken);
        return ResponseResult.success(rtnMap);
    }


    //************************************************hzx   ??????************************************

    //************************************************wuhao   ??????************************************
    @GetMapping("getDealOrderListExport")
    public ResponseResult getDealOrderListExport(OrderListInVo orderListIn) {
        orderListIn.setSelectType(OrderConsts.SELECT_ORDER_TYPE.PROBLEMINFO_LIST);
        orderListIn.setIsOuter(-1);

        String time = orderListIn.getBeginOrderTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setBeginOrderTime(time + " 00:00:00");
        }

        time = orderListIn.getEndOrderTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setEndOrderTime(time + " 23:59:59");
        }

        time = orderListIn.getBeginDependTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setBeginDependTime(time + " 00:00:00");
            } else if (time.length() == 16) {
                orderListIn.setBeginDependTime(time + ":00");
            }
        }

        time = orderListIn.getEndDependTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setEndDependTime(time + ":59");
            } else if (time.length() == 16) {
                orderListIn.setEndDependTime(time + ":59");
            }
        }

        time = orderListIn.getBeginReceiveTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setBeginReceiveTime(time + " 00:00:00");
        }

        time = orderListIn.getEndReceiveTime();
        if (StringUtils.isNotEmpty(time) && time.length() == 10) {
            orderListIn.setEndReceiveTime(time + " 23:59:59");
        }

        time = orderListIn.getBeginProblemDealTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setBeginProblemDealTime(time + " 00:00:00");
            } else if (time.length() == 16) {
                orderListIn.setBeginProblemDealTime(time + ":00");
            }
        }

        time = orderListIn.getEndProblemDealTime();
        if (StringUtils.isNotEmpty(time)) {
            if (time.length() == 10) {
                orderListIn.setEndProblemDealTime(time + " 23:59:59");
            } else if (time.length() == 16) {
                orderListIn.setEndProblemDealTime(time + ":59");
            }
        }

        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //??????token??????????????????
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("????????????????????????");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            orderInfoHService.getDealOrderListExport(orderListIn, accessToken, record);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            LOGGER.error("????????????????????????" + e);
            return ResponseResult.failure("??????????????????????????????");
        }
    }

    /**
     * APP?????????????????? ????????????:30002
     * orderId ????????? isHis ???????????????
     * @return
     */
    @GetMapping("/queryOrderDetailsAppOut")
    public ResponseResult queryOrderDetailsAppOut(Long orderId,Integer isHis,Integer selectType,boolean isTransfer){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderDetailsAppDto orderDetailsAppDto = orderInfoHService.queryOrderDetailsAppOut(orderId, isHis, selectType, isTransfer, accessToken);
        return ResponseResult.success(orderDetailsAppDto);
    }


    /**
     * WX??????????????????[30040]
     *
     * @param orderId ?????????
     */
    @GetMapping("queryOrderDetailsWx")
    @Dict
    public ResponseResult queryOrderDetailsWx(String orderId) {
        if (StringUtils.isEmpty(orderId)) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderDetailsDto orderDetail = orderInfoHService.queryOrderDetails(Long.parseLong(orderId), OrderConsts.orderDetailsType.SELECT, accessToken);
        return ResponseResult.success(orderDetail);
    }

    /**
     * ????????????????????????
     * updateType:1????????????????????????2????????????????????????3????????????????????????4?????????????????????;
     * ???????????????30072
     */
    @GetMapping("getOrderDetailForUpdate")
    public ResponseResult getOrderDetailForUpdate(Long orderId, Integer updateType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        GetOrderDetailForUpdateDto orderDetailForUpdate = orderInfoHService.getOrderDetailForUpdate(orderId, updateType, accessToken);
        return ResponseResult.success(orderDetailForUpdate);
    }

    /**
     * ????????????
     * ???????????????30073
     */
    @PostMapping("updateOrder")
    public ResponseResult updateOrder(@RequestBody UpdateOrderVo updateOrderVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderInfoHService.updateOrder(updateOrderVo, accessToken);
        return ResponseResult.success();
    }


}
