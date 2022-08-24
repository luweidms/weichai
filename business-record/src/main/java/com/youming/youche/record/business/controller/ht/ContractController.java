package com.youming.youche.record.business.controller.ht;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.record.api.contract.IContractService;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.domain.contract.Contract;

import com.youming.youche.record.dto.ContractDto;
import com.youming.youche.record.vo.ContractVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Mrchen
 * @since 2022-01-14
 */
@RestController
@RequestMapping("contract")
public class ContractController extends BaseController<Contract,IContractService > {

    @DubboReference(version = "1.0.0")
    private IContractService iContractService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContractController.class);


    /**
     * 查询合同列表信息
     *
     * @param pageNum     页码
     * @param pageSize    页面展示条数
     * @param contractDto 合同编号、客户名称
     * @return
     */
    @GetMapping("/get/list")
    public ResponseResult getCustomerList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          ContractDto contractDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<ContractVo> page = new Page<>(pageNum, pageSize);
            Page<ContractVo> page1 = iContractService.queryAll(page, accessToken, contractDto);
            return ResponseResult.success(page1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }
    }

    //根据id删除
    @DeleteMapping({"remove/{id}"})
    public ResponseResult remove(@PathVariable Long id) {
        boolean deleted = this.iContractService.delete(id);
        return deleted ? ResponseResult.success("删除成功") : ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
    }


    //修改
    @PutMapping({"update"})
    public ResponseResult update(@RequestBody Contract contract) {
        long now = System.currentTimeMillis();//获取出来的是当前时间的毫秒值
        Date date = new Date(); //把毫秒值转化为时间格式
        date.setTime(now);
        //创建格式化时间日期类
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //格式化后的时间
        contract.setHtUpdatetime(dateFormat.format(date));
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean updated = this.iContractService.ContractUpdate(contract,accessToken);
        return updated ? ResponseResult.success("编辑成功") : ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
    }



    @Override
    public IContractService getService() {
        return iContractService;
    }

    //合同管理新增
    @PostMapping({"create"})
    public ResponseResult create(@RequestBody Contract contract) {
        long now = System.currentTimeMillis();//获取出来的是当前时间的毫秒值
        Date date = new Date(); //把毫秒值转化为时间格式
        date.setTime(now);
        //创建格式化时间日期类
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //格式化后的时间
        contract.setHtUpdatetime(dateFormat.format(date));
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean created = this.iContractService.ContractAdd(contract,accessToken);
        return created ? ResponseResult.success("添加成功") : null;
    }


}
