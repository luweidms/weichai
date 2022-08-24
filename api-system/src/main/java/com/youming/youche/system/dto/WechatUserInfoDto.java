package com.youming.youche.system.dto;

import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.domain.SysUserOrgRel;
import com.youming.youche.system.domain.TenantStaffRel;
import com.youming.youche.system.domain.UserDataInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : [terry]
 * @version : [v1.0]
 * @className : WechatUserInfoDto
 * @description : [描述说明该类的功能]
 * @createTime : [2022/4/26 14:10]
 */
@Data
public class WechatUserInfoDto implements Serializable {


    private static final long serialVersionUID = 6368511197383166280L;

    private SysUser sysUser;
    private UserDataInfoDto userDataInfo;
    private List<SysUserOrgRel> organizeList;
    private List<TenantStaffRel> staffReList;
    private List<Long> permissionList;
    private Integer auth;
    private Integer bindCard;


}
