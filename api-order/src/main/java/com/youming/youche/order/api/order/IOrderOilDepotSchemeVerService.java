package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OrderOilDepotSchemeVer;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface IOrderOilDepotSchemeVerService extends IBaseService<OrderOilDepotSchemeVer> {


    /**
     * 根据订单查询油站分配版本(订单)
     * @param orderId
     * @return
     */
    List<OrderOilDepotSchemeVer> getOrderOilDepotSchemeVerByOrderId(Long orderId, Boolean isUpdate, LoginInfo user);


    /**
     * 保存油站分配版本
     * @param schemeVer
     * @throws Exception
     */
    void saveOrderOilDepotSchemeVer(OrderOilDepotSchemeVer schemeVer);

    /**
     * 修改分配方案修改状态
     * @param orderId
     * @param isUpdate
     * @throws Exception
     */
    void updateSchemeVerIsUpdate(Long orderId,int isUpdate,LoginInfo user);

}
