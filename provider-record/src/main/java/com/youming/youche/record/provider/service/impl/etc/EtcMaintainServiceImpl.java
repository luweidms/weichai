package com.youming.youche.record.provider.service.impl.etc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.api.etc.IEtcMaintainService;
import com.youming.youche.record.domain.etc.EtcMaintain;
import com.youming.youche.record.provider.mapper.etc.EtcMaintainMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * @Date:2021/12/22
 */
@DubboService(version = "1.0.0")
public class EtcMaintainServiceImpl extends ServiceImpl<EtcMaintainMapper, EtcMaintain> implements IEtcMaintainService {


    @Override
    public void untieEtc(long tenantId, String plateNumber) {
        if(StringUtils.isBlank(plateNumber)){
            throw new BusinessException("车牌号不能为空！");
        }
        if(tenantId < 0){
            throw new BusinessException("归属车队不能为空！");
        }
        QueryWrapper<EtcMaintain> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("tenant_id",tenantId)
                .eq("bind_vehicle",plateNumber);
        List<EtcMaintain> list=baseMapper.selectList(queryWrapper);
        if(list != null && list.size() > 0){
            for(EtcMaintain etcMaintain : list){
                etcMaintain.setBindVehicle(null);
                etcMaintain.setVehicleCode(null);
                etcMaintain.setCarOwner(null);
                etcMaintain.setCarPhone(null);
                etcMaintain.setCarUserId(null);
                updateById(etcMaintain);
            }
        }
    }
}
