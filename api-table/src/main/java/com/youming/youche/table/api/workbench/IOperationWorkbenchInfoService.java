package com.youming.youche.table.api.workbench;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.table.domain.workbench.OperationWorkbenchInfo;
import com.youming.youche.table.dto.workbench.BossWorkbenchInfoDto;
import com.youming.youche.table.dto.workbench.FinancialWorkbenchInfoDto;
import com.youming.youche.table.dto.workbench.WechatOperationWorkbenchInfoDto;

/**
* <p>
    *  服务类
    * </p>
* @author zengwen
* @since 2022-04-30
*/
public interface IOperationWorkbenchInfoService extends IBaseService<OperationWorkbenchInfo> {

    /**
     * 保存营运工作台数据
     */
    void initOperationWorkbenchInfoData();

    /**
     * 获取某个用户的营运工作台数据
     */
    OperationWorkbenchInfo getOperationWorkbenchInfo(String accessToken);

    /**
     * 保存财务工作台数据
     */
    void initFinancialWorkbenchInfoData();

    /**
     * 获取某个用户的财务工作台数据
     */
    FinancialWorkbenchInfoDto getFinancialWorkbenchInfo(String accessToken);

    /**
     * 保存老板工作台数据
     */
    void initBossWorkbenchInfoData();

    /**
     * 获取某个用户的老板工作台数据
     */
    BossWorkbenchInfoDto getBossWorkbenchInfo(String accessToken);

    /**
     * 获取车队小程序运营报表日报数据
     */
    void initWechatOperationWorkInfoDayData();

    /**
     * 获取车队小程序运营报表月报数据
     */
    void initWechatOperationWorkInfoMonthData();

    /**
     * 获取车队小程序营运报表数据
     */
    WechatOperationWorkbenchInfoDto getWechatOperationWorkbenchInfo(String accessToken, Integer type);
}
