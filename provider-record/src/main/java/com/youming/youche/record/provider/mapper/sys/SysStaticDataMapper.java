//package com.youming.youche.record.provider.mapper.sys;
//
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.youming.youche.record.domain.sys.SysStaticData;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//
///**
// * <p>
// * 静态数据表Mapper接口
// * </p>
// *
// * @author Terry
// * @since 2021-11-21
// */
//public interface SysStaticDataMapper extends BaseMapper<SysStaticData> {
//
//    List<SysStaticData> getSysStaticDataListByCodeName(@Param("codeType") String codeType);
//
//    SysStaticData getSysStaticDataCodeName(@Param("codeType") String codeType,
//                                                 @Param("codeValue") Integer codeValue);
//
//    String getVehicelLength(@Param("codeType")String codeType,
//                            @Param("codeTypeAlias")String codeTypeAlias);
//}
