package com.youming.youche.order.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.IOilCardVehicleRelService;
import com.youming.youche.order.domain.OilCardVehicleRel;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.provider.mapper.OilCardVehicleRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 油卡-车辆关系表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-07
 */
@DubboService(version = "1.0.0")
@Service
public class OilCardVehicleRelServiceImpl extends BaseServiceImpl<OilCardVehicleRelMapper, OilCardVehicleRel> implements IOilCardVehicleRelService {


    @Override
    public void deleteOilCardVehicleRelByCardId(Long cardId) {
        if(cardId==null||cardId<=0) {
            throw new BusinessException("油卡Id不能为空");
        }
        LambdaQueryWrapper<OilCardVehicleRel> lambda= Wrappers.lambdaQuery();
        lambda.eq(OilCardVehicleRel::getCardId,cardId);
        this.remove(lambda);
    }

    @Override
    public void saveOilCardVehicleRel(String vehicleNumber, Long cardId, String oilCardNum, Long tenantId) {
        OilCardVehicleRel rel=new OilCardVehicleRel();
        rel.setCardId(cardId);
        rel.setOilCardNum(oilCardNum);
        rel.setTenantId(tenantId);
        rel.setVehicleNumber(vehicleNumber);
        this.save(rel);
    }

}
