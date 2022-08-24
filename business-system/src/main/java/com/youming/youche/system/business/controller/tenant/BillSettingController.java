package com.youming.youche.system.business.controller.tenant;


import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.system.api.tenant.IBillSettingService;
import com.youming.youche.system.dto.Rate;
import com.youming.youche.system.dto.RateItem;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 车队的开票设置 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-09
 */
@RestController
@RequestMapping("/sys/billSetting")
public class BillSettingController {

    @DubboReference(version = "1.0.0")
    IBillSettingService billSettingService;


    /***
     * @Description: 根据费率设置ID获取费率设置
     * @Author: luwei
     * @Date: 2022/1/23 5:43 下午

     * @return: java.lang.String
     * @Version: 1.0
     **/
    @GetMapping("getRateById")
    public ResponseResult getRateById(@RequestParam(value = "rateId") Long rateId) {
        Rate rate = billSettingService.getRateById(rateId);
        List<RateItem> rateItemList = billSettingService.queryRateItem(rateId);
        rate.setRateItemList(rateItemList);
        return ResponseResult.success(rate);
    }

    /***
     * @Description: 查询所有费率
     * @Author: luwei
     * @Date: 2022/1/23 6:32 下午

     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @GetMapping("queryRateAll")
    public ResponseResult queryRateAll() {
        return ResponseResult.success(billSettingService.queryRateAll());
    }




}
