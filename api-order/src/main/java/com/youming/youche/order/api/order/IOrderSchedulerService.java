package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.dto.CheckConflictDto;
import com.youming.youche.order.dto.OrderCostRetrographyDto;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.monitor.OrderLineDto;
import com.youming.youche.order.dto.orderSchedulerDto;
import com.youming.youche.order.vo.CheckConflictVo;
import com.youming.youche.order.vo.QueryOrderLineNodeListVo;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单调度表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
public interface IOrderSchedulerService extends IBaseService<OrderScheduler> {


    /**
     * 根据订单号获取订单调度信息
     * @param orderId
     * @return
     */
    OrderScheduler getOrderScheduler(Long orderId);

    /**
     * 发送短信
     *
     * @param tenantName 车队名称
     * @param orderId 订单号
     * @param plateNumber 车牌号
     * @param carDriverPhone 司机电话
     *  @param carDriverName 司机名称
     * @param sourceCityName 出发城市
     * @param desCityName 目的城市
     * @param dependTime 靠台时间
     * @throws Exception
     */
    void sendOrderSms(String tenantName, Long orderId, String plateNumber, String carDriverPhone,String carDriverName,String sourceCityName,String desCityName,LocalDateTime dependTime,String token) throws Exception;

    /**
     * 根据车牌号码获取自有车车辆的上一个订单的订单id
     *
     * @param plateNumber

     * @param dependTime 靠台时间
     * @param  orderId 排除订单号
     * @return
     *     orderId 订单id
     *     type  1 表示原表  2 表示历史表
     */
     Map<String,Object> getPreOrderIdByPlateNumber(String plateNumber,
                                                         LocalDateTime dependTime, Long tenantId, Long orderId);


    /**
     * 根据车牌号查询靠台时间上一单订单的调度信息
     *
     * @param plateNumber 车牌号码
     * @param dependTime 靠台时间
     * @param tenandId  租户id
     * @param orderId 排除订单号
     * @return
     */
    OrderScheduler getPreOrderSchedulerByPlateNumber(String plateNumber,LocalDateTime dependTime,Long tenandId,Long orderId);

    /**
     * 获取订单调度
     * @param orderId
     * @return
     */
    OrderScheduler selectByOrderId(Long orderId);




    /**
     * 查询司机id最近的订单的调度信息
     *
     * @param userId 主驾驶的用户id
     * @param dependTime 靠台时间
     * @param orderId 排除的订单id
     * @param isLastOrder 是否最后单
     * @return
     */
     OrderScheduler getPreOrderSchedulerByUserId(Long userId,LocalDateTime dependTime,
                                                       Long tenandId,Long orderId,
                                                       Boolean isLastOrder);

    /**
     * 校验车辆上一单是否勾选满油操作完成切换其他模式
     * @param plateNumber 车牌号
     * @param dependTime 靠台时间
     * @param tenantId 租户ID
     * @param orderId 需要过滤的订单号
     * @return
     * @throws Exception
     */
    Boolean checkPayMentWaySwitchover(String plateNumber, LocalDateTime dependTime, Long tenantId, Long orderId);

    /**
     * 校验车辆、主驾、副驾是否冲突
     * @return
     * @throws Exception
     */
    CheckConflictVo checkDependTimeConflict(CheckConflictDto checkConflictDto);

    /**
     * 自有车：判断 该车辆 的这个靠台时间是否是最近的
     *
     * @param plateNumber
     * @param dependTime
     * @param fromOrderId
     * @return
     *      true 表示这个订单可以开
     *      false 表示线路冲突了
     */
    Map checkLineIsOkByPlateNumber(String plateNumber, LocalDateTime dependTime, Long fromOrderId, Long orderId);

    /**
     * 自有车：判断 该司机 的这个靠台时间是否是最近的
     *
     * @param userId
     * @param dependTime
     * @param fromOrderId
     * @return
     *      true 表示这个订单可以开
     *      false 表示线路冲突了
     */
    Map checkLineIsOkByDriverId(Long userId,LocalDateTime dependTime,Long fromOrderId,Long orderId);



    /**
     * 计算订单预估收款时间
     * @param orderScheduler 调度信息
     * @param orderInfo 订单信息
     * @param info 订单账期信息
     * @param reciveDate 本单回单时间
     * @return
     * @throws Exception
     */
    LocalDateTime getOrderReceivableDate(OrderScheduler orderScheduler, OrderInfo orderInfo, OrderPaymentDaysInfo info,
                                LocalDateTime reciveDate, LoginInfo user);

    /**
     * 获取订单轨迹状态
     * @param orderId 订单号
     * @return
     */
    OrderInfoDto queryOrderTrackType(Long orderId);

    /**
     * 获取订单轨迹状态
     * @param orderId 订单号
     * @param sysStaticDataList  城市列表
     * @return
     */
    OrderInfoDto queryOrderTrackType(Long orderId,List<SysStaticData> sysStaticDataList);


    /**
     * 查询订单线路详情
     * @param orderId
     * @return
     * orderLine 总线路
     * isTransitLine 是否有经停线路
     * @throws Exception
     */
    OrderInfoDto queryOrderLineString(Long orderId);

    /**
     * 查询订单线路详情
     * @param orderId
     * @param sysStaticDataList
     * @return
     * orderLine 总线路
     * isTransitLine 是否有经停线路
     * @throws Exception
     */
    OrderInfoDto queryOrderLineString(Long orderId,List<SysStaticData> sysStaticDataList) ;

    /**
     * 校验靠台时间
     * @param orderInfo
     * @param orderScheduler
     * @param billMethod
     */
    void checkBillDependTime(OrderInfo orderInfo,OrderScheduler orderScheduler,long billMethod,LoginInfo user) ;

    /**
     * 查询司机补贴天数列表
     * @param orderId
     * @return
     * @throws Exception
     */
    List<Map> queryOrderDriverSubsidyDay(Long orderId);

    /**
     * 订单装货校验
     */
    void orderLoadingVerify(OrderInfo orderInfo,OrderScheduler orderScheduler,boolean isFromTenant);

    /**
     * 校验开票轨迹节点合规性
     * @param orderScheduler
     * @param orderGoods
     * @param transitLineInfos 经停点集合
     * @param nandDes 目的地经度
     * @param eandDes 目的地纬度
     * @param opDate 操作时间
     * @param transitSortId 经停点序号
     * @param vehicleAffiliation 资金渠道
     */
    void verifyTrackNode(OrderScheduler orderScheduler, OrderGoods orderGoods, OrderInfo orderInfo, List<OrderTransitLineInfo> transitLineInfos
            , String nandDes, String eandDes, Date opDate, Integer transitSortId, boolean isFromTenant, String vehicleAffiliation,String accessToken);

    /**
     * 计算订单预估轨迹时间
     */
    Date queryOrderTrackDate(Long orderId,Integer trackType,Long tenantId);

    /**
     * 补偿轨迹时间
     */
    void compensationTrackDate(Long orderId,String accessToken);

    /**
     * 获取订单线路集合
     * @param orderId
     * @return
     * nand
     * eand
     * @throws Exception
     */
    List<OrderLineDto> queryOrderLineList(Long orderId, String accessToken);

    /**
     * 设置redis订单位置
     */
    void setOrderLoncationInfo(OrderGoods orderGoods,OrderScheduler orderScheduler);

    /**
     * 根据副驾驶获取自有车车辆的上一个订单的订单id
     *
     * @param
     * @param tenantId
     * @param orderId 排除的订单id
     * @return
     *     orderId 订单id
     *     type  1 表示原表  2 表示历史表
     */
    Map<String,Object> getPreOrderIdByUserid(Long userId,
                                             LocalDateTime dependTime,Long tenantId,Long orderId);

    /**
     * 根据订单号查询调度表
     */
    OrderScheduler getOrderSchedulerByOrderId(Long orderId);

    /**
     * 获取订单调度信息 30063
     */
    OrderScheduler getOrderSchedulerByOrderIdWX(Long orderId);

    /**
     * 校准订单轨迹时间
     */
    void currectOrderTrackDate(Long orderId, int trackType, Date verifyDate, Long fileId, String fileUrl,String accessToken);

/*
    */
/*
     * @param dependTime  靠台时间
     * @param orderId     排除订单号
     * @return orderId 订单id
     * type  1 表示原表  2 表示历史表
     *//*

    Map<String, Object> getPreOrderIdByPlateNumber(String plateNumber, Date dependTime, Long tenantId, Long orderId);
*/

    /**
     * 根据挂车车牌查找最后一单
     */
    Map<String, Object> getPreOrderIdByTrailerPlate(String trailerPlate, Long tenantId);

    /**
     * 根据车牌号查询靠台时间上一单订单的调度信息
     *
     * @param plateNumber 车牌号码
     * @param dependTime  靠台时间
     * @param tenandId    租户id
     * @param orderId     排除订单号
     */
    OrderScheduler getPreOrderSchedulerByPlateNumber(String plateNumber, Date dependTime,
                                                     Long tenandId, Long orderId);

    /**
     * 根据挂车车牌查找最后一单
     */
    OrderScheduler getPreOrderSchedulerByTrailerPlate(String trailerPlate, Long tenandId);

    /**
     * 获取历史订单调度
     * @param orderId
     * @return
     */
    OrderSchedulerH getOrderSchedulerH(Long orderId);

    /**
     * 查询订单车辆绑定ETC
     * @param orderId
     * @param accessToken
     * @return
     */
    String queryCarEtcCardInfo(Long orderId,String accessToken);

    /**
     * 判断订单是否有尾款
     * @param orderIds
     * @return
     */
    Map<Long,Boolean> hasFinalOrderLimit(List<Long> orderIds);

    /**
     * 查询订单所有司机
     * @param orderId
     * @return
     * userId
     * userName
     * userPhone
     * userType
     * userTypeName
     */
    List<orderSchedulerDto> queryOrderDriverList(Long orderId);

    /**
     * 获取实报实销模式月初第一单
     * @param plateNumber
     * @param month
     * @param excludeOrderId 排除订单
     */
    List<BigInteger> getMonthFirstOrderId(String plateNumber, String month, Long excludeOrderId);


    /**
     获取订单线路节点集合
     * @param orderId
     * @return
     * sourceRegion 起始点
     * desRegion 目的点
     * arrive 到达时限
     * detailLine 线路详情
     * @return
     */
    List<QueryOrderLineNodeListVo>  queryOrderLineNodeList (Long orderId, String accessToken);

    /**
     * 订单成本反写
     * @param orderId 订单号
     * @param isSelect 是否查询
     */
    OrderCostRetrographyDto orderCostRetrography(long orderId, boolean isSelect, String accessToken);

    /**
     * 查询订单下一单
     */
    Map<String,Object> getNextOrderIdByPlateNumber(String plateNumber, Date dependTime,
                                                   Long tenantId,Long orderId);

    /**
     * 根据车牌号查询靠台时间下一单订单的调度信息
     *
     * @param plateNumber 车牌号码
     * @param dependTime 靠台时间
     * @param tenandId  租户id
     * @param orderId 排除订单号
     */
    OrderScheduler getNextOrderSchedulerByPlateNumber(String plateNumber,
                                                      Date dependTime,Long tenantId,Long orderId) ;

    /**
     * 校验车辆上一单是否勾选满油操作完成切换其他模式
     * @param plateNumber 车牌号
     * @param dependTime 靠台时间
     * @param tenantId 租户ID
     * @param orderId 需要过滤的订单号
     * @return
     * @throws Exception
     */
    Boolean checkPayMentWaySwitchover(String plateNumber, String dependTime, String accessToken, Long orderId);

    /**
     * 通过车牌查找最后一单订单
     * @param plateNumber
     * @return
     */
    OrderScheduler getOrderSchedulerByPlateNumber(String plateNumber);

    /**
     * 订单录入-所选司机或车辆有未完成的订单,提交订单时要有提示
     *  所选司机或车辆有未完成的订单,提交订单时要有提示
     * 如果所选司机或车辆下订单都已完成，则不做提示
     * plateNumbers 车牌号
     * userId 司机id
     */
    void judgeVehicleOrder(String plateNumbers,Long userId ,String accessToken);

    /**
     * 获取车辆使用定位类型
     */
    Integer queryVehicleGpsType(Long vehicleCode);

    /**
     * 获取订单轨迹状态
     */
    Map<String, Object> queryOrderTrackLocation(Long orderId);

    /**
     * 查询订单上次离台时间
     */
    Map queryLastStartInfo(Long orderId, int lineType);

    /**
     * 获取订单预估时间
     */
    LocalDateTime getOrderEstimateDate(Long orderId, LocalDateTime dependTime, LocalDateTime carStartDate, Float arriveTime, Boolean isHis, int nodeId);

}
