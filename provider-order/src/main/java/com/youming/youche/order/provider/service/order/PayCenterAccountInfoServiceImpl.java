package com.youming.youche.order.provider.service.order;

import cn.hutool.db.Session;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.other.IPayCenterAccountInfoService;
import com.youming.youche.order.domain.order.PayCenterAccountInfo;
import com.youming.youche.order.provider.mapper.order.PayCenterAccountInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author liangyan
* @since 2022-03-29
*/
@DubboService(version = "1.0.0")
    public class PayCenterAccountInfoServiceImpl extends BaseServiceImpl<PayCenterAccountInfoMapper, PayCenterAccountInfo> implements IPayCenterAccountInfoService {

    @Resource
    private PayCenterAccountInfoMapper payCenterAccountInfoMapper;

    @Override
    public PayCenterAccountInfo getPayCenterAccountInfo(Long billMethodId, Long tenantId) {

        if (null == billMethodId || billMethodId <= 0 || null == tenantId || tenantId <= 0) {
            return null;
        }
        QueryWrapper<PayCenterAccountInfo> payCenterAccountInfoQueryWrapper = new QueryWrapper<>();
        payCenterAccountInfoQueryWrapper.eq("tenant_id",tenantId)
                .eq("bill_method_id",billMethodId);
        List<PayCenterAccountInfo> payCenterAccountInfos = payCenterAccountInfoMapper.selectList(payCenterAccountInfoQueryWrapper);
        if(payCenterAccountInfos != null && payCenterAccountInfos.size() > 0){
            PayCenterAccountInfo payCenterAccountInfo = payCenterAccountInfos.get(0);
            return payCenterAccountInfo;
        }
        return null;
    }
}
