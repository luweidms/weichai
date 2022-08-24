package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.finance.api.order.IOrderBillInfoService;
import com.youming.youche.order.api.order.IOrderFeeStatementHService;
import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.domain.order.OrderFeeStatement;
import com.youming.youche.order.domain.order.OrderFeeStatementH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.provider.mapper.order.OrderFeeStatementMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 订单账单信息表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
@DubboService(version = "1.0.0")
@Service
public class OrderFeeStatementServiceImpl extends BaseServiceImpl<OrderFeeStatementMapper, OrderFeeStatement> implements IOrderFeeStatementService {
    @Autowired
    private IOrderFeeStatementHService orderFeeStatementHService;


    @Resource
    IOrderFeeStatementService iOrderFeeStatementService;

    @Resource
    IOrderFeeStatementHService iOrderFeeStatementHService;
    @Lazy
    @Resource
    IOrderInfoService iOrderInfoService;

    @Resource
    IOrderInfoHService iOrderInfoHService;

    @DubboReference(version = "1.0.0")
    IOrderBillInfoService iOrderBillInfoService;

    @Override
    public OrderFeeStatement getOrderFeeStatementById(Long orderId) {
        if(orderId == null){
            throw new BusinessException("查询账单时，传入订单号为空！");
        }
        OrderFeeStatement orderFeeStatement = new OrderFeeStatement();
        LambdaQueryWrapper<OrderFeeStatement> lambda=new QueryWrapper<OrderFeeStatement>().lambda();
        lambda.eq(OrderFeeStatement::getOrderId,orderId);
        List<OrderFeeStatement> list = this.list(lambda);
        if(list == null || list.size()==0){
            LambdaQueryWrapper<OrderFeeStatementH> lambdaOne=new QueryWrapper<OrderFeeStatementH>().lambda();
            lambdaOne.eq(OrderFeeStatementH::getOrderId,orderId);
            List<OrderFeeStatementH> feeStatementHS = orderFeeStatementHService.list(lambdaOne);
            if(feeStatementHS != null && feeStatementHS.size()>0){
                OrderFeeStatementH orderFeeStatementH = feeStatementHS.get(0);
                BeanUtils.copyProperties(orderFeeStatementH,orderFeeStatement);
            }

        }else{
            orderFeeStatement = list.get(0);
        }
        return orderFeeStatement;
    }

    @Override
    public void saveOrderFeeStatement(OrderFeeStatement ofs, LoginInfo user) {
        if(ofs.getOrderId()<=0){
            throw new BusinessException("添加订单账户信息是必须传入orderId");
        }
        ofs.setOpId(user.getId());
        ofs.setCreateTime(LocalDateTime.now());
        ofs.setSourceCheckAmount(0L);
        ofs.setTenantId(user.getTenantId());
        this.saveOrUpdate(ofs);
    }

    @Override
    public void updateReceiveSettleDueDate(Long orderId, LocalDateTime receiveSettleDueDate) {
        if(orderId<=0){
            throw new BusinessException("修改订单账户信息是必须传入orderId");
        }

        OrderFeeStatement ofs =this.getOrderFeeStatement(orderId);

        if(ofs==null){
            OrderFeeStatementH ofsH = orderFeeStatementHService.getOrderFeeStatementH(orderId);
            if(ofsH==null){
                throw new BusinessException("根据订单编号查询不到订单账户信息");
            }
            ofsH.setReceiveSettleDueDate(receiveSettleDueDate);
            orderFeeStatementHService.saveOrUpdate(ofsH);
        }else{
            ofs.setReceiveSettleDueDate(receiveSettleDueDate);
            if (ofs.getId() != null){
                this.updateById(ofs);
            }
        }
    }

    @Override
    public OrderFeeStatement getOrderFeeStatement(Long orderId) {
        LambdaQueryWrapper<OrderFeeStatement> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderFeeStatement::getOrderId,orderId);
        lambda.orderByAsc(OrderFeeStatement::getId);
        List<OrderFeeStatement> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return new OrderFeeStatement();
        }
    }

    @Override
    public void updatePaySettleDueDate(Long orderId, LocalDateTime paySettleDueDate) {
        if(orderId<=0){
            throw new BusinessException("修改订单账户信息是必须传入orderId");
        }
        OrderFeeStatement ofs =this.getOrderFeeStatement(orderId);

        if(ofs==null){
            OrderFeeStatementH ofsH = orderFeeStatementHService.getOrderFeeStatementH(orderId);
            if(ofsH==null){
                throw new BusinessException("根据订单编号查询不到订单账户信息");
            }
            ofsH.setPaySettleDueDate(paySettleDueDate);
            orderFeeStatementHService.saveOrUpdate(ofsH);
        }else{
            ofs.setPaySettleDueDate(paySettleDueDate);
            if(ofs.getId() != null){
                this.updateById(ofs);
            }
        }
    }

    @Override
    public void moveOrderFeeStatementToHistory(Long orderId,String accessToken) {
        if(orderId<=0){
            throw new BusinessException("将订单账单信息移到历史表时必须传入orderId");
        }
        OrderFeeStatement orderFeeStatement = this.getOrderFeeStatement(orderId);
        OrderFeeStatementH orderFeeStatementH = new OrderFeeStatementH();
        BeanUtils.copyProperties(orderFeeStatement,orderFeeStatementH);
        if(orderFeeStatement.getId() != null){
            this.removeById(orderFeeStatement.getId());
            orderFeeStatementHService.saveOrderFeeStatementH(orderFeeStatementH,accessToken);
        }
    }

    @Override
    public void checkOrderAmountByProcess(Long orderId, Long amount, String accessToken) {
        //查找该订单对应的下一单
        boolean isExistToOrder = true;
        OrderInfo oi = iOrderInfoService.getOrder(orderId);
        OrderInfoH oiH = iOrderInfoHService.getOrderH(orderId);
        if (oi != null && oi.getToOrderId() != null && oi.getToOrderId() > 0) {
            orderId = oi.getToOrderId();
        } else if (oiH != null && oiH.getToOrderId() != null && oiH.getToOrderId() > 0) {
            orderId = oiH.getToOrderId();
        } else {
            isExistToOrder = false;
        }
        if (isExistToOrder) {

            OrderFeeStatement orderFeeStatement = this.getOrderFeeStatement(orderId);

            String billNumber = "";
            if (orderFeeStatement != null) {
                orderFeeStatement.setFinanceDate(LocalDateTime.now());
                orderFeeStatement.setSourceCheckAmount(orderFeeStatement.getSourceCheckAmount() + amount);

                orderFeeStatement.setGetAmount(orderFeeStatement.getGetAmount() + amount);
                if (orderFeeStatement.getGetAmount() >= orderFeeStatement.getConfirmAmount()) {
                    //已核销
                    orderFeeStatement.setFinanceSts(EnumConsts.FINANCE_STS.FINANCE_STS_1);
                    orderFeeStatement.setGetAmount(orderFeeStatement.getConfirmAmount());
                    orderFeeStatement.setRealIncome(orderFeeStatement.getConfirmAmount());
                } else {
                    //部分核销
                    orderFeeStatement.setFinanceSts(EnumConsts.FINANCE_STS.FINANCE_STS_2);
                }
                this.saveOrUpdate(orderFeeStatement);
                billNumber = orderFeeStatement.getBillNumber();
            } else {
                OrderFeeStatementH orderFeeStatementH = orderFeeStatementHService.getOrderFeeStatementH(orderId);
                if (orderFeeStatementH != null) {
                    orderFeeStatementH.setFinanceDate(LocalDateTime.now());
                    orderFeeStatementH.setSourceCheckAmount(orderFeeStatementH.getSourceCheckAmount() + amount);
                    orderFeeStatementH.setGetAmount(orderFeeStatementH.getGetAmount() + amount);
                    if (orderFeeStatementH.getGetAmount() >= orderFeeStatementH.getConfirmAmount()) {
                        //已核销
                        orderFeeStatementH.setFinanceSts(EnumConsts.FINANCE_STS.FINANCE_STS_1);
                        orderFeeStatementH.setGetAmount(orderFeeStatementH.getConfirmAmount());
                        orderFeeStatementH.setRealIncome(orderFeeStatementH.getConfirmAmount());
                    } else {
                        //部分核销
                        orderFeeStatementH.setFinanceSts(EnumConsts.FINANCE_STS.FINANCE_STS_2);
                    }
                    orderFeeStatementHService.saveOrUpdate(orderFeeStatementH);
                    billNumber = orderFeeStatementH.getBillNumber();
                }
            }

            if (StringUtils.isNotBlank(billNumber)) {
                //iOrderBillInfoService.saveChecksByProcess(orderId, amount, billNumber, accessToken);
            }
        }
    }

    @Override
    public void checkOrderAmountByProcess(Long orderId, Long amount) {

    }

}
