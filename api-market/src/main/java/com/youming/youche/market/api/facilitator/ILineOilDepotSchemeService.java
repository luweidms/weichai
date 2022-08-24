package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.facilitator.LineOilDepotScheme;
import com.youming.youche.market.dto.facilitator.LineOilDepotSchemeDataDto;
import com.youming.youche.market.dto.facilitator.LineOilDepotSchemeDto;
import com.youming.youche.market.dto.facilitator.LineOilQueryDto;
import com.youming.youche.market.dto.facilitator.LineOilQueryInDto;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author liangyan
* @since 2022-03-25
*/
public interface ILineOilDepotSchemeService extends IBaseService<LineOilDepotScheme> {

    /**
     * 根据线路查询油站分配(线路)单一线路
     * @param lineId
     * @return
     */
    IPage<LineOilDepotScheme> getLineOilDepotSchemeByLineId(String oilName,String accessToken,Integer pageNum,Integer pageSize);

    /**
     * 录入订单页面智能模式中油站分页条件查询
     *
     * @param pageNum  分页参数
     * @param pageSize 分页参数
     */
    Page<LineOilDepotSchemeDataDto> getLineOilDepotSchemeByLineId(String accessToken, LineOilQueryDto lineOilQueryDto,Integer pageNum,Integer pageSize);
    /**
     * 根据线路查询油站分配(线路)单一线路(推荐油站50KM范围内)
     * @param lineId
     * @return
     */
    IPage<LineOilDepotScheme> recommendLineOilDepotSchemeByLineId(String oilName,Long lineId, String accessToken,Integer pageNum,Integer pageSize);



    /**
     * 根据线路查询油站分配(线路)多条件
     * @return
     * @throws Exception
     */
    List<LineOilDepotSchemeDto> getLineOilDepotSchemeByLineId(LineOilQueryInDto lineOilQueryIn, Long tenantId);
}
