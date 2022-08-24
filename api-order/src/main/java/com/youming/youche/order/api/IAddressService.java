package com.youming.youche.order.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.Address;

import java.util.List;

/**
* <p>
    * 地址库 服务类
    * </p>
* @author liangyan
* @since 2022-03-12
*/
    public interface IAddressService extends IBaseService<Address> {
    /**
     * 通过地址名称（addressName）、导航地址（addressDetail）、应到地址（addressShow）、租户id（tenantId）模糊查询地址
     * @param addressName
     * @param addressDetail
     * @param addressShow
     * @param accessToken
     * @return
     */
    IPage<Address> queryAddress(Integer pageNum, Integer pageSize, String addressName, String addressDetail, String addressShow, String accessToken);

    /**
     * 通过关键字模糊查询地址
     * @param keywords
     * @param accessToken
     * @return
     */
    IPage<Address> queryAddressBykeywords(Integer pageNum,Integer pageSize,String keywords,String accessToken);

}
