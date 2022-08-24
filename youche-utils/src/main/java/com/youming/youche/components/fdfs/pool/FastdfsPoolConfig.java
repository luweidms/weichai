//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.fdfs.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class FastdfsPoolConfig extends GenericObjectPoolConfig {
    public FastdfsPoolConfig() {
        this.setTestWhileIdle(true);
        this.setMinEvictableIdleTimeMillis(60000L);
        this.setTimeBetweenEvictionRunsMillis(30000L);
        this.setNumTestsPerEvictionRun(-1);
        this.setTestOnBorrow(true);
        this.setMaxIdle(20);
        this.setMaxTotal(20);
    }
}
