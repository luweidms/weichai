package com.youming.youche.record.api.cm;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import com.youming.youche.record.dto.cm.CmCustomerLineDto;
import com.youming.youche.record.dto.cm.CmCustomerLineOrderExtend;
import com.youming.youche.record.dto.cm.CmCustomerLineOutDto;
import com.youming.youche.record.dto.cm.CmCustomerLinePermissionDto;
import com.youming.youche.record.dto.cm.QueryLineByTenantForWXDto;
import com.youming.youche.record.vo.cm.CmCustomerLineVo;
import com.youming.youche.record.vo.cm.QueryLineByTenantForWXVo;

import java.util.List;

/**
 * <p>
 * 客户线路信息表 服务类
 * </p>
 *
 * @author 向子俊
 * @since 2022-01-15
 */
public interface ICmCustomerLineService extends IBaseService<CmCustomerLine> {
    /**
     * @author 向子俊
     * @Description //TODO 查询所有线路档案
     * @date 15:58 2022/1/15 0015
     * @Param [customerLine]
     * @return java.util.List<com.youming.youche.domain.cm.CmCustomerLine>
     */
    Page<CmCustomerLineDto> findAllLine(Page<CmCustomerLineDto> linePage, CmCustomerLineVo customerLine, String accessToken) throws Exception;
    /**
     * @author 向子俊
     * @Description //TODO 根据编码查询线路信息
     * @date 15:56 2022/1/25 0025
     * @Param [lineId, accessToken]
     * @return com.youming.youche.record.domain.cm.CmCustomerLine
     */
    List<CmCustomerLineDto> findLineById(Long lineId, Integer isEdit, String accessToken) throws Exception;
    /**
     * @author 向子俊
     * @Description //TODO 新增或修改线路
     * @date 14:47 2022/1/22 0022
     * @Param [customerLine, accessToken]
     * @return java.lang.String
     */
    String saveOrUpdateLine(CmCustomerLineVo customerLine,String accessToken) throws BusinessException;

    /**
     * 流程结束，审核通过的回调方法
     * @param customerId    业务的主键
     * @param auditContent      结果的描述
     * @return
     */
    public void doPublicAuth(Long customerId, int authState,String auditContent);

    /**
     * 获取回程货编号
     * @return
     * @throws Exception
     */
    String createBackhaulNumber() throws Exception;

    /**
     * 查询已有编码
     */
    Page<CmCustomerLineDto> getCustomerLineByBackhaul(Page<CmCustomerLineDto> linePage,CmCustomerLineVo customerLine,String accessToken) throws Exception;

    /**
     * 客户线路档案导入
     */
    String batchImportCustomerLine(byte[] bytes, ImportOrExportRecords records, String accessToken);

    /**
     * 线路信息导出
     */
    void exportCustomerLine(ImportOrExportRecords records,CmCustomerLineVo customerLineVo, String accessToken) throws Exception;

    /**
     * 实现功能: 分页获取线路信息
     *
     * @param lineCode 线路编号
     * @return
     */
    Page<CmCustomerLine> getCustomerLineByLineCode(Page<CmCustomerLine> page,String lineCodeRule, String accessToken);

    /**
     * 查询车队线路信息
     *
     * @param tenantId 车队id
     * @param lineId   线路id
     * @return
     */
    CmCustomerLine selectLineById(Long tenantId,String lineId);
    /**
     * @author liangyan
     * @Description //TODO 选择单条线路信息确认展示客户、承运、收货、收入数据
     * @date 15:56 2022/2/25
     * @Param [lineId, accessToken]
     * @return com.youming.youche.record.domain.cm.CmCustomerLineOrderExtend
     */
    CmCustomerLineOrderExtend selectLineListByAddress(Long customerId, String accessToken,String lineCodeRule) throws Exception;

    /**
     * 根据 id  查询客户信息
     * @param lineId
     * @param workId
     * @param isEdit
     * @param accessToken
     * @return
     */
    CmCustomerLineOutDto getCustomerLineInfo(Long lineId, Long workId, Integer isEdit, String accessToken);

    /**
     * 根据ID获取线路信息
     */
    CmCustomerLine getCmCustomerLineById(long id);

    /**
     * 查询临停点列表
     * @param lineId
     * @return
     */
    List<CmCustomerLineSubway> getCustomerLineSubwayList(long lineId);


    /**
     * 根据机构列表查询线路信息
     * @param lineRuleCode 线路编号
     * @param orgList 机构列表
     * @return
     * @throws Exception
     */
     CmCustomerLine queryLineForOrderTask(String lineCodeRule,
                                          List<Long> orgList, LoginInfo user);

    /**
     * 13500
     * 根据租户查询线路快速开单--微信接口
     */
    Page<QueryLineByTenantForWXDto> doQueryLineByTenantForWX(QueryLineByTenantForWXVo vo, Integer pageNum,
                                                       Integer pageSize, String accessToken);

    /**
     * 13501
     * 根据线路id查询-小程序接口
     */
    CmCustomerLineOutDto getCustomerLineInfoForWx(Long lineId, String accessToken);

    /**
     * 营运工作台  线路档案数量
     */
    List<WorkbenchDto> getTableLineCount();

}
