//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.fdfs.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class FastdfsPool extends Pool<FastdfsClient> {
    public FastdfsPool() {
        this(new FastdfsPoolConfig(), 20);
    }

    public FastdfsPool(GenericObjectPoolConfig poolConfig, Integer objMaxActive) {
        super(poolConfig, new FastdfsPooledObjectFactory(objMaxActive));
    }

    @Override
    public void returnBrokenResource(FastdfsClient resource) {
        if (resource != null) {
            this.returnBrokenResourceObject(resource);
        }

    }

    @Override
    public void returnResource(FastdfsClient resource) {
        if (resource != null) {
            this.returnResourceObject(resource);
        }

    }
}
