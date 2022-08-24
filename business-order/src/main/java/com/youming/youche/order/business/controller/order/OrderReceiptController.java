package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderReceiptService;
import com.youming.youche.order.api.order.IOrderReportService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderReceipt;
import com.youming.youche.order.dto.OrderListAppOutDto;
import com.youming.youche.order.dto.OrderListOut;
import com.youming.youche.order.dto.OrderReceiptDto;
import com.youming.youche.order.dto.OrderReportDto;
import com.youming.youche.order.dto.QueryDriverOrderDto;
import com.youming.youche.order.dto.VehiclesDto;
import com.youming.youche.order.vo.OrderListInVo;
import com.youming.youche.order.vo.OrderReceiptVo;
import com.youming.youche.record.api.order.IOrderSchedulerService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单-回单 前端控制器
 * 聂杰伟
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@RestController
@RequestMapping("order/receipt")
public class OrderReceiptController extends BaseController<OrderReceipt , IOrderReceiptService> {

    @DubboReference(version = "1.0.0")
    private IOrderReceiptService iOrderReceiptService;

    @Resource
    IOrderInfoService iOrderInfoService;

    @DubboReference(version = "1.0.0")
    IOrderReportService orderReportService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;


    @Override
    public IOrderReceiptService getService() {
        return iOrderReceiptService;
    }


    /**
     * 回单审核列表   TODO  这个方法暂时没有用  统一走 getOrderList 订单列表的公共接口
     * @param finalTode 是否查询尾款待处理
     * @param orderType 订单类型
     * @param selectOrderState //查询类型    1 订单跟踪    2 回单审核  3 异常列表
     * @param vehicleClass
     * @param grodType
     * @param vehicleLengh 需求车长
     * @param rows
     * @param priceEnum 计价方式
     * @param orderState 订单状态
     * @param vehicleStatys 需求车辆类型
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getReciveOrderList")
    public ResponseResult getReciveOrderList(@RequestParam(value = "finalTode") Boolean finalTode,
                                             @RequestParam(value = "orderType") Integer orderType,
                                             @RequestParam(value = "selectOrderState") Integer selectOrderState,
                                             @RequestParam(value = "vehicleClass") Integer vehicleClass,
                                             @RequestParam(value = "grodType") String grodType,
                                             @RequestParam(value = "vehicleLengh") String vehicleLengh,
                                             @RequestParam(value = "rows") String rows,
                                             @RequestParam(value = "priceEnum") Integer priceEnum,
                                             @RequestParam(value = "orderState") Integer orderState,
                                             @RequestParam(value = "vehicleStatys") Integer vehicleStatys,
                                             @RequestParam(value = "pageNum") Integer pageNum,
                                             @RequestParam(value = "pageSize") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderListInVo orderListInVo = new OrderListInVo();
        orderListInVo.setFinalTodo(finalTode);
        orderListInVo.setOrderType(String.valueOf(orderType));
        orderListInVo.setSelectType(selectOrderState);
        orderListInVo.setVehicleClass(String.valueOf(vehicleClass));
        orderListInVo.setPriceEnum(priceEnum);
        orderListInVo.setVehicleLengh(vehicleLengh);
        orderListInVo.setOrderState(String.valueOf(orderState));
        orderListInVo.setVehicleStatus(vehicleStatys);

        Page<OrderListOut> orderList = iOrderInfoService.getOrderList(orderListInVo, accessToken, pageNum, pageSize);
        return ResponseResult.success();
    }


    /**
     * 保存上传的回单
     * @param
     * @param  orderReceiptVo 回单
     * @return
     */
    @PostMapping("/saveLoadRecive")
    public  ResponseResult saveLoadRecive(@RequestBody OrderReceiptVo orderReceiptVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iOrderReceiptService.saveLoadRecive(orderReceiptVo,true,accessToken);
        return  ResponseResult.success("上传成功");
    }


    /**
     * 查看回单
     * @param orderId
     * @return
     */
    @GetMapping("/findOrderReceipts")
    public ResponseResult findOrderReceipts(@RequestParam("orderId")Long orderId){

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderReceipt> orderReceipts = iOrderReceiptService.findOrderReceipts(orderId, accessToken,null);
        return ResponseResult.success(orderReceipts);
    }


    /**
     *  单条回单审核
     * @return
     */
    @PostMapping("/verifyRece")
    public  ResponseResult verifyRece(@RequestBody OrderReceiptVo orderReceiptVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderReceiptService.verifyRece(orderReceiptVo.getOrderId().toString(),
                orderReceiptVo.getLoad(),
                orderReceiptVo.getReceipt(),
                orderReceiptVo.getVerifyString(),
                orderReceiptVo.getReceiptsNumber(),
                OrderConsts.RECIVE_TYPE.SINGLE,
                accessToken);
        return  ResponseResult.success();
    }

    /**
     * 聂杰伟
     * 批量审核
     * @return
     */
    @PostMapping("/batchAuditOrder")
    public  ResponseResult batchAuditOrder(@RequestBody OrderReceiptVo orderReceiptVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<Long> list=new ArrayList<>();
        if(StringUtils.isNotBlank(orderReceiptVo.getCheckOrder())){
            String[] orderIdStrs=orderReceiptVo.getCheckOrder().split(",");
            for (String orderIdStr : orderIdStrs) {
                if(StringUtils.isNotBlank(orderIdStr)) {
                    list.add(Long.parseLong(orderIdStr));
                }
            }
        }

        List<OrderReceiptDto> orderReceiptDtos =
                iOrderReceiptService.batchAuditOrder(list,
                        orderReceiptVo.getLoad(), orderReceiptVo.getReceipt(),
                        orderReceiptVo.getVerifyDesc(), accessToken);
        return ResponseResult.success(orderReceiptDtos);
    }


    /**
     * 聂杰伟
     * 接口说明  图片上传
     * @return
     */
    @PostMapping("/saveLoad")
    public ResponseResult saveLoad(@RequestBody OrderReceiptVo orderReceiptVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderReceiptService.upLoadPic(
                orderReceiptVo.getOrderId(),
                orderReceiptVo.getType(),
                orderReceiptVo.getLoadUrl(),
                orderReceiptVo.getLoadPicIdS(),
                orderReceiptVo.getReceiptsNumber(),
                orderReceiptVo.getReceiptId(),
                accessToken);
        return  ResponseResult.success("上传成功");
    }

    /**
     * 模糊查询回单编号
     * @param order_id
     * @param receiptsNumber
     * @return
     */
    @GetMapping("/orderReceipt")
    public  ResponseResult orderReceipt(@RequestParam(value = "orderId") Long order_id,
                                        @RequestParam(value = "receiptsNumber") String receiptsNumber){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderReceipt> orderReceipts = iOrderReceiptService.orderReceipt(order_id, receiptsNumber, accessToken);
        return ResponseResult.success(orderReceipts);
    }

    /**
     * 查询报备列表 (30005)
     * @param orderId 订单号
     * @param userId 报备人
     * @return
     */
    @GetMapping("/queryOrderReportList")
    public ResponseResult queryOrderReportList(Long orderId, Long userId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderReportDto> orderReportDtos = orderReportService.queryOrderReport(orderId, userId, accessToken);
        return ResponseResult.success(orderReportDtos);
    }

    /**
     * 删除订单回单  30058
     */
    @PostMapping("removeRecive")
    public ResponseResult removeRecive(Long orderId, String flowId) {
        iOrderReceiptService.removeRecive(orderId, flowId);
        return ResponseResult.success();
    }


    /**
     * 查询合作车辆列表(30039)
     * @param vehicleCode
     * @param tenantId
     * @return
     */
    @GetMapping("/queryCooperationOrderList")
    public ResponseResult queryCooperationOrderList(Long vehicleCode,Long tenantId,
                                                    @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderListAppOutDto> orderListAppOutDtoPage = iOrderReceiptService.queryCooperationOrderList(vehicleCode, tenantId, pageNum, pageSize, accessToken);
        return ResponseResult.success(orderListAppOutDtoPage);
    }

    /**
     * 查询司机在途订单车辆(30057)
     * @param userId
     * @return
     */
    @GetMapping("/queryDriverOrderInfoPlateNumber")
    public ResponseResult queryDriverOrderInfoPlateNumber(Long userId){
        List<QueryDriverOrderDto> list = iOrderReceiptService.queryDriverOrderPlateNumber(userId);
        return ResponseResult.success(list);
    }


    /**
     * 上传回单/合同(30003)
     * @param orderReceiptVo
     * @return
     */
    @PostMapping("/upLoadReceipts")
    public ResponseResult upLoadReceipts(@RequestBody OrderReceiptVo orderReceiptVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderReceiptService.upLoadPic(
                orderReceiptVo.getOrderId(),
                orderReceiptVo.getType(),
                orderReceiptVo.getLoadUrl(),
                orderReceiptVo.getLoadPicIdS(),
                orderReceiptVo.getReceiptsNumber(),
                orderReceiptVo.getReceiptId(),
                accessToken);
        Long receiptId = orderReceiptVo.getReceiptId();
        return  ResponseResult.success(receiptId);
    }
    /**
     * 接口编码：40034
     * 现金收款
     * @return
     * @throws Exception
     */
    @PostMapping("getAddOilVehicles")
    public ResponseResult getAddOilVehicles(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        VehiclesDto s = orderReportService.getVehicle(accessToken);
        return ResponseResult.success(s);
    }
}
