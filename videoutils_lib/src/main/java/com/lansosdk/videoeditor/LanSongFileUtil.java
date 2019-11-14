package com.lansosdk.videoeditor;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;

/**
 * 各种文件操作.
 * <p>
 * <p>
 * 杭州蓝松科技有限公司
 * www.lansongtech.com
 */
public class LanSongFileUtil {

    public static final String TAG = "LanSongFileUtil";
    private static final Object mLock = new Object();
    protected static String mTmpFileSubFix = "";  //后缀,
    protected static String mTmpFilePreFix = "";  //前缀;
    public static String TMP_DIR = Environment.
            getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM/duanzi" + File.separator + "temp";
    //视频加水印的地址
    public static String VIDEO_DOWNLOAD_BY_WATER = Environment.
            getExternalStorageDirectory().getAbsolutePath() + File.separator + "DCIM/duanzi";


    public static String getPath() {
        File file = new File(TMP_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        return TMP_DIR;
    }

    /**
     * 返回文件大小, 单位M; 2位有效小数;
     *
     * @param filePath
     * @return
     */
    public static float getFileSize(String filePath) {
        if (filePath == null) {
            return 0.0f;
        } else {
            File file = new File(filePath);
            if (file.exists() == false) {
                return 0.0f;
            } else {
                long size = file.length();
                float size2 = (float) size / (1024f * 1024f);

                int n = (int) (size2 * 100f);  //截断
                return (float) n / 100f;
            }
        }
    }

    /**
     * 在指定的文件夹里创建一个文件名字, 名字是当前时间,指定后缀.
     *
     * @return
     */
    public static String createFile(String dir, String suffix) {
        synchronized (mLock) {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            int second = c.get(Calendar.SECOND);
            int millisecond = c.get(Calendar.MILLISECOND);
            year = year - 2000;

            String dirPath = dir;
            File d = new File(dirPath);
            if (!d.exists())
                d.mkdirs();

            if (!dirPath.endsWith("/")) {
                dirPath += "/";
            }

            String name = mTmpFilePreFix;
            name += String.valueOf(year);
            name += String.valueOf(month);
            name += String.valueOf(day);
            name += String.valueOf(hour);
            name += String.valueOf(minute);
            name += String.valueOf(second);
            name += String.valueOf(millisecond);
            name += mTmpFileSubFix;
//            if (suffix.startsWith(".") == false) {
//                name += ".";
//            }
            name += suffix;


            try {
                Thread.sleep(1); // 保持文件名的唯一性.
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String retPath = dirPath + name;
            File file = new File(retPath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return retPath;
        }
    }

    /**
     * 在指定的文件夹里创建一个文件名字, 指定名字,指定后缀.
     *
     * @param dir
     * @param fileName
     * @return
     */
    public static String createFileInApp(String dir, String fileName) {
        String name = dir;
        File d = new File(name);

        // 如果目录不中存在，创建这个目录
        if (!d.exists())
            d.mkdir();
        name = name + "/" + fileName;

        File file = new File(name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return name;
    }

    /**
     * 在box目录下生成一个mp4的文件,并返回名字的路径.
     *
     * @return
     */
    public static String createMp4FileInBox(String style) {
        // TODO: 2018/11/28 修改过加水印后的文件路径
        return createFile(VIDEO_DOWNLOAD_BY_WATER, style + ".mp4");
    }

    /**
     * 在box目录下生成一个mp4的文件,并返回名字的路径.
     *
     * @return
     */
    public static String createMp4FileInBox() {
        return createFile(TMP_DIR, ".mp4");
    }

    public static String createMp4FileInAppFileSor(String path, String fileNameByEnd) {
        return createFileInApp(path, fileNameByEnd);
    }


    /**
     * 在box目录下生成一个aac的文件,并返回名字的路径.
     *
     * @return
     */
    public static String createAACFileInBox() {
        return createFile(TMP_DIR, ".aac");
    }

    public static String createM4AFileInBox() {
        return createFile(TMP_DIR, ".m4a");
    }

    public static String createMP3FileInBox() {
        return createFile(TMP_DIR, ".mp3");
    }

    /**
     * 在box目录下生成一个指定后缀名的文件,并返回名字的路径.这里仅仅创建一个名字.
     *
     * @param suffix 指定的后缀名.
     * @return
     */
    public static String createFileInBox(String suffix) {
        return createFile(TMP_DIR, suffix);
    }

    /**
     * 只是在box目录生成一个路径字符串,但这个文件并不存在.
     *
     * @return
     */
    public static String newMp4PathInBox() {
        return newFilePath(TMP_DIR, ".mp4");
    }

    /**
     * 在指定的文件夹里 定义一个文件名字, 名字是当前时间,指定后缀.
     * 注意: 和 {@link #createFile(String, String)}的区别是,这里不生成文件,只是生成这个路径的字符串.
     *
     * @param suffix ".mp4"
     * @return
     */
    public static String newFilePath(String dir, String suffix) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);
        year = year - 2000;
        String name = dir;
        File d = new File(name);

        // 如果目录不中存在，创建这个目录
        if (!d.exists())
            d.mkdir();
        name += "/";

        name += String.valueOf(year);
        name += String.valueOf(month);
        name += String.valueOf(day);
        name += String.valueOf(hour);
        name += String.valueOf(minute);
        name += String.valueOf(second);
        name += String.valueOf(millisecond);
        name += suffix;

        try {
            Thread.sleep(1);  //保持文件名的唯一性.
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 根据文件路径拷贝文件
     *
     * @param resourceFile 源文件
     * @param targetPath   目标路径（包含文件名和文件格式）
     * @return boolean 成功true、失败false
     */
    public static boolean copyFile(File resourceFile, String targetPath, String fileName) {
        boolean result = false;
        if (resourceFile == null || TextUtils.isEmpty(targetPath)) {
            return result;
        }
        File target = new File(targetPath);
        if (target.exists()) {
            target.delete(); // 已存在的话先删除
        } else {
            try {
                target.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File targetFile = new File(targetPath.concat(fileName));
        if (targetFile.exists()) {
            targetFile.delete();
        } else {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileChannel resourceChannel = null;
        FileChannel targetChannel = null;
        try {
            resourceChannel = new FileInputStream(resourceFile).getChannel();
            targetChannel = new FileOutputStream(targetFile).getChannel();
            resourceChannel.transferTo(0, resourceChannel.size(), targetChannel);
            result = true;
        }  catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        try {
            resourceChannel.close();
            targetChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //删除源文件
        resourceFile.delete();
        return result;
    }

    /**
     * 使用终端的cp命令拷贝文件,拷贝成功,返回目标字符串,失败返回null;
     * <p>
     * 拷贝大文件,则有耗时,可以放到new Thread中进行.
     *
     * @param srcPath
     * @param suffix  后缀名,如".pcm"
     * @return
     */
    public static String copyFile(String srcPath, String suffix) {
        String dstPath = LanSongFileUtil.createFile(TMP_DIR, suffix);

//			 	String cmd="/system/bin/cp ";
//			 	cmd+=srcPath;
//			 	cmd+=" ";
//			 	cmd+=dstPath;
//				Runtime.getRuntime().exec(cmd).waitFor();

        File srcF = new File(srcPath);
        File dstF = new File(dstPath);

        copyFile(srcF, dstF);

        if (srcF.length() == dstF.length())
            return dstPath;
        else {
            Log.e(TAG, "fileCopy is failed! " + srcPath + " src size:" + srcF.length() + " dst size:" + dstF.length());
            LanSongFileUtil.deleteFile(dstPath);
            return null;
        }
    }

    /**
     * 删除指定的文件.
     *
     * @param path
     */
    public static void deleteFile(String path) {
        if (TextUtils.isEmpty(path))return;
        File dir = new File(path);
        if (!dir.exists()) {
            return;
        }
        if (!dir.isDirectory()) {
            dir.delete();
        }
        //删除文件新api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        //表示继续遍历
                        return FileVisitResult.CONTINUE;
                    }

                    /**
                     * 访问某个path失败时调用
                     */
                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        //如果目录的迭代完成而没有错误，有时也会返回null
                        if (exc == null) {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw exc;
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (dir.listFiles() == null) return;
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    deleteFile(path);
                }
            }
        }
        dir.delete();
    }

    /**
     * 判断 两个文件大小相等.
     *
     * @param path1
     * @param path2
     * @return
     */
    public static boolean equalSize(String path1, String path2) {
        File srcF = new File(path1);
        File srcF2 = new File(path2);
        if (srcF.length() == srcF2.length())
            return true;
        else
            return false;
    }

    public static String getFileNameFromPath(String path) {
        if (path == null)
            return "";
        int index = path.lastIndexOf('/');
        if (index > -1)
            return path.substring(index + 1);
        else
            return path;
    }

    public static String getParent(String path) {
        if (TextUtils.equals("/", path))
            return path;
        String parentPath = path;
        if (parentPath.endsWith("/"))
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        int index = parentPath.lastIndexOf('/');
        if (index > 0) {
            parentPath = parentPath.substring(0, index);
        } else if (index == 0)
            parentPath = "/";
        return parentPath;
    }

    public static boolean fileExist(String absolutePath) {
        if (absolutePath == null)
            return false;
        else {
            File file = new File(absolutePath);
            if (file.exists())
                return true;
        }
        return false;
    }

    public static boolean filesExist(String[] fileArray) {

        for (String file : fileArray) {
            if (fileExist(file) == false)
                return false;
        }
        return true;
    }

    /**
     * 获取文件后缀
     */
    public static String getFileSuffix(String path) {
        if (path == null)
            return "";
        int index = path.lastIndexOf('.');
        if (index > -1)
            return path.substring(index + 1);
        else
            return "";
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean copyFile(File src, File dst) {
        boolean ret = true;
        if (src.isDirectory()) {
            File[] filesList = src.listFiles();
            dst.mkdirs();
            for (File file : filesList)
                ret &= copyFile(file, new File(dst, file.getName()));
        } else if (src.isFile()) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(new FileInputStream(src));
                out = new BufferedOutputStream(new FileOutputStream(dst));

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                return true;
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                close(in);
                close(out);
            }
            return false;
        }
        return ret;
    }

    public static boolean close(Closeable closeable) {
        if (closeable != null)
            try {
                closeable.close();
                return true;
            } catch (IOException e) {
            }
        return false;
    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir, String style) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                String child = children[i];
                if (!child.contains(style)) {
                    continue;
                }
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    /**
     *测试

     public static void main(String[] args) {
     doDeleteEmptyDir("new_dir1");
     String newDir2 = "new_dir2";
     boolean success = deleteDir(new File(newDir2));
     if (success) {
     System.out.println("Successfully deleted populated directory: " + newDir2);
     } else {
     System.out.println("Failed to delete populated directory: " + newDir2);
     }
     } */
}
