package com.youming.youche.conts;

/**
 * Created by yangliu on 2018/3/21.
 */
public class VehicleConst {
    public static class  VEHICLE_VO_KEY{
        //审核表
        public static final String TENANT_VEHICLE_REL_VER="tenantVehicleRelVer";
        public static final String VEHICLE_DATA_INFO_VER="vehicleDataInfoVer";
        public static final String TENANT_VEHICLE_COST_REL_VER="tenantVehicleCostRelVer";
        public static final String TENANT_VEHICLE_CERT_REL_VER="tenantVehicleCertRelVer";

        //源表
        public static final String TENANT_VEHICLE_REL="tenantVehicleRel";
        public static final String VEHICLE_DATA_INFO="vehicleDataInfo";
        public static final String TENANT_VEHICLE_COST_REL="tenantVehicleCostRel";
        public static final String TENANT_VEHICLE_CERT_REL="tenantVehicleCertRel";


        public static final String TENANT_TRAILER_REL="tenantTrailerRel";
        public static final String TRAILER_MANAGEMENT="trailerManagement";

        public static final String TENANT_TRAILER_REL_VER="tenantTrailerRelVer";
        public static final String TRAILER_MANAGEMENT_VER="trailerManagementVer";
        
        /**
    	 * 我邀请、邀请我的
    	 */
    	public static class RECORD_TYPE {
    		//我邀请
    		public static final int RECORD_TYPE0 = 0;
    		
    		//邀请我的
    		public static final int RECORD_TYPE1 = 1;
    	}

    }
}
