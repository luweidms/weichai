package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.commons.domain.SysCfg;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 系统配置表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
public interface SysCfgMapper extends BaseMapper<SysCfg> {

    SysCfg getCfgBooleanVal(@Param("cfgName") String cfgName);

    String getSysCfgStrByCfgName(@Param("cfgName") String cfgName);

    Double getCfgVal(@Param("tenantId") Long tenantId,
                     @Param("cfgName") String cfgName,
                     @Param("system") Integer system);

}
