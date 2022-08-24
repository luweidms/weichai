package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OaLoan;
import com.youming.youche.order.dto.OaloanCountDto;
import com.youming.youche.order.dto.order.OaLoanDto;
import com.youming.youche.order.dto.order.OaLoanListDto;

import java.util.List;

/**
 * <p>
 * 借支信息表 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
public interface IOaLoanService extends IBaseService<OaLoan> {

    /**
     * 通过订单Id判断该订单是否存在有效的借支或者报销记录
     *
     * @param orderId
     * @return boolean
     * @throws Exception
     */
    boolean checkLoanAndExpenseByOrder(Long orderId);


    /**
     * @param orderId 订单Id
     * @return
     * @throws Exception
     * @Function: com.business.pt.ac.service.IOaLoanSV.java::getOaLoanFee
     * @Description: 该函数的功能描述:查询 除了撤销和不通过的借支申请
     * @version: v1.1.0
     * @author:huangqb Modification History:
     * -------------------------------------------------------------
     */
     List<OaLoan> getOaLoanFee(long orderId);




    /**
     * 车管借支审核通过打款后调用，将flowId 记录到对应借支记录的payFlowId
     * @param flowId
     */
    void setPayFlowIdAfterPay(Long flowId);


    /**
     *
     * @Function: com.business.pt.ac.service.IOaLoanSV.java::queryOaLoanById
     * @Description: 该函数的功能描述:根据LId查询租户信息
     * @param LId 借支Id
     * @return
     * @throws Exception
     * @version: v1.1.0
     * @author:huangqb
     * @date:2018年4月11日 下午4:34:55
     * Modification History:
     *   Date         Author          Version            Description
     *-------------------------------------------------------------
     *
     */
     OaLoan queryOaLoanById(Long LId,String ...busiCode);

    /**
     * 保存租户信息
     * @param batchId
     * @param flowId
     */
    void updOaLoanPayFlowId(Long batchId, Long flowId);

    /**
     * 获取租户信息
     * @param userId
     * @param tenantId
     * @param loanType
     * @param carPhone
     * @param orderId
     * @return
     */
    List<OaLoan> queryOaLoan(Long userId,Long tenantId,List<Integer> loanType,String carPhone,Long orderId);

    /**
     * 根据orderId查询本订单借支的总数量以及总金额
     * @param orderId
     * @param userId
     * @return
     */
    OaloanCountDto queryOaloanCount(Long orderId, Long userId);

    /**
     * 借支审核-借支列表查询  (小程序)
     * 接口编码：22004
     */
    Page<OaLoanListDto> queryOaLoanList(String oaLoanId, String loanSubjects, Long orderId, String plateNumber,
                                        String userName, String mobilePhone, String states, Integer queryType,
                                        Boolean waitDeal, String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 小程序 借支审核-借支详情
     * 接口编码：22005
     *
     * @param lId 借支Id
     */
    OaLoanDto queryOaLoanByIdWx(Long LId,String accessToken, String... busiCode);

}
