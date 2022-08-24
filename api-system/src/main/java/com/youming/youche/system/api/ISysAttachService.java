package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysAttach;

import java.util.List;

/**
 * <p>
 * 图片资源表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-17
 */
public interface ISysAttachService extends IBaseService<SysAttach> {

    /***
     * @Description: 新增图片并返回主键id
     * @Author: luwei
     * @Date: 2022/1/17 10:36 下午
     * @Param sysAttach:
     * @return: int
     * @Version: 1.0
     **/
    Long saveById(SysAttach sysAttach);


    /**
     * 方法实现说明 根据业务id和业务编码查询，业务相关的资源列表
     * @author      terry
     * @param businessId
    * @param businessCode
     * @return      java.util.List<com.youming.youche.system.domain.SysAttach>
     * @exception
     * @date        2022/1/23 16:05
     */
    List<SysAttach> selectAllByBusinessIdAndBusinessCode(Long businessId, Integer businessCode);

    /**
     * 根据主键获取数据
     *
     * @param flowIds
     * @return
     */
    List<SysAttach> selectAllInfoByIds(List<Long> flowIds);


    SysAttach getAttachByFlowId(Long flowId);
}
