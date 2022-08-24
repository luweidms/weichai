package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.OilEntity;
import com.youming.youche.finance.vo.OilEntityInfoDto;
import com.youming.youche.finance.vo.OilEntityVo;
import org.apache.ibatis.annotations.Param;

/**
* <p>
* 油充值核销表Mapper接口
* </p>
* @author WuHao
* @since 2022-04-14
*/
    public interface OilEntityMapper extends BaseMapper<OilEntity> {

    IPage<OilEntityInfoDto> selectOr(@Param("oilEntityVo") OilEntityVo oilEntityVo, @Param("tenantId") Long tenantId,Page<Object> objectPage);
}
