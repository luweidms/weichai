package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerHMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.order.provider.utils.LocalDateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
@DubboService(version = "1.0.0")
@Service
public class OrderSchedulerHServiceImpl extends BaseServiceImpl<OrderSchedulerHMapper, OrderSchedulerH> implements IOrderSchedulerHService {
    @Resource
    private OrderSchedulerMapper orderSchedulerMapper;

    @Override
    public OrderSchedulerH selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderSchedulerH> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderSchedulerH::getOrderId,orderId);
        return getOne(wrapper,false);
    }

    @Override
    public OrderSchedulerH getPreOrderSchedulerHByPlateNumber(String plateNumber, LocalDateTime dependTime, Long tenandId, Long orderId) {

        if(StringUtils.isBlank(plateNumber)){
            throw new BusinessException("传入的车牌号为空");
        }
        if(tenandId==null){
            throw new BusinessException("传入的车队为空");
        }
        String dependTimeStr = "";
        if(dependTime != null){
            dependTimeStr = LocalDateTimeUtil.convertDateToString(dependTime);
        }
        List<OrderSchedulerH> preOrderSchedulerHByPlateNumber = baseMapper.getPreOrderSchedulerHByPlateNumber14(plateNumber,
                dependTimeStr, tenandId, orderId);
        if(preOrderSchedulerHByPlateNumber != null && preOrderSchedulerHByPlateNumber.size() <0){
            return preOrderSchedulerHByPlateNumber.get(0);
        }
        return null;
    }

    @Override
    public OrderSchedulerH getOrderSchedulerH(Long orderId) {
        LambdaQueryWrapper<OrderSchedulerH> lambda=Wrappers.lambdaQuery();
        lambda.eq(OrderSchedulerH::getOrderId,orderId);
        List<OrderSchedulerH> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public OrderSchedulerH getPreOrderSchedulerHByUserId(Long userId, LocalDateTime dependTime,
                                                         Long tenantId, Long orderId,
                                                         Boolean isLastOrder) {
        if(dependTime==null){
            throw new BusinessException("传入的靠台时间为空");
        }

        if(userId==null){
            throw new BusinessException("传入的用户为空");
        }
        if(tenantId==null){
            throw new BusinessException("传入的车队为空");
        }
        OrderSchedulerH orderSchedulerH = null;

        //主驾
        List returnList= orderSchedulerMapper.getPreOrderSchedulerByUserId(userId, orderId, isLastOrder, dependTime, tenantId, 1, true);
        if(returnList!=null && returnList.size()==1){
            orderSchedulerH= (OrderSchedulerH)returnList.get(0);
        //    session.evict(orderSchedulerH);
        }

        //副驾
        returnList= orderSchedulerMapper.getPreOrderSchedulerByUserId(userId, orderId, isLastOrder, dependTime, tenantId, 2, true);
        if(returnList!=null && returnList.size()==1){
            OrderSchedulerH orderScheduler1= (OrderSchedulerH)returnList.get(0);
           // session.evict(orderScheduler1);
            if (orderSchedulerH == null) {
                orderSchedulerH = orderScheduler1;
            }else{
                if (orderScheduler1.getDependTime().isBefore(orderSchedulerH.getDependTime())) {
                    orderSchedulerH = orderScheduler1;
                }
            }
        }

        //切换司机
        returnList= orderSchedulerMapper.getPreOrderSchedulerByUserId(userId, orderId, isLastOrder, dependTime, tenantId, 3, true);
        if(returnList!=null && returnList.size()==1){
            OrderSchedulerH orderScheduler1= (OrderSchedulerH)returnList.get(0);
        //    session.evict(orderScheduler1);
            if (orderSchedulerH == null) {
                orderSchedulerH = orderScheduler1;
            }else{
                if (orderScheduler1.getDependTime().isBefore(orderSchedulerH.getDependTime())) {
                    orderSchedulerH = orderScheduler1;
                }
            }
        }
        return orderSchedulerH;
    }



    @Override
    public OrderSchedulerH getPreOrderSchedulerHByTrailerPlate(String trailerPlate, Long tenantId) {
        List<OrderSchedulerH> preOrderSchedulerHByTrailerPlate = baseMapper.getPreOrderSchedulerHByTrailerPlate(trailerPlate, tenantId);
        if (preOrderSchedulerHByTrailerPlate != null && preOrderSchedulerHByTrailerPlate.size() == 0) {
            return preOrderSchedulerHByTrailerPlate.get(0);
        }
        return null;
    }

    @Override
    public OrderSchedulerH getOrderSchedulerByPlateNumber(String plateNumber) {
        OrderSchedulerH orderSchedulerH = new OrderSchedulerH();
        QueryWrapper<OrderSchedulerH> orderSchedulerQueryWrapper = new QueryWrapper<>();
        orderSchedulerQueryWrapper.eq("plate_number",plateNumber)
                .orderByDesc("create_time");
        List<OrderSchedulerH> list = this.list(orderSchedulerQueryWrapper);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return orderSchedulerH;
    }
}
