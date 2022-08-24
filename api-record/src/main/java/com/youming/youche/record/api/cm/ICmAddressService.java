package com.youming.youche.record.api.cm;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.cm.Address;
import com.youming.youche.record.dto.cm.CmAddressDto;
import com.youming.youche.record.vo.cm.CmAddressVo;

/**
 * <p>
 * 地址库 服务类
 * </p>
 *
 * @author 向子俊
 * @since 2021-11-22
 */
public interface ICmAddressService extends IBaseService<Address> {

    /**
     * 查询所有地址
     */
    Page<CmAddressDto> findAllAddress(Page<CmAddressDto> page, CmAddressVo addressVo, String accessToken) throws Exception;

    /**
     * @return java.lang.Integer
     * @author 向子俊
     * @Description //TODO 新增地址档案
     * @date 11:13 2022/2/16 0016
     * @Param [addressVo, accessToken]
     */
    Integer saveOneAddress(CmAddressVo addressVo, String accessToken) throws Exception;

    /**
     * 线路地址修改回显
     */
    CmAddressDto findAddressById(CmAddressVo addressVo, String accessToken) throws Exception;

    /**
     * 修改地址档案
     */
    boolean modifyAddress(CmAddressVo addressVo, String accessToken) throws Exception;

    /**
     * 移除地址档案
     */
    Integer removeAddress(Long id, String accessToken) throws Exception;

}
