package com.youming.youche.system.domain.pub;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 平安城市表
 * </p>
 *
 * @author hzx
 * @since 2022-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PubPayCity extends BaseDomain {

    /**
     * 市县编码
     */
    private String cityAreacode;

    /**
     * 市县名称
     */
    private String cityAreaname;

    /**
     * 市县类型：2：市；3：县
     */
    private String cityAreatype;

    /**
     * 省编码
     */
    private String provNodecode;

    /**
     * 城市代码1
     */
    private String cityTopareacode1;

    /**
     * 城市代码2
     */
    private String cityTopareacode2;

    /**
     * 城市代码3
     */
    private String cityTopareacode3;

    /**
     * 市县编码前4位
     */
    private String cityOraareacode;


}
