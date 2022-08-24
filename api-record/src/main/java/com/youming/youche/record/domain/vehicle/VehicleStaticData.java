package com.youming.youche.record.domain.vehicle;

import com.youming.youche.commons.domain.SysStaticData;
import lombok.Data;

import java.util.List;

/**
 * @version:
 * @Title: VehicleStaticData
 * @Package: com.youming.youche.domain.vehicle
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2021/12/16 10:10
 * @company:
 */
@Data
public class VehicleStaticData extends SysStaticData {

    /**
     * 静态数据
     */
    private List<SysStaticData> vehicleLengthList;

    public VehicleStaticData() {
    }

    public VehicleStaticData(SysStaticData sysStaticData) {
        super();
        super.setTenantId(sysStaticData.getTenantId())
                .setCodeId(sysStaticData.getCodeId())
        .setCodeType(sysStaticData.getCodeType()).setCodeValue(sysStaticData.getCodeValue()).setCodeName(sysStaticData.
                getCodeName()).setCodeDesc(sysStaticData.getCodeDesc()).setCodeTypeAlias(sysStaticData.getCodeTypeAlias())
        .setSortId(sysStaticData.getSortId()).setState(sysStaticData.getState()).setId(sysStaticData.getId());

        //  this.tenantId = tenantId;
        //        this.codeId = codeId;
        //        this.codeType = codeType;
        //        this.codeValue = codeValue;
        //        this.codeName = codeName;
        //        this.codeDesc = codeDesc;
        //        this.codeTypeAlias = codeTypeAlias;
        //        this.sortId = sortId;
        //        this.state = state;
    }


}
