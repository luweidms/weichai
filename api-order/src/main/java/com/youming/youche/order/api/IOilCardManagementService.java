package com.youming.youche.order.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.OilCardSaveInDto;
import com.youming.youche.order.dto.order.GetOilCardByCardNumDto;
import com.youming.youche.order.dto.order.OilCardPledgeOrderListDto;
import com.youming.youche.order.vo.OilCardPledgeOrderListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 油卡管理表 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-07
 */
public interface IOilCardManagementService extends IBaseService<OilCardManagement> {

    //通过车牌号码查询油卡
    List<OilCardManagement> findByPlateNumber(String plateNumber, String accessToken);

    /**
     *
     * 校验油卡[30044]
     */
    Map checkCardNum(String oilCardNum, String accessToken);


    /**
     * 校验当前租户
     *
     * @param oilCardNum
     */
    boolean checkCardNum(String oilCardNum, Long tenantId);

    /**
     * 获取租户下的油卡信息
     *
     * @param oilCardNum
     * @return
     */
    List<OilCardManagement> getOilCardManagementByCard(String oilCardNum, Long tenantId);

    /**
     * 查询司机油卡余额
     * @param userId 用户id
     * @param tenantId 用户归属租户id，没有就null或-1
     * @param userType 用户类型
     * @return 油卡余额
     * @throws Exception
     */
    long queryOilBalance(Long userId,Long tenantId,int userType);

    /**
     * 抵押油卡理论余额
     * @param oilCardNum 油卡号
     * @param balance 抵押金额
     * @param tenantId 车队ID
     * @param toTenantId 接单车队ID
     * @param isRelease 是否释放
     */
    void pledgeOilCardBalance(String oilCardNum, Long balance, Long tenantId,Long toTenantId,boolean isRelease);

//    void saveOrUpdate(OilCardManagement oilCardManagement,boolean isUpdate);

    /**
     * 查询油卡通过名称
     *
     * @param oilCardNum
     * @param tenantId
     * @return
     */
     List<OilCardManagement> findByOilCardNum(String oilCardNum, Long tenantId);


    /**
     * 支付预付款--保存修改等值卡
     * @param orderInfo
     * @param cardNumber 等值卡号
     * @param cardAmount 等值卡金额
     * @param isAdd 是否增加金额
     * @param isAddCarriage 承运方是否增加
     * @throws Exception
     */
    void saveEquivalenceCard(OrderInfo orderInfo, OrderScheduler scheduler, String cardNumber,
                             Long cardAmount, Boolean isAdd, Boolean isAddCarriage,LoginInfo user);

    /**
     * 修改油卡理论金额增加日志
     *
     * @param oilCardNum   油卡号
     * @param balance      金额
     * @param isAdd        是否充值
     * @param plateNumber  车牌号
     * @param carDriverMan 司机名称
     * @param orderId      订单号
     * @param userId       使用人
     * @param isUpdateOrder 是否修改订单
     */
     void modifyOilCardBalance(String oilCardNum, Long balance, Boolean isAdd, String plateNumber,
                               String carDriverMan, Long orderId, Long userId, Long tenantId,
                               Boolean isUpdateOrder, LoginInfo baseUser);


    OilCardManagement save(OilCardSaveInDto saveIn,LoginInfo baseUser);


    void saveOrUpdate(OilCardManagement oilCardManagement,Boolean isUpdate);


    /**
     * 通过订单id查询车队油卡管理
     *
     * @param orderId
     * @param tenantId
     * @return
     * @throws Exception
     */
    List<OilCardManagement> getOilCardByOrderId(Long orderId, Long tenantId);



    /**
     * 油卡抵押接口
     *
     * @param orderId
     *            订单号
     * @param userId
     *            抵押人
     * @param vehicleAffiliation
     *            资金渠道
     * @param oilcardNum
     *            油卡
     * @param pledgeFee
     *            抵押费用
     * @param tenantId
     *            租户ID
     * @param vehicleClass
     * @throws Exception
     */
    void oilPledgeHandle(Long orderId, Long userId, String vehicleAffiliation,
                         List<String> oilCardNums, Long pledgeFee, Long tenantId,
                         Integer vehicleClass, OrderScheduler scheduler,LoginInfo user,String token);


    /**
     * 回收
     * @param orderId
     * @param cardId
     * @param tenantId
     * @param user
     * @param token
     * @return
     */
    String reclaim(Long orderId, Long cardId, Long tenantId,LoginInfo user,String token);



    /**
     * 油卡抵押释放押金（新）
     * @param userId
     * @param vehicleAffiliation 资金渠道
     * @param amountFee 单位(分) 抵扣释放金额
     * @param orderId 订单ID
     * @param tenantId 租户ID
     * @param pledgeType  0抵扣 1释放
     */
    void pledgeOrReleaseOilCardAmount(Long userId, String vehicleAffiliation, Long amountFee, Long orderId, Long tenantId, Integer pledgeType,LoginInfo user,String token);

    /**
     * 根据车牌查找有效的油卡集合
     *
     * @param plateNumber 车牌号
     */
    List<OilCardManagement> getOilCardsByPlateNumber(String plateNumber, String accessToken);

    List<OilCardManagement> getOilCardsByPlateNumbers(String bindVehicle,String accessToken);

    /**
     * 更新油卡理论金额并记录使用日志
     * @param cardNum 油卡号
     * @param balance 负数：扣减，正数：充值
     * @throws Exception
     */
    public void updateCardBalanceByCardNum(String cardNum,Long balance,Long orderId,String accessToken);

    /**
     * 释放油卡
     *
     * @param orderId  订单号
     * @param tenantId 租户ID
     * @return 释放数量
     */
    int releaseOilCardByOrderId(Long orderId, Long tenantId, String accessToken);

    int releaseOilCardByOrderId(Long orderId, Long tenantId, LoginInfo user);

    /**
     * 释放油卡关联订单
     */
    int updateOilCardByOrder(Long orderId);



    /***
     * isReclaim :是否回收
     * oilCardNum ： 油卡号
     * cardId ： 油卡ID
     * orderId : 订单号
     * @param orderId
     * @return
     * @throws Exception
     */
    List<Map<String,Object>> getOilCardByOrderIds(long orderId,String accessToken);


    /**
     * 主键查询
     *
     * @param cardId
     * @return
     */
    OilCardManagement getOilCardManagement(Long cardId);

    /**
     * 平台卡号
     * @param oilCardNum
     * @param accessToken
     * @return
     */
    int doCheckOilCardNum(String oilCardNum,String accessToken);

    /**
     * 获取平台油卡
     * @param oilCardNum
     * @param tenantId
     * @return
     */
    OilCardManagement doCheckOilCardNum(String oilCardNum,Long tenantId);

    /**
     * 油卡供应商信息
     * @param cardNum
     * @param tenantId
     * @return
     */
    ServiceInfo getServiceInfoByCardNum(String cardNum, Long tenantId);

    /**
     * 校验油卡号
     * @param oilCardNum
     * @param accessToken
     * @return
     */
    boolean verifyOilCardNumIsExists(String oilCardNum,String accessToken);

    /**
     * 支付预付款模糊查询油卡  30076
     * @param cardNum
     * @return
     */
    List<GetOilCardByCardNumDto> getOilCardByCardNum(String cardNum, String accessToken);

    /**
     * 查询油卡抵押订单信息
     * @param vo
     * @return
     */
    Page<OilCardPledgeOrderListDto> queryOilCardPledgeOrderInfo(@Param("vo") OilCardPledgeOrderListVo vo);

}
