package com.youming.youche.market.business.controller.facilitator;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.market.api.facilitator.IOilPriceProvinceService;
import com.youming.youche.market.domain.facilitator.OilPriceProvince;
import com.youming.youche.market.dto.facilitator.OilPricePeovinceDto;
import com.youming.youche.market.dto.facilitator.OilPriceProvinceDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 全国油价表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-08
 */
@RestController
@RequestMapping("/facilitator/oilpriceprovince/data/info")
public class OilPriceProvinceController extends BaseController<OilPriceProvince, IOilPriceProvinceService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OilPriceProvinceController.class);
    @DubboReference(version = "1.0.0")
    IOilPriceProvinceService oilPriceProvinceService;

    @Override
    public IOilPriceProvinceService getService() {
        return oilPriceProvinceService;
    }

    /**
     * 全国油价分页查询
     *
     * @param pageNum    分页参数
     * @param pageSize   分页参数
     * @param provinceId 省id
     * @return
     */
    @GetMapping("queryOilPrice")
    public ResponseResult queryOilPrice(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                        @RequestParam(value ="provinceId",defaultValue = "-1") Long provinceId) {
        try {
            Page<OilPriceProvince> oilPriceProvincePage = oilPriceProvinceService.queryOilPrice(provinceId, pageNum, pageSize);
            return ResponseResult.success(oilPriceProvincePage);
        } catch (Exception e) {
            return ResponseResult.failure("网络异常");
        }
    }

    /**
     * 修改油价
     * @param
     * @return
     */
    @PostMapping("edit")
    public ResponseResult edit(@RequestBody OilPriceProvinceDto oilPriceProvince){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ResponseResult responseResult = oilPriceProvinceService.updateOilPriceProvince(oilPriceProvince,accessToken);
        return responseResult;
    }


    /**
     * 油价导入
     * @param file
     * @return
     */
    @PostMapping("OilPriceExcel")
    public ResponseResult OilPriceExcel(MultipartFile file){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            InputStream inputStream = file.getInputStream();
            List<OilPricePeovinceDto> list=new ArrayList<>();
            EasyExcel.read(inputStream, OilPricePeovinceDto.class,new AnalysisEventListener<OilPricePeovinceDto>(){

                @Override
                public void invoke(OilPricePeovinceDto oilPriceProvince, AnalysisContext analysisContext) {
                    list.add(oilPriceProvince);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    LOGGER.info("成功导入"+list.size()+"条");
                }
            }).sheet().doRead();
           return   oilPriceProvinceService.importOilPriceExcel(list,accessToken);
        } catch (IOException e) {
          return ResponseResult.failure("网络异常");
        }
    }
}
