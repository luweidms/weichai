package com.youming.youche.record.api.trailer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.trailer.TrailerLineRel;

public interface ITrailerLineRelService extends IService<TrailerLineRel> {
    /**
     * 方法实现说明  删除挂车线路
     * @author      terry
     * @param id
     * @return      void
     * @exception
     * @date        2022/3/5 0:00
     */
    boolean delLineRelList(Long id);
}
