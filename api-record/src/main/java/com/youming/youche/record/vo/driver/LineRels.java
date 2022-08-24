package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: LineRels
 * @Package: com.youming.youche.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/18 17:22
 * @company:
 */
@Data
public class LineRels implements Serializable {

    private String lineId;

    private String backhaulNumber;

    private Integer  state;

    private String lineCodeRule;
}
