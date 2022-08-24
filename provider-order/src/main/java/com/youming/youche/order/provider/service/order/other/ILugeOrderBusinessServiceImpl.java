package com.youming.youche.order.provider.service.order.other;

import cn.hutool.core.bean.BeanUtil;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.other.ILugeOrderBusinessService;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.dto.OrderDetailsOutDto;
import com.youming.youche.record.common.OrderConsts;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
@Deprecated
@Service
public class ILugeOrderBusinessServiceImpl implements ILugeOrderBusinessService {


    @Resource
    private IOrderInfoService orderInfoService;
    @Resource
    private IOrderInfoHService orderInfoHService;

    @Resource
    private IOrderInfoExtService orderInfoExtService;

    @Resource
    private IOrderGoodsHService orderGoodsHService;

    @Resource
    private IOrderFeeExtHService orderFeeExtHService;

    @Resource
    private IOrderFeeExtService orderFeeExtService;

    @Resource
    private IOrderFeeHService orderFeeHService;
    @Resource
    private IOrderFeeService orderFeeService;

    @Resource
    private IOrderSchedulerHService orderSchedulerHService;

    @Resource
    private IOrderSchedulerService orderSchedulerService;

    @Resource
    private IOrderGoodsService orderGoodsService;

    @Resource
    private IOrderInfoExtHService orderInfoExtHService;


    @Override
    public Map<String, Object> syncLugeOrderInfo(Long orderId, Integer waybillType, boolean isSupplementFee) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> signAgreement(Long orderId) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> syncTrackInfo(Long orderId) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> syncOrderStateInfo(Long orderId, boolean isAnewUpload) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> querySignAgreementResult(Long orderId) throws Exception {
        return null;
    }

    @Override
    public OrderDetailsOutDto getOrderAll(Long orderId) {
        OrderDetailsOutDto out = new OrderDetailsOutDto();
        OrderInfo orderInfo = orderInfoService.get(orderId);
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderGoods orderGoods = new OrderGoods();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.selectByOrderId(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单号[" + orderId + "]信息！");
            }

            OrderInfoExtH orderInfoExtH = orderInfoExtHService.selectByOrderId(orderId);
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
            OrderGoodsH orderGoodsH = orderGoodsHService.selectByOrderId(orderId);
            OrderFeeH orderFeeH = orderFeeHService.selectByOrderId(orderId);
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.selectByOrderId(orderId);
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderFeeH, orderFee);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            BeanUtil.copyProperties(orderGoodsH, orderGoods);
            BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
            BeanUtil.copyProperties(orderFeeExtH, orderFeeExt);
            isHis = true;
        } else {
            orderScheduler = orderSchedulerService.selectByOrderId(orderId);
            orderGoods = orderGoodsService.selectByOrderId(orderId);
            orderInfoExt = orderInfoExtService.selectByOrderId(orderId);
            orderFee = orderFeeService.selectByOrderId(orderId);
            orderFeeExt = orderFeeExtService.selectByOrderId(orderId);
        }
//        orderInfoSV.evict(orderInfo);
//        orderInfoSV.evict(orderInfoExt);
//        orderGoodsSV.evict(orderGoods);
//        orderFeeSV.evict(orderFeeExt);
//        orderFeeSV.evict(orderFee);
//        orderSchedulerSV.evict(orderScheduler);
        out.setIsHis(isHis ? OrderConsts.TableType.HIS : OrderConsts.TableType.ORI);
        out.setOrderFee(orderFee);
        out.setOrderInfo(orderInfo);
        out.setOrderGoods(orderGoods);
        out.setOrderInfoExt(orderInfoExt);
        out.setOrderScheduler(orderScheduler);
        out.setOrderFeeExt(orderFeeExt);
        return out;
    }

    @Override
    public Map calculateSyncFee(OrderFee preOrderFee, OrderInfo orderInfo, OrderInfoExt preOrderInfoExt, OrderScheduler orderScheduler, boolean isHis) throws Exception {
        return null;
    }

    @Override
    public void receiptResultCallBack(JSONObject inParam) throws Exception {

    }

    @Override
    public void authenticationUserResultCallBack(JSONObject inParam) throws Exception {

    }

    @Override
    public Map<String, Object> lugeCityTransform(String provinceName, String cityName, String districtName) throws Exception {
        return null;
    }

    @Override
    public Map<String, Object> orderFinality(Long orderId) throws Exception {
        return null;
    }

    @Override
    public void authenticationUserPush(JSONObject inParam) throws Exception {

    }

    @Override
    public OrderDetailsOutDto getOrderById(Long orderId) {
        OrderDetailsOutDto out = new OrderDetailsOutDto();

        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderGoods orderGoods = new OrderGoods();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.selectByOrderId(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单号[" + orderId + "]信息！");
            }

            OrderInfoExtH orderInfoExtH = orderInfoExtHService.selectByOrderId(orderId);
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
            OrderGoodsH orderGoodsH = orderGoodsHService.selectByOrderId(orderId);
            OrderFeeH orderFeeH = orderFeeHService.selectByOrderId(orderId);
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.selectByOrderId(orderId);
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderFeeH, orderFee);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            BeanUtil.copyProperties(orderGoodsH, orderGoods);
            BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
            BeanUtil.copyProperties(orderFeeExtH, orderFeeExt);
            isHis = true;
        } else {
            orderScheduler = orderSchedulerService.selectByOrderId(orderId);
            orderGoods = orderGoodsService.selectByOrderId(orderId);
            orderInfoExt = orderInfoExtService.selectByOrderId(orderId);
            orderFee = orderFeeService.selectByOrderId(orderId);
            orderFeeExt = orderFeeExtService.selectByOrderId(orderId);
        }
//        orderInfoSV.evict(orderInfo);
//        orderInfoSV.evict(orderInfoExt);
//        orderGoodsSV.evict(orderGoods);
//        orderFeeSV.evict(orderFeeExt);
//        orderFeeSV.evict(orderFee);
//        orderSchedulerSV.evict(orderScheduler);
        out.setIsHis(isHis ? OrderConsts.TableType.HIS : OrderConsts.TableType.ORI);
        out.setOrderFee(orderFee);
        out.setOrderInfo(orderInfo);
        out.setOrderGoods(orderGoods);
        out.setOrderInfoExt(orderInfoExt);
        out.setOrderScheduler(orderScheduler);
        out.setOrderFeeExt(orderFeeExt);
        return out;
    }
}
