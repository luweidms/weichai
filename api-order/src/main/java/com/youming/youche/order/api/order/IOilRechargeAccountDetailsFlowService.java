package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsFlowOutDto;
import com.youming.youche.order.vo.OilRechargeAccountDetailsFlowVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IOilRechargeAccountDetailsFlowService extends IBaseService<OilRechargeAccountDetailsFlow> {

    /**
     * 获取订单的流水
     * @param userId
     * @param orderNum
     * @param busiType
     * @param orderBy
     * @return
     */
    List<OilRechargeAccountDetailsFlow> getOrderDetailsFlows(Long userId, String orderNum, Integer busiType, Integer orderBy);


    /**
     * 获取未分配的流水，按创建时间升序
     * @param userId
     * @param sourceType
     * @return
     */
    List<OilRechargeAccountDetailsFlow> getUnMatchedFlowsASC(Long userId,Integer sourceType);

    /**
     * 获取油品公司充值账户流水
     * @param busiCode 业务单号
     * @param sourceType 账户类型 1.返利 2现金 3授权 4继承  静态类OIL_RECHARGE_SOURCE_TYPE
     */
    OilRechargeAccountDetailsFlow getRechargeDetailsFlows(String busiCode,int sourceType);

    /**
     * 成功之后回调
     * @param busiCode
     * @param isAutomatic
     */
    void payRechargeSucess(String busiCode, int isAutomatic);

    List<OilRechargeAccountDetailsFlow> getDetailsFlows(String busiCode);

    /**
     * niejiwei
     * 司机小程序
     * 充值流水列表-小程序接口
     * 50057
     * @return
     */
    Page<OilRechargeAccountDetailsFlowOutDto> getOilRechargeAccountDetailsFlow(OilRechargeAccountDetailsFlowVo vo ,
                                                                               Integer pageNum, Integer pageSize, String accessToken );

}
