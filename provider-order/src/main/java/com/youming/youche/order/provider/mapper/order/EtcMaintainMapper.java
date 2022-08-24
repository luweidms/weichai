package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.order.EtcMaintain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
* <p>
* Mapper接口
* </p>
* @author xxx
* @since 2022-03-28
*/
    public interface EtcMaintainMapper extends BaseMapper<EtcMaintain> {
        @Select("SELECT e.etc_id AS etcId, e.bind_Vehicle AS bindVehicle, e.id AS id,e.invite_State AS inviteState  FROM etc_maintain e WHERE e.bind_Vehicle = #{ bindVehicle}  AND e.tenant_id = #{ tenantId}")
    List<Map<String, Object>> checkEtcBindVehicle(String bindVehicle, Long tenantId);

    List<EtcMaintain> queryEtcMaintainByVehicleCode(@Param("vehicleCode") Long vehicleCode,@Param("state") Integer state, @Param("tenantId") Long tenantId);

    @Select("select e.id, e.etc_id as etcId,e.tenant_id as tenantId,e.bind_Vehicle as bindVehicle,e.invite_State as inviteState from etc_maintain e where e.etc_id = #{etcId} ")
    List<Map> checkEtcCode(String etcId);
}
