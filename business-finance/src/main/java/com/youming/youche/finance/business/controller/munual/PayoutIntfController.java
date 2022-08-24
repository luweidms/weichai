package com.youming.youche.finance.business.controller.munual;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.domain.munual.MunualPaymentInfo;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.dto.DueDateDetailsCDDto;
import com.youming.youche.finance.dto.order.PayoutInfoDto;
import com.youming.youche.finance.vo.munual.QueryPayoutIntfsVo;
import com.youming.youche.finance.vo.order.QueryDouOverdueVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author zengwen
 * @date 2022/4/7 15:23
 */
@RestController
@RequestMapping("payout/intf")
public class PayoutIntfController extends BaseController<PayoutIntf, IPayoutIntfThreeService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PayoutIntfController.class);

    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService payoutIntfThreeService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IPayoutIntfThreeService getService() {
        return payoutIntfThreeService;
    }

    /**
     * 分页查询现金打款信息
     */
    @PostMapping("/queryPayoutIntfs")
    public ResponseResult queryPayoutIntfs(@RequestBody QueryPayoutIntfsVo queryPayoutIntfsVo) {
        queryPayoutIntfsVo.setPageNum(queryPayoutIntfsVo.getPageNum() - 1);
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<MunualPaymentInfo> page = payoutIntfThreeService.getPayOutIntfs(accessToken, queryPayoutIntfsVo);
        return ResponseResult.success(page);
    }

    /**
     * 查询未核销总金额已经核销总金额
     */
    @PostMapping("/queryPayoutIntfsSum")
    public ResponseResult queryPayoutIntfsSum(@RequestBody QueryPayoutIntfsVo queryPayoutIntfsVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Map map = payoutIntfThreeService.queryPayoutIntfsSum(accessToken, queryPayoutIntfsVo);
        return ResponseResult.success(map);
    }

    /**
     * 导出现金打款数据
     */
    @PostMapping("/downloadExcelFile")
    public ResponseResult downloadExcelFile(@RequestBody QueryPayoutIntfsVo queryPayoutIntfsVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("现金打款数据导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            payoutIntfThreeService.downloadExcelFile(accessToken, queryPayoutIntfsVo, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出异常审核异常" + e);
            return ResponseResult.failure("导出成功异常审核异常");
        }
    }

    /**
     * 应收逾期列表
     */
    @GetMapping("doQueryDouOverdue")
    public ResponseResult doQueryDouOverdue(QueryDouOverdueVo queryDouOverdueVo,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<PayoutInfoDto> queryDouOverdueDtoPage =
                payoutIntfThreeService.doQueryDouOverdue(pageNum, pageSize, queryDouOverdueVo, accessToken);
        return ResponseResult.success(queryDouOverdueDtoPage);
    }

    /**
     * 应收逾期--确认收款
     *
     * @param flowId 流水号
     * @return
     */
    @PostMapping("confirmPayment")
    public ResponseResult confirmPayment(@RequestParam("flowId") Long flowId) {
        if (flowId < 0) {
            throw new BusinessException("未传入流水号!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        payoutIntfThreeService.confirmPayment(flowId, accessToken);
        return ResponseResult.success();
    }

    /**
     * 线下打款确认收款
     */
    @PostMapping("confirmPaymentNew")
    public ResponseResult confirmPaymentNew(@RequestParam("flowId") Long flowId) {
        if (flowId < 0) {
            throw new BusinessException("未传入流水号!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        payoutIntfThreeService.confirmPaymentNew(flowId, accessToken);
        return ResponseResult.success();
    }

    /**
     * 应付逾期列表  过期
     *
     * @param busiCode
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("getDueDateDetailsCD")
    public ResponseResult getDueDateDetailsCD(String busiCode, String name, Integer pageNum, Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<DueDateDetailsCDDto> page = payoutIntfThreeService.getDueDateDetailsCD(busiCode, name, pageNum, pageSize, accessToken);
        return ResponseResult.success(page);
    }

    /**
     * 应付逾期列表
     *
     * @param orderId
     * @param name
     * @param businessNumber
     * @param state
     * @param userId
     * @param pageSize
     * @param pageNum
     * @return
     */
    @PostMapping("getOverdueCD")
    public ResponseResult  getOverdueCD(String orderId, String name, String businessNumber, String state, Long userId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<PayoutInfoDto> page = payoutIntfThreeService.getOverdueCD(orderId, name, businessNumber, state, userId, pageSize, pageNum, accessToken);
        return ResponseResult.success(page);
    }

    /**
     * 车队应收逾期接口-21151
     */
    @GetMapping("getDueDateDetailsCDs")
    public ResponseResult getDueDateDetailsCDs(Long orderId,String name,String businessNumbers,String  state,Long userId,
                                               @RequestParam("pageNum") Integer pageNum,
                                               @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<DueDateDetailsCDDto> page = payoutIntfThreeService.getDueDateDetailsCDs(orderId,name ,businessNumbers,state,userId, pageNum, pageSize, accessToken);
        return ResponseResult.success(page);
    }
    /**
     *
     * 接口编码:21117
     * @Description: APP应收逾期明细
     * 接口入参
     * param userId    用户ID
     * param orderId   订单号
     * param name      车队名称
     * param businessNumber   业务类型
     * param state   状态
     * @return
     * @throws Exception
     */
    @GetMapping("getDueDateDetails")
    public ResponseResult getDueDateDetails(Long userId, Long orderId, String name,
            String businessNumbers, String  state, Long sourceTenantId, String userType,Integer pageNum, Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<DueDateDetailsCDDto> page = payoutIntfThreeService.getDueDateDetails(userId,orderId ,name,businessNumbers,state,
                sourceTenantId,userType,pageNum, pageSize, accessToken);
        return ResponseResult.success(page);
    }
}
