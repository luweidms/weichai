package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.dto.AgingInfoDto;
import com.youming.youche.order.dto.OrderAgingInfoDto;
import com.youming.youche.order.dto.OrderAgingListDto;
import com.youming.youche.order.dto.OrderReportDto;
import com.youming.youche.order.dto.order.OrderAgingAppealInfoDto;
import com.youming.youche.order.dto.order.OrderAgingInfoOutDto;
import com.youming.youche.order.dto.order.OrderAgingListOutDto;
import com.youming.youche.order.vo.OrderAgingListInVo;
import com.youming.youche.order.vo.SaveAppealInfoVo;

import java.util.List;
import java.util.Map;

/**
* <p>
    * 时效罚款表 服务类
    * </p>
* @author liangyan
* @since 2022-03-22
*/
    public interface IOrderAgingInfoService extends IBaseService<OrderAgingInfo> {

    /**
     * 订单是否有时效罚款
     * @param orderId
     * @return
     * @throws Exception
     */
    boolean isExistAgingInfo(Long orderId);


    /**
     *  // TODO 根据订单号 查询  时间排序 时效罚款表 order_aging_info
     * @param orderId
     * @return
     */
    List<OrderAgingInfo> queryAgingInfoByOrderId(Long orderId);

    /**
     * 获取时效处理列表【30041 isWx = true】
     * @param orderAgingListInVo
     * @param accessToken
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<OrderAgingInfoDto> queryOrderAgingList(OrderAgingListInVo orderAgingListInVo,
                                                String accessToken,Integer pageSize,Integer pageNum);





    /**
     * 保存时效罚款
     * @param inParam
     * @return
     * @throws Exception
     */
    Boolean saveOrUpdateOrderAgingInfo(AgingInfoDto inParam,String accessToken);

    /**
     *  获取时效信息
     *  聂杰伟
     * @param id
     * @return
     */
    OrderAgingInfo getAgingInfo(Long id,String accessToken);

    /**
     * 查询订单报备
     * 聂杰伟
     * @param orderId
     * @return
     */
    List<OrderReportDto> queryOrderReport (Long orderId, String accessToken);



    /**
     * 根据位置查询是否有时效
     * @param orderId
     * @param nand
     * @param eand
     * @param eandDes
     * @param nandDes
     * @param agingId 排除时效
     * @return
     * @throws Exception
     */
    Boolean isExistAgingInfoByLocation(Long orderId,String nand,String eand,
                                       String eandDes,String nandDes,Long agingId);


    /**
     * 根据订单ID查询时效罚款
     * @param id
     * @return
     */
    OrderAgingInfo getOrderAgingInfo(Long id);
    /**
     * 第一次时效审核通过
     * @return
     * @throws Exception
     */
    Boolean auditAging(Long agingId, String verifyDesc,String accessToken);



    /**
     * 取消时效罚款
     * @return
     * @throws Exception
     */
     Boolean cancleAgingInfo(Long agingId,String accessToken);



    /**
     * 查询订单时效列表
     * @return
     * @throws Exception
     */
     List<OrderAgingListOutDto> queryOrderAgingInfoList(Long orderId,Integer selectType);

    /**
     * 查询时效罚款[30052]
     */
    OrderAgingListOutDto queryOrderAgingInfo(Long agingId, String accessToken);




    /**
     * 审核通过
     *
     * @param agingId
     *            主键
     * @param verifyDesc
     *            审核描述
     * @param isFirst
     *            是否一次审核
     * @throws Exception
     */
    void verifyPass(Long agingId, String verifyDesc, Boolean isFirst, String token);

    /**
     * 营运工作台  时效审核数量
     */
    List<WorkbenchDto> getTableInvalidExamineCount();

    /**
     * 根据用户id获取时效罚款
     * @param orderId
     * @param userId
     * @return
     */
    List<OrderAgingInfo> queryAgingInfoByUserId(Long orderId,Long userId);

    /**
     * 时效第一次审核[30059]
     */
    void verifyFirst(Long agingId, String verifyDesc, String accessToken);

    /**
     * 审核通过
     * @param busiId
     * @param desc
     * @param paramsMap
     * @param accessToken
     */
    void sucess(Long busiId, String desc, Map paramsMap, String accessToken);

    /**
     * 审核通过
     * @param appealId
     * @param verifyDesc
     * @param isFirst
     * @param accessToken
     */
    void success(Long appealId,String verifyDesc,Boolean isFirst, String accessToken);

    /**
     * 添加时效申诉（30025）
     * @param saveAppealInfoVo
     */
    boolean saveAppealInfo(SaveAppealInfoVo saveAppealInfoVo, String accessToken);

    /**
     * app-查询时效罚款列表(30026)
     * @param orderId
     * @param userId
     * @return
     */
    List<OrderAgingListDto> getOrderAgingInfoList(Long orderId, Long userId);


    /**
     * 查询时效申诉(30027)
     * @param agingId
     * @return
     */
    OrderAgingAppealInfoDto queryAppealInfo(Long agingId, String accessToken);




    /**
     * 取消时效罚款 30065
     */
    void cancelOrderAgingInfo(Long agingId, String accessToken);


    /**
     * 保存订单报备(30004)
     * @param userId
     * @param orderId
     * @param imgIds
     * @param imgUrls
     * @param reportDesc
     * @param reportType
     * @return
     */
    boolean saveOrderReport(Long userId,Long orderId,String imgIds,String imgUrls,String reportDesc,Integer reportType);

    /**
     * App接口-时效罚款  28317
     */
    List<OrderAgingInfoOutDto> doQueryTimeLimitFine(Long orderId, Long tenantId);

    /**
     * 车队小程序 首页数字统计 - 时效待审核数量
     */
    Integer getStatisticsStatuteOfLimitations(LoginInfo loginInfo);

}
