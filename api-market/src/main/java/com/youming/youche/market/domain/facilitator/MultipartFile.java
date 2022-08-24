package com.youming.youche.market.domain.facilitator;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;
@Data
public class MultipartFile implements Serializable {

    /**
     * 原始文件名
     */
    private String originalFilename;
    /**
     * 流文件
     */
    private InputStream inputStream;
    /**
     * 长度大小
     */
    private long size;
}
