package com.youming.youche.order.api.order;



import com.youming.youche.order.dto.order.IntelligentMatchCustomerDto;
import com.youming.youche.order.dto.order.OilBalanceForOilAccountDto;
import com.youming.youche.order.dto.order.QueryOrderTitleBasicsInfoDto;

import java.util.List;
import java.util.Map;

public interface IOrderStatementService {
    /**
     * 获取可使用的客户油（区分共享和自有油站）
     * 油账户减去未支付的客户油
     * @return
     * @throws Exception
     */
    OilBalanceForOilAccountDto querOilBalanceForOilAccountType(String accessToken) ;

    /**
     * 智能匹配客户(线路)信息
     * @param isCustomer
     * @return
     * @throws Exception
     */
    List<IntelligentMatchCustomerDto> queryIntelligentMatchCustomer(boolean isCustomer, String accessToken) throws Exception;

    /**
     * 查询司机在途订单中使用的油站
     * @param userId 司机ID
     * @param oilIds 油站ID集合
     * @return map key:oilId    value:orderId
     * @throws Exception
     */
    Map<String,Long> queryOrderOilEnRouteUse(Long userId,List<Long> oilIds);

    /**
     * 获取订单基础标题信息
     * @param orderId
     */
    QueryOrderTitleBasicsInfoDto queryOrderTitleBasicsInfo(Long orderId);
}