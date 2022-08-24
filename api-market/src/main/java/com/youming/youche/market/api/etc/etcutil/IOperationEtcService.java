package com.youming.youche.market.api.etc.etcutil;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.etc.CmEtcInfo;

/**
 * ETC相关操作接口
 * 聂杰伟
 */

public interface IOperationEtcService extends IBaseService<CmEtcInfo> {


    /**
     * ETC消费接口   (招商车/外调车)
     * @param cmEtcInfo  ETC消费对象
     * @param tenantId 租户id
     * @throws Exception
     * @return void
     */
    void consumeETC(CmEtcInfo cmEtcInfo, Long tenantId,long soNbr,String accessToken) ;


    /**
     *  ETC消费接口(自有车)
     * @param cmEtcInfo
     * @param tenantId
     * @throws Exception
     */
    void consumeETCHave(CmEtcInfo cmEtcInfo, Long tenantId,long soNbr,String accessToken) ;

    /***
     * 招商车挂靠车ETC扣款
     */
    void consumeETCInvestment(CmEtcInfo cmEtcInfo, Long tenantId,long soNbr,String accessToken);

}
