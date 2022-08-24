//package com.youming.youche.record.provider.mapper.sys;
//
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.youming.youche.commons.domain.SysCfg;
//import org.apache.ibatis.annotations.Param;
//
///**
// * <p>
// * 系统配置表Mapper接口
// * </p>
// *
// * @author Terry
// * @since 2021-11-21
// */
//public interface SysCfgRecordMapper extends BaseMapper<SysCfg> {
//
//    SysCfg getCfgBooleanVal(@Param("cfgName") String cfgName);
//
//    String getSysCfgStrByCfgName(@Param("cfgName") String cfgName);
//
//    Double getCfgVal(@Param("tenantId") Long tenantId,
//                     @Param("cfgName") String cfgName,
//                     @Param("system") Integer system);
//}
//
