package com.ruoyi.common.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * About:文件操作工具类
 * Other:
 * Created: jyhuang on 2016/6/17 14:32.
 * Editored: jiangxf, huangjj on 2016/6/24 09:00.
 */
public class FileUtils {


    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 文件是否存在
     *
     * @param fileName 文件全称
     * @return
     */
    public static boolean isFileExist(String fileName) {
        return new File(fileName).exists();
    }

    /**
     * 获取文件长度
     *
     * @param fileName
     * @return
     */
    public static long getFileLength(String fileName) {
        return new File(fileName).length();
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return true;
        }
        File curFile = new File(filePath);
        if(!curFile.isFile()){
            return true;
        }
        logger.warn("=======ITAS Clear file======"+filePath);
        int i = 0;
        for (; i < 20 && !curFile.delete(); i++) {
            try {
                System.gc();
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }
        return i < 20;
    }

    /**
     * 当目录为空时删除目录
     *
     * @param dir
     * @return
     */
    public static boolean deleteDirectoryIfEmpty(String dir) {
        return new File(dir).delete();
    }

    /**
     * 删除目录
     *
     * @param dir
     * @return
     */
    public static boolean deleteDirectory(String dir) {
        logger.warn("=======ITAS Clear folders======"+dir);
        return deleteDirectory(new File(dir), true);
    }

    /**
     * 删除目录,递归函数
     *
     * @param dir
     * @return
     */
    private static boolean deleteDirectory(File dir, boolean deleteSelf) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = files.length - 1; i >= 0; i--) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i], true);
                } else {
                    deleteFile(files[i].getPath());
                }
            }
            if (deleteSelf) {
                dir.delete();
            }
        }
        return true;
    }

    /**
     * 创建目录
     * @param dir
     * @return
     */
    public static String createDirectory(String dir) {
        File file = new File(dir);
        if (file.exists()) {
            return dir;
        }
        mkDir(file);
        return dir;
    }

    /**
     * 逐级创建目录
     * @param file
     */
    public static void mkDir(File file){
        if(file.getParentFile().exists()){
            file.mkdir();
        }else{
            mkDir(file.getParentFile());
            file.mkdir();
        }
    }

    /**
     * 拷贝文件
     *
     * @param src
     * @param dest
     * @return
     */
    public static String copyFile(String src, String dest) {
        File destFile = new File(dest);
        if (destFile.isDirectory()) { //目录
            if (!dest.endsWith("/")) {
                dest += "/";
            }
            int index = src.lastIndexOf('/');
            if (index == -1) {
                index = src.lastIndexOf('\\');
            }
            dest += src.substring(index + 1);
            destFile = new File(dest);
        }
        File srcFile = new File(src);
        if (destFile.exists()) { //文件已存在,且不允许覆盖
            if (srcFile.lastModified() == destFile.lastModified()) { //文件最后修改时间相同,不需要拷贝
                return dest;
            }
        }
        byte[] buffer = new byte[Math.max(1, Math.min((int) srcFile.length(), 81920))];
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            int readLen;
            while ((readLen = in.read(buffer)) > 0) {
                out.write(buffer, 0, readLen);
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {

            }
            try {
                out.close();
            } catch (Exception e) {

            }
        }
        destFile.setLastModified(srcFile.lastModified());
        return dest;
    }

    /**
     * 拷贝目录
     *
     * @param src
     * @param dest
     * @return
     */
    public static void copyDirectory(String src, String dest) {
        if (!src.endsWith("/")) {
            src += "/";
        }
        if (!dest.endsWith("/")) {
            dest += "/";
        }
        File srcDir = new File(src);
        if (!srcDir.exists()) {
            return;
        }
        File[] files = srcDir.listFiles();
        if (files == null) {
            return;
        }
        File destBaseDir = new File(dest);
        if (!destBaseDir.exists()) {
            createDirectory(dest);
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) { //目录
                File destDir = new File(dest + files[i].getName());
                if (!destDir.exists()) {
                    destDir.mkdir();
                }
                copyDirectory(src + files[i].getName(), dest + files[i].getName());
            } else { //文件
                copyFile(src + files[i].getName(), dest + files[i].getName());
            }
        }
        return;
    }

    /**
     * 创建目录
     *
     * @param baseDir
     * @param dir
     * @return
     */
    public static String createDirectory(String baseDir, String dir) {
        if (baseDir == null) {
            baseDir = "";
        }
        File file = new File(baseDir + dir);
        if (file.exists()) {
            return baseDir + dir;
        }
        if (!baseDir.endsWith("/") && !baseDir.equals("")) {
            baseDir += "/";
        }
        String[] names = dir.split("/");
        for (int i = 0; i < names.length; i++) {
            baseDir += names[i] + "/";
            file = new File(baseDir);
            if (!file.exists()) {
                for (int j = 0; j < 10 && !file.mkdir(); j++) {
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {

                    }
                }
                if (!file.exists()) {
                    throw new Error("Create new directory " + file.getPath() + " failed.");
                }
            }
        }
        return baseDir;
    }

    /**
     * 写入文本内容
     *
     * @param fileName
     * @param content
     * @param encoding
     * @return
     */
    public static boolean saveStringToTxtFile(String fileName, String content, String encoding) {
        FileOutputStream out = null;
        encoding = encoding.toUpperCase();
        try {
            //Unicode：FF FE, Unicode big_endian：EF FF, UTF-8： EF BB BF
            out = new FileOutputStream(fileName);
            if ("UTF-8".equals(encoding)) {
                out.write(0xEF);
                out.write(0xBB);
                out.write(0xBF);
            } else if ("UNICODE".equals(encoding)) {
                out.write(0xFF);
                out.write(0xFE);
            }
            out.write(encoding == null ? content.getBytes() : content.getBytes(encoding));
            return true;
        } catch (Exception ex) {
            deleteFile(fileName); //保存失败,删除文件
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException ex1) {

            }
        }
    }

    /**
     * 从文件中读取内容
     *
     * @param fileName
     * @return
     */
    public static byte[] readBytesFromFile(String fileName) {
        FileInputStream inputStream = null;
        try {
            File file = new File(fileName);
            int fileSize = (int) file.length();
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[fileSize];
            inputStream.read(bytes);
            return bytes;
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 判断文件的编码格式 - hjj
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String getFileEncodeStr(File sourceFile) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset; //文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; //文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; //文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; //文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0) break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        else break;
                    } else if (0xE0 <= read && read <= 0xEF) { // 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else break;
                        } else break;
                    }
                }
            }
            bis.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    /**
     * 根据一个文件名，读取完文件，干掉bom头。--xujq
     * @param fileName
     * @throws IOException
     */
    public static void trimBom(String fileName) throws IOException {

        FileInputStream fin = new FileInputStream(fileName);
        // 开始写临时文件
        InputStream in = getInputStream(fin);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte b[] = new byte[4096];

        int len = 0;
        while (in.available() > 0) {
            len = in.read(b, 0, 4096);
            //out.write(b, 0, len);
            bos.write(b, 0, len);
        }

        in.close();
        fin.close();
        bos.close();

        //临时文件写完，开始将临时文件写回本文件。
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(bos.toByteArray());
        out.close();
    }

    /**
     * 读取流中前面的字符，看是否有bom，如果有bom，将bom头先读掉丢弃--xujq
     * @param in
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(InputStream in) throws IOException {

        PushbackInputStream testin = new PushbackInputStream(in);
        int ch = testin.read();
        if (ch != 0xEF) {
            testin.unread(ch);
        } else if ((ch = testin.read()) != 0xBB) {
            testin.unread(ch);
            testin.unread(0xef);
        } else if ((ch = testin.read()) != 0xBF) {
            throw new IOException("错误的UTF-8格式文件");
        } else {
            // 不需要做，这里是bom头被读完了
        }
        return testin;

    }

    /**
     * 获取文件MD5值
     *
     * @param fileName
     * @return
     */
    public static String getFileMd5(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        FileInputStream fin = null;
        String md5 = "";
        try {
            fin = new FileInputStream(file);
            md5 = DigestUtils.md5Hex(fin);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return md5;
    }

    /**
     * 清空目录下文件,子目录不清空 hjj
     * @param directory
     * @throws IOException
     */
    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (!directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("读取该目录下文件失败:" + directory);
        }

        IOException exception = null;
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (!file.isDirectory()) {
                file.delete();
            }
        }

        if (null != exception) {
            throw exception;
        }
    }


    /**
     * 读取文件指定行。
     */
    public static String readAppointedLineNumber(String sourceFile, String charEncodeing, int lineNumber) throws IOException {
        InputStreamReader read = null;
        BufferedReader reader = null; //使用bufferreader效率会提高
        int lines = 0;
        String content = null;
        try {
            if (lineNumber <= 0 || lineNumber > getTotalLines(sourceFile)) {
                logger.debug("Not within the range of rows of a document (1 to total rows).");
                System.exit(0);
            }
            File file = new File(sourceFile);
            read = new InputStreamReader(new FileInputStream(file), charEncodeing);
            reader = new BufferedReader(read);
            content = reader.readLine();
            lines = 0;
            while (content != null) {
                lines++;
                content = reader.readLine();
               /* if ((lines - lineNumber) == 0) {
                    System.exit(0);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Read file to specify line exceptions" + e);
            throw new IOException();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (read != null) {
                read.close();
            }
        }
        return content;

    }


    /**
     * 读取从某行开始查找传入的字段，如果查找到，则返回此位置后面的若干（size）行记录
     * searchparam 表示要检索的内容,startlin 表示要从第几行开始查找，默认为0 便是从头开始查找
     * startline 表示从哪行开始检索
     * return  返回字符第一次出现所在的行数,便于检索的时候缩小范围,如果不存在返回0
     */
    public static int readAppointedLineNumber(String sourceFile, String charEncodeing, String searchparam, int startline) throws IOException {

        InputStreamReader read = null;
        BufferedReader reader = null; //使用bufferreader效率会提高
        int lines = 0;
        int resultline = 0;
        String content = null;
        try {
            if (startline < 0) {
                logger.debug("It's not within the line number of files.");
                System.exit(0);
            }
            File file = new File(sourceFile);
            read = new InputStreamReader(new FileInputStream(file), charEncodeing);
            reader = new BufferedReader(read);

            content = reader.readLine();
            lines = 0;
            while (content != null) {
                lines++;
                if (lines >= startline) {
                    if (content.contains(searchparam)) {
                        resultline = lines;
                        break;
                    }
                }
                content = reader.readLine();

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Read file to specify line exceptions" + e);
            throw new IOException();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (read != null) {
                read.close();
            }
        }
        return resultline;

    }


    /**
     * 读取从某行开始查找传入的字段，如果查找到，则返回此位置后面的若干（size）行记录
     * searchparam 表示要检索的内容,startlin 表示要从第几行开始查找，默认为0 便是从头开始查找
     * startline 表示从哪行开始检索
     * size 表示查找到检索内容后，从检索内容所在的行往后读取此大小的行数进行返回
     * return  返回字符第一次出现所在的行数,便于检索的时候缩小范围,如果不存在返回0
     */
    public static String readAppointedLineNumber(String sourceFile, String charEncodeing, String searchparam, int startline, int size) throws IOException {

        InputStreamReader read = null;
        BufferedReader reader = null; //使用bufferreader效率会提高
        int lines = 0;
        int resultline = -1;
        String content = null;
        StringBuffer retstr = new StringBuffer("");
        try {
            if (startline < 0) {
                logger.debug("It's not within the line number of files.");
                System.exit(0);
            }
            File file = new File(sourceFile);
            read = new InputStreamReader(new FileInputStream(file), charEncodeing);
            reader = new BufferedReader(read);

            content = reader.readLine();
            lines = 0;
            while (content != null) {
                lines++;
                if (lines >= startline) {
                    if (content.contains(searchparam) && resultline == -1) {
                        resultline = lines;
                        retstr.append(content + System.getProperty("line.separator")); //直接在末尾追加换行符号
                    } else if (resultline >= startline && lines <= (resultline + size)) {
                        retstr.append(content + System.getProperty("line.separator")); //直接在末尾追加换行符号
                    }
                }
                content = reader.readLine();

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Read file to specify line exceptions" + e);
            throw new IOException();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (read != null) {
                read.close();
            }
        }
        return retstr.toString();

    }


    /**
     * 读取文件指定行,从M行到N行。不计算总数，避免读2次io
     */
    public static List readAppointedLineNumber(String sourceFile, String charEncodeing, long lineBeginNumber, long lineEndNumber) throws IOException {
        InputStreamReader read = null;
        BufferedReader reader = null; //使用bufferreader效率会提高
        int lines = 1;
        String content = null;
        List list =new ArrayList();
//        StringBuffer retstr = new StringBuffer("");
        try {
            File file = new File(sourceFile);
            if (file.isDirectory()) {
                return null;
            }
            read = new InputStreamReader(new FileInputStream(file), charEncodeing);
            reader = new BufferedReader(read);
            content = reader.readLine();
            if (lineBeginNumber <= 0 || lineBeginNumber > lineEndNumber) {
                return null;
            }
            while (content != null) {
                if (lines < lineBeginNumber) {
                    content = reader.readLine();
                    lines++;
                    continue;
                } else if (lines >= lineBeginNumber && lines <= lineEndNumber) {
//                    retstr.append(content + System.getProperty("line.separator")); //直接在末尾追加换行符号
                    list.add(content);
                    content = reader.readLine();
                    lines++;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Read the specified line of the file, exceptions from line M to line N" + e);
            throw new IOException();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (read != null) {
                read.close();
            }
        }
        return list;

    }


    /**
     * 文件内容的总行数
     */
    public static int getTotalLines(String sourceFile) throws IOException {
        InputStreamReader read = null;
        BufferedReader reader = null; //使用bufferreader效率会提高
        int lines = 0;
        try {
            File file = new File(sourceFile);
            if (file.isDirectory()) {
                logger.debug("Can't be a folder");
                System.exit(0);
            }
            read = new InputStreamReader(new FileInputStream(file));
            reader = new BufferedReader(read);
            String s = reader.readLine();
            while (s != null) {
                lines++;
                s = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Exceptions in the total number of rows read from files" + e);
            throw new IOException();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (read != null) {
                read.close();
            }
        }
        return lines;
    }


    /**
     * 读取某个文件夹下的所有文件，非迭代读取
     */
    public static List readFileList(String filepath) throws IOException {
        List list = new ArrayList();
        File file = new File(filepath);
        if (!file.isDirectory()) {
            logger.debug("This is a document.");
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(filepath + file.separator + filelist[i]);
                if (!readfile.isDirectory()) {
                    list.add(filelist[i]); //获取文件名称
                } else if (readfile.isDirectory()) {
                    continue;
                }
            }
        }
        return list;
    }


    /**
     * 读取文件最后N行，此方法需要进一步优化
     * 根据换行符判断当前的行数，
     * 使用统计来判断当前读取第N行
     * PS:输出的List是倒叙，需要对List反转输出
     *
     * @param file    待文件
     * @param numRead 读取的行数
     * @return List<String>
     */
    public static List<String> readLastNLine(File file, String charEncodeing, int numRead) {
        // 定义结果集
        List<String> result = new ArrayList<String>();
        //行数统计
        long count = 0;
        // 排除不可读状态
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            return null;
        }
        // 使用随机读取
        RandomAccessFile fileRead = null;
        try {
            //使用读模式
            fileRead = new RandomAccessFile(file, "r");
            //读取文件长度
            long length = fileRead.length();
            //如果是0，代表是空文件，直接返回空结果
            if (length == 0L) {
                return result;
            } else {
                //初始化游标
                long pos = length - 1;
                while (pos > 0) {
                    pos--;
                    //开始读取
                    fileRead.seek(pos);
                    //如果读取到\n代表是读取到一行
                    if (fileRead.readByte() == '\n') {
                        //使用readLine获取当前行
                        String line = new String(fileRead.readLine().getBytes("iso8859-1"), charEncodeing);
                        //保存结果
                        result.add(line);
                        //打印当前行
                        //行数统计，如果到达了numRead指定的行数，就跳出循环
                        count++;
                        if (count == numRead) {
                            break;
                        }
                    }
                }
                if (pos == 0) {
                    fileRead.seek(0);
                    result.add(fileRead.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Read the last N-line exception of the file" + e);
        } finally {
            if (fileRead != null) {
                try {
                    //关闭资源
                    fileRead.close();
                } catch (Exception e) {
                }
            }
        }
        return result;
    }


    /**
     * 从文件末尾开始读取文件，并逐行打印
     *
     * @param filename file path
     * @param charset  character
     */
    public static HashMap readReverse(String filename, String charset, long startline, long size) {
        RandomAccessFile rf = null;
        HashMap map = new HashMap();
        List list = new ArrayList();
        try {
            File file = new File(filename);
            if (!file.exists() || file.isDirectory() || !file.canRead()) {
                logger.debug("file does not exist");
            }
            rf = new RandomAccessFile(filename, "r");
            long start = rf.getFilePointer();// 返回此文件中的当前偏移量
            long fileLength = rf.length();
            map.put("fileLength", fileLength);
//            long start = fileLength -1 - size;// 返回此文件中的当前偏移量
            if (start < 0) {
                start = 0;
            }
            long readIndex = start + fileLength - 1;
            /*
            可能存在空文件的情况
             */
            if(readIndex<0){
                readIndex=0;
            }
            String strline = null;
            rf.seek(readIndex);// 设置偏移量为文件末尾
            int c = -1;
            int countline = -1;
            while (readIndex > start) {
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    strline = rf.readLine();
                    countline++;
                    //如果超出要读的方位就不读了
                    if (countline >= (startline + size)) {
                        break;
                    }
                    if (countline >= startline) {
                        if (strline != null) {
                            list.add(new String(strline
                                    .getBytes("ISO-8859-1"), charset));
                        }
                    }
                    readIndex--;
                }
                readIndex--;
                rf.seek(readIndex);
                if (readIndex == 0) {// 当文件指针退至文件开始处，输出第一行
                    list.add(new String(strline
                            .getBytes("ISO-8859-1"), charset));
                }
            }
//进行倒序输出            Collections.reverse(list);
            map.put("list", list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rf != null)
                    rf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return map;
        }
    }

    public static String GetFileSize(String Path){
        return GetFileSize(new File(Path));
    }


    public static String GetFileSize(File file){
        String size = "0";
        if(file.exists() && file.isFile()){
            long fileS = file.length();
            DecimalFormat df = new DecimalFormat("#");
           /* if (fileS < 1024) {
                size = df.format((double) fileS) + "BT";
            } else if (fileS < 1048576) {
                size = df.format((double) fileS / 1024) + "KB";
            } else if (fileS < 1073741824) {*/
            size = df.format((double) fileS ) + "";//MB
           /* } else {
                size = df.format((double) fileS / 1073741824) +"GB";
            }*/
        }else if(file.exists() && file.isDirectory()){
            size = "0";
        }else{
            size = "0";
        }
        return size;
    }
    /**
     * 单个文件拷贝。
     *
     * @param srcFilename
     * @param destFilename
     * @param destFileDir
     * @param overwrite
     * @throws IOException
     */
    public static void copyFile(String srcFilename, String destFilename,String destFileDir,
                                boolean overwrite) throws IOException {
        File srcFile = new File(srcFilename);
        File file = new File(destFileDir);
        if ( !file.exists()) {
            file.mkdirs();
        }
        // 首先判断源文件是否存在
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Cannot find the source file: "
                    + srcFile.getAbsolutePath());
        }
        // 判断源文件是否可读
        if (!srcFile.canRead()) {
            throw new IOException("Cannot read the source file: "
                    + srcFile.getAbsolutePath());
        }
        File destFile = new File(destFilename);
        if (overwrite == false) {
            // 目标文件存在就不覆盖
            if (destFile.exists())
                return;
        } else {
            // 如果要覆盖已经存在的目标文件，首先判断是否目标文件可写。
            if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    throw new IOException("Cannot write the destination file: "
                            + destFile.getAbsolutePath());
                }
            } else {
                // 不存在就创建一个新的空文件。
                if (!destFile.createNewFile()) {
                    throw new IOException("Cannot write the destination file: "
                            + destFile.getAbsolutePath());
                }
            }
        }
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        byte[] block = new byte[1024];
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    destFile));
            while (true) {
                int readLength = inputStream.read(block);
                if (readLength == -1)
                    break;// end of file
                outputStream.write(block, 0, readLength);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    // just ignore
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    // just ignore
                }
            }
        }
    }

    /**
     * 根据文件全路径 创建其目录
     * @param src
     * @return
     */
    public static String mkDirByFileName(String src){
        if(StringUtils.isEmpty(src)){
            return src;
        }
        String parentDir = new File(src).getParent();
        return createDirectory(parentDir);
    }

}
