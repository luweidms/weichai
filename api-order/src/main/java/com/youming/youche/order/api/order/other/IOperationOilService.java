package com.youming.youche.order.api.order.other;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.oil.ClearAccountOilRecord;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.*;
import com.youming.youche.order.vo.OrderPaymentWayOilOut;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 油操作相关接口(和油有关的)
 * */
public interface IOperationOilService {
    /**
     * 油卡消费接口
     * 可以消费的金额，包含油卡+可提现+即将到期
     * 1、若该加油站是共享加油站，支持开票，且司机要求开票，则只能使用所有资金中需要开票的金额（通过订单判断）
     * 2、若该加油站是共享加油站，支持开票，且司机不要求开票，则只能使用所有资金中不需要开票的金额（通过订单判断）
     * 3、若该加油站是共享加油站，但不支持开票，且司机要求开票，则所有的资金都不可使用
     * 4、若该加油站是共享加油站，但不支持开票，且司机不要求开票，则只能使用所有资金中不需要开票的金额（通过订单判断）
     * 5、若该加油站只和某车队合作，支持开票，且司机要求开票，则只能使用所有资金中需要开票的金额（通过订单判断）
     * 6、若该加油站只和某车队合作，支持开票，且司机不要求开票，则只能使用所有资金中不需要开票的金额
     * 7、若该加油站只和某车队合作，不支持开票，且司机要求开票，则所有的资金都不可使用
     * 8、若该加油站只和某车队合作，不支持开票，且司机不要求开票，则只能使用所有资金中不需要开票的金额
     * 9、若该加油站即合作车队的油站，又是共享加油站，资金规则与共享油站相同，只是优先使用合作车队的金额
     * @param userId  消费用户编号
     * @param serviceUserId  收入油费手机号码
     * @param productId  产品id
     * @param oilPrice  加油价格(分/升)
     * @param oilRise  加油升数(升)
     * @param amountFee 消费金额单位(分) （amountFee = oilPrice * oilRise）保留整数
     * @param objId 业务编号
     * @param vehicleAffiliation 资金渠道
     * @param tenantId 租户ID
     * @param isNeedBill 司机开不开票 0不要，1要
     * @param localeBalanceState 是否现场价加油 0不是，1是
     * @throws Exception
     * @return void
     */
    ConsumeOilFlow payForOil( PayForOilInDto in,String accessToken) ;
    /**
     * 操作油账户
     * @param serviceProduct 产品信息
     * @param sysOperator 消费人
     * @param sysOtherOperator 接受人
     * @param accountList 账户列表
     * @param oilFlowList  消费记录
     * @param sourceTenantIdList
     * @param inParm
     * @param oilPrice 司机加油油价
     * @param curOilPrice 平台油价
     * @param canUseOil 可以使用的账户油
     * @param isSharePorduct 是否共享产品
     * @param isBill 服务商能否开票
     * @param isBelongToTenant 是否私有产品
     * @param plateNumber 车牌号
     * @return
     * @throws Exception
     */
//    Map<String, Object> operationOrderAccountOil(ServiceProduct serviceProduct, SysOperator sysOperator , SysOperator sysOtherOperator, List<OrderAccount> accountList, List<ConsumeOilFlow> oilFlowList, List<Long> sourceTenantIdList, Map<String,Object> inParm, Long oilPrice, Long curOilPrice, long canUseOil, boolean isSharePorduct, boolean isBill, boolean isBelongToTenant, long soNbr) throws Exception;
    /**
     * 操作充值油账户
     * @param serviceProduct 产品信息
     * @param sysOperator 消费人
     * @param sysOtherOperator 接受人
     * @param accountList 账户列表
     * @param oilFlowList  消费记录
     * @param sourceTenantIdList
     * @param inParm
     * @param oilPrice 司机加油油价
     * @param curOilPrice 平台油价
     * @param canUseOil 可以使用的账户油
     * @param isSharePorduct 是否共享产品
     * @param isBill 服务商能否开票
     * @param isBelongToTenant 是否私有产品
     * @param plateNumber 车牌号
     * @return
     * @throws Exception
     */
//    Map<String, Object> operationOrderAccountRechargeOil(ServiceProduct serviceProduct,SysOperator sysOperator ,SysOperator sysOtherOperator,List<OrderAccount> accountList,List<ConsumeOilFlow> oilFlowList,List<Long> sourceTenantIdList,Map<String,Object> inParm,Long oilPrice,Long curOilPrice, long canUseRechargeOil, boolean isSharePorduct, boolean isBill,boolean isBelongToTenant, long soNbr) throws Exception;
    /**
     * 油卡抵押释放押金（新）
     * @param userId
     * @param vehicleAffiliation 资金渠道
     * @param amountFee 单位(分) 抵扣释放金额
     * @param orderId 订单ID
     * @param tenantId 租户ID
     * @param pledgeType  0抵扣 1释放
     */
    void pledgeOrReleaseOilCardAmount(long userId, String vehicleAffiliation, long amountFee, long orderId, long tenantId, int pledgeType) throws Exception;

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
    OrderOilSource saveOrderOilSource(Long userId, long orderId, long sourceOrderId, long sourceAmount, long noPayOil, long paidOil, Long sourceTenantId, Date createDate, long opId, int isNeedBill, String vehicleAffiliation, Date orderDate, String oilAffiliation, Integer oilConsumer,
                                      long rebateOil, long noRebateOil, long paidRebateOil, long creditOil, long noCreditOil, long paidCreditOil, int userType, int oilAccountType, int oilBillType);
    /**
     *
     * @param userId
     * @param billId
     * @param vehicleAffiliation
     * @param orderIdc
     * @param amount
     * @param tenantId
     * @throws Exception
     */
    void oilEntityVerification(long userId, String billId, String vehicleAffiliation, long orderId, long amount, long tenantId) throws Exception;
    /**
     * 判断订单是否抵押过油卡(油卡抵押改造为从尾款抵押之前的订单)
     * @param orderId 订单号
     * @return boolean true订单抵押过油卡，false订单没有抵押过油卡
     * @throws Exception
     */
    boolean judgeOrderIsPledgeOilCard(Long orderId) ;
    /**
     * 查询司机的油账户不同模式的油费
     * @param userId 用户id
     * @param tenantId 租户id
     * @param userType 用户类型
     * @return OrderPaymentWayOilOut
     * @throws Exception
     */
    OrderPaymentWayOilOut getOrderPaymentWayOil(Long userId, Long tenantId, int userType);
    /**
     * 查询指定时间区间的扫码加油
     * @param orderIds
     * @param beginDate
     * @param endDate
     * @param userId
     * @return
     * @throws Exception
     */
//    OrderConsumeOilOut getOrderCousumeOil(List<Long> orderIds, Date beginDate, Date endDate, Long userId,Long tenantId) throws Exception;
    /**
     * 油账户充值接口
     * @param userId 充值用户id
     * @param rechargeAmount 充值金额（单位分）
     * @param isNeedBill 是否要发票（0不需要发票，1需要发票） 如果rechargeType=2，则必填，如果rechargeType=1随便填，默认填-1
     * @param tenantId  操作员租户id
     * @param oilAccountType  油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     * @param oilBillType  油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
     * @throws Exception
     */
    void rechargeOrderAccountOil(Long userId, Long rechargeAmount, int isNeedBill,Integer oilAccountType,int userType,Integer oilBillType,String accessToken) ;
    /**
     * 使用客户油接口
     * @param userId 充值用户id
     * @param rechargeAmount 充值金额（单位分）
     * @param tenantId  操作员租户id
     * @throws Exception
     */
    @Deprecated
    void rechargeByCustomerOil(Long userId, Long rechargeAmount, Long tenantId,int userType) throws Exception;
    /**
     * 使用车队油接口
     * @param userId 充值用户id
     * @param rechargeAmount 充值金额（单位分）
     * @param isNeedBill 是否要发票（0不需要发票，1需要发票）
     * @param tenantId  操作员租户id
     * @param userType 用户类型
     * @throws Exception
     */
    @Deprecated
    void rechargeByFleetOil(Long userId, Long rechargeAmount, int isNeedBill, Long tenantId,Integer oilConsumer,int userType) throws Exception;
    /**
     * 保存充值油来源关系
     * @param userId
     * @param fromFlowId
     * @param rechargeOrderId
     * @param sourceOrderId
     * @param sourceAmount
     * @param noPayOil
     * @param paidOil
     * @param tenantId
     * @param createDate
     * @param opId
     * @param isNeedBill
     * @param vehicleAffiliation
     * @param orderDate
     * @param oilAffiliation
     * @return
     * @throws Exception
     */
    RechargeOilSource saveRechargeOilSource(Long userId, Long fromFlowId, String rechargeOrderId, String sourceOrderId, long sourceAmount, long noPayOil, long paidOil, Long tenantId, Date createDate, long opId, int isNeedBill, String vehicleAffiliation, Date orderDate, String oilAffiliation, String rechargeType,
                                            Integer oilConsumer, long rebateOil, long noRebateOil, long paidRebateOil, long creditOil, long noCreditOil, long paidCreditOil, int userType, Integer oilAccountType, Integer oilBillType,String accessToken);
    /**
     * 清除油账户余额(实报实销模式下的油)
     * @param userId
     * @param
     * @return
     * @throws Exception
     */
    Map<String, Object> clearAccountOil(Long userId, String accessToken) ;
    /**
     * 清除订单油来源关系表(实报实销模式油)
     * @param userId
     * @param tenantId
     * @param soNbr
     * @return
     * @throws Exception
     */
    long clearOrderOil(Long userId, Long tenantId, long soNbr,String accessToken);
    /**
     * 清除充值油来源关系表(实报实销模式油)
     * @param userId
     * @param tenantId
     * @param soNbr
     * @return
     * @throws Exception
     */
    long clearRechargeOil(Long userId, Long tenantId, long soNbr,String accessToken);
    /**
     * 保存清除油账户记录
     * @param userId
     * @param fromTable
     * @param fromFlowId
     * @param soNbr
     * @param accId
     * @param clearAmount
     * @param tenantId
     * @return
     * @throws Exception
     */
    ClearAccountOilRecord saveClearAccountOilRecord(Long userId, Integer busiType , Integer fromTable, Long fromFlowId, long soNbr, Long accId, Long clearAmount, Long tenantId,String accessToken) ;
    /**
     *  找油网加油：服务商可以开票只能用开票的订单，不可以开票只能用不要票的订单
     * @param userId  加油司机id
     * @param serviceUserId 服务商id
     * @param productId 产品id
     * @param oilPrice 加油价格  分/升
     * @param oilRise  加油升数
     * @param amountFee 加油金额
     * @param orderNum 单号
     * @param tenantId 租户id
     * @param plateNumber 车牌号
     * @param fromType 来源
     * @param id serviceOrderInfo表id
     * @return
     * @throws Exception
     */
    ConsumeOilFlow payForOrderOil(long userId, long serviceUserId, long productId,Long oilPrice, Float oilRise, long amountFee, String orderNum, Long tenantId, String plateNumber,Integer fromType,Long id) throws Exception;
    /**
     * 获取用户油账户明细余额
     */
    OilAccountOutListDto getOilAccount(Long userId);
    /**
     * 获取用户油账户在油站可以加油最大升数，不包括用户平安可用余额,此接口的油站开不开票和油费金额是否要票是相对应的，如油站是车队私有的有账期的，怎油费金额是来自车队的非预存资金，
     * @param userId
     * @param products  油站列表
     * @param isNeedBill 0不需要开票，1需要开票
     * @param amount 油费金额，如果为0传不需要开票金额，为1传需要开票金额
     * @param tenantId 如果租户不存在或为空，则油站产品id
     * @return
     * @throws Exception
     */
//    List<OilServiceOut> getOilStationDetails(Long userId, List<OilServiceIn> products, int isNeedBill,Long amount, Long tenantId)throws Exception;
    /**
     * 扫码加油 20190625
     * @param in
     * @return
     * @throws Exception
     */
//    ConsumeOilFlow payForOil(PayForOilIn inParam) throws Exception;

    /**
     * 操作油账户
     * @param userId
     * @param serviceUserId
     * @param productId
     * @param sourceTenantIdList
     * @param inParm
     * @param oilPrice 司机加油油价
     * @param canUseOil 可以使用的账户油
     * @param isSharePorduct 是否共享产品
     * @param plateNumber 车牌号
     * @return
     * @throws Exception
     */
    Map<String, Object> payOrderAccountOil(Long userId ,Long serviceUserId,
                                           List<Long> sourceTenantIdList,
                                           Map<String,Object> inParm,
                                           Long oilPrice, long canUseOil,
                                           boolean isSharePorduct,long soNbr,
                                           Long productId,String orderNum,String accessToken) ;
    /**
     * 油老板收入
     * @param userId
     * @param serviceUserId
     * @param oilPrice
     * @param canUseOil
     * @param isSharePorduct
     * @param soNbr
     * @param productId
     * @throws Exception
     */
    Map<String, Object> payForServiceOil(Long userId ,Long serviceUserId,Long oilPrice,
                                         long canUseOil,long soNbr,Long productId,Object obj,String orderNum,String accessToken) ;
    /**
     * 车队返利给司机
     * @param userId 司机用户id
     * @param rebate 返利金额 单位分
     * @param tenantId 车队id
     * @param tenantId 母卡明细记录id
     * @throws Exception
     */
    String consumeOilRebateToDriver(Long userId, Long rebate,  Long tenantId,Long oilRechargeAccountDetailsId) throws Exception ;
    /**
     * 判断订单是否消费了油
     * @param orderId
     * @return true 消费过，false 未消费
     * @throws Exception
     */
    boolean getOrderIsConsumeOil(Long orderId) ;
    /**
     * 使用车队油接口
     * @param userId 充值用户id
     * @param rechargeAmount 充值金额（单位分）
     * @param oilAccountType 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     * @param oilBillType 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     * @param tenantId  操作员租户id
     * @param userType 用户类型
     * @throws Exception
     */
    void rechargeOil(Long userId, Long rechargeAmount, int isNeedBill ,Integer oilAccountType,int userType,Integer oilBillType,String accessToken);

    /**
     * 查询指定时间区间的扫码加油
     */
    OrderConsumeOilDto getOrderCousumeOil(List<Long> orderIds, Date beginDate, Date endDate, Long userId, Long tenantId);


    /**
     * 查询油站可以加多少油 20190625
     * @param userId
     * @param products
     * @param tenantId
     * @param userType
     * @return
     * @throws Exception
     */
    List<OilServiceOutDto> queryOilRise(Long userId, List<OilServiceInDto> products , Long tenantId, Integer userType);
}
