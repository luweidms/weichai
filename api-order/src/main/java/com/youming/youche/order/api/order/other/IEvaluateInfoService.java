package com.youming.youche.order.api.order.other;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.EvaluateInfo;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author zengwen
* @since 2022-05-23
*/
    public interface IEvaluateInfoService extends IBaseService<EvaluateInfo> {

    List<EvaluateInfo> queryEvaluateInfo(Long id, Long busiId, Integer evaluateBusiType);

    }
