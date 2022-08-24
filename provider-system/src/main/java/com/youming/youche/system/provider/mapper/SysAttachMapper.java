package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.SysAttach;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 图片资源表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-17
 */
public interface SysAttachMapper extends BaseMapper<SysAttach> {


    @Select("SELECT a.* FROM sys_attach a" + " LEFT JOIN sys_resource_business srb ON a.id = srb.resource_id"
            + " WHERE srb.business_id = #{businessId}" + " AND srb.state = 1" + " AND srb.business_code = #{businessCode}")
    List<SysAttach> selectAllByBusinessIdAndBusinessCode(Long businessId, Integer businessCode);
}
