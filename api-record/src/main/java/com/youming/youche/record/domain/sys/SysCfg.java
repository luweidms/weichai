//package com.youming.youche.record.domain.sys;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.youming.youche.commons.base.BaseDomain;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;
//
///**
// * <p>
// * 系统配置表
// * </p>
// *
// * @author Terry
// * @since 2022-01-04
// */
//@Data
//@EqualsAndHashCode(callSuper = true)
//@Accessors(chain = true)
//public class SysCfg extends BaseDomain {
//
//    private static final long serialVersionUID = 1L;
//
//
//    /**
//     * 配置Id
//     */
//    private Long cfgId;
//
//    /**
//     * 配置名称
//     */
//    private String cfgName;
//
//    /**
//     * 配置值
//     */
//    private String cfgValue;
//
//    /**
//     * 0。为公用系统参数，1。为平台门户系统参数、2，后台管理系统参数,3.手机系统参数
//     */
//    private Integer cfgSystem;
//
//    /**
//     * 配置注释
//     */
//    private String cfgRemark;
//
//
//}
