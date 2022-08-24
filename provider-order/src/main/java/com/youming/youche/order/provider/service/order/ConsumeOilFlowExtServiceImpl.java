package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IConsumeOilFlowExtService;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;
import com.youming.youche.order.provider.mapper.order.ConsumeOilFlowExtMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class ConsumeOilFlowExtServiceImpl extends BaseServiceImpl<ConsumeOilFlowExtMapper, ConsumeOilFlowExt> implements IConsumeOilFlowExtService {

    @Resource
    private ConsumeOilFlowExtMapper consumeOilFlowExtMapper;

    @Override
    public List<ConsumeOilFlowExt> getConsumeOilFlowExtByFlowId(List<Long> otherFlowIds, Integer sourceRecordType) {
        LambdaQueryWrapper<ConsumeOilFlowExt> lambda= Wrappers.lambdaQuery();
        lambda.in(ConsumeOilFlowExt::getOtherFlowId,otherFlowIds)
                .eq(ConsumeOilFlowExt::getSourceRecordType,sourceRecordType);
        return this.list(lambda);
    }

    @Override
    public ConsumeOilFlowExt queryConsumeOilFlowExtByFlowId(Long flowId) {
        QueryWrapper<ConsumeOilFlowExt> consumeOilFlowExtQueryWrapper = new QueryWrapper<>();
        consumeOilFlowExtQueryWrapper.eq("flow_id",flowId);
        List<ConsumeOilFlowExt> consumeOilFlowExts = consumeOilFlowExtMapper.selectList(consumeOilFlowExtQueryWrapper);
        if(consumeOilFlowExts != null && consumeOilFlowExts.size() > 0){
            return consumeOilFlowExts.get(0);
        }
        return null;
    }

    @Override
    public ConsumeOilFlowExt getConsumeOilFlowExtByFlowId(Long flowId) {
        LambdaQueryWrapper<ConsumeOilFlowExt> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsumeOilFlowExt::getFlowId, flowId);
        List<ConsumeOilFlowExt> list = this.list();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new ConsumeOilFlowExt();
    }

    @Override
    public ConsumeOilFlowExt createConsumeOilFlowExt(Long flowId, Long otherFlowId,
                                                     Integer sourceRecordType, Integer oilConsumer,
                                                     Integer creditLimit, Long rechargeOil, Long rebateOil,
                                                     Long creditOil, Long tenantId) {
        ConsumeOilFlowExt ext = new ConsumeOilFlowExt();
        ext.setFlowId(flowId);
        ext.setOtherFlowId(otherFlowId);
        ext.setSourceRecordType(sourceRecordType);
        ext.setOilConsumer(oilConsumer);
        ext.setCreditLimit(creditLimit);
        ext.setRechargeOil(rechargeOil);
        ext.setRebateOil(rebateOil);
        ext.setCreditOil(creditOil);
        ext.setTenantId(tenantId);
        ext.setState(0);
        ext.setBillState(0);
        ext.setCreateTime(LocalDateTime.now());
        ext.setUpdateTime(LocalDateTime.now());
        return ext;
    }

}
