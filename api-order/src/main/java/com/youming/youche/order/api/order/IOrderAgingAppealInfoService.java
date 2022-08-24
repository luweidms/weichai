package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderAgingAppealInfo;

import java.util.Map;

/**
 * <p>
 * 时效罚款申诉表 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-03-28
 */
public interface IOrderAgingAppealInfoService extends IBaseService<OrderAgingAppealInfo> {

    /**
     * 根据时效查询申诉
     *
     * @param agingId 时效Id
     * @param isApp   是否APP
     */
    OrderAgingAppealInfo getAppealInfoBYAgingId(Long agingId, boolean isApp);

    /**
     * 获取时效罚款申诉
     *
     * @param agingId 时效罚款id
     * @return
     */
    OrderAgingAppealInfo getAppealInfoBYAgingId(Long agingId);

    /**
     * 30056 WX接口-申诉第一次审核
     * @param appealId
     * @param verifyDesc
     * @param dealFinePrice
     */
    void verifyFirst(Long appealId,String verifyDesc,Long dealFinePrice, String accessToken);

    /**
     * 审核不通过
     * @param verifyDesc
     * @throws Exception
     */
    void verifyFail(Long appealId, String verifyDesc,String accessToken);

    /**
     * 审核通过
     * @param busiId
     * @param desc
     * @param paramsMap
     * @param accessToken
     */
    void sucess(Long busiId, String desc, Map paramsMap, String accessToken);

    /**
     * 审核通过
     * @param appealId
     * @param verifyDesc
     * @param isFirst
     * @param accessToken
     */
    void success(Long appealId,String verifyDesc,Boolean isFirst, String accessToken);

}
