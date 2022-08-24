package com.youming.youche.market.api.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.ServiceSerial;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.dto.facilitator.RepairInfoDto;
import com.youming.youche.market.dto.facilitator.RepairItemsWXOutDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoOutDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoVx;
import com.youming.youche.market.dto.user.UserRepairInfoDetailDto;
import com.youming.youche.market.dto.user.UserRepairInfoDto;
import com.youming.youche.market.dto.user.VehicleAfterServingDto;
import com.youming.youche.market.vo.facilitator.RepairInfoDataVo;
import com.youming.youche.market.vo.facilitator.RepairInfoVo;
import com.youming.youche.market.vo.facilitator.ServiceVo;
import com.youming.youche.market.vo.facilitator.UserRepairInfoOutVo;
import com.youming.youche.market.vo.user.UserRepairInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 维修记录
 *
 * @author hzx
 * @date 2022/3/12 11:13
 */
public interface IUserRepairInfoService extends IBaseService<UserRepairInfo> {

    /**
     * 查询维修保养
     */
    Page<UserRepairInfoDto> queryUserRepairAuth(Page<UserRepairInfoVo> page, UserRepairInfoVo userRepairInfoVo, String accessToken);

    /**
     * 维修详情
     */
    UserRepairInfoDetailDto getUserRepairDetail(long repairId, String accessToken);

    /**
     * 校验是否存在交易待确认收款的
     *
     * @return
     */
    UserRepairInfo checkIsCash(String accessToken);

    /**
     * 维修交易
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    RepairInfoDataVo queryRepairInfo(RepairInfoDto inParam, String accessToken,
                                     Integer pageSize, Integer pageNum);


    /**
     * 查询维修交易记录
     *
     * @return
     */
    Page<RepairInfoVo> queryRepairInfo(LoginInfo user, RepairInfoDto inParam,
                                       Long serviceUserId, Boolean isSum,
                                       Integer pageSize, Integer pageNum);


    /**
     * 微信保存维修单
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    Map<String, Object> doSaveUserRepairInfo(UserRepairInfoVx inParam, String accessToken);

    /**
     * 获取站点的维修订单信息
     *
     * @param productIds
     * @return
     */
    List<UserRepairInfoVo> getUserRepairByProductIds(List<Long> productIds);

    /**
     * 40023
     * 司机确认接口
     * niejiewei
     *
     * @param vo
     * @return
     */
    UserRepairInfoOutDto confirmPayRepairInfo(UserRepairInfoOutVo vo, String accessToken);

    /**
     * 接口编码：40031
     * 现金收款
     *
     * @return
     * @throws Exception
     */
    String cashReceipt(Long repairId, String accessToken);

    /**
     * 更具主键查询维修记录
     *
     * @param repairId 主键id
     */
    UserRepairInfo getUserRepairInfo(Long repairId);

    /**
     * 查询app交易记录详情
     * niejiewei
     * 40026
     *
     * @param vo
     * @return
     */
    UserRepairInfoOutDto getRepairDetail(UserRepairInfoOutVo vo, String accessToken);

    /**
     * 查询维修项-接口
     *
     * @param tenantId
     * @param repairId
     * @return
     * @throws Exception
     */
    List<RepairItemsWXOutDto> getRepairItemsWXOut(Long tenantId, Long repairId);

    /**
     * 查询时间范围内车辆的维修数据
     *
     * @param id        车辆编号
     * @param tenantId  车队id
     * @param beginDate 开始时间
     * @param endDate   结束时间
     */
    List<UserRepairInfo> lists(Long id, Long tenantId, String beginDate, String endDate);

    /**
     * 流程结束，审核通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     */
    void sucess(Long busiId, String desc, Map paramsMap, String accessToken);

    /**
     * 流程结束，审核不通过的回调方法
     *
     * @param busiId 业务的主键
     * @param desc   结果的描述
     */
    void fail(Long busiId, String desc, Map paramsMap, String accessToken);

    /**
     * （多节点、第一个节点成功后回调）审核中回调方法
     */
    void auditingCallBack(Long busiId);

    /**
     * 维修单公司付
     */
    void companyPayForRepairFee(UserRepairInfo userRepairInfo, String accessToken);

    /**
     * 车辆报表查询维修费和保养费
     */
    List<VehicleAfterServingDto> getVehicleAfterServingByMonth(String plateNumber, Long tenantId, String month);

    /**
     * @param serviceUserId 服务商ID
     * @param startTime     交易开始时间
     * @param endTime       交易结束时间
     * @param serialNumber  账单流水号
     */
    List<ServiceSerial> getUserRepairInfoList(Long serviceUserId, String startTime, String endTime,
                                              Integer serviceType, String serialNumber, String token);

}
