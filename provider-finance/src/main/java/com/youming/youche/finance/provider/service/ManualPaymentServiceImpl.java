package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.finance.api.IManualPaymentService;
import com.youming.youche.finance.api.IOilEntityService;
import com.youming.youche.finance.vo.OilEntityInfoDto;
import com.youming.youche.finance.vo.OilEntityVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@DubboService(version = "1.0.0")
public class ManualPaymentServiceImpl implements IManualPaymentService {
    @Autowired
    IOilEntityService iOilEntityService;

    @Override
    public IPage<OilEntityInfoDto> getOilEntitys(OilEntityVo oilEntityVo, Integer pageNum, Integer pageSize, String accessToken) {
        IPage<OilEntityInfoDto> list = iOilEntityService.getOilEntitys(oilEntityVo,pageNum,pageSize,accessToken);
        return list;
    }

    @Override
    public void batchVerificatOrder(String orderIds,String accessToken) {
        iOilEntityService.batchVerificatOrder(orderIds,accessToken);
    }

    @Override
    public void doAccountIn(Long userId, Integer state, String remark, Long sourceTenantId, Long userType,String accessToken) {
        iOilEntityService.doAccountIn(userId, state, remark, sourceTenantId, userType,accessToken);
    }

    @Override
    public void updateOilCarNum(String orderId, String oilCarNum, String accessToken) {
        iOilEntityService.updateOilCarNum(orderId, oilCarNum,accessToken);
    }
}
