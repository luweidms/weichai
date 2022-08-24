package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.OilExcDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.UpdateTheOrderInDto;
import com.youming.youche.order.dto.UpdateTheOwnCarOrderInDto;
import com.youming.youche.order.vo.OrderPaymentWayOilOut;
import com.youming.youche.order.vo.QueryDriverOilByOrderIdVo;

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
public interface IOrderOilSourceService extends IBaseService<OrderOilSource> {

    /**
     * 根据userId查找订单油来源关系
     * @param userType
     * @param userId
     * @return
     * @throws Exception
     */
    List<OrderOilSource> getOrderOilSourceByUserId(Long userId, Integer userType);


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
    void updateTheOwnCarOrder(UpdateTheOwnCarOrderInDto inParam, LoginInfo user,String token);


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
    void updateTheOrder(UpdateTheOrderInDto inParam, LoginInfo user,String token);


    /**
     * 查找订单油来源关系
     * @param orderId
     * @param userId
     * @return
     * @throws Exception
     */
    List<OrderOilSource> getOrderOilSourceByUserIdAndOrderId(Long userId, Long orderId,Integer userType);


    /**
     * 判断订单是否消费了油
     * @param orderId
     * @return true 消费过，false 未消费
     * @throws Exception
     */
    Boolean getOrderIsConsumeOil(Long orderId);


    /**
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    List<OrderOilSource> getOrderOilSourceByOrderId(Long orderId);



    /**
     * 根据userID、orderId、sourceOrderId查询订单油来源表
     * @param userId
     * @param orderId
     * @param sourceOrderId
     * @param tenantId
     * @param oilAccountType
     * @param oilConsumer
     * @param oilBillType
     * @return
     * @throws Exception
     */
    OrderOilSource getOrderOilSource(Long userId, Long orderId, Long sourceOrderId,String vehicleAffiliation,Long tenantId,Integer userType,
                                     Integer oilAccountType,Integer oilConsumer,Integer oilBillType,String oilAffiliation);


    /**
     * 获取订单油来源信息
     * @param userId
     * @param sourceOrderId
     * @param vehicleAffiliation
     * @param tenantId
     * @param userType
     * @return
     */
    List<OrderOilSource> getOrderOilSource(Long userId,Long sourceOrderId,
                                           String vehicleAffiliation,
                                           Long tenantId,Integer userType);




//    /**
//     * 根据订单来源租户、userId、资金渠道查找
//     */
//    List<OrderOilSource> getOrderOilSource(Long userId, String vehicleAffiliation,
//                                           Long tenantId,String oilAffiliation,Integer userType);





    /**
     * @param amount  匹配金额
     * @param userId 付款人用户id
     * @param vehicleAffiliation 资金渠道
     * @param orderId  订单号
     * @param tenantId 租户id
     * @param isNeedBill
     * @param driverUserId 收款人driverUserId
     * @param driverUserId businessId 科目大类
     * @param driverUserId subjectsId 科目小类
     * @throws Exception
     */
    List<OrderOilSource> matchOrderAccountToOrderLimit(Long amount, Long userId, Long orderId,
                                                       Long tenantId, Integer isNeedBill,
                                                       String vehicleAffiliation, Long driverUserId,
                                                       Long businessId, Long subjectsId,LoginInfo baseuser);




    /**
     * 保存订单油来源关系
     * @param userId
     * @param orderId
     * @param vehicleAffiliation
     * @param sourceOrderId
     * @param sourceAmount
     * @param noPayOil
     * @param paidOil
     * @param sourceTenantId
     * @param createDate
     * @param opId
     * @param isNeedBill
     * @param orderDate
     * @param orderDate
     * @param orderDate
     * @throws Exception
     * @return void
     */
    OrderOilSource saveOrderOilSource(Long userId, Long orderId, Long sourceOrderId,
                                      Long sourceAmount, Long noPayOil, Long paidOil,
                                      Long sourceTenantId, LocalDateTime createDate, Long opId,
                                      Integer isNeedBill, String vehicleAffiliation,
                                      LocalDateTime orderDate, String oilAffiliation, Integer oilConsumer,
                                      Long rebateOil, Long noRebateOil, Long paidRebateOil,
                                      Long creditOil, Long noCreditOil, Long paidCreditOil,
                                      Integer userType, Integer oilAccountType,
                                      Integer oilBillType, LoginInfo baseUser);



    /**
     * @param userId 用户id
     * @param billId 用户手机号
     * @param businessId 业务id
     * @param orderId 订单id
     * @param amount 费用
     * @param vehicleAffiliation 资金渠道
     * @param finalPlanDate 尾款到账日期
     */
     ParametersNewDto setParametersNew(Long userId, String billId, Long businessId,
                                             Long orderId, Long amount, String vehicleAffiliation,
                                             String finalPlanDate);


    /**
     * 业务反写订单数据
     * @param inParam
     * @param rels
     * @param user
     * @return
     */
     List busiToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user);

    /**
     * 业务反写订单数据
     * @param inParam
     * @param rels
     * @param user
     * @return
     */
     List busiToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user);

    /**
     * 根据订单查找油
     * @param orderId
     * @param userType
     * @return
     */
    List<QueryDriverOilByOrderIdVo> queryDriverOilByOrderId(Long orderId, int userType);

    /**
     * 查询司机的油账户不同模式的油费
     * @param userId 用户id
     * @param tenantId 租户id
     * @param userType 用户类型
     * @return OrderPaymentWayOilOut
     * @throws Exception
     */
    OrderPaymentWayOilOut getOrderPaymentWayOil(Long userId, String accessToken, Integer userType);

    /**
     * 获取订单油来源信息
     * @param oilExcDto
     * @return
     */
    List<OrderOilSource> getOrderOilSource(OilExcDto oilExcDto);

    /**
     * 查找充值油记录
     * @param userId
     * @param vehicleAffiliation
     * @param tenantId
     * @param oilAffiliation
     * @return
     * @throws Exception
     */
    List<RechargeOilSource> getRechargeOilSource(Long userId, Long tenantId, int userType);

    /**
     * 油未到期转可用
     * @param c  用户编号
     * @param undueAmount 未到期转可用金额 单位(分)
     * @throws Exception
     * @return void
     */
    Long payTurnCash(ConsumeOilFlow c, Long undueAmount,String token);
}
