//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.fdfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.csource.fastdfs.UploadCallback;

public class UploadFileSender implements UploadCallback {
    private InputStream inStream;

    public UploadFileSender(InputStream inStream) {
        this.inStream = inStream;
    }

    public int send(OutputStream out) throws IOException {
        byte[] buff = new byte[262144];

        int readBytes;
        while((readBytes = this.inStream.read(buff)) >= 0) {
            if (readBytes != 0) {
                out.write(buff, 0, readBytes);
            }
        }

        return 0;
    }
}
