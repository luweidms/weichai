package com.youming.youche.system.provider.mapper.mycenter;

import com.youming.youche.system.dto.mycenter.SysUserDto;
import com.youming.youche.system.vo.mycenter.UpdatePwdVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @InterfaceName MyCenterMapper
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/18 16:08
 */
public interface MyCenterMapper {

    /**
     * @description 根据userId查询系统用户信息
     * @author zag
     * @date 2022/2/18 16:58
     * @param userId
     * @return com.youming.youche.system.dto.mycenter.SysUserDto
     */
    SysUserDto seletcSysUserById(@Param("userId") Long userId,@Param("tenantId") Long tenantId);

    /**
     * @description 修改用户密码
     * @author zag
     * @date 2022/2/18 20:16
     * @param userId
     * @param newPwd
     * @return int
     */
    int updatePwd(@Param("userId") Long userId,@Param("newPwd") String newPwd);
}