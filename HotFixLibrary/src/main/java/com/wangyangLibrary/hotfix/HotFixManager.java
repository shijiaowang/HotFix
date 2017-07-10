package com.wangyangLibrary.hotfix;

import android.content.Context;
import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;


/**
 * Created by wangyang on 2017/7/9.
 * des:热修复管理类
 */

public class HotFixManager {


    //初始化
    public static void init(Context context) throws Exception {
        if (context == null) {
            throw new Exception("the context must be not null");
        }
        File dexOutputDir = context.getDir("dex", 0);
        File currentVersionDexDir = HotFixDirManager.getCurrentVersionDexDir(context);
        if (currentVersionDexDir==null || !currentVersionDexDir.isDirectory()){
            //如果不存在修复的包的文件夹就不用继续
            return;
        }

        ClassLoader classLoader = context.getClassLoader();
        Object dexElements = getDexElementsFromClassLoader(classLoader);
        //获取.dex结尾的补丁
        File[] files = currentVersionDexDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".dex");
            }
        });
        //开始热修复
        for (File file : files) {
            //创建classLoader读取，外置卡上的dex包
            DexClassLoader dexClassLoader = new DexClassLoader(file.getAbsolutePath(),dexOutputDir.getAbsolutePath()
            ,null,classLoader);
            Object patchElements = getDexElementsFromClassLoader(dexClassLoader);
            //合并原始的与补丁包
            dexElements=combineArray(patchElements,dexElements);

        }
        if (files.length>0){
            //反射设置值给系统的类加载器
            injectDexElements(classLoader,dexElements);
        }


    }
    //给类加载器设置新的dexElements
    private static void injectDexElements(ClassLoader classLoader, Object newElements) throws NoSuchFieldException, IllegalAccessException {
        Field pathListFile = BaseDexClassLoader.class.getDeclaredField("pathList");
        pathListFile.setAccessible(true);
        Object pathList = pathListFile.get(classLoader);
        Field dexElementsFile = pathList.getClass().getDeclaredField("dexElements");
        dexElementsFile.setAccessible(true);
        dexElementsFile.set(pathList,newElements);
    }

    /**
     * 合并两个dexElements数组
     *
     * @param arrayLhs 被合并的
     * @param arrayRhs 原来的数组
     * @return 合并后的
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }


   //获取classLoader的dexElements
    private static Object getDexElementsFromClassLoader(ClassLoader loader) throws Exception {
        Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
        pathListField.setAccessible(true);
        // This is a DexPathList, but that class is package private.
        Object pathList = pathListField.get(loader);
        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        // The objects in this array are Elements, but that class is package private.
        return  dexElementsField.get(pathList);
    }
}
