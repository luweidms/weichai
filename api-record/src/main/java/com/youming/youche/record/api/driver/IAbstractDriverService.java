package com.youming.youche.record.api.driver;


import java.util.Map;

public interface IAbstractDriverService {


    /**
     * 删除司机
     *
     * @param tenantUserRelId  租户会员你关系主键
     * @param tenantVehicleIds
     * @param accessToken
     * @throws Exception
     */
    public void delete(Long tenantUserRelId, String tenantVehicleIds, String accessToken) throws Exception;

}
