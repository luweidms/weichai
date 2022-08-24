package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.order.api.order.IEtcMaintainService;
import com.youming.youche.order.domain.order.EtcMaintain;
import com.youming.youche.order.provider.mapper.order.EtcMaintainMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
* <p>
    *  服务实现类
    * </p>
* @author xxx
* @since 2022-03-28
*/
@DubboService(version = "1.0.0")
    public class EtcMaintainServiceImpl extends BaseServiceImpl<EtcMaintainMapper, EtcMaintain> implements IEtcMaintainService {

    @Resource
    LoginUtils loginUtils;
    @Resource IEtcMaintainService iEtcMaintainService;
    @Override
    public EtcMaintain getETCardByNumber(String bindVehicle, Long tenantId,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(StringUtils.isBlank(bindVehicle)) {
            return null;
        }
        if(tenantId==null||tenantId<=0) {
            tenantId=loginInfo.getTenantId();
        }
        Map<String,Object> map=iEtcMaintainService.checkEtcBindVehicle(bindVehicle,tenantId);
        if("1".equals(DataFormat.getStringKey(map, "info"))) {
            map.remove("info");
            EtcMaintain etc=new EtcMaintain();
            BeanUtil.copyProperties(etc, map);
            return etc;
        }

        return null;
    }

    @Override
    public Map<String, Object> checkEtcBindVehicle(String bindVehicle, Long tenantId) {
        Map<String,Object> map = null;
        List<Map<String,Object>> list = baseMapper.checkEtcBindVehicle(bindVehicle,tenantId);
        if(list != null && list.size() > 0){
            map = list.get(0);
            map.put("info","1");
        }else {
            map = new HashMap<>();
            map.put("info","0");
        }
        return map;
    }

    @Override
    public List<EtcMaintain> queryEtcMaintainByVehicleCode(Long vehicleCode, Integer state, Long tenantId) {
        if (vehicleCode == null || vehicleCode <= 0) {
            throw new BusinessException("车牌号不能为空！");
        }
        List<EtcMaintain> list =baseMapper.queryEtcMaintainByVehicleCode(vehicleCode,state,tenantId);
        ;
        return list;
    }

    @Override
    public List<Map> checkEtcCode(String etcId) {
        return  baseMapper.checkEtcCode(etcId);
    }
}
