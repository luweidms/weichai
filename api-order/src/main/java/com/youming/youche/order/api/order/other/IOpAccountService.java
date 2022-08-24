package com.youming.youche.order.api.order.other;

import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.MarginBalanceDetailsOut;

import java.util.List;
import java.util.Map;

public interface IOpAccountService {



    /**
     * 开单创建订单限制数据
     * @param orderInfo 订单信息
     * @param orderGoodsInfo 订单货物信息
     * @param tenantId 租户id
     * @param sourceOrderId 来源单号
     * @throws Exception
     * @return void
     */
    void createOrderLimit(OrderInfo orderInfo,OrderInfoExt orderInfoExt,OrderFee orderFee,OrderScheduler scheduler, OrderGoods orderGoodsInfo,OrderFeeExt orderFeeExt, long tenantId) ;

    /**
     * 创建副驾限制表
     * @param orderInfo
     * @param orderGoodsInfo
     * @return OrderLimit
     * @return sourceOrderId 来源单号
     * @throws Exception
     */
    OrderLimit createOrderLimitCopilot(OrderInfo orderInfo,OrderInfoExt orderInfoExt, OrderFee orderFee, OrderScheduler scheduler, OrderGoods orderGoodsInfo,OrderFeeExt orderFeeExt, long tenantId) throws Exception;

    /**
     * 操作维修基金账户
     * @param userId 消费用户编号
     * @param vehicleAffiliation 资金渠道
     * @param amountFee 金额单位(分)
     * @param `businessId` 科目类别id
     * @param subjectsId 科目id
     * @param soNbr CommonUtil.createSoNbr()
     * @param objId 业务编号(或者订单号)
     * @param orderId 订单号
     * @param tenantId 租户ID
     * @param userType 用户类型
     * @throws Exception
     * @return void
     */
    void handleRepairFundAccount(long userId, String vehicleAffiliation, long amountFee, long businessId, long subjectsId, long soNbr, Long orderId, String objId, long tenantId,Integer userType) throws Exception;

    /**
     * 充值接口
     * @param userId  操作人用户编号
     * @param amountFee 充值费用 单位(分)
     * @param vehicleAffiliation 资金渠道(用0来专门标识)
     * @param billId 入账用户手机号
     * @param tenantId 租户id
     * @param userType 用户类型
     * @throws Exception
     */
    public void recharge(long userId, String vehicleAffiliation, long amountFee, long counterFee, Long tenantId,Integer userType) throws Exception;


    /**
     * 9分钟获取一次Token
     * @param param
     * @return String
     * @throws Exception
     */
//    String getToken(IParamIn param ) throws Exception;

    /**
     * 根据用户id，获取用户未到期金额
     * @param userId
     * @param userType 用户类型
     * @return
     * @throws Exception
     */
    Map<String, Object> getMarginBalanceUI(Long userId, Integer userType);
    /**
     * 根据用户id，获取用户未到期金额
     * @param userId
     * @param userType 用户类型
     * @return
     * @throws Exception
     */
    List<MarginBalanceDetailsOut> getMarginBalance(Long userId, Integer userType) ;
    /**
     * 获取用户未到期金额
     * @param userId
     * @param fleetName
     * @param userType 用户类型
     * @return
     * @throws Exception
     */
//    public Pagination getMarginBalance(Long userId, String fleetName, Integer userType) throws Exception;
    /**
     *
     * @param userId
     * @param vehicleAffiliation (默认传 "0" 对应枚举VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0)
     * @param tenantId
     * @param userType
     * @return
     * @throws Exception
     */
    OrderAccount createOrderAccount(Long userId, String vehicleAffiliation, Long tenantId, Integer userType) throws Exception;

    /**
     * @Description: 首页-账户明细
     * @param name 资金来源方 （可选，模糊匹配）
     * @param accState （可选）
     * @param userType 用户类型
     * @return
     * @throws Exception
     */
//    Pagination getAccountDetails(String name,String accState,Integer userType) throws Exception;

    /***
     *
     * @Description: 账户明细-即将到期明细
     * 接口入参：
     * @param userId
     */
//    Pagination getAccountDetailsNoPay(String userId,String orderId,String startTime,String endTime,String sourceRegion,String desRegion) throws Exception;

    /**
     * @Description: 账户明细-押金明细
     * @param userId
     * @param orderId
     * @param startTime
     * @param endTime
     * @param sourceRegion
     * @param desRegion
     * @param name  承运车队
     * @param carDriverPhone 司机手机
     * @param plateNumber 车牌号码
     * @return
     * @throws Exception
     */
//    Pagination getAccountDetailsPledge(String userId,String orderId,String startTime,String endTime,String sourceRegion,String desRegion,String name,String carDriverPhone,String plateNumber) throws Exception;
    /**
     * 操作电子油卡账户
     * @param userId
     * @param pinganPayAcctId
     * @param orderId
     * @param vehicleAffiliation
     * @param soNbr
     * @param tenantId
     * @param busiSubjectsRelList
     * @param oilAffiliation 油资金渠道
     * @throws Exception
     */
    void lockPinganAmount(Long userId, String pinganPayAcctId, String orderId, String vehicleAffiliation, long soNbr, Long tenantId, List<BusiSubjectsRel> busiSubjectsRelList, long subjectsId, String oilAffiliation) throws Exception;
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
     * @throws Exception
     */
    OrderLimit createCollectionOrderLimit(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee, OrderScheduler scheduler, OrderGoods orderGoodsInfo, OrderFeeExt orderFeeExt, long tenantId) throws Exception ;

}
