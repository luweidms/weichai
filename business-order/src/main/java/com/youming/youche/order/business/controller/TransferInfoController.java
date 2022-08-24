package com.youming.youche.order.business.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.annotation.Dict;
import com.youming.youche.order.api.ITransferInfoService;
import com.youming.youche.order.domain.TransferInfo;
import com.youming.youche.order.dto.OrderTransferInfoDetailDto;
import com.youming.youche.order.dto.ReceiveOrderDto;
import com.youming.youche.order.dto.TransferInfoDto;
import com.youming.youche.order.vo.AcceptOrderVo;
import com.youming.youche.order.vo.OrderDetailsTransferVo;
import com.youming.youche.order.vo.SaveOrderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-14
*/
@RestController
@RequestMapping("transfer/info")
@Slf4j
public class TransferInfoController extends BaseController<TransferInfo, ITransferInfoService> {

    @DubboReference(version = "1.0.0")
    ITransferInfoService iTransferInfoService;

    @Override
    public ITransferInfoService getService() {
        return iTransferInfoService;
    }

    /***
     * 在线接单页面 在线接单查询列表接口
     * @Param 车队名称 transferTenantName
     * @Param 车牌号码 plateNumber
     * @Param 原订单号 orderId
     * @Param 新订单号 transferOrderId
     * @Param 转单状态 transferOrderState 转单状态(0待接单 1 已接单 2 已拒接 3 已超时)
     * @Param 转单时间起止时间 beginTranDate
     * @Param 转单时间终止时间 endTranDate
     * @Param 接单时间起止时间 beginAcceptDate
     * @Param 接单时间终止时间 endAcceptDate
     * @return
     * @throws Exception
     */
    @GetMapping("/getTransferInfoList")
    public ResponseResult getTransferInfoList( TransferInfoDto transferInfoDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<TransferInfo> page = new Page<>(transferInfoDto.getPageNum(),transferInfoDto.getPageSize());
            Page<TransferInfo> transferInfoList = iTransferInfoService.getTransferInfoList(page,transferInfoDto, accessToken);
            return ResponseResult.success(transferInfoList);
        } catch (Exception e) {
            return ResponseResult.failure("查询异常");
        }
    }
    /***
     * 在线接单页面 接单详情接口
     * @param orderId 原订单号
     * @return
     */
    @GetMapping("/queryReceiveOrderDetail")
    @Dict
    public ResponseResult queryReceiveOrderDetail(@RequestParam String orderId) {
        //为什么都是控制器try ？？没有全局处理？先跟着写吧
        try {
            Optional.ofNullable(orderId).orElseThrow(()->new IllegalArgumentException("请输入订单orderId !"));
            String authorization = Optional.ofNullable(Header.getAuthorization(request.getHeader("Authorization"))).orElseThrow(() -> new IllegalArgumentException("缺少Authorization，非法访问!"));
            OrderDetailsTransferVo orderDetailsTransferVo = iTransferInfoService.queryReceiveOrderDetail(orderId, authorization);
            return ResponseResult.success(orderDetailsTransferVo);
        } catch (Exception e) {
            log.error("接单详情接口查询异常{}",e.getMessage());
            return ResponseResult.failure("查询订单详情异常,联系管理员处理！"+e.getMessage());
        }
    }
    /***
     * 在线接单页面 接单接口
     * @param receiveOrderDto
     * @return
     */
    @PostMapping("/receiveOrder")
    public ResponseResult receiveOrder(@RequestBody ReceiveOrderDto receiveOrderDto) {
        try {
            String authorization = Optional.ofNullable(Header.getAuthorization(request.getHeader("Authorization"))).orElseThrow(() -> new IllegalArgumentException("缺少Authorization，非法访问!"));
            iTransferInfoService.receiveOrder(receiveOrderDto,authorization);
            return ResponseResult.success("接单成功!");
        } catch (Exception e) {
            log.error("接单异常{}",e.getMessage());
            //TODO 这里暂时将异常信息抛到前端,方便调试，调试完成去掉
            return ResponseResult.failure("接单失败,联系管理员处理！"+e.getMessage());
        }
    }

    /**
     * 在线接单(30030)
     * @param acceptOrderVo
     * @return
     */
    @PostMapping("/acceptOrderWx")
    public ResponseResult acceptOrderWx(@RequestBody AcceptOrderVo acceptOrderVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long aLong = null;
        try {
            aLong = iTransferInfoService.acceptOrderWx(acceptOrderVo,accessToken);
        } catch (Exception e) {
            log.error("接单异常{}",e.getMessage());
            return ResponseResult.failure("接单失败,联系管理员处理！"+e.getMessage());
        }
        return ResponseResult.success(aLong);
    }

    /**
     * 在线接单的详情页面(30032)
     * @return
     */
    @GetMapping("/OrderTransferInfoDetail")
    public ResponseResult OrderTransferInfoDetail(Long orderId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderTransferInfoDetailDto orderTransferInfoDetailDto = iTransferInfoService.OrderTransferInfoDetail(orderId,accessToken);
        return ResponseResult.success(orderTransferInfoDetailDto);
    }


    /**
     * 快速开单（30033）
     * @param saveOrderVo
     * @return
     */
    @PostMapping("/saveOrder")
    public ResponseResult saveOrder(@RequestBody SaveOrderVo saveOrderVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s= iTransferInfoService.saveOrder(saveOrderVo, accessToken);
        return ResponseResult.success(s);
    }
}
