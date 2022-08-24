package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.BaseBillInfo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-24
 */
public interface IBaseBillInfoService extends IBaseService<BaseBillInfo> {


    /**
     * 根据订单号查询订单票据初始化信息
     * @param orderId
     * @return
     * @throws Exception
     */
    List<BaseBillInfo> getBaseBillInfo(Long orderId);

}
