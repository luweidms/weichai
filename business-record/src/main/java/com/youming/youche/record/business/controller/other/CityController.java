package com.youming.youche.record.business.controller.other;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.record.api.other.ICityService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 城市表 前端控制器
 * </p>
 *
 * @author hzx
 * @since 2022-05-13
 */
@RestController
@RequestMapping("city")
public class CityController extends BaseController {

    @DubboReference(version = "1.0.0")
    ICityService iCityService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 省市县区数据(11015)
     */
    @PostMapping("queryAddressInfo")
    public ResponseResult queryAddressInfo() {
        return ResponseResult.success(
                iCityService.queryAddressInfo()
        );
    }

    /**
     * 通过省市区名称获取id
     *
     * @param type     1 省 2 市 3 区
     * @param name     城市名臣
     * @param parentId 市区归属省市id
     * @return 接口编码：11017
     */
    @GetMapping("getProvinceName")
    public ResponseResult getProvinceName(Integer type, String name, Long parentId) {
        return ResponseResult.success(
                iCityService.getProvinceName(type, name, parentId)
        );
    }

}
