//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.fdfs.pool;

import java.io.IOException;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastdfsPooledObjectFactory implements PooledObjectFactory<FastdfsClient> {
    private final Integer objMaxActive;

    public FastdfsPooledObjectFactory(Integer objMaxActive) {
        this.objMaxActive = objMaxActive;
    }

    public PooledObject<FastdfsClient> makeObject() throws Exception {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = tracker.getStoreStorage(trackerServer);
        FastdfsClient client = new FastdfsClient(trackerServer, storageServer, this.objMaxActive);
        return new DefaultPooledObject(client);
    }

    public void destroyObject(PooledObject<FastdfsClient> p) throws Exception {
        if (p != null) {
            if (p.getObject() != null) {
                FastdfsClient client = (FastdfsClient)p.getObject();
                client.reset();
            }
        }
    }

    public boolean validateObject(PooledObject<FastdfsClient> p) {
        try {
            return ProtoCommon.activeTest(((FastdfsClient)p.getObject()).getStorageServer().getSocket());
        } catch (IOException var3) {
            var3.printStackTrace();
            return false;
        }
    }

    public void activateObject(PooledObject<FastdfsClient> p) throws Exception {
        if (p != null) {
            if (p.getObject() != null) {
                ;
            }
        }
    }

    public void passivateObject(PooledObject<FastdfsClient> p) throws Exception {
    }
}
