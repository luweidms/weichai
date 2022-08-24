package com.youming.youche.market.provider.mapper.youka;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.market.domain.youka.OilCardLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.dto.youca.OilCardLogDto;
import com.youming.youche.market.vo.youca.OilLogVo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
* <p>
* Mapper接口
* </p>
* @author XXX
* @since 2022-03-24
*/
    public interface OilCardLogMapper extends BaseMapper<OilCardLog> {


    IPage<OilCardLogDto> doQuery(Page<OilCardLogDto> pageInfo, @Param("oilLogVo") OilLogVo oilLogVo,
                                 @Param("tenantId") Long tenantId,
                                 @Param("startTime1")LocalDateTime startTime1,
                                 @Param("endTime1")LocalDateTime endTime1);
}
