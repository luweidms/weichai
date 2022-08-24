package com.youming.youche.finance.business.controller.ac;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.ac.IApplyOpenBillService;
import com.youming.youche.finance.domain.ac.ApplyOpenBill;
import com.youming.youche.finance.dto.ac.BillManageDto;
import com.youming.youche.finance.vo.ac.QueryInvoiceVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
* <p>
*  前端控制器
* </p>
* @author zengwen
* @since 2022-04-18
*/
@RestController
@RequestMapping("apply/open/bill")
        public class ApplyOpenBillController extends BaseController<ApplyOpenBill, IApplyOpenBillService> {

    @DubboReference(version = "1.0.0")
    IApplyOpenBillService applyOpenBillService;

    @Override
    public IApplyOpenBillService getService() {
        return applyOpenBillService;
    }

    /**
     * 服务商小程序 票据管理-列表 [21160]
     */
    @PostMapping("/doQueryInvoice")
    public ResponseResult doQueryInvoice(QueryInvoiceVo queryInvoiceVo,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<BillManageDto> page = applyOpenBillService.doQueryInvoice(accessToken, queryInvoiceVo, pageNum, pageSize);
        return ResponseResult.success(page);
    }


}
