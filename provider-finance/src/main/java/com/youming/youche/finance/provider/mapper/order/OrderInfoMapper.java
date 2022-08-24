package com.youming.youche.finance.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import com.youming.youche.finance.domain.order.OrderInfo;
import com.youming.youche.finance.dto.LineInfoDto;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.finance.dto.order.OrderListOutDto;
import com.youming.youche.finance.dto.order.OrderStatementListOutDto;
import com.youming.youche.finance.dto.order.VehicleOrderDto;
import com.youming.youche.finance.vo.ac.OrderListInVo;
import com.youming.youche.finance.vo.order.OrderInfoVo;
import com.youming.youche.finance.vo.order.OrderStatementListInVo;
import com.youming.youche.finance.vo.order.VehicleOrderVo;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.record.dto.trailer.DateCostDto;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 应收订单
 *
 * @author hzx
 * @date 2022/2/8 9:46
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 应收订单列表
     *
     * @param orderInfoVoPage
     * @param orderInfoVo
     * @return
     */
    Page<OrderInfoDto> queryReceviceManageOrder(Page<OrderInfoDto> orderInfoVoPage,
                                                @Param("orderInfoVo") OrderInfoVo orderInfoVo);

    List<OrderInfoDto> queryReceviceManageOrderExport(@Param("orderInfoVo") OrderInfoVo orderInfoVo);

    /**
     * 查询订单账单
     *
     * @param orderStatementListInVo
     * @return
     */
    List<OrderStatementListOutDto> queryOrderStatementList(@Param("orderStatementListInVo") OrderStatementListInVo orderStatementListInVo);

    /**
     * 查询订单账单(分页)
     *
     * @param page
     * @param orderStatementListInVo
     * @return
     */
    Page<OrderStatementListOutDto> queryOrderStatementPage(Page<OrderStatementListOutDto> page, @Param("orderStatementListInVo") OrderStatementListInVo orderStatementListInVo);

    /**
     * 查询司机指定月份订单数量
     *
     * @param month
     * @param date
     * @param lastDate
     * @param userId
     * @param tenantId
     * @return
     */
    Integer queryDriverMonthOrderInfoCount(@Param("month") String month, @Param("date") Date date, @Param("lastDate") Date lastDate, @Param("userId") Long userId, @Param("tenantId") Long tenantId);


    /**
     * 查询司机指定月份订单
     *
     * @param orderListInVo
     * @param monthStart
     * @param monthEnd
     * @param userId
     */
    Page<OrderListOutDto> queryDriverMonthOrderInfo(Page<OrderListOutDto> orderListOutDtoPage,
                                                    @Param("orderListInVo") OrderListInVo orderListInVo,
                                                    @Param("monthStart") Date monthStart,
                                                    @Param("monthEnd") Date monthEnd,
                                                    @Param("userId") Long userId);

    List<OrderListOutDto> queryDriverMonthOrderInfoList(@Param("orderListInVo") OrderListInVo orderListInVo,
                                                        @Param("monthStart") Date monthStart,
                                                        @Param("monthEnd") Date monthEnd,
                                                        @Param("userId") Long userId);

    List<OrderListOutDto> queryDriverMonthOrderInfoListNew(@Param("sendId") Long sendId);

    /**
     * 招商车订单报表
     *
     * @param page
     * @param vehicleOrderVo
     * @return
     */
    Page<VehicleOrderDto> merchantsVehicleOrder(@Param("page") Page<VehicleOrderVo> page, @Param("vehicleOrderVo") VehicleOrderVo vehicleOrderVo, @Param("tenantId") Long tenantId);

    /**
     * 获取经停信息
     *
     * @param orderId
     * @return
     */
    List<OrderAgingInfo> getLineNodeByOrderId(@Param("orderId") Long orderId);

    /**
     * 招商车订单报表导出
     *
     * @param vehicleOrderVo
     * @return
     */
    List<VehicleOrderDto> merchantsExport(@Param("vehicleOrderVo") VehicleOrderVo vehicleOrderVo, @Param("tenantId") Long tenantId);

    /**
     * 外调车订单报表
     *
     * @param page
     * @param vehicleOrderVo
     * @return
     */
    Page<VehicleOrderDto> transferVehicleOrder(@Param("page") Page<VehicleOrderVo> page, @Param("vehicleOrderVo") VehicleOrderVo vehicleOrderVo, @Param("tenantId") Long tenantId);

    /**
     * 外调订单报表导出
     *
     * @param vehicleOrderVo
     * @return
     */
    List<VehicleOrderDto> transferExport(@Param("vehicleOrderVo") VehicleOrderVo vehicleOrderVo, @Param("tenantId") Long tenantId);

    /**
     * 自有车订单报表
     *
     * @param page
     * @param vehicleOrderVo
     * @return
     */
    Page<VehicleOrderDto> ownVehicleOrder(@Param("page") Page<VehicleOrderVo> page, @Param("vehicleOrderVo") VehicleOrderVo vehicleOrderVo, @Param("tenantId") Long tenantId);

    /**
     * 自有车订单报表导出
     *
     * @param vehicleOrderVo
     * @return
     */
    List<VehicleOrderDto> ownExport(@Param("vehicleOrderVo") VehicleOrderVo vehicleOrderVo, @Param("tenantId") Long tenantId);


    List<OrderCostOtherReport> getConsumeFee(@Param("orderId") Long orderId);

    /**
     * 线路报表
     *
     * @param page
     * @param sourceName
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<LineInfoDto> lineStatements(@Param("page") Page<LineInfoDto> page, @Param("sourceName") String sourceName, @Param("beginTime") LocalDate beginTime, @Param("endTime") LocalDate endTime, @Param("tenantId") Long tenantId);

    /**
     * 线路报表导出
     *
     * @param sourceName
     * @param beginTime
     * @param endTime
     * @return
     */
    List<LineInfoDto> lineStatementsExport(@Param("sourceName") String sourceName, @Param("beginTime") LocalDate beginTime, @Param("endTime") LocalDate endTime, @Param("tenantId") Long tenantId);

    /**
     * 财务工作台    应收账户  已收金额
     */
    List<WorkbenchDto> getTableFinancialReceivableReceivedAmount();

    /**
     * 财务工作台    应收账户  剩余金额
     */
    List<WorkbenchDto> getTableFinancialReceivableSurplusAmount();

    /**
     * @param selectType //1 订单跟踪 //2 在线接单 //3 订单审核
     * @param tenantId
     * @return
     */
    Integer queryOrderNumberByState(@Param("selectType") Integer selectType, @Param("tenantId") Long tenantId);


    /**
     * 获取借支金额(订单报表)
     *
     * @param queryType
     * @param orderId
     * @return
     */
    Double getOaLoanAmount(@Param("queryType") Integer queryType, @Param("orderId") Long orderId, @Param("tenantId") Long tenantId, @Param("subjects") List<String> subjects, @Param("subOrgList") List<Long> subOrgList, @Param("aBoolean") Boolean aBoolean);


    /**
     * 获取资产单月折旧
     *
     * @param tenantId
     * @return
     */
    DateCostDto getAssetDetails(@Param("tenantId") Long tenantId, @Param("plateNumber") String plateNumber);


    /**
     * 获取借支金额(线路报表)
     *
     * @param queryType
     * @param sourceName
     * @return
     */
    Double getOaLoanAmountLine(@Param("queryType") Integer queryType, @Param("sourceName") String sourceName, @Param("tenantId") Long tenantId, @Param("subjects") List<String> subjects, @Param("subOrgList") List<Long> subOrgList, @Param("aBoolean") Boolean aBoolean);

    /**
     * 获取司机报销（订单报表）
     *
     * @param orderId
     * @param tenantId
     * @return
     */
    Double getOrderReimburse(@Param("orderId") Long orderId, @Param("tenantId") Long tenantId);


    /**
     * 获取司机账号
     *
     * @param sourceName
     * @return
     */
    List<String> getCarDriverPhone(@Param("sourceName") String sourceName);

}
