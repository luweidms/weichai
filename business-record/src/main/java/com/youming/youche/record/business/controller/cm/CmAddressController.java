package com.youming.youche.record.business.controller.cm;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.cm.ICmAddressService;
import com.youming.youche.record.domain.cm.Address;
import com.youming.youche.record.dto.cm.CmAddressDto;
import com.youming.youche.record.vo.cm.CmAddressVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author 向子俊
 * @version 1.0.0
 * @ClassName CmAddressController.java
 * @Description TODO地址
 * @createTime 2022年02月15日 10:47:00
 */
@RestController
@RequestMapping("/address")
public class CmAddressController extends BaseController<Address, ICmAddressService> {
    @DubboReference(version = "1.0.0")
    private ICmAddressService addressService;
    @Override
    public ICmAddressService getService() {
        return addressService;
    }

    /**
     * @author 向子俊
     * @Description //TODO 查询所有地址信息       
     * @date 13:12 2022/2/15 0015
     * @Param [pageNum, pageSize, addressVo]
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @RequestMapping("/getALL")
    public ResponseResult getAllAddress(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
                                        CmAddressVo addressVo){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<CmAddressDto> cmAddressPage = new Page<>(pageNum, pageSize);
            Page<CmAddressDto> allAddress = addressService.findAllAddress(cmAddressPage, addressVo, accessToken);
            return ResponseResult.success(allAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("操作异常");
        }
    }
    /**
     * @author 向子俊
     * @Description //TODO 新增地址档案
     * @date 14:24 2022/2/16 0016
     * @Param [addressVo]
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("/addAddress")
    public ResponseResult addAddress(@RequestBody CmAddressVo addressVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String addressName = addressVo.getAddressName();
        String addressDetail = addressVo.getAddressDetail();
        String addressShow = addressVo.getAddressShow();
        try {
            if (StringUtils.isEmpty(addressName)){
                throw new BusinessException("地址名称不能为空");
            }
            if (StringUtils.isEmpty(addressDetail)){
                throw new BusinessException("导航地址不能为空");
            }
            if (StringUtils.isEmpty(addressShow)){
                throw new BusinessException("应到地址不能为空");
            }
            Integer result = addressService.saveOneAddress(addressVo, accessToken);
            if (result<0){
                return ResponseResult.failure("客户已存在");
            }else if (result > 0){
                return ResponseResult.success("新增成功");
            }else return ResponseResult.failure("新增失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("操作异常");
        }
    }

    /**
     * 修改回显
     *
     * @param addressVo 地址库
     * @return
     */
    @GetMapping("/getAddressById")
    public ResponseResult getAddressById(CmAddressVo addressVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            CmAddressDto addressById = addressService.findAddressById(addressVo, accessToken);
            return ResponseResult.success(addressById);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("操作异常");
        }
    }

    /**
     * 地址修改
     * @param addressVo
     * @return
     */
    @PostMapping("/updateAddress")
    public ResponseResult updateAddress(@RequestBody CmAddressVo addressVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            boolean result = addressService.modifyAddress(addressVo, accessToken);
            if (result){
                return ResponseResult.success("修改成功");
            }else return ResponseResult.failure("修改失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("操作异常");
        }
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @RequestMapping("/cutAddress")
    public ResponseResult cutAddress(Long id){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            Integer result = addressService.removeAddress(id, accessToken);
            if (result > 0){
                return ResponseResult.success("删除成功");
            }else return ResponseResult.failure("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("操作异常");
        }
    }
}
