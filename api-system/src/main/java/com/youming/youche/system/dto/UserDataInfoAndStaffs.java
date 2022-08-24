package com.youming.youche.system.dto;

import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.domain.UserDataInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserDataInfoAndStaffs implements Serializable {


    private static final long serialVersionUID = -3775283150880251005L;

    private UserDataInfo userDataInfo;
    private List<TenantStaffRel> staffRels;
}

