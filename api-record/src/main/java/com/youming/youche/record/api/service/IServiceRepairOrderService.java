package com.youming.youche.record.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.service.ServiceRepairOrder;
import com.youming.youche.record.domain.service.ServiceRepairOrderVer;
import com.youming.youche.record.dto.DoSureDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDetailDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDetailVerDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderUpdateEchoDto;
import com.youming.youche.record.vo.service.ServiceRepairOrderIUVo;
import com.youming.youche.record.vo.service.ServiceRepairOrderVo;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * hzx
 * 车辆维保
 *
 * @date 2022/1/8 10:56
 */
public interface IServiceRepairOrderService extends IBaseService<ServiceRepairOrder> {

    /**
     * 维保审核 \ 申请维保 查询列表
     */
    Page<ServiceRepairOrderDto> doQueryOrderList(ServiceRepairOrderVo serviceRepairOrderVo, Page<ServiceRepairOrderDto> objectPage, String accessToken);

    /**
     * 维保审核 \ 申请维保 导出
     * @param serviceRepairOrderVo
     * @param accessToken
     * @param importOrExportRecords
     */
    void doQueryOrderListExport(ServiceRepairOrderVo serviceRepairOrderVo, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 获取工单详情
     */
    ServiceRepairOrderDetailDto getRepairOrderDetail(long flowId);

    /**
     * 获取工单详情
     */
    ServiceRepairOrderDetailVerDto getRepairOrderDetailVer(long flowId);

    /**
     * 修改回显 82018
     */
    ServiceRepairOrderUpdateEchoDto getServiceRepairOrder(Long flowId);

    /***
     * 新增修改维修保养工单
     */
    void doSaveOrUpdateServiceRepairOrder(ServiceRepairOrderIUVo serviceRepairOrderIUVo, String accessToken);

    /**
     * 取消
     */
    void doCancel(long flowId, String accessToken);

    /**
     * 取消 82017
     */
    void doCancelWX(long flowId, String accessToken);

    /**
     * 总费用
     */
    String getTotalFee(ServiceRepairOrderVo serviceRepairOrderVo, String accessToken);

    /**
     * 修改上单保养里程数
     *
     * @param flowId
     * @param lastOrderMileage 上单保养里程数
     */
    void doSaveLastOrderMileage(Long flowId, String lastOrderMileage);

    /**
     * 取消审核流程
     * @param auditCode
     * @param busiId
     * @param tenantId
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void cancelProcess(String auditCode, Long busiId, Long tenantId) throws InvocationTargetException, IllegalAccessException;

    /**
     * 流程结束，审核通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    public void sucess(Long busiId, String desc, Map paramsMap,String accessToken);

    /**
     * 流程结束，审核不通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    public void fail(Long busiId,String desc,Map paramsMap);

    /**
     * （多节点、第一个节点成功后回调）审核中回调方法
     * @param busiId
     */
    void auditingCallBack(Long busiId);

    /**
     * 刷新项目工时和配置项
     */
    void flushVerData(ServiceRepairOrder serviceRepairOrder, ServiceRepairOrderVer serviceRepairOrderVer, int state);

    /**
     * 根据id删除服务商维修项、根据id删除维修零配件
     */
    void deleteServiceRepairOrderItems(long flowId);

    /**
     * 支付维修费用 成功回调
     */
    void payRepairFeeCallBack(String orderCode, String accessToken);

    /**
     * 静态数据获取
     *
     * @param codeType 静态枚举类型
     */
    List<SysStaticData> getStaticDataOption(String codeType, String accessToken);

    /**
     * 支付回调
     */
    void payOrderCall(String orderCode, String accessToken);

    /**
     * 业务单号(外)查询维修保养订单
     *
     * @param orderSn 业务单号(外)
     * @return
     */
    ServiceRepairOrder getOrderByOrderCode(String orderSn);

    /**
     * 车队维修保养记录数
     *
     * @param flowIds    逐渐id
     * @param tenantList 车队
     * @return
     */
    Integer doQueryServiceRepairOrderCount(List<Long> flowIds, List<Long> tenantList);

    /**
     * 分页查询车队维修保养记录
     *
     * @param currentPage
     * @param pageSize
     * @param flowIds
     * @param tenantList
     * @return
     */
    List<ServiceRepairOrder> doQueryServiceRepairOrder(int currentPage, int pageSize,
                                                       List<Long> flowIds, List<Long> tenantList);

    /**
     * 营运工作台  维保费用  待我审
     */
    List<WorkbenchDto> getTableMaintenanceCount();

    /**
     * 营运工作台  维保费用  我发起
     */
    List<WorkbenchDto> getTableMaintenanceMeCount();

    /**
     * 保存图片文件
     */
    void doSavePicFiles(long flowId, String picFileIds, String file);

    /**
     * 工单报价确认,完工确认(82013)
     * @param dto
     * @return
     */
    boolean doSure(DoSureDto dto, String accessToken);


}
