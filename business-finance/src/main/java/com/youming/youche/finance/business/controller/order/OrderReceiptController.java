package com.youming.youche.finance.business.controller.order;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.finance.api.order.IOrderReceiptService;
import com.youming.youche.finance.domain.order.OrderReceipt;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单-回单
 *
 * @author hzx
 * @date 2022/2/18 11:01
 */
@RestController
@RequestMapping("order/receipt")
public class OrderReceiptController extends BaseController<OrderReceipt, IOrderReceiptService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInfoController.class);

    @DubboReference(version = "1.0.0")
    private IOrderReceiptService iOrderReceiptService;

    @Override
    public IOrderReceiptService getService() {
        return iOrderReceiptService;
    }

    /**
     * 获取订单回单
     */
    @PostMapping("findOrderRecive")
    public ResponseResult findOrderRecive(@RequestParam("orderId") Long orderId,
                                          @RequestParam("optType") Long optType) { //optType==2查看
        try {
            if (orderId == null || orderId <= 0) {
                throw new BusinessException("缺少订单号！");
            }
            List<OrderReceipt> orderReceipts = iOrderReceiptService.findOrderReceipts(orderId, optType);
            return ResponseResult.success(orderReceipts);
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }
}
