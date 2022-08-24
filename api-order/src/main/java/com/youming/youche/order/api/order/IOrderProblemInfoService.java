package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.ProblemVerOpt;
import com.youming.youche.order.dto.OrderInfoListDto;
import com.youming.youche.order.dto.OrderProblemInfoDto;
import com.youming.youche.order.dto.OrderProblemInfoOutDto;
import com.youming.youche.order.dto.SaveProblemInfoDto;
import com.youming.youche.order.vo.QueryOrderProblemInfoQueryVo;

import java.util.List;

/**
* <p>
    * 订单异常登记表 服务类
    * </p>
* @author liangyan
* @since 2022-03-22
*/
    public interface IOrderProblemInfoService extends IBaseService<OrderProblemInfo> {

    /***
     * 根据订单号查询异常
     * @param orderId 订单号
     * @param tenantId
     * @return
     */
    List<OrderProblemInfo> getOrderProblemInfoByOrderId(Long orderId, Long tenantId);

    /**
     * 订单是否有异常
     * @param orderId
     * @return
     * @throws Exception
     */
    boolean isExistProblemInfoInfo(Long orderId);

    /**
     * 保存转单异常
     * @param tenantId 接单方车队ID
     * @param orderId 接单方订单ID
     * @param fromOrderId 来源方订单ID
     * @throws Exception
     */
    void saveTransferInfoProblem(Long tenantId,Long orderId,Long fromOrderId)throws Exception;

    /**
     * 查询异常信息
     */
    @SuppressWarnings("rawtypes")
    Page<OrderProblemInfo> queryOrderProblemInfoPag(Long orderId, Long problemId,
                                                    String state, String problemType,
                                                    Integer problemCondition,String accessToken,
                                                    Integer pageNum, Integer pageSize);


    /**
     * 根据订单号获取订单信息
     * @param orderId
     * @return
     */
    OrderInfoListDto getOrder(Long orderId);

    /**
     * 保存订单异常信息
     * @param saveProblemInfoDto
     * @return
     */
    boolean saveOrUpdateOrderProblemInfo(SaveProblemInfoDto saveProblemInfoDto,String accessToken);


    /**
     *根据ID获取异常信息
     * @param problemId
     * @return
     */
    OrderProblemInfoDto getOrderProblemInfo(Long problemId);


    /**
     * 审核通过
     * @param problemId 异常ID
     * @param verifyDesc 审核备注
     * @param problemDealPrice 处理金额
     * @param isSave 是否新增时通过
     */
    void verifyPass(Long problemId, String verifyDesc, String problemDealPrice, Boolean isSave, String nodeNum, LoginInfo loginInfo,String accessToken);

    /**
     * 异常审核通过流程
     * （1）更新异常处理状态
     * （2）支付异常款
     * （3）更新订单款支入、支出金额
     * @param orderProblemInfo
     * @param orderFee
     * @param IsTransit 是否外发
     * @throws Exception
     */
    void commonExceptionFlowPath(OrderProblemInfo orderProblemInfo, OrderFee orderFee, Boolean IsTransit,String accessToken,LoginInfo loginInfo);

    /**
     * 异常审核完结资金流
     * @param orderProblemInfo
     * @throws Exception
     */
    void payForException(OrderProblemInfo orderProblemInfo,Boolean IsTransit,String accessToken,LoginInfo loginInfo);


    /**
     * 取消异常
     * problemId 异常ID
     */
    void cancelProblem(Long problemId,String accessToken);

    /**
     * 获取异常审核日志
     * @param problemId 异常ID
     * @return
     */
    List<ProblemVerOpt> getProblemVerOptByProId(Long problemId,String accessToken);

    /**
     * 获取订单异常信息
     * @param orderId
     * @param userId
     * @return
     */
    List<OrderProblemInfo> getOrderProblemInfoByUserId(Long orderId,Long userId);

    /**
     * 查询异常审核记录
     */
    Integer getOrderProblemInfoByOrderIds(List<Long> orderIds);

    List<OrderProblemInfo> queryOrderProblemInfoList(Long orderId,boolean isAudit,boolean isWx,String accessToken);

    /**
     * App接口-异常补偿 28313
     */
    List<OrderProblemInfoOutDto> doQueryAbnormalCompensation(Long orderId, Long tenantId);

    /**
     * App接口-异常扣减 28315
     */
    List<OrderProblemInfoOutDto> doQueryAbnormalDeduction(Long orderId,Long tenantId);

    /**
     * 订单是否还有异常未审核完成
     * @param vo
     * @return
     */
    List<OrderProblemInfo> queryOrderProblemInfoQueryList(QueryOrderProblemInfoQueryVo vo);
}

