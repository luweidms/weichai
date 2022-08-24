package com.youming.youche.order.api.order.other;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.dto.CancelTheOrderInDto;
import com.youming.youche.order.dto.DriverOrderOilOutDto;
import com.youming.youche.order.dto.OrderLimitFeeOutDto;
import com.youming.youche.order.dto.UpdateTheOrderInDto;
import com.youming.youche.order.dto.UpdateTheOwnCarOrderInDto;

import java.util.List;
import java.util.Map;

/**
 * 订单修改相关接口
 * @author dacheng
 *
 */
public interface IUpdateOrderService {

    /**
     * 订单撤单操作
     * @param userId 用户编号
     * @param vehicleAffiliation 资金渠道
     * @param amountFee 可用金额单位(分)
     * @param virtualOilFee 虚拟油卡金额单位(分)
     * @param entityOilFee 实体油卡金额单位(分)
     * @param ETCFee ETCFee金额单位(分)
     * @param orderId 订单编号
     * @param tenantId 订单开单租户id
     * @param isNeedBill 是否开票(0不开票，1承运方开票，2平台票)
     * @throws Exception
     */
    void cancelTheOrder(long userId, String vehicleAffiliation, long amountFee, long virtualOilFee,long entityOilFee,
                        long EtcFee, long orderId, Long tenantId, int isNeedBill) throws Exception;
    /**
     * 订单撤单操作
     * @param userId 用户编号
     * @param vehicleAffiliation 资金渠道
     * @param amountFee 可用金额单位(分)
     * @param virtualOilFee 虚拟油卡金额单位(分)
     * @param entityOilFee 实体油卡金额单位(分)
     * @param ETCFee ETCFee金额单位(分)
     * @param orderId 订单编号
     * @param tenantId 订单开单租户id
     * @param isNeedBill 是否开票(0不开票，1承运方开票，2平台票)
     * @throws Exception
     */
    void cancelTheOrder(CancelTheOrderInDto in,LoginInfo loginInfo,String token);

    /**
     * 自有车撤单
     * @param masterUserId  司机id(主驾驶)
     * @param orderId 订单编号
     * @param bridgeFee 路桥费 只记录流水不扣款
     * @param masterSubsidy 补贴 给司机增加金额（主驾驶）
     * @param slaveSubsidy 补贴 给司机增加金额（副驾驶）
     * @param entiyOilFee  实体油卡金额单位(分) 只记录流水
     * @param fictitiousOilFee  虚拟油卡金额单位(分)
     * @param slaveUserId  副驾驶司机id
     * @param tenantId 租户id
     * @param isNeedBill 是否开票 是否开票(0不开票，1承运方开票，2平台票)
     * @throws Exception
     * @return void
     */
    void cancelTheOwnCarOrder(Long masterUserId, String vehicleAffiliation, Long entiyOilFee,
                              Long fictitiousOilFee, Long bridgeFee, Long masterSubsidy, Long slaveSubsidy,
                              Long slaveUserId, Long orderId, Long tenantId, int isNeedBill,LoginInfo loginInfo,String token);
    /**
     * 修改订单订单操作
     * @param userId 用户编号
     * @param vehicleAffiliation 订单资金渠道
     * @param originalAmountFee 原订单可用金额单位(分)
     * @param updateAmountFee 修改后订单可用金额单位(分)
     * @param originalVirtualOilFee 原订单虚拟油卡金额单位(分)
     * @param updatelongVirtualOilFee 修改后订单虚拟油卡金额单位(分)
     * @param originalEntityOilFee 原订单实体油卡金额单位(分)
     * @param updateEntityOilFee 修改后订单实体油卡金额单位(分)
     * @param originalEtcFee 原订单ETCFee金额单位(分)
     * @param updateEtcFee 修改后订单ETCFee金额单位(分)
     * @param orderId 订单编号
     * @param tenantId 订单开单租户id
     * @param isNeedBill 是否开票(0不开票，1承运方开票，2平台票)
     * @param originalOilConsumer;//原油消费对象
     * @param updateOilConsumer;//修改后消费对象
     * @throws Exception
     * @return void
     */
    void updateTheOrder(UpdateTheOrderInDto inParam,LoginInfo user,String token);

    /**
     * 自有车修改订单
     * @param masterUserId 主驾驶用户编号
     * @param vehicleAffiliation 订单资金渠道
     * @param originalEntiyOilFee 原订单实体油金额，单位(分) 只记录流水
     * @param updateEntiyOilFee	  修改后订单实体油金额，单位(分) 只记录流水
     * @param originalFictitiousOilFee  原订单虚拟油金额，单位(分)
     * @param updateFictitiousOilFee	修改后订单虚拟油金额，单位(分)
     * @param originalBridgeFee  原订单路桥费，单位(分) 只记录流水
     * @param updateBridgeFee	 修改后订单路桥费，单位(分) 只记录流水
     * @param originalMasterSubsidy 原订单主驾驶补贴，单位(分)
     * @param updateMasterSubsidy  修改后订单主驾驶补贴，单位(分)
     * @param originalSlaveSubsidy 原订单副驾驶补贴，单位(分)
     * @param updateSlaveSubsidy  修改后订单副驾驶补贴，单位(分)
     * @param slaveUserId  副驾驶用户编号
     * @param orderId  订单编号
     * @param tenantId  订单开单租户id
     * @param isNeedBill 是否开票(0不开票，1承运方开票，2平台票)
     * @throws Exception
     * @return void
     */
    void updateTheOwnCarOrder(UpdateTheOwnCarOrderInDto inParam) throws Exception;
    /**
     * 查找订单已用金额
     * @param orderId 订单号
     * @param originalAmountFee 原订单预付款现金
     * @param originalVirtualOilFee 原订单预付虚拟油金额
     * @param originalEtcFee 原订单预付etc金额(如果是自有车路桥费，就传0)
     * @param tenantId 订单开单方租户id
     * @return OrderLimitFeeOut
     * @throws Exception
     */
    OrderLimitFeeOutDto getOrderLimitFeeOut (Long orderId, long originalAmountFee, long originalVirtualOilFee,
                                             long originalEtcFee, Long tenantId) ;

    /**
     * 修改订单，油账户分配
     * @param userId
     * @param orderId 订单id
     * @param tenantUserId  开单租户用户编号
     * @param tenantId 开单租户id
     * @param isNeedBill 是否开票
     * @param oilFee  虚拟邮费
     * @param sourceList
     * @return
     * @throws Exception
     */
    Map<String, Object> updateOrderAccountOil(Long userId, long orderId, Long tenantUserId, Long tenantId, int isNeedBill,
                                              Long oilFee, List<OrderOilSource> sourceList, LoginInfo user);
    /**
     * 根据orderId查询订单分配油
     * @param orderId
     * @return
     * @throws Exception
     */
    List<DriverOrderOilOutDto> queryDriverOilByOrderId(Long orderId);

}
