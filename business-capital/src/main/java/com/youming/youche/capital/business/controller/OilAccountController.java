package com.youming.youche.capital.business.controller;


import com.youming.youche.capital.api.IOilAccountService;
import com.youming.youche.capital.vo.AccountOilFixedDetailVo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account/oil/")
public class OilAccountController extends BaseController {
    @DubboReference(version = "1.0.0")
    private IOilAccountService iOilAccountService;

    /**
     * 车队定点油账户统计，包括总的可用额度，限额的统计、不限额的统计
     * */
    @PostMapping("fixed/info")
    public ResponseResult queryFixedInfo(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(iOilAccountService.queryFixedInfo(accessToken));
    }

    /**
     * 车队定点油账户详情
     * */
    @PostMapping("fixed/detail/list")
    public ResponseResult queryFixedDetail(@RequestBody AccountOilFixedDetailVo accountOilFixedDetailVo) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(iOilAccountService.queryFixedDetail(accessToken,accountOilFixedDetailVo));
    }

    @Override
    public IOilAccountService getService() {
        return iOilAccountService;
    }
}
