package com.youming.youche.record.api.cm;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.record.domain.cm.CmCustomerLineSubway;

import java.util.List;

/**
 * <p>
 * 客户线路经停点表 服务类
 * </p>
 *
 * @author 向子俊
 * @since 2022-01-22
 */
public interface ICmCustomerLineSubwayService extends IBaseService<CmCustomerLineSubway> {
    /**
     * 查询临停点列表
     * @param lineId 线路ID
     * @return
     */
    List<CmCustomerLineSubway> getCustomerLineSubwayList(long lineId);
}
