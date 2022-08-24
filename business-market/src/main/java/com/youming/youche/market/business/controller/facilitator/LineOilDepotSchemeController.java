package com.youming.youche.market.business.controller.facilitator;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.api.facilitator.ILineOilDepotSchemeService;
import com.youming.youche.market.domain.facilitator.LineOilDepotScheme;
import com.youming.youche.market.dto.facilitator.LineOilQueryDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-25
*/
@RestController
@RequestMapping("lineOil/depotScheme")
public class LineOilDepotSchemeController extends BaseController<LineOilDepotScheme, ILineOilDepotSchemeService> {

    @DubboReference(version = "1.0.0")
    ILineOilDepotSchemeService lineOilDepotSchemeService;
    @Override
    public ILineOilDepotSchemeService getService() {
        return lineOilDepotSchemeService;
    }

    /**
     * 录入订单页面智能模式中油站分页条件查询
     *
     * @param oilName  油站名称
     * @param pageNum  分页参数
     * @param pageSize 分页参数
     * @return
     */
    @GetMapping("getLineOilDepotSchemeByLineId")
    public ResponseResult getLineOilDepotSchemeByLineId(@RequestParam("oilName")String oilName,
                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            IPage<LineOilDepotScheme> lineOilDepotSchemeByLineId = lineOilDepotSchemeService.getLineOilDepotSchemeByLineId(oilName,accessToken, pageNum, pageSize);
        return ResponseResult.success(lineOilDepotSchemeByLineId);

    }

    /**
     * 录入订单页面智能模式中油站分页条件查询
     *
     * @param pageNum  分页参数
     * @param pageSize 分页参数
     */
    @GetMapping("getLineOilDepotSchemeByLineIdPageInfo")
    public ResponseResult getLineOilDepotSchemeByLineId(LineOilQueryDto lineOilQueryDto,
                                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return   ResponseResult.success(
                lineOilDepotSchemeService.getLineOilDepotSchemeByLineId(accessToken,lineOilQueryDto,pageNum, pageSize)
        );
    }

    /**
     * 录入订单页面智能模式中油站分页条件推荐查询（50KM范围内）
     *
     * @param oilName  油站名称
     * @param lineId   线路id
     * @param pageNum  分页参数
     * @param pageSize 分页参数
     * @return
     */
    @GetMapping("recommendLineOilDepotSchemeByLineId")
    public ResponseResult recommendLineOilDepotSchemeByLineId(@RequestParam("oilName")String oilName,
                                                              @RequestParam("lineId")Long lineId,
                                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<LineOilDepotScheme> lineOilDepotSchemeByLineId = lineOilDepotSchemeService.recommendLineOilDepotSchemeByLineId(oilName,lineId, accessToken, pageNum, pageSize);
        return ResponseResult.success(lineOilDepotSchemeByLineId);

    }

    /**
     * 微信查询油站(30029)
     * @param lineOilQueryDto
     * @return
     */
    @GetMapping("/getLineOilDepotWX")
    public ResponseResult getLineOilDepotWX(LineOilQueryDto lineOilQueryDto,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        if(lineOilQueryDto.getType() < 0 ){
            throw new BusinessException("请传入查询类型！");
        }
        if(lineOilQueryDto.getType() == 1){
            if(lineOilQueryDto.getLineId() == null || lineOilQueryDto.getLineId()<0){
                throw new BusinessException("请传入线路ID");
            }
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return   ResponseResult.success(
                lineOilDepotSchemeService.getLineOilDepotSchemeByLineId(accessToken,lineOilQueryDto,pageNum, pageSize)
        );
    }
}
