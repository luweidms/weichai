package com.youming.youche.table.provider.service.statistic;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.table.api.statistic.IStatementOwnCostDayService;
import com.youming.youche.table.domain.statistic.StatementOwnCostDay;
import com.youming.youche.table.provider.mapper.statistic.StatementOwnCostDayMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 自有成本费用表 服务实现类
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
@DubboService(version = "1.0.0")
public class StatementOwnCostDayServiceImpl extends BaseServiceImpl<StatementOwnCostDayMapper, StatementOwnCostDay> implements IStatementOwnCostDayService {


}
