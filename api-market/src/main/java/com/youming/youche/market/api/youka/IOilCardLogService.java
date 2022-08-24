package com.youming.youche.market.api.youka;

import com.youming.youche.market.domain.youka.OilCardLog;
import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.market.dto.youca.OilCardLogDto;
import com.youming.youche.market.vo.youca.OilLogVo;

/**
* <p>
    *  服务类
    * </p>
* @author XXX
* @since 2022-03-24
*/
    public interface IOilCardLogService extends IBaseService<OilCardLog> {

    /**
     * 保存油卡日志
     * @param oilCardLog
     * @param addOrReduce 使用类型
     */
    void saveOrdUpdate(OilCardLog oilCardLog, int addOrReduce);

    /**
     * 查询油卡日志信息
     */
    IPage<OilCardLogDto> doQuery(Integer pageNum, Integer pageSize, OilLogVo oilLogVo, String accessToken);
}
