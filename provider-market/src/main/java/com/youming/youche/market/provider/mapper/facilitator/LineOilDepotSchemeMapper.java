package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.LineOilDepotScheme;
import com.youming.youche.market.dto.facilitator.LineOilDepotSchemeDataDto;
import com.youming.youche.market.dto.facilitator.LineOilDepotSchemeDataInfoDto;
import com.youming.youche.market.dto.facilitator.LineOilQueryDto;
import com.youming.youche.market.dto.facilitator.LineOilQueryInDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper接口
 * </p>
 *
 * @author liangyan
 * @since 2022-03-25
 */
public interface LineOilDepotSchemeMapper extends BaseMapper<LineOilDepotScheme> {

    List<LineOilDepotSchemeDataInfoDto> getLineOilDepotSchemeByLineId(@Param("lineOilQueryIn") LineOilQueryInDto lineOilQueryIn, @Param("tenantId") Long tenantId);


    Page<LineOilDepotSchemeDataDto> getLineOilDepotSchemeByLineIdInfo(Page<LineOilDepotSchemeDataDto> page,
                                                                      @Param("user") LoginInfo user,
                                                                      @Param("lineOilQueryDto") LineOilQueryDto lineOilQueryDto,
                                                                      @Param("oilIdList") List<Long> oilIdList);
}
