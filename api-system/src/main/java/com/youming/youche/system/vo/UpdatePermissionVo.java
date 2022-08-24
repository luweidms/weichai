package com.youming.youche.system.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class UpdatePermissionVo implements Serializable {


    private static final long serialVersionUID = 8123016845987510413L;

    @NotNull(message = "请输入角色id")
    private Long roleId;
    private String roleName;
    private List<Long> buttons;
    private List<Long> menus;
}
