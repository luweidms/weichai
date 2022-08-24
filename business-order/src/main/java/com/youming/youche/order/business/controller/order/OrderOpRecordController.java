package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.domain.order.OrderOpRecord;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

    import org.springframework.web.bind.annotation.RestController;
    import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 订单变更操作记录表 前端控制器
 * </p>
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("order-op-record")
public class OrderOpRecordController extends BaseController<OrderOpRecord, IOrderOpRecordService> {

    @DubboReference(version = "1.0.0")
    private  IOrderOpRecordService iOrderOpRecordService;
    @Override
    public IOrderOpRecordService getService() {
        return iOrderOpRecordService;
    }
}
