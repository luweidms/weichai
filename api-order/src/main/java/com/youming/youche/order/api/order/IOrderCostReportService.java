package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderCostDetailReport;
import com.youming.youche.order.domain.order.OrderCostReport;
import com.youming.youche.order.domain.order.OrderMainReport;
import com.youming.youche.order.dto.order.OrderCostReportCardDto;
import com.youming.youche.order.dto.order.OrderCostReportDto;
import com.youming.youche.order.vo.OrderCostOtherReportVO;
import com.youming.youche.order.vo.OrderCostOtherTypeVO;

import java.util.List;
import java.util.Map;

/**
* <p>
    *  服务类
    * </p>
* @author liangyan
* @since 2022-03-22
*/
    public interface IOrderCostReportService extends IBaseService<OrderCostReport> {

    /**
     * 通过订单号，获取上报费用记录
     * @param orderId
     * @return
     * @throws Exception
     */
    List<OrderCostReport> getOrderCostReportByOrderId(Long orderId);



    /***
     * 查询订单无上报或者上报已提交或者保存未提交标识
     * state 0未提交 3审核中 4审核不通过 5审核通过
     * OrderConsts.ORDER_COST_REPORT
     * @param orders
     * @return  orderId , state
     * @throws Exception
     */
    List<OrderMainReport> getCostReportStateByOrderIds(List<Long> orders);

    OrderCostReportCardDto queryAll( String accessToken, Long orderId);

    /**
     * 获取费用上报其他费用中的类型
     * @param accessToken
     * @return
     */
    List<OrderCostOtherTypeVO> getOrderCostOtherTypes(String accessToken);

    /**
     * 查询其他费用类型
     * @param tenantId
     * @param typeName
     * @return
     */
    List<OrderCostOtherTypeVO> getOrderCostOtherTypeList(long tenantId,String typeName);

    /**
     * 根据订单号查询上报信息
     * @param orderId
     * @param accessToken
     * @return
     */
    OrderCostReportDto getOrderCostDetailReportByOrderId(Long orderId, String accessToken);

    OrderMainReport getObjectByOrderId(Long orderId);

    List<OrderCostReportDto> getListById(Long orderId);

    /**
     * 根据主键id查询上报信息
     * @param id
     * @param b
     * @return
     */
    List<OrderCostDetailReport> getOrderCostDetailReport(Long id, boolean b);

    /**
     * 根据费用上报id查询其他上报费用
     * @param id
     * @param b
     * @return
     */
    List<OrderCostOtherReportVO> getOrderCostOtherReport(Long id, boolean b);

    /**
     * 添加其他上报费用的消费类型
     * @param id
     * @param typeName
     * @param accessToken
     * @return
     */
    Long addOrderCostOtherType(Long id, String typeName, String accessToken);

    /**
     * 上报保存和修改
     * @param orderCostReportDto
     * @param accessToken
     */
    void doSaveOrUpdateNew(OrderCostReportDto orderCostReportDto, String accessToken);

    /**
     * 获取订单成本上报公里数
     * @param orderId
     * @param b
     * @return
     */
    List<OrderCostReport> getOrderCostReportByOrderIds(Long orderId, boolean b);

    /***
     * 获取总距离
     * @param orders
     * @return  long[0] -- 空载距离
     * @return  long[1] -- 载重距离
     */
    Map<Long,long[]> getKilometreByOrderIds(List<Long> orders);


    /**
     * 判断订单费用上报 是否已经完成审核：true订单上报的费用审核完成，false订单上报的费用还有审核未完成
     * @param orderId 订单号
     * @return
     * @throws Exception
     */
    boolean judgeOrderFeeIsExamineFinish(Long orderId, String accessToken);

    /**
     * 统计上报其他费用的消费总金额
     * @param orderId
     * @return
     * @throws Exception
     */
    Long getOrderCostOtherReportAmountByOrderId(Long orderId);

}
