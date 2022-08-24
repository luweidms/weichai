package com.youming.youche.record.provider.service.impl.vehicle;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.vehicle.IVehicleAuditOBMSService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoVerService;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfoVer;
import com.youming.youche.record.vo.DoAuditInfoVo;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @version:
 * @Title: VehicleAuditOBMSImpl
 * @Package: com.youming.youche.record.provider.service.impl.vehicle
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/24 13:52
 * @company:
 */
@DubboService(version = "1.0.0")
public class VehicleAuditOBMSImpl implements IVehicleAuditOBMSService {


    @Resource
    IVehicleDataInfoService iVehicleDataInfoService;

    @Resource
    IVehicleDataInfoVerService iVehicleDataInfoVerService;

    @Override
    public String doAuditInfo(DoAuditInfoVo doAuditInfoVo, String token) throws Exception {
        if(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2 == doAuditInfoVo.getAuthState()){
            auditSuccess(doAuditInfoVo,token);
        }else if(SysStaticDataEnum.AUTH_STATE.AUTH_STATE3 == doAuditInfoVo.getAuthState()){
            //auditFail(doAuditInfoVo,token);
        }else{
            throw new BusinessException("请选择是否认证通过！");
        }
        return "Y";
    }

    public String auditSuccess(DoAuditInfoVo doAuditInfoVo,String token)throws Exception{
        VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getById(doAuditInfoVo.getVehicleCode());
        if(null == vehicleDataInfo){
            throw new BusinessException("审核的车辆不存在！");
        }
        VehicleDataInfoVer vehicleDataInfoVer =iVehicleDataInfoVerService.getVehicleDataInfoVer(doAuditInfoVo.getVehicleCode());
        if(null == vehicleDataInfoVer){
            throw new BusinessException("未找到可审核的数据！");
        }
        //更新车量完整性
//          doUpdateVehicleCompleteness();

        return "Y";
    }

}
