package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.dto.ParametersNewDto;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author liangyan
* @since 2022-03-23
*/
    public interface IOrderFundFlowService extends IBaseService<OrderFundFlow> {

    /**
     * 消费油，维修保养特殊预支
     * @param inParam
     * @param rels
     * @param user
     * @return
     */
    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user);

    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlowNew(ParametersNewDto inParam, OrderFundFlow off, LoginInfo user);

    /**
     * 到付款
     * @param inParam
     * @param rels
     * @param user
     * @return
     */
    public List dealToOrderNewCharge(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user);

    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlowNewCharge(ParametersNewDto inParam,OrderFundFlow off, LoginInfo user);

    /**
     * 未到期转已到期
     * @param inParam
     * @param rels
     * @param user
     * @return
     */
    public List dealToOrderNewCode(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user);

    }
