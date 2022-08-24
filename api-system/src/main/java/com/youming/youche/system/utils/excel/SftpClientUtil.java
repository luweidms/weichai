/**
 *
 */
package com.youming.youche.system.utils.excel;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

/**
 * @version V3.0
 * @Title: SftpClientUtil.java
 * @Package org.myclouds.crm.business.entity.reportStatistics
 * @Description:
 * @author: 祝浪
 * @date: 2019年7月31日 上午9:28:21
 * @company: 安吉物流
 */
public class SftpClientUtil {

    /**
     * 初始化日志引擎
     */
    private final Logger logger = LoggerFactory.getLogger(SftpClientUtil.class);

    /**
     * Sftp
     */
    ChannelSftp sftp = null;
    /**
     * 主机
     */
    private String host = "";
    /**
     * 端口
     */
    private int port = 0;
    /**
     * 用户名
     */
    private String username = "";
    /**
     * 密码
     */
    private String password = "";
    /**
     * 文件存储路径
     */
    private String directory = "";

    private OutputStream outstream = null;

    private Session session = null;

    /**
     * 构造函数
     *
     * @param host     主机
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @param @throws  Exception
     * @throws
     * @Title: SftpClientUtil
     * @Description: 连接sftp服务器
     * @author: 祝浪
     * @date 2019年7月31日 下午4:47:25
     */
    public SftpClientUtil(String host, int port, String username, String password, String directory) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.directory = directory;
    }

    /**
     * 连接sftp服务器
     *
     * @param @return
     * @param @throws Exception
     * @throws
     * @Title: connect
     * @Description: 连接sftp服务器
     * @author: 祝浪
     * @date 2019年7月31日 下午4:47:25
     */
    public void connect() throws Exception {
        JSch jsch = new JSch();
        Session sshSession = jsch.getSession(this.username, this.host, this.port);
        logger.info(SftpClientUtil.class + "Session created.");

        sshSession.setPassword(password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        sshSession.connect(20000);
        logger.info(SftpClientUtil.class + " Session connected.");

        logger.info(SftpClientUtil.class + " Opening Channel.");
        Channel channel = sshSession.openChannel("sftp");
        channel.connect();
        this.session = sshSession;
        this.sftp = (ChannelSftp) channel;
        logger.info(SftpClientUtil.class + " Connected to " + this.host + ".");
    }

    /**
     * 关流
     *
     * @param @return
     * @param @throws Exception
     * @throws
     * @Title: disconnect
     * @Description: 关流
     * @author: 祝浪
     * @date 2019年7月31日 下午3:47:25
     */
    public void disconnect() {
        if (this.sftp != null) {
            if (this.sftp.isConnected()) {
                this.sftp.disconnect();
            } else if (this.sftp.isClosed()) {
                logger.info(SftpClientUtil.class + " sftp is closed already");
            }
        }
        if (this.session != null) {
            this.session.disconnect();
        }
    }

    /**
     * 上传单个文件
     *
     * @param @param  bytes 文件字节流
     * @param @param  fileName 要上传的文件名称
     * @param @return
     * @param @throws Exception
     * @throws
     * @Title: upload
     * @Description: 上传单个文件
     * @author: 祝浪
     * @date 2019年7月31日 下午3:37:25
     */
    public void upload(byte[] bytes, String fileName, String path) throws Exception {
        this.sftp.cd(directory + "/" + path);
        outstream = sftp.put(fileName);
        outstream.write(bytes);
    }

    /**
     * @param @param  bytes
     * @param @param  fileName
     * @param @param  path
     * @param @throws Exception    设定文件
     * @return void    返回类型
     * @throws
     * @Title: bacthUploadFile
     * @Description: 上传文件
     * @author: 祝浪
     * @date 2019年10月8日 下午3:38:36
     */
    public void bacthUploadFile(List<byte[]> list, List<String> listName, String path) {
        try {
            this.sftp.cd(directory + "/" + path);
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    connect();
                }
                outstream = sftp.put(listName.get(i));
                outstream.write(list.get(i));
                outstream.flush();
                outstream.close();
                this.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param @param  bytes
     * @param @param  fileName
     * @param @param  path
     * @param @throws Exception    设定文件
     * @return void    返回类型
     * @throws
     * @Title: bacthUploadFile
     * @Description: 上传文件
     * @author: 祝浪
     * @date 2019年10月8日 下午3:38:36
     */
    public void bacthUploadFile(byte[] bytes, String fileName, String path) throws Exception {
        try {
            connect();
            this.sftp.cd(directory + "/" + path);
            outstream = sftp.put(fileName);
            outstream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            outstream.flush();
            outstream.close();
            this.disconnect();
        }
    }

    /**
     * 下载单个文件
     *
     * @param @param  directory 下载目录
     * @param @param  downloadFile 下载的文件名称
     * @param @return
     * @param @throws Exception
     * @return InputStream    返回类型
     * @throws
     * @Title: download
     * @Description: 下载单个文件
     * @author: 祝浪
     * @date 2019年7月31日 下午3:37:25
     */
    public BufferedInputStream download(String directory, String downloadFile) throws Exception {
        this.sftp.cd(directory);
        return new BufferedInputStream(this.sftp.get(downloadFile));
    }

    /**
     * 查询文件
     * @Title：getInputStreamPath
     * @param path 文件夹
     * @param fileName 文件名
     * @throws Exception
     * @Author：刘综南
     * @Date：2021年5月28日 下午5:43:23
     */
    public InputStream getInputStreamPath(String path, String fileName) throws Exception {
        this.sftp.cd(directory + "/" + path);
        InputStream streatm = this.sftp.get(fileName);
        return streatm;
    }

    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @param @throws    Exception
     * @throws
     * @Title: disconnect
     * @Description: 删除文件
     * @author: 祝浪
     * @date 2019年7月31日 下午3:47:25
     */
    public void delete(String path, String deleteFile) throws Exception {
        this.sftp.cd(directory + "/" + path);
        if (isExist(deleteFile)) {
            this.sftp.rm(deleteFile);
        }
    }


    /**
     * 更改文件名
     *
     * @param directory 文件所在目录
     * @param oldFileNm 原文件名
     * @param newFileNm 新文件名
     * @param @throws   Exception
     * @throws
     * @Title: rename
     * @Description: 更改文件名
     * @author: 祝浪
     * @date 2019年7月31日 下午3:47:25
     */
    public void rename(String directory, String oldFileNm, String newFileNm) throws Exception {
        this.sftp.cd(directory);
        this.sftp.rename(oldFileNm, newFileNm);
    }

    /**
     * 进入文件夹
     *
     * @param directory 所在目录
     * @param @throws   Exception
     * @throws
     * @Title: cd
     * @Description: 进入文件夹
     * @author: 祝浪
     * @date 2019年7月31日 下午3:47:25
     */
    public void cd(String directory) throws Exception {
        this.sftp.cd(directory);
    }

    /**
     * 创建文件夹，如果存在则不创建
     *
     * @param directory
     * @param folderName
     * @throws Exception
     * @throws
     * @Title: cd
     * @Description: 创建文件夹，如果存在则不创建
     * @author: 祝浪
     * @date 2019年7月31日 下午3:47:25
     */
    public void mkdir(String folderName) throws Exception {
        this.sftp.cd(directory);
        SftpATTRS attrs = null;
        try {
            attrs = sftp.stat(folderName);
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (attrs == null) {
            this.sftp.mkdir(folderName);
            logger.info("创建子目录：" + folderName);
        }
    }

    /**
     * 得到文件
     *
     * @param directory 文件所在目录
     * @param @throws   Exception
     * @throws
     * @Title: get
     * @Description: 得到文件
     * @author: 祝浪
     * @date 2019年7月31日 下午3:47:25
     */
    public InputStream get(String directory) throws Exception {
        InputStream streatm = this.sftp.get(directory);
        return streatm;
    }

    /**
     * 判断文件或目录是否存在
     *
     * @param 路径
     * @return
     */
    public boolean isExist(String path) {
        boolean isExist = false;
        try {
            this.sftp.lstat(path);
            isExist = true;
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isExist = false;
            }
        }
        return isExist;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
