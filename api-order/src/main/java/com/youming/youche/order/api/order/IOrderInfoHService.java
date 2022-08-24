package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.OrderListOut;
import com.youming.youche.order.dto.order.GetOrderDetailForUpdateDto;
import com.youming.youche.order.dto.order.HasPermissionDto;
import com.youming.youche.order.dto.order.OrderDetailsAppDto;
import com.youming.youche.order.dto.order.OrderDetailsDto;
import com.youming.youche.order.dto.order.OrderVerDetailsDto;
import com.youming.youche.order.vo.OrderListInVo;
import com.youming.youche.order.vo.UpdateOrderVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 新的订单表 服务类
 * </p>
 *
 * @author chenzhe
 * @since 2022-03-20
 */
public interface IOrderInfoHService extends IBaseService<OrderInfoH> {

    /**
     * 根据订单号获取历史订单信息
     * @param orderId
     * @return
     */
    OrderInfoH selectByOrderId(Long orderId);


    /**
     * 获取历史订单信息
     * @param orderId
     * @return
     */
    OrderInfoH getOrderH(Long orderId);


    //************************************************hzx   开始************************************

    /**
     * 查询订单详情
     *
     * @param orderId
     * @param orderDetailsType 查询类型 参见OrderConsts类
     */
    OrderDetailsDto queryOrderDetails(Long orderId, Integer orderDetailsType, String accessToken);

    /**
     * 查询待审核数据
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    OrderVerDetailsDto queryOrderVerDetails(Long orderId, String accessToken);

    /**
     * 订单操作
     */
    void actionOrder(String action, String orderId, String accessToken);

    /**
     * 驻场／tms 靠台操作 上传合同／触发财务金额 待装货 状态转为 装货中
     *
     * @param orderId
     * @param action  depend 靠台 leave 离场 arrive 到达 departure 离场
     * @param action  desc 追加描述，例如：GPS、系统自动 ,操作人
     * @throws Exception
     */
    void loading(long orderId, String action, String desc, Date date, String redis, String accessToken);

    /**
     * 修改关联订单
     *
     * @param isTransit 是否经停点
     * @param isGps     是否GPS靠台
     * @param member    序号
     */
    void updateFromOrderState(OrderInfo orderInfo, int orderState, String desc, Date opDate, Boolean isTransit, boolean isGps, Integer member, String accessToken);

    /**
     * 更新订单应收应付时间
     */
    void updateSettleDueDate(OrderScheduler orderScheduler, OrderInfo orderInfo, Date reciveDate, LoginInfo loginInfo);

    /**
     * 付款审核通过
     *
     * @param orderId     订单号
     * @param auditCode   审核业务编码
     * @param verifyDesc  审核备注
     * @param oilCardNums 油卡列表
     *                    isFinallyNode 是否最后节点
     * @param receiptsStr 回单单号
     * @param reciveType  回单类型
     */
    void verifyPayPass(Long orderId, String auditCode, String verifyDesc, List<String> oilCardNums, String receiptsStr, Integer reciveType, String accessToken);

    /**
     * 付款审核不通过
     *
     * @param orderId     订单号
     * @param auditCode   审核业务编码
     * @param verifyDesc  审核备注
     * @param load        是否驳回合同
     * @param receipt     是否驳回回单
     * @param accessToken
     * @return
     * @throws Exception
     */
    String verifyPayFail(Long orderId, String auditCode, String verifyDesc, boolean load, boolean receipt, String accessToken);

    /**
     * 收入栏输入填写等值卡且类型为油卡，若输入的油卡卡号在本车队已存在且类型是供应商油卡时，提示该油卡已是供应商油卡，不能添加
     */
    void checkOilCarType(OrderFee orderFee, Long tenantId);

    /**
     * 取消订单
     */
    void canel(long orderId, Long problemId, boolean isCheck, String accessToken);

    /**
     * TODO 将订单对象转为历史订单对象
     */
    OrderInfoH getHisOrderInfo(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee,
                               OrderFeeExt orderFeeExt, OrderScheduler scheduler, OrderPaymentDaysInfo costPaymentDaysInfo,
                               OrderPaymentDaysInfo incomePaymentDaysInfo, String accessToken);

    /**
     * 移除多装多卸货物客户表
     * @param orderId
     */
    void moveTransitHis(Long orderId);

    /**
     * 修改预付状态
     * @param list
     * @param accessToken
     */
    void doSetPreFeeZeroToRedis(List<OrderListOut> list, String accessToken);

    /**
     * 获取用户成本和收入的权限
     * @param accessToken
     * @return
     */
    HasPermissionDto getIncomeAndCostPermission(String accessToken);

    //************************************************hzx   结束************************************

    /**
     * 获取异常处理订单列表-导出
     * @param orderListIn
     * @param accessToken
     * @param importOrExportRecords
     */
    void getDealOrderListExport(OrderListInVo orderListIn, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 获取历史订单信息
     * @param orderListInVo
     * @param accessToken
     * @return
     */
    List<OrderListOut> getOrderList(OrderListInVo orderListInVo, String accessToken);



    /**
     * 校验预付款输入的油卡号
     */
    void verifyOilCardNum(OrderInfo orderInfo, List<String> oilCardNums, String accessToken);

    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void sucess(Long busiId, String desc, Map paramsMap, String accessToken);

    /**
     * 流程结束，审核不通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void fail(Long busiId, String desc,String auditCode, Map paramsMap, String accessToken);


    /**
     * APP查询订单详情 接口编码:30002
     * orderId 订单号 isHis 是否历史单
     * @return
     */
    OrderDetailsAppDto queryOrderDetailsAppOut(Long orderId, Integer isHis, Integer selectType, boolean isTransfer, String accessToken);

    /**
     * 获取修改订单详情
     * updateType:1、修改基础信息；2、修改收入信息；3、修改调度信息；4、修改归属信息;
     * 接口编号：30072
     */
    GetOrderDetailForUpdateDto getOrderDetailForUpdate(Long orderId, Integer updateType, String accessToken);

    /**
     * 修改订单
     * 接口编号：30073
     */
    void updateOrder(UpdateOrderVo updateOrderVo, String accessToken);

}
