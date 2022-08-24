package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.DriverAndReceiverInfoOutDto;
import com.youming.youche.system.dto.OrganizeStaffDto;
import com.youming.youche.system.dto.UserDataInfoDto;
import com.youming.youche.system.dto.UserDataLinkManDto;

import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户资料信息Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-12-25
 */
public interface UserDataInfoMapper extends BaseMapper<UserDataInfo> {

	IPage<OrganizeStaffDto> selectByOrgIdAndTenantId(Page<UserDataInfo> page, @Param("orgId") Long orgId,
			@Param("tenantId") Long tenantId);


	Page<DriverAndReceiverInfoOutDto>  queryDriverAndReceiverInfo(Page<DriverAndReceiverInfoOutDto> page,
																  @Param("phone") String phone,
																  @Param("linkman") String linkman,
																  @Param("includeAllUser") Boolean includeAllUser);

   Page<UserDataLinkManDto> doQueryBackUserList(Page<UserDataLinkManDto> page,
											 @Param("linkman") String linkman,
											 @Param("mobilePhone") String mobilePhone,
											 @Param("user")LoginInfo user);
}
