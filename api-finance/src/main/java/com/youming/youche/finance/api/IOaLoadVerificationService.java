package com.youming.youche.finance.api;

import com.youming.youche.finance.domain.OaLoadVerification;
import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
public interface IOaLoadVerificationService extends IBaseService<OaLoadVerification> {


    /**
     *  查询最新一笔核销记录
     * @param Lid 借支id
     * @return
     */
    List<OaLoadVerification> queryOaLoadVerificationsById(Long  Lid);

    /**
     * 该函数的功能描述:查询核销详情信息
     */
    List<OaLoadVerification> getOaLoadVerificationsByLId(Long LId);

}
