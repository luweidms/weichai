package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderTransferInfoService;
import com.youming.youche.order.api.order.IProblemVerOptService;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderTransferInfo;
import com.youming.youche.order.dto.EstimatedCostsDto;
import com.youming.youche.order.dto.OrderDto;
import com.youming.youche.order.dto.OrderListOut;
import com.youming.youche.order.dto.OrderReciveEchoDto;
import com.youming.youche.order.dto.order.OrderListWxDto;
import com.youming.youche.order.dto.order.QueryOrderOilCardInfoDto;
import com.youming.youche.order.dto.order.QueryOrderResponsiblePartyDto;
import com.youming.youche.order.dto.order.QueryUserOrderJurisdictionDto;
import com.youming.youche.order.vo.DispatchOrderVo;
import com.youming.youche.order.vo.EstimatedCostsVo;
import com.youming.youche.order.vo.OrderListAppVo;
import com.youming.youche.order.vo.OrderListInVo;
import com.youming.youche.order.vo.OrderListWxVo;
import com.youming.youche.order.vo.OrderReceiptVo;
import com.youming.youche.record.vo.PriceAuditVo;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.constant.AuditConsts;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.alibaba.nacos.client.utils.EnvUtil.LOGGER;

/**
 * <p>
 * 新的订单表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@RestController
@RequestMapping("/order/info")
public class OrderInfoController extends BaseController<OrderInfo, IOrderInfoService> {


    @DubboReference(version = "1.0.0")
    IOrderInfoService orderInfoService;

    @DubboReference(version = "1.0.0")
    IOrderTransferInfoService orderTransferInfoService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IAuditSettingService iAuditSettingService;

    @DubboReference(version = "1.0.0")
    IProblemVerOptService problemVerOptService;

    @Override
    public IOrderInfoService getService() {
        return orderInfoService;
    }


    /**
     * 订单列表的公共接口
     *
     * @param orderListInVo
     * @return
     */
    @GetMapping("/getOrderList")
    public ResponseResult getOrderList(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                       OrderListInVo orderListInVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderListOut> orderList = new Page<>();
        try {
            orderList = orderInfoService.getOrderList(orderListInVo, accessToken, pageNum, pageSize);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseResult.success(orderList);
    }

    /**
     * 订单录入接口
     *
     * @param orderDto
     * @return
     */
    @PostMapping("saveOrderInfo")
    public ResponseResult saveOrderInfo(@RequestBody OrderDto orderDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Object message = orderInfoService.saveOrder(orderDto, accessToken);
        return ResponseResult.success(message);
    }


    /**
     * 催单
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/warn")
    public ResponseResult warn(Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderTransferInfo orderTransferInfo = orderTransferInfoService.reminder(orderId, accessToken);
        return ResponseResult.success(orderTransferInfo);
    }

    /**
     * 在线接单页面 拒单功能
     *
     * @param orderId 订单id
     * @param remark  备注
     */
    @PostMapping("refuseOrder")
    public ResponseResult refuseOrder(@RequestParam("orderId") Long orderId,
                                      @RequestParam("remark") String remark) {
        if (orderId == null) {
            throw new BusinessException("查询订单号为空！");
        }
        if (StringUtils.isEmpty(remark)) {
            throw new BusinessException("拒单原因为空");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        int i = orderInfoService.refuseOrder(orderId, remark, accessToken, 2);
        if (i == 0) {
            return ResponseResult.success("拒单失败！");
        } else {
            return ResponseResult.success("拒单成功！");
        }
    }

    @PostMapping("saveExportOrders")
    public ResponseResult saveExportOrders(@RequestParam("file") MultipartFile file) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 100) {
                throw new BusinessException("最多一次性导入100条数据");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "orderData.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("订单导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            orderInfoService.saveExportOrders(file.getBytes(), record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("订单列表查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 查询订单责任方[30038]
     */
    @GetMapping("queryOrderResponsibleParty")
    public ResponseResult queryOrderResponsibleParty(Long orderId) {
        List<QueryOrderResponsiblePartyDto> queryOrderResponsiblePartyDtos = orderInfoService.queryOrderResponsibleParty(orderId);
        return ResponseResult.success(queryOrderResponsiblePartyDtos);
    }

    /**
     * 查询修改字段差异详情[30045]
     */
    @GetMapping("queryOrderUpdateDetail")
    public ResponseResult queryOrderUpdateDetail(Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderListWxDto dto = orderInfoService.queryOrderUpdateDetail(orderId, accessToken);
        return ResponseResult.success(dto);
    }

    /**
     * 复制订单-WX接口[30047]
     */
    @PostMapping("copyOrderInfo")
    public ResponseResult copyOrderInfo(Long orderId, String dependTime) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long newOrderId = orderInfoService.copyOrderInfo(orderId, dependTime, accessToken);
        return ResponseResult.success(newOrderId);
    }

    /**
     * 微信查询订单列表(30016)
     *
     * @param orderListWxVo
     * @return
     */
    @GetMapping("/queryOrderListWxOut")
    public ResponseResult queryOrderListWxOut(OrderListWxVo orderListWxVo,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderListWxDto> orderListWxDtoPage = orderInfoService.queryOrderListWxOut(orderListWxVo, pageNum, pageSize, accessToken);
        return ResponseResult.success(orderListWxDtoPage);
    }


    /**
     * 订单轨迹变更(30017)
     *
     * @param orderId
     * @return
     */
    @PostMapping("/loading")
    public ResponseResult loading(Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = orderInfoService.loadingWx(orderId, accessToken);
        return ResponseResult.success(b);
    }

    /**
     * 获取订单充值油卡接口[30051]
     */
    @GetMapping("queryOrderOilCardInfo")
    public ResponseResult queryOrderOilCardInfo(Long orderId, String plateNumber) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<QueryOrderOilCardInfoDto> dtos = orderInfoService.queryOrderOilCardInfo(orderId, plateNumber, accessToken);
        return ResponseResult.success(dtos);
    }


    /**
     * 回单审核（30018）
     *
     * @param orderReceiptVo
     * @return
     */
    @PostMapping("/verifyRece")
    public ResponseResult verifyRece(@RequestBody OrderReceiptVo orderReceiptVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = orderInfoService.verifyRece(orderReceiptVo, accessToken);
        return ResponseResult.success(b);
    }

    /**
     * 调车（30019）
     *
     * @param dispatchOrderVo
     * @return
     */
    @PostMapping("/dispatchOrderInfo")
    public ResponseResult dispatchOrderInfo(@RequestBody DispatchOrderVo dispatchOrderVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long s = orderInfoService.dispatchOrderInfo(dispatchOrderVo, accessToken);
        return ResponseResult.success(s);
    }

    /**
     * 评价订单司机(30021)
     *
     * @param orderId
     * @param serviceStarLevel 服务星级
     * @param agingStarLevel   时效星级
     * @param mannerStarLevel  态度星级
     * @return
     */
    @PostMapping("/evaluateOrderDirver")
    public ResponseResult evaluateOrderDirver(Long orderId, Integer serviceStarLevel, Integer agingStarLevel, Integer mannerStarLevel) {
        boolean b = orderInfoService.evaluateOrderDirver(orderId, serviceStarLevel, agingStarLevel, mannerStarLevel);
        return ResponseResult.success(b);
    }

    /**
     * 上传回单回显(30024)
     *
     * @param orderId
     * @return
     */
    @GetMapping("/orderReciveEcho")
    public ResponseResult orderReciveEcho(Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderReciveEchoDto dto = orderInfoService.orderReciveEcho(orderId, accessToken);
        return ResponseResult.success(dto);
    }

    /**
     * 获取用户订单成本收入权限 30061
     *
     * @return
     */
    @GetMapping("queryUserOrderJurisdiction")
    public ResponseResult queryUserOrderJurisdiction() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        QueryUserOrderJurisdictionDto dto = orderInfoService.queryUserOrderJurisdiction(accessToken);
        return ResponseResult.success(dto);
    }

    /**
     * 查询代收人订单列表  30075
     *
     * @param orderListWxVo
     * @return
     */
    @GetMapping("/queryOrderListCollectionOut")
    public ResponseResult queryOrderListCollectionOut(OrderListWxVo orderListWxVo,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderListWxDto> orderListWxDtoPage = orderInfoService.queryOrderListCollectionOut(orderListWxVo, pageNum, pageSize, accessToken);
        return ResponseResult.success(orderListWxDtoPage);
    }

    /**
     * 接单的算费接口(30035)
     *
     * @return
     */
    @GetMapping("/getEstimatedCosts")
    public ResponseResult getEstimatedCosts(EstimatedCostsVo estimatedCostsVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        EstimatedCostsDto estimatedCosts = orderInfoService.getEstimatedCosts(estimatedCostsVo, accessToken);
        return ResponseResult.success(estimatedCosts);
    }

    /**
     * 接口编号 11014
     * 接口入参：
     * userId	                 用户编号
     * 接口出参：
     * userPriceUrl              用户头像，待确认
     * orderSum                  在途订单数量
     * balance                   我的钱包可提现金额，分为单位
     * vehicleSum                我的车辆数量
     * vehicleVer                我的车辆认证标志 0 未认证  1已认证
     * driverVer                 司机身份认证标志 0 未认证  1已认证
     * 我的视图查询接口
     */
    @GetMapping("getUserInfo")
    public ResponseResult getUserInfo(Long userId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(
                orderInfoService.getUserInfo(userId, accessToken)
        );

    }


    /**
     * APP查询订单列表(30001)
     *
     * @param vo
     * @return
     */
    @GetMapping("/queryOrderListAppOut")
    public ResponseResult queryOrderListAppOut(OrderListAppVo vo, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Object o = orderInfoService.queryOrderListApp(vo, pageNum, pageSize, accessToken);
        return ResponseResult.success(o);
    }

    /**
     * 异常审核的接口(30037)
     *
     * @param busiId
     * @param desc
     * @param chooseResult
     * @return
     */
    @PostMapping("/exceptionAudit")
    public ResponseResult exceptionAudit(Long busiId, String desc, Integer chooseResult) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        AuditCallbackDto auditCallbackDto = iAuditSettingService.sure(AuditConsts.AUDIT_CODE.PROBLEM_CODE, busiId, desc, chooseResult, accessToken);
        //  审核成功调用
        if (null != auditCallbackDto && !auditCallbackDto.getIsNext() && auditCallbackDto.getIsAudit() && chooseResult == AuditConsts.RESULT.SUCCESS) {
            problemVerOptService.sucess(busiId, desc, auditCallbackDto.getParamsMap(), accessToken);
            //  审核失败调用
        } else if (AuditConsts.RESULT.FAIL == chooseResult && null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
            problemVerOptService.fail(busiId, desc, accessToken, false);
        }
        return ResponseResult.success("审核成功");
    }


    /**
     * 价格审核(30036)
     *
     * @return
     */
    @PostMapping("priceAudit")
    public ResponseResult priceAudit(@RequestBody PriceAuditVo priceAuditVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean sure = false;
        AuditCallbackDto auditCallbackDto = iAuditSettingService.sure(priceAuditVo.getBusiCode(), priceAuditVo.getBusiId(), priceAuditVo.getDesc(), priceAuditVo.getChooseResult(), accessToken);
        //  审核成功调用
        if (null != auditCallbackDto && !auditCallbackDto.getIsNext() && auditCallbackDto.getIsAudit() && priceAuditVo.getChooseResult() == AuditConsts.RESULT.SUCCESS) {
            if (priceAuditVo.getBusiCode().equals(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE)) {
                //订单修改审核
                orderInfoService.orderUpdateAuditPass(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), true, null, accessToken);
            } else if (priceAuditVo.getBusiCode().equals(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE)) {
                //订单价格审核
                Object updateObj = auditCallbackDto.getParamsMap().get(AuditConsts.RuleMapKey.IS_UPDATE);
                Boolean isUpdate = false;
                try {
                    if (updateObj != null) {
                        isUpdate = Boolean.parseBoolean(updateObj.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                orderInfoService.auditPriceSuccess(auditCallbackDto.getBusiId(), isUpdate, auditCallbackDto.getDesc(), accessToken);
            }
            //  审核失败调用
        } else if (AuditConsts.RESULT.FAIL == priceAuditVo.getChooseResult() && null != auditCallbackDto && auditCallbackDto.getIsAudit() && !auditCallbackDto.getIsNext()) {
            if (priceAuditVo.getBusiCode().equals(AuditConsts.AUDIT_CODE.ORDER_UPDATE_CODE)) {
                //修改订单审核失败
                orderInfoService.orderUpdateAuditNoPass(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), accessToken, null);
            }else if(priceAuditVo.getBusiCode().equals(AuditConsts.AUDIT_CODE.ORDER_PRICE_CODE)){
                orderInfoService.auditPriceFail(auditCallbackDto.getBusiId(), auditCallbackDto.getDesc(), accessToken);
            }
        }
        return ResponseResult.success("审核成功");
    }






}
