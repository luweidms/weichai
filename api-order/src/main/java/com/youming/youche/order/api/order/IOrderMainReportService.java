package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderMainReport;

/**
* <p>
    *  服务类
    * </p>
* @author liangyan
* @since 2022-03-22
*/
    public interface IOrderMainReportService extends IBaseService<OrderMainReport> {
    /**
     * 订单的上报记录
     * @param orderId
     * @return
     */
    OrderMainReport getObjectByOrderId(long orderId);

    }
