package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.order.api.order.IOrderFeeStatementHService;
import com.youming.youche.order.domain.order.OrderFeeStatementH;
import com.youming.youche.order.provider.mapper.order.OrderFeeStatementHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 订单账单信息历史表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
@DubboService(version = "1.0.0")
@Service
public class OrderFeeStatementHServiceImpl extends ServiceImpl<OrderFeeStatementHMapper, OrderFeeStatementH> implements IOrderFeeStatementHService {

    @Resource
    LoginUtils loginUtils;

    @Override
    public OrderFeeStatementH getOrderFeeStatementH(Long orderId) {
        LambdaQueryWrapper<OrderFeeStatementH> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderFeeStatementH::getOrderId,orderId);
        lambda.orderByAsc(OrderFeeStatementH::getId);
        List<OrderFeeStatementH> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return new OrderFeeStatementH();
        }
    }

    @Override
    public void saveOrderFeeStatementH(OrderFeeStatementH ofsh,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(ofsh.getOrderId()<=0){
            throw new BusinessException("添加订单账户信息是必须传入orderId");
        }
        ofsh.setUpdateOpId(loginInfo.getId());
        ofsh.setUpdateTime(LocalDateTime.now());
        ofsh.setSourceCheckAmount(0L);
        this.saveOrUpdate(ofsh);
    }
}
