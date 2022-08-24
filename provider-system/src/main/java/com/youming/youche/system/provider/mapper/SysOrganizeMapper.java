package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.SysOrganize;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 组织表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
public interface SysOrganizeMapper extends BaseMapper<SysOrganize> {

    List<SysOrganize> selectAllByUserInfoId(@Param("userInfoId") Long userInfoId,@Param("tenantId")Long tenantId);
}
