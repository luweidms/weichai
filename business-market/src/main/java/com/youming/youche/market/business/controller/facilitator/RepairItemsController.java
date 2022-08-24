package com.youming.youche.market.business.controller.facilitator;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.market.api.repair.IRepairItemsService;
import com.youming.youche.market.domain.repair.RepairItems;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("repairItems/data/info")
public class RepairItemsController extends BaseController<RepairItems, IRepairItemsService> {
    @DubboReference(version = "1.0.0")
    IRepairItemsService repairItemsService;
    @Override
    public IRepairItemsService getService() {
        return repairItemsService;
    }
    /**
     * 接口编码：40009
     * 获取一级维修项
     * @return
     */
    @GetMapping("getLevelOneRepair")
    public ResponseResult getLevelOneRepair(){
        return ResponseResult.success(
                repairItemsService.getLevelOneRepair()
        );
    }
    /**
     * 接口编码：40010
     * 获取子维修项
     * @return
     */
    @GetMapping("getLevelTwoRepair")
    public ResponseResult getLevelTwoRepair(){
        return ResponseResult.success(
          repairItemsService.getLevelTwoRepair()
        );
    }
}
