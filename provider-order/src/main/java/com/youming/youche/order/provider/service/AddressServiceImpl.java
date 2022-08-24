package com.youming.youche.order.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.order.api.IAddressService;
import com.youming.youche.order.domain.Address;
import com.youming.youche.order.provider.mapper.AddressMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
* <p>
    * 地址库 服务实现类
    * </p>
* @author liangyan
* @since 2022-03-12
*/
@DubboService(version = "1.0.0")
public class AddressServiceImpl extends BaseServiceImpl<AddressMapper, Address> implements IAddressService {

    @Resource
    LoginUtils loginUtils;
    @Resource
    AddressMapper addressMapper;
    @Override
    public IPage<Address> queryAddress(Integer pageNum,Integer pageSize,String addressName, String addressDetail, String addressShow, String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("TENANT_ID",tenantId)
                .like("ADDRESS_NAME",addressName)
                .like("ADDRESS_DETAIL",addressDetail)
                .like("ADDRESS_SHOW",addressShow);
        List<Address> addresses = addressMapper.selectList(addressQueryWrapper);
        Page<Address> addressPageddress= null;
        addressPageddress = new Page<>();
        addressPageddress.setRecords(addresses);
        addressPageddress.setTotal(addresses.size());
        addressPageddress.setSize(pageSize);
        addressPageddress.setCurrent(pageNum);
        return addressPageddress;
    }

    @Override
    public IPage<Address> queryAddressBykeywords(Integer pageNum,Integer pageSize,String keywords, String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        QueryWrapper<Address> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("TENANT_ID",tenantId)
                .like(StringUtils.isNotBlank(keywords),"ADDRESS_NAME",keywords)
                .or()
                .like(StringUtils.isNotBlank(keywords),"ADDRESS_DETAIL",keywords)
                .or()
                .like(StringUtils.isNotBlank(keywords),"ADDRESS_SHOW",keywords);
        List<Address> addresses = addressMapper.selectList(addressQueryWrapper);
        Page<Address> addressPageddress= null;
        addressPageddress = new Page<>();
        addressPageddress.setRecords(addresses);
        addressPageddress.setTotal(addresses.size());
        addressPageddress.setSize(pageSize);
        addressPageddress.setCurrent(pageNum);

        return addressPageddress;
    }
}
