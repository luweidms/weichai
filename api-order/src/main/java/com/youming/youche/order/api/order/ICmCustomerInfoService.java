package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.CmCustomerInfo;
import com.youming.youche.order.dto.CustomerDto;
import com.youming.youche.order.vo.CustParamVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户信息表/客户档案表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
public interface ICmCustomerInfoService extends IBaseService<CmCustomerInfo> {

    /***
     *
     * @param inParam
     * 客户信息
     * companyName 公司名称(全称)
     * customerName 客户简称
     * lineName     联系人
     * lineTel      联系人电话
     * address      地址
     * yongyouCode  财务系统编号
     * reciveProvinceId 省id
     * reciveCityId 市id
     * reciveAddress 详细地址
     * 线路信息暂时不需要
     * @return Y-业务成功 N-业务失败
     * @throws Exception
     */
    Long doSaveTmpOrderCustLineInfo(CustomerDto inParam, LoginInfo user);




    /**
     * 根据机构列表查询客户信息
     * @param customerId 客户编号
     * @param orgList 机构列表
     * @return
     * @throws Exception
     */
    CmCustomerInfo queryCustomerForOrderTask(long customerId,
                                             List<Long> orgList, LoginInfo user);

    /**
     * 根据ID 查询客户信息
     * @param customerId
     * @param isEdit
     * @return
     */
    Object getCustomerInfo(Long customerId,Integer isEdit, String accessToken);

}
