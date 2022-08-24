package com.youming.youche.order.api.order.other;

import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.OrderDetailsOutDto;
import net.sf.json.JSONObject;

import java.util.Map;

public interface ILugeOrderBusinessService {

    /**
     * 同步路歌运单
     * @param orderId 订单号
     * @param waybillType 同步类型
     * @return
     * @throws Exception
     */
    Map<String, Object> syncLugeOrderInfo(Long orderId, Integer waybillType, boolean isSupplementFee) throws Exception;

    /**
     * 签署协议
     * @param orderId 订单号
     * @return
     * @throws Exception
     */
    Map<String, Object> signAgreement(Long orderId) throws Exception;

    /**
     * 	轨迹上报接口
     * @param xid 运单ID
     * @return
     * @throws Exception
     */
    Map<String, Object> syncTrackInfo(Long orderId) throws Exception;

    /**
     * 上报回单状态接口
     * @param xid 运单ID
     * @param receiptPhotoPath 回单图片
     * @param outPhotoPath 出库图片
     * @param contractPhotoUrl 合同图片
     * @return
     * @throws Exception
     */
    Map<String, Object> syncOrderStateInfo(Long orderId, boolean isAnewUpload) throws Exception;

    /**
     * 运输协议签署结果查询接口
     * @param xid 运单ID
     * @return
     * @throws Exception
     */
    Map<String, Object> querySignAgreementResult(Long orderId) throws Exception;

    OrderDetailsOutDto getOrderAll(Long orderId);

    /**
     * 计算费用
     * @param preOrderFee
     * @param orderInfo
     * @param preOrderInfoExt
     * @param orderScheduler
     * @param isHis 是否历史
     * @return
     * @throws Exception
     */
    Map calculateSyncFee(OrderFee preOrderFee, OrderInfo orderInfo, OrderInfoExt preOrderInfoExt,
                         OrderScheduler orderScheduler, boolean isHis) throws Exception;

    /**
     * 回单结果回调接口
     * @param inParam
     * @throws Exception
     */
    void receiptResultCallBack(JSONObject inParam) throws Exception;

    /**
     * 路歌会员认证回调接口
     * @return
     * @throws Exception
     */
    void authenticationUserResultCallBack(JSONObject inParam) throws Exception;

    /**
     * 路歌地址转换
     * @param provinceName 省份名称
     * @param cityName 城市名称
     * @param districtName 区县名称
     * @return
     * @throws Exception
     */
    Map<String,Object> lugeCityTransform(String provinceName,String cityName,String districtName)throws Exception;

    /**
     * 运单终结接口
     * @param orderId 订单号
     * @return
     * @throws Exception
     */
    Map<String, Object> orderFinality(Long orderId)throws Exception;
    /**
     * 人工审核会员认证结果推送
     * @param inParam
     * @throws Exception
     */
    void authenticationUserPush(JSONObject inParam)throws Exception;


    OrderDetailsOutDto getOrderById(Long orderId);
}
