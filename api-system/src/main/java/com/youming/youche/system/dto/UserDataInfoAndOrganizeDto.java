package com.youming.youche.system.dto;

import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.system.domain.UserDataInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class UserDataInfoAndOrganizeDto extends UserDataInfo implements Serializable {


    private static final long serialVersionUID = 5544422332421602043L;

    List<SysOrganize>  organizeDtoList;
}
