package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsDto;
import com.youming.youche.order.vo.OilRechargeAccountDetailsVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IOilRechargeAccountDetailsService extends IBaseService<OilRechargeAccountDetails> {

    /**
     *
     * @Function: com.business.pt.ac.service.IOilRechargeAccountSV.java::getOilRechargeAccountDetail
     * @Description: 该函数的功能描述:查询油充值账户信息（明细）
     * @param userId
     * @param pinganAccId
     * @param sourceType
     * @return
     * @throws Exception
     * @version: v1.1.0
     * @author:huangqb
     * @date:2019年6月17日 上午10:08:58
     * Modification History:
     *   Date         Author          Version            Description
     *-------------------------------------------------------------
     *
     */
    List<OilRechargeAccountDetails> getOilRechargeAccountDetail(Long userId, String pinganAccId,
                                                                Long sourceUserId, Integer sourceType,
                                                                String vehicleAffiliation);


    /**
     * 查询充值账户信息
     * @param userId
     * @param pinganAccId
     * @param sourceUserId
     * @param sourcePinganAccId
     * @param sourceType
     * @return
     */
    OilRechargeAccountDetails getOilRechargeAccountDetail(Long userId,String pinganAccId,Long sourceUserId,
                                                          String sourcePinganAccId,Integer sourceType);

    /**
     * niejeiwei
     * 预存资金明细-小程序接口
     * 50058
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<OilRechargeAccountDetailsDto>  getLockBalanceDetailsOrder(OilRechargeAccountDetailsVo vo, String accessToken,Integer pageNum,
                                                                   Integer pageSize);

    /**
     * 查询订单收款人
     * @param accessToken
     * @param orderId
     * @return
     */
    List<OilRechargeAccountDetailsDto> queryOrderPayee (String accessToken , Long orderId);
}
