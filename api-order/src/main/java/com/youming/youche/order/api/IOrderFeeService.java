package com.youming.youche.order.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.AdditionalFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.FeesDto;
import com.youming.youche.order.vo.AdvanceChargeVo;
import com.youming.youche.order.vo.CopilotMapVo;
import com.youming.youche.order.vo.PayArriveChargeVo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单费用表 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-09
 */
public interface IOrderFeeService extends IBaseService<OrderFee> {

//    OrderFeeVo getEstimatedCosts(OrderFeeDto orderFeeDto,Long subsidy);

    /**
     * 通过orderId 查询order_fee里数据
     *
     * @param orderId
     * @return
     */
    OrderFee getOrderFeeByOrderId(Long orderId);

    /**
     * 查询 该司机的 指定订单
     * @param driverId
     * @param paymentWay
     * @return
     */
    List<Long> getOrderByPaymentWay(Long driverId,Integer paymentWay);


    /**
     * 获取未支付的客户油
     * @return
     * @throws Exception
     */
     Long queryCustomOil(Long excludeOrderId,Long tenantId);

    /**
     * 获取可使用油
     */
     Long queryCustomOil(Long excludeOrderId, LoginInfo loginInfo);

    /**
     * 根据订单号查询订单扩展信息（现有表和历史表）
     * @param orderId
     * @return
     * @throws Exception
     */
    OrderFeeExt getOrderFeeExtByOrderId(Long orderId);

    /**
     * 根据订单号获取订单费用
     * @param orderId
     * @return
     */
    OrderFee getOrderFee(Long orderId);

    /**
     * 订单费用
     * @param orderId
     * @return
     */
    OrderFee selectByOrderId(Long orderId);

    /***
     * 校验油卡理论金额是否足够
     * @param orderId
     * @param oilCardNums
     * @param isPay
     * @param oilFee 校验油费
     * @return
     * @throws Exception
     */
    Map verifyOilCardNum(Long orderId, List<String> oilCardNums, boolean isPay, Long oilFee,String accessToken);

    /**
     * 校验预付款输入的油卡号
     * @param orderInfo
     * @param oilCardNums
     * @param accessToken
     */
    void verifyOilCardNum(OrderInfo orderInfo, List<String> oilCardNums, String accessToken);


    /**
     * 回单审核通过
     * @param orderId 订单号
     * @param auditCode 审核业务编码
     * @param verifyDesc 审核备注
     * @param oilCardNums 油卡列表
     * @param isFinallyNode 是否最后节点
     * @param reciveType 回单类型
     * @return
     *
     */
    String reciveVerifyPayPass(Long orderId,String verifyDesc,String receiptsStr,Integer reciveType,String accessToken);

    /**
     * 付款审核通过 30070
     * @param orderId
     * @param auditCode
     * @param verifyDesc
     * @param oilCardNums
     * @param receiptsStr
     * @param reciveType
     * @param accessToken
     */
    void verifyPayPass(Long orderId, String auditCode, String verifyDesc, List<String> oilCardNums, String receiptsStr, Integer reciveType, String accessToken);

    /**
     * 回单审核不通过
     * @param orderId 订单号
     * @param auditCode 审核业务编码
     * @param verifyDesc 审核备注
     * @param load 是否驳回合同
     * @param receipt 是否驳回回单
     * @return
     * @throws Exception
     */
    String reciveVerifyPayFail(Long orderId,String verifyDesc,boolean load, boolean receipt,String accessToken);

    /**
     * 订单录入的时候，同步支付中心
     *     需要平台开票，并且是指派车辆 外调车 的c端车辆 才需要同步
     *     其他情况后面的再同步
     *
     * @param orderInfo
     * @param orderGoods
     * @param orderScheduler
     * @param orderFee
     * @param orderFeeExt
     * @param orderInfoExt
     */
    public void synPayCenterAddOrder(OrderInfo orderInfo, OrderGoods orderGoods, OrderScheduler orderScheduler,
                                     OrderFee orderFee, OrderFeeExt orderFeeExt, OrderInfoExt orderInfoExt);

    /**
     * 同步订单至票据平台
     * @param orderInfo
     * @param orderGoods
     * @param orderScheduler
     * @param orderFee
     * @param orderFeeExt
     * @param orderInfoExt
     * @param isCalculateFee 是否计算费用
     * @param isUpdate 是否修改
     * @throws Exception
     */
    void syncBillFormOrder(OrderInfo orderInfo,OrderGoods orderGoods,OrderScheduler orderScheduler
            ,OrderFee orderFee,OrderFeeExt orderFeeExt,OrderInfoExt orderInfoExt,boolean isCalculateFee,boolean isUpdate);

    /**
     * 在线接单
     *
     * 1  当前的车辆为自有车时，判断 上一个订单是否需要平台开票，如果不需要，再判断有没有上一单并且时需要平台开票
     *    如果上一单时需要平台开票的，需要同步需要开票的租户跟车辆信息给路哥
     * 2  当前的车辆为C端车，判断当前的订单是否需要开平台票，如果需要，同步本订单的信息，车辆信息给路哥
     *    如果当前的订单不需要开平台票，判断上一个订单是否需要开平台票，如果需要，同步上一单的租户 跟车辆信息给路哥
     * 3  如果当前车辆为有归属车队的外调车，不需要进行处理，如果对方车队接单了，才需要进行同步
     * @param newOrderInfo
     * @param orderScheduler
     * @throws Exception
     */
    void synPayCenterAcceptOrder(OrderInfo newOrderInfo,OrderScheduler orderScheduler);


    /**
     * 修改订单(异常审核通过)同步支付中心
     */
     void synPayCenterUpdateOrderOrProblemInfo(OrderInfo orderInfo, OrderScheduler orderScheduler);




    /**
     * 计算费用
     * @param preOrderFee
     * @param preOrderInfoExt
     * @param orderInfo
     * @param orderScheduler
     * @return
     * @throws Exception
     */
    FeesDto calculateSyncFee(OrderFee orderFee, OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderScheduler orderScheduler);

    /**
     * 是否需要上传协议
     * @param orderId
     * @return
     * @throws Exception
     */
    Boolean isNeedUploadAgreementByLuge(Long orderId);

    /**
     * 计算自有车司机的补贴
     *  1 如果车辆没有上一单的数据，直接取线路的时限 * 会员信息里面的 按工资，按趟为0，按里程0
     *  2 如果有车辆有上一单的数据，取上一单的时间到达时间，如果有校准时效（时效罚款），那这个时间，如果都没有，拿预估达到时间
     *     本单达到时间=靠台时间+线路时限
     *     补贴天数：本单到达时间-（上一单到达时间+1），如果为负数 ，就取0
     *     如果上一单的时间跟本单的是不同年月的，按本单的单月1号计算
     *
     * @param userId
     * @param tenantId
     * @param dependTime 靠台时间
     * @param arriveTime 到达时限  单位小时
     * @param orderId 排除的订单id
     * @param isCopilot 是否主驾
     * @param transitLineSize 经停点数量
     * @return
     *        num :天数
     *        fee :补贴费用
     *        date: 补贴的具体天数
     *        subsidy:每天补贴金额
     *        salary：每月薪资

     */
    CopilotMapVo culateSubsidy(Long userId, Long tenantId, LocalDateTime dependTime, Float arriveTime, Long orderId, boolean isCopilot, Integer transitLineSize);

    /**
     * 同步轨迹
     */
    void syncOrderTrackTo56K(OrderInfo orderInfo, OrderFee orderFee,String accessToken);

    /**
     * 同步回单至开票平台
     * @param syncType OrderConsts.SYNC_TYPE
     */
    void syncBillForm(OrderInfo orderInfo,OrderFee orderFee,int syncType,String accessToken);

    /**
     * 支付预付款
     */
    String payProFee(long orderId, List<String> oilCardNums,String accessToken);

    /**
     * 设置首次支付状态
     */
    void setFirstPayFlag(OrderInfoExt orderInfoExt,OrderScheduler orderScheduler,OrderFee orderFee,OrderInfo orderInfo,OrderFeeExt orderFeeExt,String accessToken);
    /**
     * 更新订单异常费用
     *
     * @throws Exception
     */
    void updateOrderExceptionPrice(OrderProblemInfo info, Long problemDealPrice);

    /**
     * 油卡验证
     */
    void verifyOilCardNum(List<String> oilCardNums, OrderFee orderFee, OrderInfo orderInfo
            , long oilFee, OrderScheduler scheduler, String oilAffiliation, boolean isLastAudit, LoginInfo loginInfo);

    /**
     * 支付到付款
     */
    String payArriveFee(Long orderId,String accessToken);

    /**
     * 支付到付款
     * userId  用户编号
     * orderId 订单id
     * arriveFee 到付款金额单位(分)
     * problemInfos 异常罚款金额集合对象
     * agingInfos 时效罚款集合对象
     * tenantId 开单的租户id
     */
    void payArriveCharge(PayArriveChargeVo in,String accessToken);

    /**
     * 支付预付款
     *  userId  用户编号
     *  amountFee 可用金额单位(分)
     *  oilFee 油卡金额单位(分)
     *  virtualOilFee 虚拟油卡金额单位(分)
     *  ETCFee ETCFee金额单位(分)
     *  orderId 订单编号
     *  cardType 卡类型：1运势界油卡     2中石油（化）油卡 3混合油卡
     *  vehicleAffiliation 资金渠道
     *  totalFee 订单总金额
     *  tenantId 租户id
     *  isNeedBill 是否开票
     *  payType 支付方式 1普通支付方式（车队支付给车队、车队支付给司机）、2车队支付给小车队(临时车队)、3小车队支付给车司机、4车队支付给代收（代收人、司机）
     *  oilFeePrestore  油费是否预存
     *  pinganPayAcctId  平安虚拟子账号
     *  oilConsumer  油费消费类型:1自有油站，2共享油站
     */
    void payAdvanceCharge(AdvanceChargeVo advanceChargeIn,String accessToken);

    /**
     * 支付预付款处理56k附加运费
     *  orderId 订单号
     * @param tenantId 租户id
     */
    AdditionalFee deal56kAppendFreight(AdvanceChargeVo advanceChargeIn, Long tenantId, String accessToken);

    /**
     * 自有车开单要平台票
     * @param orderId 订单号
     * @param totalFee 总运费(开票金额)
     * @param userId 订单司机用户id
     * @param tenantId 开单租户id
     */
    void payOwnCarBillAmount(Long orderId,Long totalFee,Long userId,Long tenantId,long soNbr, String accessToken);

    /**
     * 自有车支付预付款
     *  masterUserId  司机id(主驾驶)
     *  orderId 订单编号
     *  bridgeFee 路桥费 只记录流水不扣款
     *  masterSubsidy 补贴 给司机增加金额（主驾驶）
     *  slaveSubsidy 补贴 给司机增加金额（副驾驶）
     *  entiyOilFee  实体油卡金额单位(分) 只记录流水
     *  fictitiousOilFee  虚拟油卡金额单位(分)
     *  slaveUserId  副驾驶司机id
     *  tenantId 租户id
     *  isNeedBill 是否开票
     *  totalFee 总运费
     *  oilFeePrestore  油费是否预存
     *  pinganPayAcctId  平安虚拟子账号
     *  oilConsumer  油费消费类型:1自有油站，2共享油站
     */
    void payAdvanceChargeToOwnCar(AdvanceChargeVo advanceChargeIn, String accessToken);


    /**
     * 重新发起同步支付中心
     * @param orderId
     * @return
     * @throws Exception
     */
    String synPayCenter(Long orderId,String accessToken);


    /**
     * 校验扣减费用是否超出尾款
     * @param orderScheduler
     * @param orderFee
     * @param orderInfoExt
     * @param price 扣减金额
     * @param agingId 时效ID
     * @param problemId 异常ID
     * @throws Exception
     */
    void verifyFeeOut(OrderScheduler orderScheduler,OrderFee orderFee,
                      OrderInfoExt orderInfoExt,Long price,Long agingId,Long problemId);

    /**
     * 撤销切换司机补贴
     * @param orderId
     */
    void cancelDriverSwitchSubsidy(Long orderId,LoginInfo loginInfo,String token);

    /**
     * 撤销切换司机油费
     * @param orderId
     */
    void cancelDriverSwitchOilFee(Long orderId,LoginInfo loginInfo,String token);


    String getXid(Long orderId);

    /**
     * WX接口-订单付款审核不通过 30071
     * @param orderId
     * @param auditCode
     * @param verifyDesc
     * @param load
     * @param receipt
     * @param accessToken
     */
    void verifyPayFail(Long orderId,String auditCode,String verifyDesc,boolean load, boolean receipt, String accessToken);
}
