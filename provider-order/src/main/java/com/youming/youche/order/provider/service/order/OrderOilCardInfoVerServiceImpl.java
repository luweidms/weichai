package com.youming.youche.order.provider.service.order;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.order.IOrderOilCardInfoVerService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderOilCardInfoVer;
import com.youming.youche.order.provider.mapper.order.OrderOilCardInfoVerMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class OrderOilCardInfoVerServiceImpl extends BaseServiceImpl<OrderOilCardInfoVerMapper, OrderOilCardInfoVer> implements IOrderOilCardInfoVerService {

    @Resource
    private OrderOilCardInfoVerMapper orderOilCardInfoVerMapper;
    @Resource
    private IOilCardManagementService oilCardManagementService;

    @Override
    public void releaseOilCardBalance(long orderId,Long tenantId) {

        if (orderId > 0) {

            List<OrderOilCardInfoVer> list = this.queryOrderOilCardInfoVerByOrderId(orderId, null, OrderConsts.OIL_CARD_UPDATE_STATE.AWAIT_UPDATE);
            if (list != null && list.size() > 0) {
                for (OrderOilCardInfoVer orderOilCardInfoVer : list) {
                    if (orderOilCardInfoVer.getCardType() != null && orderOilCardInfoVer.getCardType() != SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                        oilCardManagementService.pledgeOilCardBalance(orderOilCardInfoVer.getOilCardNum(), orderOilCardInfoVer.getOilFee(),
                                tenantId, null, true);
                    }
                    //还原油卡抵押
                    orderOilCardInfoVer.setUpdateState(OrderConsts.OIL_CARD_UPDATE_STATE.RESTORE);
                    this.saveOrUpdate(orderOilCardInfoVer);
                }
            }
        }

    }

    @Override
    public List<OrderOilCardInfoVer> queryOrderOilCardInfoVerByOrderId(Long orderId, String oilCardNum, Integer updateState) {
        LambdaQueryWrapper<OrderOilCardInfoVer> lambda= Wrappers.lambdaQuery();
        if (orderId > 0) {
            lambda.eq(OrderOilCardInfoVer::getOrderId, orderId);
        }
        if (StringUtils.isNotBlank(oilCardNum)) {
            lambda.eq(OrderOilCardInfoVer::getOilCardNum, oilCardNum);
        }
        if (updateState != null && updateState >= 0) {
            lambda.eq(OrderOilCardInfoVer::getUpdateState, updateState);
        }
        return this.list(lambda);
    }
}
