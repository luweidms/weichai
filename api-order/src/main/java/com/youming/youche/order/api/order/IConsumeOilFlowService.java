package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.dto.youca.ProductNearByOutDto;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.dto.ConsumeOilFlowDetailsOutDto;
import com.youming.youche.order.dto.ConsumeOilFlowDetailsWxOutDto;
import com.youming.youche.order.dto.ConsumeOilFlowDto;
import com.youming.youche.order.dto.ConsumeOilFlowWxOutDto;
import com.youming.youche.order.dto.MarginBalanceDetailsOut;
import com.youming.youche.order.dto.OilServiceInDto;
import com.youming.youche.order.dto.OilServiceOutDto;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import com.youming.youche.order.vo.ConsumeOilFlowVo;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 消费油记录表 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IConsumeOilFlowService extends IBaseService<ConsumeOilFlow> {

    /**
     * 根据流水号集合查询司机加油记录
     * @param flowIds 用户id
     */
    List<ConsumeOilFlow> getConsumeOilFlow(List<Long> flowIds);

    /**
     * 根据用户编码获取消费油记录
     * @param serviceUserId
     * @param endTime
     * @param startTime
     * @return
     */
    List<ConsumeOilFlow> getConsumeOilFlowByServiceId(Long serviceUserId,String endTime,String startTime);

    /**
     * 查询到期列表 	（分页）
     * @param advanceExpireOutVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<ConsumeOilFlow> queryConsumeOilFlowsNew(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize);

    /**
     * 获取消费油记录
     * @param orderId
     * @param userType    收款方用户类型
     * @param payUserType 付款方用户类型
     */
    List<ConsumeOilFlow> getConsumeOilFlowByOrderId(String orderId, Integer userType, Integer payUserType, Long tenantId);


    /**
     * 根据流水号查询司机加油记录
     * @param flowId 用户id
     * @throws Exception
     * @return list
     */
    ConsumeOilFlow getConsumeOilFlow(Long flowId);


    /**
     * 微信接口-油站-交易记录-流水
     * @param consumeOilFlowVo
     * @return
     */
    ConsumeOilFlowWxOutDto getConsumeOilFlowByWx(ConsumeOilFlowVo consumeOilFlowVo,Integer pageNum,Integer pageSize);


    /**
     * 微信接口-油站-交易记录-流水详情
     * @param consumeOilFlowVo
     * @return
     */
    ConsumeOilFlowDetailsWxOutDto getConsumeOilFlowDetailsByWx(ConsumeOilFlowVo consumeOilFlowVo);


    /**
     * 司机小程序
     * niejeiwei
     * APP接口-优惠加油-加油记录
     * 50000
     * @param vo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<ConsumeOilFlowDto>  getConsumeOilFlowOut (ConsumeOilFlowVo vo ,Integer pageNum,Integer pageSize, String accessToken );

    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-加油记录-加油详情
     * 50001
     * @param flowId
     * @return
     */
    ConsumeOilFlowDetailsOutDto getConsumeOilFlowDetails(Long flowId);

    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-加油记录-评
     * 50002
     * @param vo
     * @return
     */
    ConsumeOilFlowDetailsOutDto evaluateConsumeOilFlow (ConsumeOilFlowVo vo);

    /**
     * niejiewei
     * 司机小程序
     * APP接口-优惠加油-确认支付
     * @param vo
     * @return
     */
    ConsumeOilFlowDetailsOutDto confirmPayForOil (ConsumeOilFlowVo vo,String accessToken);

    /**
     *  保存司机加油记录
     * @param userId
     * @param userBill
     * @param userName
     * @param costType
     * @param orderId
     * @param amount
     * @param oilPrice
     * @param oilRise
     * @param otherUserId
     * @param otherUserBill
     * @param otherName
     * @param vehicleAffiliation
     * @param tenantId
     * @param productId
     * @param isNeedBill
     * @param userType
     * @param payUserType
     * @return
     */
    ConsumeOilFlow createConsumeOilFlow(Long userId, String userBill, String userName, Integer costType, String orderId, Long amount,Long oilPrice,Float oilRise,
                                        Long otherUserId, String otherUserBill,String otherName, String vehicleAffiliation, Long tenantId, Long productId,
                                        Integer isNeedBill,int userType,int payUserType);


    /**
     * APP接口-预支界面
     * 司机小程序
     * niejiewei
     * 50006
     * @param vo
     * @return
     */
    Long advanceUI (ConsumeOilFlowVo vo,String accessToken);

    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-查询预支手续费
     * 50007
     * @param vo
     * @return
     */
    Long getAdvanceFee(ConsumeOilFlowVo vo,String accessToken);



    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-预支
     * 50008
     * @param vo
     * @return
     */
    ConsumeOilFlowDetailsOutDto confirmAdvance(ConsumeOilFlowVo vo,String accessToken);


    /**
     * niejiewei
     * 司机小程序
     * APP接口-预支-可预支金额详情
     * 50009
     * @param vo
     * @return
     */
    List<MarginBalanceDetailsOut> getAdvanceDetails (ConsumeOilFlowVo vo,String accessToken);


    /**
     * 找油网加油_支付
     * niejiewei
     * 司机小程序
     * 50036
     * @param vo
     * @return
     */
    ConsumeOilFlowDetailsOutDto payForOrderOil (ConsumeOilFlowVo vo,String accessToken);

    /**
     * 根据找油网单号查询司机加油记录
     * @param orderNum 找油网单号
     * @param userType 收款方用户类型
     * @param payUserType 付款方用户类型
     * @throws Exception
     * @return list
     */
    List<ConsumeOilFlow> getConsumeOilByOrderNum(String orderNum,Integer userType,Integer payUserType);


    /**
     * 油账户列表
     * niejiewei
     * 司机小程序
     * 50040
     * @param vo
     * @return
     */
    ConsumeOilFlowDetailsOutDto getOilAccount (ConsumeOilFlowVo vo,String accessToken,Integer pageNum,Integer pageSize);


    /**
     * niejiewei
     * 司机小程序
     * 油账户列表油站详情
     * 50041
     * @param vo
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<ProductNearByOutDto> getOilStationDetails (ConsumeOilFlowVo vo,String accessToken,Integer pageNum,Integer pageSize);

    /**
     * 调资金组接口，查看最低可以加油多少升（达成）
     * @param userId
     * @param products
     * @param isNeedBill
     * @param amount
     * @param tenantId
     * @return
     */
    List<OilServiceOutDto> getOilStationDetailslist(Long userId, List<OilServiceInDto> products, Integer isNeedBill, Long amount,
                                             Long tenantId);
    /**
     * 交易取消(72004)
     * @param tradeId
     * @return
     */
    boolean dealCancel(String tradeId,String accessToken);

    /**
     * 油老板未到期记录查询
     * @param costType 消费类型
     * @param states 状态
     * @param getDate 到期时间
     * @param userType
     * @param payUserType
     * @throws Exception
     * @return list
     */
    List<ConsumeOilFlow> getConsumeOilFlowNew(int costType, Integer[] states, Date getDate, Integer userType, Integer payUserType,String orderId);

    /**
     * 根据加油流水更改加油记录
     * @param flowIds
     */
    public List<ConsumeOilFlow> doQueryConsumeOilFlow(String flowIds);

    /**
     * 根据加油流水更改加油记录task调用
     * @param flowIds
     */
    public void updateConsumeOilFlow(String flowIds,int state,String getResult);
}
