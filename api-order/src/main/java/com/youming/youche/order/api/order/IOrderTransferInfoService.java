package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderTransferInfo;
import com.youming.youche.order.dto.OrderDetailsOutDto;
import com.youming.youche.order.dto.OrderTransferInfoDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IOrderTransferInfoService extends IBaseService<OrderTransferInfo> {

    /**
     * 根据订单查询接单信息
     *
     * @param state    接单状态
     * @param tenantId 租户ID
     * @param orderId  订单号
     * @return
     */
    List<OrderTransferInfo> queryTransferInfoList(Integer state, Long tenantId, Long orderId);


    /**
     * 同步车辆的信息 1 上一单的指派类型为指派车队，并 当前车队指派为车辆类型并且是自有车,C端车的情况，需要同步上一单的订单车辆信息
     * 2上一单的指派为车辆类型，并且车辆类型是外调车，需要把车辆信息同步到上上一单的车辆信息
     *
     * @param orderId
     * @param orderScheduler
     * @throws Exception
     */
    void syncOrderVehicleInfo(Long orderId, OrderInfo orderInfo, OrderScheduler orderScheduler);


    /**
     * 反写转单中间表车牌号
     *
     * @param fromOrderId
     * @param plateNumber
     * @throws Exception
     */
    void updateOrderTransferPlateNumber(Long fromOrderId, String plateNumber, LoginInfo loginInfo);

//    OrderTransferInfo getOrderTransferInfo(Long orderId, Long tenantId, Integer... transferOrderState);
    OrderDetailsOutDto getOrderAll(Long orderId);

    /**
     * 订单保存的时候，保存转单中间表的数据
     *
     * @param orderScheduler
     * @param orderInfo
     * @param orderGoods
     * @param tenantId
     * @throws Exception
     */
     void saveOrderTransferInfo(OrderScheduler orderScheduler, OrderInfo orderInfo, OrderGoods orderGoods,
                                       Long tenantId,OrderFee orderfee);



    /**
     * 临时车队系统自动接单接口
     * @param orderInfo 转单订单
     * @param orderGoods
     * @param orderInfoExt
     * @param orderScheduler
     * @param orderFee
     * @param orderFeeExt
     * @param depotSchemes
     * @param remark
     * @param costPaymentDaysInfo 成本账期
     * @return
     * @throws Exception
     */
    Long acceptOrderTemp(OrderInfo orderInfo, OrderGoods orderGoods,
                         OrderInfoExt orderInfoExt, OrderScheduler orderScheduler,
                         OrderFee orderFee, OrderFeeExt orderFeeExt,
                         OrderPaymentDaysInfo costPaymentDaysInfo,LoginInfo user,
                         String accessToken);

    /**
     * 催单
     *
     * @param orderId
     *            催单的订单
     *
     *
     */
     OrderTransferInfo reminder(Long orderId ,String accessToken) ;

    /**
     *
     * @param orderId
     * @param transferTenantTenantId   转单租户
     * @param transferOrderState
     * @return
     */
     OrderTransferInfo getOrderTransferInfoBytransferTenant(Long orderId, Long transferTenantTenantId,Integer...transferOrderState ) ;

    /**
     * 查询转单时间 小于传入的时间，并且状态等于传入的状态的数据
     * @param tranDate
     * @param state
     * @param count
     * @return
     */
    List<OrderTransferInfo> queryTransferInfo(LocalDateTime tranDate, Integer state, Integer count);

    /**
     * 转单后，超时的订单
     *
     * @param orderId
     * @param tenantId
     * @param remark
     */
    void timeOutOrder(LoginInfo loginInfo,Long orderId, Long tenantId, String remark);

    /**
     * 订单在线接单信息
     * @param orderId
     * @param tenantId  接单租户的id
     * @param transferOrderState
     * @return
     */
    OrderTransferInfo getOrderTransferInfo(Long orderId,Long tenantId,Integer... transferOrderState);

    /**
     * 在线接单--列表查询（30034）
     * @param orderTransferInfoDto
     * @return
     */
    Page<OrderTransferInfoDto> queryOrderTransferInfoList(OrderTransferInfoDto orderTransferInfoDto,Integer pageNum,Integer pageSize,String accessToken);

}
