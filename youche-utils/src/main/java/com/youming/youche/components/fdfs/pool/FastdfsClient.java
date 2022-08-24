//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.fdfs.pool;

import java.io.IOException;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerServer;

public class FastdfsClient extends StorageClient1 {
    private int active;
    private final int objMaxActive;

    public FastdfsClient(TrackerServer trackerServer, StorageServer storageServer, int objMaxActive) {
        super(trackerServer, storageServer);
        this.objMaxActive = objMaxActive;
    }

    public TrackerServer getTrackerServer() {
        return this.trackerServer;
    }

    public StorageServer getStorageServer() {
        return this.storageServer;
    }

    public void tryReset() throws IOException {
        ++this.active;
        if (this.active > this.objMaxActive) {
            this.reset();
        }
    }

    public void reset() throws IOException {
        if (this.trackerServer != null) {
            try {
                this.trackerServer.close();
            } catch (IOException var14) {
                throw var14;
            } finally {
                this.trackerServer = null;
            }
        }

        if (this.storageServer != null) {
            try {
                this.storageServer.close();
            } catch (IOException var12) {
                throw var12;
            } finally {
                this.storageServer = null;
            }
        }

    }

    public void reset0() {
        if (this.storageServer != null) {
            try {
                this.storageServer.close();
            } catch (IOException var5) {
                var5.printStackTrace();
            } finally {
                this.storageServer = null;
            }
        }

    }
}
