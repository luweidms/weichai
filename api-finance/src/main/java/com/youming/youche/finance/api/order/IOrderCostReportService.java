package com.youming.youche.finance.api.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.order.OrderCostDetailReport;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import com.youming.youche.finance.domain.order.OrderCostReport;
import com.youming.youche.finance.domain.order.OrderMainReport;
import com.youming.youche.finance.domain.vehicle.VehicleMiscellaneouFeeDto;
import com.youming.youche.finance.dto.order.OrderCostReportDto;
import com.youming.youche.finance.dto.order.OrderMainReportDto;
import com.youming.youche.finance.vo.order.OrderCostReportVo;
import com.youming.youche.order.domain.order.OrderInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
public interface IOrderCostReportService extends IBaseService<OrderCostReport> {


    /**
     *  接口说明 获取费用上报列表
     *  聂杰伟
     *  2022-3-8
     * @param orderId 订单号
     * @param plateNumber 车牌号
     * @param userName 司机姓名
     * @param linkPhone 手机号
     * @param startTime 提交时间起始
     * @param endTime  提交时间中止
     * @param subUserName  提交时间中止
     * @param state 上报状态
     * @return
     */
    IPage<OrderCostReportDto> selectReport(String orderId, String plateNumber, String userName,
                                           String linkPhone, String startTime, String endTime,
                                           String subUserName, Integer state, Integer sourceRegion,
                                           Integer desRegion, Boolean waitDeal, String accessToken,
                                           Integer pageNum, Integer pageSize);


    /**
     * 接口说明  费用审核
     *  聂杰伟
     * @param busiCode
     * @param busiId
     * @param desc
     * @param chooseResult
     * @param loadMileage
     * @param capacityLoadMileage
     * @param accessToken
     */
    void  auditOrderCostReport(String busiCode,Long busiId,String desc,Integer chooseResult,String loadMileage,String capacityLoadMileage, String accessToken );


    /**
     * 处理费用上报审核记录
     * @param id
     * @param auditRemark
     * @param loadMileage
     * @param capacityLoadMileage
     */
    void  dealExamineOrderCostReport(Long id, String auditRemark,Long loadMileage,Long capacityLoadMileage);


    /**
     * 聂杰伟
     * 2022-3-10
     *  接口说明；根据订单号查询上报信息
     * @param orderId
     * @return
     */
    OrderCostReportDto getOrderCostDetailReportByOrderId(String orderId,String accessToken);


    /***
     * 根据主键id查询上报信息
     * @param orderMainReport
     * @return
     * @throws Exception
     */
    OrderCostReportDto getOrderCostDetailReport(OrderMainReport orderMainReport);

    /**
     * 接口说明 费用上报审核
     * 聂杰伟
     * 2022-3-10-15：13
     * @param orderCostReportVo
     * @return
     */
    void  examineOrderCostReport(OrderCostReportVo orderCostReportVo,  String accessToken);


    /**
     *  费用管理明细  列表
     * @param paymentWay //付款方式 1:油卡 2:现金 3:全部
     * @param tableTyp //数据类型 1油费 2路桥费
     * @param orderId //订单号
     * @param plateNumber  //车牌号
     * @param carDriverMan //司机名称
     * @param cardNo //卡号
     * @param startTime //靠台时间开始
     * @param endTime //靠台时间结束
     * @param pageNum 分页
     * @param pageSize
     * @return
     */
    PageInfo<OrderMainReportDto> queryOrderCostDetailReports(Integer paymentWay ,
                                                             Long typeId,
                                                             Long orderId,
                                                             String plateNumber,
                                                             String carDriverMan,
                                                             String cardNo, String startTime,
                                                             String endTime, Integer pageNum,
                                                             Integer pageSize, String accessToken);


    /**
     *  明细导出
     * @param paymentWay
     * @param typeId
     * @param orderId
     * @param plateNumber
     * @param carDriverMan
     * @param cardNo
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    void  exportParticulars(Integer paymentWay ,
                            Long typeId,
                            Long orderId,
                            String plateNumber,
                            String carDriverMan,
                            String cardNo, String startTime,
                            String endTime, Integer pageNum,
                            Integer pageSize, String accessToken, ImportOrExportRecords record);


    /**
     * 根据 id查询 上报费用
     * @param id
     * @param hasAudit
     * @return
     */
    List<OrderCostDetailReport> getOrderCostDetailReport(Long id, Boolean hasAudit);


    /**
     * 根据费用上报id查询其他上报费用
     * @param relId
     * @param hasAudit
     * @return
     * @throws Exception
     */
    List<OrderCostOtherReport> getOrderCostOtherReport(long relId, boolean hasAudit);


    /**
     * 流程结束，审核通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    void sucess(Long busiId,String desc,Map paramsMap,String accessToken );


    /**
     * 流程结束，审核不通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    void fail(Long busiId,String desc,Map paramsMap ,String accessToken);

    /**
     * 司机小程序
     * niejiewei
     * 50035
     * 是否可以上报
     * @param orderInfo
     * @return
     */
    Boolean isDoSave(OrderInfo orderInfo);

    /**
     * niejeiwei
     * 司机小程序
     * 费用审核
     * 50031
     * @param orderCostReportVo
     * @param accessToken
     */
    void  examineOrderCostReportByWx(OrderCostReportVo orderCostReportVo,  String accessToken);

    /**
     * 车辆报表获取订单费用上报数据
     */
    List<VehicleMiscellaneouFeeDto> getVehicleOrderFeeByMonth(String plateNumber, Long tenantId, String month);

}
