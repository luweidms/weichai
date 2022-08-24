package com.youming.youche.order.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.ITurnCashOrderService;
import com.youming.youche.order.domain.TurnCashOrder;
import com.youming.youche.order.dto.order.TurnCashDto;
import com.youming.youche.order.provider.mapper.TurnCashOrderMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import org.apache.dubbo.config.annotation.DubboService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author wuhao
* @since 2022-04-25
*/
@DubboService(version = "1.0.0")
    public class TurnCashOrderServiceImpl extends BaseServiceImpl<TurnCashOrderMapper, TurnCashOrder> implements ITurnCashOrderService {


    @Override
    public TurnCashOrder createTurnCashOrder(Long userId, Long orderId, Long batchId, Long balance, Long oilBalance, Long etcBalance, Long orderOilBalance, Long consumeOrderBalance, Long canTurnBalance, Long turnDiscount, String vehicleAffiliation, String turnMonth, String oilCardNumber, String turnType, Long tenantId, int userType) {
        TurnCashOrder turnCashOrder = new TurnCashOrder();
        turnCashOrder.setBatchId(batchId);
        turnCashOrder.setOrderId(orderId);
        turnCashOrder.setUserId(userId);
        //会员体系开始
        turnCashOrder.setUserType(userType);
        //会员体系结束
        turnCashOrder.setBalance(balance);
        turnCashOrder.setOilBalance(oilBalance);
        turnCashOrder.setEtcBalance(etcBalance);
        turnCashOrder.setOrderOilBalance(orderOilBalance);
        turnCashOrder.setConsumeOrderBalance(consumeOrderBalance);
        turnCashOrder.setCanTurnBalance(canTurnBalance);
        turnCashOrder.setTurnDiscount(turnDiscount);
        turnCashOrder.setVehicleAffiliation(vehicleAffiliation);
        turnCashOrder.setTurnMonth(turnMonth);
        turnCashOrder.setOilCardNumber(oilCardNumber);
        turnCashOrder.setTurnType(Integer.parseInt(turnType));
        turnCashOrder.setCreateTime(LocalDateTime.now());
        turnCashOrder.setTenantId(tenantId);
        return turnCashOrder;
    }

    @Override
    public List<TurnCashDto> doQueryEtcCashover(Long orderId, Long tenantId) {
        LambdaQueryWrapper<TurnCashOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TurnCashOrder::getOrderId, orderId);
        queryWrapper.eq(TurnCashOrder::getTurnType, Integer.valueOf(EnumConsts.TURN_CASH.TURN_TYPE2));

        // 查询订单转现记录
        List<TurnCashOrder> turnCashOrderList = baseMapper.selectList(queryWrapper);
        List<TurnCashDto> turnCashOutList = new ArrayList<>();
        if(turnCashOrderList!= null && turnCashOrderList.size() > 0){
            for(TurnCashOrder turnCashOrder:turnCashOrderList){
                TurnCashDto turnCashOut = new TurnCashDto();
                BeanUtil.copyProperties(turnCashOrder, turnCashOut);
                turnCashOut.setCreateDate(turnCashOrder.getCreateTime());
                turnCashOut.setTurnDiscountDouble(CommonUtil.getDoubleFormatLong(turnCashOrder.getTurnDiscount(), 2));
                turnCashOut.setTurnBalanceDouble(CommonUtil.getDoubleFormatLongMoney(turnCashOrder.getTurnBalance(), 2));
                turnCashOutList.add(turnCashOut);
            }
        }
        return turnCashOutList;
    }

    @Override
    public List<TurnCashDto> doQueryOilTransfer(Long orderId, Long tenantId) {
        LambdaQueryWrapper<TurnCashOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TurnCashOrder::getOrderId, orderId);
        queryWrapper.eq(TurnCashOrder::getTurnType, Integer.valueOf(EnumConsts.TURN_CASH.TURN_TYPE1));

        List<TurnCashOrder> turnCashOrderList = baseMapper.selectList(queryWrapper);
        List<TurnCashDto> turnCashOutList = new ArrayList<>();
        if(turnCashOrderList!= null && turnCashOrderList.size() > 0){
            for(TurnCashOrder turnCashOrder:turnCashOrderList){
                TurnCashDto turnCashOut = new TurnCashDto();
                BeanUtil.copyProperties(turnCashOrder, turnCashOut);
                turnCashOut.setCreateDate(turnCashOrder.getCreateTime());
                turnCashOut.setTurnDiscountDouble(CommonUtil.getDoubleFormatLong(turnCashOrder.getTurnDiscount(), 2));
                turnCashOut.setTurnBalanceDouble(CommonUtil.getDoubleFormatLongMoney(turnCashOrder.getTurnBalance(), 2));
                turnCashOutList.add(turnCashOut);
            }
        }
        return turnCashOutList;
    }
}
