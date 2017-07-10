package com.wangyangLibrary.hotfix;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by wangyang on 2017/7/10.
 * des: 热修复文件夹管理器
 */

public class HotFixDirManager {
    //存放dex文件的文件夹
    public static String FIX_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/com.wangyang.fixDir";

    public static File getCurrentVersionDexDir(Context context) {
        //获取当前版本的文件夹
        File fixVersionDir = getCurrentDir(context);
        if (!fixVersionDir.exists()) {
            return null;
        }
        return fixVersionDir;
    }

    //获取修复文件夹根目录
    @NonNull
    private static File getFixFile() {
        File file = new File(FIX_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取当前版本的fix目录
     *
     * @param context
     * @return
     */
    @NonNull
    private static File getCurrentDir(Context context) {
        return new File(getFixFile().getAbsolutePath(), context.getPackageName() + "_" + getCurrentVersionName(context));
    }

    /**
     * 删除之前的更新包
     * @param context
     */
    public static void deleteOldFixDir(Context context){
        File currentDir = getCurrentDir(context);
        File fixFile = getFixFile();
        if (fixFile.isDirectory()){
            File[] files = fixFile.listFiles();
            for (File file : files) {
               if (file.getName().equals(currentDir.getName()))continue;
                deleteDir(file);
            }
        }
    }
    //删除文件夹和文件夹里面的文件
    public static void deleteDir(File file) {
       if (file==null)return;
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File itemFile : files) {
                deleteDir(itemFile);
            }
        }else {
            file.delete();
        }
    }
    //将一个补丁放入文件夹
    public static void inputDex(File dexFile, Context context) {
        if (dexFile != null && dexFile.exists()) {
            File currentDir = getCurrentDir(context);
            if (!currentDir.exists()) {
                currentDir.mkdirs();
            }
            String name = dexFile.getName();
            if (new File(currentDir.getAbsolutePath(),name).exists()){
                //存在同样的版本就拒绝
                return;
            }
            try {
                copy(dexFile, currentDir);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 拷贝一个文件到指定目录
     * @param file
     * @param toFile
     * @throws Exception
     */
    public static void copy(File file, File toFile) throws Exception {
        byte[] b = new byte[1024];
        int a;
        FileInputStream fis;
        FileOutputStream fos;
        String filepath = file.getAbsolutePath();
        filepath = filepath.replaceAll("\\\\", "/");
        String toFilepath = toFile.getAbsolutePath();
        toFilepath = toFilepath.replaceAll("\\\\", "/");
        int lastIndexOf = filepath.lastIndexOf("/");
        toFilepath = toFilepath + filepath.substring(lastIndexOf, filepath.length());
        //写文件
        File newFile = new File(toFilepath);
        fis = new FileInputStream(file);
        fos = new FileOutputStream(newFile);
        while ((a = fis.read(b)) != -1) {
            fos.write(b, 0, a);
        }
        fos.flush();
        fos.close();
        fis.close();
    }

    /**
     * 获取当前应用的版本
     *
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    private static String getCurrentVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
