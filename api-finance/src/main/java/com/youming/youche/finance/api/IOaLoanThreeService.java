package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.dto.AccountBankRelDto;
import com.youming.youche.finance.dto.GetStatisticsDto;
import com.youming.youche.finance.dto.OaLoanOutDto;
import com.youming.youche.finance.dto.QueryOaloneDto;
import com.youming.youche.finance.dto.order.OrderSchedulerDto;
import com.youming.youche.finance.vo.OaFilesVo;
import com.youming.youche.finance.vo.OaLoanVo;
import com.youming.youche.order.dto.OilExcDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借支信息表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-02-07
 */
public interface IOaLoanThreeService extends IBaseService<OaLoan> {

    /**
     *方法说明 借支查询
     * @return
     */
    Page<OaLoanOutDto> selectAllByMore(Integer pageNum, Integer pageSize, Boolean waitDeal, Integer queryType, String oaLoanId,
                                        Integer state, Integer loanSubject, String userName, String orderId, String plateNumber,
                                        String startTime, String endTime, String flowId, List<Long> dataPermissionIds,
                                        List<Long> subOrgList, String acctName, String mobilePhone, String accessToken);

    /**
     *方法说明 借支导出
     */
    void borrowing_Excel(Boolean waitDeal, Integer queryType, String oaLoanId,
                         Integer state, Integer loanSubject, String userName, String orderId, String plateNumber,
                         String startTime, String endTime, String flowId, List<Long> dataPermissionIds,
                         List<Long> subOrgList, String acctName, String mobilePhone, String accessToken,com.youming.youche.commons.domain.ImportOrExportRecords records);
    /**
     *
     * @param id 借支号
     * @return 借支详情
     */
    OaLoanOutDto  queryOaLoanById(Long id,String busiCode,String accessToken);

    /**
     * 核销查询不卡控查询类型
     */
    IPage<OaLoanOutDto> selectCancelAll(Integer pageNum, Integer pageSize, Boolean waitDeal, Integer queryType, String oaLoanId,
                                        Integer state, Integer loanSubject, String userName, String orderId, String plateNumber,
                                        String startTime, String endTime, String flowId, List<Long> dataPermissionIds,
                                        List<Long> subOrgList, String acctName, String mobilePhone, String accessToken);

    /**
     * 核销报表
     * @param startTime
     * @param endTime
     * @param loanType
     * @param loanSubject
     * @param loanClassify
     * @param orderId
     * @param oaLoanId
     * @param userName
     * @param mobilePhone
     * @param acctName
     * @param amountStar
     * @param amountEnd
     * @param plateNumber
     * @param queryType
     * @param state
     * @param flowId
     * @param noPayedStar
     * @param noPayedEnd
     * @param accessToken
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<OaLoanOutDto> queryOaLoanTable (String startTime, String endTime,
                                         Integer loanType, Integer loanSubject,
                                         Integer loanClassify, String orderId,
                                         String oaLoanId, String userName ,
                                         String mobilePhone, String acctName,
                                          Long  amountStar , Long amountEnd,
                                         String plateNumber, Integer queryType,
                                         Integer state , String flowId ,
                                          Long noPayedStar, Long noPayedEnd,
                                         String accessToken , List<Long> subOrgList,
                                         Integer pageNum, Integer pageSize);

    // 核销统计
    OaLoanOutDto queryOaLoanTableSum(String startTime, String endTime,
                                           Integer loanType, Integer loanSubject,
                                           Integer loanClassify, String orderId,
                                           String oaLoanId, String userName ,
                                           String mobilePhone, String acctName,
                                           Long  amountStar , Long amountEnd,
                                           String plateNumber, Integer queryType,
                                           Integer state , String flowId ,
                                     Long noPayedStar, Long noPayedEnd,
                                           String accessToken , List<Long> subOrgList,
                                           Integer pageNum, Integer pageSize);
    /**
     * njw
     * 2022-3-3 14：55
     * 借支核销报表导出
     */
    void excel_Report(String startTime, String endTime,
                      Integer loanType, Integer loanSubject,
                      Integer loanClassify, String orderId,
                      String oaLoanId, String userName ,
                      String mobilePhone, String acctName,
                      Long  amountStar , Long amountEnd,
                      String plateNumber, Integer queryType,
                      Integer state , String flowId ,
                      Long noPayedStar, Long noPayedEnd,
                      String accessToken , List<Long> subOrgList,
                      com.youming.youche.commons.domain.ImportOrExportRecords record);
    /**
     * njw
     * 2022-3-3 10:00
     * 方法说明 核销查询导出
     */
    void excel_Export(Boolean waitDeal, Integer queryType, String oaLoanId,
                      Integer state, Integer loanSubject, String userName, String orderId, String plateNumber,
                      String startTime, String endTime, String flowId, List<Long> dataPermissionIds,
                      List<Long> subOrgList, String acctName, String mobilePhone, String accessToken,
                      com.youming.youche.commons.domain.ImportOrExportRecords records);

    /**
     * njw
     * 借支车管 新增
     * 2022-3-4 9:58
     * @param oaLoanVo
     * @param accessToken
     * @return
     */
    void saveOrUpdateOaLoanCar(OaLoanVo oaLoanVo, String accessToken);

    /**
     * njw
     * 2022-3-4
     * 方法说明 借支审核
     * @param oaLoanVo
     * @param accessToken
     * @return
     */
    int examineOaLoanCar(OaLoanVo oaLoanVo,String accessToken);


    /**
     * njw
     * 2022-3-7
     * 方法说明 借支核销
     * @param id
     * @param cashAmountStr
     * @param billAmountStr
     * @param fileUrl
     * @param verifyRemark
     * @param accessToken
     * @return
     */
    void   verificationOaLoanCar(String id, String cashAmountStr, String billAmountStr, List<String> fileUrl,
                               String strfileId, String verifyRemark,Integer fromWx, String accessToken);


    List<OaLoan> queryCarDriverOaLoan(OilExcDto oilExcDto);

    /**
     * 加一个接口判断借支号码是否重复
     * @param oaLoanId
     * @return
     */
    Integer checkOaLoanId(String oaLoanId,String accessToken);



    /**
     * 上传借支图片
     * @param oaFilesVo
     */
     void  saveOaLoanPic(OaFilesVo oaFilesVo,String accessToken);

    /**
     * 车管借支审核通过打款后调用，将flowId 记录到对应借支记录的payFlowId
     */
    void setPayFlowIdAfterPay(Long flowId);


    /**
     * 查询收款人
     * @param userName 名称
     * @param mobilePhone 手机号
     * @return
     */
    Page<AccountBankRelDto> getSelfAndBusinessBank(String userName, String mobilePhone, Integer pageNum, Integer pageSize,
                                                    String accessToken);


    /**
     * 查询订单
     * @param isHis
     * @param userName
     * @param mobilePhone
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<OrderSchedulerDto> queryOrderByCarDriver(Boolean isHis, String userName,
                                                   String mobilePhone, Integer pageNum,
                                                   Integer pageSize, String accessToken,
                                                   List<Long> dataPermissionIds, List<Long> subOrgList);

    /**
     * 查询 借支信息
     * @param LId
     * @param busiCode
     * @return
     */
     OaLoan queryOaLoanById(Long LId,String ...busiCode);

    /**
     *  跟新借支 报销的文件
     * @param id
     * @param tenantId
     * @param userId
     * @param strfileId
     * @param type 关联数据类型 1 借支  2 借支核销 3 报销
     */
    void saveFileForLoanExpense(Long id,Long tenantId,Long userId, String strfileId,Integer type);

    /**
     * 取消借支
     * @param oaLoanVo
     * @param accessToken
     * @return
     */
    String cancelOaLoan (OaLoanVo oaLoanVo ,String accessToken);

    /**
     * 流程结束，审核通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    void sucess(Long busiId, String desc, Map paramsMap,String accessToken);

    /**
     * 流程结束，审核不通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    void fail(Long busiId,String desc,Map paramsMap,String accessToken);

    /**
     * 营运工作台  费用借支 待我审
     */
    List<WorkbenchDto> getTableCostBorrowCount();

    /**
     * 营运工作台  费用借支 我发起
     */
    List<WorkbenchDto> getTableCostBorrowMeCount();

    /***
     * @Description: 部门id查询借支
     * @Author: luwei
     * @Date: 2022/5/5 10:17 上午

     * @return: com.youming.youche.finance.domain.OaLoan
     * @Version: 1.0
     **/
    Long queryAdvanceUid(Long orgId,LocalDateTime startDate, LocalDateTime endDate,Integer lauach,Long tenantId);


    /**
     *方法说明 借支查询(订单专用)
     * @return
     */
    OaLoan selectByOrder( String orderId,String accessToken);

    /***
     * @Description: 借支查询
     * @Author: luwei
     * @Date: 2022/7/2 19:34
     * @Param number:
     * @return: com.youming.youche.finance.domain.OaLoan
     * @Version: 1.0
     **/
    OaLoan selectByNumber(String number,Long userId);

    /**
     * 20000
     * 该函数的功能描述:驻场号-首页数字统计
     */
    GetStatisticsDto getStatistics(String accessToken);

    /**
     * 该函数的功能描述:驻场号-借支待审核数量统计
     */
    Integer queryOaloanCount(String accessToken);

    Integer doQueryAllPayManager(String accessToken);

    /**
     * 微信接口-获取收款人银行账号列表
     * 接口编码 ：22112notice
     * acctName 收款人姓名  支持模糊查询
     */
    Page<AccountBankRelDto> getSelfAndBusinessBankWx(String acctName, String mobilePhone, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 更新收款人信息
     * 接口编码：22114
     */
    void updatePayeeBankInfo(String accName, String accNo, Long accUserId, String bankName,
                             String bankBranch, String bankType, String collectAcctId, Long id);

    /**
     * app接口-借支列表查询21011
     * @param orderId
     * @param accessToken
     * @return
     */
    List<QueryOaloneDto> queryOaLoanList(Long orderId, String accessToken);


    /**
     * app接口-保存或者修改-21013
     * @param LId
     * @param amountString
     * @param appReason
     * @param loanSubject
     * @param plateNumber
     * @param orderId
     * @param accUserId
     * @param weight
     * @param isNeedBill
     * @param strfileId
     * @param accessToken
     * @return
     */
    String saveOrUpdateApp(Long LId, String amountString, String appReason, Integer loanSubject, String plateNumber, Long orderId, Long accUserId, String weight, String isNeedBill, String strfileId, String accessToken);

    /**
     * app接口-借支撤销-21014
     * @param id
     * @param accessToken
     * @return
     */
    String cancelOaLoans(Long id, String accessToken);
    /**21012
     *  方法说明 借支详情
     * @param id 借支id
     * @return 借支信息
     */
    OaLoanOutDto queryOaLoanByIds(Long id, String busiCode, String accessToken);

    /**
     *
     * @param nodeIndex  当前节点是第几个节点
     * @param busiId 业务主键id
     * @param targetObjectType   com.business.consts.AuditConsts.TargetObjType
     * 							角色类型
    public static final int ROLE_TYPE = 0;
    组织类型
    public static final int  ORG_TYPE= 1;
    用户类型
    public static final int  USER_TYPE= 2;
     * @param targetObjId    对应的值，用逗号隔开
     * @throws Exception
     */
    void notice(Integer nodeIndex,Long busiId, Integer targetObjectType, String targetObjId,String accessToken);

}
