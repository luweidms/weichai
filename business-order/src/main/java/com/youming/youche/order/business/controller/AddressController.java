package com.youming.youche.order.business.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.IAddressService;
import com.youming.youche.order.domain.Address;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 地址库 前端控制器
* </p>
* @author liangyan
* @since 2022-03-12
*/
@RestController
@RequestMapping("address")
public class AddressController extends BaseController<Address, IAddressService> {

    @DubboReference(version = "1.0.0")
    IAddressService iAddressService;
    @Override
    public IAddressService getService() {
        return iAddressService;
    }

    /**
     * @author liangyan
     * 通过地址名称（addressName）、导航地址（addressDetail）、应到地址（addressShow）、租户id（tenantId）模糊查询地址
     * @date 21:48 2022/3/12
     * @Param []
     */
    @GetMapping("/order/queryAddress")
    public ResponseResult queryAddress(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                       @RequestParam("addressName") String addressName,
                                       @RequestParam("addressDetail") String addressDetail,
                                       @RequestParam("addressShow") String addressShow) {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            IPage<Address> addressIPage = iAddressService.queryAddress(pageNum, pageSize, addressName, addressDetail, addressShow, accessToken);
            return ResponseResult.success(addressIPage);
    }


    /**
     * @author liangyan
     * 通过输入关键字模糊查询地址
     * @date 21:48 2022/3/12
     * @Param []
     */
    @GetMapping("/order/queryAddressBykeywords")
    public ResponseResult queryAddressBykeywords(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                                 @RequestParam("keywords") String keywords) {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            IPage<Address> addressIPage = iAddressService.queryAddressBykeywords(pageNum, pageSize, keywords, accessToken);
            return ResponseResult.success(addressIPage);
    }

}
