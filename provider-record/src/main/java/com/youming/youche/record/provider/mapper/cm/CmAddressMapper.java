package com.youming.youche.record.provider.mapper.cm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.record.domain.cm.Address;
import com.youming.youche.record.dto.cm.CmAddressDto;
import com.youming.youche.record.vo.cm.CmAddressVo;
import org.apache.ibatis.annotations.Param;

public interface CmAddressMapper extends BaseMapper<Address> {
    /**
     * @author 向子俊
     * @Description //TODO 查询所有地址
     * @date 10:42 2022/2/15 0015
     * @Param [page, addressVo]
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.youming.youche.record.domain.cm.Address>
     */
    Page<CmAddressDto> queryAllAddress(Page<CmAddressDto> page,
                                    @Param("addressVo") CmAddressVo addressVo) throws Exception;

    /**
     * @author 向子俊
     * @Description //TODO 新增地址
     * @date 14:12 2022/2/15 0015
     * @Param [addressVo]
     * @return java.lang.Integer
     */
    Integer insertAddress(@Param("addressVo") CmAddressVo addressVo) throws Exception;
    /**修改回显*/
    CmAddressDto queryAddressById(@Param("addressVo")CmAddressVo addressVo) throws Exception;
    /**地址档案是否存在*/
    Integer checkIsExist(@Param("tenantId")Long tenantId,
                         @Param("addressName")String addressName) throws Exception;
    Integer updateAddress(@Param("addressVo")CmAddressVo addressVo) throws Exception;

    Integer deleteAddressById(@Param("tenantId")Long tenantId,
                              @Param("id")Long id) throws Exception;
}
