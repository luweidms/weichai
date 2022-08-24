package com.youming.youche.finance.api.ac;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.ac.HurryBillRecord;

/**
* <p>
    *  服务类
    * </p>
* @author zengwen
* @since 2022-04-24
*/
    public interface IHurryBillRecordService extends IBaseService<HurryBillRecord> {

    /**
     * 催票通知 21163
     *
     * @param accessToken
     * @param userId
     * @param type
     * @return
     */
        Page<HurryBillRecord> doQueryNoticeOfReminder(String accessToken, Long userId, Integer type, Integer pageNum, Integer pageSize);

    }
