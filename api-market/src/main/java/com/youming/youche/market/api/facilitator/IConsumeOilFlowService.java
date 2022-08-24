package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.market.domain.facilitator.ConsumeOilFlow;
import com.youming.youche.market.domain.facilitator.OilTransaction;
import com.youming.youche.market.dto.facilitator.ServiceUnexpiredDetailDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoDto;
import com.youming.youche.market.dto.youca.ConsumeOilFlowDto;
import com.youming.youche.market.vo.youca.ConsumeOilFlowVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-09
 */
public interface IConsumeOilFlowService extends IService<ConsumeOilFlow> {
    /**
     * 分页查询
     * @return
     */
    IPage<Object> queryConsumeOilFlow(Integer pageSize, Integer pageNum,OilTransaction oilTransaction, String accessToken);

    /**
     * 查询油 账单
     * @param serviceUserId
     * @return
     */
    List<com.youming.youche.market.dto.facilitator.ConsumeOilFlowDto> queryConsumeOilFlow(Long serviceUserId,String statrtTime,String endTime,Long tenantId);

    /**
     * 查询 维修单
     * @param serviceUserId
     * @return
     */
    List<UserRepairInfoDto> queryUserRepairInfo(Long serviceUserId,String dateToString,String convertDateToString,Long tenantId);


    /**
     * 统计交易合计
     * @return
     * @throws Exception
     */
    Object totalOilTransaction(Integer pageSize, Integer pageNum, OilTransaction oilTransaction, String accessToken)throws Exception;

    /**
     * 交易记录导出
     * @param orderId
     * @param tradeTimeStart
     * @param tradeTimeEnd
     * @param consumerName
     * @param consumerBill
     * @param settlTimeStart
     * @param settlTimeEnd
     * @param state
     * @param productId
     * @param serviceType
     * @return
     */
    void queryConsumeOilFlowExport(ImportOrExportRecords importOrExportRecords, String orderId, String tradeTimeStart, String tradeTimeEnd,
                                   String consumerName, String consumerBill, String settlTimeStart,
                                   String settlTimeEnd, Integer state, Long productId, String accessToken, Integer serviceType);

    /**
     * 查询租户下的消费记录
     * @param page
     * @param consumeOilFlowVo
     * @param accessToken
     * @return
     */
    IPage<ConsumeOilFlowDto> queryConsumeOilFlowList(Page<ConsumeOilFlowDto> page, ConsumeOilFlowVo consumeOilFlowVo, String accessToken);

    /**
     * 油卡消费记录导出
     * @param consumeOilFlowVo
     * @return
     */
    void queryAllListExport(String accessToken, ImportOrExportRecords record,ConsumeOilFlowVo consumeOilFlowVo,
                           Integer pageNum,
                            Integer pageSize);

    /**
     * 中石化记录导入
     * @param bytes
     * @param record
     * @param accessToken
     */
    void batchImport(byte[] bytes, ImportOrExportRecords record, String accessToken);

    /**
     * 中石油记录导入
     * @param bytes
     * @param record
     * @param accessToken
     */
    void importYou(byte[] bytes, ImportOrExportRecords record, String accessToken);

    /**
     * 油卡充值/消费记录 统计 (服务商)
     *  startTime 记录开始 yyyy-MM-dd
     *  endTime 记录结束 yyyy-MM-dd
     *  recordType 记录类型：1充值，2油卡消费
     *  cardType 卡类型：1中石油，2中石化
     *  tenantName 车队名称
     *  cardNum 油卡卡号
     */
    List sumConsumeOilFlow(ConsumeOilFlowVo consumeOilFlowVo,String accessToken);

    /**
     * 微信接口-服务商-即将到期明细
     * 接口编码:21150
     * @param orderId
     * @param name
     * @param sourceTenantId
     */
    IPage<ServiceUnexpiredDetailDto> getServiceUnexpiredDetail(String orderId, String name, String sourceTenantId, String accessToken, Integer pageNum, Integer pageSize);


    /**
     * 获取服务费记录订单信息
     * @param productIds
     * @return
     */
    List<ConsumeOilFlowVo> getConsumeOilFlowProductIds(List<Long> productIds);

    /**
     *
     * 聂杰伟
     * 统计支付次数
     * @return
     */
    Integer paymentTimes(String accessToken);

    /**
     * 获取指定车队服务商的所有未到期金额
     */
    Long getTotalMarginBalance(Long serviceUserId, Long tenantId);

}
