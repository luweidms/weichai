package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoVerService;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoVer;
import com.youming.youche.order.provider.mapper.order.OrderTransitLineInfoVerMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
@DubboService(version = "1.0.0")
@Service
public class OrderTransitLineInfoVerServiceImpl extends BaseServiceImpl<OrderTransitLineInfoVerMapper, OrderTransitLineInfoVer> implements IOrderTransitLineInfoVerService {
    @Autowired
    private IOrderTransitLineInfoService orderTransitLineInfoService;
    @Resource
    private OrderTransitLineInfoVerMapper orderTransitLineInfoVerMapper;

    @Override
    public List<OrderTransitLineInfoVer> queryOrderTransitLineInfoByOrderId(Long orderId, Integer isUpdate) {
        LambdaQueryWrapper<OrderTransitLineInfoVer> lambda = Wrappers.lambdaQuery();
        if (orderId != null && orderId > 0) {
            lambda.eq(OrderTransitLineInfoVer::getOrderId, orderId);
        }
        if (isUpdate != null) {
            lambda.eq(OrderTransitLineInfoVer::getIsUpdate, isUpdate);
        }
        lambda.orderByAsc(OrderTransitLineInfoVer::getSortId);
        return this.list(lambda);
    }

    @Override
    public void setOrderTransitLineInfo(OrderTransitLineInfoVer transitLineInfoVer, LoginInfo baseUser) {
        OrderTransitLineInfo orderTransitLineInfo = new OrderTransitLineInfo();
        BeanUtils.copyProperties(transitLineInfoVer, orderTransitLineInfo);
        orderTransitLineInfo.setCreateTime(LocalDateTime.now());
        orderTransitLineInfo.setUpdateTime(LocalDateTime.now());
        orderTransitLineInfo.setId(null);
        orderTransitLineInfo.setOpId(baseUser.getId());
        orderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
    }

    @Override
    public String updateTransitLineInfoVerType(Long orderId, Integer isUpdateNew, Integer isUpdate) {
        if (orderId ==null || orderId <= 0) {
            throw new BusinessException("订单ID不存在!");
        }
        if (isUpdateNew ==null || isUpdateNew < 0) {
            throw new BusinessException("修改新状态不存在!");
        }
        if (isUpdate ==null || isUpdate < 0) {
            throw new BusinessException("修改状态不存在!");
        }

        OrderTransitLineInfoVer orderTransitLineInfoVer = new OrderTransitLineInfoVer();
        orderTransitLineInfoVer.setIsUpdate(isUpdateNew);
        UpdateWrapper<OrderTransitLineInfoVer> orderTransitLineInfoVerUpdateWrapper = new UpdateWrapper<>();
        orderTransitLineInfoVerUpdateWrapper.eq("order_id",orderId)
                .eq("is_update",isUpdate);
        orderTransitLineInfoVerMapper.update(orderTransitLineInfoVer,orderTransitLineInfoVerUpdateWrapper);
        return "Y";
    }
}
