package com.youming.youche.system.business.controller.pub;


import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.system.api.pub.IPubPayProvService;
import com.youming.youche.system.dto.pub.PubPayDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 平安省份表 前端控制器
 * </p>
 *
 * @author hzx
 * @since 2022-05-12
 */
@RestController
@RequestMapping("pub/pay/prov")
public class PubPayProvController extends BaseController {

    @DubboReference(version = "1.0.0")
    IPubPayProvService iPubPayProvService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 省市县区数据(10056)
     */
    @PostMapping("queryAddressInfo")
    public ResponseResult queryAddressInfo() {
        List<PubPayDto> pubPayDtos = iPubPayProvService.queryAddressInfo();
        return ResponseResult.success(pubPayDtos);
    }

}
