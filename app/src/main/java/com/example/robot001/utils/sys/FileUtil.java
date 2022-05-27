package com.example.robot001.utils.sys;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileUtil {
    private static final String FILE_SUFFIX = "avi|flv|mov|mpg|mpeg|mp3|mp4|wav|jpeg|gif|jpg|png|gif|bmp";
    private static final String SAVE_LOG_DIR = Environment.getExternalStorageDirectory() + "/Android/data/com.iflytek.jzapp/log/";
    private static final String LOG_FILE_NAME = "yunxinlog.txt";


    public static void saveFile(byte[] bfile, String filePathName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        File dir = null;
        try {
            System.out.println(filePathName.substring(0, filePathName.lastIndexOf("/")));
            dir = new File(filePathName.substring(0, filePathName.lastIndexOf("/") + 1).replace("/", "\\"));
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePathName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile, 0, bfile.length);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    // 获取文件扩展名
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    public static boolean hasExtentsion(String filename) {
        int dot = filename.lastIndexOf('.');
        if ((dot > -1) && (dot < (filename.length() - 1))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Construct a file from the set of name elements.
     *
     * @param directory the parent directory
     * @param names     the name elements
     * @return the file
     */
    public static File getFile(File directory, String... names) {
        if (directory == null) {
            throw new NullPointerException("directory must not be null");
        }
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = directory;
        for (String name : names) {
            file = new File(file, name);
        }
        return file;
    }

    public static String getFilename(String input) {
        String result = input;
        String tmp = input.substring(input.lastIndexOf('/') + 1);
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_-]+[\\.](" + FILE_SUFFIX + ")");
        Matcher matcher = pattern.matcher(tmp);
        while (matcher.find()) {
            result = matcher.group();
        }
        return result;
    }

    public static String getNameFromUrl(String url) {
        if (TextUtils.isEmpty(url)) return "";
        final CharSequence separator = "/";
        String result;
        if (url.contains(separator)) {
            result = url.substring(url.lastIndexOf('/') + 1);
        } else {
            result = url;
        }
        return result;
    }

    public static String getFileBasename(String input) {
        String filename = getNameFromUrl(input);
        int position = filename.indexOf('.');
        return position == -1 ? "" : filename.substring(0, position);
    }

    public static String getFileSuffix(String input) {
        String filename = getNameFromUrl(input);
        int position = filename.indexOf('.') + 1;
        return position == -1 ? "png" : filename.substring(position);
    }

    public static Bitmap.CompressFormat format(String suffix) {
        Bitmap.CompressFormat compressFormat;
        switch (suffix) {
            case "jpg":
            case "jpeg":
                compressFormat = Bitmap.CompressFormat.JPEG;
                break;
            case "png":
            default:
                compressFormat = Bitmap.CompressFormat.PNG;
                break;
        }
        return compressFormat;
    }

    public static boolean isFileExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static void createDirIfNotExist(String path) {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {

        }
    }

    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    public static String getExternalPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    public static void writeContentToFile(String filePath, String fileName, String content) {
        createDirIfNotExist(filePath);
        File file = new File(filePath, fileName);
        if (file.exists() && file.length() >= 2 * 1024 * 1024) {
            file.delete();
        }
        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(filePath + File.separator + fileName, "rw");
            long fileLength = f.length();// 获取文件的长度即字节数
            // 将写文件指针移到文件尾
            f.seek(fileLength);
            f.write("\r\n".getBytes());
            f.write(content.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != f) {
                try {
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<String> readFileByLines(String fileName) {
        List<String> result = new ArrayList<>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                result.add(tempString);
                line++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e1) {
                }
            }
        }
        return result;
    }

    public static void writeYunxinLog(String content) {
        writeContentToFile(SAVE_LOG_DIR, LOG_FILE_NAME, content);
    }

    public static List<String> readYunxinLog() {
        return readFileByLines(SAVE_LOG_DIR + File.separator + LOG_FILE_NAME);
    }

    // 获取不带扩展名的文件名
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 判断当前存储卡是否可用
     **/
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取当前需要查询的文件夹
     **/
    public static String getSdkRootDir(Context context) {
        if (checkSDCardAvailable()) {
            return Environment.getExternalStorageDirectory() + File.separator + "hwl";
        } else {
            return context.getFilesDir().getAbsolutePath() + File.separator + "hwl";
        }
    }

    /**
     * @param is
     * @param filePath
     * @return 保存失败，返回-1
     */
    public static long save(InputStream is, String filePath) {
        File f = new File(filePath);
        if (!f.getParentFile().exists()) {// 如果不存在上级文件夹
            f.getParentFile().mkdirs();
        }
        FileOutputStream fos = null;
        try {
            f.createNewFile();
            fos = new FileOutputStream(f);
            int read = 0;
            byte[] bytes = new byte[8091];
            while ((read = is.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
            return f.length();
        } catch (IOException e) {
            if (f != null && f.exists()) {
                f.delete();
            }
            //Logger.printStackTrace(e);
            return -1;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 防止越来越多
     *
     * @param expireMilss 过期毫秒
     * @param file
     */
    public static void deleteExpiredMillsFolders(long expireMilss, File file) {

        File[] fileList = file.listFiles();
        if (fileList == null || fileList.length == 0) {
            return;
        }

        for (File aFileList : fileList) {
            // 毫秒数
            long lastModifed = aFileList.lastModified();
            long differ = Math.abs(System.currentTimeMillis() - lastModifed);
            if (differ > expireMilss) {
                deleteFolderFile(aFileList.getAbsolutePath(), true);
            }
        }
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        try {
            File file = new File(filePath);
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (File file1 : files) {
                    deleteFolderFile(file1.getAbsolutePath(), true);
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    if (file.listFiles().length == 0) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取该链接再手机里的文件地址    xxxx/xxxx/xxx/xxx.jpg
//    public static String getImageFileNameFromUrl(String imageUrl){
//        if (imageUrl.contains("?token")){
//            imageUrl = imageUrl.substring(0, imageUrl.indexOf("?token"));
//
//        }
//        return Constant.IMG_SAVE_PATH + File.separator + getFileNameHandExtension(imageUrl);
//    }

    //获取链接地址中的图片名称  xxxxx.jpg
    public static String getImageNameFromUrl(String imageUrl){
        if (imageUrl.contains("?token")){
            imageUrl = imageUrl.substring(0, imageUrl.indexOf("?token"));
        }
        return getFileNameHandExtension(imageUrl);
    }

    //获取链接地址中的图片名称  xxxxx.jpg
    public static String getFileNameFromUrl(String imageUrl){
        if (imageUrl.contains("?token")){
            imageUrl = imageUrl.substring(0, imageUrl.indexOf("?token"));
        }
        return getFileNameHandExtension(imageUrl);
    }


    // 通过路径获取文件名，带后缀
    public static String getFileNameHandExtension(String pathHandName) {
        if (TextUtils.isEmpty(pathHandName)) {
            return "未知路径";
        }
        int start = pathHandName.lastIndexOf("/");
//        int end = pathHandName.lastIndexOf(".");
        int end = pathHandName.length();
        if (start != -1 && end != -1) {
            return pathHandName.substring(start + 1, end);
        } else {
            return "未知文件";
        }
    }

    /**
     * 格式化单位
     *
     * @param
     * @return
     */
    public static String formatSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String formatSize2(long size){
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "0";
        String wrongSize = "0B";
        double mSize = 0;
        if (size == 0) {
            return wrongSize;
        }
        if (size < 1024) {
            fileSizeString =  (int)size + "B";
        } else if (size < 1048576) {
            mSize = size * 1.0 / 1024;
            if (mSize == (int)mSize){
                fileSizeString = (int)mSize + "KB";
            }else {
                fileSizeString = df.format((double) size / 1024) + "KB";
            }
        } else if (size < 1073741824) {
            mSize = size * 1.0 / 1048576;
            if (mSize == (int)mSize){
                fileSizeString = (int)mSize + "MB";
            }else {
                fileSizeString = df.format((double) size / 1048576) + "MB";
            }
        } else {
                mSize = size * 1.0 / 1073741824;
                if (mSize == (int)mSize){
                    fileSizeString = (int)mSize + "GB";
                }else {

                    fileSizeString = df.format((double) size / 1073741824) + "GB";
                }
        }
        return fileSizeString;

    }




    /**
     * 获取缓存目录
     * @param context
     * @return
     */
    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
    /**
     * 获取文件夹大小
     *
     * @param file
     * @return
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            if (file.isDirectory()) {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);

                    } else {
                        size = size + fileList[i].length();
                    }
                }
            }else{
                size = file.length();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return size; // return size/1048576;
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
//            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
//                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
//                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
//            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
//            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
//            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
//            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }

    }



}


