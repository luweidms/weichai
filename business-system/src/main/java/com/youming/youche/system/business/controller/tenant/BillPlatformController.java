package com.youming.youche.system.business.controller.tenant;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.system.api.tenant.IBillPlatformService;
import com.youming.youche.system.domain.tenant.BillPlatform;
import com.youming.youche.system.dto.BillPlatformDto;
import com.youming.youche.system.dto.Rate;
import com.youming.youche.system.dto.RateItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 票据平台表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@RestController
@RequestMapping("/sys/billPlatform")
public class BillPlatformController extends BaseController {

    @DubboReference(version = "1.0.0")
    IBillPlatformService iBillPlatformService;

    @Override
    public IBaseService getService() {
        return iBillPlatformService;
    }

    /**
     * 查询开票方式下拉选项
     *
     * @return
     * @throws Exception
     */
    @GetMapping("getBillMethod")
    public ResponseResult getBillMethod(@RequestParam(value = "billMethod", required = false) Long billMethod) {
        BillPlatform billPlatform = new BillPlatform();
        if (billMethod != null) {
            billPlatform.setUserId(billMethod);
        }
        List<BillPlatformDto> list = iBillPlatformService.queryBillPlatformList(billPlatform);
        if (list != null) {
            for (BillPlatformDto e : list) {
                String bindState = e.getBindCards().equals("0") ? "(未绑卡)" : "";
                e.setPlatName(e.getPlatName() + bindState);
            }
        } else {
            list = new ArrayList<>();
        }
        return ResponseResult.success(list);
    }

}
