package com.youming.youche.order.business.controller;


import com.alibaba.fastjson.JSONArray;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.dto.OrderFeeDto;
import com.youming.youche.order.vo.OrderFeeVo;
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

import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;
import java.util.Map;

/**
* <p>
* 订单费用表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-09
*/
@RestController
@RequestMapping("/order/fee")
public class OrderFeeController extends BaseController<OrderFee,IOrderFeeService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(com.youming.youche.order.business.controller.OrderFeeController.class);

    @DubboReference(version = "1.0.0")
    IOrderFeeService orderFeeService;
    @DubboReference(version = "1.0.0")
    ICreditRatingRuleService iCreditRatingRuleService;
    @DubboReference(version = "1.0.0")
    IOrderInfoHService orderInfoHService;
    @Override
    public IOrderFeeService getService() {
        return orderFeeService;
    }

    /***
     * 录入订单页面 指派自有车，调度方式为智能模式
     * @Param 主驾司机id carDriverId
     * @Param 副驾司机id copilotUserId
     * @Param 车牌号码 plateNumber
     * @Param 油单价 oilPrice 分
     * @Param 过路费 pontagePer
     * @Param 距离 distance
     * @Param 车辆编码 vehicleCode
     * @Param 线路id lineId
     * @Param 空载的运输距离 emptyDistance
     * @Param 载重油耗 loadFullOilCost
     * @Param 省份id provinceId
     * @Param 到达时限 arriveTime
     * @Param 纬度 nand
     * @Param 经度 eand
     * @Param 空载油耗 loadEmptyOilCost
     * @Param 城市 region
     * @Param 挂车记录id trailerId
     * @Param 靠台时间 dependTime
     * @Param 是否修改 isUpdate
     * @Param 靠台时间  dependTime
     * @return
     * @throws Exception
     */
    @GetMapping("/getFee")
    public ResponseResult getFee(OrderFeeDto orderFeeDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

            //主驾驶和副驾驶不能是同一人
            Long carDriverId = orderFeeDto.getCarDriverId();
            Long copilotUserId = orderFeeDto.getCopilotUserId();
            if (carDriverId != null && copilotUserId != null && carDriverId > 0 && copilotUserId > 0) {
                if (carDriverId.longValue() == copilotUserId.longValue()) {
                    return ResponseResult.success("主驾驶，副驾驶不能相同，请重新选择！");
                }
            }
            if (!StringUtils.isNotEmpty(orderFeeDto.getDependTime())) {
                return ResponseResult.success("请输入靠台时间!");
            }
            if (!StringUtils.isNotEmpty(orderFeeDto.getArriveTime()) ) {
                return ResponseResult.success("请输入到达时限!");
            }
            OrderFeeVo estimatedCosts = iCreditRatingRuleService.getEstimatedCosts(accessToken,orderFeeDto,null);
            return ResponseResult.success(estimatedCosts);
        } catch (Exception e) {
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 支付预付款(30010)
     * @param orderId
     * @param oilCardNums
     * @return
     */
    @PostMapping("/payProFee")
    public ResponseResult payProFee(@RequestParam long orderId, @RequestBody List<String> oilCardNums){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = orderFeeService.payProFee(orderId, oilCardNums, accessToken);
        return ResponseResult.success(s);
    }

    /**
     * 支付预付款--油卡余额校验[30043]
     */
    @GetMapping("verifyOilCardNum")
    public ResponseResult verifyOilCardNum(Long orderId, String oilCardNum) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<String> oilCasrdNums = new ArrayList<>();
        if (StringUtils.isNotEmpty(oilCardNum)) {
            oilCasrdNums = JSONArray.parseArray(oilCardNum, String.class);
        }
        Map map = orderFeeService.verifyOilCardNum(orderId, oilCasrdNums, false, null, accessToken);
        return ResponseResult.success(map);
    }

    /**
     * 付款审核通过 30070
     */
    @GetMapping("verifyPayPass")
    public ResponseResult verifyPayPass(String verifyDesc, String auditCode, Long orderId, String oilCardNum) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderInfoHService.verifyPayPass(orderId, auditCode, verifyDesc, StringUtils.isBlank(oilCardNum)?null:Arrays.asList(oilCardNum.split(",")),null, OrderConsts.RECIVE_TYPE.SINGLE, accessToken);
        return ResponseResult.success();
    }

    /**
     * WX接口-订单付款审核不通过 30071
     */
    @PostMapping("verifyPayFail")
    public ResponseResult verifyPayFail(String verifyDesc, String auditCode, Long orderId, Boolean load, Boolean receipt) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderInfoHService.verifyPayFail(orderId, auditCode, verifyDesc, load, receipt, accessToken);
        return ResponseResult.success();
    }

}
