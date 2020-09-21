package com.ruoyi.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * About:上传、下载工具类
 * Other:
 * Created: jyhuang on 2016/6/27 17:43.
 * Editored: Huangjj
 */
public class UploadUtils {
    private static Logger log = LoggerFactory.getLogger(UploadUtils.class);
    /**
     * 上传文件
     * @param request  req请求
     * @param destPath 文件保存路径
     * @return 上传后文件名
     * 注意：uploadFile 为 前端 input file 的name属性
     */
    public static String upLoadFile(HttpServletRequest request, String destPath){

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        String fileName = file.getOriginalFilename();
        String extName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        Random random =new Random();
        fileName = System.currentTimeMillis()+String.valueOf(random.nextInt(10000))+extName;//重命名文件名称
        String dirPath = destPath.substring(0,destPath.lastIndexOf(File.separator));
        FileUtils.createDirectory(dirPath);
        try {
            //上传文件
            if (file != null && !file.isEmpty()) {
                // 文件复制
                String fullPath = destPath + File.separator + fileName;
                File localFile = new File(fullPath);
                file.transferTo(localFile);
                return fileName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       return "";
    }

    /**
     * 获取上传文件信息 - huangjj
     * @param request  req请求
     * @param destPath 文件磁盘全路径
     * @param relativePath 文件相对路径
     * @return 上传后文件信息
     * 注意：uploadFile 为 前端 input file 的name属性
     */
    public static Map<String, Object> getUpLoadFileMsg(HttpServletRequest request, String destPath, String relativePath){
        Map<String, Object> fileMap = new HashMap<>();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("uploadFile");
        String fileName = file.getOriginalFilename();
        String extName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        Long fileSize = file.getSize();// 单位字节
        long timemill = System.currentTimeMillis();
        String newFileName = timemill + extName;//重命名文件名称
        String fullPath = destPath + newFileName;// 文件全路径
        String relPath = relativePath + newFileName;// 文件相对路径

        try {
            //上传文件
            if (file != null && !file.isEmpty()) {
                // 文件复制
                File localFile = new File(fullPath);
                file.transferTo(localFile);
                fileMap.put("fileName", fileName);
                fileMap.put("fileSize", fileSize.intValue() < 1 ? 1 : fileSize.intValue());
                fileMap.put("fileFullPath", fullPath);
                fileMap.put("filePath", relPath);
                fileMap.put("fileDate", DateUtils.getTime());
                fileMap.put("fileNewName", newFileName);
                fileMap.put("extName", extName);
                fileMap.put("fileNameWithoutExt", timemill);
                return fileMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileMap;
    }

    /**
     * 上传文件
     * @param request  req请求
     * @param destPath 文件保存路径
     * @return 上传后文件名
     */
    public static Map uploadFile(HttpServletRequest request, String destPath, int fileStartPoint){
        Map resMap = new HashMap();
        //创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        //判断 request 是否有文件上传,即多部分请求
        if (multipartResolver.isMultipart(request)) {
            //转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            //取得request中的所有文件名
            Iterator<String> iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                //取得上传文件
                MultipartFile file = multiRequest.getFile(iter.next());
                //取得当前上传文件的文件名称
                String myFileName = file.getOriginalFilename();
                //如果名称不为“”,说明该文件存在，否则说明该文件不存在
                if (!StringUtils.isEmpty(myFileName)) {
                    File destFile = new File(destPath);
                    File bakFile = null;
                    FileInputStream in = null;
                    FileOutputStream out = null;
                    try {
                        if (destFile.exists() && fileStartPoint > 0) {
                            bakFile = new File(destPath + ".bak");
                            bakFile.deleteOnExit();
                            file.transferTo(bakFile);
                            byte[] buffer = new byte[Math.max(1, Math.min((int) destFile.length(), 81920))];
                            in = new FileInputStream(bakFile);
                            out = new FileOutputStream(destFile, true);
                            int readLen;
                            while ((readLen = in.read(buffer)) > 0) {
                                out.write(buffer, 0, readLen);
                            }
                        } else {
                            destFile.deleteOnExit();
                            file.transferTo(destFile);
                        }

                        resMap.put("fileName", myFileName);
                        resMap.put("fileNewName", myFileName);
                        resMap.put("filePath", destPath);
                        resMap.put("fileSize", FileUtils.GetFileSize(destFile));
                        resMap.put("fileMd5", Md5Utils.getMd5ByFile(destFile));

                    }catch (IOException e){
                        e.printStackTrace();
                    }finally {
                        try {
                            if(in != null) in.close();
                            if(out != null) out.close();
                        } catch (Exception e) {}
                        if(bakFile != null) bakFile.delete();
                    }
                }
                //一次只读取一个文件
                break;
            }
        }
        return resMap;
    }

    /**
     * 不使用mvc的文件上传，采用直接流的方式上传文件(支持断点续传)
     * @param request  req请求
     * @param destPath 文件保存路径
     * @return 上传后文件名
     */
    public static Map uploadFileByPoint(HttpServletRequest request, String destPath, int fileStartPoint){
        Map resMap = new HashMap();
        File destFile = new File(destPath);
        InputStream in = null;
        FileOutputStream out = null;
        try {
            byte[] buffer = new byte[81920];
            in = request.getInputStream();
            int readLen;
            if (destFile.exists() && fileStartPoint > 0) {
                out = new FileOutputStream(destFile, true);
                while ((readLen = in.read(buffer)) > 0) {
                    out.write(buffer, 0, readLen);
                }
            } else {
                destFile.deleteOnExit();
                out = new FileOutputStream(destFile);
                while ((readLen = in.read(buffer)) > 0) {
                    out.write(buffer, 0, readLen);
                }
            }
            resMap.put("filePath", destPath);
            resMap.put("fileSize", FileUtils.GetFileSize(destFile));
            resMap.put("fileMd5", Md5Utils.getMd5ByFile(destFile));

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(in != null) in.close();
                if(out != null) out.close();
            } catch (Exception e) {}
        }
        return resMap;
    }

    /**
     * 下载文件
     * @param path 文件全路径
     * @param response
     * @return
     */
    public static void downloadFile(HttpServletResponse response, String path) {

        String fileName = path.substring(path.lastIndexOf(File.separator)+1,path.length());
        try {
            fileName = new String(fileName.getBytes("ISO-8859-1"), "utf-8");
            fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        InputStream inputStream = null;
        OutputStream os = null;
        try {
            inputStream = new FileInputStream(new File(path));
            response.setHeader("Content-Length","" + inputStream.available());
            os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("要下载的文件文件不存在["+path+"]");
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if(os != null) {
                    os.close();
                }
                if(inputStream != null) {
                    inputStream.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件下载 add by linfux
     * 兼容downloadFile(HttpServletResponse response, String path)，
     * 同时能处理通过common.js的common.prototype.download调用下载的请求
     * @param request
     * @param response
     * @param path
     * @return
     */
    public static boolean downloadFile(HttpServletRequest request, HttpServletResponse response, String path) {
        File file = new File(path);
        if(!file.exists()){
            throw new RuntimeException("要下载的文件文件不存在["+path+"]");
        }

        //通过common.js的common.prototype.download调用下载，第一次请求无需返回文件
        String ajaxFlag = request.getParameter("ajaxFlag");
        if(ajaxFlag!=null&&ajaxFlag.equals("1")){
            return false;
        }

        downloadFile(response,path);
        return true;
    }

    /**
     * 文件下载 add by jyhuang 自定义下载文件名
     * 兼容downloadFile(HttpServletResponse response, String path)，
     * 同时能处理通过common.js的common.prototype.download调用下载的请求
     * @param request
     * @param response
     * @param path
     * @return
     */
    public static boolean downloadFile(HttpServletRequest request, HttpServletResponse response, String path, String fileName) {
        File file = new File(path);
        if(!file.exists()){
            throw new RuntimeException("要下载的文件文件不存在["+path+"]");
        }

        //通过common.js的common.prototype.download调用下载，第一次请求无需返回文件
        String ajaxFlag = request.getParameter("ajaxFlag");
        if(ajaxFlag!=null&&ajaxFlag.equals("1")){
            return false;
        }

        downloadFile(response,path, fileName);
        return true;
    }

    /**
     * 下载文件
     * @param path 文件全路径
     * @param response
     * @return
     */
    public static void downloadFile(HttpServletResponse response, String path, String fileName) {
        try {
            fileName = new String(fileName.getBytes("ISO-8859-1"), "utf-8");
            fileName = new String(fileName.getBytes("GBK"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        InputStream inputStream = null;
        OutputStream os = null;
        try {
            inputStream = new FileInputStream(new File(path));
            response.setHeader("Content-Length","" + inputStream.available());
            os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("要下载的文件文件不存在["+path+"]");
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if(os != null) {
                    os.close();
                }
                if(inputStream != null) {
                    inputStream.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取上传广告Zip文件信息重命名为时间戳 并且进行第二次zip压缩- linjie
     * @param request  req请求
     * @param destPath 文件磁盘全路径
     * @param relativePath 文件相对路径
     * @return 上传后文件信息
     * 注意：uploadFile 为 前端 input file 的name属性
     */
    public static Map<String, Object> getUpLoadFileUuidMsg(HttpServletRequest request, String destPath, String relativePath){
        Map<String, Object> fileMap = new HashMap<>();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("uploadFile");
        String fileName = file.getOriginalFilename();
        String extName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        String packageName=fileName.substring(0,fileName.lastIndexOf(".zip")).toLowerCase();

        Long fileSize = file.getSize();// 单位字节
        long timemill = System.currentTimeMillis();
        String newFileName = timemill + ".zip";//重命名文件名称为毫秒时间戳
        String oldFullPath=destPath+fileName;
        String fullPath = destPath + newFileName;// 文件全路径
        String relPath = relativePath + newFileName;// 文件相对路径

        try {
            //上传文件
            // if (file != null && !file.isEmpty()) {
            //     // 文件复制
            //     File localFile = new File(oldFullPath);
            //     file.transferTo(localFile);
            //     ZipUtils.zip(fullPath,localFile);
            //     fileMap.put("fileName", fileName);
            //     fileMap.put("fileSize", fileSize.intValue() < 1 ? 1 : fileSize.intValue());
            //     fileMap.put("fileFullPath", fullPath);
            //     fileMap.put("filePath", relPath);
            //     fileMap.put("fileDate", DateUtils.curDateTime());
            //     fileMap.put("fileNewName", newFileName);
            //     fileMap.put("extName", extName);
            //     fileMap.put("fileNameWithoutExt", timemill);
            //     fileMap.put("packageName", packageName);
            //     return fileMap;
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileMap;
    }

}
