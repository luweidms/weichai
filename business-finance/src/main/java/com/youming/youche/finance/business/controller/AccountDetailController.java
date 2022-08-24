package com.youming.youche.finance.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IAccountDetailsThreeService;
import com.youming.youche.finance.dto.AccountQueryDetailDto;
import com.youming.youche.finance.vo.AccountQueryDetailVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zengwen
 * @date 2022/4/12 18:54
 */
@RestController
@RequestMapping("account/detail")
public class AccountDetailController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDetailController.class);

    @DubboReference(version = "1.0.0")
    IAccountDetailsThreeService accountDetailsThreeService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IBaseService getService() {
        return accountDetailsThreeService;
    }

    /**
     * 查询司机账户账单明细
     */
    @PostMapping("/getAccountQueryDetails")
    public ResponseResult getAccountQueryDetails(@RequestBody AccountQueryDetailVo accountQueryDetailVo) {
        accountQueryDetailVo.setPageNum(accountQueryDetailVo.getPageNum() - 1);
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<AccountQueryDetailDto> page = accountDetailsThreeService.getAccountQueryDetails(accessToken, accountQueryDetailVo);
        return ResponseResult.success(page);
    }

    /**
     * 导出司机账户账单明细
     */
    @PostMapping("/downloadExcelFile")
    public ResponseResult downloadExcelFile(@RequestBody AccountQueryDetailVo accountQueryDetailVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("司机账单导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            accountDetailsThreeService.downloadExcelFile(accessToken, accountQueryDetailVo, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出异常审核异常" + e);
            return ResponseResult.failure("导出成功异常审核异常");
        }
    }

}
