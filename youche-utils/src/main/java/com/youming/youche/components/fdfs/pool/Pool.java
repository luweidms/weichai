//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.fdfs.pool;

import com.youming.youche.commons.exception.BusinessException;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public abstract class Pool<T> {
    private GenericObjectPool<T> internalPool;

    public Pool() {
    }

    public Pool(GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {
        this.initPool(poolConfig, factory);
    }

    public void initPool(GenericObjectPoolConfig poolConfig, PooledObjectFactory<T> factory) {
        if (this.internalPool != null) {
            try {
                this.closeInternalPool();
            } catch (Exception var4) {
            }
        }

        this.internalPool = new GenericObjectPool(factory, poolConfig);
    }

    protected void closeInternalPool() {
        try {
            this.internalPool.close();
        } catch (Throwable var2) {
            throw new BusinessException(20001,"Could not destroy the pool");
        }
    }

    public T getResource() {
        try {
            return this.internalPool.borrowObject();
        } catch (Exception var2) {
           var2.printStackTrace();
            throw new BusinessException(20001,"Could not get a resource from the pool");
        }
    }

    public void returnResourceObject(T resource) {
        if (resource != null) {
            try {
                this.internalPool.returnObject(resource);
            } catch (Exception var3) {
                throw new BusinessException(20001,"Could not return the resource to the pool");
            }
        }
    }

    public void returnBrokenResource(T resource) {
        if (resource != null) {
            this.returnBrokenResourceObject(resource);
        }

    }

    public void returnResource(T resource) {
        if (resource != null) {
            this.returnResourceObject(resource);
        }

    }

    protected void returnBrokenResourceObject(T resource) {
        try {
            this.internalPool.invalidateObject(resource);
        } catch (Exception var3) {
            throw new BusinessException(20001,"Could not return the resource to the pool");
        }
    }

    public void destroy() {
        this.closeInternalPool();
    }
}
