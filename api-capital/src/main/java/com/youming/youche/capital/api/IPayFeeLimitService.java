package com.youming.youche.capital.api;

import com.youming.youche.capital.domain.PayFeeLimit;
import com.youming.youche.capital.vo.PayFeeLimitVo;
import com.youming.youche.commons.base.IBaseService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author Terry
* @since 2022-03-02
*/
public interface IPayFeeLimitService extends IBaseService<PayFeeLimit> {

    /**
     * 查询资金信息
     * @param accessToken
     * @param codeType
     * @param codeDesc
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    List<PayFeeLimitVo> queryPayFeeLimitCfg(String accessToken, String codeType, String codeDesc) throws InvocationTargetException, IllegalAccessException;

    /**
     * 根据订单id查询借支上线
     */
    List<PayFeeLimitVo>  queryPayFeeLimitCfgOrder(String codeType, String codeDesc,Long tenantId);
}
