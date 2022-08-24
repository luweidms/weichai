package com.youming.youche.record.api.vehicle;

import com.youming.youche.record.vo.DoAuditInfoVo;




public interface IVehicleAuditOBMSService {
    /***
     * 审核接口
     * @param vehicleCode 车辆编码  必填
     * @param authState 状态(2已认证 3认证失败)
     * @param auditContent 审核原因  必填
     * @return Y--业务处理成功  N-业务处理失败
     * @throws Exception
     */
    String doAuditInfo(DoAuditInfoVo doAuditInfoVo, String token) throws Exception;
}
