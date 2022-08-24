package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.dto.*;
import com.youming.youche.order.dto.order.OrderListAppDto;
import com.youming.youche.order.dto.order.OrderListWxDto;
import com.youming.youche.order.dto.order.QueryOrderOilCardInfoDto;
import com.youming.youche.order.dto.order.QueryOrderResponsiblePartyDto;
import com.youming.youche.order.dto.order.QueryUserOrderJurisdictionDto;
import com.youming.youche.order.dto.order.UserInfoDto;
import com.youming.youche.order.vo.DispatchOrderVo;
import com.youming.youche.order.vo.EstimatedCostsVo;
import com.youming.youche.order.vo.OrderListAppVo;
import com.youming.youche.order.vo.OrderListInVo;
import com.youming.youche.order.vo.OrderListWxVo;
import com.youming.youche.order.vo.OrderReceiptVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 新的订单表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
public interface IOrderInfoService extends IBaseService<OrderInfo> {
    /**
     * 保存订单
     * @return
     */

    String  saveOrder(OrderDto orderDto,String accessToken);


    /**
     * 修改订单接口(调度后)
     * @param orderInfo
     * @param orderFee
     * @param orderGoods
     * @param orderInfoExt
     * @param orderFeeExt
     * @param orderScheduler
     * @param depotSchemes
     * @param updateType 修改类型
     * @param oilCardNums 油卡号
     * @param costPaymentDaysInfo
     * @param incomePaymentDaysInfo
     * @param oilCardInfos
     * @param transitLineInfos
     * @return
     * @throws Exception
     */
    String updateOrderInfoByPortionTwo(OrderInfo orderInfo, OrderFee orderFee, OrderGoods orderGoods, OrderInfoExt orderInfoExt, OrderFeeExt orderFeeExt,
                                    OrderScheduler orderScheduler, List<OrderOilDepotScheme> depotSchemes, Integer updateType, List<String> oilCardNums,
                                    OrderPaymentDaysInfo costPaymentDaysInfo, OrderPaymentDaysInfo incomePaymentDaysInfo,
                                    List<OrderOilCardInfo> oilCardInfos, List<OrderTransitLineInfo> transitLineInfos, LoginInfo baseUser, String accessToken);



    /**
     * 订单录入
     *
     * 1 临时线路需要调用其他接口保存客户信息，线路信息
     *
     *
     *
     *
     *
     * @param orderInfo
     * @param orderfee
     * @param orderGoods
     * @param orderInfoExt
     * @param orderFeeExt
     * @param orderScheduler
     * @param costPaymentDaysInfo 成本账期
     * @param incomePaymentDaysInfo 收入账期
     * @param orderOilCardInfos 油卡充值
     * @param transitLineInfos 经停点
     * @param isCopy
     *            true表示是复制订单，false 表示不是复制订单
     */
     Long saveOrUpdateOrderInfo(OrderInfo orderInfo, OrderFee orderfee, OrderGoods orderGoods,
                                      OrderInfoExt orderInfoExt, OrderFeeExt orderFeeExt, OrderScheduler orderScheduler,
                                      List<OrderOilDepotScheme> depotSchemes, OrderPaymentDaysInfo costPaymentDaysInfo,
                                      OrderPaymentDaysInfo incomePaymentDaysInfo, List<OrderOilCardInfo> orderOilCardInfos,
                                      List<OrderTransitLineInfo> transitLineInfos, Boolean isCopy,String accessToken,LoginInfo user);


    /**
     * 设置订单调度信息
     *
     * @param operType
     *            操作类型
     * @param desc
     *            操作名称
     * @param orderInfo
     * @param orderInfoExt
     * @param orderGoods
     * @param orderfee
     * @param orderFeeExt
     * @param orderScheduler
     * @param depotSchemes
     * @param orderOilCardInfos
     * @param transitLineInfos
     * @return
     * @throws Exception
     */
    Boolean setOrderInfoDispatch(SysOperLogConst.OperType operType, String desc,
                                 OrderInfo orderInfo, OrderInfoExt orderInfoExt,
                                 OrderGoods orderGoods, OrderFee orderfee,
                                 OrderFeeExt orderFeeExt, OrderScheduler orderScheduler,
                                 List<OrderOilDepotScheme> depotSchemes,
                                 List<OrderOilCardInfo> orderOilCardInfos,
                                 List<OrderTransitLineInfo> transitLineInfos, LoginInfo baseUser,String token);

    /**
     * 根据订单号获取订单信息
     * @param orderId
     * @return
     */
    OrderInfo getOrder(Long orderId);

    /**
     * 查询订单列表
     * 聂杰伟
     * @param orderListInVo
     * @param accessToken
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<OrderListOut> getOrderList(OrderListInVo orderListInVo, String accessToken, Integer pageNum, Integer pageSize);


    /***
     * @Description: 修改订单审核通过
     * @Author:
     * @Date: 2022/3/28 1:12 上午
     * @Param orderId:
      * @Param desc:
      * @Param isNeedAudit:
      * @Param baseUser:
      * @Param accessToken:
     * @return: void
     * @Version: 1.0
     **/
    void orderUpdateAuditPass(Long orderId, String desc, boolean isNeedAudit, LoginInfo baseUser,String accessToken);

    /**
     * 修改订单审核不通过
     *
     * @param orderId
     *            订单号
     * @param desc
     *            审核备注
     * @throws Exception
     */
    void orderUpdateAuditNoPass(Long orderId, String desc, String accessToken,LoginInfo baseUser);

    /**
     * 根据订单号获取历史订单信息
     * @param orderId
     * @return
     */
    OrderInfoH getOrderH(Long orderId);

    /**
     *
     * @param orderId 订单id
     * @param remark 备注
     * @param accessToken
     * @param transferOrderState 转单状态(0待接单 1 已接单 2 已拒接 3 已超时)
     */
    int refuseOrder (Long orderId,String remark,String accessToken,Integer transferOrderState);

    /**
     *
     * @param orderId 订单id
     * @param toTenantId 转单的租户
     * @param orderState
     * @param refuseOrderReason
     */
    void updateOrderStateH(Long orderId, Long toTenantId, Integer orderState, String refuseOrderReason);

    /**
     * 更新订单状态
     * @param orderId
     * @param toTenantId
     * @param orderState
     * @param refuseOrderReason
     */
    void updateOrderState(Long orderId, Long toTenantId, Integer orderState, String refuseOrderReason);


    /**
     * 更新订单应收应付时间
     * @param orderScheduler
     * @param orderInfo
     * @param reciveDate
     * @param user
     */
    void updateSettleDueDate(OrderScheduler orderScheduler, OrderInfo orderInfo, LocalDateTime reciveDate, LoginInfo user);

    /**
     * 保存导入订单信息
     * @param bytes
     * @param records
     * @param token
     * @return
     */
    String saveExportOrders(byte[] bytes, ImportOrExportRecords records, String token) ;

    /**
     * 通过orderid查找orderinfo
     * @param orderId
     * @return
     */
    OrderInfo getOrderInfo(Long orderId);

    /**
     * 营运工作台  改单审核数量
     * @return
     */
    List<WorkbenchDto> getTableModifyExamineCount();

    /**
     * 营运工作台  异常审核数量
     */
    List<WorkbenchDto> getTableExceptionExamineCount();

    /**
     * 营运工作台  回单审核数量
     */
    List<WorkbenchDto> getTableReceiptExamineCount();

    /**
     * 营运工作台  今日开单数量
     * @return
     */
    List<WorkbenchDto> getTableBillTodayCount();

    /**
     * 营运工作台  待付付数量
     * @return
     */
    List<WorkbenchDto> getTablePrepaidOrderCount();

    /**
     * 营运工作台  在途数量
     */
    List<WorkbenchDto> getTableIntransitOrderCount();

    /**
     * 营运工作台  待回单审核
     */
    List<WorkbenchDto> getTableRetrialOrderCount();

    /**
     * 查询订单责任方[30038]
     */
    List<QueryOrderResponsiblePartyDto> queryOrderResponsibleParty(Long orderId);

    /**
     *
     * 查询修改字段差异详情[30045]
     */
    OrderListWxDto queryOrderUpdateDetail(Long orderId, String accessToken);

    /**
     * 复制订单-WX接口[30047]
     */
    Long copyOrderInfo(Long orderId, String dependTime, String accessToken);


    /**
     * 微信查询订单列表(30016)
     * @param orderListWxVo
     */
    Page<OrderListWxDto> queryOrderListWxOut(OrderListWxVo orderListWxVo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 查询代收人订单列表  30075
     * @param orderListWxVo
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @return
     */
    Page<OrderListWxDto> queryOrderListCollectionOut(OrderListWxVo orderListWxVo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 订单轨迹变更(30017)
     * @param orderId
     * @return
     */
    boolean loadingWx(Long orderId, String accessToken);

    /**
     * 获取订单充值油卡接口[30051]
     */
    List<QueryOrderOilCardInfoDto>  queryOrderOilCardInfo(Long orderId, String plateNumber, String accessToken);


    /**
     * 回单审核（30018）
     * @param orderReceiptVo
     * @param accessToken
     */
    boolean verifyRece(OrderReceiptVo orderReceiptVo,String accessToken);



    /**
     * 重新指派
     * @param orderInfo
     * @param orderInfoExt
     * @param orderGoods
     * @param orderfee
     * @param orderFeeExt
     * @param orderScheduler
     * @param depotSchemes
     * @param costPaymentDaysInfo
     * @param orderOilCardInfos
     * @param desc
     * @param loginInfo
     * @param token
     * @return
     */
    String orderInfoReassign(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderGoods orderGoods, OrderFee orderfee,
                             OrderFeeExt orderFeeExt, OrderScheduler orderScheduler, List<OrderOilDepotScheme> depotSchemes, OrderPaymentDaysInfo costPaymentDaysInfo,
                             List<OrderOilCardInfo> orderOilCardInfos, String desc, LoginInfo loginInfo,String token);


    /**
     * 评价订单司机(30021)
     * @param orderId
     * @param serviceStarLevel
     * @param agingStarLevel
     * @param mannerStarLevel
     * @return
     */
    boolean evaluateOrderDirver(Long orderId,Integer serviceStarLevel,Integer agingStarLevel,Integer mannerStarLevel);

    /**
     * 上传回单回显(30024)
     * @param orderId
     * @return
     */
    OrderReciveEchoDto orderReciveEcho(Long orderId, String accessToken);

    /**
     * 获取用户订单成本收入权限 30061
     * @return
     */
    QueryUserOrderJurisdictionDto queryUserOrderJurisdiction(String accessToken);


    /**
     * 接单的算费接口(30035)
     * @param estimatedCostsVo
     * @param accessToken
     * @return
     */
    EstimatedCostsDto getEstimatedCosts(EstimatedCostsVo estimatedCostsVo, String accessToken);

    /**
     * 接口编号 11014
     * 接口入参：
     * userId	                 用户编号
     * 接口出参：
     * userPriceUrl              用户头像，待确认
     * orderSum                  在途订单数量
     * balance                   我的钱包可提现金额，分为单位
     * vehicleSum                我的车辆数量
     * vehicleVer                我的车辆认证标志 0 未认证  1已认证
     * driverVer                 司机身份认证标志 0 未认证  1已认证
     * 我的视图查询接口
     */
    UserInfoDto getUserInfo(Long userId, String accessToken);

    /**
     * APP查询订单列表
     */
    List<OrderListAppDto> queryOrderListAppOut(OrderListAppVo vo);


    /**
     * APP查询订单列表(30001)
     * @param vo
     * @return
     */
    Object queryOrderListApp(OrderListAppVo vo, Integer pageNum, Integer pageSize, String accessToken);



    /**
     * 订单录入价格异常审核成功的回调方法
     *
     * @param orderId
     * @param isUpdate
     * @throws Exception
     */
     void auditPriceSuccess(Long orderId, Boolean isUpdate, String  cause,String accessToken);


    /***
     * 订单录入价格异常审核失败的回调方法
     *
     * @param orderId
     * @throws Exception
     */
     void auditPriceFail(Long orderId,String  cause,String token);

    /**
     * 调车（30019）
     * @param dispatchOrderVo
     * @param accessToken
     * @return
     */
    Long dispatchOrderInfo(DispatchOrderVo dispatchOrderVo, String accessToken);

    /**
     * 查询车队在途订单
     *
     * @param tenantId 车队ID
     */
    List<OrderInfo> queryOrderInfoByTenantId(Long tenantId);

    /**
     * 小程序首页查询订单审核数量 -- 3异常审核 2回单审核
     */
    Integer getStatisticsOrderReview(String accessToken, Integer selectOrderType);

    /**
     * 车辆报表获取车辆订单部分邮费和etc
     */
    VehicleFeeFromOrderDataDto getVehicleFeeFromOrderDataByMonth(String plateNumber, Long tenantId, String month);

    /**
     * 车辆报表获取车辆当前时间的车辆运输线路
     */
    String getVehicleLineFromOrderDataByMonth(String plateNumber, Long tenantId, String month);

    /**
     * 订单消息查询订单记录
     */
    List<OrderInfo> selectOrderInfosByOrderId(Long orderId);

    /**
     * 修改订单司机读取状态
     */
    void updateOrderIdMessage(Long orderId);

}
