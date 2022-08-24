package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.order.domain.order.BillReceive;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IBillReceiveService extends IService<BillReceive> {
    /**
     * 获取对应的收件人信息
     * @param receiveId
     * @return
     */
    BillReceive getBillReceiveById(Long receiveId);
}
