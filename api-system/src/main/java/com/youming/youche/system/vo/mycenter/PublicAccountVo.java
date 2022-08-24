package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName PublicAccountVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/24 17:29
 */
@Data
public class PublicAccountVo extends CreatePublicAccountVo implements Serializable {

    /** id */
    private Long id;
    /** 商户状态 */
    private String status;
    /** 结果 */
    private String result;
    /** 商户编号 */
    private String merchNo;
    /** 子商户编号 */
    private String mbrNo;
    /** 余额 */
    private String avaBal;
}
