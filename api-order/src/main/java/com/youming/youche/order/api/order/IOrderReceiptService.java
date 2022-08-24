package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderReceipt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.OrderListAppOutDto;
import com.youming.youche.order.dto.OrderReceiptDto;
import com.youming.youche.order.dto.QueryDriverOrderDto;
import com.youming.youche.order.vo.OrderReceiptVo;

import java.util.List;

/**
 * <p>
 * 订单-回单 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
public interface IOrderReceiptService extends IBaseService<OrderReceipt> {


    /**
     * 保存 上传回单
     * @param orderRecipts
     * @param
     * @param accessToken
     */
    void saveLoadRecive(OrderReceiptVo orderReceiptVo, boolean isUpdateState, String accessToken);



    /**
     * 查看回单
     * @param orderId 订单号
     * @return
     */
    List<OrderReceipt> findOrderReceipts(Long orderId, String accessToken,String flowId);

    /**
     * 聂杰伟
     * 接口说明  图片上传
     * @param orderId 订单
     * @param type  上传类型  business 合同 recive 回单
     * @param loadUrl 图片路径
     * @param loadPicId 图片id
     * @param receiptsNumber 回单编号
     * @param receiptId 主键id
     * @return
     */
    void  upLoadPic(Long orderId,
                    String type,
                    String loadUrl,
                    String loadPicId,
                    String receiptsNumber,
                    Long receiptId,
                    String accessToken);

    /**
     *  回单审核
     * @param orderId 订单号
     * @param load  合同
     * @param receipt 回单
     * @param verifyString  备注
     * @param receiptsNumber 回单号
     * @param type
     * @return
     */
    void verifyRece(String orderId,Boolean load,Boolean receipt,
                    String verifyString,String receiptsNumber,
                    Integer type,String accessToken,boolean... isNeedLog);


    /**
     * 财务审核订单
     *
     * @param orderInfo
     * @throws Exception
     */
    void verify(OrderInfo orderInfo, OrderScheduler scheduler, OrderInfoExt orderInfoExt, String verifyString, Integer reciveType,String accessToken);


    /**
     *  批量审核
     * @param orderIdsStr 订单号集合
     * @param load
     * @param receipt
     * @param verifyDesc
     * @param accessToken
     * @return
     */
    List<OrderReceiptDto> batchAuditOrder(List<Long> orderIdsStr ,Boolean load,Boolean receipt, String verifyDesc,String accessToken);


    /**
     * 获取订单回单
     * @param order_id
     * @param receiptsNumber
     * @param accessToken
     * @return
     */
    List<OrderReceipt> orderReceipt(Long order_id,String receiptsNumber,String accessToken);

    /**
     * 删除订单回单  30058
     * @param orderId
     * @param flowId
     */
    void removeRecive(Long orderId, String flowId);

    /**
     * 查询合作车辆列表(30039)
     * @param vehicleCode
     * @param tenantId
     * @return
     */
    Page<OrderListAppOutDto> queryCooperationOrderList(Long vehicleCode, Long tenantId,Integer pageNum,Integer pageSize, String accessToken);


    /**
     * 查询司机在途订单车辆(30057)
     * @param userId
     * @return
     */
    List<QueryDriverOrderDto> queryDriverOrderPlateNumber(Long userId);


}
