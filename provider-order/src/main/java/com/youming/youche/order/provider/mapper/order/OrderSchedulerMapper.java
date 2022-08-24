package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.vo.CheckLineIsOkByPlateNumberVo;
import com.youming.youche.order.vo.OrderVerifyInfoOut;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 订单调度表
 *
 * @author hzx
 * @date 2022/3/11 14:04
 */
public interface OrderSchedulerMapper extends BaseMapper<OrderScheduler> {

    /**
     * 获取订单校验信息
     *
     * @param plateNumber      车牌号
     * @param orderId          过滤订单号
     * @param dependTime       靠台时间
     * @param orderStateLT     小于此订单状态
     * @param userId           用户ID
     * @param isQueryLastOrder 是否查上一单(否则查下一单)
     */
    List<OrderVerifyInfoOut> queryOrderVerifyInfoOut(@Param("plateNumber") String plateNumber, @Param("orderId") Long orderId,
                                                     @Param("dependTime") Date dependTime, @Param("orderStateLT") Integer orderStateLT,
                                                     @Param("userId") Long userId, @Param("isQueryLastOrder") Boolean isQueryLastOrder);

    List<OrderVerifyInfoOut> queryOrderVerifyInfoOutH(@Param("plateNumber") String plateNumber, @Param("orderId") Long orderId,
                                                      @Param("dependTime") Date dependTime, @Param("orderStateLT") Integer orderStateLT,
                                                      @Param("userId") Long userId, @Param("isQueryLastOrder") Boolean isQueryLastOrder);


    List<OrderSchedulerH> getPreOrderSchedulerByUserId(@Param("userId") Long userId, @Param("orderId") Long orderId,
                                      @Param("isLastOrder") Boolean isLastOrder,
                                      @Param("dependTime") LocalDateTime dependTime,
                                      @Param("tenantId") Long tenantId,
                                      @Param("selectType") Integer selectType,
                                      @Param("isHis") Boolean isHis);
    @Select("SELECT * from order_scheduler_h where  order_id = #{orderId} ")
    List<OrderSchedulerH> getOrderId(Long orderId);

    List<CheckLineIsOkByPlateNumberVo> checkLineIsOkByDriverId(@Param("userId") Long userId,
                                                               @Param("fromOrderId") Long fromOrderId,
                                                               @Param("dependTime") LocalDateTime dependTime,
                                                               @Param("orderId") Long orderId);

    List<CheckLineIsOkByPlateNumberVo> checkLineIsOkByDriverIdH(@Param("userId") Long userId,
                                                               @Param("fromOrderId") Long fromOrderId,
                                                               @Param("dependTime") LocalDateTime dependTime,
                                                               @Param("orderId") Long orderId);


    List<CheckLineIsOkByPlateNumberVo> checkLineIsOkByPlateNumber(@Param("plateNumber") String plateNumber,
                                                                  @Param("fromOrderId") Long fromOrderId,
                                                                  @Param("dependTime") LocalDateTime dependTime,
                                                                  @Param("orderId") Long orderId);

    List<CheckLineIsOkByPlateNumberVo> checkLineIsOkByPlateNumberH(@Param("plateNumber") String plateNumber,
                                                                   @Param("fromOrderId") Long fromOrderId,
                                                                   @Param("dependTime") LocalDateTime dependTime,
                                                                   @Param("orderId") Long orderId);

    List<BigInteger> getMonthFirstOrderId(@Param("plateNumber") String plateNumber, @Param("month") String month, @Param("excludeOrderId") Long excludeOrderId);

    /**
     * 根据时间节点查询实报实销订单id
     * @param plateNumber
     * @param startDate
     * @param endDate
     * @param month
     * @return
     * @throws Exception
     */
    List<BigInteger> queryExpenseOrderIdByTimeNode(@Param("plateNumber") String plateNumber,@Param("startDate")Date startDate,
                                                   @Param("endDate")Date endDate,@Param("month")String month);

}
