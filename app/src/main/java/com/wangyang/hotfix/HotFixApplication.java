package com.wangyang.hotfix;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.wangyangLibrary.hotfix.HotFixManager;

/**
 * Created by wangyang on 2017/7/9.
 * des:
 */

public class HotFixApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        try {
            HotFixManager.init(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
