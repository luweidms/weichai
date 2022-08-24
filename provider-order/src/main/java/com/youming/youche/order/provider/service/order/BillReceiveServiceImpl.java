package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.order.api.order.IBillReceiveService;
import com.youming.youche.order.domain.order.BillReceive;
import com.youming.youche.order.provider.mapper.order.BillReceiveMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class BillReceiveServiceImpl extends ServiceImpl<BillReceiveMapper, BillReceive> implements IBillReceiveService {


    @Override
    public BillReceive getBillReceiveById(Long receiveId) {
        LambdaQueryWrapper<BillReceive> lambda=new QueryWrapper<BillReceive>().lambda();
        lambda.eq(BillReceive::getId,receiveId);
        return this.getOne(lambda);
    }
}
