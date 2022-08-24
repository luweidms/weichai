package com.youming.youche.order.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.dto.order.GetOilCardByCardNumDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
* <p>
* 油卡管理表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-07
*/
@RestController
@RequestMapping("oil/card/management")
public class OilCardManagementController extends BaseController<OilCardManagement,IOilCardManagementService> {

    @DubboReference(version = "1.0.0")
    IOilCardManagementService oilCardManagementService;
    @Override
    public IOilCardManagementService getService() {
        return oilCardManagementService;
    }

    /**
     * 实现功能:通过车牌查询油卡
     *
     * @param plateNumber        车牌号码
     * @return
     */
    @GetMapping("/getOilCardByPlateNumber")
    public ResponseResult getOilCardByPlateNumber(@RequestParam("plateNumber") String plateNumber) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OilCardManagement> byPlateNumber = oilCardManagementService.findByPlateNumber(plateNumber, accessToken);

        return ResponseResult.success(byPlateNumber);
    }

    /**
     *
     * 校验油卡[30044]
     */
    @GetMapping("checkCardNum")
    public ResponseResult checkCardNum(String oilCardNum) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Map map = oilCardManagementService.checkCardNum(oilCardNum, accessToken);
        return ResponseResult.success(map);
    }

    /**
     * 支付预付款模糊查询油卡  30076
     */
    @GetMapping("getOilCardByCardNum")
    public ResponseResult getOilCardByCardNum(String cardNum) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<GetOilCardByCardNumDto> oilCardByCardNum = oilCardManagementService.getOilCardByCardNum(cardNum, accessToken);
        return ResponseResult.success(oilCardByCardNum);
    }
}
