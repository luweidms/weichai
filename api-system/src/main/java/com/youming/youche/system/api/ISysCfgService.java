package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.SysCfg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统配置表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
public interface ISysCfgService extends IBaseService<SysCfg> {

    /**
     * 方法实现说明 获取全部配置信息
     *
     * @param
     * @return java.util.List<com.youming.youche.commons.domain.SysCfg>
     * @throws
     * @author terry
     * @date 2022/5/31 10:58
     */
    List<SysCfg> selectAll();

    /**
     * 方法实现说明 根据配置名获取配置
     * 从redis中获取
     *
     * @param
     * @return com.youming.youche.commons.domain.SysCfg
     * @throws
     * @author terry
     * @date 2022/5/31 10:58
     */
    SysCfg get(String cfgName);

    /**
     * 方法实现说明  查询redis中，参数名对应的配置值
     *
     * @param cfgName 参数名
     * @param system  系统类型
     * @param type    返回值类型的类
     * @return java.lang.Object
     * @throws
     * @author terry
     * @date 2022/5/31 11:01
     */
    Object getCfgVal(String cfgName, int system, Class type);

    /**
     * 方法实现说明  查询redis中，参数名对应的配置值
     *
     * @param tenantId 车队id
     * @param cfgName 参数名
     * @param system 系统类型
     * @return java.lang.Double 参数对应的配置值 Double类型
     * @throws
     * @author terry
     * @date 2022/5/31 11:03
     */
    Double getCfgVal(Long tenantId, String cfgName, Integer system);

	/**
	 * 方法实现说明  获取当前登录着的车队/平台  参数名对应的参数
	 * @author      terry
	 * @param cfgName  参数名
	 * @param accessToken  令牌
	 * @return      com.youming.youche.commons.domain.SysCfg
	 * @exception
	 * @date        2022/5/31 11:05
	 */
    SysCfg getSysCfg(String cfgName, String accessToken);

    /**
     * 实现功能: 判断配置是否存在
     *
     * @param
     * @return
     */
    Boolean getCfgBooleanVal(String cfgName, Integer system);

}
