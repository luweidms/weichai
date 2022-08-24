package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.commons.domain.SysStaticData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 静态数据表Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
public interface SysStaticDataMapper extends BaseMapper<SysStaticData> {
    List<SysStaticData> getSysStaticDataListByCodeName(@Param("codeType") String codeType);

    SysStaticData getSysStaticDataCodeName(@Param("codeType") String codeType,
                                           @Param("codeValue") Integer codeValue);
}
