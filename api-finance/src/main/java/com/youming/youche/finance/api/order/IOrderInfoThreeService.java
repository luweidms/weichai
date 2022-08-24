package com.youming.youche.finance.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.order.OrderDiffInfo;
import com.youming.youche.finance.domain.order.OrderInfo;
import com.youming.youche.finance.dto.LineInfoDto;
import com.youming.youche.finance.dto.order.OrderDiffInfoDto;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.finance.dto.order.VehicleOrderDto;
import com.youming.youche.finance.vo.order.OrderInfoVo;
import com.youming.youche.finance.vo.order.VehicleOrderVo;
import org.apache.ibatis.annotations.Param;


import java.time.LocalDate;
import java.util.List;

/**
 * 应收订单
 *
 * @author hzx
 * @date 2022/2/8 9:18
 */
public interface IOrderInfoThreeService extends IBaseService<OrderInfo> {

    /**
     * 应收订单列表
     */
    PageInfo<OrderInfoDto> queryReceviceManageOrder(OrderInfoVo orderInfoVo, String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 对账调整-- 根据orderId查询订单差异列表
     */
    List<OrderDiffInfoDto> getOrderDiffList(String orderId, String accessToken);

    /**
     * 对账调整-- 保存订单差异 并且填冲确认收入金额
     *
     * @param orderId        订单Id
     * @param orderDiffInfos 差异列表
     */
    void saveOrderDiff(Long orderId, List<OrderDiffInfo> orderDiffInfos, String accessToken);

    /**
     * 修改对账名称
     *
     * @param orderId
     * @param checkName
     * @return
     */
    Integer batchUpdateCheckName(List<Long> orderId, String checkName);

    /**
     * 应收订单导出
     * @param orderInfoVo
     * @param isOrder
     * @param accessToken
     * @param importOrExportRecords
     */
    void export(OrderInfoVo orderInfoVo, Boolean isOrder, String accessToken, ImportOrExportRecords importOrExportRecords);


    /**
     * 招商车订单明细
     * @param vehicleOrderVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<VehicleOrderDto> merchantsVehicleOrder(VehicleOrderVo vehicleOrderVo, Integer pageNum, Integer pageSize,String accessToken);

    /**
     * 招商车订单明细导出
     * @param vehicleOrderVo
     * @param importOrExportRecords
     */
    void merchantsExport(VehicleOrderVo vehicleOrderVo,ImportOrExportRecords importOrExportRecords,String accessToken);

    /**
     * 外调车订单明细
     * @param vehicleOrderVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<VehicleOrderDto> transferVehicleOrder(VehicleOrderVo vehicleOrderVo, Integer pageNum, Integer pageSize,String accessToken);

    /**
     * 外调车订单明细导出
     * @param vehicleOrderVo
     * @param importOrExportRecords
     */
    void transferExport(VehicleOrderVo vehicleOrderVo,ImportOrExportRecords importOrExportRecords,String accessToken);

    /**
     * 自有车订单明细
     * @param vehicleOrderVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<VehicleOrderDto> ownVehicleOrder(VehicleOrderVo vehicleOrderVo, Integer pageNum, Integer pageSize,String accessToken);

    /**
     * 自有车订单明细导出
     * @param vehicleOrderVo
     * @param importOrExportRecords
     */
    void ownExport(VehicleOrderVo vehicleOrderVo,ImportOrExportRecords importOrExportRecords,String accessToken);

    /**
     * 线路报表
     * @param sourceName
     * @param beginTime
     * @param endTime
     * @return
     */
    Page<LineInfoDto> lineStatements(String sourceName, String beginTime, String endTime, Integer pageNum, Integer pageSize,String accessToken);

    /**
     * 线路报表导出
     * @param sourceName
     * @param beginTime
     * @param endTime
     * @return
     */
    void lineStatementsExport(String sourceName, String beginTime, String endTime,String accessToken,ImportOrExportRecords importOrExportRecords);


    /**
     * 财务工作台    应收账户  已收金额
     */
    List<WorkbenchDto> getTableFinancialReceivableReceivedAmount();

    /**
     * 财务工作台    应收账户  剩余金额（还需要减去已收）
     */
    List<WorkbenchDto> getTableFinancialReceivableSurplusAmount();

    /**
     * 查询车队在途订单数量
     *
     * @param selectType 查询类型
     */
    Integer queryOrderNumberByState(Integer selectType, String accessToken);

    /**
     * 车辆报表获取确认收入金额
     *
     * @param plateNumber     车辆号
     * @param tenantId        车队id
     * @param beginDependTime 靠台时间开始
     * @param endDependTime   开台时间结束
     */
    Long getVehicleAffirmIncomeFeeByMonth(String plateNumber, Long tenantId, String beginDependTime, String endDependTime);

}
