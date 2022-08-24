package com.youming.youche.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName Base64Utils
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 17:28
 */
public class Base64Utils {
    /**
     * 文件转化成base64字符串
     * @param filePath
     * @return
     */
    public static String getFileStr(String filePath) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = filePath;// 待处理的图片
        InputStream in = null;
        byte[] data = null;
        String encode = null; // 返回Base64编码过的字节数组字符串
        // 对字节数组Base64编码
        Path path = Paths.get(filePath);
        String contentType = getContentType(path);

        try {
            // 读取图片字节数组
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            encode = Base64.getEncoder().encodeToString(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // cretate "data URI"
        StringBuilder sb = new StringBuilder();
        sb.append("data:");
        sb.append(contentType);
        sb.append(";base64,");
        sb.append(encode);
        return sb.toString();
        //return encode;
    }

    /**
     * base64字符串转化成文件
     *
     * @param fileData
     *            文件编码
     * @param filePath
     *            存放到本地路径
     * @return
     * @throws IOException
     */
    @SuppressWarnings("finally")
    public static boolean generateFile(String fileData, String filePath) throws IOException { // 对字节数组字符串进行Base64解码并生成图片
        if (fileData == null) // 图像数据为空
            return false;
        // remove "data URI"
        fileData = fileData.substring(fileData.indexOf(',') + 1, fileData.length());
        System.out.println(fileData);
        OutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            // Base64解码
            byte[] b = Base64.getDecoder().decode(fileData);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            out.write(b);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            out.flush();
            out.close();
            return true;
        }
    }

    public static String getContentType(Path path){
        try {
            return Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片URL转Base64编码
     * @param imgUrl 图片URL
     * @return Base64编码
     */
    public static String imageUrlToBase64(String imgUrl) {
        URL url = null;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        HttpURLConnection httpUrl = null;

        try {
            url = new URL(imgUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            httpUrl.getInputStream();

            is = httpUrl.getInputStream();
            outStream = new ByteArrayOutputStream();

            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用输入流从buffer里把数据读取出来
            while( (len = is.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }

            // 对字节数组Base64编码
            String encode= encode(outStream.toByteArray());
            StringBuilder sb = new StringBuilder();
            sb.append("data:");
            sb.append(httpUrl.getContentType());
            sb.append(";base64,");
            sb.append(encode);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
                if(outStream != null) {
                    outStream.close();
                }
                if(httpUrl != null) {
                    httpUrl.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 图片转字符串
     * @param image 图片Buffer
     * @return Base64编码
     */
    public static String encode(byte[] image){
        return replaceEnter(Base64.getEncoder().encodeToString(image));
    }

    /**
     * 字符替换
     * @param str 字符串
     * @return 替换后的字符串
     */
    public static String replaceEnter(String str){
        String reg ="[\n-\r]";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

}
