package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;
import com.youming.youche.order.provider.mapper.order.OrderTransitLineInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 订单途径表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@DubboService(version = "1.0.0")
@Service
public class OrderTransitLineInfoServiceImpl extends BaseServiceImpl<OrderTransitLineInfoMapper, OrderTransitLineInfo> implements IOrderTransitLineInfoService {


    @Override
    public List<OrderTransitLineInfo> queryOrderTransitLineInfoByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderTransitLineInfo> lambda=new QueryWrapper<OrderTransitLineInfo>().lambda();
        lambda.eq(OrderTransitLineInfo::getOrderId,orderId)
                .orderByAsc(OrderTransitLineInfo::getSortId);
        return this.list(lambda);
    }

    @Override
    public String deleteOrderTransitLineInfo(Long orderId) {
        if (orderId ==null || orderId <= 0) {
            throw new BusinessException("订单ID不存在!");
        }
        LambdaQueryWrapper<OrderTransitLineInfo> lambda=new QueryWrapper<OrderTransitLineInfo>().lambda();
        lambda.eq(OrderTransitLineInfo::getOrderId,orderId);
        this.remove(lambda);
        return "Y";
    }

    @Override
    public OrderTransitLineInfo queryTransitLineInfoByLocation(Long orderId, Integer sortId, String eand, String nand) {
       LambdaQueryWrapper<OrderTransitLineInfo> lambda= Wrappers.lambdaQuery();
        if (orderId ==null || orderId <= 0) {
            throw new BusinessException("订单ID不存在!");
        }
        lambda.eq(OrderTransitLineInfo::getOrderId, orderId);
        if (sortId != null && sortId > 0) {
            lambda.eq(OrderTransitLineInfo::getSortId, sortId);
        }
        if (StringUtils.isNoneBlank(eand)) {
            lambda.eq(OrderTransitLineInfo::getEand, eand);
        }
        if (StringUtils.isNoneBlank(nand)) {
            lambda.eq(OrderTransitLineInfo::getNand, nand);
        }
        List<OrderTransitLineInfo> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public OrderTransitLineInfo queryTrackTransitLineInfo(Long orderId, Integer trackType) {
        List<OrderTransitLineInfo> list = this.queryOrderTransitLineInfoByOrderId(orderId);
        if (list != null && list.size() > 0 && trackType != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                OrderTransitLineInfo transitLineInfo = list.get(i);
                if (trackType.intValue() == OrderConsts.TRACK_TYPE.TYPE2) {//离台经停点
                    if (transitLineInfo.getCarDependDate() != null && transitLineInfo.getCarStartDate() == null) {
                        return transitLineInfo;
                    }
                }
                if (trackType.intValue() == OrderConsts.TRACK_TYPE.TYPE1) {//靠台经停点
                    if (transitLineInfo.getCarDependDate() != null && i != list.size() - 1) {
                        OrderTransitLineInfo lastTransitLineInfo = list.get(i + 1);
                        if (lastTransitLineInfo.getCarDependDate() == null) {
                            return lastTransitLineInfo;
                        }
                    } else if (transitLineInfo.getCarDependDate() != null && i == list.size() - 1) {
                        return null;
                    } else if (transitLineInfo.getCarDependDate() == null) {
                        if (i == 0) {
                            return transitLineInfo;
                        } else {
                            OrderTransitLineInfo lastTransitLineInfo = list.get(i - 1);
                            if (lastTransitLineInfo.getCarDependDate() != null) {
                                return transitLineInfo;
                            }
                        }
                    }
                }
            }
            return null;
        } else {
            return null;
        }
    }

}
