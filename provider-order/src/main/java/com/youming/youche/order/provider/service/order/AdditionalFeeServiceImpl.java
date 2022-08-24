package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IAdditionalFeeService;
import com.youming.youche.order.domain.order.AdditionalFee;
import com.youming.youche.order.provider.mapper.order.AdditionalFeeMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 附加运费 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-28
 */
@DubboService(version = "1.0.0")
public class AdditionalFeeServiceImpl extends BaseServiceImpl<AdditionalFeeMapper, AdditionalFee> implements IAdditionalFeeService {

    @Resource
    private AdditionalFeeMapper additionalFeeMapper;

    @Override
    public AdditionalFee getAdditionalFeeByOrderId(Long orderId) {

        QueryWrapper<AdditionalFee> additionalFeeQueryWrapper = new QueryWrapper<>();
        additionalFeeQueryWrapper.eq("order_id",orderId);
        List<AdditionalFee> additionalFees = additionalFeeMapper.selectList(additionalFeeQueryWrapper);
        if(additionalFees != null && additionalFees.size() > 0){
            return additionalFees.get(0);
        }
        return null;
    }
}
