package com.youming.youche.order.api.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.AcOrderSubsidyInDto;
import com.youming.youche.order.dto.OilExcDto;
import com.youming.youche.order.dto.OrderLimitDto;
import com.youming.youche.order.vo.AdvanceExpireOutVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单付款回显表 服务类
 * </p>
  origin/feature/order
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IOrderLimitService extends IBaseService<OrderLimit> {


    /**
     * 根据userId和orderId查询
     * @param userId
     * @param orderId
     * @param userType
     * @return
     * @throws Exception
     */
    OrderLimit getOrderLimitByUserIdAndOrderId(Long userId, Long orderId,Integer userType);


    /**
     * 根据userId和orderId查询
     * @param orderId
     * @param tenantId
     * @param userType
     * @return
     */
    List<OrderLimit> getOrderLimit(Long orderId,Long tenantId,Integer userType);


    /**
     * 判断订单是否有尾款
     * @param orderIds
     * @param userType
     * @return
     */
    List<OrderLimitDto> hasFinalOrderLimit(List<Long> orderIds, Integer userType);

    /**
     * WX接口-校验订单是否可添加异常[30060]
     * @param orderId
     * @param userType
     * @return
     */
    OrderLimitDto hasFinalOrderLimit(Long orderId, Integer userType);


    /**
     * 根据userID查询
     * @param userId
     * @param capitalChannel
     * @param noPayType
     * @param tenantId
     * @param userType
     * @return
     * @throws Exception
     */
    List<OrderLimit> getOrderLimit(Long userId, String capitalChannel, String noPayType, Long tenantId,Integer userType);





    /**
     * 根据对应科目金额匹配订单
     * @param amount  匹配金额
     * @param income 利润
     * @param backIncome 返现金额
     * @param fieldName 匹配字段
     * @param orderLimits 限制信息
     */
    List<OrderLimit> matchAmountToOrderLimit(Long amount, Long income, Long backIncome,
                                              List<OrderLimit> orderLimits);


    /**
     * 根据userId和orderId查询
     * @param userId
     * @param orderId
     * @param tenantId
     * @param userType
     * @param user
     * @return
     */
    List<OrderLimit> getOrderLimitUserId(Long userId, Long orderId, Long tenantId,
                                         Integer userType, LoginInfo user);




    /**
     * 匹配限制表
     * @param amount
     * @param income
     * @param backIncome
     * @param userId
     * @param vehicleAffiliation
     * @param tenantId
     * @param isNeedBill
     * @param fieldName
     * @param orderLimits
     * @return
     * @throws Exception
     */
     List<OrderLimit> matchAmountToOrderLimit(Long amount, Long income, Long backIncome,Long userId,
                                                    String vehicleAffiliation,Long tenantId,Integer isNeedBill,
                                                     List<OrderLimit> orderLimits);


    /**
     * 开单创建订单限制数据
     * @param orderInfo 订单信息
     * @param orderGoodsInfo 订单货物信息
     * @param tenantId 租户id
     * @param sourceOrderId 来源单号
     * @throws Exception
     * @return void
     */
    void createOrderLimit(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee,
                          OrderScheduler scheduler, OrderGoods orderGoodsInfo,
                          OrderFeeExt orderFeeExt, Long tenantId);


    /**
     * 创建代收司机限定表
     * @param orderInfo
     * @param orderInfoExt
     * @param orderFee
     * @param scheduler
     * @param orderGoodsInfo
     * @param orderFeeExt
     * @param tenantId
     * @return
     */
    OrderLimit createCollectionOrderLimit(OrderInfo orderInfo,OrderInfoExt orderInfoExt,OrderFee orderFee,
                                          OrderScheduler scheduler, OrderGoods orderGoodsInfo,OrderFeeExt orderFeeExt,
                                          Long tenantId);

    /**
     * 创建副驾限制表
     * @param orderInfo
     * @param orderInfoExt
     * @param orderFee
     * @param scheduler
     * @param orderGoodsInfo
     * @param orderFeeExt
     * @param tenantId
     * @return
     */
    OrderLimit createOrderLimitCopilot(OrderInfo orderInfo,OrderInfoExt orderInfoExt,
                                       OrderFee orderFee,OrderScheduler scheduler,
                                       OrderGoods orderGoodsInfo,OrderFeeExt orderFeeExt,
                                       Long tenantId);

    /**
     * 创建订单限制表
     * @param acOrderSubsidyIn
     * @return
     */
    OrderLimit createOrderLimit(AcOrderSubsidyInDto acOrderSubsidyIn);


    /**
     * 根据userId和orderId查询
     */
    OrderLimit getOrderLimit(long userId,long orderId,long tenantId,Integer userType,LoginInfo loginInfo);

    /**
     * 异常扣减费用接口
     * @param userId  用户编号
     * @param amountFee 金额单位(分)正数司机可用金额增加，附负数司机可用金额扣减
     * @param objId 异常编号
     * @param vehicleAffiliation 资金渠道
     * @param tenantId 租户ID
     * @return void
     */
    void payForExceptionOut(Long userId, String vehicleAffiliation,
                            Long amountFee, Long objId, Long tenantId,
                            Long orderId,LoginInfo loginInfo,String token);

    /**
     * 油和etc转现
     * @param
     * @return
     * @throws Exception
     */
    List<OrderLimit> queryOilAndEtcBalance(OilExcDto oilExcDto) ;


    /**
     * 查询到期列表 	（分页）
     * @param orderId	订单号
     * @param userName	用户名称
     * @param userPhone	手机号码
     * @param mainDriver 主驾司机
     * @param state		到期状态：0未到期 1已到期
     * @param isIncludeManual 是否包含手动到期数据
     * @param tenantId	租户Id
     * @param userType
     * @return
     */
    Page<OrderLimit> queryOrderLimits(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize);

    List<OrderLimit> getOrderLimitZhangPing(Long userId, Long tenantId,Integer userType);

    void doOrderLimtByFlowId(PayoutIntf payoutIntf);

    /**
     * 根据userId查询
     * @param orderId
     * @param userType
     * @return
     * @throws Exception
     */
    List<OrderLimit>  getOrderLimitsByOrderId(long orderId,Integer userType);
    /**
     * 根据userId tenantId userType查询
     *
     * @param userId
     * @param noPayType
     * @param tenantId
     * @param userType
     * @return
     */
    List<OrderLimit> getOrderLimit(long userId, String noPayType, Long tenantId,Integer userType);

    /**
     * 查询所有租户到期的尾款
     * @param fianlStss
     * @param finalPlanDate
     * @param userType
     * @return List<OrderLimit>
     * @throws Exception
     */
    List<OrderLimit> queryOrderLimit(Integer[] fianlStss, Date finalPlanDate, Integer userType);

    /**
     * 使用客户油充值账户
     * @param amount
     * @param tenantUserId
     * @param rechargeOrderId
     * @param tenantId
     * @param driverUserId
     * @param businessId
     * @param subjectsId
     * @param userType
     * @param accessToken
     * @return
     */
    List<RechargeOilSource> matchOrderAccountOilToRechargeOil(long amount, Long tenantUserId,String rechargeOrderId, long tenantId, long driverUserId,Long businessId,Long subjectsId,int userType,String accessToken);


    /**
     * 该函数的功能描述:查询用户需要开票平台开票的订单，按时间排序 （根据订单先进先出的规则）
     * @param userId
     * @return
     */
    List<OrderAccount> matchHaOrderAccount(Long userId);

    /**
     * 查询用户需要平台开票，并且未提现的订单，按时间排序
     * @param userId
     * @param tenantId
     * @param userType
     * @return
     */
    List<OrderLimit> getHaOrderLimit(long userId,Long tenantId,Integer userType);

    /**
     * 提现金额返回对应订单
     * @param inParam
     * @return
     */
    List<OrderLimit> matchOrderInfoWithdraw(Map<String,Object> inParam);

    /**
     * 根据单号集合查询订单限制表
     * @param orderId
     * @param userId
     * @return
     */
    OrderLimit queryOrderLimitByOrderId(Long orderId,Long userId);

    /**
     * 未到期转可用
     * @param userId  用户编号
     * @param amountFee 未到期转可用金额 单位(分)
     * @param vehicleAffiliation 资金渠道
     * @param orderId 订单号
     * @param tenantId 租户id
     * @param sign 1司机尾款、2服务商收入(油老板、维修商)
     * @throws Exception
     * @return void
     */
    void marginTurnCash(long userId,String vehicleAffiliation,long amountFee,long orderId,long tenantId,String sign,String oilAffiliation,Long payFlowId,Object obj,String accessToken);


    /***
     * @Description:  未到期期转到期进程进程
     * @Author: luwei
     * @Date: 2022/6/29 22:08
     * @return: java.lang.String
     * @Version: 1.0
     **/
    String execution();
}
