//package com.youming.youche.record.domain.sys;
//
//import com.youming.youche.commons.base.BaseDomain;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;
//
///**
// * <p>
// * 静态数据表
// * </p>
// *
// * @author Terry
// * @since 2022-01-04
// */
//@Data
//@EqualsAndHashCode(callSuper = true)
//@Accessors(chain = true)
//public class SysStaticData extends BaseDomain {
//
//    private static final long serialVersionUID = 1L;
//
//    public SysStaticData() {
//    }
//
//    public SysStaticData(Long tenantId, Long codeId, String codeType, String codeValue, String codeName, String codeDesc, String codeTypeAlias, Integer sortId, String state) {
//        this.tenantId = tenantId;
//        this.codeId = codeId;
//        this.codeType = codeType;
//        this.codeValue = codeValue;
//        this.codeName = codeName;
//        this.codeDesc = codeDesc;
//        this.codeTypeAlias = codeTypeAlias;
//        this.sortId = sortId;
//        this.state = state;
//    }
//
//    /**
//     * 车队ID
//     */
//    private Long tenantId;
//
//    /**
//     * 静态编码ID
//     */
//    private Long codeId;
//
//    /**
//     * 静态枚举类型
//     */
//    private String codeType;
//
//    /**
//     * 静态编码值
//     */
//    private String codeValue;
//
//    /**
//     * 静态编码名称
//     */
//    private String codeName;
//
//    /**
//     * 静态编码别名
//     */
//    private String codeDesc;
//
//    /**
//     * 静态枚举类型别名
//     */
//    private String codeTypeAlias;
//
//    /**
//     * 排序
//     */
//    private Integer sortId;
//
//    /**
//     * 状态：1有效，0无效
//     */
//    private String state;
//
//
//}
