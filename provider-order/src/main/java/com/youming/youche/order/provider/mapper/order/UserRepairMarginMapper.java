package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.order.UserRepairMargin;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import org.apache.ibatis.annotations.Param;

/**
* <p>
* 维修保养记录表Mapper接口
* </p>
* @author liangyan
* @since 2022-03-24
*/
public interface UserRepairMarginMapper extends BaseMapper<UserRepairMargin> {
    /**
     * 查询到期列表
     * @param page
     * @param advanceExpireOutVo
     * @return
     */
    Page<UserRepairMargin> queryUserRepairMargins(@Param("page") Page<UserRepairMargin> page, @Param("advanceExpireOutVo") AdvanceExpireOutVo advanceExpireOutVo);

    /**
     * 根据流水号查询司机加油记录
     * @param flowId 用户id
     * @throws Exception
     * @return list
     */
    UserRepairMargin getUserRepairMargin(Long flowId);

}
