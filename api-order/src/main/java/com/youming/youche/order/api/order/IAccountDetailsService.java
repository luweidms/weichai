package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AccountDetails;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.dto.AppAccountDetailsDto;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.vo.AppAccountDetailsVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IAccountDetailsService extends IBaseService<AccountDetails> {



    /**
     * 资金账户明细操作
     * @param businessTypes 业务类型
     * @param businessCode 业务编号
     * @param orderId 订单编号
     * @param otherUserId 对方用户编号
     * @param otherName 对方名称
     * @param account   账户信息
     * @param subjectsList 费用信息集合
     * @param tenantId 租户id
     * @param vehicleAffiliation 资金渠道
     * @return long
     * @throws Exception
     */
    Long insetAccDet(Integer businessTypes, Long businessCode, Long otherUserId,
                     String otherName, OrderAccount account, List<BusiSubjectsRel> subjectsList,
                     Long soNbr, Long orderId, String toUserName, LocalDateTime happenDate, Long tenantId,
                     String etcNumber, String plateNumber, OrderResponseDto param, String vehicleAffiliation,
                     LoginInfo user);


    /**
     * 保存资金账户
     * @param details
     */
    void saveAccountDetails(AccountDetails details);

    /**
     * 给司机未绑卡特殊使用
     * @param businessTypes 业务类型
     * @param businessCode 业务编号
     * @param orderId 订单编号
     * @param otherUserId 对方用户编号
     * @param otherName 对方名称
     * @param subjectsList 费用信息集合
     * @return long
     * @throws Exception
     */
    void createAccountDetailsNew(int businessTypes, long businessCode,Long userId, Long otherUserId, String otherName,
                                 List<BusiSubjectsRel> subjectsList, long soNbr, Long orderId, Long tenantId,int userType,Long time);

    /**
     * 和账户无关的流水记录
     *
     * @param businessTypes 业务类型
     * @param businessCode  业务编号
     * @param orderId       订单编号
     * @param otherUserId   对方用户编号
     * @param otherName     对方名称
     * @param subjectsList  费用信息集合
     * @return long
     * @throws Exception
     */
    void createAccountDetails(int businessTypes, long businessCode, Long userId, Long otherUserId, String otherName,
                              List<BusiSubjectsRel> subjectsList, long soNbr, Long orderId, Long tenantId, int userType);


    /**
     * 收支明细(21114)
     * @param appAccountDetailsVo
     */
    AppAccountDetailsDto getAccountDetail(AppAccountDetailsVo appAccountDetailsVo, String accessToken);


    /**
     * 查询流水明细(合并同一笔操作)
     * @param appAccountDetailsVo
     * @return
     */
    List<AccountDetails> queryAccountDetailsMerge(AppAccountDetailsVo appAccountDetailsVo, Integer userType);

    /**
     * 接口编码:21001
     *
     * @Description: 我的--收支明细
     * 接口入参：
     * month  查询年月
     * type   0. 全部 1. 支出、2. 收入
     * userId 用户id
     * <p>
     * 接口出参：
     * 变量名	         含义	      类型	      备注
     * deserveFee	     差额	      string	单位：分
     * incomeFee	     借支	      string	单位：分
     * userId	         用户id	      string
     * month	         返回查询数据月份	string	yyyy-MM
     * outcomeFee	     核销	      string	单位：分
     * items	         列表	      array<object>
         * amount	         流水金额	      string	单位：分
         * businessNumber	 流水名称编号	  string
         * bussinessName	 流水名称	      string
         * costType	     消费类型	      string	1. 支出、2. 收入、3.其他
         * createTime	     创建时间	      string
         * detailMonth	     流水月份	      string	yyyy-MM
         * orderId	         订单号	      string
         * subList	         列表	      array<object>	科目项
             * subCostType	     科目消费类型	  string	1. 支出、2. 收入、3.其他
             * subjectsAmount	 科目金额	      string	单位：分
             * subjectsId	     科目id	      string
             * subjectsName	 科目名称	      string
     */
    AppAccountDetailsDto getAccountDetailNew(AppAccountDetailsVo appAccountDetailsVo, String accessToken);

}
