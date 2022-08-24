package com.youming.youche.market.api.etc;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.market.domain.etc.CmEtcInfo;
import com.youming.youche.market.dto.etc.CmEtcInfoDto;
import com.youming.youche.market.dto.etc.CmEtcInfoOutDto;
import com.youming.youche.market.vo.etc.CalculatedEtcFeeVo;
import com.youming.youche.market.vo.etc.CmEtcInfoVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-03-11
 */
public interface ICmEtcInfoService extends IBaseService<CmEtcInfo> {


    /**
     * 接口说明 分页查询 ETC消费记录
     * 聂杰伟
     *
     * @param cmEtcInfoVo etc消费记录传入的值
     * @return
     */
    Page<CmEtcInfo> getAll(CmEtcInfoVo cmEtcInfoVo, String accessToken, Integer pageNum, Integer pageSize);


    /**
     * 聂杰伟
     * 条件导出所有etc卡信息
     *
     * @author
     */
    void etcOutList(CmEtcInfoVo cmEtcInfoVo, String accessToken, ImportOrExportRecords record);


    /**
     * 聂杰伟
     * ETC消费记录导入
     *
     * @param byteBuffer
     * @param records
     * @param
     * @param accessToken
     */
    void etcImport(byte[] byteBuffer, ImportOrExportRecords records, String accessToken);


    /**
     * ETC消费记录 反写订单
     * 聂杰伟
     *
     * @param etcIds
     * @return
     */
    String batchEtcDeduction(String accessToken);

    /**
     * 手动选择匹配
     * 聂杰伟
     *
     * @param etcCardNo      ETC号
     * @param consumeMoney   消费金额
     * @param etcConsumeTime 消费时间
     * @param tradingSite    交易地点
     * @param orderId        订单号
     * @return
     */
    String updateEtcInfoBy(String id, String etcCardNo, String consumeMoney, String etcConsumeTime, String tradingSite, String orderId, String accessToken);

    /**
     * 手动选择匹配 批量
     * 聂杰伟
     *
     * @param ids     ETC
     * @param orderId 订单号
     * @return
     */
    String updateEtcBys(List<String> ids, String orderId, String accessToken);

    /**
     * 覆盖上报费用
     * 聂杰伟
     *
     * @param etcid
     * @param coverType 判断是否覆盖 0：不覆盖 1：覆盖
     * @return
     */
    String coverEtc(String etcid, Integer coverType, String accessToken);


    /**
     * 聂杰伟
     * 订单ETC
     *
     * @param plate_number     车牌号
     * @param order_id         订单号
     * @param est_arrive_date  靠台开始
     * @param real_arrive_date 考台结束
     * @param cost_model       成本模式
     * @param vehicle_type     车辆类型
     * @return
     */
    Page<CmEtcInfoDto> OrderETC(String plate_number, String order_id, String est_arrive_date,
                                String real_arrive_date, Integer cost_model,
                                Integer vehicle_type, String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 获取指定对账单的ETC记录
     *
     * @param accountStatementNo
     * @return
     */
    List<CmEtcInfo> calculatedEtcFee(String accountStatementNo);

    /**
     * 获取指定对账单消费总金额
     *
     * @param accountStatementNo 对账单编码
     */
    Long calculatedEtcFeeMoney(String accountStatementNo);

    /**
     * 获取指定对账单的ETC记录
     *
     * @param calculatedEtcFeeVo
     * @return
     */
    List<CmEtcInfo> calculatedEtcFee(CalculatedEtcFeeVo calculatedEtcFeeVo);

    /**
     * 获取指定对账单消费总金额
     *
     * @param calculatedEtcFeeVo 对账单编码
     */
    Long calculatedEtcFeeMoney(CalculatedEtcFeeVo calculatedEtcFeeVo);

    /**
     * 实际消费ETC  28312
     */
    List<CmEtcInfoOutDto> doQueryActualConsumptionEtcApp(Long orderId, Long tenantId);


    /**
     * 司机小程序
     * 服务-ETC消费记录（51001）
     * niejiewei
     *
     * @param userId etc 卡号
     * @return
     */
    IPage<CmEtcInfoOutDto> getETCList(Long userId, String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 条件查询etc管理
     *
     * @param plateNumber    车牌号
     * @param tenantId       车队id
     * @param month          消费时间
     * @param receiverPhone  接收人手机
     * @param receiverUserId 接收人id
     */
    List<CmEtcInfo> getCmEtcAccountStatement(String plateNumber, Long tenantId, String month, String receiverPhone, Long receiverUserId);

    /**
     * 修改etc管理
     *
     * @param plateNumber    车牌号
     * @param tenantId       车队id
     * @param month          消费时间
     * @param receiverPhone  接收人手机
     * @param receiverUserId 接收人id
     */
    void updateCmEtcAccountStatement(String plateNumber, Long tenantId, String month, String receiverPhone, Long receiverUserId, String billNumber);

}
