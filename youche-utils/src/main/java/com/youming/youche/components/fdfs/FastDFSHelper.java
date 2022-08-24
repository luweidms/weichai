//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.components.fdfs;


import cn.hutool.core.util.StrUtil;
import com.youming.youche.components.fdfs.pool.FastdfsClient;
import com.youming.youche.components.fdfs.pool.FastdfsPool;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.test.DownloadFileWriter;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.*;


public class FastDFSHelper {
    private static transient Log log = LogFactory.getLog(FastDFSHelper.class);
    public static final String[] allowFileExts = new String[]{"txt", "xml", "json", "data", "zip", "gzip", "rar", "doc", "docx", "xls", "xlsx", "pdf", "gif", "png", "bmp", "jpg", "jpeg", "rtf", "avi", "asf", "wav", "mp3", "mpg", "mpeg"};
    public static final Set<String> allowFileExtSets;
    public static final String META_FILE_NAME = "fileName";
    public static final String META_FILE_EXE_NAME = "fileExtName";
    public static final String META_FILE_LENGTH = "fileLength";
    public static final String bigPrefixName = "_big";
    private static FastDFSHelper instance;
    private static FastdfsPool pool;

    private FastDFSHelper() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("fdfs_client.conf");
        //创建临时文件，将fdfs_client.conf的值赋值到临时文件中，创建这个临时文件的原因是springboot打成jar后无法获取classpath下文件
        String tempPath =System.getProperty("java.io.tmpdir") + System.currentTimeMillis()+".conf";
        File f = new File(tempPath);
        IOUtils.copy(classPathResource.getInputStream(),new FileOutputStream(f));
        ClientGlobal.init(f.getAbsolutePath());
        //ClientGlobal.init(this.getClass().getClassLoader().getResource("fdfs_client.conf").getPath());
    }

    public static FastDFSHelper getInstance() throws Exception {
        if (instance == null) {
           // Class var0 = FastDFSHelper.class;
            synchronized (FastDFSHelper.class) {
                if (instance == null) {
                    pool = new FastdfsPool();
                    instance = new FastDFSHelper();
                }
            }
        }

        return instance;
    }


    public String upload(FileItem fileItem) throws Exception {
        return this.upload(fileItem.getInputStream(), fileItem.getName(), fileItem.getSize());
    }


    public String upload(FileItem fileItem, NameValuePair[] metaList) throws Exception {
        return this.upload(fileItem.getInputStream(), fileItem.getName(), fileItem.getSize(), metaList);
    }

    public String upload(InputStream is, String fileName, long size) throws Exception {
        String fileExtName = "";
        if (fileName.contains(".")) {
            fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
            NameValuePair[] metaList = new NameValuePair[]{new NameValuePair("fileName", fileName), new NameValuePair("fileExtName", fileExtName), new NameValuePair("fileLength", String.valueOf(size))};
            return this.upload(is, fileName, fileExtName, size, metaList);
        } else {
            throw new Exception("未知文件后缀!");
        }
    }

    public String upload(InputStream is, String fileName, long size, NameValuePair[] metaList) throws Exception {
        String fileExtName = "";
        if (fileName.contains(".")) {
            fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
            NameValuePair[] realMetaList = new NameValuePair[3 + metaList.length];
            realMetaList[0] = new NameValuePair("fileName", fileName);
            realMetaList[1] = new NameValuePair("fileExtName", fileExtName);
            realMetaList[2] = new NameValuePair("fileLength", String.valueOf(size));
            System.arraycopy(metaList, 0, realMetaList, 3, metaList.length);
            return this.upload(is, fileName, fileExtName, size, realMetaList);
        } else {
            throw new Exception("未知文件后缀!");
        }
    }

//    public String upload(InputStream is, String fileName,String fileExtName,) throws Exception {
//        FastdfsClient client = (FastdfsClient) pool.getResource();
//        client.upload_file1(bytes,fileName,null);
//    }



    private String upload(InputStream is, String fileName, String fileExtName, long size, NameValuePair[] metaList) throws Exception {
        FastdfsClient client = (FastdfsClient) pool.getResource();
        String path = null;
        if (this.isImage(fileExtName)) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int len;
            while ((len = is.read(buffer)) > -1) {
                bos.write(buffer, 0, len);
            }

            bos.flush();
            byte[] smallBytes = this.compressImage(bos.toByteArray(), fileExtName);
            String bigPath;
            if (smallBytes != null) {
                path = client.upload_file1(smallBytes, fileExtName, metaList);
                bigPath = client.upload_file1(path, "_big", bos.toByteArray(), fileExtName, metaList);
                if (log.isDebugEnabled()) {
                    log.debug("原始大图路径为" + bigPath);
                }
            } else {
                path = client.upload_file1(bos.toByteArray(), fileExtName, metaList);
                bigPath = client.upload_file1(path, "_big", bos.toByteArray(), fileExtName, metaList);
                if (log.isDebugEnabled()) {
                    log.debug("原始大图路径为" + bigPath);
                }
            }

            if (bos != null) {
                bos.close();
                bos = null;
            }
        } else {
            path = client.upload_file1((String) null, size, new UploadFileSender(is), fileExtName, metaList);
        }

        pool.returnResource(client);
        return path;
    }

    private byte[] compressImage(byte[] bytes, String fileExtName) throws IOException {
        int whsize = 200;
        InputStream isSmall = new ByteArrayInputStream(bytes);
        BufferedImage src = ImageIO.read(isSmall);
        if (src != null && src.getWidth() > whsize && src.getHeight() > whsize) {
            Image image = src.getScaledInstance(whsize, whsize, 1);
            BufferedImage tag = new BufferedImage(whsize, whsize, 1);
            Graphics2D g2d = tag.createGraphics();
            if (fileExtName.equalsIgnoreCase("png")) {
                tag = g2d.getDeviceConfiguration().createCompatibleImage(whsize, whsize, 3);
            }

            g2d = tag.createGraphics();
            g2d.drawImage(image, 0, 0, (ImageObserver) null);
            g2d.dispose();
            ByteArrayOutputStream bosSmall = new ByteArrayOutputStream();
            ImageIO.write(tag, fileExtName.toLowerCase(), bosSmall);
            bosSmall.close();
            isSmall.close();
            return bosSmall.toByteArray();
        } else {
            return null;
        }
    }

    public String uploadWithMask(InputStream is, String fileName, String fileExtName, NameValuePair[] metaList, String maskText) throws Exception {
        FastdfsClient client = (FastdfsClient) pool.getResource();
        Image srcImg = ImageIO.read(is);
        BufferedImage buffImg = new BufferedImage(srcImg.getWidth((ImageObserver) null), srcImg.getHeight((ImageObserver) null), 1);
        Graphics2D g = buffImg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(srcImg.getScaledInstance(srcImg.getWidth((ImageObserver) null), srcImg.getHeight((ImageObserver) null), 4), 0, 0, (ImageObserver) null);
        g.rotate(Math.toRadians(-45.0D), (double) buffImg.getWidth() / 2.0D, (double) buffImg.getHeight() / 2.0D);
        g.setColor(Color.RED);
        int fontSize = 11;
        g.setFont(new Font("宋体", 0, fontSize));
        int i = 0;

        do {
            int j = 0;

            do {
                g.drawString(maskText, i, j);
                j += 5 * fontSize;
            } while ((double) j < (double) srcImg.getHeight((ImageObserver) null) * 1.5D);

            i += maskText.length() * 2 * fontSize;
        } while ((double) i < (double) srcImg.getWidth((ImageObserver) null) * 1.5D);

        g.setComposite(AlphaComposite.getInstance(3));
        g.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(buffImg, "JPG", os);
        String path = client.upload_file1((String) null, os.toByteArray(), fileExtName, metaList);
        if (os != null) {
            os.close();
        }

        pool.returnResource(client);
        return path;
    }

    public String getHttpURL(String fileId) throws Exception {
        return this.getHttpURL(fileId, false);
    }

    public String getHttpURL(String fileId, boolean isBigImg) throws Exception {
        String fileName = null;
        return this.getHttpURL(fileId, (String) fileName, isBigImg);
    }

    public String getHttpURL(String fileId, String fileName) throws Exception {
        return this.getHttpURL(fileId, fileName, false);
    }

    public String getHttpURL(String fileId, String fileName, boolean isBigImg) throws Exception {
        if (StrUtil.isEmpty(fileId)) {
            return "";
        } else if (fileId.indexOf("http") == 0) {
            return fileId;
        } else {
            String prefixPath = "";
            String fileExtName = "";
            //   int idx = true;
            if (fileId.contains(".")) {
                int idx = fileId.lastIndexOf(".");
                prefixPath = fileId.substring(0, idx);
                fileExtName = fileId.substring(idx + 1);
                if (isBigImg && this.isImage(fileExtName)) {
                    fileId = prefixPath + "_big" + "." + fileExtName;
                }
            } else {
                log.error("未知文件后缀!");
            }

            String trackerHost = ClientGlobal.getG_secret_key();
            return fileName == null ? trackerHost + "/" + fileId : trackerHost + "/" + fileId + "?filename=" + fileName;
        }
    }

    public void download(HttpServletRequest request, HttpServletResponse resp, String fileId) throws Exception {
        resp.sendRedirect(this.getHttpURL(fileId));
        request.getSession().setAttribute("isDownLoaded", "true");
    }

    public String download(String distPath, String fileId) throws Exception {
        FastdfsClient client = (FastdfsClient) pool.getResource();
        NameValuePair[] metaList = client.get_metadata1(fileId);
        String fileName = null;
        NameValuePair[] var6 = metaList;
        int var7 = metaList.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            NameValuePair nameValuePair = var6[var8];
            if ("fileName".equals(nameValuePair.getName())) {
                fileName = nameValuePair.getValue();
                break;
            }
        }

        client.download_file1(fileId, new DownloadFileWriter(distPath + File.separator + fileName));
        pool.returnResource(client);
        return null;
    }

    public String download(String distPath, String fileId, String filename) throws Exception {
        FastdfsClient client = (FastdfsClient) pool.getResource();
        NameValuePair[] metaList = client.get_metadata1(fileId);
        String fileName = "";
        if (metaList != null) {
            log.info("------metaList-----" + metaList.length);
            NameValuePair[] var7 = metaList;
            int var8 = metaList.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                NameValuePair nameValuePair = var7[var9];
                log.info("------metaList-----" + nameValuePair.getName() + ":" + nameValuePair.getValue());
                if ("fileName".equals(nameValuePair.getName())) {
                    fileName = nameValuePair.getValue();
                    break;
                }
            }
        }

        if (StrUtil.isNotEmpty(filename)) {
            if (filename.indexOf(".") >= 0) {
                fileName = filename;
            } else {
                String[] fileNames = fileId.split("\\.");
                fileName = filename + "." + fileNames[1];
            }
        }

        if (StrUtil.isEmpty(fileName)) {
            int i = fileId.lastIndexOf("/");
            fileName = fileId.substring(i + 1);
        }

        fileName = this.randomFileName(distPath, fileName);
        client.download_file1(fileId, new DownloadFileWriter(distPath + File.separator + fileName));
        pool.returnResource(client);
        return fileName;
    }

    public String randomFileName(String distPath, String fileName) {
        File file = new File(distPath + File.separator + fileName);
        if (file.exists()) {
            Random random = new Random();
            int rannum = (int) (random.nextDouble() * 90000.0D) + 10000;
            String[] fileNames = fileName.split("\\.");
            fileName = fileNames[0] + "-" + rannum + "." + fileNames[1];
            return this.randomFileName(distPath, fileName);
        } else {
            return fileName;
        }
    }

    public boolean delete(String fileId) throws Exception {
        String prefixPath = "";
        String fileExtName = "";
        /// int idx = true;
        if (fileId.contains(".")) {
            int idx = fileId.lastIndexOf(".");
            prefixPath = fileId.substring(0, idx);
            fileExtName = fileId.substring(idx + 1);
        } else {
            log.error("删除文件失败：未知文件后缀!");
        }

        FastdfsClient client = (FastdfsClient) pool.getResource();
        int retCode = client.delete_file1(fileId);
        if (retCode == 0 && this.isImage(fileExtName)) {
            retCode = client.delete_file1(prefixPath + "_big" + "." + fileExtName);
        }

        if (retCode != 0) {
            log.error("删除文件属性失败，错误码：" + retCode);
        }

        pool.returnResource(client);
        return true;
    }

    public boolean setMetaData(String fileId, NameValuePair[] metaList) throws Exception {
        FastdfsClient client = (FastdfsClient) pool.getResource();
        int retCode = client.set_metadata1(fileId, metaList, (byte) 77);
        if (retCode != 0) {
            throw new Exception("设置文件属性出错，错误码：" + retCode);
        } else {
            pool.returnResource(client);
            return true;
        }
    }

    private boolean isImage(String fileExtName) {
        fileExtName = fileExtName.toLowerCase();
        return fileExtName.equals("jpg") || fileExtName.equals("bmp") || fileExtName.equals("jpeg") || fileExtName.equals("gif") || fileExtName.equals("png");
    }

    public static void main(String[] args) throws Exception {
        String s = "group1/M00/00/52/wKhqgFME1dWAD4gRAABg4tmn39o125.png";
        int i = s.lastIndexOf("/");
        System.out.println(s.substring(i + 1));
    }

    static {
        allowFileExtSets = new HashSet(Arrays.asList(allowFileExts));
        instance = null;
        pool = null;
    }
}
