package com.youming.youche.system.business.controller;

import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.components.citys.City;
import com.youming.youche.components.citys.District;
import com.youming.youche.components.citys.Province;
import com.youming.youche.system.api.ISelectCityService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 选择城市 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-17
 */
@RestController
@RequestMapping("/sys/city")
public class SelectCityController {

    @DubboReference(version = "1.0.0")
    ISelectCityService selectCityService;

    /**
     * 查询省份
     *
     * @return
     * @throws Exception
     */
    @GetMapping({"doQueryProvices"})
    public ResponseResult doQueryProvices(Province province) {
        List<Province> lists = selectCityService.doQueryProvices(province);
        return ResponseResult.success(lists);
    }

//    /**
//     * 根据省份ID获取省份信息
//     *
//     * @return
//     * @throws Exception
//     */
//    @GetMapping({"getProvices"})
//    public ResponseResult getProvices(Province province) {
//        if (province.getId() < 1) {
//            throw new BusinessException("没有省份ID！");
//        }
//        List<Province> lists = selectCityService.doQueryProvices(province);
//        if (lists != null && lists.size() > 0)
//            return ResponseResult.success(lists.get(0));
//        else
//            return ResponseResult.failure("无省份信息");
//    }

    /***
     * @Description: 查询地市
     * @Author: luwei
     * @Date: 2022/1/24 9:45 下午
     * @Param provicesId:
     * @return: java.lang.String
     * @Version: 1.0
     **/
    @GetMapping({"doQueryRegion"})
    public ResponseResult doQueryRegion(City city) {
        return ResponseResult.success(selectCityService.doQueryRegion(city));
    }

//    /**
//     * 根据地市ID获取地市信息
//     *
//     * @return
//     * @throws Exception
//     */
//    @GetMapping({"getRegion"})
//    public ResponseResult getRegion(City city) {
//
//        if (city.getProvId() < 1) {
//            throw new BusinessException("没有省份ID！");
//        }
//        if (city.getId() < 1) {
//            throw new BusinessException("没有地市ID！");
//        }
//        List<City> lists = selectCityService.doQueryRegion(city);
//        if (lists != null && lists.size() > 0) {
//            return ResponseResult.success(lists.get(0));
//        } else {
//            return ResponseResult.failure("没有地市信息");
//        }
//    }

    /**
     * 查询县区
     *
     * @return
     */
    @GetMapping({"doQueryCounty"})
    public ResponseResult doQueryCounty(District district) {
        return ResponseResult.success(selectCityService.getDistrictData(district));
    }

//    /**
//     * 根据县区ID获取县区信息
//     *
//     * @return
//     */
//    @GetMapping({"getCounty"})
//    public ResponseResult getCounty(District district) {
//        if (district.getCityId() < 1) {
//            throw new BusinessException("没有地市ID！");
//        }
//        if (district.getId() < 1) {
//            throw new BusinessException("没有县区ID！");
//        }
//        List<District> lists = selectCityService.getDistrictData(district);
//        if (lists != null && lists.size() > 0) {
//            return ResponseResult.success(lists.get(0));
//        } else {
//            return ResponseResult.failure("没有县区信息");
//        }
//    }

//    /***
//     * @Description: 省名称查询
//     * @Author: luwei
//     * @Date: 2022/1/24 10:31 下午
//     * @Param codeValueName:
//     * @return: java.lang.String
//     * @Version: 1.0
//     **/
//    @GetMapping({"getProvinceName"})
//    public ResponseResult getProvinceName(Province province) {
//        List<Province> lists = selectCityService.doQueryProvices(province);
//        long provinceId = 0L;
//        if (lists != null && lists.size() > 0) {
//            provinceId = lists.get(0).getId();
//        }
//        return ResponseResult.success(provinceId);
//    }
//
//    public String getDistrictName(District district) throws Exception {
////        Map<String, String[]> map = SysContexts.getRequestParameterMap();
////        String codeValue = DataFormat.getStringKey(map, "codeValueName");
////        long cityId = DataFormat.getLongKey(map, "cityId"); //城市名字
//        List<District> lists = selectCityService.getDistrictData(district);
//        long districtId = 0L;
//        if(lists!=null && lists.size()>0 && codeValue!=null && !codeValue.equals("") && codeValue!=""){
//            for(District district:lists){
//                if(codeValue.indexOf(district.getName())>-1){
//                    //可能存在相同的区(使用市比较)
//                    if(cityId > 0 && district.getCityId() == cityId){
//                        districtId = district.getId()+"";
//                        break;
//                    }
//                    if(cityId <= 0){
//                        districtId = district.getId()+"";
//                        break;
//                    }
//
//
//                }
//            }
//        }
//        return districtId;
//    }

}
