package com.youming.youche.system.domain.pub;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 平安省份表
 * </p>
 *
 * @author hzx
 * @since 2022-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PubPayProv extends BaseDomain {

    /**
     * 省节点编码
     */
    private String provNodecode;

    /**
     * 省节点名称
     */
    private String provNodename;


}
