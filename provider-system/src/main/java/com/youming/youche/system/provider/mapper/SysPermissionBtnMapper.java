package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.SysPermissionBtn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色权限表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-21
 */
public interface SysPermissionBtnMapper extends BaseMapper<SysPermissionBtn> {

    Integer insertBatchByBtnIds(@Param("permissionId")Long permissionId, @Param("sysBtnIds") List<Long> sysBtnIds);
}
