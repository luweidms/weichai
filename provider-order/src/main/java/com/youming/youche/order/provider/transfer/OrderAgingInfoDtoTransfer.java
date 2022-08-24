package com.youming.youche.order.provider.transfer;

import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.dto.OrderAgingInfoDto;
import com.youming.youche.order.provider.utils.ReadisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.youming.youche.conts.EnumConsts.SysStaticData.ORDER_STATE;
import static com.youming.youche.conts.EnumConsts.SysStaticData.ORDER_TYPE;
import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_CITY;
@Component
@RequiredArgsConstructor
public class OrderAgingInfoDtoTransfer {

    private final ReadisUtil readisUtil;

    public List<OrderAgingInfoDto> toOrderAgingInfoDto(List<OrderAgingInfoDto> orderAgingInfoDto){
        if(orderAgingInfoDto != null && orderAgingInfoDto.size() >0){
            for (OrderAgingInfoDto out : orderAgingInfoDto) {
                if(out.getOrderType() != null && out.getOrderType() > 0){
                    String orderTypeName = readisUtil.getSysStaticData(ORDER_TYPE, out.getOrderType().toString()).getCodeName();
                    out.setOrderTypeName(orderTypeName);
                }
                if(out.getSourceRegion() != null && out.getSourceRegion() > 0){
                    String cityName = readisUtil.getSysStaticData(SYS_CITY, out.getSourceRegion().toString()).getCodeName();
                    out.setSourceRegionName(cityName);
                }
                if(out.getDesRegion() != null && out.getDesRegion() != 0){
                    String cityName = readisUtil.getSysStaticData(SYS_CITY, out.getDesRegion().toString()).getCodeName();
                    out.setDesRegionName(cityName);
                }
                if(out.getAuditState() != null && out.getAuditState() >=0){
                    String auditStateName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, out.getAuditState().toString()).getCodeName();
                    out.setAuditStateName(auditStateName);
                }
                if(out.getOrderState() != null && out.getOrderState() >0){
                    String codeName = readisUtil.getSysStaticData(ORDER_STATE, out.getOrderState().toString()).getCodeName();
                    out.setOrderStateName(codeName);
                }
                if(out.getAppealSts() != null && out.getAppealSts() >=0 ){
                    String appealstsName = readisUtil.getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, out.getAppealSts().toString()).getCodeName();
                    out.setAgingStsName(appealstsName);
                    String name = readisUtil.getSysStaticData(EnumConsts.SysStaticData.PRO_STATE, out.getAppealSts().toString()).getCodeName();
                    out.setAuditStateName(name);
                }else{
                    out.setAuditStateName("无");
                }
            }
            return   orderAgingInfoDto;
        }else{
            return  null;
        }
    }
}
