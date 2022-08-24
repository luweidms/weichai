package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.order.domain.order.OrderOpRecord;

/**
 * <p>
 * 订单变更操作记录表 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IOrderOpRecordService extends IBaseService<OrderOpRecord> {

    void saveOrUpdate(Long orderId, int opType,String accessToken);

}
