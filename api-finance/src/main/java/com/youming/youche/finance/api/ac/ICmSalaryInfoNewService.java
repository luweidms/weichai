package com.youming.youche.finance.api.ac;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.ac.CmSalaryInfoNew;
import com.youming.youche.finance.domain.ac.CmSalaryTemplate;
import com.youming.youche.finance.dto.CmSalaryInfoDto;
import com.youming.youche.finance.dto.CmSalaryOrderInfoDto;
import com.youming.youche.finance.dto.CompSalaryTemplateDto;
import com.youming.youche.finance.dto.SaveCmSalaryTemplateDto;
import com.youming.youche.finance.dto.SubsidyInfoDto;
import com.youming.youche.finance.dto.ac.CmSalaryComplainNewDto;
import com.youming.youche.finance.dto.ac.CmSalaryInfoNewQueryDto;
import com.youming.youche.finance.dto.ac.CmSalaryInfoQueryDto;
import com.youming.youche.finance.dto.order.OrderListOutDto;
import com.youming.youche.finance.vo.CmSalaryInfoVo;
import com.youming.youche.finance.vo.ModifySalaryInfoVo;
import com.youming.youche.finance.vo.ac.CmSalaryInfoNewQueryVo;
import com.youming.youche.finance.vo.ac.OrderListInVo;

import java.util.List;
import java.util.Map;

/**
* <p>
    *  服务类
    * </p>
* @author zengwen
* @since 2022-04-18
*/
    public interface ICmSalaryInfoNewService extends IBaseService<CmSalaryInfoNew> {

    /**
     * 分页查询司机工资单
     * @param cmSalaryInfoNewQueryVo
     * @param pageNum
     * @param pageSize
     */
        Page<CmSalaryInfoNewQueryDto> queryCmSalaryInfoNew(CmSalaryInfoNewQueryVo cmSalaryInfoNewQueryVo, String accessToken, Integer pageNum, Integer pageSize);
    /**
     * @Description: 该函数的功能描述:发送工资单
     * @param salaryIds
     * @return
     * @throws Exception
     */
    void sendBillBySalary(String salaryIds, String accessToken);

    CmSalaryInfoNew getCmSalaryInfoNew(Long salaryId);


    /**
     * 获取结算补贴信息
     * @param id
     * @return
     */
    SubsidyInfoDto getSubsidyInfo(Long id, Long tenantId, String accessToken);


    /**
     * 结算补贴
     * @param orderId
     * @param salary
     * @param id
     * @param paidSalaryFee
     * @return
     */
    int balanceSubsidy(Long orderId, double salary, Long id, Double paidSalaryFee,String accessToken);


    /**
     * 数据流转到平台支付
     * @param orderId
     * @param salaryInfo
     * @param sumFee
     * @param accessToken
     */
    void subsidySettlement(Long orderId, CmSalaryInfoNew salaryInfo, Long sumFee, String accessToken);


    /**
     * 更新工资模板
     * @param accessToken
     * @param templateMonth
     * @return
     */
    List<CompSalaryTemplateDto> compSalaryTemplate(String templateMonth, String accessToken);


    /**
     * 自定义工资单
     * @param name
     */
//    void statisticsMenuTab(String name,String accessToken);


    /**
     * 确定-自定义工资单
     */
    void doSaveCmSalaryTemplate(List<SaveCmSalaryTemplateDto> saveCmSalaryTemplateDto, String accessToken);

    /**
     * 保存新模板数据
     * @param cmSalaryTemplateNew
     * @param saveCmSalaryTemplateDto
     * @param accessToken
     */
    void doSaveTemplateField(CmSalaryTemplate cmSalaryTemplateNew, List<SaveCmSalaryTemplateDto> saveCmSalaryTemplateDto,String accessToken);



    /**
     * 修改补贴金额
     * @param id
     * @param subsidy
     * @return
     */
    int changeSubsidy(Long id,Double subsidy,String accessToken);

    /**
     * 修改工资信息
     * @param modifySalaryInfoVo
     * @return
     */
    void modifySalaryInfo(ModifySalaryInfoVo modifySalaryInfoVo,String accessToken);
    /**
     * 导出司机账单
     *
     * @param cmSalaryInfoNewQueryVo
     * @param accessToken
     * @param importOrExportRecords
     */
    void downloadExcelFile(CmSalaryInfoNewQueryVo cmSalaryInfoNewQueryVo, String accessToken, ImportOrExportRecords importOrExportRecords);


    /**
     * 查询司机工资单订单
     *
     * @param orderListInVo
     */
    Page<OrderListOutDto> getSalaryOrder(OrderListInVo orderListInVo, Integer pageNum, Integer pageSize);
    String sendBillByExcel(List<List<String>> listsIn, String accessToken,String orderId);

    String salaryAffirmByExcel(List<List<String>> listsIn,String accessToken,String orderId);
    /**
     * 导入发送账单
     */
    void batchImport(byte[] byteBuffer, ImportOrExportRecords records, Integer operType,String token,String orderId);

    String dealCheckSalaryInfos(List<List<String>> listsIn,String accessToken,String orderId);

    void batchImports(byte[] bytes, ImportOrExportRecords record, Integer operType, String accessToken,String salaryId);
    /**
     * 结算-单个
     */
    /**
     * 勾选核销
     *
     * @return
     * @throws Exception
     */
    String checkedSalaryBill(String salaryId, Long verificationSalary,String accessToken,String orderId);

    String checkedSalaryBill(String salaryId, Long verificationSalary,String accessToken);
    /**
     * 工资确认接口(70075)
     *
     * @return
     */
    void salaryAffirm(Long salaryId,String accessToken);
    /**
     * 补贴明细发送
     */
    void SalaryGoTo(String orderId,String salaryId, String accessToken);
    /**
     * 补贴明细批量发送
     */
    void SalaryGoTos(String orderIds, String salaryId,String accessToken);
    /**
     * 补贴明细确认
     */
    void SalaryConfirm(String orderId, String salaryId,String accessToken);
    /**
     * 补贴明细批量确认
     */
    void SalaryConfirms(String orderIds, String salaryId,String accessToken);
    /**
     * 补贴明细结算
     */
    void SalarySettlement(String orderId,Long salaryFee,String salaryId, String accessToken);
    /**
     * 补贴明细批量结算
     */
    void SalarySettlements(String orderIds, String salaryId,String accessToken);
    /**
     * 发送工资单
     */
    void sendBillToBySalary(String salaryIds, Long salaryFee,String accessToken,String orderId);
    /**
     * 批量发送工资单
     */
    void sendBillToBySalarys(String salaryIds, String accessToken);

    /**
     * 更新模板时间
     * @param templateMonth
     */
    void updateCmSalaryTemplate(String templateMonth,String accessToken);

    /**
     * 获取工资申诉详情
     */
    Page<CmSalaryComplainNewDto> doQuerySalaryComplain(Long salaryId, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 导出工资申诉
     */
    void downloadQuerySalaryComplainExcelFile(Long salaryId, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 工资申诉审核
     */
    void checkSalaryComplain(Long id, Integer checkStatus, String checkResult, String accessToken);

    /**
     * 该函数的功能描述:查询用户已发送待确认的工资单数量
     */
    Integer getCmSalaryInfoCount(Long userId);


    /**
     * APP-查询工资列表新版
     * @param cmSalaryInfoDto
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<CmSalaryInfoVo> queryCmSalaryInfo(CmSalaryInfoDto cmSalaryInfoDto,Integer pageNum,Integer pageSize,String accessToken);

    /**
     * 小程序获取工资列表数据
     */
    Page<CmSalaryInfoQueryDto> queryCmSalaryInfoNew(String accessToken, Integer pageNum, Integer pageSize, Long salaryId);

    /**
     * 确认发送的工资单详情
     * @param sendId
     */
    void confirmSalarySendOrder(Long sendId, String accessToken);

    /**
     * 工资单-查看详情新版
     * @param salaryId
     * @return
     */
    Map queryCmSalaryDetail(Long salaryId);


    /**
     * 工资单-申诉申请新版
     * @param salaryId
     * @param complainType
     * @param complainReason
     * @param token
     * @return
     */
    Map<String,Object> oCmSalaryComplain(Long salaryId, String complainType, String complainReason,String token);


    /**
     * APP-新版工资订单列表
     * @param cmSalaryOrderInfoDto
     * @return
     */
    List<OrderListOutDto> getCmSalaryOrderInfo(CmSalaryOrderInfoDto cmSalaryOrderInfoDto);

    /**
     * 获取工资单订单信息
     */
    List<OrderListOutDto> getCmSalaryOrderInfoNew(Long sendId, String accessToken);


    /**
     * 22036 APP-新版工资补贴
     * @param salaryId
     * @return
     */
    Map<String,Object> getDriverSubsidyInfo(Long salaryId,String channelType);

    /**
     * 定时任务 加载司机工资单数据
     */
    void newSalaryData();
}
