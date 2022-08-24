package com.youming.youche.record.provider.service.impl.cm;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.record.api.cm.ICmAddressService;
import com.youming.youche.record.domain.cm.Address;
import com.youming.youche.record.dto.cm.CmAddressDto;
import com.youming.youche.record.provider.mapper.cm.CmAddressMapper;
import com.youming.youche.record.vo.cm.CmAddressVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.constant.SysOperLogConst;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * <p>
 * 地址库 服务实现类
 * </p>
 *
 * @author 向子俊
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class CmAddressServiceImpl extends BaseServiceImpl<CmAddressMapper, Address>
		implements ICmAddressService {
	@Autowired
	private CmAddressMapper addressMapper;
	@DubboReference(version = "1.0.0")
	ISysOperLogService sysOperLogService;
	@Resource
	LoginUtils loginUtils;
	/**
	 * @author 向子俊
	 * @Description //TODO 查询所有地址
	 * @date 10:40 2022/2/15 0015
	 * @Param [page, addressVo, accessToken]
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.youming.youche.record.domain.cm.Address>
	 */
	@Override
	public Page<CmAddressDto> findAllAddress(Page<CmAddressDto> page, CmAddressVo addressVo, String accessToken) throws Exception {
		LoginInfo user = loginUtils.get(accessToken);
		addressVo.setTenantId(user.getTenantId());
		Page<CmAddressDto> cmAddressPage = addressMapper.queryAllAddress(page, addressVo);
		return cmAddressPage;
	}

	/**
	 * @author 向子俊
	 * @Description //TODO 新增地址
	 * @date 14:23 2022/2/15 0015
	 * @Param [addressVo, accessToken]
	 * @return java.lang.Integer
	 */
	@Override
	public Integer saveOneAddress(CmAddressVo addressVo, String accessToken) throws Exception {
		LoginInfo user = loginUtils.get(accessToken);
		addressVo.setTenantId(user.getTenantId());
		Integer result;
		Integer isExist = addressMapper.checkIsExist(user.getTenantId(), addressVo.getAddressName());
		if (isExist != null && isExist>0){
			result = -1;
		}else {
			result = addressMapper.insertAddress(addressVo);
			saveSysOperLog(SysOperLogConst.BusiCode.Address, SysOperLogConst.OperType.Add, "地址档案新增", accessToken, addressVo.getId());
		}

		return result;
	}

	@Override
	public CmAddressDto findAddressById(CmAddressVo addressVo, String accessToken) throws Exception {
		LoginInfo user = loginUtils.get(accessToken);
		addressVo.setTenantId(user.getTenantId());
		return addressMapper.queryAddressById(addressVo);
	}

	@Override
	public boolean modifyAddress(CmAddressVo addressVo, String accessToken) throws Exception {
		LoginInfo user = loginUtils.get(accessToken);
		addressVo.setTenantId(user.getTenantId());
		//Integer updateAddress = addressMapper.updateAddress(addressVo);
		Address address = new Address();
		BeanUtil.copyProperties(addressVo, address);
		boolean flag = super.updateById(address);
		saveSysOperLog(SysOperLogConst.BusiCode.Address, SysOperLogConst.OperType.Update, "地址档案修改", accessToken, addressVo.getId());
		return flag;
	}

	@Override
	public Integer removeAddress(Long id, String accessToken) throws Exception {
		LoginInfo user = loginUtils.get(accessToken);
		Long tenantId = user.getTenantId();
		return addressMapper.deleteAddressById(tenantId,id);
	}

	/**
	 * 记录日志
	 */
	private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
		SysOperLog operLog = new SysOperLog();
		operLog.setBusiCode(busiCode.getCode());
		operLog.setBusiName(busiCode.getName());
		operLog.setBusiId(busid);
		operLog.setOperType(operType.getCode());
		operLog.setOperTypeName(operType.getName());
		operLog.setOperComment(operCommet);
		sysOperLogService.save(operLog, accessToken);
	}
}
