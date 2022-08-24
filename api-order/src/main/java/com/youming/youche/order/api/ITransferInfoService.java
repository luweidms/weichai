package com.youming.youche.order.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.TransferInfo;
import com.youming.youche.order.dto.OrderDetailsTransferDto;
import com.youming.youche.order.dto.OrderTransferInfoDetailDto;
import com.youming.youche.order.dto.ReceiveOrderDto;
import com.youming.youche.order.dto.TransferInfoDto;
import com.youming.youche.order.vo.AcceptOrderVo;
import com.youming.youche.order.vo.OrderDetailsTransferVo;
import com.youming.youche.order.vo.SaveOrderVo;

import java.util.Date;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author liangyan
* @since 2022-03-14
*/
public interface ITransferInfoService extends IBaseService<TransferInfo> {
    /**
     * 在线接单页面 在线接单查询列表接口
     * @param page
     * @param transferInfoDto
     * @param accessToken
     * @return
     * @throws Exception
     */
    Page<TransferInfo> getTransferInfoList(Page<TransferInfo> page,TransferInfoDto transferInfoDto,
                                           String accessToken) throws Exception;

    /**
     *
     * @param orderId
     * @param tenantId
     * @param transferOrderState
     * @return
     */
    List<TransferInfo> getOrderTransferInfoList(Long orderId, Long tenantId,Integer transferOrderState);

    /**
     * 更新状态
     * @param orderId
     * @param acceptTenantId
     * @param transferOrderState
     */
    int updateOrderTransferState(Long orderId, Long acceptTenantId
            , Integer transferOrderState, Date opDate, String remark);

    /**
     * 查询订单状态是待接单，已接单的数据
     * @param orderId
     * @param tenantId
     * @return
     */
    TransferInfo getOrderTransferInfo(Long orderId, Long tenantId);

    /**
     * 查询订单状态是待接单，已接单的数据
     * @param orderId
     * @param authorization
     * @return
     */
    OrderDetailsTransferVo queryReceiveOrderDetail(String orderId, String authorization) throws Exception;
    /**
     * 在线接单
     * @param receiveOrderDto
     * @param authorization
     * @return
     */
    Boolean receiveOrder(ReceiveOrderDto receiveOrderDto, String authorization)throws Exception;

    /**
     *转单超时的订单的处理 用于定时任务
     * @param
     * @return
     */

    Boolean execution();

    /**
     * 在线接单(30030)
     * @param acceptOrderVo
     * @return
     */
    Long acceptOrderWx(AcceptOrderVo acceptOrderVo,String accessToken) throws Exception;

    /**
     * 在线接单的详情页面(30032)
     * @return
     */
    OrderTransferInfoDetailDto OrderTransferInfoDetail(Long orderId, String accessToken);

    /**
     * 查看转单后的信息
     * 限制： 租户只能查看订单状态是待接单，已接单的数据
     *
     * @param orderId 原订单id
     * @param tenantId 当前车队的租户id
     * @return
     */
    OrderDetailsTransferDto getOrderTransferInfoDetail(Long orderId, Long tenantId,String accessToken);


    String saveOrder(SaveOrderVo saveOrderVo, String accessToken);
}
