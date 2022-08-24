package com.youming.youche.capital.api;

import com.youming.youche.capital.domain.PayFeeLimitVer;
import com.youming.youche.capital.vo.PayFeeLimitVerVo;
import com.youming.youche.capital.vo.PayFeeLimitVo;
import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.exception.BusinessException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-03-03
 */
public interface IPayFeeLimitVerService extends IBaseService<PayFeeLimitVer> {

    /**
     * 查询审批失败原因
     * @return
     */
    List<PayFeeLimitVerVo> queryCheckFailMsg();

    /**
     * 查询资金风控版本信息
     * @param accessToken
     * @param codeType
     * @param codeDesc
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    List<PayFeeLimitVerVo> queryPayFeeLimitCfgUpdt(String accessToken, String codeType, String codeDesc) throws InvocationTargetException, IllegalAccessException;


    /**
     * 流程结束，审核通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    void sucess(Long busiId, String desc, Map paramsMap, String token) throws BusinessException;

    /**
     * 流程结束，审核不通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    void fail(Long busiId,String desc,Map paramsMap,String token)throws BusinessException;

    /**
     * 保存资金风控配置信息
     * @return
     * @throws Exception
     */
    String saveOrUpdate(String accessToken,List<PayFeeLimitVer> payFeeLimitVerList) throws Exception;


    /**
     * 初始化资金风控
     * @param tenantId
     * @return true 初始化成功
     */
    boolean initPayFeeLimit(Long tenantId,String accessToken) throws Exception;
}
