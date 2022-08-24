package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.ProblemVerOpt;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-29
 */
public interface IProblemVerOptService extends IBaseService<ProblemVerOpt> {
    /**
     * 获取审核日志
     *
     * @param proId    异常ID
     * @param tenantId 租户ID
     * @return
     */
    List<ProblemVerOpt> getProblemVerOptByProId(Long proId, Long tenantId);


    /**
     * 初次审核(需填写处理金额)
     *
     * @param problemId        异常ID
     * @param verifyDesc       审核信息
     * @param problemDealPrice 处理金额
     * @throws Exception
     */
    Boolean verifyFirst(Long problemId,
                        String verifyDesc,
                        String problemDealPrice,
                        String accessToken);


    /**
     * 审核失败
     *
     * @param busiId
     * @param desc   审核备注
     * @throws Exception
     */
    void fail(Long busiId, String desc, String accessToken,boolean flag);

    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     * @return
     */
    public void sucess(Long busiId, String desc, Map paramsMap,String accessToken);

    /**
     * 异常审核通过流程
     * （1）更新异常处理状态
     * （2）支付异常款
     * （3）更新订单款支入、支出金额
     *
     * @param orderProblemInfo
     * @param orderFee
     * @param IsTransit        是否外发
     * @throws Exception
     */
    void commonExceptionFlowPath(OrderProblemInfo orderProblemInfo, OrderFee orderFee, Boolean IsTransit, String accessToken);

    /**
     * 异常审核完结资金流
     *
     * @param orderProblemInfo
     * @throws Exception
     */
    void payForException(OrderProblemInfo orderProblemInfo, Boolean IsTransit, String accessToken);


}

